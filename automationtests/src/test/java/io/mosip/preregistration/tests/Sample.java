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
import org.apache.maven.plugins.assembly.io.AssemblyReadException;
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

public class Sample extends BaseTestCase {
	Logger logger = Logger.getLogger(Sample.class);
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
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createRequest = lib.createRequest(testSuite);
		System.out.println(createRequest);
		
		
	}

}
