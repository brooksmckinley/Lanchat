import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NewChannelDialog {
	public NewChannelDialog(JFrame parent, ActionListener callback) {
		JDialog dialog = new JDialog(parent, "New Channel", true);
		JPanel promptContainer = new JPanel(new FlowLayout());
		JLabel newChannelLabel = new JLabel("New channel: ");
		JTextField textField = new JTextField();
		JButton okayButton = new JButton("Create Channel");
		
		ActionListener submitListener = (l) -> {
			if (isValidChannel(textField.getText())) {
				callback.actionPerformed(new ActionEvent(dialog, l.getID(), textField.getText()));
				dialog.dispose();
			}
			else {
				new ErrorDialog(parent, "Invalid Channel", "Invalid channel name.");
			}
		};
		okayButton.addActionListener(submitListener);
		textField.addActionListener(submitListener);
		Dimension textFieldSize = textField.getPreferredSize();
		textFieldSize.width += 100;
		textField.setPreferredSize(textFieldSize);
		
		promptContainer.add(newChannelLabel);
		promptContainer.add(textField);
		dialog.add(promptContainer);
		dialog.add(okayButton, BorderLayout.SOUTH);
		Dimension size = dialog.getPreferredSize();
		size.width += 30;
		size.height += 30;
		dialog.setSize(size);
		dialog.setVisible(true);
	}

	private boolean isValidChannel(String text) {
		if (text.contains("|") || text.length() > 12 || text.length() < 1) return false;
		return true;
	}
}
