package io.mosip.authentication.idrepo.fw.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Arjun Chandramohan
 *
 */
public class PropertyFileLoader {
	/**
	 * @param pageName
	 *            accept the name of the config properties file
	 * @return return config file object to read config file
	 */
	public Properties readPropertyFile(String modulename, String apiname, String requestjsonname) {

		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("src/test/resources/" + modulename + "/" + apiname + "/" + requestjsonname
					+ "Path" + ".properties");
			;
			prop.load(input);
		} catch (FileNotFoundException e) {
			//logger.info("Provide correct name for the config file");
			System.exit(0);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
}
