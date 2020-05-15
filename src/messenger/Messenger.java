package messenger;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

import types.Message;
import types.Settings;
import types.User;

public class Messenger implements Runnable {

	private final Settings config;
	private MulticastSocket socket;
	private volatile int mID = 0;
	
	private Consumer<Message> messageConsumer;
	private Queue<Message> messagesToConsume; // For handling messages that are received before the UI initializes
	private Consumer<User> newUserConsumer;
	private Queue<User> newUsersToConsume;
	private Consumer<User> leavingUserConsumer;
	private Queue<User> leavingUsersToConsume;
	
	HashMap<String, User> clients;

	public Messenger(Settings config) throws IOException {
		this.config = config;
		socket = new MulticastSocket(config.port);
		socket.joinGroup(config.group);
		clients = new HashMap<>();
		messagesToConsume = new LinkedList<>();
		new Thread(new Pinger(this)).start();
	}

	// Public API
	public void sendMessage(String channel, String message) {
		byte[] toSend = String.format("msg|%d|%s|%s|%s", ++mID, config.username, channel, message).getBytes();
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length, config.group, config.port);

		try {
			socket.send(packet);
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
	
	// Private handlers
	private void onPacket(String packet) {
		try {
			if (packet.startsWith("msg|")) {
				Message packetMessage = Message.parse(packet);
				if (packetMessage.user.equals(config.username)) return; // Don't display messages from yourself
				onMessage(packetMessage);
			}
			else if (packet.startsWith("ping|")) {
				
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
	
	private void onNewUser(User user) {
		if (this.newUserConsumer != null)
			newUserConsumer.accept(user);
		else
			newUsersToConsume.add(user);
	}
	
	private void onLeavingUser(User user) {
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
			clients.put(user, new User(user, id));
			
		}
	}

	protected void ping() {
		byte[] toSend = String.format("ping|%d|%s|%s", mID, config.username, 
				config.networkCard.getInetAddresses().nextElement().toString()).getBytes();
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length, config.group, config.port);
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
