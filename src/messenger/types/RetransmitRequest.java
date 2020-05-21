package messenger.types;

public class RetransmitRequest {
	public String user;
	public int requestedID;
	public RetransmitRequest(String u, int r) {
		user = u; requestedID = r;
	}
	public static RetransmitRequest parse(String inp) throws IndexOutOfBoundsException, NumberFormatException {
		int start = 11;
		int end = inp.indexOf('|', start);
		String username = inp.substring(start, end);
		start = end + 1;
		int id = Integer.parseInt(inp.substring(start));
		return new RetransmitRequest(username, id);
	}
}
