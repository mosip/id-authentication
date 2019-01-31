package io.mosip.demo.authentication.service.EncryptHelper;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 * 
 * @author Arun Bose S
 * The Class EncryptUtil.
 */
public class EncryptUtil {

	/** The Constant AESPADDING. */
	private static final String AESPADDING = "AES/CBC/PKCS5Padding";

	/** The Constant RSAPADDING. */
	private static final String RSAPADDING = "RSA/ECB/PKCS1Padding";

	/** The Constant SYM_ALGORITHM. */
	private static final String SYM_ALGORITHM = "AES";
	
	/** The Constant ASYM_ALGORITHM. */
	private static final String ASYMTRC_ALGORITHM = "RSA";

	/** The Constant SYM_ALGORITHM_LENGTH. */
	private static final int SYM_ALGORITHM_LENGTH = 256;

	/** The bouncy castle provider. */
	private static BouncyCastleProvider bouncyCastleProvider;

	static {
		bouncyCastleProvider = addProvider();
	}

	/** The secure random. */
	private static SecureRandom secureRandom;

	/**
	 * Symmetric encrypt.
	 *
	 * @param data the data
	 * @param secretKey the secret key
	 * @return the byte[]
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws NoSuchPaddingException the no such padding exception
	 * @throws InvalidKeyException the invalid key exception
	 * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
	 * @throws IllegalBlockSizeException the illegal block size exception
	 * @throws BadPaddingException the bad padding exception
	 */
	public byte[] symmetricEncrypt(byte[] data, SecretKey secretKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(EncryptUtil.AESPADDING);
		byte[] randomIV = generateIV(cipher.getBlockSize());
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(randomIV), secureRandom);
		byte[] identityEncryptedValue = new byte[cipher.getOutputSize(data.length) + cipher.getBlockSize()];
		byte[] processData = cipher.doFinal(data);
		System.arraycopy(processData, 0, identityEncryptedValue, 0, processData.length);
		System.arraycopy(randomIV, 0, identityEncryptedValue, processData.length, randomIV.length);
		return identityEncryptedValue;

	}
	
	
	public SecretKey asymmetricDecrypt(PrivateKey privateKey, byte[] encryptedSecretKeyByteArr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	   Cipher  cipher=Cipher.getInstance(EncryptUtil.RSAPADDING);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		 byte[] decryptedSecretKeyByteArr=cipher.doFinal(encryptedSecretKeyByteArr,0,encryptedSecretKeyByteArr.length);
	      return new SecretKeySpec(decryptedSecretKeyByteArr, 0, decryptedSecretKeyByteArr.length,EncryptUtil.SYM_ALGORITHM);	 
	}
	
	
	public byte[] symmetricDecrypt(SecretKey secretKey, byte[] encryptedDataByteArr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
	   Cipher  cipher=Cipher.getInstance(EncryptUtil.AESPADDING);
	   cipher.init(Cipher.DECRYPT_MODE, secretKey,
				new IvParameterSpec(Arrays.copyOfRange(encryptedDataByteArr, encryptedDataByteArr.length - cipher.getBlockSize(), encryptedDataByteArr.length)),
				secureRandom);
	   byte[] dataArr=cipher.doFinal(Arrays.copyOf(encryptedDataByteArr, encryptedDataByteArr.length - cipher.getBlockSize()));
	   return dataArr;
	}

	/**
	 * Adds the provider.
	 *
	 * @return the bouncy castle provider
	 */
	private static BouncyCastleProvider addProvider() {
		BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
		Security.addProvider(bouncyCastleProvider);
		return bouncyCastleProvider;
	}

	/**
	 * Generate IV.
	 *
	 * @param blockSize the block size
	 * @return the byte[]
	 */
	private byte[] generateIV(int blockSize) {
		secureRandom = new SecureRandom();
		byte[] randomBytes = new byte[blockSize];
		secureRandom.nextBytes(randomBytes);
		return randomBytes;
	}

	/**
	 * Gen sec key.
	 *
	 * @return the secret key
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public SecretKey genSecKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen;
		SecretKey secretKey = null;
        keyGen = KeyGenerator.getInstance(EncryptUtil.SYM_ALGORITHM, bouncyCastleProvider);
		keyGen.init(EncryptUtil.SYM_ALGORITHM_LENGTH, new SecureRandom());
		secretKey = keyGen.generateKey();
        return secretKey;

	}

	/**
	 * Asymmetric encrypt.
	 *
	 * @param data the data
	 * @param publicKey the public key
	 * @return the byte[]
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws NoSuchPaddingException the no such padding exception
	 * @throws InvalidKeyException the invalid key exception
	 * @throws IllegalBlockSizeException the illegal block size exception
	 * @throws BadPaddingException the bad padding exception
	 */
	public byte[] asymmetricEncrypt(byte[] data, PublicKey publicKey) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] sessionKeyEncryptedValue = null;
		Cipher cipher = Cipher.getInstance(EncryptUtil.RSAPADDING);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		sessionKeyEncryptedValue = cipher.doFinal(data, 0, data.length);

		return sessionKeyEncryptedValue;
	}

}
