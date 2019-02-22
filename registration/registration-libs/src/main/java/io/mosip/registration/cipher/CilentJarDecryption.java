package io.mosip.registration.cipher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

	/**
	 * Decrypt the bytes
	 * 
	 * @param Jar
	 *            bytes
	 * @throws UnsupportedEncodingException
	 */
	public byte[] decrypt(byte[] data, byte[] encodedString) {
		// Generate AES Session Key
		SecretKey symmetricKey = new SecretKeySpec(encodedString, AES_ALGORITHM);

		return Base64.getEncoder().encode(SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING,
				symmetricKey, data, Cipher.DECRYPT_MODE));
	}

	/**
	 * Decrypt and save the file in temp directory
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("reg-application");
		//TODO: write the decryption logic and save the jar file to the temp directory.
	}
}
