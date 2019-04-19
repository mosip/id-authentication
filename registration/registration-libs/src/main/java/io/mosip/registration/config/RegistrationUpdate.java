package io.mosip.registration.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
		Long t1=System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		for (String jarFile : checkableJars) {

			

			executorService.execute(new Runnable() {
				public void run() {

					try {
						System.out.println("Current Thread*****"+Thread.currentThread());
						String folder = jarFile.contains(mosip) ? binFolder : libFolder;
						checkForJarFile(version, folder, jarFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			
		}
		try {
			executorService.shutdown();
			executorService.awaitTermination(500, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Long t2 = System.currentTimeMillis() - t1;
		System.out.println("Time in Millis-------->>>>"+t2/(1000));
	}

	private void checkForJarFile(String version, String folderName, String jarFileName) throws IOException {

		File jarInFolder = new File(folderName + jarFileName);
		if (!jarInFolder.exists()
				|| (!isCheckSumValid(jarInFolder, (currentVersion.equals(version)) ? localManifest : serverManifest)
						&& FileUtils.deleteQuietly(jarInFolder))) {

			// Download Jar
			Files.copy(getInputStreamOfJar(version, jarFileName), jarInFolder.toPath());

		}

	}

	private InputStream getInputStreamOfJar(String version, String jarName) throws IOException {
		System.out.println("Downloading " + jarName);
		return getInputStreamOf(serverRegClientURL + version + SLASH + libFolder + jarName);

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

		// Get latest Manifest from server
		setServerManifest(
				new Manifest(getInputStreamOf(serverRegClientURL + getLatestVersion() + SLASH + manifestFile)));
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
			String manifestCheckSum = (String) manifest.getEntries().get(jarFile.getName())
					.get(Attributes.Name.CONTENT_TYPE);
			return manifestCheckSum.equals(checkSum);

		} catch (IOException ioException) {
			return false;
		}

	}

	private boolean hasSpace(int bytes) {

		return bytes < new File("/").getFreeSpace();
	}

	private InputStream getInputStreamOf(String url) throws IOException {
		URLConnection connection = new URL(url).openConnection();

		// Space Check
		if (hasSpace(connection.getContentLength())) {
			return connection.getInputStream();
		} else {
			throw new IOException("No Disk Space");
		}

	}

}
