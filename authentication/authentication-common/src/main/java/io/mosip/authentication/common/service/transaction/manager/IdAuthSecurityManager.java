package io.mosip.authentication.common.service.transaction.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.keymanagerservice.dto.SignatureRequestDto;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;

/**
 * The Class IdAuthSecurityManager.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthSecurityManager {

	/** The Constant ENCRYPT_DECRYPT_DATA. */
	private static final String ENCRYPT_DECRYPT_DATA = "encryptDecryptData";

	/** The Constant ID_AUTH_TRANSACTION_MANAGER. */
	private static final String ID_AUTH_TRANSACTION_MANAGER = "IdAuthSecurityManager";

	/** The mosip logger. */
	private Logger mosipLogger = IdaLogger.getLogger(IdAuthSecurityManager.class);

	/** The mapper. */
	@Autowired
	private Environment env;

	/** The cryptomanager service. */
	@Autowired
	private CryptomanagerService cryptomanagerService;
	
	/** The key manager. */
	@Autowired
	private KeymanagerService keyManager;
	
	/** The sign applicationid. */
	@Value("${mosip.sign.applicationid:KERNEL}")
	private String signApplicationid;
	
	/** The sign refid. */
	@Value("${mosip.sign.refid:SIGN}")
	private String signRefid;

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return env.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_AUTH_CLIENTID);
	}

	/**
	 * Encrypt.
	 *
	 * @param dataToEncrypt the data to encrypt
	 * @param refId the ref id
	 * @param aad the aad
	 * @param saltToEncrypt the salt to encrypt
	 * @return the byte[]
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public byte[] encrypt(String dataToEncrypt, String refId, String aad, String saltToEncrypt)
			throws IdAuthenticationBusinessException {
		try {
			CryptomanagerRequestDto request = new CryptomanagerRequestDto();
			request.setApplicationId(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			request.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			request.setData(dataToEncrypt);
			request.setReferenceId(refId);
			request.setAad(aad);
			request.setSalt(saltToEncrypt);
			return CryptoUtil.decodeBase64(cryptomanagerService.encrypt(request).getData());
		} catch (NoUniqueAliasException e) {
			//TODO: check whether PUBLICKEY_EXPIRED to be thrown for NoUniqueAliasException
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED, e);
		} catch (Exception e) {
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FAILED_TO_ENCRYPT, e);
		}
	}

	/**
	 * Decrypt.
	 *
	 * @param dataToDecrypt the data to decrypt
	 * @param refId the ref id
	 * @param aad the aad
	 * @param saltToDecrypt the salt to decrypt
	 * @return the byte[]
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public byte[] decrypt(String dataToDecrypt, String refId, String aad, String saltToDecrypt)
			throws IdAuthenticationBusinessException {
		try {
			CryptomanagerRequestDto request = new CryptomanagerRequestDto();
			request.setApplicationId(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			request.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			request.setData(dataToDecrypt);
			request.setReferenceId(refId);
			request.setAad(aad);
			request.setSalt(saltToDecrypt);
			return CryptoUtil.decodeBase64(cryptomanagerService.decrypt(request).getData());
		} catch (NoUniqueAliasException e) {
			//TODO: check whether PUBLICKEY_EXPIRED to be thrown for NoUniqueAliasException
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED, e);
		} catch (Exception e) {
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FAILED_TO_ENCRYPT, e);
		}
	}
	
	/**
	 * Sign.
	 *
	 * @param data the data
	 * @return the string
	 */
	public String sign(String data) {
		//TODO: check whether any exception will be thrown
		SignatureRequestDto request = new SignatureRequestDto(signApplicationid, signRefid,
				DateUtils.getUTCCurrentDateTimeString(), data);
		return keyManager.sign(request).getData();
	}
}
