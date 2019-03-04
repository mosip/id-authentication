package io.mosip.authentication.service.filter;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

public class DefaultIDAFilter extends BaseIDAFilter{

	@Override
	protected void authenticateRequest(ResettableStreamHttpServletRequest requestWrapper)
			throws IdAuthenticationAppException {
	}

}
