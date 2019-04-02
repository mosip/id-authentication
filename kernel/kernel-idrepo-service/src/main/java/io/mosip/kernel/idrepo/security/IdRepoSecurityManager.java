package io.mosip.kernel.idrepo.security;

import java.util.Date;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.kernel.core.idrepo.constant.IdRepoConstants;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.constant.RestServicesConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.exception.RestServiceException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.idrepo.builder.RestRequestBuilder;
import io.mosip.kernel.idrepo.config.IdRepoLogger;
import io.mosip.kernel.idrepo.dto.RestRequestDTO;
import io.mosip.kernel.idrepo.helper.RestHelper;

/**
 * The Class IdRepoSecurityManager.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoSecurityManager {

	/** The mosip logger. */
	private Logger mosipLogger = IdRepoLogger.getLogger(IdRepoSecurityManager.class);
	
	private static final String ENCRYPT_DECRYPT_DATA = "encryptDecryptData";

	/** The Constant ID_REPO_SERVICE. */
	private static final String ID_REPO_SERVICE = "IdRepoService";

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
	 * @param identityInfo
	 *            the identity info
	 * @return the string
	 */
	public String hash(final byte[] identityInfo) {
		return HMACUtils.digestAsPlainText(HMACUtils.generateHash(identityInfo));
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
			ObjectNode baseRequest = new ObjectNode(mapper.getNodeFactory());
			baseRequest.put("id", "string");
			baseRequest.put("requesttime",
					DateUtils.formatDate(new Date(), env.getProperty(IdRepoConstants.DATETIME_PATTERN.getValue())));
			baseRequest.put("version", "1.0");
			ObjectNode request = new ObjectNode(mapper.getNodeFactory());
			request.put("applicationId", env.getProperty(IdRepoConstants.APPLICATION_ID.getValue()));
			request.put("timeStamp",
					DateUtils.formatDate(new Date(), env.getProperty(IdRepoConstants.DATETIME_PATTERN.getValue())));
			request.put("data", CryptoUtil.encodeBase64(dataToEncrypt));
			baseRequest.set("request", request);
			return encryptDecryptData(
					restBuilder.buildRequest(RestServicesConstants.CRYPTO_MANAGER_ENCRYPT, baseRequest, ObjectNode.class));
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA,
					e.getErrorText());
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
			ObjectNode baseRequest = new ObjectNode(mapper.getNodeFactory());
			baseRequest.put("id", "string");
			baseRequest.put("requesttime",
					DateUtils.formatDate(new Date(), env.getProperty(IdRepoConstants.DATETIME_PATTERN.getValue())));
			baseRequest.put("version", "1.0");
			ObjectNode request = new ObjectNode(mapper.getNodeFactory());
			request.put("applicationId", env.getProperty(IdRepoConstants.APPLICATION_ID.getValue()));
			request.put("timeStamp",
					DateUtils.formatDate(new Date(), env.getProperty(IdRepoConstants.DATETIME_PATTERN.getValue())));
			request.put("data", CryptoUtil.encodeBase64(dataToDecrypt));
			baseRequest.set("request", request);
			return encryptDecryptData(
					restBuilder.buildRequest(RestServicesConstants.CRYPTO_MANAGER_DECRYPT, baseRequest, ObjectNode.class));
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA,
					e.getErrorText());
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
				mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA,
						"No data block found in response");
				throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED);
			}
		} catch (RestServiceException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_SECURITY_MANAGER, ENCRYPT_DECRYPT_DATA,
					e.getErrorText());
			throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
	}
}
