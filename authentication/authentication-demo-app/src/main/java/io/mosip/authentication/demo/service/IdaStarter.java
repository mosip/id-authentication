package io.mosip.authentication.demo.service;
import java.io.FileInputStream;
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
public class IdaStarter extends Application
{
	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException
	{
		// Create the FXMLLoader
		FXMLLoader loader = new FXMLLoader();
		// Path to the FXML File
			String fxmlDocPath = "C:\\Users\\M1047395\\Desktop\\demosip\\demosip\\src\\main\\resources\\fxml\\idaFXML.fxml";
		FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);

		// Create the Pane and all Details
		GridPane root = (GridPane) loader.load(fxmlStream);

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
