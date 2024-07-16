package io.mosip.testrig.apirig.testscripts;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import io.mosip.testrig.apirig.testrunner.HealthChecker;
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.AuthenticationTestException;
import io.mosip.testrig.apirig.utils.BioDataUtility;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.EncryptionDecrptionUtil;
import io.mosip.testrig.apirig.utils.OutputValidationUtil;
import io.mosip.testrig.apirig.utils.PartnerRegistration;
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.restassured.response.Response;
@Component
public class MultiFactorAuthNew extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(MultiFactorAuthNew.class);
	protected String testCaseName = "";
	public Response response = null;
	
	@Autowired
	private EncryptionDecrptionUtil encryptDecryptUtil;
	
	@Autowired
	private BioDataUtility bioDataUtil;

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
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}
	
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {
		testCaseName = testCaseDTO.getTestCaseName();
		//String ekycPartnerKeyUrl = PartnerRegistration.mispLicKey + "/" + PartnerRegistration.partnerId + "/" + PartnerRegistration.apiKey;
		
		String partnerKeyUrl = PartnerRegistration.mispLicKey + "/" + PartnerRegistration.partnerId + "/" + PartnerRegistration.apiKey;
		String ekycPartnerKeyURL = PartnerRegistration.mispLicKey + "/" + PartnerRegistration.ekycPartnerId + "/" + PartnerRegistration.kycApiKey;
		
		if(testCaseDTO.getEndPoint().contains("$partnerKeyURL$"))
			
		{
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$partnerKeyURL$", partnerKeyUrl));
			PartnerRegistration.appendEkycOrRp = "rp-";
		}
		if(testCaseDTO.getEndPoint().contains("$ekycPartnerKeyURL$"))
		{
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$ekycPartnerKeyURL$", ekycPartnerKeyURL));
			PartnerRegistration.appendEkycOrRp = "ekyc-";
		}
		if (testCaseDTO.getEndPoint().contains("$UpdatedPartnerKeyURL$")) {
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$UpdatedPartnerKeyURL$",
					PartnerRegistration.updatedpartnerKeyUrl));
		}
		
		
		JSONObject req = new JSONObject(testCaseDTO.getInput());
//		if(testCaseDTO.getEndPoint().contains("$partnerKeyURL$"))
//		{
//			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$partnerKeyURL$", ekycPartnerKeyUrl));
//		}
		String otpRequest = null, sendOtpReqTemplate = null, sendOtpEndPoint = null, otpIdentyEnryptRequestPath = null;
		if(req.has("sendOtp")) {
			otpRequest = req.get("sendOtp").toString();
			req.remove("sendOtp");
		}
		JSONObject otpReqJson = new JSONObject(otpRequest);
		sendOtpReqTemplate = otpReqJson.getString("sendOtpReqTemplate");
		otpReqJson.remove("sendOtpReqTemplate");
		sendOtpEndPoint = otpReqJson.getString("sendOtpEndPoint");
		
		
		otpReqJson.remove("sendOtpEndPoint");
		
//		if(sendOtpEndPoint.contains("$partnerKeyURL$"))
//		{
//			sendOtpEndPoint = sendOtpEndPoint.replace("$partnerKeyURL$", ekycPartnerKeyURL);
//		}
		if(sendOtpEndPoint.contains("$partnerKeyURL$"))
		{
			sendOtpEndPoint = sendOtpEndPoint.replace("$partnerKeyURL$", partnerKeyUrl);
		}
		if(sendOtpEndPoint.contains("$ekycPartnerKeyURL$"))
		{
			sendOtpEndPoint = sendOtpEndPoint.replace("$partnerKeyURL$", ekycPartnerKeyURL);
		}
		Response otpResponse = postRequestWithAuthHeaderAndSignature(ApplnURI + sendOtpEndPoint,
				getJsonFromTemplate(otpReqJson.toString(), sendOtpReqTemplate), testCaseDTO.getTestCaseName());

		JSONObject res = new JSONObject(testCaseDTO.getOutput());
		String sendOtpResp = null, sendOtpResTemplate = null;
		if(res.has("sendOtpResp")) {
			sendOtpResp = res.get("sendOtpResp").toString();
			res.remove("sendOtpResp");
		}
		JSONObject sendOtpRespJson = new JSONObject(sendOtpResp);
		sendOtpResTemplate = sendOtpRespJson.getString("sendOtpResTemplate");
		sendOtpRespJson.remove("sendOtpResTemplate");
		Map<String, List<OutputValidationDto>> ouputValidOtp = OutputValidationUtil.doJsonOutputValidation(
				otpResponse.asString(), getJsonFromTemplate(sendOtpRespJson.toString(), sendOtpResTemplate),
				testCaseDTO, otpResponse.getStatusCode());
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValidOtp));
		OutputValidationUtil.publishOutputResult(ouputValidOtp);
		//if (!OutputValidationUtil.publishOutputResult(ouputValidOtp))
			//throw new AdminTestException("Failed at Send OTP output validation");
		
		//String id = getAutoGeneratedFieldValue(otpRequest, testCaseName);
		
		String identityRequest = null, identityRequestTemplate = null, identityRequestEncUrl = null;
		if(req.has("identityRequest")) {
			identityRequest = req.get("identityRequest").toString();
			req.remove("identityRequest");
		}
		identityRequest = buildIdentityRequest(identityRequest);
		
		if(identityRequest.contains("$DATETIME$"))
			identityRequest = identityRequest.replace("$DATETIME$", generateCurrentUTCTimeStamp());
		JSONObject identityReqJson = new JSONObject(identityRequest);
		identityRequestTemplate = identityReqJson.getString("identityRequestTemplate");
		identityReqJson.remove("identityRequestTemplate");
		identityRequestEncUrl = identityReqJson.getString("identityRequestEncUrl");
		identityReqJson.remove("identityRequestEncUrl");
		identityRequest = getJsonFromTemplate(identityReqJson.toString(), identityRequestTemplate);
		String identyEnryptRequest = updateTimestampOtp(identityRequest);
		String encryptedIdentityReq=null;
		try {
			encryptedIdentityReq = bioDataUtil.constractBioIdentityRequest(identyEnryptRequest, getResourcePath()+props.getProperty("bioValueEncryptionTemplate"), testCaseName, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (Arrays.asList(testCaseDTO.getTestCaseName().split("_")).contains("MultiFactorAuth")) {
			String demographicsMapper = "(demographics)";
			JSONObject jsonObject = new JSONObject(identityReqJson.toString());
			JSONObject jsonBioHbs = new JSONObject(encryptedIdentityReq);
			if (jsonObject.has("key") && jsonObject.has("value")) {
				JSONObject jsonHbs = new JSONObject(jsonBioHbs.toString()); // TO DO
				encryptedIdentityReq = JsonPrecondtion.parseAndReturnJsonContent(encryptedIdentityReq.toString(),
						jsonObject.get("value").toString(), demographicsMapper + jsonObject.get("key").toString());
			}

		}
		Map<String, String> bioAuthTempMap = encryptDecryptUtil.getEncryptSessionKeyValue(encryptedIdentityReq);
		String authRequest = getJsonFromTemplate(req.toString(), testCaseDTO.getInputTemplate());
		logger.info("************* Modification of OTP auth request ******************");
		Reporter.log("<b><u>Modification of otp auth request</u></b>");
		authRequest = modifyRequest(authRequest, bioAuthTempMap, getResourcePath()+props.getProperty("idaMappingPath"));
		JSONObject authRequestTemp = new JSONObject(authRequest);
		authRequestTemp.remove("env");
		authRequestTemp.put("env", "Staging");
		authRequest = authRequestTemp.toString();
		testCaseDTO.setInput(authRequest);
				
		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");		
		
		response = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + testCaseDTO.getEndPoint(), authRequest, COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
				.doJsonOutputValidation(response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));
		
		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");
		
		/*
		 * if(testCaseName.toLowerCase().contains("kyc"))
		 * encryptDecryptUtil.validateThumbPrintAndIdentity(response,
		 * testCaseDTO.getEndPoint());
		 */
		
		//if(!encryptDecryptUtil.verifyResponseUsingDigitalSignature(response.asString(), response.getHeader(props.getProperty("signatureheaderKey"))))
			//throw new AdminTestException("Failed at Signature validation");

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
//	@Test(dataProvider = "testcaselist")
//	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {
//		testCaseName = testCaseDTO.getTestCaseName();
//
//		if (HealthChecker.signalTerminateExecution) {
//			throw new SkipException(
//					GlobalConstants.TARGET_ENV_HEALTH_CHECK_FAILED + HealthChecker.healthCheckFailureMapS);
//		}
//
//		testCaseName = isTestCaseValidForExecution(testCaseDTO);
//
//		if (testCaseDTO.getTestCaseName().contains("uin") || testCaseDTO.getTestCaseName().contains("UIN")) {
//			if (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("UIN")
//					&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("uin")) {
//				throw new SkipException(GlobalConstants.UIN_FEATURE_NOT_SUPPORTED);
//			}
//		}
//
//		if (testCaseDTO.getTestCaseName().contains("VID") || testCaseDTO.getTestCaseName().contains("Vid")) {
//			if (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
//					&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
//				throw new SkipException(GlobalConstants.VID_FEATURE_NOT_SUPPORTED);
//			}
//		}
//
//		JSONObject input = new JSONObject(testCaseDTO.getInput());
//		String individualId = null;
//		if (input.has(GlobalConstants.INDIVIDUALID)) {
//			individualId = input.get(GlobalConstants.INDIVIDUALID).toString();
//			input.remove(GlobalConstants.INDIVIDUALID);
//		}
//
//		individualId = uriKeyWordHandelerUri(individualId, testCaseName);
//
//		String url = ConfigManager.getAuthDemoServiceUrl();
//
//		HashMap<String, String> requestBody = new HashMap<>();
//
//		requestBody.put("id", individualId);
//		requestBody.put("keyFileNameByPartnerName", GlobalConstants.TRUE_STRING);
//		requestBody.put("partnerName", PartnerRegistration.partnerId);
//		requestBody.put("moduleName", BaseTestCase.certsForModule);
//		requestBody.put(GlobalConstants.TRANSACTIONID, "$TRANSACTIONID$");
//
//		String token = kernelAuthLib.getTokenByRole(GlobalConstants.RESIDENT);
//
//		if (input.has("otp") && !input.get("otp").toString().equalsIgnoreCase("otp")) {
//
//			Response sendOtpReqResp = postWithOnlyQueryParamAndCookie(url + "/v1/identity/createOtpReqest",
//					requestBody.toString(), GlobalConstants.AUTHORIZATION, GlobalConstants.RESIDENT, testCaseName);
//
//			String otpInput = sendOtpReqResp.getBody().asString();
//			logger.info(otpInput);
//			String signature = sendOtpReqResp.getHeader("signature");
//			Object sendOtpBody = otpInput;
//			logger.info(sendOtpBody);
//
//			HashMap<String, String> headers = new HashMap<>();
//			headers.put(AUTHORIZATHION_HEADERNAME, token);
//			headers.put(SIGNATURE_HEADERNAME, signature);
//
//			Response otpRespon = null;
//
//			otpRespon = postRequestWithAuthHeaderAndSignatureForOtp(
//					ApplnURI + "/idauthentication/v1/otp/" + PartnerRegistration.partnerKeyUrl, sendOtpBody.toString(),
//					GlobalConstants.AUTHORIZATION, token, headers, testCaseName);
//
//			JSONObject res = new JSONObject(testCaseDTO.getOutput());
//			String sendOtpResp = null;
//			String sendOtpResTemplate = null;
//			if (res.has(GlobalConstants.SENDOTPRESP)) {
//				sendOtpResp = res.get(GlobalConstants.SENDOTPRESP).toString();
//				res.remove(GlobalConstants.SENDOTPRESP);
//			}
//			JSONObject sendOtpRespJson = new JSONObject(sendOtpResp);
//			sendOtpResTemplate = sendOtpRespJson.getString("sendOtpResTemplate");
//			sendOtpRespJson.remove("sendOtpResTemplate");
//			Map<String, List<OutputValidationDto>> ouputValidOtp = OutputValidationUtil.doJsonOutputValidation(
//					otpRespon.asString(), getJsonFromTemplate(sendOtpRespJson.toString(), sendOtpResTemplate),
//					testCaseDTO, otpRespon.getStatusCode());
//			Reporter.log(ReportUtil.getOutputValidationReport(ouputValidOtp));
//
//			if (!OutputValidationUtil.publishOutputResult(ouputValidOtp))
//				throw new AdminTestException("Failed at Send OTP output validation");
//
//			if (testCaseDTO.getTestCaseName().contains("expiredOTP")) {
//				try {
//					Thread.sleep(Long.parseLong(properties.getProperty("expireOtpTime")));
//				} catch (NumberFormatException e) {
//					logger.error("Exception : " + e.getMessage());
//				} catch (InterruptedException e) {
//					logger.error("Exception : " + e.getMessage());
//				}
//			}
//
//		}
//
//		String endPoint = testCaseDTO.getEndPoint();
//		endPoint = uriKeyWordHandelerUri(endPoint, testCaseName);
//
//		if (endPoint.contains("$partnerKeyURL$")) {
//			endPoint = endPoint.replace("$partnerKeyURL$", PartnerRegistration.partnerKeyUrl);
//		}
//		if (endPoint.contains("$PartnerName$")) {
//			endPoint = endPoint.replace("$PartnerName$", PartnerRegistration.partnerId);
//		}
//
//		if (endPoint.contains("$UpdatedPartnerKeyURL$")) {
//			endPoint = endPoint.replace("$UpdatedPartnerKeyURL$", PartnerRegistration.updatedpartnerKeyUrl);
//		}
//
//		String inputStr = buildIdentityRequest(input.toString());
//
//		String authRequest = getJsonFromTemplate(inputStr, testCaseDTO.getInputTemplate());
//		logger.info("******Post request Json to EndPointUrl: " + url + endPoint + " *******");
//
//		response = postWithBodyAndCookie(url + endPoint, authRequest, COOKIENAME, testCaseDTO.getRole(), testCaseName);
//
//		logger.info(response);
//		String ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
//
//		if (testCaseDTO.getTestCaseName().contains("uin") || testCaseDTO.getTestCaseName().contains("UIN")) {
//			if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("UIN")
//					|| BaseTestCase.getSupportedIdTypesValueFromActuator().contains("uin")) {
//				ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
//			} else {
//				ActualOPJson = AdminTestUtil.getRequestJson("config/errorUIN.json").toString();
//			}
//		} else {
//			if (testCaseDTO.getTestCaseName().contains("VID") || testCaseDTO.getTestCaseName().contains("Vid")) {
//				if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
//						|| BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
//					ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
//				} else {
//					ActualOPJson = AdminTestUtil.getRequestJson("config/errorUIN.json").toString();
//				}
//			}
//		}
//
//		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
//				.doJsonOutputValidation(response.asString(), ActualOPJson, testCaseDTO, response.getStatusCode());
//		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));
//
//		if (!OutputValidationUtil.publishOutputResult(ouputValid))
//			throw new AdminTestException("Failed at output validation");
//
//	}

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
