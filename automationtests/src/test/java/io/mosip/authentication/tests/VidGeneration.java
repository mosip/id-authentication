package io.mosip.authentication.tests;

import java.io.File;
import java.lang.reflect.Field; 
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.google.common.base.Verify;

import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;

public class VidGeneration extends IdaScriptsUtil implements ITest{
	
	private static Logger logger = Logger.getLogger(VidGeneration.class);
	private DataProviderClass objDataProvider = new DataProviderClass();
	private OutputValidationUtil objOpValiUtil = new OutputValidationUtil();
	private ReportUtil objReportUtil = new ReportUtil();
	private RunConfig objRunConfig = new RunConfig();
	private FileUtil objFileUtil = new FileUtil();
	protected static String testCaseName = "";
	private TestDataProcessor objTestDataProcessor = new TestDataProcessor();
	private String TESTDATA_PATH="ida/TestData/VIDGeneration/";
	private String TESTDATA_FILENAME="testdata.ida.VIDGeneration.mapping.yml";

	@Parameters({"testType"})
	@BeforeClass
	public void setConfigurations(String testType) {
		objRunConfig.setConfig(TESTDATA_PATH,TESTDATA_FILENAME,testType);
		objTestDataProcessor.initateTestDataProcess(TESTDATA_FILENAME,TESTDATA_PATH,"ida");	
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
			f.set(baseTestMethod, VidGeneration.testCaseName);
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
		logger.info("*************VID generation request ******************");
		Reporter.log("<b><u>VID generation request</u></b>");
		displayContentInFile(testCaseName.listFiles(), "uin");
		String uin = getValueFromJson(testCaseName.listFiles(), mapping, "uin", "uin");
		logger.info("******GET request Json to EndPointUrl: " + RunConfig.getEndPointUrl() + RunConfig.getVidGenPath()
				+ " *******");
		String url = RunConfig.getEndPointUrl() + RunConfig.getVidGenPath();
		url = url.replace("$uin$", uin);
		String response = getResponse(url);
		File outputFile = objFileUtil.getFilePath(testCaseName, "output-1-expected");
		objFileUtil.createAndWriteFile("output-1-actual.json", response);
		Map<String, List<OutputValidationDto>> ouputValid = objOpValiUtil.doOutputValidation(
				objFileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				objFileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(objReportUtil.getOutputValiReport(ouputValid));
		Verify.verify(objOpValiUtil.publishOutputResult(ouputValid));
	}
}

