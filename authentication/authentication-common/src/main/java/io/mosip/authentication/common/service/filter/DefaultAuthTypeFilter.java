package io.mosip.authentication.common.service.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Component
public class DefaultAuthTypeFilter extends DefaultInternalFilter {

	protected void decipherAndValidateRequest(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		//Nothing to do
	}
}
