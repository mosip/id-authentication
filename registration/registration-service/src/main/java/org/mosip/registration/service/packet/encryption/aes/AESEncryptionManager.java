/**
 * 
 */
package org.mosip.registration.service.packet.encryption.aes;

import java.security.Security;
import java.util.List;

import javax.crypto.SecretKey;

import static java.lang.System.arraycopy;

import org.mosip.kernel.core.security.constants.MosipSecurityMethod;
import org.mosip.kernel.core.security.encryption.MosipEncryptor;
import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.config.AuditFactory;
import org.mosip.registration.constants.AppModuleEnum;
import org.mosip.registration.constants.AuditEventEnum;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.constants.RegProcessorExceptionCode;
import org.mosip.registration.constants.RegProcessorExceptionEnum;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.service.packet.encryption.rsa.RSAEncryptionManager;
import org.mosip.registration.util.keymanager.AESKeyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME; 
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * API class to encrypt the data using AES algorithm
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class AESEncryptionManager {

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
	private RSAEncryptionManager rsaEncryptionManager;
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/**
	 * The API method to encrypt the data using AES Algorithm
	 * 
	 * @param dataToEncrypt
	 * @return encrypted data as byte array
	 * @throws RegBaseCheckedException
	 */
	public byte[] encrypt(final byte[] dataToEncrypt) throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - AES_ENCRYPTION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Packet encryption had been started");
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
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTOR - AES_ENCRYPTOR", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "In-Memory zip file encrypted using AES Algorithm successfully");

			// Encrypt the AES Session Key using RSA
			final byte[] rsaEncryptedKey = rsaEncryptionManager.encrypt(sessionKey.getEncoded());
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTOR - AES_ENCRYPTOR", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "AES Session Key encrypted using RSA Algorithm successfully");

			// Combine AES Session Key, AES Key Splitter and RSA Encrypted Data
			auditFactory.audit(AuditEventEnum.PACKET_AES_ENCRYPTED, AppModuleEnum.PACKET_AES_ENCRYPTOR,
					"RSA and AES Encryption completed successfully", "registration reference id", "123456");
			return concat(rsaEncryptedKey, encryptedData);
		} catch (MosipInvalidDataException mosipInvalidDataException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_INVALID_DATA_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_INVALID_DATA_ERROR_CODE.getErrorMessage());
		} catch (MosipInvalidKeyException mosipInvalidKeyException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_INVALID_KEY_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_INVALID_KEY_ERROR_CODE.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.AES_ENCRYPTION_MANAGER,
					runtimeException.toString());
		}
	}

	private byte[] concat(final byte[] keyByteArray, final byte[] encryptedDataByteArray) {
		LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRYPTION", "EnrollmentId", "id",
				"Encryption concatenation had been started");
		try {
			final String keySplitter = getPropertyValue(RegConstants.AES_KEY_CIPHER_SPLITTER);
			final int keyLength = keyByteArray.length;
			final int encryptedDataLength = encryptedDataByteArray.length;
			final int keySplitterLength = keySplitter.length();

			byte[] combinedData = new byte[keyLength + encryptedDataLength + keySplitterLength];

			arraycopy(keyByteArray, 0, combinedData, 0, keyLength);
			arraycopy(keySplitter.getBytes(), 0, combinedData, keyLength, keySplitterLength);
			arraycopy(encryptedDataByteArray, 0, combinedData, keyLength + keySplitterLength, encryptedDataLength);

			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRYPTION", "EnrollmentId", "id",
					"Encryption concatenation had been ended");
			return combinedData;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.CONCAT_ENCRYPTED_DATA,
					runtimeException.toString());
		}
	}

}
