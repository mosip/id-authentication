package io.mosip.preregistration.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.preregistration.service.PreRegistrationApplicationLibrary;
import io.mosip.preregistration.util.PreRegistrationUtil;
import io.mosip.preregistration.util.QRCodeUtil;

import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform QRCode For PreRegId related Positive and Negative test
 * cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class QRCode extends BaseTestCase implements ITest {

	/**
	 * Declaration of all variables
	 **/
	Logger logger = Logger.getLogger(QRCode.class);
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	String preId = "";
	String docId = "";
	SoftAssert softAssert = new SoftAssert();
	static String testCaseName = "";
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	PreRegistrationUtil preregUtil = new PreRegistrationUtil();
	ApplicationLibrary appLib = new ApplicationLibrary();
	PreRegistrationLibrary lib=new PreRegistrationLibrary();
	QRCodeUtil qrCodeUtil = new QRCodeUtil();
	HashMap<String, String> parm = new HashMap<>();
	String dest = "";
	String folderPath = "preReg/QRCode";
	String outputFile = "QRCodeRequestOutput.json";
	String requestKeyFile = "QRCodeRequest.json";
	String preReg_URI;
	String cond1;
	String cond2;

	// implement,IInvokedMethodListener
	public QRCode() {

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
	@DataProvider(name = "QRCode")
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
	 * Given QR Code valid request when I Send POST request to
	 * https://mosip.io/preregistration/v1/qrCode/generate Then I should get
	 * success response with elements defined as per specifications Given
	 * Invalid request when I send POST request to
	 * https://mosip.io/preregistration/v1/qrCode/generate Then I should get
	 * Error response along with Error Code and Error messages as per
	 * Specification
	 */
	@Test(dataProvider = "QRCode")
	public void qrCode(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		if (!(testCaseName.contains("requesttime"))) {
			actualRequest.put("requesttime", lib.getCurrentDate());
		}
		// QR Code Response
		Response qrCoderes = qrCodeUtil.QRCode(preReg_URI, actualRequest, individualToken);
		if (testCaseName.contains("smoke")) {
			outerKeys.add("responsetime");
			innerKeys.add("qrcode");
		}

		outerKeys.add("responsetime");
		logger.info("QR Code Valid TC Response::" + qrCoderes.asString());
		if(testCaseName.contains("requesttime_withAlphabets")||testCaseName.contains("requesttime_withInvalidDateOfSlashInsteadOfHypen")||testCaseName.contains("requesttime_withSpecialCharacters"))
		{
			innerKeys.add("message");
			status = AssertResponses.assertResponses(qrCoderes, Expectedresponse, outerKeys, innerKeys);
		}
		else
		{
			status = AssertResponses.assertResponses(qrCoderes, Expectedresponse, outerKeys, innerKeys);
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
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun = true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];

		testCaseName = object.get("testCaseName").toString();

		// QR Code Resource URI
		preReg_URI = preregUtil.fetchPreregProp().get("preReg_QRCodeURI");

		// Fetch the generated Authorization Token by using following Kernel
		// AuthManager APIs
		if(!lib.isValidToken(individualToken))
		{
			individualToken=lib.getToken();
		}

		// Fetching the testcase name from the property file
		cond1 = preregUtil.fetchPreregProp().get("smoke");
		cond2 = preregUtil.fetchPreregProp().get("reqTime");
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
			f.set(baseTestMethod, QRCode.testCaseName);

		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}


	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}