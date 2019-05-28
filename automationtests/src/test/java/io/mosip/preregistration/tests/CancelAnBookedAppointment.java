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

	private static Logger logger = Logger.getLogger(CancelAnBookedAppointment.class);
	static String testCaseName = "";
	String preId = "";
	SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	String dest = "";
	String folderPath = "preReg/CancelAnBookedAppointment";
	String outputFile = "CancelAnBookedOutput.json";
	String requestKeyFile = "CancelAnBookedAppointmentRequest.json";
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	CommonLibrary commonLibrary = new CommonLibrary();
	static String preReg_URI;
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	Object[][] readFolder = null;
	String testParam = null;

	/* implement,IInvokedMethodListener */
	public CancelAnBookedAppointment() {

	}

	/*
	 * Given Cancel Booking Appointment valid data when User Send PUT request to
	 * https://mosip.io/preregistration/v1/appointment/:preRegistrationId Then
	 * the appointment details for the specified pre-registration id, if
	 * appointment data exists update the availability for the slot by
	 * increasing the value and delete the record from the table and update the
	 * demographic record status "Pending_Appointment".
	 * 
	 * Given Invalid request when when User Send PUT request to
	 * https://mosip.io/preregistration/v1/appointment/:preRegistrationId Then
	 * the user should get Error response along with Error Code and Error
	 * messages as per Specification
	 * 
	 */

	@DataProvider(name = "CancelAnBookedAppointment")
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

	@Test(dataProvider = "CancelAnBookedAppointment")
	public void cancelAnBookedAppointment(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		String statusCode = null;
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		// Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg();
		preId = createApplicationResponse.jsonPath().get("response.preRegistrationId").toString();

		
			Response fetchCenter = null;

			/* Fetch availability[or]center details */

			fetchCenter = preRegLib.FetchCentre();
			/* Book An Appointment for the available data */
			Response bookAppointmentResponse = preRegLib.BookAppointment(fetchCenter, preId.toString());

			if (testCaseName.contains("smoke")) {
			
			/* Cancel an Re-booked Appointment */
			if (testCaseName.contains("CancelAnReBookedAppointment")) {
				fetchCenter = preRegLib.FetchCentre();
				Response rebookAppointmentRes = preRegLib.BookAppointment(fetchCenter, preId.toString());
			}

			/* Cancel Booked Appointment Details */
			Response CancelBookingApp = preRegLib.CancelBookingAppointment(preId);
			logger.info("CancelBookingApp::" + CancelBookingApp.asString());
			List<? extends Object> val = preRegLib.preregFetchPreregDetails(preId);
			logger.info("Vall" + val);
			Object[] TestData = null;

			for (Object obj : val) {
				TestData = (Object[]) obj;
				statusCode = TestData[1].toString();

			}
			logger.info("Status code:" + statusCode);
			// outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			innerKeys.add("transactionId");
			preRegLib.compareValues(statusCode, "Pending_Appointment");
			// Asserting actual and expected response
			status = AssertResponses.assertResponses(CancelBookingApp, Expectedresponse, outerKeys, innerKeys);

		} else {

			String preRegURI;

			if (testCaseName.contains("CancelBookingAppointmentByPassingInvalidStatusCode")) {

				String staCode = actualRequest.get("statusCode").toString();
				preRegLib.updateStatusCode(staCode, preId);

			} else {
				preId = actualRequest.get("preRegistrationId").toString();
			}
            logger.info("Pre Reg URI::"+preReg_URI);
			preRegURI = preReg_URI + preId;
			Actualresponse = applicationLibrary.putRequest_WithoutBody(preRegURI);
            logger.info("Test Case name:"+testCaseName+"Actual res:"+Actualresponse.asString());
			// outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			innerKeys.add("transactionId");
			// Asserting actual and expected response
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
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];

		testCaseName = object.get("testCaseName").toString();
		// Cancel Appointment Resource URI
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_CancelAppointmentURI");
		// Fetch the generated Authorization Token by using following Kernel
		// AuthManager APIs
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
			//f.set(baseTestMethod, CancelAnBookedAppointment.testCaseName);
			f.set(baseTestMethod, "Pre Reg_CancelAnBookedAppointment_" +CancelAnBookedAppointment.testCaseName);
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
