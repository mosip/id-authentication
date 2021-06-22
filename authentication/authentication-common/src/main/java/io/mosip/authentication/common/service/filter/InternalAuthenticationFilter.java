package io.mosip.authentication.common.service.filter;

import java.util.Map;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class InternalAuthFilter - used to authenticate the request received for
 * authenticating internal AUTH request {@link InternalAuthController}
 * 
 * @author Sanjay Murali
 * @author Loganathan Sekar
 */
public class InternalAuthenticationFilter extends IdAuthFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#
	 * validateDecipheredRequest(io.mosip.authentication.service.filter.
	 * ResettableStreamHttpServletRequest, java.util.Map)
	 */
	@Override
	protected void validateDecipheredRequest(ResettableStreamHttpServletRequest requestWrapper,
			Map<String, Object> decipherRequest) throws IdAuthenticationAppException {
		// Skipping MISP-Partner authentication/authorizations
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.common.service.filter.IdAuthFilter#fetchReferenceId()
	 */
	@Override
	protected String fetchReferenceId() {
		return env.getProperty(IdAuthConfigKeyConstants.INTERNAL_REFERENCE_ID);
	}

	@Override
	protected String getBioRefId() {
		return env.getProperty(IdAuthConfigKeyConstants.INTERNAL_BIO_REFERENCE_ID);
	}

	@Override
	protected String extractBioData(String dataFieldValue) throws IdAuthenticationAppException {
		// For internal auth the data field is plain encoded payload not a JWS.
		return dataFieldValue;
	}

	@Override
	protected boolean isSigningRequired() {
		return env.getProperty("mosip.ida.internal.signing-required", Boolean.class, true);
	}

	@Override
	protected boolean isSignatureVerificationRequired() {
		return env.getProperty("mosip.ida.internal.signature-verification-required", Boolean.class, true);
	}

	@Override
	protected boolean isTrustValidationRequired() {
		return env.getProperty("mosip.ida.internal.trust-validation-required", Boolean.class, true);
	}
	
	@Override
	protected boolean isBiometricHashValidationDisabled() {
		//Disable biometric hash validation for internal auth
		return true;
	}
	
	@Override
	protected Map<String, Object> decipherRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		//No decryption to be performed
		return requestBody;
	}

}
