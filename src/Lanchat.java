import java.io.IOException;
import java.net.SocketException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import types.Settings;

public class Lanchat {
	
	
	public static void main(String[] args) throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		SetupPrompt setup = new SetupPrompt();
		Settings config = setup.getSettings();
		System.out.println("Done waiting.");
		Messenger messenger = new Messenger(config);
		GraphicalInterface gui = new GraphicalInterface(config, messenger);
		messenger.run();
	}

}
