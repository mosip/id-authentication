package io.mosip.authentication.kyc.service.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.common.service.validator.BaseAuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class For KycAuthRequestValidator extending the
 * BaseAuthRequestValidator{@link BaseAuthRequestValidator}}
 *
 * @author Prem Kumar
 * @author Dinesh Karuppiah.T
 * 
 * 
 */

@Component
public class KycAuthRequestValidator extends AuthRequestValidator {
	
	/** The Constant SECONDARY_LANG_CODE. */
	private static final String SECONDARY_LANG_CODE = "secondaryLangCode";
	

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(KycAuthRequestValidator.class);

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return KycAuthRequestDTO.class.equals(clazz);
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
		KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO) target;
		if (kycAuthRequestDTO != null) {
			BeanPropertyBindingResult authErrors = new BeanPropertyBindingResult(kycAuthRequestDTO,
					errors.getObjectName());
			super.validate(kycAuthRequestDTO, authErrors);
			errors.addAllErrors(authErrors);

			if (!errors.hasErrors()) {
				validateConsentReq(kycAuthRequestDTO, errors);
			}

			if (!errors.hasErrors()) {
				validateLangCode(kycAuthRequestDTO.getSecondaryLangCode(), errors, SECONDARY_LANG_CODE);
			}

		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + IdAuthCommonConstants.REQUESTEDAUTH);
			errors.rejectValue(IdAuthCommonConstants.REQUESTEDAUTH, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), IdAuthCommonConstants.REQUESTEDAUTH));
		}

	}
	
	/**
	 * @return the allowedAuthType
	 */
	@Override
	protected String getAllowedAuthTypeProperty() {
		return IdAuthConfigKeyConstants.EKYC_ALLOWED_AUTH_TYPE;
	}

}
