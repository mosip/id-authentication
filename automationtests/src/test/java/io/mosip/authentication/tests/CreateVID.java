package io.mosip.authentication.tests;

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
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import io.mosip.authentication.fw.util.RunConfig;

public class CreateVID extends IdaScriptsUtil implements ITest{
	
	private static Logger logger = Logger.getLogger(CreateVID.class);
	private RunConfig objRunConfig = new RunConfig();
	protected static String testCaseName = "";
	private JsonPrecondtion objJsonPrecondtion = new JsonPrecondtion();
	private String TESTDATA_PATH="ida/TestData/UINData";
	private String TESTDATA_FILENAME="dummy.dummy";

	@Parameters({"testType"})
	@BeforeClass
	public void setConfigurations(String testType) {
		objRunConfig.setConfig(TESTDATA_PATH,TESTDATA_FILENAME,testType);
	}
		
	@Test
	public void generateVidForUin() {
		Properties prop = getProperty(
				RunConfig.getUserDirectory() + RunConfig.getSrcPath() + "ida/"+RunConfig.getTestDataFolderName()+"/RunConfig/uin.properties");
		Map<String, String> uinMap = new HashMap<String, String>();
		Map<String, String> vidMap = new HashMap<String, String>();
		for (String str : prop.stringPropertyNames()) {
			uinMap.put(str, prop.getProperty(str));
		}
		for (Entry<String, String> entry : uinMap.entrySet()) {
			if (!(entry.getValue().contains("NoVID") || entry.getValue().contains("novid"))) {
				String url = RunConfig.getEndPointUrl() + RunConfig.getVidGenPath();
				url = url.replace("$uin$", entry.getKey());
				String vidJson = getResponse(url);
				String vid = objJsonPrecondtion.getValueFromJson(vidJson, "vid");
				vidMap.put(vid, entry.getKey());
			}
		}
		generateMappingDic(
				RunConfig.getUserDirectory() + RunConfig.getSrcPath() + "ida/"+RunConfig.getTestDataFolderName()+"/RunConfig/vid.properties",
				vidMap);
	}
	
	@BeforeMethod
	public void testData(Method method, Object[] testData) {
		this.testCaseName = String.format("CreateVID");
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
			f.set(baseTestMethod, CreateVID.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

}
