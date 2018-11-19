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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.PinType;
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
import io.mosip.authentication.service.validator.IdAuthValidator;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * This class validates the parameters for Authorization Request. The class
 * {@code AuthRequestValidator} validates AuthRequestDTO
 * 
 * @author Arun Bose
 */
@Component
public class AuthRequestValidator extends IdAuthValidator {

	private static final String PIN_INFO = "pinInfo";

	private static final String REQUEST = "request";
	
	private static final String AUTH_REQUEST = "authRequest";

	private static final String AUTH_TYPE = "authType";

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

	private static final String REQ_HMAC = "reqHmac";

	private static final String VALIDATE_REQUEST_TIMED_OUT = "validateRequestTimedOut";
	private static final String VALIDATE_CHECK_OTP_AUTH = "validate -> checkOTPAuth";

	private static final String REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS = "authrequest.received-time-allowed.in-hours";
	private static final String PRIMARY_LANG_CODE = "mosip.primary.lang-code";
	private static final String SECONDARY_LANG_CODE = "mosip.secondary.lang-code";

	private static final Integer OTP_LENGTH = 6;

	private static final String INVALID_AUTH_REQUEST = "INVALID_AUTH_REQUEST-No auth type found";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(AuthRequestValidator.class);

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private DateHelper dateHelper;

	@Override
	public boolean supports(Class<?> clazz) {
		return AuthRequestDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AuthRequestDTO authRequestDto = (AuthRequestDTO) target;
		if (authRequestDto != null) {
			validateReqTime(authRequestDto.getReqTime(), errors);

			if (!errors.hasErrors()) {
				validateRequestTimedOut(authRequestDto.getReqTime(), errors);
			}

			if (!errors.hasErrors()) {

				validateId(authRequestDto.getId(), errors);

				validateVer(authRequestDto.getVer(), errors);

				validateIdvId(authRequestDto.getIdvId(), authRequestDto.getIdvIdType(), errors);

				validateMuaCode(authRequestDto.getMuaCode(), errors);

				validateTxnId(authRequestDto.getTxnID(), errors);

				validateReqHmac(authRequestDto.getReqHmac(), errors);
				
				checkAuthRequest(authRequestDto, errors);
			}
		}else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), AUTH_REQUEST));
		}
	}

	private void validateReqHmac(String reqHmac, Errors errors) {
		if (Objects.isNull(reqHmac)) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + REQ_HMAC);
			errors.rejectValue(REQ_HMAC, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), REQ_HMAC));
		}
	}

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
						"INVALID_AUTH_REQUEST_TIMESTAMP -- "
								+ String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
										Duration.between(reqTimeInstance, now).toMinutes()
												- env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Long.class)));
				errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
								Duration.between(reqTimeInstance, now).toMinutes()
										- env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Long.class)));
			}
		} catch (DateTimeParseException | IDDataValidationException e) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_REQUEST_TIMED_OUT,
					INVALID_INPUT_PARAMETER + REQ_TIME);
			errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), REQ_TIME));
		}

	}

	private void checkAuthRequest(AuthRequestDTO authRequest, Errors errors) {
		AuthTypeDTO authType = authRequest.getAuthType();
		if( !Objects.isNull(authType)) {
			boolean anyAuthType = Stream.<Supplier<Boolean>>of(authType::isOtp, authType::isBio, authType::isAddress,
					authType::isFullAddress, authType::isPin, authType::isPersonalIdentity)
					.anyMatch(Supplier<Boolean>::get);

			if (!anyAuthType) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
						INVALID_AUTH_REQUEST);
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] {"authType"}, 
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());

			} else if (authType.isOtp()) {
				checkOTPAuth(authRequest, errors);
			} else if (authType.isPersonalIdentity() || authType.isAddress() || authType.isFullAddress()) {
				checkDemoAuth(authRequest, errors);
			}
		}else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					MISSING_INPUT_PARAMETER + "authtype");
			errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"authType"}, 
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

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

	private void checkIdentityInfoValue(List<IdentityInfoDTO> identityInfos, Errors errors) {
		for (IdentityInfoDTO identityInfoDTO : identityInfos) {
			if (Objects.isNull(identityInfoDTO.getValue())) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
						"IdentityInfoDTO is invalid");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"IdentityInfoDTO"}, 
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}

		}
		
	}

	private void checkOtherValues(AuthRequestDTO authRequest, Errors errors, boolean hasMatch) {
		if (!hasMatch) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"Missing IdentityInfoDTO");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"IdentityInfoDTO"}, 
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			checkDOB(authRequest, errors);
			checkDOBType(authRequest, errors);
			checkAge(authRequest, errors);
			checkGender(authRequest, errors);
		}
	}

	private void checkGender(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> genderList = DemoMatchType.GENDER.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (genderList != null) {
			for (IdentityInfoDTO identityInfoDTO : genderList) {
				if (!GenderType.isTypePresent(identityInfoDTO.getValue())) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Demographic data – Gender(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"gender"}, 
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}

			}
		}
	}

	private void checkDOBType(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> dobTypeList = DemoMatchType.DOBTYPE.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (dobTypeList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobTypeList) {
				if (!DOBType.isTypePresent(identityInfoDTO.getValue())) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Demographic data – DOBType(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"DOBType"}, 
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}

	}

	private void checkAge(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> ageList = DemoMatchType.AGE.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (ageList != null) {
			for (IdentityInfoDTO identityInfoDTO : ageList) {
				try {
					Integer.parseInt(identityInfoDTO.getValue());
				} catch (NumberFormatException e) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Demographic data – Age(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"age"}, 
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	private void checkDOB(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> dobList = DemoMatchType.DOB.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (dobList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobList) {
				try {
					DOBMatchingStrategy.DATE_FORMAT.parse(identityInfoDTO.getValue());
				} catch (ParseException e) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Demographic data – DOB(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"dob"}, 
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	private void checkLangaugeDetails(MatchType demoMatchType, List<IdentityInfoDTO> identityInfos, Errors errors) {
		String priLangCode = env.getProperty(PRIMARY_LANG_CODE);
		String secLangCode = env.getProperty(SECONDARY_LANG_CODE);

		Map<String, Long> langCount = 
				identityInfos.stream()
				.map((IdentityInfoDTO idInfo) -> {
					String language = idInfo.getLanguage();
					if(language == null) {
						language = priLangCode;
					}
					return new SimpleEntry<>(language, idInfo);
				})
				.collect(Collectors.groupingBy(Entry::getKey, Collectors.counting()));

		Long primaryLangCount = langCount.get(priLangCode);
		Long secondaryLangCount = langCount.get(secLangCode);

		if (secondaryLangCount != null) {
			checkSecondayLanguage(demoMatchType, secondaryLangCount, errors);
		}

		boolean anyOtherLang = langCount.keySet().stream()
				.anyMatch(lang -> lang != null && !lang.equals(priLangCode) && !lang.equals(secLangCode));

		if (primaryLangCount == null || primaryLangCount > 1 || anyOtherLang) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER, "Invalid or Multiple Primary language code");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"PrimaryLanguageCode"}, 
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	private void checkSecondayLanguage(MatchType demoMatchType, long secCount, Errors errors) {
		IdMapping idMapping = demoMatchType.getIdMapping();
		boolean checkForSecondaryLanguage = Stream.of(DemoMatchType.values())
				.filter(matchType -> matchType.getIdMapping().equals(idMapping))
				.anyMatch(matchType -> matchType.getLanguageType().equals(LanguageType.SECONDARY_LANG));
		if (checkForSecondaryLanguage) {
			if (secCount > 1) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER, "Invalid or Multiple Seconday language code");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"SecondayLanguageCode"}, 
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		} else if (secCount > 0) {			
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER, "Invalid language code");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] {"SecondayLanguageCode"}, 
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());			
		}

	}

	private void checkOTPAuth(AuthRequestDTO authRequest, Errors errors) {
		Optional<String> otp = getOtpValue(authRequest);
		if (!otp.isPresent()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
					"INVALID_OTP - pinType is not OTP");
			errors.rejectValue(PIN_INFO, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), PIN_INFO));
		}else if (OTP_LENGTH != otp.orElse("").length()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
					"INVALID_OTP - pinType is not OTP");
			errors.rejectValue(PIN_INFO, IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage(), PIN_INFO));
		}
	}

	private Optional<String> getOtpValue(AuthRequestDTO authreqdto) {
		return Optional.ofNullable(authreqdto.getPinInfo())
				.flatMap(pinInfos -> pinInfos.stream()
						.filter(pinInfo -> pinInfo.getType() != null
								&& pinInfo.getType().equals(PinType.OTP.getType()))
						.findAny())
				.map(PinInfo::getValue);
	}

}
