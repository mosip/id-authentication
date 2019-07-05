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
import java.io.InputStreamReader;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
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
 * This class will update the application based on comapring the versions of the
 * jars from the Manifest. The comparison will be done by comparing the Local
 * Manifest and the meta-inf.xml file. If there is any updation available in the
 * jar then the new jar gets downloaded and the old gets archived.
 * 
 * @author YASWANTH S
 *
 */
@Component
public class SoftwareUpdateHandler extends BaseService {

	/**
	 * This constructor will read the application Property file and load the
	 * properties to the class level variable.
	 */
	public SoftwareUpdateHandler() {

		propsFilePath = new File(System.getProperty("user.dir")) + props;

		try (FileInputStream fileInputStream = new FileInputStream(propsFilePath)) {
			Properties properties = new Properties();
			properties.load(fileInputStream);
			serverRegClientURL = properties.getProperty("mosip.reg.client.url");
			serverMosipXmlFileUrl = properties.getProperty("mosip.reg.xml.file.url");
			backUpPath = properties.getProperty("mosip.reg.rollback.path");

		} catch (IOException exception) {
			LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));

		}
	}

	private String propsFilePath;
	private static String props = "/props/mosip-application.properties";
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
	private String SQL = "sql";
	private String exectionSqlFile = "initial_db_scripts.sql";
	private String rollBackSqlFile = "rollback_scripts.sql";

	/**
	 * It will check whether any software updates are available or not.
	 * <p>
	 * The check will be done by comparing the Local Manifest file version with the
	 * version of the server meta-inf.xml file
	 * </p>
	 * 
	 * @return Boolean true - If there is any update available. false - If no
	 *         updates available
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

	/**
	 * 
	 * @return Returns the current version which is read from the server meta-inf
	 *         file.
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
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
	 * <p>
	 * Checks whteher the update is available or not
	 * </p>
	 * <p>
	 * If the Update is available:
	 * </p>
	 * <p>
	 * If the jars needs to be added/updated in the local
	 * </p>
	 * <ul>
	 * <li>Take the back-up of the current jars</li>
	 * <li>Download the jars from the server and add/update it in the local</li>
	 * </ul>
	 * <p>
	 * If the jars needs to be deleted in the local
	 * </p>
	 * <ul>
	 * <li>Take the back-up of the current jars</li>
	 * <li>Delete that particular jar from the local</li>
	 * </ul>
	 * <p>
	 * If there is any error occurs while updation then the restoration of the jars
	 * will happen by taking the back-up jars
	 * </p>
	 * 
	 * @throws Exception
	 *             - IOException
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
			return checkSum.equals(manifestCheckSum);

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

	public void setLatestVersionReleaseTimestamp(String latestVersionReleaseTimestamp) {
		this.latestVersionReleaseTimestamp = latestVersionReleaseTimestamp;
	}

	/**
	 * The latest version timestamp will be taken from the server meta-inf.xml file.
	 * This timestamp will the be parsed in this method.
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
	 * This method will check whether any updation needs to be done in the DB
	 * structure.
	 * <p>
	 * If there is any updates available:
	 * </p>
	 * <p>
	 * Take the back-up of the current DB
	 * </p>
	 * <p>
	 * Run the Update queries from the sql file, which is downloaded from the server
	 * and available in the local
	 * </p>
	 * <p>
	 * If there is any error occurs during the update,then the rollback query will
	 * run from the sql file
	 * </p>
	 * 
	 * @param latestVersion
	 *            latest version
	 * @param previousVersion
	 *            previous version
	 * @return response of sql execution
	 * @throws IOException
	 */
	public ResponseDTO executeSqlFile(String latestVersion, String previousVersion) throws IOException {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"DB-Script files execution started");

		ResponseDTO responseDTO = new ResponseDTO();

		// execute sql file

		try {

			LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					"Checking Started : " + latestVersion + SLASH + exectionSqlFile);

			execute(SQL + SLASH + latestVersion + SLASH + exectionSqlFile);

			LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					"Checking completed : " + latestVersion + SLASH + exectionSqlFile);

		}

		catch (RuntimeException | IOException runtimeException) {

			LOGGER.error(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

			// ROLL BACK QUERIES
			try {

				LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
						"Checking started : " + latestVersion + SLASH + rollBackSqlFile);

				execute(SQL + SLASH + latestVersion + SLASH + rollBackSqlFile);

				LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
						"Checking completed : " + latestVersion + SLASH + rollBackSqlFile);

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

		// Update global param with current version
		globalParamService.update(RegistrationConstants.SERVICES_VERSION_KEY, latestVersion);

		addProperties(latestVersion);

		setSuccessResponse(responseDTO, RegistrationConstants.SQL_EXECUTION_SUCCESS, null);

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"DB-Script files execution completed");

		return responseDTO;
	}

	private void execute(String path) throws IOException {
		try (InputStream inputStream = SoftwareUpdateHandler.class.getClassLoader().getResourceAsStream(path)) {

			LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					inputStream != null ? path + " found" : path + " Not Found");
			
			if (inputStream != null) {
				runSqlFile(inputStream);
			}
		}
	}

	private void runSqlFile(InputStream inputStream) throws IOException {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Execution started sql file");

		try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
			try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

				String str;
				StringBuilder sb = new StringBuilder();
				while ((str = bufferedReader.readLine()) != null) {
					sb.append(str + "\n ");
				}

				List<String> statments = java.util.Arrays.asList(sb.toString().split(";"));

				for (String stat : statments) {
					if (!stat.trim().equals("")) {

						LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
								"Executing Statment : " + stat);

						jdbcTemplate.execute(stat);

					}
				}
			}

		}

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID, "Execution completed sql file");

	}

	private void rollBackSetup(File backUpFolder) throws io.mosip.kernel.core.exception.IOException {
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Replacing Backup of current version started");
		// TODO Working in Ecllipse but not in zip
		/*
		 * FileUtils.copyDirectory( FileUtils.getFile(backUpFolder.getAbsolutePath() +
		 * SLASH + FilenameUtils.getName(binFolder)),
		 * FileUtils.getFile(FilenameUtils.getName(binFolder)));
		 * FileUtils.copyDirectory( FileUtils.getFile(backUpFolder.getAbsolutePath() +
		 * SLASH + FilenameUtils.getName(libFolder)),
		 * FileUtils.getFile(FilenameUtils.getName(libFolder)));
		 * FileUtils.copyFile(FileUtils.getFile(backUpFolder.getAbsolutePath()+SLASH+
		 * FilenameUtils.getName(manifestFile)),
		 * FileUtils.getFile(FilenameUtils.getName(manifestFile)));
		 */

		FileUtils.copyDirectory(new File(backUpFolder.getAbsolutePath() + SLASH + binFolder), new File(binFolder));
		FileUtils.copyDirectory(new File(backUpFolder.getAbsolutePath() + SLASH + libFolder), new File(libFolder));

		FileUtils.copyFile(new File(backUpFolder.getAbsolutePath() + SLASH + manifestFile), new File(manifestFile));
		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Replacing Backup of current version completed");
	}

	private void rollback(ResponseDTO responseDTO, String previousVersion) {

		File file = FileUtils.getFile(backUpPath);

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Backup Path found : " + file.exists());

		boolean isBackUpCompleted = false;

		if (file.exists()) {
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
		}

		if (!isBackUpCompleted) {
			setErrorResponse(responseDTO, RegistrationConstants.BACKUP_PREVIOUS_FAILURE, null);
		}
	}

	/**
	 * This method will return the checksum of the jars by reading it from the
	 * Manifest file.
	 * 
	 * @param jarName
	 *            jarName
	 * @param manifest
	 *            localManifestFile
	 * @return String - the checksum
	 */
	public String getCheckSum(String jarName, Manifest manifest) {

		// Get Local manifest
		manifest = manifest != null ? manifest : localManifest;

		String checksum = null;

		if (manifest == null) {

			try {
				manifest = getLocalManifest();

			} catch (IOException exception) {
				LOGGER.error(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));

			}
		}

		if (manifest != null) {
			checksum = (String) manifest.getEntries().get(jarName).get(Attributes.Name.CONTENT_TYPE);
		}

		// checksum (content-type)
		return checksum;
	}

	private void addProperties(String version) {

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Started updating version property in mosip-application.properties");

		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(propsFilePath));

			properties.setProperty("mosip.reg.version", version);

			// update mosip-Version in mosip-application.properties file
			try (FileOutputStream outputStream = new FileOutputStream(propsFilePath)) {

				properties.store(outputStream, version);
			}

		} catch (IOException ioException) {

			LOGGER.error(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

		}

		LOGGER.info(LoggerConstants.LOG_REG_UPDATE, APPLICATION_NAME, APPLICATION_ID,
				"Completed updating version property in mosip-application.properties");

	}

}
