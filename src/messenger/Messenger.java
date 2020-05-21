package messenger;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import messenger.types.Message;
import messenger.types.Ping;
import messenger.types.RetransmitRequest;
import messenger.types.Settings;
import messenger.types.User;

public class Messenger implements Runnable {

	private final Settings config;
	private MulticastSocket socket;
	private volatile int mID = 0;

	private Consumer<Message> messageConsumer;
	private Queue<Message> messagesToConsume = new LinkedList<>(); // For handling messages that are received before the UI initializes
	private Consumer<User> newUserConsumer;
	private Queue<User> newUsersToConsume = new LinkedList<>();
	private Consumer<User> leavingUserConsumer;
	private Queue<User> leavingUsersToConsume = new LinkedList<>();

	private ConcurrentHashMap<String, User> clients;
	private ArrayList<Message> messagesSent = new ArrayList<>();
	private ConcurrentHashMap<String, Long> lastPing;

	public Messenger(Settings config) throws IOException {
		this.config = config;
		socket = new MulticastSocket(config.port);
		socket.joinGroup(config.group);
		clients = new ConcurrentHashMap<>();
		lastPing = new ConcurrentHashMap<>();
		new Thread(new Pinger(this)).start();
	}

	// Public API
	public void sendMessage(String channel, String message) {
		Message toSend = new Message(++mID, config.username, channel, message);
		byte[] toSendBytes = toSend.getPacketBytes();
		DatagramPacket packet = new DatagramPacket(toSendBytes, toSendBytes.length, config.group, config.port);

		try {
			socket.send(packet);
			messagesSent.add(toSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addMessageConsumer(Consumer<Message> consumer) {
		this.messageConsumer = consumer;
		for (Message m : messagesToConsume) {
			consumer.accept(m);
		}
		this.messagesToConsume = null; // Unset queue since it's not needed anymore
	}

	public void addNewUserConsumer(Consumer<User> consumer) {
		this.newUserConsumer = consumer;
		for (User u : this.newUsersToConsume) {
			consumer.accept(u);
		}
		this.newUsersToConsume = null;
	}
	
	public void addLeavingUserConsumer(Consumer<User> consumer) {
		this.leavingUserConsumer = consumer;
		for (User u : this.leavingUsersToConsume) {
			consumer.accept(u);
		}
		this.leavingUsersToConsume = null;
	}

	// Private handlers
	private void onPacket(String packet) {
		try {
			if (packet.startsWith("msg|")) {
				Message packetMessage = Message.parse(packet);
				if (packetMessage.user.equals(config.username)) return; // Don't display messages from yourself
				User from = clients.get(packetMessage.user);
				if (from != null && packetMessage.id == from.lastID + 1) // Only process if they're in order, otherwise
					onMessage(packetMessage);							 // handle in retransmit logic
			}
			else if (packet.startsWith("ping|")) {
				Ping packetPing = Ping.parse(packet);
				if (packetPing.username.equals(config.username)) return; // Don't process pings from yourself
				onPing(packetPing);
			}
			else if (packet.startsWith("retransmit|")) {
				RetransmitRequest packetRequest = RetransmitRequest.parse(packet);
				if (packetRequest.user.equals(config.username)) return; // Don't process retransmits from yourself
				retransmit(packetRequest.requestedID);
			}
		} 
		catch (Exception e) {
			System.err.println("Exception while parsing packet: ");
			e.printStackTrace();
		}
	}

	private void onMessage(Message message) {
		if (this.messageConsumer != null)
			messageConsumer.accept(message);
		else
			messagesToConsume.add(message);
		setID(message.user, message.id);
	}

	private void onPing(Ping ping) {
		lastPing.put(ping.username, Calendar.getInstance().getTimeInMillis());
		if (clients.containsKey(ping.username)) {
			User pingUser = clients.get(ping.username);
			if (ping.lastMessageID > pingUser.lastID) { // Request missing messages
				byte[] requestBytes = String.format("retransmit|%s|%d", pingUser.username, pingUser.lastID + 1).getBytes();
				DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, config.group, config.port);
				try {
					socket.send(requestPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			addUser(new User(ping.username, ping.lastMessageID));
		}
	}

	private void onNewUser(User user) {
		System.out.println("New user " + user.username);
		if (this.newUserConsumer != null)
			newUserConsumer.accept(user);
		else
			newUsersToConsume.add(user);
	}

	private void onLeavingUser(User user) {
		System.out.println("Leaving user " + user.username);
		clients.remove(user.username);
		lastPing.remove(user.username);
		if (this.leavingUserConsumer != null)
			leavingUserConsumer.accept(user);
		else
			leavingUsersToConsume.add(user);
	}

	// Utilities
	private void setID(String user, int id) {
		if (clients.containsKey(user)) {
			clients.get(user).lastID = id;
		}
		else {
			addUser(new User(user, id));
		}
	}

	private void addUser(User user) {
		clients.put(user.username, user);
		onNewUser(user);
	}

	private void retransmit(int id) {
		try {
			Message toRetransmit = messagesSent.get(id);
			byte[] toRetransmitBytes = toRetransmit.getPacketBytes();
			DatagramPacket retransmitPacket = new DatagramPacket(toRetransmitBytes, toRetransmitBytes.length, config.group, config.port);
			socket.send(retransmitPacket);
		}
		catch (IndexOutOfBoundsException e) {
			System.out.println("Cannot retransmit message we don't have");
		} catch (IOException e) {
			System.out.println("Cannot send retransmit: ");
			e.printStackTrace();
		}
	}
	
	protected void ping() {
		// Check for leaving users
		long currentTime = Calendar.getInstance().getTimeInMillis();
		for (String user : this.lastPing.keySet()) {
			long lastPingTime = lastPing.get(user);
			if (currentTime - lastPingTime > 30000) {
				this.onLeavingUser(clients.get(user));
			}
		}
		Ping toSend = new Ping(config.username, mID, config.networkCard.getInetAddresses().nextElement());
		byte[] toSendBytes = toSend.getPacketBytes();
		DatagramPacket packet = new DatagramPacket(toSendBytes, toSendBytes.length, config.group, config.port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Interface implementations
	@Override
	public void run() {
		while (true) {
			DatagramPacket buf = new DatagramPacket(new byte[65535], 65535);
			try {
				socket.receive(buf);
				String msg = new String(buf.getData()).trim();
				System.out.println(msg);
				onPacket(msg);

			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Messenger disconnected.");
				break;
			}
		}
	}
}
