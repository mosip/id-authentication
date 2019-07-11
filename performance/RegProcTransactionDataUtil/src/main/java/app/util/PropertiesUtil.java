package app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	public static final String REGID_FILE = "C:\\MOSIP_PT\\Data\\regIds.csv";
	public static final String EXCEL_FILE = "C:\\MOSIP_PT\\Data\\regIds.xlsx";
	public static String ENVIRONMENT;

	public void loadProperties(String configFile) {

		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configFile);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ENVIRONMENT = properties.getProperty("ENVIRONMENT");
	}

}
