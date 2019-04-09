package io.mosip.preregistration.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

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
	private static CommonLibrary commonLibrary = new CommonLibrary();
	ApplicationLibrary applnLib = new ApplicationLibrary();

	Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider()).build();

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}

	/*@Test
	public void updateDemographicDetailsOfPendingAppointmentApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String DOB = "25-09-1993";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createRequestResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createRequestResponse.jsonPath().get("response[0].pre_registration_id").toString();
		createRequest = JsonPath.using(config).parse(createRequest.toJSONString())
				.set("$.request.demographicDetails.identity.dateOfBirth", DOB).json();
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(createRequest, pre_registration_id);
		String actualDOB = updateDemographicDetailsResponse.jsonPath()
				.get("response[0].demographicDetails.identity.dateOfBirth").toString();
		lib.compareValues(actualDOB, DOB);
		lib.compareValues(pre_registration_id,
				updateDemographicDetailsResponse.jsonPath().get("response[0].pre_registration_id").toString());

	}

	@Test
	public void updateDemographicDetailsOfBookedAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String phone = "8240273209";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createPregResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentUploadResponse = lib.documentUpload(createPregResponse);
		Response fetchCentreResponse = lib.FetchCentre();
		lib.BookAppointment(documentUploadResponse, fetchCentreResponse, pre_registration_id);
		createRequest = JsonPath.using(config).parse(createRequest.toJSONString())
				.set("$.request.demographicDetails.identity.phone", phone).json();
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(createRequest, pre_registration_id);
		String actualphone = updateDemographicDetailsResponse.jsonPath()
				.get("response[0].demographicDetails.identity.phone").toString();
		lib.compareValues(actualphone, phone);
		lib.compareValues(pre_registration_id,
				updateDemographicDetailsResponse.jsonPath().get("response[0].pre_registration_id").toString());
	}

	@Test
	public void updateDemographicDetailsOfExpiredAppointment() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String CNIENumber = "8243417898217834901290";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createPregResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentUploadResponse = lib.documentUpload(createPregResponse);
		Response fetchCentreResponse = lib.FetchCentre();
		lib.BookExpiredAppointment(documentUploadResponse, fetchCentreResponse, pre_registration_id);
		createRequest = JsonPath.using(config).parse(createRequest.toJSONString())
				.set("$.request.demographicDetails.identity.CNIENumber", CNIENumber).json();
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(createRequest, pre_registration_id);
		String actualCNIENumber = updateDemographicDetailsResponse.jsonPath()
				.get("response[0].demographicDetails.identity.CNIENumber").toString();
		lib.compareValues(actualCNIENumber, CNIENumber);
		lib.compareValues(pre_registration_id,
				updateDemographicDetailsResponse.jsonPath().get("response[0].pre_registration_id").toString());
	}

	@Test
	public void updateDemographicDetailsOfConsumedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String CNIENumber = "8243417898217834901290";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createPregResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentUploadResponse = lib.documentUpload(createPregResponse);
		String expectedDocumentId = documentUploadResponse.jsonPath().get("response[0].documentId").toString();
		Response fetchCentreResponse = lib.FetchCentre();
		String expectedRegCenterId = fetchCentreResponse.jsonPath().get("response.regCenterId").toString();
		lib.BookAppointment(documentUploadResponse, fetchCentreResponse, pre_registration_id);
		List<String> preRegistrationIds = new ArrayList<String>();
		preRegistrationIds.add(pre_registration_id);
		lib.reverseDataSync(preRegistrationIds);
		lib.consumedStatus();
		createRequest = JsonPath.using(config).parse(createRequest.toJSONString())
				.set("$.request.demographicDetails.identity.CNIENumber", CNIENumber).json();
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(createRequest, pre_registration_id);
		String actualCNIENumber = updateDemographicDetailsResponse.jsonPath()
				.get("response[0].demographicDetails.identity.CNIENumber").toString();
	}
	@Test
	public void updateDemographicDetailsOfDiscardedApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String DOB = "25-09-1993";
		JSONObject createRequest = lib.createRequest(testSuite);
		Response createRequestResponse = lib.CreatePreReg(createRequest);
		String pre_registration_id = createRequestResponse.jsonPath().get("response[0].pre_registration_id").toString();
		lib.discardApplication(pre_registration_id);
		createRequest = JsonPath.using(config).parse(createRequest.toJSONString())
				.set("$.request.demographicDetails.identity.dateOfBirth", DOB).json();
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(createRequest, pre_registration_id);
		String actualDOB = updateDemographicDetailsResponse.jsonPath()
				.get("response[0].demographicDetails.identity.dateOfBirth").toString();

	}
	@Test
	public void updateDemographicDetailsWithInvalidPreRegistrationId() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String DOB = "25-09-1993";
		String pre_registration_id="7825432989162";
		JSONObject createRequest = lib.createRequest(testSuite);
		createRequest = JsonPath.using(config).parse(createRequest.toJSONString())
				.set("$.request.demographicDetails.identity.dateOfBirth", DOB).json();
		Response updateDemographicDetailsResponse = lib.updateDemographicDetails(createRequest, pre_registration_id);
		String actualDOB = updateDemographicDetailsResponse.jsonPath()
				.get("response[0].demographicDetails.identity.dateOfBirth").toString();
	}*/
	@Test
	public void updateDemographicDetailsOfPendingAppointmentApplication() {
	System.out.println("5555555555555555555555555555555512368123681236812368");
	}
	@BeforeMethod
	public void getAuthToken()
	{
		authToken=lib.getToken();
	}
	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		logger.info("method name:" + result.getMethod().getMethodName());
		lib.logOut();
		
	}

}
