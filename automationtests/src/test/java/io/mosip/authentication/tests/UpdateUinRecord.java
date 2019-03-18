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
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.beust.jcommander.Parameter;

import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.dto.UinDto;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.IdRepoUtil;
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.fw.util.UinVidNumberUtil;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;
import io.mosip.authentication.testdata.keywords.IdaKeywordUtil;
import io.mosip.authentication.testdata.keywords.KeywordUtil;

public class UpdateUinRecord extends IdaScriptsUtil implements ITest{
	
	private static Logger logger = Logger.getLogger(UpdateUinRecord.class);
	private DataProviderClass objDataProvider = new DataProviderClass();
	private OutputValidationUtil objOpValiUtil = new OutputValidationUtil();
	private ReportUtil objReportUtil = new ReportUtil();
	private RunConfig objRunConfig = new RunConfig();
	private FileUtil objFileUtil = new FileUtil();
	protected static String testCaseName = "";
	private TestDataProcessor objTestDataProcessor = new TestDataProcessor();
	private IdRepoUtil objIdRepoUtil = new IdRepoUtil();
	private Map<String,String> storeUinData = new HashMap<String,String>();
	private static UinVidNumberUtil objUinVidNumberUtil = new UinVidNumberUtil();
	
	private String TESTDATA_PATH="ida/TestData/UINData/UpdateTestData/";
	private String TESTDATA_FILENAME="testdata.ida.UINData.UpdateTestData.mapping.yml";

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

	@Test
	public void updateUINTestData() {
		Object[][] object = objDataProvider.getDataProvider(
				System.getProperty("user.dir") + RunConfig.getSrcPath() + RunConfig.getScenarioPath(),
				RunConfig.getScenarioPath(), RunConfig.getTestType());
		for (int i = 1; i < object.length; i++) {
			idaUpdateUINData(new TestParameters((TestParameters) object[i][0]), object[i][1].toString(),
					object[i][2].toString());
		}
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
			f.set(baseTestMethod, UpdateUinRecord.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	} 

	public void idaUpdateUINData(TestParameters objTestParameters, String testScenario, String testcaseName) {
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		String mapping = TestDataUtil.getMappingPath();
		Map<String, String> tempMap = new HashMap<String, String>();
		String uin = objUinVidNumberUtil.getRandomUINKey();
		logger.info("************* IdRepo UIN Update request ******************");
		Reporter.log("<b><u>UIN create request</u></b>");
		Assert.assertEquals(modifyRequest(testCaseName.listFiles(), tempMap, mapping, "update"), true);
		logger.info("******Post request Json to EndPointUrl: " + objIdRepoUtil.getCreateUinPath(uin) + " *******");
		wait(10000);
		Assert.assertEquals(postAndGenOutFileForUinUpdate(testCaseName.listFiles(), objIdRepoUtil.getCreateUinPath(uin),
				"update", "output-1-actual-res", 0), true);
		Map<String, List<OutputValidationDto>> ouputValid = objOpValiUtil.doOutputValidation(
				objFileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				objFileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(objReportUtil.getOutputValiReport(ouputValid));
		if (objOpValiUtil.publishOutputResult(ouputValid)) {
			Assert.assertEquals(true, true);
			storeUinData.put(uin, testcaseName);
		} else
			Assert.assertEquals(true, false);
	}
	
	@AfterClass
	public void storeUinData() {
		UinDto.setUinData(storeUinData);
		logger.info("Updated UIN: " + UinDto.getUinData());
		updateMappingDic(new File("./"+RunConfig.getSrcPath() + "ida/"+RunConfig.getTestDataFolderName()+"/RunConfig/uin.properties").getAbsolutePath(),
				UinDto.getUinData());
	}

}
