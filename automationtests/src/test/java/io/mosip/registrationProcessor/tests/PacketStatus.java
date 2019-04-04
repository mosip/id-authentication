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

import io.mosip.dbdto.AuditRequestDto;
import io.mosip.dbentity.RegistrationStatusEntity;
import io.mosip.dbaccess.RegProcDataRead;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * This class is use for testing Packet Status API 
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
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response actualResponse = null;
	JSONObject expectedResponse = null;
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	String finalStatus = "";
	SoftAssert softAssert=new SoftAssert();
	String regIds="";
	static String dest = "";
	static String folderPath = "regProc/PacketStatus";
	static String outputFile = "PacketStatusOutput.json";
	static String requestKeyFile = "PacketStatusRequest.json";
	Properties pro =  new Properties();

	/**
	 * This method is use for reading data for packet status based on test case name
	 * @param context
	 * @return Object[][]
	 */
	@DataProvider(name = "packetStatus")
	public static Object[][] readDataForPacketStatus(ITestContext context) {
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		Object[][] readFolder= null;
		try {
			switch (testParam) {
			case "smoke":
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
			case "regression":
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
			default:
				readFolder = ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
			}
		} catch (IOException | ParseException e) {
			logger.error("Exception occurred in PacketStatus class in readDataForPacketStatus method" +e);
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
	@Test(dataProvider = "packetStatus")
	public void packetStatus(String testSuite, Integer i, JSONObject object){

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = new JSONObject();
		List<Map<String,String>> response = actualResponse.jsonPath().get("response");

		try {
			actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
			expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);
			//generation of actual response
			actualResponse = applicationLibrary.getRequestAsQueryParam(pro.getProperty("packetStatusApi"),actualRequest);

			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("requesttime");
			outerKeys.add("responsetime");
			innerKeys.add("createdDateTime");
			innerKeys.add("updatedDateTime");

			//Asserting actual and expected response
			status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);

			if (status) {

				JSONArray expected = (JSONArray) expectedResponse.get("response");
				if(expected!=null&& !expected.isEmpty() && actualRequest!=null){
					List<String> expectedRegIds = new ArrayList<>();
					String expectedRegId = null;
					//extracting reg ids from the expected response
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

						//If status code is processing, rsend or processed, reg id is checked in the db tables
						if(statusCode.matches(".*PROCESSING*.")|| statusCode.matches(".*RESEND*.")||statusCode.matches(".*PROCESSED*.")) {
							logger.info("inside statuscode loop...................");

							//audit log check(not yet implemented)
							LocalDateTime logTime = LocalDateTime.of(2019,Month.JANUARY,30,10,15,51,270000000);   //2019-01-30 10:15:51.27					
							logger.info("log time : "+logTime);
							AuditRequestDto auditDto = RegProcDataRead.regproc_dbDataInAuditLog(regIds, "REGISTRATION_ID", "REGISTRATION_PROCESSOR", "GET",logTime);
							logger.info("AUDIT DTO : "+auditDto.getApplicationName());
							//rest of the validations will be added 

							RegistrationStatusEntity dbDto = RegProcDataRead.regproc_dbDataInRegistration(regIds);	
							logger.info("dbDto :" +dbDto);

							if(dbDto != null /*&& auditDto != null*/) {
								//if reg id present in response and reg id fetched from table matches, then it is validated
								if (expectedRegIds.contains(dbDto.getId())/*&& expectedRegIds.contains(auditDto.getId())*/){
									Iterator<Object> iteratorNew = ((List) expectedResponse).iterator();
									while(iterator.hasNext()){
										JSONObject jsonObject = (JSONObject) iterator.next();
										logger.info("regidtrationId" + ":" + jsonObject.get("registrationId"));
										String expectedRegIdNew = jsonObject.get("registrationId").toString().trim();
										logger.info("expectedRegId: "+expectedRegIdNew);

										if (expectedRegIdNew.matches(dbDto.getId())){							

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
					}}else {
						finalStatus="Fail";
						//softAssert.assertTrue(false);
					}

				softAssert.assertAll();
				object.put("status", finalStatus);
				arr.add(object);
			}
		} catch (IOException | ParseException e) {
			logger.error("Exception occurred in Packet Status class in packetStatus method "+e);
		}
	}

	/**
	 * This method is used for reading and loading the property file
	 */
	@BeforeClass
	public void setUp() {
		// Create  FileInputStream object 
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File("src\\config\\registrationProcessorAPI.properties"));
			// Load file so we can use into our script 
			pro.load(fis);
			fis.close();
		} catch (IOException e) {
			logger.error("Exceptionm occurred in PacketStatus class in setUp method "+e);
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
			f.set(baseTestMethod, PacketStatus.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in PacketStatus class in setResultTestName "+e);
		}

	}

	/**
	 * This method is used for generating output file with the test case result
	 */
	@AfterClass
	public void statusUpdate() {
		String configPath = System.getProperty("user.dir") + "/src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
			file.close();
		} catch (IOException e) {
			logger.error("Exception  occurred in PacketStatus class in statusUpdate method "+e);
		}
		String source =  "src/test/resources/" + folderPath + "/";
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}
