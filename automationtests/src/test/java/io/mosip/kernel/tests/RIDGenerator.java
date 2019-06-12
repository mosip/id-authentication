package io.mosip.kernel.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.google.common.base.Verify;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;


/**
 * @author Ravi Kant
 *
 */
public class RIDGenerator extends BaseTestCase implements ITest{
	RIDGenerator() {
		super();
	}

	private static Logger logger = Logger.getLogger(RIDGenerator.class);
	private final String moduleName = "kernel";
	private final String apiName = "RIDGenerator";
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String RIDGenerator_URI = props.get("RIDGenerator_URI").toString();
	private final int ridGenerationCount = 5;

	protected String testCaseName = "";
	SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	String finalStatus = "";
	public JSONArray arr = new JSONArray();
	Response response = null;
	JSONObject responseObject = null;
	private AssertKernel assertions = new AssertKernel();
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie=null;


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
	public Object[][] readData(ITestContext context){
		return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
		}

		/**
		 * This fetch the value of the data provider and run for each test case
		 * 
		 * @param fileName
		 * @param object
		 * @throws ParseException 
		 * @throws NumberFormatException 
		 * 
		 */
		@SuppressWarnings("unchecked")
		@Test(dataProvider = "fetchData", alwaysRun = true)
		public void ridGenerator(String testcaseName) throws NumberFormatException, ParseException{
			logger.info("Test Case Name:" + testcaseName);

			// getting request and expected response jsondata from json files.
			JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);

			JSONObject objectData = objectDataArray[0];
			responseObject = objectDataArray[1];
					response = applicationLibrary.getWithPathParam(RIDGenerator_URI, objectData,cookie);

		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(response);
		if (testcaseName.toLowerCase().contains("smoke")) {

			String rid = ((JSONObject)((JSONObject) new JSONParser().parse(response.asString())).get("response")).get("rid").toString();
			String timeStampWithHour = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()).replace(".", "").substring(0, 8);
			
			boolean lengthValid = rid.length()==29;
			boolean centidValid = rid.substring(0, 5).equals(objectData.get("centerid"));
			boolean machidValid = rid.substring(5, 10).equals(objectData.get("machineid"));
			boolean ridTimestampvalid = timeStampWithHour.equals(rid.substring(15,23));
			boolean alphabetValid = rid.substring(10,15).matches("[0-9]+");
			boolean sequenceValid = true;
			
			int intRidPre = Integer.parseInt(rid.substring(10,15));
			for(int i =0; i<ridGenerationCount; i++)
			{

				response = applicationLibrary.getWithPathParam(RIDGenerator_URI, objectData,cookie);

				int intRidPost = Integer.parseInt(((JSONObject)((JSONObject) new JSONParser().parse(response.asString())).get("response")).get("rid").toString().substring(10, 15));
				if(intRidPost-intRidPre!=1)
				{
					sequenceValid = false;
					break;
				}
				intRidPre = intRidPost;
			}
			softAssert.assertTrue(lengthValid, "inValid Length of generated RID");
			softAssert.assertTrue(centidValid, "generated RID does not contains passed centerID");
			softAssert.assertTrue(machidValid, "generated RID does not contains passed machineID");
			softAssert.assertTrue(ridTimestampvalid, "generated RID does not contains valid timeStamp");
			softAssert.assertTrue(alphabetValid, "generated RID contains Alphabets in sequence");
			softAssert.assertTrue(sequenceValid, "generated RID does not have incremental sequence");
			
			status = lengthValid && centidValid && machidValid && ridTimestampvalid && alphabetValid && sequenceValid;
		}

		else {
			// add parameters to remove in response before comparison like time stamp
			ArrayList<String> listOfElementToRemove = new ArrayList<String>();
			listOfElementToRemove.add("responsetime");
			status = assertions.assertKernel(response, responseObject, listOfElementToRemove);
		}

		if (!status) {
			logger.debug(response);
		}
		Verify.verify(status);
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

}
