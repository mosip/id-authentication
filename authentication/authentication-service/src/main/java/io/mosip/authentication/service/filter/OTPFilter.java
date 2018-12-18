package io.mosip.authentication.service.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class OTPFilter.
 *
 * @author Manoj SP
 */
@Component
public class OTPFilter extends BaseAuthFilter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#setTxnId(java.util.
	 * Map, java.util.Map)
	 */
	@Override
	protected Map<String, Object> setTxnId(Map<String, Object> requestBody,
			Map<String, Object> responseBody) {
		responseBody.put("txnID", requestBody.get("txnID"));
		return responseBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java
	 * .util.Map)
	 */
	@Override
	protected Map<String, Object> decodedRequest(
			Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		return requestBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#encodedResponse(
	 * java.util.Map)
	 */
	@Override
	protected Map<String, Object> encodedResponse(
			Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		return responseBody;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#validateSignature(java.util.Map, java.lang.String)
	 */
	@Override
	protected boolean validateSignature(Map<String, Object> requestBody, String signature) throws IdAuthenticationAppException {
		//TODO to be included
		return true;
	}

}
