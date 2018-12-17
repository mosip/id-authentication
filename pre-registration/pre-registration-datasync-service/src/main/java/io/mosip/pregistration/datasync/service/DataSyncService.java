package io.mosip.pregistration.datasync.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.pregistration.datasync.code.StatusCodes;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.DataSyncResponseDTO;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfo;
import io.mosip.pregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.pregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.pregistration.datasync.entity.DocumentEntity;
import io.mosip.pregistration.datasync.entity.InterfaceDataSyncTablePK;
import io.mosip.pregistration.datasync.entity.PreRegistrationEntity;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;
import io.mosip.pregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.pregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.pregistration.datasync.exception.ZipFileCreationException;
import io.mosip.pregistration.datasync.repository.DataSyncRepo;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;
import io.mosip.pregistration.datasync.repository.ReverseDataSyncRepo;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

/**
 * DataSync Service
 * 
 * @version 1.0.0
 * 
 * @author M1046129 - Jagadishwari
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
	public DataSyncResponseDTO<PreRegArchiveDTO> getPreRegistration(String preId) throws Exception {
		DataSyncResponseDTO responseDto = new DataSyncResponseDTO<>();
		PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
		PreRegistrationEntity demography = dataSyncRepository.findDemographyByPreId(preId);
		if (demography != null) {
			System.out.println("Pre id: " + demography.getPreRegistrationId());
			List<DocumentEntity> documentlist = dataSyncRepository.findDocumentByPreId(preId);
			byte[] bytes = DataSyncService.archivingFiles(demography, documentlist);
			preRegArchiveDTO.setZipBytes(bytes);
			preRegArchiveDTO.setFileName(demography.getPreRegistrationId().toString()+"_"+demography.getCreateDateTime().toString());
		} else {
			throw new DataSyncRecordNotFoundException(StatusCodes.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());
		}

		responseDto.setStatus(status);
		responseDto.setResTime(resTime);
		responseDto.setErr(errlist);
		responseDto.setResponse(preRegArchiveDTO);
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
		JSONParser jsonParser = new JSONParser();
		JSONObject demographicJsonObject = null;
		if (preRegistrationEntity != null) {
			try {
				JSONObject responseJson = new JSONObject();
				responseJson.put("Pre-registration Id", preRegistrationEntity.getPreRegistrationId());
				responseJson.put("Appointment Date", preRegistrationEntity.getCreateDateTime().toString());

				demographicJsonObject = (JSONObject) jsonParser
						.parse(new String(preRegistrationEntity.getApplicantDetailJson(), StandardCharsets.UTF_8));
				JSONObject reqObject = (JSONObject) demographicJsonObject.get("request");
				JSONObject demoObj = (JSONObject) reqObject.get("demographicDetails");

				pathDoc = Paths.get(System.getProperty("java.io.tmpdir") + File.separator
						+ preRegistrationEntity.getPreRegistrationId().toString() + "_Demographic" + ".json");

				responseJson.put("demographic-details", demoObj);

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
								+ documentEntityList.get(i).getDoc_cat_code().toString() + "_"
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

	/**
	 * @param reverseDto
	 * @return responseDTO
	 */

	public DataSyncResponseDTO<String> storeConsumedPreRegistrations(ReverseDataSyncDTO reverseDto) {
		DataSyncResponseDTO<String> responseDto = new DataSyncResponseDTO<>();
		List<ReverseDataSyncEntity> entityList = new ArrayList<>();
		List<PreRegistrationProcessedEntity> processedEntityList = new ArrayList<>();
		ReverseDataSyncEntity reverseEntity = new ReverseDataSyncEntity();
		PreRegistrationProcessedEntity processedEntity = new PreRegistrationProcessedEntity();

		ReverseDataSyncRequestDTO reverseRequestDTO = reverseDto.getRequest();
		List<String> preIdList = reverseRequestDTO.getPre_registration_ids();
		if (preIdList != null && preIdList.size() > 0 && !preIdList.equals(null)) {
			for (int i = 0; i < preIdList.size(); i++) {
				reverseEntity = new ReverseDataSyncEntity();
				InterfaceDataSyncTablePK ipprlstPK = new InterfaceDataSyncTablePK();
				ipprlstPK.setPreregId(preIdList.get(i));
				ipprlstPK.setReceivedDtimes(new Timestamp(reverseDto.getReqTime().getTime()));
				reverseEntity.setIpprlst_PK(ipprlstPK);
				reverseEntity.setLangCode("AR");
				reverseEntity.setCrBy("5766477466");
				reverseEntity.setCrDate(new Timestamp(System.currentTimeMillis()));
				entityList.add(reverseEntity);

				processedEntity = new PreRegistrationProcessedEntity();
				processedEntity.setPreRegistrationId(preIdList.get(i));
				processedEntity.setReceivedDTime(new Timestamp(reverseDto.getReqTime().getTime()));
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

			status = "true";
			responseDto.setResponse(StatusCodes.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());

		} else {
			throw new ReverseDataFailedToStoreException(StatusCodes.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());
		}

		responseDto.setStatus(status);
		responseDto.setResTime(resTime);
		responseDto.setErr(errlist);

		return responseDto;

	}

	public DataSyncResponseDTO<PreRegistrationIdsDTO> retrieveAllPreRegid(DataSyncRequestDTO dataSyncRequestDTO)
			throws ParseException {
		String fromDate = dataSyncRequestDTO.getFromDate();
		String toDate = dataSyncRequestDTO.getToDate();
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		Date myDate = DateUtils.parse(fromDate, dateFormat);
				
		Date myDate1 = null;

		if (toDate == null) {
			myDate1 = myDate;
			Calendar cal = Calendar.getInstance();
			cal.setTime(myDate1);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			myDate1 = cal.getTime();
		} else {
			myDate1 = DateUtils.parse(toDate,dateFormat); 
		}
		PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
		DataSyncResponseDTO<PreRegistrationIdsDTO> responseDto = new DataSyncResponseDTO<>();
		List<PreRegistrationEntity> preRegIdEntitylist;
		List<ExceptionJSONInfo> err = new ArrayList<>();

		try {
			preRegIdEntitylist = dataSyncRepo.findBycreateDateTimeBetween(new Timestamp(myDate.getTime()),
					new Timestamp(myDate1.getTime()));
			if (preRegIdEntitylist == null || preRegIdEntitylist.size() == 0) {
				throw new RecordNotFoundForDateRange(StatusCodes.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
			} else {

				List<String> preregIds = new ArrayList<>();
				for (PreRegistrationEntity preRegistrationEntity : preRegIdEntitylist) {
					preregIds.add(preRegistrationEntity.getPreRegistrationId());
				}
				preRegistrationIdsDTO.setPreRegistrationIds(preregIds);
				preRegistrationIdsDTO.setTransactionId("337324416082");
				responseDto.setStatus("True");
				responseDto.setErr(err);
				responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
				responseDto.setResponse(preRegistrationIdsDTO);
			}
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException(StatusCodes.REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());

		}

		return responseDto;
	}

}
