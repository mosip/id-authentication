/**
 * 
 */
package io.mosip.kernel.keymanager.softhsm;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.SecretKey;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface SofthsmKeystore {

	/**
	 * @param provider
	 */
	void addProvider(Provider provider);

	/**
	 * @param provider
	 * @return
	 * @throws KeyStoreException
	 */
	KeyStore getKeystoreInstance(Provider provider);

	/**
	 * @param keyStorePassword
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 */
	void loadKeystore();

	/**
	 * @param keyStorePassword
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 */
	List<String> getAllAlias();

	/**
	 * @param alias
	 * @param keyStorePassword
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableEntryException
	 * @throws KeyStoreException
	 * @throws CertificateEncodingException
	 */
	PrivateKeyEntry getAsymmetricKey(String alias);

	/**
	 * @param alias
	 * @param keyStorePassword
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws CertificateException
	 */
	void createAsymmetricKey(String alias);

	/**
	 * @param alias
	 * @param keyStorePassword
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableEntryException
	 * @throws KeyStoreException
	 */
	SecretKey getSymmetricKey(String alias);

	/**
	 * @param alias
	 * @param keyStorePassword
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	void createSymmetricKey(String alias);

	/**
	 * @param alias
	 * @return
	 */
	Key getKeyByAlias(String alias);

	/**
	 * @param alias
	 * @return
	 */
	PrivateKey getPrivateKey(String alias);

	/**
	 * @param alias
	 * @return
	 */
	PublicKey getPublicKey(String alias);

	/**
	 * @param alias
	 * @return
	 */
	Certificate getCertificate(String alias);

}