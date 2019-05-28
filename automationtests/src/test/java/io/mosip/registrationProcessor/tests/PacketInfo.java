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
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

public class PacketInfo extends BaseTestCase implements ITest {
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(PacketInfo.class);
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
	static String folderPath = "regProc/PacketInfo";
	static String outputFile = "PacketInfoOutput.json";
	static String requestKeyFile = "PacketInfoRequest.json";
	static String description = "";
	static String apiName = "PacketInfoApi";
	static String moduleName = "RegProc";
	CommonLibrary common = new CommonLibrary();

	/**
	 * This method is used for reading the test data based on the test case name
	 * passed
	 *
	 * @param context
	 * @return Object[][]
	 */
	@DataProvider(name = "packetInfo")
	public Object[][] readData(ITestContext context) {
		Object[][] readFolder = null;
		String propertyFilePath = System.getProperty("user.dir") + "\\"
				+ "src\\config\\RegistrationProcessorApi.properties";
		try {
			prop.load(new FileReader(new File(propertyFilePath)));
			String testParam = context.getCurrentXmlTest().getParameter("testType");
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
			logger.error("Exception occurred in Sync class in readData method " + e);
		}
		return readFolder;
	}

	/**
	 * This method is used for generating actual response and comparing it with
	 * expected response along with db check and audit log check
	 * 
	 * @param testSuite
	 * @param i
	 * @param object
	 */
	@Test(dataProvider = "packetInfo")
	public void packetInfo(String testSuite, Integer i, JSONObject object) {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		RegProcDataRead readDataFromDb = new RegProcDataRead();

		// testCaseName =testCaseName +": "+ description;
		try {
			actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
			// Expected response generation
			expectedResponse = ResponseRequestMapper.mapResponse(testSuite, object);

			// Actual response generation
			actualResponse = applicationLibrary.regProcAssignmentRequest(prop.getProperty("packetInfoApi"),
					actualRequest);

			// outer and inner keys which are dynamic in the actual response
			outerKeys.add("requesttime");
			outerKeys.add("responsetime");
			innerKeys.add("createdDateTime");
			innerKeys.add("updatedDateTime");

			// Assertion of actual and expected response
			status = AssertResponses.assertResponses(actualResponse, expectedResponse, outerKeys, innerKeys);

			logger.info("Status after assertion : " + status);

			if (status) {

				boolean isError = expectedResponse.containsKey("errors");
				logger.info("isError ========= : "+isError);

				if(!isError){
					String response = actualResponse.jsonPath().get("response"); 
					logger.info("response : "+response );
					if(response!=null) {
						finalStatus = "Pass";
						softAssert.assertTrue(true);
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

				boolean setFinalStatus = false;
				if (finalStatus.equals("Fail"))
					setFinalStatus = false;
				else if (finalStatus.equals("Pass"))
					setFinalStatus = true;
				Verify.verify(setFinalStatus);
				softAssert.assertAll();

			} catch (IOException | ParseException e) {
				logger.error("Exception occurred in PacketInfo class in PacketInfo method " + e);
				// Verify.verify(false);
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
		public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
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
				f.set(baseTestMethod, PacketInfo.testCaseName);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				logger.error("Exception occurred in PacketInfo class in setResultTestName method " + e);
			}

			/*
			 * if(result.getStatus()==ITestResult.SUCCESS) { Markup
			 * m=MarkupHelper.createCodeBlock("Request Body is  :"+System.lineSeparator()+
			 * actualRequest.toJSONString()); Markup
			 * m1=MarkupHelper.createCodeBlock("Expected Response Body is  :"+System.
			 * lineSeparator()+expectedResponse.toJSONString()); test.log(Status.PASS, m);
			 * test.log(Status.PASS, m1); }
			 * 
			 * if(result.getStatus()==ITestResult.FAILURE) { Markup
			 * m=MarkupHelper.createCodeBlock("Request Body is  :"+System.lineSeparator()+
			 * actualRequest.toJSONString()); Markup
			 * m1=MarkupHelper.createCodeBlock("Expected Response Body is  :"+System.
			 * lineSeparator()+expectedResponse.toJSONString()); test.log(Status.FAIL, m);
			 * test.log(Status.FAIL, m1); } if(result.getStatus()==ITestResult.SKIP) {
			 * Markup
			 * m=MarkupHelper.createCodeBlock("Request Body is  :"+System.lineSeparator()+
			 * actualRequest.toJSONString()); Markup
			 * m1=MarkupHelper.createCodeBlock("Expected Response Body is  :"+System.
			 * lineSeparator()+expectedResponse.toJSONString()); test.log(Status.SKIP, m);
			 * test.log(Status.SKIP, m1); }
			 */
		}

		/**
		 * This method is used for generating output file with the test case result
		 */
		@AfterClass
		public void statusUpdate() {
			String configPath = "src/test/resources/" + folderPath + "/" + outputFile;
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				file.close();
				logger.info("Successfully updated Results to " + outputFile);
			} catch (IOException e) {
				logger.error("Exception occurred in PacketInfo method in statusUpdate method " + e);
			}
			String source = "src/test/resources/" + folderPath + "/";
		}

		@Override
		public String getTestName() {
			return this.testCaseName;
		}

	}
