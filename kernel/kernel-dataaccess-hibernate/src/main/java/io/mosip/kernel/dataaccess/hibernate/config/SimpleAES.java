package io.mosip.kernel.dataaccess.hibernate.config;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.dataaccess.hibernate.entity.SecreteKeyStore;
import io.mosip.kernel.dataaccess.hibernate.repository.impl.EncryptionDao;

@Component
public class SimpleAES {

	private static EncryptionDao encryptionDao;

	final static String secret = "Encryption";

	private static String index;

	@Autowired
	public SimpleAES(EncryptionDao encryptionDao) {
		SimpleAES.encryptionDao = encryptionDao;
	}

	private static SecretKeySpec secretKey;
	private static byte[] key;

	private static SecreteKeyStore keyStore;

	@PostConstruct
	public void initialKeyStore() {
		keyStore = encryptionDao.getKey();
		LocalDateTime localTime = LocalDateTime.now(ZoneId.of("UTC"));
		if (keyStore == null) {
			newKey(localTime.plusDays(2));
		} else {
			System.out.println(keyStore.getExpiryDate().compareTo(localTime));
			if (keyStore.getExpiryDate().compareTo(localTime) < 0) {
				keyStore.setExpired(true);
				encryptionDao.updateKey(keyStore);
				newKey(localTime.plusDays(2));
			} else {
				byte[] decodedKey = Base64.getDecoder().decode(keyStore.getKey());
				secretKey = new SecretKeySpec(decodedKey, 0,decodedKey.length, "AES");
				index = keyStore.getId();
			}

		}
	}

	public static void setKey() {
		MessageDigest sha = null;
		try {
			key = secret.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static SecretKeySpec getKey(String skey) {
		MessageDigest sha = null;
		try {
			key = skey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return secretKey;
	}

	public static void newKey(LocalDateTime d) {
		secretKey = getKey(secret);
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		SecreteKeyStore entity = new SecreteKeyStore();
		entity.setKey(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
		entity.setExpired(false);
		entity.setExpiryDate(d);
		entity.setCreateDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		entity.setCreatedBy("Rajath");
		entity.setGenratedtimes(LocalDateTime.now(ZoneId.of("UTC")));
		entity.setId(randomUUIDString);
		encryptionDao.saveKey(entity);
		keyStore = entity;
		index = entity.getId();
		System.out.println("entity  " + entity);
	}

	public static String encrypt(String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			LocalDateTime localTime = LocalDateTime.now(ZoneId.of("UTC"));
			if (keyStore.getExpiryDate().compareTo(localTime) < 0) {
				keyStore.setExpired(true);
				encryptionDao.updateKey(keyStore);
				newKey(localTime.plusDays(2));
			}
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return index + ":" + Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt) {
		try {

			String[] str = strToDecrypt.split(":");
			String in = str[0];
			SecreteKeyStore entity = encryptionDao.getKey(in);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			byte[] decodedKey = Base64.getDecoder().decode(entity.getKey());
			cipher.init(Cipher.DECRYPT_MODE,
					new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));
			String op = new String(cipher.doFinal(Base64.getDecoder().decode(str[1])));
			System.out.println("output " + op);
			return op;
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

}
