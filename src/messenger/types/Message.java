package messenger.types;
public class Message {
	public String user;
	public String channel;
	public String msg;
	public int id;
	
	public Message(int i, String u, String c, String m) {
		id = i; user = u; channel = c; msg = m;
	}
	
	public static Message parse(String inp) throws IndexOutOfBoundsException, NumberFormatException {
		int start = 4;
		int end = inp.indexOf('|', start);
		int messageID = Integer.parseInt(inp.substring(start, end));
		start = end + 1;
		end = inp.indexOf('|', start);
		String user = inp.substring(start, end);
		start = end + 1;
		end = inp.indexOf('|', start);
		String channel = inp.substring(start, end);
		start = end + 1;
		String message = inp.substring(start);
		Message packetMessage = new Message(messageID, user, channel, message);
		return packetMessage;
	}
	
	public byte[] getPacketBytes() {
		return String.format("msg|%d|%s|%s|%s", id, user, channel, msg).getBytes();
	}
}
