
package io.mosip.preregistration.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.GetHeader;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Fetch all the applications created by user related
 * Positive and Negative test cases
 * 
 * @author Ashish Rastogi
 * @since 1.0.0
 */

public class FetchAllApplicationCreatedByUser extends BaseTestCase implements ITest {

	public String preId = "";
	private static String preReg_URI;
	protected static String testCaseName = "";
	public Response Actualresponse = null;
	private static Logger logger = Logger.getLogger(FetchAllApplicationCreatedByUser.class);
	public boolean status = false;
	private static CommonLibrary commonLibrary = new CommonLibrary();
	public static PreRegistrationLibrary lib = new PreRegistrationLibrary();

	FetchAllApplicationCreatedByUser() {
		super();
	}

	@Test
	public void fetchAllAplicationCreatedByUser() {
		JSONObject actualRequest;
		actualRequest = lib.createRequest("Create_PreRegistration/createPreRegistration_smoke");
		Response createResponse = lib.CreatePreReg(actualRequest);
		Actualresponse = lib.fetchAllPreRegistrationCreatedByUser();
		String expectedResult = createResponse.jsonPath().get("response.preRegistrationId").toString();
		String actualResult = Actualresponse.jsonPath().get("response.basicDetails[0].preRegistrationId").toString();
		lib.compareValues(actualResult, expectedResult);
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
		lib.logOut();
	}
	@BeforeMethod(alwaysRun=true)
	public static void getTestCaseName(Method method) {
		testCaseName="preReg_BatchJob_" + method.getName();
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_FetchAllApplicationCreatedByUserURI");
		authToken = lib.getToken();
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

}
