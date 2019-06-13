package io.mosip.preregistration.datasync.service.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections4.SetValuedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentDTO;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentsMetaData;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.datasync.code.RequestCodes;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.DocumentMetaDataDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.entity.InterfaceDataSyncEntity;
import io.mosip.preregistration.datasync.entity.InterfaceDataSyncTablePK;
import io.mosip.preregistration.datasync.entity.ProcessedPreRegEntity;
import io.mosip.preregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.datasync.exception.DemographicGetDetailsException;
import io.mosip.preregistration.datasync.exception.DocumentGetDetailsException;
import io.mosip.preregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.preregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.preregistration.datasync.exception.ZipFileCreationException;
import io.mosip.preregistration.datasync.exception.system.SystemFileIOException;
import io.mosip.preregistration.datasync.repository.InterfaceDataSyncRepo;
import io.mosip.preregistration.datasync.repository.ProcessedDataSyncRepo;

/**
 * @author Ravi C Balaji
 * @author Sanober Noor
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
	 * Autowired reference for {@link #RestTemplate}
	 */
	@Autowired
	RestTemplate restTemplate;

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
	@Value("${poa.url}")
	private String poaUrl;

	/**
	 * Reference for ${booking.resource.url} from property file
	 */
	@Value("${poi.url}")
	private String poiUrl;

	/**
	 * Reference for ${booking.resource.url} from property file
	 */
	@Value("${por.url}")
	private String porUrl;

	/**
	 * Reference for ${booking.resource.url} from property file
	 */
	@Value("${pod.url}")
	private String podUrl;

	/**
	 * Reference for ${booking.resource.url} from property file
	 */
	@Value("${booking.resource.url}")
	private String bookingResourceUrl;
	//
	// @Value("${mosip.kernel.idobjectvalidator.masterdata.documenttypes.rest.uri}")
	// private String documentTypeUri;

	@Autowired
	ValidationUtil validationUtil;

	/** The doc cat map. */
	private SetValuedMap<String, String> docCatMap;

	private static final String DOCUMENTS = "documents";

	private static final String DOCUMENTCATEGORIES = "documentcategories";

	private static final String IS_ACTIVE = "isActive";

	private static final String CODE = "code";

	private static final String NAME = "name";
	private ObjectMapper mapper = new ObjectMapper();

	@Value("${mosip.utc-datetime-pattern}")
	private String dateTimeFormat;

	private static Logger log = LoggerConfiguration.logConfig(DataSyncServiceUtil.class);

	public boolean validateDataSyncRequest(DataSyncRequestDTO dataSyncRequest, MainResponseDTO<?> mainResponseDTO) {
		log.info("sessionId", "idType", "id", "In validateDataSyncRequest method of datasync service util");
		String regId = dataSyncRequest.getRegistrationCenterId();
		String fromDate = dataSyncRequest.getFromDate();
		String format = "yyyy-MM-dd";

		if (isNull(regId)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_009.toString(),
					ErrorMessages.INVALID_REGISTRATION_CENTER_ID.getMessage(), mainResponseDTO);
		} else if (isNull(fromDate) || !parseDate(fromDate, format)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_010.toString(),
					ErrorMessages.INVALID_REQUESTED_DATE.getMessage(), mainResponseDTO);
		} else if (!isNull(dataSyncRequest.getToDate()) && !parseDate(dataSyncRequest.getToDate(), format)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_010.toString(),
					ErrorMessages.INVALID_REQUESTED_DATE.getMessage(), mainResponseDTO);
		}
		return true;
	}

	public boolean validateReverseDataSyncRequest(ReverseDataSyncRequestDTO reverseDataSyncRequest,
			MainResponseDTO<?> mainResponseDTO) {
		log.info("sessionId", "idType", "id", "In validateReverseDataSyncRequest method of datasync service util");
		List<String> preRegIdsList = reverseDataSyncRequest.getPreRegistrationIds();
		if (preRegIdsList == null || isNull(preRegIdsList)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_DATA_SYNC_011.toString(),
					ErrorMessages.INVALID_REQUESTED_PRE_REG_ID_LIST.getMessage(), mainResponseDTO);
		}
		return true;
	}

	public PreRegIdsByRegCenterIdResponseDTO getBookedPreIdsByDateAndRegCenterIdRestService(String fromDate,
			String toDate, String regCenterId) {
		log.info("sessionId", "idType", "id", "In callGetPreIdsRestService method of datasync service util");
		PreRegIdsByRegCenterIdResponseDTO preRegIdsByRegCenterIdResponseDTO = null;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("registrationCenterId", regCenterId);
			UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
					.fromHttpUrl(bookingResourceUrl + "/appointment/preRegistrationId/{registrationCenterId}");

			URI uri = uriComponentsBuilder.buildAndExpand(params).toUri();
			UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri).queryParam("from_date", fromDate)
					.queryParam("to_date", toDate);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode(StandardCharsets.UTF_8).toUriString();
			log.info("sessionId", "idType", "id", "In callGetPreIdsRestService method URL- " + uriBuilder);
			ResponseEntity<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>> respEntity = restTemplate.exchange(
					uriBuilder, HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO>>() {
					}, params);
			if (respEntity.getBody().getErrors() != null) {
				for (ExceptionJSONInfoDTO exceptionJSONInfoDTO : respEntity.getBody().getErrors()) {
					if (exceptionJSONInfoDTO != null) {
						throw new RecordNotFoundForDateRange(exceptionJSONInfoDTO.getErrorCode(),
								exceptionJSONInfoDTO.getMessage(), null);
					}
				}
			} else {
				preRegIdsByRegCenterIdResponseDTO = mapper.convertValue(respEntity.getBody().getResponse(),
						PreRegIdsByRegCenterIdResponseDTO.class);
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetPreIdsRestService method of datasync service util - " + ex.getMessage());

			throw new RecordNotFoundForDateRange(ErrorCodes.PRG_DATA_SYNC_016.toString(),
					ErrorMessages.BOOKING_NOT_FOUND.getMessage(), null);

		}
		return preRegIdsByRegCenterIdResponseDTO;
	}

	public DocumentsMetaData getDocDetails(String preId) {
		log.info("sessionId", "idType", "id", "In callGetDocDetailsRestService method of datasync service util");
		DocumentsMetaData responsestatusDto = new DocumentsMetaData();
		try {
			Map<String, String> params = new HashMap<>();
			params.put("preRegistrationId", preId);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(documentResourceUrl + "/documents/preregistration/");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<DocumentsMetaData>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			uriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id", "In callGetDocRestService method URL- " + uriBuilder);
			ResponseEntity<MainResponseDTO<DocumentsMetaData>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity, new ParameterizedTypeReference<MainResponseDTO<DocumentsMetaData>>() {
					}, params);
			if (respEntity.getBody().getErrors() != null) {
				log.info("sessionId", "idType", "id",
						"In callGetDocRestService method of datasync service util - Document not found for the pre_registration_id");
			} else {
				Object obj = respEntity.getBody().getResponse();
				responsestatusDto = mapper.convertValue(obj, DocumentsMetaData.class);
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetDocRestService method of datasync service util - " + ex.getMessage());

			throw new DocumentGetDetailsException(ErrorCodes.PRG_DATA_SYNC_006.toString(),
					ErrorMessages.FAILED_TO_FETCH_DOCUMENT.getMessage(), null);
		}
		return responsestatusDto;
	}

	public DocumentDTO getDocBytesDetails(String docId, String preId) {
		log.info("sessionId", "idType", "id", "In callGetBytesDocRestService method of datasync service util");
		DocumentDTO responsestatusDto = new DocumentDTO();
		try {
			Map<String, String> params = new HashMap<>();
			params.put("documentId", docId);

			UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
					.fromHttpUrl(documentResourceUrl + "/documents/{documentId}");
			URI uri = uriComponentsBuilder.buildAndExpand(params).toUri();
			UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri).queryParam("preRegistrationId", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<DocumentDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			log.info("sessionId", "idType", "id", "In callGetBytesDocRestService method URL- " + uriBuilder);
			ResponseEntity<MainResponseDTO<DocumentDTO>> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET,
					httpEntity, new ParameterizedTypeReference<MainResponseDTO<DocumentDTO>>() {
					}, params);
			if (respEntity.getBody().getErrors() != null) {
				log.info("sessionId", "idType", "id",
						"In callGetBytesDocRestService method of datasync service util - Document not found for the documentId");
			} else {
				Object obj = respEntity.getBody().getResponse();
				responsestatusDto = mapper.convertValue(obj, DocumentDTO.class);
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetBytesDocRestService method of datasync service util - " + ex.getMessage());

			throw new DocumentGetDetailsException(ErrorCodes.PRG_DATA_SYNC_006.toString(),
					ErrorMessages.FAILED_TO_FETCH_DOCUMENT.getMessage(), null);
		}
		return responsestatusDto;
	}

	public DemographicResponseDTO getPreRegistrationData(String preId) {
		log.info("sessionId", "idType", "id", "In callGetPreRegInfoRestService method of datasync service util");
		DemographicResponseDTO responsestatusDto = new DemographicResponseDTO();
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preId);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(demographicResourceUrl + "/applications/");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<DemographicResponseDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			uriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id", "In callGetPreRegInfoRestService method URL- " + uriBuilder);
			ResponseEntity<MainResponseDTO<DemographicResponseDTO>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<DemographicResponseDTO>>() {
					}, params);
			if (respEntity.getBody().getErrors() != null) {
				for (ExceptionJSONInfoDTO exceptionJSONInfoDTO : respEntity.getBody().getErrors()) {
					if (exceptionJSONInfoDTO != null) {
						throw new DemographicGetDetailsException(exceptionJSONInfoDTO.getErrorCode(),
								exceptionJSONInfoDTO.getMessage(), null);
					}
				}

			} else {
				responsestatusDto = mapper.convertValue(respEntity.getBody().getResponse(),
						DemographicResponseDTO.class);
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetPreRegInfoRestService method of datasync service util - " + ex.getMessage());

			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.getMessage(), null);
		}
		return responsestatusDto;
	}

	/**
	 * This private Method is used to retrieve booking data by date
	 * 
	 * @param preId
	 * @return BookingRegistrationDTO
	 * 
	 */
	public BookingRegistrationDTO getAppointmentDetails(String preId) {
		log.info("sessionId", "idType", "id",
				"In callGetAppointmentDetailsRestService method of datasync service util");
		BookingRegistrationDTO bookingRegistrationDTO = null;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("preRegistrationId", preId);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(bookingResourceUrl + "/appointment/");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<BookingRegistrationDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			uriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id", "In callGetAppointmentDetailsRestService method URL- " + uriBuilder);
			ResponseEntity<MainResponseDTO<BookingRegistrationDTO>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<BookingRegistrationDTO>>() {
					}, params);
			if (respEntity.getBody().getErrors() != null) {
				for (ExceptionJSONInfoDTO exceptionJSONInfoDTO : respEntity.getBody().getErrors()) {
					if (exceptionJSONInfoDTO != null) {
						throw new DemographicGetDetailsException(exceptionJSONInfoDTO.getErrorCode(),
								exceptionJSONInfoDTO.getMessage(), null);
					}
				}
			} else {
				bookingRegistrationDTO = mapper.convertValue(respEntity.getBody().getResponse(),
						BookingRegistrationDTO.class);
				if (bookingRegistrationDTO == null) {
					throw new RecordNotFoundForDateRange(ErrorCodes.PRG_DATA_SYNC_001.toString(),
							ErrorMessages.RECORDS_NOT_FOUND_FOR_DATE_RANGE.getMessage(), null);
				}
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetAppointmentDetailsRestService method of datasync service util - " + ex.getMessage());

			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_016.toString(),
					ErrorMessages.BOOKING_NOT_FOUND.getMessage(), null);
		}
		return bookingRegistrationDTO;
	}

	/**
	 * @param preRegistrationEntity
	 * @param documentEntityList
	 * @return zipped file's byte array
	 */
	public PreRegArchiveDTO preparePreRegArchiveDTO(DemographicResponseDTO preRegistrationDTO,
			BookingRegistrationDTO bookingRegistrationDTO) {
		log.info("sessionId", "idType", "id", "In preparePreRegArchiveDTO method of datasync service util");
		PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
		preRegArchiveDTO.setPreRegistrationId(preRegistrationDTO.getPreRegistrationId());
		preRegArchiveDTO.setRegistrationCenterId(bookingRegistrationDTO.getRegistrationCenterId());
		preRegArchiveDTO.setAppointmentDate(bookingRegistrationDTO.getRegDate());
		preRegArchiveDTO.setTimeSlotFrom(bookingRegistrationDTO.getSlotFromTime());
		preRegArchiveDTO.setTimeSlotTo(bookingRegistrationDTO.getSlotToTime());
		return preRegArchiveDTO;
	}

	/**
	 * This method is used to set the JSON values to RequestCodes constants.
	 * 
	 * @param demographicData
	 *            pass demographicData
	 * @param identityKey
	 *            pass postalcode
	 * @return values from JSON
	 * 
	 * @throws ParseException
	 *             On json Parsing Failed
	 * @throws org.json.simple.parser.ParseException
	 * 
	 */

	public JSONObject getIdJSONValue(String demographicData) throws ParseException {
		log.info("sessionId", "idType", "id",
				"In getIdJSONValue method of datasync service util to get getIdJSONValue ");
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObj = (JSONObject) jsonParser.parse(demographicData);
		return (JSONObject) jsonObj.get(RequestCodes.IDENTITY.getCode());

	}

	/**
	 * @param preRegistrationEntity
	 * @param documentEntityList
	 * @return zipped file's byte array
	 */
	public PreRegArchiveDTO archivingFiles(DemographicResponseDTO preRegistrationDTO,
			BookingRegistrationDTO bookingRegistrationDTO, DocumentsMetaData documentEntityList) {
		log.info("sessionId", "idType", "id", "In archivingFiles method of datasync service util");
		PreRegArchiveDTO preRegArchiveDTO = null;
		try {
			preRegArchiveDTO = preparePreRegArchiveDTO(preRegistrationDTO, bookingRegistrationDTO);
			Map<String, byte[]> inputFile = new HashMap<>();
			JSONObject identityJson = getIdJSONValue(
					JsonUtils.javaObjectToJsonString(preRegistrationDTO.getDemographicDetails()));
			Map<String, Object> identityMap = JsonUtils
					.jsonStringToJavaMap(JsonUtils.javaObjectToJsonString(identityJson));
			Map<String, Object> finalMap = prepareIdentityMap(documentEntityList, inputFile, identityMap,
					preRegistrationDTO.getPreRegistrationId());
			log.info("sessionId", "idType", "id",
					"In archivingFiles method of datasync service util, Json file content - "
							+ new JSONObject(finalMap).toJSONString());
			inputFile.put("ID.json", new ObjectMapper().writeValueAsBytes(finalMap));
			preRegArchiveDTO.setZipBytes(getCompressed(inputFile));
			preRegArchiveDTO.setFileName(preRegistrationDTO.getPreRegistrationId());

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In archivingFiles method of datasync service util - " + ex.getMessage());
			throw new ZipFileCreationException(ErrorCodes.PRG_DATA_SYNC_005.toString(),
					ErrorMessages.FAILED_TO_CREATE_A_ZIP_FILE.getMessage(), null);
		}
		return preRegArchiveDTO;
	}

	private Map<String, Object> prepareIdentityMap(DocumentsMetaData documentsMetaData, Map<String, byte[]> inputFile,
			Map<String, Object> jsonObject, String preId) {
		Map<String, Object> identiyMap = new HashMap<>();
		if (!isNull(documentsMetaData) && !isNull(documentsMetaData.getDocumentsMetaData())) {
			for (DocumentMultipartResponseDTO documentMultipartResponseDTO : documentsMetaData.getDocumentsMetaData()) {
				if (documentMultipartResponseDTO.getDocCatCode().equals(RequestCodes.POA.getCode())) {
					jsonObject.put(poaUrl, prepareDocumentMetaData(documentMultipartResponseDTO));
				} else if (documentMultipartResponseDTO.getDocCatCode().equals(RequestCodes.POI.getCode())) {
					jsonObject.put(poiUrl, prepareDocumentMetaData(documentMultipartResponseDTO));
				} else if (documentMultipartResponseDTO.getDocCatCode().equals(RequestCodes.POR.getCode())) {
					jsonObject.put(porUrl, prepareDocumentMetaData(documentMultipartResponseDTO));
				} else if (documentMultipartResponseDTO.getDocCatCode().equals(RequestCodes.POD.getCode())
						|| documentMultipartResponseDTO.getDocCatCode().equals(RequestCodes.POB.getCode())) {
					jsonObject.put(podUrl, prepareDocumentMetaData(documentMultipartResponseDTO));
				}
				DocumentDTO documentDTO = getDocBytesDetails(documentMultipartResponseDTO.getDocumentId(), preId);
				if (documentDTO != null && documentDTO.getDocument() != null) {
					inputFile.put(documentMultipartResponseDTO.getDocCatCode().concat("_")
							.concat(documentMultipartResponseDTO.getDocName()), documentDTO.getDocument());
				}
			}
		}
		identiyMap.put(RequestCodes.IDENTITY.getCode(), jsonObject);
		return identiyMap;
	}

	private Map<String, Object> prepareDocumentMetaData(DocumentMultipartResponseDTO documentMultipartResponseDTO) {
		DocumentMetaDataDTO documentMetaDataDTO = new DocumentMetaDataDTO();
		documentMetaDataDTO.setValue(documentMultipartResponseDTO.getDocCatCode().concat("_")
				.concat(getFileNameWithoutFormat(documentMultipartResponseDTO.getDocName())));
		documentMetaDataDTO.setType(getTypeName(documentMultipartResponseDTO.getLangCode(),
				documentMultipartResponseDTO.getDocCatCode(), documentMultipartResponseDTO.getDocTypCode()));
		documentMetaDataDTO.setFormat(getFileFormat(documentMultipartResponseDTO.getDocName()));
		try {
			return JsonUtils.jsonStringToJavaMap(JsonUtils.javaObjectToJsonString(documentMetaDataDTO));
		} catch (JsonParseException | JsonMappingException | io.mosip.kernel.core.exception.IOException
				| JsonProcessingException ex) {
			log.error("sessionId", "idType", "id",
					"In prepareDocumentMetaData method of datasync service util for JSONException - "
							+ ex.getMessage());
		}
		return null;
	}

	public String getTypeName(String langCode, String catCode, String typeCode) {
		Map<String, String> documentTypeMap = validationUtil.getDocumentTypeNameByTypeCode(langCode, catCode);
		if (documentTypeMap.containsKey(typeCode)) {
			return documentTypeMap.get(typeCode);
		}
		return typeCode;
	}

	public String getFileNameWithoutFormat(String fileName) {
		String fileNameWithoutFormat = "";
		if (fileName.contains(".")) {
			fileNameWithoutFormat = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		return fileNameWithoutFormat;
	}

	/**
	 * @param inputFIle
	 * @return compressed Zip
	 * @throws IOException
	 */
	private static byte[] getCompressed(Map<String, byte[]> inputFIle) {
		log.info("sessionId", "idType", "id", "In getCompressed method of datasync service util");
		byte[] byteArray = null;
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

			for (Map.Entry<String, byte[]> entry : inputFIle.entrySet()) {
				zipping(entry.getKey(), entry.getValue(), zipOutputStream);
			}
			zipOutputStream.close();
			byteArray = byteArrayOutputStream.toByteArray();
		} catch (IOException ex) {
			log.error("sessionId", "idType", "id",
					"In getCompressed method of datasync service util for FileNotFoundException - " + ex.getMessage());
			throw new SystemFileIOException(ErrorCodes.PRG_DATA_SYNC_014.toString(),
					ErrorMessages.FILE_IO_EXCEPTION.getMessage(), null);
		}
		return byteArray;
	}

	private static void zipping(String fileName, byte[] fileToZip, ZipOutputStream zipOutputStream) {
		log.info("sessionId", "idType", "id", "In zipping method of datasync service util");

		try {
			ZipEntry entry = new ZipEntry(fileName);
			zipOutputStream.putNextEntry(entry);
			zipOutputStream.write(fileToZip);
			zipOutputStream.flush();
		} catch (IOException ex) {
			log.error("sessionId", "idType", "id",
					"In zipping method of datasync service util for IOException - " + ex.getMessage());
			throw new SystemFileIOException(ErrorCodes.PRG_DATA_SYNC_014.toString(),
					ErrorMessages.FILE_IO_EXCEPTION.getMessage(), null);
		}
	}

	/**
	 * @param zipOut
	 * @param fis
	 * @throws IOException
	 */

	public boolean parseDate(String reqDate, String format) {
		log.info("sessionId", "idType", "id", "In parseDate method of datasync service util");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
			sdf.parse(reqDate);
		} catch (Exception e) {
			log.error("sessionId", "idType", "id", "In parseDate method of datasync service util - " + e.getMessage());
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
		log.info("sessionId", "idType", "id", "In isNull method of datasync service util");
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

	public Map<String, String> getPreregistrationUpdatedTime(PreRegIdsByRegCenterIdDTO preRegIdsDTO) {
		log.info("sessionId", "idType", "id",
				"In callGetPreRegInfoRestService method of datasync service util " + preRegIdsDTO);
		Map<String, String> response = null;
		try {
			MainRequestDTO<PreRegIdsByRegCenterIdDTO> mainRequestDTO = new MainRequestDTO<>();
			mainRequestDTO.setRequest(preRegIdsDTO);
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(demographicResourceUrl + "/applications/updatedTime");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<Map<String, String>>> httpEntity = new HttpEntity(mainRequestDTO, headers);
			String uriBuilder = builder.build().encode().toUriString();
			ResponseEntity<MainResponseDTO<Map<String, String>>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.POST, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<Map<String, String>>>() {
					});
			if (respEntity.getBody().getErrors() != null) {
				throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_011.toString(),
						ErrorMessages.INVALID_REQUESTED_PRE_REG_ID_LIST.getMessage(), null);
			} else {
				response = mapper.convertValue(respEntity.getBody().getResponse(), Map.class);
			}
		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetUpdatedTimeRestService method of datasync service util - " + ex.getMessage());

			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.getMessage(), null);
		}
		return response;
	}

	public PreRegistrationIdsDTO getLastUpdateTimeStamp(PreRegIdsByRegCenterIdDTO preRegIdsDTO) {
		log.info("sessionId", "idType", "id", "In getLastUpdateTimeStamp method of datasync service util");
		PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
		Map<String, String> preRegMap = getPreregistrationUpdatedTime(preRegIdsDTO);
		preRegistrationIdsDTO.setCountOfPreRegIds(String.valueOf(preRegMap.size()));
		preRegistrationIdsDTO.setPreRegistrationIds(preRegMap);
		preRegistrationIdsDTO.setTransactionId(UUIDGeneratorUtil.generateId());
		return preRegistrationIdsDTO;
	}

	public ReverseDatasyncReponseDTO reverseDateSyncSave(Date reqDateTime, ReverseDataSyncRequestDTO request,
			String userId) {
		log.info("sessionId", "idType", "id", "In reverseDateSyncSave method of datasync service util");
		List<InterfaceDataSyncEntity> entityList = new ArrayList<>();
		List<ProcessedPreRegEntity> processedEntityList = new ArrayList<>();
		List<String> preIdLists = request.getPreRegistrationIds();
		PreRegIdsByRegCenterIdDTO preRegIdsDTO = new PreRegIdsByRegCenterIdDTO();
		preRegIdsDTO.setPreRegistrationIds(preIdLists);
		Map<String, String> preIdsMap = getPreregistrationUpdatedTime(preRegIdsDTO);

		List<String> uniquePreIds = new ArrayList<>(preIdsMap.keySet());
		if (!uniquePreIds.isEmpty()) {
			for (String preRegId : uniquePreIds) {
				InterfaceDataSyncEntity interfaceDataSyncEntity = new InterfaceDataSyncEntity();
				InterfaceDataSyncTablePK ipprlstPK = new InterfaceDataSyncTablePK();
				ipprlstPK.setPreregId(preRegId);
				ipprlstPK.setReceivedDtimes(DateUtils.parseDateToLocalDateTime(reqDateTime));
				interfaceDataSyncEntity.setIpprlst_PK(ipprlstPK);
				interfaceDataSyncEntity.setLangCode("eng");
				interfaceDataSyncEntity.setCreatedBy(userId);
				interfaceDataSyncEntity.setCreatedDate(DateUtils.parseToLocalDateTime(getCurrentResponseTime()));
				interfaceDataSyncEntity.setUpdatedDate(DateUtils.parseToLocalDateTime(getCurrentResponseTime()));
				entityList.add(interfaceDataSyncEntity);

				ProcessedPreRegEntity processedPreRegEntity = new ProcessedPreRegEntity();
				processedPreRegEntity.setPreRegistrationId(preRegId);
				processedPreRegEntity.setReceivedDTime(DateUtils.parseDateToLocalDateTime(reqDateTime));
				processedPreRegEntity.setStatusCode(StatusCodes.CONSUMED.getCode());
				processedPreRegEntity.setStatusComments("Processed by registration processor");
				processedPreRegEntity.setLangCode("eng");
				processedPreRegEntity.setCrBy(userId);
				processedPreRegEntity.setCrDate(DateUtils.parseToLocalDateTime(getCurrentResponseTime()));
				processedPreRegEntity.setUpdDate(DateUtils.parseToLocalDateTime(getCurrentResponseTime()));
				processedEntityList.add(processedPreRegEntity);
			}
		}
		return storeReverseDataSync(entityList, processedEntityList);
	}

	public ReverseDatasyncReponseDTO storeReverseDataSync(List<InterfaceDataSyncEntity> entityList,
			List<ProcessedPreRegEntity> processedEntityList) {
		log.info("sessionId", "idType", "id", "In storeReverseDataSync method of datasync service util");
		int savedListSize = 0;
		List<String> preIds = new ArrayList<>();
		ReverseDatasyncReponseDTO reponseDTO = new ReverseDatasyncReponseDTO();
		try {
			if (entityList.size() == processedEntityList.size()) {
				List<InterfaceDataSyncEntity> savedList = interfaceDataSyncRepo.saveAll(entityList);
				if (!savedList.isEmpty()) {
					savedListSize = savedList.size();
					for (ProcessedPreRegEntity processedEntity : processedEntityList) {
						preIds.add(processedEntity.getPreRegistrationId());
						if (!processedDataSyncRepo.existsById(processedEntity.getPreRegistrationId())) {
							processedDataSyncRepo.save(processedEntity).getPreRegistrationId();
						}
					}
					reponseDTO.setCountOfStoredPreRegIds(String.valueOf(savedListSize));
					reponseDTO.setPreRegistrationIds(preIds);
					reponseDTO.setTransactionId(UUIDGeneratorUtil.generateId());
				}
			}
		} catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id",
					"In storeReverseDataSync method of datasync service util - " + ex.getMessage());
			throw new ReverseDataFailedToStoreException(ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.getMessage(),
					null);
		}
		return reponseDTO;
	}

	public String getCurrentResponseTime() {
		log.info("sessionId", "idType", "id", "In getCurrentResponseTime method of datasync service util");
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

	public String getDateString(Date date) {
		log.info("sessionId", "idType", "id", "In getDateString method of datasync service util");
		return DateUtils.formatDate(date, dateTimeFormat);
	}

	public Map<String, String> prepareRequestMap(MainRequestDTO<?> requestDto) {
		log.info("sessionId", "idType", "id", "In prepareRequestMap method of datasync service util");
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("id", requestDto.getId());
		requestMap.put("version", requestDto.getVersion());
		if (!(requestDto.getRequesttime() == null || requestDto.getRequesttime().toString().isEmpty())) {
			LocalDate date = requestDto.getRequesttime().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
			requestMap.put("requesttime", date.toString());
		} else {
			requestMap.put("requesttime", null);
		}
		requestMap.put("request", requestDto.getRequest().toString());
		return requestMap;
	}

	public String getFileFormat(String fileName) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}
	/*
	 * This method can be used to create zip file in mentioned path during zip
	 * creation.
	 * 
	 * Call this method in archiving method
	 */

	// public static void parsejson()
	// throws IOException, JSONException,
	// io.mosip.kernel.core.exception.IOException, java.io.IOException {
	// try (BufferedReader bufferedReader = new BufferedReader(
	// new FileReader(new File("C:/Users/M1046129/Desktop/idjson.json")))) {
	// StringBuilder jsonBuilder = new StringBuilder();
	// String value;
	// while ((value = bufferedReader.readLine()) != null) {
	// jsonBuilder.append(value);
	// }
	// ObjectMapper mapper = new ObjectMapper();
	// PreRegArchiveDTO archiveDTO = mapper.readValue(
	// new org.json.JSONObject(jsonBuilder.toString()).getString("response"),
	// PreRegArchiveDTO.class); // change
	// FileUtils.copyToFile(new ByteArrayInputStream(archiveDTO.getZipBytes()),
	// new File("C:/Users/M1046129/Desktop/preZipNew.zip"));
	// System.out.println("zip file saved");
	// }
	//
	// }

}
