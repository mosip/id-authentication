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
public class ClientJarDecryption extends Application {

	private static final String SLASH = "/";
	private static final String AES_ALGORITHM = "AES";
	private static final String MOSIP_CLIENT = "mosip-client.jar";
	private static final String MOSIP_SERVICES = "mosip-services.jar";
	private static String libFolder = "lib/";
	private static String binFolder = "bin/";

	ProgressBar progressBar = new ProgressBar();
	Stage primaryStage = new Stage();

	static String tempPath;

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

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("before Decryption");
		ClientJarDecryption aesDecrypt = new ClientJarDecryption();
		RegistrationUpdate registrationUpdate = new RegistrationUpdate();

		String propsFilePath = new File(System.getProperty("user.dir")) + "/props/mosip-application.properties";
		System.out.println("properties file path--->>");
		FileInputStream fileInputStream = new FileInputStream(propsFilePath);
		Properties properties = new Properties();
		properties.load(fileInputStream);
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
			Task<Void> task = new Task<Void>() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see javafx.concurrent.Task#call()
				 */
				@Override
				protected Void call() throws IOException, InterruptedException {
					try {
						checkForJars();
					} catch (io.mosip.kernel.core.exception.IOException | ParserConfigurationException
							| SAXException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			activateProgressBar(task);
			Thread t=new Thread(task);
			t.start();
			task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					System.out.println("Inside Task thread--->>>>");
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
						decryptedRegFileBytes = aesDecrypt.decrypt(FileUtils.readFileToByteArray(encryptedClientJar),
								Base64.getDecoder().decode("bBQX230Wskq6XpoZ1c+Ep1D+znxfT89NxLQ7P4KFkc4="));
					

					String clientJar = tempPath + SLASH + UUID.randomUUID();
					System.out.println("clientJar ---> " + clientJar);
					FileUtils.writeByteArrayToFile(new File(clientJar + ".jar"), decryptedRegFileBytes);

					System.out.println("Decrypt File Name====>" + encryptedServicesJar.getName());
					byte[] decryptedRegServiceBytes = aesDecrypt.decrypt(FileUtils.readFileToByteArray(encryptedServicesJar),
							Base64.getDecoder().decode("bBQX230Wskq6XpoZ1c+Ep1D+znxfT89NxLQ7P4KFkc4="));

					FileUtils.writeByteArrayToFile(new File(tempPath + SLASH + UUID.randomUUID() + ".jar"),
							decryptedRegServiceBytes);

					String libPath = new File("lib").getAbsolutePath();
					String cmd = "java -Dspring.profiles.active=qa -Dfile.encoding=UTF-8 -Dmosip.dbpath="+properties.getProperty("mosip.dbpath")+" -cp " + tempPath + "/*;" + libPath
							+ "/* io.mosip.registration.controller.Initialization";
					System.out.println("Command-->>" + cmd);
					Process process = Runtime.getRuntime().exec(cmd);
					System.out.println("the output stream is " + process.getOutputStream().getClass());
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String s;
					while ((s = bufferedReader.readLine()) != null) {
						System.out.println("The stream is : " + s);
					}
					if (0 == process.waitFor()) {

						process.destroyForcibly();

						FileUtils.deleteDirectory(new File(tempPath));

					}
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
		}
	}
	
	private void activateProgressBar(final Task<?> task) {
		progressBar.progressProperty().bind(task.progressProperty());
		primaryStage.show();
	}

	private void showDialog() {

		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER);
		stackPane.getChildren().add(progressBar);
		Scene scene = new Scene(stackPane, 400, 500);
		primaryStage.setScene(scene);
	}
}