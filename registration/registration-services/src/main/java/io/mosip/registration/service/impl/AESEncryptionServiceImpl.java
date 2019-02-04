package io.mosip.registration.service.impl;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.AESEncryptionService;
import io.mosip.registration.service.RSAEncryptionService;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_AES_ENCRYPTION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * API class to encrypt the data using AES algorithm
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class AESEncryptionServiceImpl implements AESEncryptionService {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AESEncryptionServiceImpl.class);
	@Autowired
	private Environment environment;
	@Autowired
	private RSAEncryptionService rsaEncryptionService;
	@Autowired
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;
	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.encryption.aes.AESEncryptionService#
	 * encrypt(byte[])
	 */
	@Override
	public byte[] encrypt(final byte[] dataToEncrypt) throws RegBaseCheckedException {
		LOGGER.info(LOG_PKT_AES_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID, "Packet encryption had been started");

		try {
			// Enable AES 256 bit encryption
			Security.setProperty("crypto.policy", "unlimited");
			
			// Generate AES Session Key
			final SecretKey symmetricKey = keyGenerator.getSymmetricKey();

			// Encrypt the Data using AES
			final byte[] encryptedData = encryptor.symmetricEncrypt(symmetricKey, dataToEncrypt);

			LOGGER.info(LOG_PKT_AES_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					"In-Memory zip file encrypted using AES Algorithm successfully");

			// Encrypt the AES Session Key using RSA
			final byte[] rsaEncryptedKey = rsaEncryptionService.encrypt(symmetricKey.getEncoded());

			LOGGER.info(LOG_PKT_AES_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					"AES Session Key encrypted using RSA Algorithm successfully");

			// Combine AES Session Key, AES Key Splitter and RSA Encrypted Data
			auditFactory.audit(AuditEvent.PACKET_AES_ENCRYPTED, Components.PACKET_AES_ENCRYPTOR,
					"RSA and AES Encryption completed successfully", "RID", "Packet RID");

			return CryptoUtil.combineByteArray(encryptedData, rsaEncryptedKey,
					environment.getProperty(RegistrationConstants.AES_KEY_CIPHER_SPLITTER));
		} catch (MosipInvalidDataException mosipInvalidDataException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_INVALID_DATA_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_INVALID_DATA_ERROR_CODE.getErrorMessage());
		} catch (MosipInvalidKeyException mosipInvalidKeyException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_INVALID_KEY_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_INVALID_KEY_ERROR_CODE.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.AES_ENCRYPTION_MANAGER,
					runtimeException.toString());
		}
	}

}
