package io.mosip.preregistration.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

/**
 * @author Ashish Rastogi
 *
 */

public class Audit extends BaseTestCase implements ITest {
	public Logger logger = Logger.getLogger(Audit.class);
	public PreRegistrationLibrary lib = new PreRegistrationLibrary();
	public String testSuite;
	public String preRegID = null;
	public String createdBy = null;
	public Response response = null;
	public String preID = null;
	protected static String testCaseName = "";
	public String folder = "preReg";
	public ApplicationLibrary applnLib = new ApplicationLibrary();
	public PreregistrationDAO dao = new PreregistrationDAO();

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}

	@Test
	public void getAuditDataForDemographicCreate() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		lib.CreatePreReg(createPregRequest, individualToken);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicCreate");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}


	@Test
	public void getAuditDataForDemographicDiscard() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest, individualToken);
		String preID = lib.getPreId(createResponse);
		lib.discardApplication(preID, individualToken);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicDiscard");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForDemographicUpdate() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createRequestResponse = lib.CreatePreReg(createRequest, individualToken);
		String pre_registration_id = lib.getPreId(createRequestResponse);
		JSONObject updateRequest = lib.getRequest("UpdateDemographicData/UpdateDemographicData_smoke");
		updateRequest.put("requesttime", lib.getCurrentDate());
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, pre_registration_id,
				individualToken);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicUpdate");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForDemographicFetchAllApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createRequestResponse = lib.CreatePreReg(createRequest, individualToken);
		lib.fetchAllPreRegistrationCreatedByUser(individualToken);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicFetchAllApplication");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForDemographicException() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		createRequest.put("version", "2.0");
		Response createRequestResponse = lib.CreatePreReg(createRequest, individualToken);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicException");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForGetAvailbleSlot() {
		String regCenterId = null;
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		Response fetchCenterResponse = lib.FetchCentre(individualToken);
		try {
			regCenterId = fetchCenterResponse.jsonPath().get("response.regCenterId").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception while getting registartion center id from response");
		}
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditFetchAvailibilityCenter");
		expectedRequest.put("session_user_id", userId);
		expectedRequest.put("ref_id", regCenterId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForCancelBooking() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String regCenterId = null;
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest, individualToken);
		String preID = lib.getPreId(createResponse);
		Response fetchCenterResponse = lib.FetchCentre(individualToken);
		lib.BookAppointment(fetchCenterResponse, preID, individualToken);
		lib.CancelBookingAppointment(preID, individualToken);
		try {
			regCenterId = fetchCenterResponse.jsonPath().get("response.regCenterId").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception while getting registartion center id from response");
		}
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditForCancelAppointment");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAuditDataForReBooking() {
		String regCenterId = null;
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest, individualToken);
		String preID = lib.getPreId(createResponse);
		Response fetchCenterResponse = lib.FetchCentre(individualToken);
		lib.BookAppointment(fetchCenterResponse, preID, individualToken);
		fetchCenterResponse = lib.FetchCentre(individualToken);
		lib.BookAppointment(fetchCenterResponse, preID, individualToken);
		try {
			regCenterId = fetchCenterResponse.jsonPath().get("response.regCenterId").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception while getting registartion center id from response");
		}
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditForReBooking");
		expectedRequest.put("session_user_id", userId);
		expectedRequest.put("ref_id", regCenterId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForDocumentUpload() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest, individualToken);
		String preID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse, individualToken);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditForDocumentUpload");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
	}

	@Test
	public void getAuditDataForDocumentDeleteByDocId() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest, individualToken);
		String preID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse, individualToken);
		String docId = lib.getDocId(documentResponse);
		lib.deleteAllDocumentByDocId(docId, preID, individualToken);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/GetAuditDataForDeleteDocumentForDocId");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
	}

	@Test
	public void getAuditDataForRetriveDocumentByDocId() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest, individualToken);
		String preID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse, individualToken);
		String docId = lib.getDocId(documentResponse);
		lib.getAllDocumentForDocId(preID, docId, individualToken);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/GetAuditDataForRetriveDocumentByDocId");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
	}

	@Test
	public void getAuditDataForCopyDocument() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest, individualToken);
		String sourcePreID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse, individualToken);
		createResponse = lib.CreatePreReg(createPregRequest, individualToken);
		String destPreID = lib.getPreId(createResponse);
		lib.copyUploadedDocuments(destPreID, sourcePreID, "POA", individualToken);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/getAuditDataForCopyDocument");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
	}

	@Test
	public void getAuditDataForBatchJobExpired() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest,individualToken);
		String preID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse,individualToken);
		Response avilibityResponse = lib.FetchCentre(individualToken);
		lib.BookAppointment(documentResponse, avilibityResponse, preID,individualToken);
		dao.setDate(preID);
		lib.expiredStatus();
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDataForBatchJobExpired");
		expectedRequest.put("session_user_id", "preregadminuser1");
		List<String> objs = dao.getAuditData("preregadminuser1");
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);

	}

	@Test
	public void getAuditDataForBatchJobConsumed() {
		List preRegistrationId = new ArrayList();
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest, individualToken);
		preID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse, individualToken);
		Response avilibityResponse = lib.FetchCentre(individualToken);
		lib.BookAppointment(documentResponse, avilibityResponse, preID, individualToken);
		preRegistrationId.add(preID);
		lib.reverseDataSync(preRegistrationId);
		Response consumedResponse = lib.consumedStatus();
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDataForBatchJobConsumed");
		expectedRequest.put("session_user_id", "preregadminuser1");
		List<String> objs = dao.getAuditData("preregadminuser1");
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);

	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@BeforeMethod(alwaysRun = true)
	public void login(Method method) {
		testCaseName = "preReg_BatchJob_" + method.getName();
		if (!lib.isValidToken(individualToken)) {
			individualToken = lib.getToken();
		}

	}

	@BeforeClass
	public void run() {
		individualToken = lib.getToken();
	}

	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "PreReg_Audit_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
	}
}
