package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;
import io.mosip.registration.config.SoftwareInstallationHandler;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.tpm.asymmetric.AsymmetricDecryptionService;
import io.mosip.registration.tpm.asymmetric.AsymmetricEncryptionService;
import io.mosip.registration.tpm.initialize.TPMInitialization;
import io.mosip.registration.util.LoggerFactory;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Decryption the Client Jar with Symmetric Key
 * 
 * @author Omsai Eswar M.
 *
 */
public class ClientJarDecryption extends Application {

	private static final String SLASH = "/";
	private static final String AES_ALGORITHM = "AES";
	private static final String MOSIP_CLIENT = "mosip-client.jar";
	private static final String MOSIP_SERVICES = "mosip-services.jar";
	private static String libFolder = "lib/";
	private static String binFolder = "bin/";
	private static final String MOSIP_REGISTRATION_DB_KEY = "mosip.reg.db.key";
	private static final String MOSIP_REGISTRATION_HC_URL = "mosip.reg.healthcheck.url";
	private static final String MOSIP_REGISTRATION_APP_KEY = "mosip.reg.app.key";
	private static final String ENCRYPTED_KEY = "mosip.registration.key.encrypted";
	private static final String IS_KEY_ENCRYPTED = "Y";
	private static final String MOSIP_CLIENT_TPM_AVAILABILITY = "mosip.reg.client.tpm.availability";

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientJarDecryption.class);

	ProgressBar progressBar = new ProgressBar();
	Stage primaryStage = new Stage();

	static String tempPath;
	private AsymmetricEncryptionService asymmetricEncryptionService = new AsymmetricEncryptionService();
	private AsymmetricDecryptionService asymmetricDecryptionService = new AsymmetricDecryptionService();

	/**
	 * Decrypt the bytes
	 * 
	 * @param Jar
	 *            bytes
	 * @throws UnsupportedEncodingException
	 */
	public byte[] decrypt(byte[] data, byte[] encodedString) {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Decryption Started");

		// Generate AES Session Key
		SecretKey symmetricKey = new SecretKeySpec(encodedString, AES_ALGORITHM);

		return SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING, symmetricKey, data,
				Cipher.DECRYPT_MODE, null);
	}

	/**
	 * Decrypt and save the file in temp directory
	 * 
	 * @param args
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 * @throws io.mosip.kernel.core.exception.IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static void main(String[] args) {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started run.jar");

		// Launch Reg-Client and perform necessary actions
		launch(null);

	}

	private static boolean checkForJars(boolean isToBeDownloaded)
			throws IOException, ParserConfigurationException, SAXException, io.mosip.kernel.core.exception.IOException {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Checking for jars started");

		SoftwareInstallationHandler registrationUpdate = new SoftwareInstallationHandler();

		boolean hasJars = false;
		if (registrationUpdate.getCurrentVersion() != null && registrationUpdate.hasRequiredJars()) {

			hasJars = true;

		} else if (isToBeDownloaded) {
			LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
					LoggerConstants.APPLICATION_ID, "Installing of jars started");

			registrationUpdate.installJars();

			return checkForJars(false);
		}

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Checking for jars Completed");

		return hasJars;

	}

	@Override
	public void start(Stage stage) throws Exception {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started JavaFx start");

		System.out.println("Started");
		
		ClientJarDecryption aesDecrypt = new ClientJarDecryption();

		String propsFilePath = new File(System.getProperty("user.dir")) + "/props/mosip-application.properties";

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started loading properties of mosip-application.properties");

		try (FileInputStream fileInputStream = new FileInputStream(propsFilePath)) {
			Properties properties = new Properties();
			properties.load(fileInputStream);

			// Encrypt the Keys
			boolean isTPMAvailable = isTPMAvailable(properties);

			if (isTPMAvailable) {
				encryptRequiredProperties(properties, propsFilePath);
			}

			try {
				String dbpath = new File(System.getProperty("user.dir")) + SLASH
						+ properties.getProperty("mosip.reg.dbpath");

				LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
						LoggerConstants.APPLICATION_ID, "Started DB availability check");

				dbCheck(dbpath);

				// TODO Check Internet Connectivity

				showDialog();

				
				Task<Boolean> task = new Task<Boolean>() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see javafx.concurrent.Task#call()
					 */
					@Override
					protected Boolean call() throws IOException, InterruptedException {
						try {
							LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
									LoggerConstants.APPLICATION_ID, "Started check for jars task");

							return checkForJars(true);
						} catch (io.mosip.kernel.core.exception.IOException | ParserConfigurationException
								| SAXException | IOException exception) {
							return false;
						}

					}
				};
				activateProgressBar(task);
				Thread t = new Thread(task);
				t.start();
				task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent t) {

						if (task.getValue()) {

							File encryptedClientJar = new File(binFolder + MOSIP_CLIENT);

							File encryptedServicesJar = new File(binFolder + MOSIP_SERVICES);

							tempPath = FileUtils.getTempDirectoryPath();
							tempPath = tempPath + UUID.randomUUID();

							byte[] decryptedRegFileBytes;
							try {
								byte[] decryptedKey = getValue(MOSIP_REGISTRATION_APP_KEY, properties, isTPMAvailable);

								LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
										LoggerConstants.APPLICATION_ID, "Decrypting mosip-client");

								decryptedRegFileBytes = aesDecrypt
										.decrypt(FileUtils.readFileToByteArray(encryptedClientJar), decryptedKey);

								String clientJar = tempPath + SLASH + UUID.randomUUID();

								// Decrypt Client Jar
								FileUtils.writeByteArrayToFile(new File(clientJar + ".jar"), decryptedRegFileBytes);

								LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
										LoggerConstants.APPLICATION_ID, "Decrypting mosip-services");

								byte[] decryptedRegServiceBytes = aesDecrypt
										.decrypt(FileUtils.readFileToByteArray(encryptedServicesJar), decryptedKey);

								// Decrypt Services ka
								FileUtils.writeByteArrayToFile(new File(tempPath + SLASH + UUID.randomUUID() + ".jar"),
										decryptedRegServiceBytes);

							} catch (RuntimeException | IOException runtimeException) {

								LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
										LoggerConstants.APPLICATION_ID,
										runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

								try {

									LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
											LoggerConstants.APPLICATION_ID,
											"Deleting manifest file, and jars and decrypted files");

									FileUtils.deleteDirectory(new File(tempPath));
									FileUtils.forceDelete(new File("MANIFEST.MF"));
									new SoftwareInstallationHandler();
								} catch (IOException ioException) {
									LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION,
											LoggerConstants.APPLICATION_NAME, LoggerConstants.APPLICATION_ID,
											runtimeException.getMessage()
													+ ExceptionUtils.getStackTrace(runtimeException));

								}

								LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
										LoggerConstants.APPLICATION_ID, "Terminating the application");

								// EXIT
								exit();
							}

							try {

								String libPath = "\"" + new File("lib").getAbsolutePath() + "\"";

								LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
										LoggerConstants.APPLICATION_ID, "Preparing command to launch the reg-client");

								String cmd = "java -Dspring.profiles.active=" + properties.getProperty("mosip.reg.env")
										+ " -Dmosip.reg.healthcheck.url="
										+ properties.getProperty(MOSIP_REGISTRATION_HC_URL)
										+ " -Dfile.encoding=UTF-8 -Dmosip.reg.dbpath="
										+ properties.getProperty("mosip.reg.dbpath") + " -D" + MOSIP_REGISTRATION_DB_KEY
										+ "=" + "\"" + propsFilePath + "\"" + " -cp " + tempPath + "/*;" + libPath
										+ "/* io.mosip.registration.controller.Initialization";

								Process process = Runtime.getRuntime().exec(cmd);

								process.getInputStream().close();
								process.getOutputStream().close();
								process.getErrorStream().close();

								closeStage();

								if (0 == process.waitFor()) {

									LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
											LoggerConstants.APPLICATION_ID,
											"Started Destroying proccess of reg-client and force deleting the decrypted jars");

									process.destroyForcibly();

									FileUtils.forceDelete(new File(tempPath));

									LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
											LoggerConstants.APPLICATION_ID,
											"Completed Destroying proccess of reg-client and force deleting the decrypted jars");

									exit();
								}
							} catch (RuntimeException | InterruptedException | IOException runtimeException) {
								LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
										LoggerConstants.APPLICATION_ID,
										runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

								closeStage();

								exit();
							}
						} else {

							LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
									LoggerConstants.APPLICATION_ID,
									"Not installed Fully, closing mosip run.jar screen");

							closeStage();

							exit();
						}
					}
				});
			} catch (RuntimeException runtimeException) {
				LOGGER.error(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
						LoggerConstants.APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

			}
		}
	}

	private boolean isTPMAvailable(Properties properties) {
		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started tpm availability check");

		return properties.containsKey(MOSIP_CLIENT_TPM_AVAILABILITY)
				&& String.valueOf(properties.get(MOSIP_CLIENT_TPM_AVAILABILITY)).equalsIgnoreCase(IS_KEY_ENCRYPTED);
	}

	private void activateProgressBar(final Task<?> task) {
		progressBar.progressProperty().bind(task.progressProperty());
		primaryStage.show();
	}

	private void showDialog() {

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Started Loading mosip run.jar screen");

		StackPane stackPane = new StackPane();
		VBox vBox = new VBox();
		HBox hBox = new HBox();
		InputStream ins = this.getClass().getResourceAsStream("/img/logo-final.png");
		ImageView imageView = new ImageView(new Image(ins));
		imageView.setFitHeight(150);
		imageView.setFitWidth(150);
		hBox.setMinSize(200, 400);
		hBox.getChildren().add(imageView);
		Label downloadLabel = new Label("Downloading..");
		vBox.setAlignment(Pos.CENTER_LEFT);
		vBox.getChildren().add(downloadLabel);
		vBox.getChildren().add(progressBar);
		hBox.getChildren().add(vBox);
		hBox.setAlignment(Pos.CENTER_LEFT);
		stackPane.getChildren().add(hBox);
		Scene scene = new Scene(stackPane, 255, 150);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);

		LOGGER.info(LoggerConstants.CLIENT_JAR_DECRYPTION, LoggerConstants.APPLICATION_NAME,
				LoggerConstants.APPLICATION_ID, "Completed Loading mosip run.jar screen");

	}

	private byte[] getValue(String key, Properties properties, boolean isTPMAvailable) {
		byte[] value = CryptoUtil.decodeBase64(properties.getProperty(key));
		if (isTPMAvailable) {
			value = asymmetricDecryptionService.decryptUsingTPM(TPMInitialization.getTPMInstance(), value);
		}
		return value;
	}

	private void encryptRequiredProperties(Properties properties, String propertiesFilePath) throws IOException {
		if (!(properties.containsKey(ENCRYPTED_KEY)
				&& properties.getProperty(ENCRYPTED_KEY).equals(IS_KEY_ENCRYPTED))) {
			try (OutputStream propertiesFile = new FileOutputStream(propertiesFilePath)) {
				properties.put(ENCRYPTED_KEY, IS_KEY_ENCRYPTED);
				properties.put(MOSIP_REGISTRATION_APP_KEY, getEncryptedValue(properties, MOSIP_REGISTRATION_APP_KEY));
				properties.put(MOSIP_REGISTRATION_DB_KEY, getEncryptedValue(properties, MOSIP_REGISTRATION_DB_KEY));
				properties.store(propertiesFile, "Updated");
				propertiesFile.flush();
			}
		}
	}

	private String getEncryptedValue(Properties properties, String key) {
		return CryptoUtil.encodeBase64String(asymmetricEncryptionService.encryptUsingTPM(
				TPMInitialization.getTPMInstance(), Base64.getDecoder().decode(properties.getProperty(key))));
	}

	private void closeStage() {
		primaryStage.close();
	}

	private void exit() {
		System.exit(0);
	}

	private void dbCheck(String dbPath) {
		if (!new File(dbPath).exists()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setContentText("Please provide correct path for Database");
			alert.setTitle("INFO");
			alert.setGraphic(null);
			alert.setResizable(true);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
			throw new RuntimeException();
		}
	}
}