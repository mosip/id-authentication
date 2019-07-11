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
import org.json.simple.parser.ParseException;
import org.testng.Assert;
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
 * Test Class to perform Delete Document By DocId related Positive and Negative
 * test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class DeleteDocumentByDocId extends BaseTestCase implements ITest {

	/**
	 * Declaration of all variables
	 **/
	String preId = "";
	String docId = "";
	SoftAssert softAssert = new SoftAssert();
	static String testCaseName = "";
	Logger logger = Logger.getLogger(DeleteDocumentByDocId.class);
	boolean status = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	String dest = "";
	String folderPath = "preReg/DeleteDocumentByDocId";
	String outputFile = "DeleteDocumentByDocIdRequestOutput.json";
	String requestKeyFile = "DeleteDocumentByDocIdRequest.json";
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	CommonLibrary commonLibrary = new CommonLibrary();
	String preReg_URI;
	ApplicationLibrary appLib = new ApplicationLibrary();
	HashMap<String, String> parm = new HashMap<>();

	/* implement,IInvokedMethodListener */
	public DeleteDocumentByDocId() {

	}

	/**
	 * This method is used for reading the test data based on the test case name
	 * passed
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "DeleteDocumentByDocId")
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
	 * Given Delete Documet By Document Id valid request when I Send Delete request to
	 * https://mosip.io/preregistration/v1/documents/:documentId?preRegistrationId=:preRegistrationId
	 *  Then I should get success
	 * response with elements defined as per specifications Given Invalid
	 * request when I send Delete request to
	 * https://mosip.io/preregistration/v1/documents/:documentId?preRegistrationId=:preRegistrationId
	 *  Then I should get Error
	 * response along with Error Code and Error messages as per Specification
	 * 
	 */
	@Test(dataProvider = "DeleteDocumentByDocId")
	public void deleteDocumentByDocId(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);

		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		String testCase = object.get("testCaseName").toString();

		// Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg(individualToken);
		preId = createApplicationResponse.jsonPath().get("response.preRegistrationId").toString();

		// Document Upload for created application

		Response docUploadResponse = preRegLib.documentUploadParm(createApplicationResponse, preId,individualToken);
       // logger.info("Doc upload res::"+docUploadResponse.asString());
		// Get PreId from Document upload response
		try {
			preId = docUploadResponse.jsonPath().get("response.preRegistrationId").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception occured while uploading document ");
		}
		// Get docId from Document upload response
		try {
			docId = docUploadResponse.jsonPath().get("response.docId").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Document id is not present in document upload response");
		}
		if (testCaseName.contains("smoke")) {

			// Delete All Document by Document Id
			Response delAllDocByPreId = preRegLib.deleteAllDocumentByDocId(docId, preId,individualToken);
			outerKeys.add("responsetime");
			innerKeys.add("multipartFile");
			  
             logger.info("Del Doc res:"+delAllDocByPreId.asString());
			status = AssertResponses.assertResponses(delAllDocByPreId, Expectedresponse, outerKeys, innerKeys);

			
		} else if (testCaseName.contains("DeleteDocumentByDocIdByPassingInvalidDocumentId")) {
			try {
				
				docId = actualRequest.get("documentId").toString();
			} catch (NullPointerException  e) {
				Assert.assertTrue(false, "Document id is not present in document upload response");
			}

			parm.put("preRegistrationId", preId);

			String preRegistration_URI = preReg_URI + docId;

			Actualresponse = appLib.deleteWithQueryParams(preRegistration_URI, parm,individualToken);
			logger.info("Delete Doc By Doc Id::"+"Test Case name::"+testCaseName+"Res::"+Actualresponse.asString());
			
			boolean value = testCaseName.contains("EmptyValue")?(outerKeys.add("timestamp")):outerKeys.add("responsetime");
			
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

		} else if (testCaseName.contains("DeleteDocumentByDocIdByPassingInvalidPreRegistrationId")) {
			preId = actualRequest.get("preRegistrationId").toString();
			parm.put("preRegistrationId", preId);

			preReg_URI = preReg_URI + docId;

			Actualresponse = appLib.deleteWithQueryParams(preReg_URI, parm,individualToken);
			logger.info("Delete Doc By Doc Id Act Res::"+"Test Case name::"+testCaseName+Actualresponse.asString());
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
   
		//Delete document by Document Id Resource URI
		preReg_URI = commonLibrary.fetch_IDRepo().get("prereg_DeleteDocumentByDocIdURI");
		
		//Fetch the generated Authorization Token by using following Kernel AuthManager APIs
		if (!preRegLib.isValidToken(individualToken)) {
			individualToken = preRegLib.getToken();
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
			//f.set(baseTestMethod, DeleteDocumentByDocId.testCaseName);
			f.set(baseTestMethod, "Pre Reg_DeleteAllDocumentByDocId_"+DeleteDocumentByDocId.testCaseName);
			
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}
	

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}