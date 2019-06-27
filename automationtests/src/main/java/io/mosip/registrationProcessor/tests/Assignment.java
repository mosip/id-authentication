package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import io.mosip.dbdto.ManualVerificationDTO;
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

/**
 * This class is used for testing Assignment API
 * 
 * @author Sayeri
 *
 */
public class Assignment extends BaseTestCase implements ITest {
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(Assignment.class);
	boolean status = false;
	String finalStatus = "Fail";
	static Properties prop = new Properties();
	JSONArray arr = new JSONArray(); 
	ObjectMapper mapper = new ObjectMapper();
	Response actualResponse = null;
	JSONObject expectedResponse = null;
	JSONObject actualRequest = null;
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	String regIds = "";
	String matchedRegIds = "";
	String statusCodeRes = "";
	SoftAssert softAssert = new SoftAssert();
	static String dest = "";
	static String folderPath = "regProc/Assignment";
	static String outputFile = "AssignmentOutput.json";
	static String requestKeyFile = "AssignmentRequest.json";
	static String description = "";
	static String apiName = "AssignmentApi";
	static String moduleName = "RegProc";
	CommonLibrary common = new CommonLibrary();
	
	RegProcApiRequests apiRequests=new RegProcApiRequests();
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	StageValidationMethods apiRequest=new StageValidationMethods();
	String validToken="";
	
	
	/**
	 * This method is used for generating token 
	 * 
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
	 * This method is used for reading the test data based on the test case name
	 * passed
	 *
	 * @param context
	 * @return Object[][]
	 */
	@DataProvider(name = "assignment")

	public Object[][] readData(ITestContext context) {

		Object[][] readFolder = null;
		String propertyFilePath=apiRequests.getResourcePath()+"config/registrationProcessorAPI.properties";

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
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			Assert.assertTrue(false, "not able to read the folder in Assignment class in readData method: "+ e.getCause());
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
	@Test(dataProvider = "assignment")
	public void assignment(String testSuite, Integer i, JSONObject object){

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		RegProcDataRead readDataFromDb = new RegProcDataRead();

		try{
			actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
			// Expected response generation
			expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);
			String userId = null;
				if(object.get("testCaseName").toString().contains("smoke")) {
					JSONObject request = (JSONObject) actualRequest.get("request");
					userId = request.get("userId").toString();
					boolean statusUpdated = readDataFromDb.updateStatusInManualVerification(userId, "PENDING");
					logger.info("statusUpdated : "+statusUpdated);
				}
				
				validToken=getToken("getStatusTokenGenerationFilePath");
				boolean tokenStatus=apiRequests.validateToken(validToken);
				while(!tokenStatus) {
					validToken = getToken("getStatusTokenGenerationFilePath");
					tokenStatus=apiRequests.validateToken(validToken);
				}
				
				// Actual response generation
				actualResponse = apiRequests.regProcPostRequest(prop.getProperty("assignmentApi"),actualRequest,MediaType.APPLICATION_JSON,validToken);
			
			String message = null;
			boolean noRecord = false;
			if(actualResponse.asString().contains("errors")) {
				List<Map<String,String>> errors = actualResponse.jsonPath().get("errors");
				for(Map<String,String> err : errors){
					message = err.get("message").toString();
				}
				if((message.matches("No Assigned Record Found") 
						&& object.get("testCaseName").toString().contains("smoke"))) {
					logger.info("no record assigned ========================");
					noRecord = true;
					finalStatus = "Pass";
					softAssert.assertAll();
					object.put("status", finalStatus);
					arr.add(object);
				}
			}else if(actualResponse.asString().contains("response") && object.get("testCaseName").toString().contains("RecordUnavaible")) {
				noRecord = true;
				finalStatus = "Pass";
				softAssert.assertAll();
				object.put("status", finalStatus);
				arr.add(object);
			}

			if(!noRecord) {

			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("requesttime");
			outerKeys.add("responsetime");
			innerKeys.add("createdDateTime");
			innerKeys.add("updatedDateTime");
			innerKeys.add("regId");
			innerKeys.add("matchedRefId");
			innerKeys.add("reasonCode");
			
			
				//Assertion of actual and expected response
				status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
				Assert.assertTrue(status, "object are not equal");
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


						/* for(Map<String,String> res : response){ */
							regIds=response.get("regId").toString();
							matchedRegIds = response.get("matchedRefId").toString();
							statusCodeRes = response.get("statusCode").toString();


							ManualVerificationDTO dbDto = readDataFromDb.regproc_dbDataInManualVerification(regIds,matchedRegIds,statusCodeRes);	
							//List<Object> count = readDataFromDb.countRegIdInRegistrationList(regIds);
							logger.info("dbDto :" +dbDto);


						if (dbDto != null /* && count.isEmpty()&& auditDto != null */) {
								//if reg id present in response and reg id fetched from table matches, then it is validated
								if (statusCode.matches(dbDto.getStatusCode())){

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
			}

			
				boolean setFinalStatus=false;
				if(finalStatus.equals("Fail"))
					setFinalStatus=false;
				else if(finalStatus.equals("Pass"))
					setFinalStatus=true;
				Verify.verify(setFinalStatus);
				softAssert.assertAll();

			}catch(IOException|ParseException e)
			{
				Assert.assertTrue(false, "not able to execute assignment method : "+ e.getCause());
			}
		}

		/**
		 * This method is used for fetching test case name
		 * 
		 * @param method
		 * @param testdata
		 * @param ctx
		 */
		@BeforeMethod(alwaysRun = true)
		public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
			JSONObject object = (JSONObject) testdata[2];
			testCaseName = moduleName + "_" + apiName + "_" + object.get("testCaseName").toString();

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
				f.set(baseTestMethod, Assignment.testCaseName);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				logger.error("Exception occurred in Assignment class in setResultTestName method " + e);
				Reporter.log("Exception : " + e.getMessage());
			}

		}

		@Override
		public String getTestName() {
			return this.testCaseName;
		}

	}
