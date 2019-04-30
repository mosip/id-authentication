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

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

/**
 * @author Ashish Rastogi
 *
 */

public class BatchJob extends BaseTestCase implements ITest {
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

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}
	/**
	 * Batch job service for expired application
	 */
	@Test
	public void batchJobForExpiredApplication() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookExpiredAppointment(documentResponse, avilibityResponse, preID);
		lib.expiredStatus();
		Response getPreRegistrationStatusResponse = lib.getPreRegistrationStatus(preID);
		String statusCode = getPreRegistrationStatusResponse.jsonPath().get("response[0].statusCode").toString();
		lib.compareValues(statusCode, "Expired");
	}
	/**
	 * Batch Job service Consumed Application
	 */
	@Test
	public void batchJobForConsumedApplication() {
		List preRegistrationId = new ArrayList();
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		preRegistrationId.add(preID);
		lib.reverseDataSync(preRegistrationId);
		Response consumedResponse = lib.consumedStatus();
		String message = consumedResponse.jsonPath().get("response").toString();
		lib.compareValues(message, "Demographic status to consumed updated successfully");
		Response getPreRegistrationDataResponse = lib.getPreRegistrationData(preID);
		message = getPreRegistrationDataResponse.jsonPath().get("err.message").toString();
		lib.compareValues(message, "UNABLE_TO_FETCH_THE_PRE_REGISTRATION");
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
