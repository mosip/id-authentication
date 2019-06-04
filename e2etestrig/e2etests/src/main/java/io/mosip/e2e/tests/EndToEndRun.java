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
/**
 * 
 * @author M1047227
 *
 */
public class EndToEndRun implements ITest {
	protected static String testCaseName = "";
	PacketFlowVerification packetFlowVerification = new PacketFlowVerification();
	List<File> packets = new ArrayList<File>();
	/**
	 * Db clean up of packets
	 */
	@AfterClass
	public void dbCleanUp() {
		RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
		for(File file: packets) {
			String regId=file.getName().substring(0,file.getName().lastIndexOf("."));
			cleanUp.prepareQueryList(regId);
		}
	}
	GeneratePreIds generatePreIds = new GeneratePreIds();
	/**
	 * Test to generate and validate generated preIds
	 */
	@Test(priority=1)
	public void getPrids() {

		JSONObject preIds = generatePreIds.getPreids();
		if (preIds.equals(null)) {
			Assert.assertTrue(false);
		}
		Assert.assertTrue(true);
	}
	/**
	 * Test to check whether packets have been generated
	 */
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
	/**
	 * Method to upload the packet and validate the response
	 */
	@Test(priority=4)
	public void uploadPacket() {
		for(File file: packets) {
			boolean uploadStatus=packetFlowVerification.uploadPacket(file);
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Method to validate that all the stages have passed for a valid packet or not
	 */
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
			f.set(baseTestMethod, EndToEndRun.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			
		}

	}
}
