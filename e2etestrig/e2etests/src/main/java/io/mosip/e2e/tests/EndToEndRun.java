package io.mosip.e2e.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.dao.RegProcDBCleanUp;
import io.mosip.e2e.util.BaseUtil;
import io.mosip.e2e.util.GeneratePreIds;
import io.mosip.e2e.util.PacketFlowVerification;
import io.mosip.e2e.util.PacketGenerator;
import io.mosip.e2e.util.Stagevalidations;
import io.mosip.e2e.util.TestRigException;
import io.mosip.service.RegistrationTransactionData;
public class EndToEndRun implements ITest {
	Stagevalidations stageValidations=new Stagevalidations();
	Map<String,String> uinMap=new HashMap<String,String>();
	String propertyFilePath=BaseUtil.getGlobalResourcePath()+"/src/main/resources/idRepository/TestData/RunConfig/uin.properties";
	Properties prop=new Properties();
	JSONObject preIds=null;
	PacketGenerator packetGenerator=new PacketGenerator();
	protected static String testCaseName = "";
	PacketFlowVerification packetFlowVerification = new PacketFlowVerification();
	List<File> packets = new ArrayList<File>();
	RegistrationTransactionData registartionTransactionData=new RegistrationTransactionData();
	@DataProvider(name="packets")
	public  Object[][] getSetOfPackets()
	{
		return packetGenerator.getPackets();

	}
	/**
	 * Db clean up of packets
	 * @throws IOException 
	 */
	@AfterClass 
	public void dbCleanUp() throws IOException {
		FileOutputStream fileStream=new FileOutputStream(new File(propertyFilePath));
		prop.putAll(uinMap);
		prop.store(fileStream, null);
		fileStream.close();
		RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
		for(File file: packets) {
			String regId=file.getName().substring(0,file.getName().lastIndexOf("."));
			cleanUp.prepareQueryList(regId);
		}
	}
	GeneratePreIds generatePreIds = new GeneratePreIds();

	@Test(dataProvider = "packets",priority=1)
	public void regProcSyncPacket(Object[] test) {
			
			boolean syncStatus=packetFlowVerification.syncPacket((File)test[1]);
			Assert.assertTrue(syncStatus);
	}

	@Test(dataProvider = "packets",priority=2)
	public void regProcUploadPacket(Object[] test) {
		
			boolean uploadStatus=packetFlowVerification.uploadPacket((File)test[1]);
			Assert.assertTrue(uploadStatus);
	}
	

	@Test(dataProvider = "packets",priority=3)
	public void regProcValidation(Object[] test) throws InterruptedException, TestRigException {
		Thread.sleep(60000);
		File packet=new File(test[1].toString());
		String regId=packet.getName().substring(0, packet.getName().lastIndexOf('.'));
		try{Assert.assertTrue(stageValidations.readStatus(regId));}catch (AssertionError e) {
			
			throw new TestRigException("Exception In Registration Processor ---> "+ stageValidations.readStatusComment(regId));
		}
		
	}

	@Test(dataProvider="packets",priority=4)
	public void uinGeneration(Object[] test) throws IOException, TestRigException {
		File packet=(File)test[1];
		String regId=packet.getName().substring(0,packet.getName().lastIndexOf('.'));
		try {
			String uin=packetFlowVerification.getUin(regId);
			uinMap.put(uin, test[0].toString()+"_valid");
			Assert.assertTrue(true);
		} catch (Exception e) {
			throw new TestRigException("Exception In Registration Processor ---> "+ stageValidations.readStatusComment(regId));
			
		}
	}
	
	@Override
	public String getTestName() {
		return this.testCaseName;
	}
	
@BeforeMethod
public void getTestCaseName(Object[] test) {
	Object[] fileName=(Object[])test[0];
	String name=(String) fileName[0];
	testCaseName=name;
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
			testCaseName ="E2ERun"+"_"+testCaseName+"_"+baseTestMethod.getMethodName();
			f.set(baseTestMethod, EndToEndRun.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			
		}

	}
}
