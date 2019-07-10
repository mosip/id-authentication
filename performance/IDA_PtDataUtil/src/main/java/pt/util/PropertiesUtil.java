package pt.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	private static final String CONFIG_FILE = "variables_config.properties";
	private static final String EXT_FILE = "C:\\MOSIP\\Test-data POC\\IDA\\variables_config.properties";

	public static String TSPID;
	public static String BASE_PATH;
	public static String DATA_FILE;

	public void loadProperties(String configFile) {
		InputStream input = null;
		// System.out.println(CONFIG_FILE);
		// input = this.getClass().getResourceAsStream(CONFIG_FILE);
		// System.out.println(input);
		Properties configProperties = new Properties();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configFile);
		try {
			configProperties.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BASE_PATH = configProperties.getProperty("BASE_PATH");
		DATA_FILE = configProperties.getProperty("DATA_FILE");
		TSPID = configProperties.getProperty("TSPID");
		System.out.println(BASE_PATH);
		System.out.println(DATA_FILE);
		System.out.println(TSPID);
	}

	public static void loadProperties() {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(EXT_FILE);
			// System.out.println(CONFIG_FILE);
			// input = PropertiesUtil.class.getResourceAsStream(CONFIG_FILE);
			// System.out.println(input);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			BASE_PATH = prop.getProperty("BASE_PATH");
			DATA_FILE = prop.getProperty("DATA_FILE");
			TSPID = prop.getProperty("TSPID");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
