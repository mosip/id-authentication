package io.mosip.registration.update;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.config.GlobalParamService;

/**
 * Update the Application
 * 
 * @author YASWANTH S
 *
 */
@Component
public class SoftwareUpdateHandler extends BaseService {

	public SoftwareUpdateHandler() {

		try {
			String propsFilePath = new File(System.getProperty("user.dir")) + "/props/mosip-application.properties";
			FileInputStream fileInputStream = new FileInputStream(propsFilePath);
			Properties properties = new Properties();
			properties.load(fileInputStream);
			serverRegClientURL = properties.getProperty("mosip.client.url");
			serverMosipXmlFileUrl = properties.getProperty("mosip.xml.file.url");
			backUpPath = properties.getProperty("mosip.rollback.path");

		} catch (IOException exception) {
			LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));

		}
	}

	private static String SLASH = "/";

	private String manifestFile = "MANIFEST.MF";

	private String backUpPath;
	private String serverRegClientURL;
	private String serverMosipXmlFileUrl;

	private static String libFolder = "lib/";
	private String binFolder = "bin/";

	private String currentVersion;

	private String latestVersion;

	private Manifest localManifest;

	private Manifest serverManifest;

	private String mosip = "mosip";

	private String versionTag = "version";

	private String latestVersionReleaseTimestamp;

	private String lastUpdatedTag = "lastUpdated";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${HTTP_API_READ_TIMEOUT}")
	private int readTimeout;

	@Value("${HTTP_API_WRITE_TIMEOUT}")
	private int connectTimeout;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(SoftwareUpdateHandler.class);

	@Autowired
	private GlobalParamService globalParamService;

	/**
	 * Check for updates
	 * 
	 * @return has update
	 */
	public boolean hasUpdate() {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Checking for updates");
		try {
			return !getCurrentVersion().equals(getLatestVersion());
		} catch (IOException | ParserConfigurationException | SAXException | RuntimeException exception) {
			LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			return false;
		}

	}

	private String getLatestVersion() throws IOException, ParserConfigurationException, SAXException {
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Checking for latest version started");
		// Get latest version using meta-inf.xml
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
		org.w3c.dom.Document metaInfXmlDocument = db.parse(getInputStreamOf(serverMosipXmlFileUrl));

		setLatestVersion(getElementValue(metaInfXmlDocument, versionTag));
		setLatestVersionReleaseTimestamp(getElementValue(metaInfXmlDocument, lastUpdatedTag));

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Checking for latest version completed");
		return latestVersion;
	}

	private String getElementValue(Document metaInfXmlDocument, String tagName) {
		NodeList list = metaInfXmlDocument.getDocumentElement().getElementsByTagName(tagName);
		String val = null;
		if (list != null && list.getLength() > 0) {
			NodeList subList = list.item(0).getChildNodes();

			if (subList != null && subList.getLength() > 0) {
				// Set Latest Version
				val = subList.item(0).getNodeValue();
			}
		}

		return val;

	}

	/**
	 * Get Current version of setup
	 * 
	 * @return current version
	 * @throws IOException
	 */
	public String getCurrentVersion() {
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Checking for current version started");

		// Get Local manifest file
		try {
			if (getLocalManifest() != null) {
				setCurrentVersion((String) localManifest.getMainAttributes().get(Attributes.Name.MANIFEST_VERSION));
			}
		} catch (IOException exception) {
			LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));

		}

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Checking for current version completed");
		return currentVersion;
	}

	/**
	 * update the binaries
	 * 
	 * @throws Exception
	 */
	public void update() throws Exception {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Updating latest version started");
		Path backUp = null;

		try {
			// Get Server Manifest
			getServerManifest();

			// Back Current Application
			backUp = backUpSetup();
			// replace local manifest with Server manifest
			serverManifest.write(new FileOutputStream(new File(manifestFile)));

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

			getServerManifest();

			// Download latest jars if not in local
			checkJars(getLatestVersion(), downloadJars);
			checkJars(getLatestVersion(), checkableJars);

			setLocalManifest(serverManifest);
			setServerManifest(null);
			setLatestVersion(null);

			// Update global param of software update flag as false
			globalParamService.update(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE,
					RegistrationConstants.DISABLE);

		} catch (RuntimeException | IOException | ParserConfigurationException | SAXException exception) {
			LOGGER.error(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			// Rollback setup
			File backUpFolder = backUp.toFile();

			rollBackSetup(backUpFolder);

			throw exception;
		}
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Updating latest version started");
	}

	private Path backUpSetup() throws io.mosip.kernel.core.exception.IOException {
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Backup of current version started");
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

		FileUtils.copyDirectory(new File(binFolder), bin);
		FileUtils.copyDirectory(new File(libFolder), lib);

		FileUtils.copyFile(new File(manifestFile), manifest);

		for (File backUpFile : new File(backUpPath).listFiles()) {
			if (!backUpFile.getAbsolutePath().equals(backUpFolder.getAbsolutePath())) {
				FileUtils.deleteDirectory(backUpFile);
			}
		}
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Backup of current version completed");
		return backUpFolder.toPath();

	}

	private void checkJars(String version, List<String> checkableJars) throws IOException {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Checking of jars started");
		for (String jarFile : checkableJars) {

			String folder = jarFile.contains(mosip) ? binFolder : libFolder;

			File jarInFolder = new File(folder + jarFile);

			if (!jarInFolder.exists()
					|| (!isCheckSumValid(jarInFolder, (currentVersion.equals(version)) ? localManifest : serverManifest)
							&& FileUtils.deleteQuietly(jarInFolder))) {

				LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
						"Downloading jar : " + jarFile + " started");
				// Download Jar
				Files.copy(getInputStreamOfJar(version, jarFile), jarInFolder.toPath());

			}

		}

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Checking of jars completed");
	}

	private InputStream getInputStreamOfJar(String version, String jarName) throws IOException {
		return getInputStreamOf(serverRegClientURL + version + SLASH + libFolder + jarName);

	}

	private void deleteJars(List<String> deletableJars) throws io.mosip.kernel.core.exception.IOException {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Deletion of jars started");
		for (String jarName : deletableJars) {
			File deleteFile = null;

			String deleteFolder = jarName.contains(mosip) ? binFolder : libFolder;

			deleteFile = new File(deleteFolder + jarName);

			if (deleteFile.exists()) {
				// Delete Jar
				FileUtils.forceDelete(deleteFile);

			}
		}
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Deletion of jars completed");

	}

	private Manifest getLocalManifest() throws IOException {
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Geting  of local manifest started");

		File localManifestFile = new File(manifestFile);

		if (localManifestFile.exists()) {

			// Set Local Manifest
			setLocalManifest(new Manifest(new FileInputStream(localManifestFile)));

		}
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Geting  of local manifest completed");
		return localManifest;
	}

	private Manifest getServerManifest() throws IOException, ParserConfigurationException, SAXException {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Geting  of server manifest started");
		// Get latest Manifest from server
		setServerManifest(
				new Manifest(getInputStreamOf(serverRegClientURL + getLatestVersion() + SLASH + manifestFile)));
		setLatestVersion(serverManifest.getMainAttributes().getValue(Attributes.Name.MANIFEST_VERSION));

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Geting  of server manifest completed");
		return serverManifest;

	}

	private void setLocalManifest(Manifest localManifest) {
		this.localManifest = localManifest;
	}

	private void setServerManifest(Manifest serverManifest) {
		this.serverManifest = serverManifest;
	}

	private void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	private void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	private boolean isCheckSumValid(File jarFile, Manifest manifest) {
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Checking of checksum started for jar :" + jarFile.getName());
		String checkSum;
		try {
			checkSum = HMACUtils.digestAsPlainText(HMACUtils.generateHash(Files.readAllBytes(jarFile.toPath())));

			// Get Check sum
			String manifestCheckSum = getCheckSum(jarFile.getName(), manifest);

			LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					"Checking of checksum completed for jar :" + jarFile.getName());
			return manifestCheckSum.equals(checkSum);

		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			return false;
		}

	}

	private boolean hasSpace(int bytes) {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Checking of space in machine");
		return bytes < new File("/").getFreeSpace();
	}

	private InputStream getInputStreamOf(String url) throws IOException {
		URLConnection connection = new URL(url).openConnection();

		connection.setConnectTimeout(connectTimeout);

		connection.setReadTimeout(readTimeout);

		// Space Check
		if (hasSpace(connection.getContentLength())) {
			return connection.getInputStream();
		} else {
			throw new IOException("No Disk Space");
		}

	}

	private void setLatestVersionReleaseTimestamp(String latestVersionReleaseTimestamp) {
		this.latestVersionReleaseTimestamp = latestVersionReleaseTimestamp;
	}

	/**
	 * Get timestamp when latest version has released
	 * 
	 * @return timestamp
	 */
	public Timestamp getLatestVersionReleaseTimestamp() {

		Calendar calendar = Calendar.getInstance();

		String dateString = latestVersionReleaseTimestamp;

		int year = Integer.valueOf(dateString.charAt(0) + "" + dateString.charAt(1) + "" + dateString.charAt(2) + ""
				+ dateString.charAt(3));
		int month = Integer.valueOf(dateString.charAt(4) + "" + dateString.charAt(5));
		int date = Integer.valueOf(dateString.charAt(6) + "" + dateString.charAt(7));
		int hourOfDay = Integer.valueOf(dateString.charAt(8) + "" + dateString.charAt(9));
		int minute = Integer.valueOf(dateString.charAt(10) + "" + dateString.charAt(11));
		int second = Integer.valueOf(dateString.charAt(12) + "" + dateString.charAt(13));

		calendar.set(year, month - 1, date, hourOfDay, minute, second);

		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 * Execute script files
	 * 
	 * @param latestVersion
	 *            latest version
	 * @param previousVersion
	 *            previous version
	 * @return response of sql execution
	 */
	public ResponseDTO executeSqlFile(String latestVersion, String previousVersion) {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"DB-Script files execution started");

		ResponseDTO responseDTO = new ResponseDTO();

		URL resource = this.getClass().getResource("/sql/" + latestVersion + "/");

		if (resource != null) {

			File sqlFile = getSqlFile(resource.getPath());

			// execute sql file
			try {

				runSqlFile(sqlFile);

			} catch (RuntimeException | IOException runtimeException) {

				try {
					File rollBackFile = getSqlFile(
							this.getClass().getResource("/sql/" + latestVersion + "_rollback/").getPath());

					if (rollBackFile.exists()) {
						runSqlFile(rollBackFile);
					}
				} catch (RuntimeException | IOException exception) {
					
					LOGGER.error(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
							exception.getMessage() + ExceptionUtils.getStackTrace(exception));

					
				}
				// Prepare Error Response
				setErrorResponse(responseDTO, RegistrationConstants.SQL_EXECUTION_FAILURE, null);

				// Replace with backup
				rollback(responseDTO, previousVersion);

				return responseDTO;

			}
		}

		// Update global param with current version
		globalParamService.update(RegistrationConstants.SERVICES_VERSION_KEY, latestVersion);

		setSuccessResponse(responseDTO, RegistrationConstants.SQL_EXECUTION_SUCCESS, null);

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"DB-Script files execution completed");

		return responseDTO;
	}

	private File getSqlFile(String path) {

		// Get File
		return FileUtils.getFile(path);

	}

	private void runSqlFile(File sqlFile) throws IOException {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Execution started sql file : " + sqlFile.getName());
		for (File file : sqlFile.listFiles()) {
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

				String str;
				StringBuilder sb = new StringBuilder();
				while ((str = bufferedReader.readLine()) != null) {
					sb.append(str + "\n ");
				}

				List<String> statments = java.util.Arrays.asList(sb.toString().split(";"));

				for (String stat : statments) {
					if (!stat.trim().equals("")) {

						jdbcTemplate.execute(stat);

					}
				}

			}

		}
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Execution completed sql file : " + sqlFile.getName());

	}

	private void rollBackSetup(File backUpFolder) throws io.mosip.kernel.core.exception.IOException {
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Replacing Backup of current version started");
		FileUtils.copyDirectory(FileUtils.getFile(backUpFolder.getAbsolutePath(), FilenameUtils.getName(binFolder)),
				FileUtils.getFile(FilenameUtils.getName(binFolder)));
		FileUtils.copyDirectory(FileUtils.getFile(backUpFolder.getAbsolutePath(), FilenameUtils.getName(libFolder)),
				FileUtils.getFile(FilenameUtils.getName(libFolder)));
		FileUtils.copyFile(FileUtils.getFile(backUpFolder.getAbsolutePath(), FilenameUtils.getName(manifestFile)),
				FileUtils.getFile(FilenameUtils.getName(manifestFile)));
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Replacing Backup of current version completed");
	}

	private void rollback(ResponseDTO responseDTO, String previousVersion) {
		File file = FileUtils.getFile(backUpPath);

		boolean isBackUpCompleted = false;
		for (File backUpFolder : file.listFiles()) {
			if (backUpFolder.getName().contains(previousVersion)) {

				try {
					rollBackSetup(backUpFolder);

					isBackUpCompleted = true;
					setErrorResponse(responseDTO, RegistrationConstants.BACKUP_PREVIOUS_SUCCESS, null);
				} catch (io.mosip.kernel.core.exception.IOException exception) {
					LOGGER.error(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
							exception.getMessage() + ExceptionUtils.getStackTrace(exception));

				
					setErrorResponse(responseDTO, RegistrationConstants.BACKUP_PREVIOUS_FAILURE, null);
				}
				break;

			}
		}

		if (!isBackUpCompleted) {
			setErrorResponse(responseDTO, RegistrationConstants.BACKUP_PREVIOUS_FAILURE, null);
		}
	}

	/**
	 * Get checksum
	 * 
	 * @param jarName
	 *            jarName
	 * @param manifest
	 *            localManifestFile
	 * @return
	 */
	public String getCheckSum(String jarName, Manifest manifest) {

		// Get Local manifest
		manifest = manifest != null ? manifest : localManifest;

		String checksum = null;

		if (manifest == null) {

			try {
				manifest = getLocalManifest();

				if (manifest != null) {
					checksum = (String) manifest.getEntries().get(jarName).get(Attributes.Name.CONTENT_TYPE);
				}
			} catch (IOException exception) {
				LOGGER.error(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));

			}
		}

		// checksum (content-type)
		return checksum;
	}
}
