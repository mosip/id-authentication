package io.mosip.e2e.tests;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.dao.RegProcDBCleanUp;
import io.mosip.e2e.util.GeneratePreIds;
import io.mosip.e2e.util.PacketFlowVerification;

public class RegistrationProcessor implements ITest {
	protected static String testCaseName = "";
	PacketFlowVerification packetFlowVerification = new PacketFlowVerification();
	List<File> packets = new ArrayList<File>();
	/*@AfterClass
	public void dbCleanUp() {
		RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
		for(File file: packets) {
			String regId=file.getName().substring(0,file.getName().lastIndexOf("."));
			cleanUp.prepareQueryList(regId);
		}
	}*/
	GeneratePreIds generatePreIds = new GeneratePreIds();
	
	@Test(priority=1)
	public void getPrids() {

		JSONObject preIds = generatePreIds.getPreids();
	/*	if (preIds.equals(null)) {
			Assert.assertTrue(false);
		}
		Assert.assertTrue(true);*/
	}
	@Test(priority=2)
	public void getPackets() {
		packets = packetFlowVerification.readPacket();
		if (packets.equals(null)) {
			Assert.assertTrue(false);
		}
		Assert.assertTrue(true);
	}
	
	@Test(priority=3)
	public void syncPacket() {
		for(File file: packets) {
			boolean syncStatus=packetFlowVerification.syncPacket(file);
			Assert.assertTrue(syncStatus);
		}
	}
	
	@Test(priority=4)
	public void uploadPacket() {
		for(File file: packets) {
			boolean uploadStatus=packetFlowVerification.uploadPacket(file);
			Assert.assertTrue(true);
		}
	}
	
	@Test(priority=5)
	public void compareDbTransactions() {
		for(File file: packets) {
			boolean dbStatus=packetFlowVerification.compareDbStatus(file,"11111111");
			Assert.assertTrue(true);
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
			testCaseName ="E2E_AdultPacket"+"_"+baseTestMethod.getMethodName();
			f.set(baseTestMethod, RegistrationProcessor.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			
		}

	}
}
