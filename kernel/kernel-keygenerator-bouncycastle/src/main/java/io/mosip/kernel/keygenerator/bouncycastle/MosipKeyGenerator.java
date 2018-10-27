package io.mosip.kernel.keygenerator.bouncycastle;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import io.mosip.kernel.keygenerator.bouncycastle.config.KeyGeneratorConfig;
import io.mosip.kernel.keygenerator.bouncycastle.util.KeyGeneratorUtils;

/**
 * This class generates asymmetric and symmetric key pairs
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class MosipKeyGenerator {

	/**
	 * No Args Constructor for this class
	 */
	private MosipKeyGenerator() {
	}

	/**
	 * This method generates symmetric key
	 * 
	 * @return generated symmetric key
	 */
	public static SecretKey getSymmetricKey() {

		KeyGenerator generator = KeyGeneratorUtils.getKeyGenerator(KeyGeneratorConfig.SYMMETRIC_ALGORITHM);
		return generator.generateKey();
	}

	/**
	 * This method generated Asymmetric key pairs
	 * 
	 * @return {@link MosipAsymmetricKeyPair} which contain public nad private key
	 */
	public static KeyPair getAsymmetricKey() {
		KeyPairGenerator generator = KeyGeneratorUtils.getKeyPairGenerator(KeyGeneratorConfig.ASYMMETRIC_ALGORITHM);
		return generator.generateKeyPair();

	}

}
