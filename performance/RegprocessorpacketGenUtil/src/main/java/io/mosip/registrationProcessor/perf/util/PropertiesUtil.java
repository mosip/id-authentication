package io.mosip.registrationProcessor.perf.util;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {

	public static String TEST_DATA_CSV_FILE_PATH;
	public static String NEW_PACKET_FOLDER_PATH;
	public static String VALID_PACKET_PATH_FOR_PACKET_GENERATION;
	public static String CHECKSUM_LOGFILE_PATH;
	public static String SYNCDATA__FILE_PATH;
	public static Integer NUMBER_OF_TEST_PACKETS;
	public static boolean USE_PROXY;
	public static String ENVIRONMENT;
	public static String BASE_URL;

	public void loadProperties(String configFile) {

		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configFile);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		TEST_DATA_CSV_FILE_PATH = properties.getProperty("TEST_DATA_CSV_FILE_PATH");
		NEW_PACKET_FOLDER_PATH = properties.getProperty("NEW_PACKET_FOLDER_PATH");
		VALID_PACKET_PATH_FOR_PACKET_GENERATION = properties.getProperty("PATH_FOR_VALID_REG_PACKETS");
		CHECKSUM_LOGFILE_PATH = properties.getProperty("CHECKSUM_LOGFILE_PATH");
		NUMBER_OF_TEST_PACKETS = Integer.parseInt(properties.getProperty("NUMBER_OF_TEST_PACKETS"));
		USE_PROXY = Boolean.parseBoolean(properties.getProperty("USE_PROXY"));
		SYNCDATA__FILE_PATH = properties.getProperty("SYNCDATA__FILE_PATH");
		ENVIRONMENT = properties.getProperty("ENVIRONMENT");
		BASE_URL = properties.getProperty("BASE_URL");
	}

}
