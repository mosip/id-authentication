package io.mosip.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import io.mosip.dbaccess.prereg_dbread;
import io.mosip.dbentity.AccessToken;
import io.mosip.dbentity.OtpEntity;
import io.mosip.dbentity.PreRegEntity;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.GetHeader;
import io.restassured.response.Response;

/**
 * @author Tabish,Lavanya R,Ashish Rastogi
 *
 */

public class PreRegistrationLibrary extends BaseTestCase {

	/**
	 * Declaration of all variables
	 **/

	static String folder = "preReg";
	static String testSuite = "";
	static String userId="";
	static String otp="";
	static Response createPregResponse;
	static JSONObject createPregRequest;
	static Response response;
	static JSONObject request;
	static JSONObject request1;
	static String preReg_Id = "";
	JSONParser parser = new JSONParser();
	static ApplicationLibrary applnLib = new ApplicationLibrary();
	private static Logger logger = Logger.getLogger(BaseTestCase.class);
	private static CommonLibrary commonLibrary = new CommonLibrary();
	private static String preReg_CreateApplnURI;

	private static String preReg_DataSyncnURI;
	private static String preReg_NotifyURI;
	private static String preReg_DocumentUploadURI;
	private static String preReg_FetchRegistrationDataURI;
	private static String preReg_FetchCenterIDURI;
	private static String preReg_BookingAppointmentURI;
	private static String preReg_FecthAppointmentDetailsURI;
	private static String preReg_FetchAllDocumentURI;
	private static String prereg_DeleteDocumentByDocIdURI;
	private static String preReg_DeleteAllDocumentByPreIdURI;
	private static String preReg_CopyDocumentsURI;
	private static String preReg_ConsumedURI;
	private static String preReg_FetchBookedPreIdByRegIdURI;
	private static String preReg_FetchAllApplicationCreatedByUserURI;
	private static String preReg_DiscardApplnURI;
	private static String preReg_FetchStatusOfApplicationURI;
	private static String preReg_UpdateStatusAppURI;
	private static String preReg_CancelAppointmentURI;
	private static String preReg_ExpiredURI;
	private static String preReg_ReverseDataSyncURI;
	 private static String preReg_DiscardBookingURI;
	 private static String preReg_SyncMasterDataURI;
	 private static String otpSend_URI;
	 private static String validateOTP_URI;
	 private static String langCodeKey; 
	 private static String preReg_AdminTokenURI;
	 private static String preReg_translitrationRequestURI;

	/*
	 * We configure the jsonProvider using Configuration builder.
	 */
	Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider()).build();

	/*
	 * Generic method to Create Pre-Registration Application
	 * 
	 */
	public static Response CreatePreReg() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					createPregRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		createPregResponse = applnLib.postRequest(createPregRequest, preReg_CreateApplnURI);
		/*preReg_Id = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Assert.assertTrue(preReg_Id != null);*/
		return createPregResponse;
	}
	/**
	 * Request body for translitration service
	 * @param testSuite
	 * @param from_field_lang
	 * @param from_field_value
	 * @param to_field_lang
	 * @return
	 */
	public JSONObject translitrationRequest(String testSuite,String from_field_lang,String from_field_value,String to_field_lang) {
		JSONObject createPregRequest = null;
		JSONObject translitrationRequest = null;
		/**
		 * Reading request body from configpath
		 */
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					translitrationRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}

			}
		}
		String createdBy = new Integer(createdBy()).toString();
		JSONObject object = null;
		for (Object key : translitrationRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) translitrationRequest.get(key);
				object.put("from_field_lang", from_field_lang);
				object.put("from_field_value", from_field_value);
				object.put("to_field_lang", to_field_lang);
				translitrationRequest.replace(key, object);
			}
		}

		return translitrationRequest;
	}
	/**
	 * method for translitration service
	 * @param translitrationRequest
	 * @return
	 */
	public Response translitration(JSONObject translitrationRequest) {
		try {
			response = applnLib.postRequest(translitrationRequest.toJSONString(), preReg_translitrationRequestURI);
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}
	
	/**
	 * Generate OTP
	 * @return
	 */
	public static Response generateOTP(JSONObject request) {
		response = applnLib.postRequest(request, otpSend_URI);
		return response;
	}
	
	public String getToken()
	{
		
		testSuite="generateOTP/generateOTP_smoke";
		request=otpRequest(testSuite);
		Response generateOTPResponse = generateOTP(request);
		logger.info("userid is ++++++++++++++"+userId);
		String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='"+userId+"'";
		List<Object> otpData = prereg_dbread.fetchOTPFromDB(otpQueryStr, OtpEntity.class);
		otp = otpData.get(0).toString();
		logger.info("OTP is============"+otp);
		testSuite = "validateOTP/validateOTP_smoke";
		request=validateOTPRequest(testSuite);
		Response validateOTPRes = validateOTP(request);
	    String cookieValue = validateOTPRes.getCookie("Authorization");
		String auth_token = cookieValue;
		
		return auth_token;
	
	}
	/**
	 * Fetching status of consumed application
	 * @param PreID-PRID of the consumed application  
	 * @return
	 */
	public String getConsumedStatus(String PreID)
	{
		String query="SELECT c.status_code FROM prereg.applicant_demographic_consumed c where c.prereg_id='"+PreID+"'";
		List<Object> preId_status = prereg_dbread.getConsumedStatus(query, PreRegEntity.class, "prereginteg.cfg.xml", "preregqa.cfg.xml");
		String status=preId_status.get(0).toString();
		return status;
	}
	/**
	 * Get Document Id for Consumed Application
	 * @param PreID
	 * @return
	 */
	public String getDocumentIdOfConsumedApplication(String PreID)
	{
		String query="SELECT c.id FROM prereg.applicant_document_consumed c where c.prereg_id='"+PreID+"'";
		List<Object> preId_status = prereg_dbread.getConsumedStatus(query, PreRegEntity.class, "prereginteg.cfg.xml", "preregqa.cfg.xml");
		String documentId=preId_status.get(0).toString();
		return documentId;
	}
	
	public String getRegCenterIdOfConsumedApplication(String PreID)
	{
		String query="SELECT c.regcntr_id FROM prereg.reg_appointment_consumed c where c.prereg_id='"+PreID+"'";
		List<Object> preId_status = prereg_dbread.getConsumedStatus(query, PreRegEntity.class, "prereginteg.cfg.xml", "preregqa.cfg.xml");
		String regCenterId=preId_status.get(0).toString();
		return regCenterId;
	}
	
	/**
	 * VALIDATING OTP
	 * @param request
	 * @return
	 */
	public static Response validateOTP(JSONObject request) {
		response = applnLib.postRequest(request, validateOTP_URI);
		return response;
	}
	
	/**
	 * ValidateRequest
	 * @return
	 */
	public JSONObject validateOTPRequest(String testSuite)
	{
		JSONObject otpRequest = null;
		/**
		 * Reading request body from configpath
		 */
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					otpRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				
			}
		}
		JSONObject object = null;
		for (Object key : otpRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) otpRequest.get(key);
				object.put("userId", userId);
				object.put("otp", otp);
				otpRequest.replace(key, object);
			}
		}	
		return otpRequest;
	}

	/*
	 * Function to generate the random created by data
	 * 
	 */
	public static int createdBy() {
		Random rand = new Random();
		int num = rand.nextInt(9000000) + 1000000000;
		return num;

	}

	/*
	 * Generic method to Discard the Application
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Response discardApplication(String PreRegistrationId) {
		testSuite = "Discard_Individual/Discard Individual Applicant By using Pre Registration ID_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		request.put("pre_registration_id", PreRegistrationId);
		try {
			return applnLib.deleteRequest(preReg_DiscardApplnURI, request);

		} catch (Exception e) {
			logger.info(e);
		}
		return null;
	}
	/**
	 * Method for converting JSON object into HashMap
	 * @param object
	 * @return
	 */
	public static Map<String, Object> toMap(JSONObject object) {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keySet().iterator();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}
	/**
	 * Converting JSON object into Map
	 * @param json
	 * @return
	 */
	public static Map<String, Object> jsonToMap(JSONObject json) {
		Map<String, Object> retMap = new HashMap<String, Object>();

		if (json != null) {
			retMap = toMap(json);
		}
		return retMap;
	}
	/**
	 * Method for converting JSONArray into List
	 * @param array
	 * @return
	 */
	public static List<Object> toList(JSONArray array) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}

	/**
	 * Converting byte zip array into zip and saving into preregdocs folder
	 * 
	 * @author Ashish
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public boolean fetchDocs(Response response, String folderName) {
		String data = response.jsonPath().get("response.zip-bytes").toString();
		String folder = response.jsonPath().get("response.zip-filename").toString();
		File f = new File(folderName + "/" + folder);
		f.mkdirs();
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
		ZipEntry entry = null;
		try {
			while ((entry = zipStream.getNextEntry()) != null) {

				String entryName = entry.getName();
				logger.info("ASHISH" + entryName);
				String path = folderName + "/" + folder + "/" + entryName;
				FileOutputStream out = new FileOutputStream(path);

				byte[] byteBuff = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead = zipStream.read(byteBuff)) != -1) {
					out.write(byteBuff, 0, bytesRead);
				}

				out.close();
				zipStream.closeEntry();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try {
			zipStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * Generic method to Fetch All Preregistration Created By User
	 * 
	 */

	public Response fetchAllPreRegistrationCreatedByUser(String userId) {
		testSuite = "Fetch_all_application_created_by_user/Fetch all application created User By using User ID_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					logger.info(e);
				}
			}
		}
		request.put("user_id", userId);
		try {
			logger.info("=================================" + preReg_FetchAllApplicationCreatedByUserURI);
			response = applnLib.getRequest(preReg_FetchAllApplicationCreatedByUserURI, GetHeader.getHeader(request));
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	/*
	 * Generic method to fetch the Create Pre-Registration Application
	 * 
	 */
	public Response CreatePreReg(JSONObject createRequest) {
		try {
			createPregResponse = applnLib.postRequest(createRequest.toJSONString(), preReg_CreateApplnURI);
		} catch (Exception e) {
			logger.info(e);
		}
		return createPregResponse;
	}

	/**
	 * @author Ashish Rastogi
	 * @param reverseDataSyncRequest
	 * @return method for consuming all PRID provided by Registration Processor
	 */
	public Response reverseDataSync(List<String> preRegistrationIds) {
		JSONObject reverseDataSyncRequest = null;
		testSuite = "ReverseDataSync\\ReverseDataSync_smoke";
		/**
		 * Reading request body from configpath
		 */
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					reverseDataSyncRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
			/**
			 * Adding preRegistrationIds in request
			 */
			for (Object key : reverseDataSyncRequest.keySet()) {
				try {
					reverseDataSyncRequest.get(key);
					JSONObject innerKey = (JSONObject) reverseDataSyncRequest.get(key);
					innerKey.put("preRegistrationIds", preRegistrationIds);
					innerKey.put("createdBy", userId);
					innerKey.put("updateBy", userId);

				} catch (ClassCastException e) {
					continue;
				}
			}

			try {
				response = applnLib.dataSyncPostRequest(reverseDataSyncRequest.toJSONString(), preReg_ReverseDataSyncURI);
			} catch (Exception e) {
				logger.info(e);
			}
		}
		return response;
	}

	/*
	 * Generic method to Fetch all the rebooked appointment Details of
	 * 
	 */
	public List<String> reBookGetAppointmentDetails(Response fetchCenterResponse, String date) {

		List<String> appointmentDetails = new ArrayList<>();

		int countCenterDetails = fetchCenterResponse.jsonPath().getList("response.centerDetails").size();
		for (int i = 0; i < countCenterDetails; i++) {
			if (fetchCenterResponse.jsonPath().get("response.centerDetails[0].date").toString() == date) {
				try {
					fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].timeSlots[0].fromTime")
							.toString();
				} catch (NullPointerException e) {
					continue;
				}

			}
			try {
				fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].timeSlots[0].fromTime")
						.toString();
			} catch (NullPointerException e) {
				continue;
			}
			appointmentDetails.add(fetchCenterResponse.jsonPath().get("response.regCenterId").toString());
			appointmentDetails
					.add(fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].date").toString());
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[" + i + "].timeSlots[0].fromTime").toString());
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[" + i + "].timeSlots[0].toTime").toString());
			break;
		}
		return appointmentDetails;
	}

	/*
	 * Generic method to retrieve all the preregistration data
	 * 
	 */
	public Response retrivePreRegistrationData(String preRegistrationId) {
		testSuite = "Retrive_PreRegistration/Retrive Pre registration data of an applicant after booking an appointment_smoke";
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_DataSyncnURI");
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					logger.info(e);
				}
			}
		}
		request.put("pre_registration_id", preRegistrationId);
		try {
			response = applnLib.getRequestDataSync(preReg_DataSyncnURI, GetHeader.getHeader(request));
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	/*
	 * Generic method to fetch dynamic request data
	 * 
	 */
	public JSONObject readRequest(String suite, String folderName) {
		String configPath = "src/test/resources/" + folderName + "/" + testSuite;

		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		return request;
	}

	/*
	 * Generic method to Book An Expired Appointment
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response BookExpiredAppointment(Response DocumentUploadresponse, Response FetchCentreResponse,
			String preID) {
		List<String> appointmentDetails = new ArrayList<>();
		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testSuite = "BookingAppointment\\BookingAppointment_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		JSONObject object = null;
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
					
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (Object key : request.keySet()) {
			if (key.toString().toLowerCase().equals("request")) {
				object = new JSONObject();
				JSONObject resp = null;

				try {
					resp = (JSONObject) new JSONParser().parse(DocumentUploadresponse.asString());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				JSONArray data = (JSONArray) resp.get("response");
				JSONObject json = (JSONObject) data.get(0);
				json.get("preRegistrationId");
				logger.info(preID);
				object.put("preRegistrationId", preID);
				JSONObject innerData = new JSONObject();

				appointmentDetails = getExpiredAppointmentDetails(FetchCentreResponse);
				regCenterId = appointmentDetails.get(0);
				appDate = appointmentDetails.get(1);
				timeSlotFrom = appointmentDetails.get(2);
				timeSlotTo = appointmentDetails.get(3);

				innerData.put("registration_center_id", regCenterId);
				innerData.put("appointment_date", appDate);
				innerData.put("time_slot_from", timeSlotFrom);
				innerData.put("time_slot_to", timeSlotTo);
				object.put("newBookingDetails", innerData);
				JSONArray objArr = new JSONArray();
				objArr.add(object);
				request.replace(key, objArr);

			}
		}
		response = applnLib.postRequest(request, preReg_BookingAppointmentURI);
		return response;
	}

	/*
	 * Generic method to get the date
	 * 
	 */
	public String getDate(int no) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, no); // Adding 5 days
		String date = sdf.format(c.getTime());
		return date;
	}

	/*
	 * Generic method to get the Expired Appointment Details
	 * 
	 */
	public List<String> getExpiredAppointmentDetails(Response fetchCenterResponse) {

		List<String> appointmentDetails = new ArrayList<>();
		String date = getDate(-1);

		fetchCenterResponse.jsonPath().get("response.centerDetails[1].timeSlots[20].fromTime");
		appointmentDetails.add(fetchCenterResponse.jsonPath().get("response.regCenterId").toString());
		appointmentDetails.add(date);
		appointmentDetails
				.add(fetchCenterResponse.jsonPath().get("response.centerDetails[1].timeSlots[20].fromTime").toString());
		appointmentDetails
				.add(fetchCenterResponse.jsonPath().get("response.centerDetails[1].timeSlots[20].toTime").toString());
		return appointmentDetails;
	}

	/*
	 * Generic method to get the PreRegistration Status
	 * 
	 */
	public Response getPreRegistrationStatus(String preRegistartionId) {
		testSuite = "Fetch_the_status_of_a_application/Fetch Status of the application_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					logger.info(e);
				}
			}
		}

		request.put("pre_registration_id", preRegistartionId);
		try {
			response = applnLib.getRequest(preReg_FetchStatusOfApplicationURI, GetHeader.getHeader(request));
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	/*
	 * Generic method to Upload Document
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response documentUpload(Response responseCreate) {
		testSuite = "DocumentUpload/DocumentUpload_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File file = new File(configPath + "/AadhaarCard_POA.pdf");

		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		JSONObject object = null;
		for (Object key : request.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) request.get(key);
				object.put("pre_registartion_id",
						responseCreate.jsonPath().get("response[0].preRegistrationId").toString());
				request.replace(key, object);
			}
		}

		response = applnLib.putFileAndJson(preReg_DocumentUploadURI, request, file);

		return response;
	}

	/*
	 * Generic method to Upload Document
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response documentUpload(Response responseCreate, String fileName) {
		testSuite = "DocumentUpload/DocumentUpload_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File file = new File(configPath + "/" + fileName + ".pdf");
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		JSONObject object = null;
		for (Object key : request.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) request.get(key);
				object.put("pre_registartion_id",
						responseCreate.jsonPath().get("response[0].preRegistrationId").toString());
				request.replace(key, object);
			}
		}
		try {
			response = applnLib.putFileAndJson(preReg_DocumentUploadURI, request, file);
		} catch (Exception e) {
		}
		return response;
	}
	/**
	 * method to get Pre Registration admin auth token
	 * @return
	 */
	public String preRegAdminToken()
	{
		JSONObject preRegAdminTokenRequest = null;
		testSuite = "preRegAdminToken/preRegAdminToken_smoke";
		/**
		 * Reading request body from configpath
		 */
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					preRegAdminTokenRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				
			}
		}
		try {
			response = applnLib.authPostRequest(preRegAdminTokenRequest.toJSONString(), preReg_AdminTokenURI);
		} catch (Exception e) {
			logger.info(e);
		}
		String cookieValue = response.getCookie("Authorization");
		String auth_token = cookieValue;
		return auth_token;	
	}
	public String regClientAdminToken()
	{
		JSONObject regClientAdminTokenRequest = null;
		testSuite = "regClientAdminToken/regClientAdminToken_smoke";
		/**
		 * Reading request body from configpath
		 */
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					regClientAdminTokenRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				
			}
		}
		try {
			response = applnLib.authPostRequest(regClientAdminTokenRequest.toJSONString(), preReg_AdminTokenURI);
		} catch (Exception e) {
			logger.info(e);
		}
		String cookieValue = response.getCookie("Authorization");
		String auth_token = cookieValue;
		return auth_token;	
	}


	/*
	 * Generic method to get the PreRegistration Data
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response getPreRegistrationData(String PreRegistrationId) {
		testSuite = "Get_Pre_Registartion_data/Get Pre Pregistration Data of the application_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		request.put("pre_registration_id", PreRegistrationId);
		try {

			response = applnLib.getRequest(preReg_FetchRegistrationDataURI, GetHeader.getHeader(request));
		} catch (Exception e) {
			logger.info(e);
		}
		return response;

	}

	/*
	 * Generic method to Update Pre-Registration Application
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response updatePreReg(String preRegID, String updatedBy) {
		testSuite = "Pre_Registration/smokePreReg1";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					createPregRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		logger.info("Pre Reg Request::" + createPregRequest);
		JSONObject object = null;
		for (Object key : createPregRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) createPregRequest.get(key);
				object.put("preRegistrationId", preRegID);
				object.put("updatedBy", userId);
				object.put("createdBy", userId);
				object.put("updatedDateTime", "2019-01-08T17:05:48.953Z");
				createPregRequest.replace(key, object);
			}
		}
		logger.info("Request for update---------" + createPregRequest.toString());
		createPregResponse = applnLib.postRequest(createPregRequest, preReg_CreateApplnURI);
		preReg_Id = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Assert.assertTrue(preReg_Id != null);
		return createPregResponse;
	}

	/*
	 * Generic method to compare Values
	 * 
	 */
	public void compareValues(String actual, String expected) {
		try {
			Assert.assertEquals(actual, expected);
			logger.info("values are equal");
		} catch (Exception e) {
			logger.info("values are not equal");
		}
	}

	/*
	 * Generic method to Get All Documents For Pre-Registration Id
	 * 
	 */
	public Response getAllDocumentForPreId(String preId) {

		testSuite = "GetAllDocumentForPreRegId/GetAllDocumentForPreRegId_smoke";
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_FetchAllDocumentURI");
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}

			}
		}

		/*
		 * 
		 * Pass the configuration object to using method of JsonPath and pass the json
		 * string to parse method which will return the parsed JSON. Then we pass the
		 * json path of the value that needs to be updated and the new value that we
		 * need in post Data to set method, which returns the updated POST (JSON) Data.
		 *
		 */

		ObjectNode getAllDocForPreIdReq = JsonPath.using(config).parse(request.toString())
				.set("$.pre_registration_id", preId).json();
		String getAllDocForPreId = getAllDocForPreIdReq.toString();
		JSONObject getAlldocJson = null;
		try {
			getAlldocJson = (JSONObject) parser.parse(getAllDocForPreId);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			GetHeader.getHeader(getAlldocJson);
		} catch (IOException e) {

			e.printStackTrace();
		}
		try {
			response = applnLib.getRequest(preReg_FetchAllDocumentURI, GetHeader.getHeader(getAlldocJson));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Generic method to Delete All Document by Pre-RegistrationId
	 * 
	 */
	public Response deleteAllDocumentByPreId(String preId) {

		testSuite = "DeleteAllDocumentsByPreRegID/DeleteAllDocumentForPreRegId_smoke";
		// preReg_URI =
		// commonLibrary.fetch_IDRepo("preReg_DeleteAllDocumentByPreIdURI");
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		/*
		 * 
		 * Pass the configuration object to using method of JsonPath and pass the json
		 * string to parse method which will return the parsed JSON. Then we pass the
		 * json path of the value that needs to be updated and the new value that we
		 * need in post Data to set method, which returns the updated POST (JSON) Data.
		 *
		 */
		ObjectNode deleteAllDocForPreIdReq = JsonPath.using(config).parse(request.toString())
				.set("$.pre_registration_id", preId).json();
		String delDocForPreId = deleteAllDocForPreIdReq.toString();
		JSONObject delDocByPreId = null;
		try {
			delDocByPreId = (JSONObject) parser.parse(delDocForPreId);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			response = applnLib.deleteRequest(preReg_DeleteAllDocumentByPreIdURI, GetHeader.getHeader(delDocByPreId));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Generic method to Delete All Document by Document Id
	 * 
	 */

	public Response deleteAllDocumentByDocId(String documentId) {

		testSuite = "DeleteDocumentByDocId/DeleteDocumentByDocmentId_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		// preReg_URI = commonLibrary.fetch_IDRepo("deleteDocumentByDocId_URI");
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}

			}
		}

		/*
		 * 
		 * Pass the configuration object to using method of JsonPath and pass the json
		 * string to parse method which will return the parsed JSON. Then we pass the
		 * json path of the value that needs to be updated and the new value that we
		 * need in post Data to set method, which returns the updated POST (JSON) Data.
		 *
		 */

		ObjectNode deleteAllDocForDocIdReq = JsonPath.using(config).parse(request.toString())
				.set("$.documentId", documentId).json();

		String delDocForDocId = deleteAllDocForDocIdReq.toString();
		JSONObject delDocByDocIdRes = null;
		try {
			delDocByDocIdRes = (JSONObject) parser.parse(delDocForDocId);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			response = applnLib.deleteRequest(prereg_DeleteDocumentByDocIdURI, GetHeader.getHeader(delDocByDocIdRes));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}
	public Response FetchCentre(String regCenterID) {
		testSuite = "FetchAvailabilityDataOfRegCenters/FetchAvailabilityDataOfRegCenters_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;

		ObjectNode fetchAvailabilityrequest = null;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
					fetchAvailabilityrequest = JsonPath.using(config).parse(request.toJSONString())
							.set("$.registration_center_id", regCenterID).json();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		String fetchCenterReq = fetchAvailabilityrequest.toString();
		JSONObject fetchCenterReqjson = null;
		try {
			fetchCenterReqjson = (JSONObject) parser.parse(fetchCenterReq);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			
			String preReg_FetchCenterIDURI = commonLibrary.fetch_IDRepo().get("preReg_FetchCenterIDURI");
			response = applnLib.getRequest(preReg_FetchCenterIDURI, GetHeader.getHeader(fetchCenterReqjson));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Generic method to Copy uploaded document from One Pre-Registration Id to
	 * another Pre-Registration Id
	 * 
	 */

	public Response copyUploadedDocuments(String sourcePreId, String destPreId) {
		testSuite = "CopyUploadedDocument/CopyUploadedDocument_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_CopyDocumentsURI");
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {

					e.printStackTrace();
				}

			}
		}
		/*
		 * 
		 * Pass the configuration object to using method of JsonPath and pass the json
		 * string to parse method which will return the parsed JSON. Then we pass the
		 * json path of the value that needs to be updated and the new value that we
		 * need in post Data to set method, which returns the updated POST (JSON) Data.
		 *
		 */
		ObjectNode copyDocForSrcPreId = JsonPath.using(config).parse(request.toString())
				.set("$.sourcePrId", sourcePreId).json();
		ObjectNode copyDocForDestPreId = JsonPath.using(config).parse(copyDocForSrcPreId.toString())
				.set("$.destinationPreId", destPreId).json();
		String copyDoc = copyDocForDestPreId.toString();
		JSONObject copyDocRes = null;
		try {
			copyDocRes = (JSONObject) parser.parse(copyDoc);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			GetHeader.getHeader(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			response = applnLib.postModifiedGETRequest(preReg_CopyDocumentsURI, GetHeader.getHeader(copyDocRes));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * 
	 * Generic method For Fetching the Registration center details
	 * 
	 */

	public Response FetchCentre() {
		testSuite = "FetchAvailabilityDataOfRegCenters/FetchAvailabilityDataOfRegCenters_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_FetchCenterIDURI");
		ObjectNode fetchAvailabilityrequest = null;
		String regCenterId = randomRegistrationCenterId();

		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
					fetchAvailabilityrequest = JsonPath.using(config).parse(request.toJSONString())
							.set("$.registration_center_id", regCenterId).json();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		String fetchCenterReq = fetchAvailabilityrequest.toString();
		JSONObject fetchCenterReqjson = null;
		try {
			fetchCenterReqjson = (JSONObject) parser.parse(fetchCenterReq);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			response = applnLib.getRequest(preReg_FetchCenterIDURI, GetHeader.getHeader(fetchCenterReqjson));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public Response fetchCentreWithCerterId(String regCenterId) {
		testSuite = "FetchAvailabilityDataOfRegCenters/FetchAvailabilityDataOfRegCenters_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		ObjectNode fetchAvailabilityrequest = null;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
					fetchAvailabilityrequest = JsonPath.using(config).parse(request.toJSONString())
							.set("$.registration_center_id", regCenterId).json();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		String fetchCenterReq = fetchAvailabilityrequest.toString();
		JSONObject fetchCenterReqjson = null;
		try {
			fetchCenterReqjson = (JSONObject) parser.parse(fetchCenterReq);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			response = applnLib.getRequest(preReg_FetchCenterIDURI, GetHeader.getHeader(fetchCenterReqjson));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Generic method to Book An Appointment
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Response BookAppointment(Response DocumentUploadresponse, Response FetchCentreResponse, String preID) {
		List<String> appointmentDetails = new ArrayList<>();

		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_BookingAppointmentURI");
		testSuite = "BookingAppointment/BookingAppointment_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		JSONObject object = null;
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (Object key : request.keySet()) {
			if (key.toString().toLowerCase().equals("request")) {
				object = new JSONObject();
				JSONObject resp = null;

				try {
					resp = (JSONObject) new JSONParser().parse(DocumentUploadresponse.asString());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				JSONArray data = (JSONArray) resp.get("response");
				JSONObject json = (JSONObject) data.get(0);
				json.get("preRegistrationId");
				object.put("preRegistrationId", preID);
				JSONObject innerData = new JSONObject();

				appointmentDetails = getAppointmentDetails(FetchCentreResponse);

				regCenterId = appointmentDetails.get(0);
				appDate = appointmentDetails.get(1);
				timeSlotFrom = appointmentDetails.get(2);
				timeSlotTo = appointmentDetails.get(3);

				innerData.put("registration_center_id", regCenterId);
				innerData.put("appointment_date", appDate);
				innerData.put("time_slot_from", timeSlotFrom);
				innerData.put("time_slot_to", timeSlotTo);
				object.put("newBookingDetails", innerData);
				JSONArray objArr = new JSONArray();
				objArr.add(object);
				request.replace(key, objArr);

			}
		}
		response = applnLib.postRequest(request, preReg_BookingAppointmentURI);
		return response;
	}

	/*
	 * Generic method to Book An Appointment with invalid date
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Response bookAppointmentInvalidDate(Response DocumentUploadresponse, Response FetchCentreResponse,
			String preID) {
		List<String> appointmentDetails = new ArrayList<>();

		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_BookingAppointmentURI");
		testSuite = "BookingAppointment/BookingAppointment_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		JSONObject object = null;
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (Object key : request.keySet()) {
			if (key.toString().toLowerCase().equals("request")) {
				object = new JSONObject();
				JSONObject resp = null;

				try {
					resp = (JSONObject) new JSONParser().parse(DocumentUploadresponse.asString());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				JSONArray data = (JSONArray) resp.get("response");
				JSONObject json = (JSONObject) data.get(0);
				json.get("preRegistrationId");
				object.put("preRegistrationId", preID);
				JSONObject innerData = new JSONObject();

				appointmentDetails = getAppointmentDetails(FetchCentreResponse);
				try {
					regCenterId = appointmentDetails.get(0);
					timeSlotFrom = appointmentDetails.get(2);
					timeSlotTo = appointmentDetails.get(3);
				} catch (IndexOutOfBoundsException e) {
					// TODO Auto-generated catch block
					logger.info("Center not available");
					Assert.fail("Centers unavailable");
				}

				innerData.put("registration_center_id", regCenterId);
				innerData.put("appointment_date", "2019-27-27");
				innerData.put("time_slot_from", timeSlotFrom);
				innerData.put("time_slot_to", timeSlotTo);
				object.put("newBookingDetails", innerData);
				JSONArray objArr = new JSONArray();
				objArr.add(object);
				request.replace(key, objArr);

			}
		}
		response = applnLib.postRequest(request, preReg_BookingAppointmentURI);
		return response;
	}

	/*
	 * Generic method to Fetch Appointment Details
	 * 
	 */

	public Response FetchAppointmentDetails(String preID) {
		testSuite = "FetchAppointmentDetails/FetchAppointmentDetails_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_FecthAppointmentDetailsURI");

		// logger.info("Fetch app det::" + preReg_URI);

		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}

			}
		}
		/*
		 * 
		 * Pass the configuration object to using method of JsonPath and pass the json
		 * string to parse method which will return the parsed JSON. Then we pass the
		 * json path of the value that needs to be updated and the new value that we
		 * need in post Data to set method, which returns the updated POST (JSON) Data.
		 *
		 */
		ObjectNode fetchAppDetails = JsonPath.using(config).parse(request.toString())
				.set("$.pre_registration_id", preID).json();
		String fetchAppDetStr = fetchAppDetails.toString();
		JSONObject fetchAppjson = null;
		try {
			fetchAppjson = (JSONObject) parser.parse(fetchAppDetStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			GetHeader.getHeader(fetchAppjson);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			response = applnLib.getRequest(preReg_FecthAppointmentDetailsURI, GetHeader.getHeader(fetchAppjson));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Generic method to Cancel Booking Appointment Details
	 * 
	 */

	public Response CancelBookingAppointment(Response FetchAppDet, String preID) {
		testSuite = "CancelAnBookedAppointment/CancelAnBookedAppointment_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_CancelAppointmentURI");
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		/*
		 * 
		 * Pass the configuration object to using method of JsonPath and pass the json
		 * string to parse method which will return the parsed JSON. Then we pass the
		 * json path of the value that needs to be updated and the new value that we
		 * need in post Data to set method, which returns the updated POST (JSON) Data.
		 *
		 */
		ObjectNode cancelAppPreRegId = JsonPath.using(config).parse(request.toJSONString())
				.set("$.request.pre_registration_id", preID).json();
		ObjectNode cancelAppRegCenterId = JsonPath.using(config).parse(cancelAppPreRegId.toString())
				.set("$.request.registration_center_id ",
						FetchAppDet.jsonPath().get("response.registration_center_id").toString())
				.json();
		ObjectNode cancelAppDate = JsonPath.using(config).parse(cancelAppRegCenterId.toString())
				.set("$.request.appointment_date", FetchAppDet.jsonPath().get("response.appointment_date").toString())
				.json();
		ObjectNode cancelAppTimeSlotFrom = JsonPath.using(config).parse(cancelAppDate.toString())
				.set("$.request.time_slot_from", FetchAppDet.jsonPath().get("response.time_slot_from").toString())
				.json();
		ObjectNode cancelAppRequest = JsonPath.using(config).parse(cancelAppTimeSlotFrom.toString())
				.set("$.request.time_slot_to", FetchAppDet.jsonPath().get("response.time_slot_to").toString()).json();

		String cancelAppDetStr = cancelAppRequest.toString();
		JSONObject cancelAppjson = null;
		try {
			cancelAppjson = (JSONObject) parser.parse(cancelAppDetStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		response = applnLib.putRequest_WithBody(preReg_CancelAppointmentURI, cancelAppjson);
		return response;
	}

	/*
	 * Generic method to Cancel Booking Appointment Details
	 * 
	 */

	public Response ReBookAnAppointment(String preID, Response FetchAppDet, Response FetchCentreResponse)
			throws FileNotFoundException, IOException, ParseException {
		testSuite = "ReBookAnAppointment/ReBookAnAppointment_smoke";

		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_ExpiredURI");
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		JSONObject object = null;
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}

			}
		}
		/*
		 * 
		 * Pass the configuration object to using method of JsonPath and pass the json
		 * string to parse method which will return the parsed JSON. Then we pass the
		 * json path of the value that needs to be updated and the new value that we
		 * need in post Data to set method, which returns the updated POST (JSON) Data.
		 *
		 */
		ObjectNode rebookPreRegId = JsonPath.using(config).parse(request.toJSONString())
				.set("$.request[0].preRegistrationId", preID).json();
		ObjectNode rebookAppointmetRegCenterDet = JsonPath.using(config).parse(rebookPreRegId.toString())
				.set("$.request[0].oldBookingDetails.registration_center_id",
						FetchAppDet.jsonPath().get("response.registration_center_id").toString())
				.json();
		ObjectNode rebookAppointmentAppDate = JsonPath.using(config).parse(rebookAppointmetRegCenterDet.toString())
				.set("$.request[0].oldBookingDetails.appointment_date",
						FetchAppDet.jsonPath().get("response.appointment_date").toString())
				.json();
		ObjectNode rebookTimeSlotFrom = JsonPath.using(config).parse(rebookAppointmentAppDate.toString())
				.set("$.request[0].oldBookingDetails.time_slot_from",
						FetchAppDet.jsonPath().get("response.time_slot_from").toString().toString())
				.json();
		ObjectNode rebookTimeSlotTo = JsonPath.using(config).parse(rebookTimeSlotFrom.toString())
				.set("$.request[0].oldBookingDetails.time_slot_to",
						FetchAppDet.jsonPath().get("response.time_slot_to").toString().toString())
				.json();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 5);
		String date = dateFormat.format(c1.getTime());

		List<String> details = reBookGetAppointmentDetails(FetchCentreResponse, date);
		String regCenterId = details.get(0);
		String appDate = details.get(1);
		String timeSlotFrom = details.get(2);
		String timeSlotTo = details.get(3);

		ObjectNode rebookNewAppRegCenterId = JsonPath.using(config).parse(rebookTimeSlotTo.toString())
				.set("$.request[0].newBookingDetails.registration_center_id", regCenterId).json();
		ObjectNode rebookNewAppointmentAppDate = JsonPath.using(config).parse(rebookNewAppRegCenterId.toString())
				.set("$.request[0].newBookingDetails.appointment_date", appDate).json();
		ObjectNode rebookNewAppointmentTimeSlotFrom = JsonPath.using(config)
				.parse(rebookNewAppointmentAppDate.toString())
				.set("$.request[0].newBookingDetails.time_slot_from", timeSlotFrom).json();
		ObjectNode rebookNewAppointmentTimeSlotTo = JsonPath.using(config)
				.parse(rebookNewAppointmentTimeSlotFrom.toString())
				.set("$.request[0].newBookingDetails.time_slot_to", timeSlotTo).json();

		String rebookApp = rebookNewAppointmentTimeSlotTo.toString();
		JSONObject rebookAppjson = null;
		try {
			rebookAppjson = (JSONObject) parser.parse(rebookApp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		response = applnLib.postRequest(rebookAppjson, preReg_BookingAppointmentURI);
		return response;
	}

	/**
	 * Its a batch job service which changed the status of expired application into
	 * Expired
	 * 
	 * @author Ashish
	 * @return
	 */
	public Response expiredStatus() {
		try {

			response = applnLib.adminputRequest_WithoutBody(preReg_ExpiredURI);
		} catch (Exception e) {
			logger.info(e);
		}

		return response;
	}
	
	/**
	 * Its a batch job service which changed the status of consumed application into
	 * Consumed
	 * 
	 * @author Ashish
	 * @return
	 */
	public Response consumedStatus() {
		try {

			response = applnLib.adminputRequest_WithoutBody(preReg_ConsumedURI);
		} catch (Exception e) {
			logger.info(e);
		}

		return response;
	}

	
	/*
	 * Generic method to Retrieve All PreId By Registration Center Id
	 * 
	 */
	
	
	public Response retriveAllPreIdByRegId(String regCenterId,String preId) throws FileNotFoundException, IOException, ParseException {
		testSuite = "RetrivePreIdByRegCenterId/RetrivePreIdByRegCenterId_smoke";
		
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		JSONObject object = null;
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

			}
		}
		
		ObjectNode setRegCenterId = JsonPath.using(config).parse(request.toJSONString())
				.set("$.request.registartion_center_id", regCenterId).json();
		
		ObjectNode setPreRegId = JsonPath.using(config).parse(setRegCenterId.toString())
				.set("$.request.pre_registration_ids[0]",
						preId)
				.json();
		
		String retPreIdDetStr = setPreRegId.toString();
		
		JSONObject retriveAllPreIdByRegIdjson = (JSONObject) parser.parse(retPreIdDetStr);
		
		response = applnLib.postRequest(retriveAllPreIdByRegIdjson, preReg_FetchBookedPreIdByRegIdURI);
		return response;
	}
	
	
	
	
	/*public Response retriveAllPreIdByRegId() throws FileNotFoundException, IOException, ParseException {
		testSuite = "RetrivePreIdByRegCenterId/RetrivePreIdByRegCenterId_smoke";
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_FetchBookedPreIdByRegId_URI
		// ");
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		JSONObject object = null;
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

			}
		}
		JSONObject retriveAllPreIdByRegIdjson = (JSONObject) parser.parse(request.toString());
		response = applnLib.postRequest(retriveAllPreIdByRegIdjson, preReg_FetchBookedPreIdByRegIdURI);
		return response;
	}*/

	/*
	 * Generic function to fetch the random registration centerId
	 * 
	 */
	public String randomRegistrationCenterId() {
		Random rand = new Random();

		/*
		 * List<String> givenList =
		 * Lists.newArrayList("10002","10020","10016","10014","10010","10006",
		 * "10024","10019","10111","10017","10023","10005","10026","10009","10013",
		 * "10015","10021","10004","10011","10008","10018","10022","10001","10012",
		 * "10025","54321","10007","10003", "10027");
		 */

		/*List<String> givenList = Lists.newArrayList("10002", "10020", "10027", "10014", "10010", "10016", "10006",
				"10024", "10111", "10019", "10017", "10023", "10045", "10037", "10042", "10005", "10029", "10026",
				"10038", "10009",  "10030", "10013", "10015", "10021", "10004", "10035", "10034",
				"10011", "10008", "10032",  "10018", "10001", "10022", "10012", "10033", "10025", "10007",
				"10003");*/
		List<String> givenList = Lists.newArrayList("10001","10002","10003","10004","10005","10006","10007","10008","10009","10010","10011","10012","10013","10014","10015");
		String s = null;

		int numberOfElements = givenList.size();

		for (int i = 0; i < numberOfElements; i++) {
			int randomIndex = rand.nextInt(givenList.size());
			s = givenList.remove(randomIndex);
		}
		return s;

	}

	/*
	 * Generic method to Fetch the Appointment Details
	 * 
	 */
	public List<String> getAppointmentDetails(Response fetchCenterResponse) {

		List<String> appointmentDetails = new ArrayList<>();

		int countCenterDetails = fetchCenterResponse.jsonPath().getList("response.centerDetails").size();
		for (int i = 0; i < countCenterDetails; i++) {
			try {
				fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].timeSlots[0].fromTime")
						.toString();
			} catch (NullPointerException e) {
				continue;
			}
			appointmentDetails.add(fetchCenterResponse.jsonPath().get("response.regCenterId").toString());
			appointmentDetails
					.add(fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].date").toString());
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[" + i + "].timeSlots[0].fromTime").toString());
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[" + i + "].timeSlots[0].toTime").toString());
			break;
		}
		return appointmentDetails;
	}

	/*
	 * Generic method for multiple Upload Document
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Response multipleDocumentUpload(Response responseCreate, String folderPath, String documentName)
			throws FileNotFoundException, IOException, ParseException {

		testSuite = folderPath;
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_DocumentUploadURI");
		String configPath = "src/test/resources/" + folder + "/" + testSuite;

		File file = new File(configPath + documentName);
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
			}
		}

		JSONObject object = null;
		for (Object key : request.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) request.get(key);
				object.put("pre_registartion_id",
						responseCreate.jsonPath().get("response[0].preRegistrationId").toString());
				request.replace(key, object);
			}

		}

		response = applnLib.putFileAndJson(preReg_DocumentUploadURI, request, file);

		return response;
	}
	
	
	/*
	 * Generic method to Upload Document
	 * 
	 */
	
	
	@SuppressWarnings("unchecked")
	public Response TriggerNotification() {
		testSuite = "TriggerNotification/TriggerNotification_smoke";
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File file = new File(configPath + "/AadhaarCard_POA.pdf");

		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		JSONObject object = null;
		for (Object key : request.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) request.get(key);
				//object.put("pre_registartion_id",responseCreate.jsonPath().get("response[0].preRegistrationId").toString());
				//request.replace(key, object);
				//object.remove(langCodeKey);
			}
		}
		
		
		 String value = (String)request.get(langCodeKey);
		
		request.remove(langCodeKey);
		
		response =applnLib.putFileAndJsonParam(preReg_NotifyURI, request, file,langCodeKey,value);

		return response;
	}
	
	
	/*
	 * Generic method for dynamically change the request values in json file 
	 * 
	 */
	
	
	public ObjectNode dynamicJsonRequest(String jsonPathTraverse,String jsonSetVal,String readFilePath,String writeFilePath) {
		
		String yourActualJSONString = null;
		ObjectNode newJson = null;
		try {
			yourActualJSONString = new String(Files.readAllBytes(Paths.get(readFilePath)), StandardCharsets.UTF_8);
		    newJson=JsonPath.using(config).parse(yourActualJSONString).set(jsonPathTraverse,jsonSetVal).json();
			FileWriter writer = new FileWriter(new File(writeFilePath));
			writer.append(newJson.toString());
			writer.flush();
			writer.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return newJson;
		
	}
	
	
	
	
	

	/*
	 * Generic method to fetch the dynamic request json
	 * 
	 */

	public JSONObject requestJson(String filepath) {

		String configPath = "src/test/resources/" + folder + "/" + filepath;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();

		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return request;

	}
	public JSONObject createRequest(String testSuite)
	{
		JSONObject createPregRequest = null;
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		/**
		 * Reading request body from configpath
		 */
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					createPregRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				
			}
		}
		String createdBy = new Integer(createdBy()).toString();
		JSONObject object = null;
		for (Object key : createPregRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) createPregRequest.get(key);
				object.put("createdBy", userId);
				createPregRequest.replace(key, object);
			}
		}
		
		
		
		return createPregRequest;
	}
	public JSONObject otpRequest(String testSuite)
	{
		JSONObject otpRequest = null;
		testSuite = "generateOTP/generateOTP_smoke";
		/**
		 * Reading request body from configpath
		 */
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					otpRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				
			}
		}
		double d = (Math.random()*1000000000);
		long l= (long)d;
		userId = "9"+l ;
		JSONObject object = null;
		for (Object key : otpRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) otpRequest.get(key);
				object.put("userId", userId);
				otpRequest.replace(key, object);
			}
		}	
		return otpRequest;
	}
	public JSONObject getOtpRequest(String testSuite)
	{
		JSONObject otpRequest = null;
		/**
		 * Reading request body from configpath
		 */
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				try {
					otpRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				
			}
		}
		return otpRequest;
	}

	

	/*
	 * Generic method for Discard Booking
	 * 
	 */
	
	public Response discardBooking(String preId)  {

		testSuite = "DiscardBooking/DiscardBooking_smoke";
		//preReg_URI = commonLibrary.fetch_IDRepo("preReg_DeleteAllDocumentByPreIdURI");
		String configPath = "src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		/*
		 * 
		 * Pass the configuration object to using method of JsonPath and pass the json
		 * string to parse method which will return the parsed JSON. Then we pass the
		 * json path of the value that needs to be updated and the new value that we
		 * need in post Data to set method, which returns the updated POST (JSON) Data.
		 *
		 */
		ObjectNode discardBooking = JsonPath.using(config).parse(request.toString())
				.set("$.pre_registration_id", preId).json();
		String delBookPreId = discardBooking.toString();
		JSONObject delBookByPreId = null;
		try {
			delBookByPreId = (JSONObject) parser.parse(delBookPreId);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			response = applnLib.deleteRequest(preReg_DiscardBookingURI, GetHeader.getHeader(delBookByPreId));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	
	
	
	/*
	 * Generic method for sync master data
	 * 
	 */
	public Response syncMasterData() {
		Response syncMasterDataRes = null;
		try {

			 syncMasterDataRes = applnLib.get_RequestWithoutBody(preReg_SyncMasterDataURI);
		} catch (Exception e) {
			logger.info(e);
		}

		return syncMasterDataRes;
	}
	

	
	
	
	
	@BeforeClass
	public void PreRegistrationResourceIntialize() {
		preReg_CreateApplnURI = commonLibrary.fetch_IDRepo().get("preReg_CreateApplnURI");
		preReg_DocumentUploadURI = commonLibrary.fetch_IDRepo().get("preReg_DocumentUploadURI");
		preReg_FetchCenterIDURI = commonLibrary.fetch_IDRepo().get("preReg_FetchCenterIDURI");
		preReg_BookingAppointmentURI = commonLibrary.fetch_IDRepo().get("preReg_BookingAppointmentURI");
		preReg_DataSyncnURI = commonLibrary.fetch_IDRepo().get("preReg_DataSyncnURI");
		preReg_FetchRegistrationDataURI = commonLibrary.fetch_IDRepo().get("preReg_FetchRegistrationDataURI");
		preReg_FetchCenterIDURI = commonLibrary.fetch_IDRepo().get("preReg_FetchCenterIDURI");
		preReg_FecthAppointmentDetailsURI = commonLibrary.fetch_IDRepo().get("preReg_FecthAppointmentDetailsURI");
		preReg_FetchAllDocumentURI = commonLibrary.fetch_IDRepo().get("preReg_FetchAllDocumentURI");
		prereg_DeleteDocumentByDocIdURI = commonLibrary.fetch_IDRepo().get("prereg_DeleteDocumentByDocIdURI");
		preReg_DeleteAllDocumentByPreIdURI = commonLibrary.fetch_IDRepo().get("preReg_DeleteAllDocumentByPreIdURI");
		preReg_CopyDocumentsURI = commonLibrary.fetch_IDRepo().get("preReg_CopyDocumentsURI");
		preReg_FetchBookedPreIdByRegIdURI = commonLibrary.fetch_IDRepo().get("preReg_FetchBookedPreIdByRegIdURI");
		preReg_FetchStatusOfApplicationURI = commonLibrary.fetch_IDRepo().get("preReg_FetchStatusOfApplicationURI");
		preReg_DiscardApplnURI = commonLibrary.fetch_IDRepo().get("preReg_DiscardApplnURI");
		preReg_UpdateStatusAppURI = commonLibrary.fetch_IDRepo().get("preReg_UpdateStatusAppURI");
		preReg_CancelAppointmentURI = commonLibrary.fetch_IDRepo().get("preReg_CancelAppointmentURI");
		preReg_ExpiredURI = commonLibrary.fetch_IDRepo().get("preReg_ExpiredURI");
		preReg_ConsumedURI = commonLibrary.fetch_IDRepo().get("preReg_ConsumedURI");
		preReg_ReverseDataSyncURI = commonLibrary.fetch_IDRepo().get("preReg_ReverseDataSyncURI");
		preReg_FetchAllApplicationCreatedByUserURI = commonLibrary.fetch_IDRepo().get("preReg_FetchAllApplicationCreatedByUserURI");
		 preReg_DiscardBookingURI=commonLibrary.fetch_IDRepo().get("preReg_DiscardBookingURI");
		 preReg_SyncMasterDataURI=commonLibrary.fetch_IDRepo().get("preReg_SyncMasterDataURI");
		 preReg_NotifyURI=commonLibrary.fetch_IDRepo().get("preReg_NotifyURI");
		 langCodeKey=commonLibrary.fetch_IDRepo().get("langCode.key");
		 otpSend_URI=commonLibrary.fetch_IDRepo().get("otpSend_URI");
		 validateOTP_URI=commonLibrary.fetch_IDRepo().get("validateOTP_URI");
		 preReg_AdminTokenURI=commonLibrary.fetch_IDRepo().get("preReg_AdminTokenURI");
		 preReg_translitrationRequestURI=commonLibrary.fetch_IDRepo().get("preReg_translitrationRequestURI");
		
	}

}