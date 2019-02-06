package io.mosip.registration.cipher;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;

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
	 * @param Jar
	 *            bytes
	 * @throws UnsupportedEncodingException
	 */
	public byte[] encyrpt(byte[] data, byte[] encodedString) throws UnsupportedEncodingException {
		// Generate AES Session Key
		SecretKey symmetricKey = new SecretKeySpec(encodedString, "AES");

		return Base64.getEncoder().encode(SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING, symmetricKey, data,
				Cipher.ENCRYPT_MODE));
	}

	/**
	 * Encrypt and save the file in client module
	 * 
	 * args[0]/args[1] --> To provide the ciennt jar 
	 * args[2] --> Secret key String
	 * args[3] --> project version
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			byte[] fileByteArray = null;

			if (new File(args[0]).exists()) {
				fileByteArray = FileUtils.readFileToByteArray(new File(args[0]));
			} else if (new File(args[1]).exists()) {
				fileByteArray = FileUtils.readFileToByteArray(new File(args[1]));
			}

			if (fileByteArray != null) {
				System.out.println("File Path :::" + args[0]);
				System.out.println("File Path :::" + args[1]);
				System.out.println("Key:::" + args[2]);
				System.out.println("version :::" + args[3]);
				
				String encryptedFileToSave = args[0].substring(0, args[0].lastIndexOf("/")) + "/registration-client-" + args[3] + "-encrypted.jar";
				ClientJarEncryption aes = new ClientJarEncryption();
				byte[] encryptedFileBytes = aes.encyrpt(fileByteArray, Base64.getDecoder().decode(args[2].getBytes()));
				FileUtils.writeByteArrayToFile(new File(encryptedFileToSave), encryptedFileBytes);
				
				System.out.println("File Path created :::" + encryptedFileToSave);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}