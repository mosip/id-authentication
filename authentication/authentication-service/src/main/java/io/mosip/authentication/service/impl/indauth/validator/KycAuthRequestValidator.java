package io.mosip.authentication.service.impl.indauth.validator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.EkycAuthType;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * @author Prem Kumar
 * @author Dinesh Karuppiah.T
 *
 */

@Component
public class KycAuthRequestValidator extends BaseAuthRequestValidator {

	@Autowired
	private AuthRequestValidator authRequestValidator;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(KycAuthRequestValidator.class);

	private static final String AUTH_REQUEST = "authRequest";

	/** The Constant INVALID_INPUT_PARAMETER. */
	private static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String KYC_REQUEST_VALIDATOR = "AUTH_REQUEST_VALIDATOR";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	private static final String CONSENT_REQ = "consentReq";

	private static final String ACCESS_LEVEL = "ekyc.mua.accesslevel.";

	private static final String INVALID_AUTH_REQUEST = "Invalid Auth Request";

	private static final String AUTH_TYPE = "eKycAuthType";

	private static final String MISSING_INPUT_PARAMETER = "Missing Input Parameter";

	/** The env. */
	@Autowired
	private Environment env;

	@Override
	public boolean supports(Class<?> clazz) {
		return KycAuthRequestDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);
		KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO) target;
		if (kycAuthRequestDTO != null) {

			if (kycAuthRequestDTO.getAuthRequest() != null) {
				AuthRequestDTO authRequest = kycAuthRequestDTO.getAuthRequest();
				BeanPropertyBindingResult authErrors = new BeanPropertyBindingResult(authRequest, errors.getObjectName());
				authRequestValidator.validate(authRequest, authErrors);
				errors.addAllErrors(authErrors);
			} else {
				mosipLogger.error(SESSION_ID, KYC_REQUEST_VALIDATOR, VALIDATE, INVALID_AUTH_REQUEST + AUTH_REQUEST);
				errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
								AUTH_REQUEST));
			}

			if (!errors.hasErrors()) {
				validateConsentReq(kycAuthRequestDTO, errors);
			}

			if (!errors.hasErrors()) {
				validateAuthType(errors, kycAuthRequestDTO);
			}

			if (!errors.hasErrors()) {
				validateMUAPermission(errors, kycAuthRequestDTO);
			}

		} else {
			mosipLogger.error(SESSION_ID, KYC_REQUEST_VALIDATOR, VALIDATE, INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), AUTH_REQUEST));
		}

	}

	private void validateMUAPermission(Errors errors, KycAuthRequestDTO kycAuthRequestDTO) {
		String key = ACCESS_LEVEL + Optional.ofNullable(kycAuthRequestDTO.getAuthRequest()).map(AuthRequestDTO::getTxnID).orElse("");
		String accesslevel = env.getProperty(key);
		if (accesslevel != null && accesslevel.equals(KycType.NONE.getType())) {
			mosipLogger.error(SESSION_ID, KYC_REQUEST_VALIDATOR, VALIDATE, INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.UNAUTHORISED_KUA.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.UNAUTHORISED_KUA.getErrorMessage(), AUTH_REQUEST));
		}
		//FIXME handle accesslevel being null for the KUA
	}

	private void validateAuthType(Errors errors, KycAuthRequestDTO kycAuthRequestDTO) {
		if (kycAuthRequestDTO.getEKycAuthType() != null) {
			boolean isValidAuthtype = kycAuthRequestDTO.getEKycAuthType().chars().mapToObj(i -> (char) i)
					.map(String::valueOf)
					.allMatch(authTypeStr -> EkycAuthType.getEkycAuthType(authTypeStr).filter(eAuthType -> eAuthType
							.getAuthTypePredicate().test(kycAuthRequestDTO.getAuthRequest().getAuthType()))
							.isPresent());
			if (!isValidAuthtype) {
				mosipLogger.error(SESSION_ID, KYC_REQUEST_VALIDATOR, VALIDATE, INVALID_INPUT_PARAMETER + AUTH_TYPE);
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_EKYC_AUTHTYPE.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_EKYC_AUTHTYPE.getErrorMessage(),
								AUTH_TYPE));
			}
		} else {
			mosipLogger.error(SESSION_ID, KYC_REQUEST_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + AUTH_TYPE);
			errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), AUTH_TYPE));
		}

	}

	private void validateConsentReq(KycAuthRequestDTO kycAuthRequestDTO, Errors errors) {
		if (!kycAuthRequestDTO.isConsentReq()) {
			errors.rejectValue(CONSENT_REQ, IdAuthenticationErrorConstants.INVALID_EKYC_CONCENT.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_EKYC_CONCENT.getErrorMessage(), CONSENT_REQ));
		}
	}

}
