import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SetupPrompt {

	private Settings settings;

	public SetupPrompt() {
		settings = null;
		JFrame settingsPrompt = new JFrame();
		settingsPrompt.add(new JLabel("Username: "));
		JTextField usernameField = new JTextField();
		settingsPrompt.add(usernameField);
		settingsPrompt.setVisible(true);
	}

	public Settings getSettings() throws InterruptedException {
		if (settings == null) {
			synchronized (this) {
				this.wait();
			}
		}
		return settings;
	}
}
