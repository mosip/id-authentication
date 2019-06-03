package io.mosip.registrationProcessor.tests;

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

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

import io.mosip.dto.AuditRequestDto;
import io.mosip.dto.SyncRegistrationDto;
import io.mosip.dao.RegProcDataRead;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;

/**
 * Test script for syncing packet
 * 
 * @author Sayeri Mishra
 *
 */

public class Sync extends BaseTestCase implements ITest {
	//implement,IInvokedMethodListener
	public Sync() {

	}

	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(Sync.class);
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static final String regProc_URI = "/registrationstatus/v0.1/registration-processor/registration-status/sync";
	static 	String regIds="";
	static SoftAssert softAssert=new SoftAssert();
	static String dest = "";
	static String folderPath = "regProc/Sync";
	static String outputFile = "SyncOutput.json";
	static String requestKeyFile = "SyncRequest.json";


	@DataProvider(name = "syncPacket")
	public static Object[][] readData1(ITestContext context) throws Exception {
		//CommonLibrary.configFileWriter(folderPath,requestKeyFile,"DemographicCreate","smokePreReg");
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		switch (testParam) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	@Test(dataProvider = "syncPacket")
	public void generate_Response1(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		try {
			Actualresponse = applicationLibrary.postRequest(actualRequest.toJSONString(), regProc_URI);
		} catch (Exception e) {
			logger.info(e);
		}
		//String statusCode = Actualresponse.jsonPath().get("status").toString();

		List<Map<String,String>> response = Actualresponse.jsonPath().get("response"); 
		logger.info("response : "+response );
		/*if(statusCode.equals("true")) {
			regId=(Actualresponse.jsonPath().get("response[0].registrationId")).toString();
		}*/
		outerKeys.add("resTime");
		outerKeys.add("timestamp");
		outerKeys.add("requestTimestamp");
		outerKeys.add("responseTimestamp");
		innerKeys.add("preRegistrationId");
		innerKeys.add("updatedBy");
		innerKeys.add("createdDateTime");
		innerKeys.add("updatedDateTime");


		status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

		logger.info("Status after assertion : "+status);

		if (status) {
			JSONArray expected = (JSONArray) Expectedresponse.get("response");
			List<String> expectedRegIds = new ArrayList<>();
			String expectedRegId = null;
			logger.info("expected: "+expected);
			String expectedErrorCode = "";
			Iterator<Object> iterator = expected.iterator();
			while(iterator.hasNext()){
				JSONObject jsonObject = (JSONObject) iterator.next();
				expectedRegId = jsonObject.get("registrationId").toString().trim();
				logger.info("expectedRegId: "+expectedRegId);
				expectedRegIds.add(expectedRegId);
				
				/*expectedErrorCode = jsonObject.get("errorCode").toString().trim();
				logger.info("expectedErrorCode: "+expectedErrorCode);*/
				//String timestamp = jsonObject.get("timestamp").toString().trim();
			}


			for(Map<String,String> res : response){
				String statusCode = res.get("status");
				logger.info("statusCode : "+statusCode);

				regIds=res.get("registrationId").toString();
				logger.info("Reg Id is : " +regIds);




				if(statusCode.matches(".*SUCCESS*.")) {

					SyncRegistrationDto dbDto = RegProcDataRead.regproc_dbDataInRegistrationList(regIds);	
					logger.info("dbDto :" +dbDto);

					LocalDateTime logTime = LocalDateTime.of(2019,Month.JANUARY,30,10,15,51,270000000);   //2019-01-30 10:15:51.27					
					logger.info("log time : "+logTime);
					AuditRequestDto auditDto = RegProcDataRead.regproc_dbDataInAuditLog(regIds, "REGISTRATION_ID", "REGISTRATION_PROCESSOR", "GET",logTime);
					logger.info("AUDIT DTO : "+auditDto.getApplicationName());



					if(dbDto != null /*&& auditDto != null*/) {
						if (expectedRegIds.contains(dbDto.getRegistrationId())/*&& expectedRegIds.contains(auditDto.getId())*/){

							logger.info("Validated in DB.......");
							finalStatus = "Pass";
							softAssert.assertTrue(true);
						} 

					}

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

		}else {
			finalStatus="Fail";
			softAssert.assertTrue(false);
		}

		softAssert.assertAll();
		object.put("status", finalStatus);
		arr.add(object);

	}

	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		//	testName.set(object.get("testCaseName").toString());
		testCaseName = object.get("testCaseName").toString();
	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {

			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, Sync.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
	IllegalAccessException {
		String configPath =  "src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}
		String source = "src/test/resources/" + folderPath + "/";
		//CommonLibrary.backUpFiles(source, folderPath);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

	/*	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		// TODO Auto-generated method stub
		  logger.info("beforeInvocation: runs before every method in the Test Class");
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		// TODO Auto-generated method stub
		if(testResult.getMethod().isTest()) {
			if(testResult.getStatus() == ITestResult.SUCCESS) {
			    ITestContext tc = Reporter.getCurrentTestResult().getTestContext();
	            tc.getPassedTests().addResult(testResult, Reporter.getCurrentTestResult().getMethod());
	            tc.getPassedTests().getAllMethods().remove(Reporter.getCurrentTestResult().getMethod());
	            Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
	            Reporter.getCurrentTestResult().setThrowable(new Exception("test Fail"));
	            tc.getSkippedTests().addResult(testResult, Reporter.getCurrentTestResult().getMethod());
			}
		}

	}*/
}
