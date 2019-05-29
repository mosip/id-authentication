package io.mosip.kernel.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

import io.mosip.kernel.util.CommonLibrary;

import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;

import io.mosip.service.BaseTestCase;
import io.mosip.util.TestCaseReader;
import io.restassured.response.Response;

/**
 * @author Ravi Kant
 *
 */
public class OTP extends BaseTestCase implements ITest {
	OTP() {
		super();
	}

	private static Logger logger = Logger.getLogger(OTP.class);
	private  final String jiraID = "MOS-33/34/35/36/423/5486/991";
	private final String moduleName = "kernel";
	private final String apiName = "OTP";
	private final String requestJsonName = "OTPRequest";
	private final String outputJsonName = "OTPOutput";
	private final Map<String, String> props = new CommonLibrary().kernenReadProperty();
	private final String OTPGeneration = props.get("OTPGeneration").toString();
	private final String OTPValidation = props.get("OTPValidation").toString();

	protected String testCaseName = "";
	SoftAssert softAssert = new SoftAssert();
	boolean status = true;
	String finalStatus = "";
	public JSONArray arr = new JSONArray();
	Response response = null;
	JSONObject responseObject = null;
	private AssertKernel assertions = new AssertKernel();
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie;

	/**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName+"_"+apiName+"_"+object.toString();
		cookie=auth.getAuthForIndividual();
	}

	/**
	 * This data provider will return a test case name
	 * 
	 * @param context
	 * @return test case name as object
	 */
	@DataProvider(name = "FetchData")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
			return TestCaseReader.readTestCases(moduleName + "/" + apiName,testLevel);
		}
	/**
	 * This fetch the value of the data provider and run for each test case
	 * 
	 * @param fileName
	 * @param object
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "FetchData", alwaysRun = true)
	public void otp(String testcaseName, JSONObject object)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		logger.info("Test Case Name:" + testcaseName);

		object.put("Test case Name", testcaseName);
		object.put("Jira ID", jiraID);

		String fieldNameArray[] = testcaseName.split("_");
		String fieldName = fieldNameArray[1];

		JSONObject requestJson = new TestCaseReader().readRequestJson(moduleName, apiName, requestJsonName);

		for (Object key : requestJson.keySet()) {
			if (fieldName.equals(key.toString()))
				object.put(key.toString(), "invalid");
			else
				object.put(key.toString(), "valid");
		}

		String configPath ="src/test/resources/" + moduleName + "/" + apiName
				+ "/" + testcaseName;
		File folder = new File(configPath);
		File[] listofFiles = folder.listFiles();
		JSONObject objectData = null;
		String otpCase = null;
		for (int k = 0; k < listofFiles.length; k++) {

			if (listofFiles[k].getName().toLowerCase().contains("request")) {
				objectData = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
				if(objectData.containsKey("case"))
				{
					otpCase = objectData.get("case").toString();
					objectData.remove("case");
				}
				logger.info("Json Request Is : " + objectData.toJSONString());

				response = applicationLibrary.postRequest(objectData.toJSONString(), OTPGeneration,cookie);

				
			} else if (listofFiles[k].getName().toLowerCase().contains("response"))
				responseObject = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
		}

		// add parameters to remove in response before comparison like time stamp
		ArrayList<String> listOfElementToRemove = new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		listOfElementToRemove.add("timestamp");

		int statusCode = response.statusCode();
		logger.info("Status Code is : " + statusCode);

		if (otpCase!=null && !otpCase.equals("blocked")) {
			if (statusCode == 200) {
				
				String otp = ((HashMap<String, String>)response.jsonPath().get("response")).get("otp").toString();
				logger.info("otp is : " + otp);
				JSONObject reqJson = (JSONObject) objectData.get("request");
				switch(otpCase) {
				
				case "blockUser":
					reqJson.put("otp", "000000");

					// reading validation attempt from property
					int attempt = Integer.parseInt(props.get("attempt").toString());
					for(int i=0;i<attempt;i++)
					{
						response = applicationLibrary.getRequestAsQueryParam(OTPValidation, reqJson,cookie);

					}
					break;
				case "otherKey":
					reqJson.put("otp", otp);
					reqJson.put("key", "otherKey");
					break;
				case "alphanumeric":
					reqJson.put("otp", "12ad23");
					break;
				case "otpEmpty":
					reqJson.put("otp", "");
					break;
				case "keyEmpty":
					reqJson.put("key", "");
					reqJson.put("otp", otp);
					break;
				case "expired":
					try {
						// reading validation timeout from property
						TimeUnit.SECONDS.sleep(Integer.parseInt(props.get("OTPTimeOut").toString()));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					reqJson.put("otp", otp);
					break;
				case "incorrect":
					reqJson.put("otp", "000000");
					break;
				default:
					reqJson.put("otp", otp);
				}
				
				response = applicationLibrary.getRequestAsQueryParam(OTPValidation, reqJson,cookie);


				logger.info("Obtained Response: " + response);
				logger.info("Expected Response:" + responseObject.toJSONString());
				status = assertions.assertKernel( response, responseObject, listOfElementToRemove);

			}
		} else {
			if (otpCase!=null && otpCase.equals("blocked"))
			{
				
				JSONObject reqJson = (JSONObject) objectData.get("request");
				reqJson.put("otp", "000000");

				int attempt = Integer.parseInt(props.get("attempt").toString());
				for(int i=0;i<=attempt;i++)
				{
					response = applicationLibrary.getRequestAsQueryParam(OTPValidation, reqJson,cookie);
				}
				reqJson.remove("otp");
				response = applicationLibrary.postRequest(objectData.toJSONString(), OTPGeneration,cookie);
			}
			listOfElementToRemove.add("otp");
			logger.info("Obtained Response: " + response);
			logger.info("Expected Response:" + responseObject.toJSONString());
			status = assertions.assertKernel(response, responseObject, listOfElementToRemove);

		}

		if (status) {
			finalStatus = "Pass";
		} else {
			finalStatus = "Fail";
		}
		object.put("status", finalStatus);

		arr.add(object);
		boolean setFinalStatus = false;
		if (finalStatus.equals("Fail")) {
			setFinalStatus = false;
			logger.debug(response);
		} else if (finalStatus.equals("Pass"))
			setFinalStatus = true;
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
			f.set(baseTestMethod, testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * this method write the output to corressponding json
	 */
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath = "./src/test/resources/" + moduleName + "/" + apiName
				+ "/" + outputJsonName + ".json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputJsonName + ".json file.......................!!");
		}
	}
}