package io.mosip.idrepository.core.security;

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.constant.RestServicesConstants;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * The Class IdRepoSecurityManager.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoSecurityManager {

	/** The mosip logger. */
	private Logger mosipLogger = IdRepoLogger.getLogger(IdRepoSecurityManager.class);

	/** The Constant ENCRYPT_DECRYPT_DATA. */
	private static final String ENCRYPT_DECRYPT_DATA = "encryptDecryptData";

	/** The Constant ID_REPO_SECURITY_MANAGER. */
	private static final String ID_REPO_SECURITY_MANAGER = "IdRepoSecurityManager";

	/** The rest factory. */
	@Autowired
	private RestRequestBuilder restBuilder;

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The env. */
	@Autowired
	private Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/**
	 * Hash.
	 *
	 * @param data the identity info
	 * @return the string
	 */
	public String hash(final byte[] data) {
		return HMACUtils.digestAsPlainText(HMACUtils.generateHash(data));
	}

	/**
	 * Hash with salt.
	 *
	 * @param data the identity info
	 * @param salt the salt
	 * @return the string
	 */
	public String hashwithSalt(final byte[] data, final byte[] salt) {
		return HMACUtils.digestAsPlainTextWithSalt(data, salt);
	}
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public static String getUser() {
		if (Objects.nonNull(SecurityContextHolder.getContext())
				&& Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())
				&& Objects.nonNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AuthUserDetails) {
			return ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
					.getUserId();
		} else {
			return null;
		}
	}

	/**
	 * Encrypt.
	 *
	 * @param dataToEncrypt the data to encrypt
	 * @return the byte[]
	 * @throws IdRepoAppException the id repo app exception
	 */
	public byte[] encrypt(final byte[] dataToEncrypt) throws IdRepoAppException {
		try {
			RequestWrapper<ObjectNode> baseRequest = new RequestWrapper<>();
			baseRequest.setId("string");
			baseRequest.setRequesttime(DateUtils.getUTCCurrentDateTime());
			baseRequest.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
			ObjectNode request = new ObjectNode(mapper.getNodeFactory());
			request.put("applicationId", env.getProperty(IdRepoConstants.APPLICATION_ID.getValue()));
			request.put("timeStamp",
					DateUtils.formatDate(new Date(), env.getProperty(IdRepoConstants.DATETIME_PATTERN.getValue())));
			request.put("data", CryptoUtil.encodeBase64(dataToEncrypt));
			baseRequest.setRequest(request);
			return encryptDecryptData(restBuilder.buildRequest(RestServicesConstants.CRYPTO_MANAGER_ENCRYPT,
					baseRequest, ObjectNode.class));
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA, e.getErrorText());
			throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
	}
	
	/**
	 * Encrypt.
	 *
	 * @param dataToEncrypt the data to encrypt
	 * @param saltToEncrypt the salt to encrypt
	 * @return the byte[]
	 * @throws IdRepoAppException the id repo app exception
	 */
	public byte[] encryptWithSalt(final byte[] dataToEncrypt,final byte[] saltToEncrypt) throws IdRepoAppException {
		try {
			RequestWrapper<ObjectNode> baseRequest = new RequestWrapper<>();
			baseRequest.setId("string");
			baseRequest.setRequesttime(DateUtils.getUTCCurrentDateTime());
			baseRequest.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
			ObjectNode request = new ObjectNode(mapper.getNodeFactory());
			request.put("applicationId", env.getProperty(IdRepoConstants.APPLICATION_ID.getValue()));
			request.put("timeStamp",
					DateUtils.formatDate(new Date(), env.getProperty(IdRepoConstants.DATETIME_PATTERN.getValue())));
			request.put("data", CryptoUtil.encodeBase64(dataToEncrypt));
			request.put("salt", CryptoUtil.encodeBase64(saltToEncrypt));
			baseRequest.setRequest(request);
			return encryptDecryptData(restBuilder.buildRequest(RestServicesConstants.CRYPTO_MANAGER_ENCRYPT,
					baseRequest, ObjectNode.class));
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA, e.getErrorText());
			throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
	}

	/**
	 * Decrypt.
	 *
	 * @param dataToDecrypt the data to decrypt
	 * @return the byte[]
	 * @throws IdRepoAppException the id repo app exception
	 */
	public byte[] decrypt(final byte[] dataToDecrypt) throws IdRepoAppException {
		try {
			RequestWrapper<ObjectNode> baseRequest = new RequestWrapper<>();
			baseRequest.setId("string");
			baseRequest.setRequesttime(DateUtils.getUTCCurrentDateTime());
			baseRequest.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
			ObjectNode request = new ObjectNode(mapper.getNodeFactory());
			request.put("applicationId", env.getProperty(IdRepoConstants.APPLICATION_ID.getValue()));
			request.put("timeStamp",
					DateUtils.formatDate(new Date(), env.getProperty(IdRepoConstants.DATETIME_PATTERN.getValue())));
			request.put("data", new String(dataToDecrypt));
			baseRequest.setRequest(request);
			return CryptoUtil.decodeBase64(new String(encryptDecryptData(restBuilder
					.buildRequest(RestServicesConstants.CRYPTO_MANAGER_DECRYPT, baseRequest, ObjectNode.class))));
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA, e.getErrorText());
			throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
	}
	
	/**
	 * Decrypt with salt.
	 *
	 * @param dataToDecrypt the data to decrypt
	 * @param saltToDecrypt the salt to decrypt
	 * @return the byte[]
	 * @throws IdRepoAppException the id repo app exception
	 */
	public byte[] decryptWithSalt(final byte[] dataToDecrypt, final byte[] saltToDecrypt) throws IdRepoAppException {
		try {
			RequestWrapper<ObjectNode> baseRequest = new RequestWrapper<>();
			baseRequest.setId("string");
			baseRequest.setRequesttime(DateUtils.getUTCCurrentDateTime());
			baseRequest.setVersion(env.getProperty(IdRepoConstants.APPLICATION_VERSION.getValue()));
			ObjectNode request = new ObjectNode(mapper.getNodeFactory());
			request.put("applicationId", env.getProperty(IdRepoConstants.APPLICATION_ID.getValue()));
			request.put("timeStamp",
					DateUtils.formatDate(new Date(), env.getProperty(IdRepoConstants.DATETIME_PATTERN.getValue())));
			request.put("data", CryptoUtil.encodeBase64(dataToDecrypt));
			request.put("salt", CryptoUtil.encodeBase64(saltToDecrypt));
			baseRequest.setRequest(request);
			return CryptoUtil.decodeBase64(new String(encryptDecryptData(restBuilder
					.buildRequest(RestServicesConstants.CRYPTO_MANAGER_DECRYPT, baseRequest, ObjectNode.class))));
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA, e.getErrorText());
			throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
	}

	/**
	 * Encrypt decrypt data.
	 *
	 * @param restRequest the rest request
	 * @return the byte[]
	 * @throws IdRepoAppException the id repo app exception
	 */
	private byte[] encryptDecryptData(final RestRequestDTO restRequest) throws IdRepoAppException {
		try {
			ObjectNode response = restHelper.requestSync(restRequest);

			if (response.has("response") && Objects.nonNull(response.get("response"))
					&& response.get("response").has("data") && Objects.nonNull(response.get("response").get("data"))) {
				return response.get("response").get("data").asText().getBytes();
			} else {
				mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA,
						"No data block found in response");
				throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED);
			}
		} catch (RestServiceException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA, e.getErrorText());
			throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
	}
}
