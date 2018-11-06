package io.mosip.kernel.keygenerator.bouncycastle;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.keygenerator.bouncycastle.util.KeyGeneratorUtils;

/**
 * This class generates asymmetric and symmetric key pairs
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Component
public class KeyGenerator {

	/**
	 * Symmetric key algorithm Name
	 */
	@Value("${mosip.kernel.keygenerator.bouncycastle.symmetric.algorithm.name}")
	private String symmetricKeyAlgorithm;

	/**
	 * Symmetric key length
	 */
	@Value("${mosip.kernel.keygenerator.bouncycastle.symmetric.algorithm.length}")
	private int symmetricKeyLength;

	/**
	 * Asymmetric key algorithm Name
	 */
	@Value("${mosip.kernel.keygenerator.bouncycastle.asymmetric.algorithm.name}")
	private String asymmetricKeyAlgorithm;

	/**
	 * Asymmetric key length
	 */
	@Value("${mosip.kernel.keygenerator.bouncycastle.asymmetric.algorithm.length}")
	private int asymmetricKeyLength;

	/**
	 * This method generates symmetric key
	 * 
	 * @return generated symmetric key
	 */
	public SecretKey getSymmetricKey() {
		javax.crypto.KeyGenerator generator = KeyGeneratorUtils
				.getKeyGenerator(symmetricKeyAlgorithm, symmetricKeyLength);
		return generator.generateKey();
	}

	/**
	 * This method generated Asymmetric key pairs
	 * 
	 * @return {@link AsymmetricKeyPair} which contain public nad private key
	 */
	public KeyPair getAsymmetricKey() {
		KeyPairGenerator generator = KeyGeneratorUtils.getKeyPairGenerator(
				asymmetricKeyAlgorithm, asymmetricKeyLength);
		return generator.generateKeyPair();

	}

}
