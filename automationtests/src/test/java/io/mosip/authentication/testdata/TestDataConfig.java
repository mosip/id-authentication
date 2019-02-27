package io.mosip.authentication.testdata;

/**
 * To configure sourcepath, current user directory , modulename (ida,prereg,kernel,reg) to call
 * respective module keyword class and test data path to initialize test data
 * processor
 * 
 * @author Vignesh
 *
 */
public class TestDataConfig {
	
	private static String srcPath;
	private static String userDirectory;
	private static String moduleName;
	private static String testDataPath;
	public static String getTestDataPath() {
		return testDataPath;
	}
	public static void setTestDataPath(String testDataPath) {
		TestDataConfig.testDataPath = testDataPath;
	}
	public static String getSrcPath() {
		return srcPath;
	}
	public static void setSrcPath(String srcPath) {
		TestDataConfig.srcPath = srcPath;
	}
	public static String getUserDirectory() {
		return userDirectory;
	}
	public static void setUserDirectory(String userDirectory) {
		TestDataConfig.userDirectory = userDirectory;
	}
	public static String getModuleName() {
		return moduleName;
	}
	public static void setModuleName(String moduleName) {
		TestDataConfig.moduleName = moduleName;
	}
	
	public void setConfig(String moduleName, String testDataPath) {
		setModuleName(moduleName);
		setTestDataPath(testDataPath);
		setSrcPath("/src/test/resources/");
		setUserDirectory(System.getProperty("user.dir"));
	}

}
