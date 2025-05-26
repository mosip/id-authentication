package io.mosip.testrig.apirig.auth.testscripts;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.testrig.apirig.auth.utils.IdAuthConfigManager;
import io.mosip.testrig.apirig.auth.utils.IdAuthenticationUtil;
import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.HealthChecker;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.AuthenticationTestException;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.OutputValidationUtil;
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.mosip.testrig.apirig.utils.SecurityXSSException;
import io.restassured.response.Response;

public class UpdateIdentity extends IdAuthenticationUtil implements ITest {
	private static final Logger logger = Logger.getLogger(UpdateIdentity.class);
	protected String testCaseName = "";
	private static String identity;

	@BeforeClass
	public static void setLogLevel() {
		if (IdAuthConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	public static void saveIdentityForUpdateIdentityVerification(String id) {
		identity = id;
	}

	public static String getIdentityForUpdateIdentityVerification() {
		return identity;
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
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException, SecurityXSSException {
		testCaseName = testCaseDTO.getTestCaseName();
		testCaseName = IdAuthenticationUtil.isTestCaseValidForExecution(testCaseDTO);
		updateIdentity(testCaseDTO);

	}

	public void updateIdentity(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException, SecurityXSSException {

		testCaseName = testCaseDTO.getTestCaseName();
		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException(
					GlobalConstants.TARGET_ENV_HEALTH_CHECK_FAILED + HealthChecker.healthCheckFailureMapS);
		}

		if (testCaseDTO.getTestCaseName().contains("VID") || testCaseDTO.getTestCaseName().contains("Vid")) {
			if (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
					&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
				throw new SkipException(GlobalConstants.VID_FEATURE_NOT_SUPPORTED);
			}
		}

		JSONObject req = new JSONObject(testCaseDTO.getInput());

		JSONObject otpReqJson = null;
		String otpRequest = null;
		String sendOtpReqTemplate = null;
		String sendOtpEndPoint = null;
		if (req.has(GlobalConstants.SENDOTP)) {
			otpRequest = req.get(GlobalConstants.SENDOTP).toString();
			req.remove(GlobalConstants.SENDOTP);
			otpReqJson = new JSONObject(otpRequest);
			sendOtpReqTemplate = otpReqJson.getString("sendOtpReqTemplate");
			otpReqJson.remove("sendOtpReqTemplate");
			sendOtpEndPoint = otpReqJson.getString("sendOtpEndPoint");
			otpReqJson.remove("sendOtpEndPoint");
			testCaseDTO.setInput(req.toString());

		}
		JSONObject res = new JSONObject(testCaseDTO.getOutput());
		String sendOtpResp = null, sendOtpResTemplate = null;
		if (res.has(GlobalConstants.SENDOTPRESP)) {
			sendOtpResp = res.get(GlobalConstants.SENDOTPRESP).toString();
			res.remove(GlobalConstants.SENDOTPRESP);
			testCaseDTO.setOutput(res.toString());
		}

		DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		String timestampValue = dateFormatter.format(cal.getTime());
		String genRid = "27847" + generateRandomNumberString(10) + timestampValue;
		generatedRid = genRid;

		String inputJson = getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate());

		JSONObject reqJsonObject = new JSONObject(inputJson);

		

		String phone = getValueFromAuthActuator("json-property", "phone_number");
		String result = phone.replaceAll("\\[\"|\"\\]", "");

		String email = getValueFromAuthActuator("json-property", "emailId");
		String emailResult = email.replaceAll("\\[\"|\"\\]", "");

		JSONArray dobArray = new JSONArray(getValueFromAuthActuator("json-property", "dob"));
		String dob = dobArray.getString(0);

		inputJson = inputJson.replace("\"phone\":", "\"" + result + "\":");
		inputJson = inputJson.replace("\"email\":", "\"" + emailResult + "\":");

		inputJson = inputJson.replace("$RID$", genRid);

		if ((testCaseName.startsWith("IdRepository_") || testCaseName.startsWith("Auth_"))
				&& inputJson.contains("dateOfBirth") && (!isElementPresent(globalRequiredFields, dob))) {
			JSONObject reqJson = new JSONObject(inputJson);
			reqJson.getJSONObject("request").getJSONObject("identity").remove("dateOfBirth");
			inputJson = reqJson.toString();
			if (testCaseName.contains("dob"))
				throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);
		}

		if ((testCaseName.startsWith("IdRepository_") || testCaseName.startsWith("Auth_"))
				&& inputJson.contains("email")
				&& (!isElementPresent(globalRequiredFields, emailResult))) {
			JSONObject reqJson = new JSONObject(inputJson);
			reqJson.getJSONObject("request").getJSONObject("identity").remove(emailResult);
			if (reqJson.getJSONObject("request").getJSONObject("identity").has(result)) {
				reqJson.getJSONObject("request").getJSONObject("identity").remove(result);
			}
			if (testCaseName.contains("email") || testCaseName.contains("phonenumber"))
				throw new SkipException(GlobalConstants.FEATURE_NOT_SUPPORTED_MESSAGE);

			inputJson = reqJson.toString();
		}

		if (inputJson.contains("$PRIMARYLANG$"))
			inputJson = inputJson.replace("$PRIMARYLANG$", BaseTestCase.languageList.get(0));

		Response response = patchWithBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(), inputJson, COOKIENAME,
				testCaseDTO.getRole(), testCaseDTO.getTestCaseName());

		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
				response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()),
				testCaseDTO, response.getStatusCode());
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));
		Assert.assertEquals(OutputValidationUtil.publishOutputResult(ouputValid), true);

		if (otpReqJson != null) {
			Response otpResponse = null;
			otpResponse = postRequestWithAuthHeaderAndSignature(ApplnURI + sendOtpEndPoint,
					getJsonFromTemplate(otpReqJson.toString(), sendOtpReqTemplate), testCaseDTO.getTestCaseName());

			JSONObject sendOtpRespJson = new JSONObject(sendOtpResp);
			sendOtpResTemplate = sendOtpRespJson.getString("sendOtpResTemplate");
			sendOtpRespJson.remove("sendOtpResTemplate");
			Map<String, List<OutputValidationDto>> ouputValidOtp = OutputValidationUtil.doJsonOutputValidation(
					otpResponse.asString(), getJsonFromTemplate(sendOtpRespJson.toString(), sendOtpResTemplate),
					testCaseDTO, otpResponse.getStatusCode());
			Reporter.log(ReportUtil.getOutputValidationReport(ouputValidOtp));

			if (!OutputValidationUtil.publishOutputResult(ouputValidOtp))
				throw new AdminTestException("Failed at Send OTP output validation");
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
}
