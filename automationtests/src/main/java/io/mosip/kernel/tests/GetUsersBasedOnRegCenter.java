package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.google.common.base.Verify;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author Arunakumar Rati
 *
 */
public class GetUsersBasedOnRegCenter extends BaseTestCase implements ITest {

	public GetUsersBasedOnRegCenter() {
		super();
	}
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(GetUsersBasedOnRegCenter.class);
	protected  String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "GetusersBasedOnRegCenter";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String getusersBasedOnRegCenter = props.get("getusersBasedOnRegCenter");
	private JSONObject Expectedresponse = null;
	private KernelAuthentication auth=new KernelAuthentication();

	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();
		if(!lib.isValidToken(registrationOfficerCookie))
			registrationOfficerCookie=auth.getAuthForRegistrationOfficer();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "GetusersBasedOnRegCenter")
	public Object[][] readData1(ITestContext context) throws Exception {
			return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
	}
	
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getusersBasedOnRegCenter
	 * Given input Json as per defined folders When GET request is sent to /syncdata/v1.0/configuration/{registrationCenterId}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetusersBasedOnRegCenter")
	public void getusersBasedOnRegCenter(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		Expectedresponse = objectDataArray[1];		
		// Calling the get method 
		Response res=applicationLibrary.getWithPathParam(getusersBasedOnRegCenter, actualRequest,registrationOfficerCookie);
		
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		
		// Removing of unstable attributes from response		
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		outerKeys.add("responsetime");
		innerKeys.add("lastSyncTime");
		if(testCaseName.equalsIgnoreCase("Kernel_GetusersBasedOnRegCenter_smoke") || testCaseName.equals("Kernel_GetusersBasedOnRegCenter_response_time"))
		{
			String password = res.getBody().jsonPath().get("response.userDetails[0].userPassword").toString();
			String password1 = res.getBody().jsonPath().get("response.userDetails[1].userPassword").toString();
			 ((JSONObject)((JSONArray)((JSONObject)Expectedresponse.get("response")).get("userDetails")).get(0)).put("userPassword", password).toString();
			 ((JSONObject)((JSONArray)((JSONObject)Expectedresponse.get("response")).get("userDetails")).get(1)).put("userPassword", password1).toString();
		}
		// Comparing expected and actual response
		status = AssertResponses.assertResponses(res, Expectedresponse, outerKeys, innerKeys);
		if (!status) {
			logger.debug(res);
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
