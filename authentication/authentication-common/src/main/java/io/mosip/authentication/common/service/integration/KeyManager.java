package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.dto.EncryptDataRequestDto;
import io.mosip.authentication.common.service.integration.dto.SymmetricKeyRequestDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
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


	/** The Constant SESSION_KEY. */
	private static final String SESSION_KEY = "requestSessionKey";

	
	/** The secure random. */
	private static  SecureRandom secureRandom;



	/** The app id. */
	@Value("${" +IdAuthConfigKeyConstants.APPLICATION_ID+ "}")
	private String appId;
	
	/** The partner id. */
	@Value("${" +IdAuthConfigKeyConstants.CRYPTO_PARTNER_ID+ "}")
	private String partnerId;

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The rest request factory. */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

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
			byte[] encryptedRequest = (byte[]) requestBody.get(IdAuthCommonConstants.REQUEST);
			Optional<String> encryptedSessionKey = Optional.ofNullable(requestBody.get(SESSION_KEY))
					.map(String::valueOf);
			if (encryptedSessionKey.isPresent()) {
				request = decipherData(mapper, encryptedRequest, encryptedSessionKey.get());
			}
		} catch (IOException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "requestData", e.getMessage());
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
			symmetricKeyRequestDto.setReferenceId(partnerId);
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
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
			Optional<Object> responseBody = e.getResponseBody();
			if (responseBody.isPresent()) {
				handleRestError(responseBody.get());
			}

			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.SERVER_ERROR);
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
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
			String keyExpErrorCode = IdAuthCommonConstants.KER_PUBLIC_KEY_EXPIRED; // TODO FIXME integrate with kernel error constant
			if (!idRepoerrorList.isEmpty()
					&& idRepoerrorList.stream().anyMatch(map -> map.containsKey(ERROR_CODE)
							&& ((String) map.get(ERROR_CODE)).equalsIgnoreCase(keyExpErrorCode))) {
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED);
			} if (!idRepoerrorList.isEmpty()
					&& idRepoerrorList.stream().anyMatch(map -> map.containsKey(ERROR_CODE)
							&& ((String) map.get(ERROR_CODE)).equalsIgnoreCase(IdAuthCommonConstants.KER_DECRYPTION_FAILURE))) {
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
	
	@SuppressWarnings("unchecked")
	public String encryptData(Map<String, Object> responseBody) throws IdAuthenticationAppException {
		Optional<String> identity = Optional.ofNullable(responseBody.get("identity"))
				.map(String::valueOf);
		Map<String, Object> response;
		RestRequestDTO restRequestDTO = null;
		if (identity.isPresent()) {
			EncryptDataRequestDto encryptDataRequestDto = new EncryptDataRequestDto();
			encryptDataRequestDto.setApplicationId(appId);
			encryptDataRequestDto.setReferenceId(partnerId);
			encryptDataRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			encryptDataRequestDto.setData(CryptoUtil.encodeBase64(identity.get().getBytes()));
			try {
				restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.ENCRYPTION_SERVICE,
						RestRequestFactory.createRequest(encryptDataRequestDto), Map.class);
				response = restHelper.requestSync(restRequestDTO);
				return (String)((Map<String,Object>) response.get("response")).get("data");
			} catch (IDDataValidationException | RestServiceException e) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_ENCRYPTION,e);
			}
		}
		return null;
	}

}
