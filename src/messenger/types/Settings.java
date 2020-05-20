package messenger.types;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class Settings {
	public String username;
	public NetworkInterface networkCard;
	public InetAddress group;
	public int port;
	public Settings(String u, NetworkInterface nC, InetAddress g, int p) {
		username = u;
		networkCard = nC;
		group = g;
		port = p;
	}
}
