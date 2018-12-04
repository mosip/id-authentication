package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@RestController
public class SampleTestClass {

	@Autowired
	private KeyGenerator keyGenerator;

	@Autowired
	private EncryptorImpl encryptor;

	@Autowired
	private DecryptorImpl decryptor;

	private static final String FORMAT = "UTF-8";

	private static final Provider provider = new BouncyCastleProvider();

	@GetMapping(value = "/test")
	public void Test() throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
		SecretKey symmetricKey = keyGenerator.getSymmetricKey();
		byte[] sessionKey = symmetricKey.getEncoded();
		KeyPair asymmetricKey = keyGenerator.getAsymmetricKey();
		PublicKey publicKey = asymmetricKey.getPublic();
		PrivateKey privateKey = asymmetricKey.getPrivate();
		String data = "{ \"authType\": { \"address\": false, \"bio\": false, \"face\": false, \"fingerprint\": false, \"fullAddress\": false, \"iris\": false, \"otp\": true, \"personalIdentity\": false, \"pin\": false }, \"id\": \"mosip.identity.auth\", \"idvId\": \"426789089018\", \"idvIdType\": \"D\", \"key\": { \"publicKeyCert\": \"string\", \"sessionKey\": \"string\" }, \"muaCode\": \"0123456789\", \"pinInfo\": [ { \"type\": \"OTP\", \"value\": \"526206\" } ], \"reqHmac\": \"string\", \"reqTime\": \"2018-11-26T07:14:30.086+0000\", \"txnID\": \"1234567890\", \"ver\": \"1.0\" }";

		SecretKeySpec secretKey = null;
		secretKey = prepareSecretKey(sessionKey, "AES");
		// Encrypt data with session key
		byte[] encryptedData = encryptor.symmetricEncrypt(secretKey, data.getBytes());

		// Encrypt session key with public key
		byte[] encryptedKey = encryptor.asymmetricPublicEncrypt(publicKey, sessionKey);

		byte[] decryptedKey = decryptor.asymmetricPrivateDecrypt(privateKey, encryptedData);

//		SecretKey originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
//		
//		byte[] finalData = decryptor.symmetricDecrypt(originalKey, encryptedData);

//		byte[] decryptedData = decryptor.asymmetricPrivateDecrypt(privateKey, encryptedData);

//		System.err.println(decryptedData.toString());

	}

	private SecretKeySpec prepareSecretKey(byte[] sessionKey, String type)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digester;
		digester = MessageDigest.getInstance("SHA-256", provider);
		digester.update(String.valueOf(sessionKey).getBytes(FORMAT));
		byte[] key = digester.digest();
		SecretKeySpec spec = new SecretKeySpec(key, type);
		return spec;
	}

}
