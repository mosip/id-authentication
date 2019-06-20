package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.EncryptData;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.registrationProcessor.util.RegProcTokenGenerate;
import io.mosip.registrationProcessor.util.StageValidationMethods;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;

/**
 * This class is used for testing Packet Receiver API
 * 
 * @author Sayeri Mishra
 *
 */

public class PacketReceiver extends  BaseTestCase implements ITest {	

	private static Logger logger = Logger.getLogger(PacketReceiver.class);	
	protected static String testCaseName = "";
	boolean status = false;
	String finalStatus = "";	
	JSONArray arr = new JSONArray();	
	ObjectMapper mapper = new ObjectMapper();
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	SoftAssert softAssert=new SoftAssert();
	Response actualResponse = null;
	JSONObject expectedResponse = null;
	String dest = "";
	String testSuite = null;
	static String folderPath = "regProc/PacketReceiver";
	static String outputFile = "PacketReceiverOutput.json";
	static String requestKeyFile = "PacketReceiverRequest.json";
	String rId = null;
	static String moduleName="RegProc";
	RegProcTokenGenerate tokenGenearte=new RegProcTokenGenerate();
	String token="";
	Properties prop =  new Properties();
	RegProcApiRequests apiRequests=new RegProcApiRequests();

	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	String validToken="";


	/**
	 * This method is used for generating token
	 * @param tokenType
	 * @return token
	 */
	public String getToken(String tokenType) {
		String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
	}

	/**
	 * This method is used for reading the test data based on the test case name passed
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "packetReceiver")
	public Object[][] readData(ITestContext context){
		String propertyFilePath=System.getProperty("user.dir")+"/"+"src/config/registrationProcessorAPI.properties";
		testLevel=System.getProperty("env.testLevel");
		Object[][] readFolder = null;
		try {
			prop.load(new FileReader(new File(propertyFilePath)));
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
		}catch (IOException | ParseException|IllegalArgumentException|NullPointerException e) {
			Assert.assertTrue(false, "not able to read the folder in PacketReceiver class in readData method: "+ e.getCause());		}
		return readFolder;
	}

	/**
	 * This method is used for generating actual response and comparing it with expected response
	 * along with db check and audit log check
	 * @param testSuite
	 * @param i
	 * @param object
	 */
	@Test(dataProvider="packetReceiver")
	public void packetReceiver(String testSuite, Integer i, JSONObject object){

		File file = null;
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		String configPath = "src/test/resources/" + testSuite + "/";
		File folder = new File(configPath);
		File[] listOfFolders = folder.listFiles();
		JSONObject objectData = new JSONObject();

		EncryptData encryptData=new EncryptData();
		String regId = null;
		JSONObject requestToEncrypt = null;
		RegistrationPacketSyncDTO registrationPacketSyncDto = new RegistrationPacketSyncDTO();

		try {
			JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);	
			expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);

			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("requesttimestamp");
			outerKeys.add("responsetime");
			innerKeys.add("createdDateTime");
			innerKeys.add("updatedDateTime");

			for (int j = 0; j < listOfFolders.length; j++) {
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
			}



			//generation of actual response
			actualResponse = apiRequests.regProcPacketUpload(file, prop.getProperty("packetReceiverApi"),validToken);

			String message = null;
			boolean uploaded = false;
			Response syncResponse = null;
			if(actualResponse.asString().contains("errors")) {
				List<Map<String,String>> error = actualResponse.jsonPath().get("errors");
				for(Map<String,String> err : error){
					message = err.get("message").toString();
				}
				logger.info("message : "+message);
				if(message.matches("The request received is a duplicate request to upload a Packet") 
						&& object.get("testCaseName").toString().matches("PacketReceiver_smoke")) {
					logger.info("Inside duplicate message block ========================");
					uploaded = true;
					finalStatus = "Pass";
					softAssert.assertAll();
					object.put("status", finalStatus);
					arr.add(object);
				}else if(message.matches("Registration packet is not in Sync with Sync table")) {
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
						}

					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}




			//Asserting actual and expected response
			//		status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
			//	Assert.assertTrue(status, "object are not equal");
			if(!uploaded) {
				status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
				Assert.assertTrue(status, "object are not equal");
				if (status) {
					boolean isError = expectedResponse.containsKey("errors");
					logger.info("isError ========= : "+isError);

					if(!isError){
						String actualStatus = null;
						String expectedStatus = null;
						Map<String,String> response = actualResponse.jsonPath().get("response"); 
						JSONObject expected = (JSONObject) expectedResponse.get("response");

						//extracting status from the expected response
						expectedStatus = expected.get("status").toString().trim();



						for(Map.Entry<String,String> res: response.entrySet()){
							if(res.getKey().equals("status"))
								actualStatus =  res.getValue().toString();
							if (expectedStatus.matches(actualStatus)){
								logger.info("STATUS MATCHED....");
								finalStatus = "Pass";
								softAssert.assertAll();
								object.put("status", finalStatus);
								arr.add(object);
							} 
						}

					}else{

						JSONArray expectedError = (JSONArray) expectedResponse.get("errors");
						String expectedErrorCode = null;
						List<Map<String,String>> error = actualResponse.jsonPath().get("errors"); 
						for(Map<String,String> err : error){
							String errorCode = err.get("errorCode").toString();
							Iterator<Object> iterator1 = expectedError.iterator();
							// extracting error code from expected response
							while(iterator1.hasNext()){
								JSONObject jsonObject = (JSONObject) iterator1.next();
								expectedErrorCode = jsonObject.get("errorCode").toString().trim();
							}
							if(expectedErrorCode.matches(errorCode)){
								finalStatus = "Pass";
								softAssert.assertAll();
								object.put("status", finalStatus);
								arr.add(object);
							}
						}
					}
				}else{
					finalStatus="Fail";
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


	/*
	@Test
	public void packetReceiverForSmoke() throws FileNotFoundException, IOException, ParseException {
		testSuite = "regProc/PacketReceiver/PacketReceiver_smoke";
		String propertyFilePath=System.getProperty("user.dir")+"/"+"src/config/registrationProcessorAPI.properties";
		prop.load(new FileReader(new File(propertyFilePath)));
		//JSONObject createRequest = Res.createRequest(testSuite);
		File file = ResponseRequestMapper.mapCreateRequest(testSuite);	
		//logger.info("actualRequest : "+actualRequest);

		actualResponse = apiRequests.regProcPacketUpload(file, prop.getProperty("packetReceiverApi"),validToken);
		logger.info("actualResponse : "+actualResponse);


	}*/

	/**
	 * This method is used for fetching test case name
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
		validToken=getToken("syncTokenGenerationFilePath");
		JSONObject object = (JSONObject) testdata[2];
		String apiName="packetReceiver";
		testCaseName =moduleName+"_"+apiName+"_"+ object.get("testCaseName").toString();
	}

	/**
	 * This method is used for generating report
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {

		Field method = null;
		try {
			method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, PacketReceiver.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in PacketReceiver class in setResultTestName "+e);
			Reporter.log("Exception : " + e.getMessage());
		}


	}

	/**
	 * This method is used for generating output file with the test case result
	 */
	@AfterClass
	public void statusUpdate() {
		String configPath =  "src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
			file.close();
		} catch (IOException e) {
			logger.error("Exception occurred in PacketReceiver class in statusUpdate "+e);
		}
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}
