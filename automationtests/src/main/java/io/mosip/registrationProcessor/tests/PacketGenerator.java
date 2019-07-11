package io.mosip.registrationProcessor.tests;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.dbaccess.RegProcTransactionDb;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.EncrypterDecrypter;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.mosip.util.TokenGeneration;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class PacketGenerator  extends  BaseTestCase implements ITest {
	 private static Logger logger = Logger.getLogger(PacketReceiver.class);	 
	 protected static String testCaseName = "";
	 boolean status = false;
	 boolean dbStatus=false;
	 String finalStatus = "";	 
	 JSONArray arr = new JSONArray();	 
	 ObjectMapper mapper = new ObjectMapper();
	 ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	 SoftAssert softAssert=new SoftAssert();
	 Response actualResponse = null;
	 JSONObject expectedResponse = null;
	 String dest = "";
	 static String folderPath = "regProc/DeactivateRequest";
	 static String outputFile = "DeactivateRequestOutput.json";
	 static String requestKeyFile = "DeactivateUinRequest.json";
	 String rId = null;
	 static String moduleName="RegProc";
	 static String apiName="PacketGeneratorDeactivate";
		RegProcApiRequests apiRequests=new RegProcApiRequests();
		TokenGeneration generateToken=new TokenGeneration();
		TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
		String validToken="";
		
		public String getToken(String tokenType) { String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
		}
	 Properties prop =  new Properties();
	 RegProcTransactionDb regProcDbRead=new RegProcTransactionDb();
	 /**
	  * This method is used for reading the test data based on the test case name passed
	  * 
	  * @param context
	  * @return object[][]
	  * @throws Exception
	  */
	 @DataProvider(name = "DeactivateUin")
	 public Object[][] readData(ITestContext context){
		 RegProcApiRequests apiRequests = new RegProcApiRequests();
		String propertyFilePath=apiRequests.getResourcePath()+"config/registrationProcessorAPI.properties";
	 	 String testParam = context.getCurrentXmlTest().getParameter("testType");
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
	 	 }catch (IOException | ParseException e) {
	 	 	 logger.error("Exception occurred in Packet Receiver class in readData method"+e);
	 	 }
	 	 return readFolder;
	 }

	 /**
	  * This method is used for generating actual response and comparing it with expected response
	  * along with db check and audit log check
	  * @param testSuite
	  * @param i
	  * @param object
	  */
	 @Test(dataProvider="DeactivateUin")
	 public void packetGenerator(String testSuite, Integer i, JSONObject object){
	 	 List<String> outerKeys = new ArrayList<String>();
	 	 List<String> innerKeys = new ArrayList<String>();	
	 	 String currentTestCaseName=object.get("testCaseName").toString();
	 	 EncrypterDecrypter encrypter = new EncrypterDecrypter();
	 	 try {
	 	 	 JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);	 
	 	 	 expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);

	 	 	 //outer and inner keys which are dynamic in the actual response
	 	 	 outerKeys.add("responsetime");
	 	 	 innerKeys.add("registrationId");
	 	 	validToken=getToken("getStatusTokenGenerationFilePath");
			boolean tokenStatus=apiRequests.validateToken(validToken);
			while(!tokenStatus) {
				validToken = getToken("getStatusTokenGenerationFilePath");
				tokenStatus=apiRequests.validateToken(validToken);
			}
			

			actualResponse=apiRequests.regProcPacketGenerator(actualRequest, prop.getProperty("packetGeneratorApi"),MediaType.APPLICATION_JSON,validToken);
	 	 	

	 	 	String message="";
	 	 	 try {
	 	 		 message=actualResponse.jsonPath().get("response.message").toString();
	 	 		 }catch (Exception e) {
	 	 			 message=actualResponse.jsonPath().get("errors[0].message").toString();
			}
	 	 	 boolean idRepoStatus=false;
	 	  if(message.equals("Packet created and uploaded")) {
	 		  			
	 		  			String idRepoToken=getToken("syncTokenGenerationFilePath");
	 		  			boolean idRepoTokenStatus=apiRequests.validateToken(idRepoToken);
	 		  			while(!idRepoTokenStatus) {
	 		  				idRepoToken = getToken("syncTokenGenerationFilePath");
	 						tokenStatus=apiRequests.validateToken(idRepoToken);
	 					}
	 	 				idRepoStatus=apiRequests.getUinStatusFromIDRepo(actualRequest, idRepoToken, "DEACTIVATED");
	 			
	 	 	 }
	 	
	 		 //Asserting actual and expected response
	 	 	 status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
	 	 	 if(status) { 
	 	 	 	 finalStatus="Pass";
	 	 	 	 if(message.equals("Packet created and uploaded")) {
	 	 	 		 if(idRepoStatus)
	 	 	 			 finalStatus="Pass";
	 	 	 		 else
	 	 	 			 finalStatus="Fail";
	 	 	 			 
	 	 	 	 }
	 	 	 }else
	 	 	 	 finalStatus="Fail";
	 	
	 	 	 object.put("status", finalStatus);
	 	 	 arr.add(object);
	 	 	 boolean setFinalStatus = false;
	 	 	 if (finalStatus.equals("Fail")) {
	 	 	 	 setFinalStatus = false;
	 	 	 } else if (finalStatus.equals("Pass"))
	 	 	 	 setFinalStatus = true;
	 	 	 Verify.verify(setFinalStatus);
	 	 	 softAssert.assertAll();
	 	 } catch (IOException | ParseException e) {
	 	 	 logger.error("Exception occcurred in Packet Receiver class in packetReceiver method "+e);
	 	 }
	 	 
	 }

	 /**
	  * This method is used for fetching test case name
	  * @param method
	  * @param testdata
	  * @param ctx
	  */
	 @BeforeMethod(alwaysRun=true)
	 public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
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

	 	 Field method = null;
	 	 try {
	 	 	 method = TestResult.class.getDeclaredField("m_method");
	 	 	 method.setAccessible(true);
	 	 	 method.set(result, result.getMethod().clone());
	 	 	 BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
	 	 	 Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
	 	 	 f.setAccessible(true);
	 	 	 f.set(baseTestMethod, PacketGenerator.testCaseName);
	 	 } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
	 	 	 logger.error("Exception occurred in PacketReceiver class in setResultTestName "+e);
	 	 }
	 	
	 }

	 @Override
	 public String getTestName() {
	 	 return this.testCaseName;
	 }

}
