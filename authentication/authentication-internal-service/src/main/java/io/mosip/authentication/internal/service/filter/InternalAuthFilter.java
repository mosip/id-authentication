package io.mosip.authentication.internal.service.filter;

import java.util.Map;

import io.mosip.authentication.common.service.filter.IdAuthFilter;
import io.mosip.authentication.common.service.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.internal.service.controller.InternalAuthController;

/**
 * The Class InternalAuthFilter - used to authenticate the
 * request received for authenticating internal AUTH request
 * {@link InternalAuthController}
 * 
 * @author Sanjay Murali
 */
public class InternalAuthFilter extends IdAuthFilter {

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#validateDecipheredRequest(io.mosip.authentication.service.filter.ResettableStreamHttpServletRequest, java.util.Map)
	 */
	@Override
	protected void validateDecipheredRequest(ResettableStreamHttpServletRequest requestWrapper,
			Map<String, Object> decipherRequest) throws IdAuthenticationAppException {
		//Skipping MISP-Partner authentication/authorizations
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.filter.IdAuthFilter#fetchReferenceId()
	 */
	@Override
	protected String fetchReferenceId() {
		return env.getProperty(IdAuthConfigKeyConstants.INTERNAL_REFERENCE_ID);
	}

}
