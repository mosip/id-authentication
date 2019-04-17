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

import com.google.gson.JsonObject;

import io.mosip.dbaccess.prereg_dbread;
import io.mosip.dbentity.OtpEntity;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class SendOtp extends BaseTestCase implements ITest {
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
	 * Scripting for send OTP API
	 */
	@Test
	public void sendOtpToEmailId() {
		testSuite = "SendOtp/SendOtpToEmail";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<Object> otpData = prereg_dbread.fetchOTPFromDB(otpQueryStr, OtpEntity.class);
		String otp = otpData.get(0).toString();
		lib.compareValues(response.jsonPath().get("response.message").toString(), "Email Request submitted");
	}
	@Test
	public void sendOtpToMobile() {
		testSuite = "SendOtp/SendOtpMobile";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		response = lib.generateOTP(sendOtpRequest);
		String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		List<Object> otpData = prereg_dbread.fetchOTPFromDB(otpQueryStr, OtpEntity.class);
		String otp = otpData.get(0).toString();
		lib.compareValues(response.jsonPath().get("response.message").toString(), "Sms Request Sent");
	}
	@Test
	public void sendOtpToInvalidEmailId() {
		testSuite = "SendOtp/SendOtpToInvalid_Email_Id";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		Response generateOTPResponse = lib.generateOTP(sendOtpRequest);
		String errorCode=generateOTPResponse.jsonPath().get("errors[0].errorCode").toString();
		String message=generateOTPResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "PRG_PAM_LGN_008");
		lib.compareValues(message, "Invlaid Request userId recieved");
	}
	@Test
	public void sendOtpToInvalidMobileNo() {
		testSuite = "SendOtp/SendOtpToInvalidMobileNo";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		Response generateOTPResponse = lib.generateOTP(sendOtpRequest);
		String errorCode=generateOTPResponse.jsonPath().get("errors[0].errorCode").toString();
		String message=generateOTPResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "PRG_PAM_LGN_008");
		lib.compareValues(message, "Invlaid Request userId recieved");
	}
	@Test
	public void sendOtpWithoutGivingUserId() {
		testSuite = "SendOtp/sendOtpWithoutGivingUserId";
		JSONObject sendOtpRequest = lib.getOtpRequest(testSuite);
		Map request = (Map) sendOtpRequest.get("request");
		String userId = request.get("userId").toString();
		Response generateOTPResponse = lib.generateOTP(sendOtpRequest);
		String errorCode=generateOTPResponse.jsonPath().get("errors[0].errorCode").toString();
		String message=generateOTPResponse.jsonPath().get("errors[0].message").toString();
		lib.compareValues(errorCode, "PRG_PAM_LGN_008");
		lib.compareValues(message, "Invlaid Request userId recieved");
	}
	@Test
	public void sendOtpToBlockedUser() {
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
		Response generateOTP  = lib.generateOTP(sendOtpRequest);
		String message = generateOTP.jsonPath().get("response.message").toString();
		lib.compareValues(message, "USER_BLOCKED");
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
