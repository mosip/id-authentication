package io.mosip.preregistration.tests;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class TestClass extends BaseTestCase {
	Logger logger = Logger.getLogger(IntegrationScenarios.class);
	PreRegistrationLibrary lib = new PreRegistrationLibrary();
	String testSuite;
	String expectedMessageDeleteDoc = "DOCUMENT_DELETE_SUCCESSFUL";
	String docMissingMessage = "DOCUMENT_IS_MISSING";
	String unableToFetchPreReg = "UNABLE_TO_FETCH_THE_PRE_REGISTRATION";
	String appointmentCanceledMessage = "APPOINTMENT_SUCCESSFULLY_CANCELED";
	String bookingSuccessMessage = "APPOINTMENT_SUCCESSFULLY_BOOKED";

	String preRegID = null;
	String createdBy = null;
	Response response = null;
	String preID = null;
	static String folder = "preReg";

	@BeforeTest
	public void readPropertiesFile() {
		initialize();
	}

	@Test
	public void testMethod() {
		// TODO Auto-generated method stub
		
		
		
	}
}
