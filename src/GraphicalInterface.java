import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import types.Message;
import types.Settings;

public class GraphicalInterface {
	private final Settings config;
	
	public GraphicalInterface(Settings config, Messenger messenger) {
		this.config = config;
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
			String msg = String.format("<%s> %s", config.username, l.getActionCommand());
			messenger.sendMessage("default", l.getActionCommand());
			SwingUtilities.invokeLater(() -> {
				messages.setText(messages.getText() + msg + "\n");
				textBox.setText("");
			});
		});
		messageBox.add(textBox, BorderLayout.SOUTH);
		mainWin.add(messageBox);

		JPanel channelPanel = new JPanel();
		channelPanel.setLayout(new BorderLayout());
		DefaultListModel<String> channels = new DefaultListModel<>();
		channels.addElement("Default");
		channels.addElement("Alternate");
		JList<String> channelList = new JList<String>(channels);
		channelList.setSelectedIndex(0);
		JButton newChannelButton = new JButton("New Channel");
		channelPanel.add(channelList);
		channelPanel.add(newChannelButton, BorderLayout.SOUTH);
		mainWin.add(channelPanel, BorderLayout.WEST);
		

		DefaultListModel<String> users = new DefaultListModel<>();
		users.addElement("Bruhmoment");
		users.addElement("SomeoneElse");
		JList<String> usersList = new JList<>(users);
		mainWin.add(usersList, BorderLayout.EAST);
		textBox.setPreferredSize(new Dimension(textBox.getWidth(), newChannelButton.getPreferredSize().height));
		mainWin.setVisible(true);

		messenger.addMessageConsumer((msg) -> {
			String screenMSG = String.format("<%s> %s", msg.user, msg.msg);
			SwingUtilities.invokeLater(() -> {
				messages.setText(messages.getText() + screenMSG + "\n");
			});
		});
	}

	private void switchChannel(String channel) {
	}
}
