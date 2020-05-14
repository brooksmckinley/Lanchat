import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

import types.Message;
import types.Settings;

public class Messenger implements Runnable {

	private GraphicalInterface gui;
	private final Settings config;
	private MulticastSocket socket;
	private volatile int mID = 0;
	private Consumer<Message> consumer;
	private Queue<Message> toConsume; // For handling messages that are received before the UI initializes
	HashMap<String, Integer> clients;

	public Messenger(Settings config) throws IOException {
		this.gui = null;
		this.config = config;
		socket = new MulticastSocket(config.port);
		socket.joinGroup(config.group);
		clients = new HashMap<>();
		toConsume = new LinkedList<>();
		new Thread(new Pinger(this)).start();
	}

	public void sendMessage(String channel, String message) {
		byte[] toSend = String.format("msg|%d|%s|%s|%s", ++mID, config.username, channel, message).getBytes();
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length, config.group, config.port);

		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onPacket(String packet) {
		try {
			if (packet.startsWith("msg|")) {
				Message packetMessage = Message.parse(packet);
				if (packetMessage.user.equals(config.username)) return; // Don't display messages from yourself
				if (this.consumer != null)
					consumer.accept(packetMessage);
				else
					toConsume.add(packetMessage);
				clients.put(packetMessage.user, packetMessage.id);
			}
			else if (packet.startsWith("ping|")) {
				
			}
		} 
		catch (Exception e) {
			System.err.println("Exception while parsing packet: ");
			e.printStackTrace();
		}
	}

	public void addMessageConsumer(Consumer<Message> consumer) {
		this.consumer = consumer;
		for (Message m : toConsume) {
			consumer.accept(m);
		}
		this.toConsume = null; // Unset queue since it's not needed anymore
	}

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

	public void ping() {
		byte[] toSend = String.format("ping|%d|%s|%s", mID, config.username, 
				config.networkCard.getInetAddresses().nextElement().toString()).getBytes();
		DatagramPacket packet = new DatagramPacket(toSend, toSend.length, config.group, config.port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
