package io.mosip.preregistration.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.apache.maven.plugins.assembly.io.AssemblyReadException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

/**
 * @author Ashish Rastogi
 *
 */

public class PreRegistrationConfigData extends BaseTestCase implements ITest {
	Logger logger = Logger.getLogger(PreRegistrationConfigData.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	String testSuite;
	String preRegID = null;
	String createdBy = null;
	Response response = null;
	String preID = null;
	protected static String testCaseName = "";
	static String folder = "preReg";
	private static CommonLibrary commonLibrary = new CommonLibrary();
	ApplicationLibrary applnLib = new ApplicationLibrary();

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}

	/**
	 * Get Pre Registration configdata and compare with expected config data
	 */
	@Test
	public void getPreRegistrationConfigData() {
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		boolean status = false;
		outerKeys.add("responsetime");
		testSuite = "PreRegistrationConfigData/PreRegistrationConfigData_smoke";
		Response getPreRegistrationConfigData = lib.getPreRegistrationConfigData();
		JSONObject expectedRequest = lib.getRequest(testSuite);
		try {
			status = AssertResponses.assertResponses(getPreRegistrationConfigData, expectedRequest, outerKeys,
					innerKeys);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
			if (status)
				logger.info("No change in config parameter");
			else
			{
				logger.info("the config parameters do not match the expected auto suite requirements and that the suite will not run unless this is fixed manually");
			}
			
			
	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		System.out.println("method name:" + result.getMethod().getMethodName());
	}
}
