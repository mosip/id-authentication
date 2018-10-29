package io.mosip.registration.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RSAEncryptionService;
import io.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationExceptions.REG_RSA_INVALID_DATA;
import static io.mosip.registration.constants.RegistrationExceptions.REG_RSA_INVALID_KEY;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_RSA_ENCRYPTION;

/**
 * Accepts AES session key as bytes and encrypt it by using RSA algorithm
 * 
 * @author YASWANTH S
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class RSAEncryptionServiceImpl implements RSAEncryptionService {

	@Autowired
	public RSAKeyGenerator rsaKeyGenerator;

	private static MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.packet.encryption.rsa.RSAEncryptionService#encrypt(byte[])
	 */
	@Override
	public byte[] encrypt(final byte[] sessionKey) throws RegBaseCheckedException {
		try {
			logger.debug(LOG_PKT_RSA_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					"Packet RSA Encryption had been called");
			
			// TODO: Will be removed upon KeyManager is implemented in Kernel App
			// Generate key pair public and private key
			rsaKeyGenerator.generateKey();
			// Read public key from file
			final byte[] publicKey = rsaKeyGenerator.getEncodedKey(true);

			// encrypt AES Session Key using RSA public key
			return MosipEncryptor.asymmetricPublicEncrypt(publicKey, sessionKey,
					MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
		} catch (MosipInvalidDataException mosipInvalidDataException) {
			throw new RegBaseCheckedException(REG_RSA_INVALID_DATA.getErrorCode(),
					REG_RSA_INVALID_DATA.getErrorMessage());
		} catch (MosipInvalidKeyException mosipInvalidKeyException) {
			throw new RegBaseCheckedException(REG_RSA_INVALID_KEY.getErrorCode(),
					REG_RSA_INVALID_KEY.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.RSA_ENCRYPTION_MANAGER,
					runtimeException.toString(), runtimeException);
		}
	}

}
