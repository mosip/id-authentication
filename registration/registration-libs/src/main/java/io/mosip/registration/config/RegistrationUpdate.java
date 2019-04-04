package io.mosip.registration.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.util.FileUtils;

/**
 * Update the Application
 * 
 * @author YASWANTH S
 *
 */
@Component
public class RegistrationUpdate {

	private static String backUpPath = "D://mosip/AutoBackUp";

	private static String SLASH = "/";

	private static String manifestFile = "MANIFEST.MF";

	// TODO move to application.properties
	private static String serverRegClientURL = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/";
	private static String serverMosipXmlFileUrl = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/maven-metadata.xml";

	private static String libFolder = "lib/";
	private static String binFolder = "bin/";

	private static String currentVersion;

	private static String latestVersion;

	private static Manifest localManifest;

	private static Manifest serverManifest;

	private String mosip = "mosip";

	private String versionTag = "version";

	public boolean hasUpdate() throws IOException, ParserConfigurationException, SAXException, NullPointerException {
		return !getCurrentVersion().equals(getLatestVersion());
	}

	private String getLatestVersion() throws IOException, ParserConfigurationException, SAXException {
		System.out.println("Getting latest Version");
		if (latestVersion != null) {
			return latestVersion;
		} else {

			// Get latest version using meta-inf.xml
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
			org.w3c.dom.Document metaInfXmlDocument = (org.w3c.dom.Document) db
					.parse(new URL(serverMosipXmlFileUrl).openStream());

			NodeList list = metaInfXmlDocument.getDocumentElement().getElementsByTagName(versionTag);
			if (list != null && list.getLength() > 0) {
				NodeList subList = list.item(0).getChildNodes();

				if (subList != null && subList.getLength() > 0) {
					latestVersion = subList.item(0).getNodeValue();
				}
			}

		}

		return latestVersion;
	}

	public String getCurrentVersion() throws IOException {
		if (currentVersion != null) {
			return currentVersion;
		} else {
			// Get Local manifest file
			Manifest localManifest = getLocalManifest();
			if (localManifest != null) {
				setCurrentVersion((String) localManifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
			}
		}
		System.out.println("Getting current Version  " + currentVersion);
		return currentVersion;
	}

	public void getWithLatestJars()
			throws IOException, ParserConfigurationException, SAXException, io.mosip.kernel.core.exception.IOException {
		Manifest local = getLocalManifest();
		Manifest server = getServerManifest();

		List<String> downloadJars = new LinkedList<>();
		List<String> deletableJars = new LinkedList<>();
		List<String> checkableJars = new LinkedList<>();

		Map<String, Attributes> localAttributes = local.getEntries();
		Map<String, Attributes> serverAttributes = server.getEntries();

		// Compare local and server Manifest
		for (Entry<String, Attributes> jar : localAttributes.entrySet()) {
			checkableJars.add(jar.getKey());
			if (!serverAttributes.containsKey(jar.getKey())) {

				/* unnecessary jar after update */
				deletableJars.add(jar.getKey());

			} else {
				Attributes localAttribute = jar.getValue();
				Attributes serverAttribute = serverAttributes.get(jar.getKey());
				if (!localAttribute.getValue(Attributes.Name.CONTENT_TYPE)
						.equals(serverAttribute.getValue(Attributes.Name.CONTENT_TYPE))) {

					/* Jar to be downloaded */
					downloadJars.add(jar.getKey());

				}
				server.getEntries().remove(jar.getKey());

			}
		}

		for (Entry<String, Attributes> jar : serverAttributes.entrySet()) {
			downloadJars.add(jar.getKey());
		}

		Path backUpPath = backUpCurrentApplication();

		try {
			deleteJars(deletableJars);

			// Un-Modified jars exist or not
			checkableJars.removeAll(deletableJars);
			checkableJars.removeAll(downloadJars);

			// Download latest jars if not in local
			checkJars(getLatestVersion(), downloadJars, true);
			checkJars(getLatestVersion(), checkableJars, false);

			replaceManifest();

		} catch (RuntimeException exception) {

			replaceBackupWithCurrentApplication(backUpPath);

			throw exception;
		}
	}

	private Path backUpCurrentApplication() throws IOException, io.mosip.kernel.core.exception.IOException {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String date = timestamp.toString().replace(":", "-") + "Z";

		File backUpFolder = new File(backUpPath + SLASH + getCurrentVersion() + "_" + date);

		// bin backup folder
		File bin = new File(backUpFolder.getAbsolutePath() + SLASH + binFolder);
		bin.mkdirs();

		// lib backup folder
		File lib = new File(backUpFolder.getAbsolutePath() + SLASH + libFolder);
		lib.mkdirs();

		// manifest backup file
		File manifest = new File(backUpFolder.getAbsolutePath() + SLASH + manifestFile);

		try {

			FileUtils.copyDirectory(new File(binFolder), bin);
			FileUtils.copyDirectory(new File(libFolder), lib);

			FileUtils.copyFile(new File(manifestFile), manifest);
		} catch (io.mosip.kernel.core.exception.IOException ioException) {
			throw ioException;
		}

		for (File backUpFile : new File(backUpPath).listFiles()) {
			if (!backUpFile.getAbsolutePath().equals(backUpFolder.getAbsolutePath())) {
				FileUtils.deleteDirectory(backUpFile);
			}
		}
		return backUpFolder.toPath();

	}

	private void replaceBackupWithCurrentApplication(Path currentApplicationbackUpPath)
			throws io.mosip.kernel.core.exception.IOException {

		File backUpFolder = currentApplicationbackUpPath.toFile();

		FileUtils.copyDirectory(new File(backUpFolder.getAbsolutePath() + SLASH + binFolder), new File(binFolder));
		FileUtils.copyDirectory(new File(backUpFolder.getAbsolutePath() + SLASH + libFolder), new File(libFolder));

		FileUtils.copyFile(new File(backUpFolder.getAbsolutePath() + SLASH + manifestFile), new File(manifestFile));

	}

	private void checkJars(String version, List<String> checkableJars, boolean isToBeDownloaded)
			throws IOException, io.mosip.kernel.core.exception.IOException {
		if (isToBeDownloaded) {
			deleteJars(checkableJars);
		}
		for (String jarFile : checkableJars) {
			if (jarFile.contains(mosip)) {
				checkForJarFile(version, binFolder, jarFile);
			} else {
				checkForJarFile(version, libFolder, jarFile);
			}
		}

	}

	private void checkForJarFile(String version, String folderName, String jarFileName) throws IOException {

		File jarInFolder = new File(folderName + jarFileName);
		if (!jarInFolder.exists()) {

			// Download Jar
			Files.copy(getInputStreamOfJar(version, jarFileName), jarInFolder.toPath());

		}
	}

	private static InputStream getInputStreamOfJar(String version, String jarName) throws IOException {
		System.out.println("Downloading " + jarName);
		// TODO No Internet Connection Please Try Again
		return new URL(serverRegClientURL + version + SLASH + libFolder + jarName).openStream();

	}

	private void deleteJars(List<String> deletableJars) {
		deletableJars.forEach(jarName -> {
			try {
				deleteJar(jarName);
			} catch (io.mosip.kernel.core.exception.IOException ioException) {
				throw new RuntimeException();
			}
		});

	}

	private void deleteJar(String jarName) throws io.mosip.kernel.core.exception.IOException {
		File deleteFile = null;
		if (jarName.contains(mosip)) {
			deleteFile = new File(binFolder + jarName);
		} else {
			deleteFile = new File(libFolder + jarName);
		}

		// Delete Jars
		if (deleteFile.exists()) {
			try {
				FileUtils.forceDelete(deleteFile);
			} catch (io.mosip.kernel.core.exception.IOException ioException) {
				throw ioException;
			}
		}
	}

	private Manifest getLocalManifest() throws IOException {
		if (localManifest != null) {
			return localManifest;
		}
		File localManifestFile = new File(manifestFile);

		if (localManifestFile.exists()) {

			// Set Local Manifest
			setLocalManifest(new Manifest(new FileInputStream(localManifestFile)));

		}
		return localManifest;
	}

	private Manifest getServerManifest() throws IOException, ParserConfigurationException, SAXException {

		if (serverManifest != null) {
			return serverManifest;
		}

		// Get latest Manifest from server
		setServerManifest(
				new Manifest(new URL(serverRegClientURL + getLatestVersion() + SLASH + manifestFile).openStream()));

		return serverManifest;

	}

	public void getJars()
			throws IOException, ParserConfigurationException, SAXException, io.mosip.kernel.core.exception.IOException {

		if (new File(libFolder).list().length == 0 || new File(binFolder).list().length == 0) {

			// TODO Mandatory Internet Required
			if (hasUpdate()) {
				getWithLatestJars();
			} else {
				checkJars();
			}
		} else {
			checkJars();
		}

	}

	private void checkJars() throws IOException, io.mosip.kernel.core.exception.IOException {
		Manifest manifest = getLocalManifest();

		if (manifest != null) {
			Map<String, Attributes> localAttributes = manifest.getEntries();

			List<String> checkableJars = new LinkedList<>();
			for (Entry<String, Attributes> jar : localAttributes.entrySet()) {
				checkableJars.add(jar.getKey());
			}

			// check all the jars in the manifest were available in zip extracted folder
			if (!checkableJars.isEmpty()) {
				checkJars(getCurrentVersion(), checkableJars, false);
			}

		}
	}

	private void replaceManifest() throws IOException, ParserConfigurationException, SAXException {

		File manifest = new File(manifestFile);

		setServerManifest(null);
		try (FileOutputStream fileOutputStream = new FileOutputStream(manifest)) {
			getServerManifest().write(fileOutputStream);

			// Refresh Local Manifest
			setLocalManifest(getServerManifest());

			setCurrentVersion(getLatestVersion());

			setLatestVersion(null);

			setServerManifest(null);
		}

	}

	private void setLocalManifest(Manifest localManifest) {
		RegistrationUpdate.localManifest = localManifest;
	}

	private void setServerManifest(Manifest serverManifest) {
		RegistrationUpdate.serverManifest = serverManifest;
	}

	public void setCurrentVersion(String currentVersion) {
		RegistrationUpdate.currentVersion = currentVersion;
	}

	public void setLatestVersion(String latestVersion) {
		RegistrationUpdate.latestVersion = latestVersion;
	}
}
