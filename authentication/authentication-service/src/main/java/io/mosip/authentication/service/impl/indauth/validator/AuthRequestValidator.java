package io.mosip.authentication.service.impl.indauth.validator;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.BioType;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.impl.indauth.builder.AuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DOBMatchingStrategy;
import io.mosip.authentication.service.impl.indauth.service.demo.DOBType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.GenderType;
import io.mosip.authentication.service.impl.indauth.service.demo.IdMapping;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchType;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

/**
 * 
 * This class validates the parameters for Authorization Request. The class
 * {@code AuthRequestValidator} validates AuthRequestDTO
 * 
 * @author Manoj SP
 * @author Rakesh Roshan
 * 
 */
@Component
public class AuthRequestValidator extends BaseAuthRequestValidator {

	/** The Constant PIN_INFO. */
	private static final String PIN_INFO = "pinInfo";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant AUTH_REQUEST. */
	private static final String AUTH_REQUEST = "authRequest";

	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "authType";

	/** The Constant IDV_ID_TYPE. */
	private static final String IDV_ID_TYPE = "idvIdType";

	/** The Constant IDV_ID. */
	private static final String IDV_ID = "idvId";

	/** The Constant TXN_ID. */
	private static final String TXN_ID = "txnID";

	/** The Constant MISSING_INPUT_PARAMETER. */
	private static final String MISSING_INPUT_PARAMETER = "MISSING_INPUT_PARAMETER - ";

	/** The Constant INVALID_INPUT_PARAMETER. */
	private static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String AUTH_REQUEST_VALIDATOR = "AUTH_REQUEST_VALIDATOR";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant REQ_TIME. */
	private static final String REQ_TIME = "reqTime";

	/** The Constant REQ_HMAC. */
	private static final String REQ_HMAC = "reqHmac";

	/** The Constant VALIDATE_REQUEST_TIMED_OUT. */
	private static final String VALIDATE_REQUEST_TIMED_OUT = "validateRequestTimedOut";

	/** The Constant VALIDATE_CHECK_OTP_AUTH. */
	private static final String VALIDATE_CHECK_OTP_AUTH = "validate -> checkOTPAuth";

	/** The Constant REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS. */
	private static final String REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS = "authrequest.received-time-allowed.in-hours";

	/** The Constant PRIMARY_LANG_CODE. */
	private static final String PRIMARY_LANG_CODE = "mosip.primary.lang-code";

	/** The Constant SECONDARY_LANG_CODE. */
	private static final String SECONDARY_LANG_CODE = "mosip.secondary.lang-code";

	/** The Constant OTP_LENGTH. */
	private static final Integer OTP_LENGTH = 6;

	/** The Constant INVALID_AUTH_REQUEST. */
	private static final String INVALID_AUTH_REQUEST = "INVALID_AUTH_REQUEST-No auth type found";

	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String ID_AUTH_VALIDATOR = "ID_AUTH_VALIDATOR";

	/** The Constant A_Z0_9_10. */
	private static final Pattern A_Z0_9_10 = Pattern.compile("^[A-Z0-9]{10}");

	/** The Constant MUA_CODE. */
	private static final String MUA_CODE = "muaCode";

	/** The uin validator. */
	@Autowired
	private UinValidatorImpl uinValidator;

	/** The vid validator. */
	@Autowired
	private VidValidatorImpl vidValidator;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(AuthRequestValidator.class);

	/** The env. */
	@Autowired
	private Environment env;

	/** The date helper. */
	@Autowired
	private DateHelper dateHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return AuthRequestDTO.class.equals(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		AuthRequestDTO authRequestDto = (AuthRequestDTO) target;

		if (authRequestDto != null) {
			validateReqTime(authRequestDto.getReqTime(), errors);

			if (!errors.hasErrors()) {
				validateRequestTimedOut(authRequestDto.getReqTime(), errors);
			}

			if (!errors.hasErrors()) {
				super.validate(target, errors);

				validateIdvId(authRequestDto.getIdvId(), authRequestDto.getIdvIdType(), errors);

				validateMuaCode(authRequestDto.getMuaCode(), errors);

				validateTxnId(authRequestDto.getTxnID(), errors);

				validateReqHmac(authRequestDto.getReqHmac(), errors);

				//validateBioDetails(authRequestDto, errors);

				if (!errors.hasErrors()) {
					checkAuthRequest(authRequestDto, errors);
				}
			}
		} else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Validate req hmac.
	 *
	 * @param reqHmac the req hmac
	 * @param errors  the errors
	 */
	private void validateReqHmac(String reqHmac, Errors errors) {
		if (Objects.isNull(reqHmac)) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + REQ_HMAC);
			errors.rejectValue(REQ_HMAC, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { REQ_HMAC },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate request timed out.
	 *
	 * @param reqTime the req time
	 * @param errors  the errors
	 */
	private void validateRequestTimedOut(String reqTime, Errors errors) {
		try {
			Instant reqTimeInstance = dateHelper.convertStringToDate(reqTime).toInstant();
			Instant now = Instant.now();
			mosipLogger.debug(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
					"reqTimeInstance" + reqTimeInstance.toString() + " -- current time : " + now.toString());
			if (Duration.between(reqTimeInstance, now).toHours() > env
					.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Integer.class)) {
				mosipLogger.debug(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
						"Time difference in min : " + Duration.between(reqTimeInstance, now).toMinutes());
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
						"INVALID_AUTH_REQUEST_TIMESTAMP -- " + String.format(
								IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST_TIMESTAMP.getErrorMessage(),
								Duration.between(reqTimeInstance, now).toMinutes()
										- env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Long.class)));
				errors.rejectValue(REQ_TIME,
						IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST_TIMESTAMP.getErrorCode(),
						new Object[] { "24" },
						IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST_TIMESTAMP.getErrorMessage());
			}
		} catch (DateTimeParseException | IDDataValidationException e) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
					INVALID_INPUT_PARAMETER + REQ_TIME);
			errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { REQ_TIME },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}

	}

	/**
	 * Check auth request.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkAuthRequest(AuthRequestDTO authRequest, Errors errors) {
		AuthTypeDTO authType = authRequest.getAuthType();
		if (!Objects.isNull(authType)) {
			boolean anyAuthType = Stream
					.<Supplier<Boolean>>of(authType::isOtp, authType::isBio, authType::isAddress,
							authType::isFullAddress, authType::isPin, authType::isPersonalIdentity)
					.anyMatch(Supplier<Boolean>::get);

			if (!anyAuthType) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, INVALID_AUTH_REQUEST);
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { AUTH_TYPE },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());

			} else if (authType.isOtp()) {
				checkOTPAuth(authRequest, errors);
			} else if (authType.isPersonalIdentity() || authType.isAddress() || authType.isFullAddress()) {
				checkDemoAuth(authRequest, errors);
			}
		} else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + AUTH_TYPE);
			errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { AUTH_TYPE },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Check demo auth.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkDemoAuth(AuthRequestDTO authRequest, Errors errors) {
		AuthType[] authTypes = AuthType.values();
		boolean hasMatch = false;
		for (AuthType authType : authTypes) {
			if (authType.isAuthTypeEnabled(authRequest)) {
				Set<MatchType> associatedMatchTypes = authType.getAssociatedMatchTypes();
				for (MatchType matchType : associatedMatchTypes) {
					List<IdentityInfoDTO> identityInfos = matchType.getIdentityInfoFunction()
							.apply(authRequest.getRequest().getIdentity());
					if (identityInfos != null) {
						hasMatch = true;
						checkIdentityInfoValue(identityInfos, errors);
						checkLangaugeDetails(matchType, identityInfos, errors);
					}
				}
			}
		}
		checkOtherValues(authRequest, errors, hasMatch);
	}

	/**
	 * Check identity info value.
	 *
	 * @param identityInfos the identity infos
	 * @param errors        the errors
	 */
	private void checkIdentityInfoValue(List<IdentityInfoDTO> identityInfos, Errors errors) {
		for (IdentityInfoDTO identityInfoDTO : identityInfos) {
			if (Objects.isNull(identityInfoDTO.getValue())) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "IdentityInfoDTO is invalid");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "IdentityInfoDTO" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}

		}

	}

	/**
	 * Check other values.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 * @param hasMatch    the has match
	 */
	private void checkOtherValues(AuthRequestDTO authRequest, Errors errors, boolean hasMatch) {
		if (!hasMatch) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Missing IdentityInfoDTO");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "IdentityInfoDTO" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			checkDOB(authRequest, errors);
			checkDOBType(authRequest, errors);
			checkAge(authRequest, errors);
			checkGender(authRequest, errors);
		}
	}

	/**
	 * Check gender.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkGender(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> genderList = DemoMatchType.GENDER.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (genderList != null) {
			for (IdentityInfoDTO identityInfoDTO : genderList) {
				if (!GenderType.isTypePresent(identityInfoDTO.getValue())) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
							"Demographic data – Gender(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "gender" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}

			}
		}
	}

	/**
	 * Check DOB type.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkDOBType(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> dobTypeList = DemoMatchType.DOBTYPE.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (dobTypeList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobTypeList) {
				if (!DOBType.isTypePresent(identityInfoDTO.getValue())) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
							"Demographic data – DOBType(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "DOBType" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}

	}

	/**
	 * Check age.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkAge(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> ageList = DemoMatchType.AGE.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (ageList != null) {
			for (IdentityInfoDTO identityInfoDTO : ageList) {
				try {
					Integer.parseInt(identityInfoDTO.getValue());
				} catch (NumberFormatException e) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
							"Demographic data – Age(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "age" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check DOB.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkDOB(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> dobList = DemoMatchType.DOB.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (dobList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobList) {
				try {
					DOBMatchingStrategy.getDateFormat().parse(identityInfoDTO.getValue());
				} catch (ParseException e) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
							"Demographic data – DOB(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "dob" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check langauge details.
	 *
	 * @param demoMatchType the demo match type
	 * @param identityInfos the identity infos
	 * @param errors        the errors
	 */
	private void checkLangaugeDetails(MatchType demoMatchType, List<IdentityInfoDTO> identityInfos, Errors errors) {
		String priLangCode = env.getProperty(PRIMARY_LANG_CODE);
		String secLangCode = env.getProperty(SECONDARY_LANG_CODE);

		Map<String, Long> langCount = identityInfos.stream().map((IdentityInfoDTO idInfo) -> {
			String language = idInfo.getLanguage();
			if (language == null) {
				language = priLangCode;
			}
			return new SimpleEntry<>(language, idInfo);
		}).collect(Collectors.groupingBy(Entry::getKey, Collectors.counting()));

		Long primaryLangCount = langCount.get(priLangCode);
		Long secondaryLangCount = langCount.get(secLangCode);

		if (secondaryLangCount != null) {
			checkSecondayLanguage(demoMatchType, secondaryLangCount, errors);
		}

		boolean anyOtherLang = langCount.keySet().stream()
				.anyMatch(lang -> lang != null && !lang.equals(priLangCode) && !lang.equals(secLangCode));

		if (primaryLangCount != null && primaryLangCount > 1 || anyOtherLang) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
					"Invalid or Multiple Primary language code");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "PrimaryLanguageCode" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Check seconday language.
	 *
	 * @param demoMatchType the demo match type
	 * @param secCount      the sec count
	 * @param errors        the errors
	 */
	private void checkSecondayLanguage(MatchType demoMatchType, long secCount, Errors errors) {
		IdMapping idMapping = demoMatchType.getIdMapping();
		boolean checkForSecondaryLanguage = Stream.of(DemoMatchType.values())
				.filter(matchType -> matchType.getIdMapping().equals(idMapping))
				.anyMatch(matchType -> matchType.getLanguageType().equals(LanguageType.SECONDARY_LANG));
		if (checkForSecondaryLanguage) {
			if (secCount > 1) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
						"Invalid or Multiple Seconday language code");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "SecondayLanguageCode" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		} else {
			if (secCount > 0) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER, "Invalid language code");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "SecondayLanguageCode" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}

	}

	/**
	 * Check OTP auth.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkOTPAuth(AuthRequestDTO authRequest, Errors errors) {
		Optional<String> otp = getOtpValue(authRequest);
		if (!otp.isPresent()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
					"INVALID_OTP - pinType is not OTP");
			errors.rejectValue(PIN_INFO, IdAuthenticationErrorConstants.OTP_NOT_PRESENT.getErrorCode(),
					IdAuthenticationErrorConstants.OTP_NOT_PRESENT.getErrorMessage());
		} else if (OTP_LENGTH != otp.orElse("").length()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
					"INVALID_OTP - pinType is not OTP");
			errors.rejectValue(PIN_INFO, IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage());
		}
	}

	/**
	 * Gets the otp value.
	 *
	 * @param authreqdto the authreqdto
	 * @return the otp value
	 */
	private Optional<String> getOtpValue(AuthRequestDTO authreqdto) {
		return Optional.ofNullable(authreqdto.getPinInfo())
				.flatMap(pinInfos -> pinInfos.stream()
						.filter(pinInfo -> pinInfo.getType() != null && pinInfo.getType().equals(PinType.OTP.getType()))
						.findAny())
				.map(PinInfo::getValue);
	}

	/**
	 * Adding IdAuthValidator Methods in AuthRequestValidator Class *.
	 *
	 * @param id     the id
	 * @param idType the id type
	 * @param errors the errors
	 */

	/**
	 * Validate individual's id - check whether id is null or not and if valid,
	 * validates idType and UIN/VID.
	 *
	 * @param id              the id
	 * @param idType          the id type
	 * @param idFieldName     the id field name
	 * @param idTypeFieldName the id type field name
	 * @param errors          the errors
	 */
	protected void validateIdvId(String id, String idType, Errors errors) {
		if (Objects.isNull(id)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + IDV_ID);
			errors.rejectValue(IDV_ID, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDV_ID }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else {
			validateIdtypeUinVid(id, idType, errors);
		}
	}

	/**
	 * Validate mua code - check whether it is of length 10 and alphanumeric.
	 *
	 * @param muaCode the mua code
	 * @param errors  the errors
	 */
	protected void validateMuaCode(String muaCode, Errors errors) {
		if (Objects.isNull(muaCode)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + MUA_CODE);
			errors.rejectValue(MUA_CODE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { MUA_CODE },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else if (!A_Z0_9_10.matcher(muaCode).matches()) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - muaCode - value -> " + muaCode);
			errors.rejectValue(MUA_CODE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { MUA_CODE },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate idtype uin vid.
	 *
	 * @param id     the id
	 * @param idType the id type
	 * @param errors the errors
	 */
	private void validateIdtypeUinVid(String id, String idType, Errors errors) {
		if (Objects.isNull(idType)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + IDV_ID_TYPE);
			errors.rejectValue(IDV_ID_TYPE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDV_ID_TYPE },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else if (idType.equals(IdType.UIN.getType())) {
			try {
				uinValidator.validateId(id);
			} catch (InvalidIDException e) {
				mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "MosipInvalidIDException - " + e);
				errors.rejectValue(IDV_ID, IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage());
			}
		} else if (idType.equals(IdType.VID.getType())) {
			try {
				vidValidator.validateId(id);
			} catch (InvalidIDException e) {
				mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "MosipInvalidIDException - " + e);
				errors.rejectValue(IDV_ID, IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_VID.getErrorMessage());
			}
		} else {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "INCORRECT_IDTYPE - " + idType);
			errors.rejectValue(IDV_ID_TYPE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDV_ID_TYPE },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate txn id - check whether it is of length 10 and alphanumeric.
	 *
	 * @param txnID  the txn ID
	 * @param errors the errors
	 */
	protected void validateTxnId(String txnID, Errors errors) {
		if (Objects.isNull(txnID)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + TXN_ID);
			errors.rejectValue(TXN_ID, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { TXN_ID }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}

		if (!A_Z0_9_10.matcher(txnID).matches()) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - txnID - value -> " + txnID);
			errors.rejectValue(TXN_ID, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { TXN_ID }, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate req time.
	 *
	 * @param reqTime the req time
	 * @param errors  the errors
	 */
	protected void validateReqTime(String reqTime, Errors errors) {
		if (Objects.isNull(reqTime)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + REQ_TIME);
			errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { REQ_TIME },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	private void validateBioDetails(AuthRequestDTO authRequestDTO, Errors errors) {

		AuthTypeDTO authTypeDTO = authRequestDTO.getAuthType();
		List<BioInfo> bioInfo = authRequestDTO.getBioInfo();
		if ((authTypeDTO != null && authTypeDTO.isBio()) && (bioInfo != null && !bioInfo.isEmpty())) {

			if (isContainBioInfo(bioInfo, BioType.FGRMIN) && isDuplicateBioRequest(authRequestDTO, BioType.FGRMIN)) {
				checkAtleastOneFingerRequestAvailable(authRequestDTO, errors);
				validateFingerRequestCount(authRequestDTO, errors);
			}
			if (isContainBioInfo(bioInfo, BioType.FGRIMG) && isDuplicateBioRequest(authRequestDTO, BioType.FGRIMG)) {
				checkAtleastOneFingerRequestAvailable(authRequestDTO, errors);
				validateFingerRequestCount(authRequestDTO, errors);
			}

			if (isContainBioInfo(bioInfo, BioType.IRISIMG) && isDuplicateBioRequest(authRequestDTO, BioType.IRISIMG)) {
				checkAtleastOneIrisRequestAvailable(authRequestDTO, errors);
				validateIrisRequestCount(authRequestDTO);
			}

			if (isContainBioInfo(bioInfo, BioType.FACEIMG) && isDuplicateBioRequest(authRequestDTO, BioType.FACEIMG)) {
				checkAtleastOneFaceRequestAvailable(authRequestDTO, errors);
			}

		} else {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}

	}

	private boolean isContainBioInfo(List<BioInfo> bioInfoList, BioType bioType) {
		return bioInfoList.parallelStream().filter(bio -> bio.getBioType() != null && !bio.getBioType().isEmpty())
				.anyMatch(bio -> bio.getBioType().equals(bioType.getType()));
	}

	public void checkAtleastOneFingerRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {

		boolean isAtleastOneFingerRequestAvailable = Optional.ofNullable(authRequestDTO.getRequest())
				.map(RequestDTO::getIdentity).map(IdentityDTO::getLeftIndex).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftLittle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftMiddle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftRing).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftThumb).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightIndex).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightLittle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightMiddle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightRing).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightThumb).filter(list -> !list.isEmpty()).isPresent();
		if (!isAtleastOneFingerRequestAvailable) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}

	}

	public void checkAtleastOneIrisRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {
		boolean isIrisRequestAvailable = authRequestDTO.getRequest() != null
				&& authRequestDTO.getRequest().getIdentity() != null
				&& authRequestDTO.getRequest().getIdentity().getLeftEye() != null
				&& !authRequestDTO.getRequest().getIdentity().getLeftEye().isEmpty()
				|| authRequestDTO.getRequest() != null && authRequestDTO.getRequest().getIdentity() != null
						&& authRequestDTO.getRequest().getIdentity().getRightEye() != null
						&& !authRequestDTO.getRequest().getIdentity().getRightEye().isEmpty();
		if (!isIrisRequestAvailable) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}
	}

	public void checkAtleastOneFaceRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {
		boolean isFaceRequestAvailable = authRequestDTO.getRequest() != null
				&& authRequestDTO.getRequest().getIdentity() != null
				&& authRequestDTO.getRequest().getIdentity().getFace() != null;
		if (!isFaceRequestAvailable) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}
	}

	public boolean isDuplicateBioRequest(AuthRequestDTO authRequestDTO, BioType bioType) {
		List<BioInfo> bioInfo = authRequestDTO.getBioInfo();
		Long bioTypeCount = Optional.ofNullable(bioInfo).map(List::parallelStream)
				.map(stream -> stream
						.filter(bio -> bio.getBioType().isEmpty() && bio.getBioType().equals(bioType.getType()))
						.count())
				.orElse((long) 0);

		return bioTypeCount <= 1;

	}

	public void validateFingerRequestCount(AuthRequestDTO authRequestDTO, Errors errors) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();

		// --- Left Finger ---
		List<IdentityInfoDTO> leftThumb = identity.getLeftThumb();
		Long leftThumbCount = Optional.ofNullable(leftThumb).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> leftIndex = identity.getLeftIndex();
		Long leftIndexCount = Optional.ofNullable(leftIndex).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> leftMiddle = identity.getLeftMiddle();
		Long leftMiddleCount = Optional.ofNullable(leftMiddle).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> leftRing = identity.getLeftRing();
		Long leftRingCount = Optional.ofNullable(leftRing).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> leftLittle = identity.getLeftLittle();
		Long leftLittleCount = Optional.ofNullable(leftLittle).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		// - Right Finger ----
		List<IdentityInfoDTO> rightThumb = identity.getRightThumb();
		Long rightThumbCount = Optional.ofNullable(rightThumb).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightIndex = identity.getRightIndex();
		Long rightIndexCount = Optional.ofNullable(rightIndex).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightMiddle = identity.getRightMiddle();
		Long rightMiddleCount = Optional.ofNullable(rightMiddle).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightRing = identity.getRightRing();
		Long rightRingCount = Optional.ofNullable(rightRing).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightLittle = identity.getRightLittle();
		Long rightLittleCount = Optional.ofNullable(rightLittle).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		if (leftThumbCount > 1 || leftIndexCount > 1 || leftMiddleCount > 1 || leftRingCount > 1 || leftLittleCount > 1
				|| rightThumbCount > 1 || rightIndexCount > 1 || rightMiddleCount > 1 || rightRingCount > 1
				|| rightLittleCount > 1) {

			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "Duplicate fingers ");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorMessage(), REQUEST));
		}

		Long fingerCountExceeding = leftThumbCount + leftIndexCount + leftMiddleCount + leftRingCount + leftLittleCount
				+ rightThumbCount + rightIndexCount + rightMiddleCount + rightRingCount + rightLittleCount;
		if (fingerCountExceeding > 10) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorMessage(), REQUEST));
		}
	}

	private void validateIrisRequestCount(AuthRequestDTO authRequestDTO) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();

		List<IdentityInfoDTO> leftEye = identity.getLeftEye();
		Long leftEyeCount = Optional.ofNullable(leftEye).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightEye = identity.getRightEye();
		Long rightEyeCount = Optional.ofNullable(rightEye).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		if (leftEyeCount > 1 || rightEyeCount > 1) {
			// add errors
		}

	}
}
