package io.mosip.authentication.internal.service.validator;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;

@Component
public class UpdateAuthtypeStatusValidator extends IdAuthValidator {

	private static final String MISSING_AUTH_TYPES = "authType(s)";

	private static final String CONSENT_OBTAINED = "consentObtained";

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
			if (!errors.hasErrors()) {
				validateReqTime(authTypeStatusDto.getRequestTime(), errors, IdAuthCommonConstants.REQ_TIME);
			}
			if (!errors.hasErrors()) {
				validateAuthTypeRequest(authTypeStatusDto.getRequest(), errors);
			}

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
			} else if (!allowedAuthSubTypes.contains(authSubType)) {
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
			availableAuthTypeInfos.add(authType.getConfigKey().toLowerCase());
		}
		return availableAuthTypeInfos;
	}

	private Set<String> getAllowedAuthtype() {
		Set<String> allowedAuthTypes = new HashSet<>();
		allowedAuthTypes.add(MatchType.Category.DEMO.getType());
		allowedAuthTypes.add(MatchType.Category.BIO.getType());
		allowedAuthTypes.add(MatchType.Category.OTP.getType());
		allowedAuthTypes.add(MatchType.Category.SPIN.getType());
		return allowedAuthTypes;
	}

}
