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

import io.mosip.dbentity.OtpEntity;
import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class ValidateOtp extends BaseTestCase implements ITest {
	protected static String testCaseName = "";
	public Logger logger = Logger.getLogger(BatchJob.class);
	public String testSuite;
	public String preRegID = null;
	public String createdBy = null;
	public Response response = null;
	public PreRegistrationLibrary lib = new PreRegistrationLibrary();
	PreregistrationDAO dao = new PreregistrationDAO();

	@BeforeClass
	public void readPropertiesFile() {
		initialize();
	}
	@Test
	public void validateOtpSendToMobileNo() {
		testSuite = "SendOtp/SendOtpMobile";
		String validateTestSuite = "validateOTP/validateOTP_smoke";
		JSONObject sendOtpRequest = lib.otpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		String otp = dao.getOTP(userId).get(0);
		testSuite = "validateOTP/validateOTP_smoke";
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, userId, otp);
		Response validateOTPRes = lib.validateOTP(validateOTPRequest);
		
		lib.compareValues(validateOTPRes.jsonPath().get("response.message").toString(), "VALIDATION_SUCCESSFUL");
	}
	
	@Test
	public void validateOtpSendToEmail() {
		testSuite = "SendOtp/SendOtpToEmail";
		String validateTestSuite = "validateOTP/validateOTP_smoke";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		String otp = dao.getOTP(userId).get(0);
		testSuite = "validateOTP/validateOTP_smoke";
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, userId, otp);
		Response validateOTPRes = lib.validateOTP(validateOTPRequest);
		lib.compareValues(validateOTPRes.jsonPath().get("response.message").toString(), "VALIDATION_SUCCESSFUL");
	}
	@Test
	public void validateExpired() {
		testSuite = "SendOtp/SendOtpMobile";
		String validateTestSuite = "validateOTP/validateOTP_smoke";
		JSONObject sendOtpRequest = lib.otpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		try {
			Thread.sleep(180000);
		} catch (InterruptedException e) {
			logger.info(e);
		}
		String otp = dao.getOTP(userId).get(0);
		testSuite = "validateOTP/validateOTP_smoke";
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, userId, otp);
		Response validateOTPRes = lib.validateOTP(validateOTPRequest);
		lib.compareValues(validateOTPRes.jsonPath().get("errors[0].message").toString(), "OTP_EXPIRED");
		lib.compareValues(validateOTPRes.jsonPath().get("errors[0].errorCode").toString(), "PRG_PAM_LGN_013");
	}
	@Test
	public void validateWithoutGeneratingOtp(){
		
		String validateTestSuite = "validateOTP/validateOTP_smoke";
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, "9804605101", "385140");
		Response validateOTPRes = lib.validateOTP(validateOTPRequest);
		lib.compareValues(validateOTPRes.jsonPath().get("errors[0].message").toString(), "User Detail doesn't exist");
		lib.compareValues(validateOTPRes.jsonPath().get("errors[0].errorCode").toString(), "KER-ATH-003");
	}
	
	@Test
	public void blockedUser() {
		List<String> otps=new ArrayList<String>();
		testSuite = "SendOtp/SendOtpMobile";
		String validateTestSuite = "validateOTP/validateOTP_smoke";
		JSONObject sendOtpRequest = lib.otpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		String otp = dao.getOTP(userId).get(0);
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, userId, "236578");
		for(int i=1;i<=10;i++)
		{
			lib.validateOTP(validateOTPRequest);
		}
		Response validateOTP = lib.validateOTP(validateOTPRequest);
		String message = validateOTP.jsonPath().get("errors[0].message").toString();
		lib.compareValues(message, "USER_BLOCKED");
	}
	@Test
	public void validateWithInvalidOtp() {
		testSuite = "SendOtp/SendOtpMobile";
		String validateTestSuite = "validateOTP/validateOTP_smoke";
		JSONObject sendOtpRequest = lib.otpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, userId, "236578");
		Response validateOTP = lib.validateOTP(validateOTPRequest);
		String message = validateOTP.jsonPath().get("errors[0].message").toString();
		lib.compareValues(message, "VALIDATION_UNSUCCESSFUL");
	}
	@Test
	public void validateWithInvalidUserID() {
		List<String> otps=new ArrayList<String>();
		testSuite = "SendOtp/SendOtpMobile";
		String validateTestSuite = "validateOTP/validateOTP_smoke";
		JSONObject sendOtpRequest = lib.otpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		String otp = dao.getOTP(userId).get(0);
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, "Ashish", otp);
		Response validateOTP = lib.validateOTP(validateOTPRequest);
		String message = validateOTP.jsonPath().get("errors[0].message").toString();
		lib.compareValues(message, "User Detail doesn't exist");
	}
	@Override
	public String getTestName() {
		return this.testCaseName;

	}
	@BeforeMethod(alwaysRun = true)
	public void login( Method method)
	{
		testCaseName="preReg_Demogarphic_" + method.getName();
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
