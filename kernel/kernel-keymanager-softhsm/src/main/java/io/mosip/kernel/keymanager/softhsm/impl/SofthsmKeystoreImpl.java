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
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.keymanager.spi.SofthsmKeystore;
import io.mosip.kernel.keymanager.softhsm.util.X509CertUtil;
import sun.security.pkcs11.SunPKCS11;
import sun.security.x509.X509CertImpl;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class SofthsmKeystoreImpl implements SofthsmKeystore {

	private static final Logger LOGGER = Logger.getLogger(SofthsmKeystoreImpl.class.getName());

	private final String keystorePass;

	private final KeyStore keyStore;

	public SofthsmKeystoreImpl(@Value("${mosip.kernel.keymanager.softhsm.config-path}") String configPath,
			@Value("${mosip.kernel.keymanager.softhsm.keystore-type}") String keystoreType,
			@Value("${mosip.kernel.keymanager.softhsm.keystore-pass}") String keystorePass) {

		Provider provider = new SunPKCS11(configPath);
		addProvider(provider);
		this.keyStore = getKeystoreInstance(keystoreType, provider);
		this.keystorePass = keystorePass;
		loadKeystore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.softhsm.impl.MosipSoftHS#addProvider(java.security.Provider)
	 */
	@Override
	public void addProvider(Provider provider) {
		if (-1 == Security.addProvider(provider)) {
			LOGGER.info("could not add security provider");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.softhsm.impl.MosipSoftHS#loadKeystore(char[])
	 */
	@Override
	public void loadKeystore() {

		try {
			keyStore.load(null, keystorePass.toCharArray());
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			LOGGER.info(e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.softhsm.impl.MosipSoftHS#getKeystoreInstance(java.security.Provider)
	 */
	@Override
	public KeyStore getKeystoreInstance(String keystoreType, Provider provider) {
		KeyStore mosipKeyStore = null;
		try {
			mosipKeyStore = KeyStore.getInstance(keystoreType, provider);
		} catch (KeyStoreException e) {
			LOGGER.info(e.getMessage());
		}
		return mosipKeyStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.softhsm.impl.MosipSoftHS#getAllAlias(char[])
	 */
	@Override
	public List<String> getAllAlias() {
		Enumeration<String> enumeration = null;
		try {
			enumeration = keyStore.aliases();
		} catch (KeyStoreException e) {
			LOGGER.info(e.getMessage());
		}
		return Collections.list(enumeration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.keystore.MosipKeystore#getKeyByAlias(java.lang.String)
	 */
	@Override
	public Key getKey(String alias) {
		Key key = null;
		try {
			key = keyStore.getKey(alias, keystorePass.toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			LOGGER.info(e.getMessage());
		}
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.softhsm.impl.MosipSoftHS#getAsymmetricKey(java.lang.String,
	 * char[])
	 */
	@Override
	public PrivateKeyEntry getAsymmetricKey(String alias) {
		PrivateKeyEntry privateKeyEntry = null;
		try {
			if (keyStore.entryInstanceOf(alias, PrivateKeyEntry.class)) {
				ProtectionParameter password = new PasswordProtection(keystorePass.toCharArray());
				privateKeyEntry = (PrivateKeyEntry) keyStore.getEntry(alias, password);
			} else {
				LOGGER.info("alias does not exists");
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
			LOGGER.info(e.getMessage());
		}
		return privateKeyEntry;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.keystore.MosipKeystore#getPrivateKey(java.lang.String)
	 */
	@Override
	public PrivateKey getPrivateKey(String alias) {
		PrivateKeyEntry privateKeyEntry = getAsymmetricKey(alias);
		return privateKeyEntry.getPrivateKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.keystore.MosipKeystore#getPublicKey(java.lang.String)
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
	 * @see io.mosip.keystore.MosipKeystore#getCertificate(java.lang.String)
	 */
	@Override
	public Certificate getCertificate(String alias) {
		PrivateKeyEntry privateKeyEntry = getAsymmetricKey(alias);
		Certificate[] certificates = privateKeyEntry.getCertificateChain();
		return certificates[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.softhsm.impl.MosipSoftHS#storeAsymmetricKey(java.lang.String,
	 * char[])
	 */
	@Override
	public void storeAsymmetricKey(KeyPair keyPair, String alias) {

		X509CertImpl x509CertImpl = X509CertUtil.generateX509Certificate(keyPair);
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = x509CertImpl;

		PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(keyPair.getPrivate(), chain);
		ProtectionParameter password = new PasswordProtection(keystorePass.toCharArray());

		try {
			keyStore.setEntry(alias, privateKeyEntry, password);
			keyStore.store(null, keystorePass.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			LOGGER.info(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.softhsm.impl.MosipSoftHS#getSymmetricKey(java.lang.String,
	 * char[])
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
				LOGGER.info("alias does not exists");
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
			LOGGER.info(e.getMessage());
		}
		return secretKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.softhsm.impl.MosipSoftHS#storeSymmetricKey(java.lang.String,
	 * char[])
	 */
	@Override
	public void storeSymmetricKey(SecretKey secretKey, String alias) {

		SecretKeyEntry secret = new SecretKeyEntry(secretKey);
		ProtectionParameter password = new PasswordProtection(keystorePass.toCharArray());
		try {
			keyStore.setEntry(alias, secret, password);
			keyStore.store(null, keystorePass.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			LOGGER.info(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.keymanager.softhsm.SofthsmKeystore#deleteKey(java.lang.
	 * String)
	 */
	@Override
	public void deleteKey(String alias) {
		try {
			keyStore.deleteEntry(alias);
		} catch (KeyStoreException e) {
			LOGGER.info(e.getMessage());
		}
	}
}
