package io.mosip.registration.controller;

import java.util.Timer;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.context.ApplicationContext;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class FXComponents {
	
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(ApplicationContext.class);
	
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
