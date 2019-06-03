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
import io.mosip.entity.RegistrationStatusEntity;
import io.mosip.dao.RegProcDataRead;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test script for getting registration id status
 * 
 * @author Sayeri Mishra
 *
 */
public class PacketStatus extends BaseTestCase implements ITest {
	//implement,IInvokedMethodListener
	public PacketStatus() {

	}

	private static Logger logger = Logger.getLogger(PacketStatus.class);
	protected static String testCaseName = "";

	boolean status = false;
	String[] regId = null;
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response actualResponse = null;
	static JSONObject expectedResponse = null;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static final String regProc_URI = "/registrationstatus/v0.1/registration-processor/registration-status/registrationstatus";
	String finalStatus = "";
	static SoftAssert softAssert=new SoftAssert();
	static 	String regIds="";
	static String dest = "";
	static String folderPath = "regProc/PacketStatus";
	static String outputFile = "PacketStatusOutput.json";
	static String requestKeyFile = "PacketStatusRequest.json";

	/**
	 * This method is use for reading data for packet status
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@DataProvider(name = "packetStatus")
	public static Object[][] readDataForPacketStatus(ITestContext context) throws Exception {
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

	/**
	 * This method is use for getting packet status based on registration id
	 * @param testSuite
	 * @param i
	 * @param object
	 * @throws Exception
	 */
	@Test(dataProvider = "packetStatus")
	public void packetStatus(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);
		try {

			actualResponse = applicationLibrary.getRequestAsQueryParam(regProc_URI,actualRequest);

		} catch (Exception e) {
			logger.info(e);
		}


		/*if(statusCode.equals("true")) {
			regId=(Actualresponse.jsonPath().get("response[0].registrationId")).toString();
		}*/
		outerKeys.add("resTime");
		outerKeys.add("requestTimestamp");
		outerKeys.add("responseTimestamp");
		innerKeys.add("preRegistrationId");
		innerKeys.add("updatedBy");
		innerKeys.add("createdDateTime");
		innerKeys.add("updatedDateTime");


		status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
		List<Map<String,String>> response = actualResponse.jsonPath().get("response");

		if (status) {

			JSONArray expected = (JSONArray) expectedResponse.get("response");
			if(expected!=null&& !expected.isEmpty() && actualRequest!=null){
				List<String> expectedRegIds = new ArrayList<>();
				String expectedRegId = null;
				logger.info("expected: "+expected);
				Iterator<Object> iterator = expected.iterator();
				while(iterator.hasNext()){
					JSONObject jsonObject = (JSONObject) iterator.next();
					expectedRegId = jsonObject.get("registrationId").toString().trim();
					logger.info("expectedRegId: "+expectedRegId);
					expectedRegIds.add(expectedRegId);

				}
				for(Map<String,String> res : response){
					String statusCode = res.get("statusCode");
					logger.info("statusCode : "+statusCode);

					regIds=res.get("registrationId").toString();
					logger.info("Reg Id is : " +regIds);

					if(statusCode.matches(".*PROCESSING*.")|| statusCode.matches(".*RESEND*.")||statusCode.matches(".*PROCESSED*.")) {
						logger.info("inside statuscode loop...................");

						LocalDateTime logTime = LocalDateTime.of(2019,Month.JANUARY,30,10,15,51,270000000);   //2019-01-30 10:15:51.27					
						logger.info("log time : "+logTime);

						RegistrationStatusEntity dbDto = RegProcDataRead.regproc_dbDataInRegistration(regIds);	
						AuditRequestDto auditDto = RegProcDataRead.regproc_dbDataInAuditLog(regIds, "REGISTRATION_ID", "REGISTRATION_PROCESSOR", "GET",logTime);

						logger.info("AUDIT DTO : "+auditDto.getApplicationName());
						//rest of the validations will be added 

						logger.info("dbDto :" +dbDto);

						if(dbDto != null && auditDto != null) {
							if (expectedRegIds.contains(dbDto.getId())/*&& expectedRegIds.contains(auditDto.getId())*/){

<<<<<<< HEAD:JAR_POC/mosipRegistrationProcessor/src/test/java/io/mosip/registrationProcessor/tests/PacketStatus.java
=======
						Iterator<Object> iterator = expectedResponse.iterator();
						while(iterator.hasNext()){
							JSONObject jsonObject = (JSONObject) iterator.next();
							logger.info("regidtrationId" + ":" + jsonObject.get("registrationId"));
							String expectedRegId = jsonObject.get("registrationId").toString().trim();
							logger.info("expectedRegId: "+expectedRegId);
							
							if (expectedRegId.matches(dbDto.getId())){							
>>>>>>> 40461e4b8758ee6bdc38e4fa2fe0a6d06a6bc0a3:automationtests/src/test/java/io/mosip/regProc/tests/Assignment.java
								logger.info("Validated in DB.......");
								finalStatus = "Pass";
							} 
						}
					}else {
						finalStatus="Fail";
					}
				}
			}else {
				finalStatus="Pass";
			}
			softAssert.assertTrue(true);
		}else {
			finalStatus="Fail";
			//softAssert.assertTrue(false);
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
		boolean flag = false;
		boolean flag_reg = false;



		try {
		/*	for(String rId : regId){
				//	flag = RegProcDataRead.regproc_dbDeleteRecordInRegistrationList(rId);
				logger.info("FLAG INSIDE AFTER METHOD FOR REGISTRATION LIST: "+flag);
				//	flag_reg = RegProcDataRead.regproc_dbDeleteRecordInRegistration(rId);
				logger.info("FLAG INSIDE AFTER METHOD FOR REGISTRATION LIST: "+flag_reg);

			}*/

			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, PacketStatus.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
	IllegalAccessException {
		String configPath = System.getProperty("user.dir") + "/src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}
		String source =  "src/test/resources/" + folderPath + "/";
		//CommonLibrary.backUpFiles(source, folderPath);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}
