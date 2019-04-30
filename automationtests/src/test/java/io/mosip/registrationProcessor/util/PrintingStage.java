package io.mosip.registrationProcessor.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.ITest;
import org.testng.annotations.Test;


import io.mosip.dbaccess.RegProcStageDb;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CbeffImpl;
import io.mosip.util.CbeffToBiometricUtil;
import io.mosip.util.CbeffUtil;
import io.mosip.util.CryptoUtil;
import io.mosip.util.JsonUtil;
import io.mosip.util.JsonValue;
import io.mosip.util.MosipQueue;
import io.mosip.util.MosipQueueConnectionFactory;
import io.mosip.util.MosipQueueConnectionFactoryImpl;
import io.restassured.response.Response;

public class PrintingStage extends BaseTestCase{
	private static Logger logger = Logger.getLogger(PrintingStage.class);
	boolean isPrintingStageValidated = false;
	private String username = "admin";
	private String password = "admin";
	private String url = "tcp://104.211.200.46:61616";
	private String typeOfQueue = "ACTIVEMQ";
	Map<String, Object> attributes = new LinkedHashMap<>();
	
	public boolean validatePrintingStage(String rid) throws IOException{
		boolean isPrintingStageValidated =  false;
		RegProcStageDb dbData= new RegProcStageDb();
		String uin = dbData.regproc_getUIN(rid);
		Response actualResponse = null;
		ApplicationLibrary applicationLibrary = new ApplicationLibrary();
		String id_url = "/v1/idrepo/identity";
		

		

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
			
			//creating text file
			if (response!= null) {
				
					for (Map.Entry<String, Map<String,String>> entry : response.entrySet()) {
						//	logger.info(entry.getKey() + "/" + entry.getValue());
						if(entry.getKey().contains("identity")){
							Map<String, String> values =  entry.getValue();
							setTemplateAttributes(values.toString(),attributes);
						}
					}
			}
		//	String jsonString = response.
				
			//create active mq connection
			MosipQueueConnectionFactory mosipConnectionFactory = new MosipQueueConnectionFactoryImpl();
			MosipQueue queue = (MosipQueue) mosipConnectionFactory.createConnection(typeOfQueue, username, password, url);

			if(queue == null){
				isPrintingStageValidated = false;
				logger.info("Conenction not created...");
			}

			//	boolean isAddedToQueue = sendToQueue(queue, documentBytesMap, 0, uin);

		}
		//	logger.info("uin : "+uin);
		return isPrintingStageValidated;

	}

	private void setTemplateAttributes(String string, Map<String, Object> attributes) throws IOException {
		JSONObject demographicIdentity = JsonUtil.objectMapperReadValue(string, JSONObject.class);
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
							/*String obj = val.get("category");*/
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

			//List<Documents> documents = response.getResponse().getDocuments();

			/*for (Documents doc : documents) {
				if (doc.getCategory().equals(INDIVIDUAL_BIOMETRICS)) {
					value = doc.getValue();
					break;
				}
			}*/
		}
		/*	if (value != null) {
			CbeffToBiometricUtil util = new CbeffToBiometricUtil(cbeffutil);
			List<String> subtype = new ArrayList<>();
			byte[] photobyte = util.getImageBytes(value, FACE, subtype);
			String imageString = CryptoUtil.encodeBase64String(photobyte);
			attributes.put(APPLICANT_PHOTO, "data:image/png;base64," + imageString);
			isPhotoSet = true;
		}*/

		return isPhotoSet;
	}

	/*public static void main(String args[]){
		PrintingStage ps = new PrintingStage();
		ps.validatePrintingStage("10002100320000220190417100557");
	}*/
	@Test
	public void testRun(){
		PrintingStage ps = new PrintingStage();
		try {
			ps.validatePrintingStage("10002100320000220190417100557");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
