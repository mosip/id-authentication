package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.crypto.jce.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.util.CryptoUtils;

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
	private static final String MOSIP_EXE_JAR = "bin/run.jar";
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

	private SecureRandom secureRandom;

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

		return symmetricEncrypt(symmetricSecretKey, data, null);
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

					// Executable jar run.jar
					fileNameByBytes.put(MOSIP_EXE_JAR, runExecutbale);

					// Bat file run.bat
					fileNameByBytes.put(MOSIP_RUN_BAT, FileUtils.readFileToByteArray(new File(args[9])));
					readDirectoryToByteArray(MOSIP_JRE, new File(args[8]), fileNameByBytes);

					// Certificate file
					File mosipCertificateFile = new File(args[4]);

					if (mosipCertificateFile.exists()) {
						fileNameByBytes.put(MOSIP_CER + SLASH + mosipCertificateFile.getName(),
								FileUtils.readFileToByteArray(mosipCertificateFile));
					}

					// Add mosip-Version to mosip-application.properties file
					addProperties(new File(args[10]), args[2]);

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
			} catch(InvalidKeyException invalidKeyException) {
				invalidKeyException.printStackTrace();
			}
		}
	}

	private static void addProperties(File file, String version) throws IOException {
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

	private static byte[] symmetricEncrypt(SecretKey key, byte[] data, byte[] aad) {
		Objects.requireNonNull(key, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		byte[] output = null;

		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

			byte[] randomIV = generateIV(cipher.getBlockSize());
			SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), AES_ALGORITHM);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, randomIV);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
			output = new byte[cipher.getOutputSize(data.length) + cipher.getBlockSize()];
			if (aad != null && aad.length != 0) {
				cipher.updateAAD(aad);
			}
			byte[] processData = doFinal(data, cipher);
			System.arraycopy(processData, 0, output, 0, processData.length);
			System.arraycopy(randomIV, 0, output, processData.length, randomIV.length);
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage(), e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorMessage(), e);
		} catch (java.security.NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage(),
					noSuchAlgorithmException);
		} catch (NoSuchPaddingException noSuchPaddingException) {
			throw new InvalidKeyException("No Such Padding Exception", "No Such Padding Exception",
					noSuchPaddingException);

		}
		return output;
	}

	/**
	 * Generator for IV(Initialisation Vector)
	 * 
	 * @param blockSize
	 *            blocksize of current cipher
	 * @return generated IV
	 */
	private static byte[] generateIV(int blockSize) {
		byte[] byteIV = new byte[blockSize];
		new SecureRandom().nextBytes(byteIV);
		return byteIV;
	}

	private static byte[] doFinal(byte[] data, Cipher cipher) {
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException e) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_DATA_SIZE_EXCEPTION.getErrorCode(), e.getMessage(), e);
		} catch (BadPaddingException e) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
	}

}