package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
	private static final String MOSIP_EXE_JAR = "bin/run.jar";
	private static final String MDM_EXE_JAR = "mdm.jar";
	private static final String MDM_FOLDER = "mdm";
	private static final String MOSIP_LIB = "lib";
	private static final String MOSIP_DB = "db";
	private static final String MOSIP_ZIP = ".zip";
	private static final String MOSIP_JAR = ".jar";

	private static final String MOSIP_REG_LIBS = "registration-libs-";
	private static final String MANIFEST_FILE_NAME = "MANIFEST";
	private static final String MANIFEST_FILE_FORMAT = ".MF";
	private static final String MOSIP_BIN = "bin";
	private static final String MOSIP_SERVICES = "mosip-services.jar";
	private static final String MOSIP_CLIENT = "mosip-client.jar";
	private static final String MOSIP_CER = "cer";

	private static final String MOSIP_JRE = "jre";

	private static final String MOSIP_RUN_BAT = "run.bat";
	private static final String MOSIP_MDM_STSRT_BAT = "mdm_start.bat";
	private static final String MOSIP_MDM_STOP_BAT = "mdm_stop.bat";

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
					byte[] mdmExecutbale = FileUtils.readFileToByteArray(new File(args[11]));
					File listOfJars = new File(file.getParent() + SLASH + MOSIP_LIB).getAbsoluteFile();

					// Add files to be archived into zip file
					Map<String, byte[]> fileNameByBytes = new HashMap<>();

					// fileNameByBytes.put(encryptedFileToSave, encryptedFileBytes);
					fileNameByBytes.put(MOSIP_LIB + SLASH, new byte[] {});
					fileNameByBytes.put(MOSIP_BIN + SLASH, new byte[] {});

					// Executable jar run.jar
					fileNameByBytes.put(MOSIP_EXE_JAR, runExecutbale);

					fileNameByBytes.put(MDM_FOLDER + SLASH + MDM_EXE_JAR, mdmExecutbale);

					// Bat file run.bat
					fileNameByBytes.put(MOSIP_RUN_BAT, FileUtils.readFileToByteArray(new File(args[9])));
					fileNameByBytes.put(MOSIP_MDM_STSRT_BAT, FileUtils.readFileToByteArray(new File(args[12])));
					fileNameByBytes.put(MOSIP_MDM_STOP_BAT, FileUtils.readFileToByteArray(new File(args[13])));
					readDirectoryToByteArray(MOSIP_JRE, new File(args[8]), fileNameByBytes);

					// Certificate file
					File mosipCertificateFile = new File(args[4]);

					if (mosipCertificateFile.exists()) {
						fileNameByBytes.put(MOSIP_CER + SLASH + mosipCertificateFile.getName(),
								FileUtils.readFileToByteArray(mosipCertificateFile));
					}

					// Add mosip-Version to mosip-application.properties file
					addProperties(new File(args[10]), args[2]);
					fileNameByBytes.put(propertiesFile, FileUtils.readFileToByteArray(new File(args[10])));

					// DB file
					File regFolder = new File(args[5]);
					readDirectoryToByteArray(MOSIP_DB, regFolder, fileNameByBytes);

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

	private static void addProperties(File file, String version) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));

		properties.setProperty("mosip.reg.version", version);

		// Add mosip-Version to mosip-application.properties file
		try (FileOutputStream outputStream = new FileOutputStream(file)) {

			properties.store(outputStream, version);
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