package io.mosip.registration.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.util.FileUtils;

/**
 * Update the Application
 * 
 * @author YASWANTH S
 *
 */
@Component
public class RegistrationApp {

	private static String backUpPath = "D://mosip/AutoBackUp";

	private static String folderSeperator = "/";

	private static String buildVersion = "0.9.6";
	private static String zipFileFormat = "mosip-sw-";
	private static String zipFile = ".zip";

	private static String manifestFile = "MANIFEST.MF";

	// TODO move to application.properties
	private static String serverMosipZipFileUrl = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client"
			+ folderSeperator + buildVersion + folderSeperator + zipFileFormat + buildVersion + zipFile;

	private static String serverMosipManifestFileUrl = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client"
			+ folderSeperator + buildVersion + folderSeperator + manifestFile;

	/**
	 * Check whether the current application has an update or not
	 * 
	 * @return response
	 */
	public static boolean hasUpdate() {

		try {

			File userDir = getCurrentDirectoryFile();
			File localManifestFile = new File(
					userDir.getParentFile().getParentFile().getParentFile() + folderSeperator + manifestFile);

			if (localManifestFile.exists()) {
				Manifest localManifest = new Manifest(new FileInputStream(localManifestFile));

				// Download manifest file from server */
				Manifest serverManifest = new Manifest(new URL(serverMosipManifestFileUrl).openStream());

				Map<String, Attributes> localAttributes = localManifest.getEntries();
				Map<String, Attributes> serverAttributes = serverManifest.getEntries();

				List<String> removableJars = new LinkedList<>();
				List<String> updateJars = new LinkedList<>();
				List<String> downloadJars = new LinkedList<>();

				// Compare local and server Manifest
				for (Entry<String, Attributes> jar : localAttributes.entrySet()) {
					if (!serverAttributes.containsKey(jar.getKey())) {

						/* unnecessary jar after update */
						removableJars.add(jar.getKey());

					} else {
						Attributes localAttribute = jar.getValue();
						Attributes serverAttribute = serverAttributes.get(jar.getKey());
						if (!localAttribute.getValue(Attributes.Name.CONTENT_TYPE)
								.equals(serverAttribute.getValue(Attributes.Name.CONTENT_TYPE))) {

							/* Jar to be downloaded */
							updateJars.add(jar.getKey());

						}
						serverManifest.getEntries().remove(jar.getKey());

					}
				}

				for (Entry<String, Attributes> jar : serverAttributes.entrySet()) {
					downloadJars.add(jar.getKey());
				}

				if (!updateJars.isEmpty() || !removableJars.isEmpty() || !downloadJars.isEmpty()) {
					return true;
				} else {
					return false;
				}
			} else {
				/*
				 * As current application does not maintain manifest file, it was elligible to
				 * update
				 */

				return false;
			}

		} catch (MalformedURLException malformedURLException) {
			malformedURLException.printStackTrace();
			return false;
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}

		
	}

	/**
	 * Update the current application
	 * 
	 * @return response
	 * @throws MalformedURLException
	 */
	public static void updateApplication() {

		try {
			downloadLatestApplication();
		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
		}

		

	}

	private static void downloadLatestApplication() throws RuntimeException {

		URL mosipZipUrl;

		try {
			mosipZipUrl = new URL(serverMosipZipFileUrl);

			File userDir = new File(System.getProperty("user.dir"));

			File mosipZipFile = new File(userDir.getParentFile().getParentFile().getAbsolutePath() + folderSeperator
					+ FilenameUtils.getName(mosipZipUrl.getPath()));

			ResponseEntity<byte[]> response = new RestTemplate().getForEntity(serverMosipZipFileUrl, byte[].class);

			try (FileOutputStream fileOutputStream = new FileOutputStream(mosipZipFile)) {

				/* TODO compare bytes and confirm whether to save it or not */
				
				File currentMosipZipFile = new File(
						getCurrentDirectoryFile().getParentFile().getAbsolutePath() + ".zip");

				if (currentMosipZipFile.exists()) { // TODO Back up current mosip zip file
					backUpCurrentApplication(currentMosipZipFile);
				}
				
				//moveToFolder(currentMosipZipFile.getParentFile());

				fileOutputStream.write(response.getBody());
				File mosipExtractFileDir = new File(userDir.getParentFile().getParentFile().getAbsolutePath()
						+ folderSeperator + FilenameUtils.getBaseName(mosipZipUrl.getFile()));

				extractAll(mosipZipFile.getAbsolutePath(), mosipExtractFileDir);
			}

			catch (IOException exception) {
				exception.printStackTrace();
			}
		} catch (MalformedURLException malformedURLException) {

			malformedURLException.printStackTrace();
		}
	}

	private static void extractAll(String zipFilePath, File mosipExtractFileDir) {
		try {
			/* mosip zip file */
			ZipFile zipFile = new ZipFile(zipFilePath);

			/* mosip zip entries */
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File file = new File(mosipExtractFileDir.getAbsolutePath() + folderSeperator + entry.getName());

				InputStream inputStream;

				inputStream = zipFile.getInputStream(entry);

				if (inputStream.read() != -1) {
					/* If file/folder is not empty */
					org.apache.commons.io.FileUtils.copyToFile(inputStream, file);

				} else {
					file.mkdir();
				}

			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static void backUpCurrentApplication(File currentZipFile) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String date = timestamp.toString().replace(":", "-") + "Z";

		File backUpFolder = new File(backUpPath + folderSeperator + date + folderSeperator + currentZipFile.getName());
		try {

			FileUtils.copyFile(currentZipFile, backUpFolder);
		} catch (io.mosip.kernel.core.exception.IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private static void moveToFolder(File dest) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String date = timestamp.toString().replace(":", "-") + "Z";

		File backUpFolder = new File(backUpPath + folderSeperator + date + folderSeperator);
		backUpFolder.mkdir();
		try {
			org.apache.commons.io.FileUtils.moveDirectory(dest,backUpFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*private void replaceCurrentApplicationWithUpdate() {

	}*/

	private static File getCurrentDirectoryFile() {
		return new File(System.getProperty("user.dir"));
	}

}
