package io.mosip.authentication.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.entity.DataEncryptKeystoreRepository;
import io.mosip.authentication.entity.KeyAlias;
import io.mosip.authentication.entity.KeyAliasRepository;
import sun.security.pkcs11.SunPKCS11;

/**
 * The Class IdAuthSecurityManager.
 *
 * @author Manoj SP
 */
@SuppressWarnings("restriction")
@Component
public class IdAuthSecurityManager {

	@Value("${mosip.kernel.keymanager.softhsm.config-path}")
	private String configPath;

	@Value("${mosip.kernel.crypto.symmetric-algorithm-name}")
	private String aesGCMTransformation;

	@Value("${mosip.kernel.keymanager.softhsm.keystore-type}")
	private String keyStoreType;

	@Value("${mosip.kernel.keymanager.softhsm.keystore-pass}")
	private String keyStorePass;

	@Value("${application.id}")
	private String applicationId;

	@Value("${identity-cache.reference.id}")
	private String referenceId;

	private static final String HASH_ALGO = "SHA-256";

	private static final int GCM_NONCE_LENGTH = 12;

	private static final int GCM_AAD_LENGTH = 32;

	private static final String WRAPPING_TRANSFORMATION = "AES/ECB/NoPadding";

	private static final int GCM_TAG_LENGTH = 16;

	private static final int INT_BYTES_LEN = 4;

	/** The mapper. */
	@Autowired
	private Environment env;

	@Autowired
	private DataEncryptKeystoreRepository repo;

	@Autowired
	private KeyAliasRepository keyAliasRepository;

	private Provider provider;

	@PostConstruct
	public void getProvider() {
		Provider provider = new SunPKCS11(configPath);
		Security.addProvider(provider);
		this.provider = provider;
	}

	public byte[] encryptWithAES(String id, byte[] dataToEncrypt) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException,
			KeyStoreException, CertificateException, UnrecoverableEntryException, IOException {
		int saltModuloConstant = env.getProperty("ida.keygenerator.uin.salt.modulo", Integer.class);
		int randomKeyIndex = (Integer.parseInt(id) % saltModuloConstant);
		String encryptedKeyData = repo.findKeyById(Integer.parseInt(id));
		Key secretKey = getDecryptedKey(encryptedKeyData);

		Key derivedKey = getDerivedKey(Integer.parseInt(id), secretKey);

		SecureRandom sRandom = new SecureRandom();
		byte[] nonce = new byte[GCM_NONCE_LENGTH];
		byte[] aad = new byte[GCM_AAD_LENGTH];

		sRandom.nextBytes(nonce);
		sRandom.nextBytes(aad);

		byte[] encryptedData = doCipherOps(derivedKey, dataToEncrypt, Cipher.ENCRYPT_MODE, nonce, aad);
		byte[] dbIndexBytes = getIndexBytes(randomKeyIndex);

		byte[] finalEncData = new byte[encryptedData.length + dbIndexBytes.length + GCM_AAD_LENGTH + GCM_NONCE_LENGTH];
		System.arraycopy(dbIndexBytes, 0, finalEncData, 0, dbIndexBytes.length);
		System.arraycopy(nonce, 0, finalEncData, dbIndexBytes.length, nonce.length);
		System.arraycopy(aad, 0, finalEncData, dbIndexBytes.length + nonce.length, aad.length);
		System.arraycopy(encryptedData, 0, finalEncData, dbIndexBytes.length + nonce.length + aad.length,
				encryptedData.length);
		return Base64.getEncoder().encode(finalEncData);
	}

	public byte[] decryptWithAES(String id, byte[] dataToDecrypt) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException,
			KeyStoreException, CertificateException, UnrecoverableEntryException, IOException {
		byte[] decodedData = Base64.getDecoder().decode(dataToDecrypt);

		byte[] nonce = Arrays.copyOfRange(decodedData, INT_BYTES_LEN, GCM_NONCE_LENGTH + INT_BYTES_LEN);
		byte[] aad = Arrays.copyOfRange(decodedData, INT_BYTES_LEN + GCM_NONCE_LENGTH,
				GCM_AAD_LENGTH + GCM_NONCE_LENGTH + INT_BYTES_LEN);
		byte[] encryptedData = Arrays.copyOfRange(decodedData, INT_BYTES_LEN + GCM_NONCE_LENGTH + GCM_AAD_LENGTH,
				decodedData.length);

		String encryptedKeyData = repo.findKeyById(Integer.parseInt(id));
		Key secretKey = getDecryptedKey(encryptedKeyData);

		Key derivedKey = getDerivedKey(Integer.parseInt(id), secretKey);

		return doCipherOps(derivedKey, encryptedData, Cipher.DECRYPT_MODE, nonce, aad);
	}

	private Key getDecryptedKey(String encryptedKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, KeyStoreException, CertificateException, UnrecoverableEntryException, IOException {
		Cipher cipher = Cipher.getInstance(WRAPPING_TRANSFORMATION, provider);

		byte[] encryptedKeyData = Base64.getDecoder().decode(encryptedKey);
		cipher.init(Cipher.DECRYPT_MODE, getMasterKeyFromHSM());
		byte[] unwrappedKey = cipher.doFinal(encryptedKeyData, 0, encryptedKeyData.length);
		return new SecretKeySpec(unwrappedKey, 0, unwrappedKey.length, "AES");
	}

	private Key getDerivedKey(Integer id, Key key) throws NoSuchAlgorithmException {
		byte[] idBytes = String.valueOf(id).getBytes();
		byte[] keyBytes = key.getEncoded();

		MessageDigest mDigest = MessageDigest.getInstance(HASH_ALGO);
		mDigest.update(idBytes, 0, idBytes.length);
		mDigest.update(keyBytes, 0, keyBytes.length);
		byte[] hashBytes = mDigest.digest();

		return new SecretKeySpec(hashBytes, 0, hashBytes.length, "AES");
	}

	private Key getMasterKeyFromHSM() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableEntryException {
		KeyStore hsmStore = KeyStore.getInstance(keyStoreType, provider);
		hsmStore.load(null, keyStorePass.toCharArray());
		String keyAlias = getKeyAlias();
		if (hsmStore.isKeyEntry(keyAlias)) {
            System.out.println("SoftHSM - Key Alias found in store, returning the key");
			KeyStore.SecretKeyEntry secretEntry = (KeyStore.SecretKeyEntry) hsmStore.getEntry(keyAlias,
					new KeyStore.PasswordProtection(keyStorePass.toCharArray()));
			return secretEntry.getSecretKey();
		}
		throw new IllegalStateException("HSM - Key Not found for the alias in HSM.");
	}

	private String getKeyAlias() {
		List<KeyAlias> keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, referenceId)
				.stream().sorted((alias1, alias2) -> {
					return alias1.getKeyGenerationTime().compareTo(alias2.getKeyGenerationTime());
				}).collect(Collectors.toList());
		List<KeyAlias> currentKeyAliases = keyAliases.stream().filter((keyAlias) -> {
			return isValidTimestamp(LocalDateTime.now(), keyAlias);
		}).collect(Collectors.toList());

		if (!currentKeyAliases.isEmpty() && currentKeyAliases.size() == 1) {
			System.err.println("CurrentKeyAlias size is one. Will decrypt symmetric key for this alias");
			return currentKeyAliases.get(0).getAlias();
		}

		throw new IllegalStateException("CurrentKeyAlias is not unique. KeyAlias count: " + currentKeyAliases.size());
	}

	private boolean isValidTimestamp(LocalDateTime timeStamp, KeyAlias keyAlias) {
		return timeStamp.isEqual(keyAlias.getKeyGenerationTime()) || timeStamp.isEqual(keyAlias.getKeyExpiryTime())
				|| timeStamp.isAfter(keyAlias.getKeyGenerationTime())
						&& timeStamp.isBefore(keyAlias.getKeyExpiryTime());
	}

	private byte[] doCipherOps(Key key, byte[] data, int mode, byte[] nonce, byte[] aad)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(aesGCMTransformation);
		GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
		cipher.init(mode, key, gcmSpec);
		cipher.updateAAD(aad);
		return cipher.doFinal(data, 0, data.length);
	}

	private byte[] getIndexBytes(int randomIndex) {
		ByteBuffer byteBuff = ByteBuffer.allocate(INT_BYTES_LEN);
		byteBuff.putInt(randomIndex);
		return byteBuff.array();
	}
}
