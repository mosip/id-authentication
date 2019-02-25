/*package io.mosip.kernel.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Properties;
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
import com.google.common.base.Verify;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.mosip.dbDTO.MachineSpecificationDto;
import io.mosip.dbaccess.KernelMasterDataR;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.util.GenerateJsonPath;
import io.mosip.util.PropertyFileLoader;
import io.mosip.util.TestCaseReader;
import io.mosip.util.TestDataGenerator;
import io.restassured.response.Response;

*//**
 * @author Arjun chandramohan
 *
 *//*
public class CreateMachineSpecification extends BaseTestCase implements ITest {
	CreateMachineSpecification() {
		super();
	}

	private static Logger logger = Logger.getLogger(CreateMachineSpecification.class);
	private static final String jiraID = "MOS-551";
	private static final String moduleName = "kernel";
	private static final String apiName = "CreateMachineSpecification";
	private static final String requestJsonStructure = "CreateMachineSpecificationRequest";
	private static final String outputJsonName = "CreateMachineSpecificationOutput";
	private static final String service_URI = "/masterdata/v1.0/machinespecifications";
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

	*//**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 *//*
	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();
		new GenerateJsonPath().generatePath(moduleName, apiName, requestJsonStructure);
		prop = new PropertyFileLoader().readPropertyFile(moduleName, apiName, requestJsonStructure);

	}

	*//**
	 * This data provider will return a test case name
	 * 
	 * @param context
	 * @return test case name as object
	 *//*
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

	*//**
	 * This fetch the value of the data provider and run for each test case
	 * 
	 * @param fileName
	 * @param object
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * 
	 *//*
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "FetchData", alwaysRun = true)
	public void validatingTestCases(String testcaseName, JSONObject object)
			throws JsonParseException, JsonMappingException, IOException, ParseException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		logger.info("Test Case Name:" + testcaseName);
		object.put("Test case Name", testcaseName);
		object.put("Jira ID", jiraID);

		JSONObject requestJsonCondition = new TestCaseReader().readRequestJsonCondition(moduleName, apiName,
				testcaseName, "Request");
		Object requestJsonStruct = new TestCaseReader().readRequestJson(moduleName, apiName, requestJsonStructure);

		for (Object key : requestJsonCondition.keySet()) {
			object.put(key.toString(), requestJsonCondition.get(key.toString()));

			if (requestJsonCondition.get(key.toString()).toString().contains("empty"))
				PropertyUtils.setProperty(requestJsonStruct, prop.get(key.toString()).toString(), "");
			else {
				String testDataValue = new TestDataGenerator().getYamlData(moduleName, apiName, testDataFileName,
						key.toString() + "_" + requestJsonCondition.get(key.toString())).toString();
				if (testDataValue.contains("BOOLEAN"))
					PropertyUtils.setProperty(requestJsonStruct, prop.get(key.toString()).toString(),
							Boolean.parseBoolean(testDataValue.split(":")[1]));
				else
					PropertyUtils.setProperty(requestJsonStruct, prop.get(key.toString()).toString(), testDataValue);
			}
		}
		response = applicationLibrary.postRequest(requestJsonStruct.toString(), service_URI);
		String configPath = "src/test/resources/" + moduleName + "/" + apiName + "/" + testcaseName;
		File folder = new File(configPath);
		File[] listofFiles = folder.listFiles();
		logger.info("Json Request Is : " + requestJsonStruct.toString());

		for (int k = 0; k < listofFiles.length; k++) {
			if (listofFiles[k].getName().toLowerCase().contains("response"))
				responseObject = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
		}

		logger.info("Actual Response:" + response.asString());
		logger.info("Expected Response:" + responseObject.toJSONString());

		// add parameters to remove in response before comparison like time stamp
		ArrayList<String> listOfElementToRemove = new ArrayList<String>();
		listOfElementToRemove.add("timestamp");
		if(!testcaseName.toLowerCase().contains("smoke"))
		status = assertions.assertKernel(response, responseObject, listOfElementToRemove);

		int statusCode = response.statusCode();
		logger.info("Status Code is : " + statusCode);

		if (testcaseName.toLowerCase().contains("smoke")) {
			String id = (response.jsonPath().get("id")).toString();
			logger.info("id is : " + id);
			String queryStr = "SELECT * FROM master.machine_spec WHERE id='" + id + "'";
			boolean valid = KernelMasterDataR.validateDB(queryStr, MachineSpecificationDto.class);
			if (valid) {
				status = true;
			} else {
				status = false;
			}

		}
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
			f.set(baseTestMethod, CreateMachineSpecification.testCaseName);
			f.set(baseTestMethod, testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	*//**
	 * this method write the output to corressponding json
	 *//*
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath = "./src/test/resources/" + moduleName + "/" + apiName + "/" + outputJsonName + ".json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputJsonName + ".json file.......................!!");
		}
	}
}
*/