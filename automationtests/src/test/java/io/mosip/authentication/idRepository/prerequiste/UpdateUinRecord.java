package io.mosip.authentication.idRepository.prerequiste;

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
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.IdRepoUtil;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;
import io.mosip.authentication.testdata.keywords.IdaKeywordUtil;
import io.mosip.authentication.testdata.keywords.KeywordUtil;

public class UpdateUinRecord extends AuthTestsUtil implements ITest {

	private static Logger logger = Logger.getLogger(UpdateUinRecord.class);
	protected static String testCaseName = "";
	private Map<String, String> storeUinData = new HashMap<String, String>();
	private String TESTDATA_PATH;
	private String TESTDATA_FILENAME;
	private String testType;
	private int invocationCount = 0;
	private String cookieValue;

	/**
	 * Set Test Type - Smoke, Regression or Integration
	 * 
	 * @param testType
	 */
	@BeforeClass
	public void setTestType() {
		this.testType = RunConfigUtil.getTestLevel();
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
		this.testCaseName = String.format("Update UIN");
		invocationCount++;
		setTestDataPathsAndFileNames(invocationCount);
		setConfigurations(this.testType);
	}

	/**
	 * The test update the generated UIN test data in property file
	 * @throws AuthenticationTestException 
	 */
	@Test
	public void updateUINTestData() throws AuthenticationTestException {
		Object[][] object = DataProviderClass.getDataProvider(
				RunConfigUtil.objRunConfig.getUserDirectory() + RunConfigUtil.objRunConfig.getSrcPath() + RunConfigUtil.objRunConfig.getScenarioPath(),
				RunConfigUtil.objRunConfig.getScenarioPath(), "smokeandregression");
		cookieValue=getAuthorizationCookie(getCookieRequestFilePathForUinGenerator(),RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()+RunConfigUtil.objRunConfig.getClientidsecretkey(),AUTHORIZATHION_COOKIENAME);
		for (int i = 1; i < object.length; i++) {
			updateUinRecordTest(new TestParameters((TestParameters) object[i][0]), object[i][1].toString(),
					object[i][2].toString());
		}
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
			f.set(baseTestMethod, "Update VID");
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * The method update the UIN record available in property file
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws AuthenticationTestException 
	 */
	public void updateUinRecordTest(TestParameters objTestParameters, String testScenario, String testcaseName) throws AuthenticationTestException {
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		String mapping = TestDataUtil.getMappingPath();
		logger.info("************* IdRepo UIN Update request ******************");
		Reporter.log("<b><u>UIN Update request</u></b>");
		logger.info("******Post request Json to EndPointUrl: " + IdRepoUtil.getCreateUinPath() + " *******");
		wait(5000);
		if(!postRequestAndGenerateOuputFileForUINUpdate(testCaseName.listFiles(),
				IdRepoUtil.getCreateUinPath(), "update", "output-1-actual-res",AUTHORIZATHION_COOKIENAME,cookieValue, 0))
			throw new AuthenticationTestException("Failed at HTTP-POST request");
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		String uin=JsonPrecondtion.getValueFromJson(getContentFromFile(testCaseName.listFiles(),"update"), "request.identity.UIN");
		if (OutputValidationUtil.publishOutputResult(ouputValid)) {
			storeUinData.put(uin, testcaseName);
		} else
			throw new AuthenticationTestException("Failed at output response validation");
	}

	/**
	 * The method store or modify updated UIN in property file
	 * 
	 */
	@AfterClass
	public void storeUinData() {
		UinDto.setUinData(storeUinData);
		logger.info("Updated UIN: " + UinDto.getUinData());
		updateMappingDic(new File("./" + RunConfigUtil.objRunConfig.getSrcPath() + "ida/" + RunConfigUtil.objRunConfig.getTestDataFolderName()
				+ "/RunConfig/uin.properties").getAbsolutePath(), UinDto.getUinData());
		updateMappingDic(new File("./" + RunConfigUtil.objRunConfig.getSrcPath() + "idRepository/" + RunConfigUtil.objRunConfig.getTestDataFolderName()
		+ "/RunConfig/uin.properties").getAbsolutePath(), UinDto.getUinData());
	}
}
