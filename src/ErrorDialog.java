import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class ErrorDialog {
	public ErrorDialog(Frame owner, String title, String text) {
		JDialog dialog = new JDialog(owner, title, true);
		JLabel warningText = new JLabel(text);
		JButton okayButton = new JButton("Okay");
		dialog.add(warningText);
		dialog.add(okayButton, BorderLayout.SOUTH);
		Dimension size = dialog.getPreferredSize();
		size.height += 30;
		size.width += 30;
		dialog.setSize(size);
		okayButton.addActionListener((action) -> {
			dialog.dispose();
		});
		dialog.setVisible(true);
	}
}
