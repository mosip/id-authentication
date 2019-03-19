package io.mosip.authentication.service.filter;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class DefaultIDAFilter.
 * 
 * @author Sanjay Murali
 */
public class DefaultIDAFilter extends BaseIDAFilter{

	@Override
	protected void authenticateRequest(ResettableStreamHttpServletRequest requestWrapper)
			throws IdAuthenticationAppException {
	}

}
