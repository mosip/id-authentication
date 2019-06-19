package io.mosip.preregistration.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class SyncAvailabilityBatchJob extends BaseTestCase implements ITest {
	public Logger logger = Logger.getLogger(BatchJob.class);
	public PreRegistrationLibrary lib = new PreRegistrationLibrary();
	public String testSuite;
	public String preRegID = null;
	public String createdBy = null;
	public Response response = null;
	public String preID = null;
	protected static String testCaseName = "";
	public String folder = "preReg";
	public ApplicationLibrary applnLib = new ApplicationLibrary();
	public PreregistrationDAO dao=new PreregistrationDAO();
	
	
	@Transactional(rollbackOn=Exception.class)
	@Test
	public void makeRegistartionCenterInactive() {
		String syncAvailability = null;
		dao.makeregistartionCenterActive("10001");
		lib.syncAvailability();
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre("10001");
		Response bookingResponse = lib.BookAppointment(documentResponse, avilibityResponse, preID);
		lib.compareValues(bookingResponse.jsonPath().get("response.bookingMessage").toString(),
				"Appointment booked successfully");
		dao.makeregistartionCenterDeActive("10001");
		Response syncAvailabilityResponse = lib.syncAvailability();
		try {
			syncAvailability = syncAvailabilityResponse.jsonPath().get("response").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception while running syncAvailibility");
		}

		lib.compareValues(syncAvailability, "MASTER_DATA_SYNCED_SUCCESSFULLY");
		Response fetchCenterResponse = lib.FetchCentre("10001");
		try {
			lib.compareValues(fetchCenterResponse.jsonPath().get("errors[0].message").toString(),
					"No available slots found for specified registration center");
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "error while fetching availibility data for booking");
		}

		Response appointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		try {
			lib.compareValues(appointmentDetailsResponse.jsonPath().get("errors[0].message").toString(),
					"Booking data not found");
			Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(preID);
			lib.compareValues(getPreRegistrationStatusResponse.jsonPath().get("response.statusCode").toString(),
					"Pending_Appointment");
			dao.makeregistartionCenterActive("10001");
			syncAvailabilityResponse = lib.syncAvailability();
			lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),
					"MASTER_DATA_SYNCED_SUCCESSFULLY");
			lib.FetchCentre("10001");

		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception occured while sync availibility");
		}
	}
	@Test
	public void makeRegCntrInactiveAndCheckAppointmentGettingCanceled() {
		try {
			dao.makeregistartionCenterActive("10001");
			lib.syncAvailability();
			testSuite = "Create_PreRegistration/createPreRegistration_smoke";
			JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createResponse = lib.CreatePreReg(createPregRequest);
			String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
			Response documentResponse = lib.documentUpload(createResponse);
			Response avilibityResponse = lib.FetchCentre("10001");
			Response bookingResponse = lib.BookAppointment(documentResponse, avilibityResponse, preID);
			lib.compareValues(bookingResponse.jsonPath().get("response.bookingMessage").toString(),"Appointment booked successfully");
			dao.makeregistartionCenterDeActive("10001");
			Response syncAvailabilityResponse = lib.syncAvailability();
			lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
			Response appointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
			lib.compareValues(appointmentDetailsResponse.jsonPath().get("errors[0].message").toString(), "Booking data not found");
			Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(preID);
			lib.compareValues(getPreRegistrationStatusResponse.jsonPath().get("response.statusCode").toString(), "Pending_Appointment");
			dao.makeregistartionCenterActive("10001");
			syncAvailabilityResponse = lib.syncAvailability();
			lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception while running sync master data");
		}
		
	}
	@Test
	public void makeAdayAsHoliday() {
		try {
			testSuite = "Create_PreRegistration/createPreRegistration_smoke";
			JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createResponse = lib.CreatePreReg(createPregRequest);
			String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
			Response documentResponse = lib.documentUpload(createResponse);
			Response avilibityResponse = lib.FetchCentre("10001");
			Response bookingResponse = lib.BookAppointment(documentResponse, avilibityResponse, preID);
			lib.compareValues(bookingResponse.jsonPath().get("response.bookingMessage").toString(),"Appointment booked successfully");
			Date date = dao.MakeDayAsHoliday();
			Response syncAvailabilityResponse = lib.syncAvailability();
			lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
			Response fetchAppointmentDetailsresponse = lib.FetchAppointmentDetails(preID);
			dao.updateHoliday(date);
			syncAvailabilityResponse = lib.syncAvailability();
			lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
			
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception while running sync master data for holiday");
		}
	}
	/*@Test
	public void changeHolidayToNormalDay() {
		Date date = dao.MakeDayAsHoliday();
		Response syncAvailabilityResponse = lib.syncAvailability();
		lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
		dao.updateHoliday(date);
		syncAvailabilityResponse = lib.syncAvailability();
		lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
		Response avilibityResponse = lib.FetchCentre("10001");
	}
*/
	@Override
	public String getTestName() {
		return this.testCaseName;

	}
	@BeforeMethod(alwaysRun=true)
	public void login( Method method)
	{
		authToken=lib.getToken();
		testCaseName="preReg_syncAvaibility_BatchJob_" + method.getName();
	}


	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_syncAvaibility_BatchJob_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
	}
}
