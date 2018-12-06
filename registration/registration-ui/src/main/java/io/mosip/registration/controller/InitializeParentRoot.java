package io.mosip.registration.controller;

import org.springframework.stereotype.Component;

import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class InitializeParentRoot {
	private Stage stage;
	private Scene scene;
	public Stage getStage() {
		return stage;
	}
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	public Scene getScene() {
		return scene;
	}
	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
