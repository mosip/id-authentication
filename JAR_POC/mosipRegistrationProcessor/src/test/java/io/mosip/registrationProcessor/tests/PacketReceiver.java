package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

import io.mosip.entity.RegistrationStatusEntity;
import io.mosip.dao.RegProcDataRead;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test script for packet receiver
 * 
 * @author Sayeri Mishra
 *
 */

public class PacketReceiver extends  BaseTestCase implements ITest {	

	private static Logger logger = Logger.getLogger(PacketReceiver.class);	
	protected static String testCaseName = "";
	boolean status = false;
	String finalStatus = "";	
	public static JSONArray arr = new JSONArray();	
	ObjectMapper mapper = new ObjectMapper();
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static final String regProc_URI = "/packetreceiver/v0.1/registration-processor/packet-receiver/registrationpackets";
	static SoftAssert softAssert=new SoftAssert();
	static Response actualResponse = null;
	static JSONObject expectedResponse = null;
	static String dest = "";
	static String folderPath = "regProc/PacketReceiver";
	static String outputFile = "PacketReceiverOutput.json";
	static String requestKeyFile = "PacketReceiverRequest.json";
	String rId = null;

	static String testParam = null;

	/**
	 * This method is use for reading data
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "documentUpload")
	public Object[][] readData(ITestContext context) throws Exception {

		testParam = context.getCurrentXmlTest().getParameter("testType");
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
	 * This method is use for receiving a packet and uploading it
	 * @param testSuite
	 * @param i
	 * @param object
	 * @throws Exception
	 */
	@Test(dataProvider="documentUpload")
	public void packetReceiver(String testSuite, Integer i, JSONObject object) throws Exception {

		File file = null;
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);
		JSONObject objectData = new JSONObject();

		outerKeys.add("resTime");
		outerKeys.add("timestamp");
		outerKeys.add("requestTimestamp");
		outerKeys.add("responseTimestamp");
		innerKeys.add("preRegistrationId");
		innerKeys.add("updatedBy");
		innerKeys.add("createdDateTime");
		innerKeys.add("updatedDateTime");

		String configPath = "src/test/resources/" + testSuite + "/";
		File folder = new File(configPath);
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				if (listOfFolders[j].getName().equals(object.get("testCaseName").toString())) {
					logger.info("Testcase name is" + listOfFolders[j].getName());
					File[] listOfFiles = listOfFolders[j].listFiles();
					for (File f : listOfFiles) {
						if (f.getName().toLowerCase().contains("request")) {
							objectData = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
							file=new File(f.getParent()+"/"+objectData.get("path"));
							rId = file.getName().substring(0, file.getName().length()-4);
						}
					}
				}
			}
		}
		try {
			actualResponse = applicationLibrary.putMultipartFile(file, regProc_URI);

		} catch (Exception e) {
			logger.info(e);
		}
		String statusCode = null;
		String errorCode = null;
		Map<String,String> response  = actualResponse.jsonPath().get("response");
		Map<String,String> error  = actualResponse.jsonPath().get("error");

		status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
		if (status) {
			switch ("smokeAndRegression"){

			case "smoke" :
				statusCode = response.get("status");
				logger.info("statusCode : "+statusCode);

				if(statusCode.matches("PACKET_UPLOADED_TO_VIRUS_SCAN")){
					RegistrationStatusEntity dbDto = RegProcDataRead.regproc_dbDataInRegistration(rId);	
					//String status = dbDto.getStatusCode();
					if (dbDto!=null) {
						logger.info("Validated in db..........");
						finalStatus="Pass";
						softAssert.assertTrue(true);
					}	
				}else {
					finalStatus="Fail";
					softAssert.assertTrue(false);
				}

			case "regression" :
				errorCode = error.get("errorcode");
				logger.info("errorCode in switch : "+errorCode);

				if(errorCode.matches("RPR-PKR-005")){
					//String status = dbDto.getStatusCode();
					logger.info("DUPLICATE PACKET RECEIVED");
					finalStatus="Pass";
					softAssert.assertTrue(true);
				}else if(errorCode.matches("RPR-PKR-001")){
					//String status = dbDto.getStatusCode();
					logger.info("PACKET NOT SYNCED");
					finalStatus="Pass";
					softAssert.assertTrue(true);
				}else if(errorCode.matches("RPR-PKR-002")){
					//String status = dbDto.getStatusCode();
					logger.info("PACKET SIZED INVALID");
					finalStatus="Pass";
					softAssert.assertTrue(true);
				}else {
					finalStatus="Fail";
					softAssert.assertTrue(false);
				}
				
			default : 
				statusCode = response.get("status");
				logger.info("statusCode : "+statusCode);

				if(statusCode.matches("PACKET_UPLOADED_TO_VIRUS_SCAN")){
					RegistrationStatusEntity dbDto = RegProcDataRead.regproc_dbDataInRegistration(rId);	
					//String status = dbDto.getStatusCode();
					if (dbDto!=null) {
						logger.info("Validated in db..........");
						finalStatus="Pass";
						softAssert.assertTrue(true);
					}	
				}else {
					finalStatus="Fail";
					softAssert.assertTrue(false);
				}
				
				errorCode = error.get("errorcode");
				logger.info("errorCode in switch : "+errorCode);

				if(errorCode.matches("RPR-PKR-005")){
					//String status = dbDto.getStatusCode();
					logger.info("DUPLICATE PACKET RECEIVED");
					finalStatus="Pass";
					softAssert.assertTrue(true);
				}else if(errorCode.matches("RPR-PKR-001")){
					//String status = dbDto.getStatusCode();
					logger.info("PACKET NOT SYNCED");
					finalStatus="Pass";
					softAssert.assertTrue(true);
				}else if(errorCode.matches("RPR-PKR-002")){
					//String status = dbDto.getStatusCode();
					logger.info("PACKET SIZED INVALID");
					finalStatus="Pass";
					softAssert.assertTrue(true);
				}else {
					finalStatus="Fail";
					softAssert.assertTrue(false);
				}
				
			}

			softAssert.assertTrue(true);

		}else{
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
		boolean flag_reg_transaction = false;
		boolean flag_reg = false;
		try {
			/*flag_reg_transaction = RegProcDataRead.regproc_dbDeleteRecordInRegistrationTransaction(rId);
			logger.info("FLAG INSIDE AFTER METHOD FOR REGISTRATION TRANSACTION : "+flag_reg);
			flag_reg = RegProcDataRead.regproc_dbDeleteRecordInRegistration(rId);
			logger.info("FLAG INSIDE AFTER METHOD FOR REGISTRATION: "+flag_reg);*/
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, PacketReceiver.testCaseName);
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
		String source =  "src/test/resources/" + folderPath + "/";
		//CommonLibrary.backUpFiles(source, folderPath);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}
