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

public class SendOTP extends BaseTestCase implements ITest{

	public SendOTP() {
		super();
	}


	// Declaration of all variables
	private static Logger logger = Logger.getLogger(SendOTP.class);
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "SendOtp";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String sendOtp = props.get("sendOtp");
	private AssertKernel assertKernel = new AssertKernel();
	private JSONObject Expectedresponse = null;
	
	//Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName+"_"+apiName+"_"+object.toString();
	}
	
	 //Data Providers to read the input json files from the folders
	@DataProvider(name = "SendOTP")
	public Object[][] readData1(ITestContext context) throws Exception { 
			return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
		}
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getAllTemplateByTemplateTypeCode
	 * Given input Json as per defined folders When post request is sent to /masterdata/v1.0/authmanager/authenticate/sendotp
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@Test(dataProvider="SendOTP")
	public void sendOTP(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		Expectedresponse = objectDataArray[1];
	    // Calling the get method 
		Response res=applicationLibrary.postWithJson(sendOtp, actualRequest);
		
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
      if (status) {
				status = true;
      }
		else {
			status =false;
			logger.debug(res.asString());
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
