package org.mosip.registration.util.keymanager.impl;

import static org.mosip.registration.consts.RegConstants.AES_KEY_MANAGER_ALG;
import static org.mosip.registration.consts.RegConstants.AES_KEY_SEED_LENGTH;
import static org.mosip.registration.consts.RegConstants.AES_SESSION_KEY_LENGTH;
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.mosip.registration.consts.RegProcessorExceptionCode;
import org.mosip.registration.consts.RegProcessorExceptionEnum;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.util.keymanager.AESKeyManager;
import org.springframework.stereotype.Component;

/**
 * Class to generate the AES Session Key
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class AESKeyManagerImpl implements AESKeyManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mosip.client.service.KeyManager#generateKey()
	 */
	public SecretKey generateSessionKey(final List<String> aesKeySeeds) throws RegBaseCheckedException {
		try {
			// Concatenate the seeds for AES Session Key
			byte[] seedArray = aesKeySeeds.stream().reduce("", String::concat).getBytes();
	
			// Ensure Seed Length is not more than 32
			Integer aesKeySeedLength = Integer.parseInt(getPropertyValue(AES_KEY_SEED_LENGTH));
			if (seedArray.length > aesKeySeedLength) {
				seedArray = Arrays.copyOf(seedArray, aesKeySeedLength);
			}

			final KeyGenerator aesKeyGenerator = KeyGenerator
					.getInstance(getPropertyValue(AES_KEY_MANAGER_ALG));
			aesKeyGenerator.init(Integer.parseInt(getPropertyValue(AES_SESSION_KEY_LENGTH)),
					new SecureRandom(seedArray));
			return aesKeyGenerator.generateKey();
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage());
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.AES_KEY_MANAGER, uncheckedException.getMessage());
		}
	}

}
