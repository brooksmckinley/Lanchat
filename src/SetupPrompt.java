import java.awt.Dimension;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import types.Settings;

public class SetupPrompt {

	private Settings settings;
	private JFrame settingsPrompt;

	public SetupPrompt() throws SocketException {
		settings = null;
		// Create elements
		settingsPrompt = new JFrame();
		JLabel usernameLabel = new JLabel("Username: ");
		JTextField usernameField = new JTextField();
		JLabel groupLabel = new JLabel("Multicast Group: ");
		JTextField groupField = new JTextField("229.10.20.30");
		JLabel portLabel = new JLabel("Port: ");
		JTextField portField = new JTextField("2345");
		JLabel interfaceLabel = new JLabel("Network Interface: ");
		JComboBox<String> interfaceMenu = new JComboBox<>();
		for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {
			NetworkInterface card = e.nextElement();
			if (card.supportsMulticast() && card.isUp()) {
				interfaceMenu.addItem(card.getName());
			}
		}
		JButton startButton = new JButton("Start");

		// Layout
		GroupLayout layout = new GroupLayout(settingsPrompt.getContentPane());
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(usernameLabel)
								.addComponent(groupLabel)
								.addComponent(portLabel)
								.addComponent(interfaceLabel))
						.addGroup(layout.createParallelGroup()
								.addComponent(usernameField)
								.addComponent(groupField)
								.addComponent(portField)
								.addComponent(interfaceMenu))
						)
				.addComponent(startButton)
				);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(usernameLabel)
						.addComponent(usernameField))
				.addGroup(layout.createParallelGroup()
						.addComponent(groupLabel)
						.addComponent(groupField))
				.addGroup(layout.createParallelGroup()
						.addComponent(portLabel) 
						.addComponent(portField))
				.addGroup(layout.createParallelGroup() 
						.addComponent(interfaceLabel)
						.addComponent(interfaceMenu))
				.addComponent(startButton)
				);

		// Interaction
		startButton.addActionListener((action) -> {
			submitSettings(usernameField.getText(), groupField.getText(), portField.getText(), (String) interfaceMenu.getSelectedItem());
		}); 

		// JFrame setup
		settingsPrompt.getContentPane().setLayout(layout);
		Dimension size = settingsPrompt.getPreferredSize();
		size.height += 30;
		size.width += 30;
		startButton.setSize(size.width, startButton.getHeight());
		settingsPrompt.setSize(size);
		settingsPrompt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		settingsPrompt.setVisible(true);

	}

	private void submitSettings(String usernameString, String groupString, String portString, String interfaceName) {
		if (!usernameString.matches("^[a-z0-9A-Z]*$") || usernameString.length() > 16 || usernameString.length() == 0) {
			new ErrorDialog(settingsPrompt, "Invalid Username", "Invalid username. Please select another.");
			return;
		}
		InetAddress group;
		try {
			group = InetAddress.getByName(groupString);
		} catch (UnknownHostException e) {
			new ErrorDialog(settingsPrompt, "Invalid Group", "Invalid IP Address.");
			e.printStackTrace();
			return;
		}
		if (!group.isMulticastAddress()) {
			new ErrorDialog(settingsPrompt, "Invalid Group", "IP address provided is not a multicast address.");
			return;
		}
		int port = -1;
		try {
			port = Integer.parseInt(portString);
		}
		catch (NumberFormatException e) {
			new ErrorDialog(settingsPrompt, "Invalid Port", "Invalid port. Please use a number from 1025 to 65535");
			e.printStackTrace();
			return;
		}
		if (port < 1025 || port > 65535) {
			new ErrorDialog(settingsPrompt, "Invalid Port", "Invalid port. Please use a number from 1025 to 65535");
			return;
		}
		NetworkInterface networkCard;
		try {
			networkCard = NetworkInterface.getByName(interfaceName);
		} catch (SocketException e) {
			new ErrorDialog(settingsPrompt, "Invalid Network Interface", "Invalid network interface. I'm not sure how this is supposed to appear but I'm writing this anyways.");
			e.printStackTrace();
			return;
		}
		settings = new Settings(usernameString, networkCard, group, port);
		synchronized (this) {
			this.notify();
		}
		this.settingsPrompt.dispose();
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
