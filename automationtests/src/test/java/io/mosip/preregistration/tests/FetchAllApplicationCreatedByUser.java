
package io.mosip.preregistration.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.GetHeader;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Fetch all the applications created by user related
 * Positive and Negative test cases
 * 
 * @author Ashish Rastogi
 * @since 1.0.0
 */

public class FetchAllApplicationCreatedByUser extends BaseTestCase implements ITest {

	static String preId = "";
	static SoftAssert softAssert = new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(FetchAllApplicationCreatedByUser.class);
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static CommonLibrary commonLibrary = new CommonLibrary();
	private static String preReg_URI;
	static String dest = "";
	static String configPaths = "";
	static String folderPath = "preReg/Fetch_all_application_created_by_user";
	static String outputFile = "Fetch_all_application_created_by_userOutput.json";
	static String requestKeyFile = "Fetch_all_application_created_by_userRequest.json";
	String testParam = null;

	FetchAllApplicationCreatedByUser() {
		super();
	}

	static PreRegistrationLibrary preLab = new PreRegistrationLibrary();
	/*
	 * Data Prividers to read the input json files from the folders
	 */

	@DataProvider(name = "Fetch_all_application_created_by_user")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		testParam = context.getCurrentXmlTest().getParameter("testType");
		switch ("smokeAndRegression") {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");

		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	@Test(dataProvider = "Fetch_all_application_created_by_user")
	public void fetchAllAplicationCreatedByUser(String testSuite, Integer i, JSONObject object) throws Exception {
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		if (testCaseName.toLowerCase().contains("smoke")) {
			actualRequest = preLab.createRequest("Create_PreRegistration/createPreRegistration_smoke");
			Response createResponse = preLab.CreatePreReg(actualRequest);
			String createdBy = createResponse.jsonPath().get("response[0].createdBy").toString();
			Actualresponse = preLab.fetchAllPreRegistrationCreatedByUser(createdBy);
		} else {

			Actualresponse = applicationLibrary.getRequest(preReg_URI, GetHeader.getHeader(actualRequest));
		}
		outerKeys.add("responsetime");
		outerKeys.add("timestamp");
		innerKeys.add("createdDatTime");
		innerKeys.add("preRegistrationId");
		status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
		if (status) {
			finalStatus = "Pass";
			softAssert.assertAll();
			object.put("status", finalStatus);
			arr.add(object);
		} else {
			finalStatus = "Fail";
		}
		boolean setFinalStatus = false;
		if (finalStatus.equals("Fail"))
			setFinalStatus = false;
		else if (finalStatus.equals("Pass"))
			setFinalStatus = true;
		Verify.verify(setFinalStatus);
		softAssert.assertAll();
	}

	/**
	 * Writing OutPut to the config path
	 * 
	 * @throws IOException
	 */
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath = "src/test/resources/preReg/Fetch_all_application_created_by_user/Fetch_all_application_created_by_userOutput.json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info(
					"Successfully updated Results to Fetch_all_application_created_by_userOutput.json file.......................!!");

		}

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
			f.set(baseTestMethod, FetchAllApplicationCreatedByUser.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_FetchAllApplicationCreatedByUserURI");

		/*
		 * Fetch all the applications created by user Resource URI
		 */

	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}
