package io.mosip.authentication.common.service.filter;

import java.util.Map;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class InternalAuthFilter - used to authenticate the request received for
 * authenticating internal AUTH request {@link InternalAuthController}
 * 
 * @author Sanjay Murali
 */
public class InternalAuthFilter extends IdAuthFilter {
	
	/** The Constant AUTH. */
	private static final String INTERNAL = "internal";

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
		return EnvUtil.getInternalAuthInternalRefId();
	}

	@Override
	protected String getBioRefId() {
		return EnvUtil.getInternalAuthInternalBioRefId();
	}

	@Override
	protected String extractBioData(String dataFieldValue) throws IdAuthenticationAppException {
		// For internal auth the data field is plain encoded payload not a JWS.
		return dataFieldValue;
	}

	@Override
	protected boolean isSigningRequired() {
		return EnvUtil.getInternalAuthSigningRequired();
	}

	@Override
	protected boolean isSignatureVerificationRequired() {
		return EnvUtil.getInternalAuthSignatureVerificationRequired();
	}

	//After integration with 1.1.5.1 version of keymanager, thumbprint is always mandated for decryption.
//	@Override
//	protected boolean isThumbprintValidationRequired() {
//		return env.getProperty("mosip.ida.internal.thumbprint-validation-required", Boolean.class, false);
//	}

	@Override
	protected boolean isTrustValidationRequired() {
		return EnvUtil.getInternalAuthTrustValidationRequired();
	}
	
	@Override
	protected boolean isBiometricHashValidationDisabled() {
		//Disable biometric hash validation for internal auth
		return EnvUtil.getInternalAuthBioHashValidationDisabled();
	}
	
	/**
	 * Fetch id.
	 *
	 * @param requestWrapper the request wrapper
	 * @param attribute the attribute
	 * @return the string
	 */
	@Override
	protected String fetchId(ResettableStreamHttpServletRequest requestWrapper, String attribute) {
		return attribute + INTERNAL;
	}
	
	protected boolean needStoreAuthTransaction() {
		return true;
	}
	
	protected boolean needStoreAnonymousProfile() {
		return false;
	}

	@Override
	protected boolean isMispPolicyValidationRequired() {
		return false;
	}

	@Override
	protected boolean isCertificateValidationRequired() {
		return false;
	}

	@Override
	protected boolean isAMRValidationRequired() {
		return false;
	}
}
