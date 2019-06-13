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

import io.mosip.dbaccess.RegProcDataRead;
import io.mosip.dbdto.AuditRequestDto;
import io.mosip.dbdto.ManualVerificationDTO;
import io.mosip.dbdto.SyncRegistrationDto;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.registrationProcessor.util.StageValidationMethods;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;

public class Decision extends BaseTestCase implements ITest{
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(Assignment.class);
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
	String matchedRegIds = "";
	String statusCodeRes = "";
	SoftAssert softAssert=new SoftAssert();
	static String dest = "";
	static String folderPath = "regProc/Decision";
	static String outputFile = "DecisionOutput.json";
	static String requestKeyFile = "DecisionRequest.json";
	static String description="";
	static String apiName="DecisionApi";
	static String moduleName="RegProc";
	CommonLibrary common=new CommonLibrary();
	RegProcApiRequests apiRequests=new RegProcApiRequests();
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	StageValidationMethods apiRequest=new StageValidationMethods();
	String validToken="";
	public String getToken(String tokenType) {
		String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
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
	@DataProvider(name = "decision")
	public  Object[][] readData(ITestContext context){ 
		Object[][] readFolder = null;
		String propertyFilePath=System.getProperty("user.dir")+"/"+"src/config/registrationProcessorAPI.properties";
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
		}catch(IOException | ParseException e){
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
	 */
	@Test(dataProvider = "decision")
	public void sync(String testSuite, Integer i, JSONObject object){
		
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		RegProcDataRead readDataFromDb = new RegProcDataRead();
		
		//testCaseName =testCaseName +": "+ description;
		try{
			actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
			// Expected response generation
			expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);

			// Actual response generation
			actualResponse = apiRequests.regProcPostRequest(prop.getProperty("decisionApi"),actualRequest,MediaType.APPLICATION_JSON,validToken);

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
					Map<String,String>response = actualResponse.jsonPath().get("response"); 
					logger.info("response : "+response );
					JSONObject expected = (JSONObject) expectedResponse.get("response");
					List<String> expectedRegIds = new ArrayList<>();
					List<String> expectedMatchedRegIds = new ArrayList<>();
					String expectedRegId = null;
					String expectedMatchedRegId = null;
					String statusCode = null;
					logger.info("expected: "+expected);
					
					//extracting reg ids from the expected response
					
			
						expectedRegId = expected.get("regId").toString().trim();
						expectedMatchedRegId = expected.get("matchedRefId").toString().trim();
						expectedRegIds.add(expectedRegId);
						expectedMatchedRegIds.add(expectedMatchedRegId);
						statusCode = expected.get("statusCode").toString().trim();
					

					/*for(Map<String,String> res : response){*/
						regIds=response.get("regId").toString();
						matchedRegIds = response.get("matchedRefId").toString();
						statusCodeRes = response.get("statusCode").toString();
						

						ManualVerificationDTO dbDto = readDataFromDb.regproc_dbDataInManualVerification(regIds,matchedRegIds,statusCodeRes);	
						//List<Object> count = readDataFromDb.countRegIdInRegistrationList(regIds);
						logger.info("dbDto :" +dbDto);

						//Checking audit logs (not yet implemented)
							/*LocalDateTime logTime = LocalDateTime.of(2019,Month.JANUARY,30,10,15,51,270000000);   //2019-01-30 10:15:51.27					
							logger.info("log time : "+logTime);
							AuditRequestDto auditDto = RegProcDataRead.regproc_dbDataInAuditLog(regIds, "REGISTRATION_ID", "REGISTRATION_PROCESSOR", "GET",logTime);
							logger.info("AUDIT DTO : "+auditDto.getApplicationName());
*/
						if(dbDto != null /*&& count.isEmpty()*//*&& auditDto != null*/) {
							//if reg id present in response and reg id fetched from table matches, then it is validated
							if ((expectedRegId.matches(dbDto.getRegId())&& (expectedMatchedRegId.matches(dbDto.getMatchedRefId()))) 
									&& statusCode.matches(dbDto.getStatusCode())){

								logger.info("Validated in DB.......");
								finalStatus = "Pass";
								softAssert.assertTrue(true);
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
	       
		}catch(IOException | ParseException e){
			logger.error("Exception occurred in Sync class in sync method "+e);
			 //Verify.verify(false);
		}
	}  

	/**
	 * This method is used for fetching test case name
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx){
		validToken=getToken("getStatusTokenGenerationFilePath");
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
			f.set(baseTestMethod, Decision.testCaseName);
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
