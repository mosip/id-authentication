package io.mosip.testrig.apirig.testscripts;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
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
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.AuthenticationTestException;
import io.mosip.testrig.apirig.utils.IdAuthConfigManager;
import io.mosip.testrig.apirig.utils.EncryptionDecrptionUtil;
import io.mosip.testrig.apirig.utils.IdAuthenticationUtil;
import io.mosip.testrig.apirig.utils.OutputValidationUtil;
import io.mosip.testrig.apirig.utils.PartnerRegistration;
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.restassured.response.Response;
@Component
public class DemoAuth extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(DemoAuth.class);
	protected String testCaseName = "";
	public Response response = null;
	public boolean isInternal = false;
	
	private EncryptionDecrptionUtil encryptDecryptUtil = new EncryptionDecrptionUtil();

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
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}
	
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {		
		testCaseName = testCaseDTO.getTestCaseName();
		testCaseName = IdAuthenticationUtil.isTestCaseValidForExecution(testCaseDTO);
		
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
		JSONObject request = new JSONObject(testCaseDTO.getInput());
		String identityRequest = null, identityRequestTemplate = null, identityRequestEncUrl = null;
		if(request.has("identityRequest")) {
			identityRequest = request.get("identityRequest").toString();
			request.remove("identityRequest");
		}
		
		if (identityRequest.contains("$PRIMARYLANG$"))
			identityRequest = identityRequest.replace("$PRIMARYLANG$", BaseTestCase.languageList.get(0));
		
		if (identityRequest.contains("name") & testCaseDTO.getTestCaseName().contains("titleFromAdmin")) {
			identityRequest = AdminTestUtil.inputTitleHandler(identityRequest);
		}

		if (identityRequest.contains("$NAMEPRIMARYLANG$")) {
			String name = "";
			if (BaseTestCase.isTargetEnvLTS())
				name = propsMap.getProperty("fullName");
			else
				name = propsMap.getProperty("firstName");
			identityRequest = identityRequest.replace("$NAMEPRIMARYLANG$", name + BaseTestCase.languageList.get(0));
		}
		
		JSONObject identityReqJson = new JSONObject(identityRequest);
		identityRequestTemplate = identityReqJson.getString("identityRequestTemplate");
		identityReqJson.remove("identityRequestTemplate");
		identityRequest = getJsonFromTemplate(identityReqJson.toString(), identityRequestTemplate);
		
		if (BaseTestCase.certsForModule.equals("DSL")) {
			JSONObject jsonObject = new JSONObject(identityRequest);
			JSONObject demographics = jsonObject.getJSONObject("demographics");
			if (!demographics.get("name").toString().equals("[]")) {
				demographics.remove("age");
				identityRequest = jsonObject.toString();
			}
		}
		
		identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, generateCurrentUTCTimeStamp(), "timestamp");
		Map<String, String> demoAuthTempMap = encryptDecryptUtil.getEncryptSessionKeyValue(identityRequest);
		String authRequest = getJsonFromTemplate(request.toString(), testCaseDTO.getInputTemplate());
		logger.info("************* Modification of bio auth request ******************");
		Reporter.log("<b><u>Modification of demo auth request</u></b>");
		authRequest = modifyRequest(authRequest, demoAuthTempMap, getResourcePath()+props.getProperty("idaMappingPath"));
		JSONObject authRequestTemp = new JSONObject(authRequest);
		authRequestTemp.remove("env");
		authRequestTemp.put("env", "Staging");
		authRequest = authRequestTemp.toString();
		testCaseDTO.setInput(authRequest);
		testCaseDTO.setInput(authRequest);
				
		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");		
		
		response = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + testCaseDTO.getEndPoint(), authRequest, COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
		
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
				.doJsonOutputValidation(response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));
		
		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");

		//if(!encryptDecryptUtil.verifyResponseUsingDigitalSignature(response.asString(), response.getHeader(props.getProperty("signatureheaderKey"))))
			//	throw new AdminTestException("Failed at Signature validation");


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
//		if (HealthChecker.signalTerminateExecution) {
//			throw new SkipException(
//					GlobalConstants.TARGET_ENV_HEALTH_CHECK_FAILED + HealthChecker.healthCheckFailureMapS);
//		}
//
//		if (testCaseDTO.getTestCaseName().contains("uin") || testCaseDTO.getTestCaseName().contains("UIN")) {
//			if (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("UIN")
//					&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("uin")) {
//				throw new SkipException(GlobalConstants.UIN_FEATURE_NOT_SUPPORTED);
//			}
//		}
//		if (testCaseDTO.getTestCaseName().contains("VID") || testCaseDTO.getTestCaseName().contains("Vid")) {
//			if (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
//					&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
//				throw new SkipException(GlobalConstants.VID_FEATURE_NOT_SUPPORTED);
//			}
//		}
//
//		if (testCaseDTO.getEndPoint().contains("$partnerKeyURL$")) {
//			testCaseDTO.setEndPoint(
//					testCaseDTO.getEndPoint().replace("$partnerKeyURL$", PartnerRegistration.partnerKeyUrl));
//		}
//		JSONObject request = new JSONObject(testCaseDTO.getInput());
//		String identityRequest = null;
//		String identityRequestTemplate = null;
//		if (request.has(GlobalConstants.IDENTITYREQUEST)) {
//			identityRequest = request.get(GlobalConstants.IDENTITYREQUEST).toString();
//			request.remove(GlobalConstants.IDENTITYREQUEST);
//		}
//
//		if (identityRequest.contains("$PRIMARYLANG$"))
//			identityRequest = identityRequest.replace("$PRIMARYLANG$", BaseTestCase.languageList.get(0));
//
//		JSONObject identityReqJson = new JSONObject(identityRequest);
//		identityRequestTemplate = identityReqJson.getString("identityRequestTemplate");
//		identityReqJson.remove("identityRequestTemplate");
//		identityRequest = getJsonFromTemplate(identityReqJson.toString(), identityRequestTemplate);
//		identityRequest = JsonPrecondtion.parseAndReturnJsonContent(identityRequest, generateCurrentUTCTimeStamp(),
//				"identityRequest.timestamp");
//		Map<String, String> demoAuthTempMap = encryptDecryptUtil.getEncryptSessionKeyValue(identityRequest);
//		String authRequest = getJsonFromTemplate(request.toString(), testCaseDTO.getInputTemplate());
//		logger.info("************* Modification of bio auth request ******************");
//		Reporter.log("<b><u>Modification of demo auth request</u></b>");
//		authRequest = modifyRequest(authRequest, demoAuthTempMap,
//				getResourcePath() + properties.getProperty("idaMappingPath"));
//		JSONObject authRequestTemp = new JSONObject(authRequest);
//		authRequestTemp.remove("env");
//		authRequestTemp.put("env", "Staging");
//		authRequest = authRequestTemp.toString();
//		testCaseDTO.setInput(authRequest);
//
//		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");
//
//		response = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + testCaseDTO.getEndPoint(), authRequest,
//				COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
//
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
