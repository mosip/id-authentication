package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
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
import io.mosip.kernel.core.http.ResponseWrapper;
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
	@SuppressWarnings("unchecked")
	public Map<String, Object> requestData(Map<String, Object> requestBody, ObjectMapper mapper)
			throws IdAuthenticationAppException {
		Map<String, Object> request = null;
		SecretKey secretKey=null;
		byte[] decryptedData=null;
		try {
			byte[] encryptedRequest = (byte[]) requestBody.get(REQUEST);
			Optional<String> encryptedSessionKey = Optional.ofNullable(requestBody.get(SESSION_KEY))
					.map(String::valueOf);
			if (encryptedSessionKey.isPresent()) {
				RestRequestDTO restRequestDTO = null;
				SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto();
				ResponseWrapper<Map<String,Object>> symmetricKeyResponseDto = null;
				byte[] decryptedSymmetricKey = null;
				try {
					symmetricKeyRequestDto.setApplicationId(appId);
					symmetricKeyRequestDto.setReferenceId(environment.getProperty("mosip.ida.publickey"));
					symmetricKeyRequestDto.setTimeStamp(
							DateUtils.getUTCCurrentDateTime());
					// cryptoManagerRequestDto.setTimeStamp("2031-03-07T12:58:41.762Z");
					symmetricKeyRequestDto.setEncryptedSymmetricKey(encryptedSessionKey.get());
					restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.DECRYPTION_SERVICE,
							RestRequestFactory.createRequest(symmetricKeyRequestDto), ResponseWrapper.class);
					symmetricKeyResponseDto = restHelper.requestSync(restRequestDTO);
					Object symmetricKeyValue = symmetricKeyResponseDto.getResponse().get("symmetricKey");
					decryptedSymmetricKey = Base64.decodeBase64(symmetricKeyValue instanceof String ? (String) symmetricKeyValue : "");
					secretKey=new SecretKeySpec(decryptedSymmetricKey, 0, decryptedSymmetricKey.length, SYMMETRIC_ALGORITHM_NAME);
						decryptedData=symmetricDecrypt(secretKey, encryptedRequest);
				}
				catch (RestServiceException e) {
					logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
					Optional<Object> responseBody = e.getResponseBody();
					if (responseBody.isPresent()) {
						Map<String, Object> idrepoMap = (Map<String, Object>) responseBody.get();
						if (idrepoMap.containsKey("errors")) {
							List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) idrepoMap
									.get("errors");
							String keyExpErrorCode = "KER-KMS-003"; // TODO FIXME integrate with kernel error constant
							if (!idRepoerrorList.isEmpty()
									&& idRepoerrorList.stream().anyMatch(map -> map.containsKey("errCode")
											&& ((String) map.get("errCode")).equalsIgnoreCase(keyExpErrorCode))) {
								throw new IdAuthenticationAppException(
										IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED);
							} else {
								throw new IdAuthenticationAppException(
										IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
							}
						}
					}

					logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.SERVER_ERROR);
				} catch (IDDataValidationException e) {
					logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
				}

				request = mapper.readValue(decryptedData,Map.class);
				request.put(SECRET_KEY, secretKey);
			}
		} catch (IOException e) {
			logger.error(SESSION_ID, this.getClass().getSimpleName(), "requestData", e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
		return request;
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
		  Cipher cipher=null;;
		try {
			cipher = Cipher.getInstance(AESPADDING);
			 cipher.init(Cipher.DECRYPT_MODE, secretKey,
						new IvParameterSpec(Arrays.copyOfRange(encryptedDataByteArr, encryptedDataByteArr.length - cipher.getBlockSize(), encryptedDataByteArr.length)),secureRandom);
			   byte[] dataArr=cipher.doFinal(Arrays.copyOf(encryptedDataByteArr, encryptedDataByteArr.length - cipher.getBlockSize()));
			   return dataArr;
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
