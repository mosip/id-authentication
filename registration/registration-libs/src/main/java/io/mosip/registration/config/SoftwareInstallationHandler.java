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

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.util.LoggerFactory;

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

		latestVersion = properties.getProperty("mosip.reg.version");

		getLocalManifest();

		deleteUnNecessaryJars();

	}

	private static String SLASH = "/";

	private static String manifestFile = "MANIFEST.MF";

	private  String serverRegClientURL;

	private static String libFolder = "lib/";
	private String binFolder = "bin/";

	private String currentVersion;

	private String latestVersion;

	private Manifest localManifest;

	private Manifest serverManifest;

	private String mosip = "mosip";

	private static final Logger LOGGER = LoggerFactory.getLogger(SoftwareInstallationHandler.class);

	private String getLatestVersion() {
		LOGGER.info(LoggerConstants.SOFTWARE_INSTALLATION_HANDLER, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Getting latest version : " + latestVersion);

		return latestVersion;
	}

	public String getCurrentVersion() throws IOException {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Getting current version started");

		// Get Local manifest file
		getLocalManifest();
		if (localManifest != null) {
			setCurrentVersion((String) localManifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
		}

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Getting current version completed");

		return currentVersion;
	}

	public void installJars() throws IOException, io.mosip.kernel.core.exception.IOException {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started installing jars");

		// Get Latest Version
		getLatestVersion();

		// Get Server Manifest
		getServerManifest();

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started downloading server manifest and replacing");

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

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Completed installing jars");

	}

	private void checkJars(String version, List<String> checkableJars) {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Checking jars : " + checkableJars.toString());

		ExecutorService executorService = Executors.newFixedThreadPool(5);
		for (String jarFile : checkableJars) {

			executorService.execute(new Runnable() {
				public void run() {

					try {

						String folder = jarFile.contains(mosip) ? binFolder : libFolder;

						File jarInFolder = new File(folder + jarFile);
						if (!jarInFolder.exists() || (!isCheckSumValid(jarInFolder,
								(currentVersion.equals(version)) ? localManifest : serverManifest)
								&& FileUtils.deleteQuietly(jarInFolder))) {

							// Download Jar
							Files.copy(getInputStreamOfJar(version, jarFile), jarInFolder.toPath());

						}

					} catch (IOException ioException) {
						LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
								LoggerConstants.APPLICATION_ID,
								ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

						LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
								LoggerConstants.APPLICATION_ID, "Terminating application");

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
			executorService.shutdown();
			LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
					LoggerConstants.APPLICATION_ID, exception.getMessage() + ExceptionUtils.getStackTrace(exception));

		}
	}

	private InputStream getInputStreamOfJar(String version, String jarName) throws IOException {
		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started Downloading of : " + jarName);

		return getInputStreamOf(serverRegClientURL + version + SLASH + libFolder + jarName);

	}

	private void deleteJars(List<String> deletableJars) throws io.mosip.kernel.core.exception.IOException {

		for (String jarName : deletableJars) {
			deleteJar(jarName);
		}

	}

	private void deleteJar(String jarName) throws io.mosip.kernel.core.exception.IOException {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started Deleting : " + jarName);

		File deleteFile = null;

		String deleteFolder = jarName.contains(mosip) ? binFolder : libFolder;

		deleteFile = new File(deleteFolder + jarName);

		if (deleteFile.exists()) {
			// Delete Jar
			FileUtils.forceDelete(deleteFile);

		}

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Completed Deleting : " + jarName);

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

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started Downloading manifest of version :" + getLatestVersion());

		// Get latest Manifest from server

		// Get latest Manifest from server
		setServerManifest(
				new Manifest(getInputStreamOf(serverRegClientURL + getLatestVersion() + SLASH + manifestFile)));
		setLatestVersion(serverManifest.getMainAttributes().getValue(Attributes.Name.MANIFEST_VERSION));

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Completed Downloading manifest of version :" + getLatestVersion());

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

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Checking required jars of local started");

		Map<String, Attributes> localAttributes = localManifest.getEntries();

		List<String> checkableJars = new LinkedList<>();
		for (Entry<String, Attributes> jar : localAttributes.entrySet()) {
			checkableJars.add(jar.getKey());
		}

		// check all the jars in the manifest were available in zip extracted folder
		if (!checkableJars.isEmpty()) {
			return checkLocalJars(checkableJars);
		}

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Completed required jars of local started");

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

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Checking check sum for : " + jarFile.getName());

		String checkSum;
		try {
			checkSum = HMACUtils.digestAsPlainText(HMACUtils.generateHash(Files.readAllBytes(jarFile.toPath())));
			String manifestCheckSum = (String) manifest.getEntries().get(jarFile.getName())
					.get(Attributes.Name.CONTENT_TYPE);

			return manifestCheckSum.equals(checkSum);

		} catch (IOException ioException) {
			
			LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
					LoggerConstants.APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

			try {
				LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
						LoggerConstants.APPLICATION_ID, "Deleting : " + jarFile.getName());

				FileUtils.forceDelete(jarFile);
			} catch (io.mosip.kernel.core.exception.IOException exception) {
				
				LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
						LoggerConstants.APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));

				return false;
			}
			return false;
		}

	}

	private boolean hasSpace(int bytes) {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "space check for : " + bytes +" in bytes");

		return bytes < new File("/").getFreeSpace();
	}

	private InputStream getInputStreamOf(String url) throws IOException {
		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Getting Inputstream for url : "+url);

		InputStream inputStream = null;
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setConnectTimeout(50000);
			// Space Check
			if (hasSpace(connection.getContentLength())) {
				inputStream = connection.getInputStream();
			} else {
				throw new IOException("No Disk Space");
			}
		} catch (NoRouteToHostException noRouteToHostException) {
			LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
					LoggerConstants.APPLICATION_ID,
					noRouteToHostException.getMessage() + ExceptionUtils.getStackTrace(noRouteToHostException));

			throw noRouteToHostException;
		}
		return inputStream;
	}

	private void deleteUnNecessaryJars() {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Deletion of un-necessary jars started");

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
				LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
						LoggerConstants.APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

			}
		}

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Deletion of un-necessary jars completed");
	}

	private void deleteFiles(List<File> deletableJars) throws io.mosip.kernel.core.exception.IOException {
		for (File jar : deletableJars) {
			

			LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
					LoggerConstants.APPLICATION_ID, "Deleting jar : "+jar.getName());
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
