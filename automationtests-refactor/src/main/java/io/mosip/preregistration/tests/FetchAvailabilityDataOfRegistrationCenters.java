package io.mosip.preregistration.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.dbaccess.PreRegDbread;
import io.mosip.preregistration.util.BookingUtil;
import io.mosip.preregistration.util.PreRegistrationUtil;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Fetch Availability Data Of RegistrationCenters related
 * Positive and Negative test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class FetchAvailabilityDataOfRegistrationCenters extends BaseTestCase implements ITest {

	/**
	 * Declaration of all variables
	 **/
	String preId = "";
	SoftAssert softAssert = new SoftAssert();
	static String testCaseName = "";
	Logger logger = Logger.getLogger(FetchAvailabilityDataOfRegistrationCenters.class);
	boolean status = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
    String dest = "";
	String folderPath = "preReg/FetchAvailabilityDataOfRegCenters";
	String outputFile = "FetchAvailabilityDataOfRegCentersOutput.json";
	String requestKeyFile = "FetchAvailabilityDataOfRegcentersRequest.json";
	CommonLibrary commonLibrary = new CommonLibrary();
	String preReg_URI;
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
    PreRegistrationUtil preRegUtil=new PreRegistrationUtil();
    BookingUtil bookingUtil = new BookingUtil();
    
    
	// implement,IInvokedMethodListener
	public FetchAvailabilityDataOfRegistrationCenters() {

	}

	/**
	 * This method is used for reading the test data based on the test case name
	 * passed
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "fetchRegCenterDetails")
	public Object[][] readData(ITestContext context) throws Exception {
		switch (testLevel) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	/*
	 * Given Fetch Availability Details valid data when User Send GET request to
	 * https://mosip.io/preregistration/v1/appointment/availability/:registrationCenterId
	 *  Then the user should be able to retrieve Pre-Registration appointment details
	 *  by pre-Registration id.
	 * 
	 * Given Invalid request when when User Send GET request to
	 * https://mosip.io/preregistration/v1/appointment/availability/:registrationCenterId Then
	 * the user should get Error response along with Error Code and Error
	 * messages as per Specification
	 * 
	 */
	@Test(dataProvider = "fetchRegCenterDetails")
	public void FetchAvailabilityDataOfRegistrationCenters(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);

		if (testCaseName.contains("smoke")) {			
			Actualresponse = bookingUtil.FetchCentre("null",individualToken);
			logger.info("Fetch Book App:" + Actualresponse.asString());
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			innerKeys.add("regCenterId");
			innerKeys.add("centerDetails");
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

		}

		else {
			String regCenterid = actualRequest.get("registrationCenterId").toString();
			
			Actualresponse = bookingUtil.FetchCentre(regCenterid,individualToken);
			
			logger.info("Fetch Book App:" + Actualresponse.asString());
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

		}

		if (status) {
			finalStatus = "Pass";
			softAssert.assertAll();
			object.put("status", finalStatus);
			arr.add(object);
		} else {
			finalStatus = "Fail";
		}

		boolean setFinalStatus = false;

		setFinalStatus = finalStatus.equals("Pass") ? true : false;

		Verify.verify(setFinalStatus);
		softAssert.assertAll();
	}

	/**
	  * This method is used for fetching test case name
	  * @param method
	  * @param testdata
	  * @param ctx
	  */
	@BeforeMethod(alwaysRun = true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];

		testCaseName = object.get("testCaseName").toString();
		//Fetch Availability data by registration center Id Resource URI
		
		preReg_URI = preRegUtil.fetchPreregProp().get("preReg_FetchCenterIDURI");
		//Fetch the generated Authorization Token by using following Kernel AuthManager APIs
		if(!preRegLib.isValidToken(individualToken))
		{
			individualToken=preRegLib.getToken();
		}
	}

	/**
	 * This method is used for generating report
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			//f.set(baseTestMethod, FetchAvailabilityDataOfRegistrationCenters.testCaseName);
			f.set(baseTestMethod, "Pre Reg_FetchAvailabilityDataOfRegistrationCenters_" +FetchAvailabilityDataOfRegistrationCenters.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}



	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}
