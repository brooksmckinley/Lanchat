import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class GraphicalInterface {
	static String username = "Bruhmoment";
	
	public GraphicalInterface() {
		JFrame mainWin = new JFrame();
		mainWin.setTitle("Lanchat");
		mainWin.setSize(800, 600);
		mainWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel messageBox = new JPanel(new BorderLayout());
		JTextPane messages = new JTextPane();
		messages.setEditable(false);
		messageBox.add(new JScrollPane(messages));
		JTextField textBox = new JTextField();
		textBox.addActionListener((l) -> {
			System.out.println("Event: " + l.getActionCommand());
			String msg = String.format("<%s> %s", username, l.getActionCommand());
			messages.setText(messages.getText() + msg + "\n");
			textBox.setText("");
		});
		messageBox.add(textBox, BorderLayout.SOUTH);
		mainWin.add(messageBox);
		
		DefaultListModel<String> channels = new DefaultListModel<>();
		channels.addElement("Default Channel");
		channels.addElement("Alternate Channel");
		JList<String> channelList = new JList<String>(channels);
		channelList.setSelectedIndex(0);
		mainWin.add(channelList, BorderLayout.WEST);
		
		DefaultListModel<String> users = new DefaultListModel<>();
		users.addElement("Bruhmoment");
		users.addElement("SomeoneElse");
		JList<String> usersList = new JList<>(users);
		mainWin.add(usersList, BorderLayout.EAST);
		mainWin.setVisible(true);
	}
}
