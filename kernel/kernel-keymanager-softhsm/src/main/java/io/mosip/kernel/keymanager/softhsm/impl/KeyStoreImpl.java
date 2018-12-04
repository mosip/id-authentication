package io.mosip.kernel.keymanager.softhsm.impl;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.keymanager.exception.KeystoreProcessingException;
import io.mosip.kernel.core.keymanager.exception.NoSuchSecurityProviderException;
import io.mosip.kernel.keymanager.softhsm.constant.KeymanagerErrorCode;
import io.mosip.kernel.keymanager.softhsm.util.CertificateUtility;
import sun.security.pkcs11.SunPKCS11;

/**
 * Softhsm Keymanager implementation based on OpenDNSSEC that handles and stores
 * its cryptographic keys via the PKCS#11 interface. This is a software
 * implementation of a generic cryptographic device. SoftHSM can work with other
 * cryptographic device because of the PKCS#11 interface.
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class KeyStoreImpl implements io.mosip.kernel.core.keymanager.spi.KeyStore,InitializingBean {

	/**
	 * Common name for generating certificate
	 */
	@Value("${mosip.kernel.keymanager.softhsm.certificate.common-name}")
	private String commonName;

	/**
	 * Organizational Unit for generating certificate
	 */
	@Value("${mosip.kernel.keymanager.softhsm.certificate.organizational-unit}")
	private String organizationalUnit;

	/**
	 * Organization for generating certificate
	 */
	@Value("${mosip.kernel.keymanager.softhsm.certificate.organization}")
	private String organization;

	/**
	 * Country for generating certificate
	 */
	@Value("${mosip.kernel.keymanager.softhsm.certificate.country}")
	private String country;
	
	@Value("${mosip.kernel.keymanager.softhsm.config-path}") 
	private String configPath;
	
	@Value("${mosip.kernel.keymanager.softhsm.keystore-type}") 
	private String keystoreType;
	
	@Value("${mosip.kernel.keymanager.softhsm.keystore-pass}") 
	private String keystorePass;

   /**
	 * The Keystore instance
	 */
	private KeyStore keyStore;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		    Provider provider = setupProvider(configPath);
			addProvider(provider);
			this.keyStore = getKeystoreInstance(keystoreType, provider);
			loadKeystore();
	}
	
   /**
	 * Setup a new SunPKCS11 provider
	 * 
	 * @param configPath
	 *            The path of config file
	 * @return Provider
	 */
	private Provider setupProvider(String configPath) {
		Provider provider = null;
		try {
			provider = new SunPKCS11(configPath);
		} catch (ProviderException providerException) {
			throw new NoSuchSecurityProviderException(KeymanagerErrorCode.INVALID_CONFIG_FILE.getErrorCode(),
					KeymanagerErrorCode.INVALID_CONFIG_FILE.getErrorMessage());
		}
		return provider;
	}

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
	private void addProvider(Provider provider) {
		if (-1 == Security.addProvider(provider)) {
			throw new NoSuchSecurityProviderException(KeymanagerErrorCode.NO_SUCH_SECURITY_PROVIDER.getErrorCode(),
					KeymanagerErrorCode.NO_SUCH_SECURITY_PROVIDER.getErrorMessage());
		}
	}

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
	private KeyStore getKeystoreInstance(String keystoreType, Provider provider) {
		KeyStore mosipKeyStore = null;
		try {
			mosipKeyStore = KeyStore.getInstance(keystoreType, provider);
		} catch (KeyStoreException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}
		return mosipKeyStore;
	}

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
	private void loadKeystore() {

		try {
			keyStore.load(null, keystorePass.toCharArray());
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#getAllAlias()
	 */
	@Override
	public List<String> getAllAlias() {
		Enumeration<String> enumeration = null;
		try {
			enumeration = keyStore.aliases();
		} catch (KeyStoreException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}
		return Collections.list(enumeration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#getKey(java.lang.String)
	 */
	@Override
	public Key getKey(String alias) {
		Key key = null;
		try {
			key = keyStore.getKey(alias, keystorePass.toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#getAsymmetricKey(java.
	 * lang.String)
	 */
	@Override
	public PrivateKeyEntry getAsymmetricKey(String alias) {
		PrivateKeyEntry privateKeyEntry = null;
		try {
			if (keyStore.entryInstanceOf(alias, PrivateKeyEntry.class)) {
				ProtectionParameter password = new PasswordProtection(keystorePass.toCharArray());
				privateKeyEntry = (PrivateKeyEntry) keyStore.getEntry(alias, password);
			} else {
				throw new NoSuchSecurityProviderException(KeymanagerErrorCode.NO_SUCH_ALIAS.getErrorCode(),
						KeymanagerErrorCode.NO_SUCH_ALIAS.getErrorMessage() + alias);
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}
		return privateKeyEntry;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#getPrivateKey(java.lang.
	 * String)
	 */
	@Override
	public PrivateKey getPrivateKey(String alias) {
		PrivateKeyEntry privateKeyEntry = getAsymmetricKey(alias);
		return privateKeyEntry.getPrivateKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#getPublicKey(java.lang.
	 * String)
	 */
	@Override
	public PublicKey getPublicKey(String alias) {
		PrivateKeyEntry privateKeyEntry = getAsymmetricKey(alias);
		Certificate[] certificates = privateKeyEntry.getCertificateChain();
		return certificates[0].getPublicKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#getCertificate(java.lang.
	 * String)
	 */
	@Override
	public X509Certificate getCertificate(String alias) {
		PrivateKeyEntry privateKeyEntry = getAsymmetricKey(alias);
		X509Certificate[] certificates = (X509Certificate[]) privateKeyEntry.getCertificateChain();
		return certificates[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#storeAsymmetricKey(java.
	 * security.KeyPair, java.lang.String)
	 */
	@Override
	public void storeAsymmetricKey(KeyPair keyPair, String alias, LocalDateTime validityFrom,
			LocalDateTime validityTo) {

		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = CertificateUtility.generateX509Certificate(keyPair, commonName, organizationalUnit, organization,
				country, validityFrom, validityTo);
		PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(keyPair.getPrivate(), chain);
		ProtectionParameter password = new PasswordProtection(keystorePass.toCharArray());

		try {
			keyStore.setEntry(alias, privateKeyEntry, password);
			keyStore.store(null, keystorePass.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#getSymmetricKey(java.lang
	 * .String)
	 */
	@Override
	public SecretKey getSymmetricKey(String alias) {
		SecretKey secretKey = null;
		try {
			if (keyStore.entryInstanceOf(alias, SecretKeyEntry.class)) {
				ProtectionParameter password = new PasswordProtection(keystorePass.toCharArray());
				SecretKeyEntry retrivedSecret = (SecretKeyEntry) keyStore.getEntry(alias, password);
				secretKey = retrivedSecret.getSecretKey();
			} else {
				throw new NoSuchSecurityProviderException(KeymanagerErrorCode.NO_SUCH_ALIAS.getErrorCode(),
						KeymanagerErrorCode.NO_SUCH_ALIAS.getErrorMessage() + alias);
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}
		return secretKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#storeSymmetricKey(javax.
	 * crypto.SecretKey, java.lang.String)
	 */
	@Override
	public void storeSymmetricKey(SecretKey secretKey, String alias) {

		SecretKeyEntry secret = new SecretKeyEntry(secretKey);
		ProtectionParameter password = new PasswordProtection(keystorePass.toCharArray());
		try {
			keyStore.setEntry(alias, secret, password);
			keyStore.store(null, keystorePass.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.keymanager.spi.SofthsmKeystore#deleteKey(java.lang.
	 * String)
	 */
	@Override
	public void deleteKey(String alias) {
		try {
			keyStore.deleteEntry(alias);
		} catch (KeyStoreException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}
	}
	

	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}
}
