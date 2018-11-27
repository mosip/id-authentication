package io.mosip.kernel.keygenerator.bouncycastle.util;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.KeyGenerator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.keygenerator.bouncycastle.constant.KeyGeneratorExceptionConstant;

/**
 * This is a utils class for keygenerator
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class KeyGeneratorUtils {

	/**
	 * Bouncy-Castle provider instance
	 */
	private static BouncyCastleProvider provider;

	static {
		provider = init();
	}

	/**
	 * No Args Constructor for this class
	 */
	private KeyGeneratorUtils() {
	}

	/**
	 * This class configures {@link KeyGenerator}
	 * 
	 * @param algorithmName
	 *            algorithm name as configured
	 * @param keylength
	 *            key-length as configured
	 * @return configured {@link KeyGenerator} instance
	 */
	public static javax.crypto.KeyGenerator getKeyGenerator(
			String algorithmName, int keylength) {

		javax.crypto.KeyGenerator generator = null;
		try {
			generator = javax.crypto.KeyGenerator.getInstance(algorithmName,provider);
		} catch (java.security.NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException(
					KeyGeneratorExceptionConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					KeyGeneratorExceptionConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
		SecureRandom random = new SecureRandom();
		generator.init(keylength, random);
		return generator;
	}

	/**
	 * This class configures {@link KeyPairGenerator}
	 * 
	* @param algorithmName
	 *            algorithm name as configured
	 * @param keylength
	 *            key-length as configured
	 * @return configured {@link KeyPairGenerator} instance
	 */
	public static KeyPairGenerator getKeyPairGenerator(String algorithmName,
			int keylength) {

		KeyPairGenerator generator = null;
		try {
			generator = KeyPairGenerator.getInstance(algorithmName, provider);
		} catch (java.security.NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException(
					KeyGeneratorExceptionConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION
							.getErrorCode(),
					KeyGeneratorExceptionConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION
							.getErrorMessage());
		}
		SecureRandom random = new SecureRandom();
		generator.initialize(keylength, random);
		return generator;
	}

	/**
	 * Initialize by adding bouncy castle provider in JVM.
	 * 
	 * @return {@link BouncyCastleProvider}
	 */
	private static BouncyCastleProvider init() {
		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		return provider;
	}
}
