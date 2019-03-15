package io.mosip.registration.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.dto.ResponseDTO;

/**
 * Update the Application
 * 
 * @author YASWANTH S
 *
 */
@Component
public class RegistrationApplication {

	// TODO move to application.properties
	private static String serverMosipZipFileUrl = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/0.9.2/mosip-sw-0.9.2.zip";
	private static String serverMosipManifestFileUrl = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/0.9.2/MANIFEST.MF";

	/**
	 * Check whether the current application has an update or not
	 * 
	 * @return response
	 */
	public ResponseDTO hasUpdate() {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			Manifest localManifest = null;

			// Download manifest file from server */
			Manifest serverManifest = new Manifest(new URL(serverMosipManifestFileUrl).openStream());

			Map<String, Attributes> localAttributes = localManifest.getEntries();
			Map<String, Attributes> serverAttributes = serverManifest.getEntries();

			List<String> removableJars = new LinkedList<>();
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
						downloadJars.add(jar.getKey());

					}
				}
			}

			if (!downloadJars.isEmpty() || !removableJars.isEmpty()) {
				/* TODO Set Success Response */
			} else {
				/* TODO Set Error Response */
			}
		} catch (MalformedURLException malformedURLException) {
			malformedURLException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return responseDTO;
	}

	/**
	 * Update the current application
	 * 
	 * @return response
	 * @throws MalformedURLException
	 */
	public ResponseDTO updateApplication(){

		ResponseDTO responseDTO = new ResponseDTO();

		
		downloadLatestApplication();

		// TODO Back up current mosip zip file
		backUpCurrentApplication();

		//TODO replace current zip file with downloaded zip file
		replaceCurrentApplicationWithUpdate();

		return responseDTO;
	}

	private void downloadLatestApplication() {

		URL mosipZipUrl;
		try {
			mosipZipUrl = new URL(serverMosipZipFileUrl);

			File userDir = new File(System.getProperty("user.dir"));

			File mosipZipFile = new File(userDir.getParentFile().getParentFile().getParentFile().getAbsolutePath() + "/"
					+ FilenameUtils.getName(mosipZipUrl.getPath()));

			FileUtils.copyInputStreamToFile(mosipZipUrl.openStream(), mosipZipFile);

			File mosipExtractFileDir = new File(
					userDir.getParentFile().getParentFile().getParentFile().getAbsolutePath() + "/"
							+ FilenameUtils.getBaseName(mosipZipUrl.getFile()));

			extractAll(mosipZipFile.getAbsolutePath(), mosipExtractFileDir);

		} catch (io.mosip.kernel.core.exception.IOException | IOException exception) {
			exception.printStackTrace();
		}
	}

	private void extractAll(String zipFilePath, File mosipExtractFileDir) {
		try {
			/* mosip zip file */
			ZipFile zipFile = new ZipFile(zipFilePath);
			
			/* mosip zip entries */
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File file = new File(mosipExtractFileDir.getAbsolutePath() + "/" + entry.getName());

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

	private void backUpCurrentApplication() {

	}

	private void replaceCurrentApplicationWithUpdate() {

	}

}
