package io.mosip.preregistration.tests;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.apache.maven.plugins.assembly.io.AssemblyReadException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

/**
 * @author Vidya Shankar N S
 *
 */

public class Sample extends BaseTestCase {
	Logger logger = Logger.getLogger(Sample.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	String testSuite;
	 private static  String preReg_CreateApplnURI ;
	String expectedMessageDeleteDoc = "DOCUMENT_DELETE_SUCCESSFUL";
	String docMissingMessage = "DOCUMENT_IS_MISSING";
	String preRegID = null;
	String createdBy = null;
	Response response = null;
	String preID = null;
	static String folder = "preReg";
	Rebook rb = new Rebook();
	private static CommonLibrary commonLibrary = new CommonLibrary();
	@BeforeClass
	public void intializ()
	{
		//lib.intialize1();
	}
	
	@Test(groups = { "IntegrationScenarios" })
	public void bookAppointmentForDiscardedApplication() throws FileNotFoundException, IOException, ParseException {
		JSONObject createPregRequest = null;
		testSuite = "Create_PreRegistration\\createPreRegistration_smoke";
		/**
		 * Reading request body from configpath
		 */
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					createPregRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
			/**
			 * Creating an userId and puting it into actual request
			 */
			String createdBy = new Integer(lib.createdBy()).toString();
			JSONObject object = null;
			for (Object key : createPregRequest.keySet()) {
				if (key.equals("request")) {
					object = (JSONObject) createPregRequest.get(key);
					object.put("createdBy", createdBy);
					createPregRequest.replace(key, object);
				}
			}
			Response createResponse = lib.CreatePreReg(createPregRequest);
			String preID = createResponse.jsonPath().get("response[0].preRegistrationId").toString();
			Response documentUpload = lib.documentUpload(createResponse);
			Response FetchCentreResponse = lib.FetchCentre();
			lib.BookExpiredAppointment(documentUpload, FetchCentreResponse, preID);
			lib.expiredStatus();
			lib.getPreRegistrationStatus(preID);
			
		}
	}
}
