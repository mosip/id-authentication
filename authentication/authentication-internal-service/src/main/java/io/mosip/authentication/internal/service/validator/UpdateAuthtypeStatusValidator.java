package io.mosip.authentication.internal.service.validator;

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
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class UpdateAuthtypeStatusValidator.
 *
 * @author Dinesh T
 */
@Component
public class UpdateAuthtypeStatusValidator extends IdAuthValidator {

	/** The Constant MISSING_AUTH_TYPES. */
	private static final String MISSING_AUTH_TYPES = "authType(s)";

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
					IdAuthCommonConstants.VALIDATE, MISSING_AUTH_TYPES);
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { MISSING_AUTH_TYPES },
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

	/**
	 * Gets the allowed sub types.
	 *
	 * @return the allowed sub types
	 */
	private Set<String> getAllowedSubTypes() {
		return Stream.of(BioAuthType.values()).map(BioAuthType::getType).collect(Collectors.toSet());
	}

	/**
	 * Gets the allowed authtype.
	 *
	 * @return the allowed authtype
	 */
	private Set<String> getAllowedAuthtype() {
		return Stream.of(Category.values()).map(Category::getType).collect(Collectors.toSet());
	}

}
