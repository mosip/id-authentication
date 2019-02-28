package io.mosip.preregistration.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.ActionMapUIResource;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

/**
 * ByteToZip Class Covert Byte Array to zip and unzip and store in folder
 * 
 * @author Ashish Rastogi
 * @since 1.0.0
 */

public class ByteToZip extends BaseTestCase {
	Logger logger = Logger.getLogger(ByteToZip.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	String testSuite;

	String expectedMessageDeleteDoc = "DOCUMENT_DELETE_SUCCESSFUL";
	String docMissingMessage = "DOCUMENT_IS_MISSING";
	String preRegID = null;
	String createdBy = null;
	Response response = null;
	String preID = null;
	static String folder = "preReg";
	Rebook rb = new Rebook();

	@BeforeTest
	public void readPropertiesFile() {
		initialize();
	}

	/**
	 * Data Providers to read the input json files from the folders
	 * @param context
	 * @return input request file
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test(groups = { "IntegrationScenarios" })
	public void dataOfDiscardedApplication() throws FileNotFoundException, IOException, ParseException {
		JSONObject actualRequest1 = null;
		JSONObject createPregRequest = null;
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		testSuite = "Create_PreRegistration\\createPreRegistration_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				createPregRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

			}
		}
		ObjectMapper mapper = new ObjectMapper();
		Response sourceCreateResponse = lib.CreatePreReg(createPregRequest);

		HashMap<String, String> response1 = sourceCreateResponse.jsonPath().get("response[0].demographicDetails");

		String sourcePreID = sourceCreateResponse.jsonPath().get("response[0].preRegistrationId").toString();
		/**
		 * Data to Compare expected to be in ID.json
		 */
		//String identity = sourceCreateResponse.jsonPath().get("response[0].demographicDetails.identity").toString();
		Response documentUpload = lib.documentUpload(sourceCreateResponse);
		Response FetchCentreResponse = lib.FetchCentre();
		lib.BookAppointment(documentUpload, FetchCentreResponse, sourcePreID);
		Response retrivePreRegistrationDataresponse = lib.retrivePreRegistrationData(sourcePreID);

		lib.fetchDocs(retrivePreRegistrationDataresponse, "PreRegDocs");
		JSONObject actualRequest = null;
		String testSuite1 = "PreRegDocs\\" + sourcePreID;
		
		String requestConfigPath = System.getProperty("user.dir") + "\\" + testSuite1;
		
		File requestFolder = new File(requestConfigPath);
		File[] RequestListOfFiles = requestFolder.listFiles();
		for (File f : RequestListOfFiles) {
			if (f.getName().contains("ID")) {
			HashMap<String, String>	actualRequest11 = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				
			}

		}

	}

}
