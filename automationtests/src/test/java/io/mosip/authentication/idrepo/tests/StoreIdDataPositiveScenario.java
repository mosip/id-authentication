package io.mosip.authentication.idrepo.tests;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.authentication.idrepo.fw.util.PropertyFileLoader;
import io.mosip.authentication.idrepo.fw.util.RidGenerator;
import io.mosip.authentication.idrepo.fw.util.TestDataGenerator;
import io.mosip.authentication.testdata.Precondtion;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.util.TestCaseReader;
import io.restassured.response.Response;

public class StoreIdDataPositiveScenario extends BaseTestCase implements ITest {
	StoreIdDataPositiveScenario() {
		super();
	}

	private static Logger logger = Logger.getLogger(StoreIdDataPositiveScenario.class);
	private static final String jiraID = "MOS-1423,MOS-12231";
	private static final String moduleName = "IdRepo";
	private static final String apiName = "store-id-data-postive-scenario";
	private static final String requestJsonStructure = "RequestMasterJsonStructure";
	private static final String outputJsonName = "PostiveScenerioOutput";
	private static final String service_base_URI = "/idrepo/identity/v1.0/";
	private static final String service_URI_uin = "/uingenerator/v1.0/uin";
	private static final String testDataFileName = "TestData";

	protected static String testCaseName = "";
	static SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	String finalStatus = "";
	private JSONObject responseObject = null;
	private Response response = null;
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
		 * calling the uin generator rest api and storing as JSON object
		 */
		JSONObject uin = (JSONObject) new JSONParser()
				.parse(applicationLibrary.GetRequestNoParameter(service_URI_uin).asString());
		logger.info("Uin generated:" + uin);

		/**
		 * reading the master request Json
		 */
		Object requestJsonStruct = new TestCaseReader().readRequestJson(moduleName, apiName, requestJsonStructure);

		/**
		 * getting all the keys of the master json
		 */
		Set<Object> propertyFileKeys = prop.keySet();
		String testDataProperty = "valid";
		String testDataValue = "";
		JSONObject inputJson = null;
		inputJson = (JSONObject) new JSONParser().parse(requestJsonStruct.toString());
		/**
		 * generating test data for each key
		 */

		for (Object key : propertyFileKeys) {
			/**
			 * calling registration id
			 */
			if ((key.toString().equalsIgnoreCase("registrationId")))
				testDataValue = new RidGenerator().generateRID(testDataProperty);

			else if ((key.toString().equalsIgnoreCase("individualBiometrics.format")))
				testDataValue = new TestDataGenerator().getYamlData(moduleName, apiName, testDataFileName,
						"BiometricsFormat" + "_" + testDataProperty);

			else if ((key.toString().equalsIgnoreCase("IDSchemaVersion"))
					|| (key.toString().equalsIgnoreCase("version"))
					|| (key.toString().split(Pattern.quote("."))[(key.toString().split(Pattern.quote(".")).length - 1)]
							.equalsIgnoreCase("version")))
				testDataValue = new TestDataGenerator().getYamlData(moduleName, apiName, testDataFileName,
						"version" + "_" + testDataProperty);

			else if ((key.toString().contains("0.language")))
				testDataValue = new TestDataGenerator().getYamlData(moduleName, apiName, testDataFileName,
						"language-1" + "_" + testDataProperty);

			else if ((key.toString().contains("1.language")))
				testDataValue = new TestDataGenerator().getYamlData(moduleName, apiName, testDataFileName,
						"language-2" + "_" + testDataProperty);

			else if (key.toString().equalsIgnoreCase("documents.0.value"))
				testDataValue = new TestDataGenerator().getYamlData(moduleName, apiName, testDataFileName,
						"individualBiometrics-value" + "_" + testDataProperty);

			else if (key.toString().equalsIgnoreCase("documents.1.value")
					|| key.toString().equalsIgnoreCase("documents.2.value"))
				testDataValue = new TestDataGenerator().getYamlData(moduleName, apiName, testDataFileName,
						"encoded-value" + "_" + testDataProperty);

			else if (key.toString().contains(".value") || key.toString().contains(".type")
					|| key.toString().contains(".format"))
				testDataValue = new TestDataGenerator().getYamlData(moduleName, apiName, testDataFileName,
						"stringType" + "_" + testDataProperty);

			else
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
		 * sending request to the post method
		 */

		logger.info("Input Json:" + inputJson.toString());
		response = applicationLibrary.postRequest(inputJson.toString(), service_base_URI + uin.get("uin"));
		switch (testcaseName.split("_")[1]) {
		case "bio":
			PropertyUtils.setProperty(inputJson, "request.(documents)[1].value", "$REMOVE$");
			PropertyUtils.setProperty(inputJson, "request.(documents)[1].category", "$REMOVE$");
			PropertyUtils.setProperty(inputJson, "request.(documents)[2].value", "$REMOVE$");
			PropertyUtils.setProperty(inputJson, "request.(documents)[2].category", "$REMOVE$");
			PropertyUtils.setProperty(inputJson, "request.(documents)[0].value","");
			break;
		case "demo":
			PropertyUtils.setProperty(inputJson, "request.(documents)[0].value", "$REMOVE$");
			PropertyUtils.setProperty(inputJson, "request.(documents)[0].category", "$REMOVE$");
			break;
		case "all":
			PropertyUtils.setProperty(inputJson, "request.(documents)[0].value","");
		default:
			break;
		}

		/**
		 * remove the elements bases on testcase bio - remove demographic documents demo
		 * - remove biometric documents all - keep all the data
		 */
		String responseAfterKeyRemovel = new Precondtion().removeObject(new org.json.JSONObject(inputJson.toString()));

		logger.info("Json After Clean Up:" + responseAfterKeyRemovel);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode requestJsonToCompare = mapper.readTree(responseAfterKeyRemovel.toString());
		HashMap<String, String> inputParameters = new HashMap<>();
		inputParameters.put("type", testcaseName.split("_")[1].toLowerCase());
		/**
		 * calling get request
		 */
		responseObject = (JSONObject) new JSONParser().parse(applicationLibrary
				.getRequestAsQueryParam(service_base_URI + uin.get("uin"), inputParameters).asString());
		
		if(testcaseName.split("_")[1].equals("bio") || testcaseName.split("_")[1].equals("all"))
			PropertyUtils.setProperty(responseObject, "response.(documents)[0].value","");

		logger.info("Output of get Request" + responseObject.toString());
		ArrayList<String> listOfElementToRemove = new ArrayList<String>();

		JsonNode responseJsonToCompare = mapper.readTree(responseObject.toString());
		/**
		 * asserting post request "request body" and get request "response body"
		 */
		logger.info("Request object to compare:" + requestJsonToCompare.get("request"));
		logger.info("Response object to compare:" + responseJsonToCompare.get("response").toString());
		
		/**
		 * comparing input json for post request and json response from get request
		 */
		status = assertions.assertIdRepo(requestJsonToCompare.get("request"), responseJsonToCompare.get("response").toString(),
				listOfElementToRemove);

		/**
		 * add parameters to remove in response before comparison like time stamp
		 */

		int statusCode = response.statusCode();
		logger.info("Status Code is : " + statusCode);

		if (status)
			finalStatus = "Pass";
		else
			finalStatus = "Fail";

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
		String configPath = "./src/test/resources/" + moduleName + "/" + apiName + "/" + outputJsonName + ".json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputJsonName + ".json file.......................!!");
		}
	}
}
