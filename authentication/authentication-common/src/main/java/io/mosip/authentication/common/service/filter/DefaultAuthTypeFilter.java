package io.mosip.authentication.common.service.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class DefaultAuthTypeFilter.
 *
 * @author Dinesh Karuppiah.T
 */
@Component
public class DefaultAuthTypeFilter extends DefaultInternalFilter {

	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.filter.BaseAuthFilter#decipherAndValidateRequest(io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest, java.util.Map)
	 */
	protected void decipherAndValidateRequest(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		//Nothing to do
	}
}
