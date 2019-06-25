package io.mosip.registration.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * Update the Application
 * 
 * @author YASWANTH S
 *
 */
@Component
public class SoftwareInstallationHandler {

	public SoftwareInstallationHandler() throws IOException {
		String propsFilePath = new File(System.getProperty("user.dir")) + "/props/mosip-application.properties";
		FileInputStream fileInputStream = new FileInputStream(propsFilePath);
		Properties properties = new Properties();
		properties.load(fileInputStream);
		serverRegClientURL = properties.getProperty("mosip.reg.client.url");
		serverMosipXmlFileUrl = properties.getProperty("mosip.reg.xml.file.url");

		latestVersion = properties.getProperty("mosip.reg.version");

		getLocalManifest();

		deleteUnNecessaryJars();

	}

	private static String SLASH = "/";

	private static String manifestFile = "MANIFEST.MF";

	private static String serverRegClientURL;
	private static String serverMosipXmlFileUrl;

	private static String libFolder = "lib/";
	private String binFolder = "bin/";

	private String currentVersion;

	private String latestVersion;

	private Manifest localManifest;

	private Manifest serverManifest;

	private String mosip = "mosip";

	private String getLatestVersion() {

		return latestVersion;
	}

	public String getCurrentVersion() throws IOException {

		// Get Local manifest file
		getLocalManifest();
		if (localManifest != null) {
			setCurrentVersion((String) localManifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
		}

		return currentVersion;
	}

	public void installJars() throws IOException, io.mosip.kernel.core.exception.IOException {

		// Get Latest Version
		getLatestVersion();
		System.out.println("Current Version fetch finished");
		// Get Server Manifest
		getServerManifest();
		System.out.println("Server Manifet fetch finished");
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

		getServerManifest();

		deleteJars(deletableJars);

		checkableJars.removeAll(deletableJars);
		checkableJars.removeAll(downloadJars);

		// Download latest jars if not in local
		checkJars(latestVersion, downloadJars);

		// Un-Modified jars exist or not
		checkJars(latestVersion, checkableJars);

	}

	private void checkJars(String version, List<String> checkableJars) throws IOException {
		Long t1 = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		String errorMsg = "";
		for (String jarFile : checkableJars) {

			executorService.execute(new Runnable() {
				public void run() {

					try {
						System.out.println("Current Thread*****" + Thread.currentThread());
						String folder = jarFile.contains(mosip) ? binFolder : libFolder;

						File jarInFolder = new File(folder + jarFile);
						if (!jarInFolder.exists() || (!isCheckSumValid(jarInFolder,
								(currentVersion.equals(version)) ? localManifest : serverManifest)
								&& FileUtils.deleteQuietly(jarInFolder))) {

							// Download Jar
							Files.copy(getInputStreamOfJar(version, jarFile), jarInFolder.toPath());

						}

					} catch (IOException ioException) {
						ioException.printStackTrace();

						// TODO Need to terminate from here.
						System.exit(0);
					}
				}
			});

		}
		try {
			executorService.shutdown();
			executorService.awaitTermination(500, TimeUnit.SECONDS);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		Long t2 = System.currentTimeMillis() - t1;
		System.out.println("Time in Millis-------->>>>" + t2 / (1000));
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

		File localManifestFile = new File(manifestFile);

		if (localManifestFile.exists()) {

			// Set Local Manifest
			setLocalManifest(new Manifest(new FileInputStream(localManifestFile)));

		}
		return localManifest;
	}

	private Manifest getServerManifest() throws IOException {

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
			try {
				FileUtils.forceDelete(jarFile);
			} catch (io.mosip.kernel.core.exception.IOException exception) {
				return false;
			}
			return false;
		}

	}

	private boolean hasSpace(int bytes) {

		return bytes < new File("/").getFreeSpace();
	}

	private InputStream getInputStreamOf(String url) throws IOException {
		InputStream is = null;
		try {
			System.out.println("Inside Url connnection");
			URLConnection connection = new URL(url).openConnection();
			connection.setConnectTimeout(10000);
			System.out.println("End Url connnection");
			// Space Check
			if (hasSpace(connection.getContentLength())) {
				is = connection.getInputStream();
			} else {
				throw new IOException("No Disk Space");
			}
		} catch (NoRouteToHostException noRouteToHostException) {
			System.out.println("Error in connection");
			throw noRouteToHostException;
		}
		return is;
	}

	private void deleteUnNecessaryJars() {

		// Bin Folder
		File bin = new File(binFolder);

		// Lib Folder
		File lib = new File(libFolder);

		// Manifest's Attributes
		Map<String, Attributes> localManifestAttributes = null;
		if (localManifest != null) {
			localManifestAttributes = localManifest.getEntries();
		}

		List<File> deletableJars = new LinkedList<>();

		if (bin.listFiles().length != 0) {

			addDeletableJars(bin.listFiles(), deletableJars, localManifestAttributes, binFolder);
		}
		if (lib.listFiles().length != 0) {
			addDeletableJars(lib.listFiles(), deletableJars, localManifestAttributes, libFolder);
		}

		if (!deletableJars.isEmpty()) {
			try {
				deleteFiles(deletableJars);
			} catch (io.mosip.kernel.core.exception.IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	private void deleteFiles(List<File> deletableJars) throws io.mosip.kernel.core.exception.IOException {
		for (File jar : deletableJars) {
			// Delete Jar
			FileUtils.forceDelete(jar);
		}

	}

	private void addDeletableJars(File[] jarFiles, List<File> deletableJars,
			Map<String, Attributes> localManifestAttributes, String folder) {
		for (File jar : jarFiles) {

			if (!(jar.getName().contains("run") && folder.equals(binFolder))
					&& ((jar.getName().contains(mosip) && folder.equals(libFolder))
							|| (!jar.getName().contains(mosip)) && folder.equals(binFolder)
							|| localManifestAttributes == null
							|| !localManifestAttributes.containsKey(jar.getName()))) {

				deletableJars.add(jar);

			}
		}
	}

}
