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
 */
/**
 * @author Arun Bose
 *
 */
@Component
public class IdAuthSecurityManager {

	private static final String ENCRYPT_DECRYPT_DATA = "encryptDecryptData";

	private static final String ID_AUTH_TRANSACTION_MANAGER = "IdAuthSecurityManager";

	private Logger mosipLogger = IdaLogger.getLogger(IdAuthSecurityManager.class);

	/** The mapper. */
	@Autowired
	private Environment env;

	@Autowired
	private CryptomanagerService cryptomanagerService;
	
	@Autowired
	private KeymanagerService keyManager;
	
	@Value("${mosip.sign.applicationid:KERNEL}")
	private String signApplicationid;
	
	@Value("${mosip.sign.refid:SIGN}")
	private String signRefid;

	public String getUser() {
		return env.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_AUTH_CLIENTID);
	}

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
	
	public String sign(String data) {
		//TODO: check whether any exception will be thrown
		SignatureRequestDto request = new SignatureRequestDto(signApplicationid, signRefid,
				DateUtils.getUTCCurrentDateTimeString(), data);
		return keyManager.sign(request).getData();
	}
}
