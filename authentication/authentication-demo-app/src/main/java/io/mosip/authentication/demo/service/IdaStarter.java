package io.mosip.authentication.demo.service;

import java.io.IOException;

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
public class IdaStarter extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		// Create the FXMLLoader
		FXMLLoader loader = new FXMLLoader();

		// Create the Pane and all Details
		GridPane root = loader.load(this.getClass().getClassLoader().getResourceAsStream("fxml/idaFXML.fxml"));

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
