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
	Logger logger = Logger.getLogger(Audit.class);
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
	PreregistrationDAO dao = new PreregistrationDAO();

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
		authToken = lib.getToken();
	}
	/**
	 * Batch job service for expired application
	 */
	@Test
	public void getAuditDataForDemographicCreate() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject createPregRequest = lib.createRequest(testSuite);
		lib.CreatePreReg(createPregRequest);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicCreate");
		expectedRequest.put("session_user_id", userId);
		List<Object> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 0);
		Assert.assertEquals(true, auditDatas.equals(expectedRequest));
	}
	@Test
	public void getAuditDataForDemographicDiscard() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		lib.discardApplication(preID);
		String userId = lib.userId;
		JSONObject expectedRequest = lib.getRequest("Audit/AuditDemographicDiscard");
		expectedRequest.put("session_user_id", userId);
		List<Object> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 1);
		Assert.assertEquals(true, auditDatas.equals(expectedRequest));
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
		List<Object> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 1);
		Assert.assertEquals(true, auditDatas.equals(expectedRequest));
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
		List<Object> objs = dao.getAuditData(userId);
		JSONObject auditDatas = lib.getAuditData(objs, 1);
		System.out.println("====================="+auditDatas.toString());
		if(auditDatas.equals(expectedRequest))
			logger.info("both object are equal");
		else
		{
			logger.info("expected is==="+expectedRequest.toString());
			logger.info("but found is === "+auditDatas.toString());
			Assert.fail();
		}
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
