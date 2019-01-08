package io.mosip.preregistration.datasync.service.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;
import io.mosip.preregistration.datasync.code.RequestCodes;
import io.mosip.preregistration.datasync.code.StatusCodes;
import io.mosip.preregistration.datasync.dto.BookingRegistrationDTO;
import io.mosip.preregistration.datasync.dto.CreateDemographicDTO;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.DocumentServiceDTO;
import io.mosip.preregistration.datasync.dto.MainListResponseDTO;
import io.mosip.preregistration.datasync.dto.MainRequestDTO;
import io.mosip.preregistration.datasync.dto.MainResponseDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.datasync.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.entity.InterfaceDataSyncEntity;
import io.mosip.preregistration.datasync.entity.InterfaceDataSyncTablePK;
import io.mosip.preregistration.datasync.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.preregistration.datasync.exception.DemographicGetDetailsException;
import io.mosip.preregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.preregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.preregistration.datasync.exception.ZipFileCreationException;
import io.mosip.preregistration.datasync.exception.system.SystemFileIOException;
import io.mosip.preregistration.datasync.exception.system.SystemFileNotFoundException;
import io.mosip.preregistration.datasync.repository.InterfaceDataSyncRepo;
import io.mosip.preregistration.datasync.repository.ProcessedDataSyncRepo;

/**
 * @author Ravi C Balaji
 * @since 1.0.0
 */
@Component
public class DataSyncServiceUtil {
	/**
	 * Autowired reference for {@link #DataSyncRepository}
	 */
	@Autowired
	private InterfaceDataSyncRepo interfaceDataSyncRepo;

	/**
	 * Autowired reference for {@link #ReverseDataSyncRepo}
	 */
	@Autowired
	private ProcessedDataSyncRepo processedDataSyncRepo;

	/**
	 * Autowired reference for {@link #RestTemplateBuilder}
	 */
	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	/**
	 * Reference for ${demographic.resource.url} from property file
	 */
	@Value("${demographic.resource.url}")
	private String demographicResourceUrl;

	/**
	 * Reference for ${document.resource.url} from property file
	 */
	@Value("${document.resource.url}")
	private String documentResourceUrl;

	/**
	 * Reference for ${booking.resource.url} from property file
	 */
	@Value("${booking.resource.url}")
	private String bookingResourceUrl;

	private ObjectMapper mapper = new ObjectMapper();

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * This method is used to add the initial request values into a map for input
	 * validations.
	 * 
	 * @param RequestDto<DataSyncRequestDTO>
	 * @return a map for request input validation
	 */
	public Map<String, String> prepareRequestParamMap(MainRequestDTO<?> requestDto) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.ID.getCode(), requestDto.getId());
		inputValidation.put(RequestCodes.VER.getCode(), requestDto.getVer());
		inputValidation.put(RequestCodes.REQ_TIME.getCode(), getDateString(requestDto.getReqTime()));
		inputValidation.put(RequestCodes.REQUEST.getCode(), requestDto.getRequest().toString());
		return inputValidation;
	}

	public boolean validateDataSyncRequest(DataSyncRequestDTO dataSyncRequest) {
		String regId = dataSyncRequest.getRegClientId();
		String fromDate = dataSyncRequest.getFromDate();
		String toDate = dataSyncRequest.getToDate();
		String userId = dataSyncRequest.getUserId();
		String format = "dd-MM-yyyy HH:mm:ss";

		if (regId == null || isNull(regId)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_009.toString(),
					ErrorMessages.INVALID_REGISTRATION_CENTER_ID.toString());
		}

		if (fromDate == null || isNull(fromDate) || !parseDate(fromDate, format)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_010.toString(),
					ErrorMessages.INVALID_REQUESTED_DATE.toString());
		}

		if (toDate == null || isNull(toDate) || !parseDate(toDate, format)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_010.toString(),
					ErrorMessages.INVALID_REQUESTED_DATE.toString());
		}

		if (userId == null || isNull(userId)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_003.toString(),
					ErrorMessages.INVALID_USER_ID.toString());
		}

		return true;
	}

	public boolean validateReverseDataSyncRequest(ReverseDataSyncRequestDTO reverseDataSyncRequest) {
		List<String> preRegIdsList = reverseDataSyncRequest.getPreRegistrationIds();
		String langCode = reverseDataSyncRequest.getLangCode();
		String createdBy = reverseDataSyncRequest.getCreatedBy();
		Date createdDateTime = reverseDataSyncRequest.getCreatedDateTime();
		String updatedBy = reverseDataSyncRequest.getUpdateBy();
		Date updatedDateTime = reverseDataSyncRequest.getUpdateDateTime();

		if (preRegIdsList == null || isNull(preRegIdsList)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_011.toString(),
					ErrorMessages.INVALID_REQUESTED_PRE_REG_ID_LIST.toString());
		}

		if (langCode == null || isNull(langCode)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_012.toString(),
					ErrorMessages.INVALID_REQUESTED_LANG_CODE.toString());
		}

		if (createdBy == null || isNull(createdBy)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_003.toString(),
					ErrorMessages.INVALID_CREATED_USER_ID.toString());
		}

		if (createdDateTime == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_010.toString(),
					ErrorMessages.INVALID_REQUESTED_CREATED_DATE.toString());
		} else {
			try {
				new SimpleDateFormat(dateTimeFormat).format(createdDateTime);
			} catch (Exception ex) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_010.toString(),
						ErrorMessages.INVALID_REQUESTED_CREATED_DATE.toString());
			}
		}

		if (updatedBy == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_003.toString(),
					ErrorMessages.INVALID_UPDATE_USER_ID.toString());
		}

		if (updatedDateTime == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_010.toString(),
					ErrorMessages.INVALID_REQUESTED_CREATED_DATE.toString());
		} else {
			try {
				new SimpleDateFormat(dateTimeFormat).format(updatedDateTime);
			} catch (Exception ex) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_010.toString(),
						ErrorMessages.INVALID_REQUESTED_UPDATED_DATE.toString());
			}
		}

		return true;
	}

	public List<String> callGetPreIdsRestService(String fromDate, String toDate) {
		List<String> responseList = new LinkedList<>();
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(demographicResourceUrl + "/applicationDataByDateTime").queryParam("fromDate", fromDate)
					.queryParam("toDate", toDate);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<?>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			System.out.println(uriBuilder);
			@SuppressWarnings("rawtypes")
			ResponseEntity<MainListResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET,
					httpEntity, MainListResponseDTO.class);
			for (Object obj : respEntity.getBody().getResponse()) {
				responseList.add(mapper.convertValue(obj, String.class));
			}
			if (responseList.isEmpty()) {
				throw new RecordNotFoundForDateRange(ErrorMessages.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
			}
		} catch (RestClientException e) {
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), e.getCause());
		}
		return responseList;
	}

	public PreRegIdsByRegCenterIdResponseDTO callGetPreIdsByRegCenterIdRestService(String regCenterId,
			List<String> preRegIds) {
		PreRegIdsByRegCenterIdResponseDTO idResponseDTO = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(bookingResourceUrl + "/bookedPreIdsByRegId");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			PreRegIdsByRegCenterIdDTO preRegIdsByRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
			preRegIdsByRegCenterIdDTO.setRegistrationCenterId(regCenterId);
			preRegIdsByRegCenterIdDTO.setPreRegistrationIds(preRegIds);
			MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDto = new MainRequestDTO<>();
			requestDto.setRequest(preRegIdsByRegCenterIdDTO);
			@SuppressWarnings({ "rawtypes", "unchecked" })
			HttpEntity<MainResponseDTO<?>> httpEntity = new HttpEntity(requestDto, headers);
			String uriBuilder = builder.build().encode().toUriString();
			@SuppressWarnings("rawtypes")
			ResponseEntity<MainListResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.POST,
					httpEntity, MainListResponseDTO.class);
			idResponseDTO = mapper.convertValue(respEntity.getBody().getResponse().get(0),
					PreRegIdsByRegCenterIdResponseDTO.class);
		} catch (RestClientException e) {
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_013.toString(),
					ErrorMessages.FAILED_TO_GET_PRE_REG_ID_BY_REG_CLIENT_ID.toString(), e.getCause());
		}
		return idResponseDTO;
	}

	public List<DocumentServiceDTO> callGetDocRestService(String preId) {
		List<DocumentServiceDTO> responsestatusDto = new ArrayList<>();
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(documentResourceUrl + "/getDocument")
					.queryParam("preId", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<?>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			@SuppressWarnings("rawtypes")
			ResponseEntity<MainListResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET,
					httpEntity, MainListResponseDTO.class);
			for (Object obj : respEntity.getBody().getResponse()) {
				responsestatusDto.add(mapper.convertValue(obj, DocumentServiceDTO.class));
			}
		} catch (RestClientException e) {
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_006.toString(),
					ErrorMessages.FAILED_TO_FETCH_DOCUMENT.toString(), e.getCause());
		}
		return responsestatusDto;
	}

	public CreateDemographicDTO callGetPreRegInfoRestService(String preId) {
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(demographicResourceUrl + "/applicationData")
					.queryParam("preRegId", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainListResponseDTO<?>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			@SuppressWarnings("rawtypes")
			ResponseEntity<MainListResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET,
					httpEntity, MainListResponseDTO.class);
			CreateDemographicDTO responsestatusDto = mapper.convertValue(respEntity.getBody().getResponse().get(0),
					CreateDemographicDTO.class);
			if (responsestatusDto == null) {
				throw new DataSyncRecordNotFoundException(ErrorCodes.PRG_DATA_SYNC_004.toString(),
						ErrorMessages.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());
			} else {
				return responsestatusDto;
			}
		} catch (RestClientException e) {
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), e.getCause());
		}
	}

	/**
	 * This private Method is used to retrieve booking data by date
	 * 
	 * @param preId
	 * @return BookingRegistrationDTO
	 * 
	 */
	public BookingRegistrationDTO callGetAppointmentDetailsRestService(String preId) {
		BookingRegistrationDTO bookingRegistrationDTO = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(bookingResourceUrl + "/appointmentDetails")
					.queryParam("preRegID", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			@SuppressWarnings("rawtypes")
			HttpEntity<MainResponseDTO> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			@SuppressWarnings("rawtypes")
			ResponseEntity<MainResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					MainResponseDTO.class);
			bookingRegistrationDTO = mapper.convertValue(respEntity.getBody().getResponse(),
					BookingRegistrationDTO.class);
			if (bookingRegistrationDTO == null) {
				throw new RecordNotFoundForDateRange(ErrorCodes.PRG_DATA_SYNC_001.toString(),
						ErrorMessages.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
			}
		} catch (RestClientException e) {
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), e.getCause());
		}
		return bookingRegistrationDTO;
	}

	/**
	 * @param preRegistrationEntity
	 * @param documentEntityList
	 * @return zipped file's byte array
	 */
	public PreRegArchiveDTO archivingFiles(CreateDemographicDTO preRegistrationDTO,
			BookingRegistrationDTO bookingRegistrationDTO, List<DocumentServiceDTO> documentEntityList) {
		File fileDoc = null;
		List<String> inputMultiFileList = new ArrayList<>();
		Path pathDoc = null;
		PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
		try {
			preRegArchiveDTO.setPreRegistrationId(preRegistrationDTO.getPreRegistrationId());
			preRegArchiveDTO.setRegistrationCenterId(bookingRegistrationDTO.getRegistrationCenterId());
			preRegArchiveDTO.setAppointmentDate(bookingRegistrationDTO.getRegDate());
			preRegArchiveDTO.setTimeSlotFrom(bookingRegistrationDTO.getSlotFromTime());
			preRegArchiveDTO.setTimeSlotTo(bookingRegistrationDTO.getSlotToTime());

			JSONObject demographicJsonObject = preRegistrationDTO.getDemographicDetails();

			pathDoc = Paths.get(System.getProperty("java.io.tmpdir") + File.separator
					+ preRegistrationDTO.getPreRegistrationId().trim() + "_Demographic" + ".json");

			File jsonFile = new File(pathDoc.toString());
			if (jsonFile.exists()) {
				FileUtils.forceDelete(jsonFile);
			}

			if (jsonFile.createNewFile()) {
				outputStream(jsonFile, demographicJsonObject.toJSONString().getBytes(), inputMultiFileList);
			}

			if (documentEntityList != null && !documentEntityList.isEmpty()) {
				for (int i = 0; i < documentEntityList.size(); i++) {
					pathDoc = Paths.get(System.getProperty("java.io.tmpdir") + File.separator
							+ documentEntityList.get(i).getPrereg_id() + "_"
							+ documentEntityList.get(i).getDoc_cat_code() + "_"
							+ documentEntityList.get(i).getDoc_name());

					fileDoc = new File(pathDoc.toString());
					byte[] docBytes = documentEntityList.get(i).getMultipartFile();
					if (fileDoc.exists()) {
						 FileUtils.forceDelete(fileDoc);
					}
					if (fileDoc.createNewFile()) {
						outputStream(fileDoc, docBytes, inputMultiFileList);
					}
				}

			}
			preRegArchiveDTO.setZipBytes(getCompressed(inputMultiFileList));
			preRegArchiveDTO.setFileName(preRegistrationDTO.getPreRegistrationId());

			if (inputMultiFileList != null && !inputMultiFileList.isEmpty()) {
				for (String filePath : inputMultiFileList) {
					FileUtils.forceDelete(Paths.get(filePath).toFile());
				}
			}
		} catch (Exception e) {
			throw new ZipFileCreationException(ErrorCodes.PRG_DATA_SYNC_005.toString(),
					ErrorMessages.FAILED_TO_CREATE_A_ZIP_FILE.toString(),e.getCause());
		} finally {
			inputMultiFileList.clear();
		}
		return preRegArchiveDTO;
	}

	/**
	 * @param inputFIle
	 * @return compressed Zip
	 * @throws IOException
	 */
	private static byte[] getCompressed(List<String> inputFIle) {
		File fileToZip = null;
		List<String> srcFiles = new ArrayList<>();
		srcFiles.addAll(inputFIle);
		byte[] byteArray = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
		try {
			for (String srcFile : srcFiles) {
				fileToZip = new File(srcFile);
				try (FileInputStream fileInputStream = new FileInputStream(fileToZip);
						BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 1024)) {
					ZipEntry entry = new ZipEntry(fileToZip.getName());
					zipOutputStream.putNextEntry(entry);
					readFile(zipOutputStream, fileInputStream);
				} catch (FileNotFoundException e) {
					throw new SystemFileNotFoundException(ErrorCodes.PRG_DATA_SYNC_015.toString(),
							ErrorMessages.FILE_NOT_FOUND.toString(), e.getCause());
				} catch (IOException e) {
					throw new SystemFileIOException(ErrorCodes.PRG_DATA_SYNC_014.toString(),
							ErrorMessages.FILE_IO_EXCEPTION.toString(), e.getCause());
				}
			}
			zipOutputStream.close();
			byteArray = byteArrayOutputStream.toByteArray();
		} catch (IOException e1) {
			throw new SystemFileIOException(ErrorCodes.PRG_DATA_SYNC_014.toString(),
					ErrorMessages.FILE_IO_EXCEPTION.toString(), e1.getCause());
		}

		return byteArray;
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

	public boolean parseDate(String reqDate, String format) {
		try {
			new SimpleDateFormat(format).parse(reqDate);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * This method is used as Null checker for different input keys.
	 *
	 * @param key
	 * @return true if key not null and return false if key is null.
	 */
	public boolean isNull(Object key) {
		if (key instanceof String) {
			if (key.equals(""))
				return true;
		} else if (key instanceof List<?>) {
			if (((List<?>) key).isEmpty())
				return true;
		} else {
			if (key == null)
				return true;
		}
		return false;

	}

	private void outputStream(File file, byte[] docBytes, List<String> inputMultiFileList) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			fileOutputStream.write(docBytes);
			inputMultiFileList.add(file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			throw new SystemFileNotFoundException(ErrorCodes.PRG_DATA_SYNC_015.toString(),
					ErrorMessages.FILE_NOT_FOUND.toString(),e.getCause());
		} catch (IOException e) {
			throw new SystemFileIOException(ErrorCodes.PRG_DATA_SYNC_014.toString(),
					ErrorMessages.FILE_IO_EXCEPTION.toString(),e.getCause());
		}
	}

	public PreRegistrationIdsDTO getLastUpdateTimeStamp(List<String> preRegIds) {
		PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
		Map<String, String> preRegMap = new HashMap<>();
		for (String preRegId : preRegIds) {
			CreateDemographicDTO demographicDTO = callGetPreRegInfoRestService(preRegId);
			preRegMap.put(preRegId, new SimpleDateFormat(dateTimeFormat).format(demographicDTO.getUpdatedDateTime()));
		}
		preRegistrationIdsDTO.setCountOfPreRegIds(String.valueOf(preRegMap.size()));
		preRegistrationIdsDTO.setPreRegistrationIds(preRegMap);
		preRegistrationIdsDTO.setTransactionId(UUIDGeneratorUtil.generateId());
		return preRegistrationIdsDTO;
	}

	public ReverseDatasyncReponseDTO reverseDateSyncSave(Date reqDateTime, ReverseDataSyncRequestDTO request) {
		List<InterfaceDataSyncEntity> entityList = new ArrayList<>();
		List<ProcessedPreRegEntity> processedEntityList = new ArrayList<>();
		List<String> preIdList = request.getPreRegistrationIds();
		if (preIdList != null && !preIdList.isEmpty()) {
			for (String preRegId : preIdList) {
				InterfaceDataSyncEntity interfaceDataSyncEntity = new InterfaceDataSyncEntity();
				InterfaceDataSyncTablePK ipprlstPK = new InterfaceDataSyncTablePK();
				ipprlstPK.setPreregId(preRegId);
				ipprlstPK.setReceivedDtimes(DateUtils.parseDateToLocalDateTime(reqDateTime));
				interfaceDataSyncEntity.setIpprlst_PK(ipprlstPK);
				interfaceDataSyncEntity.setLangCode(request.getLangCode());
				interfaceDataSyncEntity.setCreatedBy(request.getCreatedBy());
				interfaceDataSyncEntity
						.setCreatedDate(DateUtils.parseDateToLocalDateTime(request.getCreatedDateTime()));
				interfaceDataSyncEntity
						.setUpdatedDate(DateUtils.parseDateToLocalDateTime(request.getCreatedDateTime()));
				entityList.add(interfaceDataSyncEntity);

				ProcessedPreRegEntity processedPreRegEntity = new ProcessedPreRegEntity();
				processedPreRegEntity.setPreRegistrationId(preRegId);
				processedPreRegEntity.setReceivedDTime(DateUtils.parseDateToLocalDateTime(reqDateTime));
				processedPreRegEntity.setStatusCode(StatusCodes.CONSUMED.getCode());
				processedPreRegEntity.setStatusComments("Processed by registration processor");
				processedPreRegEntity.setLangCode(request.getLangCode());
				processedPreRegEntity.setCrBy(request.getCreatedBy());
				processedPreRegEntity.setCrDate(DateUtils.parseDateToLocalDateTime(request.getCreatedDateTime()));
				processedPreRegEntity.setUpdDate(DateUtils.parseDateToLocalDateTime(request.getCreatedDateTime()));
				processedEntityList.add(processedPreRegEntity);
			}
		}
		return storeReverseDataSync(entityList, processedEntityList);
	}

	public ReverseDatasyncReponseDTO storeReverseDataSync(List<InterfaceDataSyncEntity> entityList,
			List<ProcessedPreRegEntity> processedEntityList) {
		int savedListSize = 0;
		int alreadyProcessedSize = 0;
		ReverseDatasyncReponseDTO reponseDTO = new ReverseDatasyncReponseDTO();
		try {
			if (entityList.size() == processedEntityList.size()) {
				List<InterfaceDataSyncEntity> savedList = interfaceDataSyncRepo.saveAll(entityList);
				if (!savedList.isEmpty()) {
					savedListSize = savedList.size();
					for (ProcessedPreRegEntity processedEntity : processedEntityList) {
						if (!processedDataSyncRepo.existsById(processedEntity.getPreRegistrationId())) {
							processedDataSyncRepo.save(processedEntity);
						} else {
							alreadyProcessedSize++;
						}
					}
					reponseDTO.setCountOfStoredPreRegIds(String.valueOf(savedListSize));
					reponseDTO.setAlreadyStoredPreRegIds(String.valueOf(alreadyProcessedSize));
					reponseDTO.setTransactionId(UUIDGeneratorUtil.generateId());
				}
			}
		} catch (DataAccessLayerException e) {
			throw new ReverseDataFailedToStoreException(ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());
		}
		return reponseDTO;
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

	public String getDateString(Date date) {
		return DateUtils.formatDate(date, dateTimeFormat);
	}
}
