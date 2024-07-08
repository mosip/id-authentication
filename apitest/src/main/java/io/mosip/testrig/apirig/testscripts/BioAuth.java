package io.mosip.testrig.apirig.testscripts;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BioAuth extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(BioAuth.class);
	protected String testCaseName = "";
	public Response response = null;
	public Response newResponse = null;
	public boolean isInternal = false;
	
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
		isInternal = Boolean.parseBoolean(context.getCurrentXmlTest().getLocalParameters().get("isInternal"));
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {
		testCaseName = testCaseDTO.getTestCaseName();
//		String ekycPartnerId = getPartnerIdFromPartnerURL(ekycPartnerKeyUrl);
//		String mispLicKey ="";
//		String kycApiKey = KeyCloakUserAndAPIKeyGeneration.createKCUserAndGetAPIKeyForKyc();
//		mispLicKey = MispPartnerAndLicenseKeyGeneration.getAndUploadCertificatesAndGenerateMispLicKey();
		
	
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
		if (request.has("identityRequest")) {
			identityRequest = request.get("identityRequest").toString();
			request.remove("identityRequest");
		}
		identityRequest = buildIdentityRequest(identityRequest);
		
		JSONObject identityReqJson = new JSONObject(identityRequest);
		identityRequestTemplate = identityReqJson.getString("identityRequestTemplate");
		identityReqJson.remove("identityRequestTemplate");
		identityRequestEncUrl = identityReqJson.getString("identityRequestEncUrl");
		identityReqJson.remove("identityRequestEncUrl");
		identityRequest = getJsonFromTemplate(identityReqJson.toString(), identityRequestTemplate);

		String encryptedIdentityReq = null;
		try {
			encryptedIdentityReq = bioDataUtil.constractBioIdentityRequest(identityRequest,
					getResourcePath() + props.getProperty("bioValueEncryptionTemplate"), testCaseName, isInternal);
			System.out.println("encryptedIdentityReq = " + encryptedIdentityReq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, String> bioAuthTempMap = (isInternal)
				? encryptDecryptUtil.getInternalEncryptSessionKeyValue(encryptedIdentityReq)
				: encryptDecryptUtil.getEncryptSessionKeyValue(encryptedIdentityReq);
		// storeValue(bioAuthTempMap);
		String authRequest = getJsonFromTemplate(request.toString(), testCaseDTO.getInputTemplate());
		logger.info("************* Modification of bio auth request ******************");
		Reporter.log("<b><u>Modification of bio auth request</u></b>");
		
		logger.info("authRequest is = " + authRequest);
		logger.info("bioAuthTempMap is = " + bioAuthTempMap);
		authRequest = modifyRequest(authRequest, bioAuthTempMap,
				getResourcePath() + props.getProperty("idaMappingPath"));
		logger.info("authRequestTemp is = " + authRequest);
		JSONObject authRequestTemp = new JSONObject(authRequest);
		authRequestTemp.remove("env");
		authRequestTemp.put("env", "Staging");
		authRequest = authRequestTemp.toString();
		testCaseDTO.setInput(authRequest);
		// storeValue(authRequest,"authRequest");

		logger.info("******Post request Json to EndPointUrl: " + ApplnURI + testCaseDTO.getEndPoint() + " *******");

		response = postRequestWithCookieAuthHeaderAndSignature(ApplnURI + testCaseDTO.getEndPoint(), authRequest,
				COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName());

		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
				response.asString(), getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate()));
		Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));

		if (!OutputValidationUtil.publishOutputResult(ouputValid))
			throw new AdminTestException("Failed at output validation");

		
		/*
		 * if (testCaseName.toLowerCase().contains("kyc")) { String error = null; if
		 * (response.getBody().asString().contains("errors")) error =
		 * JsonPrecondtion.getJsonValueFromJson(response.getBody().asString(),
		 * "errors"); if (error.equalsIgnoreCase("null"))
		 * encryptDecryptUtil.validateThumbPrintAndIdentity(response,
		 * testCaseDTO.getEndPoint()); }
		 */
		 

		/*
		 * if
		 * (!encryptDecryptUtil.verifyResponseUsingDigitalSignature(response.asString(),
		 * response.getHeader(props.getProperty("signatureheaderKey")))) throw new
		 * AdminTestException("Failed at Signature validation");
		 */

	}



//	/**
//	 * Test method for OTP Generation execution
//	 * 
//	 * @param objTestParameters
//	 * @param testScenario
//	 * @param testcaseName
//	 * @throws AuthenticationTestException
//	 * @throws AdminTestException
//	 */
//	@Test(dataProvider = "testcaselist")
//	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {
//		testCaseName = testCaseDTO.getTestCaseName();
//		String[] kycFields = testCaseDTO.getKycFields();
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
//		if (testCaseDTO.getEndPoint().contains("$PartnerKeyURL$")) {
//			testCaseDTO.setEndPoint(
//					testCaseDTO.getEndPoint().replace("$PartnerKeyURL$", PartnerRegistration.partnerKeyUrl));
//		}
//
//		if (testCaseDTO.getEndPoint().contains("$KycPartnerKeyURL$")) {
//			testCaseDTO.setEndPoint(
//					testCaseDTO.getEndPoint().replace("$KycPartnerKeyURL$", PartnerRegistration.ekycPartnerKeyUrl));
//		}
//
//		if (testCaseDTO.getEndPoint().contains("$UpdatedPartnerKeyURL$")) {
//			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$UpdatedPartnerKeyURL$",
//					PartnerRegistration.updatedpartnerKeyUrl));
//		}
//
//		if (testCaseDTO.getEndPoint().contains("$PartnerName$")) {
//			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$PartnerName$", PartnerRegistration.partnerId));
//		}
//
//		if (testCaseDTO.getEndPoint().contains("$KycPartnerName$")) {
//			testCaseDTO.setEndPoint(
//					testCaseDTO.getEndPoint().replace("$KycPartnerName$", PartnerRegistration.ekycPartnerId));
//		}
//		String request = testCaseDTO.getInput();
//		request = buildIdentityRequest(request);
//
//		String inputJSON = getJsonFromTemplate(request, testCaseDTO.getInputTemplate());
//		String resolvedUri = null;
//		String individualId = null;
//		resolvedUri = uriKeyWordHandelerUri(testCaseDTO.getEndPoint(), testCaseName);
//
//		individualId = AdminTestUtil.getValueFromUrl(resolvedUri, "id");
//
//		String url = ConfigManager.getAuthDemoServiceUrl();
//
//		response = postWithBodyAndCookie(url + testCaseDTO.getEndPoint(), inputJSON, COOKIENAME, testCaseDTO.getRole(),
//				testCaseDTO.getTestCaseName());
//
//		String ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
//
//		if (testCaseDTO.getTestCaseName().contains("uin") || testCaseDTO.getTestCaseName().contains("UIN")) {
//			if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("UIN")
//					|| BaseTestCase.getSupportedIdTypesValueFromActuator().contains("uin")) {
//				ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
//			} else {
//				if (testCaseDTO.getTestCaseName().contains("auth_EkycBio")) {
//					ActualOPJson = AdminTestUtil.getRequestJson("config/errorUINKyc.json").toString();
//				} else {
//					ActualOPJson = AdminTestUtil.getRequestJson("config/errorUIN.json").toString();
//				}
//
//			}
//		} else {
//			if (testCaseDTO.getTestCaseName().contains("VID") || testCaseDTO.getTestCaseName().contains("Vid")) {
//				if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
//						|| BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
//					ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
//				} else {
//					if (testCaseDTO.getTestCaseName().contains("auth_EkycBio")) {
//						ActualOPJson = AdminTestUtil.getRequestJson("config/errorUINKyc.json").toString();
//					} else {
//						ActualOPJson = AdminTestUtil.getRequestJson("config/errorUIN.json").toString();
//					}
//
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
//		if (testCaseName.toLowerCase().contains("kyc")) {
//			JSONObject resJsonObject = new JSONObject(response.asString());
//			String res = "";
//			try {
//				// res = resJsonObject.get("response").toString();
//				resJsonObject = new JSONObject(response.getBody().asString()).getJSONObject("authResponse")
//						.getJSONObject("body").getJSONObject("response");
//
//				res = AdminTestUtil.ekycDataDecryption(url, resJsonObject, PartnerRegistration.ekycPartnerId, true);
//
//				JSONObject jsonObjectkycRes = new JSONObject(res);
//				JSONObject jsonObjectFromKycData = new JSONObject();
//				JSONObject jsonObjectFromIdentityData = new JSONObject();
//				// List<String> myList =new ArrayList<>();
//
//				ArrayList<String> names = new ArrayList<>();
//				ArrayList<String> names2 = new ArrayList<>();
//
//				for (int i = 0; i < kycFields.length; i++) {
//					for (String key : jsonObjectkycRes.keySet()) {
//						if (key.contains(kycFields[i])) {
//							names.add(key);// dob gender_eng
//							names2.add(kycFields[i]);// dob gender
//							jsonObjectFromKycData.append(key, jsonObjectkycRes.getString(key));
//							break;
//						}
//					}
//
//				}
//
//				newResponse = RestClient.getRequestWithCookie(
//						ApplnURI + props.getProperty("retrieveIdByUin") + individualId, MediaType.APPLICATION_JSON,
//						MediaType.APPLICATION_JSON, COOKIENAME, kernelAuthLib.getTokenByRole("idrepo"),
//						IDTOKENCOOKIENAME, null);
//
//				GlobalMethods.reportResponse(newResponse.getHeaders().asList().toString(), url, newResponse);
//
//				JSONObject responseBody = new JSONObject(newResponse.getBody().asString()).getJSONObject("response")
//						.getJSONObject("identity");
//
//				for (int j = 0; j < names2.size(); j++) {
//
//					String mappingField = getValueFromAuthActuator("json-property", names2.get(j));
//					mappingField = mappingField.replaceAll("\\[\"|\"\\]", "");
//					JSONArray valueOfJsonArray = responseBody.optJSONArray(mappingField);
//					if (valueOfJsonArray != null) {
//						jsonObjectFromIdentityData.append(names.get(j), valueOfJsonArray.getJSONObject(0).get("value"));
//
//						valueOfJsonArray = null;
//					} else {
//						jsonObjectFromIdentityData.append(names.get(j), responseBody.getString(mappingField));
//					}
//
//				}
//
//				ouputValid = OutputValidationUtil.doJsonOutputValidation(jsonObjectFromIdentityData.toString(),
//						jsonObjectFromKycData.toString(), testCaseDTO, response.getStatusCode());
//				Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));
//
//				if (!OutputValidationUtil.publishOutputResult(ouputValid))
//					throw new AdminTestException("Failed at output validation");
//
//			} catch (JSONException e) {
//				logger.error(e.getMessage());
//			}
//
//		}
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
	}
}
