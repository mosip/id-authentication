package io.mosip.authentication.common.service.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class InternalEventNotificationFilter.
 * @author Loganathan Sekar
 */
@Component
public class InternalEventNotificationFilter extends DefaultInternalFilter {
	
	@Override
	protected void validateRequest(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		// Skip ID and Version Validation
	}
	
}
