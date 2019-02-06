package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;
import io.mosip.kernel.keygenerator.bouncycastle.util.KeyGeneratorUtils;

/**
 * Encrypt the Client Jar with Symmetric Key
 * 
 * @author M1045980
 *
 */
public class ClientJarEncryption {

	/**
	 * Encrypt the bytes
	 * 
	 * @param Jar bytes 
	 * @throws UnsupportedEncodingException 
	 */
	public void encyrpt(byte[] data) throws UnsupportedEncodingException {
		// Generate AES Session Key
		SecretKey symmetricKey = KeyGeneratorUtils.getKeyGenerator("AES", 256).generateKey();
		System.out.println(new String(symmetricKey.getEncoded()));
		// Encrypt the Data using AES
		byte[] encryptedBytes = SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING, symmetricKey,
				data, Cipher.ENCRYPT_MODE);
		//FileInputStream fis = new FileInputStream(inFile);
	}

	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream("../registration-client/target/registration-client.jar");
		ClientJarEncryption aes = new ClientJarEncryption();
		//aes.processFile(null, null);
		String filePath = "";
		aes.encyrpt(null);
	}
	
	private String processFile(Cipher cipher, String inFile)
			throws FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {
		
		FileInputStream fis = new FileInputStream("../registration-client/target/registration-client.jar");
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