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
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author M9010714
 *
 */
public class UINStatusUpdate extends BaseTestCase implements ITest {

	public UINStatusUpdate() {
		
		super();
	}
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(UINStatusUpdate.class);
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "UINStatusUpdate";
	static SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String uingenerator =props.get("uingenerator");
	private JSONObject Expectedresponse = null;
	private KernelAuthentication auth=new KernelAuthentication();
	private Response res=null;
	private String uin="";
	private Response res1=null;
	private String uin1="";
	private JSONObject response=null;
	public KernelDataBaseAccess dbConnection=new KernelDataBaseAccess();
	private String query=null;
	private String UIN=null;
	private AssertKernel assertKernel = new AssertKernel();

	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();
		if(!lib.isValidToken(regProcCookie))
			regProcCookie=auth.getAuthForRegistrationProcessor();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "UINStatusUpdate")
	public Object[][] readData1(ITestContext context) throws Exception {
		return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
	}
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * updateUINStatusUpdate
	 * Given input Json as per defined folders When GET request is sent to /uingenerator/v1.0/uin
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings({ "unchecked"})
	@Test(dataProvider="UINStatusUpdate")
	public void updateUINStatusUpdate(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		Expectedresponse = objectDataArray[1];
		
		// Removing of unstable attributes from response
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		outerKeys.add("responsetime");
		outerKeys.add("response.uin");
		outerKeys.add("response.status");
		innerKeys.add("uin");
		innerKeys.add("status");
		
		//Fetching UIN which status is unused
		res1=applicationLibrary.getWithoutParams(uingenerator,regProcCookie);
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res1);
	
	switch(testCaseName)
		{
		case "Kernel_UINStatusUpdate_UIN_Status_smoke_IssuedToUnused": 
			uin=res1.jsonPath().get("response.uin");
			JSONObject request=(JSONObject) actualRequest.get("request");
			request.put("uin", uin);
			response=(JSONObject) Expectedresponse.get("response");
			response.put("uin", uin);
			break;

		case "Kernel_UINStatusUpdate_UIN_Status_AssignedToIssued" : 
			uin1=res1.jsonPath().get("response.uin");
			request=(JSONObject) actualRequest.get("request");
			request.put("uin", uin1);
			request.put("status", "ASSIGNED");
			actualRequest.put("request", request);
			res=applicationLibrary.putWithJson(uingenerator, actualRequest,regProcCookie);
			break;

		case "Kernel_UINStatusUpdate_UIN_Status_AssignedToUnused":
			uin1=res1.jsonPath().get("response.uin");
			request=(JSONObject) actualRequest.get("request");
			request.put("uin", uin1);
			request.put("status", "ASSIGNED");
			res=applicationLibrary.putWithJson(uingenerator, actualRequest,regProcCookie);
			request.put("status", "UNASSIGNED");
			break;

		case "Kernel_UINStatusUpdate_UIN_Status_IssuedToAssigned" :
			uin1=res1.jsonPath().get("response.uin");
			request=(JSONObject) actualRequest.get("request");
			request.put("uin", uin1);
			response=(JSONObject) Expectedresponse.get("response");
			response.put("uin", uin1);
			break;
			
		case "Kernel_UINStatusUpdate_UIN_Status_UnusedToAssigned":
			//Getting the status of the UIN 
			 query="select u.uin from kernel.uin u where u.uin_status='UNUSED'";
			 UIN = dbConnection.getDbData( query,"kernel").get(0);
			request=(JSONObject) actualRequest.get("request");
			request.put("uin", UIN);
			break;
			
		case "Kernel_UINStatusUpdate_UIN_Status_empty_status":
			//Getting the status of the UIN 
			 query="select u.uin from kernel.uin u where u.uin_status='UNUSED'";
			 UIN = dbConnection.getDbData( query,"kernel").get(0);
			request=(JSONObject) actualRequest.get("request");
			request.put("uin", UIN);
			break;
			
		default : break;
		}
		res=applicationLibrary.putWithJson(uingenerator, actualRequest,regProcCookie);
		
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
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
