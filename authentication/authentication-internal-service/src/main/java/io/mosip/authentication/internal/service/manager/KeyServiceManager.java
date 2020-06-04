package io.mosip.authentication.internal.service.manager;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.SignatureStatusDto;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;

/**
 * 
 * @author Nagarjuna
 *
 */

@Component
public class KeyServiceManager {

	/** The mosip logger. */
	private Logger mosipLogger = IdaLogger.getLogger(KeyServiceManager.class);
	
	/** The Constant ENCRYPT_DECRYPT_DATA. */
	private static final String ENCRYPT_DECRYPT_DATA = "encryptDecryptData";

	/** The Constant ID_AUTH_TRANSACTION_MANAGER. */
	private static final String ID_KEY_TRANSACTION_MANAGER = "IdKeymanagerServiceImpl";
	
	/** The cryptomanager service. */
	@Autowired
	private CryptomanagerService cryptomanagerService;
	
	/** The keymanager service. */
	@Autowired
	private KeymanagerService keymanagerService;
	
	/** The mapper. */
	@Autowired
	private Environment env;
	
	/**
	 * kernel crypto core
	 */
	@Autowired
	private CryptoCore cryptoCore;
	
	/**
	 * 
	 * @param applicationId
	 * @param timeStamp
	 * @param referenceId
	 * @return
	 * @throws IdAuthenticationBusinessException 
	 */
	public PublicKeyResponse<String> getPublicKey(String applicationId, String timeStamp,
			Optional<String> referenceId) throws IdAuthenticationBusinessException {
		try {
			return keymanagerService.getPublicKey(applicationId, timeStamp, referenceId);
		}catch(Exception e) {
			mosipLogger.error(getUser(), ID_KEY_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
	/**
	 * 
	 * @param applicationId
	 * @param timeStamp
	 * @param referenceId
	 * @return
	 * @throws IdAuthenticationBusinessException 
	 */
	public PublicKeyResponse<String> getSignPublicKey(String applicationId, String timeStamp,
			Optional<String> referenceId) throws IdAuthenticationBusinessException {
		try {
			return keymanagerService.getSignPublicKey(applicationId, timeStamp, referenceId);
		}catch(Exception e) {
			mosipLogger.error(getUser(), ID_KEY_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);			
		}
	}
	
	/**
	 * 
	 * @param dataToEncrypt
	 * @param refId
	 * @param aad
	 * @param saltToEncrypt
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public CryptomanagerResponseDto encrypt(String dataToEncrypt, String refId, String aad, String saltToEncrypt)
			throws IdAuthenticationBusinessException {
		try {
			CryptomanagerRequestDto request = new CryptomanagerRequestDto();
			request.setApplicationId(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			request.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			request.setData(dataToEncrypt);
			request.setReferenceId(refId);
			request.setAad(aad);
			request.setSalt(saltToEncrypt);
			return cryptomanagerService.encrypt(request);
		} catch (NoUniqueAliasException e) {
			// TODO: check whether PUBLICKEY_EXPIRED to be thrown for NoUniqueAliasException
			mosipLogger.error(getUser(), ID_KEY_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED, e);
		} catch (Exception e) {
			mosipLogger.error(getUser(), ID_KEY_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FAILED_TO_ENCRYPT, e);
		}
	}
	/**
	 * Decrypts the data
	 * @param cryptoRequestDto
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public CryptomanagerResponseDto decrypt(CryptomanagerRequestDto cryptoRequestDto) throws IdAuthenticationBusinessException {
		try {
			return cryptomanagerService.decrypt(cryptoRequestDto);
		}catch (Exception e) {
			mosipLogger.error(getUser(), ID_KEY_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_ENCRYPTION, e);
		}		
	}
	
	/**
	 * Verifies the jwsSignature 
	 * @param jwsSignature
	 * @return
	 */
	public SignatureStatusDto verifySignature(String jwsSignature) {
		SignatureStatusDto status = new SignatureStatusDto();
		if(cryptoCore.verifySignature(jwsSignature)) {
			status.setStatus("VALID");
			status.setPayload(new String(CryptoUtil.decodeBase64(getPayloadFromJwsSingature(jwsSignature))));
		} else {
			status.setStatus("INVALID");
		}
		
		return status;
	}
	
	/**
	 * Getting the payload from jws
	 * @param jws
	 * @return
	 */
	private String getPayloadFromJwsSingature(String jws) {
		String[] split = jws.split("\\.");
		if(split.length >= 2) {
			return split[1];
		}
		return jws;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return env.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_AUTH_CLIENTID);
	}	

}
