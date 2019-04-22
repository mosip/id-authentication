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

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Delete Document By DocId related Positive and Negative test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class DeleteDocumentByDocId extends BaseTestCase implements ITest {
	
	/**
	 *  Declaration of all variables
	 **/
	static 	String preId="";
	static 	String docId="";
	static SoftAssert softAssert=new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(DeleteDocumentByDocId.class);
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	static String dest = "";
	static String folderPath = "preReg/DeleteDocumentByDocId";
	static String outputFile = "DeleteDocumentByDocIdRequestOutput.json";
	static String requestKeyFile = "DeleteDocumentByDocIdRequest.json";
	static PreRegistrationLibrary preRegLib=new PreRegistrationLibrary();
	private static CommonLibrary commonLibrary = new CommonLibrary();
	private static String preReg_URI ;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	 HashMap<String, String> parm= new HashMap<>();
	
	/*implement,IInvokedMethodListener*/
	public DeleteDocumentByDocId() {

	}
	
	/**
	 * Data Providers to read the input json files from the folders
	 * @param context
	 * @return input request file
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	@DataProvider(name = "DeleteDocumentByDocId")
	public static Object[][] readData(ITestContext context) throws Exception {
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		switch ("smokeAdRegression") {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	@Test(dataProvider = "DeleteDocumentByDocId")
	public void deleteDocumentByDocId(String testSuite, Integer i, JSONObject object) throws Exception {
	
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		String testCase = object.get("testCaseName").toString();
		
		System.out.println("Test Case name:"+testCase);
		
		//Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg();
		preId=createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
		
		//Document Upload for created application
		
		Response docUploadResponse = preRegLib.documentUploadParm(createApplicationResponse,preId);
		
		//Get PreId from Document upload response
		preId=docUploadResponse.jsonPath().get("response[0].preRegistrationId").toString();
		
		//Get docId from Document upload response
		docId=docUploadResponse.jsonPath().get("response[0].documentId").toString();
	
		if(testCaseName.contains("smoke"))
		{

			
			//Delete All Document by Document Id
			Response delAllDocByPreId = preRegLib.deleteAllDocumentByDocId( docId, preId);
			outerKeys.add("responsetime");
			innerKeys.add("multipartFile");
			
			status = AssertResponses.assertResponses(delAllDocByPreId, Expectedresponse, outerKeys, innerKeys);
			
			
		}
		else if(testCaseName.contains("DeleteDocumentByDocIdByPassingInvalidDocumentId"))
		{
			docId= actualRequest.get("documentId").toString();
			
			
			 parm.put("preRegistrationId", preId);
			 
			 preReg_URI=preReg_URI+docId;
			 
			 Actualresponse = applicationLibrary.deleteRequestPathAndQueryParam(preReg_URI, parm);
			 outerKeys.add("responsetime");
			 status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
			 
		}
		else if(testCaseName.contains("DeleteDocumentByDocIdByPassingInvalidPreRegistrationId"))
		{
			 preId= actualRequest.get("preRegistrationId").toString();
			 parm.put("preRegistrationId", preId);
			 
			 preReg_URI=preReg_URI+docId;
			 
			 Actualresponse = applicationLibrary.deleteRequestPathAndQueryParam(preReg_URI, parm);
			 outerKeys.add("responsetime");
			 status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
			 
			
		}
		
		/*
		
		switch (testCase)
		{
		
		case "DeleteDocumentByDocmentId_smoke":
			
			//Delete All Document by Document Id
			Response delAllDocByPreId = preRegLib.deleteAllDocumentByDocId( docId, preId);
			outerKeys.add("responsetime");
			innerKeys.add("multipartFile");
			
			status = AssertResponses.assertResponses(delAllDocByPreId, Expectedresponse, outerKeys, innerKeys);
			
			break;
			
			
		case "DeleteDocumentByDocIdByPassingInvalidDocumentId_Alphabets":
			
			
			//preId = actualRequest.get("preRegistrationId").toString();
			docId= actualRequest.get("documentId").toString();
			
			
			 parm.put("preRegistrationId", preId);
			 
			 preReg_URI=preReg_URI+docId;
			 
			 Actualresponse = applicationLibrary.deleteRequestPathAndQueryParam(preReg_URI, parm);
			 System.out.println("Actualresponse:"+Actualresponse.asString());
			 
			 outerKeys.add("responsetime");
			 status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
			 
			break;
			
         case "DeleteDocumentByDocIdByPassingInvalidDocumentId_AlphaNumeric":
			
			//preId = actualRequest.get("preRegistrationId").toString();
			docId= actualRequest.get("documentId").toString();
			
			
			 parm.put("preRegistrationId", preId);
			 
			 preReg_URI=preReg_URI+docId;
			 
			 Actualresponse = applicationLibrary.deleteRequestPathAndQueryParam(preReg_URI, parm);
			 System.out.println("Actualresponse:"+Actualresponse.asString());
			 outerKeys.add("responsetime");
			 status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
			 
			break;
			
         case "DeleteDocumentByDocIdByPassingInvalidDocumentId_AlphaNumericAndSpecialCharacters":
 			
 			//preId = actualRequest.get("preRegistrationId").toString();
 			docId= actualRequest.get("documentId").toString();
 			
 			
 			 parm.put("preRegistrationId", preId);
 			 
 			 preReg_URI=preReg_URI+docId;
 			 
 			 Actualresponse = applicationLibrary.deleteRequestPathAndQueryParam(preReg_URI, parm);
 			 
 			System.out.println("Actualresponse:"+Actualresponse.asString());
 			 
 			 outerKeys.add("responsetime");
 			 status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
 			 
 			break;
 			
		
         case "DeleteDocumentByDocIdByPassingInvalidDocumentId_SpecialCharacters":
  			
  			//preId = actualRequest.get("preRegistrationId").toString();
  			docId= actualRequest.get("documentId").toString();
  			
  			
  			 parm.put("preRegistrationId", preId);
  			 
  			 preReg_URI=preReg_URI+docId;
  			 
  			 Actualresponse = applicationLibrary.deleteRequestPathAndQueryParam(preReg_URI, parm);
  			 
  			System.out.println("Actualresponse:"+Actualresponse.asString());
  			 
  			 outerKeys.add("responsetime");
  			 status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
  			 
  			break;
 			
 			
		}
		*/
		
		/*
		if(testCaseName.contains("smoke"))
		{
		
			
		
		//Delete All Document by Document Id
		
		Response delAllDocByPreId = preRegLib.deleteAllDocumentByDocId( docId, preId);
		
		System.out.println("Dele All Doc::"+delAllDocByPreId.asString());
		
		outerKeys.add("responsetime");
		
		
		status = AssertResponses.assertResponses(delAllDocByPreId, Expectedresponse, outerKeys, innerKeys);
		
		}
		
		else
		{
			
			String preRegId;
			String preRegDocId;
			
			 preRegId = actualRequest.get("preRegistrationId").toString();
			 preRegDocId= actualRequest.get("documentId").toString();
			
			 HashMap<String, String> parm= new HashMap<>();
			 parm.put("preRegistrationId", preRegId);
			 
			 preReg_URI=preReg_URI+preRegDocId;
			 
			 Actualresponse = applicationLibrary.deleteRequestPathAndQueryParam(preReg_URI, parm);
			 
			 
			 outerKeys.add("resTime");
			 status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
			 
		}*/
		
		if (status) {
			finalStatus="Pass";		
		softAssert.assertAll();
		object.put("status", finalStatus);
		arr.add(object);
		}
		else {
			finalStatus="Fail";
		}
		
		boolean setFinalStatus=false;
		
		setFinalStatus = finalStatus.equals("Pass") ? true : false ;
		
        Verify.verify(setFinalStatus);
        softAssert.assertAll();
		
	}

	
	@BeforeMethod(alwaysRun = true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
	
		testCaseName = object.get("testCaseName").toString();
		
		/**
         * Delete document by Document Id Resource URI           
         */
        
        preReg_URI = commonLibrary.fetch_IDRepo().get("prereg_DeleteDocumentByDocIdURI");
        authToken=preRegLib.getToken();
		
	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, DeleteDocumentByDocId.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		String configPath = "src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}
		String source =  "src/test/resources/" + folderPath + "/";
		
		//Add generated PreRegistrationId to list to be Deleted from DB AfterSuite 
		preIds.add(preId);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}


}