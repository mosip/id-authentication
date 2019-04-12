package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

/**
 * The Class KeyManager is used to decipher the request
 * and returning the decipher request to the filter
 * to do further authentication.
 * 
 * @author Sanjay Murali
 */
@Component
public class KeyManager {

	/** The Constant ERROR_CODE. */
	private static final String ERROR_CODE = "errorCode";

	/** The Constant SECRET_KEY. */
	private static final String SECRET_KEY = "secretKey";

	/** The Constant AESPADDING. */
	private static final String AESPADDING = "AES/CBC/PKCS5Padding";

	/** The Constant SYMMETRIC_ALGORITHM_NAME. */
	private static final String SYMMETRIC_ALGORITHM_NAME = "AES";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant SESSION_KEY. */
	private static final String SESSION_KEY = "requestSessionKey";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";
	
	/** The secure random. */
	private static  SecureRandom secureRandom;

	/** KeySplitter. */
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;

	/** The app id. */
	@Value("${application.id}")
	private String appId;

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The rest request factory. */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(KeyManager.class);

	/**
	 * requestData method used to decipher the request block {@link RequestDTO}
	 * present in AuthRequestDTO {@link AuthRequestDTO}
	 *
	 * @param requestBody the request body
	 * @param mapper      the mapper
	 * @return the map
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationBusinessException
	 */
	public Map<String, Object> requestData(Map<String, Object> requestBody, ObjectMapper mapper)
			throws IdAuthenticationAppException {
		Map<String, Object> request = null;
		try {
			byte[] encryptedRequest = (byte[]) requestBody.get(REQUEST);
			Optional<String> encryptedSessionKey = Optional.ofNullable(requestBody.get(SESSION_KEY))
					.map(String::valueOf);
			if (encryptedSessionKey.isPresent()) {
				request = decipherData(mapper, encryptedRequest, encryptedSessionKey.get());
			}
		} catch (IOException e) {
			logger.error(SESSION_ID, this.getClass().getSimpleName(), "requestData", e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
		return request;
	}

	/**
	 * decipherData method used to derypt data if
	 * session key is present
	 *
	 * @param mapper the mapper
	 * @param encryptedRequest the encrypted request
	 * @param encryptedSessionKey the encrypted session key
	 * @return the map
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> decipherData(ObjectMapper mapper, byte[] encryptedRequest, String encryptedSessionKey)
			throws IdAuthenticationAppException, IOException {
		SecretKey secretKey=null;
		byte[] decryptedData=null;
		Map<String, Object> request;
		RestRequestDTO restRequestDTO = null;
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto();
		Map<String,Object> symmetricKeyResponseDto = null;
		byte[] decryptedSymmetricKey = null;
		try {
			symmetricKeyRequestDto.setApplicationId(appId);
			symmetricKeyRequestDto.setReferenceId(environment.getProperty("mosip.ida.publickey"));
			symmetricKeyRequestDto.setTimeStamp(
					DateUtils.getUTCCurrentDateTime());
			symmetricKeyRequestDto.setEncryptedSymmetricKey(encryptedSessionKey);
			restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.DECRYPTION_SERVICE,
					RestRequestFactory.createRequest(symmetricKeyRequestDto), Map.class);
			symmetricKeyResponseDto = restHelper.requestSync(restRequestDTO);
			Object symmetricKeyValue = ((Map<String,Object>) symmetricKeyResponseDto.get("response")).get("symmetricKey");
			decryptedSymmetricKey = Base64.decodeBase64(symmetricKeyValue instanceof String ? (String) symmetricKeyValue : "");
			secretKey=new SecretKeySpec(decryptedSymmetricKey, 0, decryptedSymmetricKey.length, SYMMETRIC_ALGORITHM_NAME);
				decryptedData=symmetricDecrypt(secretKey, encryptedRequest);
		}
		catch (RestServiceException e) {
			logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				handleRestError(responseBody.get());
			}

			logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.SERVER_ERROR);
		} catch (IDDataValidationException e) {
			logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}

		request = mapper.readValue(decryptedData,Map.class);
		request.put(SECRET_KEY, secretKey);
		return request;
	}

	/**
	 * handleRestError method used to handle the rest exception
	 * Occurring while decryption of data
	 *
	 * @param errorBody the error body
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	private void handleRestError(Object errorBody) throws IdAuthenticationAppException {
		Map<String, Object> responseMap = errorBody instanceof Map ? (Map<String, Object>) errorBody : Collections.emptyMap();
		if (responseMap.containsKey("errors")) {
			List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) responseMap.get("errors");
			String keyExpErrorCode = "KER-KMS-003"; // TODO FIXME integrate with kernel error constant
			if (!idRepoerrorList.isEmpty()
					&& idRepoerrorList.stream().anyMatch(map -> map.containsKey(ERROR_CODE)
							&& ((String) map.get(ERROR_CODE)).equalsIgnoreCase(keyExpErrorCode))) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED);
			} if (!idRepoerrorList.isEmpty()
					&& idRepoerrorList.stream().anyMatch(map -> map.containsKey(ERROR_CODE)
							&& ((String) map.get(ERROR_CODE)).equalsIgnoreCase("KER-FSE-003"))) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.INVALID_ENCRYPTION);
			} else {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
		}
	}
	
	/**
	 * symmetricDecrypt method used to decrypt the session key present.
	 *
	 * @param secretKey the secret key
	 * @param encryptedDataByteArr the encrypted data byte arr
	 * @return the byte[]
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public byte[] symmetricDecrypt(SecretKey secretKey, byte[] encryptedDataByteArr) throws IdAuthenticationAppException  {
		  Cipher cipher=null;
		try {
			cipher = Cipher.getInstance(AESPADDING);
			 cipher.init(Cipher.DECRYPT_MODE, secretKey,
						new IvParameterSpec(Arrays.copyOfRange(encryptedDataByteArr, encryptedDataByteArr.length - cipher.getBlockSize(), encryptedDataByteArr.length)),secureRandom);
			 return cipher.doFinal(Arrays.copyOf(encryptedDataByteArr, encryptedDataByteArr.length - cipher.getBlockSize()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_ENCRYPTION,e);
		}
		 
		}

	/**
	 * getSymmetricKey method used to generate a 
	 * symmetric key
	 *
	 * @return the symmetric key
	 */
	public SecretKey getSymmetricKey() {
		return keyGenerator.getSymmetricKey();
	}

}
