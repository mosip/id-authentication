package io.mosip.preregistration.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.GetHeader;
import io.restassured.response.Response;

/**
 * @author Lavanya R
 *
 */

public class Expired extends BaseTestCase {
	static final String folder = "preReg";
	static String testSuite = "";
	static Response createPregResponse;
	static JSONObject createPregRequest;
	static Response response;
	static JSONObject request;
	static JSONObject request1;
	static String preReg_Id = "";
	JSONParser parser = new JSONParser();
	static ApplicationLibrary applnLib = new ApplicationLibrary();
	// private static Logger logger =
	// Logger.getLogger(PreRegistrationLibrary.class);
	private static final String preReg_URI = "/demographic/v0.1/pre-registration/applications";
	private static final String retrivePreRegistrationData_URI = "/datasync/v0.1/pre-registration/data-sync/datasync";
	private static final String documentUpload_URI = "/document/v0.1/pre-registration/documents";
	private static final String getPreReg_URI = "/demographic//v0.1/pre-registration/applicationData";
	private static final String getCenterID_URI = "/booking/v0.1/pre-registration/booking/availability";
	private static final String bookAppointment_URI = "/booking/v0.1/pre-registration/booking/book";
	private static final String fecthAppointmentDetails_URI = "/booking/v0.1/pre-registration/booking/appointmentDetails";
	private static final String cancelBookAppointment_URI = "/booking/v0.1/pre-registration/booking/book";
	private static final String getAllDocument_URI = "/document/v0.1/pre-registration/getDocument";
	private static final String deleteDocumentByDocId_URI = "/document/v0.1/pre-registration/deleteDocument";
	private static final String deleteAllDocumentByPreId_URI = "/document/v0.1/pre-registration/deleteAllByPreRegId";
	private static final String copyDocuments_URI = "/document/v0.1/pre-registration/copyDocuments";
	private static final String bookedPreIdByRegId_URI = "/booking/v0.1/pre-registration/booking/bookedPreIdsByRegId";
	private static final String fetchAllPreRegistrationCreatedByUser_URI = "/demographic/v0.1/pre-registration/applications";
	private static final String getPreRegistrationStatus_URI = "/demographic/v0.1/pre-registration/applicationStatus";
	private static Logger logger = Logger.getLogger(BaseTestCase.class);
	/*
	 * We configure the jsonProvider using Configuration builder.
	 */
	Configuration config = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider()).build();

	/*
	 * Generic method to Create Pre-Registration Application
	 * 
	 */
	public Response CreatePreReg() throws FileNotFoundException, IOException, ParseException {
		testSuite = "Pre_Registration\\smokePreReg1";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				createPregRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

			}
		}
		createPregResponse = applnLib.postRequest(createPregRequest, preReg_URI);
		preReg_Id = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Assert.assertTrue(preReg_Id != null);
		return createPregResponse;
	}

	public Response CreatePreReg(JSONObject createRequest) {
		try {
			createPregResponse = applnLib.postRequest(createRequest.toJSONString(), preReg_URI);
		} catch (Exception e) {
			logger.info(e);
		}
		return createPregResponse;
	}

	public int createdBy() {
		Random rand = new Random();
		int num = rand.nextInt(9000000) + 1000000000;
		return num;

	}

	/**
	 * @author ASHISH. RASTOGI method for discarding application
	 * @param response
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public Response discardApplication(String PreRegistrationId) {
		testSuite = "Discard_Individual\\TC_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
			return applnLib.deleteRequest(preReg_URI, request);

		} catch (Exception e) {
			logger.info(e);
		}
		return null;
	}

	/**
	 * @author ASHISH. RASTOGI method for fetchAllPreRegistrationCreatedByUser
	 * @param userId
	 * @return
	 */
	public Response fetchAllPreRegistrationCreatedByUser(String userId) {
		testSuite = "Fetch_all_application_created_by_user\\Fetch_all_application_created_by_user_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
			response = applnLib.getRequest(fetchAllPreRegistrationCreatedByUser_URI, GetHeader.getHeader(request));
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	/**
	 * Re Book Appointment Details
	 * 
	 * @param fetchCenterResponseF
	 * @param date
	 * @return
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

	/**
	 * @author ASHISH. RASTOGI Method for retriving pre registration data
	 * @param preRegistrationId
	 * @return
	 */
	public Response retrivePreRegistrationData(String preRegistrationId) {
		testSuite = "Retrive_PreRegistration\\Retrive Pre registration data_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
			response = applnLib.getRequest(retrivePreRegistrationData_URI, GetHeader.getHeader(request));
		} catch (Exception e) {
			logger.info(e);
		}
		return response;
	}

	/**
	 * @author ASHISH. RASTOGI method for reading request from Specified folder
	 * @param suite
	 * @param folder
	 * @return
	 */
	public JSONObject readRequest(String suite, String folder) {
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		System.out.println("folder name" + configPath);
		File folder1 = new File(configPath);
		File[] listOfFiles = folder1.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request1 = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
		return request1;
	}

	/**
	 * @author ASHISH. RASTOGI method for getPreRegistrationStatus
	 * @param userId
	 * @return
	 */
	public Response getPreRegistrationStatus(String preRegistartionId) {
		testSuite = "Fetch_the_status_of_a_application\\Fetch_application_status_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
		System.out.println("=========request++++" + request.toString());
		request.put("pre_registration_id", preRegistartionId);
		try {
			response = applnLib.getRequest(getPreRegistrationStatus_URI, GetHeader.getHeader(request));
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
		testSuite = "DocumentUpload\\DocumentUpload_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File file = new File(configPath + "\\ProofOfAddress.PDF");
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
			response = applnLib.putFileAndJson(documentUpload_URI, request, file);
		} catch (Exception e) {
		}
		response = applnLib.putFileAndJson(documentUpload_URI, request, file);
		return response;
	}

	/*
	 * Generic method to Upload Document
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response documentUpload(Response responseCreate, String fileName) {
		testSuite = "DocumentUpload\\DocumentUpload_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File file = new File(configPath + "\\" + fileName + ".PDF");
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
			response = applnLib.putFileAndJson(documentUpload_URI, request, file);
		} catch (Exception e) {
		}
		response = applnLib.putFileAndJson(documentUpload_URI, request, file);
		return response;
	}

	/**
	 * Method to get registration data
	 * 
	 * @param PreRegistrationId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Response getPreRegistrationData(String PreRegistrationId) {
		testSuite = "Get_Pre_Registartion_data\\Get_Pre_Registartion_data_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
			// return applnLib.DeleteRequest(getpreReg_URI, request);
			response = applnLib.getRequest(getPreReg_URI, GetHeader.getHeader(request));
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
	public Response updatePreReg(String preRegID, String updatedBy)
			throws FileNotFoundException, IOException, ParseException {
		testSuite = "Pre_Registration\\smokePreReg1";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().contains("request")) {
				createPregRequest = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

			}
		}
		System.out.println("Pre Reg Request::" + createPregRequest);
		JSONObject object = null;
		for (Object key : createPregRequest.keySet()) {
			if (key.equals("request")) {
				object = (JSONObject) createPregRequest.get(key);
				object.put("preRegistrationId", preRegID);
				object.put("updatedBy", updatedBy);
				object.put("createdBy", updatedBy);
				object.put("updatedDateTime", "2019-01-08T17:05:48.953Z");
				createPregRequest.replace(key, object);
			}
		}
		logger.info("Request for update---------" + createPregRequest.toString());
		createPregResponse = applnLib.postRequest(createPregRequest, preReg_URI);
		preReg_Id = createPregResponse.jsonPath().get("response[0].preRegistrationId").toString();
		Assert.assertTrue(preReg_Id != null);
		return createPregResponse;
	}

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

		testSuite = "GetAllDocumentForPreRegId\\GetAllDocumentForPreRegId_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
			response = applnLib.getRequest(getAllDocument_URI, GetHeader.getHeader(getAlldocJson));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Generic method to Delete All Document by Pre-RegistrationId
	 * 
	 */
	public Response deleteAllDocumentByPreId(String preId) throws FileNotFoundException, IOException, ParseException {

		testSuite = "DeleteAllDocumentsByPreRegID\\DeleteAllDocumentForPreRegId_smoke";

		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

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
		JSONObject delDocByPreId = (JSONObject) parser.parse(delDocForPreId);
		response = applnLib.deleteRequest(deleteAllDocumentByPreId_URI, GetHeader.getHeader(delDocByPreId));
		return response;
	}
	/*
	 * Generic method to Delete All Document by Document Id
	 * 
	 */

	public Response deleteAllDocumentByDocId(String documentId)
			throws FileNotFoundException, IOException, ParseException {

		testSuite = "DeleteDocumentByDocId\\DeleteDocumentByDocId_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

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
		JSONObject delDocByDocIdRes = (JSONObject) parser.parse(delDocForDocId);

		response = applnLib.deleteRequest(deleteDocumentByDocId_URI, GetHeader.getHeader(delDocByDocIdRes));

		return response;
	}

	/*
	 * Generic method to Copy uploaded document from One Pre-Registration Id to
	 * another Pre-Registration Id
	 * 
	 */

	public Response copyUploadedDocuments(String sourcePreId, String destPreId) {
		testSuite = "CopyUploadedDocument\\CopyUploadedDocument_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
			response = applnLib.postModifiedGetRequest(copyDocuments_URI, GetHeader.getHeader(copyDocRes));
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
		testSuite = "FetchAvailabilityDataOfRegCenters\\FetchAvailabilityDataOfRegCenters_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				try {
					request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			GetHeader.getHeader(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			response = applnLib.getRequest(getCenterID_URI, GetHeader.getHeader(request));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	/**
	 * get Date Method
	 */
	public String getDate(int no)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, no); // Adding 5 days
		String date = sdf.format(c.getTime());
		return date;
	}
	/**
	 * Generic method to Book Expired Appointment
	 * @param DocumentUploadresponse
	 * @param FetchCentreResponse
	 * @param preID
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public Response BookExpiredAppointment(Response DocumentUploadresponse, Response FetchCentreResponse, String preID) {
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
			if (key.toString().toLowerCase().contains("request")) {
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
		response = applnLib.postRequest(request, bookAppointment_URI);
		return response;
	}

	/**
	 *Expired Appointment details appointment
	 * 
	 * @param fetchCenterResponse
	 * @return
	 */
	public List<String> getExpiredAppointmentDetails(Response fetchCenterResponse) {

		List<String> appointmentDetails = new ArrayList<>();
		String date = getDate(-1);

		int countCenterDetails = fetchCenterResponse.jsonPath().getList("response.centerDetails").size();
			fetchCenterResponse.jsonPath().get("response.centerDetails[0].timeSlots[5].fromTime");
			appointmentDetails.add(fetchCenterResponse.jsonPath().get("response.regCenterId").toString());
			appointmentDetails
					.add(date);
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[0].timeSlots[5].fromTime").toString());
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[0].timeSlots[5].toTime").toString());
		return appointmentDetails;
	}

	/*
	 * Generic method to Fetch Appointment Details
	 * 
	 */

	public Response FetchAppointmentDetails(String preID) {
		testSuite = "FetchAppointmentDetails\\FetchAppointmentDetails_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
			response = applnLib.getRequest(fecthAppointmentDetails_URI, GetHeader.getHeader(fetchAppjson));
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
		testSuite = "CancelAnBookedAppointment\\CancelAnBookedAppointment_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
				.set("$.request.registration_center_id ", FetchAppDet.jsonPath().get("response.registration_center_id").toString())
				.json();
		ObjectNode cancelAppDate = JsonPath.using(config).parse(cancelAppRegCenterId.toString())
				.set("$.request.appointment_date",
						FetchAppDet.jsonPath().get("response.appointment_date").toString())
				.json();
		ObjectNode cancelAppTimeSlotFrom = JsonPath.using(config).parse(cancelAppDate.toString())
				.set("$.request.time_slot_from",
						FetchAppDet.jsonPath().get("response.time_slot_from").toString())
				.json();
		ObjectNode cancelAppRequest = JsonPath.using(config).parse(cancelAppTimeSlotFrom.toString())
				.set("$.request.time_slot_to",
						FetchAppDet.jsonPath().get("response.time_slot_to").toString())
				.json();

		String cancelAppDetStr = cancelAppRequest.toString();
		JSONObject cancelAppjson = null;
		try {
			cancelAppjson = (JSONObject) parser.parse(cancelAppDetStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		response = applnLib.putRequest_WithBody(cancelBookAppointment_URI, cancelAppjson);
		return response;
	}

	/*
	 * Generic method to Cancel Booking Appointment Details
	 * 
	 */

	public Response ReBookAnAppointment(String preID, Response FetchAppDet, Response FetchCentreResponse) {
		testSuite = "ReBookAnAppointment\\ReBookAnAppointment_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
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
		System.out.println(dateFormat.format(c1.getTime()));
		List<String> details = reBookGetAppointmentDetails(FetchCentreResponse, date);
		String regCenterId = details.get(0);
		String appDate = details.get(1);
		String timeSlotFrom = details.get(2);
		String timeSlotTo = details.get(3);
	System.out.println(regCenterId);
	System.out.println(appDate);
	System.out.println(timeSlotFrom);
	System.out.println(timeSlotTo);
		ObjectNode rebookNewAppRegCenterId = JsonPath.using(config).parse(rebookTimeSlotTo.toString())
				.set("$.request[0].newBookingDetails.registration_center_id",
						regCenterId)
				.json();
		ObjectNode rebookNewAppointmentAppDate = JsonPath.using(config).parse(rebookNewAppRegCenterId.toString())
				.set("$.request[0].newBookingDetails.appointment_date",
						appDate)
				.json();
		ObjectNode rebookNewAppointmentTimeSlotFrom = JsonPath.using(config)
				.parse(rebookNewAppointmentAppDate.toString())
				.set("$.request[0].newBookingDetails.time_slot_from", timeSlotFrom)
				.json();
		ObjectNode rebookNewAppointmentTimeSlotTo = JsonPath.using(config)
				.parse(rebookNewAppointmentTimeSlotFrom.toString())
				.set("$.request[0].newBookingDetails.time_slot_to",
						timeSlotTo)
				.json();

		String rebookApp = rebookNewAppointmentTimeSlotTo.toString();
		JSONObject rebookAppjson = null;
		try {
			rebookAppjson = (JSONObject) parser.parse(rebookApp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		response = applnLib.postRequest(rebookAppjson, bookAppointment_URI);
		return response;
	}

	/*
	 * Generic method to Retrieve All PreId By Registration Center Id
	 * 
	 */
	public Response retriveAllPreIdByRegId() throws FileNotFoundException, IOException, ParseException {
		testSuite = "RetrivePreIdByRegCenterId\\RetrivePreIdByRegCenterId_smoke";
		String configPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folder + "\\" + testSuite;
		File folder = new File(configPath);
		File[] listOfFiles = folder.listFiles();
		JSONObject object = null;
		for (File f : listOfFiles) {
			if (f.getName().toLowerCase().contains("request")) {
				request = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));

			}
		}
		JSONObject retriveAllPreIdByRegIdjson = (JSONObject) parser.parse(request.toString());
		response = applnLib.postRequest(retriveAllPreIdByRegIdjson, bookedPreIdByRegId_URI);
		return response;
	}

	public String randomRegistrationCenterId() {
		return null;
	}

	@Test
	public void runTest() throws FileNotFoundException, IOException, ParseException {

		Response response = documentUpload(CreatePreReg());

		/*
		 * String
		 * srcPreID=response.jsonPath().get("response[0].preRegistrationId").toString();
		 * 
		 * Response resGetAllDoc = getAllDocumentForPreId(srcPreID);
		 */

	}

}
