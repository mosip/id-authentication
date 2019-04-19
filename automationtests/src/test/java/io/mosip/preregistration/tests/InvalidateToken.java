package io.mosip.preregistration.tests;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class InvalidateToken extends BaseTestCase implements ITest {
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
	 * Script for invalidating Token
	 */
	@Test
	public void invalidateToken() {
		Response invalidateTokenResponse = lib.logOut();
		String message = invalidateTokenResponse.jsonPath().get("message").toString();
		lib.compareValues(message, "Token has been invalidated successfully");
		Response createPreRegResponse = lib.CreatePreReg();
		String errorCode = createPreRegResponse.jsonPath().get("errors[0].errorCode").toString();
		String errorMessage = createPreRegResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "KER-ATH-401");
		lib.compareValues(errorMessage, "Auth token has been changed,Please try with new login");
		
	}
	@BeforeMethod
	public void login()
	{
		authToken=lib.getToken();
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
