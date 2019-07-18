package io.mosip.preregistration.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import io.mosip.dao.PreRegistrationDAO;
import io.mosip.restassured.RestAssuredMethod;
import io.restassured.response.Response;


@SuppressWarnings("deprecation")
public class PreRegistartionUtil {

	
	RestAssuredMethod restMethod = new RestAssuredMethod();
	PreRegistrationDAO dao=new PreRegistrationDAO();
	Properties prop = new Properties();
	public static SessionFactory factory;
	static Session session;
	String userId;

	/**
	 * Method for creating an application
	 * 
	 * @param request-Demographic
	 *            details of the applicant
	 * @param url-Url
	 *            to create an application
	 * @param token
	 * @return-Response after creating application
	 */
	public Response createApplication(JSONObject request, String token) {
		String craeteApplicationURI = getProperty().get("craeteApplicationURI");
		request.put("requesttime", getCurrentDate());
		
		return restMethod.postRequestWithToken(request, craeteApplicationURI, token);
	}

	/**
	 * Method to upload document
	 * 
	 * @param responseCreate
	 * @param testSuite
	 * @param request
	 * @param preReg_DocumentUploadURI
	 * @param authToken
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Response documentUpload(Response responseCreate, JSONObject request, String authToken,
			String documentName) {
		List<String> documents=new ArrayList<>();
		documents.add("POA_birth.pdf");
		Response response = null;
		String preReg_DocumentUploadURI = getProperty().get("documentUploadURI");
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + "preReg" + "/" + "documents";
		
			File file = new File(configPath + "/" + documentName+".pdf");
			HashMap<String, String> parm = new HashMap<>();
			String preRegistrationId = responseCreate.jsonPath().get("response.preRegistrationId").toString();
			parm.put("preRegistrationId", preRegistrationId);
			request.put("requesttime", getCurrentDate());
			response = restMethod.putFileAndJsonWithParm(preReg_DocumentUploadURI, request, file, parm, authToken);
			return response;
	}

	/*
	 * Generic function to fetch the random registration centerId
	 * 
	 */
	public String randomRegistrationCenterId() {
		Random rand = new Random();
		List<String> givenList = new ArrayList<String>();
		givenList.add("10002");
		givenList.add("10003");
		givenList.add("10001");
		givenList.add("10004");
		givenList.add("10005");
		givenList.add("10006");
		givenList.add("10007");
		givenList.add("10008");
		givenList.add("10009");
		givenList.add("10010");
		givenList.add("10011");
		givenList.add("10012");
		givenList.add("10013");
		givenList.add("10014");
		givenList.add("10015");
		String s = null;
		int numberOfElements = givenList.size();
		for (int i = 0; i < numberOfElements; i++) {
			int randomIndex = rand.nextInt(givenList.size());
			s = givenList.remove(randomIndex);
		}
		return s;

	}

	/**
	 * this method will book appointmnet for child
	 * @return
	 */
	public String getPrIdOfChild(String childRequestParameters)
	{
		String token = getToken();
		JSONObject childRequest = getRequest(childRequestParameters);
		Response craeteReponse = createApplication(childRequest, token);
		String preID = craeteReponse.jsonPath().get("response.preRegistrationId").toString();
		List<String> documents=new ArrayList<>();
		documents.add("Rental contract");
		documents.add("CNIE card");
		documents.add("Certificate of Relationship");
		for(String document:documents)
		{
			JSONObject documentRequest = getRequest("documents/"+document);
			documentUpload(craeteReponse, documentRequest, token,document);
		}
		Response fetchCenterResponse = FetchCentre(token);
		BookAppointment(fetchCenterResponse, preID, token);
		return preID;
	}
	public void bookappointment(int noOfAppointment)
	{
		for(int i=1;i<=noOfAppointment;i++)
		{
			String token = getToken();
			JSONObject childRequest = getRequest("adultRequest");
			Response craeteReponse = createApplication(childRequest, token);
			String preID = craeteReponse.jsonPath().get("response.preRegistrationId").toString();
			Response fetchCenterResponse = FetchCentre(token);
			BookAppointment(fetchCenterResponse, preID, token);
		}
	
	}
	public void bookExpiredappointment(int noOfAppointment)
	{
		for(int i=1;i<=noOfAppointment;i++)
		{
			String token = getToken();
			JSONObject childRequest = getRequest("adultRequest");
			Response craeteReponse = createApplication(childRequest, token);
			String preID = craeteReponse.jsonPath().get("response.preRegistrationId").toString();
			Response fetchCenterResponse = FetchCentre(token);
			BookAppointment(fetchCenterResponse, preID, token);
			dao.setDate(preID);
		}
	
	}
	/**
	 * this method will book appointment for adult
	 * @return
	 */
	public String getPrIdOfAdult(String adultRequestParam)
	{
		String token = getToken();
		JSONObject adultRequest = getRequest(adultRequestParam);
		Response craeteReponse = createApplication(adultRequest, token);
		String preID = craeteReponse.jsonPath().get("response.preRegistrationId").toString();
		List<String> documents=new ArrayList<>();
		documents.add("Rental contract");
		documents.add("CNIE card");
		for(String document:documents)
		{
			JSONObject documentRequest = getRequest("documents/"+document);
			documentUpload(craeteReponse, documentRequest, token,document);
		}
		Response fetchCenterResponse = FetchCentre(token);
		BookAppointment(fetchCenterResponse, preID, token);
		return preID;
	}
	/**
	 * this method will book appointment for child without uploading document
	 * @return
	 */
	public String getPrIdOfChildWithoutDocs(String childRequestParam)
	{
		String token = getToken();
		JSONObject childRequest = getRequest(childRequestParam);
		Response craeteReponse = createApplication(childRequest, token);
		String preID = craeteReponse.jsonPath().get("response.preRegistrationId").toString();
		Response fetchCenterResponse = FetchCentre(token);
		BookAppointment(fetchCenterResponse, preID, token);
		return preID;
	}
	/**
	 * this method will book appointment for adult without uploading document
	 * @return
	 */
	public String getPrIdOfAdultWithoutDocs(String adultRequestParam)
	{
		String token = getToken();
		JSONObject adultRequest = getRequest(adultRequestParam);
		Response craeteReponse = createApplication(adultRequest, token);
		String preID = craeteReponse.jsonPath().get("response.preRegistrationId").toString();
		Response fetchCenterResponse = FetchCentre(token);
		BookAppointment(fetchCenterResponse, preID, token);
		return preID;
	}



	/**
	 * Availability data to book an appointment
	 * 
	 * @param preReg_FetchCenterIDURI
	 * @param authToken
	 * @return
	 */
	public Response FetchCentre(String authToken) {
		String regCenter = randomRegistrationCenterId();
		String preReg_FetchCenterIDURI = getProperty().get("fetchCenterIdURI");
		String preRegFetchCenterIDURI = preReg_FetchCenterIDURI + "10001";
		Response response = restMethod.getRequestWithoutParm(preRegFetchCenterIDURI, authToken);
		return response;
	}

	/**
	 * To Read Request From folder
	 * 
	 * @param testSuite-name
	 *            of the suite where request is stored
	 * @return
	 */
	public static JSONObject getRequest(String testSuite) {
		JSONObject request = null;
		/**
		 * Reading request body from configpath
		 */
		String configPath = System.getProperty("user.dir")+"/src/test/resources/" + "preReg" + "/" + testSuite;
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
		return request;
	}

	/**
	 * Get current time stamp
	 * 
	 * @return time stamp
	 */
	public String getCurrentDate() {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
				.format(Calendar.getInstance().getTime());
		return timeStamp;
	}

	/**
	 * Get OTP request
	 * 
	 * @param testSuite
	 * @return otpRequest
	 */

	@SuppressWarnings("unchecked")
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
	 * ValidateRequest
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject validateOTPRequest(String testSuite, String otp) {
		JSONObject otpRequest = null;
		/**
		 * Reading request body from configpath
		 */
		otpRequest = getRequest("validateOTP/validateOTP_smoke");
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
	/**
	 * Get Auth Token after validating user
	 * 
	 * @return
	 */

	@SuppressWarnings("unused")
	public String getToken() {
		String testSuite = "generateOTP/generateOTP_smoke";
		JSONObject request = otpRequest(testSuite);
		String sendOtpURI = getProperty().get("sendOtpURI");
		String validateOtpURI = getProperty().get("validateOtpURI");
		Response generateOTPResponse = restMethod.postRequestWithOutToken(request, sendOtpURI);
		String otpQueryStr = "SELECT E.otp FROM kernel.otp_transaction E WHERE id='" + userId + "'";
		 List<String> otpData = dao.getOTP(userId);
		String otp = otpData.get(0).toString();
		testSuite = "validateOTP/validateOTP_smoke";
		request = validateOTPRequest(testSuite, otp);
		Response validateOTPRes = restMethod.postRequestWithOutToken(request, validateOtpURI);
		String cookieValue = validateOTPRes.getCookie("Authorization");
		return cookieValue;
	}

	/**
	 * Method to Book an appointment
	 * 
	 * @param FetchCentreResponse-center
	 *            details to book appointment
	 * @param preID
	 * @param preReg_BookingAppointmenturi
	 * @param authToken
	 * @return
	 */
	public Response BookAppointment(Response FetchCentreResponse, String preID, String authToken) {
		List<String> appointmentDetails = new ArrayList<>();
		String bookingAppointmentURI = getProperty().get("bookingAppointmentURI");
		String regCenterId = null;
		String appDate = null;
		String timeSlotFrom = null;
		String timeSlotTo = null;
		String testSuite = "BookingAppointment";
		JSONObject object = null;
		JSONObject request = getRequest(testSuite);
		for (Object key : request.keySet()) {
			if (key.toString().toLowerCase().equals("request")) {
				object = new JSONObject();
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
		HashMap<String, String> parm = new HashMap<>();
		parm.put("preRegistrationId", preID);
		Response response = restMethod.postRequestWithParm(request, bookingAppointmentURI, parm, authToken);
		return response;
	}

	/**
	 * Get appointment details to book appointment
	 * 
	 * @param fetchCenterResponse
	 * @return
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
	/**
	 * Method to read property file
	 * @return
	 */
	public Map<String, String> getProperty() {
		try {
			FileInputStream fi=new FileInputStream(new File(System.getProperty("user.dir")+"/src/config/preRegApi.properties"));
			prop.load(fi);
			fi.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Map<String, String> mapProp = prop.entrySet().stream()
				.collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));

		return mapProp;

	}
	/**
	 * Auth token for regClient
	 * @return-reg client auth token
	 */
	public String regClientAdminToken() {
		JSONObject regClientAdminTokenRequest = null;
		String testSuite = "regClientAdminToken";
		String authTokenURI = getProperty().get("authTokenURI");
		/**
		 * Reading request body from configpath
		 */
		regClientAdminTokenRequest = getRequest(testSuite);
		Response response = null;
		try {
			response = restMethod.postRequestWithOutToken(regClientAdminTokenRequest.toJSONString(), authTokenURI);
		} catch (Exception e) {
			
		}
		String cookieValue = response.getCookie("Authorization");
		String auth_token = cookieValue;
		return auth_token;
	}
	/**
	 * Methode to get pre registration data
	 * @param preRegistrationId
	 * @return return pre registration data in byte array format
	 */
	public Response retrivePreRegistrationData(String preRegistrationId) {
		HashMap<String, String> parm=new HashMap<>();
		String authToken = regClientAdminToken();
		String dataSyncURI = getProperty().get("dataSyncURI");
		parm.put("preRegistrationId", preRegistrationId);
		Response response = null;
		try {
			response = restMethod.getRequestDataSync(dataSyncURI, parm,authToken);
		} catch (Exception e) {
			
		}
		return response;
	}
	/**
	 * Storing retrive pre registration data into folder
	 * @param PrID
	 * @return
	 */
	public void storePreRegistrationData(String PrID) {
		boolean finalResult = false;
		Response response = retrivePreRegistrationData(PrID);
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
	}

}
