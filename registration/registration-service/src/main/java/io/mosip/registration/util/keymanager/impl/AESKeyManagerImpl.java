package io.mosip.registration.util.keymanager.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.constants.RegProcessorExceptionEnum;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.keymanager.AESKeyManager;

import static io.mosip.registration.constants.RegConstants.AES_KEY_MANAGER_ALG;
import static io.mosip.registration.constants.RegConstants.AES_KEY_SEED_LENGTH;
import static io.mosip.registration.constants.RegConstants.AES_SESSION_KEY_LENGTH;
import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_AES_KEY_GENERATION;

/**
 * Class to generate the AES Session Key
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class AESKeyManagerImpl implements AESKeyManager {

	@Autowired
	private Environment environment;
	/**
	 * Instance of {@link MosipLogger}
	 */
	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mosip.client.service.KeyManager#generateKey()
	 */
	public SecretKey generateSessionKey(final List<String> aesKeySeeds) throws RegBaseCheckedException {
		logger.debug(LOG_PKT_AES_KEY_GENERATION, APPLICATION_NAME, APPLICATION_ID,
				"Generating AES Encryption had been started");
		try {
			// Concatenate the seeds for AES Session Key
			byte[] seedArray = aesKeySeeds.stream().reduce("", String::concat).getBytes();

			// Ensure Seed Length is not more than 32
			Integer aesKeySeedLength = Integer.parseInt(environment.getProperty(AES_KEY_SEED_LENGTH));
			if (seedArray.length > aesKeySeedLength) {
				seedArray = Arrays.copyOf(seedArray, aesKeySeedLength);
			}

			final KeyGenerator aesKeyGenerator = KeyGenerator.getInstance(environment.getProperty(AES_KEY_MANAGER_ALG));
			aesKeyGenerator.init(Integer.parseInt(environment.getProperty(AES_SESSION_KEY_LENGTH)),
					new SecureRandom(seedArray));
			logger.debug(LOG_PKT_AES_KEY_GENERATION, APPLICATION_NAME,
					APPLICATION_ID, "Generating AES Encryption had been ended");
			return aesKeyGenerator.generateKey();
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.AES_KEY_MANAGER, runtimeException.toString());
		}
	}

}
