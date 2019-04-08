package io.mosip.authentication.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.tests.BioIrisAuthentication;
import io.mosip.testDataDTO.YamlDTO;

/**
 * Initialization  of test data processor
 * 
 * @author Vignesh
 *
 */
public class TestDataProcessor {
	
	private TestDataUtil objTestDataUtil = new TestDataUtil();	
	private TestDataConfig objTestDataConfig = new TestDataConfig();
	private static Logger logger = Logger.getLogger(TestDataProcessor.class);

	/**
	 * To initiate test data processor with following argument
	 * 
	 * @param testDataFileName
	 * @param testDataPath
	 * @param moduleName
	 */
	public void initateTestDataProcess(String testDataFileName,String testDataPath, String moduleName) {
		try {
			objTestDataConfig.setConfig(moduleName, testDataPath);
			File testDataFilePath = new File("./"+TestDataConfig.getSrcPath()
					+ TestDataConfig.getTestDataPath() + testDataFileName);
			objTestDataUtil.loadTestData(testDataFilePath);
			objTestDataUtil.createTestData();
		} catch (Exception e1) {
			logger.error("Exception Occured in test data processor: " + e1.getMessage());
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
	public String getYamlData(String modulename, String apiname, String testData, String dataParam) {
		Yaml yaml = new Yaml();
		String testdata = null;
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(
					"src/test/resources/" + modulename + "/" + apiname + "/" + testData + ".yaml");
			YamlDTO obj = new YamlDTO();
			//obj.setYamlObject((Map<String, List<Object>>) yaml.load(inputStream));
			//List<Object> list = obj.getYamlObject().get(dataParam);
			Random random = new Random();
			//testdata = (String) list.get(random.nextInt(list.size())).toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return testdata;
	}
}

