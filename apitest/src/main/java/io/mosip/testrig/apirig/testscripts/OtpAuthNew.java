package io.mosip.testrig.apirig.testscripts;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.HealthChecker;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.AuthenticationTestException;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.OutputValidationUtil;
import io.mosip.testrig.apirig.utils.PartnerRegistration;
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.restassured.response.Response;

public class OtpAuthNew extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(OtpAuthNew.class);
	protected String testCaseName = "";
	public Response response = null;
	public boolean isInternal = false;

	@BeforeClass
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	/**
	 * get current testcaseName
	 */
	@Override
	public String getTestName() {
		return testCaseName;
	}

	/**
	 * Data provider class provides test case list
	 * 
	 * @return object of data provider
	 */
	@DataProvider(name = "testcaselist")
	public Object[] getTestCaseList(ITestContext context) {
		String ymlFile = context.getCurrentXmlTest().getLocalParameters().get("ymlFile");
		isInternal = Boolean.parseBoolean(context.getCurrentXmlTest().getLocalParameters().get("isInternal"));
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}

	/**
	 * Test method for OTP Generation execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws AuthenticationTestException
	 * @throws AdminTestException
	 */
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {
		testCaseName = testCaseDTO.getTestCaseName();

		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException(
					GlobalConstants.TARGET_ENV_HEALTH_CHECK_FAILED + HealthChecker.healthCheckFailureMapS);
		}

		if (testCaseDTO.getTestCaseName().contains("uin") || testCaseDTO.getTestCaseName().contains("UIN")) {
			if (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("UIN")
					&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("uin")) {
				throw new SkipException(GlobalConstants.UIN_FEATURE_NOT_SUPPORTED);
			}
		}

		if (testCaseDTO.getTestCaseName().contains("VID") || testCaseDTO.getTestCaseName().contains("Vid")) {
			if (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
					&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
				throw new SkipException(GlobalConstants.VID_FEATURE_NOT_SUPPORTED);
			}
		}

		testCaseName = isTestCaseValidForExecution(testCaseDTO);

		if (testCaseDTO.getEndPoint().contains("$PartnerKeyURL$")) {
			testCaseDTO.setEndPoint(
					testCaseDTO.getEndPoint().replace("$PartnerKeyURL$", PartnerRegistration.partnerKeyUrl));
		}

		if (testCaseDTO.getEndPoint().contains("$KycPartnerKeyURL$")) {
			testCaseDTO.setEndPoint(
					testCaseDTO.getEndPoint().replace("$KycPartnerKeyURL$", PartnerRegistration.ekycPartnerKeyUrl));
		}

		if (testCaseDTO.getEndPoint().contains("$UpdatedPartnerKeyURL$")) {
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$UpdatedPartnerKeyURL$",
					PartnerRegistration.updatedpartnerKeyUrl));
		}

		if (testCaseDTO.getEndPoint().contains("$PartnerName$")) {
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$PartnerName$", PartnerRegistration.partnerId));
		}

		if (testCaseDTO.getEndPoint().contains("$KycPartnerName$")) {
			testCaseDTO.setEndPoint(
					testCaseDTO.getEndPoint().replace("$KycPartnerName$", PartnerRegistration.ekycPartnerId));
		}

		JSONObject input = new JSONObject(testCaseDTO.getInput());
		String individualId = null;
		if (input.has(GlobalConstants.INDIVIDUALID)) {
			individualId = input.get(GlobalConstants.INDIVIDUALID).toString();
			input.remove(GlobalConstants.INDIVIDUALID);
		}

		individualId = uriKeyWordHandelerUri(individualId, testCaseName);

		String url = ConfigManager.getAuthDemoServiceUrl();

		HashMap<String, String> requestBody = new HashMap<>();

		requestBody.put("id", individualId);
		requestBody.put("keyFileNameByPartnerName", GlobalConstants.TRUE_STRING);
		requestBody.put("partnerName", PartnerRegistration.partnerId);
		requestBody.put("moduleName", BaseTestCase.certsForModule);
		requestBody.put(GlobalConstants.TRANSACTIONID, "$TRANSACTIONID$");

		String token = kernelAuthLib.getTokenByRole(GlobalConstants.RESIDENT);

		Response sendOtpReqResp = postWithOnlyQueryParamAndCookie(url + "/v1/identity/createOtpReqest",
				requestBody.toString(), GlobalConstants.AUTHORIZATION, GlobalConstants.RESIDENT, testCaseName);

		logger.info(sendOtpReqResp);

		String otpInput = sendOtpReqResp.getBody().asString();
		logger.info(otpInput);
		String signature = sendOtpReqResp.getHeader("signature");
		Object sendOtpBody = otpInput;
		logger.info(sendOtpBody);

		HashMap<String, String> headers = new HashMap<>();
		headers.put(AUTHORIZATHION_HEADERNAME, token);
		headers.put(SIGNATURE_HEADERNAME, signature);

		Response otpRespon = null;

		if (testCaseDTO.getTestCaseName().contains("EkycOtp")) {
			otpRespon = postRequestWithAuthHeaderAndSignatureForOtp(
					ApplnURI + "/idauthentication/v1/otp/" + PartnerRegistration.ekycPartnerKeyUrl,
					sendOtpBody.toString(), GlobalConstants.AUTHORIZATION, token, headers, testCaseName);
		} else {

			otpRespon = postRequestWithAuthHeaderAndSignatureForOtp(
					ApplnURI + "/idauthentication/v1/otp/" + PartnerRegistration.partnerKeyUrl, sendOtpBody.toString(),
					GlobalConstants.AUTHORIZATION, token, headers, testCaseName);

		}

		JSONObject res = new JSONObject(testCaseDTO.getOutput());
		String sendOtpResp = null;
		String sendOtpResTemplate = null;
		if (res.has(GlobalConstants.SENDOTPRESP)) {
			sendOtpResp = res.get(GlobalConstants.SENDOTPRESP).toString();
			res.remove(GlobalConstants.SENDOTPRESP);
		}
		JSONObject sendOtpRespJson = new JSONObject(sendOtpResp);
		sendOtpResTemplate = sendOtpRespJson.getString("sendOtpResTemplate");
		sendOtpRespJson.remove("sendOtpResTemplate");
		Map<String, List<OutputValidationDto>> ouputValidOtp = OutputValidationUtil.doJsonOutputValidation(
				otpRespon.asString(), getJsonFromTemplate(sendOtpRespJson.toString(), sendOtpResTemplate), testCaseDTO,
				otpRespon.getStatusCode());
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValidOtp));

		if (!OutputValidationUtil.publishOutputResult(ouputValidOtp))
			throw new AdminTestException("Failed at Send OTP output validation");

		String endPoint = testCaseDTO.getEndPoint();
		endPoint = uriKeyWordHandelerUri(endPoint, testCaseName);

		if (endPoint.contains("$partnerKeyURL$")) {
			endPoint = endPoint.replace("$partnerKeyURL$", PartnerRegistration.partnerKeyUrl);
		}
		if (endPoint.contains("$PartnerName$")) {
			endPoint = endPoint.replace("$PartnerName$", PartnerRegistration.partnerId);
		}

		String authRequest = "";

		if (!(BaseTestCase.certsForModule.equals("DSL-IDA"))) {
			authRequest = getJsonFromTemplate(input.toString(), testCaseDTO.getInputTemplate());
		} else {
			authRequest = input.toString();
		}

		logger.info("******Post request Json to EndPointUrl: " + url + endPoint + " *******");

		response = postWithBodyAndCookie(url + endPoint, authRequest, COOKIENAME, testCaseDTO.getRole(), testCaseName);

		String ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());

		if (testCaseDTO.getTestCaseName().contains("uin") || testCaseDTO.getTestCaseName().contains("UIN")) {
			if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("UIN")
					|| BaseTestCase.getSupportedIdTypesValueFromActuator().contains("uin")) {
				ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
			} else {
				if (testCaseDTO.getTestCaseName().contains("auth_EkycOtp")) {
					ActualOPJson = AdminTestUtil.getRequestJson("config/errorUINKyc.json").toString();
				} else {
					ActualOPJson = AdminTestUtil.getRequestJson("config/errorUIN.json").toString();
				}

			}
		} else {
			if (testCaseDTO.getTestCaseName().contains("VID") || testCaseDTO.getTestCaseName().contains("Vid")) {
				if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
						|| BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
					ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
				} else {
					if (testCaseDTO.getTestCaseName().contains("auth_EkycOtp")) {
						ActualOPJson = AdminTestUtil.getRequestJson("config/errorUINKyc.json").toString();
					} else {
						ActualOPJson = AdminTestUtil.getRequestJson("config/errorUIN.json").toString();
					}

				}
			}
		}
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
				.doJsonOutputValidation(response.asString(), ActualOPJson, testCaseDTO, response.getStatusCode());
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));

		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");

		if (testCaseName.toLowerCase().contains("kyc")) {
			JSONObject resJsonObject = new JSONObject(response.asString());
			String resp = "";
			try {
				resp = resJsonObject.get("response").toString();
			} catch (JSONException e) {
				logger.error(e.getMessage());
			}
		}

	}

	/**
	 * The method ser current test name to result
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@AfterClass
	public static void authTestTearDown() {
		logger.info("Terminating authpartner demo application...");
	}
}
