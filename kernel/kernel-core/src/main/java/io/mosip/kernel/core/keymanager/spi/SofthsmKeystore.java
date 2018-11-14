package io.mosip.kernel.core.keymanager.spi;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.List;

import javax.crypto.SecretKey;

/**
 * SoftHSM Keystore implementation based on OpenDNSSEC that handles and stores
 * its cryptographic keys via the PKCS#11 interface. This is a software
 * implementation of a generic cryptographic device. SoftHSM is designed to meet
 * the requirements of OpenDNSSEC, but can also work together with other
 * cryptographic products because of the PKCS#11 interface.
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface SofthsmKeystore {

	/**
	 * Lists all the alias names of this keystore.
	 * 
	 * @return list of all alias in keystore
	 */
	List<String> getAllAlias();

	/**
	 * Returns the key associated with the given alias, using the given password to
	 * recover it. The key must have been associated with the alias by a call to
	 * setKeyEntry, or by a call to setEntry with a PrivateKeyEntry or
	 * SecretKeyEntry.
	 * 
	 * @param alias
	 *            the alias
	 * @return the requested key, or null if the given alias does not exist or does
	 *         not identify a key-related entry
	 */
	Key getKey(String alias);

	/**
	 * Get private key from keystore
	 * 
	 * @param alias
	 *            the alias
	 * @return The private key
	 */
	PrivateKey getPrivateKey(String alias);

	/**
	 * Get public key from keystore
	 * 
	 * @param alias
	 *            the alias
	 * @return The public key
	 */
	PublicKey getPublicKey(String alias);

	/**
	 * Get certificate from keystore
	 * 
	 * @param alias
	 *            the alias
	 * @return The certificate
	 */
	Certificate getCertificate(String alias);

	/**
	 * Get Symmetric key from keystore
	 * 
	 * @param alias
	 *            the alias
	 * @return The Symmetric key
	 */
	SecretKey getSymmetricKey(String alias);

	/**
	 * Get Asymmetric key from keystore
	 * 
	 * @param alias
	 *            the alias
	 * @return The asymmetric key
	 */
	PrivateKeyEntry getAsymmetricKey(String alias);

	/**
	 * Store symmetric key in keystore
	 * 
	 * @param secretKey
	 *            the secret key
	 * @param alias
	 *            the alias
	 */
	void storeSymmetricKey(SecretKey secretKey, String alias);

	/**
	 * Store keypair in keystore
	 * 
	 * @param keyPair
	 *            the keypair
	 * @param alias
	 *            the alias
	 * @param validDays
	 *            validity days
	 */
	void storeAsymmetricKey(KeyPair keyPair, String alias, int validDays);

	/**
	 * Delete key form keystore
	 * 
	 * @param alias
	 *            the alias
	 */
	void deleteKey(String alias);

}