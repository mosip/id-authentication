package io.mosip.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
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
import org.testng.annotations.BeforeClass;
import org.testng.collections.Lists;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.preregistration.util.PreRegistrationUtil;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
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
	public io.mosip.kernel.service.ApplicationLibrary appLib = new io.mosip.kernel.service.ApplicationLibrary();
	private static Logger logger = Logger.getLogger(BaseTestCase.class);
	// private static CommonLibrary commonLibrary = new CommonLibrary();
	io.mosip.kernel.util.CommonLibrary cLib = new io.mosip.kernel.util.CommonLibrary();
	RegProcApiRequests regproc=new RegProcApiRequests();

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
	private static String preReg_DocUploadURI;
	PreRegistrationUtil preRegUtil = new PreRegistrationUtil();
	/*
	 * We configure the jsonProvider using Configuration builder.
	 */
	Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider()).build();

	/*
	 * Generic method to Create Pre-Registration Application
	 * 
	 */
	public Response CreatePreReg(String cookie) {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		request = getRequest(testSuite);
		request.put("requesttime", getCurrentDate());
		createPregResponse = appLib.postWithJson(preReg_CreateApplnURI, request, cookie);
		return createPregResponse;
	}

	public JSONObject getRequest(String testSuite) {
		JSONObject request = null;
		/**
		 * Reading request body from configpath
		 */
		String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		FileReader fileReader = null;
		try {
			for (File f : listOfFiles) {
				if (f.getName().contains("request")) {

					try {
//						fileReader =  new InputStreamReader(new FileInputStream("myFile.txt"), "utf-8");
						fileReader = new FileReader(f.getPath());
						//request = (JSONObject) new JSONParser().parse(fileReader);
						request = (JSONObject) new JSONParser().parse(new InputStreamReader(new FileInputStream(f.getAbsolutePath()), "UTF-8"));
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					} finally {
						try {
							fileReader.close();
						} catch (IOException e) {
							e.printStackTrace();
							logger.info(e.getMessage());
						}
					}

				}
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
			Assert.assertTrue(false, "File was not present at given path  " + configPath + e.getClass());

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
		JSONObject translitrationRequest = null;
		/**
		 * Reading request body from configpath
		 */
		String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
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
			Assert.assertTrue(false, "File was not present at given path  " + configPath + e.getClass());
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
	public Response translitration(JSONObject translitrationRequest, String cookie) {
		try {
			response = appLib.postWithJson(preReg_translitrationRequestURI, translitrationRequest.toJSONString(),
					cookie);
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	/**
	 * @param cookie
	 * @return this method is for checking cookie(token) is expired or not.
	 */
	public boolean isValidToken(String cookie) {
		
		if(regproc.validateToken(cookie))
		{
			// we will have to read configCookieTime, token and secret from property file
			String token_base = "Mosip-Token";
			String secret = "authjwtsecret";
			long configCookieTime = 20;
			Integer cookieGenerationTimeMili = null;

			try {
				cookieGenerationTimeMili = (Integer) Jwts.parser().setSigningKey(secret)
						.parseClaimsJws(cookie.substring(token_base.length())).getBody().get("iat");
			} catch (ExpiredJwtException | NullPointerException | UnsupportedJwtException | MalformedJwtException
					| SignatureException | IllegalArgumentException e) {
				logger.info(e.getMessage());
				return false;
			}
			Date date = new Date(Long.parseLong(Integer.toString(cookieGenerationTimeMili)) * 1000);
			Date currentDate = new Date();
			long intervalMin = (currentDate.getTime() - date.getTime()) / (60 * 1000) % 60;

			if (intervalMin <= configCookieTime)
				return true;
			else
				return false;

		}
		else
			return false;
		
	}

	/**
	 * Generate OTP
	 * 
	 * @return
	 */
	public Response generateOTP(JSONObject request) {
		response = appLib.postWithJson(otpSend_URI, request);
		return response;
	}

	public Response pagination(String index, String cookie) {
		HashMap<String, String> query = new HashMap<>();
		query.put("pageIndex", index);
		Response paginationResponse = appLib.getWithQueryParam(preReg_CreateApplnURI, query, cookie);
		return paginationResponse;
	}

	public String getToken() {
		testSuite = "generateOTP/generateOTP_smoke";
		request = otpRequest(testSuite);
		generateOTP(request);
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
	public Response validateOTP(JSONObject request) {
		response = appLib.postWithJson(validateOTP_URI, request);
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
	public Response discardApplication(String PreRegistrationId, String cookie) {
		testSuite = "Discard_Individual/Discard Individual Applicant By using Pre Registration ID_smoke";
		request = getRequest(testSuite);
		request.put("preRegistrationId", PreRegistrationId);
		try {
			return appLib.deleteWithPathParams(preReg_DiscardApplnURI, request, cookie);

		} catch (Exception e) {
			logger.info(e);
		}
		return null;
	}

	public boolean fetchDocs1(Response response, String folderName) {
		String data = response.jsonPath().get("response.zip-bytes").toString();
		String folder = response.jsonPath().get("response.zip-filename").toString();
		String folderPath = cLib.getResourcePath() + "preReg" + "/" + folderName;
		File f = new File(folderPath + "/" + folder);
		f.mkdirs();
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
		ZipEntry entry = null;
		try {
			while ((entry = zipStream.getNextEntry()) != null) {

				String entryName = entry.getName();
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
		uiConfigParams = cLib.readProperty("IDRepo").get(configParameter);
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
		String folderPath = cLib.getResourcePath() + "preReg" + "/" + folderName;
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

	public Response fetchAllPreRegistrationCreatedByUser(String cookie) {
		try {
			response = appLib.getWithoutParams(preReg_FetchAllApplicationCreatedByUserURI, cookie);
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	public Response getPreRegistrationConfigData(String cookie) {
		try {
			response = appLib.getWithoutParams(preReg_GetPreRegistrationConfigData, cookie);
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	/*
	 * Generic method to fetch the Create Pre-Registration Application
	 * 
	 */
	public Response CreatePreReg(JSONObject createRequest, String cookie) {
		try {
			createPregResponse = appLib.postWithJson(preReg_CreateApplnURI, createRequest.toJSONString(), cookie);
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
		if (!isValidToken(regClientToken)) {
			regClientToken = regClientAdminToken();
		}
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

		response = appLib.postWithJson(preReg_ReverseDataSyncURI, reverseDataSyncRequest.toJSONString(),
				regClientToken);

		return response;
	}

	/*
	 * Generic method to retrieve all the preregistration data
	 * 
	 */
	public Response retrivePreRegistrationData(String preRegistrationId) {
		if (!isValidToken(regClientToken)) {
			regClientToken = regClientAdminToken();
		}
		testSuite = "Retrive_PreRegistration/Retrive Pre registration data of an applicant after booking an appointment_smoke";
		request = getRequest(testSuite);
		request.put("preRegistrationId", preRegistrationId);
		try {
			response = appLib.getWithPathParam(preReg_DataSyncnURI, request, regClientToken);
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

	public Response updateDemographicDetails(JSONObject body, String pre_registration_id, String cookie) {
		testSuite = "Retrive_PreRegistration/Retrive Pre registration data of an applicant after booking an appointment_smoke";
		request = getRequest(testSuite);
		request.put("preRegistrationId", pre_registration_id);
		response = appLib.putWithPathParamsBody(preReg_UpdateStatusAppURI, request, body, cookie);
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
	 * Generic method to get the PreRegistration Status
	 * 
	 */
	public Response getPreRegistrationStatus(String preRegistartionId, String cookie) {
		testSuite = "Fetch_the_status_of_a_application/Fetch Status of the application_smoke";
		request = getRequest(testSuite);
		request.put("preRegistrationId", preRegistartionId);
		try {
			response = appLib.getWithPathParam(preReg_FetchStatusOfApplicationURI, request, cookie);
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}
	/*
	 * Generic method for multiple Upload Document
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Response multipleDocumentUpload(Response responseCreate, String folderPath, String documentName,
			String cookie) throws FileNotFoundException, IOException, ParseException {

		testSuite = folderPath;
		// preReg_URI = commonLibrary.fetch_IDRepo("preReg_DocumentUploadURI");
		String configPath = cLib.getResourcePath() + folder + "/" + testSuite;

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

		response = appLib.putFileAndJson(preReg_DocumentUploadURI, request, file, cookie);

		return response;
	}

	/*
	 * Generic method to Upload Document
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response documentUpload(Response responseCreate, String cookie) {
		testSuite = "DocumentUpload/DocumentUpload_smoke";
		String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
		File file = null;
		file = new File(configPath + "/AadhaarCard_POI.pdf");
		logger.info("File Name:" + file);
		request = getRequest(testSuite);
		request.put("requesttime", getCurrentDate());
		String Document_request = cLib.readProperty("IDRepo").get("req.Documentrequest");
		// preReg_DocumentUploadURI=preReg_DocumentUploadURI+PreRegistrationId;
		preReg_DocumentUploadURI = preReg_DocUploadURI + getPreId(responseCreate);
		HashMap<String, String> map = new HashMap<>();
		map.put(Document_request, request.toJSONString());
		response = appLib.postWithFileFormParams(preReg_DocumentUploadURI, map, file, "file", cookie);

		return response;
	}

	@SuppressWarnings("unchecked")
	public Response documentUpload(Response responseCreate, String PreRegistrationId, String documentName,
			String cookie) {
		testSuite = "DocumentUpload/DocumentUpload_smoke";
		String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
		File file = null;
		if (documentName == null) {
			file = new File(configPath + "/AadhaarCard_POI.pdf");
		} else {
			file = new File(configPath + "/" + documentName);
		}
		logger.info("File Name:" + file);
		request = getRequest(testSuite);
		request.put("requesttime", getCurrentDate());
		String Document_request = cLib.readProperty("IDRepo").get("req.Documentrequest");
		// preReg_DocumentUploadURI=preReg_DocumentUploadURI+PreRegistrationId;
		preReg_DocumentUploadURI = preReg_DocUploadURI + PreRegistrationId;
		HashMap<String, String> map = new HashMap<>();
		map.put(Document_request, request.toJSONString());
		response = appLib.postWithFileFormParams(preReg_DocumentUploadURI, map, file, "file", cookie);

		return response;
	}

	/**
	 * Method to get PreId From craete response
	 * 
	 * @param createResponse
	 * @return
	 */
	public String getPreId(Response createResponse) {
		String preId = null;
		try {
			preId = createResponse.jsonPath().get("response.preRegistrationId").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception occured while creating application  " + e.getMessage());
		}
		return preId;
	}

	/**
	 * Method to get errormessage from response
	 * 
	 * @param response
	 * @return
	 */
	public String getErrorMessage(Response response) {
		String message = null;
		try {
			message = response.jsonPath().get("errors[0].message").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception occured while getting error message from response  " + e.getMessage());
		}
		return message;
	}

	/**
	 * method to get errorCode from response
	 * 
	 * @param response
	 * @return
	 */
	public String getErrorCode(Response response) {
		String errorCode = null;
		try {
			errorCode = response.jsonPath().get("errors[0].errorCode").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception occured while getting error message from response  " + e.getMessage());
		}
		return errorCode;
	}

	@SuppressWarnings("unchecked")
	public Response documentUploadParm(Response responseCreate, String PreRegistrationId, String cookie) {
		testSuite = "DocumentUpload/DocumentUpload_smoke";
		String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
		File file = null;
		file = new File(configPath + "/AadhaarCard_POI.pdf");
		logger.info("File Name:" + file);
		request = getRequest(testSuite);
		request.put("requesttime", getCurrentDate());
		String Document_request = cLib.readProperty("IDRepo").get("req.Documentrequest");
		preReg_DocumentUploadURI = preReg_DocUploadURI + PreRegistrationId;
		HashMap<String, String> map = new HashMap<>();
		map.put(Document_request, request.toJSONString());
		response = appLib.postWithFileFormParams(preReg_DocumentUploadURI, map, file, "file", cookie);
		return response;
	}

	/*
	 * Generic method to Upload Document
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response documentUploadmultiple(Response responseCreate, String fileName) {
		testSuite = "DocumentUpload/DocumentUpload_smoke";
		String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
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
			response = appLib.postWithJson(preReg_AdminTokenURI, preRegAdminTokenRequest.toJSONString());
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
			response = appLib.postWithJson(preReg_AdminTokenURI, regClientAdminTokenRequest.toJSONString());
		} catch (Exception e) {
			logger.info(e);
		}
		String cookieValue = response.getCookie("Authorization");
		String auth_token = cookieValue;
		return auth_token;
	}

	public Response syncAvailability() {
		if (!isValidToken(preRegAdminToken)) {
			preRegAdminToken = preRegAdminToken();
		}

		return response = appLib.getWithoutParams(preReg_syncAvailability, preRegAdminToken);
	}

	/*
	 * Generic method to get the PreRegistration Data
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response getPreRegistrationData(String PreRegistrationId, String cookie) {
		testSuite = "Get_Pre_Registartion_data/Get Pre Pregistration Data of the application_smoke";
		request = getRequest(testSuite);
		request.put("preRegistrationId", PreRegistrationId);
		try {

			response = appLib.getWithPathParam(preReg_FetchRegistrationDataURI, request, cookie);
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
	public Response updatePreReg(String preRegID, String cookie) {
		testSuite = "UpdateDemographicData/UpdateDemographicData_smoke";
		JSONObject updateRequest = getRequest(testSuite);
		updateRequest.put("requesttime", getCurrentDate());
		Response updateDemographicDetailsResponse = updateDemographicDetails(updateRequest, preRegID, cookie);
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
			Assert.assertTrue(false,
					"Response Data Mismatch Failure  : expected is : " + expected + " found is : " + actual);
		}
	}

	/*
	 * Generic method to Get All Documents For Pre-Registration Id
	 * 
	 */

	public Response getAllDocumentForPreId(String preId, String cookie) {
		String preRegGetDocByPreIdURI = preReg_GetDocByPreId + preId;
		response = appLib.getWithoutParams(preRegGetDocByPreIdURI, cookie);
		return response;
	}

	/*
	 * Generic method to Get All Documents For Pre-Registration Id
	 * 
	 */
	public Response getAllDocumentForDocId(String preId, String DocId, String cookie) {
		HashMap<String, String> parm = new HashMap<>();
		parm.put("preRegistrationId", preId);

		String preRegGetDocByDocId = preReg_GetDocByDocId + DocId;
		response = appLib.getWithQueryParam(preRegGetDocByDocId, parm, cookie);
		return response;
	}

	/*
	 * Generic method to Delete All Document by Pre-RegistrationId
	 * 
	 */

	public Response deleteAllDocumentByDocId(String docId, String preId, String cookie) {

		HashMap<String, String> parm = new HashMap<>();
		parm.put("preRegistrationId", preId);
		HashMap<String, String> parm1 = new HashMap<>();
		parm1.put("documentId", docId);
		String preregDeleteDocumentByDocIdURI = prereg_DeleteDocumentByDocIdURI + docId;
		response = appLib.deleteWithQueryParams(preregDeleteDocumentByDocIdURI, parm, cookie);
		return response;
	}

	public Response FetchCentre(String regCenterID, String cookie) {
		testSuite = "FetchAvailabilityDataOfRegCenters/prereg_FetchAvailabilityDataOfRegCenters_smoke";
		request = getRequest(testSuite);
		request.put("registrationCenterId", regCenterID);
		try {

			String preReg_FetchCenterIDURI = cLib.readProperty("IDRepo").get("preReg_FetchCenterIDuri");
			response = appLib.getWithPathParam(preReg_FetchCenterIDURI, request, cookie);
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

	public Response copyUploadedDocuments(String destPreId, String sourcePreId, String docCatCode, String cookie) {

		String preRegCopyDocumentsURI = preReg_CopyDocumentsURI + destPreId;

		HashMap<String, String> parm = new HashMap<>();
		parm.put("catCode", docCatCode);
		parm.put("sourcePreId", sourcePreId);
		response = appLib.putWithQueryParams(preRegCopyDocumentsURI, parm, cookie);
		return response;
	}

	/*
	 * 
	 * Generic method For Fetching the Registration center details
	 * 
	 */

	public Response FetchCentre(String cookie) {
		String regCenterId = randomRegistrationCenterId();
		String preRegFetchCenterIDURI = preReg_FetchCenterIDURI + regCenterId;
		response = appLib.getWithoutParams(preRegFetchCenterIDURI, cookie);
		return response;
	}

	/*
	 * Generic method to Book An Appointment
	 * 
	 */
	public Response BookAppointment(Response FetchCentreResponse, String preID, String cookie) {
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
				// object.put("preRegistrationId", preID);
				/*
				 * JSONArray objArr = new JSONArray(); objArr.add(object);
				 */
				request.replace(key, object);
				request.put("requesttime", getCurrentDate());
			}
		}

		String preReg_BookingAppURI = preReg_BookingAppointmentURI + preID;
		response = appLib.postWithJson(preReg_BookingAppURI, request, cookie);
		return response;
	}

	@SuppressWarnings("unchecked")
	public Response BookAppointment(Response DocumentUploadresponse, Response FetchCentreResponse, String preID,
			String cookie) {
		List<String> appointmentDetails = new ArrayList<>();
		HashMap<String, String> parm = new HashMap<>();

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
				try {
					regCenterId = appointmentDetails.get(0);
					appDate = appointmentDetails.get(1);
					timeSlotFrom = appointmentDetails.get(2);
					timeSlotTo = appointmentDetails.get(3);
				} catch (IndexOutOfBoundsException e) {
					Assert.fail("slots are not available for give registration center");
				}
			
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
		response = appLib.postWithPathParams(preReg_BookingAppointmenturi, request, parm, cookie);
		return response;
	}

	/*
	 * Generic method to Book An Appointment with invalid date
	 * 
	 */

	@SuppressWarnings("unchecked")
	public Response bookAppointmentInvalidDate(Response DocumentUploadresponse, Response FetchCentreResponse,
			String preID, String cookie) {
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

				/*
				 * JSONArray data = (JSONArray) resp.get("response"); JSONObject json =
				 * (JSONObject) data.get(0);
				 */
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
				/*
				 * JSONArray objArr = new JSONArray(); objArr.add(object);
				 */
				request.replace(key, object);
				request.put("requesttime", getCurrentDate());

			}
		}
		response = appLib.postWithJson(preReg_BookingAppointmentURI + preID, request, cookie);
		return response;
	}

	/*
	 * Generic method to Fetch Appointment Details
	 * 
	 */
	public Response FetchAppointmentDetails(String preID, String cookie) {
		String preRegFetchAppDet = preReg_FecthAppointmentDetailsURI + preID;
		response = appLib.getWithoutParams(preRegFetchAppDet, cookie);
		return response;
	}

	public Response CancelBookingAppointment(String preID, String cookie) {
		String preReg_CancelAppURI = preReg_CancelAppointmentURI + preID;
		response = appLib.putWithoutData(preReg_CancelAppURI, cookie);
		return response;
	}

	public Response deleteAllDocumentByPreId(String preId, String cookie) {
		String deleteDocumetByPreIdURI = preReg_DeleteAllDocumentByPreIdURI + preId;
		response = appLib.deleteWithoutParams(deleteDocumetByPreIdURI, cookie);
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
		if (!isValidToken(preRegAdminToken)) {
			preRegAdminToken = preRegAdminToken();
		}
		response = appLib.putWithoutData(preReg_ExpiredURI, preRegAdminToken);
		return response;
	}

	public Response logOut(String cookie) {
		try {
			response = appLib.postWithoutJson(invalidateToken_URI, cookie);
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
		if (!isValidToken(preRegAdminToken)) {
			preRegAdminToken = preRegAdminToken();
		}
		response = appLib.putWithoutData(preReg_ConsumedURI, preRegAdminToken);
		return response;
	}

	/*
	 * Generic method to Retrieve All PreId By Registration Center Id
	 * 
	 */

	public Response retriveAllPreIdByRegId(Response fetchAppDet, String preId, String cookie)
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
		logger.info("preReg_RetriveBookedPreIdsByRegIdURI:" + preReg_RetriveBookedPreIdsByRegIdURI);
		response = appLib.getWithQueryParam(preReg_RetriveBookedPreIdsByRegIdURI, parm, cookie);

		return response;
	}

	/*
	 * Generic function to fetch the random registration centerId
	 * 
	 */
	public String randomRegistrationCenterId() {
		Random rand = new Random();
		List<String> givenList = Lists.newArrayList("10002", "10013", "10010", "10015", "10006", "10004",
				"10008", "10012", "10005", "10003", "10007");
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
			} catch (NullPointerException e) {
				Assert.assertTrue(false, "Failed to fetch registration details while booking appointment");
			}
			break;
		}
		return appointmentDetails;
	}

	/*
	 * Generic method to fetch the dynamic request json
	 * 
	 */

	public JSONObject requestJson(String filepath) {

		String configPath = cLib.getResourcePath() + folder + "/" + filepath;
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
		String configPath = cLib.getResourcePath() + folder + "/" + testSuite;
		request = getRequest(testSuite);
		request.put("requesttime", getCurrentDate());
		return request;
	}

	/**
	 * This method compares bytes array response from retrieve pre registration data
	 * with actual demographic data
	 * 
	 * @param retrivePreRegistrationDataResponse @param PrID @param
	 * expectedDemographicDetails @return @throws
	 */
	public boolean validateRetrivePreRegistrationData(Response response, String PrID, Response craeteResponse) {
		boolean finalResult = false;
		HashMap<String, String> expectedDemographicDetails = craeteResponse.jsonPath()
				.get("response.demographicDetails");
		String folderName = "PreRegDocs";
		String systemPath = System.getProperty("user.dir");
		String data = response.jsonPath().get("response.zip-bytes").toString();
		String folder = response.jsonPath().get("response.zip-filename").toString();
		String folderPath = cLib.getResourcePath() + "preReg" + "/" + folderName;
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
		String configPath = cLib.getResourcePath() + folder2 + "/" + "PreRegDocs" + "/" + PrID;
		File folder1 = new File(configPath);
		File[] listOfFiles = folder1.listFiles();
		try {
			for (File f1 : listOfFiles) {

				if (f1.getName().contains("ID")) {
					request = (JSONObject) new JSONParser().parse(new FileReader(f1.getPath()));
				}

				Map<String, Object> actualDemographicDetails = jsonObjectToMap(request);
				finalResult = actualDemographicDetails.keySet().equals(expectedDemographicDetails.keySet());
			}
		} catch (NullPointerException | IOException | ParseException e) {
			Assert.fail("File is not present at specified path ::" + configPath);
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

	/*
	 * get current date in utc format
	 * 
	 */
	public static String getCurrentDate() {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
		String timeStamp = dateFormatGmt.format(Calendar.getInstance().getTime());
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
			Assert.assertTrue(false, "Response Data Mismatch Failure  : difference is : " + diffJson);
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

	public String getDocId(Response documentUploadResponse) {
		String docId = null;
		try {
			docId = documentUploadResponse.jsonPath().get("response.docId").toString();
		} catch (NullPointerException e) {
			Assert.assertTrue(false, "Exception while getting DocId from document Upload Response");
		}
		return docId;

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
	public Response multipleBookApp(Response FetchCentreResponseOne, Response FetchCentreResponseTwo,
			String preIDFirstUsr, String preIDSecondUsr, String cookie) {
		List<String> appointmentDetailsFirstUsr = new ArrayList<>();
		List<String> appointmentDetailsSecondUsr = new ArrayList<>();
		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testSuite = "MultipleBookingAppointment/MultipleBookingAppointment_smoke";
		JSONObject object = null;
		request = getRequest(testSuite);

		appointmentDetailsFirstUsr = getAppointmentDetails(FetchCentreResponseOne);
		appointmentDetailsSecondUsr = getAppointmentDetails(FetchCentreResponseTwo);

		ObjectNode mutBookPreIdFirstUsr = JsonPath.using(config).parse(request.toJSONString())
				.set("$.request.bookingRequest[0].preRegistrationId", preIDFirstUsr).json();

		ObjectNode mutBookAppDateFirstUsr = JsonPath.using(config).parse(mutBookPreIdFirstUsr.toString())
				.set("$.request.bookingRequest[0].appointment_date", appointmentDetailsFirstUsr.get(1)).json();
		ObjectNode mutBookRegCenterIdFirstUsr = JsonPath.using(config).parse(mutBookAppDateFirstUsr.toString())
				.set("$.request.bookingRequest[0].registration_center_id", appointmentDetailsFirstUsr.get(0)).json();

		ObjectNode mutBookAppTimeSlotFromFirstUsr = JsonPath.using(config).parse(mutBookRegCenterIdFirstUsr.toString())
				.set("$.request.bookingRequest[0].time_slot_from", appointmentDetailsFirstUsr.get(2)).json();
		ObjectNode mutBookAppTimeSlotToFirstUsr = JsonPath.using(config)
				.parse(mutBookAppTimeSlotFromFirstUsr.toString())
				.set("$.request.bookingRequest[0].time_slot_to", appointmentDetailsFirstUsr.get(3)).json();

		ObjectNode mutBookPreIdSecondUsr = JsonPath.using(config).parse(mutBookAppTimeSlotToFirstUsr.toString())
				.set("$.request.bookingRequest[1].preRegistrationId", preIDSecondUsr).json();

		ObjectNode mutBookAppDateSecondUsr = JsonPath.using(config).parse(mutBookPreIdSecondUsr.toString())
				.set("$.request.bookingRequest[1].appointment_date", appointmentDetailsSecondUsr.get(1)).json();

		ObjectNode mutBookRegCenterIdSecondUsr = JsonPath.using(config).parse(mutBookAppDateSecondUsr.toString())
				.set("$.request.bookingRequest[1].registration_center_id", appointmentDetailsSecondUsr.get(0)).json();

		ObjectNode mutBookAppTimeSlotFromSecondUsr = JsonPath.using(config)
				.parse(mutBookRegCenterIdSecondUsr.toString())
				.set("$.request.bookingRequest[1].time_slot_from", appointmentDetailsSecondUsr.get(2)).json();

		ObjectNode mutBookAppTimeSlotToSecondUsr = JsonPath.using(config)
				.parse(mutBookAppTimeSlotFromSecondUsr.toString())
				.set("$.request.bookingRequest[1].time_slot_to", appointmentDetailsSecondUsr.get(3)).json();

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
		// response = applnLib.postRequest(multipleBookAppjson, preReg_BookingAppURI);
		response = appLib.postWithJson(preReg_BookingAppURI, multipleBookAppjson, cookie);
		return response;
	}

	/*
	 * Generic method to Multiple BookAn Appointment
	 * 
	 */
	public JSONObject multipleBookAppRequest(Response FetchCentreResponseOne, Response FetchCentreResponseTwo,
			String preIDFirstUsr, String preIDSecondUsr) {
		List<String> appointmentDetailsFirstUsr = new ArrayList<>();
		List<String> appointmentDetailsSecondUsr = new ArrayList<>();
		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		testSuite = "MultipleBookingAppointment/MultipleBookingAppointment_smoke";
		JSONObject object = null;
		request = getRequest(testSuite);

		appointmentDetailsFirstUsr = getAppointmentDetails(FetchCentreResponseOne);
		appointmentDetailsSecondUsr = getAppointmentDetails(FetchCentreResponseTwo);

		ObjectNode mutBookPreIdFirstUsr = JsonPath.using(config).parse(request.toJSONString())
				.set("$.request.bookingRequest[0].preRegistrationId", preIDFirstUsr).json();

		ObjectNode mutBookAppDateFirstUsr = JsonPath.using(config).parse(mutBookPreIdFirstUsr.toString())
				.set("$.request.bookingRequest[0].appointment_date", appointmentDetailsFirstUsr.get(1)).json();
		ObjectNode mutBookRegCenterIdFirstUsr = JsonPath.using(config).parse(mutBookAppDateFirstUsr.toString())
				.set("$.request.bookingRequest[0].registration_center_id", appointmentDetailsFirstUsr.get(0)).json();

		ObjectNode mutBookAppTimeSlotFromFirstUsr = JsonPath.using(config).parse(mutBookRegCenterIdFirstUsr.toString())
				.set("$.request.bookingRequest[0].time_slot_from", appointmentDetailsFirstUsr.get(2)).json();
		ObjectNode mutBookAppTimeSlotToFirstUsr = JsonPath.using(config)
				.parse(mutBookAppTimeSlotFromFirstUsr.toString())
				.set("$.request.bookingRequest[0].time_slot_to", appointmentDetailsFirstUsr.get(3)).json();

		ObjectNode mutBookPreIdSecondUsr = JsonPath.using(config).parse(mutBookAppTimeSlotToFirstUsr.toString())
				.set("$.request.bookingRequest[1].preRegistrationId", preIDSecondUsr).json();

		ObjectNode mutBookAppDateSecondUsr = JsonPath.using(config).parse(mutBookPreIdSecondUsr.toString())
				.set("$.request.bookingRequest[1].appointment_date", appointmentDetailsSecondUsr.get(1)).json();

		ObjectNode mutBookRegCenterIdSecondUsr = JsonPath.using(config).parse(mutBookAppDateSecondUsr.toString())
				.set("$.request.bookingRequest[1].registration_center_id", appointmentDetailsSecondUsr.get(0)).json();

		ObjectNode mutBookAppTimeSlotFromSecondUsr = JsonPath.using(config)
				.parse(mutBookRegCenterIdSecondUsr.toString())
				.set("$.request.bookingRequest[1].time_slot_from", appointmentDetailsSecondUsr.get(2)).json();

		ObjectNode mutBookAppTimeSlotToSecondUsr = JsonPath.using(config)
				.parse(mutBookAppTimeSlotFromSecondUsr.toString())
				.set("$.request.bookingRequest[1].time_slot_to", appointmentDetailsSecondUsr.get(3)).json();

		String multiplBookAppDetStr = mutBookAppTimeSlotToSecondUsr.toString();
		JSONObject multipleBookAppjson = null;
		try {
			multipleBookAppjson = (JSONObject) parser.parse(multiplBookAppDetStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		multipleBookAppjson.put("requesttime", getCurrentDate());
		logger.info("Multiple Book App Res::" + multipleBookAppjson.toString());
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
				// object.put("preRegistrationId", preID);
				/*
				 * JSONArray objArr = new JSONArray(); objArr.add(object);
				 */
				request.replace(key, object);
				request.put("requesttime", getCurrentDate());
			}
		}

		logger.info("Request::Value Of book App::" + request.toString());

		return request;
	}

	@BeforeClass
	public void PreRegistrationResourceIntialize() {
		preReg_CreateApplnURI = cLib.readProperty("IDRepo").get("preReg_CreateApplnURI");
		preReg_DocumentUploadURI = cLib.readProperty("IDRepo").get("preReg_DocumentUploadURI");
		preReg_BookingAppointmentURI = cLib.readProperty("IDRepo").get("preReg_BookingAppointmentURI");
		preReg_DataSyncnURI = cLib.readProperty("IDRepo").get("preReg_DataSyncnURI");
		preReg_FetchRegistrationDataURI = cLib.readProperty("IDRepo").get("preReg_FetchRegistrationDataURI");
		preReg_FecthAppointmentDetailsURI = cLib.readProperty("IDRepo").get("preReg_FecthAppointmentDetailsURI");
		preReg_FetchAllDocumentURI = cLib.readProperty("IDRepo").get("preReg_FetchAllDocumentURI");
		prereg_DeleteDocumentByDocIdURI = cLib.readProperty("IDRepo").get("prereg_DeleteDocumentByDocIdURI");
		preReg_DeleteAllDocumentByPreIdURI = cLib.readProperty("IDRepo").get("preReg_DeleteAllDocumentByPreIdURI");
		preReg_CopyDocumentsURI = cLib.readProperty("IDRepo").get("preReg_CopyDocumentsURI");
		preReg_FetchBookedPreIdByRegIdURI = cLib.readProperty("IDRepo").get("preReg_FetchBookedPreIdByRegIdURI");
		preReg_FetchStatusOfApplicationURI = cLib.readProperty("IDRepo").get("preReg_FetchStatusOfApplicationURI");
		preReg_DiscardApplnURI = cLib.readProperty("IDRepo").get("preReg_DiscardApplnURI");
		preReg_UpdateStatusAppURI = cLib.readProperty("IDRepo").get("preReg_UpdateStatusAppURI");
		preReg_CancelAppointmentURI = cLib.readProperty("IDRepo").get("preReg_CancelAppointmentURI");
		preReg_ExpiredURI = cLib.readProperty("IDRepo").get("preReg_ExpiredURI");
		preReg_ConsumedURI = cLib.readProperty("IDRepo").get("preReg_ConsumedURI");
		preReg_ReverseDataSyncURI = cLib.readProperty("IDRepo").get("preReg_ReverseDataSyncURI");
		preReg_FetchAllApplicationCreatedByUserURI = cLib.readProperty("IDRepo")
				.get("preReg_FetchAllApplicationCreatedByUserURI");
		preReg_DiscardBookingURI = cLib.readProperty("IDRepo").get("preReg_DiscardBookingURI");

		langCodeKey = cLib.readProperty("IDRepo").get("langCode.key");
		otpSend_URI = cLib.readProperty("IDRepo").get("otpSend_URI");
		validateOTP_URI = cLib.readProperty("IDRepo").get("validateOTP_URI");
		preReg_AdminTokenURI = cLib.readProperty("IDRepo").get("preReg_AdminTokenURI");
		preReg_translitrationRequestURI = cLib.readProperty("IDRepo").get("preReg_translitrationRequestURI");
		invalidateToken_URI = cLib.readProperty("IDRepo").get("invalidateToken_URI");
		preReg_GetDocByDocId = cLib.readProperty("IDRepo").get("preReg_GetDocByDocId");
		preReg_CancelAppointmenturi = cLib.readProperty("IDRepo").get("preReg_CancelAppointmenturi");
		preReg_GetPreRegistrationConfigData = cLib.readProperty("IDRepo").get("preReg_GetPreRegistrationConfigData");
		preReg_BookingAppointmenturi = cLib.readProperty("IDRepo").get("preReg_BookingAppointmenturi");
		preReg_syncAvailability = cLib.readProperty("IDRepo").get("preReg_syncAvailability");
		preReg_FecthAppointmentDetailsuri = cLib.readProperty("IDRepo").get("preReg_FecthAppointmentDetailsuri");
		preReg_GetDocByPreId = cLib.readProperty("IDRepo").get("preReg_GetDocByPreId");
		qrCode_URI = cLib.readProperty("IDRepo").get("qrCode_URI");
		preReg_DocUploadURI = cLib.readProperty("IDRepo").get("preReg_DocUploadURI");
		preReg_RetriveBookedPreIdsByRegId = preRegUtil.fetchPreregProp().get("preReg_RetriveBookedPreIdsByRegId");
		QRCodeFilePath = preRegUtil.fetchPreregProp().get("QRCodeFilePath");
		preReg_NotifyURI = preRegUtil.fetchPreregProp().get("preReg_NotifyURI");
		preReg_SyncMasterDataURI = preRegUtil.fetchPreregProp().get("preReg_SyncMasterDataURI");
		preReg_FetchCenterIDURI = preRegUtil.fetchPreregProp().get("preReg_FetchCenterIDURI");
		preReg_MultipleBookAppURI = preRegUtil.fetchPreregProp().get("preReg_MultipleBooking");
	}

}
