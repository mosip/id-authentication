package io.mosip.authentication.service.kyc.filter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.filter.IdAuthFilter;
import io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.authentication.core.partner.dto.MispPolicyDTO;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class KycExchangeFilter - used to validate the request and returns
 * encrypted kyc data in JWE format as response.
 * 
 * @author Mahammed Taheer
 */
@Component
public class KycExchangeFilter extends IdAuthFilter {

	private static Logger mosipLogger = IdaLogger.getLogger(KycAuthFilter.class);

	/** The Constant KYC. */
	private static final String KYC_EXCHANGE = "kycexchange";
	
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
		if (!isAllowedAuthType(KYC_EXCHANGE, authPolicies)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNAUTHORISED_KYC_EXCHANGE_PARTNER.getErrorCode(),
					IdAuthenticationErrorConstants.UNAUTHORISED_KYC_EXCHANGE_PARTNER.getErrorMessage());

		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.filter.IdAuthFilter#checkMandatoryAuthTypeBasedOnPolicy(java.util.Map, java.util.List)
	 */
	@Override
	protected void checkMandatoryAuthTypeBasedOnPolicy(Map<String, Object> requestBody,
			List<AuthPolicy> mandatoryAuthPolicies) throws IdAuthenticationAppException {
		// Nothing to do
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
		return attribute + KYC_EXCHANGE;
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

	@Override
	protected boolean isAMRValidationRequired() {
		return false;
	}

	@Override
	protected void checkMispPolicyAllowed(MispPolicyDTO mispPolicy) throws IdAuthenticationAppException {
		// check whether policy is allowed for kyc exchange or not.
		if (!mispPolicy.isAllowKycRequestDelegation()) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "checkMispPolicyAllowed", 
							"MISP Partner not allowed for the Auth Type - kyc-exchange.");
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.KYC_EXCHANGE_NOT_ALLOWED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.KYC_EXCHANGE_NOT_ALLOWED.getErrorMessage(), "KYC-EXCHANGE"));
		}
	}
}
