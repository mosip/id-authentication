package io.mosip.e2e.tests;

import java.lang.reflect.Field;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.e2e.util.GeneratePreIds;

public class PreRegistrationTests implements ITest {
	protected static String testCaseName = "";
	GeneratePreIds generatePreIds = new GeneratePreIds();
	
	@Test(priority=1)
	public void getPrids() {

		JSONObject preIds = generatePreIds.getPreids();
		if (preIds.equals(null)) {
			Assert.assertTrue(false);
		}
		Assert.assertTrue(true);
	}
	
	@Override
	public String getTestName() {
		return this.testCaseName;
	}
	/*	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx){
		
		JSONObject object = (JSONObject) testdata[2];
		testCaseName ="PreRegistration"+"_"+"Get Packets"+"_"+ object.get("testCaseName").toString();
	}*/

	/**
	 * This method is used for generating report
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		
		Field method;
		try {
			method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			testCaseName ="E2E_PreRegistration"+"_"+baseTestMethod.getMethodName();
			f.set(baseTestMethod, PreRegistrationTests.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			
		}

	}
}
