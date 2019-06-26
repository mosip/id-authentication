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
import org.json.simple.parser.ParseException;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.preregistration.util.PreRegistrationUtil;
import io.mosip.preregistration.util.TriggerNotificationUtil;

import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Trigger notification related Positive and Negative test
 * cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class TriggerNotification extends BaseTestCase implements ITest {
	/**
	 * Declaration of all variables
	 **/
	String folder = "preReg";
	String preId = "";
	SoftAssert softAssert = new SoftAssert();
	static String testCaseName = "";
	Logger logger = Logger.getLogger(TriggerNotification.class);
	boolean status = false;
	boolean statuOfSmokeTest = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	String preReg_URI;
	String create_preReg_URI;
	CommonLibrary commonLibrary = new CommonLibrary();
	String dest = "";
	String configPaths = "";
	String folderPath = "preReg/TriggerNotification";
	String outputFile = "TriggerNotificationRequestOutput.json";
	String requestKeyFile = "TriggerNotificationRequest.json";
	String testParam = null;
	boolean status_val = false;
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	PreRegistrationUtil preregUtil = new PreRegistrationUtil();
	TriggerNotificationUtil triggerNotUtil = new TriggerNotificationUtil();
	PreRegistrationUtil preRegUtil=new PreRegistrationUtil();
	PreRegistrationLibrary lib =new PreRegistrationLibrary();
	ApplicationLibrary appLib=new ApplicationLibrary();
	String name;
	String appDate;
	String timeSlotFrom;
	String mobNum;
	String emailId;
	String langCode;
	String id;
	String version;
	String requesttime;
	
	public TriggerNotification() {

	}

	/**
	 * Data Providers to read the input json files from the folders
	 * 
	 * @param context
	 * @return input request file
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	@DataProvider(name = "TriggerNotification")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		switch (testLevel) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");

		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}

	}

	/*
	 * Given Document Upload valid request when I Send POST request to
	 * https://mosip.io/preregistration/v1/notification/notify Then I should get
	 * success response with elements defined as per specifications Given
	 * Invalid request when I send POST request to
	 * https://mosip.io/preregistration/v1/notification/notify Then I should get
	 * Error response along with Error Code and Error messages as per
	 * Specification
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "TriggerNotification")
	public void triggerNotification(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		JSONObject actualReq;
		char timeVal = 0;
		/*id="mosip.pre-registration.notification.notify";
		version="1.0";*/
		id= preregUtil.fetchPreregProp().get("req.id");
		version=preregUtil.fetchPreregProp().get("req.ver");
		
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		String val = null;
		if(testCaseName.contains("true"))
		{
			val="true";
		}
		else
		{
			val="false";
		}
		
		actualReq= preRegUtil.requestJson("TriggerNotification/preReg_TriggerNotification_additionalRecipient_"+val+"_smoke/","request");
		JSONObject req = preRegUtil.requestJson("TriggerNotification/preReg_TriggerNotification_additionalRecipient_"+val+"_smoke/","val");
		
		req.put("requesttime", preRegLib.getCurrentDate());
		Response createApplicationResponse =appLib.postWithJson(create_preReg_URI, req, authToken);
		
		logger.info("createApplicationResponse::"+createApplicationResponse.asString());
		
		preId = createApplicationResponse.jsonPath().get("response.preRegistrationId").toString();
		langCode= createApplicationResponse.jsonPath().get("response.langCode").toString();
		emailId=createApplicationResponse.jsonPath().get("response.demographicDetails.identity.email").toString();
		mobNum= createApplicationResponse.jsonPath().get("response.demographicDetails.identity.phone").toString();
		
		/* Fetch availability[or]center details */
		Response fetchCenter = preRegLib.FetchCentre(authToken);

		logger.info("fetchCenter:"+fetchCenter.asString());
		
		/* Book An Appointment for the available data */
		Response bookAppRes = preRegLib.BookAppointment(fetchCenter, preId.toString(),authToken);
		Response fetchApp = preRegLib.FetchAppointmentDetails(preId, authToken);
		appDate= fetchApp.jsonPath().get("response.appointment_date").toString();
		timeSlotFrom= fetchApp.jsonPath().get("response.time_slot_from").toString();
		
        /** Convert 24 to 12 hour format **/
		
		String appTime=timeSlotFrom;
		String[] parts = appTime.split(":");
		int val1=Integer.parseInt(parts[0]);
		String val2 = parts[1];
		int res=val1-12;
		String value;
		if(res > 0)
		{
			
			String s1 = String.format("%02d", res);
			value=s1+":"+val2+" PM";
		}
		else
		{
			String s2 =String.format("%02d", val1);
			value=s2+":"+val2+" AM";
		}
		
		
		String[] split = testCaseName.split("_");
		String parameter = split[2];
		
		switch (parameter) 
		{
		case "preRegistrationId":
			
			preId=actualRequest.get("preRegistrationId").toString();
			
			break;
			
       case "appointmentDate":
    	   
    	   appDate=actualRequest.get("appointmentDate").toString();
			
			break;
			
       case "appointmentTime":
    	   
    	   value=actualRequest.get("appointmentTime").toString();
			
			break;

       case "mobNum":
    	  
    	   mobNum=actualRequest.get("mobNum").toString();
   			
   			break;
   			
   			
       case "emailId":
    	  
    	   emailId=actualRequest.get("emailID").toString();
  			
  			break;
  			
       case "langcode":
    	   
    	   langCode=actualRequest.get("langcode").toString();
 			
 			break;
 			
       case "id":
    	   
    	   id=actualRequest.get("id").toString();
    	   
 			break;
       case "version":
    	   
    	   version=actualRequest.get("version").toString();
    	
 			break;
        case "requesttime":
    	   
        	requesttime=actualRequest.get("requesttime").toString();
    	   
 			break;

		default:
			break;
		}
		
	
		JSONObject verReq = preRegUtil.dynamicChangeOfRequest(actualReq, "$.version", version);
		JSONObject idReq = preRegUtil.dynamicChangeOfRequest(verReq, "$.id", id);
        JSONObject MobReq = preRegUtil.dynamicChangeOfRequest(idReq, "$.request.mobNum", mobNum);
		JSONObject emailReq = preRegUtil.dynamicChangeOfRequest(MobReq, "$.request.emailID", emailId);
		JSONObject reqPreId = preRegUtil.dynamicChangeOfRequest(emailReq, "$.request.preRegistrationId", preId);
		JSONObject reqAppDate =preRegUtil.dynamicChangeOfRequest(reqPreId, "$.request.appointmentDate", appDate);
		JSONObject langCodReq = preRegUtil.dynamicChangeOfRequest(reqAppDate, "$.request.langCode", langCode);
		actualReq=preRegUtil.dynamicChangeOfRequest(langCodReq, "$.request.appointmentTime", value);
		
		 
		if (!(testCaseName.contains("requesttime"))) 
		{
			actualReq.put("requesttime", preRegLib.getCurrentDate());
		}
		else
		{
			actualReq.put("requesttime", requesttime);
		}


		
		logger.info("Actual request:"+actualReq);
		
		Actualresponse =triggerNotUtil.TriggerNotification(preReg_URI,actualReq,authToken,testCaseName);
		logger.info("Test Case Name:"+testCaseName+"\nTrigger Notification Response:"+Actualresponse.asString());

			// outer and inner keys which are dynamic in the actual response
			outerKeys.add("responsetime");
			// Asserting actual and expected response
			status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
			
		if (status) {
			finalStatus = "Pass";
			softAssert.assertAll();
			object.put("status", finalStatus);
			arr.add(object);
		} else {
			finalStatus = "Fail";
		}

		boolean setFinalStatus = false;

		setFinalStatus = finalStatus.equals("Pass") ? true : false;

		Verify.verify(setFinalStatus);
		softAssert.assertAll();

	}


	/**
	 * Writing test case name into testng
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, TriggerNotification.testCaseName);

		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
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
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();

		// Trigger Notification Resource URI
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_NotifyURI");
		create_preReg_URI =commonLibrary.fetch_IDRepo().get("preReg_CreateApplnURI");
		// Fetch the generated Authorization Token by using following Kernel AuthManager APIs
		authToken = preRegLib.getToken();
		if (!lib.isValidToken(individualToken)) {
			individualToken = lib.getToken();
		}

	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}
