package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.google.common.base.Verify;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
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
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String licKeyGenerator = props.get("licKeyGenerator");
	private final String mapLicenseKey = props.get("mapLicenseKey");
	private final String fetchmapLicenseKey = props.get("fetchmapLicenseKey");
	private String folderPath = "LicenseKeyController/GenerateLicenseKey";
	private JSONObject Expectedresponse = null;
	private String folderPath1 = "LicenseKeyController/MapLicenseKeyPermission";
	private String folderPath2 = "LicenseKeyController/FetchLicenseKeyPermissions";
	private Response res_map=null;
	private String licenseKey="";
	private String tspId="";
	private AssertKernel assertKernel = new AssertKernel();
	 
	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "LicenseKeyGenerator")
	public Object[][] readData1(ITestContext context) throws Exception {	 
				return new TestCaseReader().readTestCases(moduleName + "/" + folderPath, testLevel);
		}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * generateLicenseKey
	 * Given input Json as per defined folders When POSt request is sent to /licensekeymanager/v1.0/license/generate"
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@Test(dataProvider="LicenseKeyGenerator",priority=0)
	public void generateLicenseKey(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, folderPath, testcaseName);
		JSONObject actualRequest1 = objectDataArray[0];
		Expectedresponse = objectDataArray[1];
		// Calling the Post method 
		 Response res = applicationLibrary.postWithJson(licKeyGenerator, actualRequest1);
		
		//Storing the licence key and its corrosponding tspid
		 if(testCaseName.equals("Kernel_GenerateLicenseKey_smoke_generateLicenceKey"))
			{
			 tspId=((JSONObject)actualRequest1.get("request")).get("tspId").toString();
			 licenseKey=res.jsonPath().getMap("response").get("licenseKey").toString();
			
			}
		// Removing of unstable attributes from response
		 ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		 listOfElementToRemove.add("responsetime");
		 listOfElementToRemove.add("licenseKey");
		
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
     if(status){
    	  if(testCaseName.contains("Kernel_GenerateLicenseKey_smoke_generateLicenceKey")){     
    		  int length=licenseKey.length();
    		  if(length==16){
    			  status =true;
    		  	}
    		  else{
    			  status =false;
    		  	}
    	   }
    	  else
    		  status =true;
     }			
		else {
			status =false;
			logger.error(res);
		}
     if (!status) {
			logger.debug(res);
		}
		Verify.verify(status);
		softAssert.assertAll();
}
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "mapLicenseKey")
	public Object[][] readData3(ITestContext context) throws Exception {
				return new TestCaseReader().readTestCases(moduleName + "/" + folderPath1, testLevel);
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
	public void mapLicenceKey(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, folderPath1, testcaseName);
		JSONObject actualRequest_map = objectDataArray[0];
		Expectedresponse = objectDataArray[1];
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		 
	    // adding the tspid and corresponding license key to the request and expected response od smok test case
	    JSONObject request = (JSONObject) actualRequest_map.get("request");
	    if(testCaseName.contains("Kernel_MapLicenseKeyPermission_smoke"))
	    {
	    	request.put("tspId", tspId);
	    	request.put("licenseKey", licenseKey);	
	    	actualRequest_map.putAll(request);
	    }    
	    	// Calling the Post method 
			 res_map = applicationLibrary.postWithJson(mapLicenseKey, actualRequest_map);
	    
	  // Comparing expected and actual response
	   status= assertKernel.assertKernel(res_map, Expectedresponse,listOfElementToRemove);
      if (status) {
    	     status =true;
			}	
		
		else {
			status =false;
		}
      if (!status) {
			logger.debug(res_map);
		}
		Verify.verify(status);
		softAssert.assertAll();
}
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "fetchmapLicenseKey")
	public Object[][] readData(ITestContext context) throws Exception {	 
				return new TestCaseReader().readTestCases(moduleName + "/" + folderPath2, testLevel);
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
	public void fetchMapLicenceKeyPermissions(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
  
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, folderPath2, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		Expectedresponse = objectDataArray[1];
		//adding the tspid and corrosponding license key to the smoke request
		if(testCaseName.contains("Kernel_FetchLicenseKeyPermissions_smoke_FetchLicenseKeyPermissions"))
		{
			actualRequest.put("tspId", tspId);
			actualRequest.put("licenseKey", licenseKey);
		}
		// Calling the get method 
		Response response=applicationLibrary.getWithQueryParam(fetchmapLicenseKey, actualRequest, "");
		// Comparing expected and actual response
		status = assertKernel.assertKernel(response, Expectedresponse,listOfElementToRemove);
      if (status) {	            
    	  status =true;
			}	
		else {
			status =false;
			}
      if (!status) {
			logger.debug(response);
		}
		Verify.verify(status);
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
				f.set(baseTestMethod, testCaseName);	
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
}