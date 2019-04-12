package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.dbaccess.RegProcDBCleanUp;
import io.mosip.registrationProcessor.util.StageValidationMethods;
import io.mosip.registrationProcessor.util.TweakRegProcPackets;
import io.mosip.service.BaseTestCase;
import io.mosip.util.SetStageStatusCode;

public class StageValidationTests extends BaseTestCase implements ITest {
	protected static String testCaseName = "";
	StageValidationMethods scenario = new StageValidationMethods();
	SetStageStatusCode codeList = new SetStageStatusCode();
	static List<String> userList = new ArrayList<String>();
	static List<String> dbList = new ArrayList<String>();
	String invalidPacketPath = "";
	String regID="";
	RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
	SoftAssert softAssert=new SoftAssert();
	@BeforeClass
	public void readUserStage() {
		Properties folderPath = new Properties();
		try {
			folderPath.load(
					new FileReader(new File(System.getProperty("user.dir") + "/src/config/folderPaths.properties")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		invalidPacketPath = folderPath.getProperty("invalidPacketFolderPath");
		TweakRegProcPackets e = new TweakRegProcPackets();

		Properties property = new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/src/config/folderPaths.properties";
		FileReader reader = null;
		try {
			reader = new FileReader(new File(propertyFilePath));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			property.load(reader);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String validPacketPath = property.getProperty("pathForValidRegProcPackets");
		String invalidPacketFolderPath = property.getProperty("invalidPacketFolderPath");
		
	  e.packetValidatorPropertyFileReader("packetValidator.properties",validPacketPath, invalidPacketFolderPath);
		 for (int i = 0; i < 2; i++) {
		  e.demoDedupePropertyFileReader("IDjson.properties", validPacketPath, invalidPacketFolderPath); 
		  }
		 e.osiValidatorPropertyFileReader("packetProperties.properties",validPacketPath, invalidPacketFolderPath);
		
		try {
			reader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@DataProvider(name = "packetValidatorStage")
	public File[] getInvalidPacketValidatorPackets() {
		Object[][] reutr = null;
		File file = new File(invalidPacketPath + "/PacketValidator");
		File[] listOfPackets = file.listFiles();
		File[] listOfInvalidPackets = new File[listOfPackets.length - 1];
		for (int i = 0; i < listOfPackets.length; i++) {
			if (listOfPackets[i].isDirectory()) {
				listOfInvalidPackets[i] = listOfPackets[i];
			}
		}
		return listOfInvalidPackets;
	}
	@DataProvider(name="osiValidatorStage")
	public File[] getInvalidOsiValidatorPackets() {
		
		File file = new File(invalidPacketPath + "/OsiValidation");
		File[] listOfPackets = file.listFiles();
		File[] listOfInvalidPackets = new File[listOfPackets.length - 1];
		for (int i = 0; i < listOfPackets.length; i++) {
			if (listOfPackets[i].isDirectory()) {
				listOfInvalidPackets[i] = listOfPackets[i];
			}
		}
		return listOfInvalidPackets;
	}
	@DataProvider(name="demoDedupeStage")
	public File[] getInvalidDemoDedupePackets() {
		
		File file = new File(invalidPacketPath + "/DemoDedupe");
		File[] listOfPackets = file.listFiles();
		File[] listOfInvalidPackets = new File[listOfPackets.length - 1];
		for (int i = 0; i < listOfPackets.length; i++) {
			if (listOfPackets[i].isDirectory()) {
				listOfInvalidPackets[i] = listOfPackets[i];
			}
		}
		return listOfInvalidPackets;
	}
	
	
	@Test(dataProvider = "packetValidatorStage")
	public void packetValidatorStage(File[] listOfInvpackets) {

		List<String> statusCodes = new ArrayList<String>();
		Properties prop = new Properties();

		try {
			prop.load(new FileReader(new File(invalidPacketPath + "/PacketValidator/StageBits.properties")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder stageString = new StringBuilder();
		stageString.append(prop.getProperty("StageBits").toString());
		userList = codeList.getStatusCodesList(stageString);
		for (File invalidPacket : listOfInvpackets) {
			if (invalidPacket.isDirectory()) {
				for (File packet : invalidPacket.listFiles()) {
					regID = packet.getName().substring(0, packet.getName().lastIndexOf('.'));
					scenario.syncPacket(regID);
					scenario.uploadPacket(packet);
					statusCodes = scenario.getStatusList(regID);
					break;
				}
			}
		}
		
		dbList=scenario.getStatusCodeListFromDb(statusCodes);
		logger.info("User list :: "+ userList);
		logger.info("Db list :: "+ dbList);
		softAssert.assertTrue(userList.equals(dbList));
		
		userList.clear();
		dbList.clear();
		softAssert.assertAll();
		cleanUp.prepareQueryList(regID);
		regID="";
	
	}
	
	@Test(dataProvider = "osiValidatorStage")
	public void osiValidatorStage(File[] listOfInvpackets) {
		List<String> statusCodes = new ArrayList<String>();
		Properties prop = new Properties();

		try {
			prop.load(new FileReader(new File(invalidPacketPath + "/DemoDedupe/StageBits.properties")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder stageString = new StringBuilder();
		stageString.append(prop.getProperty("StageBits").toString());
		userList = codeList.getStatusCodesList(stageString);
		for (File invalidPacket : listOfInvpackets) {
			if (invalidPacket.isDirectory()) {
				for (File packet : invalidPacket.listFiles()) {
					 regID = packet.getName().substring(0, packet.getName().lastIndexOf('.'));
					scenario.syncPacket(regID);
					scenario.uploadPacket(packet);
					statusCodes = scenario.getStatusList(regID);
					break;
				}
			}
		}
		dbList=scenario.getStatusCodeListFromDb(statusCodes);
		logger.info("User list :: "+ userList);
		logger.info("Db list :: "+ dbList);
		softAssert.assertTrue(userList.equals(dbList));
		
		userList.clear();
		dbList.clear();
		cleanUp.prepareQueryList(regID);
		softAssert.assertAll();
		regID="";
	}
	@Test(dataProvider = "demoDedupeStage")
	public void demoDedupeStage(File[] listOfInvpackets) {
		List<String> statusCodes = new ArrayList<String>();
		Properties prop = new Properties();

		try {
			prop.load(new FileReader(new File(invalidPacketPath + "/PacketValidator/StageBits.properties")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder stageString = new StringBuilder();
		stageString.append(prop.getProperty("StageBits").toString());
		userList = codeList.getStatusCodesList(stageString);
		for (File invalidPacket : listOfInvpackets) {
			if (invalidPacket.isDirectory()) {
				for (File packet : invalidPacket.listFiles()) {
					regID = packet.getName().substring(0, packet.getName().lastIndexOf('.'));
					scenario.syncPacket(regID);
					scenario.uploadPacket(packet);
					statusCodes = scenario.getStatusList(regID);
					break;
				}
			}
		}
		dbList=scenario.getStatusCodeListFromDb(statusCodes);
		logger.info("User list :: "+ userList);
		logger.info("Db list :: "+ dbList);
		softAssert.assertTrue(userList.equals(dbList));
		
		userList.clear();
		dbList.clear();
		cleanUp.prepareQueryList(regID);
		softAssert.assertAll();
		regID="";
	}
	
	
/*	@AfterTest
	public void compareList() {
		Set<String> uniqueDbList = new LinkedHashSet<>(dbList);
		Assert.assertTrue(userList.equals(uniqueDbList));
		userList.clear();
		dbList.clear();
	}*/

	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
		Object[] obj =  (Object[]) testdata[0];
		String name="";
		if(obj[0] instanceof File) {
			File file=(File) obj[0];
			name=file.getName();
		} 
		testCaseName=name+"Test";
	}

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
			f.set(baseTestMethod, Sync.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in Sync class in setResultTestName method " + e);
		}

	}

	@Override
	public String getTestName() {
		// TODO Auto-generated method stub
		return this.testCaseName;
	}
}