package io.mosip.preregistration.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
//import org.apache.maven.plugins.assembly.io.AssemblyReadException;
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
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

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

public class OperationWithDifferentToken extends BaseTestCase implements ITest {
	public Logger logger = Logger.getLogger(OperationWithDifferentToken.class);
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

	@Test
	public void getUserADemographicDataUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		preID=lib.getPreId(createResponse);
		String cookie = lib.getToken();
		Response getPreRegistrationDataResponse = lib.getPreRegistrationData(preID, cookie);
		String errorCode = lib.getErrorCode(getPreRegistrationDataResponse);
		String message = lib.getErrorMessage(getPreRegistrationDataResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	
	@Test
	public void discardUserADataUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		String preID = lib.getPreId(createResponse);
		individualToken=lib.getToken();
		Response discardResponse = lib.discardApplication(preID, individualToken);
		String errorCode = lib.getErrorCode(discardResponse);
		String message = lib.getErrorMessage(discardResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void bookMultipleAppointmentUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse1 = lib.CreatePreReg(createPregRequest, individualToken);
		preID = lib.getPreId(createResponse1);
		Response documentResponse = lib.documentUpload(createResponse1, individualToken);
		Response avilibityResponse = lib.FetchCentre(individualToken);
		String cookie = lib.getToken();
		Response createResponse2 = lib.CreatePreReg(createPregRequest, cookie);
		String preID2 = lib.getPreId(createResponse2);
		Response documentResponse2 = lib.documentUpload(createResponse2, cookie);
		Response avilibityResponse2 = lib.FetchCentre(cookie);
		Response multipleBookingResponse = lib.multipleBookApp(avilibityResponse, avilibityResponse2, preID, preID2, cookie);
		String errorCode = lib.getErrorCode(multipleBookingResponse);
		String message = lib.getErrorMessage(multipleBookingResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
		
	}
	@Test
	public void updateUserADataUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createPregResponse = lib.CreatePreReg(createRequest,individualToken);
		String pre_registration_id = createPregResponse.jsonPath().get("response.preRegistrationId").toString();
		JSONObject updateRequest = lib.getRequest("UpdateDemographicData/UpdateDemographicData_smoke");
		updateRequest.put("requesttime", lib.getCurrentDate());
		individualToken=lib.getToken();
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, pre_registration_id,individualToken);
		String errorCode = lib.getErrorCode(updateDemographicDetailsResponse);
		String message = lib.getErrorMessage(updateDemographicDetailsResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void uploadUserADocumentUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		preID=lib.getPreId(createResponse);
		String cookie = lib.getToken();
		Response documentResponse = lib.documentUpload(createResponse,cookie);
		String errorCode = lib.getErrorCode(documentResponse);
		String message = lib.getErrorMessage(documentResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void copyUserADocumentToUserB() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		String sourcePreID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		String cookie = lib.getToken();
		Response createDestResponse = lib.CreatePreReg(createPregRequest,cookie);
		String destPreID = lib.getPreId(createDestResponse);
		Response copyUploadedDocuments = lib.copyUploadedDocuments(destPreID, sourcePreID, "POA", cookie);
		String errorCode = lib.getErrorCode(copyUploadedDocuments);
		String message = lib.getErrorMessage(copyUploadedDocuments);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void deleteDocumentUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		String PreID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		String cookie = lib.getToken();
		Response deleteDocumentResponse = lib.deleteAllDocumentByPreId(PreID, cookie);
		String errorCode = lib.getErrorCode(deleteDocumentResponse);
		String message = lib.getErrorMessage(deleteDocumentResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void getPreRegistrationStatusUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		String PreID = lib.getPreId(createResponse);
		String cookie = lib.getToken();
		Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(PreID, cookie);
		String errorCode = lib.getErrorCode(getPreRegistrationStatusResponse);
		String message = lib.getErrorMessage(getPreRegistrationStatusResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void getUserADocumentUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		String PreID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		String cookie = lib.getToken();
		Response getAllDocumentForPreIdResponse = lib.getAllDocumentForPreId(PreID, cookie);
		String errorCode = lib.getErrorCode(getAllDocumentForPreIdResponse);
		String message = lib.getErrorMessage(getAllDocumentForPreIdResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void getDocumentByDocIdUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		String PreID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		String docId = documentResponse.jsonPath().get("response.docId").toString();
		String cookie = lib.getToken();
		Response getAllDocumentForDocIdResponse = lib.getAllDocumentForDocId(PreID, docId, cookie);
		String errorCode = lib.getErrorCode(getAllDocumentForDocIdResponse);
		String message = lib.getErrorMessage(getAllDocumentForDocIdResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void deleteAllDocumentByDocIdUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		preID=lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		String docId = documentResponse.jsonPath().get("response.docId").toString();
		String cookie = lib.getToken();
		Response deleteAllDocumentByDocId = lib.deleteAllDocumentByDocId(docId, preID, cookie);
		String errorCode = lib.getErrorCode(deleteAllDocumentByDocId);
		String message = lib.getErrorMessage(deleteAllDocumentByDocId);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void bookAppointmentUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		preID=lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		Response avilibityResponse = lib.FetchCentre(individualToken);
		String cookie = lib.getToken();
		Response bookingResponse = lib.BookAppointment(documentResponse, avilibityResponse, preID,cookie);
		String errorCode = lib.getErrorCode(bookingResponse);
		String message = lib.getErrorMessage(bookingResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void cancelAppointmentUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		String PreID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		Response avilibityResponse = lib.FetchCentre(individualToken);
		lib.BookAppointment(documentResponse, avilibityResponse, preID,individualToken);
		String cookie = lib.getToken();
		Response cancelBookingAppointmentResponse = lib.CancelBookingAppointment(PreID, cookie);
		String errorCode = lib.getErrorCode(cancelBookingAppointmentResponse);
		String message = lib.getErrorMessage(cancelBookingAppointmentResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void rebookAppointmentUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		preID=lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		Response avilibityResponse = lib.FetchCentre(individualToken);
		lib.BookAppointment(documentResponse, avilibityResponse, preID,individualToken);
		avilibityResponse = lib.FetchCentre(individualToken);
		String cookie = lib.getToken();
		Response bookingResponse = lib.BookAppointment(avilibityResponse, preID, cookie);
		String errorCode = lib.getErrorCode(bookingResponse);
		String message = lib.getErrorMessage(bookingResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}
	@Test
	public void fetchAppointmentDetailsOfUserAUsingUserBToken() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		preID=lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		Response avilibityResponse = lib.FetchCentre(individualToken);
		lib.BookAppointment(documentResponse, avilibityResponse, preID,individualToken);
		String cookie = lib.getToken();
		Response fetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID, cookie);
		String errorCode = lib.getErrorCode(fetchAppointmentDetailsResponse);
		String message = lib.getErrorMessage(fetchAppointmentDetailsResponse);
		lib.compareValues(message, "Requested preregistration id does not belong to the user");
		lib.compareValues(errorCode, "PRG_PAM_APP_017");
	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}
	@BeforeMethod(alwaysRun=true)
	public void login( Method method)
	{
		testCaseName="preReg_BatchJob_" + method.getName();
	}
	@BeforeClass
	public void getToken()
	{
		if(!lib.isValidToken(individualToken))
		{
			individualToken=lib.getToken();
		}
	}
	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_BatchJob_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
	}
}
