
package io.mosip.preregistration.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;
import com.ibm.icu.impl.Assert;

import io.mosip.dbaccess.PreRegDbread;
import io.mosip.kernel.service.ApplicationLibrary;
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
 * Test Class to perform Retrieve All PreRegId By RegCenterId related Positive
 * and Negative test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class RetriveAllPreRegIdByRegCenterId extends BaseTestCase implements ITest {

	/**
	 * Declaration of all variables
	 **/
	String preId = "";
	SoftAssert softAssert = new SoftAssert();
	static String testCaseName = "";
	Logger logger = Logger.getLogger(RetriveAllPreRegIdByRegCenterId.class);
	boolean status = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	String dest = "";
	String folderPath = "preReg/RetrivePreIdByRegCenterId";
	String outputFile = "RetrivePreIdByRegCenterIdOutput.json";
	String requestKeyFile = "RetrivePreIdByRegCenterIdRequest.json";
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	CommonLibrary commonLibrary = new CommonLibrary();
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	PreRegistrationUtil preRegUtil=new PreRegistrationUtil();
	BookingUtil bookUtil=new BookingUtil();
	String preReg_URI;

	// implement,IInvokedMethodListener
	public RetriveAllPreRegIdByRegCenterId() {

	}

	/**
	 * This method is used for reading the test data based on the test case name
	 * passed
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "RetrivePreIdByRegCenterId")
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
	 * Given Document Upload valid request when I Send GET request to
	 * https://mosip.io/preregistration/v1/appointment/availability/:registrationCenterId
	 * Then I should get success
	 * response with elements defined as per specifications Given Invalid
	 * request when I send GET request to
	 * https://mosip.io/preregistration/v1/appointment/availability/:registrationCenterId
	 * Then I should get Error
	 * response along with Error Code and Error messages as per Specification
	 */
	@Test(dataProvider = "RetrivePreIdByRegCenterId")
	public void retrivePreRegistrationByRegistrationCenterId(String testSuite, Integer i, JSONObject object)
			throws Exception {
		String fetchAppStr =null;

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();

		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		String frmDate;
		String toDate;
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDate fromDate = currentTime.toLocalDate();
		frmDate=fromDate.toString();
		toDate=currentTime.toString();
		String regCenterId;
		
		String val = null;
		String name = null;
		if (testCaseName.contains("smoke")) {
			val = testCaseName;
		} else {
			String[] parts = testCaseName.split("_");
			val = parts[0]+"_"+parts[1]+"_"+parts[2];
			name = parts[3];
		}

		// Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg(individualToken);
		try {
			preId = createApplicationResponse.jsonPath().get("response.preRegistrationId").toString();
		} catch (NullPointerException e) {
			Assert.fail("Failed to craete Application");
		}
		
		
		/* Fetch availability[or]center details */
		Response fetchCenter = preRegLib.FetchCentre(individualToken);

		/* Book An Appointment for the available data */
		Response bookAppointmentResponse = preRegLib.BookAppointment(fetchCenter, preId.toString(),individualToken);
		
		Response fetchAppDet = preRegLib.FetchAppointmentDetails(preId,individualToken);
		try {
			 fetchAppStr = fetchAppDet.jsonPath().get("response.appointment_date").toString();
		} catch (NullPointerException e) {
			Assert.fail("Exception while fetching appointment details");
		}
		
		logger.info("Fetch App Res::" + fetchAppStr);

		 toDate = fetchAppDet.jsonPath().get("response.appointment_date").toString();
		regCenterId = fetchAppDet.jsonPath().get("response.registration_center_id").toString();
		
		
		HashMap<String, String> parm = new HashMap<>();
		
		if(testCaseName.contains("registrationCenterId"))
		{
			regCenterId = actualRequest.get("registartion_center_id").toString();
		}
		else if (testCaseName.contains("fromDate"))
		{
			frmDate= actualRequest.get("from_date").toString();
		}
		else if (testCaseName.contains("toDate"))
		{
			toDate= actualRequest.get("to_date").toString();
		}
		
		
		parm.put("from_date", frmDate);
		parm.put("to_date", toDate);
		
		Actualresponse = bookUtil.retriveAllPreRegIdByRegCenterId(preReg_URI+regCenterId, parm, individualToken);
		logger.info("Retrive All Pre Reg ::"+Actualresponse.asString());
		if(testCaseName.contains("smoke"))
		{
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			innerKeys.add("registration_center_id");
			innerKeys.add("pre_registration_ids");
		}
		else
		{
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
		}
		
		status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
		
		
		/*switch (val) {
		case "preReg_RetrivePreIdByRegCenterId_smoke":
			// Retrieve all pre-registration ids by registration center id
			Response retrivePreIDFromRegCenId = preRegLib.retriveAllPreIdByRegId(fetchAppDet, preId,individualToken);
			logger.info("preReg_RetrivePreIdByRegCenterId_smoke::" + retrivePreIDFromRegCenId.asString());
			
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			innerKeys.add("registration_center_id");
			innerKeys.add("pre_registration_ids");
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(retrivePreIDFromRegCenId, Expectedresponse, outerKeys, innerKeys);
			break;
		case "prereg_RetrivePreIdByRegCenterId_registrationCenterId":
			String registartionCenterId = actualRequest.get("registartion_center_id").toString();
			HashMap<String, String> parm = new HashMap<>();
			parm.put("from_date", fromDate.toString());
			parm.put("to_date", toDate);
			String preReg_RetriveBookedPreRegIdsByRegId = preReg_URI + registartionCenterId;
			Actualresponse = applicationLibrary
					.get_Request_pathAndMultipleQueryParam(preReg_RetriveBookedPreRegIdsByRegId, parm);
			logger.info("My test case name:" + val + "_" + name + "My res::" + Actualresponse.asString());
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
			break;
		case "prereg_RetrivePreIdByRegCenterId_fromDate":
			String frmDate = actualRequest.get("from_date").toString();
			HashMap<String, String> invPreIdParm = new HashMap<>();
			invPreIdParm.put("from_date", frmDate);
			invPreIdParm.put("to_date", toDate);
			String preReg_RetriveBookedPreRegIdByRegId = preReg_URI + regCenterId;
			Actualresponse = applicationLibrary
					.get_Request_pathAndMultipleQueryParam(preReg_RetriveBookedPreRegIdByRegId, invPreIdParm);
			logger.info("My test case name:" + val + "_" + name + "My resuu::" + Actualresponse.asString());
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
			break;
			
		case "prereg_RetrivePreIdByRegCenterId_toDate":
			LocalDate curFrmDate = currentTime.toLocalDate();
			
			String invToDate = actualRequest.get("to_date").toString();
			HashMap<String, String> parmForToDate = new HashMap<>();
			parmForToDate.put("from_date", curFrmDate.toString());
			parmForToDate.put("to_date", invToDate);
			String preReg_RetriveBookedPreRegIdByRegId_InvTodate = preReg_URI + regCenterId;
			Actualresponse = applicationLibrary
					.get_Request_pathAndMultipleQueryParam(preReg_RetriveBookedPreRegIdByRegId_InvTodate, parmForToDate);
			logger.info("My test case name:" + val + "_" + name + "My resuu::" + Actualresponse.asString());
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
			break;
		default:
			break;
		}
*/		/*if (name != null) {
			testCaseName = val + "_" + name;
		}*/

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
		//Retrive All PreRegId by Registration Center Id Resource URI
		
		preReg_URI =preRegUtil.fetchPreregProp().get("preReg_RetriveBookedPreIdsByRegId");
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
			f.set(baseTestMethod, RetriveAllPreRegIdByRegCenterId.testCaseName);
			//f.set(baseTestMethod, "Pre Reg_RetriveAllPreRegIdByRegCenterId_" +RetriveAllPreRegIdByRegCenterId.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	
	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}
