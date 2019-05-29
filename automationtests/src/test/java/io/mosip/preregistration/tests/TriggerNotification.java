package io.mosip.preregistration.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
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

import io.mosip.preregistration.util.PreRegistrationUtil;
import io.mosip.preregistration.util.TriggerNotificationUtil;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Trigger notification related Positive and Negative test cases
 * @author Lavanya R
 * @since 1.0.0
 */

public class TriggerNotification extends BaseTestCase implements ITest {
	/**
	 * Declaration of all variables
	 **/
	String folder = "preReg";
	String preId = "";
	SoftAssert softAssert = new SoftAssert();
	static String testCaseName = "";
	Logger logger = Logger.getLogger(FetchAllApplicationCreatedByUser.class);
	boolean status = false;
	boolean statuOfSmokeTest = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	String preReg_URI;
	CommonLibrary commonLibrary = new CommonLibrary();
	String dest = "";
	String configPaths = "";
	String folderPath = "preReg/TriggerNotification";
	String outputFile = "TriggerNotificationRequestOutput.json";
	String requestKeyFile = "TriggerNotificationRequest.json";
	String testParam = null;
	boolean status_val = false;
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	TriggerNotificationUtil triggerNotUtil=new TriggerNotificationUtil();

	public TriggerNotification() {

	}

	/**
	 * Data Providers to read the input json files from the folders
	 * 
	 * @param context
	 * @return input request file
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	@DataProvider(name = "TriggerNotification")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
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
	 * Given Document Upload valid request when I Send POST request to
	 * https://mosip.io/preregistration/v1/notification/notify Then I should get success
	 * response with elements defined as per specifications Given Invalid
	 * request when I send POST request to
	 * https://mosip.io/preregistration/v1/notification/notify Then I should get Error
	 * response along with Error Code and Error messages as per Specification
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "TriggerNotification")
	public void triggerNotification(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);

		String testCase = object.get("testCaseName").toString();
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		if (testCaseName.contains("smoke")) {

			/* Creating the Pre-Registration Application */
			Response createApplicationResponse = preRegLib.CreatePreReg();
			logger.info("triggerNotifyResponsuyuyuyuyuye:"+testCaseName);
			//Response triggerNotifyResponse = preRegLib.TriggerNotification();
			Response triggerNotifyResponse =triggerNotUtil.TriggerNotification(testCaseName);
			logger.info("triggerNotifyResponse:" + triggerNotifyResponse.asString());
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(triggerNotifyResponse, Expectedresponse, outerKeys, innerKeys);


		} 
		else if(testCaseName.contains("preReg_TriggerNotification_id"))
		{

			String langCodeKey = commonLibrary.fetch_IDRepo().get("langCode.key");
			testSuite = "TriggerNotification/"+testCase;
			String configPath = "src/test/resources/" + folder + "/" + testSuite;
			File file = new File(configPath + "/AadhaarCard_POA.pdf");
			String value = null;
			JSONObject object1 = null;
			for (Object key : actualRequest.keySet()) {
				if (key.equals("request")) {
					object1 = (JSONObject) actualRequest.get(key);
					value = (String) object1.get(langCodeKey);
					actualRequest.put("requesttime", preRegLib.getCurrentDate());
					// object.put("pre_registartion_id",responseCreate.jsonPath().get("response[0].preRegistrationId").toString());
					// request.replace(key, object);
					object1.remove(langCodeKey);
				}
			}

			Response response = applicationLibrary.postFileAndJsonParam(preReg_URI, actualRequest, file, langCodeKey,
					value);

			outerKeys.add("responsetime");
			logger.info("Response TriggerNotification_id::" + response.asString()+"Test case name:"+testCaseName);
			status = AssertResponses.assertResponses(response, Expectedresponse, outerKeys, innerKeys);

		}
		
		else if(testCaseName.contains("preReg_TriggerNotification_requesttime"))
		{
			String langCodeKey = commonLibrary.fetch_IDRepo().get("langCode.key");
			testSuite = "TriggerNotification/"+testCase;
			String configPath = "src/test/resources/" + folder + "/" + testSuite;
			File file = new File(configPath + "/AadhaarCard_POA.pdf");
			String value = null;
			JSONObject object1 = null;
			for (Object key : actualRequest.keySet()) {
				if (key.equals("request")) {
					object1 = (JSONObject) actualRequest.get(key);
					value = (String) object1.get(langCodeKey);
					
					object1.remove(langCodeKey);
				}
			}

			Response response = applicationLibrary.postFileAndJsonParam(preReg_URI, actualRequest, file, langCodeKey,
					value);

			outerKeys.add("responsetime");
			logger.info("Response requesttime::" + response.asString()+"Test case name:"+testCaseName);
			status = AssertResponses.assertResponses(response, Expectedresponse, outerKeys, innerKeys);

		}
		else if(testCaseName.contains("preReg_TriggerNotification_version"))
		{
			String langCodeKey = commonLibrary.fetch_IDRepo().get("langCode.key");
			testSuite = "TriggerNotification/"+testCase;
			String configPath = "src/test/resources/" + folder + "/" + testSuite;
			File file = new File(configPath + "/AadhaarCard_POA.pdf");
			String value = null;
			JSONObject object1 = null;
			for (Object key : actualRequest.keySet()) {
				if (key.equals("request")) {
					object1 = (JSONObject) actualRequest.get(key);
					value = (String) object1.get(langCodeKey);
					actualRequest.put("requesttime", preRegLib.getCurrentDate());
					object1.remove(langCodeKey);
				}
			}

			Response response = applicationLibrary.postFileAndJsonParam(preReg_URI, actualRequest, file, langCodeKey,
					value);

			outerKeys.add("responsetime");
			logger.info("Response TriggerNotification_version::" + response.asString()+"Test case name:"+testCaseName);
			status = AssertResponses.assertResponses(response, Expectedresponse, outerKeys, innerKeys);

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
	 * Writing output into configpath
	 * 
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */

	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {

		String configPath = "src/test/resources/" + folderPath + "/" + outputFile;

		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}

		String source = "src/test/resources/" + folderPath + "/";
		CommonLibrary.backUpFiles(source, folderPath);

		// Add generated PreRegistrationId to list to be Deleted from DB AfterSuite
		preIds.add(preId);
	}

	/**
	 * Writing test case name into testng
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
			//f.set(baseTestMethod, TriggerNotification.testCaseName);
			f.set(baseTestMethod, "Pre Reg_TriggerNotification_" +TriggerNotification.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
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

		//Trigger Notification Resource URI
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_NotifyURI");
		//Fetch the generated Authorization Token by using following Kernel AuthManager APIs
		authToken = preRegLib.getToken();
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}
