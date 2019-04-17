/*
package io.mosip.preregistration.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;

import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

*//**
 * @author Vidya Shankar N S
 *
 *//*

public class IntegrationScenarios extends BaseTestCase {
	Logger logger = Logger.getLogger(IntegrationScenarios.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	String testSuite;
	String expectedMessageDeleteDoc = "DOCUMENT_DELETE_SUCCESSFUL";
	String docMissingMessage = "DOCUMENT_IS_MISSING";
	String unableToFetchPreReg = "UNABLE_TO_FETCH_THE_PRE_REGISTRATION";
	String appointmentCanceledMessage = "APPOINTMENT_SUCCESSFULLY_CANCELED";
	String bookingSuccessMessage = "APPOINTMENT_SUCCESSFULLY_BOOKED";
	String expectedErrMessageDocGreaterThanFileSize = "DOCUMENT_EXCEEDING_PREMITTED_SIZE";
	String expectedErrCodeDocGreaterThanFileSize = "PRG_PAM_DOC_007";
	String filepathPOA = "IntegrationScenario/DocumentUpload_POA";
	String filepathPOB = "IntegrationScenario/DocumentUpload_POB";
	String filepathPOI = "IntegrationScenario/DocumentUpload_POI";
	String filepathDocGreaterThanFileSize = "IntegrationScenario/DocumentUploadGreaterThanFileSize";
	String POADocName = "AadhaarCard_POA.pdf";
	String POBDocName = "ProofOfBirth_POB.pdf";
	String POIDocName = "LicenseCertification_POI.pdf";
	String ExceedingSizeDocName = "ProofOfAddress.pdf";
	String regCenterId;
	String preRegID = null;
	String createdBy = null;
	Response response = null;
	String preID = null;
	static String folder = "preReg";

	@BeforeTest
	public void readPropertiesFile() {
		initialize();
		// lib.intialize1();
	}

	
	 * @BeforeMethod public void intializ() { lib.intialize(); }
	 
	@Test(groups = { "IntegrationScenarios" })
	public void createAppUpdateGetPreRegData() {

		// Create PreReg
		try {
			response = lib.CreatePreReg();
			preRegID = response.jsonPath().get("response[0].preRegistrationId").toString();
			createdBy = response.jsonPath().get("response[0].createdBy").toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Get PreReg Data
		response = lib.getPreRegistrationData(preRegID);

		lib.compareValues(response.jsonPath().get("response[0].preRegistrationId").toString(), preRegID);
		// Update PreReg
		try {
			response = lib.updatePreReg(preRegID, createdBy);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Get PreReg Data
		response = lib.getPreRegistrationData(preRegID);

		lib.compareValues(response.jsonPath().get("response[0].preRegistrationId").toString(), preRegID);

	}


	@Test(groups = { "IntegrationScenarios" })
	public void createAppUploadDocDeleteDocByDocId() {

		// Create PreReg
		try {
			response = lib.CreatePreReg();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Upload document
		try {
			response = lib.documentUpload(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		String documentId = response.jsonPath().get("response[0].documentId").toString();
		logger.info("Document ID: " + documentId);

		// Delete document by document Id
		try {
			//response = lib.deleteAllDocumentByDocId(documentId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		String actualMessage = response.jsonPath().get("response[0].resMsg").toString();
		lib.compareValues(actualMessage, expectedMessageDeleteDoc);

		// Check if document is deleted successfully
		try {
		//	response = lib.deleteAllDocumentByDocId(documentId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		actualMessage = response.jsonPath().get("err.message").toString();
		lib.compareValues(actualMessage, "DOCUMENT_IS_MISSING");
	}


	@Test(groups = { "IntegrationScenarios" })
	public void createAppUploadDocDeleteDocByPreRegId() {

		// Create PreReg

		response = lib.CreatePreReg();
		preRegID = response.jsonPath().get("response[0].preRegistrationId").toString();

		// Upload document

		response = lib.documentUpload(response);

		String documentId = response.jsonPath().get("response[0].documentId").toString();
		logger.info("Document ID: " + documentId);

		// Delete document by PreReg Id

		response = lib.deleteAllDocumentByPreId(preRegID);

		String actualMessage = response.jsonPath().get("response[0].resMsg").toString();
		lib.compareValues(actualMessage, expectedMessageDeleteDoc);

		// Check if document is deleted successfully

		response = lib.getAllDocumentForPreId(preRegID);

		actualMessage = response.jsonPath().get("err.message").toString();
		lib.compareValues(actualMessage, "DOCUMENT_IS_MISSING");
	}

	@Test(groups = { "IntegrationScenarios" })
	public void createAppDiscardUploadDoc() throws FileNotFoundException, IOException, ParseException {

		// Create PreReg

		response = lib.CreatePreReg();
		preRegID = response.jsonPath().get("response[0].preRegistrationId").toString();

		// Discard App
		response = lib.discardApplication(preRegID);
		// lib.compareValues(response.jsonPath().get("response[0].preRegistrationId").toString(),
		// preRegID);

		// Upload Document

		response = lib.documentUpload(response);

		String errMessage = response.jsonPath().get("err.message").toString();
		logger.info("Error message: " + errMessage);
		lib.compareValues(errMessage, "PRG_PAM_APP_005 --> UNABLE_TO_FETCH_THE_PRE_REGISTRATION");

	}
	@Test(groups = { "IntegrationScenarios" })
	public void createAppUpdateDiscard() {
		// Create PreReg

		response = lib.CreatePreReg();
		preRegID = response.jsonPath().get("response[0].preRegistrationId").toString();
		createdBy = response.jsonPath().get("response[0].createdBy").toString();

		// Update PreReg

		response = lib.updatePreReg(preRegID, createdBy);

		// Discard App
		response = lib.discardApplication(preRegID);
		lib.compareValues(response.jsonPath().getString("response[0].preRegistrationId").toString(), preRegID);
	}

	@Test(groups = { "IntegrationScenarios" })
	public void cancelAppointmentFetchCenterDetails() {

		// Create PreReg

		response = lib.CreatePreReg();
		preRegID = response.jsonPath().get("response[0].preRegistrationId").toString();

		// Upload document

		response = lib.documentUpload(response);

		String documentId = response.jsonPath().get("response[0].documentId").toString();
		logger.info("Document ID: " + documentId);

		// Fetch Center
		Response fetchCenterResponse = lib.FetchCentre();
		 String regCenterId = fetchCenterResponse.jsonPath().get("response.regCenterId").toString();

		// Book Appointment
		lib.BookAppointment(response, fetchCenterResponse, preRegID);

		// Cancel Appointment
		response = lib.CancelBookingAppointment(lib.FetchAppointmentDetails(preRegID), preRegID);
		lib.compareValues(response.jsonPath().get("response.message"), "APPOINTMENT_SUCCESSFULLY_CANCELED");
		lib.compareValues(fetchCenterResponse.jsonPath().get("response").toString(),
				lib.FetchCentre(regCenterId).jsonPath().get("response").toString());
	}

	@Test(groups = { "IntegrationScenarios" })
	public void createAppUploadBookUpdate() {

		// Create PreReg
		try {
			response = lib.CreatePreReg();
			preRegID = response.jsonPath().get("response[0].preRegistrationId").toString();
			createdBy = response.jsonPath().get("response[0].createdBy").toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Upload document
		try {
			response = lib.documentUpload(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		String documentId = response.jsonPath().get("response[0].documentId").toString();
		logger.info("Document ID: " + documentId);

		// Fetch Center
		Response fetchCenterResponse = lib.FetchCentre();

		// Book Appointment
		response = lib.BookAppointment(response, fetchCenterResponse, preRegID);
		lib.compareValues(response.jsonPath().getString("response[0].bookingMessage"), "APPOINTMENT_SUCCESSFULLY_BOOKED");

		// Update PreReg
		try {
			response = lib.updatePreReg(preRegID, createdBy);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		Assert.assertNotNull(response.jsonPath().get("response[0].updatedBy"));

	}

	@SuppressWarnings("unchecked")
	@Test(groups = { "IntegrationScenarios" })
	public void createMultipleAppDeleteFewFetchAllAppsByUserId() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response preRegResponse1 = lib.CreatePreReg(createPregRequest);
		Response preRegResponse2 = lib.CreatePreReg(createPregRequest);
		Response preRegResponse3 = lib.CreatePreReg(createPregRequest);
		// Delete a preReg
		String preRegIdToDelete = preRegResponse3.jsonPath().get("response[0].preRegistrationId").toString();
		response = lib.discardApplication(preRegIdToDelete);
		lib.compareValues(response.jsonPath().getString("response[0].preRegistrationId").toString(), preRegIdToDelete);

		Response fetchResponse = lib.fetchAllPreRegistrationCreatedByUser();
		
			int no = fetchResponse.jsonPath().getList("response.preRegistrationId").size();
			Assert.assertEquals(no, 2);
			fetchResponse.jsonPath().get("response[0].preRegistrationId").toString()
					.contains((preRegResponse1.jsonPath().get("response[0].preRegistrationId")).toString());
			fetchResponse.jsonPath().get("response[1].preRegistrationId").toString()
					.contains((preRegResponse2.jsonPath().get("response[0].preRegistrationId")).toString());

	}

	@Test(groups = { "IntegrationScenarios" })
	public void createAppUploadFetchBookAppFetchApp() {
		List<String> appointmentDetails = new ArrayList<>();

		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;

		// Create PreReg
		try {
			response = lib.CreatePreReg();
			preRegID = response.jsonPath().get("response[0].preRegistrationId").toString();
			createdBy = response.jsonPath().get("response[0].createdBy").toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Upload document
		try {
			response = lib.documentUpload(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		String documentId = response.jsonPath().get("response[0].documentId").toString();
		logger.info("Document ID: " + documentId);

		// Fetch Center
		Response fetchCenterResponse = lib.FetchCentre();

		// Book Appointment
		response = lib.BookAppointment(response, fetchCenterResponse, preRegID);
		lib.compareValues(response.jsonPath().getString("response[0].bookingMessage"), bookingSuccessMessage);

		// Update PreReg
		try {
			response = lib.updatePreReg(preRegID, createdBy);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		Assert.assertNotNull(response.jsonPath().get("response[0].updatedBy"));

	}

	@Test(groups = { "IntegrationScenarios" })
	public void uploadMultipleDocsForSameCategory() {

		String file1 = "ProofOfAddress";
		String file2 = "AadhaarCard_POA";

		// Create PreReg
		try {
			response = lib.CreatePreReg();
			preRegID = response.jsonPath().get("response[0].preRegistrationId").toString();
		} catch (

		Exception e) { // TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Upload first document
		try {
			response = lib.documentUpload(response, file1);
		} catch (Exception e) { // TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Upload second document
		try {
			response = lib.documentUpload(response, file2);
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		// Fetch document by pre-registration ID
		try {
			response = lib.getAllDocumentForPreId(preRegID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	*//**
	 * @author Ashish Fetch Pending appointment created by(done) user
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void fetchMultipleApplicationCreatedByUser() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		*//**
		 * creating preRegistration and fetching created pre registration by user id.
		 *//*
		Response createPreRegResponse = lib.CreatePreReg(createPregRequest);
		Response fetchResponse = lib.fetchAllPreRegistrationCreatedByUser();
		*//**
		 * adding assertion
		 *//*
		lib.compareValues((createPreRegResponse.jsonPath().get("response[0].preRegistrationId")).toString(),
				fetchResponse.jsonPath().get("response[0].preRegistrationId").toString());
	
	}

	*//**
	 * @author Ashish fetch multiple pre registration created by user(done)
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void fetchMultipleUserCreatedByUser() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response preRegResponse1 = lib.CreatePreReg(createPregRequest);
		Response preRegResponse2 = lib.CreatePreReg(createPregRequest);
		Response fetchResponse = lib.fetchAllPreRegistrationCreatedByUser();
		try {
			if (fetchResponse.jsonPath().get("status").toString().equalsIgnoreCase("true")) {
				int no = fetchResponse.jsonPath().getList("response.preRegistrationId").size();
				Assert.assertEquals(no, 2);
				fetchResponse.jsonPath().get("response[0].preRegistrationId").toString()
						.contains((preRegResponse1.jsonPath().get("response[0].preRegistrationId")).toString());
				fetchResponse.jsonPath().get("response[0].preRegistrationId").toString()
						.contains((preRegResponse1.jsonPath().get("response[0].preRegistrationId")).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	*//**
	 * @author Ashish Fetch booked appointment created by user(done)
	 * 
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void fetchBookedAppointmentCreatedByUser() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response fetchResponse = lib.fetchAllPreRegistrationCreatedByUser();
			lib.compareValues(preID, fetchResponse.jsonPath().get("response[0].preRegistrationId").toString());
			Response fetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
			lib.compareValues(fetchResponse.jsonPath().get("response[0].bookingRegistrationDTO").toString(),
					fetchAppointmentDetailsResponse.jsonPath().get("response").toString());

	}


	*//**
	 * @author Ashish Scenario Fetch canceled appointment created by user(done)
	 * 
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void fetchCanceledAppointmentCreatedByUser() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response FetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		Response cancelBookingAppointmentResponse = lib.CancelBookingAppointment(FetchAppointmentDetailsResponse,
				preID);
		Assert.assertEquals(cancelBookingAppointmentResponse.jsonPath().get("response.message").toString(),
				"APPOINTMENT_SUCCESSFULLY_CANCELED");
		Response fetchAllPreRegistrationCreatedByUserResponse = lib.fetchAllPreRegistrationCreatedByUser();
		Assert.assertEquals(
				fetchAllPreRegistrationCreatedByUserResponse.jsonPath().get("response[0].preRegistrationId").toString(),
				preID);
		Assert.assertNull(
				fetchAllPreRegistrationCreatedByUserResponse.jsonPath().get("response[0].bookingRegistrationDTO"));

	}


	*//**
	 * @author Ashish cancel appointment for expired Application
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void cancelAppointmentForExpiredApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookExpiredAppointment(documentResponse, avilibityResponse, preID);
		Response FetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		lib.expiredStatus();
		lib.getPreRegistrationStatus(preID);
		Response CancelBookingAppointmentResponse = lib.CancelBookingAppointment(FetchAppointmentDetailsResponse,
				preID);
		String msg = CancelBookingAppointmentResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(msg, "APPOINTMENT_CANNOT_BE_CANCELED");

	}

	*//**
	 * @author Ashish Update pre Registration data for expired application
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void updatePreRegistrationDataForExpiredApplication()
			throws FileNotFoundException, IOException, ParseException {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookExpiredAppointment(documentResponse, avilibityResponse, preID);
		Response FetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		lib.expiredStatus();
		Response updateResponse = lib.updatePreReg(preID, createdBy);
		String updatePreId = updateResponse.jsonPath().get("response[0].preRegistrationId").toString();
		lib.compareValues(updatePreId, preID);
		lib.CancelBookingAppointment(FetchAppointmentDetailsResponse, preID);
	}

	*//**
	 * @author Ashish Copy document for discarded application.(discard source pre
	 *         id)
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void copyDocumentForDiscardApplication() throws FileNotFoundException, IOException, ParseException {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response sourceResponse = lib.CreatePreReg(createPregRequest);
		String sourcePreId = sourceResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response desResponse = lib.CreatePreReg();
		lib.documentUpload(sourceResponse);
		lib.discardApplication(desResponse.jsonPath().get("response[0].preRegistrationId").toString());
		String desPreId = desResponse.jsonPath().get("response[0].preRegistrationId").toString();
		//Response copyUploadedDocuments = lib.copyUploadedDocuments(sourcePreId, desPreId);
		//lib.compareValues(copyUploadedDocuments.jsonPath().get("err.errorCode").toString(), "PRG_PAM_APP_005");
		//lib.compareValues(copyUploadedDocuments.jsonPath().get("err.message").toString(),
			//	"PRG_PAM_APP_005 --> UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
	}

	*//**
	 * @author Ashish Book appointment for expired application
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void bookAppointmentForExpiredApplication() throws FileNotFoundException, IOException, ParseException {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookExpiredAppointment(documentResponse, avilibityResponse, preID);
		Response FetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		lib.expiredStatus();
		Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(preID);
		lib.compareValues(getPreRegistrationStatusResponse.jsonPath().get("response[0].statusCode").toString(),
				"Expired");
		Response reBookAnAppointmentResponse = lib.ReBookAnAppointment(preID, FetchAppointmentDetailsResponse,
				avilibityResponse);
		lib.compareValues(reBookAnAppointmentResponse.jsonPath().get("response[0].bookingStatus").toString(), "Booked");

	}

	*//**
	 * @author Ashish Fetch discarded pre registration created by user
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void fetchDiscardedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		*//**
		 * creating preRegistration and fetching created pre registration by user id.
		 *//*
		Response createPreRegResponse = lib.CreatePreReg(createPregRequest);
		preID = createPreRegResponse.jsonPath().get("response[0].preRegistrationId").toString();
		lib.discardApplication(preID);
		Response fetchResponse = lib.fetchAllPreRegistrationCreatedByUser();
		lib.compareValues(fetchResponse.jsonPath().get("err.message").toString(), "NO_RECORD_FOUND_FOR_USER_ID");

	}

	*//**
	 * @author Ashish Fetch appointment details for discarded Booked
	 *         Appointment(done)
	 * 
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void discardBookedAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		String expectedRegCenterId = avilibityResponse.jsonPath().get("response.regCenterId").toString();
		String expectedCenterDetails = avilibityResponse.jsonPath().get("response.centerDetails[0].timeSlots[0]").toString();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response discardResponse = lib.discardApplication(preID);
			Response fetchAppointmentResponse = lib.FetchAppointmentDetails(preID);
			Assert.assertEquals(fetchAppointmentResponse.jsonPath().get("errors[0].message").toString(),
					"UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
			avilibityResponse = lib.FetchCentre(expectedRegCenterId);
			String actualRegCenterId = avilibityResponse.jsonPath().get("response.regCenterId").toString();
			lib.compareValues(actualRegCenterId, expectedRegCenterId);
			String actualCenterDetails = avilibityResponse.jsonPath().get("response.centerDetails[0].timeSlots[0]").toString();
			lib.compareValues(expectedCenterDetails, actualCenterDetails);
			
	}
	*//**
	 * @author M9010713 update demographic data after booking an appointment
	 *//*

	@Test(groups = { "IntegrationScenarios" })
	public void updateDemographicDataAfterBookingAppointMent()
			throws FileNotFoundException, IOException, ParseException {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response updatePreRegResponse = lib.updatePreReg(preID, createdBy);
		String preIDAfterUpdate = updatePreRegResponse.jsonPath().get("response[0].preRegistrationId").toString();
		lib.compareValues(preIDAfterUpdate, preID);
	}

	*//**
	 * @author Ashish Fetch get Pre Registration data for Booked Appointment
	 * 
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void getPreRegistrationDataForBookedAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response getPreRegistrationResponse = lib.getPreRegistrationData(preID);
		Assert.assertEquals(preID,
				getPreRegistrationResponse.jsonPath().get("response[0].preRegistrationId").toString());
		Assert.assertEquals(getPreRegistrationResponse.jsonPath().get("response[0].statusCode"), "Booked");
	}

	*//**
	 * Book an appointment giving invalid date
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void bookAppForInvalidDate() {

		// Create PreReg
		try {
			response = lib.CreatePreReg();
			preRegID = response.jsonPath().get("response[0].preRegistrationId").toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Upload document
		try {
			response = lib.documentUpload(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		String documentId = response.jsonPath().get("response[0].documentId").toString();
		logger.info("Document ID: " + documentId);

		// Fetch Center
		Response fetchCenterResponse = lib.FetchCentre();

		// Book Appointment
		response = lib.bookAppointmentInvalidDate(response, fetchCenterResponse, preRegID);
		System.out.println(response.jsonPath().get("response").toString());

		// Assert.assertNull(response.jsonPath().get("response"));
		Assert.assertEquals("[]", response.jsonPath().get("response").toString());

	}

	*//**
	 * @author Ashish get pre registration data for discarded application
	 * 
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void getPreRegistrationDataForDiscardedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		createdBy = createResponse.jsonPath().get("response[0].createdBy").toString();
		Response discardResponse = lib.discardApplication(preID);
		Assert.assertEquals(preID, discardResponse.jsonPath().get("response[0].preRegistrationId").toString());
		Assert.assertEquals(createdBy, discardResponse.jsonPath().get("response[0].deletedBy").toString());
		Response getPreRegistrationDataResponse = lib.getPreRegistrationData(preID);
		Assert.assertEquals(getPreRegistrationDataResponse.jsonPath().get("err.message").toString(),
				"UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
	}

	*//**
	 * @author Ashish get pre registration data for pending appointment application
	 *         application
	 * 
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void getPreRegistrationDataForPendingApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response getPreRegistrationDataResponse = lib.getPreRegistrationData(preID);
		lib.compareValues(getPreRegistrationDataResponse.jsonPath().getString("response[0].preRegistrationId"), preID);
	}

	*//**
	 * @author Ashish get Status Of Booked Appointment Appointment
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void getStatusOfBookedAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response getPreRegistrationStatus = lib.getPreRegistrationStatus(preID);
		Assert.assertEquals(getPreRegistrationStatus.jsonPath().get("response[0].statusCode"), "Booked");
		Assert.assertEquals(getPreRegistrationStatus.jsonPath().get("response[0].preRegistartionId"), preID);
	}

	*//**
	 * @author Ashish get Status Of Canceled Appointment Appointment
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void getStatusOfCanceledAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response getPreRegistrationStatus = lib.getPreRegistrationStatus(preID);
		Assert.assertEquals(getPreRegistrationStatus.jsonPath().get("response[0].statusCode"), "Booked");
		Response FetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		lib.CancelBookingAppointment(FetchAppointmentDetailsResponse, preID);
		Response getPreRegistrationStatusAfterCancel = lib.getPreRegistrationStatus(preID);
		Assert.assertEquals(getPreRegistrationStatusAfterCancel.jsonPath().get("response[0].statusCode"),
				"Pending_Appointment");
	}

	*//**
	 * @author Ashish retrivePreRegistrationDataAfterBookingAppointment
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void retrivePreRegistrationDataAfterBookingAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response FetchAppointmentDetails = lib.FetchAppointmentDetails(preID);
		Response retrivePreRegistrationDataResponse = lib.retrivePreRegistrationData(preID);
		lib.compareValues(
				retrivePreRegistrationDataResponse.jsonPath().get("response.registration-client-id").toString(),
				FetchAppointmentDetails.jsonPath().get("response.registration_center_id").toString());
		// lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("response.appointment_date").toString(),
		// FetchAppointmentDetails.jsonPath().get("response.appointment_date").toString());
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("response.zip-filename").toString(), preID);
	}

	*//**
	 * @author Ashish Retrive Pre Registration of discarded application
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*

	@Test(groups = { "IntegrationScenarios" })
	public void retrivePreRegistrationDataOfDiscardedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response discardResponse = lib.discardApplication(preID);
		Response retrivePreRegistrationDataResponse = lib.retrivePreRegistrationData(preID);
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("err.message"),
				"UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("err.errorCode"), "PRG_PAM_APP_005");

	}
		

	*//**
	 * @author Ashish Retrive Pre Registration cancel appointment
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*

	@Test(groups = { "IntegrationScenarios" })
	public void retrivePreRegistrationDataForCancelAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response FetchAppointmentDetails = lib.FetchAppointmentDetails(preID);
		lib.CancelBookingAppointment(FetchAppointmentDetails, preID);
		Response retrivePreRegistrationDataResponse = lib.retrivePreRegistrationData(preID);
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("err.message"), "BOOKING_DATA_NOT_FOUND");
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("err.errorCode"), "PRG_BOOK_RCI_013");

	}

	*//**
	 * @author Ashish Retrive Pre Registration After uploading demographic details
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void retrivePreRegistrationDataAfterUploadingDemographicDetails() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();

		Response retrivePreRegistrationDataResponse = lib.retrivePreRegistrationData(preID);
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("err.message").toString(),
				"BOOKING_DATA_NOT_FOUND");
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("err.errorCode").toString(),
				"PRG_BOOK_RCI_013");

	}

	*//**
	 * @author Ashish create,discard,get application data
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void getPreRegistrationDataOfDiscardedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		lib.discardApplication(preID);
		Response getPreRegistrationDataResponse = lib.getPreRegistrationData(preID);
		lib.compareValues(getPreRegistrationDataResponse.jsonPath().get("err.message").toString(),
				"UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
	}

	*//**
	 * @author Ashish Book Appointment for discarded application
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void bookAppointmentForDiscardedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentUpload = lib.documentUpload(createResponse);
		lib.discardApplication(preID);
		Response FetchCentreResponse = lib.FetchCentre();
		Response BookAppointmentResponse = lib.BookAppointment(documentUpload, FetchCentreResponse, preID);
		String errorCode = BookAppointmentResponse.jsonPath().get("errors[0].errorCode").toString();
		String message = BookAppointmentResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "PRG_PAM_APP_005");
		lib.compareValues(message, "UNABLE_TO_FETCH_THE_PRE_REGISTRATION");

	}

	*//**
	 * @author Ashish Book multiple appointment for same PRID
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void bookMultipleAppointmentForSamePRID() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentUpload = lib.documentUpload(createResponse);
		Response FetchCentreResponse = lib.FetchCentre();
		lib.BookAppointment(documentUpload, FetchCentreResponse, preID);
		Response FetchCentreResponse1 = lib.FetchCentre();
		Response responsed = lib.BookAppointment(documentUpload, FetchCentreResponse1, preID);
		lib.compareValues(responsed.jsonPath().get("message").toString(),
				"PRG_CORE_REQ_005 --> APPOINTMENT_CANNOT_BE_BOOKED_FOR_BOOKED");

	}

	*//**
	 * @author Ashish get data for discarded application
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void dataOfDiscardedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		lib.discardApplication(preID);
		Response getPreRegistrationDataResponse = lib.getPreRegistrationData(preID);
		String errorCode = getPreRegistrationDataResponse.jsonPath().get("errors[0].errorCode").toString();
		String message = getPreRegistrationDataResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "PRG_PAM_APP_005");
		lib.compareValues(message, "UNABLE_TO_FETCH_THE_PRE_REGISTRATION");

	}

	*//**
	 * Create,Discard,upload document
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void uploadDocumentForDiscardedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		lib.discardApplication(preID);
		Response documentResponse = lib.documentUpload(createResponse);
		String errorCode = documentResponse.jsonPath().get("err.errorCode").toString();
		String message = documentResponse.jsonPath().get("err.message").toString();
		lib.compareValues(message, "PRG_PAM_APP_005 --> UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
		lib.compareValues(errorCode, "PRG_PAM_APP_005");

	}

	*//**
	 * @author Ashish Retrive Pre Registration After uploading document
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 *//*

	@Test(groups = { "IntegrationScenarios" })
	public void retrivePreRegistrationDataAfterUploadingDocument() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		lib.documentUpload(createResponse);
		Response retrivePreRegistrationDataResponse = lib.retrivePreRegistrationData(preID);
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("err.message").toString(),
				"BOOKING_DATA_NOT_FOUND");
		lib.compareValues(retrivePreRegistrationDataResponse.jsonPath().get("err.errorCode").toString(),
				"PRG_BOOK_RCI_013");

	}

	*//**
	 * @author Ashish create application,u[pload document[parent],create
	 *         application[child],copy document from source to dest
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void copyDocument() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponseSource = lib.CreatePreReg(createPregRequest);
		String preIDSource = createResponseSource.jsonPath().get("response[0].preRegistrationId").toString();
		lib.documentUpload(createResponseSource);
		Response createResponseDestination = lib.CreatePreReg(createPregRequest);
		String preIDDestination = createResponseDestination.jsonPath().get("response[0].preRegistrationId").toString();
		//Response copyUploadedDocumentsResponse = lib.copyUploadedDocuments(preIDSource, preIDDestination);
	}
	*//**@author Ashish
	 * Consumed booked appointment
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void cosumedBookedAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createPregResponse = lib.CreatePreReg(createPregRequest);
			String PreID = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
			Response documentUploadResponse = lib.documentUpload(createPregResponse);
			String expectedDocumentId = documentUploadResponse.jsonPath().get("response[0].documentId").toString();
			Response fetchCentreResponse = lib.FetchCentre();
			 String expectedRegCenterId = fetchCentreResponse.jsonPath().get("response.regCenterId").toString();
			lib.BookAppointment(documentUploadResponse, fetchCentreResponse, PreID);
			List<String> preRegistrationIds = new ArrayList<String>();
			preRegistrationIds.add(PreID);
			lib.reverseDataSync(preRegistrationIds);
			lib.consumedStatus();
			String status = lib.getConsumedStatus(PreID);
			String actualRegCenterId = lib.getRegCenterIdOfConsumedApplication(PreID);
			String actualDocumentId = lib.getDocumentIdOfConsumedApplication(PreID);
			lib.compareValues(actualDocumentId, expectedDocumentId);
			lib.compareValues(status, "Consumed");
			lib.compareValues(actualRegCenterId, expectedRegCenterId);
		}
	*//**@author Ashish
	 * Consumed Expired appointment
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void cosumedExpiredAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createPregResponse = lib.CreatePreReg(createPregRequest);
			String PreID = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
			Response documentUploadResponse = lib.documentUpload(createPregResponse);
			String expectedDocumentId = documentUploadResponse.jsonPath().get("response[0].documentId").toString();
			Response fetchCentreResponse = lib.FetchCentre();
			 String expectedRegCenterId = fetchCentreResponse.jsonPath().get("response.regCenterId").toString();
			lib.BookExpiredAppointment(documentUploadResponse, fetchCentreResponse, PreID);
			lib.expiredStatus();
			Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(PreID);
			String expiredStatus = getPreRegistrationStatusResponse.jsonPath().get("response[0].statusCode").toString();
			lib.compareValues(expiredStatus, "Expired");
			List<String> preRegistrationIds = new ArrayList<String>();
			preRegistrationIds.add(PreID);
			lib.reverseDataSync(preRegistrationIds);
			lib.consumedStatus();
			String status = lib.getConsumedStatus(PreID);
			String actualRegCenterId = lib.getRegCenterIdOfConsumedApplication(PreID);
			String actualDocumentId = lib.getDocumentIdOfConsumedApplication(PreID);
			lib.compareValues(actualDocumentId, expectedDocumentId);
			lib.compareValues(status, "Consumed");
			lib.compareValues(actualRegCenterId, expectedRegCenterId);
		}
	*//**@author Ashish
	 * Changing status to expired using batch job service
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void expiredBatchJobService() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createPregResponse = lib.CreatePreReg(createPregRequest);
			String PreID = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
			Response documentUploadResponse = lib.documentUpload(createPregResponse);
			Response fetchCentreResponse = lib.FetchCentre();
			lib.BookExpiredAppointment(documentUploadResponse, fetchCentreResponse, PreID);
			lib.expiredStatus();
			Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(PreID);
			String status = getPreRegistrationStatusResponse.jsonPath().get("response[0].statusCode").toString();
			lib.compareValues(status, "Expired");
			
		}
	
	*//**@author Ashish
	 * Changing status to Consumed using batch job service
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void consumedBatchJobService() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createPregResponse = lib.CreatePreReg(createPregRequest);
			String PreID = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
			Response documentUploadResponse = lib.documentUpload(createPregResponse);
			String expectedDocumentId = documentUploadResponse.jsonPath().get("response[0].documentId").toString();
			Response fetchCentreResponse = lib.FetchCentre();
			 String expectedRegCenterId = fetchCentreResponse.jsonPath().get("response.regCenterId").toString();
			lib.BookAppointment(documentUploadResponse, fetchCentreResponse, PreID);
			List<String> preRegistrationIds = new ArrayList<String>();
			preRegistrationIds.add(PreID);
			lib.reverseDataSync(preRegistrationIds);
			lib.consumedStatus();
			String status = lib.getConsumedStatus(PreID);
			String actualRegCenterId = lib.getRegCenterIdOfConsumedApplication(PreID);
			String actualDocumentId = lib.getDocumentIdOfConsumedApplication(PreID);
			lib.compareValues(actualDocumentId, expectedDocumentId);
			lib.compareValues(status, "Consumed");
			lib.compareValues(actualRegCenterId, expectedRegCenterId);
		}
	
	*//**@author Ashish
	 * retrive PreRegistration data for consumed Application
	 *//*
	
	@Test(groups = { "IntegrationScenarios" })
	public void retrivePreRegDataConsumedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createPregResponse = lib.CreatePreReg(createPregRequest);
			String PreID = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
			Response documentUploadResponse = lib.documentUpload(createPregResponse);
			Response fetchCentreResponse = lib.FetchCentre();
			lib.BookAppointment(documentUploadResponse, fetchCentreResponse, PreID);
			List<String> preRegistrationIds=new ArrayList<String>();
			preRegistrationIds.add(PreID);
			lib.reverseDataSync(preRegistrationIds);
			lib.consumedStatus();
			Response getPreRegistrationStatusResposne = lib.getPreRegistrationStatus(PreID);
			String massege = getPreRegistrationStatusResposne.jsonPath().get("err.message").toString();
			lib.compareValues(massege, "UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
			
		}
	
	*//**
	 * @author Ashish 
	 * Consumed multiple pre registration ids
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void consumedMultiplePRID() {
		List<String> preRegistrationIds = new ArrayList<String>();
		String PreID = null;
		for (int i = 1; i <= 3; i++) {
			testSuite = "Create_PreRegistration/createPreRegistration_smoke";
			JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createPregResponse = lib.CreatePreReg(createPregRequest);
			PreID = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
			Response documentUploadResponse = lib.documentUpload(createPregResponse);
			Response fetchCentreResponse = lib.FetchCentre();
			lib.BookAppointment(documentUploadResponse, fetchCentreResponse, PreID);
			preRegistrationIds.add(PreID);
			
		}
		lib.reverseDataSync(preRegistrationIds);
		lib.consumedStatus();
		for(String PreRegId:preRegistrationIds)
		{
			Response getPreRegistrationStatusResposne = lib.getPreRegistrationStatus(PreRegId);
			lib.compareValues(getPreRegistrationStatusResposne.jsonPath().get("err.message").toString(), "UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
		}
		
	
	}
	
	
	
	*//**@author Ashish
	 * Book an Appointment Delete all document then retrive PreRegistration data
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void retrivePreRegDataAfterDeletingDocument() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createPregResponse = lib.CreatePreReg(createPregRequest);
			String PreID = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
			Response documentUploadResponse = lib.documentUpload(createPregResponse);
			Response fetchCentreResponse = lib.FetchCentre();
			lib.BookAppointment(documentUploadResponse, fetchCentreResponse, PreID);
			lib.deleteAllDocumentByPreId(PreID);
			Response retrivePreRegistrationDataResponse = lib.retrivePreRegistrationData(PreID);
			lib.fetchDocs(retrivePreRegistrationDataResponse, "PreRegDocs");
		}
	
	*//**
	 * @author Ashish Consumed multiple pre registration ids with some invalid PRID
	 *//*
	@Test(groups = { "IntegrationScenarios" })
	public void consumedMultiplePRIDWithInvalidPRID() {
		List<String> preRegistrationIds = new ArrayList<String>();
		String PreID = null;
		for (int i = 1; i <= 3; i++) {
			testSuite = "Create_PreRegistration/createPreRegistration_smoke";
			JSONObject createPregRequest = lib.createRequest(testSuite);
			Response createPregResponse = lib.CreatePreReg(createPregRequest);
			PreID = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
			Response documentUploadResponse = lib.documentUpload(createPregResponse);
			Response fetchCentreResponse = lib.FetchCentre();
			lib.BookAppointment(documentUploadResponse, fetchCentreResponse, PreID);
			preRegistrationIds.add(PreID);

		}
		preRegistrationIds.add("invalid");
		lib.reverseDataSync(preRegistrationIds);
		lib.consumedStatus();
		int count=0;
		for (String PreRegId : preRegistrationIds) {
			Response getPreRegistrationStatusResposne = lib.getPreRegistrationStatus(PreRegId);
			lib.compareValues(getPreRegistrationStatusResposne.jsonPath().get("err.message").toString(), "UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
			count++;
			
		}
		String actualCount = Integer.toString(count);
		lib.compareValues(actualCount, "4");

	
	}

	// Integration scenario for copy document i.e.,create application,upload
	// document[for one application],create one more application,copy document
	// from
	// one application to another application Status Of Canceled Appointment
	// Appointment

	@Test(groups = { "IntegrationScenarios" })
	public void copyUploadedDocument() {

		// Creating the Pre-Registration Application
		Response createApplicationResponse;
		try {
			createApplicationResponse = lib.CreatePreReg();

			String preId = createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();

			// Document Upload for created application
			Response docUploadResponse = lib.documentUpload(createApplicationResponse);

			// PreId of Uploaded document
			String srcPreID = docUploadResponse.jsonPath().get("response[0].preRegistrationId").toString();

			// Creating the Pre-Registration Application for Destination PreId
			Response createApplicationRes = lib.CreatePreReg();
			String destPreId = createApplicationRes.jsonPath().get("response[0].preRegistrationId").toString();

			// Copy uploaded document from Source PreId to Destination PreId
			Response copyDocresponse = lib.copyUploadedDocuments(srcPreID, destPreId);

			lib.compareValues(copyDocresponse.jsonPath().get("response[0].sourcePreRegId").toString(), srcPreID);
			lib.compareValues(copyDocresponse.jsonPath().get("response[0].destPreRegId").toString(), destPreId);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Integration scenario - Create application, document upload for diff cat
	// codes,get all document for pre id

	@Test(groups = { "IntegrationScenarios" })
	public void multipleDocumentUpload() {

		PreRegistrationLibrary lib = new PreRegistrationLibrary();

		// Create PreReg

		String preRegID = null;
		String createdBy = null;
		Response createApplicationResponse = null;
		Response docUploadRes_POA = null;
		Response docUploadRes_POB = null;
		Response docUploadRes_POI = null;
		Response getAllDocForPreId = null;

		try {
			createApplicationResponse = lib.CreatePreReg();
			preRegID = createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
			createdBy = createApplicationResponse.jsonPath().get("response[0].createdBy").toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Upload document for different cat codes for same preId
		try {

			docUploadRes_POA = lib.multipleDocumentUpload(createApplicationResponse, filepathPOA, "/" + POADocName);
			docUploadRes_POB = lib.multipleDocumentUpload(createApplicationResponse, filepathPOB, "/" + POBDocName);
			docUploadRes_POI = lib.multipleDocumentUpload(createApplicationResponse, filepathPOI, "/" + POIDocName);

			getAllDocForPreId = lib.getAllDocumentForPreId(preRegID);

			JSONObject filePathPOAReq = lib.requestJson(filepathPOA);
			JSONObject filePathPOBReq = lib.requestJson(filepathPOB);
			JSONObject filePathPOIReq = lib.requestJson(filepathPOI);

			// Assertion for Document category POA - 0th element in response

			lib.compareValues(getAllDocForPreId.jsonPath().get("response[0].prereg_id").toString(), preRegID);
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[0].doc_name").toString(), POADocName);
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[0].doc_cat_code").toString(),
					JsonPath.parse(filePathPOAReq).read("$.request.doc_cat_code"));
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[0].doc_typ_code").toString(),
					JsonPath.parse(filePathPOAReq).read("$.request.doc_typ_code"));
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[0].doc_file_format").toString(),
					JsonPath.parse(filePathPOAReq).read("$.request.doc_file_format"));

			// Assertion for Document category POB - 1st element in response

			lib.compareValues(getAllDocForPreId.jsonPath().get("response[1].prereg_id").toString(), preRegID);
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[1].doc_name").toString(), POBDocName);
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[1].doc_cat_code").toString(),
					JsonPath.parse(filePathPOBReq).read("$.request.doc_cat_code"));
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[1].doc_typ_code").toString(),
					JsonPath.parse(filePathPOBReq).read("$.request.doc_typ_code"));
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[1].doc_file_format").toString(),
					JsonPath.parse(filePathPOBReq).read("$.request.doc_file_format"));

			// Assertion for Document category POI - 2nd element in response

			lib.compareValues(getAllDocForPreId.jsonPath().get("response[2].prereg_id").toString(), preRegID);
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[2].doc_name").toString(), POIDocName);
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[2].doc_cat_code").toString(),
					JsonPath.parse(filePathPOIReq).read("$.request.doc_cat_code"));
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[2].doc_typ_code").toString(),
					JsonPath.parse(filePathPOIReq).read("$.request.doc_typ_code"));
			lib.compareValues(getAllDocForPreId.jsonPath().get("response[2].doc_file_format").toString(),
					JsonPath.parse(filePathPOIReq).read("$.request.doc_file_format"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	// Integration scenario for create the application,upload the multiple
	// upload
	// document,delete the documents from preId

	@Test(groups = { "IntegrationScenarios" })
	public void multipleDocumentUploadDeleteDocByPreId() {
		PreRegistrationLibrary lib = new PreRegistrationLibrary();

		// Create PreReg

		String preRegID = null;
		String createdBy = null;
		Response createApplicationResponse = null;
		Response docUploadRes_POA = null;
		Response docUploadRes_POB = null;
		Response docUploadRes_POI = null;
		Response getAllDocForPreId = null;

		try {
			createApplicationResponse = lib.CreatePreReg();
			preRegID = createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
			createdBy = createApplicationResponse.jsonPath().get("response[0].createdBy").toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		// Upload document for Multiple cat codes for same preId
		try {

			docUploadRes_POA = lib.multipleDocumentUpload(createApplicationResponse, filepathPOA, "/" + POADocName);
			docUploadRes_POB = lib.multipleDocumentUpload(createApplicationResponse, filepathPOB, "/" + POBDocName);
			docUploadRes_POI = lib.multipleDocumentUpload(createApplicationResponse, filepathPOI, "/" + POIDocName);

			getAllDocForPreId = lib.getAllDocumentForPreId(preRegID);

			Response delDocPreRegId = lib.deleteAllDocumentByPreId(preRegID);
			lib.compareValues(delDocPreRegId.jsonPath().get("response[0].resMsg").toString(), expectedMessageDeleteDoc);
			lib.compareValues(delDocPreRegId.jsonPath().get("response[1].resMsg").toString(), expectedMessageDeleteDoc);
			lib.compareValues(delDocPreRegId.jsonPath().get("response[2].resMsg").toString(), expectedMessageDeleteDoc);

			lib.compareValues(delDocPreRegId.jsonPath().get("response[0].documnet_Id").toString(),
					getAllDocForPreId.jsonPath().get("response[0].doc_id").toString());
			lib.compareValues(delDocPreRegId.jsonPath().get("response[1].documnet_Id").toString(),
					getAllDocForPreId.jsonPath().get("response[1].doc_id").toString());
			lib.compareValues(delDocPreRegId.jsonPath().get("response[2].documnet_Id").toString(),
					getAllDocForPreId.jsonPath().get("response[2].doc_id").toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	// Integration scenario - Create application ,upload document for exceeding
	// size

	@Test(groups = { "IntegrationScenarios" })
	public void documentUploadGreaterThanFileSize() {
		PreRegistrationLibrary lib = new PreRegistrationLibrary();

		// Create PreReg

		String preRegID = null;
		String createdBy = null;
		Response createApplicationResponse = null;
		Response docUploadResGreaterThanFileSize = null;
		try {
			createApplicationResponse = lib.CreatePreReg();
			preRegID = createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
			createdBy = createApplicationResponse.jsonPath().get("response[0].createdBy").toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		try {

			docUploadResGreaterThanFileSize = lib.multipleDocumentUpload(createApplicationResponse,
					filepathDocGreaterThanFileSize, "/" + ExceedingSizeDocName);

			// Assertion Document exceeding the permitted size
			lib.compareValues(docUploadResGreaterThanFileSize.jsonPath().get("errors[0].errorCode").toString(),
					expectedErrCodeDocGreaterThanFileSize);
			lib.compareValues(docUploadResGreaterThanFileSize.jsonPath().get("errors[0].message").toString(),
					expectedErrMessageDocGreaterThanFileSize);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	// Integration scenario - Delete document for discard application by using
	// document id

	@Test(groups = { "IntegrationScenarios" })
	public void delDocByDocIdForDiscardedApplication() {
		PreRegistrationLibrary lib = new PreRegistrationLibrary();

		// Create PreReg

		String preRegID = null;
		String createdBy = null;
		Response createApplicationResponse = null;

		try {
			createApplicationResponse = lib.CreatePreReg();
			preRegID = createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
			createdBy = createApplicationResponse.jsonPath().get("response[0].createdBy").toString();

			Response uploadDoc = lib.documentUpload(createApplicationResponse);

			String docId = uploadDoc.jsonPath().get("response[0].documentId").toString();

			Response discardApp = lib.discardApplication(preRegID);
			//Response delDocumentByDocId = lib.deleteAllDocumentByDocId(docId);

			//lib.compareValues(delDocumentByDocId.jsonPath().get("errors[0].errorCode").toString(), "PRG_PAM_DOC_005");
			//lib.compareValues(delDocumentByDocId.jsonPath().get("errors[0].message").toString(), "DOCUMENT_IS_MISSING");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	@Test(groups = { "IntegrationScenarios" })
	public void retriveAllPreRegIdsByRegId() {
		PreRegistrationLibrary lib = new PreRegistrationLibrary();

		// Create PreReg

		String preRegID = null;
		String createdBy = null;

		Response createApplicationResponse = null;
		Response bookApp = null;

		try {

			for (int i = 0; i <= 4; i++) {
				createApplicationResponse = lib.CreatePreReg();
				preRegID = createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
				createdBy = createApplicationResponse.jsonPath().get("response[0].createdBy").toString();
				Response docUploadResponse = lib.documentUpload(createApplicationResponse);

				Response fetchavaRes = lib.FetchCentre();

				bookApp = lib.BookAppointment(docUploadResponse, fetchavaRes, preRegID);
				System.out.println("My Book App det::" + bookApp.asString());

			}

			Response retriveAllPreregId = lib.retriveAllPreIdByRegId();

			// Assertion for Retrieve PreId By Reg Center Id
			lib.compareValues(retriveAllPreregId.jsonPath().get("response[0].preRegistrationId").toString(), preRegID);
			lib.compareValues(retriveAllPreregId.jsonPath().get("response[0].registartion_center_id").toString(),
					"10022");
			lib.compareValues(retriveAllPreregId.jsonPath().get("response[0].pre_registration_ids[0]").toString(),
					"97186158062160");
			lib.compareValues(retriveAllPreregId.jsonPath().get("response[0].pre_registration_ids[1]").toString(),
					"65180632596528");
			lib.compareValues(retriveAllPreregId.jsonPath().get("response[0].pre_registration_ids[2]").toString(),
					"82378490340132");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

}

*/