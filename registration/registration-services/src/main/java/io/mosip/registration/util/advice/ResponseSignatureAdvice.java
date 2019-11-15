package io.mosip.registration.util.advice;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.RequestHTTPDTO;

/**
 * All the responses of the rest call services which are invoking from the
 * reg-client will get signed from this class.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Aspect
@Component
public class ResponseSignatureAdvice {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = AppConfig.getLogger(ResponseSignatureAdvice.class);

	/** The decryptor. */
	@Autowired       
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

	/** The key generator. */
	@Autowired
	KeyGenerator keyGenerator;

	/** The SignatureUtil. */
	@Autowired
	SignatureUtil signatureUtil;

	/** The policy sync DAO. */
	@Autowired
	private PolicySyncDAO policySyncDAO;

	/**
	 * <p>
	 * It is an after returning method in which for each and everytime after
	 * successfully invoking the
	 * "io.mosip.registration.util.restclient.RestClientUtil.invoke()" method, this
	 * method will be called.
	 * </p>
	 * 
	 * Here we are passing three arguments as parameters
	 * <ol>
	 * <li>SignIn Key - Public Key from Kernel</li>
	 * <li>Response - Signature from response header</li>
	 * <li>Response Body - Getting from the Service response</li>
	 * </ol>
	 * 
	 * The above three values are passed to the {@link SignatureUtil} where the
	 * validation will happen for the response that we send
	 * 
	 * @param joinPoint - the JointPoint
	 * @param result - the object result
	 * @return the rest client response as {@link Map}
	 * @throws RegBaseCheckedException - the exception class that handles all the checked exceptions
	 */
	@SuppressWarnings("unchecked")
	@AfterReturning(pointcut = "execution(* io.mosip.registration.util.restclient.RestClientUtil.invoke(..))", returning = "result")
	public synchronized Map<String, Object> responseSignatureValidation(JoinPoint joinPoint, Object result)
			throws RegBaseCheckedException {

		LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
				"Entering into response signature method");

		HttpHeaders responseHeader = null;
		Object[] requestHTTPDTO = joinPoint.getArgs();
		RequestHTTPDTO requestDto = (RequestHTTPDTO) requestHTTPDTO[0];
		LinkedHashMap<String, Object> restClientResponse = null;
		String publicKey = RegistrationConstants.EMPTY;

		try {

			restClientResponse = (LinkedHashMap<String, Object>) result;
			
			LinkedHashMap<String, Object> keyResponse = (LinkedHashMap<String, Object>) restClientResponse
					.get(RegistrationConstants.REST_RESPONSE_BODY);

			if (null != requestDto 
					&& requestDto.getIsSignRequired() 
					&& null != keyResponse 
					&& keyResponse.size() > 0 
					&& null != keyResponse.get(RegistrationConstants.RESPONSE)) {

				KeyStore keyStore = policySyncDAO.getPublicKey(RegistrationConstants.KER);

				if (null != keyStore && null != keyStore.getPublicKey()) {
					publicKey = new String(keyStore.getPublicKey());
				} else {
					if (keyResponse.size() > 0
							&& null != keyResponse.get(RegistrationConstants.RESPONSE)) {
						LinkedHashMap<String, Object> resp = (LinkedHashMap<String, Object>) keyResponse
								.get(RegistrationConstants.RESPONSE);
						publicKey = (String) resp.get(RegistrationConstants.PUBLIC_KEY);
					}
				}

				LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
						requestDto.getUri().getPath().replaceAll("/", "====>"));

				LinkedHashMap<String, Object> responseBodyMap = (LinkedHashMap<String, Object>) restClientResponse
						.get(RegistrationConstants.REST_RESPONSE_BODY);

				LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
						"Getting public key");

				responseHeader = (HttpHeaders) restClientResponse.get(RegistrationConstants.REST_RESPONSE_HEADERS);

				if (signatureUtil.validateWithPublicKey(
						responseHeader.get(RegistrationConstants.RESPONSE_SIGNATURE).get(0),
						new ObjectMapper().writeValueAsString(responseBodyMap), publicKey)) {
					LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
							"response signature is valid...");
					return restClientResponse;
				} else {
					LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
							"response signature is Invalid...");
					restClientResponse.put(RegistrationConstants.REST_RESPONSE_BODY, new LinkedHashMap<>());
					restClientResponse.put(RegistrationConstants.REST_RESPONSE_HEADERS, new LinkedHashMap<>());
				}
			}

		} catch (InvalidKeySpecException | NoSuchAlgorithmException | JsonProcessingException
				| RuntimeException regBaseCheckedException) {
			LOGGER.error(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
			throw new RegBaseCheckedException("Exception in response signature", regBaseCheckedException.getMessage());
		}

		LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
				"successfully leaving response signature method...");

		return restClientResponse;

	}

}
