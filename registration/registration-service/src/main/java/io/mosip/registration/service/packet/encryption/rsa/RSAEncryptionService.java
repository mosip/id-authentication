package io.mosip.registration.service.packet.encryption.rsa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegProcessorExceptionEnum.REG_RSA_INVALID_DATA;
import static io.mosip.registration.constants.RegProcessorExceptionEnum.REG_RSA_INVALID_KEY;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_RSA_ENCRYPTION;

/**
 * Accepts aes encrypted bytes and encrypt it by using rsa algorithm
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Service
public class RSAEncryptionService {

	@Autowired
	public RSAKeyGenerator rsaKeyGenerator;

	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Encrypts the AES Session Key using RSA encryption algorithm
	 * 
	 * @param sessionKey
	 *            has to be encrypted
	 * @return rsaEncryptedBytes has encrypted by rsa
	 * @throws RegBaseCheckedException
	 */
	public byte[] encrypt(final byte[] sessionKey) throws RegBaseCheckedException {
		try {
			logger.debug(LOG_PKT_RSA_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					"Packet RSA Encryption had been called");
			// TODO: Will be removed upon KeyManager is implemented in Kernel App
			// Generate key pair public and private key
			rsaKeyGenerator.generateKey();
			// Read public key from file
			final byte[] publicKey = rsaKeyGenerator.getEncodedKey(true);

			// encrypt AES Session Key using RSA public keyF
			return MosipEncryptor.asymmetricPublicEncrypt(publicKey, sessionKey,
					MosipSecurityMethod.RSA_WITH_PKCS1PADDING);

		} catch (MosipInvalidDataException mosipInvalidDataException) {
			throw new RegBaseCheckedException(REG_RSA_INVALID_DATA.getErrorCode(),
					REG_RSA_INVALID_DATA.getErrorMessage());
		} catch (MosipInvalidKeyException mosipInvalidKeyException) {
			throw new RegBaseCheckedException(REG_RSA_INVALID_KEY.getErrorCode(),
					REG_RSA_INVALID_KEY.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.RSA_ENCRYPTION_MANAGER,
					runtimeException.toString(), runtimeException);
		}
	}

}
