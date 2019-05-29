package io.mosip.registration.cipher;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.AsymmetricProcessor;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;
import io.mosip.kernel.keygenerator.bouncycastle.util.KeyGeneratorUtils;

public class EncryptionUtility {
	private static final String PRE_QA_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4IxNWmHNTTIiDfhneGGroO57mmXhuAqV0oaoMkCMQfZwLFiFZcZkBoLux8BtHh-bpYvWTx9yp98ILS1OvdEwCZ_Jb4vJWNXrbSgEqxEI0RqpgbrMTa0cDKa_KR60nwWHbW2ekI_E4EZBgYeYxHgMa8IxNVXhrXxGbdlqMIGsz327qcVbP9xx5ZgCQ8QPwTAI2gaI1mVtwGLSKt3t5oT7K-VLEIZoBZ2D8KOBWLA6jtI1ApcHluqnNXtDTKlpD8TzhYnbIVkXlqMaQMV3AleCeyMX3So27Inm1uABlfxJlifofJoW9y32gEWfB61i-3BmJMtx1_tY8J8YIdgbQ_-YjQIDAQAB";
	private static final String input_data = "omsai";
	
	
	public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
		Security.setProperty("crypto.policy", "unlimited");
		System.out.println(Base64.encodeBase64String(encyrpt(input_data.getBytes())));
		//System.out.println(new String(Base64.getDecoder().decode("b21zYWk")));
	}
	
	public static byte[] encyrpt(byte[] data) throws InvalidKeySpecException, NoSuchAlgorithmException {
		// Generate AES Session Key
		SecretKey symmetricSecretKey = KeyGeneratorUtils.getKeyGenerator("AES", 256).generateKey();

		byte[] encryptedData = SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING, symmetricSecretKey, data,
				Cipher.ENCRYPT_MODE, null);
		
		PublicKey publicKey = EncryptionUtility.generatePublicKey(PRE_QA_PUBLIC_KEY.getBytes());
		
		byte[] rsaEncryptedKey =  AsymmetricProcessor.process(SecurityMethod.RSA_WITH_PKCS1PADDING, publicKey, symmetricSecretKey.getEncoded(),
				Cipher.ENCRYPT_MODE);
		
		return CryptoUtil.combineByteArray(encryptedData, rsaEncryptedKey, "#KEY_SPLITTER#");
	}
	
	public static PublicKey generatePublicKey(byte[] encodedKey)
			throws InvalidKeySpecException, NoSuchAlgorithmException {


		return KeyFactory
				.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(new String(encodedKey))));
	}
}
