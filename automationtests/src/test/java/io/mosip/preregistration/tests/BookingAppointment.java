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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Verify;

import io.mosip.preregistration.util.PreRegistrationUtil;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * Test Class to perform Booking Appointment related Positive and Negative test
 * cases
 * 
 * @author Lavanya R
 * @since 1.0.0
 */

public class BookingAppointment extends BaseTestCase implements ITest {

	/**
	 * Declaration of all variables
	 **/

	private static Logger logger = Logger.getLogger(BookingAppointment.class);
	PreRegistrationLibrary preRegLib = new PreRegistrationLibrary();
	CommonLibrary commonLibrary = new CommonLibrary();
	static String testCaseName = "";
	String preId = "";
	SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	boolean statuOfSmokeTest = false;
	String finalStatus = "";
	JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	Response Actualresponse = null;
	JSONObject Expectedresponse = null;
	ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	String preReg_URI;
	String dest = "";
	String configPaths = "";
	String folderPath = "preReg/BookingAppointment";
	String outputFile = "BookingAppointmentOutput.json";
	String requestKeyFile = "BookingAppointmentRequest.json";
	String testParam = null;
	boolean status_val = false;
	JSONParser parser = new JSONParser();
	String URI;
	Response fetchCenter;
	JSONObject actRes;
	JSONObject dynamicReq;
	JSONObject dynamicRes;
	Response response;
	PreRegistrationUtil preRegUtil=new PreRegistrationUtil();
	

	/* implement,IInvokedMethodListener */
	public BookingAppointment() {

	}

	/*
	 * Given Booking Appointment valid request when User Send POST request to
	 * https://mosip.io/preregistration/v1/appointment/:preRegistrationId Then I
	 * should get success response with elements defined as per specifications
	 * 
	 * Given Invalid request when User send POST request to
	 * https://mosip.io/preregistration/v1/appointment/:preRegistrationId Then I
	 * should get Error response along with Error Code and Error messages as per
	 * Specification
	 */
	@DataProvider(name = "bookAppointment")
	public  Object[][] readData(ITestContext context) throws Exception {
		switch (testLevel) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	

	@SuppressWarnings("unchecked")
	@Test(dataProvider = "bookAppointment")
	public void bookingAppointment(String testSuite, Integer i, JSONObject object) throws Exception {

		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);

		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);

		String val = null;
		String name = null;
		
		/*Reading test case name from folder and based on the test case name the switching happens */
		if (testCaseName.contains("smoke")) {
			val = testCaseName;
		} else {
			String[] parts = testCaseName.split("_");
			val = parts[0];
			name = parts[1];
		}
		
		/*Creating the Pre-Registration Application*/
		Response createApplicationResponse = preRegLib.CreatePreReg();
		preId = createApplicationResponse.jsonPath().get("response.preRegistrationId").toString();

		/* Fetch availability[or]center details */
		Response fetchCenter = preRegLib.FetchCentre();

		/* Book An Appointment for the available data */
		Response bookAppointmentResponse = preRegLib.BookAppointment(fetchCenter, preId.toString());
		logger.info("bookAppointmentResponse::"+bookAppointmentResponse.asString());
		
		switch (val) {

		case "BookingAppointment_smoke":

			outerKeys.add("responsetime");
			status = AssertResponses.assertResponses(bookAppointmentResponse, Expectedresponse, outerKeys, innerKeys);

			break;

		case "ReBookingAppointment_smoke":
			
			Response rebookAppointmentRes = null;
			try {
				fetchCenter = preRegLib.FetchCentre();
				 rebookAppointmentRes = preRegLib.BookAppointment(fetchCenter, preId.toString());
				 logger.info("rebookAppointmentRes::"+rebookAppointmentRes.asString());
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			outerKeys.add("responsetime");
			status = AssertResponses.assertResponses(rebookAppointmentRes, Expectedresponse, outerKeys, innerKeys);

			break;
          case "BookAnAppointmentByPassingValidStatusCodeExpired_smoke":
			
        	  preRegLib.updateStatusCode("Expired", preId);
  			/* Fetch availability[or]center details */
  			Response fetchCenStatus = preRegLib.FetchCentre();

  			/* Book An Appointment for the available data */
  			Response bookAppointmentResStatus = preRegLib.BookAppointment(fetchCenStatus, preId.toString());
  			 logger.info("BookAnAppointmentByPassingInvalidStatusCode::"+bookAppointmentResStatus.asString());
  			outerKeys.add("responsetime");
  			innerKeys.add("preRegistrationId");
  			status = AssertResponses.assertResponses(bookAppointmentResponse, Expectedresponse, outerKeys, innerKeys);

            break;
		case "BookAnAppointmentByPassingInvalidPreRegistrationId":
			
			String preRegId= actualRequest.get("preRegistrationId").toString();
			String preRegBookingAppointmentURI = preReg_URI + preRegId;
			Response fetchCentInvPreId = preRegLib.FetchCentre();
			JSONObject actualReqInvPreId = preRegLib.BookAppointmentRequest(fetchCentInvPreId, preId.toString());
			//JSONObject actualReqInvPreId = preRegLib.BookAppointmentRequest(fetchCentInvPreId, preId.toString());
			actualReqInvPreId.put("requesttime", preRegLib.getCurrentDate());
			logger.info("BookAnAppointmentByPassingRegCen::"+actualReqInvPreId.toString());
			Response respInvPreId = applicationLibrary.postRequest(actualReqInvPreId, preRegBookingAppointmentURI);
			logger.info("BookAnAppointmentByPassingInvalidRegistrationCenterId::"+respInvPreId.asString());
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			status = AssertResponses.assertResponses(respInvPreId, Expectedresponse, outerKeys, innerKeys);

			break;

		case "BookAnAppointmentByPassingInvalidStatusCode":

			//preRegLib.updateStatusCode("Consumed", preId);
			preRegLib.updateStatusCode("Consumed", preId);
			
			/* Fetch availability[or]center details */
			Response fetchCen = preRegLib.FetchCentre();
			
			/* Book An Appointment for the available data */
			Response bookAppointmentRes = preRegLib.BookAppointment(fetchCen, preId.toString());
			 logger.info("BookAnAppointmentByPassingInvalidStatusCode::"+bookAppointmentRes.asString());
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			status = AssertResponses.assertResponses(bookAppointmentResponse, Expectedresponse, outerKeys, innerKeys);

			break;
		case "BookAnAppointmentByPassingInvalidId":

		    String id= actualRequest.get("id").toString();
		    
		    
		    String preRegBookAppURIInvId = preReg_URI + preId;
			Response fetchCenIdInvId = preRegLib.FetchCentre();
			JSONObject actualReqInvId = preRegLib.BookAppointmentRequest(fetchCenIdInvId, preId.toString());
			actualReqInvId.put("id", id);
			actualReqInvId.put("requesttime", preRegLib.getCurrentDate());
			logger.info("BookAnAppointmentByPassingInvalidIdRegCen::"+actualReqInvId.toString());
			Response respInvId = applicationLibrary.postRequest(actualReqInvId, preRegBookAppURIInvId);
			logger.info("BookAnAppointmentByPassingInvalidId::"+respInvId.asString());
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			status = AssertResponses.assertResponses(respInvId, Expectedresponse, outerKeys, innerKeys);
		    
			
			break;
		case "BookAnAppointmentByPassingInvalidRegistrationCenterId":
			String regCenterId = actualRequest.get("registration_center_id").toString();
			logger.info("Invalid reg centterererer:"+regCenterId);
			
			//dynamicChangeOfRequest
			
			String preRegBookAppURI = preReg_URI + preId;
			Response fetchCent = preRegLib.FetchCentre();
			JSONObject actualReq = preRegLib.BookAppointmentRequest(fetchCent, preId.toString());
			JSONObject actReqInvRegCenter = preRegUtil.dynamicChangeOfRequest(actualReq, "$.request.registration_center_id", regCenterId);
			
			actReqInvRegCenter.put("requesttime", preRegLib.getCurrentDate());
			logger.info("BookAnAppointmentByPassingRegCen::"+actReqInvRegCenter.toString());
			Response resp = applicationLibrary.postRequest(actReqInvRegCenter, preRegBookAppURI);
			logger.info("BookAnAppointmentByPassingInvalidRegistrationCenterId::"+resp.asString());
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			status = AssertResponses.assertResponses(resp, Expectedresponse, outerKeys, innerKeys);

			break;
		case "BookAnAppointmentByPassingInvalidAppointmentDate":

			String appDate = actualRequest.get("appointment_date").toString();
			logger.info("Invalid reg centterererer:"+appDate);
			
			//dynamicChangeOfRequest
			
			String preRegBookAppAppDateURI = preReg_URI + preId;
			Response fetchCentAppDate = preRegLib.FetchCentre();
			JSONObject actualAppDateReq = preRegLib.BookAppointmentRequest(fetchCentAppDate, preId.toString());
			JSONObject actReqInvAppDate = preRegUtil.dynamicChangeOfRequest(actualAppDateReq, "$.request.appointment_date", appDate);
			
			actReqInvAppDate.put("requesttime", preRegLib.getCurrentDate());
			logger.info("BookAnAppointmentByPassingRegCen::"+actReqInvAppDate.toString());
			Response respAppDate = applicationLibrary.postRequest(actReqInvAppDate, preRegBookAppAppDateURI);
			logger.info("BookAnAppointmentByPassingInvalidAppointmentDate::"+respAppDate.asString());
			if(testCaseName.contains("DateLessThanToday"))
			{
				outerKeys.add("responsetime");
				innerKeys.add("message");
				
				preRegLib.compareValues(respAppDate.jsonPath().get("errors[0].message"), "Invalid Booking Date Time found for preregistration id - "+preId);
				
			}else
			{
				outerKeys.add("responsetime");
				innerKeys.add("preRegistrationId");
				innerKeys.add("message");
			}
			
			status = AssertResponses.assertResponses(respAppDate, Expectedresponse, outerKeys, innerKeys);

			break;
		case "BookAnAppointmentByPassingInvalidTimeSlotFrom":
			
			String timeSlotFrom = actualRequest.get("time_slot_from").toString();
			logger.info("Invalid reg centterererer:"+timeSlotFrom);
			
			//dynamicChangeOfRequest
			
			String preRegBookAppTimeSlotFrmURI = preReg_URI + preId;
			Response fetchCentTimeSlotFrm = preRegLib.FetchCentre();
			JSONObject actualTimeSlotFrmReq = preRegLib.BookAppointmentRequest(fetchCentTimeSlotFrm, preId.toString());
			JSONObject actReqInvTimeSlotFrm = preRegUtil.dynamicChangeOfRequest(actualTimeSlotFrmReq, "$.request.time_slot_from", timeSlotFrom);
			
			actReqInvTimeSlotFrm.put("requesttime", preRegLib.getCurrentDate());
			logger.info("BookAnAppointmentByPassingRegCen::"+actReqInvTimeSlotFrm.toString());
			Response respTimeSlotFrm = applicationLibrary.postRequest(actReqInvTimeSlotFrm, preRegBookAppTimeSlotFrmURI);
			logger.info("BookAnAppointmentByPassingInvalidRegistrationCenterId::"+respTimeSlotFrm.asString());
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			status = AssertResponses.assertResponses(respTimeSlotFrm, Expectedresponse, outerKeys, innerKeys);

			break;

		case "BookAnAppointmentByPassingInvalidTimeSlotTo":


			String timeSlotTo = actualRequest.get("time_slot_to").toString();
			logger.info("Invalid reg centterererer:"+timeSlotTo);
			
			//dynamicChangeOfRequest
			
			String preRegBookAppTimeSlotToURI = preReg_URI + preId;
			Response fetchCentTimeSlotTo = preRegLib.FetchCentre();
			JSONObject actualTimeSlotToReq = preRegLib.BookAppointmentRequest(fetchCentTimeSlotTo, preId.toString());
			JSONObject actReqInvTimeSlotTo = preRegUtil.dynamicChangeOfRequest(actualTimeSlotToReq, "$.request.time_slot_to", timeSlotTo);
			
			actReqInvTimeSlotTo.put("requesttime", preRegLib.getCurrentDate());
			logger.info("BookAnAppointmentByPassingRegCen To::"+actReqInvTimeSlotTo.toString());
			Response respTimeSlotTo = applicationLibrary.postRequest(actReqInvTimeSlotTo, preRegBookAppTimeSlotToURI);
			logger.info("BookAnAppointmentByPassingInvalidRegistrationCenterId To::"+respTimeSlotTo.asString());
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			status = AssertResponses.assertResponses(respTimeSlotTo, Expectedresponse, outerKeys, innerKeys);

			break;

       case "BookAnAppointmentByPassingInvalidRequestTime":
			
			String reqTime = actualRequest.get("requesttime").toString();
			logger.info("Invalid requesttime:"+reqTime);
			
			//dynamicChangeOfRequest
			
			 URI = preReg_URI + preId;
			 fetchCenter = preRegLib.FetchCentre();
			JSONObject actRes = preRegLib.BookAppointmentRequest(fetchCenter, preId.toString());
			JSONObject dynamicReq = preRegUtil.dynamicChangeOfRequest(actRes, "$.requesttime", reqTime);
			
			
			logger.info("BookAnAppointmentByPassingInvalidRequestTime::"+dynamicReq.toString());
			 response = applicationLibrary.postRequest(dynamicReq, URI);
			logger.info("BookAnAppointmentByPassingInvalidRequestTime::"+response.asString());
			outerKeys.add("responsetime");
			innerKeys.add("preRegistrationId");
			status = AssertResponses.assertResponses(response, Expectedresponse, outerKeys, innerKeys);

			break;
		default:

			break;
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

		setFinalStatus = finalStatus.equals("Pass") ? true : false;

		Verify.verify(setFinalStatus);
		softAssert.assertAll();

	}

	/**
	 * Writing output into configpath
	 * 
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */

	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {

		String configPath = "src/test/resources/" + folderPath + "/" + outputFile;

		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}

		String source = "src/test/resources/" + folderPath + "/";
		CommonLibrary.backUpFiles(source, folderPath);

		/*
		 * Add generated PreRegistrationId to list to be Deleted from DB AfterSuite
		 */

		// preIds.add(preId); 

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
			f.set(baseTestMethod, "Pre Reg_BookAnAppointment_" +BookingAppointment.testCaseName);
			//f.set(baseTestMethod, BookingAppointment.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	/**
	 * Declaring the Booking Appointment Resource URI and getting the test case
	 * name
	 * 
	 * @param result
	 */
	@BeforeMethod(alwaysRun = true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();

		// Booking Appointment Resource URI
		preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_BookingAppointmentURI");
		//Fetch the generated Authorization Token by using following Kernel AuthManager APIs
		authToken = preRegLib.getToken();
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}















