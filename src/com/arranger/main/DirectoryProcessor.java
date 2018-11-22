package com.arranger.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * For the received and validated path, performs directory arrangement.
 * 
 * @see {@link com.arranger.UI.Validator} {@link com.arranger.main.FileUtils}
 * 
 * @author Adarsh Raj
 */
public class DirectoryProcessor {
	private Properties directories = new Properties();
	private Map<String, String> visited = new HashMap<>();

	private final static DirectoryProcessor INSTANCE = new DirectoryProcessor();
	private final static Logger LOGGER = LogManager.getLogger(DirectoryProcessor.class.getName());

	private boolean firstMiscItem = true;
	public int countOfMovedFiles;
	public int countofNotMovedFiles;
	public boolean needsNewErrorReport = true;

	private static FileUtils fileUtils;

	private DirectoryProcessor() {
		fileUtils = FileUtils.getInstance();
	}

	public static DirectoryProcessor getInstance() {
		return INSTANCE;
	}

	/**
	 * 
	 * @param sourcePath
	 *            The path of the folder to be arranged. Input from UI
	 * @see {@link com.arranger.UI.WelcomeInterface}.
	 * @throws IOException
	 * @throws RuntimeException
	 */
	public void begin(String sourcePath) throws IOException {
		// Reset old values to initial state.
		flush();

		if (directories.isEmpty()) {
			LOGGER.debug("Method:: begin : Loading properties file.");
			directories.load(new FileInputStream("D:/Extension.properties"));
			LOGGER.debug("Method:: begin : Properties file loaded.");
		}

		LOGGER.debug("Method:: begin : Starting to stream files from folder: " + sourcePath);

		Path source = Paths.get(sourcePath);

		// Iterate through the folder. Skip the folders and process the files.
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
			for (Path filePath : stream) {
				try {
					if (!fileUtils.isAFile(filePath)) {
						continue;
					}
					if (fileUtils.isFileLocked(filePath)) {
						LOGGER.warn("FILE: " + filePath + " IS LOCKED. IT SEEMS TO BE OPEN IN SOME APP OR CANNOT BE ACESSED.");
						countofNotMovedFiles++;

						fileUtils.writeErrorReport(filePath, null, "Files seem to be locked by some other app or cannot be accessed.");
						continue;
					}
					fileUtils.moveFile(filePath, processFile(filePath));
				} catch (IOException e) {
					countofNotMovedFiles++;
				}
			}
		}
		LOGGER.info("A total of " + countOfMovedFiles + " file(s) moved.");
	}

	private void flush() {
		countOfMovedFiles = 0;
		countofNotMovedFiles = 0;
		firstMiscItem = true;
		needsNewErrorReport = true;
		visited.clear();
	}

	private String processFile(Path filePath) {
		LOGGER.debug("Method:: processFile: Beginning to process file: " + filePath);

		String fileName = filePath.getFileName().toString();
		String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();

		/*
		 * If a file of this extension has been processed before, skip iterating
		 * through the properties file and get the name of the directory (from
		 * map) where files with same extension are stored.
		 */
		String destinationFolder = visited.get(fileExtension);

		// NULL indicates this extension is not encountered before.
		if (destinationFolder != null) {
			return destinationFolder + File.separator + fileExtension;
		}

		Set<Object> keySet = directories.keySet();

		// Iterate through the loaded properties file.
		for (Object key : keySet) {
			if (directories.getProperty((String) key).contains(fileExtension)) {
				/*
				 * Put this extension and key(Name of parent folder) as
				 * key-value pair.
				 */
				visited.put(fileExtension, (String) key);
				createFolder(filePath, (String) key + File.separator + fileExtension);
				return (String) key + File.separator + fileExtension;
			}
		}

		/*
		 * Create the MISC.. folder for those files whose parent folder cannot
		 * be recognized thorough the properties file.
		 */
		if (firstMiscItem) {
			LOGGER.info("Method:: begin : Creating the \"MISCELLANEOUS\" folder.");
			createFolder(filePath, "MISCELLANEOUS");
			firstMiscItem = false;
		}

		return "MISCELLANEOUS";
	}

	private void createFolder(Path item, String directoryName) {
		String tempVar = item.getParent().toString();
		File destination = new File(tempVar + File.separator + directoryName);

		if (!destination.exists()) {
			LOGGER.debug("Method:: createFolder : Creating folder with location: " + destination);
			destination.mkdirs();
		}
	}
}
