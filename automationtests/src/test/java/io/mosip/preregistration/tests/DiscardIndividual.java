package io.mosip.preregistration.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.postgresql.ssl.jdbc4.LibPQFactory;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.dbHealthcheck.DBHealthCheck;
import io.mosip.dbaccess.PreRegDbread;
import io.mosip.registrationProcessor.tests.IntegrationScenarios;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertPreReg;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.GetHeader;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

public class DiscardIndividual extends BaseTestCase implements ITest {

	static String preId = "";
	static SoftAssert softAssert = new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(DiscardIndividual.class);
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static String preReg_URI;
	private static CommonLibrary commonLibrary = new CommonLibrary();
	static String dest = "";
	static String configPaths = "";
	static String folderPath = "preReg/Discard_Individual";
	static String outputFile = "Discard_IndividualOutput.json";
	static String requestKeyFile = "Discard_IndividualRequest.json";
	static PreRegistrationLibrary lib = new PreRegistrationLibrary();

	public DiscardIndividual() {
		super();
	}

	/**
	 * Data Providers to read the input json files from the folders
	 * 
	 * @param context
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */

	IntegrationScenarios ins = new IntegrationScenarios();
	static PreRegistrationLibrary prl = new PreRegistrationLibrary();

	@DataProvider(name = "Discard_Individual")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		switch (testLevel) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");

		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}

	}

	/**
	 * Script for testing the Retrive_PreRegistration api
	 * 
	 * @param testSuite
	 * @param i
	 * @param object
	 * @throws Exception
	 */
	@Test(dataProvider = "Discard_Individual")
	public void discardIndividual(String testSuite, Integer i, JSONObject object) throws Exception {
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		if (testCaseName.toLowerCase().contains("smoke")) {
			testSuite = "Create_PreRegistration/createPreRegistration_smoke";
			JSONObject createPregRequest = prl.createRequest(testSuite);
			Response createPregResponse = prl.CreatePreReg(createPregRequest);
			String preReg_Id = createPregResponse.jsonPath().get("response.preRegistrationId").toString();
			Actualresponse = prl.discardApplication(preReg_Id);
			preId = Actualresponse.jsonPath().get("response.preRegistrationId").toString();
			Response getPreRegistrationDataResponse = prl.getPreRegistrationData(preReg_Id);
			String message = getPreRegistrationDataResponse.jsonPath().get("errors[0].message").toString();
			prl.compareValues(message, "No data found for the requested pre-registration id");
			Assert.assertEquals(preId, preReg_Id);
			status = true;
		} else {
			outerKeys.add("responsetime");
			Actualresponse = applicationLibrary.deleteRequestWithParm(preReg_URI, actualRequest);
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
		}
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
	 * Writing response to the specified config path
	 * 
	 * @throws IOException
	 */
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath = "src/test/resources/preReg/Retrive_PreRegistration/Retrive_PreRegistrationOutput.json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info(
					"Successfully updated Results to Retrive_PreRegistrationOutput.json file.......................!!");

		}
		lib.logOut();
	}
	@BeforeClass
	public void getToken()
	{
		authToken = lib.getToken();
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
			f.set(baseTestMethod, "Pre Reg_Demographic_" + DiscardIndividual.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
		
	}

	@BeforeMethod(alwaysRun = true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_DiscardApplnURI");
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}