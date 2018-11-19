package io.mosip.authentication.service.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * @author Manoj SP
 *
 */
@Component
public class OTPFilter extends BaseAuthFilter {

	@Override
	protected Map<String, Object> setTxnId(Map<String, Object> requestBody, Map<String, Object> responseBody) {
		responseBody.put("txnID", requestBody.get("txnID"));
		return responseBody;
	}

	@Override
	protected Map<String, Object> decodedRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		return requestBody;
	}

	@Override
	protected Map<String, Object> encodedResponse(Map<String, Object> responseBody) throws IdAuthenticationAppException {
		return responseBody;
	}

}
