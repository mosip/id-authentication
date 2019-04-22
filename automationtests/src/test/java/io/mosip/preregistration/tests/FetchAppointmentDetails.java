package io.mosip.preregistration.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
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
import com.google.common.base.Verify;

import io.mosip.dbaccess.PreRegDbread;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.GetHeader;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to Fetch Appointment Details related Positive and Negative test cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class FetchAppointmentDetails extends BaseTestCase implements ITest {
//implement,IInvokedMethodListener
	public FetchAppointmentDetails() {

	}
	static 	String preId="";
	static SoftAssert softAssert=new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(FetchAppointmentDetails.class);
	boolean status = false;
	boolean statuOfSmokeTest = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	static String testParam=null;
	boolean status_val = false;
	private static String preReg_URI ;
	private static CommonLibrary commonLibrary = new CommonLibrary();
	
	
	static String dest = "";
	static String folderPath = "preReg/FetchAppointmentDetails";
	static String outputFile = "FetchAppointmentDetailsOutput.json";
	static String requestKeyFile = "FetchAppointmentDetailsRequest.json";
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	static PreRegistrationLibrary preRegLib=new PreRegistrationLibrary();
	/**
	 * Reading data from file
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@DataProvider(name = "FetchAppointmentDetails")
	public static Object[][] readData(ITestContext context) throws Exception {

		//testParam="smoke";
		testParam = context.getCurrentXmlTest().getParameter("testType");
		switch ("regression") {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	@SuppressWarnings("unchecked")
	@Test(dataProvider = "FetchAppointmentDetails")
	public void fetchAppointmentDetails(String testSuite, Integer i, JSONObject object) throws Exception {
	
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
        JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		
		
		String testCase = object.get("testCaseName").toString();
		/*String val = testCaseName.contains("PendingAppointment")
				?(testCaseName="cond1"):testCaseName.contains("FetchAppointmentDetailsByPassingInvalidStatusCode")
				?(testCaseName="cond2"):(testCaseName="cond3");*/
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		// Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg();
		preId = createApplicationResponse.jsonPath().get("response[0].preRegistrationId").toString();
		
		
		if(testCase.contains("smoke"))
		{	
		
		
		//Fetch availability[or]center details
		Response fetchCenter = preRegLib.FetchCentre();
		
		//Book An Appointment for the available data
		Response bookAppointmentResponse = preRegLib.BookAppointment(fetchCenter, preId.toString());
		
		
		//Fetch Appointment Details
		Response fetchAppointmentDetailsResponse = preRegLib.FetchAppointmentDetails(preId);
		
		
		outerKeys.add("responsetime");
		innerKeys.add("registration_center_id");
		innerKeys.add("appointment_date");
		innerKeys.add("time_slot_from");
		innerKeys.add("time_slot_to");
		
		status = AssertResponses.assertResponses(fetchAppointmentDetailsResponse, Expectedresponse, outerKeys, innerKeys);
		
		}
		
		else
		{	
		
			if(testCase.contains("FetchAppointmentDetailsByPassingInvalidStatusCode"))
			{	
			
			  String statusCode=actualRequest.get("statusCode").toString();
			  preRegLib.updateStatusCode(statusCode, preId);
			  
			}
			else
			{
				preId = actualRequest.get("preRegistrationId").toString();
			}
			System.out.println("PreId:"+preId);
			preReg_URI = preReg_URI + preId;
			Actualresponse = applicationLibrary.get_RequestWithoutBody(preReg_URI);
			System.out.println("Status Code::"+testCase+"Fetch App Det:"+Actualresponse.asString());
			outerKeys.add("responsetime");
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);

		
		}
		
		
		if (status) {
			finalStatus = "Pass";
			softAssert.assertAll();
			object.put("status", finalStatus);
			arr.add(object);
		} else {
			finalStatus = "Fail";
		}
		boolean setFinalStatus = false;
		if (finalStatus.equals("Fail"))
			setFinalStatus = false;
		else if (finalStatus.equals("Pass"))
			setFinalStatus = true;
		Verify.verify(setFinalStatus);
		softAssert.assertAll();
	
		
	}

	
	
	@BeforeMethod(alwaysRun = true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
	
		testCaseName = object.get("testCaseName").toString();
		
		 /**
         * Fetch Appointment Details Resource URI            
         */
        
        preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_FecthAppointmentDetailsURI");
        authToken=preRegLib.getToken();
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
			f.set(baseTestMethod, FetchAppointmentDetails.testCaseName);
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
		
		//Add generated PreRegistrationId to list to be Deleted from DB AfterSuite 
				preIds.add(preId);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}


}
