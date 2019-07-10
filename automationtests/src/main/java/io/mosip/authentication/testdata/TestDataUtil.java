package io.mosip.authentication.testdata;

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * TestDataUtil will generate the request and response file for all the test
 * cases written in test data yml file
 * 
 * @author M1049813
 *
 */
public class TestDataUtil {
		
	private static final Logger TESTDATAUTILITY_LOGGER = Logger.getLogger(TestDataUtil.class);
	private static String scenarioPath="";
	private static String mapping="";
	private static Map<String, Map<String, Map<String, String>>> currentTestData;
	private static Map<String, Map<String, String>> currTestDataDic;
	private static String mappingPath;
	private static String testCaseName;
	/**
	 * The method get scenario path
	 * 
	 * @return scenario path
	 */
	public static String getScenarioPath() {
		return scenarioPath;
	}
	/**
	 * The method set scenario path
	 * 
	 * @param scenarioPath
	 */
	public static void setScenarioPath(String scenarioPath) {
		TestDataUtil.scenarioPath = scenarioPath;
	}	
	/**
	 * The method get mapping path
	 * 
	 * @return mappingDicPath
	 */
	public static String getMappingPath() {
		return mappingPath;
	}
	/**
	 * The method set mapping path
	 * 
	 * @param mappingPath
	 */
	public static void setMappingPath(String mappingPath) {
		TestDataUtil.mappingPath = mappingPath;
	}
	/**
	 * The method get current test data path
	 * 
	 * @return map
	 */
	public static Map<String, Map<String, Map<String, String>>> getCurrentTestData() {
		return currentTestData;
	}
	/**
	 * Method set current test data path
	 * 
	 * @param currentTestData
	 */
	public static void setCurrentTestData(Map<String, Map<String, Map<String, String>>> currentTestData) {
		TestDataUtil.currentTestData = currentTestData;
	}
	/**
	 * The method set current test data dictionary
	 * 
	 * @return map
	 */
	public static Map<String, Map<String, String>> getCurrTestDataDic() {
		return currTestDataDic;
	}
	/**
	 * The method set current test data dictionary
	 * 
	 * @param currTestDataDic
	 */
	public static void setCurrTestDataDic(Map<String, Map<String, String>> currTestDataDic) {
		TestDataUtil.currTestDataDic = currTestDataDic;
	}
	/**
	 * The method get current test case name
	 * 
	 * @return testCaseName
	 */
	public static String getTestCaseName() {
		return testCaseName;
	}
	/**
	 * The method set current test case Name
	 * 
	 * @param testCaseName
	 */
	public static void setTestCaseName(String testCaseName) {
		TestDataUtil.testCaseName = testCaseName;
	}	
	/**
	 * The method is to load all the test data in yml.
	 * 
	 * @param filePath
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static void loadTestData(File filePath) throws FileNotFoundException {
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(filePath.getAbsoluteFile());
			TestDataDto
					.setTestdata((Map<String, Map<String, Map<String, Map<String, Object>>>>) yaml.load(inputStream));
			inputStream.close();
			setFilePathFromTestdataFileName(filePath);
		} catch (IOException e) {
			TESTDATAUTILITY_LOGGER.error("Exception Occured in testdata processor : " + e.getMessage());
		}
	}	
	/**
	 * To set mapping file path and scenario path from the test data filename
	 * 
	 * @param filePath - Test data file path
	 */
	private static void setFilePathFromTestdataFileName(File filePath) {
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
	public static void createTestData() {
		for (Entry<String, Map<String, Map<String, Map<String, Object>>>> testdata : TestDataDto.getTestdata()
				.entrySet()) {
			Map<String, Map<String, Map<String, String>>> currenttest = new HashMap<String, Map<String, Map<String, String>>>();
			for (Entry<String, Map<String, Map<String, Object>>> testCase : testdata.getValue().entrySet()) {
				boolean flag = true;
				setTestCaseName(testCase.getKey());
				TESTDATAUTILITY_LOGGER.info("TestCaseName : " + getTestCaseName());
				Map<String, Map<String, String>> currentTestDatajsonFile = new HashMap<String, Map<String, String>>();
				Map<String, String> fieldValue = new HashMap<String, String>();
				TestDataUtil.setCurrTestDataDic(null);
				for (Entry<String, Map<String, Object>> jsonFile : testCase.getValue().entrySet()) {
					flag=true;
					String[] file = jsonFile.getKey().toString().split(Pattern.quote("."));
					String type = file[0];
					String jsonFileName = file[1];
					String inputJsonFilePath = "";
					if (type.equalsIgnoreCase("input"))
						inputJsonFilePath = new File(RunConfigUtil.getResourcePath()
								+ TestDataConfig.getTestDataPath() + "input/" + jsonFileName + ".json").getAbsolutePath();
					else if (type.equalsIgnoreCase("output"))
						inputJsonFilePath = new File( RunConfigUtil.getResourcePath()
								+ TestDataConfig.getTestDataPath() + "output/" + jsonFileName + ".json").getAbsolutePath();
					else if (type.equalsIgnoreCase("audit")) {
						String auditMappingPath = new File(RunConfigUtil.getResourcePath()
								+ "ida/TestData/Audit/" + jsonFileName + ".properties").getAbsolutePath();
						fieldValue = new HashMap<String, String>();
						for (Entry<String, Object> fieldvalMap : jsonFile.getValue().entrySet()) {
							fieldValue.put(fieldvalMap.getKey(), fieldvalMap.getValue().toString());
						}	
						fieldValue = Precondtion.parseAndWritePropertyFile(auditMappingPath, fieldValue,
								new File(RunConfigUtil.getResourcePath() + scenarioPath + "/"
										+ getTestCaseName() + "/" + jsonFileName + ".properties").getAbsolutePath());
						//Hashing UIN- kernel dependency	
						for (Entry<String, String> tempMap : fieldValue.entrySet()) {
							String value = "";
							if (tempMap.getKey().equals("ref_id") && jsonFile.getKey().contains("auth_transaction"))
								value = HMACUtils.digestAsPlainText(
										HMACUtils.generateHash(tempMap.getValue().toString().getBytes()));
							else
								value = tempMap.getValue().toString();
							fieldValue.put(tempMap.getKey(), value);
						}
						AuthTestsUtil.generateMappingDic(new File(RunConfigUtil.getResourcePath() + scenarioPath + "/"
										+ getTestCaseName() + "/" + jsonFileName + ".properties").getAbsolutePath(), fieldValue);
						flag = false;
					}
					else if (type.equalsIgnoreCase("email")) {
						String emailNotiConfigFile = new File(RunConfigUtil.getResourcePath()
								+ "ida/TestData/RunConfig/" + jsonFileName + ".properties").getAbsolutePath();
						fieldValue = new HashMap<String, String>();
						for (Entry<String, Object> fieldvalMap : jsonFile.getValue().entrySet()) {
							fieldValue.put(fieldvalMap.getKey(), fieldvalMap.getValue().toString());
						}
						fieldValue = Precondtion.parseAndWriteEmailNotificationPropertyFile(emailNotiConfigFile, fieldValue,
								new File(RunConfigUtil.getResourcePath() + scenarioPath + "/"
										+ getTestCaseName() + "/" + jsonFileName + ".properties").getAbsolutePath());
						flag = false;
					}
					else if (type.equalsIgnoreCase("endpoint")) {
						fieldValue = new HashMap<String, String>();
						for (Entry<String, Object> fieldvalMap : jsonFile.getValue().entrySet()) {
							fieldValue.put(fieldvalMap.getKey(), fieldvalMap.getValue().toString());
						}
						fieldValue = Precondtion.parseAndWritePropertyFile(fieldValue,
								new File(RunConfigUtil.getResourcePath() + scenarioPath + "/"
										+ getTestCaseName() + "/" + "url" + ".properties").getAbsolutePath());
						flag = false;
					}					
					if (flag) {
						fieldValue = new HashMap<String, String>();
						String mappingPath = new File(RunConfigUtil.getResourcePath()
								+ TestDataConfig.getTestDataPath() + mapping + ".properties").getAbsolutePath();
						setMappingPath(mappingPath);
						String outputJsonFilePath = new File(RunConfigUtil.getResourcePath()
								+ scenarioPath + "/" + getTestCaseName() + "/" + jsonFileName + ".json").getAbsolutePath();

						for (Entry<String, Object> fieldvalMap : jsonFile.getValue().entrySet()) {
							fieldValue.put(fieldvalMap.getKey(), fieldvalMap.getValue().toString());
						}
						FileUtil.createFile(new File(outputJsonFilePath), "");
						fieldValue = Precondtion.parseAndWriteTestDataJsonFile(inputJsonFilePath, fieldValue,
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
