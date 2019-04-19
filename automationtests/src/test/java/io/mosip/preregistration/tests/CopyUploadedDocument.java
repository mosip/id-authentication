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
	static PreRegistrationLibrary preRegLib=new PreRegistrationLibrary();
	private static CommonLibrary commonLibrary = new CommonLibrary();
	private static ApplicationLibrary appLibrary = new ApplicationLibrary();
	private static String preReg_URI ;
	HashMap<String, String> parm= new HashMap<>();
	
	
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
		switch ("regression") {
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
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
	
		
		String val = testCaseName.contains("smoke")
				?(testCaseName="cond1"):testCaseName.contains("CopyUploadedDocumentByPassingInvalidCatCode")
				?(testCaseName="cond2"):testCaseName.contains("CopyUploadedDocumentByPassingInvalidDestinationPreId")
				?(testCaseName="cond3"):testCaseName.contains("CopyUploadedDocumentByPassingInvalidSourcePreId")
				?(testCaseName="cond4"):testCaseName.contains("CopyUploadedDocumentByPassingDestPreIdForWhichPOADocAlreadyExists")
				?(testCaseName="cond5"):(testCaseName="cond6");
		
				testCaseName="cond2";
		//Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg();
		preId=createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
		
		//Document Upload for created application
		Response docUploadResponse = preRegLib.documentUploadParm(createApplicationResponse,preId);
		
		//PreId of Uploaded document
		String srcPreID=docUploadResponse.jsonPath().get("response[0].preRegistrationId").toString();
		//String docId=docUploadResponse.jsonPath().get("response[0].docId").toString();
		String docCatCode=docUploadResponse.jsonPath().get("response[0].docCatCode").toString();
		
		//Creating the Pre-Registration Application for Destination PreId
		Response createApplicationRes = preRegLib.CreatePreReg();
		String destPreId = createApplicationRes.jsonPath().get("response[0].preRegistrationId").toString();
		
		
		
		switch (val) {
 		case "cond1":
 			
 			//Copy uploaded document from Source PreId to Destination PreId
 			
 			Response copyDocresponse=preRegLib.copyUploadedDocuments(destPreId,srcPreID,docCatCode);
 			outerKeys.add("responsetime");
 			innerKeys.add("docId");
 			//preRegLib.compareValues(actual, expected);
 			status = AssertResponses.assertResponses(copyDocresponse, Expectedresponse, outerKeys, innerKeys);
 			
         break;
        case "cond2":
 			
        	docCatCode=actualRequest.get("catCode").toString();
 			preReg_URI=preReg_URI+destPreId;
 			parm.put("catCode", docCatCode);
 			parm.put("sourcePreId", srcPreID);
 			Actualresponse = appLibrary.getRequestPathAndQueryParam(preReg_URI, parm);
			outerKeys.add("responsetime");
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
 			
 	         break;
 		case "cond3":
 			
 			destPreId=actualRequest.get("destinationPreId").toString();
 			preReg_URI=preReg_URI+destPreId;
 			parm.put("catCode", docCatCode);
 			parm.put("sourcePreId", srcPreID);
 			Actualresponse = appLibrary.getRequestPathAndQueryParam(preReg_URI, parm);
			outerKeys.add("responsetime");
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
 			
 	         break;
        case "cond4":
 			
        	srcPreID=actualRequest.get("sourcePrId").toString();
 			preReg_URI=preReg_URI+destPreId;
 			
 			parm.put("catCode", docCatCode);
 			parm.put("sourcePreId", srcPreID);
 			Actualresponse = appLibrary.getRequestPathAndQueryParam(preReg_URI, parm);
			outerKeys.add("responsetime");
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys); 
 			
 			System.out.println("33333");
         break;
 		case "cond5":
 			System.out.println("2222");
 	         break;
 		case "cond6":
 			System.out.println("2222");
 	         break;
 		default:
 			System.out.println("33333");
 			break;
 		

 	}
		
		
		
		
		
		
		
		
		
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

	
	@BeforeMethod(alwaysRun = true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
	
		testCaseName = object.get("testCaseName").toString();
		
		/**
         * Copy Uploaded document Resource URI          
         */
        
        preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_CopyDocumentsURI");
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
				//preIds.add(preId);
				//preIds.add(destPreId);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}


}
