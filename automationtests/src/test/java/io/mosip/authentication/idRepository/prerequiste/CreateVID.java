package io.mosip.authentication.idRepository.prerequiste;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.AuthenticationTestException;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.testdata.TestDataProcessor;

/**
 * Test to generate VID for UIN
 * 
 * @author Athila
 *
 */
public class CreateVID extends AuthTestsUtil implements ITest {

	private static final Logger logger = Logger.getLogger(CreateVID.class);
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
	 * Test method for VID generation
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws AuthenticationTestException 
	 */
	@Test
	public void generateVidForUin() throws AuthenticationTestException {
		String cookieValue = getAuthorizationCookie(getCookieRequestFilePathForUinGenerator(),
				RunConfigUtil.objRunConfig.getIdRepoEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey(),
				AUTHORIZATHION_COOKIENAME);
		Properties prop = getPropertyFromFilePath(new File(RunConfigUtil.getResourcePath() + "/ida/"
				+ RunConfigUtil.objRunConfig.getTestDataFolderName() + "/RunConfig/uin.properties").getAbsolutePath());
		Map<String, String> uinMap = new HashMap<String, String>();
		Map<String, String> vidMap = new HashMap<String, String>();
		boolean status=true;
		for (String str : prop.stringPropertyNames()) {
			uinMap.put(str, prop.getProperty(str));
		}
		for (Entry<String, String> entry : uinMap.entrySet()) {
			if (!(entry.getValue().contains("NoVID") || entry.getValue().contains("Deactivated")
					|| entry.getValue().contains("novid"))) {
				String url = RunConfigUtil.objRunConfig.getEndPointUrl()
						+ RunConfigUtil.objRunConfig.getIdRepoCreateVIDRecordPath();
				String reqVidJson = JsonPrecondtion.parseAndReturnJsonContent(getVidRequestContent().toString(),
						"LONG:" + entry.getKey(), "request.UIN".toString());
				if (entry.getValue().contains("Temporary") || entry.getValue().contains("temporary"))
					reqVidJson = JsonPrecondtion
							.parseAndReturnJsonContent(
									reqVidJson, TestDataProcessor.getYamlData("ida", "TestData/RunConfig",
											"authenitcationTestdata", "valid_VID_T_Type"),
									"request.vidType".toString());
				else
					reqVidJson = JsonPrecondtion
							.parseAndReturnJsonContent(
									reqVidJson, TestDataProcessor.getYamlData("ida", "TestData/RunConfig",
											"authenitcationTestdata", "valid_VID_P_Type"),
									"request.vidType".toString());

				String resVIDJson = postRequestAndGetResponseForVIDGeneration(reqVidJson, url,
						AUTHORIZATHION_COOKIENAME, cookieValue);
				if (resVIDJson.contains("\"VID\":")) {
					String vid = JsonPrecondtion.getValueFromJson(resVIDJson, "response.VID");
					String vidType = JsonPrecondtion.getValueFromJson(reqVidJson, "request.vidType");
					vidMap.put(entry.getKey(), vid + "." + vidType + ".ACTIVE");
				}
				else
					status=false;
			}
		}
		generateMappingDic(new File(RunConfigUtil.getResourcePath() + "ida/"
				+ RunConfigUtil.objRunConfig.getTestDataFolderName() + "/RunConfig/vid.properties").getAbsolutePath(),
				vidMap);
		generateMappingDic(new File(RunConfigUtil.getResourcePath() + "idRepository/"
				+ RunConfigUtil.objRunConfig.getTestDataFolderName() + "/RunConfig/vid.properties").getAbsolutePath(),
				vidMap);
		if(!status)
			throw new AuthenticationTestException("Create VID failed. This may lead to failure for some authentication test execution");
	}

	/**
	 * The method set test case name
	 * 
	 * @param method
	 * @param testData
	 */
	@BeforeMethod
	public void testData(Method method, Object[] testData) {
		this.testCaseName = String.format("Authenticaiton_CreateVID");
		invocationCount++;
		setTestDataPathsAndFileNames(invocationCount);
		setConfigurations(this.testType);
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
			f.set(baseTestMethod, CreateVID.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}
}
