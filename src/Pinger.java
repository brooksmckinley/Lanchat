
public class Pinger implements Runnable {
	Messenger msg;
	
	public Pinger(Messenger msg) {
		this.msg = msg;
	}

	@Override
	public void run() {
		while (true) {
			msg.ping();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
