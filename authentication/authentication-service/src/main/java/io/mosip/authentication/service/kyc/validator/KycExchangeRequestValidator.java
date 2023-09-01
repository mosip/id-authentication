package io.mosip.authentication.service.kyc.validator;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.common.service.validator.BaseAuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class For KycExchangeRequestValidator extending the
 * BaseAuthRequestValidator{@link BaseAuthRequestValidator}}
 *
 * @author Mahammed Taheer
 * 
 * 
 */

@Component
public class KycExchangeRequestValidator extends AuthRequestValidator {	

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(KycExchangeRequestValidator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return KycExchangeRequestDTO.class.equals(clazz);
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
		KycExchangeRequestDTO kycExchangeRequestDTO = (KycExchangeRequestDTO) target;
		if (kycExchangeRequestDTO != null) {
			if (!errors.hasErrors()) {
				validateReqTime(kycExchangeRequestDTO.getRequestTime(), errors, IdAuthCommonConstants.REQ_TIME);
			}

			if (!errors.hasErrors()) {
				validateKycToken(kycExchangeRequestDTO.getKycToken(), errors, IdAuthCommonConstants.KYC_TOKEN);
			}

			// commented below validation because end user can provide nil consent.
			/* if (!errors.hasErrors()) {
				validateConsentObtainedList(kycExchangeRequestDTO.getConsentObtained(), errors, IdAuthCommonConstants.CONSENT_OBTAINED);
			} */

			if (!errors.hasErrors()) {
				validateTxnId(kycExchangeRequestDTO.getTransactionID(), errors, IdAuthCommonConstants.TRANSACTION_ID);
			}
			
		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + IdAuthCommonConstants.REQUEST);
			errors.rejectValue(IdAuthCommonConstants.REQUEST, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), IdAuthCommonConstants.REQUEST));
		}

	}

	private void validateKycToken(String kycToken, Errors errors, String paramName) {

		if (kycToken == null || StringUtils.isEmpty(kycToken.trim())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + paramName);
			errors.rejectValue(paramName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} 
	}

	private void validateConsentObtainedList(List<String> consentObList, Errors errors, String paramName) {

		if (consentObList == null || consentObList.size() == 0) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + paramName);
			errors.rejectValue(paramName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} 
	}
}
