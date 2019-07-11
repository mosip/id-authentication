package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.dbaccess.RegProcDataRead;
import io.mosip.dbaccess.RegProcTransactionDb;
import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.dbdto.SyncRegistrationDto;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.EncryptData;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;

/**
 * This class is used for testing the PrintingStage API
 * 
 * @author Sayeri Mishra
 *
 */

public class PrintingStage extends BaseTestCase implements ITest {
	private final String encrypterURL="/v1/cryptomanager/encrypt";
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(PrintingStage.class);
	boolean status = false;
	String finalStatus = "Fail";
	static Properties prop =  new Properties();
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response actualResponse = null;
	JSONObject expectedResponse = null;
	JSONObject actualRequest=null;
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	String regIds="";
	SoftAssert softAssert=new SoftAssert();
	static String dest = "";
	static String folderPath = "regProc/PrintingStage";
	static String outputFile = "PrintingStageOutput.json";
	static String requestKeyFile = "PrintingStageRequest.json";
	static String description="";
	static String apiName="PrintingStage";
	static String moduleName="RegProc";
	CommonLibrary common=new CommonLibrary();
	
	RegProcApiRequests apiRequests=new RegProcApiRequests();
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	String validToken="";
	
	/**
	 * This method is used for creating token
	 * 
	 * @param tokenType
	 * @return token
	 */
	public String getToken(String tokenType) { String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
		}
	
	
	
	/**
	 *This method is used for reading the test data based on the test case name passed
	 *
	 * @param context
	 * @return Object[][]
	 */
	@DataProvider(name = "printingStage")
	public  Object[][] readData(ITestContext context){ 
		Object[][] readFolder = null;
		String propertyFilePath=apiRequests.getResourcePath()+"config/registrationProcessorAPI.properties";
		try {
			prop.load(new FileReader(new File(propertyFilePath)));
			testLevel=System.getProperty("env.testLevel");
			switch (testLevel) {
			case "smoke":
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
				break;
			case "regression":
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
				break;
			default:
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
			}
		}catch(IOException | ParseException |NullPointerException e){
			Assert.assertTrue(false, "not able to read the folder in PrintingStage class in readData method: "+ e.getCause());
		}
		return readFolder;
	}

	/**
	 * This method is used for generating actual response and comparing it with expected response
	 * along with db check and audit log check
	 *  
	 * @param testSuite
	 * @param i
	 * @param object
	 * @throws java.text.ParseException 
	 */
	@Test(dataProvider = "printingStage")
	public void printingStage(String testSuite, Integer i, JSONObject object) throws java.text.ParseException{
		
		File file = null;
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		String configPath = apiRequests.getResourcePath() + testSuite + "/";
		File folder = new File(configPath);
		File[] listOfFolders = folder.listFiles();
		JSONObject objectData = new JSONObject();

		EncryptData encryptData=new EncryptData();
		String regId = null;
		JSONObject requestToEncrypt = null;
		RegistrationPacketSyncDTO registrationPacketSyncDto = new RegistrationPacketSyncDTO();

		try {
			file=ResponseRequestMapper.getUpdatePacket(testSuite, object);
			expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);

			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("requesttimestamp");
			outerKeys.add("responsetime");
			innerKeys.add("createdDateTime");
			innerKeys.add("updatedDateTime");

			/*for (int j = 0; j < listOfFolders.length; j++) {
				if (listOfFolders[j].isDirectory()) {
					if (listOfFolders[j].getName().equals(object.get("testCaseName").toString())) {
						logger.info("Testcase name is" + listOfFolders[j].getName());
						File[] listOfFiles = listOfFolders[j].listFiles();
						for (File f : listOfFiles) 
							if (f.getName().toLowerCase().contains("request")) {
								objectData = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
								file=new File(f.getParent()+"/"+objectData.get("path"));
								rId = file.getName().substring(0, file.getName().length()-4);
							}
					}
				}
			}*/

			
			validToken=getToken("syncTokenGenerationFilePath");
			boolean tokenStatus=apiRequests.validateToken(validToken);
			while(!tokenStatus) {
				validToken = getToken("syncTokenGenerationFilePath");
				tokenStatus=apiRequests.validateToken(validToken);
			}


			//generation of actual response
			actualResponse = apiRequests.regProcPacketUpload(file, prop.getProperty("packetReceiverApi"),validToken);

			String message = null;
			boolean uploaded = false;
			Response syncResponse = null;
			regId = file.getName().substring(0, file.getName().lastIndexOf("."));
			logger.info("regId : " + regId);
			if(actualResponse.asString().contains("errors") && actualResponse.jsonPath().get("errors")!=null) {
				List<Map<String,String>> error = actualResponse.jsonPath().get("errors");
				for(Map<String,String> err : error){
					message = err.get("message").toString();
				}
				logger.info("message : "+message);
				if(message.matches("Duplicate Request Received")) {
					logger.info("Inside duplicate message block ========================");
					uploaded = true;
				}else if(message.matches("Packet Not Found in Sync Table")) {
					try {
						registrationPacketSyncDto=encryptData.createSyncRequest(file,"NEW");

						regId=registrationPacketSyncDto.getSyncRegistrationDTOs().get(0).getRegistrationId();
						requestToEncrypt=encryptData.encryptData(registrationPacketSyncDto);

						String center_machine_refID=regId.substring(0,5)+"_"+regId.substring(5, 10);
						String encrypterURL = "/v1/cryptomanager/encrypt";
						Response resp=apiRequests.postRequestToDecrypt(encrypterURL ,requestToEncrypt,MediaType.APPLICATION_JSON,
								MediaType.APPLICATION_JSON,validToken);
						String encryptedData = resp.jsonPath().get("response.data").toString();
						LocalDateTime timeStamp = encryptData.getTime(regId);

						syncResponse = apiRequests.regProcSyncRequest(prop.getProperty("syncListApi"),encryptedData,center_machine_refID,
								timeStamp.toString()+"Z", MediaType.APPLICATION_JSON,validToken);

						if(syncResponse.toString().contains("response")) {
							actualResponse = apiRequests.regProcPacketUpload(file, prop.getProperty("packetReceiverApi"),validToken);	
							uploaded =true;
						}

					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}



			if(uploaded) {
				RegProcTransactionDb transaction = new RegProcTransactionDb();
				boolean isPrinted = transaction.printAndPost(regId);
				if(isPrinted) {
					logger.info("Validated in DB....");
					finalStatus = "Pass";
					softAssert.assertTrue(true);
				}else {
					logger.info("Print stage is not present...");
					if(testCaseName.contains("Invalid"))
					{
						finalStatus="Pass";
					}else
						finalStatus = "Fail";
				}
				
			}

			boolean setFinalStatus=false;
			if(finalStatus.equals("Fail"))
				setFinalStatus=false;
			else if(finalStatus.equals("Pass"))
				setFinalStatus=true;
			Verify.verify(setFinalStatus);
			softAssert.assertAll();

		} catch (IOException | ParseException e) {
			Assert.assertTrue(false, "not able to execute packetInfo method : "+ e.getCause());
		}
	}  


	/**
	 * This method is used for fetching test case name
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName =moduleName+"_"+apiName+"_"+ object.get("testCaseName").toString();
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
			f.set(baseTestMethod, PrintingStage.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in PrintingStage class in setResultTestName method "+e);
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}
