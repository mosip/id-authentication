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
import io.mosip.util.Cookie;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

/**
 * @author Ashish Rastogi
 *
 */

public class Pagination extends BaseTestCase implements ITest {
	public Logger logger = Logger.getLogger(Pagination.class);
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

	/**
	 * 
	 *  Script for pagination service
	 *  Here we need to pass page index it will return number of application are there in that page
	 *  page index is start from 0.
	 */
	@Test
	public void pagination_Smoke() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response fetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		Response paginationResponse = lib.pagination("0");
		try {
			lib.compareValues(paginationResponse.jsonPath().get("response.basicDetails[0].preRegistrationId").toString(), preID);
			lib.compareValues(paginationResponse.jsonPath().get("response.basicDetails[0].bookingMetadata").toString(), fetchAppointmentDetailsResponse.jsonPath().get("response").toString());
			lib.compareValues(paginationResponse.jsonPath().get("response.basicDetails[0].demographicMetadata.proofOfAddress.documentId").toString(), documentResponse.jsonPath().get("response.docId").toString());
		} catch (NullPointerException e) {
			Assert.fail("Exception occured while fetching data from pagination response");		}

	}
	@Test
	public void pagination_invalidIndex()
	{
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		Response paginationResponse = lib.pagination("abc");
		String errorCode = lib.getErrorCode(paginationResponse);
		String errorMessage = lib.getErrorMessage(paginationResponse);
		lib.compareValues(errorCode, "PRG_PAM_APP_019");
		lib.compareValues(errorMessage, "Invalid page index value");
	}
	@Test
	public void pagination_withoutPageIndexValue()
	{
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response fetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		Response paginationResponse = lib.pagination("");
		try {
			lib.compareValues(paginationResponse.jsonPath().get("response.basicDetails[0].preRegistrationId").toString(), preID);
			lib.compareValues(paginationResponse.jsonPath().get("response.basicDetails[0].bookingMetadata").toString(), fetchAppointmentDetailsResponse.jsonPath().get("response").toString());
			lib.compareValues(paginationResponse.jsonPath().get("response.basicDetails[0].demographicMetadata.proofOfAddress.documentId").toString(), documentResponse.jsonPath().get("response.docId").toString());
		} catch (NullPointerException e) {
			Assert.fail("Exception occured while fetching data from pagination response");
		}
	}
	@Test
	public void pagination_withNullValue()
	{
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		Response paginationResponse = lib.pagination("null");
		String errorCode = lib.getErrorCode(paginationResponse);
		String errorMessage = lib.getErrorMessage(paginationResponse);
		lib.compareValues(errorCode, "PRG_PAM_APP_019");
		lib.compareValues(errorMessage, "Invalid page index value");
	}
	@Test
	public void pagination_noRecordPresentForThatPageRange()
	{
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = lib.getPreId(createResponse);
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre();
		lib.BookAppointment(documentResponse, avilibityResponse, preID);
		Response fetchAppointmentDetailsResponse = lib.FetchAppointmentDetails(preID);
		Response paginationResponse = lib.pagination("10");
		String errorCode = lib.getErrorCode(paginationResponse);
		String errorMessage = lib.getErrorMessage(paginationResponse);
		lib.compareValues(errorCode, "PRG_PAM_APP_016");
		lib.compareValues(errorMessage, "no record found for the requested page index");
		
	}

	@Test
	public void pagination_noApplicationCreatedForThatUser()
	{
		Response paginationResponse = lib.pagination("0");
		String errorCode = lib.getErrorCode(paginationResponse);
		String errorMessage = lib.getErrorMessage(paginationResponse);
		lib.compareValues(errorCode, "PRG_PAM_APP_005");
		lib.compareValues(errorMessage, "No record found for the requested user id");
	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@BeforeMethod(alwaysRun = true)
	public void run() {
		authToken = lib.getToken();
	}

	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_Demographic_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
	}
}
