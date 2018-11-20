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
			Map<String, Object> authRequest = (Map<String, Object>) decode((String) requestBody.get(AUTH_REQUEST));
			authRequest.replace(REQUEST, decode((String) authRequest.get(REQUEST)));
			requestBody.replace(AUTH_REQUEST, authRequest);
			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	@Override
	protected Map<String, Object> encodedResponse(Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		try {
			Map<String, Object> response = (Map<String, Object>) responseBody.get(RESPONSE);
			if (response != null) {
				Object kyc = response.get(KYC);
				
				if (kyc != null) {
					response.replace(KYC, encode(kyc.toString()));
				}
				
				Object auth = response.get(AUTH);
				if (auth != null) {
					response.replace(AUTH, encode(auth.toString()));
				}
				
				responseBody.replace(RESPONSE, encode(responseBody.get(RESPONSE).toString()));
			}
			return responseBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	@Override
	protected Map<String, Object> setTxnId(Map<String, Object> requestBody, Map<String, Object> responseBody) {
		Map<String, Object> authReq = (Map<String, Object>) requestBody.get("authRequest");
		responseBody.replace("txnID", authReq.get("txnID"));
		return responseBody;
	}

}
