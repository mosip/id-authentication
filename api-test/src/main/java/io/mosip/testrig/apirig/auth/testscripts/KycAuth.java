package io.mosip.testrig.apirig.auth.testscripts;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.testrig.apirig.auth.utils.IDAConstants;
import io.mosip.testrig.apirig.auth.utils.IdAuthConfigManager;
import io.mosip.testrig.apirig.auth.utils.IdAuthenticationUtil;
import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.HealthChecker;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AuthTestsUtil;
import io.mosip.testrig.apirig.utils.AuthenticationTestException;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.OutputValidationUtil;
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.mosip.testrig.apirig.utils.SecurityXSSException;
import io.restassured.response.Response;

public class KycAuth extends IdAuthenticationUtil implements ITest {
	private static final Logger logger = Logger.getLogger(KycAuth.class);
	protected String testCaseName = "";
	public Response response = null;
	public boolean isInternal = false;

	@BeforeClass
	public static void setLogLevel() {
		if (IdAuthConfigManager.IsDebugEnabled())
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
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException, JsonMappingException, JsonProcessingException, SecurityXSSException {
		testCaseName = testCaseDTO.getTestCaseName();
		testCaseName = IdAuthenticationUtil.isTestCaseValidForExecution(testCaseDTO);
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
		JSONObject request = new JSONObject(testCaseDTO.getInput());
		
		logger.info("******request: " + request);
		
		String createAuthReqJson = null;
		if (request.has(IDAConstants.CREATE_AUTH_REQUEST_STRING)) {
			createAuthReqJson = request.getJSONObject(IDAConstants.CREATE_AUTH_REQUEST_STRING).toString();
			request.remove(IDAConstants.CREATE_AUTH_REQUEST_STRING);
		}
		
		
		createAuthReqJson = IdAuthenticationUtil.inputStringKeyWordHandeler(createAuthReqJson, testCaseName);
		
		String reqString = getJsonFromTemplate(request.toString(), testCaseDTO.getInputTemplate());
		
		reqString = IdAuthenticationUtil.inputStringKeyWordHandeler(reqString, testCaseName);
		
		
		
		reqString = buildIdentityRequest(reqString);
		
		ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = mapper.readValue(reqString, HashMap.class);

        logger.info("map = " + map);  
		
        logger.info("reqString = " + reqString);
		logger.info("******createAuthReqJson: " + createAuthReqJson);
		
//		Map<String, Object> authRequestMap = new HashMap<String, Object>();

		String authRequestString = "";
		try {
			authRequestString = AuthTestsUtil.createAuthRequest(
					replaceIdWithAutogeneratedId("$ID:AddIdentity_KycAuth_smoke_Pos_UIN$", "$ID:"), "UIN", true, false,
					"bio", TRANSACTION_ID, generateCurrentUTCTimeStamp(), false, false, true,
					replaceIdWithAutogeneratedId("$ID:PartnerSelfRegistration_Misp_Valid_Smoke_sid_partnerId$", "$ID:"),
					true, map, null, BaseTestCase.certsForModule);
		} catch (Exception e) {
			logger.error("Failed while creating auth request" + e.getMessage());
		}
		
		logger.info("authRequestString = " + authRequestString);
		
		JSONObject authReqJson = new JSONObject(authRequestString);
		String signatureHeader = null;
		if (authReqJson.has(IDAConstants.SIGNATURE_HEADER_STRING)) {
			signatureHeader = authReqJson.get(IDAConstants.SIGNATURE_HEADER_STRING).toString();
			authReqJson.remove(IDAConstants.SIGNATURE_HEADER_STRING);
		}
		
		
		String kycAuthEndPoint = "";
		kycAuthEndPoint = testCaseDTO.getEndPoint();
//		if (request.has(GlobalConstants.KYCAUTHENDPOINT)) {
//			kycAuthEndPoint = request.get(GlobalConstants.KYCAUTHENDPOINT).toString();
//			request.remove(GlobalConstants.KYCAUTHENDPOINT);
//		}
//
//		String requestString = buildIdentityRequest(request.toString());
//
//		String input = getJsonFromTemplate(requestString, testCaseDTO.getInputTemplate());
//
//		String url = "";
//
//		logger.info("******Post request Json to EndPointUrl: " + url + testCaseDTO.getEndPoint() + " *******");
//
//		Response authResponse = null;
//		
//		input = IdAuthenticationUtil.inputStringKeyWordHandeler(input, testCaseName);
//
//		authResponse = postWithBodyAndCookieWithText(url + testCaseDTO.getEndPoint(), input, COOKIENAME,
//				testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
//
//		String signature = authResponse.getHeader("signature");
//
//		logger.info(signature);
//
//		String authResBody = authResponse.getBody().asString();
//
//		logger.info(authResBody);
//
//		JSONObject responseBody = new JSONObject(authResponse.getBody().asString());
//
//		String requestJson = null;

		HashMap<String, String> headers = new HashMap<>();
		headers.put(SIGNATURE_HEADERNAME, signatureHeader);
		String token = kernelAuthLib.getTokenByRole(testCaseDTO.getRole());
		headers.put(COOKIENAME, token);

		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");
		
//		authResBody = IdAuthenticationUtil.inputStringKeyWordHandeler(authResBody, testCaseName);
		
		kycAuthEndPoint = IdAuthenticationUtil.inputStringKeyWordHandeler(kycAuthEndPoint, testCaseName);

		response = postRequestWithAuthHeaderAndSignatureForOtp(ApplnURI + kycAuthEndPoint, authReqJson.toString(), COOKIENAME,
				token, headers, testCaseDTO.getTestCaseName());

		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
				response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()),
				testCaseDTO, response.getStatusCode());
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));

		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");

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
		return;
	}
}
