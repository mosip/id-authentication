package io.mosip.authentication.service.filter;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class OTPFilter.
 *
 * @author Manoj SP
 */
@Component
public class OTPFilter extends BaseAuthFilter {

	/** The Constant X_509. */
	private static final String X_509 = "X.509";
	
	/** The Constant PUBLIC_KEY_CERT. */
	private static final String PUBLIC_KEY_CERT = "publicKeyCert";
	
	/** The Constant KEY. */
	private static final String KEY = "key";

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
