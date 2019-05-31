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
 * @author M9010714
 *
 */
public class UINStatusUpdate extends BaseTestCase implements ITest {

	public UINStatusUpdate() {
		
		super();
	}
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(UINStatusUpdate.class);
	protected static String testCaseName = "";
	static SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private final Map<String, String> props = new CommonLibrary().kernenReadProperty();
	private final String uingenerator =props.get("uingenerator");
	private String folderPath = "kernel/UINStatusUpdate";
	private String outputFile = "UINStatusUpdateOutput.json";
	private String requestKeyFile = "UINStatusUpdateInput.json";
	private JSONObject Expectedresponse = null;
	private String finalStatus = "";
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie=null;
	private Response res=null;
	private String uin="";
	private Response res1=null;
	private String uin1="";
	private JSONObject response=null;

	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		cookie=auth.getAuthForRegistrationProcessor();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "UINStatusUpdate")
	public Object[][] readData1(ITestContext context) throws Exception {
		switch (testLevel) {
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
	 * Given input Json as per defined folders When GET request is sent to /uingenerator/v1.0/uin
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings({ "unchecked"})
	@Test(dataProvider="UINStatusUpdate")
	public void updateUINStatusUpdate(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		// Removing of unstable attributes from response
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		outerKeys.add("responsetime");
		outerKeys.add("response.uin");
		outerKeys.add("response.status");
		innerKeys.add("uin");
		innerKeys.add("status");
		
		
		switch(testCaseName)
		{

		case "Kernel_UINStatusUpdate_UIN_Status_smoke_IssuedToUnused": 
			res1=applicationLibrary.getRequestNoParameter(uingenerator,cookie);
			uin=res1.jsonPath().get("response.uin");
			JSONObject request=(JSONObject) actualRequest.get("request");
			request.put("uin", uin);
			response=(JSONObject) Expectedresponse.get("response");
			response.put("uin", uin);
			break;
			

		case "Kernel_UINStatusUpdate_UIN_Status_AssignedToIssued" : 
			res1=applicationLibrary.getRequestNoParameter(uingenerator,cookie);
			uin1=res1.jsonPath().get("response.uin");
			request=(JSONObject) actualRequest.get("request");
			request.put("uin", uin1);
			request.put("status", "ASSIGNED");
			actualRequest.put("request", request);
			res=applicationLibrary.putRequestWithBody(uingenerator, actualRequest,cookie);
			break;
			

		case "Kernel_UINStatusUpdate_UIN_Status_AssignedToUnused":
			res1=applicationLibrary.getRequestNoParameter(uingenerator,cookie);
			uin1=res1.jsonPath().get("response.uin");
			request=(JSONObject) actualRequest.get("request");
			request.put("uin", uin1);
			request.put("status", "ASSIGNED");
			res=applicationLibrary.putRequestWithBody(uingenerator, actualRequest,cookie);
			request.put("status", "UNASSIGNED");
			break;
			

		case "Kernel_UINStatusUpdate_UIN_Status_IssuedToAssigned" :
			res1=applicationLibrary.getRequestNoParameter(uingenerator,cookie);
			uin1=res1.jsonPath().get("response.uin");
			request=(JSONObject) actualRequest.get("request");
			request.put("uin", uin1);
			response=(JSONObject) Expectedresponse.get("response");
			response.put("uin", uin1);
			break;
			
			
		default : break;
		}
		
		res=applicationLibrary.putRequestWithBody(uingenerator, actualRequest,cookie);
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		// Comparing expected and actual response
		status = AssertResponses.assertResponses(res, Expectedresponse, outerKeys, innerKeys);
      if (status) {
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
				f.set(baseTestMethod, UINStatusUpdate.testCaseName);	
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/UINStatusUpdate/UINStatusUpdateOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to UINStatusUpdateOutput.json file.......................!!");
			}
		}
	
}
