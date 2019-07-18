package io.mosip.authentication.testdata;

/**
 * To configure sourcepath, modulename (ida,prereg,kernel,reg) to call
 * respective module keyword class and test data path to initialize test data
 * processor
 * 
 * @author Athila
 *
 */
public class TestDataConfig {
	
	private static String srcPath;
	private static String moduleName;
	private static String testDataPath;
	/**
	 * Method current get test data path
	 * 
	 * @return string
	 */
	public static String getTestDataPath() {
		return testDataPath;
	}
	/**
	 * Method set current test data path
	 * 
	 * @param testDataPath
	 */
	public static void setTestDataPath(String testDataPath) {
		TestDataConfig.testDataPath = testDataPath;
	}
	/**
	 * The method get source path
	 * 
	 * @return sourcePath
	 */
	public static String getSrcPath() {
		return srcPath;
	}
	/**
	 * The method set source path
	 * 
	 * @param srcPath
	 */
	public static void setSrcPath(String srcPath) {
		TestDataConfig.srcPath = srcPath;
	}
	/**
	 * The method get moduleName
	 * 
	 * @return moduleName
	 */
	public static String getModuleName() {
		return moduleName;
	}
	/**
	 * The method set module name
	 * 
	 * @param moduleName
	 */
	public static void setModuleName(String moduleName) {
		TestDataConfig.moduleName = moduleName;
	}	
	/**
	 * The method set test processor config
	 * 
	 * @param moduleName
	 * @param testDataPath
	 */
	public static void setConfig(String moduleName, String testDataPath) {
		setModuleName(moduleName);
		setTestDataPath(testDataPath);
		//setSrcPath("/src/test/resources/");
	}

}
