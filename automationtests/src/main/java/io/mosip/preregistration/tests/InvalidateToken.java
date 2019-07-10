package io.mosip.preregistration.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class InvalidateToken extends BaseTestCase implements ITest {
	public Logger logger = Logger.getLogger(BatchJob.class);
	public PreRegistrationLibrary lib = new PreRegistrationLibrary();
	public String testSuite;
	public String preRegID = null;
	public String createdBy = null;
	public Response response = null;
	public String preID = null;
	protected String testCaseName = "";
	public String folder = "preReg";
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
		String cookie = lib.getToken();
		Response invalidateTokenResponse = lib.logOut(cookie);
		String message = invalidateTokenResponse.jsonPath().get("response.message").toString();
		lib.compareValues(message, "Token has been invalidated successfully");
		Response createPreRegResponse = lib.CreatePreReg(cookie);
		String errorCode = createPreRegResponse.jsonPath().get("errors[0].errorCode").toString();
		String errorMessage = createPreRegResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "KER-ATH-401");
		lib.compareValues(errorMessage, "Invalid Token");
		
	}
	@Test
	public void invalidateInactiveToken()
	{
		String cookie = lib.getToken();
		Response invalidateTokenResponse = lib.logOut(cookie);
		String message = invalidateTokenResponse.jsonPath().get("response.message").toString();
		lib.compareValues(message, "Token has been invalidated successfully");
		invalidateTokenResponse = lib.logOut(cookie);
		String errorCode = lib.getErrorCode(invalidateTokenResponse);
		message=lib.getErrorMessage(invalidateTokenResponse);
		lib.compareValues(message, "Token is not present in datastore,Please try with new token");
		lib.compareValues(errorCode, "KER-ATH-008");
	}
	@BeforeMethod(alwaysRun=true)
	public void login( Method method)
	{
		testCaseName="preReg_Authentication_" + method.getName();
	}
	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@AfterMethod(alwaysRun=true)
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_Authentication_" + method.getName());
			
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
	
	}
}
