package io.mosip.authentication.common.service.filter;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class DefaultIDAFilter - used to authenticate the request received
 * for the static pin storage and VID
 * generation .
 *
 * @author Sanjay Murali
 */
public class DefaultIDAFilter extends BaseIDAFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseIDAFilter#authenticateRequest(io.
	 * mosip.authentication.service.filter.ResettableStreamHttpServletRequest)
	 */
	@Override
	protected void authenticateRequest(ResettableStreamHttpServletRequest requestWrapper)
			throws IdAuthenticationAppException {
		//default implementation does nothing
	}

	@Override
	protected boolean isSigningRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isSignatureVerificationRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isThumbprintValidationRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isTrustValidationRequired() {
		// TODO Auto-generated method stub
		return false;
	}

}
