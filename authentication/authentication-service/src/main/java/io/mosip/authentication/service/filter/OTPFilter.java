package io.mosip.authentication.service.filter;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class OTPFilter.
 *
 * @author Manoj SP
 */
@Component
public class OTPFilter extends BaseAuthFilter {

	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

}
