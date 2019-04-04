package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;



import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
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

import io.mosip.dbaccess.RegProcDataRead;
import io.mosip.dbdto.AuditRequestDto;
import io.mosip.dbdto.SyncRegistrationDto;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * This class is used for testing the Sync API
 * 
 * @author Sayeri Mishra
 *
 */

public class Sync extends BaseTestCase implements ITest {

	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(Sync.class);
	boolean status = false;
	String finalStatus = "";
	Properties pro =  new Properties();
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response actualresponse = null;
	JSONObject expectedresponse = null;
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	String regIds="";
	SoftAssert softAssert=new SoftAssert();
	static String dest = "";
	static String folderPath = "regProc/Sync";
	static String outputFile = "SyncOutput.json";
	static String requestKeyFile = "SyncRequest.json";


	/**
	 *This method is used for reading the test data based on the test case name passed
	 *
	 * @param context
	 * @return Object[][]
	 */
	@DataProvider(name = "syncPacket")
	public static Object[][] readData(ITestContext context){ 
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		Object[][] readFolder = null;
		try{
			switch (testParam) {
			case "smoke":
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
			case "regression":
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
			default:
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
			}
		}catch(IOException | ParseException e){
			logger.error("Exception occurred i Sync class in readData method "+e);
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
	@Test(dataProvider = "syncPacket")
	public void sync(String testSuite, Integer i, JSONObject object){
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();

		try{
			JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
			// Expected response generation
			expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
			try {
				// Actual response generation
				actualresponse = applicationLibrary.postRequest(actualRequest.toJSONString(),pro.getProperty("syncListApi"));
			} catch (Exception e) {
				logger.info(e);
			}

			List<Map<String,String>> response = actualresponse.jsonPath().get("response"); 
			logger.info("response : "+response );

			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("requesttime");
			outerKeys.add("responsetime");
			innerKeys.add("createdDateTime");
			innerKeys.add("updatedDateTime");

			//Assertion of actual and expected response
			status = AssertResponses.assertResponses(actualresponse, expectedresponse, outerKeys, innerKeys);

			logger.info("Status after assertion : "+status);

			if (status) {
				JSONArray expected = (JSONArray) expectedresponse.get("response");
				List<String> expectedRegIds = new ArrayList<>();
				String expectedRegId = null;
				logger.info("expected: "+expected);
				String expectedErrorCode = "";
				Iterator<Object> iterator = expected.iterator();
				//extracting reg ids from the expected response
				while(iterator.hasNext()){
					JSONObject jsonObject = (JSONObject) iterator.next();
					expectedRegId = jsonObject.get("registrationId").toString().trim();
					logger.info("expectedRegId: "+expectedRegId);
					expectedRegIds.add(expectedRegId);
				}

				for(Map<String,String> res : response){
					String statusCode = res.get("status");
					logger.info("statusCode : "+statusCode);

					regIds=res.get("registrationId").toString();
					logger.info("Reg Id is : " +regIds);

					//If status code is success, reg id is checked in the db tables
					if(statusCode.matches(".*SUCCESS*.")) {

						SyncRegistrationDto dbDto = RegProcDataRead.regproc_dbDataInRegistrationList(regIds);	
						logger.info("dbDto :" +dbDto);

						//Checking audit logs (not yet implemented)
						LocalDateTime logTime = LocalDateTime.of(2019,Month.JANUARY,30,10,15,51,270000000);   //2019-01-30 10:15:51.27					
						logger.info("log time : "+logTime);
						AuditRequestDto auditDto = RegProcDataRead.regproc_dbDataInAuditLog(regIds, "REGISTRATION_ID", "REGISTRATION_PROCESSOR", "GET",logTime);
						logger.info("AUDIT DTO : "+auditDto.getApplicationName());



						if(dbDto != null /*&& auditDto != null*/) {
							//if reg id present in response and reg id fetched from table matches, then it is validated
							if (expectedRegIds.contains(dbDto.getRegistrationId())/*&& expectedRegIds.contains(auditDto.getId())*/){

								logger.info("Validated in DB.......");
								finalStatus = "Pass";
								softAssert.assertTrue(true);
							} 

							/*//remove this block of code after checking the updated response
						if(dbDto != null) {

							Iterator<Object> iteratorNew = ((List) Expectedresponse).iterator();
							while(iterator.hasNext()){
								JSONObject jsonObject = (JSONObject) iterator.next();
								logger.info("regidtrationId" + ":" + jsonObject.get("registrationId"));
								String expectedRegIdNew = jsonObject.get("registrationId").toString().trim();
								logger.info("expectedRegId: "+expectedRegIdNew);

								if (expectedRegIdNew.matches(dbDto.getRegistrationId())){

									logger.info("Validated in DB.......");
									finalStatus = "Pass";
								} 
							}

						}*/

						}else if (statusCode.matches(".*FAILURE.*")){
							String errorCode = res.get("errorCode").toString();
							logger.info("errorCode : "+errorCode);
							String regID = res.get("registrationId").toString();
							logger.info("regID : "+regID);
							Iterator<Object> iterator1 = expected.iterator();
							while(iterator1.hasNext()){
								JSONObject jsonObject = (JSONObject) iterator1.next();
								expectedErrorCode = jsonObject.get("errorCode").toString().trim();
								logger.info("expectedErrorCode: "+expectedErrorCode);
							}
							if(expectedErrorCode.matches(errorCode)){
								finalStatus = "Pass";
								softAssert.assertTrue(true);
							}

						}else {
							finalStatus="Fail";
							softAssert.assertTrue(false);
						}
					}

				}
			}else {
				finalStatus="Fail";
				softAssert.assertTrue(false);
			}

			softAssert.assertAll();
			object.put("status", finalStatus);
			arr.add(object);
		}catch(IOException | ParseException e){
			logger.error("Exception occurred in Sync class in sync method "+e);
		}
	}
	
	/**
	 * This method is used for reading and loading the property file
	 */
	@BeforeClass
	public void setUp(){
		// Create  FileInputStream object 
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File("src\\config\\registrationProcessorAPI.properties"));

		// Load file so we can use into our script 
			pro.load(fis);
			fis.close();
		} catch (IOException e) {
			logger.error("Exception occurred in Sync class in setUp method "+e);
		}

	}  

	/**
	 * This method is used for fetching test case name
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx){
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
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
