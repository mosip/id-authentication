package io.kernel.idrepo.validator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import io.kernel.idrepo.config.IdRepoLogger;
import io.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.jsonvalidator.exception.ConfigServerConnectionException;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.exception.NullJsonNodeException;
import io.mosip.kernel.core.jsonvalidator.exception.NullJsonSchemaException;
import io.mosip.kernel.core.jsonvalidator.exception.UnidentifiedJsonException;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

/**
 * The Class IdRequestValidator.
 *
 * @author Manoj SP
 */
@Component
public class IdRequestValidator implements Validator {

	private static final String LANGUAGE = "language";

	private static final String VALIDATE_REQUEST = "validateRequest - \n";

	private static final String ID_REQUEST_VALIDATOR = "IdRequestValidator";

	private static final String ID_REPO = "IdRepo";

	private static final String SESSION_ID = "sessionId";

	Logger mosipLogger = IdRepoLogger.getLogger(IdRequestValidator.class);

	/** The Constant TIMESTAMP. */
	private static final String TIMESTAMP = "timestamp";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant REGISTRATION_ID. */
	private static final String REGISTRATION_ID = "registrationId";

	/** The Constant STATUS_FIELD. */
	private static final String STATUS_FIELD = "status";

	/** The Constant UIN. */
	private static final String UIN = "uin";

	/** The Constant ID_FIELD. */
	private static final String ID_FIELD = "id";

	private static final String SCHEMA_NAME = "mosip-identity-json-schema.json";

	/** The env. */
	@Autowired
	private Environment env;

	/** The id. */
	@Resource
	private Map<String, String> id;

	/** The status. */
	@Resource
	private Map<String, String> status;

	/** The uin validator. */
	@Autowired
	private UinValidatorImpl uinValidator;

	@Autowired
	private JsonValidator jsonValidator;

	@Autowired
	private ObjectMapper mapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(IdRequestDTO.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		IdRequestDTO request = (IdRequestDTO) target;

		validateReqTime(request.getTimestamp(), errors);

		if (!errors.hasErrors()) {
			validateId(request.getId(), errors);
			validateStatus(request.getStatus(), errors);
			validateRequest(request.getRequest(), errors);
		}

		if (!errors.hasErrors() && (request.getId().equals(id.get("update")))) {
			validateUin(request.getUin(), errors);
		}

		if (!errors.hasErrors() && (request.getId().equals(id.get("create")))) {
			validateRegId(request.getRegistrationId(), errors);
		}
	}

	/**
	 * Validate id.
	 *
	 * @param id
	 *            the id
	 * @param errors
	 *            the errors
	 */
	private void validateId(String id, Errors errors) {
		if (Objects.isNull(id)) {
			errors.rejectValue(ID_FIELD, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), ID_FIELD));
		} else if (!this.id.containsValue(id)) {
			errors.rejectValue(ID_FIELD, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), ID_FIELD));
		}
	}

	/**
	 * Validate uin.
	 *
	 * @param uin
	 *            the uin
	 * @param errors
	 *            the errors
	 */
	private void validateUin(String uin, Errors errors) {
		if (Objects.isNull(uin)) {
			errors.rejectValue(UIN, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), UIN));
		} else {
			try {
				uinValidator.validateId(uin);
			} catch (InvalidIDException e) {
				errors.rejectValue(UIN, IdRepoErrorConstants.INVALID_UIN.getErrorCode(),
						IdRepoErrorConstants.INVALID_UIN.getErrorMessage());
			}
		}
	}

	/**
	 * Validate status.
	 *
	 * @param status
	 *            the status
	 * @param errors
	 *            the errors
	 */
	private void validateStatus(String status, Errors errors) {
		if (Objects.isNull(status)) {
			errors.rejectValue(STATUS_FIELD, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), STATUS_FIELD));
		} else if (!this.status.containsValue(status)) {
			errors.rejectValue(STATUS_FIELD, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), STATUS_FIELD));
		}
	}

	/**
	 * Validate reg id.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param errors
	 *            the errors
	 */
	private void validateRegId(String registrationId, Errors errors) {
		if (Objects.isNull(registrationId)) {
			errors.rejectValue(REGISTRATION_ID, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), REGISTRATION_ID));
		}
	}

	/**
	 * Validate request.
	 *
	 * @param request
	 *            the request
	 * @param errors
	 *            the errors
	 */
	private void validateRequest(Object request, Errors errors) {
		if (Objects.isNull(request)) {
			errors.rejectValue(REQUEST, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), REQUEST));
		} else {
			try {
				jsonValidator.validateJson(mapper.writeValueAsString(request), SCHEMA_NAME);
				Map<String, Map<String, List<Map<String, String>>>> requestMap = mapper.readValue(
						mapper.writeValueAsBytes(request),
						new TypeReference<Map<String, Map<String, List<Map<String, String>>>>>() {
						});

				Optional<Boolean> isInvalidLang = checkForInvalidLang(requestMap);
				if ((isInvalidLang.isPresent() && isInvalidLang.get()) || checkForDuplicates(requestMap)) {
					mosipLogger.error(SESSION_ID, ID_REPO, ID_REQUEST_VALIDATOR,
							VALIDATE_REQUEST + " - Invalid language");
					errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), REQUEST));
				}
			} catch (UnidentifiedJsonException | IOException | JsonValidationProcessingException | JsonIOException
					| JsonSchemaIOException | FileIOException e) {
				mosipLogger.error(SESSION_ID, ID_REPO, ID_REQUEST_VALIDATOR,
						(VALIDATE_REQUEST + ExceptionUtils.getStackTrace(e)));
				errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), REQUEST));
			} catch (NullJsonSchemaException | ConfigServerConnectionException | HttpRequestException
					| NullJsonNodeException e) {
				mosipLogger.error(SESSION_ID, ID_REPO, ID_REQUEST_VALIDATOR,
						VALIDATE_REQUEST + ExceptionUtils.getStackTrace(e));
				errors.rejectValue(REQUEST, IdRepoErrorConstants.INTERNAL_SERVER_ERROR.getErrorCode(),
						IdRepoErrorConstants.INTERNAL_SERVER_ERROR.getErrorMessage());
			}
		}
	}

	private Optional<Boolean> checkForInvalidLang(Map<String, Map<String, List<Map<String, String>>>> requestMap) {
		return requestMap.get("identity").values().parallelStream()
				.map(listOfMap -> listOfMap.parallelStream().filter(map -> map.containsKey(LANGUAGE))
						.peek(leftMap -> leftMap.replace(LANGUAGE, leftMap.get(LANGUAGE).toUpperCase()))
						.anyMatch(map -> !Lists
								.newArrayList(env.getProperty("mosip.idrepo.primary-lang").toUpperCase(),
										env.getProperty("mosip.idrepo.secondary-lang").toUpperCase())
								.contains(map.get(LANGUAGE).toUpperCase())))
				.findFirst();
	}

	private boolean checkForDuplicates(Map<String, Map<String, List<Map<String, String>>>> requestMap) {
		TreeSet<Map<String, String>> identitySet = Sets.newTreeSet((Map<String, String> map1,
				Map<String, String> map2) -> StringUtils.compareIgnoreCase(map1.get(LANGUAGE), map2.get(LANGUAGE)));
		return requestMap.get("identity").values().parallelStream()
				.peek(identitySet::addAll)
				.peek(listOfMap -> System.err.println("list-> " + listOfMap))
				.peek(listOfMap -> System.err.println("identitySet-> " + identitySet))
				.anyMatch(listOfMap -> listOfMap.size() != identitySet.size());
	}

	/**
	 * Validate req time.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @param errors
	 *            the errors
	 */
	private void validateReqTime(String timestamp, Errors errors) {
		if (Objects.isNull(timestamp)) {
			errors.rejectValue(TIMESTAMP, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), TIMESTAMP));
		} else {
			try {
				DateTimeFormatterFactory timestampFormat = new DateTimeFormatterFactory(
						env.getProperty("datetime.pattern"));
				timestampFormat.setTimeZone(TimeZone.getTimeZone(env.getProperty("datetime.timezone")));
				if (!DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter()).isBeforeNow()) {
					errors.rejectValue(TIMESTAMP, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), TIMESTAMP));
				}
			} catch (IllegalArgumentException e) {
				errors.rejectValue(TIMESTAMP, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), TIMESTAMP));
			}
		}
	}

}
