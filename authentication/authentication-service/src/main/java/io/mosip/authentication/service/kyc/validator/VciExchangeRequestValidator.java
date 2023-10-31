package io.mosip.authentication.service.kyc.validator;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PUBLIC_KEY_EXPONENT_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PUBLIC_KEY_MODULUS_KEY;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.COLON;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.common.service.validator.BaseAuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.VCFormats;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import net.minidev.json.JSONObject;

/**
 * The Class For VciExchangeRequestValidator extending the
 * BaseAuthRequestValidator{@link BaseAuthRequestValidator}}
 *
 * @author Mahammed Taheer
 * 
 * 
 */

@Component
public class VciExchangeRequestValidator extends AuthRequestValidator {	

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(VciExchangeRequestValidator.class);

	private static final ObjectMapper OBJECT_MAPPER;

	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.registerModule(new AfterburnerModule());
	}

	@Value("#{'${mosip.ida.vci.supported.cred.types:}'.split(',')}")
	private List<String> supportedCredTypes;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return VciExchangeRequestDTO.class.equals(clazz);
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
		VciExchangeRequestDTO vciExchangeRequestDTO = (VciExchangeRequestDTO) target;
		if (vciExchangeRequestDTO != null) {
			if (!errors.hasErrors()) {
				validateReqTime(vciExchangeRequestDTO.getRequestTime(), errors, IdAuthCommonConstants.REQ_TIME);
			}

			if (!errors.hasErrors()) {
				validateTxnId(vciExchangeRequestDTO.getTransactionID(), errors, IdAuthCommonConstants.TRANSACTION_ID);
			}

			if (!errors.hasErrors()) {
				validateAuthToken(vciExchangeRequestDTO.getVcAuthToken(), errors, IdAuthCommonConstants.VC_AUTH_TOKEN);
			}

			if (!errors.hasErrors()) {
				validateCredSubjectId(vciExchangeRequestDTO.getCredSubjectId(), errors, IdAuthCommonConstants.CREDENTIAL_SUBJECT_ID);
			}

			if (!errors.hasErrors()) {
				validateCredSubjectIdDIDFormat(vciExchangeRequestDTO.getCredSubjectId(), errors, IdAuthCommonConstants.CREDENTIAL_SUBJECT_ID);
			}

			if (!errors.hasErrors()) {
				validateVCFormat(vciExchangeRequestDTO.getVcFormat(), errors, IdAuthCommonConstants.VC_FORMAT);
			} 

			if (!errors.hasErrors()) {
				validateAllowedVCFormats(vciExchangeRequestDTO.getVcFormat(), errors, IdAuthCommonConstants.VC_FORMAT);
			}

			if (!errors.hasErrors()) {
				validateCredentialType(vciExchangeRequestDTO.getCredentialsDefinition().getType(), errors, IdAuthCommonConstants.VC_CREDENTIAL_DEF);
			}

		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + IdAuthCommonConstants.REQUEST);
			errors.rejectValue(IdAuthCommonConstants.REQUEST, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), IdAuthCommonConstants.REQUEST));
		}

	}

	private void validateAuthToken(String kycToken, Errors errors, String paramName) {

		if (kycToken == null || StringUtils.isEmpty(kycToken.trim())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + paramName);
			errors.rejectValue(paramName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} 
	}

	private void validateCredSubjectId(String credSubjectId, Errors errors, String paramName) {
		if (credSubjectId == null || StringUtils.isEmpty(credSubjectId.trim())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + paramName);
			errors.rejectValue(paramName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	private void validateVCFormat(String vcFormat, Errors errors, String paramName) {
		if (vcFormat == null || StringUtils.isEmpty(vcFormat.trim())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + paramName);
			errors.rejectValue(paramName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	private void validateCredentialType(List<String> credentialType, Errors errors, String paramName) {
		if (credentialType == null || credentialType.isEmpty()) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + paramName + "/type" );
			errors.rejectValue(paramName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName + "/type" },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else {
			if(!supportedCredTypes.containsAll(credentialType)) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + paramName + "/type" );
				errors.rejectValue(paramName, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { paramName + "/type" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}
	}

	private void validateCredSubjectIdDIDFormat(String credSubjectId, Errors errors, String paramName) {
		String[] didArray = StringUtils.split(credSubjectId, COLON);
		if (didArray.length != 3) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					"Invalid DID Format input for credential subject ID: " + credSubjectId);
			errors.rejectValue(paramName, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			String identityJwk = new String(CryptoUtil.decodeBase64(didArray[2]));
			try {
				JSONObject jsonObject = OBJECT_MAPPER.readValue(identityJwk, JSONObject.class);
				validatePublicKeyAttributes(jsonObject, errors, PUBLIC_KEY_MODULUS_KEY, paramName);
				validatePublicKeyAttributes(jsonObject, errors, PUBLIC_KEY_EXPONENT_KEY, paramName);
			} catch (IOException ioe) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					"Error formating Identity JWK", ioe);
				errors.rejectValue(paramName, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());	
			}
		}
	}

	private void validatePublicKeyAttributes(JSONObject jsonObject, Errors errors, String publicKeyAttribute, String paramName) {
		String value = jsonObject.getAsString(publicKeyAttribute);
		if (value == null || StringUtils.isEmpty(value.trim())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, MISSING_INPUT_PARAMETER + publicKeyAttribute);
			errors.rejectValue(paramName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { paramName }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	private void validateAllowedVCFormats(String vcFormat, Errors errors, String paramName) {
		boolean allowed = Stream.of(VCFormats.values()).filter(t -> t.getFormat().equalsIgnoreCase(vcFormat)).findAny().isPresent();
		if (!allowed) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					"Not Supported VC Format: " + vcFormat);
			errors.rejectValue(paramName, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());	
		}
	}
}
