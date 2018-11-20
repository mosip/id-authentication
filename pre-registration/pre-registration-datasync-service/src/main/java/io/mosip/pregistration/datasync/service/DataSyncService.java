package io.mosip.pregistration.datasync.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.mosip.pregistration.datasync.code.StatusCodes;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.entity.DocumentEntity;
import io.mosip.pregistration.datasync.entity.PreRegistrationEntity;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;
import io.mosip.pregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.exception.ReverseDataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;

/**
 * 
 * @version 1.0.0
 * 
 * @author M1046129
 *
 */
@Service
public class DataSyncService {

	@Autowired
	@Qualifier("dataSyncRepository")
	private DataSyncRepository dataSyncRepository;

	List<ExceptionJSONInfo> errlist = new ArrayList<>();
	ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo("", "");
	String status = "true";

	Timestamp resTime = new Timestamp(System.currentTimeMillis());

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseDTO getPreRegistration(String preId) throws Exception {

		PreRegistrationEntity demography = dataSyncRepository.findDemographyByPreId(preId);
		ResponseDTO responseDto = new ResponseDTO<>();
		List responseList = new ArrayList<>();
		if (demography != null) {
			System.out.println("Pre id: " + demography.getPreRegistrationId());
			new DataSyncService().archiving(demography);

			List<DocumentEntity> documentlist = dataSyncRepository.findDocumentByPreId(preId);
			if (documentlist != null && documentlist.size() > 0) {
				new DataSyncService().archiving(documentlist);
				status = "true";
				exceptionJSONInfo = new ExceptionJSONInfo("", "");
				responseDto.setResponse(responseList);

			}
			// else {
			//
			// status = "false";
			// exceptionJSONInfo = new
			// ExceptionJSONInfo(ErrorCodes.PRG_DATA_SYNC_006.toString(),
			// "Document not found");
			// errlist.add(exceptionJSONInfo);
			// throw new
			// DocumentNotFoundException(StatusCodes.DOCUMENT_IS_MISSING.toString());
			// }

		} else {
			throw new DataSyncRecordNotFoundException(StatusCodes.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());

		}

		responseDto.setStatus(status);
		responseDto.setResTime(resTime);
		responseDto.setErr(errlist);
		return responseDto;
	}

	public List archiving(List<DocumentEntity> documentlist) throws Exception {
		/*------------------------------------Document part--------------------------------------*/
		Path pathDoc = null;

		for (int i = 0; i < documentlist.size(); i++) {
			pathDoc = Paths.get("src" + File.separator + "main" + File.separator + "resources" + File.separator
					+ documentlist.get(i).getPreregId().toString() +"_"+ documentlist.get(i).getDoc_name());
			File fileDoc = new File(pathDoc.toString());
			byte[] docBytes = documentlist.get(i).getDoc_store();
			if (!fileDoc.exists() && !fileDoc.isDirectory()) {
				fileDoc.createNewFile();
			}
			if (fileDoc.exists()) {
				FileOutputStream fileOutputStream = new FileOutputStream(fileDoc);
				fileOutputStream.write(docBytes);
				fileOutputStream.flush();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List archiving(PreRegistrationEntity entity) throws Exception {

		JSONObject responseJson = new JSONObject();
		responseJson.put("Pre-registration Id", entity.getPreRegistrationId());
		responseJson.put("Appointment Date", entity.getCreateDateTime().toString());
		responseJson.put("Identity", new String(entity.getApplicantDetailJson(), "UTF-8"));

		Path path = Paths.get("src" + File.separator + "main" + File.separator + "resources" + File.separator
				+ entity.getPreRegistrationId().toString() + ".json");
		File demo = new File(path.toString());

		if (!demo.exists() && !demo.isDirectory()) {
			demo.createNewFile();
		}
		if (demo.exists()) {
			FileWriter fileWriter = new FileWriter(demo);
			fileWriter.write(responseJson.toJSONString());
			fileWriter.flush();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseDTO<ReverseDataSyncDTO> storeConsumedPreRegistrations(ReverseDataSyncDTO reverseDto) {
		ResponseDTO<ReverseDataSyncDTO> responseDto = new ResponseDTO<>();
		List<ReverseDataSyncEntity> entityList = new ArrayList<>();
		List<PreRegistrationProcessedEntity> processedEntityList = new ArrayList<>();
		ReverseDataSyncEntity reverseEntity = new ReverseDataSyncEntity();
		PreRegistrationProcessedEntity processedEntity = new PreRegistrationProcessedEntity();

		ReverseDataSyncRequestDTO reverseRequestDTO = reverseDto.getRequest();
		List<String> preIdList = reverseRequestDTO.getPre_registration_ids();
		for (int i = 0; i < preIdList.size(); i++) {
			reverseEntity = new ReverseDataSyncEntity();
			reverseEntity.setPreRegistrationId(preIdList.get(i));
			reverseEntity.setReceivedDTime(reverseDto.getReqTime());
			entityList.add(reverseEntity);

			processedEntity = new PreRegistrationProcessedEntity();
			processedEntity.setPreRegistrationId(preIdList.get(i));
			processedEntity.setReceivedDTime(reverseDto.getReqTime());
			processedEntity.setStatusCode("Processed");
			processedEntity.setStatusComments("Processed by registration processor");

			processedEntityList.add(processedEntity);
		}

		List<ReverseDataSyncEntity> savedList = dataSyncRepository.saveAll(entityList);

		if (savedList != null && !savedList.equals(null) && savedList.size() > 0) {

			dataSyncRepository.saveAll(processedEntityList);

			status = "true";
			exceptionJSONInfo = new ExceptionJSONInfo("", "");
			errlist.add(exceptionJSONInfo);
			List responseList = new ArrayList<>();
			responseList.add(StatusCodes.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());
			responseDto.setResponse(responseList);

		} else {
			throw new ReverseDataSyncRecordNotFoundException(
					StatusCodes.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());
		}

		responseDto.setStatus(status);
		responseDto.setResTime(resTime);
		responseDto.setErr(errlist);

		return responseDto;

	}

}
