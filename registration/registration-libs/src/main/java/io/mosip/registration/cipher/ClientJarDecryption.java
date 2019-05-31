package io.mosip.registration.cipher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;
import io.mosip.registration.config.SoftwareInstallationHandler;
import io.mosip.registration.tpm.asymmetric.AsymmetricDecryptionService;
import io.mosip.registration.tpm.asymmetric.AsymmetricEncryptionService;
import io.mosip.registration.tpm.initialize.TPMInitialization;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
	private static final String MOSIP_REGISTRATION_DB_KEY = "mosip.registration.db.key";
	private static final String MOSIP_REGISTRATION_APP_KEY = "mosip.registration.app.key";
	private static final String ENCRYPTED_KEY = "mosip.registration.key.encrypted";
	private static final String IS_KEY_ENCRYPTED = "Y";
	private static final String MOSIP_CLIENT_TPM_AVAILABILITY = "mosip.client.tpm.registration";

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
	public static void main(String[] args) throws IOException, InterruptedException,
			io.mosip.kernel.core.exception.IOException, ParserConfigurationException, SAXException {

		launch(null);
		/*
		 * ProcessBuilder clientBuilder = new ProcessBuilder("java", "-jar", tempPath +
		 * "/mosip/mosip-client.jar");
		 * 
		 * Process process = clientBuilder.start();
		 * 
		 * System.out.println("Invoked suuceessfully");
		 * 
		 * int status = process.waitFor(); if (status == 0) {
		 * System.out.println("Registration Client stopped with the status: " + status);
		 * process.destroy(); FileUtils.deleteDirectory(new File(tempPath + "mosip\\"));
		 * }
		 */
	}

	private static boolean checkForJars(boolean isToBeDownloaded)
			throws IOException, ParserConfigurationException, SAXException, io.mosip.kernel.core.exception.IOException {
		SoftwareInstallationHandler registrationUpdate = new SoftwareInstallationHandler();

		if (registrationUpdate.getCurrentVersion() != null && registrationUpdate.hasRequiredJars()) {

			return true;

		} else if (isToBeDownloaded) {
			System.out.println("Jars Dowloading Starts");
			registrationUpdate.installJars();

			return checkForJars(false);
		}

		return true;

	}

	@Override
	public void start(Stage stage) throws Exception {
		System.out.println("before Decryption");
		ClientJarDecryption aesDecrypt = new ClientJarDecryption();

		String propsFilePath = new File(System.getProperty("user.dir")) + "/props/mosip-application.properties";
		System.out.println("properties file path--->>");
		FileInputStream fileInputStream = new FileInputStream(propsFilePath);
		Properties properties = new Properties();
		properties.load(fileInputStream);

		// Encrypt the Keys
		boolean isTPMAvailable = isTPMAvailable(properties);
		if (isTPMAvailable) {
			encryptRequiredProperties(properties, propsFilePath);
		}

		try {
			String dbpath = new File(System.getProperty("user.dir")) + SLASH + properties.getProperty("mosip.dbpath");
			if (!new File(dbpath).exists()) {
				System.out.println("coming alert");
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

			// TODO Check Internet Connectivity

			showDialog();

			System.out.println("Inside try statement");
			Task<Boolean> task = new Task<Boolean>() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see javafx.concurrent.Task#call()
				 */
				@Override
				protected Boolean call() throws IOException, InterruptedException {
					try {
						return checkForJars(true);
					} catch (io.mosip.kernel.core.exception.IOException | ParserConfigurationException | SAXException
							| IOException exception) {
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
						System.out.println("  Inside Task thread--->>>>");
						primaryStage.close();
						System.out.println("Inside try statement1");

						File encryptedClientJar = new File(binFolder + MOSIP_CLIENT);

						File encryptedServicesJar = new File(binFolder + MOSIP_SERVICES);

						tempPath = FileUtils.getTempDirectoryPath();
						tempPath = tempPath + UUID.randomUUID();

						System.out.println(tempPath);

						System.out.println("Decrypt File Name====>" + encryptedClientJar.getName());
						byte[] decryptedRegFileBytes;
						try {
							byte[] decryptedKey = getValue(MOSIP_REGISTRATION_APP_KEY, properties, isTPMAvailable);

							decryptedRegFileBytes = aesDecrypt
									.decrypt(FileUtils.readFileToByteArray(encryptedClientJar), decryptedKey);

							String clientJar = tempPath + SLASH + UUID.randomUUID();
							System.out.println("clientJar ---> " + clientJar);
							FileUtils.writeByteArrayToFile(new File(clientJar + ".jar"), decryptedRegFileBytes);

							System.out.println("Decrypt File Name====>" + encryptedServicesJar.getName());
							byte[] decryptedRegServiceBytes = aesDecrypt
									.decrypt(FileUtils.readFileToByteArray(encryptedServicesJar), decryptedKey);

							FileUtils.writeByteArrayToFile(new File(tempPath + SLASH + UUID.randomUUID() + ".jar"),
									decryptedRegServiceBytes);

							String libPath = new File("lib").getAbsolutePath();
							String cmd = "java -Dspring.profiles.active=" + properties.getProperty("mosip.env")
									+ " -Dfile.encoding=UTF-8 -Dmosip.dbpath=" + properties.getProperty("mosip.dbpath")
									+ " -D" + MOSIP_REGISTRATION_DB_KEY + "=" + propsFilePath + " -cp " + tempPath
									+ "/*;" + libPath + "/* io.mosip.registration.controller.Initialization";
							System.out.println("Command-->>" + cmd);
							Process process = Runtime.getRuntime().exec(cmd);
							System.out.println("the output stream is " + process.getOutputStream().getClass());
							BufferedReader bufferedReader = new BufferedReader(
									new InputStreamReader(process.getInputStream()));
							String s;
							while ((s = bufferedReader.readLine()) != null) {
								System.out.println("The stream is : " + s);
							}
							if (0 == process.waitFor()) {

								process.destroyForcibly();

								FileUtils.deleteDirectory(new File(tempPath));

							}
						} catch (IOException | InterruptedException | RuntimeException exception) {
							// TODO Auto-generated catch block
							exception.printStackTrace();

							try {
								FileUtils.forceDelete(new File("MANIFEST.MF"));
								new SoftwareInstallationHandler();
							} catch (IOException ioException) {
								ioException.printStackTrace();
							}

						}
					} else {
						System.out.println("Not Downloaded Fully");
						primaryStage.close();
					}
				}
			});
		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
		}
	}

	private boolean isTPMAvailable(Properties properties) {
		return !properties.containsKey(MOSIP_CLIENT_TPM_AVAILABILITY);
	}

	private void activateProgressBar(final Task<?> task) {
		progressBar.progressProperty().bind(task.progressProperty());
		primaryStage.show();
	}

	private void showDialog() {

		StackPane stackPane = new StackPane();
		VBox vBox = new VBox();
		HBox hBox = new HBox();
		File file = new File("logo-final.png");
		ImageView imageView = new ImageView(new Image(file.toURI().toString()));
		imageView.setFitHeight(150);
		imageView.setFitWidth(200);
		hBox.setMinSize(200, 100);
		hBox.getChildren().add(imageView);
		Label downloadLabel = new Label("Downloading..");
		vBox.setMinSize(150, 150);
		vBox.setAlignment(Pos.CENTER_LEFT);
		vBox.setPadding(new Insets(10.0));
		vBox.getChildren().add(downloadLabel);
		vBox.getChildren().add(progressBar);
		hBox.getChildren().add(vBox);
		hBox.setAlignment(Pos.CENTER_LEFT);
		stackPane.getChildren().add(hBox);
		Scene scene = new Scene(stackPane, 255, 150);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);
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
}