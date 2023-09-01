package io.mosip.authentication.service.kyc.validator;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BINDING_PUBLIC_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PUBLIC_KEY_EXPONENT_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PUBLIC_KEY_MODULUS_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.AUTH_FACTOR_TYPE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.IDENTITY_KEY_BINDING_OBJECT;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.common.service.validator.BaseAuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class For IdentityKeyBindingRequestValidator extending the
 * BaseAuthRequestValidator{@link BaseAuthRequestValidator}}
 *
 * @author Mahammed Taheer
 * 
 */

@Component
public class IdentityKeyBindingRequestValidator extends AuthRequestValidator {	

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdentityKeyBindingRequestValidator.class);

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return IdentityKeyBindingRequestDTO.class.equals(clazz);
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
		IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO = (IdentityKeyBindingRequestDTO) target;
		if (identityKeyBindingRequestDTO != null) {
			BeanPropertyBindingResult authErrors = new BeanPropertyBindingResult(identityKeyBindingRequestDTO,
					errors.getObjectName());
			super.validate(identityKeyBindingRequestDTO, authErrors);
			errors.addAllErrors(authErrors);

			if (!errors.hasErrors()) {
				validateIdentityKeyBinding(identityKeyBindingRequestDTO.getIdentityKeyBinding(), errors);
			}

			if (!errors.hasErrors()) {
				validateIdentityKeyBindingPublicKey(identityKeyBindingRequestDTO.getIdentityKeyBinding().getPublicKeyJWK(), errors);
			}
			
			if (!errors.hasErrors()) {
				validateIdentityKeyBindingAuthFactorType(identityKeyBindingRequestDTO.getIdentityKeyBinding().getAuthFactorType(), errors);
			}
		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + IdAuthCommonConstants.REQUEST);
			errors.rejectValue(IdAuthCommonConstants.REQUEST, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), IdAuthCommonConstants.REQUEST));
		}

	}
	
	/**
	 * @return the allowedAuthType
	 */
	@Override
	protected String getAllowedAuthTypeProperty() {
		return EnvUtil.getEkycAllowedAuthType();
	}

	private void validateIdentityKeyBinding(IdentityKeyBindingDTO identityKeyBindingDTO, Errors errors) {

		if (Objects.isNull(identityKeyBindingDTO)) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, MISSING_INPUT_PARAMETER + IDENTITY_KEY_BINDING_OBJECT);
			errors.rejectValue(IDENTITY_KEY_BINDING_OBJECT, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDENTITY_KEY_BINDING_OBJECT }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} 
	}

	private void validateIdentityKeyBindingPublicKey(Map<String, Object> publicKeyJWK, Errors errors) {

		if (publicKeyJWK == null || publicKeyJWK.isEmpty()) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, MISSING_INPUT_PARAMETER + BINDING_PUBLIC_KEY);
			errors.rejectValue(BINDING_PUBLIC_KEY, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { BINDING_PUBLIC_KEY }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else {
			validatePublicKeyAttributes(publicKeyJWK, errors, PUBLIC_KEY_MODULUS_KEY);
			validatePublicKeyAttributes(publicKeyJWK, errors, PUBLIC_KEY_EXPONENT_KEY);
		}
	}

	private void validatePublicKeyAttributes(Map<String, Object> publicKeyJWK, Errors errors, String publicKeyAttribute) {
		if (!publicKeyJWK.containsKey(publicKeyAttribute) || (publicKeyJWK.get(publicKeyAttribute) == null) ||
			StringUtils.isEmpty((String) publicKeyJWK.get(publicKeyAttribute))) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, MISSING_INPUT_PARAMETER + publicKeyAttribute);
			errors.rejectValue(publicKeyAttribute, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { publicKeyAttribute }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	private void validateIdentityKeyBindingAuthFactorType(String authFactorType, Errors errors) {
		if (authFactorType == null || StringUtils.isEmpty(authFactorType.trim())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + AUTH_FACTOR_TYPE);
			errors.rejectValue(AUTH_FACTOR_TYPE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { AUTH_FACTOR_TYPE }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} 
	}
}
