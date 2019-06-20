package io.mosip.preregistration.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.internal.thread.DaemonThreadFactory;

import io.mosip.dbentity.OtpEntity;
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

public class Sample extends BaseTestCase implements ITest {
	Logger logger = Logger.getLogger(Sample.class);
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
	String updateSuite = "UpdateDemographicData/UpdateDemographicData_smoke";
	PreregistrationDAO dao = new PreregistrationDAO();
	public String expectedMessageDeleteDoc = "DOCUMENT_DELETE_SUCCESSFUL";
	public String docMissingMessage = "Documents is not found for the requested pre-registration id";
	public String unableToFetchPreReg = "UNABLE_TO_FETCH_THE_PRE_REGISTRATION";
	public String appointmentCanceledMessage = "APPOINTMENT_SUCCESSFULLY_CANCELED";
	public String bookingSuccessMessage = "APPOINTMENT_SUCCESSFULLY_BOOKED";
	public String expectedErrMessageDocGreaterThanFileSize = "DOCUMENT_EXCEEDING_PREMITTED_SIZE";
	public String expectedErrCodeDocGreaterThanFileSize = "PRG_PAM_DOC_007";
	public String filepathPOA = "IntegrationScenario/DocumentUpload_POA";
	public String filepathPOB = "IntegrationScenario/DocumentUpload_POB";
	public String filepathPOI = "IntegrationScenario/DocumentUpload_POI";
	public String filepathDocGreaterThanFileSize = "IntegrationScenario/DocumentUploadGreaterThanFileSize";
	public String POADocName = "AadhaarCard_POA.pdf";
	public String POBDocName = "ProofOfBirth_POB.pdf";
	public String POIDocName = "LicenseCertification_POI.pdf";
	public String ExceedingSizeDocName = "ProofOfAddress.pdf";

	SoftAssert soft = new SoftAssert();
	

	@BeforeClass
	public void readPropertiesFile() {
		initialize();

	}

	/**
	 * Batch job service for expired application
	 * 
	 * @throws java.text.ParseException
	 * 
	 * 
	 */
	
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
				createApplicationResponse = lib.CreatePreReg(individualToken);
				preRegID = createApplicationResponse.jsonPath().get("response.preRegistrationId").toString();
				createdBy = createApplicationResponse.jsonPath().get("response.createdBy").toString();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}

			// Upload document for different cat codes for same preId
			try {

				docUploadRes_POA = lib.multipleDocumentUpload(createApplicationResponse, filepathPOA, "/" + POADocName,individualToken);
				docUploadRes_POB = lib.multipleDocumentUpload(createApplicationResponse, filepathPOB, "/" + POBDocName,individualToken);
				docUploadRes_POI = lib.multipleDocumentUpload(createApplicationResponse, filepathPOI, "/" + POIDocName,individualToken);

				getAllDocForPreId = lib.getAllDocumentForPreId(preRegID,individualToken);

				JSONObject filePathPOAReq = lib.requestJson(filepathPOA);
				JSONObject filePathPOBReq = lib.requestJson(filepathPOB);
				JSONObject filePathPOIReq = lib.requestJson(filepathPOI);

				// Assertion for Document category POA - 0th element in response

				lib.compareValues(getAllDocForPreId.jsonPath().get("response.prereg_id").toString(), preRegID);
				lib.compareValues(getAllDocForPreId.jsonPath().get("response.doc_name").toString(), POADocName);
				lib.compareValues(getAllDocForPreId.jsonPath().get("response.doc_cat_code").toString(),
						JsonPath.parse(filePathPOAReq).read("$.request.doc_cat_code"));
				lib.compareValues(getAllDocForPreId.jsonPath().get("response.doc_typ_code").toString(),
						JsonPath.parse(filePathPOAReq).read("$.request.doc_typ_code"));
				lib.compareValues(getAllDocForPreId.jsonPath().get("response.doc_file_format").toString(),
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

		
	

	@BeforeMethod(alwaysRun = true)
	public void run() {
if(!lib.isValidToken(individualToken))
		{
			individualToken=lib.getToken();
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