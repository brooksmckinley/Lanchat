package messenger.types;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ping {
	public String username;
	public int lastMessageID;
	public InetAddress returnAddr;
	public Ping(String u, int lmi, InetAddress rtr) {
		username = u; lastMessageID = lmi; returnAddr = rtr;
	}
	public static Ping parse(String packet) throws UnknownHostException {
		int start = 5;
		int end = packet.indexOf('|', start);
		int lastMessageID = Integer.parseInt(packet.substring(start, end));
		start = end + 1;
		end = packet.indexOf('|', start);
		String user = packet.substring(start, end);
		start = end + 1;
		String returnAddrString = packet.substring(start);
		return new Ping(user, lastMessageID, InetAddress.getByName(returnAddrString));
	}
}
