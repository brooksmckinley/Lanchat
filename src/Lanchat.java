import java.io.IOException;

import javax.swing.UnsupportedLookAndFeelException;

import gui.GraphicalInterface;
import gui.SetupPrompt;
import messenger.Messenger;
import messenger.types.Settings;

public class Lanchat {
	
	
	public static void main(String[] args) throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
//		UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		SetupPrompt setup = new SetupPrompt();
		Settings config = setup.getSettings();
		System.out.println("Done waiting.");
		Messenger messenger = new Messenger(config);
		new GraphicalInterface(config, messenger);
		messenger.run();
	}

}
