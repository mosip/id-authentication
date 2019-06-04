/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.service.util;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorDocumentMapping;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectMasterDataValidator;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.HashUtill;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.DemographicGetDetailsException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.InvalidDocumentIdExcepion;

/**
 * This class provides the utility methods for DocumentService
 * 
 * @author Rajath KR
 * @author Tapaswini
 * @since 1.0.0
 */
@Component
public class DocumentServiceUtil {

	/**
	 * Autowired reference for {@link #VirusScanner}
	 */
	@Autowired
	private VirusScanner<Boolean, InputStream> virusScan;

	/**
	 * Reference for ${max.file.size} from property file
	 */
	@Value("${max.file.size}")
	private int maxFileSize;

	@Autowired
	private IdObjectMasterDataValidator idObjectMasterDataValidator;

	/**
	 * Reference for ${file.extension} from property file
	 */
	@Value("${preregistration.document.extention}")
	private String fileExtension;

	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	@Value("${mosip.kernel.idobjectvalidator.masterdata.documentcategories.rest.uri}")
	private String documentCategoryUri;

	@Value("${mosip.kernel.idobjectvalidator.masterdata.documenttypes.rest.uri}")
	private String documentTypeUri;

	/** The doc cat map. */
	private SetValuedMap<String, String> docCatMap;

	/** The doc type map. */
	private SetValuedMap<String, String> docTypeMap;

	private static final String DOCUMENTS = "documents";

	private static final String DOCUMENTCATEGORIES = "documentcategories";

	private static final String IS_ACTIVE = "isActive";

	private static final String LANG_CODE = "langCode";

	private static final String CODE = "code";

	private static final String NAME = "name";

	/** The Constant READ_OPTIONS. */
	private static final Configuration READ_OPTIONS = Configuration.defaultConfiguration()
			.addOptions(Option.SUPPRESS_EXCEPTIONS);
	/**
	 * Autowired reference for {@link #RestTemplateBuilder}
	 */
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ValidationUtil validationUtil;

	/**
	 * Reference for ${demographic.resource.url} from property file
	 */
	@Value("${demographic.resource.url}")
	private String demographicResourceUrl;

	@Autowired
	private FileSystemAdapter fs;

	@Autowired
	private Environment env;

	/**
	 * Logger configuration for DocumentServiceUtil
	 */
	private static Logger log = LoggerConfiguration.logConfig(DocumentServiceUtil.class);

	/**
	 * This method is used to assign the input JSON values to DTO
	 * 
	 * @param documentJsonString
	 *            pass the document json
	 * @return UploadRequestDTO
	 * @throws JSONException
	 *             on json error
	 * @throws JsonParseException
	 *             on json parsing error
	 * @throws JsonMappingException
	 *             on json mapping error
	 * @throws IOException
	 *             on input error
	 * @throws ParseException
	 *             on parsing error
	 */
	public MainRequestDTO<DocumentRequestDTO> createUploadDto(String documentJsonString, String preRegistrationId)
			throws JSONException, JsonParseException, JsonMappingException, IOException, ParseException {
		log.info("sessionId", "idType", "id", "In createUploadDto method of document service util");
		MainRequestDTO<DocumentRequestDTO> uploadReqDto = new MainRequestDTO<>();
		JSONObject documentData = new JSONObject(documentJsonString);
		JSONObject docDTOData = (JSONObject) documentData.get("request");
		validateDocuments(documentJsonString, new ArrayList<>());
		DocumentRequestDTO documentDto = (DocumentRequestDTO) JsonUtils.jsonStringToJavaObject(DocumentRequestDTO.class,
				docDTOData.toString());
		uploadReqDto.setId(documentData.get("id").toString());
		uploadReqDto.setVersion(documentData.get("version").toString());
		if (!(documentData.get("requesttime") == null || documentData.get("requesttime").toString().isEmpty())) {
			uploadReqDto.setRequesttime(
					new SimpleDateFormat(utcDateTimePattern).parse(documentData.get("requesttime").toString()));
		} else {
			uploadReqDto.setRequesttime(null);
		}
		uploadReqDto.setRequest(documentDto);
		return uploadReqDto;
	}

	/**
	 * This method assigns the values from DTO to entity
	 * 
	 * @param dto
	 *            pass the document dto
	 * @return DocumentEntity
	 */
	public DocumentEntity dtoToEntity(MultipartFile file, DocumentRequestDTO dto, String userId,
			String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In dtoToEntity method of document service util");
		DocumentEntity documentEntity = new DocumentEntity();
		documentEntity.setDocumentId(UUIDGeneratorUtil.generateId());
		documentEntity.setDocId(preRegistrationId + "/" + dto.getDocCatCode() + "_" + documentEntity.getDocumentId());
		documentEntity.setPreregId(preRegistrationId);
		documentEntity.setDocCatCode(dto.getDocCatCode());
		documentEntity.setDocTypeCode(dto.getDocTypCode());
		documentEntity.setDocFileFormat(FilenameUtils.getExtension(file.getOriginalFilename()));
		documentEntity.setStatusCode(StatusCodes.DOCUMENT_UPLOADED.getCode());
		documentEntity.setLangCode(dto.getLangCode());
		documentEntity.setCrDtime(LocalDateTime.now(ZoneId.of("UTC")));
		documentEntity.setCrBy(userId);
		documentEntity.setUpdBy(userId);
		documentEntity.setUpdDtime(LocalDateTime.now(ZoneId.of("UTC")));
		// documentEntity.setEncryptedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		return documentEntity;
	}

	/**
	 * This method is used to check whether the key is null
	 * 
	 * @param key
	 *            pass the key
	 * @return true if key is null, else false
	 */
	public boolean isNull(Object key) {
		log.info("sessionId", "idType", "id", "In isNull method of document service util");
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

	/**
	 * @return maximum file size defined.
	 */
	public long getMaxFileSize() {
		log.info("sessionId", "idType", "id", "In getMaxFileSize method of document service util");
		return Math.multiplyExact(this.maxFileSize, Math.multiplyExact(1024, 1024));
	}

	/**
	 * @return defined document extension.
	 *//*
		 * public String getFileExtension() { log.info("sessionId", "idType", "id",
		 * "In getFileExtension method of document service util"); return
		 * this.fileExtension; }
		 */

	public String getCurrentResponseTime() {
		log.info("sessionId", "idType", "id", "In getCurrentResponseTime method of document service util");
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
	}

	public String getDateString(Date date) {
		log.info("sessionId", "idType", "id", "In getDateString method of document service util");
		return DateUtils.formatDate(date, utcDateTimePattern);
	}

	public Integer parseDocumentId(String documentId) {
		log.info("sessionId", "idType", "id", "In parseDocumentId method of document service util");
		try {
			return Integer.parseInt(documentId);
		} catch (NumberFormatException ex) {
			log.error("sessionId", "idType", "id",
					"In parseDocumentId method of document service util- " + ex.getMessage());

			throw new InvalidDocumentIdExcepion(ErrorCodes.PRG_PAM_DOC_019.toString(),
					ErrorMessages.INVALID_DOCUMENT_ID.getMessage());
		}

	}

	public boolean isValidCatCode(String catCode) {
		log.info("sessionId", "idType", "id", "In isValidCatCode method of document service util");
		if (catCode.equals("POA")) {
			return true;
		} else {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_DOC_018.toString(),
					ErrorMessages.INVALID_DOCUMENT_CATEGORY_CODE.getMessage(), null);
		}
	}

	public DocumentEntity documentEntitySetter(String destinationPreId, DocumentEntity sourceEntity,
			DocumentEntity destEntity) throws java.io.IOException {
		log.info("sessionId", "idType", "id", "In documentEntitySetter method of document service util");
		DocumentEntity copyDocumentEntity = new DocumentEntity();
		if (destEntity != null) {
			copyDocumentEntity.setDocumentId(destEntity.getDocumentId());
		} else {
			copyDocumentEntity.setDocumentId(UUIDGeneratorUtil.generateId());
		}
		copyDocumentEntity.setPreregId(destinationPreId);
		copyDocumentEntity.setDocId(sourceEntity.getDocId());
		String key = sourceEntity.getDocCatCode() + "_" + sourceEntity.getDocumentId();
		InputStream file = fs.getFile(sourceEntity.getPreregId(), key);

		copyDocumentEntity.setDocHash(HashUtill.hashUtill(IOUtils.toByteArray(file)));
		copyDocumentEntity.setDocName(sourceEntity.getDocName());
		copyDocumentEntity.setDocTypeCode(sourceEntity.getDocTypeCode());
		copyDocumentEntity.setDocCatCode(sourceEntity.getDocCatCode());
		copyDocumentEntity.setDocFileFormat(sourceEntity.getDocFileFormat());
		copyDocumentEntity.setCrBy(sourceEntity.getCrBy());
		copyDocumentEntity.setUpdBy(sourceEntity.getUpdBy());
		copyDocumentEntity.setLangCode(sourceEntity.getLangCode());
		copyDocumentEntity.setEncryptedDateTime(sourceEntity.getEncryptedDateTime());
		copyDocumentEntity.setCrDtime(LocalDateTime.now(ZoneId.of("UTC")));
		copyDocumentEntity.setUpdDtime(LocalDateTime.now(ZoneId.of("UTC")));
		copyDocumentEntity.setStatusCode(StatusCodes.DOCUMENT_UPLOADED.getCode());
		return copyDocumentEntity;
	}

	/**
	 * This method checks the size of uploaded file
	 * 
	 * @param uploadedFileSize
	 *            pass uploaded file
	 * @return true if file size is within the limit, else false
	 */
	public boolean fileSizeCheck(long uploadedFileSize) {
		log.info("sessionId", "idType", "id", "In fileSizeCheck method of document service util");
		long maxAllowedSize = getMaxFileSize();
		if (uploadedFileSize < maxAllowedSize) {
			return true;
		} else {
			throw new DocumentSizeExceedException(ErrorCodes.PRG_PAM_DOC_007.toString(),
					ErrorMessages.DOCUMENT_EXCEEDING_PREMITTED_SIZE.getMessage());
		}
	}

	/**
	 * This method checks the file extension
	 * 
	 * @param file
	 *            pass uploaded file
	 * @throws DocumentNotValidException
	 *             if uploaded document is not valid
	 */
	public boolean fileExtensionCheck(MultipartFile file) {
		log.info("sessionId", "idType", "id", "In fileExtensionCheck method of document service util");
		List<String> fileExtensionList = Arrays.asList(fileExtension.split("\\s*,\\s*"));
		if (fileExtensionList.contains(FilenameUtils.getExtension(file.getOriginalFilename()).toUpperCase())) {
			return true;
		} else {
			throw new DocumentNotValidException(ErrorCodes.PRG_PAM_DOC_004.toString(),
					ErrorMessages.DOCUMENT_INVALID_FORMAT.getMessage());
		}

	}

	/**
	 * 
	 * @param dto
	 *            DocumentRequestDTO
	 * @return boolean
	 */

	public boolean isValidRequest(DocumentRequestDTO dto, String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In isValidRequest method of document service util");
		if (isNull(preRegistrationId)) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_DOC_018.toString(),
					ErrorMessages.INVALID_PRE_ID.getMessage(), null);
		} else if (isNull(dto.getDocCatCode())) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_DOC_018.toString(),
					ErrorMessages.INVALID_DOC_CAT_CODE.getMessage(), null);
		} else if (isNull(dto.getDocTypCode())) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_DOC_018.toString(),
					ErrorMessages.INVALID_DOC_TYPE_CODE.getMessage(), null);
		} else if (isNull(dto.getLangCode())) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_PAM_DOC_018.toString(),
					ErrorMessages.INVALID_LANG_CODE.getMessage(), null);

		}
		return true;
	}

	/**
	 * This method checks the file extension
	 * 
	 * @param file
	 *            pass uploaded file
	 * @throws DocumentNotValidException
	 *             if uploaded document is not valid
	 */
	public boolean isVirusScanSuccess(MultipartFile file) {
		try {
			log.info("sessionId", "idType", "id", "In isVirusScanSuccess method of document service util");
			return virusScan.scanDocument(file.getBytes());
		} catch (java.io.IOException e) {
			log.error("sessionId", "idType", "id", e.getMessage());
			throw new VirusScannerException(ErrorCodes.PRG_PAM_DOC_010.toString(),
					ErrorMessages.DOCUMENT_FAILED_IN_VIRUS_SCAN.getMessage());
		}
	}

	public boolean getPreRegInfoRestService(String preId) {
		log.info("sessionId", "idType", "id", "In callGetPreRegInfoRestService method of document service util");
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("preRegistrationId", preId);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(demographicResourceUrl + "/applications/");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<MainResponseDTO<DemographicResponseDTO>> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			uriBuilder += "{preRegistrationId}";
			log.info("sessionId", "idType", "id",
					"In callGetPreRegInfoRestService method of document service util url " + uriBuilder);
			ResponseEntity<MainResponseDTO<DemographicResponseDTO>> respEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, httpEntity,
					new ParameterizedTypeReference<MainResponseDTO<DemographicResponseDTO>>() {
					}, params);
			if (respEntity.getBody().getErrors() != null) {
				throw new DemographicGetDetailsException(respEntity.getBody().getErrors().get(0).getErrorCode(),
						respEntity.getBody().getErrors().get(0).getMessage());
			}

		} catch (RestClientException ex) {
			log.error("sessionId", "idType", "id",
					"In callGetPreRegInfoRestService method of document service util- " + ex.getMessage());

			throw new DemographicGetDetailsException(ErrorCodes.PRG_PAM_DOC_020.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.getMessage(), ex.getCause());
		}
		return true;
	}

	private void loadDocCategories() {
		ResponseWrapper<LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>> responseBody = restTemplate
				.getForObject(documentCategoryUri, ResponseWrapper.class);
		if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
			ArrayList<LinkedHashMap<String, Object>> response = responseBody.getResponse().get(DOCUMENTCATEGORIES);
			docCatMap = new HashSetValuedHashMap<>(response.size());
			IntStream.range(0, response.size()).filter(index -> (Boolean) response.get(index).get(IS_ACTIVE))
					.forEach(index -> docCatMap.put(String.valueOf(response.get(index).get(LANG_CODE)),
							String.valueOf(response.get(index).get(CODE))));
		}
		System.out.println("docCatMap: "+docCatMap);
	}

	@SuppressWarnings("unchecked")
	void loadDocTypes() {
		docTypeMap = new HashSetValuedHashMap<>();
		if (Objects.nonNull(docCatMap) && !docCatMap.isEmpty()) {
			docCatMap.keySet().stream().forEach(langCode -> docCatMap.get(langCode).stream().forEach(docCat -> {
				String uri = UriComponentsBuilder.fromUriString(documentTypeUri).buildAndExpand(docCat, langCode)
						.toUriString();
				ResponseWrapper<LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>> responseBody = restTemplate
						.getForObject(uri, ResponseWrapper.class);
				if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
					ArrayList<LinkedHashMap<String, Object>> response = responseBody.getResponse().get(DOCUMENTS);
					IntStream.range(0, response.size()).filter(index -> (Boolean) response.get(index).get(IS_ACTIVE))
							.forEach(index -> {
								docTypeMap.put(docCat, String.valueOf(response.get(index).get(NAME)));
								docTypeMap.put(docCat, String.valueOf(response.get(index).get("code")));
							});
				}
			}));
		}
		System.out.println("docTypeMap: "+docTypeMap);
	}

	/**
	 * Convert to path.
	 *
	 * @param jsonPath
	 *            the json path
	 * @return the string
	 */
	private String convertToPath(String jsonPath) {
		String path = String.valueOf(jsonPath.replaceAll("[$']", ""));
		return path.substring(1, path.length() - 1).replace("][", "/");
	}

	/**
	 * Validate documents.
	 *
	 * @param identityString
	 *            the identity string
	 * @param errorList
	 *            the error list
	 */
	public void validateDocuments(String identityString, List<ServiceError> errorList) {
		loadDocCategories();
		loadDocTypes();
		IdObjectValidatorDocumentMapping.getAllMapping().entrySet().stream()
				.filter(entry -> docTypeMap.containsKey(entry.getKey())).forEach(entry -> {
					// JsonPath jsonPath = JsonPath.compile("identity." + entry.getValue() +
					// ".type");
					System.out.println("entry: " + entry.getValue());
					JsonPath jsonPath = JsonPath.compile(entry.getValue());
					Object value = jsonPath.read(identityString, READ_OPTIONS);
					System.out.println("value: " + value);
					if (Objects.nonNull(value) && !docTypeMap.get(entry.getKey()).contains(value)) {
						errorList.add(
								new ServiceError(IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getErrorCode(),
										String.format(
												IdObjectValidatorErrorConstant.INVALID_INPUT_PARAMETER.getMessage(),
												convertToPath(jsonPath.getPath()))));
					}
				});
	}
}
