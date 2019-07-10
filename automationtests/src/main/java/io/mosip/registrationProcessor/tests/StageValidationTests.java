 package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.dbaccess.RegProcDBCleanUp;
import io.mosip.dbaccess.RegProcTransactionDb;
import io.mosip.registrationProcessor.service.IntegMethods;
import io.mosip.registrationProcessor.util.EncryptData;
import io.mosip.registrationProcessor.util.PacketFlowStatus;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.registrationProcessor.util.StageValidationMethods;
import io.mosip.registrationProcessor.util.TweakRegProcPackets;
import io.mosip.service.BaseTestCase;
import io.mosip.util.SetStageStatusCode;

public class StageValidationTests extends BaseTestCase implements ITest {
	protected static String testCaseName = "";
	StageValidationMethods scenario = new StageValidationMethods();
	IntegMethods regProcRequests=new IntegMethods();
	SetStageStatusCode codeList = new SetStageStatusCode();
	static List<String> userList = new ArrayList<String>();
	static List<String> dbList = new ArrayList<String>();
	String invalidPacketPath = "";
	String regID="";
	RegProcDBCleanUp cleanUp=new RegProcDBCleanUp();
	SoftAssert softAssert=new SoftAssert();
	static String moduleName="RegProc";
	static String apiName="Stagevalidation";
	EncryptData encryptData=new EncryptData();
	PacketFlowStatus packetFlow=new PacketFlowStatus();
	RegProcTransactionDb transaction=new RegProcTransactionDb();
	@BeforeClass
	public void readUserStage() {
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		Properties folderPath = new Properties();
		try {
			FileReader reader=new FileReader(new File(apiRequests.getResourcePath()+"config/folderPaths.properties"));
			folderPath.load(reader);
			reader.close();
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		invalidPacketPath = apiRequests.getResourcePath()+folderPath.getProperty("invalidPacketFolderPath");
		TweakRegProcPackets e = new TweakRegProcPackets();

		Properties property = new Properties();
		String propertyFilePath =  apiRequests.getResourcePath()+"config/folderPaths.properties";
		FileReader reader = null;
		try {
			reader = new FileReader(new File(propertyFilePath));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			property.load(reader);
			reader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String validPacketPath =apiRequests.getResourcePath()+ property.getProperty("validPacketForPacketGeneration");
		String invalidPacketFolderPath = apiRequests.getResourcePath()+property.getProperty("invalidPacketFolderPath");
		String updatePacketFolderPath = apiRequests.getResourcePath()+property.getProperty("updatedPacketFolderPath");
		File file=new File(invalidPacketFolderPath);
		File[] listOfFiles=file.listFiles();
		/*if(listOfFiles.length==0) {
		e.packetValidatorPropertyFileReader("packetValidator.properties", validPacketPath, invalidPacketFolderPath);

		e.osiValidatorPropertyFileReader("packetProperties.properties", validPacketPath, invalidPacketFolderPath);


		}*/
		e.updatePacketPropertyFileReader("updatePacketProperties.properties",validPacketPath,updatePacketFolderPath);


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
		List<File> listOfInvalidPackets = new ArrayList<File>();
		for (int i = 0; i < listOfPackets.length; i++) {
			if (listOfPackets[i].isDirectory()) {
				listOfInvalidPackets.add(listOfPackets[i]);
			}
		}
		File [] objArray = new File[listOfInvalidPackets.size()];
		for(int i=0;i< listOfInvalidPackets.size();i++){
		    objArray[i] = listOfInvalidPackets.get(i);
		 } 
		return objArray;
		
	}
	@DataProvider(name="osiValidatorStage")
	public File[] getInvalidOsiValidatorPackets() {
		
		File file = new File(invalidPacketPath + "/OsiValidation");
		File[] listOfPackets = file.listFiles();
		List<File> listOfInvalidPackets = new ArrayList<File>();
		for (int i = 0; i < listOfPackets.length; i++) {
			if (listOfPackets[i].isDirectory()) {
				listOfInvalidPackets.add(listOfPackets[i]);
			}
		}
		File [] objArray = new File[listOfInvalidPackets.size()];
		for(int i=0;i< listOfInvalidPackets.size();i++){
		    objArray[i] = listOfInvalidPackets.get(i);
		 } 
		return objArray;
	}

	

	
	
	@Test(dataProvider = "packetValidatorStage")
	public void packetValidatorStage(File[] listOfInvpackets) {

		List<String> statusCodes = new ArrayList<String>();
		Properties prop = new Properties();

		try {
			FileReader reader=new FileReader(new File(invalidPacketPath + "/PacketValidator/StageBits.properties"));
			prop.load(reader);
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder stageString = new StringBuilder();
		stageString.append(prop.getProperty("StageBits").toString());
		List<Integer> stageList=packetFlow.getList(stageString.toString());
		List<String> expectedTransactionList=packetFlow.setCharacter(stageList);
		userList=expectedTransactionList;
		
		for (File invalidPacket : listOfInvpackets) {
			if (invalidPacket.isDirectory()) {
				for (File packet : invalidPacket.listFiles()) {
					boolean syncStatus=false;
					try {
						syncStatus = regProcRequests.syncList(packet);
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(syncStatus) {
						scenario.uploadPacket(packet);
					}
					regID=packet.getName().substring(0,packet.getName().lastIndexOf("."));
					break;
				}
			}
		}
		
		dbList=transaction.readStatus(regID);
		regID="";
		logger.info("User list :: "+ userList);
		logger.info("Db list :: "+ dbList);
		boolean listStatus=false;
		for(int i=0;i<dbList.size();i++) {
			if(dbList.get(i).equals(userList.get(i)))
				listStatus=true;
			else
				listStatus=false;
		}
		softAssert.assertTrue(listStatus);
		userList.clear();
		dbList.clear();
		softAssert.assertAll();
		//cleanUp.prepareQueryList(regID);
		regID="";
	
	}
	
	@Test(dataProvider = "osiValidatorStage")
	public void osiValidatorStage(File[] listOfInvpackets) {
		List<String> statusCodes = new ArrayList<String>();
		Properties prop = new Properties();

		try {
			FileReader reader=new FileReader(new File(invalidPacketPath + "/OsiValidation/StageBits.properties"));
			prop.load(reader);
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder stageString = new StringBuilder();
		stageString.append(prop.getProperty("StageBits").toString());
		List<Integer> stageList=packetFlow.getList(stageString.toString());
		List<String> expectedTransactionList=packetFlow.setCharacter(stageList);
		userList=expectedTransactionList;
		
		for (File invalidPacket : listOfInvpackets) {
			if (invalidPacket.isDirectory()) {
				for (File packet : invalidPacket.listFiles()) {
					boolean syncStatus=false;
					try {
						syncStatus = regProcRequests.syncList(packet);
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(syncStatus) {
						scenario.uploadPacket(packet);
					}
					regID=packet.getName().substring(0,packet.getName().lastIndexOf("."));
					break;
				}
			}
		}
		dbList=transaction.readStatus(regID);
		regID="";
		logger.info("User list :: "+ userList);
		logger.info("Db list :: "+ dbList);
		boolean listStatus=false;
		for(int i=0;i<dbList.size()-1;i++) {
			if(dbList.get(i).equals(userList.get(i)))
				listStatus=true;
			else
				listStatus=false;
		}
		softAssert.assertTrue(listStatus);
		
		userList.clear();
		dbList.clear();

		regID="";
	}
/*@Test(dataProvider = "demoDedupeStage")
	public void demoDedupeStage(File[] listOfInvpackets) {
		List<String> statusCodes = new ArrayList<String>();
		Properties prop = new Properties();

		try {
			FileReader reader=new FileReader(new File(invalidPacketPath + "/PacketValidator/StageBits.properties"));
			prop.load(reader);
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder stageString = new StringBuilder();
		stageString.append(prop.getProperty("StageBits").toString());
		
		List<Integer> stageList=packetFlow.getList(stageString.toString());
		List<String> expectedTransactionList=packetFlow.setCharacter(stageList);
		userList=expectedTransactionList;
		for (File invalidPacket : listOfInvpackets) {
			if (invalidPacket.isDirectory()) {
				for (File packet : invalidPacket.listFiles()) {
					boolean syncStatus=false;
					try {
						syncStatus = regProcRequests.syncList(packet);
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(syncStatus) {
						scenario.uploadPacket(packet);
					}
					regID=packet.getName().substring(0,packet.getName().lastIndexOf("."));
					break;
				}
			}
		}
		dbList=transaction.readStatus(regID);
		regID="";
		logger.info("User list :: "+ userList);
		logger.info("Db list :: "+ dbList);
		
		userList.clear();
		dbList.clear();
	//	cleanUp.prepareQueryList(regID);
		boolean listStatus=false;
		for(int i=0;i<dbList.size();i++) {
			if(dbList.get(i).equals(userList.get(i)))
				listStatus=true;
			else
				listStatus=false;
		}
		softAssert.assertTrue(listStatus);
		softAssert.assertAll();
		regID="";
	}*/


	@BeforeMethod(alwaysRun=true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
		Object[] obj =  (Object[]) testdata[0];
		String name="";
		String stageName="";
		if(obj[0] instanceof File) {
			File file=(File) obj[0];
			name=file.getName();
			stageName=file.getParentFile().getName();
		} 
		testCaseName =moduleName+"_"+apiName+"_"+ name+"_"+stageName.toString();
		
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
			f.set(baseTestMethod, StageValidationTests.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in Sync class in setResultTestName method " + e);
		}
		
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}