package io.mosip.preregistration.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

import com.google.gson.JsonObject;

import io.mosip.dbentity.OtpEntity;
import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class SendOtp extends BaseTestCase implements ITest {
	public Logger logger = Logger.getLogger(BatchJob.class);
	public PreRegistrationLibrary lib = new PreRegistrationLibrary();
	public String testSuite;
	public String preRegID = null;
	public String createdBy = null;
	public Response response = null;
	public String preID = null;
	protected static String testCaseName = "";
	public String folder = "preReg";
	ApplicationLibrary applnLib = new ApplicationLibrary();
	PreregistrationDAO dao = new PreregistrationDAO();

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}

	/**
	 * Scripting for send OTP API
	 */
	@Test
	public void sendOtpToEmailId() {
		testSuite = "SendOtp/SendOtpToEmail";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		String otp = dao.getOTP(userId).get(0);
		lib.compareValues(response.jsonPath().get("response.message").toString(), "Email Request submitted");
	}

	@Test
	public void sendOtpToMobile() {
		testSuite = "SendOtp/SendOtpMobile";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		String otp = dao.getOTP(userId).get(0);
		lib.compareValues(response.jsonPath().get("response.message").toString(), "Sms Request Sent");
	}

	@Test
	public void sendOtpToInvalidEmailId() {
		testSuite = "SendOtp/SendOtpToInvalid_Email_Id";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		Response generateOTPResponse = lib.generateOTP(sendOtpRequest);
		String errorCode = generateOTPResponse.jsonPath().get("errors[0].errorCode").toString();
		String message = generateOTPResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "PRG_PAM_LGN_008");
		lib.compareValues(message, "Invalid Request userId received");
	}

	@Test
	public void sendOtpToInvalidMobileNo() {
		testSuite = "SendOtp/SendOtpToInvalidMobileNo";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		Response generateOTPResponse = lib.generateOTP(sendOtpRequest);
		String errorCode = generateOTPResponse.jsonPath().get("errors[0].errorCode").toString();
		String message = generateOTPResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "PRG_PAM_LGN_008");
		lib.compareValues(message, "Invalid Request userId received");
	}

	@Test
	public void sendOtpWithoutGivingUserId() {
		testSuite = "SendOtp/sendOtpWithoutGivingUserId";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		Response generateOTPResponse = lib.generateOTP(sendOtpRequest);
		String errorCode = generateOTPResponse.jsonPath().get("errors[0].errorCode").toString();
		String message = generateOTPResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "PRG_PAM_LGN_008");
		lib.compareValues(message, "Invalid Request userId received");
	}

	@Test
	public void sendOtpToBlockedUser() {
		List<String> otps = new ArrayList<String>();
		testSuite = "SendOtp/SendOtpMobile";
		String validateTestSuite = "validateOTP/validateOTP_smoke";
		JSONObject sendOtpRequest = lib.otpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		String otp = dao.getOTP(userId).get(0);
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, userId, "236578");
		for (int i = 1; i <= 10; i++) {
			lib.validateOTP(validateOTPRequest);
		}
		Response generateOTP = lib.generateOTP(sendOtpRequest);
		String message = generateOTP.jsonPath().get("response.message").toString();
		lib.compareValues(message, "USER_BLOCKED");
	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}

	@BeforeMethod(alwaysRun = true)
	public void login( Method method)
	{
		testCaseName="preReg_Authentication_" + method.getName();
		authToken=lib.getToken();
		
	}

	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_Authentication_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
		lib.logOut();
	}

}
