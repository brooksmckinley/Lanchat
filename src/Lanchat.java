import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Lanchat {
	
	
	public static void main(String[] args) throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		SetupPrompt setup = new SetupPrompt();
		setup.getSettings();
		System.out.println("Done waiting.");
		GraphicalInterface gui = new GraphicalInterface();
	}

}
