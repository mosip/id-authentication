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
		// authToken = lib.getToken();
	}

	@Test
	public void getAuditDataForDemographicCreate() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		lib.CreatePreReg(createPregRequest);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicCreate");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 1);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForDemographicDiscard() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		lib.discardApplication(preID);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicDiscard");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 2);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForDemographicUpdate() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createRequestResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createRequestResponse.jsonPath().get("response.preRegistrationId").toString();
		JSONObject updateRequest = lib.getRequest("UpdateDemographicData/UpdateDemographicData_smoke");
		updateRequest.put("requesttime", lib.getCurrentDate());
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, pre_registration_id);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicUpdate");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 2);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForDemographicFetchAllApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createRequestResponse = lib.CreatePreReg(createRequest);
		lib.fetchAllPreRegistrationCreatedByUser();
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicFetchAllApplication");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 2);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForDemographicException() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		createRequest.put("version", "2.0");
		System.out.println(createRequest.toString());
		Response createRequestResponse = lib.CreatePreReg(createRequest);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicException");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 1);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForGetAvailbleSlot() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		Response fetchCenterResponse = lib.FetchCentre();
		String regCenterId = fetchCenterResponse.jsonPath().get("response.regCenterId").toString();
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditFetchAvailibilityCenter");
		expectedRequest.put("session_user_id", userId);
		expectedRequest.put("ref_id", regCenterId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 1);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForBooking() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		Response fetchCenterResponse = lib.FetchCentre();
		lib.BookAppointment(fetchCenterResponse, preID);
		String regCenterId = fetchCenterResponse.jsonPath().get("response.regCenterId").toString();
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditForBooking");
		expectedRequest.put("session_user_id", userId);
		expectedRequest.put("ref_id", regCenterId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 3);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Test
	public void getAuditDataForCancelBooking() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		Response fetchCenterResponse = lib.FetchCentre();
		lib.BookAppointment(fetchCenterResponse, preID);
		lib.CancelBookingAppointment(preID);
		String regCenterId = fetchCenterResponse.jsonPath().get("response.regCenterId").toString();
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditForCancelAppointment");
		expectedRequest.put("session_user_id", userId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 4);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}
	@SuppressWarnings("unchecked")
	@Test
	public void getAuditDataForReBooking() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		Response fetchCenterResponse = lib.FetchCentre();
		lib.BookAppointment(fetchCenterResponse, preID);
		fetchCenterResponse = lib.FetchCentre();
		lib.BookAppointment(fetchCenterResponse, preID);
		String regCenterId = fetchCenterResponse.jsonPath().get("response.regCenterId").toString();
		String userId = lib.userId;

		JSONObject expectedRequest = lib.getRequest("Audit/AuditForReBooking");
		expectedRequest.put("session_user_id", userId);
		expectedRequest.put("ref_id", regCenterId);
		List<String> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 5);
		boolean result = lib.jsonComparison(expectedRequest, auditDatas);
		Assert.assertTrue(result, "object are not equal");
	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@BeforeMethod(alwaysRun = true)
	public void login( Method method)
	{
		testCaseName="preReg_BatchJob_" + method.getName();
		authToken=lib.getToken();
		
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
