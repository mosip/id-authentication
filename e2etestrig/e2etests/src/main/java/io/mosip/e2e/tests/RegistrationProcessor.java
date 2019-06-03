package io.mosip.e2e.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.e2e.util.PacketFlowVerification;

public class RegistrationProcessor implements ITest {
	protected static String testCaseName = "";
	PacketFlowVerification packetFlowVerification = new PacketFlowVerification();
	List<File> packets = new ArrayList<File>();

	@Test
	public void getPackets() {
		packets = packetFlowVerification.readPacket();
		if (packets.equals(null)) {
			Assert.assertTrue(false);
		}
		Assert.assertTrue(true);
	}
	
	@Test
	public void syncPacket() {
		for(File file: packets) {
			boolean syncStatus=packetFlowVerification.syncPacket(file);
			Assert.assertTrue(syncStatus);
		}
	}
	
	@Test
	public void uploadPacket() {
		for(File file: packets) {
			boolean uploadStatus=packetFlowVerification.uploadPacket(file);
			Assert.assertTrue(uploadStatus);
		}
	}
	
	@Test
	public void compareDbTransactions() {
		for(File file: packets) {
			boolean dbStatus=packetFlowVerification.compareDbStatus(file,"11111111");
			Assert.assertTrue(dbStatus);
		}
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
			testCaseName ="E2E_RegistrationProcessor"+"_"+baseTestMethod.getMethodName();
			f.set(baseTestMethod, RegistrationProcessor.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			
		}

	}
}
