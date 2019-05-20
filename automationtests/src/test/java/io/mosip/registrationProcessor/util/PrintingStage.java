package io.mosip.registrationProcessor.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.mosip.dbaccess.RegProcStageDb;
import io.mosip.dbdto.JsonFileDTO;
import io.mosip.dbdto.JsonRequestDTO;
import io.mosip.dbdto.PrintQueueDTO;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CbeffImpl;
import io.mosip.util.CbeffToBiometricUtil;
import io.mosip.util.CbeffUtil;
import io.mosip.util.CryptoUtil;
import io.mosip.util.DateUtils;
import io.mosip.util.JsonUtil;
import io.mosip.util.JsonValue;
import io.mosip.util.MosipActiveMq;
import io.mosip.util.MosipActiveMqImpl;
import io.mosip.util.MosipQueue;
import io.mosip.util.MosipQueueConnectionFactory;
import io.mosip.util.MosipQueueConnectionFactoryImpl;
import io.mosip.util.MosipQueueManager;
import io.mosip.util.PrintPostServiceImpl;
import io.mosip.util.QrCodeGenerator;
import io.mosip.util.QrVersion;
import io.mosip.util.QrcodeGeneratorImpl;
import io.mosip.util.TemplateGenerator;
import io.mosip.util.UINCardConstant;
import io.mosip.util.UinCardGenerator;
import io.mosip.util.UinCardGeneratorImpl;
import io.mosip.util.UinCardType;
import io.mosip.util.Utilities;
import io.restassured.response.Response;

public class PrintingStage extends BaseTestCase{
	private static Logger logger = Logger.getLogger(PrintingStage.class);
	boolean isPrintingStageValidated = false;
	private String username = "admin";
	private String password = "admin";
	private String url = "tcp://104.211.200.46:61616";
	private String typeOfQueue = "ACTIVEMQ";
	private String address = "print-service-qa";
	Map<String, Object> attributes = new LinkedHashMap<>();
	String primaryLang= "ara" ;
	String secondaryLang = "fra";
	MosipQueueManager<MosipQueue, byte[]> mosipQueueManager = new MosipActiveMqImpl();

	public boolean validatePrintingStage(String rid) throws IOException{
		boolean isPrintingStageValidated =  false;
		RegProcStageDb dbData= new RegProcStageDb();
		String uin = dbData.regproc_getUIN(rid);
		Response actualResponse = null;
		ApplicationLibrary applicationLibrary = new ApplicationLibrary();
		String id_url = "/v1/idrepo/identity";
		Map<String, byte[]> byteMap = new HashMap<>();



		if(uin == null){
			isPrintingStageValidated = false;
		}else{
			HashMap<String, String> type_new = new HashMap<>();
			type_new.put("type", "all");
			HashMap<String, String> uin_new = new HashMap<>();
			uin_new.put("uin", uin);
			String id_url_path = "/v1/idrepo/identity/"+uin;

			//get document
			actualResponse = applicationLibrary.getRequest(id_url_path, type_new);
			//	actualResponse = applicationLibrary.getRequestPathQueryPara(id_url, uin_new, type_new);
			logger.info("Actual response : "+actualResponse.asString());
			Map<String,Map<String,String>> response = actualResponse.jsonPath().get("response"); 
			logger.info("response  : "+response);

			//set applicant photograph
			boolean isPhotoset = setApplicantPhoto(response);
			if(!isPhotoset){
				isPrintingStageValidated = false;
				logger.info("Photo is not set");
			}

			//creating template
			if (response!= null) {

				for (Map.Entry<String, Map<String,String>> entry : response.entrySet()) {
					//	logger.info(entry.getKey() + "/" + entry.getValue());
					if(entry.getKey().contains("identity")){
						Map<String, String> values =  entry.getValue();
						ObjectMapper mapperObj = new ObjectMapper();
						String jsonResp = mapperObj.writeValueAsString(values);
						logger.info("jsonResp : "+jsonResp);
						setTemplateAttributes(jsonResp,attributes);
					}
				}
			}
			attributes.put("UIN", uin);
			logger.info("attributes : "+attributes.toString());

			//creating text file
			byte[] textFileByte = createTextFile();
			byteMap.put("textFile", textFileByte);

			boolean isQRcodeSet = setQrCode(textFileByte);
			if(!isQRcodeSet){
				isPrintingStageValidated = false;
				logger.info("qr code not set");
			}

			// getting template and placing original values
			TemplateGenerator templateGenerator = new TemplateGenerator();
			InputStream uinArtifact = templateGenerator.getTemplate("RPR_UIN_CARD_TEMPLATE", attributes, primaryLang);
			if (uinArtifact == null) {
				isPrintingStageValidated = false;
			}else
				logger.info("template generated............");

			// generating pdf
			UinCardGenerator<ByteArrayOutputStream> uinCardGenerator = new UinCardGeneratorImpl();
			ByteArrayOutputStream pdf = uinCardGenerator.generateUinCard(uinArtifact, UinCardType.PDF);

			byte[] pdfbytes = pdf.toByteArray();
			byteMap.put("uinPdf", pdfbytes);

			byte[] uinbyte = attributes.get("UIN").toString().getBytes();
			byteMap.put("UIN", uinbyte);

			//create active mq connection
			MosipQueueConnectionFactory mosipConnectionFactory = new MosipQueueConnectionFactoryImpl();
			MosipQueue queue = (MosipQueue) mosipConnectionFactory.createConnection(typeOfQueue, username, password, url);

			if(queue == null){
				isPrintingStageValidated = false;
				logger.info("Conenction not created...");
			}

			boolean isAddedToQueue = sendToQueue(queue, byteMap, 0, uin);
			
			if (isAddedToQueue) {
				isPrintingStageValidated = true;
				logger.info("Pdf added to the mosip queue for printing");
				
			} else {
				isPrintingStageValidated = false;
				logger.info("Pdf was not added to queue due to queue failure");
			}
			PrintPostServiceImpl printPostService = new PrintPostServiceImpl();
			printPostService.generatePrintandPostal(rid, queue, mosipQueueManager);
			
			if (consumeResponseFromQueue(rid, queue)) {
				logger.info("Print and Post Completed for the regId : " + rid);
			} else {
				logger.info("Re-Send uin card with regId " + rid + " for printing");		
			}
		}
		//	logger.info("uin : "+uin);
		return isPrintingStageValidated;

	}

	private boolean sendToQueue(MosipQueue queue, Map<String, byte[]> byteMap, int count, String uin) {
		boolean isAddedToQueue = false;
		try {
			PrintQueueDTO queueDto = new PrintQueueDTO();
			queueDto.setPdfBytes(byteMap.get("uinPdf"));
			queueDto.setTextBytes(byteMap.get("textFile"));
			queueDto.setUin(uin);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(queueDto);
			oos.flush();
			byte[] printQueueBytes = bos.toByteArray();
			MosipQueueManager<MosipQueue, byte[]> mosipQueueManager = null;
			isAddedToQueue = mosipQueueManager.send(queue, printQueueBytes, address);

		
			if (count < 5) {
				sendToQueue(queue, byteMap, count + 1, uin);
			} else {
				logger.error("count is more than 5..");
				isAddedToQueue = false;
			}
		} catch (Exception e) {
			
		}
		return isAddedToQueue;
		
	}
	
	
	
	private boolean consumeResponseFromQueue(String regId, MosipQueue queue) {
		boolean result = false;

		// Consuming the response from the third party service provider
		
		byte[] responseFromQueue = mosipQueueManager.consume(queue, "postal-service");
		String response = new String(responseFromQueue);
		JSONParser parser = new JSONParser();
		JSONObject identityJson = null;
		try {
			identityJson = (JSONObject) parser.parse(response);
			String uinFieldCheck = (String) identityJson.get("Status");
			if (uinFieldCheck.equals("Success")) {
				result = true;
			}
		} catch (ParseException e) {
			logger.error("parse exception ",e);
		}
		return result;
	}

	private boolean setQrCode(byte[] textFileByte) {
		String qrString = new String(textFileByte);
		boolean isQRCodeSet = false;
		QrCodeGenerator<QrVersion> qrCodeGenerator = new QrcodeGeneratorImpl();
		byte[] qrCodeBytes = qrCodeGenerator.generateQrCode(qrString, QrVersion.V30);
		if (qrCodeBytes != null) {
			String imageString = CryptoUtil.encodeBase64String(qrCodeBytes);
			attributes.put("QrCode", "data:image/png;base64," + imageString);
			isQRCodeSet = true;
		}
		return isQRCodeSet;
	}

	private byte[] createTextFile() {
		JsonFileDTO jsonDto = new JsonFileDTO();
		jsonDto.setId("mosip.registration.print.send");
		jsonDto.setVersion("1.0");
		jsonDto.setRequestTime(DateUtils.getUTCCurrentDateTimeString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

		JsonRequestDTO request = new JsonRequestDTO();
		request.setNameLang1((String) attributes.get(UINCardConstant.NAME + "_" + primaryLang));
		request.setAddressLine1Lang1((String) attributes.get(UINCardConstant.ADDRESSLINE1 + "_" + primaryLang));
		request.setAddressLine2Lang1((String) attributes.get(UINCardConstant.ADDRESSLINE2 + "_" + primaryLang));
		request.setAddressLine3Lang1((String) attributes.get(UINCardConstant.ADDRESSLINE3 + "_" + primaryLang));
		request.setRegionLang1((String) attributes.get(UINCardConstant.REGION + "_" + primaryLang));
		request.setProvinceLang1((String) attributes.get(UINCardConstant.PROVINCE + "_" + primaryLang));
		request.setCityLang1((String) attributes.get(UINCardConstant.CITY + "_" + primaryLang));

		request.setNameLang2((String) attributes.get(UINCardConstant.NAME + "_" + secondaryLang));
		request.setAddressLine1Lang2((String) attributes.get(UINCardConstant.ADDRESSLINE1 + "_" + secondaryLang));
		request.setAddressLine2Lang2((String) attributes.get(UINCardConstant.ADDRESSLINE2 + "_" + secondaryLang));
		request.setAddressLine3Lang2((String) attributes.get(UINCardConstant.ADDRESSLINE3 + "_" + secondaryLang));
		request.setRegionLang2((String) attributes.get(UINCardConstant.REGION + "_" + secondaryLang));
		request.setProvinceLang2((String) attributes.get(UINCardConstant.PROVINCE + "_" + secondaryLang));
		request.setCityLang2((String) attributes.get(UINCardConstant.CITY + "_" + secondaryLang));
		request.setPostalCode((String) attributes.get(UINCardConstant.POSTALCODE));
		request.setPhoneNumber((String) attributes.get(UINCardConstant.PHONE));

		jsonDto.setRequest(request);

		File jsonText = new File(attributes.get("UIN").toString() + ".txt");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		byte[] jsonTextFileBytes = null;

		try {
			mapper.writeValue(jsonText, jsonDto);


			InputStream fileStream = new FileInputStream(jsonText);
			jsonTextFileBytes = IOUtils.toByteArray(fileStream);
			fileStream.close();
			FileUtils.forceDelete(jsonText);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonTextFileBytes;
	}

	private void setTemplateAttributes(String string, Map<String, Object> attributes) throws IOException {
		JSONObject demographicIdentity = JsonUtil.objectMapperReadValue(string, JSONObject.class);
		Utilities utility = new Utilities();
		if (demographicIdentity == null)
			isPrintingStageValidated = false;
		//throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

		String mapperJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorIdentityJson());
		JSONObject mapperJson = JsonUtil.objectMapperReadValue(mapperJsonString, JSONObject.class);
		JSONObject mapperIdentity = JsonUtil.getJSONObject(mapperJson,
				utility.getGetRegProcessorDemographicIdentity());

		List<String> mapperJsonKeys = new ArrayList<>(mapperIdentity.keySet());
		for (String key : mapperJsonKeys) {
			JSONObject jsonValue = JsonUtil.getJSONObject(mapperIdentity, key);
			Object object = JsonUtil.getJSONValue(demographicIdentity, (String) jsonValue.get("value"));
			if (object instanceof ArrayList) {
				JSONArray node = JsonUtil.getJSONArray(demographicIdentity, (String) jsonValue.get("value"));
				JsonValue[] jsonValues = JsonUtil.mapJsonNodeToJavaObject(JsonValue.class, node);
				for (int count = 0; count < jsonValues.length; count++) {
					String lang = jsonValues[count].getLanguage();
					attributes.put(key + "_" + lang, jsonValues[count].getValue());
				}
			} else if (object instanceof LinkedHashMap) {
				JSONObject json = JsonUtil.getJSONObject(demographicIdentity, (String) jsonValue.get("value"));
				attributes.put(key, json.get("value"));
			} else {
				attributes.put(key, object);
			}
		}
	}

	private boolean setApplicantPhoto(Map<String, Map<String, String>> response) {
		String value = null;
		boolean isPhotoSet = false;
		String documents = null;
		/*for (String res : response){
			documents = res.get("documents").toString();
			logger.info("documents  : "+documents);
		}*/
		if (response!= null) {
			try {
				for (Map.Entry<String, Map<String,String>> entry : response.entrySet()) {
					//	logger.info(entry.getKey() + "/" + entry.getValue());
					if(entry.getKey().contains("documents")){
						List<Map<String,String>> values = (List<Map<String, String>>) entry.getValue();
						for(Map<String,String> val : values){
							String obj = val.get("category");
							if(val.get("category").matches("individualBiometrics")){
								value = val.get("value");
								logger.info("value : "+value);
								break;
							}

						}
					}
				}

				if(value!=null){
					CbeffUtil cbeffutil = new CbeffImpl();
					CbeffToBiometricUtil util = new CbeffToBiometricUtil(cbeffutil);
					List<String> subtype = new ArrayList<>();
					byte[] photobyte;
					photobyte = util.getImageBytes(value, "Face", subtype);
					String imageString = CryptoUtil.encodeBase64String(photobyte);
					attributes.put("ApplicantPhoto", "data:image/png;base64," + imageString);
					isPhotoSet = true;

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		return isPhotoSet;
	}
/*
	public static void main(String args[]){
		PrintingStage ps = new PrintingStage();
		ps.validatePrintingStage("10002100320000220190417100557");
	}*/
	@Test
	public void testRun(){
		PrintingStage ps = new PrintingStage();
		try {
			ps.validatePrintingStage("10011100110001920190518080310");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test=extent.createTest("testRun");
	}

}
