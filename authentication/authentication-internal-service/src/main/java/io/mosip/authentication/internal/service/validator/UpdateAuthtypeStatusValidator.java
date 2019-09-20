package io.mosip.authentication.internal.service.validator;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Validator for Update Auth Type Status service.
 *
 * @author Dinesh T
 * @author Loganathan S
 */
@Component
public class UpdateAuthtypeStatusValidator extends IdAuthValidator {

	private static final String AUTH_TYPE_SEPERATOR = "-";
	/** The Constant MISSING_AUTH_TYPES. */
	private static final String AUTH_TYPES = "authType(s)";
	private static final String LOCKED = "locked";

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return AuthTypeStatusDto.class.equals(clazz);
	}

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(UpdateAuthtypeStatusValidator.class);

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		AuthTypeStatusDto authTypeStatusDto = (AuthTypeStatusDto) target;
		if (authTypeStatusDto != null) {
			validateConsentReq(authTypeStatusDto.isConsentObtained(), errors);
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
			
			if (!errors.hasErrors()) {
				validateLockedStatus(authTypeStatusDto.getRequest(), errors);
			}

		}

	}

	private void validateLockedStatus(List<AuthtypeStatus> list, Errors errors) {
		for (AuthtypeStatus authtypeStatus : list) {
			Boolean locked = authtypeStatus.getLocked();
			
			if (locked == null) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { LOCKED },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, LOCKED);
			}
			
		}
	}

	/**
	 * Validate auth type request.
	 *
	 * @param list the list
	 * @param errors the errors
	 */
	private void validateAuthTypeRequest(List<AuthtypeStatus> list, Errors errors) {
		if (list == null || list.isEmpty()) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, AUTH_TYPES);
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { AUTH_TYPES },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else {
			validateAuthType(list, errors);
		}

	}

	/**
	 * Validate auth type.
	 *
	 * @param list the list
	 * @param errors the errors
	 */
	private void validateAuthType(List<AuthtypeStatus> list, Errors errors) {

		Set<String> allowedAuthTypes = getAllowedAuthTypes();
		for (AuthtypeStatus authtypeStatus : list) {
			String authType = authtypeStatus.getAuthType();
			String authSubType = authtypeStatus.getAuthSubType();
			
			if (StringUtils.isEmpty(authType)) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { AUTH_TYPES },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, AUTH_TYPES);
			}
			
			String authTypeStr;
			if(authSubType == null || authSubType.trim().isEmpty()) {
				authTypeStr = authType;
			} else {
				authTypeStr = authType + AUTH_TYPE_SEPERATOR + authSubType;
			}

			if (!allowedAuthTypes.contains(authTypeStr)) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
						new Object[] { authTypeStr },
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, AUTH_TYPES);
			}
		}
	}

}
