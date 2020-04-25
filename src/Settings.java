import java.net.InetAddress;
import java.net.NetworkInterface;

public class Settings {
	String username;
	NetworkInterface networkCard;
	InetAddress group;
	int port;
	public Settings(String u, NetworkInterface nC, InetAddress g, int p) {
		username = u;
		networkCard = nC;
		group = g;
		port = p;
	}
}
