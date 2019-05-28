package io.mosip.preregistration.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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

import io.mosip.dbaccess.PreRegDbread;
import io.mosip.preregistration.util.PreRegistrationUtil;
import io.mosip.preregistration.util.SyncMasterDataUtil;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Sync Master Data For PreRegId related Positive and
 * Negative test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class SyncMasterData extends BaseTestCase implements ITest {

	/**
	 * Declaration of all variables
	 **/
	String preId = "";
	String docId = "";
	SoftAssert softAssert = new SoftAssert();
	static String testCaseName = "";
	Logger logger = Logger.getLogger(SyncMasterData.class);
	boolean status = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	CommonLibrary commonLibrary = new CommonLibrary();
	String preReg_URI;
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	HashMap<String, String> parm = new HashMap<>();
	String dest = "";
	String folderPath = "preReg/SyncMasterData";
	String outputFile = "SyncMasterDataOutput.json";
	String requestKeyFile = "SyncMasterDataRequest.json";
    PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
    PreRegistrationUtil preRegUtil=new PreRegistrationUtil();
    SyncMasterDataUtil syncUtil=new SyncMasterDataUtil();
	// implement,IInvokedMethodListener
	public SyncMasterData() {

	}

	/**
	 * Data Providers to read the input json files from the folders
	 * 
	 * @param context
	 * @return input request file
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	@DataProvider(name = "SyncMasterData")
	public Object[][] readData(ITestContext context) throws Exception {
		switch (testLevel) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	/*
	 * Given Document Upload valid request when I Send GET request to
	 * https://mosip.io/preregistration/v1/appointment/availability/sync
	 * Then I should get success
	 * response with elements defined as per specifications Given Invalid
	 * request when I send GET request to
	 * https://mosip.io/preregistration/v1/appointment/availability/sync
	 * Then I should get Error
	 * response along with Error Code and Error messages as per Specification
	 */
	@Test(dataProvider = "SyncMasterData")
	public void syncMasterData(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);

		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		Response syncMsterData = syncUtil.syncMasterData();
		logger.info("Sync Master data Res:" + syncMsterData.asString());

		//Removing the dynamic value from Sync Master data response
		outerKeys.add("responsetime");
		innerKeys.add("documentId");
		innerKeys.add("multipartFile");
		//Asserting actual and expected response
		status = AssertResponses.assertResponses(syncMsterData, Expectedresponse, outerKeys, innerKeys);

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
	  * This method is used for fetching test case name
	  * @param method
	  * @param testdata
	  * @param ctx
	  */
	@BeforeMethod(alwaysRun = true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];

		testCaseName = object.get("testCaseName").toString();
		/**
		 * Get All Document by Document Id Resource URI
		 */

		preReg_URI = preRegUtil.fetchPreregProp().get("preReg_SyncMasterDataURI");
		authToken = preRegLib.preRegAdminToken();
	}

	/**
	 * This method is used for generating report
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			//f.set(baseTestMethod, SyncMasterData.testCaseName);
			f.set(baseTestMethod, "Pre Reg_SyncMasterData_" +SyncMasterData.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * This method is used for generating output file with the test case result
	 */
	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		String configPath = System.getProperty("user.dir") + "/src/test/resources/" + folderPath + "/" + outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}
		String source = "src/test/resources/" + folderPath + "/";

		// Add generated PreRegistrationId to list to be Deleted from DB AfterSuite
		// preIds.add(preId);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}
