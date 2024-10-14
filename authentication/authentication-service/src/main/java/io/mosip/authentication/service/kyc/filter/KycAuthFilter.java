package io.mosip.authentication.service.kyc.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.filter.IdAuthFilter;
import io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.AuthPolicy;
import io.mosip.authentication.core.partner.dto.MispPolicyDTO;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class KycAuthFilter - used to authenticate the request and returns
 * kyc token and partner specific token as response.
 * 
 * @author Mahammed Taheer
 */
@Component
public class KycAuthFilter extends IdAuthFilter {

	private static Logger mosipLogger = IdaLogger.getLogger(KycAuthFilter.class);

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
		try {
			KycAuthRequestDTO kycAuthRequestDTO = mapper.readValue(mapper.writeValueAsBytes(requestBody),
									KycAuthRequestDTO.class);
			if (AuthTypeUtil.isKeyBindedToken(kycAuthRequestDTO)) {
				super.checkAllowedAuthTypeForKeyBindedToken(requestBody, authPolicies);
			}
			
			super.checkAllowedAuthTypeForPassword(requestBody, authPolicies);
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
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

	@Override
	protected boolean isAMRValidationRequired() {
		return true;
	}

	@Override
	protected void checkMispPolicyAllowed(MispPolicyDTO mispPolicy) throws IdAuthenticationAppException {
		// check whether policy is allowed for kyc auth or not.
		if (!mispPolicy.isAllowKycRequestDelegation()) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "checkMispPolicyAllowed", 
							"MISP Partner not allowed for the Auth Type - kyc-auth.");
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.KYC_AUTH_NOT_ALLOWED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.KYC_AUTH_NOT_ALLOWED.getErrorMessage(), "KYC-AUTH"));
		}
	}

	@Override
	protected void checkAllowedAMRForKBT(Map<String, Object> requestBody, Set<String> allowedAMRs) 
		throws IdAuthenticationAppException {
		try {
			KycAuthRequestDTO kycAuthRequestDTO = mapper.readValue(mapper.writeValueAsBytes(requestBody),
										KycAuthRequestDTO.class);

			if (AuthTypeUtil.isKeyBindedToken(kycAuthRequestDTO)) {
				super.checkAllowedAMRForKeyBindedToken(requestBody, allowedAMRs);
			}
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
}
