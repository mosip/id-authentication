


package io.mosip.authentication.tests;

import java.io.File; 
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.google.common.base.Verify;

import io.mosip.authentication.fw.util.AuditValidUtil;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import io.mosip.authentication.fw.util.OutputValidationDto;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.testdata.TestDataProcessor;
import io.mosip.testdata.TestDataUtil;

import org.testng.Reporter;

/**
 * Tests to execute biometric authentication for ida
 * 
 * @author Vignesh
 *
 */
public class BioAuthentication extends IdaScriptsUtil implements ITest{

	private static Logger logger = Logger.getLogger(BioAuthentication.class);
	private DataProviderClass objDataProvider = new DataProviderClass();
	private OutputValidationUtil objOpValiUtil = new OutputValidationUtil();
	private ReportUtil objReportUtil = new ReportUtil();
	private RunConfig objRunConfig = new RunConfig();
	private FileUtil objFileUtil = new FileUtil();
	protected static String testCaseName = "";
	private TestDataProcessor objTestDataProcessor = new TestDataProcessor();
	private AuditValidUtil objAuditValidUtil = new AuditValidUtil();

	@Parameters({ "testDatPath" , "testDataFileName" ,"testType"})
	@BeforeClass
	public void setConfigurations(String testDatPath,String testDataFileName,String testType) {
		objRunConfig.setConfig(testDatPath,testDataFileName,testType);
		objTestDataProcessor.initateTestDataProcess(testDataFileName,testDatPath,"ida");	
	}
	
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

	@DataProvider(name = "testcaselist")
	public Object[][] getTestCaseList() {
		return objDataProvider.getDataProvider(
				System.getProperty("user.dir") + RunConfig.getSrcPath() + RunConfig.getScenarioPath(),
				RunConfig.getScenarioPath(), RunConfig.getTestType());
	}
	
	@Override
	public String getTestName() {
		return this.testCaseName;
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
			f.set(baseTestMethod, BioAuthentication.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	} 

	@Test(dataProvider = "testcaselist")
	public void idaApiBioAuthExecution(TestParameters objTestParameters, String testScenario, String testcaseName) {
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		String mapping = TestDataUtil.getMappingPath();
		Map<String, String> tempMap = new HashMap<String, String>();
		for (Entry<String, String> entry : getEncryptKeyvalue(testCaseName.listFiles(), "identity-encrypt")
				.entrySet()) {
			tempMap.put("key", entry.getKey());
			tempMap.put("data", entry.getValue());
		}
		logger.info("************* Modification of bio auth request ******************");
		Reporter.log("<b><u>Modification of bio auth request</u></b>");
		Assert.assertEquals(modifyRequest(testCaseName.listFiles(), tempMap, mapping, "bio-auth"), true);
		logger.info("******Post request Json to EndPointUrl: " + RunConfig.getEndPointUrl() + RunConfig.getAuthPath()
				+ " *******");
		Assert.assertEquals(postAndGenOutFile(testCaseName.listFiles(),
				RunConfig.getEndPointUrl() + RunConfig.getAuthPath(), "request", "output-1-actual-res", 200), true);
		Map<String, List<OutputValidationDto>> ouputValid = objOpValiUtil.doOutputValidation(
				objFileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				objFileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(objReportUtil.getOutputValiReport(ouputValid));
		Assert.assertEquals(objOpValiUtil.publishOutputResult(ouputValid), true);
		if(objFileUtil.verifyFilePresent(testCaseName.listFiles(), "auth_transaction")) {
			wait(5000);
			logger.info("************* Auth Transaction Validation ******************");
			Reporter.log("<b><u>Auth Transaction Validation</u></b>");
			Map<String, List<OutputValidationDto>> auditTxnvalidation = objAuditValidUtil
					.verifyAuditTxn(testCaseName.listFiles(), "auth_transaction");
			Reporter.log(objReportUtil.getOutputValiReport(auditTxnvalidation));
			Assert.assertEquals(objOpValiUtil.publishOutputResult(auditTxnvalidation), true);
		}if (objFileUtil.verifyFilePresent(testCaseName.listFiles(), "audit_log")) {
			wait(5000);
			logger.info("************* Audit Log Validation ******************");
			Reporter.log("<b><u>Audit Log Validation</u></b>");
			Map<String, List<OutputValidationDto>> auditLogValidation = objAuditValidUtil
					.verifyAuditLog(testCaseName.listFiles(), "audit_log");
			Reporter.log(objReportUtil.getOutputValiReport(auditLogValidation));
			Assert.assertEquals(objOpValiUtil.publishOutputResult(auditLogValidation), true);
		}
	}

}
