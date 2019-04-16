package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
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

import com.google.common.base.Verify;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * @author Arunakumar Rati
 *
 */
public class LicenseKeyController extends BaseTestCase implements ITest{

	public LicenseKeyController() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	/**
	 *  Declaration of all variables
	 */
	
	private static Logger logger = Logger.getLogger(LicenseKeyController.class);
	protected static String testCaseName = "";
	static SoftAssert softAssert=new SoftAssert();
	public static JSONArray arr = new JSONArray();
	boolean status = false;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static AssertKernel assertKernel = new AssertKernel();
	private static final String licKeyGenerator = "/v1/licensekeymanager/license/generate";
	private static final String mapLicenseKey = "/v1/licensekeymanager/license/permission";
	private static final String fetchmapLicenseKey = "/v1/licensekeymanager/license/permission";
	static String dest = "";
	static String folderPath = "kernel/LicenseKeyController/GenerateLicenseKey";
	static String outputFile = "GenerateLicenseKeyrOutput.json";
	static String requestKeyFile = "GenerateLicenceKeyInput.json";
	static JSONObject Expectedresponse = null;
	String finalStatus = "";
	static String testParam="";
	static String folderPath1 = "kernel/LicenseKeyController/MapLicenseKeyPermission";
	static String outputFile1= "MapLicenseKeyPermissionOutput.json";
	static String requestKeyFile1 = "MapLicenseKeyPermissionInput.json";
	static JSONObject actualRequest1=null;
	static String folderPath2 = "kernel/LicenseKeyController/FetchLicenseKeyPermissions";
	static String outputFile2= "FetchLicenseKeyPermissionsOutput.json";
	static String requestKeyFile2 = "FetchLicenseKeyPermissionsInput.json";
	Response res_map=null;
	 String licenseKey="";
	 String tspId="";
	/*
	 * Data Providers to read the input json files from the folders
	 */
	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		
		testCaseName = object.get("testCaseName").toString();
	} 
	
	/**
	 * @return input jsons folders
	 * @throws Exception
	 */
	@DataProvider(name = "LicenseKeyGenerator")
	public static Object[][] readData1(ITestContext context) throws Exception {
		 testParam = context.getCurrentXmlTest().getParameter("testType");
		switch (testParam) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}
	
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getRegCenterByID_Timestamp
	 * Given input Json as per defined folders When POSt request is sent to /licensekeymanager/v1.0/license/generate"
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@Test(dataProvider="LicenseKeyGenerator",priority=0)
	public void generateLicenseKey(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		 actualRequest1 = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		 Response res = applicationLibrary.postRequest(actualRequest1, licKeyGenerator);
		 if(testCaseName.equals("smoke_generateLicenceKey"))
			{
			 tspId=((JSONObject)actualRequest1.get("request")).get("tspId").toString();
			 licenseKey=res.jsonPath().getMap("response").get("licenseKey").toString();
			
			}
		/*
		 *  Removing of unstable attributes from response
		 */
		 
		
		//innerKeys.add("errorMessage");	
	    outerKeys.add("responsetime");
		outerKeys.add("licenseKey");
		innerKeys.add("licenseKey");
		
		/*
		 * Comparing expected and actual response
		 */
		
		status = AssertResponses.assertResponses(res, Expectedresponse, outerKeys, innerKeys);
     if(status)
     {
    	  if(testCaseName.equals("smoke_generateLicenceKey"))
    	  {     
    		  int length=licenseKey.length();
    		  if(length==16)
    		  	{
    			  finalStatus ="Pass";
    		  	}
    		  else
    		  	{
				finalStatus="fail";
    		  	}
    	   }
    	  else
			finalStatus ="Pass";
    	   
     }			
			
		else {
			finalStatus="Fail";
			logger.error(res);
			//softAssert.assertTrue(false);
		}
		
		softAssert.assertAll();
		object.put("status", finalStatus);
		arr.add(object);
		boolean setFinalStatus=false;
		if(finalStatus.equals("Fail"))
			setFinalStatus=false;
		else if(finalStatus.equals("Pass"))
			setFinalStatus=true;
		Verify.verify(setFinalStatus);
		softAssert.assertAll();
}
	/**
	 * @return input jsons folders
	 * @throws Exception
	 */
	@DataProvider(name = "mapLicenseKey")
	public static Object[][] readData3(ITestContext context) throws Exception {
	
		 testParam = context.getCurrentXmlTest().getParameter("testType");
		switch (testParam) {
		case "smoke":
			return ReadFolder.readFolders(folderPath1, outputFile1, requestKeyFile1, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath1, outputFile1, requestKeyFile1, "regression");
		default:
			return ReadFolder.readFolders(folderPath1, outputFile1, requestKeyFile1, "smokeAndRegression");
		}
	}
	
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getRegCenterByID_Timestamp
	 * Given input Json as per defined folders When POSt request is sent to /licensekeymanager/v1.0/license/generate"
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@Test(dataProvider="mapLicenseKey",priority=1)
	public void mapLicenceKey(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		
		/*
		 *  Removing of unstable attributes from response
		 */
		
		 outerKeys.add("responsetime");
		 innerKeys.add("errorMessage");
	    JSONObject actualRequest_map = ResponseRequestMapper.mapRequest(testSuite, object);
	    JSONObject request = (JSONObject) actualRequest_map.get("request");
	    if(testCaseName.equals("smoke_MapLicenseKeyPermission"))
	    {
	    	request.put("tspId", tspId);
	    	request.put("licenseKey", licenseKey);		 
	    	actualRequest_map.putAll(request);
		 Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		  res_map = applicationLibrary.postRequest(actualRequest_map, mapLicenseKey);
	    }
	    else
	    {
	    	Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
			
			  res_map = applicationLibrary.postRequest(actualRequest_map, mapLicenseKey);
	    }
		/*
		 * Comparing expected and actual response
		 */
		
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		status = AssertResponses.assertResponses(res_map, Expectedresponse, outerKeys, innerKeys);
      if (status) {
	                
				finalStatus = "Pass";
			}	
		
		else {
			finalStatus="Fail";
			//softAssert.assertTrue(false);
		}
		
		softAssert.assertAll();
		object.put("status", finalStatus);
		arr.add(object);
		boolean setFinalStatus=false;
		if(finalStatus.equals("Fail"))
			setFinalStatus=false;
		else if(finalStatus.equals("Pass"))
			setFinalStatus=true;
		Verify.verify(setFinalStatus);
		softAssert.assertAll();
}
	/**
	 * @return input jsons folders
	 * @throws Exception
	 */

	@DataProvider(name = "fetchmapLicenseKey")
	public static Object[][] readData(ITestContext context) throws Exception {
		
		 testParam = context.getCurrentXmlTest().getParameter("testType");
		switch (testParam) {
		case "smoke":
			return ReadFolder.readFolders(folderPath2, outputFile2, requestKeyFile2, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath2, outputFile2, requestKeyFile2, "regression");
		default:
			return ReadFolder.readFolders(folderPath2, outputFile2, requestKeyFile2, "smokeAndRegression");
		}
	}
	
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getRegCenterByID_Timestamp
	 * Given input Json as per defined folders When POSt request is sent to /licensekeymanager/v1.0/license/generate"
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@Test(dataProvider="fetchmapLicenseKey",priority=2)
	public void fetchMapLicenceKeyPermissions(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		LicenseKeyController lc=new LicenseKeyController();
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		 outerKeys.add("responsetime");

		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		if(testCaseName.equalsIgnoreCase("smoke_FetchLicenseKeyPermissions"))
		{
			actualRequest.put("tspId", tspId);
			actualRequest.put("licenseKey", licenseKey);
		}
		
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		@SuppressWarnings("unchecked")
		Response response=applicationLibrary.getRequestAsQueryParam(fetchmapLicenseKey, actualRequest);
				
		
		/*
		 *Comparing expected and actual response
		 */
		
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("timestamp");
		status = AssertResponses.assertResponses(response, Expectedresponse, outerKeys, innerKeys);
      if (status) {	            
				finalStatus = "Pass";
			}	
		
		else {
			finalStatus="Fail";
			//logger.error(res);
			//softAssert.assertTrue(false);
		}
		
		softAssert.assertAll();
		object.put("status", finalStatus);
		arr.add(object);
		boolean setFinalStatus=false;
		if(finalStatus.equals("Fail"))
			setFinalStatus=false;
		else if(finalStatus.equals("Pass"))
			setFinalStatus=true;
		Verify.verify(setFinalStatus);
		softAssert.assertAll();
}
		@Override
		public String getTestName() {
			return this.testCaseName;
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

				f.set(baseTestMethod, LicenseKeyController.testCaseName);

				
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/LicenseKeyController/LicenseKeyControllerOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to LicenseKeyControllerOutput.json file.......................!!");
			}
		}

}
