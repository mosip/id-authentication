package io.mosip.testrig.apirig.auth.testscripts;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
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

import io.mosip.testrig.apirig.auth.utils.IdAuthConfigManager;
import io.mosip.testrig.apirig.auth.utils.IdAuthenticationUtil;
import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.utils.AdminTestException;
import io.mosip.testrig.apirig.utils.AuthenticationTestException;
import io.mosip.testrig.apirig.utils.GlobalMethods;
import io.mosip.testrig.apirig.utils.OutputValidationUtil;
import io.mosip.testrig.apirig.utils.ReportUtil;
import io.mosip.testrig.apirig.utils.RestClient;
import io.mosip.testrig.apirig.utils.SecurityXSSException;
import io.restassured.response.Response;

public class AuthLockUnlockNewLocal extends IdAuthenticationUtil implements ITest {
	private static final Logger logger = Logger.getLogger(AuthLockUnlockNewLocal.class);
	protected String testCaseName = "";
	public Response response = null;

	@BeforeClass
	public static void setLogLevel() {
		if (IdAuthConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	@Override
	public String getTestName() {
		return testCaseName;
	}

	@DataProvider(name = "testcaselist")
	public Object[] getTestCaseList(ITestContext context) {
		String ymlFile = context.getCurrentXmlTest().getLocalParameters().get("ymlFile");
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}

	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO)
			throws AuthenticationTestException, AdminTestException, SecurityXSSException {
		testCaseName = testCaseDTO.getTestCaseName();
		if (!IdAuthConfigManager.isLocalRun()) {
			throw new SkipException("Local run only: the new auth lock/unlock API is mocked locally");
		}
		testCaseName = IdAuthenticationUtil.isTestCaseValidForExecution(testCaseDTO);

		String url = ApplnURI + testCaseDTO.getEndPoint();
		String inputJson = getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate());
		inputJson = inputJsonKeyWordHandeler(inputJson, testCaseName);
		String expected = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());

		JSONObject request = new JSONObject(inputJson);
		JSONArray authTypes = request.getJSONObject("request").getJSONArray("authTypes");
		if (authTypes.length() == 0)
			throw new AdminTestException("authTypes is empty: no auth-lock-unlock request would be sent");
		for (int i = 0; i < authTypes.length(); i++) {
			JSONObject single = new JSONObject(request.toString());
			single.getJSONObject("request").put("authTypes", new JSONArray().put(authTypes.getJSONObject(i)));
			String body = single.toString();

			GlobalMethods.reportRequest(null, body, url);
			response = RestClient.postRequest(url, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
			GlobalMethods.reportResponse(response.getHeaders().asList().toString(), url, response);

			Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil
					.doJsonOutputValidation(response.asString(), expected, testCaseDTO, response.getStatusCode());
			Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));
			if (!OutputValidationUtil.publishOutputResult(ouputValid))
				throw new AdminTestException("Failed at output validation");

			settle();
		}
	}

	private static void settle() {
		String settleMillis = IdAuthConfigManager.getproperty("local.mock.settleMillis");
		try {
			Thread.sleep(settleMillis == null || settleMillis.isBlank() ? 2000L : Long.parseLong(settleMillis.trim()));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		result.setAttribute("TestCaseName", testCaseName);
	}
}
