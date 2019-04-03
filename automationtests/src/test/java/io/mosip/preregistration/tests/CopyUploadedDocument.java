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

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Copy Uploaded Document related Positive and Negative test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class CopyUploadedDocument extends BaseTestCase implements ITest {
	
	/**
	 *  Declaration of all variables
	 **/
	static 	String preId="";
	static String destPreId="";
	static SoftAssert softAssert=new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(CopyUploadedDocument.class);
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	static String dest = "";
	static String folderPath = "preReg/CopyUploadedDocument";
	static String outputFile = "CopyUploadedDocumentRequestOutput.json";
	static String requestKeyFile = "CopyUploadedDocumentRequest.json";
	PreRegistrationLibrary preRegLib=new PreRegistrationLibrary();
	private static CommonLibrary commonLibrary = new CommonLibrary();
	private static String preReg_URI ;
	
	/*implement,IInvokedMethodListener*/
	public CopyUploadedDocument() 
	{

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

	@DataProvider(name = "CopyUploadedDocument")
	public static Object[][] readData(ITestContext context) throws Exception {
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		switch (testParam) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	@Test(dataProvider = "CopyUploadedDocument")
	public void copyUploadedDocument(String testSuite, Integer i, JSONObject object) throws Exception {
	
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		
		
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
	
		//Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg();
		preId=createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
		
		//Document Upload for created application
		Response docUploadResponse = preRegLib.documentUpload(createApplicationResponse);
		
		//PreId of Uploaded document
		String srcPreID=docUploadResponse.jsonPath().get("response[0].preRegistrationId").toString();
		
		//Creating the Pre-Registration Application for Destination PreId
		Response createApplicationRes = preRegLib.CreatePreReg();
		String destPreId = createApplicationRes.jsonPath().get("response[0].preRegistrationId").toString();
		
		//Copy uploaded document from Source PreId to Destination PreId
		 Response copyDocresponse=preRegLib.copyUploadedDocuments(srcPreID, destPreId);
		 
		
		 
		outerKeys.add("responsetime");
		innerKeys.add("sourcePreRegId");
		innerKeys.add("sourceDocumnetId");
		innerKeys.add("destPreRegId");
		innerKeys.add("destDocumnetId");
		
		
		status = AssertResponses.assertResponses(copyDocresponse, Expectedresponse, outerKeys, innerKeys);
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
        if(finalStatus.equals("Fail"))
              setFinalStatus=false;
        else if(finalStatus.equals("Pass"))
              setFinalStatus=true;
        Verify.verify(setFinalStatus);
        softAssert.assertAll();
		
		
	}

	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
	
		testCaseName = object.get("testCaseName").toString();
		
		/**
         * Copy Uploaded document Resource URI          
         */
        
        preReg_URI = commonLibrary.fetch_IDRepo().get("prereg_DeleteDocumentByDocIdURI");
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
			f.set(baseTestMethod, CopyUploadedDocument.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}
	/**
	 * Writing Output to the configpath
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		String configPath =  "src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}
		String source =  "src/test/resources/" + folderPath + "/";
		
		//Add generated PreRegistrationId to list to be Deleted from DB AfterSuite 
				preIds.add(preId);
				preIds.add(destPreId);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}


}
