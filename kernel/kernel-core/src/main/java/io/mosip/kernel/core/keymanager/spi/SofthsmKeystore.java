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
	 * Adds a provider to the next position available.
	 * 
	 * If there is a security manager, the
	 * java.lang.SecurityManager.checkSecurityAccess method is called with the
	 * "insertProvider" permission target name to see if it's ok to add a new
	 * provider. If this permission check is denied, checkSecurityAccess is called
	 * again with the "insertProvider."+provider.getName() permission target name.
	 * If both checks are denied, a SecurityException is thrown.
	 * 
	 * @param provider
	 *            the provider to be added
	 */
	void addProvider(Provider provider);

	/**
	 * Returns a keystore object of the specified type.
	 * 
	 * A new KeyStore object encapsulating the KeyStoreSpi implementation from the
	 * specified Provider object is returned. Note that the specified Provider
	 * object does not have to be registered in the provider list.
	 * 
	 * @param keystoreType
	 *            the type of keystore
	 * @param provider
	 *            provider
	 * @return a keystore object of the specified type.
	 */
	KeyStore getKeystoreInstance(String keystoreType, Provider provider);

	/**
	 * 
	 * 
	 * Loads this KeyStore from the given input stream.
	 * 
	 * A password may be given to unlock the keystore (e.g. the keystore resides on
	 * a hardware token device), or to check the integrity of the keystore data. If
	 * a password is not given for integrity checking, then integrity checking is
	 * not performed.
	 * 
	 * In order to create an empty keystore, or if the keystore cannot be
	 * initialized from a stream, pass null as the stream argument.
	 * 
	 * Note that if this keystore has already been loaded, it is reinitialized and
	 * loaded again from the given input stream.
	 * 
	 */
	void loadKeystore();

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