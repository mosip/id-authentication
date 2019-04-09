package io.mosip.registration.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.springframework.stereotype.Component;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * Update the Application
 * 
 * @author YASWANTH S
 *
 */
@Component
public class RegistrationUpdate {

	public RegistrationUpdate() throws IOException {
		getLocalManifest();
	}

	private static String SLASH = "/";

	private static String manifestFile = "MANIFEST.MF";

	// TODO move to application.properties
	private static String serverRegClientURL = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/";
	private static String serverMosipXmlFileUrl = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/maven-metadata.xml";

	private static String libFolder = "lib/";
	private String binFolder = "bin/";

	private String currentVersion;

	private String latestVersion;

	private Manifest localManifest;

	private Manifest serverManifest;

	private String mosip = "mosip";

	private String versionTag = "version";

	public boolean hasUpdate() throws IOException, ParserConfigurationException, SAXException {
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
			org.w3c.dom.Document metaInfXmlDocument = db.parse(new URL(serverMosipXmlFileUrl).openStream());

			NodeList list = metaInfXmlDocument.getDocumentElement().getElementsByTagName(versionTag);
			if (list != null && list.getLength() > 0) {
				NodeList subList = list.item(0).getChildNodes();

				if (subList != null && subList.getLength() > 0) {
					// Latest Version
					setLatestVersion(subList.item(0).getNodeValue());
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
			getLocalManifest();
			if (localManifest != null) {
				setCurrentVersion((String) localManifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
			}
		}
		System.out.println("Getting current Version  " + currentVersion);
		return currentVersion;
	}

	public void getWithLatestJars()
			throws IOException, ParserConfigurationException, SAXException, io.mosip.kernel.core.exception.IOException {

		// Get Latest Version
		getLatestVersion();

		// Get Server Manifest
		getServerManifest();

		// replace local manifest with Server manifest
		serverManifest.write(new FileOutputStream(new File(manifestFile)));

		Map<String, Attributes> serverAttributes = serverManifest.getEntries();
		List<String> downloadJars = new LinkedList<>();
		List<String> deletableJars = new LinkedList<>();
		List<String> checkableJars = new LinkedList<>();

		if (localManifest != null) {

			Map<String, Attributes> localAttributes = localManifest.getEntries();

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

		}
		for (Entry<String, Attributes> jar : serverAttributes.entrySet()) {
			downloadJars.add(jar.getKey());
		}

		deleteJars(deletableJars);

		checkableJars.removeAll(deletableJars);
		checkableJars.removeAll(downloadJars);

		// Download latest jars if not in local
		checkJars(latestVersion, downloadJars);

		// Un-Modified jars exist or not
		checkJars(latestVersion, checkableJars);

	}

	private void checkJars(String version, List<String> checkableJars) throws IOException {

		for (String jarFile : checkableJars) {

			String folder = jarFile.contains(mosip) ? binFolder : libFolder;

			checkForJarFile(version, folder, jarFile);

		}

	}

	private void checkForJarFile(String version, String folderName, String jarFileName) throws IOException {

		File jarInFolder = new File(folderName + jarFileName);
		if (!jarInFolder.exists()) {

			// Download Jar
			Files.copy(getInputStreamOfJar(version, jarFileName), jarInFolder.toPath());

		} else if (!isCheckSumValid(jarInFolder, (currentVersion.equals(version)) ? localManifest : serverManifest)
				&& jarInFolder.delete()) {

			// Download Jar
			Files.copy(getInputStreamOfJar(version, jarFileName), jarInFolder.toPath());
		}

	}

	private static InputStream getInputStreamOfJar(String version, String jarName) throws IOException {
		System.out.println("Downloading " + jarName);
		return new URL(serverRegClientURL + version + SLASH + libFolder + jarName).openStream();

	}

	private void deleteJars(List<String> deletableJars) throws io.mosip.kernel.core.exception.IOException {

		for (String jarName : deletableJars) {
			deleteJar(jarName);
		}

	}

	private void deleteJar(String jarName) throws io.mosip.kernel.core.exception.IOException {
		File deleteFile = null;

		String deleteFolder = jarName.contains(mosip) ? binFolder : libFolder;

		deleteFile = new File(deleteFolder + jarName);

		if (deleteFile.exists()) {
			// Delete Jar
			FileUtils.forceDelete(deleteFile);

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
		setLatestVersion(serverManifest.getMainAttributes().getValue(Attributes.Name.MANIFEST_VERSION));

		return serverManifest;

	}

	private void setLocalManifest(Manifest localManifest) {
		this.localManifest = localManifest;
	}

	private void setServerManifest(Manifest serverManifest) {
		this.serverManifest = serverManifest;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	public boolean hasRequiredJars() {

		Map<String, Attributes> localAttributes = localManifest.getEntries();

		List<String> checkableJars = new LinkedList<>();
		for (Entry<String, Attributes> jar : localAttributes.entrySet()) {
			checkableJars.add(jar.getKey());
		}

		// check all the jars in the manifest were available in zip extracted folder
		if (!checkableJars.isEmpty()) {
			return checkLocalJars(checkableJars);
		}

		return true;
	}

	private boolean checkLocalJars(List<String> jarList) {
		for (String jarFile : jarList) {

			File jar = jarFile.contains(mosip) ? new File(binFolder + SLASH + jarFile)
					: new File(libFolder + SLASH + jarFile);

			if (!(jar.exists()) || !isCheckSumValid(jar, localManifest)) {
				return false;
			}

		}

		return true;
	}

	private boolean isCheckSumValid(File jarFile, Manifest manifest) {
		String checkSum;
		try {
			checkSum = HMACUtils.digestAsPlainText(HMACUtils.generateHash(Files.readAllBytes(jarFile.toPath())));
			System.out.println(jarFile.getName());
			String manifestCheckSum = (String) manifest.getEntries().get(jarFile.getName())
					.get(Attributes.Name.CONTENT_TYPE);
			return manifestCheckSum.equals(checkSum);

		} catch (IOException ioException) {
			ioException.printStackTrace();
			return false;
		}

	}

}
