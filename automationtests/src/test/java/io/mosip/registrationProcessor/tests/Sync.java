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

public class Sync extends BaseTestCase implements ITest {
	private final String encrypterURL="/v1/cryptomanager/encrypt";
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(Sync.class);
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
	static String folderPath = "regProc/Sync";
	static String outputFile = "SyncOutput.json";
	static String requestKeyFile = "SyncRequest.json";
	static String description="";
	static String apiName="SyncApi";
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
	@DataProvider(name = "syncPacket")
	public  Object[][] readData(ITestContext context){ 
		Object[][] readFolder = null;
		String propertyFilePath=System.getProperty("user.dir")+"/"+"src/config/registrationProcessorApi.properties";
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
			logger.error("Exception occurred in Sync class in readData method "+e);
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
	@Test(dataProvider = "syncPacket")
	public void sync(String testSuite, Integer i, JSONObject object) throws java.text.ParseException{
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


			// Expected response generation
			expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);

			// Actual response generation
			logger.info("sync API url : "+prop.getProperty("syncListApi"));
			actualResponse = apiRequests.regProcSyncRequest(prop.getProperty("syncListApi"),encryptedData,center_machine_refID,
					timeStamp.toString()+"Z", MediaType.APPLICATION_JSON,validToken);

			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("requesttime");
			outerKeys.add("responsetime");
			innerKeys.add("createdDateTime");
			innerKeys.add("updatedDateTime");

			//Assertion of actual and expected response
			status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);

			logger.info("Status after assertion : "+status);

			if (status) {

				boolean isError = expectedResponse.containsKey("errors");
				logger.info("isError ========= : "+isError);

				if(!isError){
					List<Map<String,String>> response = actualResponse.jsonPath().get("response"); 
					logger.info("response : "+response );
					JSONArray expected = (JSONArray) expectedResponse.get("response");
					List<String> expectedRegIds = new ArrayList<>();
					String expectedRegId = null;
					logger.info("expected: "+expected);
					Iterator<Object> iterator = expected.iterator();
					//extracting reg ids from the expected response
					while(iterator.hasNext()){
						JSONObject jsonObject = (JSONObject) iterator.next();
						expectedRegId = jsonObject.get("registrationId").toString().trim();
						logger.info("expectedRegId: "+expectedRegId);
						expectedRegIds.add(expectedRegId);
					}

					for(Map<String,String> res : response){
						regIds=res.get("registrationId").toString();
						logger.info("Reg Id is : " +regIds);

						SyncRegistrationDto dbDto = readDataFromDb.regproc_dbDataInRegistrationList(regIds);	
						List<Object> count = readDataFromDb.countRegIdInRegistrationList(regIds);
						logger.info("dbDto :" +dbDto);

						//Checking audit logs (not yet implemented)
						/*	LocalDateTime logTime = LocalDateTime.of(2019,Month.JANUARY,30,10,15,51,270000000);   //2019-01-30 10:15:51.27					
							logger.info("log time : "+logTime);
							AuditRequestDto auditDto = RegProcDataRead.regproc_dbDataInAuditLog(regIds, "REGISTRATION_ID", "REGISTRATION_PROCESSOR", "GET",logTime);
							logger.info("AUDIT DTO : "+auditDto.getApplicationName());*/

						if(dbDto != null && count.isEmpty()/*&& auditDto != null*/) {
							//if reg id present in response and reg id fetched from table matches, then it is validated
							if (expectedRegIds.contains(dbDto.getRegistrationId())/*&& expectedRegIds.contains(auditDto.getId())*/){

								logger.info("Validated in DB.......");
								finalStatus = "Pass";
								softAssert.assertTrue(true);
							} 
						}

					}
				}else{
					JSONArray expectedError = (JSONArray) expectedResponse.get("errors");
					String expectedErrorCode = null;
					List<Map<String,String>> error = actualResponse.jsonPath().get("errors"); 
					logger.info("error : "+error );
					for(Map<String,String> err : error){
						String errorCode = err.get("errorCode").toString();
						logger.info("errorCode : "+errorCode);
						Iterator<Object> iterator1 = expectedError.iterator();

						while(iterator1.hasNext()){
							JSONObject jsonObject = (JSONObject) iterator1.next();
							expectedErrorCode = jsonObject.get("errorCode").toString().trim();
							logger.info("expectedErrorCode: "+expectedErrorCode);
						}
						if(expectedErrorCode.matches(errorCode)){
							finalStatus = "Pass";
							softAssert.assertAll();
							object.put("status", finalStatus);
							arr.add(object);
						}
					}
				}

			}else {
				finalStatus="Fail";
			}
			boolean setFinalStatus=false;
			if(finalStatus.equals("Fail"))
				setFinalStatus=false;
			else if(finalStatus.equals("Pass"))
				setFinalStatus=true;
			Verify.verify(setFinalStatus);
			softAssert.assertAll();

		}catch(IOException | ParseException |NullPointerException | IllegalArgumentException e){
			logger.error("Exception occurred in Sync class in sync method "+e);

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
