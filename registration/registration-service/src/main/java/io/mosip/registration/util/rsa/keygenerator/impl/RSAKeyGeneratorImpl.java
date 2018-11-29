package io.mosip.registration.util.rsa.keygenerator.impl;

import static io.mosip.registration.constants.RegistrationExceptions.REG_IO_ERROR_CODE;
import static io.mosip.registration.constants.RegistrationExceptions.REG_NO_SUCH_ALGORITHM_ERROR_CODE;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;

/**
 * RSA key generation
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class RSAKeyGeneratorImpl implements RSAKeyGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mindtree.mosip.utility.rsa.keygenerator.RSAKeyGenerator#generateKey()
	 */
	public void generateKey() {
		KeyPairGenerator keyPairGenerator = null;
		try {
			// Generate key pair generator
			keyPairGenerator = KeyPairGenerator.getInstance(RegistrationConstants.RSA_ALG);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		}
		// initialize key pair generator
		keyPairGenerator.initialize(2048);
		// get key pair
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		// get public key from key pair
		PublicKey publicKey = keyPair.getPublic();
		// getting private key from key pair
		PrivateKey privateKey = keyPair.getPrivate();

		try {
			// Save public key in public.key file
			FileUtils.writeByteArrayToFile(new File(RegistrationConstants.RSA_PUBLIC_KEY_FILE), publicKey.getEncoded());

			// save private key in private.key file
			FileUtils.writeByteArrayToFile(new File(RegistrationConstants.RSA_PRIVATE_KEY_FILE), privateKey.getEncoded());
		} catch (IOException mosipIOException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(), REG_IO_ERROR_CODE.getErrorMessage(),
					mosipIOException);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator#getEncodedKey(
	 * boolean)
	 */
	public byte[] getEncodedKey(boolean isPublic) {
		try {
			return FileUtils.readFileToByteArray(
					new File(isPublic ? RegistrationConstants.RSA_PUBLIC_KEY_FILE : RegistrationConstants.RSA_PRIVATE_KEY_FILE));
		} catch (IOException mosipIOException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(), REG_IO_ERROR_CODE.getErrorMessage(),
					mosipIOException);
		}
	}
}
