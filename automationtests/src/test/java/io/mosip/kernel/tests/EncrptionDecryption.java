package io.mosip.kernel.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Verify;

import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.kernel.util.TestCaseReader;
import io.restassured.response.Response;

/**
 * @author Ravi Kant
 *
 */
public class EncrptionDecryption extends BaseTestCase implements ITest {

	EncrptionDecryption() {
		super();
	}

	private static Logger logger = Logger.getLogger(EncrptionDecryption.class);
	private final String jiraID = "MOS-9284";
	private final String moduleName = "kernel";
	private final String apiName = "EncrptionDecryption";
	private final String requestJsonName = "encryptdecryptRequest";
	private final String outputJsonName = "encryptdecryptOutput";

	private final Map<String, String> props = new CommonLibrary().kernenReadProperty();

	private final String encrypt_URI = props.get("encrypt_URI").toString();
	private final String decrypt_URI = props.get("decrypt_URI").toString();

	protected String testCaseName = "";
	SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	public JSONArray arr = new JSONArray();
	Response response = null;
	JSONObject responseObject = null;
	private AssertKernel assertions = new AssertKernel();
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	KernelAuthentication auth = new KernelAuthentication();
	String cookie = null;

	/**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun = true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName + "_" + apiName + "_" + object.toString();
		cookie = auth.getAuthForRegistrationProcessor();
	}

	/**
	 * This data provider will return a test case name
	 * 
	 * @param context
	 * @return test case name as object
	 */
	@DataProvider(name = "fetchData")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel, requestJsonName);
	}

	/**
	 * This fetch the value of the data provider and run for each test case
	 * 
	 * @param fileName
	 * @param object
	 * @throws ParseException
	 * @throws IOException
	 * @throws JsonProcessingException
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "fetchData", alwaysRun = true)
	public void auditLog(String testcaseName, JSONObject object) throws ParseException {
		logger.info("Test Case Name:" + testcaseName);
		object.put("Jira ID", jiraID);

		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);

		JSONObject objectData = objectDataArray[0];
		responseObject = objectDataArray[1];
		String requestData = null;
		JSONObject request = null;
		String encDecCase = "default";
		if (objectData.containsKey("case"))
			encDecCase = objectData.get("case").toString();
		objectData.remove("case");

		requestData = ((JSONObject) objectData.get("request")).get("data").toString();
		String encoded = Base64.getEncoder().encodeToString(requestData.getBytes());
		request = (JSONObject) objectData.get("request");
		request.put("data", encoded);
		objectData.put("request", request);

		logger.info("Json Request Is : " + objectData.toJSONString());

		response = applicationLibrary.postRequest(objectData.toJSONString(), encrypt_URI, cookie);

		logger.info("Expected Response:" + responseObject.toJSONString());

		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(response);
		int statusCode = response.statusCode();
		logger.info("Encryption Status Code is : " + statusCode);
		ArrayList<String> listOfElementToRemove = new ArrayList<String>();
		listOfElementToRemove.add("responsetime");

		JSONObject responseJson = (JSONObject) ((JSONObject) new JSONParser().parse(response.asString()))
				.get("response");

		if (responseJson != null) {

			request = ((JSONObject) objectData.get("request"));
			request.put("data", ((HashMap<String, String>) response.jsonPath().get("response")).get("data"));
			objectData.put("request", request);
			switch (encDecCase) {
			case "diffRefToDecrypt":
				request.put("referenceId", "diffFromEncrypt");
				break;
			case "diffAppIdToDecrypt":
				request.put("applicationId", "diffFromEncrypt");
				break;
			case "diffDataToDecrypt":
				request.put("data", "diffFromEncrypt");
				break;
			case "diffTimeStampBefToDecrypt":
				request.put("timeStamp", "2018-12-09T06:12:52.994Z");
				break;
			case "diffTimeStampAfToDecrypt":
				request.put("timeStamp", "2018-12-11T06:12:52.994Z");
				break;

			}
			objectData.put("request", request);
			
			response = applicationLibrary.postRequest(objectData.toJSONString(), decrypt_URI, cookie);
			statusCode = response.statusCode();
			logger.info("Decryption Status Code is : " + statusCode);
			//This method is for checking the authentication is pass or fail in rest services
			new CommonLibrary().responseAuthValidation(response);
			responseJson = (JSONObject) ((JSONObject)new JSONParser().parse(response.asString())).get("response");
			if (responseJson!=null) {

				String responseData = new String(
						Base64.getDecoder().decode(((HashMap<String, String>) response.jsonPath().get("response"))
								.get("data").toString().getBytes()));

				status = requestData.equals(responseData);

			} else {
				status = assertions.assertKernel(response, responseObject, listOfElementToRemove);

			}

		}

		else {
			status = assertions.assertKernel(response, responseObject, listOfElementToRemove);

		}

		if (!status) {
			logger.debug(response);
			object.put("status", "Fail");
		} else if (status) {
			object.put("status", "Pass");
		}
		Verify.verify(status);
		softAssert.assertAll();
		arr.add(object);
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
		String configPath = "src/test/resources/" + moduleName + "/" + apiName + "/" + outputJsonName + ".json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputJsonName + ".json file.......................!!");
		}
	}
}
