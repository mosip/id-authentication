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
import org.testng.Assert;
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


import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Copy Upload Document related Positive and Negative test
 * cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class CopyUploadedDocument extends BaseTestCase implements ITest {

	/**
	 * Declaration of all variables
	 **/
	String preId = "";
	String destPreId = "";
	SoftAssert softAssert = new SoftAssert();
	static String testCaseName = "";
	Logger logger = Logger.getLogger(CopyUploadedDocument.class);
	boolean status = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	String dest = "";
	String folderPath = "preReg/CopyUploadedDocument";
	String outputFile = "CopyUploadedDocumentRequestOutput.json";
	String requestKeyFile = "CopyUploadedDocumentRequest.json";
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	CommonLibrary commonLibrary = new CommonLibrary();
	ApplicationLibrary appLibrary = new ApplicationLibrary();
	String preReg_URI;
	HashMap<String, String> parm = new HashMap<>();

	/* implement,IInvokedMethodListener */
	public CopyUploadedDocument() {

	}

	/**
	 * This method is used for reading the test data based on the test case name
	 * passed
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "CopyUploadedDocument")
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
	 * Given Copy Document Upload valid request when I Send PUT request to
	 * https://mosip.io/preregistration/v1/documents/:preRegistrationId?catCode=:doc_cat_code&sourcePreId=:preRegistrationId
	 *  Then I should get success
	 * response with elements defined as per specifications Given Invalid
	 * request when I send PUT request to
	 * https://mosip.io/preregistration/v1/documents/:preRegistrationId?catCode=:doc_cat_code&sourcePreId=:preRegistrationId Then I should get Error
	 * response along with Error Code and Error messages as per Specification
	 * 
	 */
	@Test(dataProvider = "CopyUploadedDocument")
	public void copyUploadedDocument(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		String srcPreID=null;
		String docCatCode=null;
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);

		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		String val = null;
		String name = null;
		if (testCaseName.contains("smoke")) {
			val = testCaseName;
			
		} else {
			String[] parts = testCaseName.split("_");
			val = parts[0];
			name = parts[1];
		}

		String testCaseValue=val+"_"+name;
		// Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg();
		preId = preRegLib.getPreId(createApplicationResponse);
		// Document Upload for created application
		Response docUploadResponse = preRegLib.documentUploadParm(createApplicationResponse, preId);
		// PreId of Uploaded document
		try {
			 srcPreID = docUploadResponse.jsonPath().get("response.preRegistrationId").toString();
			 docCatCode = docUploadResponse.jsonPath().get("response.docCatCode").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception while fetching document cat code from response");
		}
		// Creating the Pre-Registration Application for Destination PreId
		Response createApplicationRes = preRegLib.CreatePreReg();
		String destPreId = preRegLib.getPreId(createApplicationRes);
		switch (val) {
		case "CopyUploadedDocument_smoke":

			// Copy uploaded document from Source PreId to Destination PreId

			Response copyDocresponse = preRegLib.copyUploadedDocuments(destPreId, srcPreID, docCatCode);
			logger.info("Copy Uploadede Doc:" + copyDocresponse.asString());
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			innerKeys.add("docId");
			
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(copyDocresponse, Expectedresponse, outerKeys, innerKeys);

			break;
		case "CopyUploadedDocumentByPassingDestPreIdForWhichPOADocAlreadyExists_smoke":

			// Copy uploaded document from Source PreId to Destination PreId

			Response copyDocrespons = preRegLib.copyUploadedDocuments(destPreId, srcPreID, docCatCode);
			logger.info("Copy Uploadede Doc POA :" + copyDocrespons.asString());
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			innerKeys.add("docId");
			
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(copyDocrespons, Expectedresponse, outerKeys, innerKeys);

			break;
		case "CopyUploadedDocumentByPassingInvalidCatCode":

			docCatCode = actualRequest.get("catCode").toString();
			String preReg_URI1 = preReg_URI + destPreId;
			HashMap<String, String> parmInvalidCatCode = new HashMap<>();
			parmInvalidCatCode.put("catCode", docCatCode);
			parmInvalidCatCode.put("sourcePreId", srcPreID);
			Actualresponse = appLibrary.put_Request_pathAndMultipleQueryParam(preReg_URI1, parmInvalidCatCode);
			logger.info("CopyUploadedDocumentByPassingInvalidCatCode:" + Actualresponse.asString()+"Test casename:"+testCaseName);
			outerKeys.add("responsetime");
			
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

			break;
		case "CopyUploadedDocumentByPassingInvalidDestinationPreId":

			destPreId = actualRequest.get("destinationPreId").toString();
			String preReg_URI2 = preReg_URI + destPreId;
			HashMap<String, String> parmInvalidDestId = new HashMap<>();
			parmInvalidDestId.put("catCode", docCatCode);
			parmInvalidDestId.put("sourcePreId", srcPreID);
			Actualresponse = appLibrary.put_Request_pathAndMultipleQueryParam(preReg_URI2, parmInvalidDestId);
			logger.info("CopyUploadedDocumentByPassingInvalidDestinationPreId:" + Actualresponse.asString()+"Test casename:"+testCaseName);
			
			if(name.contains("Empty"))
			{
				outerKeys.add("timestamp");
			}
			else
			{
				outerKeys.add("responsetime");
			}
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

			break;
		case "CopyUploadedDocumentByPassingSourcePreIdForWhichNoDocUploaded":

			Response createApplicationResNoDocUpload = preRegLib.CreatePreReg();
			srcPreID = createApplicationResNoDocUpload.jsonPath().get("response.preRegistrationId").toString();
			//srcPreID = actualRequest.get("sourcePrId").toString();
			String preReg_URINoDocUpload = preReg_URI + destPreId;
			HashMap<String, String> parmNoDocUpload= new HashMap<>();
			parmNoDocUpload.put("catCode", docCatCode);
			parmNoDocUpload.put("sourcePreId", srcPreID);
			Actualresponse = appLibrary.put_Request_pathAndMultipleQueryParam(preReg_URINoDocUpload, parmNoDocUpload);
			logger.info("CopyUploadedDocumentByPassingSourcePreIdForWhichNoDocUploaded:" + Actualresponse.asString()+"Test casename:"+testCaseName);
			outerKeys.add("responsetime");
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
			break;
		case "CopyUploadedDocumentByPassingInvalidSourcePreId":
			srcPreID = actualRequest.get("sourcePrId").toString();
			String preReg_URI3 = preReg_URI + destPreId;
			HashMap<String, String> parmInvalidSrcId = new HashMap<>();
			parmInvalidSrcId.put("catCode", docCatCode);
			parmInvalidSrcId.put("sourcePreId", srcPreID);
			Actualresponse = appLibrary.put_Request_pathAndMultipleQueryParam(preReg_URI3, parmInvalidSrcId);
			logger.info("CopyUploadedDocumentByPassingInvalidSourcePreId:" + Actualresponse.asString()+"Test casename:"+testCaseName);
			outerKeys.add("responsetime");
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
			break;
		case "CopyUploadedDocumentByPassingDestPreIdForWhichPOADocAlreadyExists":

			//srcPreID = actualRequest.get("sourcePrId").toString();
			String preReg_URI4 = preReg_URI + destPreId;
			HashMap<String, String> parmInvalidSrcPreId = new HashMap<>();
			parmInvalidSrcPreId.put("catCode", docCatCode);
			parmInvalidSrcPreId.put("sourcePreId", srcPreID);
			Actualresponse = appLibrary.put_Request_pathAndMultipleQueryParam(preReg_URI4, parmInvalidSrcPreId);
			logger.info("CopyUploadedDocumentByPassingDestPreIdForWhichPOADocAlreadyExists:" + Actualresponse.asString()+"Test casename:"+testCaseName);
			outerKeys.add("responsetime");
			
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

			break;
			
			
			

		default:

			break;

		}

		if (name != null) {
			testCaseName = val + "_" + name;
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

		//Copy Uploaded document Resource URI
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_CopyDocumentsURI");
		
		//Fetch the generated Authorization Token by using following Kernel AuthManager APIs
		authToken = preRegLib.getToken();
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
			//f.set(baseTestMethod, CopyUploadedDocument.testCaseName);
			f.set(baseTestMethod, "Pre Reg_CopyDocument_" +CopyUploadedDocument.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * This method is used for generating output file with the test case result
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

	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}

