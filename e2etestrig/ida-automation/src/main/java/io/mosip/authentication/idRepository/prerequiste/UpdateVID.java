package io.mosip.authentication.idRepository.prerequiste;

import java.io.File; 
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

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
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.IdRepoUtil;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.dto.VidDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.OutputValidationUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.fw.util.TestParameters;
import io.mosip.authentication.fw.util.VIDUtil;
import io.mosip.authentication.idRepository.fw.util.IdRepoTestsUtil;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;

import org.testng.Reporter;

/**
 * Tests to execute the demographic authentication
 * 
 * @author Athila
 *
 */
public class UpdateVID extends AuthTestsUtil implements ITest {

	private static final Logger logger = Logger.getLogger(UpdateVID.class);
	protected static String testCaseName = "";
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
		this.testCaseName = String.format(testCase);
	}

	/**
	 * Data provider class provides test case list
	 * 
	 * @return object of data provider
	 * @throws AuthenticationTestException 
	 */
	@Test
	public void updateVidStatus() throws AuthenticationTestException {
		invocationCount++;
		setTestDataPathsAndFileNames(invocationCount);
		setConfigurations(this.testType);
		Object[][] object =DataProviderClass.getDataProvider(
				RunConfigUtil.getResourcePath() + RunConfigUtil.objRunConfig.getScenarioPath(),
				RunConfigUtil.objRunConfig.getScenarioPath(), "smokeandregression");
		for (int i = 1; i < object.length; i++) {
			updateVidStatusRecord(new TestParameters((TestParameters) object[i][0]), object[i][1].toString(),
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
	 * Test method for demographic authentication execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws AuthenticationTestException 
	 */
	public void updateVidStatusRecord(TestParameters objTestParameters, String testScenario,
			String testcaseName) throws AuthenticationTestException {
		cookieValue=getAuthorizationCookie(getCookieRequestFilePath(),RunConfigUtil.objRunConfig.getIdRepoEndPointUrl()+RunConfigUtil.objRunConfig.getClientidsecretkey(),AUTHORIZATHION_COOKIENAME);
		File testCaseName = objTestParameters.getTestCaseFile();
		int testCaseNumber = Integer.parseInt(objTestParameters.getTestId());
		displayLog(testCaseName, testCaseNumber);
		setTestFolder(testCaseName);
		setTestCaseId(testCaseNumber);
		setTestCaseName(testCaseName.getName());
		String name = getTestCaseName();
		String mapping = TestDataUtil.getMappingPath();
		String vid[] = VIDUtil.getVidKeyForVIDUpdate(name).split(Pattern.quote(".")); 
		String vidNumber=vid[0];
		String vidType=vid[1];
		
		//String vid = RunConfigUtil.getRandomVidKey().split(".")[1].toString();
		//if(getContentFromFile(testCaseName.listFiles(),"create").toString().contains("$generate_UIN$"))
		//{
			//Map<String, String> tempMap = new HashMap<String, String>();
			//tempMap.put("UIN", "LONG:" + uin);
			logger.info("************* IdRepo VID Update request ******************");
			Reporter.log("<b><u>VID Update request</u></b>");
			//Assert.assertEquals(modifyRequest(testCaseName.listFiles(), tempMap, mapping, "create"), true);
		//}
		logger.info("******Post request Json to EndPointUrl: " + IdRepoUtil.getUpdateVidStatusPath(vidNumber) + " *******");
		if(!postRequestAndGenerateOuputFileForUINUpdate(testCaseName.listFiles(), IdRepoUtil.getUpdateVidStatusPath(vidNumber),
				"create", "output-1-actual-res",AUTHORIZATHION_COOKIENAME,cookieValue,0))
			throw new AuthenticationTestException("Failed at HTTP-POST request");
		Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doOutputValidation(
				FileUtil.getFilePath(testCaseName, "output-1-actual").toString(),
				FileUtil.getFilePath(testCaseName, "output-1-expected").toString());
		Reporter.log(ReportUtil.getOutputValiReport(ouputValid));
		if(OutputValidationUtil.publishOutputResult(ouputValid))
		{
			Map<String,String> tempMap = new HashMap<String,String>();
			String responseJson=getContentFromFile(testCaseName.listFiles(), "output-1-actual");
			String vidStatus=JsonPrecondtion.getValueFromJson(responseJson, "response.vidStatus");
			String uin="";
			for(Entry<String, String> entry: VidDto.getVid().entrySet())
			{
				if(entry.getValue().contains(vidNumber))
					uin=entry.getKey();
			}
			if(vidStatus.equals("INVALIDATED"))
				vidStatus="INACTIVE";
			tempMap.put(uin, vidNumber+"."+vidType+"."+vidStatus);
			updateMappingDic(RunConfigUtil.getResourcePath()+"ida/TestData/RunConfig/vid.properties", tempMap);
			updateMappingDic(RunConfigUtil.getResourcePath()+"idRepository/TestData/RunConfig/vid.properties", tempMap);
		}
		else
			throw new AuthenticationTestException("Failed at output response validation");
	}

}
