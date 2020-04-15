package types;

public class Ping {
	String username;
	int lastMessageID;
	public Ping(String u, int lmi) {
		username = u; lastMessageID = lmi;
	}
}
