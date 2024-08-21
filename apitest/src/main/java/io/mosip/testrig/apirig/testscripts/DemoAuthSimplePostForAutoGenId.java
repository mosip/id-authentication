package io.mosip.testrig.apirig.testscripts;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.HealthChecker;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.AuthenticationTestException;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.GlobalMethods;
import io.mosip.testrig.apirig.utils.OutputValidationUtil;
import io.mosip.testrig.apirig.utils.PartnerRegistration;
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.mosip.testrig.apirig.utils.RestClient;
import io.restassured.response.Response;

public class DemoAuthSimplePostForAutoGenId extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(DemoAuthSimplePostForAutoGenId.class);
	protected String testCaseName = "";
	public String idKeyName = null;
	public Response response = null;
	public Response newResponse = null;
	String url = "";

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
		idKeyName = context.getCurrentXmlTest().getLocalParameters().get("idKeyName");
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
	 * @throws NoSuchAlgorithmException
	 */
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO)
			throws AuthenticationTestException, AdminTestException, NoSuchAlgorithmException {
		testCaseName = testCaseDTO.getTestCaseName();
		String[] kycFields = testCaseDTO.getKycFields();
		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException(
					GlobalConstants.TARGET_ENV_HEALTH_CHECK_FAILED + HealthChecker.healthCheckFailureMapS);
		}
		testCaseName = isTestCaseValidForExecution(testCaseDTO);
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

		if (testCaseDTO.getEndPoint().contains("$PartnerKeyURL$")) {
			testCaseDTO.setEndPoint(
					testCaseDTO.getEndPoint().replace("$PartnerKeyURL$", PartnerRegistration.partnerKeyUrl));
		}

		if (testCaseDTO.getEndPoint().contains("$KycPartnerKeyURL$")) {
			testCaseDTO.setEndPoint(
					testCaseDTO.getEndPoint().replace("$KycPartnerKeyURL$", PartnerRegistration.ekycPartnerKeyUrl));
		}

		if (testCaseDTO.getEndPoint().contains("$PartnerName$")) {
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$PartnerName$", PartnerRegistration.partnerId));
		}

		if (testCaseDTO.getEndPoint().contains("$KycPartnerName$")) {
			testCaseDTO.setEndPoint(
					testCaseDTO.getEndPoint().replace("$KycPartnerName$", PartnerRegistration.ekycPartnerId));
		}

		if (testCaseDTO.getEndPoint().contains("$UpdatedPartnerKeyURL$")) {
			testCaseDTO.setEndPoint(testCaseDTO.getEndPoint().replace("$UpdatedPartnerKeyURL$",
					PartnerRegistration.updatedpartnerKeyUrl));
		}

		String input = testCaseDTO.getInput();

		if (input.contains("$PRIMARYLANG$"))
			input = input.replace("$PRIMARYLANG$", BaseTestCase.languageList.get(0));

		if (input.contains("name") & testCaseDTO.getTestCaseName().contains("titleFromAdmin")) {
			input = AdminTestUtil.inputTitleHandler(input);
		}

		if (input.contains("$NAMEPRIMARYLANG$")) {
			String name = "";
			if (BaseTestCase.isTargetEnvLTS())
				name = propsMap.getProperty("fullName");
			else
				name = propsMap.getProperty("firstName");
			input = input.replace("$NAMEPRIMARYLANG$", name + BaseTestCase.languageList.get(0));
		}

		String[] templateFields = testCaseDTO.getTemplateFields();
		String resolvedUri = null;
		String individualId = null;
		resolvedUri = uriKeyWordHandelerUri(testCaseDTO.getEndPoint(), testCaseName);

		individualId = AdminTestUtil.getValueFromUrl(resolvedUri, "id");

		String inputJson = getJsonFromTemplate(input, testCaseDTO.getInputTemplate());

		JSONObject jsonObject = new JSONObject(inputJson);
		JSONObject demographics = jsonObject.getJSONObject("demographics");
		if (!demographics.get("name").toString().equals("[]")) {
			demographics.remove("age");
			inputJson = jsonObject.toString();
		}

		String outputJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());

		if (testCaseDTO.getTemplateFields() != null && templateFields.length > 0) {
			ArrayList<JSONObject> inputtestCases = AdminTestUtil.getInputTestCase(testCaseDTO);
			ArrayList<JSONObject> outputtestcase = AdminTestUtil.getOutputTestCase(testCaseDTO);
			languageList = Arrays.asList(System.getProperty("env.langcode").split(","));
			for (int i = 0; i < languageList.size(); i++) {
				response = postWithBodyAndCookieForAutoGeneratedId(ApplnURI + testCaseDTO.getEndPoint(),
						getJsonFromTemplate(inputtestCases.get(i).toString(), testCaseDTO.getInputTemplate()),
						COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), idKeyName);

				Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
						response.asString(),
						getJsonFromTemplate(outputtestcase.get(i).toString(), testCaseDTO.getOutputTemplate()),
						testCaseDTO, response.getStatusCode());
				if (testCaseDTO.getTestCaseName().toLowerCase().contains("dynamic")) {
					JSONObject json = new JSONObject(response.asString());
					idField = json.getJSONObject("response").get("id").toString();
				}
				Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));

				if (!OutputValidationUtil.publishOutputResult(ouputValid))
					throw new AdminTestException("Failed at output validation");
			}
		} else {
			if (testCaseName.contains("partnerDemoDown")) {

				//url = ConfigManager.getAuthDemoServiceUrl() + "local";
			} else {
				//url = ConfigManager.getAuthDemoServiceUrl();
			}

			response = postWithBodyAndCookie(url + testCaseDTO.getEndPoint(), inputJson, COOKIENAME,
					testCaseDTO.getRole(), testCaseDTO.getTestCaseName());
			String ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());

			if (testCaseDTO.getTestCaseName().contains("uin") || testCaseDTO.getTestCaseName().contains("UIN")) {
				if (BaseTestCase.getSupportedIdTypesValueFromActuator().contains("UIN")
						|| BaseTestCase.getSupportedIdTypesValueFromActuator().contains("uin")) {
					ActualOPJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
				} else {
					if (testCaseDTO.getTestCaseName().contains("auth_EkycDemo")) {
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
						if (testCaseDTO.getTestCaseName().contains("auth_EkycDemo")) {
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
		}

		if (testCaseName.toLowerCase().contains("kyc")) {
			JSONObject resJsonObject = new JSONObject(response.asString());
			String res = "";
			try {
				// res = resJsonObject.get("response").toString();
				resJsonObject = new JSONObject(response.getBody().asString()).getJSONObject("authResponse")
						.getJSONObject("body").getJSONObject("response");

				res = AdminTestUtil.ekycDataDecryptionForDemo(url, resJsonObject, PartnerRegistration.ekycPartnerId,
						true);

				JSONObject jsonObjectkycRes = new JSONObject(res);
				JSONObject jsonObjectFromKycData = new JSONObject();
				JSONObject jsonObjectFromIdentityData = new JSONObject();
				// List<String> myList =new ArrayList<>();

				ArrayList<String> names = new ArrayList<>();
				ArrayList<String> names2 = new ArrayList<>();

				for (int i = 0; i < kycFields.length; i++) {
					for (String key : jsonObjectkycRes.keySet()) {
						if (key.contains(kycFields[i])) {
							names.add(key);// dob gender_eng
							names2.add(kycFields[i]);// dob gender
							jsonObjectFromKycData.append(key, jsonObjectkycRes.getString(key));
							break;
						}
					}

				}

				newResponse = RestClient.getRequestWithCookie(
						ApplnURI + props.getProperty("retrieveIdByUin") + individualId, MediaType.APPLICATION_JSON,
						MediaType.APPLICATION_JSON, COOKIENAME, kernelAuthLib.getTokenByRole("idrepo"),
						IDTOKENCOOKIENAME, null);

				GlobalMethods.reportResponse(newResponse.getHeaders().asList().toString(), url, newResponse);

				JSONObject responseBody = new JSONObject(newResponse.getBody().asString()).getJSONObject("response")
						.getJSONObject("identity");

				for (int j = 0; j < names2.size(); j++) {

					String mappingField = getValueFromAuthActuator("json-property", names2.get(j));
					mappingField = mappingField.replaceAll("\\[\"|\"\\]", "");
					JSONArray valueOfJsonArray = responseBody.optJSONArray(mappingField);
					if (valueOfJsonArray != null) {
						jsonObjectFromIdentityData.append(names.get(j), valueOfJsonArray.getJSONObject(0).get("value"));

						valueOfJsonArray = null;
					} else {
						jsonObjectFromIdentityData.append(names.get(j), responseBody.getString(mappingField));
					}

				}

				Map<String, List<OutputValidationDto>> ouputValidNew = OutputValidationUtil.doJsonOutputValidation(
						jsonObjectFromIdentityData.toString(), jsonObjectFromKycData.toString(), testCaseDTO,
						newResponse.getStatusCode());
				Reporter.log(ReportUtil.getOutputValidationReport(ouputValidNew));

				if (!OutputValidationUtil.publishOutputResult(ouputValidNew))
					throw new AdminTestException("Failed at output validation");

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
}
