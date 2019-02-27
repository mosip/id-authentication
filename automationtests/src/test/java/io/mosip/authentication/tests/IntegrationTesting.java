package io.mosip.authentication.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.authentication.fw.util.DataProviderClass;
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.TestParameters;

public class IntegrationTesting extends IdaScriptsUtil implements ITest {
	
	private static Logger logger = Logger.getLogger(IntegrationTesting.class);
	private RunConfig objRunConfig = new RunConfig();
	private DataProviderClass objDataProvider = new DataProviderClass();
	protected static String testCaseName = "";
	
	@BeforeClass
	public void setConfigurations() {
		objRunConfig.setConfig("dummy","dummy.dummy","smokeandregression");
	}
	
	public Map<String, String> getUinNumber() {
		Properties prop = getProperty(
				RunConfig.getUserDirectory() + RunConfig.getSrcPath() + "ida/IntegrationTestData/uin.properties");
		Map<String, String> uinMap = new HashMap<String, String>();
		for (String str : prop.stringPropertyNames()) {
			uinMap.put(str, prop.getProperty(str));
		}
		return uinMap;
	}
	
	@DataProvider(name = "uinList")
	public Object[][] getTestCaseList() {
		return objDataProvider.getIntegTestDataProvider(getUinNumber());
	}
	
	@Override
	public String getTestName() {
		return this.testCaseName;
	} 
	
	@BeforeMethod
	public void testData(Method method, Object[] testData) {
		String testCase = "";
		if (testData != null && testData.length > 0) {
			testCase=testData[0].toString()+"_"+testData[1].toString();
		}
		this.testCaseName = String.format(testCase);
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
			f.set(baseTestMethod, IntegrationTesting.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	} 
	
	@Test(dataProvider = "uinList")
	public void startIntegTesting(String uinKey,String uinValue) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(uinKey, uinValue);
			generateMappingDic(RunConfig.getUserDirectory() + RunConfig.getSrcPath()
					+ "ida/IntegrationTestData/RunConfig/uin.properties", map);
			startIntegTestSuite();
	}

	public void startIntegTestSuite()
	{
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		suites.add(RunConfig.getUserDirectory() + "/testngIntegrationTestingSuite.xml");
		testng.setTestSuites(suites);
		testng.getReporters();
		testng.run();
	}

}
