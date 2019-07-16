package io.mosip.authentication.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.tests.BiometricAuthentication;
import io.mosip.testDataDTO.YamlDTO;

/**
 * Initialization  of test data processor
 * 
 * @author Vignesh
 *
 */
public class TestDataProcessor {
	
	private static final Logger TESTDATAPROC_LOGGER = Logger.getLogger(TestDataProcessor.class);

	/**
	 * To initiate test data processor with following argument
	 * 
	 * @param testDataFileName
	 * @param testDataPath
	 * @param moduleName
	 */
	public static void initateTestDataProcess(String testDataFileName, String testDataPath, String moduleName) {
		try {
			TestDataConfig.setConfig(moduleName, testDataPath);
			File testDataFilePath = new File(
					RunConfigUtil.getResourcePath() + TestDataConfig.getTestDataPath() + testDataFileName);
			TestDataUtil.loadTestData(testDataFilePath);
			TestDataUtil.createTestData();
		} catch (Exception e1) {
			TESTDATAPROC_LOGGER.error("Exception Occured in test data processor: " + e1.getMessage());
		}
	}
	
	/**
	 * To get random testdata from yml file for the key
	 * 
	 * @author Arjun
	 * 
	 * @param modulename
	 * @param apiname
	 * @param testData
	 * @param dataParam
	 * @return testdata 
	 */
	@SuppressWarnings("unchecked")
	public static String getYamlData(String modulename, String apiname, String testData, String dataParam) {
		Yaml yaml = new Yaml();
		String testdata = null;
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(
					RunConfigUtil.getResourcePath() + modulename + "/" + apiname + "/" + testData + ".yaml");
			YamlDTO obj = new YamlDTO();
			obj.setYamlObject((Map<String, List<Object>>) yaml.load(inputStream));
			inputStream.close();
			List<Object> list = obj.getYamlObject().get(dataParam);
			Random random = new Random();
			testdata = (String) list.get(random.nextInt(list.size())).toString();
		} catch (FileNotFoundException e) {
			TESTDATAPROC_LOGGER.error("File is not available :" + e.getMessage());
		} catch (IOException e) {
			TESTDATAPROC_LOGGER.error("File is not available :" + e.getMessage());
		}
		return testdata;
	}
}
