package io.mosip.authentication.idRepository.fw.util;

import java.io.File; 
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;

public class IdRepoTestsUtil extends AuthTestsUtil{
	
	private static final Logger IDAREPOSCRIPT_LOGGER = Logger.getLogger(AuthTestsUtil.class);
	/**
	 * The method returns run config path
	 */
	public static String getIdRepoRunConfigFile() {
		return RunConfigUtil.getResourcePath()+"idRepository/TestData/RunConfig/runConfiguration.properties";
	}
	
	/**
	 * The method return test data path from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataPath(String className, int index) {
		return getPropertyAsMap(new File(getIdRepoRunConfigFile()).getAbsolutePath().toString())
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
		return getPropertyAsMap(new File(getIdRepoRunConfigFile()).getAbsolutePath().toString())
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
			input = new FileInputStream(new File(RunConfigUtil.getResourcePath()+"idRepository/TestData/RunConfig/envRunConfig.properties").getAbsolutePath());
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
		return RunConfigUtil.getResourcePath()
				+ "idRepository/TestData/Security/GetCookie/getCookieRequest.json".toString();
	}
	public static String getCookieRequestFilePathForUinGenerator() {
		return RunConfigUtil.getResourcePath()
				+ "idRepository/TestData/Security/GetCookie/getCookieForUinGenerator.json".toString();
	}
	
	public static void copyIdrepoTestResource() {
		try {
			File source = new File(RunConfigUtil.getGlobalResourcePath() + "/idRepository");
			File destination = new File(RunConfigUtil.getGlobalResourcePath() + "/AuthenticationTestResource");
			FileUtils.copyDirectoryToDirectory(source, destination);
			IDAREPOSCRIPT_LOGGER.info("Copied the idrepository test resource successfully");
		} catch (Exception e) {
			IDAREPOSCRIPT_LOGGER.error("Exception occured while copying the file: "+e.getMessage());
		}
	}

}
