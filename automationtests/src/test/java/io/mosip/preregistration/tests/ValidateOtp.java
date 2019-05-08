package io.mosip.preregistration.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.mosip.dbaccess.prereg_dbread;
import io.mosip.dbentity.OtpEntity;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class ValidateOtp extends BaseTestCase implements ITest {
	protected static String testCaseName = "";
	Logger logger = Logger.getLogger(BatchJob.class);
	String testSuite;
	String preRegID = null;
	String createdBy = null;
	Response response = null;
	PreRegistrationLibrary lib = new PreRegistrationLibrary();

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
		String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<Object> otpData = prereg_dbread.fetchOTPFromDB(otpQueryStr, OtpEntity.class);
		String otp = otpData.get(0).toString();
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
		String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<Object> otpData = prereg_dbread.fetchOTPFromDB(otpQueryStr, OtpEntity.class);
		String otp = otpData.get(0).toString();
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
			Thread.sleep(120000);
		} catch (InterruptedException e) {
			logger.info(e);
		}
		String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<Object> otpData = prereg_dbread.fetchOTPFromDB(otpQueryStr, OtpEntity.class);
		String otp = otpData.get(0).toString();
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
		lib.compareValues(validateOTPRes.jsonPath().get("errors[0].message").toString(), "Authentication failed");
		lib.compareValues(validateOTPRes.jsonPath().get("errors[0].errorCode").toString(), "PRG_AUTH_002");
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
		String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<Object> otpData = prereg_dbread.fetchOTPFromDB(otpQueryStr, OtpEntity.class);
		String otp = otpData.get(0).toString();
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, userId, "236578");
		for(int i=1;i<=3;i++)
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
		String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<Object> otpData = prereg_dbread.fetchOTPFromDB(otpQueryStr, OtpEntity.class);
		String otp = otpData.get(0).toString();
		JSONObject validateOTPRequest = lib.validateOTPRequest(validateTestSuite, "Ashish", otp);
		Response validateOTP = lib.validateOTP(validateOTPRequest);
		String message = validateOTP.jsonPath().get("errors[0].message").toString();
		lib.compareValues(message, "Authentication failed");
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
