package io.mosip.registration.util.rsa.keygenerator;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface RSAKeyGenerator {
	/**
	 * Generate public key and private key for encryption and decryption
	 */
	void generateKey();

	/**
	 * Save key into file
	 * @param filePath
	 *            file path to save key
	 * @param mod key modulus value
	 * @param exp key exponent value
	 */
	void saveKey(String filePath, BigInteger mod, BigInteger exp);

	/**
	 * Read PublicKey for encryption purpose
	 * @param filePath to read public key
	 * @return PublicKey to encrypt data
	 */
	PublicKey readPublickey(String filePath);

	/**
	 * Read private key for decryption purpose
	 * @param filePath to read private key
	 * @return PrivateKey
	 */
	PrivateKey readPrivatekey(String filePath);
}
