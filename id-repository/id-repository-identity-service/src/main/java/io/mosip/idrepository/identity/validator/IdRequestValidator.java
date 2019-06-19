package io.mosip.idrepository.identity.validator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.IdRequestDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.core.validator.BaseIdRepoValidator;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class IdRequestValidator - Validator for {@code IdRequestDTO}.
 *
 * @author Manoj SP
 */
@Component
public class IdRequestValidator extends BaseIdRepoValidator implements Validator {

	private static final String UIN = "uin";

	private static final String READ = "read";

	/** The Constant DOC_VALUE. */
	private static final String DOC_VALUE = "value";

	/** The Constant DOC_TYPE. */
	private static final String DOC_CAT = "category";

	/** The Constant DOCUMENTS. */
	private static final String DOCUMENTS = "documents";

	/** The Constant CREATE. */
	private static final String CREATE = "create";

	/** The Constant CREATE. */
	private static final String UPDATE = "update";

	/** The Constant VALIDATE_REQUEST. */
	private static final String VALIDATE_REQUEST = "validateRequest - \n";

	/** The Constant ID_REQUEST_VALIDATOR. */
	private static final String ID_REQUEST_VALIDATOR = "IdRequestValidator";

	/** The Constant ID_REPO. */
	private static final String ID_REPO = "IdRepo";
	

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRequestValidator.class);

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant REGISTRATION_ID. */
	private static final String REGISTRATION_ID = "registrationId";

	/** The Constant STATUS_FIELD. */
	private static final String STATUS_FIELD = "status";

	/** The status. */
	@Resource
	private List<String> uinStatus;

	/** The rid validator impl. */
	@Autowired
	private RidValidator<String> ridValidator;

	/** The json validator. */
	@Autowired
	private IdObjectValidator idObjectValidator;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The allowed types. */
	@Resource
	private List<String> allowedTypes;

	/** The uin validator. */
	@Autowired
	private UinValidator<String> uinValidator;

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
	public void validate(@Nonnull Object target, Errors errors) {
		IdRequestDTO request = (IdRequestDTO) target;

		validateReqTime(request.getRequesttime(), errors);

		if (!errors.hasErrors()) {
			validateVersion(request.getVersion(), errors);
		}

		if (!errors.hasErrors() && Objects.nonNull(request.getId())) {
			if (request.getId().equals(id.get(CREATE))) {
				validateStatus(request.getRequest().getStatus(), errors, CREATE);
				validateRequest(request.getRequest(), errors, CREATE);
			} else if (request.getId().equals(id.get(UPDATE))) {
				validateStatus(request.getRequest().getStatus(), errors, UPDATE);
				validateRequest(request.getRequest(), errors, UPDATE);
			}

			validateRegId(request.getRequest().getRegistrationId(), errors);
		}

	}

	/**
	 * Validate status.
	 *
	 * @param status the status
	 * @param errors the errors
	 * @param method the method
	 */
	private void validateStatus(String status, Errors errors, String method) {
		if (Objects.nonNull(status) && (method.equals(UPDATE) && !this.uinStatus.contains(status))) {
			errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), STATUS_FIELD));
		}
	}

	/**
	 * Validate reg id.
	 *
	 * @param registrationId the registration id
	 * @param errors         the errors
	 */
	private void validateRegId(String registrationId, Errors errors) {
		if (Objects.isNull(registrationId)) {
			errors.rejectValue(REQUEST, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), REGISTRATION_ID));
		} else {
			try {
				validateRid(registrationId);
			} catch (InvalidIDException e) {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REQUEST_VALIDATOR, "validateRegId",
						"\n" + ExceptionUtils.getStackTrace(e));
				errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), REGISTRATION_ID));
			}
		}
	}

	public void validateRid(String registrationId) {
		ridValidator.validateId(registrationId);
	}

	/**
	 * Validate request.
	 *
	 * @param request the request
	 * @param errors  the errors
	 * @param method  the method
	 */
	@SuppressWarnings("rawtypes")
	private void validateRequest(Object request, Errors errors, String method) {
		try {
			if (Objects.nonNull(request)) {
				Map<String, Object> requestMap = convertToMap(request);
				if (!(requestMap.containsKey(IdRepoConstants.ROOT_PATH.getValue())
						&& Objects.nonNull(requestMap.get(IdRepoConstants.ROOT_PATH.getValue())))) {
					if (method.equals(CREATE)) {
						errors.rejectValue(REQUEST, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
								String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
										IdRepoConstants.ROOT_PATH.getValue()));
					}
				} else if (((Map) requestMap.get(IdRepoConstants.ROOT_PATH.getValue())).isEmpty()) {
					errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
									IdRepoConstants.ROOT_PATH.getValue()));
				} else {
					validateDocuments(requestMap, errors);
					requestMap.keySet().parallelStream()
							.filter(key -> !key.contentEquals(IdRepoConstants.ROOT_PATH.getValue()))
							.forEach(requestMap::remove);
					if (!errors.hasErrors()) {
						if (method.equals(CREATE)) {
						idObjectValidator.validateIdObject(requestMap,
								IdObjectValidatorSupportedOperations.NEW_REGISTRATION);
						} else {
							idObjectValidator.validateIdObject(requestMap,
									IdObjectValidatorSupportedOperations.UPDATE_UIN);
						}
					}
				}
			} else if (method.equals(CREATE)) {
				errors.rejectValue(REQUEST, IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), REQUEST));
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REQUEST_VALIDATOR,
					(VALIDATE_REQUEST + ExceptionUtils.getStackTrace(e)));
			errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
							IdRepoConstants.ROOT_PATH.getValue()));
		} catch (IdObjectValidationFailedException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REQUEST_VALIDATOR,
					(VALIDATE_REQUEST + ExceptionUtils.getStackTrace(e)));
			e.getErrorTexts().parallelStream().forEach(errorText -> errors.rejectValue(REQUEST,
					IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), errorText));
		} catch (IdObjectIOException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REQUEST_VALIDATOR,
					VALIDATE_REQUEST + ExceptionUtils.getStackTrace(e));
			errors.rejectValue(REQUEST, IdRepoErrorConstants.ID_OBJECT_PROCESSING_FAILED.getErrorCode(),
					IdRepoErrorConstants.ID_OBJECT_PROCESSING_FAILED.getErrorMessage());
		}
	}

	/**
	 * Validate documents.
	 *
	 * @param requestMap the request map
	 * @param errors     the errors
	 */
	@SuppressWarnings("unchecked")
	private void validateDocuments(Map<String, Object> requestMap, Errors errors) {
		try {
			if (requestMap.containsKey(DOCUMENTS) && requestMap.containsKey(IdRepoConstants.ROOT_PATH.getValue())
					&& Objects.nonNull(requestMap.get(IdRepoConstants.ROOT_PATH.getValue()))) {
				Map<String, Object> identityMap = convertToMap(requestMap.get(IdRepoConstants.ROOT_PATH.getValue()));
				if (Objects.nonNull(requestMap.get(DOCUMENTS)) && requestMap.get(DOCUMENTS) instanceof List
						&& !((List<Map<String, String>>) requestMap.get(DOCUMENTS)).isEmpty()) {
					if (!((List<Map<String, String>>) requestMap.get(DOCUMENTS)).parallelStream()
							.allMatch(doc -> doc.containsKey(DOC_CAT) && Objects.nonNull(doc.get(DOC_CAT))
									&& doc.containsKey(DOC_VALUE) && Objects.nonNull(doc.get(DOC_VALUE)))) {
						errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), String
								.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), DOCUMENTS));
					} else {
						checkForDuplicates(requestMap, errors);
					}
					((List<Map<String, String>>) requestMap.get(DOCUMENTS)).parallelStream().filter(
							doc -> !errors.hasErrors() && doc.containsKey(DOC_CAT) && Objects.nonNull(doc.get(DOC_CAT)))
							.forEach(doc -> {
								if (!identityMap.containsKey(doc.get(DOC_CAT))) {
									mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REQUEST_VALIDATOR,
											(VALIDATE_REQUEST + "- validateDocuments failed for " + doc.get(DOC_CAT)));
									errors.rejectValue(REQUEST,
											IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
											String.format(
													IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
													doc.get(DOC_CAT)));
								}
								if (StringUtils.isEmpty(doc.get(DOC_VALUE))) {
									mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REQUEST_VALIDATOR,
											(VALIDATE_REQUEST + "- empty doc value failed for " + doc.get(DOC_CAT)));
									errors.rejectValue(REQUEST,
											IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
											String.format(
													IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
													doc.get(DOC_CAT)));
								}
							});
				}
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REQUEST_VALIDATOR,
					(VALIDATE_REQUEST + ExceptionUtils.getStackTrace(e)));
			errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
							IdRepoConstants.ROOT_PATH.getValue()));
		}
	}

	/**
	 * Check for duplicates.
	 *
	 * @param requestMap the request map
	 * @param errors     the errors
	 */
	@SuppressWarnings("unchecked")
	private void checkForDuplicates(Map<String, Object> requestMap, Errors errors) {
		try {
			((List<Map<String, String>>) requestMap.get(DOCUMENTS)).parallelStream()
					.collect(Collectors.toMap(doc -> doc.get(DOC_CAT), doc -> doc.get(DOC_CAT)));
		} catch (IllegalStateException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO, ID_REQUEST_VALIDATOR,
					(VALIDATE_REQUEST + "  " + e.getMessage()));
			errors.rejectValue(REQUEST, IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), DOCUMENTS + " - "
							+ StringUtils.substringBefore(StringUtils.reverseDelimited(e.getMessage(), ' '), " ")));
		}
	}

	

	/**
	 * Convert to map.
	 *
	 * @param identity the identity
	 * @return the map
	 * @throws IdRepoAppException the id repo app exception
	 */
	private Map<String, Object> convertToMap(Object identity) throws IdRepoAppException {
		try {
			return mapper.readValue(mapper.writeValueAsBytes(identity), new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REQUEST_VALIDATOR, "convertToMap", "\n" + e.getMessage());
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), REQUEST), e);
		}
	}

	public void validateUin(String uin,String idType) throws IdRepoAppException {
		try {
			uinValidator.validateId(uin);
		} catch (InvalidIDException e) {
			if(!idType.equals(READ)) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REQUEST_VALIDATOR, "validateUin",
					"\n" + ExceptionUtils.getStackTrace(e));
				throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								env.getProperty(IdRepoConstants.MOSIP_KERNEL_IDREPO_JSON_PATH.getValue()))
								.replace(".", "/"));
			}
			else {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REQUEST_VALIDATOR, "validateUin",
						"\n" + ExceptionUtils.getStackTrace(e));
				throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), UIN), e);
			}
		}
	}

}
