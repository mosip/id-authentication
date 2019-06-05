package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.dbaccess.RegProcDataRead;
import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.dbdto.SyncRegistrationDto;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.EncryptData;
import io.mosip.registrationProcessor.util.HashSequenceUtil;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.registrationProcessor.util.StageValidationMethods;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.EncrypterDecrypter;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;

/**
 * This class is used for testing the Sync API
 * 
 * @author Sayeri Mishra
 *
 */

public class UpdatePacket extends BaseTestCase implements ITest {
	private final String encrypterURL="/v1/cryptomanager/encrypt";
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(UpdatePacket.class);
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
	static String folderPath = "regProc/UpdatePacket";
	static String outputFile = "UpdatePacketOutput.json";
	static String requestKeyFile = "UpdatePacketRequest.json";
	static String description="";
	static String apiName="UpdatePacketApi";
	static String moduleName="RegProc";
	RegProcApiRequests apiRequests=new RegProcApiRequests();
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	//StageValidationMethods apiRequest=new StageValidationMethods();
	String validToken="";
	public String getToken(String tokenType) {
		String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
	}
	CommonLibrary common=new CommonLibrary();


	/**
	 *This method is used for reading the test data based on the test case name passed
	 *
	 * @param context
	 * @return Object[][]
	 */
	@DataProvider(name = "updatePacketPacket")
	public  Object[][] readData(ITestContext context){ 
		Object[][] readFolder = null;
		String propertyFilePath=System.getProperty("user.dir")+"\\"+"src\\config\\RegistrationProcessorApi.properties";
		try {
			prop.load(new FileReader(new File(propertyFilePath)));
			String testParam = context.getCurrentXmlTest().getParameter("testType");
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
			logger.error("Exception occurred in UpdatePacket class in readData method "+e);
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
	@Test(dataProvider = "updatePacketPacket")
	public void updatePacket(String testSuite, Integer i, JSONObject object) throws java.text.ParseException{
		ObjectMapper mapper=new ObjectMapper();
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		RegProcDataRead readDataFromDb = new RegProcDataRead();

		EncryptData encryptData=new EncryptData();
		String regId = null;
		JSONObject requestToEncrypt = null;
		File file=ResponseRequestMapper.getPacket(testSuite, object);
		RegistrationPacketSyncDTO registrationPacketSyncDto = new RegistrationPacketSyncDTO();
		try{
			if(file!=null){
				registrationPacketSyncDto=encryptData.createSyncRequest(file);

				regId=registrationPacketSyncDto.getSyncRegistrationDTOs().get(0).getRegistrationId();

				requestToEncrypt=encryptData.encryptData(registrationPacketSyncDto);
			}
			else {
				actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
				JSONArray request = (JSONArray) actualRequest.get("request");
				for(int j = 0; j<request.size() ; j++){
					JSONObject obj  = (JSONObject) request.get(j);
					regId = obj.get("registrationId").toString();
					registrationPacketSyncDto = encryptData.createSyncRequest(actualRequest);
					requestToEncrypt = encryptData.encryptData(registrationPacketSyncDto);
				}
			}

			String center_machine_refID=regId.substring(0,5)+"_"+regId.substring(5, 10);
			Response resp=apiRequests.postRequestToDecrypt(encrypterURL,requestToEncrypt,MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON,validToken);
			String encryptedData = resp.jsonPath().get("response.data").toString();
			LocalDateTime timeStamp = encryptData.getTime(regId);

			// Actual response generation
			logger.info("sync API url : "+prop.getProperty("syncListApi"));
			actualResponse = apiRequests.regProcSyncRequest(prop.getProperty("syncListApi"),encryptedData,center_machine_refID,
					timeStamp.toString()+"Z", MediaType.APPLICATION_JSON,validToken);

			String status = null;
			List<Map<String,String>> response = actualResponse.jsonPath().get("response"); 
			for(Map<String,String> res : response){
				status=res.get("status").toString();
				logger.info("status is : " +status);
			}
			Response uploadPacketResponse  = null;
			String uploadStatus = null;
			if(status.equalsIgnoreCase("SUCCESS")) {
				uploadPacketResponse = apiRequests.regProcPacketUpload(file, prop.getProperty("packetReceiverApi"), validToken);
				boolean isError = uploadPacketResponse.asString().contains("errors");
				logger.info("isError : "+isError);
				if(!isError) {
					List<Map<String,String>> uploadResponse = uploadPacketResponse.jsonPath().get("response"); 
					for(Map<String,String> res : uploadResponse){
						uploadStatus=res.get("status").toString();
						if (uploadStatus.matches("\"Packet is in PACKET_RECEIVED status\"")){
							logger.info("Packet Uploaded ...........");
						} 
					}
				}else {
					List<Map<String,String>> error = uploadPacketResponse.jsonPath().get("errors"); 
					logger.info("error : "+error);
					for(Map<String,String> err : error){
						String errorCode = err.get("errorCode").toString();
						logger.info("errorCode : "+errorCode);
						if(errorCode.matches("RPR-PKR-005")) {
							logger.info("Packet Already Uploaded ...........");
						}
					}
				}
				String idRepoUrl = prop.getProperty("idRepoByRid") + regId + "?type=all" ;
				Response idRepoResponse = apiRequests.regProcGetIdRepo(idRepoUrl, validToken);
				Long uin = null;
				Map<String, Object> identity = null ;
				Map<String,Map<String,Object>> idRepoResponseBody = idRepoResponse.jsonPath().get("response"); 
				for(Map.Entry<String,Map<String,Object>> entry : idRepoResponseBody.entrySet()){
					if(entry.getKey().matches("identity")) {
						identity = entry.getValue();
						for(Map.Entry<String, Object> idObj : identity.entrySet()) {
							if(idObj.getKey().matches("UIN")) {
								uin = (Long) idObj.getValue();
								logger.info("UIN : "+uin);
							}
						}
					}
				}
			}

		}catch(IOException | ParseException |NullPointerException | IllegalArgumentException e){
			logger.error("Exception occurred in UpdatePacket class in updatePacket method "+e);

		}
	}  


	/**
	 * This method is used for fetching test case name
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx){
		validToken=getToken("syncTokenGenerationFilePath");
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
			f.set(baseTestMethod, Sync.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in Sync class in setResultTestName method "+e);
		}


		/*		if(result.getStatus()==ITestResult.SUCCESS) {
				Markup m=MarkupHelper.createCodeBlock("Request Body is  :"+System.lineSeparator()+actualRequest.toJSONString());
				Markup m1=MarkupHelper.createCodeBlock("Expected Response Body is  :"+System.lineSeparator()+expectedResponse.toJSONString());
				test.log(Status.PASS, m);
				test.log(Status.PASS, m1);
			}

			if(result.getStatus()==ITestResult.FAILURE) {
				Markup m=MarkupHelper.createCodeBlock("Request Body is  :"+System.lineSeparator()+actualRequest.toJSONString());
				Markup m1=MarkupHelper.createCodeBlock("Expected Response Body is  :"+System.lineSeparator()+expectedResponse.toJSONString());
				test.log(Status.FAIL, m);
				test.log(Status.FAIL, m1);
			}
			if(result.getStatus()==ITestResult.SKIP) {
				Markup m=MarkupHelper.createCodeBlock("Request Body is  :"+System.lineSeparator()+actualRequest.toJSONString());
				Markup m1=MarkupHelper.createCodeBlock("Expected Response Body is  :"+System.lineSeparator()+expectedResponse.toJSONString());
				test.log(Status.SKIP, m);
				test.log(Status.SKIP, m1);
			}*/
	}

	/**
	 * This method is used for generating output file with the test case result
	 */
	@AfterClass
	public void statusUpdate(){
		String configPath =  "src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			file.close();
			logger.info("Successfully updated Results to " + outputFile);
		} catch (IOException e) {
			logger.error("Exception occurred in Sync method in statusUpdate method "+e);
		}
		String source = "src/test/resources/" + folderPath + "/";
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}
