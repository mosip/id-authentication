package io.mosip.registration.controller;

import java.util.Timer;

import org.springframework.stereotype.Component;

import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class InitializeParentRoot {
	private Stage stage;
	private Scene scene;
	private Timer timer;
	
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
	public Timer getTimer() {
		return timer;
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
}
