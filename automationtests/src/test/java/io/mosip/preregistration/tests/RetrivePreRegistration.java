package io.mosip.preregistration.tests;

/**
 * @author Ashish
 */
import java.io.File;
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
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
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
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.service.AssertPreReg;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.GetHeader;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

public class RetrivePreRegistration extends BaseTestCase implements ITest {

	public String preId = "";
	public SoftAssert softAssert = new SoftAssert();
	protected static String testCaseName = "";
	private Logger logger = Logger.getLogger(RetrivePreRegistration.class);
	boolean status = false;
	public String finalStatus = "";
	public JSONArray arr = new JSONArray();
	public ObjectMapper mapper = new ObjectMapper();
	public Response Actualresponse = null;
	public JSONObject Expectedresponse = null;
	ApplicationLibrary appLib =new ApplicationLibrary();
	private String preReg_URI;
	public String dest = "";
	public String configPaths="";
	public String folderPath = "preReg/Retrive_PreRegistration";
	public String outputFile = "Retrive_PreRegistrationOutput.json";
	public String requestKeyFile = "Retrive_PreRegistrationRequest.json";
	private CommonLibrary commonLibrary = new CommonLibrary();
	public static PreRegistrationLibrary lib=new PreRegistrationLibrary();
	public RetrivePreRegistration() {
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_DataSyncnURI");
		
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

	@DataProvider(name = "Retrive_PreRegistration")
	public Object[][] readData(ITestContext context) throws JsonParseException, JsonMappingException, IOException, ParseException {
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
	@Test(dataProvider = "Retrive_PreRegistration")
	public void retrivePreRegistrationData(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		if (testCaseName.toLowerCase().contains("smoke")) {
			testSuite = "Create_PreRegistration/createPreRegistration_smoke";
			JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
			String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
			Response documentResponse = lib.documentUpload(createResponse,individualToken);
			Response avilibityResponse = lib.FetchCentre(individualToken);
			lib.BookAppointment(documentResponse, avilibityResponse, preID,individualToken);
			Response retrivePreRegistrationDataresponse = lib.retrivePreRegistrationData(preID);
			status = lib.validateRetrivePreRegistrationData(retrivePreRegistrationDataresponse,preID , createResponse);
			System.err.println(status);
		} else {
			try {
				Actualresponse = appLib.getWithPathParam(preReg_URI, actualRequest,regClientToken);

			} catch (Exception e) {
				logger.info(e);
			}
			outerKeys.add("responsetime");
			innerKeys.add("zip-bytes");
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
		String configPath =  "src/test/resources/preReg/Retrive_PreRegistration/Retrive_PreRegistrationOutput.json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info(
					"Successfully updated Results to Retrive_PreRegistrationOutput.json file.......................!!");

		}
		//CommonLibrary.backUpFiles(configPaths, dest);
	}

	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_DataSync_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
	}

	@BeforeMethod(alwaysRun=true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		if(!lib.isValidToken(individualToken))
		{
			individualToken=lib.getToken();
		}
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}
