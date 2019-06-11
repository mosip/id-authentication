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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Verify;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;
/**
 * @author Arunakumar.Rati
 *
 */
public class OtpGenerate extends BaseTestCase implements ITest{
	
	OtpGenerate() {
		super();
	}
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(OtpGenerate.class);
	protected static String testCaseName = "";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private String folderPath = "kernel/otpGenerate";
	private String outputFile = "otpGenerateOutput.json";
	private String requestKeyFile = "otpGenerateInput.json";
	private JSONObject Expectedresponse = null;
	private String finalStatus = "";
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String OTPGeneration = props.get("OTPGeneration");
	private final String OTPValidation = props.get("OTPValidation");
	private Response res=null;
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie;
	private AssertKernel assertKernel = new AssertKernel();
	
    // Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		 cookie=auth.getAuthForRegistrationAdmin();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "otpGenerate")
	public Object[][] readData1(ITestContext context) throws Exception { 
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, testLevel);
		}
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * otpGenerate
	 * Given input Json as per defined folders When POST request is sent to /otpmanager/v1.0/otp/generate
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "otpGenerate")
	public void otpGenerate(String testSuite, Integer i, JSONObject object)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		listOfElementToRemove.add("timestamp");
		listOfElementToRemove.add("otp");
		
	    //making key as frozen key        
    	  if(testCaseName.equalsIgnoreCase("Kernel_otpGenerate_key_frozen"))
    	  {
    		String key=new CommonLibrary().randomAlphaNumeric(5);
    		//adding random string to the request
    		 JSONObject requestArray = (JSONObject)actualRequest.get("request");
    		  requestArray.put("key", key);
    		// Calling the post method 
    		  res=applicationLibrary.postWithJson(OTPGeneration, actualRequest, cookie);
    		  HashMap<String, String> otp=new HashMap<>();
    		  otp.put("key", key);
    		  otp.put("otp", "123456");
    		  for(int k=0;k<3;k++)
    		  {
    			// Calling the get method and making key as frozen
    			  applicationLibrary.getWithQueryParam(OTPValidation, otp,cookie);
    		  }
    		// Calling the post method 

    		   res=applicationLibrary.postWithJson(OTPGeneration, actualRequest, cookie);  
    	  }
    	  else
    		// Calling the post method 
    		   res=applicationLibrary.postWithJson(OTPGeneration, actualRequest, cookie);

    	//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
    	
		// Comparing expected and actual response
    	 status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);	
		
    	  if(status)
    		  finalStatus="Pass";
    	  else
    		  finalStatus="Fail";

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

				f.set(baseTestMethod, OtpGenerate.testCaseName);

				
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/otpGenerate/otpGenerateOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to OtpGenerate.json file.......................!!");
			}
		}
}
