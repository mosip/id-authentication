package io.mosip.kernel.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Verify;
import com.google.common.io.BaseEncoding;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.mosip.util.GetHeader;
import io.restassured.response.Response;

/**
 * @author Ravi Kant
 *
 */
public class SyncPublicKeyToRegClient extends BaseTestCase implements ITest {
	SyncPublicKeyToRegClient() {
		super();
	}

	private static Logger logger = Logger.getLogger(SyncPublicKeyToRegClient.class);
	private final String jiraID = "MOS-997";
	private final String moduleName = "kernel";
	private final String apiName = "SyncPublicKeyToRegClient";
	private final String requestJsonName = "syncPublicKeyRequest";
	private final String outputJsonName = "syncPublicKeyOutput";
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String SyncPublicKeyToRegClient_URI = props.get("SyncPublicKeyToRegClient_URI").toString();
	protected String testCaseName = "";
	SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	String finalStatus = "";
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
		cookie = auth.getAuthForRegistrationAdmin();
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
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "fetchData", alwaysRun = true)
	public void syncPublicKeyToRegClient(String testcaseName, JSONObject object)
			throws JsonParseException, JsonMappingException, IOException {
		logger.info("Test Case Name:" + testcaseName);
		object.put("Jira ID", jiraID);

		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);

		JSONObject objectData = objectDataArray[0];
		responseObject = objectDataArray[1];
		String applicationId = objectData.get("applicationId").toString();
		objectData.remove("applicationId");
		response = applicationLibrary.getWithQueryParam(SyncPublicKeyToRegClient_URI + applicationId,
				GetHeader.getHeader(objectData), cookie);

		// This method is for checking the authentication is pass or fail in rest
		// services
		new CommonLibrary().responseAuthValidation(response);

		ArrayList<String> listOfElementToRemove = new ArrayList<String>();
		listOfElementToRemove.add("responsetime");

		if (testcaseName.toLowerCase().contains("smoke")) {
			String referenceId = (objectData.get("referenceId")).toString();
			String queryStr = "select public_key from kernel.key_store where id = (select id from kernel.key_alias where ref_id = '"
					+ referenceId + "' and app_id='" + applicationId + "')";

			List<Object> publicKey = new KernelDataBaseAccess().getData(queryStr, "kernel");
			String s = null;
			if (publicKey.size() > 0) {
				byte b[] = (byte[]) publicKey.get(0);
				s = BaseEncoding.base64().encode(b);
			}
			if (s != null) {
				s = s.replace('/', '_');
				s = s.replace('+', '-');
			}

			logger.info("obtained key from db : " + s);

			status = (((HashMap<String, String>) response.jsonPath().get("response")).get("publicKey")).toString()
					.equals(s);

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
