
package io.mosip.authentication.tests;

import java.io.File;   
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

import io.mosip.authentication.fw.util.AuditValidation;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;

import org.testng.Reporter;
 
/**
 * Tests to execute Static Pin Authentication
 * 
 * @author Athila
 *
 */
public class InternalStaticPinAuthentication extends IdaScriptsUtil implements ITest{

	private static final Logger logger = Logger.getLogger(InternalStaticPinAuthentication.class);
	protected static String testCaseName = "";
	
	@Parameters({ "testDatPath" , "testDataFileName" ,"testType"})
	@BeforeClass
	public void setConfigurations(String testDatPath,String testDataFileName,String testType) {
		RunConfig.setConfig(testDatPath,testDataFileName,testType);
		TestDataProcessor.initateTestDataProcess(testDataFileName,testDatPath,"ida");	
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
		return DataProviderClass.getDataProvider(
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
			f.set(baseTestMethod, InternalStaticPinAuthentication.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	} 

	@Test(dataProvider = "testcaselist")
	public void idaStaticPinStorage(TestParameters objTestParameters,String testScenario,String testcaseName) {
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		String mapping = TestDataUtil.getMappingPath();
		logger.info("************* Internal StaticPin  authenticaiton request ******************");
		Reporter.log("<b><u>Internal Static pin authentication request</u></b>");
		displayContentInFile(testCaseName.listFiles(),"request");
		logger.info("******Post request Json to EndPointUrl: " + RunConfig.getEndPointUrl() + RunConfig.getInternalAuthPath()
				+ " *******");
		Assert.assertEquals(postRequestAndGenerateOuputFile(testCaseName.listFiles(),
				RunConfig.getEndPointUrl() + RunConfig.getInternalAuthPath(), "request", "output-1-actual-res",200), true);
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		Assert.assertEquals(OutputValidationUtil.publishOutputResult(ouputValid), true);
		if(FileUtil.verifyFilePresent(testCaseName.listFiles(), "auth_transaction")) {
			wait(5000);
			logger.info("************* Auth Transaction Validation ******************");
			Reporter.log("<b><u>Auth Transaction Validation</u></b>");
			Map<String, List<OutputValidationDto>> auditTxnvalidation = AuditValidation
					.verifyAuditTxn(testCaseName.listFiles(), "auth_transaction");
			Reporter.log(ReportUtil.getOutputValiReport(auditTxnvalidation));
			Assert.assertEquals(OutputValidationUtil.publishOutputResult(auditTxnvalidation), true);
		}if (FileUtil.verifyFilePresent(testCaseName.listFiles(), "audit_log")) {
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

