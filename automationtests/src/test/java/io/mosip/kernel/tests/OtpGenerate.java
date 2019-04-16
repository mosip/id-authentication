
package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import io.mosip.dbaccess.KernelMasterDataR;
import io.mosip.dbentity.OtpEntity;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.AssertResponses;
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
		// TODO Auto-generated constructor stub
	}
	/**
	 *  Declaration of all variables
	 */
	private static Logger logger = Logger.getLogger(OtpGenerate.class);
	protected static String testCaseName = "";
	static SoftAssert softAssert=new SoftAssert();
	public static JSONArray arr = new JSONArray();
	boolean status = false;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static AssertKernel assertKernel = new AssertKernel();
	static String dest = "";
	static String folderPath = "kernel/otpGenerate";
	static String outputFile = "otpGenerateOutput.json";
	static String requestKeyFile = "otpGenerateInput.json";
	static JSONObject Expectedresponse = null;
	String finalStatus = "";
	static String testParam="";
	private static final String otpGenerate_URI = "/v1/otpmanager/otp/generate";
	private static final String otpValidate_URI = "/v1/otpmanager/otp/validate";
	private Response res=null;
	
	/*
	 * Data Providers to read the input json files from the folders
	 */
	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
	} 
	
	/**
	 * @return input jsons folders
	 * @throws Exception
	 */
	@DataProvider(name = "otpGenerate")
	public static Object[][] readData1(ITestContext context) throws Exception {
		 testParam = context.getCurrentXmlTest().getParameter("testType");
		switch (testParam) {
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
	 * Given input Json as per defined folders When POST request is sent to /otpmanager/v1.0/otp/generate
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "otpGenerate")
	public void otpGenerate(String testSuite, Integer i, JSONObject object)throws JsonParseException, JsonMappingException, IOException, ParseException {
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
//			Response res=applicationLibrary.postRequest(actualRequest, otpGenerate_URI);
			
		/*
		 *  Removing of unstable attributes from response
		 */
		
		outerKeys.add("responsetime");
		outerKeys.add("timestamp");
		innerKeys.add("otp");
		
		/*
		 *  Comparing expected and actual response
		 */
		
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("otp");
		listOfElementToRemove.add("timestamp");
	            
    	  if(testCaseName.equalsIgnoreCase("invalid_key_frozen"))
    	  {
    		  res=applicationLibrary.postRequest(actualRequest, otpGenerate_URI);
    		  HashMap<String, String> otp=new HashMap<>();
    		  JSONObject requestArray = (JSONObject)actualRequest.get("request");
    		  String key = requestArray.get("key").toString();
    		  otp.put("key", key);
    		  otp.put("otp", "123456");
    		  for(int k=0;k<3;k++)
    		  {
    			  applicationLibrary.getRequestAsQueryParam(otpValidate_URI, otp);
    		  }
    		   res=applicationLibrary.postRequest(actualRequest, otpGenerate_URI);
    		  
    	  }
    	  else
    		   res=applicationLibrary.postRequest(actualRequest, otpGenerate_URI);
    	  
    	  
    	  
    		  status = AssertResponses.assertResponses(res, Expectedresponse, outerKeys, innerKeys);	
		
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
