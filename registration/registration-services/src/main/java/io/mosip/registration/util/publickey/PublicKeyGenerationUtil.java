package io.mosip.registration.util.publickey;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;

/**
 * Utility class to generate the RSA Public Key from encoded bytes
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class PublicKeyGenerationUtil {

	private static final Logger LOGGER = AppConfig.getLogger(PublicKeyGenerationUtil.class);

	private PublicKeyGenerationUtil() {
	}

	/**
	 * Generates the RSA public key form the input encoded key
	 * 
	 * @param encodedKey
	 *            the RSA encoded key. This encoded bytes have to be base64 encoded
	 *            using {@link CryptoUtil}
	 * @return the RSA {@link PublicKey}
	 * @throws InvalidKeySpecException
	 *             if encoded key is invalid
	 * @throws NoSuchAlgorithmException
	 *             if key is invalid
	 */
	public static PublicKey generatePublicKey(byte[] encodedKey)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		LOGGER.info("LoggerConstants.UTIL_PUBLIC_KEY_GENERATION", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Generating RSA public key from encoded key");

		return KeyFactory
				.getInstance(RegistrationConstants.ASYMMETRIC_ALG_NAME)
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(new String(encodedKey))));
	}

}
