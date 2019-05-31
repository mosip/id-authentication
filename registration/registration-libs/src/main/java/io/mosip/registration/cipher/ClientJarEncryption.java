package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import io.mosip.kernel.core.util.HMACUtils;
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
	private static final String REGISTRATION = "registration";
	private static final String MOSIP_APPLICATION_PROPERTIES_PATH = "props/mosip-application.properties";
	private static final String MOSIP_EXE_JAR = "run.jar";
	private static final String MOSIP_LIB = "lib";
	private static final String MOSIP_DB = "db";
	private static final String MOSIP_ZIP = ".zip";
	private static final String MOSIP_JAR = ".jar";
	private static final String MOSIP_LOG_PARAM = "mosip.logpath= ";
	private static final String MOSIP_DB_PARAM = "mosip.dbpath= ";
	private static final String MOSIP_ENV_PARAM = "mosip.env= ";
	private static final String MOSIP_CLIENT_URL = "mosip.client.url=";
	private static final String MOSIP_XML_FILE_URL = "mosip.xml.file.url=";
	private static final String MOSIP_PACKET_STORE_PARAM = "mosip.packetstorepath= ";
	private static final String MOSIP_PACKET_STORE_PATH = "../PacketStore";
	private static final String MOSIP_LOG_PATH = "../logs";
	private static final String MOSIP_DB_PATH = "db/reg";
	private static final String MOSIP_ENV_VAL = "qa";
	private static final String MOSIP_REG_LIBS = "registration-libs-";
	private static final String MANIFEST_FILE_NAME = "MANIFEST";
	private static final String MANIFEST_FILE_FORMAT = ".MF";
	private static final String MOSIP_BIN = "bin";
	private static final String MOSIP_SERVICES = "mosip-services.jar";
	private static final String MOSIP_CLIENT = "mosip-client.jar";
	private static final String MOSIP_CER = "cer";
	private static final String MOSIP_CER_PARAM = "mosip.cerpath= ";
	private static final String MOSIP_CER_PATH = "/cer/";
	private static final String MOSIP_CLIENT_URL_VAL = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/";
	private static final String MOSIP_XML_FILE_URL_VAL = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/maven-metadata.xml";

	// For TPM
	private static final String MOSIP_CLIENT_DB_KEY = "mosip.registration.db.key = ";
	private static final String MOSIP_CLIENT_APP_KEY = "mosip.registration.app.key = ";
	private static final String MOSIP_CLIENT_DB_BOOT = "bW9zaXAxMjM0NQ==";
	private static final String MOSIP_CLIENT_TPM_AVAILABILITY = "mosip.client.tpm.registration = N";

	private static final String MOSIP_ROLLBACK_PATH_PARAM = "mosip.rollback.path= ";
	private static final String MOSIP_ROLLBACK_PATH = "D://mosip/AutoBackUp";

	private static final String MOSIP_JRE = "jre";

	private static final String MOSIP_RUN_BAT = "run.bat";

	/**
	 * Encrypt the bytes
	 * 
	 * @param Jar
	 *            bytes
	 * @throws UnsupportedEncodingException
	 */
	public byte[] encyrpt(byte[] data, byte[] encodedString) {
		// Generate AES Session Key
		SecretKey symmetricSecretKey = new SecretKeySpec(encodedString, AES_ALGORITHM);

		return SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING, symmetricSecretKey, data,
				Cipher.ENCRYPT_MODE, null);
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
			File file = (args[0] != null && new File(args[0]).exists() ? new File(args[0]) : null);

			File clientJar = new File(args[0]);

			try (FileOutputStream fileOutputStream = new FileOutputStream(
					new File(file.getParent() + SLASH + MANIFEST_FILE_NAME + MANIFEST_FILE_FORMAT))) {

				Manifest manifest = new Manifest();

				/* Add Version to Manifest */
				manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, args[2]);

				System.out.println("Zip Creation started");

				if (file != null && file.exists()) {
					String propertiesFile = MOSIP_APPLICATION_PROPERTIES_PATH;
					String libraries = MOSIP_LIB + SLASH;

					String zipFilename = file.getParent() + SLASH + "mosip-sw-" + args[2] + MOSIP_ZIP;

					byte[] runExecutbale = FileUtils
							.readFileToByteArray(new File(args[3] + MOSIP_REG_LIBS + args[2] + MOSIP_JAR));
					File listOfJars = new File(file.getParent() + SLASH + MOSIP_LIB).getAbsoluteFile();

					// Add files to be archived into zip file
					Map<String, byte[]> fileNameByBytes = new HashMap<>();

					// fileNameByBytes.put(encryptedFileToSave, encryptedFileBytes);
					fileNameByBytes.put(MOSIP_LIB + SLASH, new byte[] {});
					fileNameByBytes.put(MOSIP_BIN + SLASH, new byte[] {});

					//Executable jar run.jar
					fileNameByBytes.put(MOSIP_EXE_JAR, runExecutbale);

					//Bat file run.bat
					fileNameByBytes.put(MOSIP_RUN_BAT, FileUtils.readFileToByteArray(new File(args[9]).listFiles()[0]));

					readDirectoryToByteArray(MOSIP_JRE, new File(args[8]), fileNameByBytes);

					// Certificate file
					File mosipCertificateFile = new File(args[4]);

					if (mosipCertificateFile.exists()) {
						fileNameByBytes.put(MOSIP_CER + SLASH + mosipCertificateFile.getName(),
								FileUtils.readFileToByteArray(mosipCertificateFile));
					}

					byte[] propertiesBytes = (MOSIP_LOG_PARAM + MOSIP_LOG_PATH + "\n" + MOSIP_DB_PARAM + MOSIP_DB_PATH
							+ "\n" + MOSIP_ENV_PARAM + MOSIP_ENV_VAL + "\n" + MOSIP_CLIENT_URL + MOSIP_CLIENT_URL_VAL
							+ "\n" + MOSIP_ROLLBACK_PATH_PARAM + MOSIP_ROLLBACK_PATH + "\n" + MOSIP_XML_FILE_URL
							+ MOSIP_XML_FILE_URL_VAL + "\n" + MOSIP_PACKET_STORE_PARAM + MOSIP_PACKET_STORE_PATH + "\n"
							+ MOSIP_CER_PARAM + MOSIP_CER_PATH + SLASH + mosipCertificateFile.getName() + "\n"
							+ MOSIP_CLIENT_APP_KEY.concat(args[1]).concat("\n").concat(MOSIP_CLIENT_DB_KEY)
									.concat(MOSIP_CLIENT_DB_BOOT)
							+ "\n" + MOSIP_CLIENT_TPM_AVAILABILITY).getBytes();

					fileNameByBytes.put(propertiesFile, propertiesBytes);

					// DB file
					File regFolder = new File(args[5]);
					readDirectoryToByteArray(MOSIP_DB, regFolder, fileNameByBytes);

					/*
					 * // TODO temporary zip file System.out.println("Shaded Zip Started"); String
					 * shadedzipFilename = file.getParent() + SLASH + "mosip-sw-shaded-" + args[3] +
					 * MOSIP_ZIP; Map<String, byte[]> shadedZipFileBytes = new HashMap<>();
					 * readDirectoryToByteArray(null, regFolder, shadedZipFileBytes); File shadedJar
					 * = args[1] != null && new File(args[1]).exists() ? new File(args[1]) : new
					 * File(args[7]); shadedZipFileBytes.put(shadedJar.getName(),
					 * FileUtils.readFileToByteArray(shadedJar));
					 * aes.writeFileToZip(shadedZipFileBytes, shadedzipFilename);
					 * 
					 * System.out.println("Shaded Zip Created");
					 */

					String path = new File(args[3]).getPath();

					File regLibFile = new File(path + SLASH + libraries);
					regLibFile.mkdir();

					byte[] clientJarEncryptedBytes = aes.getEncryptedBytes(Files.readAllBytes(clientJar.toPath()),
							Base64.getDecoder().decode(args[1].getBytes()));

					String filePath = listOfJars.getAbsolutePath() + SLASH + MOSIP_CLIENT;

					try (FileOutputStream regFileOutputStream = new FileOutputStream(new File(filePath))) {
						regFileOutputStream.write(clientJarEncryptedBytes);

					}
					/* Add To Manifest */
					addToManifest(MOSIP_CLIENT, clientJarEncryptedBytes, manifest);

					// /* Save Client jar to registration-libs */
					// saveLibJars(clientJarEncryptedBytes, clientJar.getName(), regLibFile);

					File rxtxJarFolder = new File(args[7]);

					FileUtils.copyDirectory(rxtxJarFolder, listOfJars);

					// Adding lib files into map
					for (File files : listOfJars.listFiles()) {

						if (files.getName().contains(REGISTRATION)) {

							String regpath = files.getParentFile().getAbsolutePath() + SLASH;
							if (files.getName().contains("client")) {
								regpath += MOSIP_CLIENT;
							} else {
								regpath += MOSIP_SERVICES;
							}
							byte[] encryptedRegFileBytes = aes.encyrpt(FileUtils.readFileToByteArray(files),
									Base64.getDecoder().decode(args[1].getBytes()));
							// fileNameByBytes.put(libraries + files.getName(), encryptedRegFileBytes);

							File servicesJar = new File(regpath);
							try (FileOutputStream regFileOutputStream = new FileOutputStream(servicesJar)) {
								regFileOutputStream.write(encryptedRegFileBytes);
								files.deleteOnExit();

							}

							/* Add To Manifest */
							addToManifest(servicesJar.getName(), encryptedRegFileBytes, manifest);

							// saveLibJars(encryptedRegFileBytes, files.getName(), regLibFile);
						} else if (files.getName().contains("pom")
								|| (files.getName().contains("javassist-3.12.1.GA"))) {
							FileUtils.forceDelete(files);

						} else {
							// fileNameByBytes.put(libraries + files.getName(),
							// FileUtils.readFileToByteArray(files));

							/* Add To Manifest */
							addToManifest(files.getName(), Files.readAllBytes(files.toPath()), manifest);

							// saveLibJars(files, regLibFile);
						}
					}

					writeManifest(fileOutputStream, manifest);

					// Removed manifest file from zip content
					// fileNameByBytes.put(MANIFEST_FILE_NAME + MANIFEST_FILE_FORMAT,
					// FileUtils.readFileToByteArray(
					// new File(file.getParent() + SLASH + MANIFEST_FILE_NAME +
					// MANIFEST_FILE_FORMAT)));

					aes.writeFileToZip(fileNameByBytes, zipFilename);

					System.out.println("Zip Creation ended with path :::" + zipFilename);
				}
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

	private static void addToManifest(String fileName, byte[] bytes, Manifest manifest) {

		String hashText = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytes));

		Attributes attribute = new Attributes();
		attribute.put(Attributes.Name.CONTENT_TYPE, hashText);

		manifest.getEntries().put(fileName, attribute);

	}

	private static void writeManifest(FileOutputStream fileOutputStream, Manifest manifest) {

		try {

			manifest.write(fileOutputStream);

		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

	}

	/*
	 * private static void saveLibJars(File srcFile, File destFile) {
	 * 
	 * try {
	 * 
	 * String val =
	 * srcFile.getName().split("\\.")[srcFile.getName().split("\\.").length - 1]; if
	 * ("jar".equals(val)) { FileUtils.copyFileToDirectory(srcFile, destFile); } }
	 * catch (NullPointerException | IOException exception) {
	 * exception.printStackTrace(); }
	 * 
	 * }
	 */

	/*
	 * private static void saveLibJars(byte[] jarBytes, String srcFileName, File
	 * destDir) {
	 * 
	 * File file = new File(destDir.getAbsolutePath() + SLASH + srcFileName);
	 * 
	 * try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
	 * fileOutputStream.write(jarBytes); } catch (NullPointerException | IOException
	 * exception) { exception.printStackTrace(); }
	 * 
	 * }
	 */

	private byte[] getEncryptedBytes(byte[] jarBytes, byte[] decodeBytes) {
		return encyrpt(jarBytes, decodeBytes);
	}

	private static void readDirectoryToByteArray(String directory, File srcFile, Map<String, byte[]> fileNameByBytes)
			throws IOException {

		directory = directory != null ? directory + SLASH : "";
		if (srcFile.isDirectory()) {

			File[] listFiles = srcFile.listFiles();
			if (listFiles.length == 0) {
				fileNameByBytes.put(directory + srcFile.getName() + SLASH, new byte[] {});
			} else {
				for (File file : srcFile.listFiles()) {
					if (file.isDirectory()) {
						readDirectoryToByteArray(directory + srcFile.getName(), file, fileNameByBytes);
					} else {
						byte[] fileBytes = FileUtils.readFileToByteArray(file);
						fileBytes = fileBytes.length > 0 ? fileBytes : new byte[] {};
						fileNameByBytes.put(directory + srcFile.getName() + SLASH + file.getName(), fileBytes);
					}
				}
			}

		} else {
			byte[] fileBytes = FileUtils.readFileToByteArray(srcFile);
			fileBytes = fileBytes.length > 0 ? fileBytes : new byte[] {};

			fileNameByBytes.put(directory + srcFile.getName(), fileBytes);
		}

	}
}