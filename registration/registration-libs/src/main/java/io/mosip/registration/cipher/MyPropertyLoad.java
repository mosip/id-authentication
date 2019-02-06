package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Properties;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class MyPropertyLoad {

	private static Properties properties;

	public static void main(String a[]) throws NoSuchAlgorithmException, InvalidKeySpecException {

		InputStream is = null;
		try {
			properties = new Properties();
			is = new FileInputStream(new File("D:/application.properties"));
			properties.load(is);
			System.out.println("Algorithm: " + properties.getProperty("ALGORITHM"));
			generateSecretKey();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static SecretKey generateSecretKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		System.out.println("Algorithm 1: " + properties.getProperty("SECRET_KEY_ALGO_ENCRYPTION_MODE"));
		byte[] salt = new byte[8];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(salt);
		SecretKeyFactory factory = SecretKeyFactory.getInstance(properties.getProperty("SECRET_KEY_ALGO_ENCRYPTION_MODE").toString());
		KeySpec keySpec = new PBEKeySpec(properties.getProperty("PASSWORD").toCharArray(), salt, 65536, 256);
		SecretKey secretKey = factory.generateSecret(keySpec);
		SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), properties.getProperty("ALGORITHM"));
		System.out.println(secret.toString());

		return secret;
	}
}