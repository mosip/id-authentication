package io.mosip.registrationProcessor.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import java.util.Date;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;

import io.mosip.dbdto.CryptomanagerDto;
import io.mosip.dbdto.DecrypterDto;
import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.dbdto.SyncRegistrationDto;

public class EncryptData {
	private String applicationId="REGISTRATION";
	ObjectMapper objectMapper=new ObjectMapper();
	
	public void encryptData(JSONObject request) {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		JSONArray requestDataArray = (JSONArray) request.get("request");
		JSONObject syncDto = (JSONObject) requestDataArray.get(0);
		
		Gson g = new Gson();
		SyncRegistrationDto d=g.fromJson(syncDto.toString(),SyncRegistrationDto.class);
		RegistrationPacketSyncDTO p = g.fromJson(request.toString(), RegistrationPacketSyncDTO.class);
		p.setSyncRegistrationDTOs(d);
		String outputJson="";
		try {
			outputJson=objectMapper.writeValueAsString(p);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] byteArray=outputJson.getBytes();
		String encryptedString=Base64.encodeBase64URLSafeString(byteArray);
		JSONObject encryptRequest=new JSONObject();
		CryptomanagerDto cryptoReq=new CryptomanagerDto();
		JSONObject cryptographicRequest=new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		
		DecrypterDto decrypterDto=new DecrypterDto();
		JSONArray requestData = (JSONArray) request.get("request");
		JSONObject obj = (JSONObject) requestData.get(0);
		String registrationId = obj.get("registrationId").toString();
		String referenceId=registrationId.substring(0,5)+"_"+registrationId.substring(5,10);
		
		try {
					
			String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
			int n = 100 + new Random().nextInt(900);
			String milliseconds = String.valueOf(n);
			
			Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
					+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6)+milliseconds);
			LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			Date currentDate=new Date();
			LocalDateTime requestTime=LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
			decrypterDto.setApplicationId(applicationId);
			decrypterDto.setReferenceId(referenceId);
			decrypterDto.setData(encryptedString);
			decrypterDto.setTimeStamp(ldt);
			cryptoReq.setRequesttime(requestTime);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			cryptographicRequest.put("applicationId", applicationId);
			cryptographicRequest.put("data", encryptedString);
			cryptographicRequest.put("referenceId", referenceId);
			cryptographicRequest.put("timeStamp",decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("id","");
			encryptRequest.put("metadata","");
			encryptRequest.put("request",cryptographicRequest);
			encryptRequest.put("requesttime", cryptoReq.getRequesttime().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("version","1.0");
		}  catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(encryptRequest);
	}
	public static void main(String[] args) {
		String useMe="{\r\n" + 
				"	\"id\": \"mosip.registration.sync\",\r\n" + 
				"	\"requesttime\": \"2019-03-02T06:29:41.011Z\",\r\n" + 
				"	\"version\": \"1.0\",\r\n" + 
				"	\"request\": [{\r\n" + 
				"		\"langCode\": \"eng\",\r\n" + 
				"		\"registrationId\": \"10011100110001920190325120310\",\r\n" + 
				"		\"registrationType\": \"NEW\",\r\n" + 
				"		\"packetHashValue\": \"D7C87DC5D3A759D77433B02B80435CFAB5087F1A942543F51A5075BC441BF7EB\",\r\n" + 
				"		\"packetSize\": 5242880,\r\n" + 
				"		\"supervisorStatus\": \"APPROVED\",\r\n" + 
				"		\"supervisorComment\": \"Approved, all good\",\r\n" + 
				"		\"optionalValues\": [{\r\n" + 
				"			\"key\": \"CNIE\",\r\n" + 
				"			\"value\": \"122223456\"\r\n" + 
				"		}]\r\n" + 
				"	}]\r\n" + 
				"}";
		JSONObject js=new JSONObject(useMe);
		EncryptData e=new EncryptData();
		e.encryptData(js);
	}
}
