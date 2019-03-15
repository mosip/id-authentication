package io.mosip.authentication.service.filter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.service.policy.AuthPolicy;

/**
 * The Class OTPFilter.
 *
 * @author Manoj SP
 */
@Component
public class OTPFilter extends IdAuthFilter {

	private static final String OTP_REQUEST = "otp-request";

	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}
	
	@Override
	protected void checkAllowedAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException {
		if(!isAllowedAuthType(OTP_REQUEST, authPolicies)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHTYPE_NOT_ALLOWED);
		}
	}

}
