package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

	private static final String ALGORITHM = "AES";
	private static final String ALGORITHM_ENCRYPTION_MODE = "AES/CBC/PKCS5Padding";
	private static final String SECRET_KEY_ALGO_ENCRYPTION_MODE = "PBKDF2WithHmacSHA1";
	private static final String PASSWORD = "m0sip.io@m@9!%^Y+{_-*~Z,]";
	private static final String SALT = "0Mindtree@1046113#";
	private static final int PWD_ITERATIONS = 65536;
	private static final int KEY_SIZE = 256;

	public static String encrypt(String path) throws Exception {
		byte[] saltBytes = SALT.getBytes("UTF-8");
		SecretKey secret = generateSecretKey(saltBytes);
		Cipher cipher = Cipher.getInstance(ALGORITHM_ENCRYPTION_MODE);
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		writeFiles("src/main/resources/iv.enc", params.getParameterSpec(IvParameterSpec.class).getIV());
		return processFile(cipher, path);
	}

	public static String decrypt(String path) throws Exception {
		byte[] saltBytes = SALT.getBytes("UTF-8");
		byte[] iv = readFiles("src/main/resources/iv.enc", 16);
		SecretKey secret = generateSecretKey(saltBytes);
		Cipher cipher = Cipher.getInstance(ALGORITHM_ENCRYPTION_MODE);
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		return processFile(cipher, path);
	}

	private static SecretKey generateSecretKey(byte[] salt)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGO_ENCRYPTION_MODE);
		KeySpec keySpec = new PBEKeySpec(PASSWORD.toCharArray(), salt, PWD_ITERATIONS, KEY_SIZE);
		SecretKey secretKey = factory.generateSecret(keySpec);
		SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
		return secret;
	}

	private static byte[] generateSalt() throws IOException {
		byte[] salt = new byte[8];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(salt);
		writeFiles("src/main/resources/salt.enc", salt);
		return salt;
	}

	private static void writeFiles(String fileNameWithPath, byte[] dataBytes) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileNameWithPath);
		fos.write(dataBytes);
		fos.flush();
		fos.close();
	}

	private static byte[] readFiles(String fileNameWithPath, int byteSize) throws IOException {
		FileInputStream saltFis = new FileInputStream(fileNameWithPath);
		byte[] salt = new byte[byteSize];
		saltFis.read(salt);
		saltFis.close();
		return salt;
	}

	private static String processFile(Cipher cipher, String inFile)
			throws FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {
		FileInputStream fis = new FileInputStream(inFile);
		File tempFile = createTempDirectory();
		String outPutFile = tempFile.getAbsolutePath() + "/" + UUID.randomUUID().toString() + ".jar";
		FileOutputStream fos = new FileOutputStream(outPutFile);
		byte[] input = new byte[64];
		int bytesRead;
		while ((bytesRead = fis.read(input)) != -1) {
			byte[] output = cipher.update(input, 0, bytesRead);
			if (output != null)
				fos.write(output);
		}
		byte[] output = cipher.doFinal();
		if (output != null)
			fos.write(output);
		fis.close();
		fos.flush();
		fos.close();
		return outPutFile;
	}

	public static File createTempDirectory() throws IOException {
		final File temp;
		temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		}
		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		}
		return (temp);
	}
}
