package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.dto.CryptomanagerRequestDto;
import io.mosip.authentication.common.service.integration.dto.EncryptDataRequestDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;

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

	/** The Constant SESSION_KEY. */
	private static final String SESSION_KEY = "requestSessionKey";
	
	/** The app id. */
	@Value("${" +IdAuthConfigKeyConstants.APPLICATION_ID+ "}")
	private String appId;
	
	/** The partner id. */
	@Value("${" +IdAuthConfigKeyConstants.CRYPTO_PARTNER_ID+ "}")
	private String partnerId;
	
	/** The key splitter. */
	@Value("${" +IdAuthConfigKeyConstants.KEY_SPLITTER+ "}")
	private String keySplitter;
	
	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The rest request factory. */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(KeyManager.class);
	
	

	/**
	 * requestData method used to decipher the request block {@link RequestDTO}
	 * present in AuthRequestDTO {@link AuthRequestDTO}.
	 *
	 * @param requestBody the request body
	 * @param mapper      the mapper
	 * @param refId the ref id
	 * @return the map
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 */
	public Map<String, Object> requestData(Map<String, Object> requestBody, ObjectMapper mapper, String refId)
			throws IdAuthenticationAppException {
		Map<String, Object> request = null;
		try {
			byte[] encryptedRequest = (byte[]) requestBody.get(IdAuthCommonConstants.REQUEST);
			byte[] encryptedSessionkey = Base64.decodeBase64((String)requestBody.get(SESSION_KEY));
			request = decipherData(mapper, encryptedRequest, encryptedSessionkey, refId);
		} catch (IOException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "requestData", e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
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
	private Map<String, Object> decipherData(ObjectMapper mapper, byte[] encryptedRequest, byte[] encryptedSessionKey, String refId)
			throws IdAuthenticationAppException, IOException {
		return mapper.readValue(kernelDecrypt(encryptedRequest, encryptedSessionKey, refId) ,Map.class);
	}

	/**
	 * This method does the necessary decryption of the request.
	 *
	 * @param encryptedRequest the encrypted request
	 * @param encryptedSessionKey the encrypted session key
	 * @param refId the ref id
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	public String kernelDecrypt(byte[] encryptedRequest, byte[] encryptedSessionKey, String refId)
			throws IdAuthenticationAppException {
		String decryptedRequest=null;
		CryptomanagerRequestDto cryptoManagerRequestDto = new CryptomanagerRequestDto();
		try {
			cryptoManagerRequestDto.setApplicationId(appId);
			cryptoManagerRequestDto.setReferenceId(refId);
			cryptoManagerRequestDto.setTimeStamp(
					DateUtils.getUTCCurrentDateTime());
			cryptoManagerRequestDto.setData(CryptoUtil.encodeBase64(
					CryptoUtil.combineByteArray(encryptedRequest, encryptedSessionKey, keySplitter)));
			RestRequestDTO restRequestDTO= restRequestFactory.buildRequest(RestServicesConstants.DECRYPTION_SERVICE,
					RestRequestFactory.createRequest(cryptoManagerRequestDto), Map.class);
			Map<String,Object> cryptoResponseMap = restHelper.requestSync(restRequestDTO);
			Object encodedIdentity= ((Map<String,Object>) cryptoResponseMap.get(IdAuthCommonConstants.RESPONSE)).get(IdAuthCommonConstants.DATA);
			decryptedRequest = new String(Base64.decodeBase64((String)encodedIdentity),
					StandardCharsets.UTF_8);
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
		return decryptedRequest;
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
	 * This method is used to encrypt the KYC identity response.
	 *
	 * @param responseBody the response body
	 * @param mapper the mapper
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	public String encryptData(Map<String, Object> responseBody, ObjectMapper mapper) throws IdAuthenticationAppException {
		Map<String, Object> identity = responseBody.get("identity") instanceof Map ? (Map<String, Object>) responseBody.get("identity") : null;
		Map<String, Object> response;
		RestRequestDTO restRequestDTO = null;
		if (Objects.nonNull(identity)) {
			EncryptDataRequestDto encryptDataRequestDto = new EncryptDataRequestDto();
			encryptDataRequestDto.setApplicationId(appId);
			encryptDataRequestDto.setReferenceId(partnerId);
			encryptDataRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			encryptDataRequestDto.setData(CryptoUtil.encodeBase64(toJsonString(identity, mapper).getBytes()));
			try {
				restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.ENCRYPTION_SERVICE,
						RestRequestFactory.createRequest(encryptDataRequestDto), Map.class);
				response = restHelper.requestSync(restRequestDTO);
				return (String)((Map<String,Object>) response.get(IdAuthCommonConstants.RESPONSE)).get(IdAuthCommonConstants.DATA);
			} catch (IDDataValidationException | RestServiceException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
			}
		}
		return null;
	}
	
	/**
	 * This method is used to convert the map to JSON format
	 *
	 * @param map the map
	 * @param mapper the mapper
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private String toJsonString(Object map, ObjectMapper mapper) throws IdAuthenticationAppException {
		try {
			return mapper.writerFor(Map.class).writeValueAsString(map);
		} catch (JsonProcessingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
		}
	}
	
	/**
	 * This method is used to digitally sign the response
	 *
	 * @param responseAsString the response got after authentication which to be signed
	 * @return the signed response string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	public String signResponse(String responseAsString) throws IdAuthenticationAppException {
		Map<String, Object> signResponse = new HashMap<>();
		signResponse.put("data", responseAsString);
		Map<String, Object> response;
		RestRequestDTO restRequestDTO = null;
		try {
			restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.DIGITAL_SIGNATURE_SIGN_SERVICE,
					RestRequestFactory.createRequest(signResponse), Map.class);
			response = restHelper.requestSync(restRequestDTO);
			if(response.containsKey(IdAuthCommonConstants.RESPONSE) && Objects.nonNull(response.get(IdAuthCommonConstants.RESPONSE))) {
				return (String)((Map<String,Object>) response.get(IdAuthCommonConstants.RESPONSE)).get("signature");
			} else {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
		} catch (IDDataValidationException | RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
		
		}
	}

}
