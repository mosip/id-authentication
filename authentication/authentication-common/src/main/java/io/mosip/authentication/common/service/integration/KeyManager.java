package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.function.ConsumerWithThrowable;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class KeyManager is used to decipher the request and returning the
 * decipher request to the filter to do further authentication.
 * 
 * @author Sanjay Murali
 * @author Manoj SP
 * 
 */
@Component
public class KeyManager {

	/** The Constant SESSION_KEY. */
	private static final String SESSION_KEY = "requestSessionKey";

	/** The app id. */
	@Value("${" + IdAuthConfigKeyConstants.APPLICATION_ID + "}")
	private String appId;

	/** The partner id. */
	@Value("${" + IdAuthConfigKeyConstants.PARTNER_REFERENCE_ID + "}")
	private String partnerId;

	/** The key splitter. */
	@Value("${" + IdAuthConfigKeyConstants.KEY_SPLITTER + "}")
	private String keySplitter;

	/** The security manager. */
	@Autowired
	private IdAuthSecurityManager securityManager;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(KeyManager.class);

	/**
	 * requestData method used to decipher the request block {@link RequestDTO}
	 * present in AuthRequestDTO {@link AuthRequestDTO}.
	 *
	 * @param requestBody   the request body
	 * @param mapper        the mapper
	 * @param refId         the ref id
	 * @param reqHMAC
	 * @param dataValidator
	 * @return the map
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationBusinessException
	 */
	public Map<String, Object> requestData(Map<String, Object> requestBody, ObjectMapper mapper, String refId,
			String thumbprint, Boolean isThumbprintEnabled,
			ConsumerWithThrowable<String, IdAuthenticationAppException> dataValidator)
			throws IdAuthenticationAppException {
		Map<String, Object> request = null;
		try {
			byte[] encryptedRequest = (byte[]) requestBody.get(IdAuthCommonConstants.REQUEST);
			byte[] encryptedSessionkey = CryptoUtil.decodeBase64((String) requestBody.get(SESSION_KEY));
			request = decipherData(mapper, thumbprint, encryptedRequest, encryptedSessionkey, refId,
					isThumbprintEnabled, dataValidator);
		} catch (IOException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "requestData",
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
		}
		return request;
	}

	/**
	 * decipherData method used to derypt data if session key is present.
	 *
	 * @param mapper              the mapper
	 * @param encryptedRequest    the encrypted request
	 * @param encryptedSessionKey the encrypted session key
	 * @param refId               the ref id
	 * @param reqHMAC
	 * @return the map
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IOException                       Signals that an I/O exception has
	 *                                           occurred.
	 * @throws IdAuthenticationBusinessException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> decipherData(ObjectMapper mapper, String thumbprint, byte[] encryptedRequest,
			byte[] encryptedSessionKey, String refId, Boolean isThumbprintEnabled,
			ConsumerWithThrowable<String, IdAuthenticationAppException> dataValidator)
			throws IdAuthenticationAppException, IOException {
		String decryptedAndDecodedData = kernelDecryptAndDecode(thumbprint,
				CryptoUtil
						.encodeBase64(CryptoUtil.combineByteArray(encryptedRequest, encryptedSessionKey, keySplitter)),
				refId, isThumbprintEnabled);

		if (dataValidator != null) {
			dataValidator.accept(decryptedAndDecodedData);
		}

		return mapper.readValue(decryptedAndDecodedData, Map.class);
	}

	/**
	 * Kernel decrypt and decode.
	 *
	 * @param data  the data
	 * @param refId the ref id
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public String kernelDecryptAndDecode(String thumbprint, String data, String refId, Boolean isThumbprintEnabled)
			throws IdAuthenticationAppException {
		return internalKernelDecryptAndDecode(thumbprint, data, refId, null, null, true, isThumbprintEnabled);
	}

	/**
	 * Kernel decrypt.
	 *
	 * @param data  the data
	 * @param refId the ref id
	 * @param aad   the aad
	 * @param salt  the salt
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public String kernelDecrypt(String thumbprint, String data, String refId, String aad, String salt,
			Boolean isThumbprintEnabled) throws IdAuthenticationAppException {
		return internalKernelDecryptAndDecode(thumbprint, data, refId, aad, salt, false, isThumbprintEnabled);
	}

	/**
	 * Internal kernel decrypt and decode.
	 *
	 * @param data   the data
	 * @param refId  the ref id
	 * @param aad    the aad
	 * @param salt   the salt
	 * @param decode the decode
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private String internalKernelDecryptAndDecode(String thumbprint, String data, String refId, String aad, String salt,
			Boolean decode, Boolean isThumbprintEnabled) throws IdAuthenticationAppException {
		String decryptedRequest = null;
		if (isThumbprintEnabled) {
			data = CryptoUtil.encodeBase64(
					ArrayUtils.addAll(CryptoUtil.decodeBase64(thumbprint), CryptoUtil.decodeBase64(data)));
		}
		try {
			String encodedIdentity = CryptoUtil
					.encodeBase64(securityManager.decrypt(data, refId, aad, salt, isThumbprintEnabled));
			if (decode) {
				decryptedRequest = new String(CryptoUtil.decodeBase64(encodedIdentity), StandardCharsets.UTF_8);
			} else {
				decryptedRequest = encodedIdentity;
			}
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			if (e.getErrorCode().contentEquals(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED.getErrorCode())) {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED, e);
			} else {
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_ENCRYPTION, e);
			}
		}
		return decryptedRequest;
	}

	/**
	 * Encrypt data.
	 *
	 * @param responseBody the response body
	 * @param mapper       the mapper
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	@SuppressWarnings("unchecked")
	public String encryptData(Map<String, Object> responseBody, ObjectMapper mapper)
			throws IdAuthenticationAppException {
		Map<String, Object> identity = responseBody.get("identity") instanceof Map
				? (Map<String, Object>) responseBody.get("identity")
				: null;
		if (Objects.nonNull(identity)) {
			try {
				String encodedData = CryptoUtil
						.encodeBase64(toJsonString(identity, mapper).getBytes(StandardCharsets.UTF_8));
				return CryptoUtil.encodeBase64(securityManager.encrypt(encodedData, partnerId, null, null));
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
						e.getErrorText());
				if (e.getErrorCode().contentEquals(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED.getErrorCode())) {
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED, e);
				} else {
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.FAILED_TO_ENCRYPT, e);
				}
			}
		}
		return null;
	}

	/**
	 * This method is used to convert the map to JSON format.
	 *
	 * @param map    the map
	 * @param mapper the mapper
	 * @return the string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	private String toJsonString(Object map, ObjectMapper mapper) throws IdAuthenticationAppException {
		try {
			return mapper.writerFor(Map.class).writeValueAsString(map);
		} catch (JsonProcessingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * This method is used to digitally sign the response.
	 *
	 * @param data the response got after authentication which to be signed
	 * @return the signed response string
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public String signResponse(String data) throws IdAuthenticationAppException {
		return securityManager.sign(data);
	}

}
