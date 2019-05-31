package io.mosip.authentication.idRepositoty.fw.util;

import java.io.File; 
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;

public class IdRepoTestsUtil extends AuthTestsUtil{
	
	private static final Logger IDAREPOSCRIPT_LOGGER = Logger.getLogger(AuthTestsUtil.class);
	/**
	 * The method returns run config path
	 */
	public static String getIdRepoRunConfigFile() {
		return "src/test/resources/idRepository/TestData/RunConfig/runConfiguration.properties";
	}
	
	/**
	 * The method return test data path from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataPath(String className, int index) {
		return getPropertyAsMap(new File("./" + getIdRepoRunConfigFile()).getAbsolutePath().toString())
				.get(className + ".testDataPath[" + index + "]");
	}
	/**
	 * The method will return test data file name from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataFileName(String className, int index) {
		return getPropertyAsMap(new File("./" + getIdRepoRunConfigFile()).getAbsolutePath().toString())
				.get(className + ".testDataFileName[" + index + "]");
	}
	/**
	 * The method get env config details
	 * 
	 * @return properties
	 */
	private static Properties getRunConfigData() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			RunConfigUtil.objRunConfig.setUserDirectory();
			input = new FileInputStream(new File(RunConfigUtil.objRunConfig.getUserDirectory()+"src/test/resources/idRepository/TestData/RunConfig/envRunConfig.properties").getAbsolutePath());
			prop.load(input);
			return prop;
		} catch (Exception e) {
			IDAREPOSCRIPT_LOGGER.error("Exception: " + e.getMessage());
			return prop;
		}
	}
	/**
	 * The method get property value for the key
	 * 
	 * @param key
	 * @return string
	 */
	public static String getPropertyValue(String key) {
		return getRunConfigData().getProperty(key);
	}
	protected static String getCookieRequestFilePath() {
		return RunConfigUtil.objRunConfig.getUserDirectory() + RunConfigUtil.objRunConfig.getSrcPath()
				+ "idRepository/TestData/Security/GetCookie/getCookieRequest.json".toString();
	}
	public static String getCookieRequestFilePathForUinGenerator() {
		return RunConfigUtil.objRunConfig.getUserDirectory() + RunConfigUtil.objRunConfig.getSrcPath()
				+ "idRepository/TestData/Security/GetCookie/getCookieForUinGenerator.json".toString();
	}

}
