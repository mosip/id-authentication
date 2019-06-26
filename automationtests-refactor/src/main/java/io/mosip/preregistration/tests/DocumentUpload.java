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
 * Test Class to perform Document Upload related Positive and Negative test
 * cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class DocumentUpload extends BaseTestCase implements ITest {
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
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	String preReg_URI;
	CommonLibrary commonLibrary = new CommonLibrary();
	String dest = "";
	String configPaths = "";
	String folderPath = "preReg/DocumentUpload";
	String outputFile = "DocumentUploadOutput.json";
	String requestKeyFile = "DocumentUploadRequest.json";
	String testParam = null;
	boolean status_val = false;
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	public io.mosip.kernel.util.CommonLibrary cLib=new io.mosip.kernel.util.CommonLibrary();

	/* implement,IInvokedMethodListener */
	public DocumentUpload() {

	}

	/**
	 * This method is used for reading the test data based on the test case name
	 * passed
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "documentUpload")
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
	 * /pre-registration/v1.0/document/documents Then I should get success
	 * response with elements defined as per specifications Given Invalid
	 * request when I send POST request to
	 * /pre-registration/v1.0/document/documents Then I should get Error
	 * response along with Error Code and Error messages as per Specification
	 */
	@Test(dataProvider = "documentUpload")
	public void documentUpload(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		// Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg(authToken);
		String preRegIdCreateAPI = createApplicationResponse.jsonPath().get("response.preRegistrationId").toString();

		if (testCaseName.contains("smoke")) {
			// Document Upload for created application
			Response docUploadResponse = preRegLib.documentUpload(createApplicationResponse, preRegIdCreateAPI,null,authToken);
			logger.info("res::" + docUploadResponse.asString());
			// PreId of Uploaded document
			preId = docUploadResponse.jsonPath().get("response.preRegistrationId").toString();

			// Removing the dynamic value from Document Upload response and
			// Asserting actual and expected response
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			innerKeys.add("docId");
			preRegLib.compareValues(preId, preRegIdCreateAPI);
			//Asserting actual and expected response
			status = AssertResponses.assertResponses(docUploadResponse, Expectedresponse, outerKeys, innerKeys);

		} else {
			try {

				if (testCaseName.contains("DocumentUploadInvalidDocType")) {
					testSuite = "Get_Pre_Registartion_data/Get Pre Pregistration Data of the application_smoke";
					JSONObject parm = preRegLib.getRequest(testSuite);
					parm.put("preRegistrationId", preRegIdCreateAPI);
					actualRequest.put("requesttime", preRegLib.getCurrentDate());
					testSuite = "DocumentUpload/DocumentUploadInvalidDocType_xlsx";
					String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
					File file = new File(configPath + "/input.xlsx");
					Actualresponse = applicationLibrary.putFileAndJsonWithParm(preReg_URI, actualRequest, file, parm);
					logger.info("DocumentUploadInvalidDocType:"+Actualresponse.asString());
					outerKeys.add("responsetime");
					//Asserting actual and expected response
					status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

				} 
				else if(testCaseName.contains("DocumentUploadInvalidRequesttime")){
					testSuite = "Get_Pre_Registartion_data/Get Pre Pregistration Data of the application_smoke";
					JSONObject parm = preRegLib.getRequest(testSuite);
					parm.put("preRegistrationId", preRegIdCreateAPI);
					
					testSuite = "DocumentUpload/DocumentUpload_smoke";
					String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
					File file = new File(configPath + "/AadhaarCard_POI.pdf");
					Actualresponse = applicationLibrary.putFileAndJsonWithParm(preReg_URI, actualRequest, file, parm);
					logger.info("DocumentUploadInvalidRequesttime:"+Actualresponse.asString());
					outerKeys.add("responsetime");
					//Asserting actual and expected response
					status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

				}
				else {
					testSuite = "Get_Pre_Registartion_data/Get Pre Pregistration Data of the application_smoke";
					JSONObject parm = preRegLib.getRequest(testSuite);
					parm.put("preRegistrationId", preRegIdCreateAPI);
					actualRequest.put("requesttime", preRegLib.getCurrentDate());
					testSuite = "DocumentUpload/DocumentUpload_smoke";
					String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
					File file = new File(configPath + "/AadhaarCard_POI.pdf");
					Actualresponse = applicationLibrary.putFileAndJsonWithParm(preReg_URI, actualRequest, file, parm);
					logger.info("DocumentUploadInvalidVersionORId:"+Actualresponse.asString()+"TC nmaw::"+testCaseName);
					outerKeys.add("responsetime");
					//Asserting actual and expected response
					status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

				}

			} catch (Exception e) {
				logger.info(e);
			}
			
			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			innerKeys.add("documentId");

			
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

	@BeforeClass
	public void getToken()
	{
		authToken=preRegLib.getToken();
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
			//f.set(baseTestMethod, DocumentUpload.testCaseName);
			f.set(baseTestMethod, "Pre Reg_DocumentUpload_"+DocumentUpload.testCaseName);
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

		//Document Upload Resource URI
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_DocumentUploadURI");
		
	}

	@Override
	public String getTestName() {
		return DocumentUpload.testCaseName;
	}
}
