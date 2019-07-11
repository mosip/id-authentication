package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.perf.dto.RegPacketSyncDto;
import io.mosip.registrationProcessor.perf.util.EncryptData;
import io.mosip.registrationProcessor.perf.util.FileUtil;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.RegProcApiRequests;
import io.mosip.registrationProcessor.perf.util.TokenGeneration;
import io.restassured.response.Response;

public class SyncRequestCreater {
	private final String encrypterURL = "/v1/cryptomanager/encrypt";
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	TokenGeneration generateToken = new TokenGeneration();
	TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
	String validToken = "";

	public void createSyncRequestMaster() {
		List<String> checksumLines = new ArrayList<>();
		String fileWithPacketChecksum = PropertiesUtil.CHECKSUM_LOGFILE_PATH;
		try {
			checksumLines = FileUtil.readLinesOfFile(fileWithPacketChecksum);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String line = checksumLines.get(0);
		String[] literals = line.split(",");
		String regid = literals[0];
		String checksum = literals[1];
		Long fileSize = Long.parseLong(literals[2]);
		try {
			createSyncRequest(regid, checksum, fileSize);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getToken(String tokenType) {
		String tokenGenerationProperties = generateToken.readPropertyFile(tokenType);
		tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token = generateToken.getToken(tokenEntity);
		return token;
	}

	public void createSyncRequest(String regId, String checksum, Long fileSize) {

//		validToken = getToken("getStatusTokenGenerationFilePath");
//		boolean tokenStatus = apiRequests.validateToken(validToken);
//		while (!tokenStatus) {
//			validToken = getToken("syncTokenGenerationFilePath");
//			tokenStatus = apiRequests.validateToken(validToken);
//		}
		EncryptData encryptData = new EncryptData();
		JSONObject requestToEncrypt = null;
		String filePath = PropertiesUtil.NEW_PACKET_FOLDER_PATH + "Generated/" + regId + ".zip";
		File file = new File(filePath);
		RegistrationPacketSyncDTO registrationPacketSyncDto = new RegistrationPacketSyncDTO();
		if (file != null) {
			try {
				registrationPacketSyncDto = encryptData.createSyncRequest(file, "NEW");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			requestToEncrypt = encryptData.encryptData(registrationPacketSyncDto);
		}
		String syncEncryptedData = null;
		String referenceId = null;
		for (Object key : requestToEncrypt.keySet()) {
			if ("request".equals((String) key)) {
				Object value = requestToEncrypt.get(key);
				if (value instanceof JSONObject) {
					JSONObject request = (JSONObject) value;
					syncEncryptedData = (String) request.get("data");
					referenceId = (String) request.get("referenceId");
				}
				break;
			}
		}

		// syncEncryptedData = (String) requestToEncrypt.get("request.data");
		// referenceId = (String) requestToEncrypt.get("request.referenceId");

		RegPacketSyncDto syncDto = new RegPacketSyncDto();
		syncDto.setRegId(regId);
		syncDto.setSyncData(syncEncryptedData);
		syncDto.setPacketPath(filePath);
		syncDto.setReferenceId(referenceId);

		FileUtil.logSyncDataToFile(syncDto, PropertiesUtil.SYNCDATA__FILE_PATH);

//		String filedir = PropertiesUtil.NEW_PACKET_FOLDER_PATH + "syncData";
//		new File(filedir).mkdirs();
//		String filepath = filedir + File.separator + regid + ".json";
//		Gson gson = new Gson();
//		JSONUtil.writeJsonToFile(gson.toJson(requestToEncrypt), filepath);
//		String center_machine_refID = regid.substring(0, 5) + "_" + regid.substring(5, 10);
//		Response resp = apiRequests.postRequestToDecrypt(encrypterURL, requestToEncrypt, MediaType.APPLICATION_JSON,
//				MediaType.APPLICATION_JSON, validToken);
//		String encryptedData = resp.jsonPath().get("response.data").toString();
//		LocalDateTime timeStamp = null;
//		try {
//			timeStamp = encryptData.getTime(regid);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		String syncApi = "/registrationprocessor/v1/registrationstatus/sync";
//		Response actualResponse = apiRequests.regProcSyncRequest(syncApi, encryptedData, center_machine_refID,
//				timeStamp.toString() + "Z", MediaType.APPLICATION_JSON, validToken);

	}

}
