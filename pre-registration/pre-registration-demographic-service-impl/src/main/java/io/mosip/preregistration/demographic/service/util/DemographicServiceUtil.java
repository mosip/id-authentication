/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.demographic.service.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.serviceimpl.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.EncryptionFailedException;
import io.mosip.preregistration.core.exception.RestCallException;
import io.mosip.preregistration.core.util.CryptoUtil;
import io.mosip.preregistration.core.util.HashUtill;
import io.mosip.preregistration.demographic.code.RequestCodes;
import io.mosip.preregistration.demographic.dto.DemographicCreateResponseDTO;
import io.mosip.preregistration.demographic.dto.DemographicRequestDTO;
import io.mosip.preregistration.demographic.dto.DemographicUpdateResponseDTO;
import io.mosip.preregistration.demographic.dto.PridFetchResponseDto;
import io.mosip.preregistration.demographic.errorcodes.ErrorCodes;
import io.mosip.preregistration.demographic.errorcodes.ErrorMessages;
import io.mosip.preregistration.demographic.exception.OperationNotAllowedException;
import io.mosip.preregistration.demographic.exception.system.DateParseException;
import io.mosip.preregistration.demographic.exception.system.JsonParseException;
import io.mosip.preregistration.demographic.exception.system.SystemFileIOException;
import io.mosip.preregistration.demographic.exception.system.SystemIllegalArgumentException;

/**
 * This class provides the utility methods for DemographicService
 * 
 * @author Ravi C Balaji
 * @author Sanober Noor
 * @since 1.0.0
 */
@Component
public class DemographicServiceUtil {

	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	/**
	 * Environment instance
	 */
	@Autowired
	private Environment env;

	@Autowired
	private RestTemplate restTemplate;
	
	
	@Value("${mosip.io.prid.url}")
	private String pridURl;

	@Value("${preregistartion.config.identityjson}")
	private String preregistrationIdJson;

	/**
	 * Logger instance
	 */
	private Logger log = LoggerConfiguration.logConfig(DemographicServiceUtil.class);

	@Autowired
	CryptoUtil cryptoUtil;

	/**
	 * This setter method is used to assign the initial demographic entity values to
	 * the createDTO
	 * 
	 * @param demographicEntity
	 *            pass the demographicEntity
	 * @return createDTO with the values
	 */
	public DemographicResponseDTO setterForCreateDTO(DemographicEntity demographicEntity) {
		log.info("sessionId", "idType", "id", "In setterForCreateDTO method of pre-registration service util");
		JSONParser jsonParser = new JSONParser();
		DemographicResponseDTO createDto = new DemographicResponseDTO();
		try {
			createDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
			createDto.setDemographicDetails((JSONObject) jsonParser.parse(new String(cryptoUtil
					.decrypt(demographicEntity.getApplicantDetailJson(), demographicEntity.getEncryptedDateTime()))));
			createDto.setStatusCode(demographicEntity.getStatusCode());
			createDto.setLangCode(demographicEntity.getLangCode());
			createDto.setCreatedBy(demographicEntity.getCreatedBy());
			createDto.setCreatedDateTime(getLocalDateString(demographicEntity.getCreateDateTime()));
			createDto.setUpdatedBy(demographicEntity.getUpdatedBy());
			createDto.setUpdatedDateTime(getLocalDateString(demographicEntity.getUpdateDateTime()));
		} catch (ParseException ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In setterForCreateDTO method of pre-registration service- " + ex.getMessage());
			throw new JsonParseException(ErrorCodes.PRG_PAM_APP_007.getCode(),
					ErrorMessages.JSON_PARSING_FAILED.getMessage(), ex.getCause());
		} catch (EncryptionFailedException ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In setterForCreateDTO method of pre-registration service- " + ex.getMessage());
			throw ex;
		}
		return createDto;
	}

	/**
	 * This setter method is used to assign the initial demographic entity values to
	 * the createDTO
	 * 
	 * @param demographicEntity
	 *            pass the demographicEntity
	 * @return createDTO with the values
	 */
	public DemographicCreateResponseDTO setterForCreatePreRegistration(DemographicEntity demographicEntity,
			JSONObject requestJson) {
		log.info("sessionId", "idType", "id", "In setterForCreateDTO method of pre-registration service util");
		DemographicCreateResponseDTO createDto = new DemographicCreateResponseDTO();
		try {
			createDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
			createDto.setDemographicDetails(requestJson);
			createDto.setStatusCode(demographicEntity.getStatusCode());
			createDto.setLangCode(demographicEntity.getLangCode());
			createDto.setCreatedDateTime(getLocalDateString(demographicEntity.getCreateDateTime()));
		} catch (EncryptionFailedException ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In setterForCreateDTO method of pre-registration service- " + ex.getMessage());
			throw ex;
		}
		return createDto;
	}

	/**
	 * This setter method is used to assign the initial demographic entity values to
	 * the createDTO
	 * 
	 * @param demographicEntity
	 *            pass the demographicEntity
	 * @return createDTO with the values
	 */
	public DemographicUpdateResponseDTO setterForUpdatePreRegistration(DemographicEntity demographicEntity) {
		log.info("sessionId", "idType", "id", "In setterForCreateDTO method of pre-registration service util");
		JSONParser jsonParser = new JSONParser();
		DemographicUpdateResponseDTO createDto = new DemographicUpdateResponseDTO();
		try {
			createDto.setPreRegistrationId(demographicEntity.getPreRegistrationId());
			createDto.setDemographicDetails((JSONObject) jsonParser.parse(new String(cryptoUtil
					.decrypt(demographicEntity.getApplicantDetailJson(), demographicEntity.getEncryptedDateTime()))));
			createDto.setStatusCode(demographicEntity.getStatusCode());
			createDto.setLangCode(demographicEntity.getLangCode());
			createDto.setUpdatedDateTime(getLocalDateString(demographicEntity.getCreateDateTime()));
		} catch (ParseException ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In setterForCreateDTO method of pre-registration service- " + ex.getMessage());
			throw new JsonParseException(ErrorCodes.PRG_PAM_APP_007.getCode(),
					ErrorMessages.JSON_PARSING_FAILED.getMessage(), ex.getCause());
		} catch (EncryptionFailedException ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In setterForCreateDTO method of pre-registration service- " + ex.getMessage());
			throw ex;
		}
		return createDto;
	}

	/**
	 * This method is used to set the values from the request to the
	 * demographicEntity entity fields.
	 * 
	 * @param demographicRequest
	 *            pass demographicRequest
	 * @param requestId
	 *            pass requestId
	 * @param entityType
	 *            pass entityType
	 * @return demographic entity with values
	 */
	public DemographicEntity prepareDemographicEntityForCreate(DemographicRequestDTO demographicRequest,
			String statuscode, String userId, String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In prepareDemographicEntity method of pre-registration service util");
		DemographicEntity demographicEntity = new DemographicEntity();
		demographicEntity.setPreRegistrationId(preRegistrationId);
		LocalDateTime encryptionDateTime = DateUtils.getUTCCurrentDateTime();
		log.info("sessionId", "idType", "id", "Encryption start time : " + DateUtils.getUTCCurrentDateTimeString());
		byte[] encryptedDemographicDetails = cryptoUtil
				.encrypt(demographicRequest.getDemographicDetails().toJSONString().getBytes(), encryptionDateTime);
		log.info("sessionId", "idType", "id", "Encryption end time : " + DateUtils.getUTCCurrentDateTimeString());
		demographicEntity.setApplicantDetailJson(encryptedDemographicDetails);
		demographicEntity.setLangCode(demographicRequest.getLangCode());
		demographicEntity.setCrAppuserId(userId);
		demographicEntity.setCreatedBy(userId);
		demographicEntity.setCreateDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		demographicEntity.setStatusCode(statuscode);
		log.info("sessionId", "idType", "id", "Hashing start time : " + DateUtils.getUTCCurrentDateTimeString());
		demographicEntity.setDemogDetailHash(HashUtill.hashUtill(demographicEntity.getApplicantDetailJson()));
		log.info("sessionId", "idType", "id", "Hashing end time : " + DateUtils.getUTCCurrentDateTimeString());
		demographicEntity.setUpdatedBy(userId);
		demographicEntity.setUpdateDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		demographicEntity.setEncryptedDateTime(encryptionDateTime);
		return demographicEntity;
	}

	/**
	 * This method is used to set the values from the request to the
	 * demographicEntity entity fields.
	 * 
	 * @param demographicRequest
	 *            pass demographicRequest
	 * @param requestId
	 *            pass requestId
	 * @param entityType
	 *            pass entityType
	 * @return demographic entity with values
	 */
	public DemographicEntity prepareDemographicEntityForUpdate(DemographicEntity demographicEntity,
			DemographicRequestDTO demographicRequest, String statuscode, String userId, String preRegistrationId)
			throws EncryptionFailedException {
		log.info("sessionId", "idType", "id", "In prepareDemographicEntity method of pre-registration service util");
		demographicEntity.setPreRegistrationId(preRegistrationId);
		LocalDateTime encryptionDateTime = DateUtils.getUTCCurrentDateTime();
		log.info("sessionId", "idType", "id", "Encryption start time : " + DateUtils.getUTCCurrentDateTimeString());
		byte[] encryptedDemographicDetails = cryptoUtil
				.encrypt(demographicRequest.getDemographicDetails().toJSONString().getBytes(), encryptionDateTime);
		log.info("sessionId", "idType", "id", "Encryption end time : " + DateUtils.getUTCCurrentDateTimeString());
		demographicEntity.setApplicantDetailJson(encryptedDemographicDetails);
		demographicEntity.setLangCode(demographicRequest.getLangCode());
		demographicEntity.setStatusCode(statuscode);
		log.info("sessionId", "idType", "id", "Hashing start time : " + DateUtils.getUTCCurrentDateTimeString());
		demographicEntity.setDemogDetailHash(HashUtill.hashUtill(demographicEntity.getApplicantDetailJson()));
		log.info("sessionId", "idType", "id", "Hashing end time : " + DateUtils.getUTCCurrentDateTimeString());
		demographicEntity.setUpdateDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		demographicEntity.setEncryptedDateTime(encryptionDateTime);
		return demographicEntity;
	}

	/**
	 * This method is used to add the initial request values into a map for input
	 * validations.
	 *
	 * @param demographicRequestDTO
	 *            pass demographicRequestDTO
	 * @return a map for request input validation
	 */

	public Map<String, String> prepareRequestMap(MainRequestDTO<?> requestDto) {
		log.info("sessionId", "idType", "id", "In prepareRequestMap method of Login Service Util");
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

	/**
	 * This method is used to set the JSON values to RequestCodes constants.
	 * 
	 * @param demographicData
	 *            pass demographicData
	 * @param identityKey
	 *            pass identityKey
	 * @return values from JSON based on key
	 * 
	 * @throws ParseException
	 *             On json Parsing Failed
	 * @throws org.json.simple.parser.ParseException
	 * 
	 */
	public JSONArray getValueFromIdentity(byte[] demographicData, String identityKey)
			throws ParseException, org.json.simple.parser.ParseException {
		log.info("sessionId", "idType", "id", "In getValueFromIdentity method of pre-registration service util ");
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObj = (JSONObject) jsonParser.parse(new String(demographicData));
		JSONObject identityObj = (JSONObject) jsonObj.get(RequestCodes.IDENTITY.getCode());
		return (JSONArray) identityObj.get(identityKey);
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

	public String getIdJSONValue(String demographicData, String value) throws ParseException {
		log.info("sessionId", "idType", "id",
				"In getValueFromIdentity method of pe-registration service util to get getIdJSONValue ");

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObj = (JSONObject) jsonParser.parse(demographicData);
		JSONObject identityObj = (JSONObject) jsonObj.get(RequestCodes.IDENTITY.getCode());
		if (identityObj.get(value) != null)
			return identityObj.get(value).toString();
		return "";

	}

	/**
	 * This method is used as Null checker for different input keys.
	 *
	 * @param key
	 *            pass the key
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

	/**
	 * This method is used to validate Pending_Appointment and Booked status codes.
	 * 
	 * @param statusCode
	 *            pass statusCode
	 * @return true or false
	 */
	public boolean checkStatusForDeletion(String statusCode) {
		log.info("sessionId", "idType", "id", "In checkStatusForDeletion method of pre-registration service util ");
		if (statusCode.equals(StatusCodes.PENDING_APPOINTMENT.getCode())
				|| statusCode.equals(StatusCodes.BOOKED.getCode())) {
			return true;
		} else {
			throw new OperationNotAllowedException(ErrorCodes.PRG_PAM_APP_003.getCode(),
					ErrorMessages.DELETE_OPERATION_NOT_ALLOWED.getMessage());
		}
	}

	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
	}

	public Date getDateFromString(String date) {
		log.info("sessionId", "idType", "id", "In getDateFromString method of pre-registration service util ");
		try {
			return new SimpleDateFormat(utcDateTimePattern).parse(date);
		} catch (java.text.ParseException ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In getDateFromString method of pre-registration service- " + ex.getCause());
			throw new DateParseException(ErrorCodes.PRG_PAM_APP_011.getCode(),
					ErrorMessages.UNSUPPORTED_DATE_FORMAT.getMessage(), ex.getCause());
		}
	}

	public String getLocalDateString(LocalDateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(utcDateTimePattern);
		return date.format(dateTimeFormatter);
	}

	public boolean isStatusValid(String status) {
		for (StatusCodes choice : StatusCodes.values())
			if (choice.getCode().equals(status))
				return true;
		return false;
	}

	/**
	 * This method will return the MainResponseDTO with id and version
	 * 
	 * @param mainRequestDto
	 * @return MainResponseDTO<?>
	 */
	public MainResponseDTO<?> getMainResponseDto(MainRequestDTO<?> mainRequestDto) {
		log.info("sessionId", "idType", "id", "In getMainResponseDTO method of Login Common Util");
		MainResponseDTO<?> response = new MainResponseDTO<>();
		response.setId(mainRequestDto.getId());
		response.setVersion(mainRequestDto.getVersion());

		return response;
	}

	public static Integer parsePageIndex(String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			throw new SystemIllegalArgumentException(ErrorCodes.PRG_PAM_APP_019.getCode(),
					ErrorMessages.INVALID_PAGE_INDEX_VALUE.getMessage());
		}
	}

	public static Integer parsePageSize(String text) {
		try {
			return Integer.parseInt(text);
		} catch (IllegalArgumentException e) {
			throw new SystemIllegalArgumentException(ErrorCodes.PRG_PAM_APP_015.getCode(),
					ErrorMessages.PAGE_SIZE_MUST_BE_GREATER_THAN_ZERO.getMessage());
		}
	}

	/**
	 * This method is used for config rest call
	 * 
	 * @param filname
	 * @return
	 */
	public String getJson(String filename) {
		try {
			String configServerUri = env.getProperty("spring.cloud.config.uri");
			String configLabel = env.getProperty("spring.cloud.config.label");
			String configProfile = env.getProperty("spring.profiles.active");
			String configAppName = env.getProperty("spring.cloud.config.name");
			StringBuilder uriBuilder = new StringBuilder();
			uriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
					.append(configLabel + "/").append(filename);
			// uriBuilder.append(
			// "http://104.211.212.28:51000/preregistration/dev/master/PreRegistrationIdentitiyMapping.json");
			log.info("sessionId", "idType", "id", " URL in demographic service util of getJson " + uriBuilder);
			return restTemplate.getForObject(uriBuilder.toString(), String.class);
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In pre-registration service util of getPreregistrationIdentityJson- " + ex.getMessage());
			throw new SystemFileIOException(ErrorCodes.PRG_PAM_APP_018.getCode(),
					ErrorMessages.UBALE_TO_READ_IDENTITY_JSON.getMessage(), null);
		}
	}
	
	public String generateId() {
		String prid=null;
		try {
			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(pridURl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<RequestWrapper<RegistrationCenterResponseDto>> entity = new HttpEntity<>(headers);
			String uriBuilder = regbuilder.build().encode().toUriString();
			log.info("sessionId", "idType", "id",
					"In callRegCenterDateRestService method of Booking Service URL- " + uriBuilder);
			ResponseEntity<ResponseWrapper<PridFetchResponseDto>> responseEntity = restTemplate.exchange(
					uriBuilder, HttpMethod.GET, entity,
					new ParameterizedTypeReference<ResponseWrapper<PridFetchResponseDto>>() {
					});
			if (responseEntity.getBody().getErrors() != null && !responseEntity.getBody().getErrors().isEmpty()) {
				throw new RestCallException(responseEntity.getBody().getErrors().get(0).getErrorCode(),
						responseEntity.getBody().getErrors().get(0).getMessage());
			}
			prid = responseEntity.getBody().getResponse().getPrid();
			if (prid == null || prid.isEmpty()) {
				throw new RestCallException(ErrorCodes.PRG_PAM_APP_020.getCode(),
						ErrorMessages.PRID_RESTCALL_FAIL.getMessage());
			}

		} catch (RestClientException ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In callRegCenterDateRestService method of Booking Service Util for HttpClientErrorException- "
							+ ex.getMessage());
			throw new RestCallException(ErrorCodes.PRG_PAM_APP_020.getCode(),
					ErrorMessages.PRID_RESTCALL_FAIL.getMessage());
		}
		return prid;

		
	}

}