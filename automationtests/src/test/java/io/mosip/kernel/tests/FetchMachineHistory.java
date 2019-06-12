
package io.mosip.kernel.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
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

import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.kernel.util.TestCaseReader;
import io.restassured.response.Response;

/**
 * @author Ravi Kant
 *
 */
public class FetchMachineHistory extends BaseTestCase implements ITest {
	FetchMachineHistory() {
		super();
	}

	private static Logger logger = Logger.getLogger(FetchMachineHistory.class);
	private final String jiraID = "MOS-8229";
	private final String moduleName = "kernel";
	private final String apiName = "FetchMachineHistory";
	private final String requestJsonName = "FetchMachineHistoryRequest";
	private final String outputJsonName = "FetchMachineHistoryOutput";
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String FetchMachineHistory_URI = props.get("FetchMachineHistory_URI").toString();
	protected String testCaseName = "";
	SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	public JSONArray arr = new JSONArray();
	Response response = null;
	JSONObject responseObject = null;
	private AssertKernel assertions = new AssertKernel();
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	KernelAuthentication auth=new KernelAuthentication();
	String cookie=null;

	/**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName+"_"+apiName+"_"+object.toString();
		cookie=auth.getAuthForRegistrationProcessor();
	}

	/**
	 * This data provider will return a test case name
	 * 
	 * @param context
	 * @return test case name as object
	 */
	@DataProvider(name = "fetchData")
	public Object[][] readData(ITestContext context)
			throws ParseException {	
		return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel, requestJsonName);
	}

	/**
	 * This fetch the value of the data provider and run for each test case
	 * 
	 * @param fileName
	 * @param object
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "fetchData", alwaysRun = true)
	public void fetchMachineHistory(String testcaseName, JSONObject object)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		logger.info("Test Case Name:" + testcaseName);
		object.put("Jira ID", jiraID);

		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);

		JSONObject objectData = objectDataArray[0];
		responseObject = objectDataArray[1];
		
		if(testcaseName.contains("smoke") | testcaseName.contains("invalid_id_unexisting") |testcaseName.contains("invalid_langcode_unexisting in DB")) {
			// getting current timestamp and changing it to yyyy-MM-ddTHH:mm:ss.sssZ format.
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
			Calendar calender = Calendar.getInstance();
			calender.setTime(new Date());
			String time = sdf.format(calender.getTime());
			time = time.replace(' ', 'T')+"Z";
			objectData.put("effdatetimes", time);
		}
		
				response = applicationLibrary.getWithPathParam(FetchMachineHistory_URI, objectData,cookie);
				logger.info("----"+response.asString());
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(response);
		if (testcaseName.toLowerCase().contains("smoke")) {

			// fetching json object from response
			JSONObject responseJson = (JSONObject) ((JSONObject) new JSONParser().parse(response.asString())).get("response");
			if (responseJson == null || !responseJson.containsKey("machineHistoryDetails"))
				Assert.assertTrue(false, "Response does not contain machineHistoryDetails");
			String query = "select count(*) from master.machine_master_h where id = '" + objectData.get("id")
					+ "' and lang_code = '" + objectData.get("langcode") + "' and eff_dtimes <= '"
					+ objectData.get("effdatetimes").toString().split("Z")[0].replace('T', ' ') + "'";
			
			long obtainedObjectsCount = new KernelDataBaseAccess().validateDBCount(query,"masterdata");

			// fetching json array of objects from response
			JSONArray responseArrayFromGet = (JSONArray) responseJson.get("machineHistoryDetails");
			logger.info("===Dbcount===" + obtainedObjectsCount + "===Get-count===" + responseArrayFromGet.size());

			// validating number of objects obtained form db and from get request
			if (responseArrayFromGet.size() <= obtainedObjectsCount) {

				// list to validate existance of attributes in response objects
				List<String> attributesToValidateExistance = new ArrayList<String>();
				attributesToValidateExistance.add("id");
				attributesToValidateExistance.add("name");
				attributesToValidateExistance.add("macAddress");
				attributesToValidateExistance.add("ipAddress");
				attributesToValidateExistance.add("serialNum");
				attributesToValidateExistance.add("machineSpecId");
				attributesToValidateExistance.add("isActive");

				// key value of the attributes passed to fetch the data, should be same in all
				// obtained objects
				HashMap<String, String> passedAttributesToFetch = new HashMap<String, String>();
				if (objectData != null) {
						passedAttributesToFetch.put("id", objectData.get("id").toString());
						passedAttributesToFetch.put("langCode", objectData.get("langcode").toString());
					
				}

				status = AssertKernel.validator(responseArrayFromGet, attributesToValidateExistance,
						passedAttributesToFetch);
			} else
				status = false;

		}

		else {

			// add parameters to remove in response before comparison like time stamp
			ArrayList<String> listOfElementToRemove = new ArrayList<String>();
			listOfElementToRemove.add("responsetime");
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