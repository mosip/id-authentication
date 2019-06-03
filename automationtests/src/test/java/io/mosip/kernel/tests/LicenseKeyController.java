package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import io.mosip.kernel.util.CommonLibrary;
import io.mosip.service.ApplicationLibrary;
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
		super();
	}
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(LicenseKeyController.class);
	protected static String testCaseName = "";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private final Map<String, String> props = new CommonLibrary().kernenReadProperty();
	private final String licKeyGenerator = props.get("licKeyGenerator");
	private final String mapLicenseKey = props.get("mapLicenseKey");
	private final String fetchmapLicenseKey = props.get("fetchmapLicenseKey");
	private String folderPath = "kernel/LicenseKeyController/GenerateLicenseKey";
	private String outputFile = "GenerateLicenseKeyrOutput.json";
	private String requestKeyFile = "GenerateLicenceKeyInput.json";
	private JSONObject Expectedresponse = null;
	private String finalStatus = "";
	private String folderPath1 = "kernel/LicenseKeyController/MapLicenseKeyPermission";
	private String outputFile1= "MapLicenseKeyPermissionOutput.json";
	private String requestKeyFile1 = "MapLicenseKeyPermissionInput.json";
	private JSONObject actualRequest1=null;
	private String folderPath2 = "kernel/LicenseKeyController/FetchLicenseKeyPermissions";
	private String outputFile2= "FetchLicenseKeyPermissionsOutput.json";
	private String requestKeyFile2 = "FetchLicenseKeyPermissionsInput.json";
	private Response res_map=null;
	private String licenseKey="";
	private String tspId="";
	 
	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "LicenseKeyGenerator")
	public Object[][] readData1(ITestContext context) throws Exception {	 
				return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, testLevel);
		}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * generateLicenseKey
	 * Given input Json as per defined folders When POSt request is sent to /licensekeymanager/v1.0/license/generate"
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="LicenseKeyGenerator",priority=0)
	public void generateLicenseKey(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		 actualRequest1 = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		// Calling the Post method 
		 Response res = applicationLibrary.postRequest(actualRequest1, licKeyGenerator);
		
		//Storing the licence key and its corrosponding tspid
		 if(testCaseName.equals("Kernel_GenerateLicenseKey_smoke_generateLicenceKey"))
			{
			 tspId=((JSONObject)actualRequest1.get("request")).get("tspId").toString();
			 licenseKey=res.jsonPath().getMap("response").get("licenseKey").toString();
			
			}
		// Removing of unstable attributes from response
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
	    outerKeys.add("responsetime");
		outerKeys.add("licenseKey");
		innerKeys.add("licenseKey");
		
		// Comparing expected and actual response
		status = AssertResponses.assertResponses(res, Expectedresponse, outerKeys, innerKeys);
     if(status){
    	  if(testCaseName.contains("Kernel_GenerateLicenseKey_smoke_generateLicenceKey")){     
    		  int length=licenseKey.length();
    		  if(length==16){
    			  finalStatus ="Pass";
    		  	}
    		  else{
				finalStatus="fail";
    		  	}
    	   }
    	  else
			finalStatus ="Pass";
     }			
		else {
			finalStatus="Fail";
			logger.error(res);
		}
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
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "mapLicenseKey")
	public Object[][] readData3(ITestContext context) throws Exception {
				return ReadFolder.readFolders(folderPath1, outputFile1, requestKeyFile1, testLevel);
		}
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * mapLicenceKey
	 * Given input Json as per defined folders When POSt request is sent to /licensekeymanager/v1.0/license/generate"
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="mapLicenseKey",priority=1)
	public void mapLicenceKey(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		JSONObject actualRequest_map = ResponseRequestMapper.mapRequest(testSuite, object);
		
		//Removing of unstable attributes from response
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		 outerKeys.add("responsetime");
		 
	    // adding the tspid and corresponding license key to the request and expected response od smok test case
	    JSONObject request = (JSONObject) actualRequest_map.get("request");
	    if(testCaseName.contains("Kernel_MapLicenseKeyPermission_smoke_MapLicenseKeyPermission"))
	    {
	    	request.put("tspId", tspId);
	    	request.put("licenseKey", licenseKey);		 
	    	actualRequest_map.putAll(request);
		    Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		   // Calling the Post method 
		    res_map = applicationLibrary.postRequest(actualRequest_map, mapLicenseKey);
	    }
	    else
	    {
	    	Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
	    	// Calling the Post method 
			 res_map = applicationLibrary.postRequest(actualRequest_map, mapLicenseKey);
	    }
	  // Comparing expected and actual response
		status = AssertResponses.assertResponses(res_map, Expectedresponse, outerKeys, innerKeys);
      if (status) {
	                
				finalStatus = "Pass";
			}	
		
		else {
			finalStatus="Fail";
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
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "fetchmapLicenseKey")
	public Object[][] readData(ITestContext context) throws Exception {	 
				return ReadFolder.readFolders(folderPath2, outputFile2, requestKeyFile2, testLevel);
		}
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * fetchMapLicenceKeyPermissions
	 * Given input Json as per defined folders When POSt request is sent to /licensekeymanager/v1.0/license/generate"
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="fetchmapLicenseKey",priority=2)
	public void fetchMapLicenceKeyPermissions(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		// Removing of unstable attributes from response
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		outerKeys.add("responsetime");
  
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		//adding the tspid and corrosponding license key to the smoke request

		if(testCaseName.contains("Kernel_FetchLicenseKeyPermissions_smoke_FetchLicenseKeyPermissions"))
		{
			actualRequest.put("tspId", tspId);
			actualRequest.put("licenseKey", licenseKey);
		}
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		// Calling the get method 
		Response response=applicationLibrary.getRequestAsQueryParam(fetchmapLicenseKey, actualRequest);
				
		// Comparing expected and actual response
		status = AssertResponses.assertResponses(response, Expectedresponse, outerKeys, innerKeys);
      if (status) {	            
				finalStatus = "Pass";
			}	
		
		else {
			finalStatus="Fail";
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
		@SuppressWarnings("static-access")
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
