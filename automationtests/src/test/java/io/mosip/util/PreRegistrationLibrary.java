package io.mosip.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import io.mosip.dbaccess.PreregDB;
import io.mosip.dbentity.AccessToken;
import io.mosip.dbentity.OtpEntity;
import io.mosip.dbentity.PreRegEntity;
import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.preregistration.util.PreRegistrationUtil;
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
	public static String userId = "";
	static String otp = "";
	static Response createPregResponse;
	static JSONObject createPregRequest;
	static Response response;
	static JSONObject request;
	static JSONObject request1;
	static String preReg_Id = "";
	JSONParser parser = new JSONParser();
	static String preReg_MultipleBookAppURI;
	static ApplicationLibrary applnLib = new ApplicationLibrary();
	private static Logger logger = Logger.getLogger(BaseTestCase.class);
	private static CommonLibrary commonLibrary = new CommonLibrary();

	private static String preReg_CreateApplnURI;
	PreregistrationDAO dao = new PreregistrationDAO();
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
	private static String invalidateToken_URI;
	private static String preReg_GetDocByDocId;
	private static String preReg_CancelAppointmenturi;
	private static String preReg_RetriveBookedPreIdsByRegId;
	private static String preReg_GetPreRegistrationConfigData;
	private static String preReg_BookingAppointmenturi;
	private static String uiConfigParams;
	private static String preReg_syncAvailability;
	private static String preReg_FecthAppointmentDetailsuri;
	private static String preReg_GetDocByPreId;
	private static String QRCodeFilePath;
	private static String qrCode_URI;
	PreRegistrationUtil preRegUtil=new PreRegistrationUtil();
	/*
	 * We configure the jsonProvider using Configuration builder.
	 */
	Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider()).build();

	/*
	 * Generic method to Create Pre-Registration Application
	 * 
	 */
	public Response CreatePreReg() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		request = getRequest(testSuite);
		request.put("requesttime", getCurrentDate());
		createPregResponse = applnLib.postRequest(request, preReg_CreateApplnURI);
		return createPregResponse;
	}

	public JSONObject getRequest(String testSuite) {
		JSONObject request = null;
		/**
		 * Reading request body from configpath
		 */
		String configPath = System.getProperty("user.dir") + "/src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		FileReader fileReader = null;
		try {
			for (File f : listOfFiles) {
				if (f.getName().contains("request")) {

					try {
						fileReader = new FileReader(f.getPath());
						request = (JSONObject) new JSONParser().parse(fileReader);
					} catch (Exception e) {
						logger.error(e.getMessage());
					} finally {
						try {
							fileReader.close();
						} catch (IOException e) {
							logger.info(e.getMessage());
						}
					}

				}
			}
			
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "File was not present at given path  "+configPath + e.getClass());

		}
		
		return request;
	}

	/**
	 * Request body for translitration service
	 * 
	 * @param testSuite
	 * @param from_field_lang
	 * @param from_field_value
	 * @param to_field_lang
	 * @return
	 */
	public JSONObject translitrationRequest(String testSuite, String from_field_lang, String from_field_value,
			String to_field_lang) {
		JSONObject createPregRequest = null;
		JSONObject translitrationRequest = null;
		/**
		 * Reading request body from configpath
		 */
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		FileReader fileReader = null;
		try {
			for (File f : listOfFiles) {
				if (f.getName().contains("request")) {

					try {
						fileReader = new FileReader(f.getPath());
						translitrationRequest = (JSONObject) new JSONParser().parse(fileReader);
					} catch (Exception e) {
						logger.error(e.getMessage());
					} finally {
						try {
							fileReader.close();
						} catch (IOException e) {
							logger.info(e.getMessage());
						}
					}

				}
			}
			
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "File was not present at given path  "+configPath + e.getClass());
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
		translitrationRequest.put("requesttime", getCurrentDate());
		return translitrationRequest;
	}

	/**
	 * method for translitration service
	 * 
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
	 * 
	 * @return
	 */
	public  Response generateOTP(JSONObject request) {
		response = applnLib.postRequest(request, otpSend_URI);
		return response;
	}
	public Response pagination(String index)
	{
		HashMap<String, String> query=new HashMap<>();
		query.put("pageIndex", index);
		Response paginationResponse = applnLib.getRequestAsQueryParam(preReg_CreateApplnURI, query);
		return paginationResponse;
	}

	public String getToken() {
		testSuite = "generateOTP/generateOTP_smoke";
		request = otpRequest(testSuite);
		Response generateOTPResponse = generateOTP(request);
		try {
			otp = dao.getOTP(userId).get(0);
		} catch (IndexOutOfBoundsException e) {
			Assert.assertTrue(false, "send otp failed");
		}
		testSuite = "validateOTP/validateOTP_smoke";
		request = validateOTPRequest(testSuite);
		Response validateOTPRes = validateOTP(request);
		String cookieValue = validateOTPRes.getCookie("Authorization");
		return cookieValue;
	}

	/**
	 * Fetching status of consumed application
	 * 
	 * @param PreID-PRID
	 *            of the consumed application
	 * @return
	 */
	public String getConsumedStatus(String PreID) {
		return dao.getConsumedStatus(PreID);
	}

	/**
	 * Get Document Id for Consumed Application
	 * 
	 * @param PreID
	 * @return
	 */
	public String getDocumentIdOfConsumedApplication(String PreID) {
		return dao.getDocumentIdOfConsumedApplication(PreID);
	}

	public String getRegCenterIdOfConsumedApplication(String PreID) {
		return dao.getRegCenterIdOfConsumedApplication(PreID);
	}

	/**
	 * VALIDATING OTP
	 * 
	 * @param request
	 * @return
	 */
	public static Response validateOTP(JSONObject request) {
		response = applnLib.postRequest(request, validateOTP_URI);
		return response;
	}

	/**
	 * ValidateRequest
	 * 
	 * @return
	 */
	public JSONObject validateOTPRequest(String testSuite) {
		JSONObject otpRequest = null;
		/**
		 * Reading request body from configpath
		 */
		otpRequest = getRequest(testSuite);
		JSONObject object = null;
		for (Object key : otpRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) otpRequest.get(key);
				object.put("userId", userId);
				object.put("otp", otp);
				otpRequest.replace(key, object);
			}
		}
		otpRequest.put("requesttime", getCurrentDate());
		return otpRequest;
	}

	public JSONObject validateOTPRequest(String testSuite, String userID, String OTP) {
		JSONObject otpRequest = null;
		/**
		 * Reading request body from configpath
		 */
		otpRequest = getRequest(testSuite);
		JSONObject object = null;
		for (Object key : otpRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) otpRequest.get(key);
				object.put("userId", userID);
				object.put("otp", OTP);
				otpRequest.replace(key, object);
			}
		}
		otpRequest.put("requesttime", getCurrentDate());
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
		request = getRequest(testSuite);
		request.put("preRegistrationId", PreRegistrationId);
		try {
			return applnLib.deleteRequestWithParm(preReg_DiscardApplnURI, request);

		} catch (Exception e) {
			logger.info(e);
		}
		return null;
	}

	public boolean fetchDocs1(Response response, String folderName) {
		String data = response.jsonPath().get("response.zip-bytes").toString();
		String folder = response.jsonPath().get("response.zip-filename").toString();
		String folderPath = "src/test/resources/" + "preReg" + "/" + folderName;
		File f = new File(folderPath + "/" + folder);
		f.mkdirs();
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
		ZipEntry entry = null;
		try {
			while ((entry = zipStream.getNextEntry()) != null) {

				String entryName = entry.getName();
				System.out.println("ASHISH" + entryName);
				String path = folderPath + "/" + folder + "/" + entryName;
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

	/**
	 * Method for reading config property from config server
	 * 
	 * @param url-Url
	 *            of config server
	 * @param configParameter-keys
	 *            which you want to read from config server
	 * @return
	 */
	public HashMap<String, String> readConfigProperty(String url, String configParameter) {
		List<String> reqParams = new ArrayList<>();
		Map<String, String> configParamMap = new HashMap<>();
		uiConfigParams = commonLibrary.fetch_IDRepo().get(configParameter);
		String[] uiParams = uiConfigParams.split(",");
		for (int i = 0; i < uiParams.length; i++) {
			reqParams.add(uiParams[i]);
		}
		RestTemplate restTemplate = new RestTemplate();

		String s = restTemplate.getForObject(url, String.class);
		final Properties p = new Properties();
		try {
			p.load(new StringReader(s));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (Entry<Object, Object> e : p.entrySet()) {
			if (reqParams.contains(String.valueOf(e.getKey()))) {
				configParamMap.put(String.valueOf(e.getKey()), e.getValue().toString());
			}

		}
		return (HashMap<String, String>) configParamMap;
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
		String folderPath = "src/test/resources/" + "preReg" + "/" + folderName;
		File f = new File(folderPath + "/" + folder);
		f.mkdirs();
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
		ZipEntry entry = null;
		try {
			while ((entry = zipStream.getNextEntry()) != null) {

				String entryName = entry.getName();
				logger.info("ASHISH" + entryName);
				String path = folderPath + "/" + folder + "/" + entryName;
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
		}
		return true;
	}

	/*
	 * Generic method to Fetch All Preregistration Created By User
	 * 
	 */

	public Response fetchAllPreRegistrationCreatedByUser() {
		try {
			response = applnLib.getRequestWithoutParm(preReg_FetchAllApplicationCreatedByUserURI);
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	public Response getPreRegistrationConfigData() {
		try {
			response = applnLib.getRequestWithoutParm(preReg_GetPreRegistrationConfigData);
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
		testSuite = "ReverseDataSync//ReverseDataSync_smoke";
		/**
		 * Reading request body from configpath
		 */
		reverseDataSyncRequest = getRequest(testSuite);
		/**
		 * Adding preRegistrationIds in request
		 */
		for (Object key : reverseDataSyncRequest.keySet()) {
			try {
				reverseDataSyncRequest.get(key);
				JSONObject innerKey = (JSONObject) reverseDataSyncRequest.get(key);
				innerKey.put("preRegistrationIds", preRegistrationIds);
			} catch (ClassCastException e) {
				continue;
			}
			reverseDataSyncRequest.put("requesttime", getCurrentDate());
		}

		response = applnLib.dataSyncPostRequest(reverseDataSyncRequest.toJSONString(), preReg_ReverseDataSyncURI);

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
		request = getRequest(testSuite);
		request.put("preRegistrationId", preRegistrationId);
		try {
			response = applnLib.getRequestDataSync(preReg_DataSyncnURI, request);
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	/**
	 * Update demographic details method
	 * 
	 * @param body
	 * @param pre_registration_id
	 */

	public Response updateDemographicDetails(JSONObject body, String pre_registration_id) {
		testSuite = "Retrive_PreRegistration/Retrive Pre registration data of an applicant after booking an appointment_smoke";
		request = getRequest(testSuite);
		request.put("preRegistrationId", pre_registration_id);
		response = applnLib.putRequestWithParameter(preReg_UpdateStatusAppURI, request, body);
		return response;
	}

	public JSONObject objectToJSONObject(Object object) {
		Object json = null;
		JSONObject jsonObject = null;
		try {
			json = new JSONTokener(object.toString()).nextValue();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (json instanceof Object) {
			jsonObject = (JSONObject) json;
		}
		return jsonObject;
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
		JSONObject object = null;
		testSuite = "BookingAppointment\\BookingAppointment_smoke";
		request = getRequest(testSuite);
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
				JSONObject innerData = new JSONObject();

				appointmentDetails = getExpiredAppointmentDetails(FetchCentreResponse);

				regCenterId = appointmentDetails.get(0);
				appDate = appointmentDetails.get(1);
				timeSlotFrom = appointmentDetails.get(2);
				timeSlotTo = appointmentDetails.get(3);

				object.put("registration_center_id", regCenterId);
				object.put("appointment_date", appDate);
				object.put("time_slot_from", timeSlotFrom);
				object.put("time_slot_to", timeSlotTo);
				// object.put("preRegistrationId", preID);
				// object.put("newBookingDetails", innerData);
				JSONArray objArr = new JSONArray();
				objArr.add(object);
				request.replace(key, objArr);
				request.put("requesttime", getCurrentDate());

			}
		}
		testSuite = "Discard_Individual/Discard Individual Applicant By using Pre Registration ID_smoke";
		JSONObject parm = getRequest(testSuite);
		parm.put("preRegistrationId", preID);
		response = applnLib.postRequestWithParm(request, preReg_BookingAppointmenturi, parm);
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

		fetchCenterResponse.jsonPath().get("response.centerDetails[1].timeSlots[16].fromTime");
		appointmentDetails.add(fetchCenterResponse.jsonPath().get("response.regCenterId").toString());
		appointmentDetails.add(date);
		appointmentDetails
				.add(fetchCenterResponse.jsonPath().get("response.centerDetails[1].timeSlots[16].fromTime").toString());
		appointmentDetails
				.add(fetchCenterResponse.jsonPath().get("response.centerDetails[1].timeSlots[16].toTime").toString());
		return appointmentDetails;
	}

	/*
	 * Generic method to get the PreRegistration Status
	 * 
	 */
	public Response getPreRegistrationStatus(String preRegistartionId) {
		testSuite = "Fetch_the_status_of_a_application/Fetch Status of the application_smoke";
		request = getRequest(testSuite);
		request.put("preRegistrationId", preRegistartionId);
		try {
			response = applnLib.getRequestParm(preReg_FetchStatusOfApplicationURI, request);
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
		String configPath = null;
		File file = null;
		String PreRegistrationId=null;
		HashMap<String, String> parm=new HashMap<>();
		testSuite = "DocumentUpload/DocumentUpload_smoke";
		try {
			 configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + testSuite;
			 file = new File(configPath + "/AadhaarCard_POA.pdf");
		} catch (NullPointerException  e) {
			Assert.assertTrue(false, "File not present At given path "+configPath);
		}
		request = getRequest(testSuite);
		try {
			 PreRegistrationId = responseCreate.jsonPath().get("response.preRegistrationId").toString();
			
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception occured while creating Application"+e.getMessage());
		}
		parm.put("preRegistrationId", PreRegistrationId);
		request.put("requesttime", getCurrentDate());
		logger.info("document upload request ::::"+request.toString());
		response = applnLib.putFileAndJsonWithParm(preReg_DocumentUploadURI, request, file, parm);
		return response;
	}
	/**
	 * Method to get PreId From craete response
	 * @param createResponse
	 * @return
	 */
	public String getPreId(Response createResponse)
	{
		String preId=null;
		try {
			preId = createResponse.jsonPath().get("response.preRegistrationId").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception occured while creating application  " + e.getMessage());
		}
		return preId;
	}
	/**
	 * Method to get errormessage from response
	 * @param response
	 * @return
	 */
	public String getErrorMessage(Response response)
	{
		String message=null;
		try {
			message = response.jsonPath().get("errors[0].message").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception occured while getting error message from response  " + e.getMessage());
		}
		return message;
	}
	/**
	 * method to get errorCode from response
	 * @param response
	 * @return
	 */
	public String getErrorCode(Response response)
	{
		String errorCode=null;
		try {
			errorCode = response.jsonPath().get("errors[0].errorCode").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception occured while getting error message from response  " + e.getMessage());
		}
		return errorCode;
	}
	@SuppressWarnings("unchecked")
	public Response documentUploadParm(Response responseCreate, String PreRegistrationId) {
		testSuite = "Get_Pre_Registartion_data/Get Pre Pregistration Data of the application_smoke";
		JSONObject parm = getRequest(testSuite);
		parm.put("preRegistrationId", PreRegistrationId);

		testSuite = "DocumentUpload/DocumentUpload_smoke";
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + testSuite;
		File file = new File(configPath + "/AadhaarCard_POI.pdf");
		request = getRequest(testSuite);
		request.put("requesttime", getCurrentDate());	
		response = applnLib.putFileAndJsonWithParm(preReg_DocumentUploadURI, request, file, parm);
		
		return response;
	}

	/*
	 * Generic method to Upload Document
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response documentUpload(Response responseCreate, String fileName) {
		testSuite = "DocumentUpload/DocumentUpload_smoke";
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + testSuite;
		File file = new File(configPath + "/" + fileName + ".pdf");
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		FileReader fileReader = null;
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {

				try {
					fileReader = new FileReader(f.getPath());
					request = (JSONObject) new JSONParser().parse(fileReader);
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					try {
						fileReader.close();
					} catch (IOException e) {
						logger.info(e.getMessage());
					}
				}

			}
		}
		JSONObject object = null;
		for (Object key : request.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) request.get(key);
				object.put("pre_registartion_id",
						responseCreate.jsonPath().get("response.preRegistrationId").toString());
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
	 * 
	 * @return
	 */
	public String preRegAdminToken() {
		JSONObject preRegAdminTokenRequest = null;
		testSuite = "preRegAdminToken/preRegAdminToken_smoke";
		/**
		 * Reading request body from configpath
		 */
		preRegAdminTokenRequest = getRequest(testSuite);
		try {
			response = applnLib.authPostRequest(preRegAdminTokenRequest.toJSONString(), preReg_AdminTokenURI);
		} catch (Exception e) {
			logger.info(e);
		}
		String cookieValue = response.getCookie("Authorization");
		String auth_token = cookieValue;
		return auth_token;
	}

	public String regClientAdminToken() {
		JSONObject regClientAdminTokenRequest = null;
		testSuite = "regClientAdminToken/regClientAdminToken_smoke";
		/**
		 * Reading request body from configpath
		 */
		regClientAdminTokenRequest = getRequest(testSuite);
		try {
			response = applnLib.authPostRequest(regClientAdminTokenRequest.toJSONString(), preReg_AdminTokenURI);
		} catch (Exception e) {
			logger.info(e);
		}
		String cookieValue = response.getCookie("Authorization");
		String auth_token = cookieValue;
		return auth_token;
	}

	public Response syncAvailability()
	{

		return response = applnLib.get_RequestSync(preReg_syncAvailability);
	}

	/*
	 * Generic method to get the PreRegistration Data
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response getPreRegistrationData(String PreRegistrationId) {
		testSuite = "Get_Pre_Registartion_data/Get Pre Pregistration Data of the application_smoke";
		request = getRequest(testSuite);
		request.put("preRegistrationId", PreRegistrationId);
		try {

			response = applnLib.getRequestParm(preReg_FetchRegistrationDataURI, request);
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
	public Response updatePreReg(String preRegID) {
		testSuite = "UpdateDemographicData/UpdateDemographicData_smoke";
		JSONObject updateRequest = getRequest(testSuite);
		updateRequest.put("requesttime", getCurrentDate());
		Response updateDemographicDetailsResponse = updateDemographicDetails(updateRequest, preRegID);
		return updateDemographicDetailsResponse;
	}

	/*
	 * Generic method to compare Values
	 * 
	 */
	public void compareValues(String actual, String expected) {
		try {
			Assert.assertEquals(actual, expected);
			logger.info("values are equal");
		} catch (AssertionError e) {
			Assert.assertTrue(false, "Response Data Mismatch Failure  : expected is : "+expected+" found is : "+actual);
		}
	}

	/*
	 * Generic method to Get All Documents For Pre-Registration Id
	 * 
	 */

	public Response getAllDocumentForPreId(String preId) {
		String preRegGetDocByPreIdURI = preReg_GetDocByPreId+preId;
		response = applnLib.getRequestWithoutParm(preRegGetDocByPreIdURI);
		return response;
	}

	/*
	 * Generic method to Get All Documents For Pre-Registration Id
	 * 
	 */
	public Response getAllDocumentForDocId(String preId, String DocId) {
		HashMap<String, String> parm = new HashMap<>();
		parm.put("preRegistrationId", preId);

		String preRegGetDocByDocId = preReg_GetDocByDocId + DocId;
		response = applnLib.getRequestPathAndQueryParam(preRegGetDocByDocId, parm);
		return response;
	}

	/*
	 * Generic method to Delete All Document by Pre-RegistrationId
	 * 
	 */

	public Response deleteAllDocumentByDocId(String docId, String preId) {

		HashMap<String, String> parm = new HashMap<>();
		parm.put("preRegistrationId", preId);

		String preregDeleteDocumentByDocIdURI = prereg_DeleteDocumentByDocIdURI + docId;
		response = applnLib.deleteRequestPathAndQueryParam(preregDeleteDocumentByDocIdURI, parm);

		return response;
	}

	public Response FetchCentre(String regCenterID) {
		testSuite = "FetchAvailabilityDataOfRegCenters/prereg_FetchAvailabilityDataOfRegCenters_smoke";
		request = getRequest(testSuite);
		request.put("registrationCenterId", regCenterID);
		try {

			String preReg_FetchCenterIDURI = commonLibrary.fetch_IDRepo().get("preReg_FetchCenterIDuri");
			response = applnLib.getRequestParm(preReg_FetchCenterIDURI, request);
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

	public Response copyUploadedDocuments(String destPreId, String sourcePreId, String docCatCode) {

		String preRegCopyDocumentsURI = preReg_CopyDocumentsURI + destPreId;

		HashMap<String, String> parm = new HashMap<>();
		parm.put("catCode", docCatCode);
		parm.put("sourcePreId", sourcePreId);
		response = applnLib.put_Request_pathAndMultipleQueryParam(preRegCopyDocumentsURI, parm);
		return response;
	}

	/*
	 * 
	 * Generic method For Fetching the Registration center details
	 * 
	 */
	
	public Response FetchCentre() {
		String regCenterId = randomRegistrationCenterId();		
		String preRegFetchCenterIDURI=preReg_FetchCenterIDURI+regCenterId;
		response = applnLib.getRequestWithoutParm(preRegFetchCenterIDURI);
		return response;
	}
	public Response fetchCentreWithCerterId(String regCenterId) {
		testSuite = "FetchAvailabilityDataOfRegCenters/prereg_FetchAvailabilityDataOfRegCenters_smoke";
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + testSuite;
		ObjectNode fetchAvailabilityrequest = null;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		FileReader fileReader = null;
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {

				try {
					fileReader = new FileReader(f.getPath());
					request = (JSONObject) new JSONParser().parse(fileReader);
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					try {
						fileReader.close();
					} catch (IOException e) {
						logger.info(e.getMessage());
					}
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
	/*
	 * @SuppressWarnings("unchecked") public Response BookAppointment(Response
	 * DocumentUploadresponse, Response FetchCentreResponse, String preID) {
	 * List<String> appointmentDetails = new ArrayList<>();
	 * 
	 * String regCenterId = null; String appDate = null; String timeSlotFrom = null;
	 * String timeSlotTo = null; testSuite =
	 * "BookingAppointment/BookingAppointment_smoke"; JSONObject object = null;
	 * request = getRequest(testSuite); for (Object key : request.keySet()) { if
	 * (key.toString().toLowerCase().equals("request")) { object = new JSONObject();
	 * JSONObject resp = null;
	 * 
	 * try { resp = (JSONObject) new
	 * JSONParser().parse(DocumentUploadresponse.asString()); } catch
	 * (ParseException e) { e.printStackTrace(); }
	 * 
	 * JSONArray data = (JSONArray) resp.get("response"); JSONObject json =
	 * (JSONObject) data.get(0); json.get("preRegistrationId");
	 * //object.put("preRegistrationId", preID); JSONObject innerData = new
	 * JSONObject();
	 * 
	 * appointmentDetails = getAppointmentDetails(FetchCentreResponse);
	 * 
	 * regCenterId = appointmentDetails.get(0); appDate = appointmentDetails.get(1);
	 * timeSlotFrom = appointmentDetails.get(2); timeSlotTo =
	 * appointmentDetails.get(3);
	 * 
	 * <<<<<<< HEAD object.put("registration_center_id", regCenterId);
	 * object.put("appointment_date", appDate); object.put("time_slot_from",
	 * timeSlotFrom); object.put("time_slot_to", timeSlotTo);
	 * //object.put("preRegistrationId", preID); //object.put("newBookingDetails",
	 * innerData); ======= innerData.put("registration_center_id", regCenterId);
	 * innerData.put("appointment_date", appDate); innerData.put("time_slot_from",
	 * timeSlotFrom); innerData.put("time_slot_to", timeSlotTo);
	 * 
	 * >>>>>>> 70dca8c4df8fd3054b43f2f2568bd23c25be9a4a JSONArray objArr = new
	 * JSONArray(); objArr.add(object); request.replace(key, objArr);
	 * request.put("requesttime", getCurrentDate());
	 * 
	 * 
	 * } } testSuite =
	 * "Discard_Individual/Discard Individual Applicant By using Pre Registration ID_smoke"
	 * ; JSONObject parm = getRequest(testSuite); parm.put("preRegistrationId",
	 * preID); response = applnLib.postRequestWithParm(request,
	 * preReg_BookingAppointmentURI,parm); return response; }
	 */

	/*
	 * Generic method to Book An Appointment
	 * 
	 */
	public Response BookAppointment(Response FetchCentreResponse, String preID) {
		List<String> appointmentDetails = new ArrayList<>();

		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testSuite = "BookingAppointment/BookingAppointment_smoke";
		JSONObject object = null;
		request = getRequest(testSuite);
		for (Object key : request.keySet()) {
			if (key.toString().toLowerCase().equals("request")) {
				object = new JSONObject();
				JSONObject innerData = new JSONObject();
				appointmentDetails = getAppointmentDetails(FetchCentreResponse);
				regCenterId = appointmentDetails.get(0);
				appDate = appointmentDetails.get(1);
				timeSlotFrom = appointmentDetails.get(2);
				timeSlotTo = appointmentDetails.get(3);
				object.put("registration_center_id", regCenterId);
				object.put("appointment_date", appDate);
				object.put("time_slot_from", timeSlotFrom);
				object.put("time_slot_to", timeSlotTo);
				//object.put("preRegistrationId", preID);
				/*JSONArray objArr = new JSONArray();
				objArr.add(object);*/
				request.replace(key, object);
				request.put("requesttime", getCurrentDate());
			}
		}

		System.out.println("Request::Value Of book App::" + request.toString());

		String preReg_BookingAppURI = preReg_BookingAppointmentURI + preID;
		response = applnLib.postRequest(request, preReg_BookingAppURI);
		return response;
	}

	@SuppressWarnings("unchecked")
	public Response BookAppointment(Response DocumentUploadresponse, Response FetchCentreResponse, String preID) {
		List<String> appointmentDetails = new ArrayList<>();
		HashMap<String, String> parm=new HashMap<>();

		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testSuite = "BookingAppointment/BookingAppointment_smoke";
		JSONObject object = null;
		request = getRequest(testSuite);
		for (Object key : request.keySet()) {
			if (key.toString().toLowerCase().equals("request")) {
				object = new JSONObject();
				JSONObject innerData = new JSONObject();
				appointmentDetails = getAppointmentDetails(FetchCentreResponse);
				regCenterId = appointmentDetails.get(0);
				appDate = appointmentDetails.get(1);
				timeSlotFrom = appointmentDetails.get(2);
				timeSlotTo = appointmentDetails.get(3);
				object.put("registration_center_id", regCenterId);
				object.put("appointment_date", appDate);
				object.put("time_slot_from", timeSlotFrom);
				object.put("time_slot_to", timeSlotTo);
				JSONArray objArr = new JSONArray();
				objArr.add(object);
				request.replace(key, object);
				request.put("requesttime", getCurrentDate());
			}
		}
		parm.put("preRegistrationId", preID);
		response = applnLib.postRequestWithParm(request, preReg_BookingAppointmenturi, parm);
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
		JSONObject object = null;
		request = getRequest(testSuite);
		for (Object key : request.keySet()) {
			if (key.toString().toLowerCase().equals("request")) {
				object = new JSONObject();
				JSONObject resp = null;

				try {
					resp = (JSONObject) new JSONParser().parse(DocumentUploadresponse.asString());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				/*JSONArray data = (JSONArray) resp.get("response");
				JSONObject json = (JSONObject) data.get(0);*/
				/*
				 * json.get("preRegistrationId"); object.put("preRegistrationId", preID);
				 */
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

				object.put("registration_center_id", regCenterId);
				object.put("appointment_date", "2019-27-27");
				object.put("time_slot_from", timeSlotFrom);
				object.put("time_slot_to", timeSlotTo);
				// object.put("newBookingDetails", innerData);
				/*JSONArray objArr = new JSONArray();
				objArr.add(object);*/
				request.replace(key, object);
				request.put("requesttime", getCurrentDate());

			}
		}
		response = applnLib.postRequest(request, preReg_BookingAppointmentURI + preID);
		return response;
	}

	/*
	 * Generic method to Fetch Appointment Details
	 * 
	 */
	public Response FetchAppointmentDetails(String preID) {

		
		String preRegFetchAppDet = preReg_FecthAppointmentDetailsURI+preID;
		response = applnLib.getRequestWithoutParm(preRegFetchAppDet);
		return response;
	}

	public Response CancelBookingAppointment(String preID) {

		String preReg_CancelAppURI = preReg_CancelAppointmentURI + preID;
		response = applnLib.putRequest_WithoutBody(preReg_CancelAppURI);
		return response;
	}

	/*
	 * Generic method to Cancel Booking Appointment Details
	 * 
	 */
	public Response CancelBookingAppointment(Response FetchAppDet, String preID) {
		testSuite = "CancelAnBookedAppointment/CancelAnReBookedAppointment_smoke";
		request = getRequest(testSuite);
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
		testSuite = "FetchAppointmentDetails/FetchAppointmentDetails_smoke";
		JSONObject parm = getRequest(testSuite);
		parm.put("preRegistrationId", preID);
		cancelAppjson.put("requesttime", getCurrentDate());
		response = applnLib.putRequestWithParameter(preReg_CancelAppointmenturi, parm, cancelAppjson);
		return response;
	}

	public Response deleteAllDocumentByPreId(String preId) {
		
		String deleteDocumetByPreIdURI=preReg_DeleteAllDocumentByPreIdURI+preId;
		response=applnLib.deleteRequestWithPathParam(deleteDocumetByPreIdURI);
		return response;
	}

	/*
	 * Generic method to Cancel Booking Appointment Details
	 * 
	 */

	public Response ReBookAnAppointment(Response FetchCentreResponse, String preID) {
		List<String> appointmentDetails = new ArrayList<>();
		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testSuite = "BookingAppointment/BookingAppointment_smoke";
		JSONObject object = null;
		request = getRequest(testSuite);
		for (Object key : request.keySet()) {
			if (key.toString().toLowerCase().equals("request")) {
				object = new JSONObject();
				JSONObject innerData = new JSONObject();
				appointmentDetails = getAppointmentDetails(FetchCentreResponse);
				regCenterId = appointmentDetails.get(0);
				appDate = appointmentDetails.get(1);
				timeSlotFrom = appointmentDetails.get(2);
				timeSlotTo = appointmentDetails.get(3);
				object.put("registration_center_id", regCenterId);
				object.put("appointment_date", appDate);
				object.put("time_slot_from", timeSlotFrom);
				object.put("time_slot_to", timeSlotTo);
				JSONArray objArr = new JSONArray();
				objArr.add(object);
				request.replace(key, objArr);
				request.put("requesttime", getCurrentDate());
			}
		}
		testSuite = "Discard_Individual/Discard Individual Applicant By using Pre Registration ID_smoke";
		JSONObject parm = getRequest(testSuite);
		parm.put("preRegistrationId", preID);
		response = applnLib.postRequestWithParm(request, preReg_BookingAppointmenturi, parm);
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

	public Response logOut() {
		try {

			response = applnLib.postRequestWithoutBody(invalidateToken_URI);
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

	public Response retriveAllPreIdByRegId(Response fetchAppDet, String preId)
			throws FileNotFoundException, IOException, ParseException {

		String toDate = fetchAppDet.jsonPath().get("response.appointment_date").toString();
		String regCenterId = fetchAppDet.jsonPath().get("response.registration_center_id").toString();
		
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDate fromDate = currentTime.toLocalDate();

		logger.info("Local Date:" + fromDate.toString());

		logger.info("Form Date:" + fromDate);
		HashMap<String, String> parm = new HashMap<>();
		
		parm.put("from_date", fromDate.toString());
		parm.put("to_date", toDate);
		String preReg_RetriveBookedPreIdsByRegIdURI = preReg_RetriveBookedPreIdsByRegId + regCenterId;
		logger.info("preReg_RetriveBookedPreIdsByRegIdURI:"+preReg_RetriveBookedPreIdsByRegIdURI);
		response = applnLib.get_Request_pathAndMultipleQueryParam(preReg_RetriveBookedPreIdsByRegIdURI, parm);


		return response;
	}

	/*
	 * Generic function to fetch the random registration centerId
	 * 
	 */
	public String randomRegistrationCenterId() {
		Random rand = new Random();
		List<String> givenList = Lists.newArrayList("10002", "10013", "10014", "10010", "10015", "10006", "10004", "10008", "10012", "10005", "10003", "10007", "10009");
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
		int countCenterDetails = 0;
		List<String> appointmentDetails = new ArrayList<>();
		try {
			 countCenterDetails = fetchCenterResponse.jsonPath().getList("response.centerDetails").size();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Failed to fetch registration details while booking appointment");
		}
		
		for (int i = 0; i < countCenterDetails; i++) {
			try {
				fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].timeSlots[0].fromTime")
						.toString();
			} catch (NullPointerException e) {
				continue;
			}
			try {
				appointmentDetails.add(fetchCenterResponse.jsonPath().get("response.regCenterId").toString());
			appointmentDetails
					.add(fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].date").toString());
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[" + i + "].timeSlots[0].fromTime").toString());
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[" + i + "].timeSlots[0].toTime").toString());
			} 
			catch (NullPointerException e) {
				Assert.assertTrue(false, "Failed to fetch registration details while booking appointment");
			}
			break;
		}
		return appointmentDetails;
	}
	/*public String getErrorMessage(Response response)
	{
		
	}*/

	/*
	 * Generic method for multiple Upload Document
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Response multipleDocumentUpload(Response responseCreate, String folderPath, String documentName)
			throws FileNotFoundException, IOException, ParseException {

		testSuite = folderPath;
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_DocumentUploadURI");
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + testSuite;

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
						responseCreate.jsonPath().get("response.preRegistrationId").toString());
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
		testSuite = "TriggerNotification/preReg_TriggerNotification_emailId_outlookAccount_smoke";
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + testSuite;
		File file = new File(configPath + "/AadhaarCard_POA.pdf");

		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		FileReader fileReader = null;
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {

				try {
					fileReader = new FileReader(f.getPath());
					request = (JSONObject) new JSONParser().parse(fileReader);
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					try {
						fileReader.close();
					} catch (IOException e) {
						logger.info(e.getMessage());
					}
				}

			}
		}
		String value = null;
		JSONObject object = null;
		for (Object key : request.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) request.get(key);
				value = (String) object.get(langCodeKey);
				// object.put("pre_registartion_id",responseCreate.jsonPath().get("response.preRegistrationId").toString());
				// request.replace(key, object);
				object.remove(langCodeKey);
			}
		}
		request.put("requesttime", getCurrentDate());
		response = applnLib.postFileAndJsonParam(preReg_NotifyURI, request, file, langCodeKey, value);

		return response;
	}

	/*
	 * Generic method for dynamically change the request values in json file
	 * 
	 */

	public ObjectNode dynamicJsonRequest(String jsonPathTraverse, String jsonSetVal, String readFilePath,
			String writeFilePath) {

		String yourActualJSONString = null;
		ObjectNode newJson = null;
		try {
			yourActualJSONString = new String(Files.readAllBytes(Paths.get(readFilePath)), StandardCharsets.UTF_8);
			newJson = JsonPath.using(config).parse(yourActualJSONString).set(jsonPathTraverse, jsonSetVal).json();
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

		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + filepath;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();

		FileReader fileReader = null;
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {

				try {
					fileReader = new FileReader(f.getPath());
					request = (JSONObject) new JSONParser().parse(fileReader);
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					try {
						fileReader.close();
					} catch (IOException e) {
						logger.info(e.getMessage());
					}
				}

			}
		}

		return request;

	}

	public JSONObject createRequest(String testSuite) {
		JSONObject createPregRequest = null;
		createPregRequest = getRequest(testSuite);
		createPregRequest.put("requesttime", getCurrentDate());
		return createPregRequest;
	}

	public JSONObject otpRequest(String testSuite) {
		JSONObject otpRequest = null;
		testSuite = "generateOTP/generateOTP_smoke";
		/**
		 * Reading request body from configpath
		 */
		otpRequest = getRequest(testSuite);

		long number = (long) Math.floor(Math.random() * 9_000_000_00L) + 1_000_000_00L;
		userId = Long.toString(number);
		userId = "9" + userId;
		JSONObject object = null;
		for (Object key : otpRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) otpRequest.get(key);
				object.put("userId", userId);
				otpRequest.replace(key, object);
			}
		}
		otpRequest.put("requesttime", getCurrentDate());
		return otpRequest;
	}

	/**
	 * Get Otp Request
	 * 
	 * @param testSuite
	 * @return
	 */
	public JSONObject getOtpRequest(String testSuite) {
		JSONObject otpRequest = null;
		otpRequest = getRequest(testSuite);
		/**
		 * Reading request body from configpath
		 */
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + testSuite;
		request = getRequest(testSuite);
		request.put("requesttime", getCurrentDate());
		return request;
	}

	/**
	 * This method compares bytes array response from retrieve pre registration data
	 * with actual demographic data
	 * 
	 * @param retrivePreRegistrationDataResponse
	 * @param PrID
	 * @param expectedDemographicDetails
	 * @return
	 */
	public boolean validateRetrivePreRegistrationData(Response response, String PrID, Response craeteResponse) {
		boolean finalResult = false;
		HashMap<String, String> expectedDemographicDetails = craeteResponse.jsonPath()
				.get("response.demographicDetails");
		String folderName = "PreRegDocs";
		String data = response.jsonPath().get("response.zip-bytes").toString();
		String folder = response.jsonPath().get("response.zip-filename").toString();
		String folderPath = "src/test/resources/" + "preReg" + "/" + folderName;
		File f = new File(folderPath + "/" + folder);
		f.mkdirs();
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
		ZipEntry entry = null;
		try {
			while ((entry = zipStream.getNextEntry()) != null) {

				String entryName = entry.getName();
				logger.info("ASHISH" + entryName);
				String path = folderPath + "/" + folder + "/" + entryName;
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
		}
		JSONObject actualResponse = null;
		testSuite = folderName + "/" + PrID;
		JSONObject request = null;
		/**
		 * Reading request body from configpath
		 */
		String folder2 = "preReg";
		String configPath = System.getProperty("user.dir")+"\\src\\test\\resources\\" + folder2 + "\\" + "PreRegDocs" + "\\" + PrID;
		System.out.println("syso======================"+configPath);
		File folder1 = new File(configPath);
		File[] listOfFiles = folder1.listFiles();
		for (File f1 : listOfFiles) {
			if (f1.getName().contains("ID")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f1.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}

			}
			Map<String, Object> actualDemographicDetails = jsonObjectToMap(request);
			finalResult = actualDemographicDetails.keySet().equals(expectedDemographicDetails.keySet());
		}
		return finalResult;
	}

	public Map<String, Object> jsonObjectToMap(JSONObject object) {
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> map = gson.fromJson(object.toJSONString(), type);
		return map;
	}


	/*
	 * Generic method for Discard Booking
	 * 
	 */
	public static Map<String, Object> toMap(org.json.JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toList((org.json.JSONArray) value);
			}

			else if (value instanceof org.json.JSONObject) {
				value = toMap((org.json.JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(org.json.JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if (value instanceof org.json.JSONArray) {
				value = toList((org.json.JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((org.json.JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}

	public Response discardBooking(String preId) {

		testSuite = "DiscardBooking/DiscardBooking_smoke";
		request = getRequest(testSuite);
		/*
		 * 
		 * Pass the configuration object to using method of JsonPath and pass the json
		 * string to parse method which will return the parsed JSON. Then we pass the
		 * json path of the value that needs to be updated and the new value that we
		 * need in post Data to set method, which returns the updated POST (JSON) Data.
		 *
		 */
		ObjectNode discardBooking = JsonPath.using(config).parse(request.toString()).set("$.pre_registration_id", preId)
				.json();
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
	public static String getCurrentDate() {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
		String timeStamp=dateFormatGmt.format(Calendar.getInstance().getTime());
		return timeStamp;
	}

	public void updateStatusCode(String statusCode, String preregId) {
		dao.updateStatusCode(statusCode, preregId);
	}

	public List<? extends Object> preregFetchPreregDetails(String preregId) {
		return dao.preregFetchPreregDetails(preregId);
	}
	public JSONObject getAuditData(List<? extends Object> objs, int data) {
		List<Object> auditData = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		Object[] TestData = null;
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			int noOfData = TestData.length;
			if (obj.equals(objs.get(data))) {
				for (int i = 0; i < noOfData; i++) {
					Object audit = TestData[i];
					auditData.add(audit);
				}
			}

		}

		JSONObject object = getAuditData(auditData);
		return object;
	}

	public JSONObject getAuditData(List<Object> auditDatas) {
		JSONObject object = new JSONObject();
		String log_desc = auditDatas.get(0).toString();
		String event_id = auditDatas.get(1).toString();
		String event_type = auditDatas.get(2).toString();
		String event_name = auditDatas.get(3).toString();
		String session_user_id = auditDatas.get(4).toString();
		String module_name = auditDatas.get(5).toString();
		String ref_id = auditDatas.get(6).toString();
		String ref_id_type = auditDatas.get(7).toString();
		object.put("log_desc", log_desc);
		object.put("event_id", event_id);
		object.put("event_type", event_type);
		object.put("event_name", event_name);
		object.put("session_user_id", session_user_id);
		object.put("module_name", module_name);
		object.put("ref_id", ref_id);
		object.put("ref_id_type", ref_id_type);
		return object;
	}
	public boolean jsonComparison(Object expectedResponseBody, Object actualResponseBody) {
		JSONObject reqObj = (JSONObject) expectedResponseBody;
		JSONObject resObj = (JSONObject) actualResponseBody;
		ObjectMapper mapper = new ObjectMapper();
			JsonNode requestJson = null;
			try {
				requestJson = mapper.readTree(reqObj.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JsonNode responseJson = null;
			try {
				responseJson = mapper.readTree(resObj.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			JsonNode diffJson = JsonDiff.asJson(requestJson, responseJson);

			logger.error("======" + diffJson + "==========");
			try {
				Assert.assertEquals(diffJson.toString(), "[]");
				logger.info("equal");
				
			} catch (AssertionError e) {
				Assert.assertTrue(false, "Response Data Mismatch Failure  : difference is : "+diffJson);
			}
			for (int i = 0; i < diffJson.size(); i++) {
				JsonNode operation = diffJson.get(i);
				if (!operation.get("op").toString().equals("\"move\"")) {
					logger.error("not equal");
					return false;
				}
			}
		logger.info("equal");
		return true;

	}

	public String getDocId(Response documentUploadResponse)
	{
		String docId=null;
		try {
			docId=documentUploadResponse.jsonPath().get("response.docId").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception while getting DocId from document Upload Response");
		}
		return docId;
		
	}
	
	
	
	
	/*
	 * Generic method to QR Code
	 * 
	 */

	public Response QRCode() {
		testSuite = QRCodeFilePath;
		logger.info("Path Val:"+QRCodeFilePath);
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + folder + "/" + testSuite;

		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		FileReader fileReader = null;
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {

				try {
					fileReader = new FileReader(f.getPath());
					request = (JSONObject) new JSONParser().parse(fileReader);
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					try {
						fileReader.close();
					} catch (IOException e) {
						logger.info(e.getMessage());
					}
				}

			}
		}

		request.put("requesttime", getCurrentDate());
		response = applnLib.authPostRequest(request, qrCode_URI);

		return response;
	}

	public Response syncMasterData() {
		Response syncMasterDataRes = null;
		try {
			syncMasterDataRes = applnLib.getRequestWithoutBody(preReg_SyncMasterDataURI);
		} catch (Exception e) {
			logger.info(e);
		}

		return syncMasterDataRes;
	}

	
	
	
	/*
	 * Generic method to Multiple BookAn Appointment
	 * 
	 */
	public Response multipleBookApp(Response FetchCentreResponseOne,Response FetchCentreResponseTwo,String preIDFirstUsr,String preIDSecondUsr) {
		List<String> appointmentDetailsFirstUsr = new ArrayList<>();
		List<String> appointmentDetailsSecondUsr = new ArrayList<>();
		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testSuite = "MultipleBookingAppointment/MultipleBookingAppointment_smoke";
		JSONObject object = null;
		request = getRequest(testSuite);
		
		
		appointmentDetailsFirstUsr=getAppointmentDetails(FetchCentreResponseOne);
		appointmentDetailsSecondUsr=getAppointmentDetails(FetchCentreResponseTwo);
		
		
		ObjectNode mutBookPreIdFirstUsr = JsonPath.using(config).parse(request.toJSONString())
				.set("$.request.bookingRequest[0].preRegistrationId", preIDFirstUsr).json();
		
		ObjectNode mutBookAppDateFirstUsr = JsonPath.using(config).parse(mutBookPreIdFirstUsr.toString())
				.set("$.request.bookingRequest[0].appointment_date", appointmentDetailsFirstUsr.get(1))
				.json();
		ObjectNode mutBookRegCenterIdFirstUsr = JsonPath.using(config).parse(mutBookAppDateFirstUsr.toString())
				.set("$.request.bookingRequest[0].registration_center_id", appointmentDetailsFirstUsr.get(0))
				.json();
		
		ObjectNode mutBookAppTimeSlotFromFirstUsr = JsonPath.using(config).parse(mutBookRegCenterIdFirstUsr.toString())
				.set("$.request.bookingRequest[0].time_slot_from", appointmentDetailsFirstUsr.get(2))
				.json();
		ObjectNode mutBookAppTimeSlotToFirstUsr = JsonPath.using(config).parse(mutBookAppTimeSlotFromFirstUsr.toString())
				.set("$.request.bookingRequest[0].time_slot_to", appointmentDetailsFirstUsr.get(3))
				.json();
		
		
		
	
		
		ObjectNode mutBookPreIdSecondUsr = JsonPath.using(config).parse(mutBookAppTimeSlotToFirstUsr.toString())
				.set("$.request.bookingRequest[1].preRegistrationId", preIDSecondUsr).json();
		
		ObjectNode mutBookAppDateSecondUsr = JsonPath.using(config).parse(mutBookPreIdSecondUsr.toString())
				.set("$.request.bookingRequest[1].appointment_date", appointmentDetailsSecondUsr.get(1))
				.json();
		
		ObjectNode mutBookRegCenterIdSecondUsr = JsonPath.using(config).parse(mutBookAppDateSecondUsr.toString())
				.set("$.request.bookingRequest[1].registration_center_id", appointmentDetailsSecondUsr.get(0))
				.json();
		
		
		ObjectNode mutBookAppTimeSlotFromSecondUsr = JsonPath.using(config).parse(mutBookRegCenterIdSecondUsr.toString())
				.set("$.request.bookingRequest[1].time_slot_from", appointmentDetailsSecondUsr.get(2))
				.json();
		
		ObjectNode mutBookAppTimeSlotToSecondUsr = JsonPath.using(config).parse(mutBookAppTimeSlotFromSecondUsr.toString())
				.set("$.request.bookingRequest[1].time_slot_to", appointmentDetailsSecondUsr.get(3))
				.json();
		
		
		
		String multiplBookAppDetStr = mutBookAppTimeSlotToSecondUsr.toString();
		JSONObject multipleBookAppjson = null;
		try {
			multipleBookAppjson = (JSONObject) parser.parse(multiplBookAppDetStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		 multipleBookAppjson.put("requesttime", getCurrentDate());
		 logger.info("Request::Multiple Book Appointment::" + multipleBookAppjson.toString());
		 JSONObject yuu = multipleBookAppjson;
		String preReg_BookingAppURI = preReg_MultipleBookAppURI;
		response = applnLib.postRequest(multipleBookAppjson, preReg_BookingAppURI);
		return response;
	}
	
	
	
	
	
	/*
	 * Generic method to Multiple BookAn Appointment
	 * 
	 */
	public JSONObject multipleBookAppRequest(Response FetchCentreResponseOne,Response FetchCentreResponseTwo,String preIDFirstUsr,String preIDSecondUsr) {
		List<String> appointmentDetailsFirstUsr = new ArrayList<>();
		List<String> appointmentDetailsSecondUsr = new ArrayList<>();
		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testSuite = "MultipleBookingAppointment/MultipleBookingAppointment_smoke";
		JSONObject object = null;
		request = getRequest(testSuite);
		
		
		appointmentDetailsFirstUsr=getAppointmentDetails(FetchCentreResponseOne);
		appointmentDetailsSecondUsr=getAppointmentDetails(FetchCentreResponseTwo);
		
		
		ObjectNode mutBookPreIdFirstUsr = JsonPath.using(config).parse(request.toJSONString())
				.set("$.request.bookingRequest[0].preRegistrationId", preIDFirstUsr).json();
		
		ObjectNode mutBookAppDateFirstUsr = JsonPath.using(config).parse(mutBookPreIdFirstUsr.toString())
				.set("$.request.bookingRequest[0].appointment_date", appointmentDetailsFirstUsr.get(1))
				.json();
		ObjectNode mutBookRegCenterIdFirstUsr = JsonPath.using(config).parse(mutBookAppDateFirstUsr.toString())
				.set("$.request.bookingRequest[0].registration_center_id", appointmentDetailsFirstUsr.get(0))
				.json();
		
		ObjectNode mutBookAppTimeSlotFromFirstUsr = JsonPath.using(config).parse(mutBookRegCenterIdFirstUsr.toString())
				.set("$.request.bookingRequest[0].time_slot_from", appointmentDetailsFirstUsr.get(2))
				.json();
		ObjectNode mutBookAppTimeSlotToFirstUsr = JsonPath.using(config).parse(mutBookAppTimeSlotFromFirstUsr.toString())
				.set("$.request.bookingRequest[0].time_slot_to", appointmentDetailsFirstUsr.get(3))
				.json();
		
		
		
	
		
		ObjectNode mutBookPreIdSecondUsr = JsonPath.using(config).parse(mutBookAppTimeSlotToFirstUsr.toString())
				.set("$.request.bookingRequest[1].preRegistrationId", preIDSecondUsr).json();
		
		ObjectNode mutBookAppDateSecondUsr = JsonPath.using(config).parse(mutBookPreIdSecondUsr.toString())
				.set("$.request.bookingRequest[1].appointment_date", appointmentDetailsSecondUsr.get(1))
				.json();
		
		ObjectNode mutBookRegCenterIdSecondUsr = JsonPath.using(config).parse(mutBookAppDateSecondUsr.toString())
				.set("$.request.bookingRequest[1].registration_center_id", appointmentDetailsSecondUsr.get(0))
				.json();
		
		
		ObjectNode mutBookAppTimeSlotFromSecondUsr = JsonPath.using(config).parse(mutBookRegCenterIdSecondUsr.toString())
				.set("$.request.bookingRequest[1].time_slot_from", appointmentDetailsSecondUsr.get(2))
				.json();
		
		ObjectNode mutBookAppTimeSlotToSecondUsr = JsonPath.using(config).parse(mutBookAppTimeSlotFromSecondUsr.toString())
				.set("$.request.bookingRequest[1].time_slot_to", appointmentDetailsSecondUsr.get(3))
				.json();
		
		
		
		String multiplBookAppDetStr = mutBookAppTimeSlotToSecondUsr.toString();
		JSONObject multipleBookAppjson = null;
		try {
			multipleBookAppjson = (JSONObject) parser.parse(multiplBookAppDetStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		 multipleBookAppjson.put("requesttime", getCurrentDate());
		 logger.info("Multiple Book App Res::"+multipleBookAppjson.toString());
		return multipleBookAppjson;
		
	}
	
	
	
	
	/*
	 * Generic method to Book An Appointment
	 * 
	 */
	public JSONObject BookAppointmentRequest(Response FetchCentreResponse, String preID) {
		List<String> appointmentDetails = new ArrayList<>();

		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testSuite = "BookingAppointment/BookingAppointment_smoke";
		JSONObject object = null;
		request = getRequest(testSuite);
		for (Object key : request.keySet()) {
			if (key.toString().toLowerCase().equals("request")) {
				object = new JSONObject();
				JSONObject innerData = new JSONObject();
				appointmentDetails = getAppointmentDetails(FetchCentreResponse);
				regCenterId = appointmentDetails.get(0);
				appDate = appointmentDetails.get(1);
				timeSlotFrom = appointmentDetails.get(2);
				timeSlotTo = appointmentDetails.get(3);
				object.put("registration_center_id", regCenterId);
				object.put("appointment_date", appDate);
				object.put("time_slot_from", timeSlotFrom);
				object.put("time_slot_to", timeSlotTo);
				//object.put("preRegistrationId", preID);
				/*JSONArray objArr = new JSONArray();
				objArr.add(object);*/
				request.replace(key, object);
				request.put("requesttime", getCurrentDate());
			}
		}

		logger.info("Request::Value Of book App::" + request.toString());

		
		return request;
	}
	
	
	@Test
	void myTest()
	{
		
		//multipleBookApp("090909");
	}
	
	
	
	@BeforeClass
	public void PreRegistrationResourceIntialize() {
		preReg_CreateApplnURI = commonLibrary.fetch_IDRepo().get("preReg_CreateApplnURI");
		preReg_DocumentUploadURI = commonLibrary.fetch_IDRepo().get("preReg_DocumentUploadURI");
		preReg_BookingAppointmentURI = commonLibrary.fetch_IDRepo().get("preReg_BookingAppointmentURI");
		preReg_DataSyncnURI = commonLibrary.fetch_IDRepo().get("preReg_DataSyncnURI");
		preReg_FetchRegistrationDataURI = commonLibrary.fetch_IDRepo().get("preReg_FetchRegistrationDataURI");
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
		preReg_FetchAllApplicationCreatedByUserURI = commonLibrary.fetch_IDRepo()
				.get("preReg_FetchAllApplicationCreatedByUserURI");
		preReg_DiscardBookingURI = commonLibrary.fetch_IDRepo().get("preReg_DiscardBookingURI");
		
		langCodeKey = commonLibrary.fetch_IDRepo().get("langCode.key");
		otpSend_URI = commonLibrary.fetch_IDRepo().get("otpSend_URI");
		validateOTP_URI = commonLibrary.fetch_IDRepo().get("validateOTP_URI");
		preReg_AdminTokenURI = commonLibrary.fetch_IDRepo().get("preReg_AdminTokenURI");
		preReg_translitrationRequestURI = commonLibrary.fetch_IDRepo().get("preReg_translitrationRequestURI");
		invalidateToken_URI = commonLibrary.fetch_IDRepo().get("invalidateToken_URI");
		preReg_GetDocByDocId = commonLibrary.fetch_IDRepo().get("preReg_GetDocByDocId");
		preReg_CancelAppointmenturi = commonLibrary.fetch_IDRepo().get("preReg_CancelAppointmenturi");
		preReg_GetPreRegistrationConfigData = commonLibrary.fetch_IDRepo().get("preReg_GetPreRegistrationConfigData");
		preReg_BookingAppointmenturi = commonLibrary.fetch_IDRepo().get("preReg_BookingAppointmenturi");
		preReg_syncAvailability = commonLibrary.fetch_IDRepo().get("preReg_syncAvailability");
		preReg_FecthAppointmentDetailsuri = commonLibrary.fetch_IDRepo().get("preReg_FecthAppointmentDetailsuri");
		preReg_GetDocByPreId= commonLibrary.fetch_IDRepo().get("preReg_GetDocByPreId");
		qrCode_URI = commonLibrary.fetch_IDRepo().get("qrCode_URI");
		
		preReg_RetriveBookedPreIdsByRegId = preRegUtil.fetchPreregProp().get("preReg_RetriveBookedPreIdsByRegId");
		QRCodeFilePath=preRegUtil.fetchPreregProp().get("QRCodeFilePath");
		preReg_NotifyURI = preRegUtil.fetchPreregProp().get("preReg_NotifyURI");
		preReg_SyncMasterDataURI = preRegUtil.fetchPreregProp().get("preReg_SyncMasterDataURI");
		preReg_FetchCenterIDURI = preRegUtil.fetchPreregProp().get("preReg_FetchCenterIDURI");
		preReg_MultipleBookAppURI = preRegUtil.fetchPreregProp().get("preReg_MultipleBooking");
		}

}