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
	private static final String SLASH = "/";
	private static final String AES_ALGORITHM = "AES";

	/**
	 * Encrypt the bytes
	 * 
	 * @param Jar
	 *            bytes
	 * @throws UnsupportedEncodingException
	 */
	public byte[] encyrpt(byte[] data, byte[] encodedString) {
		// Generate AES Session Key
		SecretKey symmetricKey = new SecretKeySpec(encodedString, AES_ALGORITHM);

		return Base64.getEncoder().encode(SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING,
				symmetricKey, data, Cipher.ENCRYPT_MODE));
	}

	/**
	 * Encrypt and save the file in client module
	 * 
	 * args[0]/args[1] --> To provide the ciennt jar args[2] --> Secret key String
	 * args[3] --> project version
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		if (args != null && args.length > 2) {
			File file = args[0] != null ? new File(args[0]) : (args[1] != null ? new File(args[1]) : null);

			if (file != null && file.exists()) {
				byte[] fileByteArray = FileUtils.readFileToByteArray(file);
				String encryptedFileToSave = file.getParent() + SLASH + file.getName().replaceAll(".jar", "")
						+ "-encrypted.jar";
				ClientJarEncryption aes = new ClientJarEncryption();
				byte[] encryptedFileBytes = aes.encyrpt(fileByteArray, Base64.getDecoder().decode(args[2].getBytes()));
				FileUtils.writeByteArrayToFile(new File(encryptedFileToSave), encryptedFileBytes);

				System.out.println("File Path created :::" + encryptedFileToSave);
			}
		}
	}
}