package io.mosip.kernel.idrepo.security;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.kernel.core.exception.ExceptionUtils;
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

	/** The Constant MOSIP_KERNEL_IDREPO_APPLICATION_ID. */
	private static final String MOSIP_KERNEL_IDREPO_APPLICATION_ID = "mosip.kernel.idrepo.application.id";

	/** The mosip logger. */
	private Logger mosipLogger = IdRepoLogger.getLogger(IdRepoSecurityManager.class);

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.utc-datetime-pattern";

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

	/** The request. */
	private ObjectNode request;

	/**
	 * Builds the requet.
	 */
	@PostConstruct
	public void buildRequest() {
		request = new ObjectNode(mapper.getNodeFactory());
		request.put("applicationId", env.getProperty(MOSIP_KERNEL_IDREPO_APPLICATION_ID));
		request.put("timeStamp", DateUtils.formatDate(new Date(), env.getProperty(DATETIME_PATTERN)));
	}

	/**
	 * Hash.
	 *
	 * @param identityInfo
	 *            the identity info
	 * @return the string
	 */
	public String hash(byte[] identityInfo) {
		return HMACUtils.digestAsPlainText(HMACUtils.generateHash(identityInfo));
	}

	/**
	 * Encrypt.
	 *
	 * @param dataToEncrypt the data to encrypt
	 * @return the byte[]
	 * @throws IdRepoAppException the id repo app exception
	 */
	public byte[] encrypt(byte[] dataToEncrypt) throws IdRepoAppException {
		try {
			request.put("data", CryptoUtil.encodeBase64(dataToEncrypt));
			return encryptDecryptData(
					restBuilder.buildRequest(RestServicesConstants.CRYPTO_MANAGER_ENCRYPT, request, ObjectNode.class));
		} catch (IdRepoAppException e) {
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
	public byte[] decrypt(byte[] dataToDecrypt) throws IdRepoAppException {
		try {
			request.put("data", CryptoUtil.encodeBase64(dataToDecrypt));
			return encryptDecryptData(
					restBuilder.buildRequest(RestServicesConstants.CRYPTO_MANAGER_DECRYPT, request, ObjectNode.class));
		} catch (IdRepoAppException e) {
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
	private byte[] encryptDecryptData(RestRequestDTO restRequest) throws IdRepoAppException {
		try {
			ObjectNode response = restHelper.requestSync(restRequest);

			if (response.has("data")) {
				return response.get("data").asText().getBytes();
			} else {
				mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SECURITY_MANAGER, "encryptDecryptData",
						"No data block found in response");
				throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED);
			}
		} catch (RestServiceException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SECURITY_MANAGER, "encryptDecryptData",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
	}
}
