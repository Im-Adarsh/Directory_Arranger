package com.arranger.UI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.JRootPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * For the path entered at UI, perform validation. Validation ranges from
 * correct path pattern for a directory to existence of a directory at that
 * path. This application has been tested on Windows platform only.
 * 
 * @See {@link com.arranger.UI.WelcomeInterface}
 * 
 */
public class Validator {
	private static final Logger LOGGER = LogManager.getLogger(Validator.class.getName());

	JRootPane rootPane;

	public Validator(JRootPane rootPane2) {
		this.rootPane = rootPane2;
	}

	public boolean validInput(String textEntered) {
		if (textEntered.trim().isEmpty() || textEntered == null) {
			LOGGER.error("Method:: validInput : No or blank input received.");

			JOptionPane.showMessageDialog(rootPane, "Please enter a path.", "No Value Entered", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (isNotADirectory(textEntered.trim())) {
			LOGGER.error("Method:: validInput : The entered path doesn't point to a file.");

			JOptionPane.showMessageDialog(rootPane, "Your path doesn't seem to point to a file. Try again!", null,
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (isNotValidInput(textEntered.trim())) {
			LOGGER.error("Method:: validInput : The entered path fails Reg-Ex check.");

			JOptionPane.showMessageDialog(rootPane, "Invalid path. Try removing the slash at last, if any.", null,
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * 
	 * Method's visibility public for UT purpose
	 * 
	 * @param textEntered
	 *            Input path
	 * @return A boolean value based on regular-expression check
	 */
	public boolean isNotValidInput(String textEntered) {
		if (textEntered.matches("^[a-zA-Z]:([\\\\\\/]){0,2}(([\\\\\\/]){0,2}[\\w\\. -]+)*")) {
			return false;
		}
		return true;
	}

	/**
	 * Check if received path points to a directory
	 * 
	 * @param textEntered
	 *            Input path
	 * @return A boolean value based on whether entered path points to a
	 *         directory.
	 */
	private boolean isNotADirectory(String textEntered) {
		Path path = Paths.get(textEntered);
		return !Files.isDirectory(path);
	}
}
