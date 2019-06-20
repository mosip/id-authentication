package io.mosip.preregistration.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class UpdateDemographicDetails extends BaseTestCase implements ITest {
	Logger logger = Logger.getLogger(BatchJob.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	String testSuite;
	String preRegID = null;
	String createdBy = null;
	Response response = null;
	String preID = null;
	protected static String testCaseName = "";
	static String folder = "preReg";
	String updateSuite = "UpdateDemographicData/UpdateDemographicData_smoke";
	private static CommonLibrary commonLibrary = new CommonLibrary();
	ApplicationLibrary applnLib = new ApplicationLibrary();
	PreregistrationDAO dao = new PreregistrationDAO();

	Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider()).build();

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}

	@Test
	public void updateDemographicDetailsOfPendingAppointmentApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createRequestResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createRequestResponse.jsonPath().get("response.preRegistrationId").toString();
		JSONObject updateRequest = lib.getRequest(updateSuite);
		updateRequest.put("requesttime", lib.getCurrentDate());
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, pre_registration_id);
		lib.compareValues(pre_registration_id,
				updateDemographicDetailsResponse.jsonPath().get("response.preRegistrationId").toString());
		Response getPreRegistrationData = lib.getPreRegistrationData(pre_registration_id);
	}

	@Test
	public void updateDemographicDetailsOfBookedAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String phone = "8240273209";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createPregResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createPregResponse.jsonPath().get("response.preRegistrationId").toString();
		Response documentUploadResponse = lib.documentUpload(createPregResponse);
		Response fetchCentreResponse = lib.FetchCentre();
		lib.BookAppointment(documentUploadResponse, fetchCentreResponse, pre_registration_id);
		JSONObject updateRequest = lib.getRequest(updateSuite);
		updateRequest.put("requesttime", lib.getCurrentDate());
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, pre_registration_id);
		lib.compareValues(pre_registration_id,
				updateDemographicDetailsResponse.jsonPath().get("response.preRegistrationId").toString());
		Response getPreRegistrationData = lib.getPreRegistrationData(pre_registration_id);
	}

	@Test
	public void updateDemographicDetailsOfExpiredAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String CNIENumber = "8243417898217834901290";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createPregResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createPregResponse.jsonPath().get("response.preRegistrationId").toString();
		Response documentUploadResponse = lib.documentUpload(createPregResponse);
		Response fetchCentreResponse = lib.FetchCentre();
		lib.BookAppointment(documentUploadResponse, fetchCentreResponse, pre_registration_id);
		dao.setDate(pre_registration_id);
		lib.expiredStatus();
		JSONObject updateRequest = lib.getRequest(updateSuite);
		updateRequest.put("requesttime", lib.getCurrentDate());
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, pre_registration_id);
		lib.compareValues(pre_registration_id,
				updateDemographicDetailsResponse.jsonPath().get("response.preRegistrationId").toString());
		Response getPreRegistrationData = lib.getPreRegistrationData(pre_registration_id);
	}

	@Test
	public void updateDemographicDetailsOfConsumedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String CNIENumber = "8243417898217834901290";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createPregResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createPregResponse.jsonPath().get("response.preRegistrationId").toString();
		Response documentUploadResponse = lib.documentUpload(createPregResponse);
		String expectedDocumentId = documentUploadResponse.jsonPath().get("response.docId").toString();
		Response fetchCentreResponse = lib.FetchCentre();
		String expectedRegCenterId = fetchCentreResponse.jsonPath().get("response.regCenterId").toString();
		lib.BookAppointment(documentUploadResponse, fetchCentreResponse, pre_registration_id);
		List<String> preRegistrationIds = new ArrayList<String>();
		preRegistrationIds.add(pre_registration_id);
		lib.reverseDataSync(preRegistrationIds);
		lib.consumedStatus();
		JSONObject updateRequest = lib.getRequest(updateSuite);
		updateRequest.put("requesttime", lib.getCurrentDate());
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, pre_registration_id);
		lib.compareValues(updateDemographicDetailsResponse.jsonPath().get("errors[0].message").toString(),
				"No data found for the requested pre-registration id");
	}

	@Test
	public void updateDemographicDetailsOfDiscardedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String DOB = "25-09-1993";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createRequestResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createRequestResponse.jsonPath().get("response.preRegistrationId").toString();
		lib.discardApplication(pre_registration_id);
		JSONObject updateRequest = lib.getRequest(updateSuite);
		updateRequest.put("requesttime", lib.getCurrentDate());
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, pre_registration_id);
		lib.compareValues(updateDemographicDetailsResponse.jsonPath().get("errors[0].message").toString(),
				"No data found for the requested pre-registration id");
	}

	@Test
	public void updateDemographicDetailsWithInvalidPreRegistrationId() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String pre_registration_id = "7825432989162";
		JSONObject createRequest = lib.createRequest(testSuite);
		JSONObject updateRequest = lib.getRequest(updateSuite);
		updateRequest.put("requesttime", lib.getCurrentDate());
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(updateRequest, pre_registration_id);
		lib.compareValues(updateDemographicDetailsResponse.jsonPath().get("errors[0].message").toString(),
				"No data found for the requested pre-registration id");
	}

	@BeforeMethod(alwaysRun = true)
	public void login( Method method)
	{
		testCaseName="preReg_Demographic_" + method.getName();
		authToken=lib.getToken();
		
	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_Demogarphic_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
		lib.logOut();
	}

}
