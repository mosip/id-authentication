package io.mosip.preregistration.core.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.code.RequestCodes;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

@Component
public class ValidationUtil {

	private static String utcDateTimePattern;

	private static String preIdRegex;

	private static String preIdLength;

	private static String emailRegex;

	private static String phoneRegex;

	private static String langCodes;

	private static String documentTypeUri;

	private static String documentCategoryUri;

	private static Logger log = LoggerConfiguration.logConfig(ValidationUtil.class);

	private ValidationUtil() {
	}

	@Value("${mosip.utc-datetime-pattern}")
	public void setDateTime(String value) {
		ValidationUtil.utcDateTimePattern = value;
	}

	@Value("${mosip.kernel.prid.length}")
	public void setLength(String value) {
		ValidationUtil.preIdLength = value;
	}

	@Value("${mosip.id.validation.identity.email}")
	public void setEmailRegex(String value) {
		ValidationUtil.emailRegex = value;
	}

	@Value("${mosip.id.validation.identity.phone}")
	public void setPhoneRegex(String value) {
		ValidationUtil.phoneRegex = value;
	}

	@Value("${mosip.supported-languages}")
	public void setLanCode(String value) {
		ValidationUtil.langCodes = value;
	}

	public static boolean emailValidator(String email) {
		return email.matches(emailRegex);
	}

	public static boolean phoneValidator(String phone) {
		return phone.matches(phoneRegex);
	}

	public static boolean idValidation(String value, String regex) {
		if (!isNull(value)) {
			return value.matches(regex);
		}
		return false;
	}

	@Value("${mosip.kernel.idobjectvalidator.masterdata.documenttypes.rest.uri}")
	public void setDocType(String value) {
		ValidationUtil.documentTypeUri = value;
	}

	@Value("${mosip.kernel.idobjectvalidator.masterdata.documentcategories.lang.rest.uri}")
	public void setDocCatCode(String value) {
		ValidationUtil.documentCategoryUri = value;
	}

	/** The doc cat map. */
	private ConcurrentHashMap<String, String> docCatMap;

	/** The doc type map. */
	private ConcurrentHashMap<String, String> docTypeMap;

	@Autowired
	RestTemplate restTemplate;

	private static final String DOCUMENTS = "documents";

	private static final String DOCUMENTCATEGORIES = "documentcategories";

	private static final String IS_ACTIVE = "isActive";

	private static final String CODE = "code";

	private static final String NAME = "name";

	public static boolean requestValidator(MainRequestDTO<?> mainRequest) {
		log.info("sessionId", "idType", "id",
				"In requestValidator method of pre-registration core with mainRequest " + mainRequest);
		if (mainRequest.getId() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.getCode(),
					ErrorMessages.INVALID_REQUEST_ID.getMessage(), null);
		} else if (mainRequest.getRequest() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_004.getCode(),
					ErrorMessages.INVALID_REQUEST_BODY.getMessage(), null);
		} else if (mainRequest.getRequesttime() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_003.getCode(),
					ErrorMessages.INVALID_REQUEST_DATETIME.getMessage(), null);
		} else if (mainRequest.getVersion() == null) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_002.getCode(),
					ErrorMessages.INVALID_REQUEST_VERSION.getMessage(), null);
		}
		return true;
	}

	// public static boolean requestValidator(MainListRequestDTO<?> mainRequest) {
	// log.info("sessionId", "idType", "id",
	// "In requestValidator method of pre-registration core with mainRequest " +
	// mainRequest);
	// if (mainRequest.getId() == null) {
	// throw new
	// InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.getCode(),
	// ErrorMessages.INVALID_REQUEST_ID.getMessage(),null);
	// } else if (mainRequest.getRequest() == null) {
	// throw new
	// InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_004.getCode(),
	// ErrorMessages.INVALID_REQUEST_BODY.getMessage(),null);
	// } else if (mainRequest.getRequesttime() == null) {
	// throw new
	// InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_003.getCode(),
	// ErrorMessages.INVALID_REQUEST_DATETIME.getMessage(),null);
	// } else if (mainRequest.getVersion() == null) {
	// throw new
	// InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_002.getCode(),
	// ErrorMessages.INVALID_REQUEST_VERSION.getMessage(),null);
	// }
	// return true;
	// }

	public static boolean requestValidator(Map<String, String> requestMap, Map<String, String> requiredRequestMap) {
		log.info("sessionId", "idType", "id", "In requestValidator method of pre-registration core with requestMap "
				+ requestMap + " againt requiredRequestMap " + requiredRequestMap);
		for (String key : requestMap.keySet()) {
			if (key.equals(RequestCodes.ID) && (requestMap.get(RequestCodes.ID) == null
					|| !requestMap.get(RequestCodes.ID).equals(requiredRequestMap.get(RequestCodes.ID)))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.getCode(),
						ErrorMessages.INVALID_REQUEST_ID.getMessage(), null);
			} else if (key.equals(RequestCodes.VER) && (requestMap.get(RequestCodes.VER) == null
					|| !requestMap.get(RequestCodes.VER).equals(requiredRequestMap.get(RequestCodes.VER)))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_002.getCode(),
						ErrorMessages.INVALID_REQUEST_VERSION.getMessage(), null);
			} else if (key.equals(RequestCodes.REQ_TIME) && requestMap.get(RequestCodes.REQ_TIME) == null) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_003.getCode(),
						ErrorMessages.INVALID_REQUEST_DATETIME.getMessage(), null);

			} else if (key.equals(RequestCodes.REQ_TIME) && requestMap.get(RequestCodes.REQ_TIME) != null) {
				try {
					LocalDate localDate = LocalDate.parse(requestMap.get(RequestCodes.REQ_TIME));
					LocalDate serverDate = new Date().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
					if (localDate.isBefore(serverDate) || localDate.isAfter(serverDate)) {
						throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_013.getCode(),
								ErrorMessages.INVALID_REQUEST_DATETIME_NOT_CURRENT_DATE.getMessage(), null);
					}

				} catch (Exception ex) {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_013.getCode(),
							ErrorMessages.INVALID_REQUEST_DATETIME_NOT_CURRENT_DATE.getMessage(), null);
				}

			} else if (key.equals(RequestCodes.REQUEST) && (requestMap.get(RequestCodes.REQUEST) == null
					|| requestMap.get(RequestCodes.REQUEST).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_004.getCode(),
						ErrorMessages.INVALID_REQUEST_BODY.getMessage(), null);
			}
		}
		return true;
	}

	public static boolean requstParamValidator(Map<String, String> requestMap) {
		log.info("sessionId", "idType", "id",
				"In requstParamValidator method of pre-registration core with requestMap " + requestMap);
		for (String key : requestMap.keySet()) {
			if (key.equals(RequestCodes.USER_ID) && (requestMap.get(RequestCodes.USER_ID) == null
					|| requestMap.get(RequestCodes.USER_ID).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.getCode(),
						ErrorMessages.MISSING_REQUEST_PARAMETER.getMessage(), null);
			} else if (key.equals(RequestCodes.PRE_REGISTRATION_ID)
					&& (requestMap.get(RequestCodes.PRE_REGISTRATION_ID) == null
							|| requestMap.get(RequestCodes.PRE_REGISTRATION_ID).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.getCode(),
						ErrorMessages.MISSING_REQUEST_PARAMETER.getMessage(), null);
			} else if (key.equals(RequestCodes.STATUS_CODE) && (requestMap.get(RequestCodes.STATUS_CODE) == null
					|| requestMap.get(RequestCodes.STATUS_CODE).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.getCode(),
						ErrorMessages.INVALID_STATUS_CODE.getMessage(), null);
			} else if (key.equals(RequestCodes.FROM_DATE) && (requestMap.get(RequestCodes.FROM_DATE) == null
					|| requestMap.get(RequestCodes.FROM_DATE).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.getCode(),
						ErrorMessages.INVALID_DATE.getMessage(), null);
			} else if (key.equals(RequestCodes.FROM_DATE) && requestMap.get(RequestCodes.FROM_DATE) != null) {
				try {
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(requestMap.get(RequestCodes.FROM_DATE));
				} catch (Exception ex) {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_003.getCode(),
							ErrorMessages.INVALID_REQUEST_DATETIME.getMessage() + "_FORMAT --> yyyy-MM-dd HH:mm:ss",
							null);
				}
			} else if (key.equals(RequestCodes.TO_DATE) && (requestMap.get(RequestCodes.TO_DATE) == null
					|| requestMap.get(RequestCodes.TO_DATE).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.getCode(),
						ErrorMessages.INVALID_DATE.getMessage(), null);
			} else if (key.equals(RequestCodes.TO_DATE) && requestMap.get(RequestCodes.TO_DATE) != null) {
				try {
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(requestMap.get(RequestCodes.TO_DATE));
				} catch (Exception ex) {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_003.getCode(),
							ErrorMessages.INVALID_REQUEST_DATETIME.getMessage() + "_FORMAT --> yyyy-MM-dd HH:mm:ss",
							null);
				}
			}

		}
		return true;
	}

	/**
	 * This method is used as Null checker for different input keys.
	 *
	 * @param key
	 *            pass the key
	 * @return true if key not null and return false if key is null.
	 */
	public static boolean isNull(Object key) {
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

	public boolean langvalidation(String langCode) {
		List<String> reqParams = new ArrayList<>();
		String[] langList = langCodes.split(",");
		for (int i = 0; i < langList.length; i++) {
			reqParams.add(langList[i]);
		}

		if (reqParams.contains(langCode)) {
			return true;
		} else {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_014.getCode(),
					ErrorMessages.INVALID_LANG_CODE.getMessage(), null);
		}
	}

	public void getAllDocCategories(String langcode) {
		String uri = UriComponentsBuilder.fromUriString(ValidationUtil.documentCategoryUri).buildAndExpand(langcode)
				.toUriString();
		@SuppressWarnings("unchecked")
		ResponseWrapper<LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>> responseBody = restTemplate
				.getForObject(uri, ResponseWrapper.class);
		if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
			ArrayList<LinkedHashMap<String, Object>> response = responseBody.getResponse().get(DOCUMENTCATEGORIES);
			docCatMap = new ConcurrentHashMap<>(response.size());
			IntStream.range(0, response.size()).filter(index -> (Boolean) response.get(index).get(IS_ACTIVE)).forEach(
					index -> docCatMap.put(String.valueOf(langcode), String.valueOf(response.get(index).get(CODE))));
		}
	}

	public void getAllDocumentTypes(String langCode, String catCode) {
		docTypeMap = new ConcurrentHashMap<>();
		if (Objects.nonNull(docCatMap) && !docCatMap.isEmpty()) {
			String uri = UriComponentsBuilder.fromUriString(ValidationUtil.documentTypeUri)
					.buildAndExpand(catCode, langCode).toUriString();
			@SuppressWarnings("unchecked")
			ResponseWrapper<LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>> responseBody = restTemplate
					.getForObject(uri, ResponseWrapper.class);
			if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
				ArrayList<LinkedHashMap<String, Object>> response = responseBody.getResponse().get(DOCUMENTS);
				IntStream.range(0, response.size()).filter(index -> (Boolean) response.get(index).get(IS_ACTIVE))
						.forEach(index -> {
							docTypeMap.put(catCode, String.valueOf(response.get(index).get(CODE)));
						});
			}
		}
	}

	public boolean validateDocuments(String langCode, String catCode, String typeCode) {
		getAllDocCategories(langCode);
		log.debug("sessionId", "idType", "id","In validateDocuments method with docCatMap "+docCatMap);
		log.debug("sessionId", "idType", "id","In validateDocuments method with langCode "+langCode+" and catCode "+catCode);
		if (docCatMap.get(langCode).contains(catCode)) {
			getAllDocumentTypes(langCode, catCode);
			log.debug("sessionId", "idType", "id","In validateDocuments method with docTypeMap "+docTypeMap);
			log.debug("sessionId", "idType", "id","In validateDocuments method with typeCode "+typeCode+" and catCode "+catCode);
			if (docTypeMap.get(catCode).contains(typeCode)) {
				return true;
			} else {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_017.toString(),
						ErrorMessages.INVALID_DOC_TYPE_CODE.getMessage(), null);
			}
		} else {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_018.toString(),
					ErrorMessages.INVALID_DOC_CAT_CODE.getMessage(), null);
		}
	}

	public Map<String, String> getDocumentTypeNameByTypeCode(String langCode, String catCode) {
		Map<String, String> documentTypeMap = new HashMap<>();
		String uri = UriComponentsBuilder.fromUriString(ValidationUtil.documentTypeUri)
				.buildAndExpand(catCode, langCode).toUriString();
		@SuppressWarnings("unchecked")
		ResponseWrapper<LinkedHashMap<String, ArrayList<LinkedHashMap<String, Object>>>> responseBody = restTemplate
				.getForObject(uri, ResponseWrapper.class);
		if (Objects.isNull(responseBody.getErrors()) || responseBody.getErrors().isEmpty()) {
			ArrayList<LinkedHashMap<String, Object>> response = responseBody.getResponse().get(DOCUMENTS);
			IntStream.range(0, response.size()).filter(index -> (Boolean) response.get(index).get(IS_ACTIVE))
					.forEach(index -> {
						documentTypeMap.put(String.valueOf(response.get(index).get(CODE)),
								String.valueOf(response.get(index).get(NAME)));
					});
		}
		return documentTypeMap;
	}

	public static boolean parseDate(String reqDate, String format) {
		log.info("sessionId", "idType", "id", "In parseDate method of core validation util");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
			sdf.parse(reqDate);
			LocalDate.parse(reqDate);
		} catch (Exception e) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(e));
			log.error("sessionId", "idType", "id", "In parseDate method of core validation util - " + e.getMessage());
			return false;
		}
		return true;
	}
}