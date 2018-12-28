package io.mosip.pregistration.datasync.service.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.pregistration.datasync.code.RequestCodes;
import io.mosip.pregistration.datasync.dto.BookingRegistrationDTO;
import io.mosip.pregistration.datasync.dto.BookingResponseDTO;
import io.mosip.pregistration.datasync.dto.CreateDemographicDTO;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.DataSyncResponseDTO;
import io.mosip.pregistration.datasync.dto.DocumentServiceDTO;
import io.mosip.pregistration.datasync.dto.MainRequestDTO;
import io.mosip.pregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.pregistration.datasync.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.pregistration.datasync.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.entity.InterfaceDataSyncTablePK;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;
import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.pregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.pregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.exception.DemographicGetDetailsException;
import io.mosip.pregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.pregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.pregistration.datasync.exception.ZipFileCreationException;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;
import io.mosip.pregistration.datasync.repository.ReverseDataSyncRepo;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

/**
 * @author Ravi C Balaji
 * @since 1.0.0
 */
@Component
public class DataSyncServiceUtil {
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

	/**
	 * Reference for ${appointment.resource.url} from property file
	 */
	/*@Value("${appointment.resource.url}")
	private String appointmentResourseUrl;*/

	/**
	 * This method is used to add the initial request values into a map for input
	 * validations.
	 * 
	 * @param RequestDto<DataSyncRequestDTO>
	 * @return a map for request input validation
	 */
	public Map<String, String> prepareRequestParamMap(MainRequestDTO<DataSyncRequestDTO> requestDto) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.toString(), requestDto.getId());
		inputValidation.put(RequestCodes.ver.toString(), requestDto.getVer());
		inputValidation.put(RequestCodes.reqTime.toString(),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(requestDto.getReqTime()));
		inputValidation.put(RequestCodes.request.toString(), requestDto.getRequest().toString());
		return inputValidation;
	}

	public boolean validateRequest(DataSyncRequestDTO dataSyncRequestDTO) {
		String regId = dataSyncRequestDTO.getRegClientId();
		String fromDate = dataSyncRequestDTO.getFromDate();
		String toDate = dataSyncRequestDTO.getToDate();
		String userId = dataSyncRequestDTO.getUserId();
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> callGetPreIdsRestService(String fromDate, String toDate) {

		List<String> responseList = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(demographicResourceUrl + "/applicationDataByDateTime").queryParam("fromDate", fromDate)
					.queryParam("toDate", toDate);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<ResponseDTO> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			System.out.println("uriBuilder: " + uriBuilder);
			ResponseEntity<ResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					ResponseDTO.class);
			ObjectMapper mapper = new ObjectMapper();
			responseList = mapper.convertValue(respEntity.getBody().getResponse(), List.class);

			if (responseList == null || responseList.isEmpty()) {
				throw new RecordNotFoundForDateRange(ErrorMessages.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
			}
		} catch (RestClientException e) {
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), e.getCause());
		}
		return responseList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
			preRegIdsByRegCenterIdDTO.setRegistration_center_id(regCenterId);
			preRegIdsByRegCenterIdDTO.setPre_registration_ids(preRegIds);
			MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDto = new MainRequestDTO<>();
			requestDto.setRequest(preRegIdsByRegCenterIdDTO);

			HttpEntity<BookingResponseDTO<?>> httpEntity = new HttpEntity(requestDto, headers);

			String uriBuilder = builder.build().encode().toUriString();
			System.out.println("uriBuilder: " + uriBuilder);
			ResponseEntity<ResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.POST,
					httpEntity, ResponseDTO.class);
			ObjectMapper mapper = new ObjectMapper();
			idResponseDTO = mapper.convertValue(respEntity.getBody().getResponse().get(0),
					PreRegIdsByRegCenterIdResponseDTO.class);
		} catch (RestClientException e) {
			e.printStackTrace();
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), e.getCause());
		}
		return idResponseDTO;
	}

	@SuppressWarnings({ "rawtypes" })
	public List<DocumentServiceDTO> callGetDocRestService(String preId) {
		List<DocumentServiceDTO> responsestatusDto = new ArrayList<>();
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(documentResourceUrl + "/getDocument")
					.queryParam("preId", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<ResponseDTO> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			System.out.println("uriBuilder: " + uriBuilder);
			ResponseEntity<ResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					ResponseDTO.class);
			ObjectMapper mapper = new ObjectMapper();
			for (Object obj : respEntity.getBody().getResponse()) {
				responsestatusDto.add(mapper.convertValue(obj, DocumentServiceDTO.class));
			}
		} catch (RestClientException e) {
			return responsestatusDto;
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
			HttpEntity<ResponseDTO<?>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			@SuppressWarnings("rawtypes")
			ResponseEntity<ResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					ResponseDTO.class);
			ObjectMapper mapper = new ObjectMapper();
			CreateDemographicDTO responsestatusDto = mapper.convertValue(respEntity.getBody().getResponse().get(0),
					CreateDemographicDTO.class);
			if (responsestatusDto == null) {
				throw new DataSyncRecordNotFoundException(
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
	@SuppressWarnings({ "rawtypes" })
	public BookingRegistrationDTO callGetAppointmentDetailsRestService(String preId) {

		BookingRegistrationDTO bookingRegistrationDTO = null;
		try {
			RestTemplate restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(bookingResourceUrl + "/appointmentDetails")
					.queryParam("preRegID", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<BookingResponseDTO> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			System.out.println("uriBuilder: " + uriBuilder);
			ResponseEntity<BookingResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					BookingResponseDTO.class);
			System.out.println(respEntity);
			ObjectMapper mapper = new ObjectMapper();
			bookingRegistrationDTO = mapper.convertValue(respEntity.getBody().getResponse(), BookingRegistrationDTO.class);

			if (bookingRegistrationDTO == null) {
				throw new RecordNotFoundForDateRange(ErrorMessages.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
			}
		} catch (RestClientException e) {
			e.printStackTrace();
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), e.getCause());
		}
		return bookingRegistrationDTO;
	}


	/**
	 * @param preRegistrationEntity
	 * @param documentEntityList
	 * @return zipped file's byte array
	 * @throws IOException
	 */
	public PreRegArchiveDTO archivingFiles(CreateDemographicDTO preRegistrationDTO,
			BookingRegistrationDTO bookingRegistrationDTO, List<DocumentServiceDTO> documentEntityList) {
		File fileDoc = null;
		List<String> inputMultiFileList = new ArrayList<>();
		Path pathDoc = null;
		PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
		try {
			preRegArchiveDTO.setPre_registration_id(preRegistrationDTO.getPreRegistrationId());
			preRegArchiveDTO.setRegistration_center_id(bookingRegistrationDTO.getRegistrationCenterId());
			preRegArchiveDTO.setAppointment_date(bookingRegistrationDTO.getRegDate());
			preRegArchiveDTO.setTime_slot_from(bookingRegistrationDTO.getSlotFromTime());
			preRegArchiveDTO.setTime_slot_to(bookingRegistrationDTO.getSlotToTime());

			JSONObject demographicJsonObject = preRegistrationDTO.getDemographicDetails();

			pathDoc = Paths.get(System.getProperty("java.io.tmpdir") + File.separator
					+ preRegistrationDTO.getPreRegistrationId().trim() + "_Demographic" + ".json");

			File jsonFile = new File(pathDoc.toString());
			if (jsonFile.exists()) {
				Files.deleteIfExists(pathDoc);
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
						Files.deleteIfExists(pathDoc);
					}
					if (fileDoc.createNewFile()) {
						outputStream(fileDoc, docBytes, inputMultiFileList);
					}
					inputMultiFileList.add(fileDoc.getAbsolutePath());
				}

			}
			preRegArchiveDTO.setZipBytes(getCompressed(inputMultiFileList));
			preRegArchiveDTO.setFileName(preRegistrationDTO.getPreRegistrationId());
		} catch (Exception e) {
			throw new ZipFileCreationException(ErrorMessages.FAILED_TO_CREATE_A_ZIP_FILE.toString());
		} finally {
			if (inputMultiFileList != null && !inputMultiFileList.isEmpty()) {
				for (String s : inputMultiFileList) {
					Path path = Paths.get(s);
					try {
						Files.deleteIfExists(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			inputMultiFileList.clear();
		}
		return preRegArchiveDTO;
	}

	/**
	 * @param inputFIle
	 * @return compressed Zip
	 * @throws IOException
	 */
	private static byte[] getCompressed(List<String> inputFIle) throws IOException {
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reverseDatasyncEntitySetter(ReverseDataSyncDTO reverseDto, List<ReverseDataSyncEntity> entityList,
			List<PreRegistrationProcessedEntity> processedEntityList, List<String> preIdList, int i) {
		ReverseDataSyncEntity reverseEntity = new ReverseDataSyncEntity();
		InterfaceDataSyncTablePK ipprlstPK = new InterfaceDataSyncTablePK();
		ipprlstPK.setPreregId(preIdList.get(i));
		ipprlstPK.setReceivedDtimes(new Timestamp(reverseDto.getReqTime().getTime()));
		reverseEntity.setIpprlst_PK(ipprlstPK);
		reverseEntity.setLangCode("AR");
		reverseEntity.setCrBy("5766477466");
		reverseEntity.setCrDate(new Timestamp(System.currentTimeMillis()));
		entityList.add(reverseEntity);

		PreRegistrationProcessedEntity processedEntity = new PreRegistrationProcessedEntity();
		processedEntity.setPreRegistrationId(preIdList.get(i));
		processedEntity.setReceivedDTime(new Timestamp(reverseDto.getReqTime().getTime()));
		processedEntity.setStatusCode("Processed");
		processedEntity.setStatusComments("Processed by registration processor");
		processedEntity.setLangCode("AR");
		processedEntity.setCrBy("5766477466");
		processedEntity.setCrDate(new Timestamp(System.currentTimeMillis()));
		processedEntityList.add(processedEntity);
	}

	public void reverseDatasyncSave(DataSyncResponseDTO<String> responseDto, List<ReverseDataSyncEntity> entityList,
			List<PreRegistrationProcessedEntity> processedEntityList, ReverseDataSyncRepo reverseDataSyncRepo,
			DataSyncRepository dataSyncRepo) {
		List<ReverseDataSyncEntity> savedList = dataSyncRepo.saveAll(entityList);
		if (savedList != null && !savedList.isEmpty()) {
			for (PreRegistrationProcessedEntity processedEntity : processedEntityList) {
				if (!reverseDataSyncRepo.existsById(processedEntity.getPreRegistrationId()))
					reverseDataSyncRepo.save(processedEntity);
			}
			responseDto.setResponse(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());

		} else {
			throw new ReverseDataFailedToStoreException(ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());
		}
	}

	public Map<String, String> getLastUpdateTimeStamp(List<String> preRegIds) {
		Map<String, String> preRegMap = new HashMap<>();
		for (String preRegId : preRegIds) {
			CreateDemographicDTO demographicDTO = callGetPreRegInfoRestService(preRegId);
			preRegMap.put(preRegId, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(demographicDTO.getUpdatedDateTime()));
		}
		return preRegMap;
	}
}
