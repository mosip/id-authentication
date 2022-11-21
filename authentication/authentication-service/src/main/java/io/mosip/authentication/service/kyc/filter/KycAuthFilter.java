package io.mosip.authentication.service.kyc.filter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.filter.IdAuthFilter;
import io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.partner.dto.AuthPolicy;

/**
 * The Class KycAuthFilter - used to authenticate the request and returns
 * kyc token and partner specific token as response.
 * 
 * @author Mahammed Taheer
 */
@Component
public class KycAuthFilter extends IdAuthFilter {

	/** The Constant KYC. */
	private static final String KYC_AUTH = "kycauth";
	
	@Override
	protected boolean isPartnerCertificateNeeded() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.filter.IdAuthFilter#
	 * checkAllowedAuthTypeBasedOnPolicy(java.util.Map, java.util.List)
	 */
	@Override
	protected void checkAllowedAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException {
		if (!isAllowedAuthType(KYC_AUTH, authPolicies)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNAUTHORISED_KYC_AUTH_PARTNER.getErrorCode(),
					IdAuthenticationErrorConstants.UNAUTHORISED_KYC_AUTH_PARTNER.getErrorMessage());

		}
		super.checkAllowedAuthTypeBasedOnPolicy(requestBody, authPolicies);
	}
	
	@Override
	protected boolean isSigningRequired() {
		return true;
	}

	@Override
	protected boolean isSignatureVerificationRequired() {
		return true;
	}

	@Override
	protected boolean isTrustValidationRequired() {
		return true;
	}
	
	@Override
	protected String fetchId(ResettableStreamHttpServletRequest requestWrapper, String attribute) {
		return attribute + KYC_AUTH;
	}
	
	protected boolean needStoreAuthTransaction() {
		return true;
	}
	
	protected boolean needStoreAnonymousProfile() {
		return true;
	}

	@Override
	protected boolean isMispPolicyValidationRequired() {
		return true;
	}

	@Override
	protected boolean isCertificateValidationRequired() {
		return true;
	}
}
