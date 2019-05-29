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
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * @author Arunakumar.Rati
 *
 */
public class OtpNotificationController extends BaseTestCase implements ITest{

	public OtpNotificationController() {
		
		super();
	}
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(OtpNotificationController.class);
	protected static String testCaseName = "";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private final Map<String, String> props = new CommonLibrary().kernenReadProperty();
	private final String otpNotifier =props.get("otpNotifier");
	private String folderPath = "kernel/OtpNotificationController";
	private String outputFile = "OtpNotificationControllerOutput.json";
	private String requestKeyFile = "OtpNotificationControllerInput.json";
	private JSONObject Expectedresponse = null;
	private String finalStatus = "";
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie;

	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		 cookie=auth.getAuthForRegistrationAdmin();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "OtpNotificationController")
	public Object[][] readData1(ITestContext context) throws Exception { 

			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, testLevel);
		}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * otpNotificationController
	 * Given input Json as per defined folders When GET request is sent to /otpnotifier/v1.0/otp/send
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="OtpNotificationController")
	public void otpNotificationController(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		//Calling POST method 
		Response res=applicationLibrary.postRequest(actualRequest, otpNotifier,cookie);
		
		// Removing of unstable attributes from response
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		outerKeys.add("timestamp");
		
		// Comparing expected and actual response
		status = AssertResponses.assertResponses(res, Expectedresponse, outerKeys, innerKeys);
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
				f.set(baseTestMethod, OtpNotificationController.testCaseName);
				
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/OtpNotificationController/OtpNotificationControllerOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to OtpNotificationControllerOutput.json file.......................!!");
			}
		}

		
}
