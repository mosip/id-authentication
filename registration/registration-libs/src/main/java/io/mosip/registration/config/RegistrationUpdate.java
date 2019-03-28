package io.mosip.registration.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RegistrationUpdate {

	private static String backUpPath = "D://mosip/AutoBackUp";

	private static String folderSeperator = "/";

	private static String buildVersion = "0.9.6";
	private static String zipFileFormat = "mosip-sw-";
	private static String zipFile = ".zip";

	private static String manifestFile = "MANIFEST.MF";

	// TODO move to application.properties
	private static String serverRegClientURL = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client";

	private static String serverMosipXmlFileUrl = "http://13.71.87.138:8040/artifactory/libs-release/io/mosip/registration/registration-client/maven-metadata.xml";

	private static String libsFolder = "/libs/";

	private static String currentVersion;

	private static String latestVersion;

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

			NodeList list = metaInfXmlDocument.getDocumentElement().getElementsByTagName("version");
			if (list != null && list.getLength() > 0) {
				NodeList subList = list.item(0).getChildNodes();

				if (subList != null && subList.getLength() > 0) {
					latestVersion = subList.item(0).getNodeValue();
				}
			}

		}

		return latestVersion;
	}

	private String getCurrentVersion() throws IOException {
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

		downloadJarsWithVersion(getLatestVersion());
		deleteJars(deletableJars);

		// Un-Modified jars exist or not
		checkableJars.removeAll(deletableJars);
		checkableJars.removeAll(downloadJars);

		checkJars(getLatestVersion(), checkableJars);

	}

	private void checkJars(String version, List<String> checkableJars) throws IOException {
		for (String jarFile : checkableJars) {
			if (jarFile.contains("registration")) {
				checkForJarFile(version, "bin", jarFile);
			} else {
				checkForJarFile(version, "lib", jarFile);
			}
		}

	}

	private void checkForJarFile(String version, String folderName, String jarFileName) throws IOException {
		String folderSeparator = "/";
		File userDir = new File(System.getProperty("user.dir"));
		File jarInFolder = new File(
				userDir.getParentFile() + folderSeparator + folderName + folderSeparator + jarFileName);
		if (!jarInFolder.exists()) {

			// get input stream
			Files.copy(getInputStreamOfJar(version, jarFileName), jarInFolder.toPath());
		}
	}

	private static InputStream getInputStreamOfJar(String version, String jarName)
			throws MalformedURLException, IOException {
		return new URL(serverRegClientURL + version + libsFolder + jarName).openStream();

	}

	private void deleteJars(List<String> deletableJars) {
		// TODO Auto-generated method stub

	}

	private void downloadJarsWithVersion(String latestVersion) {
		// TODO Auto-generated method stub

	}

	private Manifest getLocalManifest() throws FileNotFoundException, IOException {
		File userDir = getCurrentDirectoryFile();
		File localManifestFile = new File(userDir.getParentFile() + folderSeperator + manifestFile);

		if (localManifestFile.exists()) {
			return new Manifest(new FileInputStream(localManifestFile));

		}
		return null;
	}

	private File getCurrentDirectoryFile() {
		return new File(System.getProperty("user.dir"));
	}

	private Manifest getServerManifest() throws IOException, ParserConfigurationException, SAXException {

		return new Manifest(
				new FileInputStream(serverRegClientURL + getLatestVersion() + folderSeperator + manifestFile));

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
}
