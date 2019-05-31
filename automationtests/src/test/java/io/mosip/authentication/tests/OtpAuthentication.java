package io.mosip.authentication.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.google.common.base.Verify;

import io.mosip.authentication.fw.util.AuditValidation;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;

import org.testng.Reporter;

/**
 * Tests to execute otp authentication
 * 
 * @author Athila
 *
 */
public class OtpAuthentication extends AuthTestsUtil implements ITest {

	private static final Logger logger = Logger.getLogger(OtpAuthentication.class);
	protected static String testCaseName = "";
	private String TESTDATA_PATH;
	private String TESTDATA_FILENAME;
	private String testType;
	private int invocationCount = 0;

	/**
	 * Set Test Type - Smoke, Regression or Integration
	 * 
	 * @param testType
	 */
	@Parameters({ "testType" })
	@BeforeClass
	public void setTestType(String testType) {
		this.testType = testType;
	}

	/**
	 * Method set Test data path and its filename
	 * 
	 * @param index
	 */
	public void setTestDataPathsAndFileNames(int index) {
		this.TESTDATA_PATH = getTestDataPath(this.getClass().getSimpleName().toString(), index);
		this.TESTDATA_FILENAME = getTestDataFileName(this.getClass().getSimpleName().toString(), index);
	}

	/**
	 * Method set configuration
	 * 
	 * @param testType
	 */
	public void setConfigurations(String testType) {
		RunConfigUtil.getRunConfigObject("ida");
		RunConfigUtil.objRunConfig.setConfig(this.TESTDATA_PATH, this.TESTDATA_FILENAME, testType);
		TestDataProcessor.initateTestDataProcess(this.TESTDATA_FILENAME, this.TESTDATA_PATH, "ida");
	}

	/**
	 * The method set test case name
	 * 
	 * @param method
	 * @param testData
	 */
	@BeforeMethod
	public void testData(Method method, Object[] testData) {
		String testCase = "";
		if (testData != null && testData.length > 0) {
			TestParameters testParams = null;
			// Check if test method has actually received required parameters
			for (Object testParameter : testData) {
				if (testParameter instanceof TestParameters) {
					testParams = (TestParameters) testParameter;
					break;
				}
			}
			if (testParams != null) {
				testCase = testParams.getTestCaseName();
			}
		}
		this.testCaseName = String.format(testCase);
	}

	/**
	 * Data provider class provides test case list
	 * 
	 * @return object of data provider
	 */
	@DataProvider(name = "testcaselist")
	public Object[][] getTestCaseList() {
		invocationCount++;
		setTestDataPathsAndFileNames(invocationCount);
		setConfigurations(this.testType);
		return DataProviderClass.getDataProvider(
				RunConfigUtil.objRunConfig.getUserDirectory() + RunConfigUtil.objRunConfig.getSrcPath() + RunConfigUtil.objRunConfig.getScenarioPath(),
				RunConfigUtil.objRunConfig.getScenarioPath(), RunConfigUtil.objRunConfig.getTestType());
	}

	/**
	 * Set current testcaseName
	 */
	@Override
	public String getTestName() {
		return this.testCaseName;
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
			f.set(baseTestMethod, OtpAuthentication.testCaseName);
			test=extent.createTest(testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * Test method for OTP authentication execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 */
	@Test(dataProvider = "testcaselist")
	public void idaOtpAuthenticationTest(TestParameters objTestParameters, String testScenario, String testcaseName) {
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		String mapping = TestDataUtil.getMappingPath();
		String extUrl = getExtendedUrl(new File(objTestParameters.getTestCaseFile() + "/url.properties"));
		logger.info("*************Otp generation request ******************");
		Reporter.log("<b><u>Otp generation request</u></b>");
		displayContentInFile(testCaseName.listFiles(), "otp-generate");
		logger.info("******Post request Json to EndPointUrl: " + RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getOtpPath()
				+ extUrl + " *******");
		Assert.assertEquals(postRequestAndGenerateOuputFile(testCaseName.listFiles(),
				RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getOtpPath() + extUrl, "otp-generate", "output-1-actual-res",
				200), true);
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		Verify.verify(OutputValidationUtil.publishOutputResult(ouputValid));
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("pinInfovalue", getOtpValue(
				FileUtil.getFilePath(testCaseName, "identity-encrypt").getAbsolutePath(), mapping, "pinInfovalue"));
		Reporter.log("<b><u>Modification of otp value in identity request</u></b>");
		Assert.assertEquals(modifyRequest(testCaseName.listFiles(), tempMap, mapping, "identity-encrypt"), true);
		Map<String, String> tempEncryptMap = getEncryptKeyvalue(testCaseName.listFiles(), "identity-encrypt");
		logger.info("*************Modification OTP Authentication request ******************");
		Reporter.log("<b><u>Modification of OTP Authentication request</u></b>");
		Assert.assertEquals(modifyRequest(testCaseName.listFiles(), tempEncryptMap, mapping, "otp-auth-request"), true);
		logger.info("******Post request Json to EndPointUrl: " + RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getAuthPath()
				+ extUrl + " *******");
		Assert.assertEquals(postRequestAndGenerateOuputFile(testCaseName.listFiles(),
				RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getAuthPath() + extUrl, "otp-auth-request",
				"output-2-actual-res", 200), true);
		Map<String, List<OutputValidationDto>> ouputValid2 = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-2-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-2-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid2));
		Verify.verify(OutputValidationUtil.publishOutputResult(ouputValid2));
		if (FileUtil.verifyFilePresent(testCaseName.listFiles(), "auth_transaction")) {
			wait(5000);
			logger.info("************* Auth Transaction Validation ******************");
			Reporter.log("<b><u>Auth Transaction Validation</u></b>");
			Map<String, List<OutputValidationDto>> auditTxnvalidation = AuditValidation
					.verifyAuditTxn(testCaseName.listFiles(), "auth_transaction");
			Reporter.log(ReportUtil.getOutputValiReport(auditTxnvalidation));
			Assert.assertEquals(OutputValidationUtil.publishOutputResult(auditTxnvalidation), true);
		}
		if (FileUtil.verifyFilePresent(testCaseName.listFiles(), "audit_log")) {
			wait(5000);
			logger.info("************* Audit Log Validation ******************");
			Reporter.log("<b><u>Audit Log Validation</u></b>");
			Map<String, List<OutputValidationDto>> auditLogValidation = AuditValidation
					.verifyAuditLog(testCaseName.listFiles(), "audit_log");
			Reporter.log(ReportUtil.getOutputValiReport(auditLogValidation));
			Assert.assertEquals(OutputValidationUtil.publishOutputResult(auditLogValidation), true);
		}
	}

}
