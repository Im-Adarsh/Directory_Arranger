package com.arranger.UI;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class MessageFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	JButton button;
	JScrollPane scrollpane;

	MessageFrame(JList<String> displayList) {
		Container conn = getContentPane();
		conn.setLayout(new FlowLayout());

		button = new JButton("OK");
		button.setSize(100, 100);
		conn.add(button);

		scrollpane = new JScrollPane(displayList);
		conn.add(scrollpane);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		button.addActionListener(this);

	}

	public static void initialize(JList<String> displayList) {
		MessageFrame sd = new MessageFrame(displayList);
		sd.setSize(300, 300);
		sd.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
	}
}
