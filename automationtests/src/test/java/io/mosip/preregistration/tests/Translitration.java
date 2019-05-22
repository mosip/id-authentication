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

public class Translitration extends BaseTestCase implements ITest {
	public Logger logger = Logger.getLogger(Translitration.class);
	public PreRegistrationLibrary lib = new PreRegistrationLibrary();
	public String testSuite;
	public String preRegID = null;
	public String createdBy = null;
	public Response response = null;
	public String preID = null;
	protected static String testCaseName = "";
	public String folder = "preReg";
	public ApplicationLibrary applnLib = new ApplicationLibrary();

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}

	/**
	 * Translitration
	 */
	@Test
	public void translitrationFromArabicToFranch() {
		testSuite = "Translitration/Translitration_smoke";
		JSONObject translitrationRequest = null;
		String toLang="ara";
		String fromLang="fra";
		String from_value = "اسلمى";
		translitrationRequest=lib.translitrationRequest(testSuite, toLang, from_value, fromLang);
		Response translitrationResponse = lib.translitration(translitrationRequest);
		String to_field_value = translitrationResponse.jsonPath().get("response.to_field_value").toString();
		translitrationRequest=lib.translitrationRequest(testSuite, fromLang, to_field_value,toLang );
		translitrationResponse=lib.translitration(translitrationRequest);
		String expectedto_field_value = translitrationResponse.jsonPath().get("response.to_field_value").toString();
		lib.compareValues(from_value, expectedto_field_value);
	}
	@Test
	public void translitrationFromFranchToArabic() {
		testSuite = "Translitration/Translitration_smoke";
		JSONObject translitrationRequest = null;
		String fromLang="fra";
		String toLang="ara";
		String from_value = "assurance mensuelle";
		translitrationRequest=lib.translitrationRequest(testSuite,fromLang, from_value, toLang);
		Response translitrationResponse = lib.translitration(translitrationRequest);
		String to_field_value = translitrationResponse.jsonPath().get("response.to_field_value").toString();
		translitrationRequest=lib.translitrationRequest(testSuite, toLang, to_field_value,fromLang );
		translitrationResponse=lib.translitration(translitrationRequest);
		String expectedto_field_value = translitrationResponse.jsonPath().get("response.to_field_value").toString();
		lib.compareValues(expectedto_field_value, from_value);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}
	@BeforeMethod(alwaysRun = true)
	public void login( Method method)
	{
		testCaseName="preReg_Translitration_" + method.getName();
		authToken=lib.getToken();
		
	}	

	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_Translitration_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
		lib.logOut();
	}

}
