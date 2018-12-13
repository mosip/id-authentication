package io.mosip.authentication.service.filter;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
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

	/** The Constant X_509. */
	private static final String X_509 = "X.509";
	
	/** The Constant PUBLIC_KEY_CERT. */
	private static final String PUBLIC_KEY_CERT = "publicKeyCert";
	
	/** The Constant KEY. */
	private static final String KEY = "key";
	
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
	
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		
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
			if(null != (String) requestBody.get(REQUEST)) {
				Map<String, Object> request = keyManager.requestData(requestBody, env, decryptor, mapper);
				requestBody.replace(REQUEST, request);				
			}
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

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#validateSignature(java.util.Map, java.lang.String)
	 */
	@Override
	protected boolean validateSignature(Map<String, Object> requestBody, String signature) throws IdAuthenticationAppException {
		/*boolean isSigned = false;
		Optional<String> map = Optional.ofNullable(requestBody.get(KEY))
				.filter(obj -> obj instanceof Map)
				.map(obj -> String.valueOf(((Map<String, Object>)obj).get(PUBLIC_KEY_CERT)));
		if(map.isPresent()) {
			byte[] cert = Base64.getDecoder().decode(map.get());
			try {
				X509Certificate certNew = (X509Certificate) CertificateFactory.getInstance(X_509)
						.generateCertificate(new ByteArrayInputStream(cert));
				JsonWebSignature jws = new JsonWebSignature();
				jws.setCompactSerialization(signature);
				jws.setKey(certNew.getPublicKey());
				isSigned = jws.verifySignature();
			} catch (CertificateException | JoseException e) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
						.getErrorCode(),
				IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
						.getErrorMessage());
			}
		}
		return isSigned;*/
		return true;
	}

}