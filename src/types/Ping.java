package types;

import java.net.InetAddress;

public class Ping {
	String username;
	int lastMessageID;
	InetAddress returnAddr;
	public Ping(String u, int lmi, InetAddress rtr) {
		username = u; lastMessageID = lmi; returnAddr = rtr;
	}
}
