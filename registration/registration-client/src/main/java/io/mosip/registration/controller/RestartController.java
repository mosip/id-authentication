package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.service.config.JobConfigurationService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

/**
 * Restart Controller was to restart the application
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Controller
public class RestartController extends BaseController {

	@Autowired
	private JobConfigurationService jobConfigurationService;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RestartController.class);

	/**
	 * Generated alerts list
	 */
	private static List<Alert> generatedAlerts = new LinkedList<>();

	@PostConstruct
	public void initiateRestartTimer() {
		LOGGER.info("REGISTRATION - RESTART  - RESTART CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Initiate Restart Timer started");

		/* Create Sync Restart timer */
		createSyncRestartTimer();

		LOGGER.info("REGISTRATION - RESTART  - RESTART CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Initiate Restart Timer completed");

	}

	public boolean isToBeRestarted() {

		LOGGER.info("REGISTRATION - RESTART  - RESTART CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Is to be restart check started");

		/* Check any eligible restart-able jobs completed with success */
		SuccessResponseDTO successResponseDTO = jobConfigurationService.isRestart().getSuccessResponseDTO();

		/* Is application eligible to restart */
		if (successResponseDTO != null) {

			/*
			 * Remove the particular completed job from completed_job_map as user will
			 * receive alert
			 */
			BaseJob.removeCompletedJobInMap(
					(String) successResponseDTO.getOtherAttributes().get(RegistrationConstants.JOB_ID));

			/* Generate alert */
			Alert restartAlert = createAlert(AlertType.CONFIRMATION, RegistrationUIConstants.SYNC_SUCCESS,
					successResponseDTO.getMessage(), RegistrationUIConstants.RESTART_APPLICATION,
					RegistrationConstants.OK_MSG, RegistrationConstants.CANCEL_MSG);

			generatedAlerts.add(restartAlert);

			restartAlert.showAndWait();

			/* Get Option from user */
			ButtonType result = restartAlert.getResult();
			if (result == ButtonType.OK) {

				/* Clear all other Restart request alerts */
				generatedAlerts.forEach(alert -> {
					alert.close();
				});
				return true;
			}

		}
		LOGGER.info("REGISTRATION - RESTART  - RESTART CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Is to be restart check completed");
		return false;
	}

	/**
	 * Restart the Application
	 */
	public void restart() {
		LOGGER.info("REGISTRATION - RESTART  - RESTART CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Restart started");

		// Registration-Client Termination
		System.exit(0);

		// Need to check with application context
		/*
		 * Close the current application context ((AbstractApplicationContext)
		 * Initialization.getApplicationContext()).close();
		 * 
		 * Close the current java fx stage Initialization.getPrimaryStage().close();
		 * 
		 * Create and Set newly created application context
		 * Initialization.setApplicationContext(new
		 * AnnotationConfigApplicationContext(AppConfig.class));
		 * 
		 * create and set new java fx stage new Initialization().start(new Stage());
		 */

		LOGGER.info("REGISTRATION - RESTART  - RESTART CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Restart completed");
	}

	private void createSyncRestartTimer() {
		LOGGER.info("REGISTRATION - RESTART  - RESTART CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Creation of sync restart timer started");

		/* Get Restart time for timer */
		SuccessResponseDTO successResponseDTO = jobConfigurationService.getRestartTime().getSuccessResponseDTO();

		if (successResponseDTO != null) {
			Timeline syncRestartTimer = new Timeline(
					new KeyFrame(Duration.seconds((int) (Integer.parseInt(successResponseDTO.getMessage()) * 0.001)),
							new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent event) {

									Platform.runLater(() -> {

										LOGGER.info("REGISTRATION - RESTART  - RESTART CONTROLLER", APPLICATION_NAME,
												APPLICATION_ID, "Restart Timer Task restart");

										/* Check whether the user wanted to restart the application */
										while (isToBeRestarted()) {
											/* Clear the completed job map */
											BaseJob.clearCompletedJobMap();

											/* Restart the application */
											restart();
										}

									});
								}
							}));
			syncRestartTimer.setCycleCount(Timeline.INDEFINITE);
			syncRestartTimer.play();
		}

		LOGGER.info("REGISTRATION - RESTART  - RESTART CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Creation of sync restart timer completed");

	}
}
