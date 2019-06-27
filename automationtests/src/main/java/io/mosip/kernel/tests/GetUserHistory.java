package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class GetUserHistory extends BaseTestCase implements ITest{

	public GetUserHistory() {
		
		super();
	}
	
    //Declaration of all variables
	private static Logger logger = Logger.getLogger(GetUserHistory.class);
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "GetUserHistory";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String getUserHistory = props.get("getUserHistory");
	private JSONObject Expectedresponse = null;
	private KernelAuthentication auth=new KernelAuthentication();
	private AssertKernel assertKernel = new AssertKernel();
	
	// Before method is to get the test case name from the input folders
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName+"_"+apiName+"_"+object.toString();
		if(!lib.isValidToken(regProcCookie))
			regProcCookie=auth.getAuthForRegistrationProcessor();
	} 
	
	// Data Providers to read the input json files from the folder	 
	@DataProvider(name = "GetUserHistory")
	public Object[][] readData1(ITestContext context) throws Exception {	 
			return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
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
	public void getUserHistory(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		Expectedresponse = objectDataArray[1];
		
		if(testCaseName.contains("smoke") | testCaseName.contains("validating_request")|testCaseName.contains("firstUpdateDate")|testCaseName.contains("secondUpdateDate")) {
			// getting current timestamp and changing it to yyyy-MM-ddTHH:mm:ss.sssZ format.
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
			Calendar calender = Calendar.getInstance();
			calender.setTime(new Date());
			String time = sdf.format(calender.getTime());
			time = time.replace(' ', 'T')+"Z";
			actualRequest.put("eff_dtimes", time);
		}
		// Calling the get method 
		Response res=applicationLibrary.getWithPathParam(getUserHistory, actualRequest,regProcCookie);
		
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		listOfElementToRemove.add("lastSyncTime");
		
		// Comparing expected and actual response
		status=assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
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
