package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.FileWriter;
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
import org.testng.annotations.AfterClass;
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
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

public class GetUserHistory extends BaseTestCase implements ITest{

	public GetUserHistory() {
		
		super();
	}
	
    //Declaration of all variables
	private static Logger logger = Logger.getLogger(GetUserHistory.class);
	protected static String testCaseName = "";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String getUserHistory = props.get("getUserHistory");
	private String folderPath = "kernel/GetUserHistory";
	private String outputFile = "GetUserHistoryOutput.json";
	private String requestKeyFile = "GetUserHistoryInput.json";
	private JSONObject Expectedresponse = null;
	private String finalStatus = "";
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie;
	private AssertKernel assertKernel = new AssertKernel();
	
	// Before method is to get the test case name from the input folders
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = "Kernel_"+"GetUserHistory_"+object.get("testCaseName").toString();
		 cookie=auth.getAuthForRegistrationProcessor();
	} 
	
	// Data Providers to read the input json files from the folder	 
	@DataProvider(name = "GetUserHistory")
	public Object[][] readData1(ITestContext context) throws Exception {	 
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, testLevel);
		}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getUserHistory
	 * Given input Json as per defined folders When GET request is sent to /syncdata/v1.0/configuration/{registrationCenterId}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetUserHistory")
	public void getUserHistory(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		// Calling the get method 
		Response res=applicationLibrary.getWithPathParam(getUserHistory, actualRequest,cookie);
		
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		listOfElementToRemove.add("lastSyncTime");
		
		// Comparing expected and actual response
		status=assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
      if (status) {
	            
				finalStatus = "Pass";
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
				f.set(baseTestMethod, GetUserHistory.testCaseName);	
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/GetUserHistory/GetUserHistoryOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to GetUserHistoryOutput.json file.......................!!");
			}
		}
}
