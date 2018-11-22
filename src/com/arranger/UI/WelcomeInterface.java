package com.arranger.UI;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.arranger.main.DirectoryProcessor;

/**
 * Provide the path to your folder; one click and your folder is soothingly
 * arranged.
 * 
 * @see {@link https://stackoverflow.com/questions/1390592/check-if-file-is-already-open
 *      }
 * @category WARNING This method works for Windows platform only. Using on other
 *           platforms may result in loss of data (When files are open in some
 *           other application.)
 * 
 * @author Adarsh
 */

public class WelcomeInterface extends JFrame implements ActionListener {

	private static final Logger LOGGER = LogManager.getLogger(WelcomeInterface.class.getName());
	private static final long serialVersionUID = 1L;

	JButton button;
	JLabel label;
	JTextField text;
	Container con;
	JRootPane root;
	JPanel contentPane;
	JPanel topPanel;
	JPanel centerPanel;
	JPanel bottomPanel;

	DirectoryProcessor dp;
	Validator validator;

	WelcomeInterface() {
		con = getContentPane();
		con.setLayout(new GridLayout(3, 1));

		initButtons();
		initRoot();
		initPanes();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		validator = new Validator(rootPane);
		dp = DirectoryProcessor.getInstance();
	}

	private void initRoot() {
		root = getRootPane();
		root.setWindowDecorationStyle(HEIGHT);
		root.setBackground(Color.blue);
		root.setVisible(true);
	}

	private void initButtons() {
		button = new JButton("Click to arrange directory");
		button.setBackground(Color.LIGHT_GRAY);
		button.addActionListener(this);
	}

	private void initPanes() {
		initTopPanel();
		initCenterPanel();
		initBottomPanel();
	}

	private void initCenterPanel() {
		text = new JTextField("Your Path..", 10);
		text.setBackground(Color.white);

		centerPanel = new JPanel(new GridLayout(1, 2));
		centerPanel.setBackground(Color.white);
		centerPanel.add(text);
		centerPanel.add(button);
		con.add(centerPanel);
	}

	private void initBottomPanel() {
		JLabel label = new JLabel("    WARNING: This may not give expected result ");
		label.setFont(new Font("Courier New", Font.ITALIC, 15));
		label.setForeground(Color.DARK_GRAY);
		JLabel label2 = new JLabel("for platforms other than Windows.");
		label2.setFont(new Font("Courier New", Font.ITALIC, 15));
		label2.setForeground(Color.DARK_GRAY);

		bottomPanel = new JPanel();
		bottomPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
		bottomPanel.add(label);
		bottomPanel.add(label2);
		con.add(bottomPanel);
	}

	private void initTopPanel() {
		topPanel = new JPanel();
		topPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 5), "GitHub Link"));
		topPanel.add(new JLabel("The source code can be viewed at: https://github.com/Im-Adarsh/Arrangers_Java"));
		con.add(topPanel);
	}

	public static void main(String[] args) {
		LOGGER.debug("Method:: Main : Process begins.");
		try {
			WelcomeInterface mainClass = new WelcomeInterface();

			mainClass.setIconImage(ImageIO.read(new File("src/icons8-open-80.png")));
			mainClass.setResizable(false);
			mainClass.setSize(500, 400);
			mainClass.setVisible(true);
			mainClass.setTitle("Directory Arranger");

		} catch (IOException e) {
			LOGGER.error("Method:: Main : The favicon of the image couldn't be loaded. More info:");

			JOptionPane.showMessageDialog(null, "Couldn't load the application", null, JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String textEntered = text.getText();
		LOGGER.debug("Method:: actionPerformed : Received text: " + textEntered);
		try {
			if (validator.validInput(textEntered)) {
				JOptionPane.showMessageDialog(rootPane, "Arranger at work...", null, JOptionPane.INFORMATION_MESSAGE);

				dp.begin(textEntered);
				showDialog();
			} else {
				text.setText("Enter a correct path.");
			}
		} catch (IOException | RuntimeException e) {
			LOGGER.error("Method:: actionPerformed : File movement failed. More info:" + e);

			JOptionPane.showMessageDialog(rootPane, "A fatal error occured. Aborting program.", null, JOptionPane.ERROR_MESSAGE);

			try {
				Thread.sleep(200);
				System.exit(0);
			} catch (InterruptedException e1) {
				System.exit(1);
			}
		}
	}

	/**
	 * Show the number of files moved and not moved. Also, generate an error
	 * report if 1 or more files are not moved citing probable causes.
	 */
	private void showDialog() {
		JOptionPane.showMessageDialog(rootPane, dp.countOfMovedFiles + " File(s) moved successfully!", null,
				JOptionPane.INFORMATION_MESSAGE);
		if (dp.countofNotMovedFiles != 0) {
			JOptionPane.showMessageDialog(rootPane, dp.countofNotMovedFiles
					+ " File(s) are locked or already exist at target location. These couldn't be moved. An error report has been generated.",
					null, JOptionPane.WARNING_MESSAGE);
			generateErrorReport();
		}
	}

	private void generateErrorReport() {
		// TODO Auto-generated method stub

	}
}