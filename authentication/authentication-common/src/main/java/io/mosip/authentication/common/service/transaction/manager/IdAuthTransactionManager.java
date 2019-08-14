package io.mosip.authentication.common.service.transaction.manager;

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class IdAuthTransactionManager.
 */
@Component
public class IdAuthTransactionManager {
	
	/** The Constant ENCRYPT_DECRYPT_DATA. */
	private static final String ENCRYPT_DECRYPT_DATA = "encryptDecryptData";

	/** The Constant ID_AUTH_TRANSACTION_MANAGER. */
	private static final String ID_AUTH_TRANSACTION_MANAGER = "IdAuthTransactionManager";

	/** The mosip logger. */
	private Logger mosipLogger = IdaLogger.getLogger(IdAuthTransactionManager.class);
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;	
	
/** The environment. */
@Autowired
private Environment environment;

/** The rest builder. */
@Autowired
private RestRequestFactory restBuilder;

/** The rest helper. */
@Autowired
private RestHelper restHelper;

	/**
	 * provides the user id.
	 *
	 * @return the user
	 */
	public String getUser() {
		return environment.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_AUTH_CLIENTID);
		
	}
	
	

	
	/**
	 * Encryption of data by making rest call to kernel-cryptomanager with salt.
	 *
	 * @param dataToEncrypt the data to encrypt
	 * @param saltToEncrypt the salt to encrypt
	 * @return the byte[]
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public byte[] encryptWithSalt(final byte[] dataToEncrypt,final byte[] saltToEncrypt) throws IdAuthenticationBusinessException{
		try {
			RequestWrapper<ObjectNode> baseRequest = new RequestWrapper<>();
			baseRequest.setId("string");
			baseRequest.setRequesttime(DateUtils.getUTCCurrentDateTime());
			baseRequest.setVersion(environment.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_API_VERSION));
			ObjectNode request = new ObjectNode(mapper.getNodeFactory());
			request.put("applicationId", environment.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			request.put("timeStamp",
					DateUtils.getUTCCurrentDateTimeString());
			request.put("data", CryptoUtil.encodeBase64(dataToEncrypt));
			request.put("salt", CryptoUtil.encodeBase64(saltToEncrypt));
			baseRequest.setRequest(request);
			return encryptDecryptData(restBuilder.buildRequest(RestServicesConstants.ENCRYPTION_SERVICE,
					baseRequest, ObjectNode.class));
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER,ENCRYPT_DECRYPT_DATA, e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_ENCRYPTION,e);
		} 
	}
	
	/**
	 * Rest calls is made to kernel-cryptomanager and required data from response is 
	 * extracted and handled.
	 *
	 * @param restRequest the rest request
	 * @return the byte[]
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private byte[] encryptDecryptData(final RestRequestDTO restRequest) throws IdAuthenticationBusinessException {
		try {
			ObjectNode response = restHelper.requestSync(restRequest);

			if (response.has("response") && Objects.nonNull(response.get("response"))
					&& response.get("response").has("data") && Objects.nonNull(response.get("response").get("data"))) {
				return response.get("response").get("data").asText().getBytes();
			} else {
				mosipLogger.error(getUser(),  ID_AUTH_TRANSACTION_MANAGER,ENCRYPT_DECRYPT_DATA,
						"No data block found in response");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_ENCRYPTION);
			}
		} catch (RestServiceException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(),  ID_AUTH_TRANSACTION_MANAGER,ENCRYPT_DECRYPT_DATA, e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_ENCRYPTION);
		}
	}
	
}
