package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_AES_ENCRYPTION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static java.lang.System.arraycopy;

import java.security.Security;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.AESEncryptionService;
import io.mosip.registration.service.AESSeedGenerator;
import io.mosip.registration.service.RSAEncryptionService;
import io.mosip.registration.util.keymanager.AESKeyManager;

/**
 * API class to encrypt the data using AES algorithm
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class AESEncryptionServiceImpl implements AESEncryptionService {

	@Autowired
	private Environment environment;
	/**
	 * Class to generate the seeds for AES Session Key
	 */
	@Autowired
	private AESSeedGenerator aesSeedGenerator;
	/**
	 * Class to generate the AES Session Key
	 */
	@Autowired
	private AESKeyManager aesKeyManager;
	/**
	 * Class to encrypt the AES Session Key using RSA Algorithm
	 */
	@Autowired
	private RSAEncryptionService rsaEncryptionService;
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AESEncryptionServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.packet.encryption.aes.AESEncryptionService#encrypt(byte[])
	 */
	@Override
	public byte[] encrypt(final byte[] dataToEncrypt) throws RegBaseCheckedException {
		LOGGER.debug(LOG_PKT_AES_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
				"Packet encryption had been started");
		
		try {
			// Enable AES 256 bit encryption
			Security.setProperty("crypto.policy", "unlimited");
			// Get the Seeds for AES Session Key
			List<String> aesKeySeeds = aesSeedGenerator.generateAESKeySeeds();

			// Generate AES Session Key
			final SecretKey sessionKey = aesKeyManager.generateSessionKey(aesKeySeeds);

			// Encrypt the Data using AES
			final byte[] encryptedData = MosipEncryptor.symmetricEncrypt(sessionKey.getEncoded(), dataToEncrypt,
					MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
			
			LOGGER.debug(LOG_PKT_AES_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					"In-Memory zip file encrypted using AES Algorithm successfully");

			// Encrypt the AES Session Key using RSA
			final byte[] rsaEncryptedKey = rsaEncryptionService.encrypt(sessionKey.getEncoded());
			
			LOGGER.debug(LOG_PKT_AES_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					"AES Session Key encrypted using RSA Algorithm successfully");

			// Combine AES Session Key, AES Key Splitter and RSA Encrypted Data
			auditFactory.audit(AuditEvent.PACKET_AES_ENCRYPTED, Components.PACKET_AES_ENCRYPTOR,
					"RSA and AES Encryption completed successfully", "RID", "Packet RID");
			
			return concat(rsaEncryptedKey, encryptedData);
		} catch (MosipInvalidDataException mosipInvalidDataException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_INVALID_DATA_ERROR_CODE.getErrorCode(),
					RegistrationExceptions.REG_INVALID_DATA_ERROR_CODE.getErrorMessage());
		} catch (MosipInvalidKeyException mosipInvalidKeyException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_INVALID_KEY_ERROR_CODE.getErrorCode(),
					RegistrationExceptions.REG_INVALID_KEY_ERROR_CODE.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.AES_ENCRYPTION_MANAGER,
					runtimeException.toString());
		}
	}

	private byte[] concat(final byte[] keyByteArray, final byte[] encryptedDataByteArray) {
		LOGGER.debug(LOG_PKT_AES_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
				"Encryption concatenation had been started");
		
		try {
			final String keySplitter = environment.getProperty(RegistrationConstants.AES_KEY_CIPHER_SPLITTER);
			final int keyLength = keyByteArray.length;
			final int encryptedDataLength = encryptedDataByteArray.length;
			final int keySplitterLength = keySplitter.length();

			byte[] combinedData = new byte[keyLength + encryptedDataLength + keySplitterLength];

			arraycopy(keyByteArray, 0, combinedData, 0, keyLength);
			arraycopy(keySplitter.getBytes(), 0, combinedData, keyLength, keySplitterLength);
			arraycopy(encryptedDataByteArray, 0, combinedData, keyLength + keySplitterLength, encryptedDataLength);

			LOGGER.debug(LOG_PKT_AES_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					"Encryption concatenation had been ended");
			
			return combinedData;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.CONCAT_ENCRYPTED_DATA,
					runtimeException.toString());
		}
	}

}
