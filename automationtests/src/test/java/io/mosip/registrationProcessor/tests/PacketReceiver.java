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

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
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
import com.google.common.base.Verify;

import io.mosip.registrationProcessor.util.RegProcTokenGenerate;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * This class is used for testing Packet Receiver API
 * 
 * @author Sayeri Mishra
 *
 */

public class PacketReceiver extends  BaseTestCase implements ITest {	

	private static Logger logger = Logger.getLogger(PacketReceiver.class);	
	protected static String testCaseName = "";
	boolean status = false;
	String finalStatus = "";	
	JSONArray arr = new JSONArray();	
	ObjectMapper mapper = new ObjectMapper();
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	SoftAssert softAssert=new SoftAssert();
	Response actualResponse = null;
	JSONObject expectedResponse = null;
	String dest = "";
	static String folderPath = "regProc/PacketReceiver";
	static String outputFile = "PacketReceiverOutput.json";
	static String requestKeyFile = "PacketReceiverRequest.json";
	String rId = null;
	static String moduleName="RegProc";
	RegProcTokenGenerate tokenGenearte=new RegProcTokenGenerate();
	String token="";
	Properties prop =  new Properties();

	/**
	 * This method is used for reading the test data based on the test case name passed
	 * 
	 * @param context
	 * @return object[][]
	 * @throws Exception
	 */
	@DataProvider(name = "PacketReceiver")
	public Object[][] readData(ITestContext context){
		String propertyFilePath=System.getProperty("user.dir")+"\\"+"src\\config\\RegistrationProcessorApi.properties";
		String testParam = context.getCurrentXmlTest().getParameter("testType");
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
		}catch (IOException | ParseException|IllegalArgumentException|NullPointerException e) {
			logger.error("Exception occurred in Packet Receiver class in readData method"+e);
		}
		return readFolder;
	}
@BeforeClass
public void getToken() {
	token=tokenGenearte.getRegProcAuthToken();
}
	/**
	 * This method is used for generating actual response and comparing it with expected response
	 * along with db check and audit log check
	 * @param testSuite
	 * @param i
	 * @param object
	 */
	@Test(dataProvider="PacketReceiver")
	public void packetReceiver(String testSuite, Integer i, JSONObject object){
		
		File file = null;
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		String configPath = "src/test/resources/" + testSuite + "/";
		File folder = new File(configPath);
		File[] listOfFolders = folder.listFiles();
		JSONObject objectData = new JSONObject();

		try {
			JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);	
			expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);

			//outer and inner keys which are dynamic in the actual response
			outerKeys.add("requesttimestamp");
			outerKeys.add("responsetime");
			innerKeys.add("createdDateTime");
			innerKeys.add("updatedDateTime");

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


			//generation of actual response
			actualResponse = applicationLibrary.regProcPacketUpload(file, prop.getProperty("packetReceiverApi"));

			//Asserting actual and expected response
			status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);
			if (status) {
				boolean isError = expectedResponse.containsKey("errors");
				logger.info("isError ========= : "+isError);

				if(!isError){
					String actualStatus = null;
					String expectedStatus = null;
					List<Map<String,String>> response = actualResponse.jsonPath().get("response"); 
					JSONArray expected = (JSONArray) expectedResponse.get("response");
					Iterator<Object> iterator = expected.iterator();
					//extracting status from the expected response
					while(iterator.hasNext()){
						JSONObject jsonObject = (JSONObject) iterator.next();
						expectedStatus = jsonObject.get("status").toString().trim();
					}


					for(Map<String,String> res : response){
						actualStatus=res.get("status").toString();
						if (expectedStatus.matches(actualStatus)){
							logger.info("STATUS MATCHED....");
							finalStatus = "Pass";
							softAssert.assertAll();
							object.put("status", finalStatus);
							arr.add(object);
						} 
					}

				}else{
					JSONArray expectedError = (JSONArray) expectedResponse.get("errors");
					String expectedErrorCode = null;
					List<Map<String,String>> error = actualResponse.jsonPath().get("errors"); 
					for(Map<String,String> err : error){
						String errorCode = err.get("errorCode").toString();
						Iterator<Object> iterator1 = expectedError.iterator();
						// extracting error code from expected response
						while(iterator1.hasNext()){
							JSONObject jsonObject = (JSONObject) iterator1.next();
							expectedErrorCode = jsonObject.get("errorCode").toString().trim();
						}
						if(expectedErrorCode.matches(errorCode)){
							finalStatus = "Pass";
							softAssert.assertAll();
							object.put("status", finalStatus);
							arr.add(object);
						}
					}
				}
			}else{
				finalStatus="Fail";
			}
			/*boolean setFinalStatus=false;
	        if(finalStatus.equals("Fail"))
	              setFinalStatus=false;
	        else if(finalStatus.equals("Pass"))
	              setFinalStatus=true;
	        Verify.verify(setFinalStatus);
	        softAssert.assertAll();*/

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
String apiName="packetReceiver";
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
			f.set(baseTestMethod, PacketReceiver.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in PacketReceiver class in setResultTestName "+e);
		}
		
		
	}

	/**
	 * This method is used for generating output file with the test case result
	 */
	@AfterClass
	public void statusUpdate() {
		String configPath =  "src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
			file.close();
		} catch (IOException e) {
			logger.error("Exception occurred in PacketReceiver class in statusUpdate "+e);
		}
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}
