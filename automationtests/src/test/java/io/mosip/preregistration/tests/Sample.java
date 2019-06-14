package io.mosip.preregistration.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.apache.maven.plugins.assembly.io.AssemblyReadException;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.mongodb.internal.thread.DaemonThreadFactory;

import io.mosip.dbentity.OtpEntity;
import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

/**
 * @author Ashish Rastogi
 *
 */

public class Sample extends BaseTestCase implements ITest {
	Logger logger = Logger.getLogger(Sample.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	String testSuite;
	String preRegID = null;
	String createdBy = null;
	Response response = null;
	String preID = null;
	protected static String testCaseName = "";
	static String folder = "preReg";
	private static CommonLibrary commonLibrary = new CommonLibrary();
	ApplicationLibrary applnLib = new ApplicationLibrary();
	String updateSuite = "UpdateDemographicData/UpdateDemographicData_smoke";
	PreregistrationDAO dao = new PreregistrationDAO();

	SoftAssert soft = new SoftAssert();
	@BeforeClass
	public void readPropertiesFile() {
		initialize();

	}
	/**
	 * Batch job service for expired application
	 * 
	 * @throws java.text.ParseException
	 * 
	 * 
	 */
	@Test(groups = { "IntegrationScenarios" })
	public void fetchBookedAppointmentCreatedByUser() {
		dao.makeregistartionCenterActive("10009");
		lib.syncAvailability();
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre("10009");
		Response bookingResponse = lib.BookAppointment(documentResponse, avilibityResponse, preID);
		lib.compareValues(bookingResponse.jsonPath().get("response.bookingMessage").toString(),"Appointment booked successfully");
		dao.makeregistartionCenterDeActive("10009");
		Response syncAvailabilityResponse = lib.syncAvailability();
		lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
		Response fetchCenterResponse = lib.FetchCentre("10009");
		lib.compareValues(fetchCenterResponse.jsonPath().get("errors[0].message").toString(), "No available slots found for specified registration center");
		Response appointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		lib.compareValues(appointmentDetailsResponse.jsonPath().get("errors[0].message").toString(), "Booking data not found");
		Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(preID);
		lib.compareValues(getPreRegistrationStatusResponse.jsonPath().get("response.statusCode").toString(), "Pending_Appointment");
		dao.makeregistartionCenterActive("10009");
		syncAvailabilityResponse = lib.syncAvailability();
		lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
		lib.FetchCentre("10009");

	}

	@BeforeMethod(alwaysRun = true)
	public void run() {
		authToken = lib.getToken();
	}
	@Override
	public String getTestName() {
		return this.testCaseName;
	}
	@AfterMethod
	public void afterMethod(ITestResult result) {
		System.out.println("method name:" + result.getMethod().getMethodName());

	}
}