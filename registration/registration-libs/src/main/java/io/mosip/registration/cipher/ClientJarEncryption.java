package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
	private static final String REGISTRATION="registration";
	private static final String MOSIP_CLIENT_JAR_PATH="bin/mosip-client.jar";
	private static final String MOSIP_APPLICATION_PROPERTIES_PATH="props/mosip-application.properties";
	private static final String MOSIP_EXE_JAR="bin/mosip-exec.jar";
	private static final String MOSIP_LIB="lib";
	private static final String MOSIP_DB="db";
	private static final String MOSIP_ZIP=".zip";
	private static final String MOSIP_JAR=".jar";
	private static final String MOSIP_LOG_PATH="mosip.logpath= ";
	private static final String MOSIP_DB_PATH="mosip.dbpath= ";
	private static final String MOSIP_REG_LIBS="registration-libs-";
	/**
	 * Encrypt the bytes
	 * 
	 * @param Jar bytes
	 * @throws UnsupportedEncodingException
	 */
	public byte[] encyrpt(byte[] data, byte[] encodedString) {
		// Generate AES Session Key
		SecretKey symmetricKey = new SecretKeySpec(encodedString, AES_ALGORITHM);

		return SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING,
				symmetricKey, data, Cipher.ENCRYPT_MODE);
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
				String encryptedFileToSave = MOSIP_CLIENT_JAR_PATH;
				String propertiesFile = MOSIP_APPLICATION_PROPERTIES_PATH;
				String runFileName = MOSIP_EXE_JAR;
				String libraries = MOSIP_LIB+SLASH;

				String zipFilename = file.getParent() + SLASH + "mosip-sw-" + args[3] + MOSIP_ZIP;

				byte[] encryptedFileBytes = aes.encyrpt(FileUtils.readFileToByteArray(file),
						Base64.getDecoder().decode(args[2].getBytes()));
				byte[] propertiesBytes = (MOSIP_LOG_PATH + "\n" + MOSIP_DB_PATH).getBytes();
				byte[] runExecutbale = FileUtils
						.readFileToByteArray(new File(args[4] + MOSIP_REG_LIBS + args[3] + MOSIP_JAR));
				File listOfJars = new File(file.getParent() + SLASH + MOSIP_LIB).getAbsoluteFile();

				// Add files to be archived into zip file
				Map<String, byte[]> fileNameByBytes = new HashMap<>();

				fileNameByBytes.put(encryptedFileToSave, encryptedFileBytes);
				fileNameByBytes.put(propertiesFile, propertiesBytes);
				fileNameByBytes.put(runFileName, runExecutbale);
				fileNameByBytes.put(MOSIP_DB+SLASH, new byte[] {});

				// Adding lib files into map
				for (File files : listOfJars.listFiles()) {

					
					if (files.getName().contains(REGISTRATION)) {
						byte[] encryptedRegFileBytes = aes.encyrpt(FileUtils.readFileToByteArray(files),
								Base64.getDecoder().decode(args[2].getBytes()));
						fileNameByBytes.put(libraries + files.getName(), encryptedRegFileBytes);
					} else {
						fileNameByBytes.put(libraries + files.getName(), FileUtils.readFileToByteArray(files));
					}
				}

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