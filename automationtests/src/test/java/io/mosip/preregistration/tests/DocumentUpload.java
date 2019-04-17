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

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Document Upload related Positive and Negative test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class DocumentUpload extends BaseTestCase implements ITest {
	/**
	 *  Declaration of all variables
	 **/
	static String folder = "preReg";
	static 	String preId="";
	static SoftAssert softAssert=new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(FetchAllApplicationCreatedByUser.class);
	boolean status = false;
	boolean statuOfSmokeTest = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static String preReg_URI ;
	private static CommonLibrary commonLibrary = new CommonLibrary();
	static String dest = "";
	static String configPaths="";
	static String folderPath = "preReg/DocumentUpload";
	static String outputFile = "DocumentUploadOutput.json";
	static String requestKeyFile = "DocumentUploadRequest.json";
	String testParam=null;
	boolean status_val = false;
	static PreRegistrationLibrary preRegLib=new PreRegistrationLibrary();
	
	public DocumentUpload() {

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
	@DataProvider(name = "documentUpload")
	public Object[][] readData(ITestContext context) throws JsonParseException, JsonMappingException, IOException, ParseException {
		  testParam = context.getCurrentXmlTest().getParameter("testType");
		 switch ("smoke") {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile,requestKeyFile,"smoke");
			
		case "regression":	
			return ReadFolder.readFolders(folderPath, outputFile,requestKeyFile,"regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile,requestKeyFile,"smokeAndRegression");
		}
		
	}
	
	/*
	 * Given Document Upload valid request when I Send POST request to /pre-registration/v1.0/document/documents
	 * Then I should get success response with elements defined as per specifications
	 * Given Invalid request when I send POST request to /pre-registration/v1.0/document/documents
	 * Then I should get Error response along with Error Code and Error messages as per Specification
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "documentUpload")
	public void bookingAppointment(String testSuite, Integer i, JSONObject object) throws Exception {
	
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		
		
		String testCase = object.get("testCaseName").toString();
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		if(testCase.contains("smoke"))
		{
			//Creating the Pre-Registration Application
			Response createApplicationResponse = preRegLib.CreatePreReg();
			
			
			String preRegIdCreateAPI=createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
			//Document Upload for created application
			//Response docUploadResponse = preRegLib.documentUpload(createApplicationResponse);
			
			Response docUploadResponse = preRegLib.documentUploadParm(createApplicationResponse,preRegIdCreateAPI); 
		
			System.out.println("Doc Upload:"+docUploadResponse.asString());
			
			//PreId of Uploaded document
			preId=docUploadResponse.jsonPath().get("response[0].preRegistrationId").toString();
			
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			innerKeys.add("documentId");
		    preRegLib.compareValues(preId, preRegIdCreateAPI);
			
			status = AssertResponses.assertResponses(docUploadResponse, Expectedresponse, outerKeys, innerKeys);
			
			}
		else
	{
		try 
		{
		
			
			testSuite = "DocumentUpload/DocumentUpload_smoke";
			
			String configPath = "src/test/resources/" + folder + "/" + testSuite;
			String fileName = "AadhaarCard_POA.pdf";
			File file = new File(configPath + "/"+fileName);
			
			Actualresponse =applicationLibrary.putFileAndJson(preReg_URI, actualRequest, file);
			
			
			
		} catch (Exception e) {
			logger.info(e);
		}
				outerKeys.add("responsetime");
				innerKeys.add("preRegistrationId");
				innerKeys.add("documentId");
	   
				status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);		
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
		
		setFinalStatus = finalStatus.equals("Pass") ? true : false ;
		
        Verify.verify(setFinalStatus);
        softAssert.assertAll();
		
		
		
	}
	/**
	 * Writing output into configpath
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
		
		//Add generated PreRegistrationId to list to be Deleted from DB AfterSuite 
		//preIds.add(preId);
	}
	/**
	 * Writing test case name into testng
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
                f.set(baseTestMethod, DocumentUpload.testCaseName);
          } catch (Exception e) {
                Reporter.log("Exception : " + e.getMessage());
          }
    }
    @BeforeMethod(alwaysRun = true)
    public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
          JSONObject object = (JSONObject) testdata[2];
          testCaseName = object.get("testCaseName").toString();
          

          /**
           * Document Upload Resource URI            
           */
          
          preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_DocumentUploadURI");
          authToken=preRegLib.getToken(); 
    }
	@Override
    public String getTestName() {
          return DocumentUpload.testCaseName;
    }
}
