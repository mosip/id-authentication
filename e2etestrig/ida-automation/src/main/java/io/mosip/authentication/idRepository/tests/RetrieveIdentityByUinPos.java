package io.mosip.authentication.idRepository.tests;

import java.lang.reflect.Field;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.IdRepoUtil;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.idRepository.fw.util.IdRepoTestsUtil;

public class RetrieveIdentityByUinPos extends IdRepoTestsUtil implements ITest {

	private static final Logger logger = Logger.getLogger(RetrieveIdentityByUinPos.class);
	protected static String testCaseName = "";
	
	@Test
	public void RetrieveUIN() {
		String cookieValue = getAuthorizationCookie(getCookieRequestFilePath(),
				RunConfigUtil.objRunConfig.getIdRepoEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AUTHORIZATHION_COOKIENAME);
		String expIdentity = getIdentityForUpdateIdentityVerification();
		String uinNumber = JsonPrecondtion.getJsonValueFromJson(expIdentity, "UIN");
		logger.info("******Post request Json to EndPointUrl: " + IdRepoUtil.getRetrieveIdentityByUIN(uinNumber)
				+ " *******");
		String actualIdentity = getResponseForRequestUrl(IdRepoUtil.getRetrieveIdentityByUIN(uinNumber),
				AUTHORIZATHION_COOKIENAME, cookieValue);
		Assert.assertEquals(
				OutputValidationUtil.compareTwoKycMap(JsonPrecondtion.jsonToMap(new JSONObject(expIdentity)),
						JsonPrecondtion.jsonToMap(new JSONObject(actualIdentity))),
				true);

	}

	/**
	 * Set current testcaseName
	 */
	@Override
	public String getTestName() {
		return this.testCaseName;
	}
	
	/**
	 * The method set test case name
	 * 
	 * @param method
	 * @param testData
	 */
	@BeforeMethod
	public void setTestCaseName() {
		this.testCaseName = String.format("Verify Retrieve Identity By UIN");
	}
	
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, RetrieveIdentityByUinPos.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}
}
