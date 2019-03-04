package io.mosip.registration.cipher;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;

/**
 * Decryption the Client Jar with Symmetric Key
 * 
 * @author Omsai Eswar M.
 *
 */
public class CilentJarDecryption {
	private static final String SLASH = "/";
	private static final String AES_ALGORITHM = "AES";
	private static final String REGISTRATION = "registration";

	/**
	 * Decrypt the bytes
	 * 
	 * @param Jar bytes
	 * @throws UnsupportedEncodingException
	 */
	public byte[] decrypt(byte[] data, byte[] encodedString) {
		// Generate AES Session Key
		SecretKey symmetricKey = new SecretKeySpec(encodedString, AES_ALGORITHM);

		return SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING, symmetricKey, data,
				Cipher.DECRYPT_MODE);
	}

	/**
	 * Decrypt and save the file in temp directory
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		CilentJarDecryption aesDecrypt = new CilentJarDecryption();
		// ResourceBundle resourceBundle = ResourceBundle.getBundle("reg-application");
		String tempPath = FileUtils.getTempDirectoryPath();
		File file = new File("D:\\decryption\\mosip-sw-0.8.10\\bin\\mosip-client.jar");

		String source = "D:\\decryption\\mosip-sw-0.8.10\\lib";
		File srcDir = new File(source);

		System.out.println("Decrypt File Name====>" + file.getName());
		byte[] decryptedRegFileBytes = aesDecrypt.decrypt(FileUtils.readFileToByteArray(file),
				Base64.getDecoder().decode("bBQX230Wskq6XpoZ1c+Ep1D+znxfT89NxLQ7P4KFkc4="));

		FileUtils.writeByteArrayToFile(new File(tempPath + "/mosip/"+file.getName()), decryptedRegFileBytes);

		for (File files : srcDir.listFiles()) {

			byte[] decryptedReFileBytes = null;

			if (files.getName().contains(REGISTRATION)) {

				System.out.println("Decrypt File Name====>" + files.getName());
				decryptedReFileBytes = aesDecrypt.decrypt(FileUtils.readFileToByteArray(files),
						Base64.getDecoder().decode("bBQX230Wskq6XpoZ1c+Ep1D+znxfT89NxLQ7P4KFkc4="));

				FileUtils.writeByteArrayToFile(new File(tempPath + "/mosip/"+ files.getName()),
						decryptedReFileBytes);

			} /*else {
				FileUtils.writeByteArrayToFile(new File(tempPath + "/mosip/lib/" + files.getName()),
						FileUtils.readFileToByteArray(files));
			}*/

		}

	}
}
