package io.mosip.authentication.idrepo.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
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
import io.mosip.util.PropertyFileLoader;
import io.mosip.util.RidGenerator;
import io.mosip.util.TestCaseReader;
import io.mosip.util.TestDataGenerator;
import io.restassured.response.Response;

public class RetriveIdentityDetail extends BaseTestCase implements ITest {
	RetriveIdentityDetail() {
		super();
	}

	private static Logger logger = Logger.getLogger(RetriveIdentityDetail.class);
	private static final String jiraID = "MOS-12231";
	private static final String moduleName = "IdRepo";
	private static final String apiName = "RetriveIdentityDetail";
	private static final String requestJsonStructure = "RequestMasterJsonStructure";
	private static final String outputJsonName = "RetriveIdentityDetailOutput";
	private static final String service_URI = "/idrepo/identity/v1.0/";
	private static final String service_URI_uin = "/uingenerator/v1.0/uin";
	private static final String testDataFileName = "TestData";

	protected static String testCaseName = "";
	static SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	String finalStatus = "";
	private JSONObject expectedResponse = null;
	private Response actualResponse = null;
	public static JSONArray arr = new JSONArray();
	private static AssertKernel assertions = new AssertKernel();
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static Properties prop = null;

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
		prop = new PropertyFileLoader().readPropertyFile(moduleName, apiName, requestJsonStructure);

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
		switch ("regression") {
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
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * 
	 */

	@SuppressWarnings("unchecked")
	@Test(dataProvider = "FetchData", alwaysRun = true)
	public void validatingTestCases(String testcaseName, JSONObject object)
			throws JsonParseException, JsonMappingException, IOException, ParseException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		logger.info("Test Case Name:" + testcaseName);
		object.put("Test case Name", testcaseName);
		object.put("Jira ID", jiraID);

		/**
		 *  reading the master request Json
		 */
		Object requestJsonStruct = new TestCaseReader().readRequestJson(moduleName, apiName, requestJsonStructure);

		/**
		 *  getting all the keys of the master json
		 */
		Set<Object> propertyFileKeys = prop.keySet();
		String testDataProperty = "";
		String testDataValue = "";
		JSONObject inputJson = null;
		inputJson = (JSONObject) new JSONParser().parse(requestJsonStruct.toString());
		
		/**
		 *  generating test data for each key or removing the element if not required
		 */
		for (Object key : propertyFileKeys) {
			if (testcaseName.split("_")[1].equalsIgnoreCase(key.toString()))
				testDataProperty = "invalid";
			else
				testDataProperty = "valid";

			testDataValue = new TestDataGenerator().getYamlData(moduleName, apiName, testDataFileName,
					key.toString() + "_" + testDataProperty);

			if (!testDataValue.isEmpty()) {
				if (testDataValue.contains("BOOLEAN"))
					PropertyUtils.setProperty(inputJson, prop.get(key.toString()).toString(),
							Boolean.parseBoolean(testDataValue.split(":")[1]));
				else if (testDataValue.contains("DOUBLE"))
					PropertyUtils.setProperty(inputJson, prop.get(key.toString()).toString(),
							Double.parseDouble(testDataValue.split(":")[1]));
				else if (testDataValue.contains("INTEGER"))
					PropertyUtils.setProperty(inputJson, prop.get(key.toString()).toString(),
							Integer.parseInt(testDataValue.split(":")[1]));
				else if (testDataValue.contains("LONG"))
					PropertyUtils.setProperty(inputJson, prop.get(key.toString()).toString(),
							Long.parseLong(testDataValue.split(":")[1]));
				else
					PropertyUtils.setProperty(inputJson, prop.get(key.toString()).toString(), testDataValue);
			}

		}
		
		/**
		 *  getting new uin 
		 */
		JSONObject uinObject = (JSONObject) new JSONParser()
				.parse(applicationLibrary.GetRequestNoParameter(service_URI_uin).asString());
		
		
		String uin="";
		if(testcaseName.split("_")[1].equalsIgnoreCase("noRecordFound"))
			uin=(String) uinObject.get("uin");
		else
		uin=inputJson.get("uin").toString();

		HashMap<String, String> inputParameters = new HashMap<>();
		inputParameters.put("type", inputJson.get("type").toString());
		actualResponse = applicationLibrary.getRequestAsQueryParam(service_URI + uin, inputParameters);

		String configPath = "src/test/resources/" + moduleName + "/" + apiName + "/" + testcaseName;
		File folder = new File(configPath);
		File[] listofFiles = folder.listFiles();

		for (int k = 0; k < listofFiles.length; k++) {
			if (listofFiles[k].getName().toLowerCase().contains("response"))
				expectedResponse = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
		}
		logger.info("Input uin:" + inputJson.get("uin").toString());
		logger.info("Input type:" + inputJson.get("type").toString());
		logger.info("Actual Response:" + actualResponse.asString());
		logger.info("Expected Response:" + expectedResponse.toJSONString());

		/**
		 *  add parameters to remove in response before comparison like time stamp
		 */
		ArrayList<String> listOfElementToRemove = new ArrayList<String>();
		listOfElementToRemove.add("timestamp");
			status = assertions.assertKernel(actualResponse, expectedResponse, listOfElementToRemove);

		if (status)
			finalStatus = "Pass";
		else
			finalStatus = "Fail";

		object.put("status", finalStatus);

		arr.add(object);
		boolean setFinalStatus = false;
		if (finalStatus.equals("Fail")) {
			setFinalStatus = false;
			logger.debug(actualResponse);
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
	 * this method write the output to corresponding json
	 */
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath = "./src/test/resources/" + moduleName + "/" + apiName + "/" + outputJsonName + ".json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputJsonName + ".json file.......................!!");
		}
	}
}
