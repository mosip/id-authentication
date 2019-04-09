package io.mosip.registration.cipher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;
import io.mosip.registration.config.RegistrationUpdate;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Decryption the Client Jar with Symmetric Key
 * 
 * @author Omsai Eswar M.
 *
 */
public class CilentJarDecryption extends Application {

	private static final String SLASH = "/";
	private static final String AES_ALGORITHM = "AES";
	private static final String MOSIP_CLIENT = "mosip-client.jar";
	private static final String MOSIP_SERVICES = "mosip-services.jar";
	private static String libFolder = "lib/";
	private static String binFolder = "bin/";

	private Service<String> taskService;

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
				Cipher.DECRYPT_MODE);
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

	private static void checkForJars()
			throws IOException, ParserConfigurationException, SAXException, io.mosip.kernel.core.exception.IOException {
		RegistrationUpdate registrationUpdate = new RegistrationUpdate();

		if (registrationUpdate.getCurrentVersion() != null && registrationUpdate.hasRequiredJars()) {

			// TODO Decrypt Client and Services

		} else {
			// TODO Internet Required
			registrationUpdate.getWithLatestJars();
		}

	}

	private static boolean setProperties() throws IOException {

		String propsFilePath = new File(System.getProperty("user.dir")) + "/props/mosip-application.properties";

		FileInputStream fileInputStream = new FileInputStream(propsFilePath);
		Properties properties = new Properties();
		properties.load(fileInputStream);

		System.setProperty("reg.db.path", properties.getProperty("mosip.dbpath"));

		String dbpath = new File(System.getProperty("user.dir")) + properties.getProperty("mosip.dbpath");

		if (new File(dbpath).exists()) {
			System.setProperty("reg.db.path", properties.getProperty("mosip.dbpath"));
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER);
		ProgressBar progressBar = new ProgressBar();
		stackPane.getChildren().add(progressBar);
		progressBar.setVisible(false);
		Scene scene = new Scene(stackPane, 400, 500);

		primaryStage.setScene(scene);
		primaryStage.show();

		/**
		 * This anonymous service class will do the pre application launch task
		 * progress.
		 * 
		 */
		taskService = new Service<String>() {
			@Override
			protected Task<String> createTask() {
				return new Task<String>() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see javafx.concurrent.Task#call()
					 */
					@Override
					protected String call() throws IOException, InterruptedException {
						progressBar.setVisible(true);
						System.out.println("before Decryption");
						CilentJarDecryption aesDecrypt = new CilentJarDecryption();
						RegistrationUpdate registrationUpdate = new RegistrationUpdate();

						if (!setProperties()) {
							return "NOTEXISTS";
						}

						// TODO Check Internet Connectivity

						try {
							checkForJars();
						} catch (io.mosip.kernel.core.exception.IOException | ParserConfigurationException
								| SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						File encryptedClientJar = new File(binFolder + MOSIP_CLIENT);

						File encryptedServicesJar = new File(binFolder + MOSIP_SERVICES);

						String tempPath = FileUtils.getTempDirectoryPath();
						tempPath = tempPath + UUID.randomUUID();

						System.out.println(tempPath);

						System.out.println("Decrypt File Name====>" + encryptedClientJar.getName());
						byte[] decryptedRegFileBytes = aesDecrypt.decrypt(
								FileUtils.readFileToByteArray(encryptedClientJar),
								Base64.getDecoder().decode("bBQX230Wskq6XpoZ1c+Ep1D+znxfT89NxLQ7P4KFkc4="));

						String clientJar = tempPath + SLASH + UUID.randomUUID();
						System.out.println("clientJar ---> " + clientJar);
						FileUtils.writeByteArrayToFile(new File(clientJar + ".jar"), decryptedRegFileBytes);

						System.out.println("Decrypt File Name====>" + encryptedServicesJar.getName());
						byte[] decryptedRegServiceBytes = aesDecrypt.decrypt(
								FileUtils.readFileToByteArray(encryptedServicesJar),
								Base64.getDecoder().decode("bBQX230Wskq6XpoZ1c+Ep1D+znxfT89NxLQ7P4KFkc4="));

						FileUtils.writeByteArrayToFile(new File(tempPath + SLASH + UUID.randomUUID() + ".jar"),
								decryptedRegServiceBytes);
						try {

							String libPath = new File("lib").getAbsolutePath();

							Process process = Runtime.getRuntime()
									.exec("java -Dspring.profiles.active=dev -Djava.ext.dirs=" + libPath + ";"
											+ tempPath + ";" + "C:/Program%20Files/Java/jre1.8.0_181/lib/ext" + " -jar "
											+ clientJar + ".jar");
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
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						return "";
					}
				};
			}
		};
		progressBar.progressProperty().bind(taskService.progressProperty());
		taskService.start();
		taskService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				progressBar.setVisible(false);
				primaryStage.close();

				if ("NOTEXISTS".equalsIgnoreCase(taskService.getValue())) {
					System.out.println("coming alert");
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Please provide correct path for Database");
					alert.setTitle("INFO");
					alert.setGraphic(null);
					alert.setResizable(true);
					alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
					alert.showAndWait();
				}
			}
		});

	}
}