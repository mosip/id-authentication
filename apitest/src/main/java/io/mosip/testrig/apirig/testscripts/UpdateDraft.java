package io.mosip.testrig.apirig.testscripts;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.restassured.response.Response;

public class UpdateDraft extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(UpdateDraft.class);
	protected String testCaseName = "";
	String pathParams = null;
	public Response response = null;

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
		pathParams = context.getCurrentXmlTest().getLocalParameters().get("pathParams");
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}

	/**
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
		testCaseDTO.setInputTemplate(AdminTestUtil.generateHbsForUpdateDraft());
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

		String jsonInput = testCaseDTO.getInput();

		if (BaseTestCase.languageList.size() == 2) {
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"FR\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Female\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Mrs Lisa.GN\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Line1\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Line2\" }", "");
			jsonInput = jsonInput.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Line3\" }", "");
		} else if (BaseTestCase.languageList.size() == 1) {
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"FR\" }, { \"language\": \"$3RDLANG$\", \"value\": \"FR\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Female\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Female\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Mrs Lisa.GN\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Mrs Lisa.GN\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Line1\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Line1\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Line2\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Line2\" }",
					"");
			jsonInput = jsonInput.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Line3\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Line3\" }",
					"");
		}

		String inputJson = getJsonFromTemplate(jsonInput, testCaseDTO.getInputTemplate(), false);

		if (inputJson.contains("$1STLANG$"))
			inputJson = inputJson.replace("$1STLANG$", BaseTestCase.languageList.get(0));
		if (inputJson.contains("$2NDLANG$"))
			inputJson = inputJson.replace("$2NDLANG$", BaseTestCase.languageList.get(1));
		if (inputJson.contains("$3RDLANG$"))
			inputJson = inputJson.replace("$3RDLANG$", BaseTestCase.languageList.get(2));

		response = patchWithPathParamsBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(), inputJson, COOKIENAME,
				testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), pathParams);

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

	@AfterClass(alwaysRun = true)
	public void waittime() {

		try {
			logger.info(
					"waiting for" + properties.getProperty("Delaytime") + " mili secs after UIN Generation In IDREPO");
			Thread.sleep(Long.parseLong(properties.getProperty("Delaytime")));
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			Thread.currentThread().interrupt();
		}

	}
}