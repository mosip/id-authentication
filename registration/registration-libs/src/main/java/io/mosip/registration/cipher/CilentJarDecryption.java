package io.mosip.registration.cipher;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.cmc.DecryptedPOP;

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
	private static final String REGISTRATION="registration";

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
		CilentJarDecryption aesDecrypt = new CilentJarDecryption();
		ResourceBundle resourceBundle = ResourceBundle.getBundle("reg-application");
		File file = new File("D:\\decryption\\mosip-sw-0.8.10\\lib");
		for (File files : file.listFiles()) {

			if (files.getName().contains(REGISTRATION)) {
				System.out.println("Decrypt File Name====>"+files.getName());
				byte[] encryptedRegFileBytes = aesDecrypt.decrypt(FileUtils.readFileToByteArray(files),
						Base64.getDecoder().decode( "fdHPgbFn5LZjPE8fX5S0UQ=="));
				
			}

		}

		// TODO: write the decryption logic and save the jar file to the temp directory.
	}
}
