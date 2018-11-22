package io.mosip.pregistration.datasync.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.pregistration.datasync.code.StatusCodes;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.dto.ResponseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.pregistration.datasync.entity.DocumentEntity;
import io.mosip.pregistration.datasync.entity.Ipprlst_PK;
import io.mosip.pregistration.datasync.entity.PreRegistrationEntity;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;
import io.mosip.pregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.pregistration.datasync.exception.ReverseDataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.exception.ZipFileCreationException;
import io.mosip.pregistration.datasync.repository.DataSyncRepo;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;
import io.mosip.pregistration.datasync.repository.ReverseDataSyncRepo;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

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

	@Autowired
	private DataSyncRepo dataSyncRepo;

	@Autowired
	private ReverseDataSyncRepo reversedataSyncRepo;

	List<ExceptionJSONInfo> errlist = new ArrayList<>();
	ExceptionJSONInfo exceptionJSONInfo = new ExceptionJSONInfo("", "");
	String status = "true";

	Timestamp resTime = new Timestamp(System.currentTimeMillis());

	/**
	 * @param preId
	 * @return Zipped File
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseDTO getPreRegistration(String preId) throws Exception {
		List responseList = new ArrayList<>();
		PreRegistrationEntity demography = dataSyncRepository.findDemographyByPreId(preId);
		ResponseDTO responseDto = new ResponseDTO<>();
		if (demography != null) {
			System.out.println("Pre id: " + demography.getPreRegistrationId());
			List<DocumentEntity> documentlist = dataSyncRepository.findDocumentByPreId(preId);
			byte[] bytes = DataSyncService.archivingFiles(demography, documentlist);
			responseList.add(bytes);
			responseList.add(demography.getPreRegistrationId().toString());
		} else {
			throw new DataSyncRecordNotFoundException(StatusCodes.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());
		}

		responseDto.setStatus(status);
		responseDto.setResTime(resTime);
		responseDto.setErr(errlist);
		responseDto.setResponse(responseList);
		return responseDto;
	}

	/**
	 * @param preRegistrationEntity
	 * @param documentEntityList
	 * @return zipped file's byte array
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static byte[] archivingFiles(PreRegistrationEntity preRegistrationEntity,
			List<DocumentEntity> documentEntityList) throws IOException {
		FileOutputStream fileOutputStream = null;
		FileOutputStream demofileOutputStream = null;
		File jsonFile = null;
		File fileDoc = null;
		List<String> inputMultiFileList = new ArrayList<>();
		Path pathDoc = null;
		byte[] inputStream = null;
		byte[] returnInputStream = null;
		if (preRegistrationEntity != null) {
			try {
				JSONObject responseJson = new JSONObject();
				responseJson.put("Pre-registration Id", preRegistrationEntity.getPreRegistrationId());
				responseJson.put("Appointment Date", preRegistrationEntity.getCreateDateTime().toString());
				responseJson.put("Identity", new String(preRegistrationEntity.getApplicantDetailJson(), "UTF-8"));
				pathDoc = Paths.get(System.getProperty("java.io.tmpdir") + File.separator
						+ preRegistrationEntity.getPreRegistrationId().toString() + ".json");
				jsonFile = new File(pathDoc.toString());
				if (jsonFile.exists()) {
					jsonFile.delete();
				}
				jsonFile.createNewFile();

				demofileOutputStream = new FileOutputStream(jsonFile);
				demofileOutputStream.write(responseJson.toJSONString().getBytes());
				demofileOutputStream.close();

				System.out.println("Demography path: " + jsonFile.getAbsolutePath());
				inputMultiFileList.add(jsonFile.getAbsolutePath());
				if (documentEntityList != null && documentEntityList.size() > 0) {
					for (int i = 0; i < documentEntityList.size(); i++) {
						pathDoc = Paths.get(System.getProperty("java.io.tmpdir") + File.separator
								+ documentEntityList.get(i).getPreregId().toString() + "_"
								+ documentEntityList.get(i).getDoc_name());

						fileDoc = new File(pathDoc.toString());
						byte[] docBytes = documentEntityList.get(i).getDoc_store();
						if (fileDoc.exists()) {
							fileDoc.delete();
						}
						fileDoc.createNewFile();
						fileOutputStream = new FileOutputStream(fileDoc);
						fileOutputStream.write(docBytes);
						fileOutputStream.close();
						System.out.println("FileDoc path: " + fileDoc.getAbsolutePath());
						inputMultiFileList.add(fileDoc.getAbsolutePath());

					}

				}
				inputStream = getCompressed(inputMultiFileList);
				returnInputStream = inputStream;
			} catch (Exception e) {
				throw new ZipFileCreationException(StatusCodes.FAILED_TO_CREATE_A_ZIP_FILE.toString());
			} finally {
				inputStream = new byte[1024];
				if (inputMultiFileList != null && !inputMultiFileList.equals(null) && inputMultiFileList.size() > 0) {
					for (String s : inputMultiFileList) {
						Path path = Paths.get(s);
						Files.deleteIfExists(path);
					}
				}
				inputMultiFileList.clear();
				;
			}
		}
		return returnInputStream;
	}

	/**
	 * @param zipOut
	 * @param fis
	 * @throws IOException
	 */
	private static void readFile(ZipOutputStream zipOut, FileInputStream fis) throws IOException {
		final byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}

	}

	/**
	 * @param inputFIle
	 * @return compressed Zip
	 * @throws IOException
	 */
	public static byte[] getCompressed(List<String> inputFIle) throws IOException {
		File fileToZip = null;
		List<String> srcFiles = new ArrayList<>();
		srcFiles.addAll(inputFIle);
		FileInputStream fileInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		ZipOutputStream zipOutputStream = null;
		BufferedInputStream bufferedInputStream = null;
		byteArrayOutputStream = new ByteArrayOutputStream();
		zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

		for (String srcFile : srcFiles) {
			fileToZip = new File(srcFile);
			fileInputStream = new FileInputStream(fileToZip);
			bufferedInputStream = new BufferedInputStream(fileInputStream, 1024);
			ZipEntry entry = new ZipEntry(fileToZip.getName());
			zipOutputStream.putNextEntry(entry);
			readFile(zipOutputStream, fileInputStream);

			bufferedInputStream.close();
		}
		byteArrayOutputStream.close();

		zipOutputStream.close();
		fileInputStream.close();

		return byteArrayOutputStream.toByteArray();
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
		if (preIdList != null && preIdList.size() > 0 && !preIdList.equals(null)) {
			for (int i = 0; i < preIdList.size(); i++) {
				reverseEntity = new ReverseDataSyncEntity();
				Ipprlst_PK ipprlst_PK = new Ipprlst_PK();
				ipprlst_PK.setPrereg_id(preIdList.get(i));
				ipprlst_PK.setReceived_dtimes(reverseDto.getReqTime());
				// reverseEntity.setPreRegistrationId(preIdList.get(i));
				// reverseEntity.setReceivedDTime(reverseDto.getReqTime());
				reverseEntity.setIpprlst_PK(ipprlst_PK);
				reverseEntity.setLangCode("AR");
				reverseEntity.setCrBy("5766477466");
				reverseEntity.setCrDate(new Timestamp(System.currentTimeMillis()));
				entityList.add(reverseEntity);

				processedEntity = new PreRegistrationProcessedEntity();
				processedEntity.setPreRegistrationId(preIdList.get(i));
				processedEntity.setReceivedDTime(reverseDto.getReqTime());
				processedEntity.setStatusCode("Processed");
				processedEntity.setStatusComments("Processed by registration processor");
				processedEntity.setLangCode("AR");
				processedEntity.setCrBy("5766477466");
				processedEntity.setCrDate(new Timestamp(System.currentTimeMillis()));
				processedEntityList.add(processedEntity);
			}
		}

		List<ReverseDataSyncEntity> savedList = dataSyncRepository.saveAll(entityList);

		if (savedList != null && !savedList.equals(null) && savedList.size() > 0) {
			for (PreRegistrationProcessedEntity s : processedEntityList) {
				if (!reversedataSyncRepo.existsById(s.getPreRegistrationId()))
					reversedataSyncRepo.save(s);
			}
			// reversedataSyncRepo.saveAll(processedEntityList);

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

	public ResponseDTO<ResponseDataSyncDTO> retrieveAllPreRegid(DataSyncRequestDTO dataSyncRequestDTO) {

		Timestamp fromDate = dataSyncRequestDTO.getFromDate();
		Timestamp toDate = dataSyncRequestDTO.getToDate();

		ResponseDataSyncDTO responseDataSyncDTO = new ResponseDataSyncDTO();
		ResponseDTO<ResponseDataSyncDTO> responseDto = new ResponseDTO<>();
		List<PreRegistrationEntity> preRegIdEntitylist;
		List<ExceptionJSONInfo> err = new ArrayList<>();
		List<ResponseDataSyncDTO> responseDataSyncList = new ArrayList<>();

		try {
			preRegIdEntitylist = dataSyncRepo.findBycreateDateTimeBetween(fromDate, toDate);
			if (preRegIdEntitylist == null || preRegIdEntitylist.size() == 0) {
				throw new RecordNotFoundForDateRange(StatusCodes.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
			} else {

				List<String> preregIds = new ArrayList<>();
				for (PreRegistrationEntity preRegistrationEntity : preRegIdEntitylist) {
					preregIds.add(preRegistrationEntity.getPreRegistrationId());
				}
				responseDataSyncDTO.setPreRegistrationIds(preregIds);
				responseDataSyncDTO.setTransactionId("337324416082");
				responseDataSyncList.add(responseDataSyncDTO);
				responseDto.setStatus("True");
				responseDto.setErr(err);
				responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
				responseDto.setResponse(responseDataSyncList);
			}
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException(StatusCodes.REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());

		}

		return responseDto;
	}

}
