package io.mosip.kernel.keymanager.softhsm.impl;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
import java.security.SecureRandom;
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

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import io.mosip.kernel.keymanager.softhsm.SofthsmKeystore;
import io.mosip.kernel.keymanager.softhsm.util.X509CertUtil;
import sun.security.pkcs11.SunPKCS11;
import sun.security.x509.X509CertImpl;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class SofthsmKeystoreImpl implements SofthsmKeystore {

	private static final Logger LOGGER = Logger.getLogger(SofthsmKeystoreImpl.class.getName());

	private static final String PKCS11 = "PKCS11";
	private static final String SOFTHSM2_CONF = "D:\\SoftHSM2\\etc\\softhsm2-demo.conf";
	private static final char[] KEYSTORE_PASS = "1234".toCharArray();
	private KeyStore keyStore;

	public SofthsmKeystoreImpl() {
		Provider provider = new SunPKCS11(SOFTHSM2_CONF);
		addProvider(provider);
		keyStore = getKeystoreInstance(provider);
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
			keyStore.load(null, KEYSTORE_PASS);
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
	public KeyStore getKeystoreInstance(Provider provider) {
		KeyStore mosipKeyStore = null;
		try {
			mosipKeyStore = KeyStore.getInstance(PKCS11, provider);
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
	public Key getKeyByAlias(String alias) {
		Key key = null;
		try {
			key = keyStore.getKey(alias, KEYSTORE_PASS);
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
				ProtectionParameter password = new PasswordProtection(KEYSTORE_PASS);
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
	public void createAsymmetricKey(String alias) {

		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			LOGGER.info(e.getMessage());
		}
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		X509CertImpl x509CertImpl = X509CertUtil.generateX509Certificate(keyPair);
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = x509CertImpl;

		PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(keyPair.getPrivate(), chain);
		ProtectionParameter password = new PasswordProtection(KEYSTORE_PASS);

		try {
			keyStore.setEntry(alias, privateKeyEntry, password);
			keyStore.store(null, KEYSTORE_PASS);
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
				ProtectionParameter password = new PasswordProtection(KEYSTORE_PASS);
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
	public void createSymmetricKey(String alias) {

		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			LOGGER.info(e.getMessage());
		}
		SecureRandom secureRandom = new SecureRandom();
		int keyBitSize = 256;
		keyGenerator.init(keyBitSize, secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();

		SecretKeyEntry secret = new SecretKeyEntry(secretKey);
		ProtectionParameter password = new PasswordProtection(KEYSTORE_PASS);
		try {
			keyStore.setEntry(alias, secret, password);
			keyStore.store(null, KEYSTORE_PASS);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			LOGGER.info(e.getMessage());
		}
	}

}
