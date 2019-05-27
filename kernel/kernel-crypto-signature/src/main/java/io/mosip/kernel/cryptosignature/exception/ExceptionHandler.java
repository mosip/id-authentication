package io.mosip.kernel.cryptosignature.exception;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilClientException;
import io.mosip.kernel.cryptosignature.constant.SigningDataErrorCode;


/** Crypto Signature Utils
 * 
 * @author Urvil Joshi
 *
 */
public class ExceptionHandler {

	
	private ExceptionHandler() {
	}
	
	public static  void authExceptionHandler(HttpStatusCodeException ex, List<ServiceError> validationErrorsList, String source) {
		if (ex.getRawStatusCode() == 401) {
			if (!validationErrorsList.isEmpty()) {
				throw new AuthNException(validationErrorsList);
			} else {
				throw new BadCredentialsException("Authentication failed for "+source);
			}
		}
		if (ex.getRawStatusCode() == 403) {
			if (!validationErrorsList.isEmpty()) {
				throw new AuthZException(validationErrorsList);
			} else {
				throw new AccessDeniedException("Access denied for "+source);
			}
		}
	}
	
	public static void throwExceptionIfExist(ResponseEntity<String> response) {
		if(response == null) {
			throw new ParseResponseException(SigningDataErrorCode.REST_CRYPTO_CLIENT_EXCEPTION.getErrorCode(),
					SigningDataErrorCode.REST_CRYPTO_CLIENT_EXCEPTION.getErrorMessage() );
		}
		String responseBody = response.getBody();
		List<ServiceError> validationErrorList = ExceptionUtils.getServiceErrorList(responseBody);
		if (!validationErrorList.isEmpty()) {
			throw new SignatureUtilClientException(validationErrorList);
		}
	}
	
	public static <S> S getResponse(ObjectMapper objectMapper,ResponseEntity<String> response, Class<S> clazz) {
		try {
			JsonNode res =objectMapper.readTree(response.getBody());
			return objectMapper.readValue(res.get("response").toString(), clazz);
		} catch (IOException|NullPointerException exception) {
			throw new ParseResponseException(SigningDataErrorCode.RESPONSE_PARSE_EXCEPTION.getErrorCode(),
					SigningDataErrorCode.RESPONSE_PARSE_EXCEPTION.getErrorMessage() + exception.getMessage(), exception);
		}
	}
	
}
