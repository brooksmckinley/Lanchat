package messenger.types;

public class RetransmitRequest {
	public String user;
	public int requestedID;
	public RetransmitRequest(String u, int r) {
		user = u; requestedID = r;
	}
	
}
