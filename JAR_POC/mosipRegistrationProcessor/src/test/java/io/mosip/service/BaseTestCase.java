package io.mosip.service;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;
/**
 * This is the main class for TestNG that will setup and begin running tests.
 * All suite level before and after tests will be completed here.
 *
 */

public class BaseTestCase {
	private static Logger logger = Logger.getLogger(BaseTestCase.class);
	
	public static List<String> preIds=new ArrayList<String> ();
		
	/**
	 * Method that will take care of framework setup
	 */
	// GLOBAL CLASS VARIABLES
	private Properties prop;
	public static String ApplnURI;
	
	
	public static String SEPRATOR="";
	public  static String getOSType(){
		String type=System.getProperty("os.name");
		if(type.toLowerCase().contains("windows")){
			SEPRATOR="\\\\";
			return "WINDOWS";
		}else if(type.toLowerCase().contains("linux")||type.toLowerCase().contains("unix"))
		{
			SEPRATOR="/";
			return "OTHERS";
		}
		return null;
	}
	
	
	
	public void initialize()
	{
		try {
			getOSType();
			logger.info("We have created a Config Manager. Beginning to read properties!");
			prop = new Properties();
			InputStream inputStream = new FileInputStream("src"+BaseTestCase.SEPRATOR+"config"+BaseTestCase.SEPRATOR+"test.properties");
			prop.load(inputStream);
			logger.info("Setting test configs/TestEnvironment from " +  "src/config/test.properties");
			ApplnURI = prop.getProperty("testEnvironment");
			logger.info("Configs from properties file are set.");
			

		} catch (IOException e) {
			logger.error("Could not find the properties file.\n" + e);
		}
		
	
	}
	
	// ================================================================================================================
		// TESTNG BEFORE AND AFTER SUITE ANNOTATIONS
		// ================================================================================================================

		/**
		 * Before entire test suite we need to setup everything we will need.
		 */
		@BeforeSuite(alwaysRun = true)
		public void suiteSetup() {
			logger.info("Test Framework for Mosip api Initialized");
			logger.info("Logging initialized: All logs are located at " +  "src/logs/mosip-api-test.log");
			initialize();
			logger.info("Done with BeforeSuite and test case setup! BEGINNING TEST EXECUTION!\n\n");
		} // End suiteSetup

		/**
		 * After the entire test suite clean up rest assured
		 */
		/*@AfterSuite(alwaysRun = true)
		public void testTearDown(ITestContext ctx) {
			
			Calling up PreReg DB clean Up step
			if(preIds.size()>=1)
			{
            System.out.println("Elements from PreId List are========");
            for(String elem : preIds)
            	System.out.println(elem.toString());
            boolean status=false;
           status=prereg_dbread.prereg_db_CleanUp(preIds);
            if(status)
           	 logger.info("PreId is deleted from the DB");
            else
                   logger.info("PreId is NOT deleted from the DB");
			}
			RestAssured.reset();
			logger.info("\n\n");
			logger.info("Rest Assured framework has been reset because all tests have been executed.");
			logger.info("TESTING COMPLETE: SHUTTING DOWN FRAMEWORK!!");
		} // end testTearDown
*/
	}

