package io.mosip.authentication.service;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.entity.DataEncryptKeystoreRepository;

@Service
public class IDAService {

	private static final String HASH_ALGO = "SHA-256";
	
	private static final int GCM_NONCE_LENGTH = 12;
	
	private static final int GCM_AAD_LENGTH = 32;
	
	private static final String WRAPPING_TRANSFORMATION = "AES/ECB/NoPadding";
	
	private static final String PKCS11_KEY_STORE_TYPE = "PKCS11";

    private static final String KEY_ALIAS = "IDA";

    private static final char[] KEY_PROTECT = "kP@09Sv".toCharArray();
    
    private static final String AES_GCM_TRANSFORMATION = "AES/GCM/PKCS5Padding";

    private static final int GCM_TAG_LENGTH = 16;
    
    private static final int INT_BYTES_LEN = 4;

	@Autowired
	private DataEncryptKeystoreRepository repo;
	
	@Autowired
	private Provider provider;

	public byte[] encrypt(Integer id, String data) throws Exception {
		int randomKeyIndex = id % 1000;
		System.out.println("randomKeyIndex::" + randomKeyIndex);
		String encryptedKeyData = repo.findKeyById(id);
		Key secretKey = getDecryptedKey(encryptedKeyData);

		Key derivedKey = getDerivedKey(id, secretKey);

		SecureRandom sRandom = new SecureRandom();
		byte[] nonce = new byte[GCM_NONCE_LENGTH];
		byte[] aad = new byte[GCM_AAD_LENGTH];

		sRandom.nextBytes(nonce);
		sRandom.nextBytes(aad);

		byte[] encryptedData = doCipherOps(derivedKey, data.getBytes(), Cipher.ENCRYPT_MODE, nonce, aad);
		byte[] dbIndexBytes = getIndexBytes(randomKeyIndex);

		byte[] finalEncData = new byte[encryptedData.length + dbIndexBytes.length + GCM_AAD_LENGTH + GCM_NONCE_LENGTH];
		System.arraycopy(dbIndexBytes, 0, finalEncData, 0, dbIndexBytes.length);
		System.arraycopy(nonce, 0, finalEncData, dbIndexBytes.length, nonce.length);
		System.arraycopy(aad, 0, finalEncData, dbIndexBytes.length + nonce.length, aad.length);
		System.arraycopy(encryptedData, 0, finalEncData, dbIndexBytes.length + nonce.length + aad.length,
				encryptedData.length);
		return Base64.getEncoder().encode(finalEncData);
	}

	public byte[] decrypt(Integer id, String data) throws Exception {

        byte[] decodedData = Base64.getDecoder().decode(data);
        
        byte[] nonce = Arrays.copyOfRange(decodedData, INT_BYTES_LEN, GCM_NONCE_LENGTH + INT_BYTES_LEN);
        byte[] aad = Arrays.copyOfRange(decodedData, INT_BYTES_LEN + GCM_NONCE_LENGTH, GCM_AAD_LENGTH + GCM_NONCE_LENGTH + INT_BYTES_LEN);
        byte[] encryptedData = Arrays.copyOfRange(decodedData, INT_BYTES_LEN + GCM_NONCE_LENGTH + GCM_AAD_LENGTH, decodedData.length);

        String encryptedKeyData = repo.findKeyById(id);
        Key secretKey = getDecryptedKey(encryptedKeyData);

        Key derivedKey = getDerivedKey(id, secretKey);

        byte[] decryptedData = doCipherOps(derivedKey, encryptedData, Cipher.DECRYPT_MODE, nonce, aad);
        return decryptedData;

	}

	private Key getDecryptedKey(String encryptedKey) throws Exception {
		Cipher cipher = Cipher.getInstance(WRAPPING_TRANSFORMATION, provider);

		byte[] encryptedKeyData = Base64.getDecoder().decode(encryptedKey);
		cipher.init(Cipher.DECRYPT_MODE, getMasterKeyFromHSM());
		byte[] unwrappedKey = cipher.doFinal(encryptedKeyData, 0, encryptedKeyData.length);
		return new SecretKeySpec(unwrappedKey, 0, unwrappedKey.length, "AES");
	}

	private Key getDerivedKey(Integer id, Key key) throws Exception {
		byte[] idBytes = String.valueOf(id).getBytes();
		byte[] keyBytes = key.getEncoded();

		MessageDigest mDigest = MessageDigest.getInstance(HASH_ALGO);
		mDigest.update(idBytes, 0, idBytes.length);
		mDigest.update(keyBytes, 0, keyBytes.length);
		byte[] hashBytes = mDigest.digest();

		return new SecretKeySpec(hashBytes, 0, hashBytes.length, "AES");
	}

    private Key getMasterKeyFromHSM() throws Exception {
        
        KeyStore hsmStore = KeyStore.getInstance(PKCS11_KEY_STORE_TYPE, provider);
		hsmStore.load(null, "1234".toCharArray());
		if (hsmStore.isKeyEntry(KEY_ALIAS)){
            System.out.println("SoftHSM - Key Alias found in store, returning the key");
            KeyStore.SecretKeyEntry secretEntry = (KeyStore.SecretKeyEntry) hsmStore.getEntry(KEY_ALIAS, 
                                                        new KeyStore.PasswordProtection(KEY_PROTECT));
            return secretEntry.getSecretKey();
        }
        System.out.println("SoftHSM - Key Not found for the alias in HSM.");
        throw new Exception ("Key Not Found in HSM.");
    }

    private byte[] doCipherOps(Key key, byte[] data, int mode, byte[] nonce, byte[] aad) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        cipher.init(mode, key, gcmSpec);
        cipher.updateAAD(aad);
        byte[] processedData = cipher.doFinal(data, 0, data.length);
        return processedData;
    }

    private byte[] getIndexBytes(int randomIndex) {
        ByteBuffer byteBuff = ByteBuffer.allocate(INT_BYTES_LEN);
        byteBuff.putInt(randomIndex);
        return byteBuff.array();
    }

}
