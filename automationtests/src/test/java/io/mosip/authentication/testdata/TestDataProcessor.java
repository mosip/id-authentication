package io.mosip.authentication.testdata;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.tests.BioIrisAuthentication;

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
			File testDataFilePath = new File(TestDataConfig.getUserDirectory() + TestDataConfig.getSrcPath()
					+ TestDataConfig.getTestDataPath() + testDataFileName);
			objTestDataUtil.loadTestData(testDataFilePath);
			objTestDataUtil.createTestData();
		} catch (Exception e1) {
			logger.error("Exception Occured in test data processor: " + e1.getMessage());
		}
	}

}
