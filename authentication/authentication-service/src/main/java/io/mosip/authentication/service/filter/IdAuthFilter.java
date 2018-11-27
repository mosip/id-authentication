package io.mosip.authentication.service.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class IdAuthFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthFilter extends BaseAuthFilter {

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#setTxnId(java.util.Map, java.util.Map)
	 */
	@Override
	protected Map<String, Object> setTxnId(Map<String, Object> requestBody,
			Map<String, Object> responseBody) {
		responseBody.replace("txnID", requestBody.get("txnID"));
		return responseBody;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java.util.Map)
	 */
	@Override
	protected Map<String, Object> decodedRequest(
			Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		try {
			requestBody.replace(REQUEST,
					decode((String) requestBody.get(REQUEST)));
			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorMessage());
		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#encodedResponse(java.util.Map)
	 */
	@Override
	protected Map<String, Object> encodedResponse(
			Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		try {
			responseBody.replace(REQUEST,
					encode((String) responseBody.get(REQUEST)));
			return responseBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorMessage());
		}
	}

}