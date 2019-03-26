package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Properties;

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

	static {
		String tempPath = System.getProperty("java.io.tmpdir");
		System.setProperty("java.ext.dirs", "D:/ManifestTesting/mosip-sw-0.9.6/lib/;" + tempPath + "/mosip/");

		System.out.println(System.getProperty("java.ext.dirs"));
	}

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

		return SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING, symmetricKey, data,
				Cipher.DECRYPT_MODE);
	}

	/**
	 * Decrypt and save the file in temp directory
	 * 
	 * @param args
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {

		CilentJarDecryption aesDecrypt = new CilentJarDecryption();

		File encryptedClientJar = new File(
				new File(System.getProperty("user.dir")).getAbsolutePath() + "/" + "mosip-client.jar");

		File encryptedServicesJar = new File(new File(System.getProperty("user.dir")).getParent() + "/" + "lib/"
				+ "registration-services-" + "0.9.6" + ".jar");

		String tempPath = FileUtils.getTempDirectoryPath();

		System.out.println("Decrypt File Name====>" + encryptedClientJar.getName());
		byte[] decryptedRegFileBytes = aesDecrypt.decrypt(FileUtils.readFileToByteArray(encryptedClientJar),
				Base64.getDecoder().decode("bBQX230Wskq6XpoZ1c+Ep1D+znxfT89NxLQ7P4KFkc4="));

		FileUtils.writeByteArrayToFile(new File(tempPath + "/mosip/" + encryptedClientJar.getName()),
				decryptedRegFileBytes);

		System.out.println("Decrypt File Name====>" + encryptedServicesJar.getName());
		byte[] decryptedRegServiceBytes = aesDecrypt.decrypt(FileUtils.readFileToByteArray(encryptedServicesJar),
				Base64.getDecoder().decode("bBQX230Wskq6XpoZ1c+Ep1D+znxfT89NxLQ7P4KFkc4="));

		FileUtils.writeByteArrayToFile(new File(tempPath + "/mosip/" + encryptedServicesJar.getName()),
				decryptedRegServiceBytes);

		ProcessBuilder clientBuilder = new ProcessBuilder("java", "-jar", tempPath + "/mosip/mosip-client.jar");

		Process process = clientBuilder.start();

		System.out.println("Invoked suuceessfully");

		int status = process.waitFor();
		if (status == 0) {
			System.out.println("Registration Client stopped with the status: " + status);
			process.destroy();
			FileUtils.deleteDirectory(new File(tempPath + "mosip\\"));
		}
	}

	private void setProperties() throws IOException {

		String propsFilePath = new File(System.getProperty("user.dir")).getParentFile()
				+ "/props/mosip-application.properties";

		FileInputStream fileInputStream = new FileInputStream(propsFilePath);
		Properties properties = new Properties();
		properties.load(fileInputStream);

		System.setProperty("reg.db.path", properties.getProperty("mosip.dbpath"));

	}
}