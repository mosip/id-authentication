package io.mosip.registration.util.restclient;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;

@Aspect
@Component
public class ResponseSignatureAdvice {

	private static final Logger LOGGER = AppConfig.getLogger(ResponseSignatureAdvice.class);

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	@Autowired
	KeyGenerator keyGenerator;

	@Value("${PUBLIC_KEY}")
	private String publicKey;

	@SuppressWarnings("unchecked")
	@AfterReturning(pointcut = "execution(* io.mosip.registration.util.restclient.RestClientUtil.invoke(..))", returning = "result")
	public Map<String, Object> responseSignature(JoinPoint joinPoint, Object result) throws RegBaseCheckedException {

		LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
				"Entering into response signature method");

		Object[] requestHTTPDTO = joinPoint.getArgs();
		RequestHTTPDTO requestDto = (RequestHTTPDTO) requestHTTPDTO[0];
		LinkedHashMap<String, Object> restClientResponse = null;

		try {

			restClientResponse = (LinkedHashMap<String, Object>) result;

			if (null != requestDto && requestDto.getIsSignRequired()) {

				LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
						requestDto.getUri().getPath().replaceAll("/", "====>"));

				LinkedHashMap<String, Object> responseBodyMap = (LinkedHashMap<String, Object>) restClientResponse
						.get(RegistrationConstants.REST_RESPONSE_BODY);

				byte[] syncDataBytearray = HMACUtils
						.generateHash(new ObjectMapper().writeValueAsString(responseBodyMap).getBytes());

				LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
						"Getting public key");

				PublicKey key = KeyFactory.getInstance("RSA")
						.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKey)));

				Map<String, Object> responseMap = (Map<String, Object>) restClientResponse
						.get(RegistrationConstants.REST_RESPONSE_HEADERS);

				byte[] decodedEncryptedData = CryptoUtil.decodeBase64(responseMap.get("response-signature").toString());
				byte[] hashedEncodedData = decryptor.asymmetricPublicDecrypt(key, decodedEncryptedData);

				if (new String(hashedEncodedData).equals(CryptoUtil.encodeBase64(syncDataBytearray))) {
					LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
							"response singature is valid...");
					return restClientResponse;
				} else {
					LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
							"response singature is Invalid...");
					restClientResponse.put(RegistrationConstants.REST_RESPONSE_BODY, new LinkedHashMap<>());
					restClientResponse.put(RegistrationConstants.REST_RESPONSE_HEADERS, new LinkedHashMap<>());
				}
			}

		} catch (JsonProcessingException | InvalidKeySpecException | NoSuchAlgorithmException regBaseCheckedException) {
			LOGGER.error(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
					ExceptionUtils.getStackTrace(regBaseCheckedException));
			throw new RegBaseCheckedException("Exception in response signature", regBaseCheckedException.getMessage());
		}

		LOGGER.info(LoggerConstants.RESPONSE_SIGNATURE_VALIDATION, APPLICATION_ID, APPLICATION_NAME,
				"succesfully leaving response signature method...");

		return restClientResponse;

	}

}
