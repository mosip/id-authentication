package io.mosip.authentication.demo.service;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import io.mosip.authentication.demo.helper.CryptoUtility;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * The Class IdaStarter.
 * 
 * @author Sanjay Murali
 */
@SuppressWarnings("restriction")
@SpringBootApplication
@Import({CryptoCore.class, CryptoUtility.class})
public class IdaStarter extends Application {

	private ConfigurableApplicationContext context;
	FXMLLoader loader = new FXMLLoader();
	GridPane root;

	public static void main(String[] args) {
		Application.launch(IdaStarter.class, args);
	}

	@Override
	public void init() throws Exception {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(IdaStarter.class);
		context = builder.run(getParameters().getRaw().toArray(new String[0]));

		loader.setControllerFactory(context::getBean);
		root = loader.load(this.getClass().getClassLoader().getResourceAsStream("fxml/idaFXML.fxml"));
	}

	@Override
	public void start(Stage stage) throws IOException {
		// Create the Scene
		Scene scene = new Scene(root);
		// Set the Scene to the Stage
		stage.setScene(scene);
		// Set the Title to the Stage
		stage.setTitle("IDA");
		// Display the Stage
		stage.show();
	}
}
