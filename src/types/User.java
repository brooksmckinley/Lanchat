package types;

import java.net.InetAddress;

public class User {
	public String username;
	public int lastID;
	public InetAddress returnAddr; // Set on first ping
	public User(String u, int l) {
		username = u;
		lastID = l;
	}
}
