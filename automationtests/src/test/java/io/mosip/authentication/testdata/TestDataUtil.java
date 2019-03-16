package io.mosip.authentication.testdata;

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.testdata.precondtion.Precondtion;

/**
 * TestDataUtil will generate the request and response file for all the test
 * cases written in test data yml file
 * 
 * @author M1049813
 *
 */
public class TestDataUtil {
		
	private static Logger logger = Logger.getLogger(TestDataUtil.class);
	private Precondtion objPrecondtion = new Precondtion();
	private static String scenarioPath="";
	public static String getScenarioPath() {
		return scenarioPath;
	}
	public static void setScenarioPath(String scenarioPath) {
		TestDataUtil.scenarioPath = scenarioPath;
	}

	private static String mapping="";
	private FileUtil objFileUtil = new FileUtil();
	private static Map<String, Map<String, Map<String, String>>> currentTestData;
	private static Map<String, Map<String, String>> currTestDataDic;
	private static String mappingPath;
	public static String getMappingPath() {
		return mappingPath;
	}
	public static void setMappingPath(String mappingPath) {
		TestDataUtil.mappingPath = mappingPath;
	}
	public static Map<String, Map<String, Map<String, String>>> getCurrentTestData() {
		return currentTestData;
	}
	public static void setCurrentTestData(Map<String, Map<String, Map<String, String>>> currentTestData) {
		TestDataUtil.currentTestData = currentTestData;
	}
	public static Map<String, Map<String, String>> getCurrTestDataDic() {
		return currTestDataDic;
	}
	public static void setCurrTestDataDic(Map<String, Map<String, String>> currTestDataDic) {
		TestDataUtil.currTestDataDic = currTestDataDic;
	}
	public static String getTestCaseName() {
		return testCaseName;
	}
	public static void setTestCaseName(String testCaseName) {
		TestDataUtil.testCaseName = testCaseName;
	}

	private static String testCaseName;
	
	/**
	 * The method is to load all the test data in yml.
	 * 
	 * @param filePath
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void loadTestData(File filePath) throws FileNotFoundException
	{
		Yaml yaml = new Yaml();
		InputStream inputStream = new FileInputStream(filePath.getAbsoluteFile());		
		TestDataDto.setTestdata((Map<String, Map<String, Map<String, Map<String, Object>>>>) yaml.load(inputStream));
		setFilePathFromTestdataFileName(filePath);
	}
	
	/**
	 * To set mapping file path and scenario path from the test data filename
	 * 
	 * @param filePath - Test data file path
	 */
	private void setFilePathFromTestdataFileName(File filePath) {
		String[] folderList = filePath.getName().split(Pattern.quote("."));
		String temp = "";
		for (int i = 1; i < folderList.length - 2; i++) {
			temp = temp + "/" + folderList[i];
		}
		scenarioPath = temp;
		setScenarioPath(scenarioPath);
		mapping = folderList[folderList.length - 2];
		setMappingPath(mapping);
	}
	
	/**
	 * The method is to create test data in a configured folder structure
	 * 
	 * No Parameter
	 */
	public void createTestData() {
		for (Entry<String, Map<String, Map<String, Map<String, Object>>>> testdata : TestDataDto.getTestdata()
				.entrySet()) {
			Map<String, Map<String, Map<String, String>>> currenttest = new HashMap<String, Map<String, Map<String, String>>>();
			for (Entry<String, Map<String, Map<String, Object>>> testCase : testdata.getValue().entrySet()) {
				boolean flag = true;
				setTestCaseName(testCase.getKey());
				logger.info("TestCaseName : " + getTestCaseName());
				Map<String, Map<String, String>> currentTestDatajsonFile = new HashMap<String, Map<String, String>>();
				for (Entry<String, Map<String, Object>> jsonFile : testCase.getValue().entrySet()) {
					String[] file = jsonFile.getKey().toString().split(Pattern.quote("."));
					String type = file[0];
					String jsonFileName = file[1];
					String inputJsonFilePath = "";
					if (type.equalsIgnoreCase("input"))
						inputJsonFilePath = TestDataConfig.getUserDirectory() + TestDataConfig.getSrcPath()
								+ TestDataConfig.getTestDataPath() + "input/" + jsonFileName + ".json";
					else if (type.equalsIgnoreCase("output"))
						inputJsonFilePath = TestDataConfig.getUserDirectory() + TestDataConfig.getSrcPath()
								+ TestDataConfig.getTestDataPath() + "output/" + jsonFileName + ".json";
					else if (type.equalsIgnoreCase("audit")) {
						String auditMappingPath = TestDataConfig.getUserDirectory() + TestDataConfig.getSrcPath()
								+ "ida/TestData/Audit/" + jsonFileName + ".properties";
						Map<String, String> fieldValue = new HashMap<String, String>();
						for (Entry<String, Object> fieldvalMap : jsonFile.getValue().entrySet()) {
							fieldValue.put(fieldvalMap.getKey(), fieldvalMap.getValue().toString());
						}
						fieldValue = objPrecondtion.parseAndWritePropertyFile(auditMappingPath, fieldValue,
								TestDataConfig.getUserDirectory() + TestDataConfig.getSrcPath() + scenarioPath + "/"
										+ getTestCaseName() + "/" + jsonFileName + ".properties");
						flag = false;
					}
					Map<String, String> fieldValue = new HashMap<String, String>();
					if (flag) {
						String mappingPath = TestDataConfig.getUserDirectory() + TestDataConfig.getSrcPath()
								+ TestDataConfig.getTestDataPath() + mapping + ".properties";
						setMappingPath(mappingPath);
						String outputJsonFilePath = TestDataConfig.getUserDirectory() + TestDataConfig.getSrcPath()
								+ scenarioPath + "/" + getTestCaseName() + "/" + jsonFileName + ".json";

						for (Entry<String, Object> fieldvalMap : jsonFile.getValue().entrySet()) {
							fieldValue.put(fieldvalMap.getKey(), fieldvalMap.getValue().toString());
						}
						objFileUtil.createFile(new File(outputJsonFilePath), "");
						fieldValue = objPrecondtion.parseAndWriteTestDataJsonFile(inputJsonFilePath, fieldValue,
								outputJsonFilePath, mappingPath);
					}
					currentTestDatajsonFile.put(jsonFile.getKey(), fieldValue);
					setCurrTestDataDic(currentTestDatajsonFile);
				}
				currenttest.put(getTestCaseName(), currentTestDatajsonFile);
				setCurrentTestData(currenttest);
			}
		}
	}

}
