import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ActionMap;
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
	private ArrayList<String> channelNames = new ArrayList<>();
	private HashMap<String, Integer> channelIDs = new HashMap<>();
	private ArrayList<JTextPane> channelBuffers = new ArrayList<>();
	private int currentChannelID;
	
	// Components
	private JFrame mainWin = new JFrame();
	private JPanel messageBox = new JPanel(new BorderLayout());
	private JScrollPane messageScroller = new JScrollPane();
	private JTextPane currentChannelBuffer = null;
	private JTextField textBox = new JTextField();
	private JPanel channelPanel = new JPanel(new BorderLayout());
	private DefaultListModel<String> channels = new DefaultListModel<>();
	private JList<String> channelList = new JList<>(channels);
	private JButton newChannelButton = new JButton("New Channel");
	private DefaultListModel<String> users = new DefaultListModel<>();
	private JList<String> usersList = new JList<>(users);
	
	public GraphicalInterface(Settings config, Messenger messenger) {
		this.config = config;
		mainWin.setTitle("Lanchat");
		mainWin.setSize(800, 600);
		mainWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Message area and "write a message" box
		switchChannel(addChannel("default"));
		messageBox.add(messageScroller);
		textBox.addActionListener((l) -> {
			System.out.println("Event: " + l.getActionCommand());
			String msg = String.format("<%s> %s", config.username, l.getActionCommand());
			messenger.sendMessage(channelNames.get(currentChannelID), l.getActionCommand());
			SwingUtilities.invokeLater(() -> {
				currentChannelBuffer.setText(currentChannelBuffer.getText() + msg + "\n");
				textBox.setText("");
			});
		});
		messageBox.add(textBox, BorderLayout.SOUTH);
		mainWin.add(messageBox);

		// Channel listing
		channelList.getSelectionModel().addListSelectionListener((e) -> {
			switchChannel(channelList.getSelectedIndex());
		});
		channelList.setSelectedIndex(currentChannelID);
		channelPanel.add(new JScrollPane(channelList));
		newChannelButton.addActionListener((l) -> {
			new NewChannelDialog(this.mainWin, (event) -> {
				if (!channelIDs.containsKey(event.getActionCommand()))
					switchChannel(addChannel(event.getActionCommand()));
			});
		}); 
		channelPanel.add(newChannelButton, BorderLayout.SOUTH);
		mainWin.add(channelPanel, BorderLayout.WEST);
		

		// User list
		users.addElement("Bruhmoment");
		users.addElement("SomeoneElse");
		mainWin.add(new JScrollPane(usersList), BorderLayout.EAST);
		textBox.setPreferredSize(new Dimension(textBox.getWidth(), newChannelButton.getPreferredSize().height));
		mainWin.setVisible(true);

		// Integration with Messenger
		messenger.addMessageConsumer((msg) -> {
			String screenMSG = String.format("<%s> %s", msg.user, msg.msg);
			SwingUtilities.invokeLater(() -> {
				Integer receivedChannelID = channelIDs.get(msg.channel);
				if (receivedChannelID == null)
					receivedChannelID = addChannel(msg.channel);
				JTextPane receivedBuffer = channelBuffers.get(receivedChannelID);
				receivedBuffer.setText(receivedBuffer.getText() + screenMSG + "\n");
			});
		});
	}
	
	private int addChannel(String channel) {
		channelNames.add(channel);
		channels.addElement(channel);
		JTextPane messages = new JTextPane();
		messages.setEditable(false);
		channelBuffers.add(messages);
		channelIDs.put(channel, channelNames.size() - 1);
		return channelNames.size() - 1; // return channel ID
	}
	
	private void switchChannel(int channelIndex) {
		System.out.println("Switching channel to " + channelIndex);
		currentChannelBuffer = channelBuffers.get(channelIndex);
		currentChannelID = channelIndex;
		SwingUtilities.invokeLater(() -> {
			messageScroller.setViewportView(currentChannelBuffer);
		});
	}
}
