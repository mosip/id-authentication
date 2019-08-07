package io.mosip.authentication.internal.service.validator;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;

@Component
public class UpdateAuthtypeStatusValidator extends IdAuthValidator {

	private static final String MISSING_AUTH_TYPES = "authType(s)";

	private static final String CONSENT_OBTAINED = "consentObtained";

	/** The Constant VALIDATE_REQUEST_TIMED_OUT. */
	private static final String VALIDATE_REQUEST_TIMED_OUT = "validateRequestTimedOut";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	@Override
	public boolean supports(Class<?> clazz) {
		return AuthTypeStatusDto.class.equals(clazz);
	}

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(UpdateAuthtypeStatusValidator.class);

	@Override
	public void validate(Object target, Errors errors) {
		AuthTypeStatusDto authTypeStatusDto = (AuthTypeStatusDto) target;
		if (authTypeStatusDto != null) {
			validateConsentRequest(authTypeStatusDto.isConsentObtained(), errors);
			if (!errors.hasErrors()) {
				validateIdvId(authTypeStatusDto.getIndividualId(), authTypeStatusDto.getIndividualIdType(), errors);
			}
			String requestTime = authTypeStatusDto.getRequestTime();
			if (!errors.hasErrors()) {
				validateReqTime(requestTime, errors, IdAuthCommonConstants.REQ_TIME);
			}

			if (!errors.hasErrors()) {
				validateRequestTimedOut(requestTime, errors);
			}

			if (!errors.hasErrors()) {
				validateAuthTypeRequest(authTypeStatusDto.getRequest(), errors);
			}

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
			Instant reqTimeInstance = DateUtils
					.parseToDate(reqTime, env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN)).toInstant();
			Instant now = Instant.now();
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					VALIDATE_REQUEST_TIMED_OUT,
					"reqTimeInstance" + reqTimeInstance.toString() + " -- current time : " + now.toString());
			Long reqDateMaxTimeLong = env
					.getProperty(IdAuthConfigKeyConstants.AUTHREQUEST_RECEIVED_TIME_ALLOWED_IN_MINUTES, Long.class);
			Instant maxAllowedEarlyInstant = now.minus(reqDateMaxTimeLong, ChronoUnit.MINUTES);
			if (reqTimeInstance.isBefore(maxAllowedEarlyInstant)) {
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						VALIDATE_REQUEST_TIMED_OUT,
						"Time difference in min : " + Duration.between(reqTimeInstance, now).toMinutes());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						VALIDATE_REQUEST_TIMED_OUT,
						"INVALID_AUTH_REQUEST_TIMESTAMP -- "
								+ String.format(IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage(),
										Duration.between(reqTimeInstance, now).toMinutes() - reqDateMaxTimeLong));
				errors.rejectValue(IdAuthCommonConstants.REQ_TIME,
						IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorCode(),
						new Object[] { reqDateMaxTimeLong },
						IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage());
			}
		} catch (DateTimeParseException | ParseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					VALIDATE_REQUEST_TIMED_OUT,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + IdAuthCommonConstants.REQ_TIME);
			errors.rejectValue(IdAuthCommonConstants.REQ_TIME,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IdAuthCommonConstants.REQ_TIME },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}

	}

	private void validateConsentRequest(boolean consentValue, Errors errors) {
		if (!consentValue) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					"consentObtained - " + consentValue);
			errors.rejectValue(CONSENT_OBTAINED, IdAuthenticationErrorConstants.CONSENT_NOT_AVAILABLE.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.CONSENT_NOT_AVAILABLE.getErrorMessage(),
							CONSENT_OBTAINED));
		}

	}

	private void validateAuthTypeRequest(List<AuthtypeStatus> list, Errors errors) {
		if (list == null || list.isEmpty()) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, MISSING_AUTH_TYPES);
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { MISSING_AUTH_TYPES },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else {
			validateAuthType(list, errors);
		}

	}

	private void validateAuthType(List<AuthtypeStatus> list, Errors errors) {

		Set<String> allowedAuthTypes = getAllowedAuthtype();
		Set<String> allowedAuthSubTypes = getAllowedSubTypes();
		for (AuthtypeStatus authtypeStatus : list) {
			String authType = authtypeStatus.getAuthType();
			String authSubType = authtypeStatus.getAuthSubType();
			if (StringUtils.isEmpty(authType)) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { MISSING_AUTH_TYPES },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, MISSING_AUTH_TYPES);
			} else if (!allowedAuthTypes.contains(authType)) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
						new Object[] { MatchType.Category.BIO.getType() + "-" + authType },
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, MISSING_AUTH_TYPES);
			} else if (authType.equals(Category.BIO.getType()) && !allowedAuthSubTypes.contains(authSubType)) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
						new Object[] { MatchType.Category.BIO.getType() + "-" + authSubType },
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, MISSING_AUTH_TYPES);
			}
		}
	}

	private Set<String> getAllowedSubTypes() {
		Set<String> availableAuthTypeInfos = new HashSet<>();
		BioAuthType[] authTypes = BioAuthType.values();
		for (BioAuthType authType : authTypes) {
			availableAuthTypeInfos.add(authType.getType());
		}
		return availableAuthTypeInfos;
	}

	private Set<String> getAllowedAuthtype() {
		return Stream.of(Category.values()).map(Category::getType).collect(Collectors.toSet());
	}

}
