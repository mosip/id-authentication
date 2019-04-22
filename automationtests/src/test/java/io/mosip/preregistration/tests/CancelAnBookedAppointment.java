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
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Cancel An Booked Appointment related Positive and
 * Negative test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class CancelAnBookedAppointment extends BaseTestCase implements ITest {
	/**
	 * Declaration of all variables
	 **/

	static String preId = "";
	static SoftAssert softAssert = new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(CancelAnBookedAppointment.class);
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	static String dest = "";
	static String folderPath = "preReg/CancelAnBookedAppointment";
	static String outputFile = "CancelAnBookedOutput.json";
	static String requestKeyFile = "CancelAnBookedAppointmentRequest.json";
	static PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	private static CommonLibrary commonLibrary = new CommonLibrary();
	private static String preReg_URI;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();

	/* implement,IInvokedMethodListener */
	public CancelAnBookedAppointment() {

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

	@DataProvider(name = "CancelAnBookedAppointment")
	public static Object[][] readData(ITestContext context) throws Exception {

		String testParam = context.getCurrentXmlTest().getParameter("testType");
		switch ("regression") {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	@Test(dataProvider = "CancelAnBookedAppointment")
	public void cancelAnBookedAppointment(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		String statusCode = null;
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		// Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg();
		preId = createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();

		if (testCaseName.contains("smoke")) {
			Response fetchCenter = null;
				
				/* Fetch availability[or]center details */
				
				fetchCenter=preRegLib.FetchCentre();
				/* Book An Appointment for the available data */
				Response bookAppointmentResponse = preRegLib.BookAppointment(fetchCenter, preId.toString());
				
				
				if(testCaseName.contains("CancelAnReBookedAppointment"))
				{
					fetchCenter=preRegLib.FetchCentre();
					Response rebookAppointmentRes = preRegLib.BookAppointment(fetchCenter, preId.toString());
			    }
				
			// Cancel Booked Appointment Details
			Response CancelBookingApp = preRegLib.CancelBookingAppointment(preId);

			
			List<? extends Object> val = preRegLib.preregFetchPreregDetails(preId);
			
			Object[] TestData = null;
			
	
			for (Object obj : val) {
				TestData = (Object[]) obj;
				statusCode=TestData[1].toString();
				
				
			}
			
			System.out.println("Cancel Book App:"+CancelBookingApp.asString());
		
			// removing the keys for assertion 
			outerKeys.add("responsetime");
			innerKeys.add("transactionId");
			preRegLib.compareValues(statusCode, "Pending_Appointment");
			status = AssertResponses.assertResponses(CancelBookingApp, Expectedresponse, outerKeys, innerKeys);

		} else {
			
			String preRegURI;
			
			if(testCaseName.contains("CancelBookingAppointmentByPassingInvalidStatusCode"))
			{	
			
			  String staCode=actualRequest.get("statusCode").toString();
			  preRegLib.updateStatusCode(staCode, preId);
			  
			}
			else {
				preId = actualRequest.get("preRegistrationId").toString();
			}
			
		    preRegURI = preReg_URI + preId;
			Actualresponse = applicationLibrary.putRequest_WithoutBody(preRegURI);
			System.out.println("Cancel Book App:"+Actualresponse.asString());
			outerKeys.add("responsetime");
			innerKeys.add("transactionId");
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
	 * Declaring the Cancel Appointment Appointment Resource URI and getting the
	 * test case name
	 * 
	 * @param result
	 */

	@BeforeMethod(alwaysRun = true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];

		testCaseName = object.get("testCaseName").toString();

		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_CancelAppointmentURI");
		authToken = preRegLib.getToken();
	}

	/**
	 * Writing test case name into testng
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
			f.set(baseTestMethod, CancelAnBookedAppointment.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * Writing output into configpath
	 * 
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		String configPath = "src/test/resources/" + folderPath + "/" + outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}

		// Add generated PreRegistrationId to list to be Deleted from DB
		// AfterSuite
		preIds.add(preId);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}

