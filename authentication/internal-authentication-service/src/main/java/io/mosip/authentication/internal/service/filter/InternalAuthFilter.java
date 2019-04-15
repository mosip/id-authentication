package io.mosip.authentication.internal.service.filter;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.mosip.authentication.common.authentication.filter.BaseAuthFilter;
import io.mosip.authentication.common.authentication.filter.ResettableStreamHttpServletRequest;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.internal.service.impl.indauth.controller.InternalAuthController;

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
		
	}

}
