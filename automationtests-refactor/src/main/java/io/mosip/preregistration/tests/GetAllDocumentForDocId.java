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
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform GetAll Document For Document Id related Positive and
 * Negative test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class GetAllDocumentForDocId extends BaseTestCase implements ITest {

	/**
	 * Declaration of all variables
	 **/
	String preId = "";
	String docId = "";
	SoftAssert softAssert = new SoftAssert();
	static String testCaseName = "";
	Logger logger = Logger.getLogger(GetAllDocumentForDocId.class);
	boolean status = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	CommonLibrary commonLibrary = new CommonLibrary();
	String preReg_URI;
	ApplicationLibrary appLib=new ApplicationLibrary();
	HashMap<String, String> parm = new HashMap<>();
	String dest = "";
	String folderPath = "preReg/GetAllDocumentForDocId";
	String outputFile = "GetAllDocumentForDocIdOutput.json";
	String requestKeyFile = "GetAllDocumentForDocIdRequest.json";
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();

	// implement,IInvokedMethodListener
	public GetAllDocumentForDocId() {

	}

	/**
	 * This method is used for reading the test data based on the test case name
	 * passed
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "GetAllDocumentForDocId")
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
	 * https://mosip.io/preregistration/v1/documents/preregistration/:preRegistrationId
	 *  Then I should get success
	 * response with elements defined as per specifications Given Invalid
	 * request when I send GET request to
	 * https://mosip.io/preregistration/v1/documents/:documentId?preRegistrationId=:preRegistrationId
	 * Then I should get Error
	 * response along with Error Code and Error messages as per Specification
	 * 
	 */
	@Test(dataProvider = "GetAllDocumentForDocId")
	public void getAllDocumentForDocId(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);

		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		// Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg(individualToken);
		preId = createApplicationResponse.jsonPath().get("response.preRegistrationId").toString();

		// Document Upload for created application
		//Response docUploadResponse = preRegLib.documentUploadParm(createApplicationResponse, preId);
		Response docUploadResponse = preRegLib.documentUpload(createApplicationResponse, preId,null, individualToken);

		// Get PreId from Document upload response
		preId = docUploadResponse.jsonPath().get("response.preRegistrationId").toString();

		// Get docId from Document upload response
		docId = preRegLib.getDocId(docUploadResponse);

		if (testCaseName.contains("smoke")) {

			// Get All Document For PreID
			Response getAllDocRes = preRegLib.getAllDocumentForDocId(preId, docId,individualToken);
			logger.info("Get All Doc Res:" + getAllDocRes.asString());
			outerKeys.add("responsetime");
			innerKeys.add("document");
			status = AssertResponses.assertResponses(getAllDocRes, Expectedresponse, outerKeys, innerKeys);

		} else if (testCaseName.contains("GetAllDocumentByDocIdByPassingInvalidDocumentId")) {
			docId = actualRequest.get("documentId").toString();

			parm.put("preRegistrationId", preId);

			String preRegURL = preReg_URI + docId;

			Actualresponse = appLib.getWithQueryParam(preRegURL, parm,individualToken);
			boolean value = testCaseName.contains("EmptyValue")?(outerKeys.add("timestamp")):outerKeys.add("responsetime");
			
			//outerKeys.add("responsetime");
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

		} else if (testCaseName.contains("GetAllDocumentByDocIdByPassingInvalidPreRegistrationId")) {
		String	preIdVal = actualRequest.get("preRegistrationId").toString();
			parm.put("preRegistrationId", preIdVal);

			String preRegURI = preReg_URI + docId;

			Actualresponse = appLib.getWithQueryParam(preRegURI, parm,individualToken);
			//boolean value = testCaseName.contains("EmptyValue")?(outerKeys.add("timestamp")):outerKeys.add("responsetime");
			
			outerKeys.add("responsetime");
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
		if (finalStatus.equals("Fail"))
			setFinalStatus = false;
		else if (finalStatus.equals("Pass"))
			setFinalStatus = true;
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
		// Document Upload Resource URI
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_GetDocByDocId");
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
			//f.set(baseTestMethod, GetAllDocumentForDocId.testCaseName);
			f.set(baseTestMethod, "Pre Reg_GetAllDocumentForDocId_" +BookingAppointment.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	
	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}