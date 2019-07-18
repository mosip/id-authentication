
package io.mosip.service;


import java.io.File; 
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.mosip.authentication.fw.util.AuthTestsUtil;

//import io.mosip.dbentity.TokenGenerationEntity;
//import io.mosip.util.TokenGeneration;

//import io.mosip.prereg.scripts.Create_PreRegistration;
import io.restassured.RestAssured;
/**
 * This is the main class for TestNG that will setup and begin running tests.
 * All suite level before and after tests will be completed here.
 *
 */


public class BaseTestCase{

	protected static Logger logger = Logger.getLogger(BaseTestCase.class);
	
	public static List<String> preIds=new ArrayList<String> ();
	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public ExtentTest test;
	
		
	/**
	 * Method that will take care of framework setup
	 */
	// GLOBAL CLASS VARIABLES
	private Properties prop;
	public static String ApplnURI;	
	public static String authToken;
	public static String regProcAuthToken;
	public static String getStatusRegProcAuthToken;
	public static String environment;
	public static String testLevel;
	public static String adminRegProcAuthToken;
		public static String SEPRATOR="";
	public static String buildNumber="";
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
			
			BasicConfigurator.configure();
			
			/**
			 * Make sure test-output is there 
			 */
			File testOutput = new File("test-output");
			File oldReport = new File(System.getProperty("user.dir")+"/test-output/emailable-report.html");
			oldReport.delete();
			testOutput.mkdirs();
			
			getOSType();
			logger.info("We have created a Config Manager. Beginning to read properties!");
			prop = new Properties();
			InputStream inputStream = new FileInputStream("src"+BaseTestCase.SEPRATOR+"config"+BaseTestCase.SEPRATOR+"test.properties");
			prop.load(inputStream);

			logger.info("Setting test configs/TestEnvironment from " + "src/config/test.properties");
			// ApplnURI = prop.getProperty("testEnvironment");

			
			System.setProperty("env.user","qa");
			System.setProperty("env.endpoint","https://qa.mosip.io");
			System.setProperty("env.testLevel", "smoke");
			environment = System.getProperty("env.user");
			logger.info("Environemnt is  ==== :" + environment);
			ApplnURI = System.getProperty("env.endpoint");
			logger.info("Application URI ======" + ApplnURI);
			testLevel = System.getProperty("env.testLevel");
			logger.info("Test Level ======" + testLevel);

			logger.info("Configs from properties file are set.");
			

		} catch (IOException e) {
			logger.error("Could not find the properties file.\n" + e);
		}
		
	
	}
	
	// ================================================================================================================
		// TESTNG BEFORE AND AFTER SUITE ANNOTATIONS
		// ================================================================================================================


		/*
		 * Saving TestNG reports to be published
		 */
		@BeforeSuite(alwaysRun = true)
		public void suiteSetup() {
			logger.info("Test Framework for Mosip api Initialized");
			logger.info("Logging initialized: All logs are located at " +  "src/logs/mosip-api-test.log");
			initialize();
			logger.info("Done with BeforeSuite and test case setup! BEGINNING TEST EXECUTION!\n\n");
			AuthTestsUtil.initiateAuthTest();
		} // End suiteSetup

		/**
		 * After the entire test suite clean up rest assured
		 */
		@AfterSuite(alwaysRun = true)
		public void testTearDown() {			
			RestAssured.reset();
			logger.info("\n\n");
			logger.info("Rest Assured framework has been reset because all tests have been executed.");
			logger.info("TESTING COMPLETE: SHUTTING DOWN FRAMEWORK!!");
		} 


	}

