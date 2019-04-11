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
public class InternalAuthFilter extends BaseAuthFilter {
	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java.
	 * util.Map)
	 */
	@Override
	protected Map<String, Object> decipherRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		try {
			requestBody.replace(REQUEST, decode((String) requestBody.get(REQUEST)));
			if (Objects.nonNull(requestBody.get(REQUEST))) {
				Map<String, Object> request = keyManager.requestData(requestBody, mapper);
				requestBody.replace(REQUEST, request);
				validateRequestHMAC((String) requestBody.get("requestHMAC"), mapper.writeValueAsString(request));
			}
			return requestBody;
		} catch (ClassCastException | JsonProcessingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#validateSignature(java.lang.String, byte[])
	 */
	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#validateDecipheredRequest(io.mosip.authentication.service.filter.ResettableStreamHttpServletRequest, java.util.Map)
	 */
	@Override
	protected void validateDecipheredRequest(ResettableStreamHttpServletRequest requestWrapper,
			Map<String, Object> decipherRequest) throws IdAuthenticationAppException {
		
	}

}
