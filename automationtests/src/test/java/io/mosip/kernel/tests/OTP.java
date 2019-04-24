package io.mosip.kernel.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
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
	private static final String jiraID = "MOS-33/34/35/36/423/5486/991";
	private static final String moduleName = "kernel";
	private static final String apiName = "OTP";
	private static final String requestJsonName = "OTPRequest";
	private static final String outputJsonName = "OTPOutput";
	private static final String service_URI_OTPGeneration = "/v1/otpmanager/otp/generate";
	private static final String service_URI_OTPValidation = "/v1/otpmanager/otp/validate";

	protected static String testCaseName = "";
	static SoftAssert softAssert = new SoftAssert();
	static boolean status = true;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	static Response response = null;
	static JSONObject responseObject = null;
	private static AssertKernel assertions = new AssertKernel();
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();

	/**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();

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
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		switch (testParam) {
		case "smoke":
			return TestCaseReader.readTestCases(moduleName + "/" + apiName, "smoke");

		case "regression":
			return TestCaseReader.readTestCases(moduleName + "/" + apiName, "regression");
		default:
			return TestCaseReader.readTestCases(moduleName + "/" + apiName, "smokeAndRegression");
		}

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
		for (int k = 0; k < listofFiles.length; k++) {

			if (listofFiles[k].getName().toLowerCase().contains("request")) {
				objectData = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
				JSONObject objectDataToSent = new JSONObject();
				objectDataToSent.put("key", objectData.get("key"));
				logger.info("Json Request Is : " + objectDataToSent.toJSONString());
				response = applicationLibrary.postRequest(objectDataToSent.toJSONString(), service_URI_OTPGeneration);
				
			} else if (listofFiles[k].getName().toLowerCase().contains("response"))
				responseObject = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
		}

		logger.info("Expected Response:" + responseObject.toJSONString());

		// add parameters to remove in response before comparison like time stamp
		ArrayList<String> listOfElementToRemove = new ArrayList<String>();
		listOfElementToRemove.add("timestamp");

		int statusCode = response.statusCode();
		logger.info("Status Code is : " + statusCode);

		if (objectData.containsKey("case")) {
			if (statusCode == 200) {
				
				String otp = (response.jsonPath().get("otp")).toString();
				logger.info("otp is : " + otp);
				String value = objectData.get("case").toString();
				objectData.remove("case");
				
				switch(value) {
				
				case "blockUser":
					objectData.put("otp", "000000");
					for(int i=0;i<3;i++)
					{
						response = applicationLibrary.getRequestAsQueryParam(service_URI_OTPValidation, objectData);
					}
					break;
				case "otherKey":
					objectData.put("otp", otp);
					objectData.put("key", "otherKey");
					break;
				case "alphanumeric":
					objectData.put("otp", "12ad23");
					break;
				case "otpEmpty":
					objectData.put("otp", "");
					break;
				case "keyEmpty":
					objectData.put("key", "");
					objectData.put("otp", otp);
					break;
				case "expired":
					try {
						TimeUnit.SECONDS.sleep(121);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					objectData.put("otp", otp);
					break;
				case "incorrect":
					objectData.put("otp", "000000");
					break;
				default:
					objectData.put("otp", otp);
				}
				
					
				response = applicationLibrary.getRequestAsQueryParam(service_URI_OTPValidation, objectData);

				logger.info("Validation Response is : " + response);
				status = assertions.assertKernel( response, responseObject, listOfElementToRemove);

			}
		} else {
			
			if (objectData.get("key").equals("Blocked"))
			{
				objectData.put("otp", "000000");
				for(int i=0;i<4;i++)
				{
					response = applicationLibrary.getRequestAsQueryParam(service_URI_OTPValidation, objectData);
				}
				objectData.remove("otp");
				response = applicationLibrary.postRequest(objectData.toJSONString(), service_URI_OTPGeneration);
			}
			listOfElementToRemove.add("otp");
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