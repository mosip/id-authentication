/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.documents.code.RequestCodes;
import io.mosip.preregistration.documents.code.StatusCodes;
import io.mosip.preregistration.documents.dto.DocumentRequestDTO;
import io.mosip.preregistration.documents.dto.MainListResponseDTO;
import io.mosip.preregistration.documents.dto.MainRequestDTO;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.DemographicGetDetailsException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.InvalidDocumnetIdExcepion;

/**
 * This class provides the utility methods for DocumentService
 * 
 * @author Rajath KR
 * @since 1.0.0
 */
@Component
public class DocumentServiceUtil {

	/**
	 * Autowired reference for {@link #VirusScanner}
	 */
/*	@Autowired
	private VirusScanner<Boolean, String> virusScan;*/

	/**
	 * Reference for ${max.file.size} from property file
	 */
	@Value("${max.file.size}")
	private int maxFileSize;

	/**
	 * Reference for ${file.extension} from property file
	 */
	@Value("${file.extension}")
	private String fileExtension;

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

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
	
	private ObjectMapper mapper = new ObjectMapper();
	/**
	 * This method adds the initial request values to inputValidation map
	 * 
	 * @param docReqDto
	 *            pass the document Request
	 * @return inputValidation map
	 */
	public Map<String, String> prepareRequestParamMap(MainRequestDTO<DocumentRequestDTO> docReqDto) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.toString(), docReqDto.getId());
		inputValidation.put(RequestCodes.ver.toString(), docReqDto.getVer());
		inputValidation.put(RequestCodes.reqTime.toString(),
				new SimpleDateFormat(dateTimeFormat).format(docReqDto.getReqTime()));
		inputValidation.put(RequestCodes.request.toString(), docReqDto.getRequest().toString());
		return inputValidation;
	}

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
	public MainRequestDTO<DocumentRequestDTO> createUploadDto(String documentJsonString)
			throws JSONException, JsonParseException, JsonMappingException, IOException, ParseException {
		MainRequestDTO<DocumentRequestDTO> uploadReqDto = new MainRequestDTO<>();
		JSONObject documentData = new JSONObject(documentJsonString);
		JSONObject docDTOData = (JSONObject) documentData.get("request");
		DocumentRequestDTO documentDto = (DocumentRequestDTO) JsonUtils.jsonStringToJavaObject(DocumentRequestDTO.class,
				docDTOData.toString());
		uploadReqDto.setId(documentData.get("id").toString());
		uploadReqDto.setVer(documentData.get("ver").toString());
		uploadReqDto.setReqTime(
				new SimpleDateFormat(dateTimeFormat).parse(documentData.get("reqTime").toString()));
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
	public DocumentEntity dtoToEntity(DocumentRequestDTO dto) {
		DocumentEntity documentEntity = new DocumentEntity();
		documentEntity.setPreregId(dto.getPreregId());
		documentEntity.setDocCatCode(dto.getDocCatCode());
		documentEntity.setDocTypeCode(dto.getDocTypeCode());
		documentEntity.setDocFileFormat(dto.getDocFileFormat());
		documentEntity.setStatusCode(dto.getStatusCode());
		documentEntity.setLangCode(dto.getLangCode());
		documentEntity.setCrDtime(DateUtils.parseDateToLocalDateTime(new Date()));
		documentEntity.setUpdBy(dto.getUploadBy());
		documentEntity.setUpdDtime(DateUtils.parseDateToLocalDateTime(dto.getUploadDateTime()));
		return documentEntity;
	}

	/**
	 * This method assigns the values from entity to DTO
	 * 
	 * @param entity
	 *            pass document entity
	 * @return DocumentDTO
	 */
	public DocumentRequestDTO entityToDto(DocumentEntity entity) {
		return null;
	}

	/**
	 * This method is used to check whether the key is null
	 * 
	 * @param key
	 *            pass the key
	 * @return true if key is null, else false
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

	/**
	 * @return maximum file size defined.
	 */
	public long getMaxFileSize() {
		return Math.multiplyExact(this.maxFileSize, Math.multiplyExact(1024, 1024));
	}

	/**
	 * @return defined document extension.
	 */
	public String getFileExtension() {
		return this.fileExtension;
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

	public String getDateString(Date date) {
		return DateUtils.formatDate(date, dateTimeFormat);
	}

	public Integer parseDocumentId(String documentId) {
		try {
			return Integer.parseInt(documentId);
		} catch (NumberFormatException e) {
			throw new InvalidDocumnetIdExcepion(ErrorCodes.PRG_PAM_DOC_019.toString(),
					ErrorMessages.INVALID_DOCUMENT_ID.toString());
		}

	}

	public boolean isValidCatCode(String catCode) {
		if (catCode.equals("POA")) {
			return true;
		} else {
			throw new InvalidDocumnetIdExcepion(ErrorCodes.PRG_PAM_DOC_019.toString(),
					ErrorMessages.INVALID_DOCUMENT_CATEGORY_CODE.toString());
		}
	}

	public DocumentEntity documentEntitySetter(String destinationPreId, DocumentEntity documentEntity) {
		DocumentEntity copyDocumentEntity = new DocumentEntity();
		copyDocumentEntity.setPreregId(destinationPreId);
		copyDocumentEntity.setDocName(documentEntity.getDocName());
		copyDocumentEntity.setDocTypeCode(documentEntity.getDocTypeCode());
		copyDocumentEntity.setDocCatCode(documentEntity.getDocCatCode());
		copyDocumentEntity.setDocFileFormat(documentEntity.getDocFileFormat());
		copyDocumentEntity.setCrBy(documentEntity.getCrBy());
		copyDocumentEntity.setUpdBy(documentEntity.getUpdBy());
		copyDocumentEntity.setLangCode(documentEntity.getLangCode());
		copyDocumentEntity.setCrDtime(DateUtils.parseDateToLocalDateTime(new Date()));
		copyDocumentEntity.setUpdDtime(DateUtils.parseDateToLocalDateTime(new Date()));
		copyDocumentEntity.setStatusCode(StatusCodes.Pending_Appointment.toString());
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
		long maxAllowedSize = getMaxFileSize();
		if (uploadedFileSize < maxAllowedSize) {
			return true;
		} else {
			throw new DocumentSizeExceedException(ErrorCodes.PRG_PAM_DOC_007.toString(),
					ErrorMessages.DOCUMENT_EXCEEDING_PREMITTED_SIZE.toString());
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
		if (file.getOriginalFilename().toUpperCase().endsWith(getFileExtension())) {
			return true;
		} else {
			throw new DocumentNotValidException(ErrorCodes.PRG_PAM_DOC_004.toString(),
					ErrorMessages.DOCUMENT_INVALID_FORMAT.toString());
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
	public boolean isVirusScanSuccess(MultipartFile file) {
		// return virusScan.scanDocument(file.getBytes());
		return true;
	}
	
	
	public boolean callGetPreRegInfoRestService(String preId) {
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
			if(respEntity.getStatusCode().is2xxSuccessful()) {
				return true;
			}else {
				throw new DemographicGetDetailsException(ErrorCodes.PRG_PAM_DOC_020.toString(),
						ErrorMessages.DEMOGRAPHIC_DATA_NOT_FOUND.toString());
			}
		} catch (RestClientException e) {
			throw new DemographicGetDetailsException(ErrorCodes.PRG_PAM_DOC_020.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), e.getCause());
		}
	}

}
