package io.mosip.kernel.keygenerator.utils;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.KeyGenerator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import io.mosip.kernel.keygenerator.config.KeyGeneratorConfig;
import io.mosip.kernel.keygenerator.constants.KeyGeneratorExceptionConstants;
import io.mosip.kernel.keygenerator.exception.MosipNoSuchAlgorithmException;

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
	 * This class configures keygenerator
	 * 
	 * @param config
	 *            algorithm and size configuration {@link KeyGeneratorConfig}
	 * @return configured {@link KeyGenerator} instance
	 */
	public static KeyGenerator getKeyGenerator(KeyGeneratorConfig config) {

		KeyGenerator generator = null;
		try {
			generator = KeyGenerator.getInstance(config.getAlgorithmName(), provider);
		} catch (NoSuchAlgorithmException e) {
			throw new MosipNoSuchAlgorithmException(KeyGeneratorExceptionConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION);
		}
		SecureRandom random = new SecureRandom();
		generator.init(Integer.parseInt(config.getSize()), random);
		return generator;
	}

	/**
	 * This class configures keypairgenerator
	 * 
	 * @param config
	 *            algorithm and size configuration {@link KeyGeneratorConfig}
	 * @return configured {@link KeyPairGenerator} instance
	 */
	public static KeyPairGenerator getKeyPairGenerator(KeyGeneratorConfig config) {

		KeyPairGenerator generator = null;
		try {
			generator = KeyPairGenerator.getInstance(config.getAlgorithmName(), provider);
		} catch (NoSuchAlgorithmException e) {
			throw new MosipNoSuchAlgorithmException(KeyGeneratorExceptionConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION);
		}
		SecureRandom random = new SecureRandom();
		generator.initialize(Integer.parseInt(config.getSize()), random);
		return generator;
	}

	/**
	 * Initialize by adding bouncy castle provider in jvm.
	 * 
	 * @return {@link BouncyCastleProvider}
	 */
	private static BouncyCastleProvider init() {
		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		return provider;
	}
}
