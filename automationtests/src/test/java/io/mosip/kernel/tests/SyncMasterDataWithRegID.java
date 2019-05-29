package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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

import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * @author M9010714
 *
 */
public class SyncMasterDataWithRegID extends BaseTestCase implements ITest{

	public SyncMasterDataWithRegID() {
		super();
	}

	// Declaration of all variables
	private static Logger logger = Logger.getLogger(SyncMasterDataWithRegID.class);
	protected static String testCaseName = "";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private AssertKernel assertKernel = new AssertKernel();
	private final Map<String, String> props = new CommonLibrary().kernenReadProperty();
	private final String fetchmasterdata = props.get("fetchmasterdatawithRID");
	private String folderPath = "kernel/SyncMasterDataWithRegID";
	private String outputFile = "SyncMasterDataWithRegIDOutput.json";
	private String requestKeyFile = "SyncMasterDataWithRegIDInput.json";
	private JSONObject Expectedresponse = null;
	private String finalStatus = "";
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie=null;

	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		cookie=auth.getAuthForRegistrationOfficer();

	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "SyncMasterDataWithRegID")
	public Object[][] readData1(ITestContext context) throws Exception { 
	
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, testLevel);
		}
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * syncMasterDataWithRegID
	 * Given input Json as per defined folders When GET request is sent to /syncdata/v1.0/masterdata/{regcenterId}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Test(dataProvider="SyncMasterDataWithRegID")
	public void syncMasterDataWithRegID(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		//Getting the path parameter from the request and also removing it from the request 
		String request=actualRequest.remove("regcenterId").toString();
		HashMap<String, String> regid=new HashMap<String, String>();
		regid.put("regcenterId", request);
		
		
		//  Calling the GET method with both path and query parameter
		 
		Response res=applicationLibrary.getRequestPathQueryParaString(fetchmasterdata, regid, actualRequest,cookie);
		
		// Removing the unstable attributes from response	
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		listOfElementToRemove.add("response.lastSyncTime");
		
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
      if (status) {
	            
				finalStatus = "Pass";
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
				f.set(baseTestMethod, SyncMasterDataWithRegID.testCaseName);		
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/SyncMasterDataWithRegID/SyncMasterDataWithRegIDOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to SyncMasterDataWithRegIDOutput.json file.......................!!");
			}
		}
}
