package io.mosip.registration.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.util.FileUtils;

public class RegistrationUpdate {

	private static String backUpPath = "D://mosip/AutoBackUp";

	private static String SLASH = "/";

	private static String manifestFile = "MANIFEST.MF";

	// TODO move to application.properties
	private static String serverRegClientURL = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/";
	private static String serverMosipXmlFileUrl = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/maven-metadata.xml";

	private static String libsFolder = "lib/";
	private static String binFolder = "bin/";

	private static String currentVersion;

	private static String latestVersion;

	private static Manifest localManifest;

	private static Manifest serverManifest;

	private String registration = "registration";

	private String versionTag = "version";

	public boolean hasUpdate() throws IOException, ParserConfigurationException, SAXException {
		return !getCurrentVersion().equals(getLatestVersion());
	}

	private String getLatestVersion() throws IOException, ParserConfigurationException, SAXException {
		if (latestVersion != null) {
			return latestVersion;
		} else {
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
			Manifest localManifest = getLocalManifest();
			if (localManifest != null) {
				currentVersion = (String) localManifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION);
			}
		}
		return currentVersion;
	}

	public void getWithLatestJars() throws IOException, ParserConfigurationException, SAXException {
		Manifest localManifest = getLocalManifest();
		Manifest serverManifest = getServerManifest();

		List<String> downloadJars = new LinkedList<>();
		List<String> deletableJars = new LinkedList<>();
		List<String> checkableJars = new LinkedList<>();

		Map<String, Attributes> localAttributes = localManifest.getEntries();
		Map<String, Attributes> serverAttributes = serverManifest.getEntries();

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
				serverManifest.getEntries().remove(jar.getKey());

			}
		}

		for (Entry<String, Attributes> jar : serverAttributes.entrySet()) {
			downloadJars.add(jar.getKey());
		}

		deleteJars(deletableJars);

		// Un-Modified jars exist or not
		checkableJars.removeAll(deletableJars);
		checkableJars.removeAll(downloadJars);

		// Download latest jars if not in local
		checkJars(getLatestVersion(), downloadJars);
		checkJars(getLatestVersion(), checkableJars);

		replaceManifest();

	}

	private void checkJars(String version, List<String> checkableJars) throws IOException {
		for (String jarFile : checkableJars) {
			if (jarFile.contains(registration)) {
				checkForJarFile(version, binFolder, jarFile);
			} else {
				checkForJarFile(version, libsFolder, jarFile);
			}
		}

	}

	private void checkForJarFile(String version, String folderName, String jarFileName) throws IOException {

		File jarInFolder = new File(folderName + jarFileName);
		if (!jarInFolder.exists()) {

			// TODO Temporary fix to get rid of Client jar
			if (!(jarFileName.contains("client") || jarFileName.contains("pom"))) {
				// get input stream
				Files.copy(getInputStreamOfJar(version, jarFileName), jarInFolder.toPath());
			}
		}
	}

	private static InputStream getInputStreamOfJar(String version, String jarName)
			throws MalformedURLException, IOException {
		return new URL(serverRegClientURL + version + SLASH + libsFolder + jarName).openStream();

	}

	private void deleteJars(List<String> deletableJars) {
		deletableJars.forEach(jarName -> {
			File deleteFile = null;
			if (jarName.contains(registration)) {
				deleteFile = new File(binFolder + jarName);
			} else {
				deleteFile = new File(libsFolder + jarName);
			}

			if (deleteFile.exists()) {
				try {
					FileUtils.forceDelete(deleteFile);
				} catch (io.mosip.kernel.core.exception.IOException ioException) {
					ioException.printStackTrace();
				}
			}
		});

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

	public void getJars() throws IOException {

		Manifest localManifest = getLocalManifest();

		if (localManifest != null) {
			Map<String, Attributes> localAttributes = localManifest.getEntries();

			List<String> checkableJars = new LinkedList<>();
			for (Entry<String, Attributes> jar : localAttributes.entrySet()) {
				checkableJars.add(jar.getKey());
			}

			// check all the jars in the manifest were available in zip extracted folder
			if (!checkableJars.isEmpty()) {
				checkJars(getCurrentVersion(), checkableJars);
			}

		}
	}

	private void replaceManifest() throws IOException, ParserConfigurationException, SAXException {
		Manifest serverManifest = getServerManifest();

		File manifest = new File(manifestFile);

		try (FileOutputStream fileOutputStream = new FileOutputStream(manifest)) {
			serverManifest.write(fileOutputStream);

			// Refresh Local Manifest
			setLocalManifest(serverManifest);
		}

	}

	private void setLocalManifest(Manifest localManifest) {
		this.localManifest = localManifest;
	}

	private void setServerManifest(Manifest serverManifest) {
		this.serverManifest = serverManifest;
	}
}
