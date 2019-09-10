package io.mosip.authentication.common.service.filter;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;

/**
 * The Class BaseAuthFilter - The Base Auth Filter that does all necessary
 * authentication/authorization before allowing the request to the respective
 * controllers.
 * 
 * @author Manoj SP
 * @author Sanjay Murali
 */
@Component
public abstract class BaseAuthFilter extends BaseIDAFilter {

	/** The Constant BASE_AUTH_FILTER. */
	private static final String BASE_AUTH_FILTER = "BaseAuthFilter";

	/** The Constant EVENT_FILTER. */
	private static final String EVENT_FILTER = "Event_filter";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(BaseAuthFilter.class);

	/** The public key. */
	protected PublicKey publicKey;

	/** The encryptor. */
	protected EncryptorImpl encryptor;

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseIDAFilter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		WebApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(filterConfig.getServletContext());
		encryptor = context.getBean(EncryptorImpl.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseIDAFilter#consumeRequest(io.mosip.
	 * authentication.service.filter.ResettableStreamHttpServletRequest)
	 */
	@Override
	protected void consumeRequest(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		super.consumeRequest(requestWrapper, requestBody);
		authenticateRequest(requestWrapper);

		decipherAndValidateRequest(requestWrapper, requestBody);

	}

	/**
	 * Decipher and validate request - Method used to decipher the input stream request
	 * and validate it using {@link validateDecipheredRequest} method.
	 *
	 * @param requestWrapper the request wrapper
	 * @param requestBody the request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void decipherAndValidateRequest(ResettableStreamHttpServletRequest requestWrapper, Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		try {
			requestWrapper.resetInputStream();
			Map<String, Object> decipherRequest = decipherRequest(requestBody);
			validateDecipheredRequest(requestWrapper, decipherRequest);
			String requestAsString = mapper.writeValueAsString(decipherRequest);
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Input Request: \n" + requestAsString);
			requestWrapper.replaceData(requestAsString.getBytes());
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}

	/**
	 * validateDecipheredRequest - Method used to validate the input stream request
	 * by validating the policy, partner and MISP id of the authenticating partner
	 * once the request is decoded and deciphered.
	 *
	 * @param requestWrapper {@link ResettableStreamHttpServletRequest}
	 * @param decipherRequest the request got after decode and decipher the input stream
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected abstract void validateDecipheredRequest(ResettableStreamHttpServletRequest requestWrapper,
			Map<String, Object> decipherRequest) throws IdAuthenticationAppException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseIDAFilter#authenticateRequest(io.
	 * mosip.authentication.service.filter.ResettableStreamHttpServletRequest)
	 */
	@Override
	protected void authenticateRequest(ResettableStreamHttpServletRequest requestWrapper)
			throws IdAuthenticationAppException {
		String signature = requestWrapper.getHeader("Authorization");// FIXME header name
		try {
			requestWrapper.resetInputStream();
			if (!validateSignature(signature, IOUtils.toByteArray(requestWrapper.getInputStream()))) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Invalid Signature");
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DSIGN_FALIED);
			}

		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DSIGN_FALIED, e);
		}
	}

	/**
	 * validateSignature method is used to authenticate the request
	 * received from the authenticating partner from the pay load received
	 * which consists of the JSON signature and certificate .
	 *
	 * @param signature     the JWS serialization received through the request
	 * @param requestAsByte the byte array of the request got after decipher
	 * @return true, if successful once the signature is validated
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		boolean isSigned = false;
		JsonWebSignature jws = new JsonWebSignature();
		try {
			jws.setCompactSerialization(signature);
			List<X509Certificate> certificateChainHeaderValue = jws.getCertificateChainHeaderValue();
			if (certificateChainHeaderValue.size() == NumberUtils.INTEGER_ONE
					&& jws.getAlgorithmHeaderValue().equals(env.getProperty(IdAuthConfigKeyConstants.MOSIP_JWS_CERTIFICATE_ALGM))) {
				X509Certificate certificate = certificateChainHeaderValue.get(0);
				KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				keyStore.load(null);
				keyStore.setCertificateEntry("password", certificate);
				certificate.checkValidity();
				publicKey = certificate.getPublicKey();
				certificate.verify(publicKey);
				jws.setKey(publicKey);
				isSigned = checkValidSign(requestAsByte, isSigned, certificate, jws);
			} else {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "certificate not present");
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_CERTIFICATE);
			}
		} catch (JoseException | InvalidKeyException | CertificateException | NoSuchAlgorithmException
				| NoSuchProviderException | SignatureException | KeyStoreException | IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, "Invalid certificate");
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_CERTIFICATE, e);
		}
		return isSigned;
	}

	/**
	 * checkValidSign method used to check if all the parameters in 
	 * the signature are valid and returns true or false once
	 * validation is performed.
	 *
	 * @param requestAsByte the byte array of the request got after decipher
	 * @param isSigned      the final result which determines is the validation successful or not
	 * @param certificate   the certificate received from the signature pay load
	 * @param jws           the JSON Web Signature received through request
	 * @return true, if successful
	 * @throws JoseException the jose exception
	 */
	private boolean checkValidSign(byte[] requestAsByte, boolean isSigned, X509Certificate certificate,
			JsonWebSignature jws) throws JoseException {
		if (jws.verifySignature() && validateOrg(certificate) && jws.getPayload()
				.equalsIgnoreCase(HMACUtils.digestAsPlainText(HMACUtils.generateHash((requestAsByte))))) {
			isSigned = true;
		}
		return isSigned;
	}

	/**
	 * validateOrg method used to validate the organization
	 * present in the certificate.
	 *
	 * @param certNew the X509 certificate fetched from the signature
	 * @return true, if successful
	 */
	private boolean validateOrg(X509Certificate certNew) {
		String[] subject = certNew.getSubjectDN().getName().split(",");
		return Stream.of(subject).map(s -> s.split("=")).filter(ar -> ar.length == 2)
				.filter(ar -> ar[0].trim().equals("O"))
				.anyMatch(ar -> ar[1].trim().equals(env.getProperty(IdAuthConfigKeyConstants.MOSIP_TSP_ORGANIZATION)));
	}

	/**
	 * Decode method is used to decode the encoded string.
	 *
	 * @param stringToDecode the encoded string
	 * @return the object the decoded string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected static Object decode(String stringToDecode) throws IdAuthenticationAppException {
		try {
			if (Objects.nonNull(stringToDecode)) {
				return CryptoUtil.decodeBase64(stringToDecode);
			} else {
				return stringToDecode;
			}
		} catch (IllegalArgumentException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DSIGN_FALIED, e);
		}
	}

	/**
	 * Encode method is used to encode the string which
	 * is passed through it.
	 *
	 * @param stringToEncode the string to be encoded
	 * @return the string the encoded string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected static String encode(String stringToEncode) throws IdAuthenticationAppException {
		try {
			if (Objects.nonNull(stringToEncode)) {
				return CryptoUtil.encodeBase64String(stringToEncode.getBytes());
			} else {
				return stringToEncode;
			}
		} catch (IllegalArgumentException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, EVENT_FILTER, BASE_AUTH_FILTER, e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DSIGN_FALIED, e);
		}
	}

	/**
	 * decipherRequest method is used to get the deciphered request
	 * from the encoded and enciphered request passed by the 
	 * authenticating partner.
	 *
	 * @param requestBody the encoded and enciphered request body
	 * @return the map the decoded and deciphered request body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected Map<String, Object> decipherRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		return requestBody;
	}

	/**
	 * encipherResponse method is used to encoded and encrypt 
	 * the response received while returning the KYC response.
	 *
	 * @param responseBody the response received after authentication
	 * @return the map the final encoded and enciphered response
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected Map<String, Object> encipherResponse(Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		return responseBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseIDAFilter#transformResponse(java.
	 * util.Map)
	 */
	@Override
	protected Map<String, Object> transformResponse(Map<String, Object> responseMap)
			throws IdAuthenticationAppException {
		return encipherResponse(responseMap);
	}

	/**
	 * validateRequestHMAC method is used to validate the HMAC 
	 * of the request with the deciphered request block and 
	 * requestHMAC received in the request body.
	 *
	 * @param requestHMAC the requestHMAC received in the request body
	 * @param generatedHMAC the generated HMAC computed once the request is decoded and deciphered
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	protected void validateRequestHMAC(String requestHMAC, String generatedHMAC) throws IdAuthenticationAppException {
		if (!requestHMAC.equals(HMACUtils.digestAsPlainText(HMACUtils.generateHash(generatedHMAC.getBytes())))) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.HMAC_VALIDATION_FAILED);
		}
	}
}
