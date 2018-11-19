package io.mosip.authentication.service.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

@Component
public class KycAuthFilter extends BaseAuthFilter {
	
	private static final String AUTH_REQUEST = "authRequest";
	
	private static final String REQUEST = "request";
	
	private static final String RESPONSE = "response";
	
	private static final String AUTH = "auth";
	
	private static final String KYC = "kyc";

	@Override
	protected Map<String, Object> decodedRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		try {
			requestBody.replace(AUTH_REQUEST, decode((String) requestBody.get(AUTH_REQUEST)));
			requestBody.replace(REQUEST, decode((String) requestBody.get(REQUEST)));
			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	@Override
	protected Map<String, Object> encodedResponse(Map<String, Object> responseBody)	throws IdAuthenticationAppException {
		try {
			responseBody.replace(KYC, encode(((Map<String, Object>) responseBody.get(RESPONSE)).get(KYC).toString()));
			responseBody.replace(AUTH, encode(((Map<String, Object>) responseBody.get(RESPONSE)).get(AUTH).toString()));
			responseBody.replace(RESPONSE, encode(responseBody.get(RESPONSE).toString()));
			return responseBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	@Override
	protected Map<String, Object> setTxnId(Map<String, Object> requestBody, Map<String, Object> responseBody) {
		responseBody.replace("txnID", requestBody.get("txnID"));
		return responseBody;
	}

}
