package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;

/**
 * Encrypt the Client Jar with Symmetric Key
 * 
 * @author Omsai Eswar M.
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
		ClientJarEncryption aes = new ClientJarEncryption();
		if (args != null && args.length > 2) {
			File file = args[1] != null && new File(args[1]).exists() ? new File(args[1])
					: (args[0] != null && new File(args[0]).exists() ? new File(args[0]) : null);
			
			System.out.println("Zip Creation started");
			
			if (file != null && file.exists()) {
				String encryptedFileToSave = "bin/mosip-client.jar";
				String propertiesFile = "props/mosip-application.properties";
				String runFileName = "bin/mosip-exec.jar";


				String zipFilename = file.getParent() + SLASH + "mosip-sw-" + args[3] + ".zip";

				byte[] encryptedFileBytes = aes.encyrpt(FileUtils.readFileToByteArray(file), Base64.getDecoder().decode(args[2].getBytes()));
				byte[] propertiesBytes = ("mosip.logpath= " + "\n" + "mosip.dbpath= ").getBytes();
				byte[] runExecutbale = FileUtils.readFileToByteArray(new File(args[4] + "registration-libs-" + args[3] + ".jar" ));


				// Add files to be archived into zip file
				Map<String, byte[]> fileNameByBytes = new HashMap<>();

				fileNameByBytes.put(encryptedFileToSave, encryptedFileBytes);
				fileNameByBytes.put(propertiesFile, propertiesBytes);
				fileNameByBytes.put(runFileName, runExecutbale);
				fileNameByBytes.put("db/", new byte[] {});
				fileNameByBytes.put("lib/", new byte[] {});

				aes.writeFileToZip(fileNameByBytes, zipFilename);

				System.out.println("Zip Creation ended with path :::" + zipFilename);
			}
		}
	}

	/**
	 * Write file to zip.
	 * 
	 * @param files
	 * @param zipFilename
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void writeFileToZip(Map<String, byte[]> fileNameByBytes, String zipFilename) throws IOException {
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(new File(zipFilename)))) {

			fileNameByBytes.forEach((key, value) -> {
				ZipEntry zipEntry = new ZipEntry(key);
				try {
					zipOutputStream.putNextEntry(zipEntry);
					zipOutputStream.write(value);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			});
		}
	}
}