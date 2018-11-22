package com.arranger.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtils {
	private final static Logger LOGGER = LogManager.getLogger(FileUtils.class.getName());
	private final static FileUtils INSTANCE = new FileUtils();
	private String currentFileSource;

	private FileUtils() {
	}

	public static FileUtils getInstance() {
		return INSTANCE;
	}

	/**
	 * Write an error-report meant for a comprehensive understanding of crash
	 * report (meant for end-user).
	 * 
	 * For the multiple crash in same thread i.e., during arrangement in same
	 * directory, write all logs in same file.
	 * 
	 * 
	 * @param sourcePath
	 * @param targetpath
	 * @param probableCause
	 *            The most probable cause for the failure of this file movement.
	 * @throws IOException
	 */
	public void writeErrorReport(Path sourcePath, Path targetpath, String probableCause) throws IOException {
		/*
		 * for the current arrangement, create error-report once; write all
		 * crash-logs in this file.
		 */
		if (DirectoryProcessor.getInstance().needsNewErrorReport) {
			Calendar cal = Calendar.getInstance();

			StringBuilder report = new StringBuilder();
			createReportString(cal, report, sourcePath, targetpath, probableCause);

			currentFileSource = "D:/Error_Report_" + (cal.getTimeInMillis() + new Random().nextInt(100));
			writeReport(report, false);

			// For current session, put all crash reports in a single file.
			DirectoryProcessor.getInstance().needsNewErrorReport = false;

			LOGGER.info("Method:: writeReport | Error report generated successfully.");
		} else {
			StringBuilder report = new StringBuilder();

			createReportString(Calendar.getInstance(), report, sourcePath, targetpath, probableCause);
			writeReport(report, true);

			LOGGER.info("Method:: writeReport | Error report generated successfully.");
		}

	}

	private void writeReport(StringBuilder report, boolean sameSessionReport) throws IOException {
		Writer writer = null;
		BufferedWriter bufferedWriter = null;
		try {
			File error_report = new File(currentFileSource);
			if (!sameSessionReport)
				error_report.createNewFile();
			writer = new FileWriter(error_report, true);

			bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.append(report.toString());

		} catch (IOException ie) {
			LOGGER.error("Method:: writeReport: Failed to generate error report. More info:" + ie);
			throw ie;
		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
			if (writer != null)
				writer.close();
		}
	}

	private void createReportString(Calendar cal, StringBuilder report, Path sourcePath, Path targetpath, String probableCause) {
		report.append("			********************************			\r\n			"
				+ "********************************			\r\n" + "Error Report created at ");
		report.append(cal.getTime() + "\r\n");
		report.append("			********************************			\r\n			"
				+ "********************************			\r\n");
		report.append("File Source: ");
		report.append(sourcePath + "\r\n");
		report.append("Target destination: ");
		report.append(targetpath + "\r\n");
		report.append("Probable Cause:");
		report.append(probableCause + "\r\n");
	}

	/**
	 * Moves file from provide source-path to dynamically generated
	 * target-destination.
	 * 
	 * @param sourcePath
	 *            Complete path of the source file
	 * @param targetDir
	 *            Run-time generated target path.
	 * @throws IOException
	 */
	void moveFile(Path sourcePath, String targetDir) throws IOException {
		Path target = null;
		try {
			String tempVar = sourcePath.getParent() + File.separator + targetDir + File.separator + sourcePath.getFileName().toString();
			target = Paths.get(tempVar);
			Files.move(sourcePath, target);
		} catch (FileAlreadyExistsException e) {
			LOGGER.error(
					"Method:: moveFile : Failed to move file: " + sourcePath + " This file already exists at target location. More:" + e);

			writeErrorReport(sourcePath, target, "File Already Exists at target location.");
			throw e;
		} catch (FileNotFoundException e) {
			LOGGER.error("Method:: moveFile : Failed to move file: " + sourcePath + "Target folder doesn't exists/cannot be accessed." + e);

			writeErrorReport(sourcePath, target,
					"The directory or sub-directory where this file is to be moved couldn't be found or accessed.");
			throw e;
		} catch (IOException e) {
			LOGGER.error("Method:: moveFile : Failed to move file: " + sourcePath + e);

			writeErrorReport(sourcePath, target, "There was trouble in accessing source or target locations.(PROBABLE)");
			throw e;
		} catch (RuntimeException re) {
			LOGGER.error("Method:: moveFile : Failed to move file: " + sourcePath + re);
			throw re;
		}
		DirectoryProcessor.getInstance().countOfMovedFiles++;
		LOGGER.debug("Method:: moveFile: File " + sourcePath + " moved to " + target);
	}

	boolean isAFile(Path path) {
		File file = new File(path.toString());
		return file.isFile();
	}

	/**
	 * WARNING: This method works for Windows platform only.
	 * 
	 * @param item
	 *            The complete path of the file.
	 * @return A boolean variable base on whether the file is locked or not.
	 * @see {@link https://stackoverflow.com/questions/1390592/check-if-file-is-already-open
	 *      }
	 */
	boolean isFileLocked(Path item) {
		return !item.toFile().renameTo(item.toFile());
	}
}
