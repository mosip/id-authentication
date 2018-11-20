package io.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.GlobalContextParam;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.GlobalContextParamService;
import io.mosip.registration.service.SyncStatusValidatorService;
import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Base class for all controllers
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

@Component
public class BaseController {

	@Autowired
	private SyncStatusValidatorService syncStatusValidatorService;
	@Autowired
	protected AuditFactory auditFactory;
	@Autowired
	private GlobalContextParamService globalContextParamService;

	protected static Stage stage;

	/**
	 * Adding events to the stage
	 * 
	 * @return
	 */
	protected static Stage getStage() {
		EventHandler<Event> event = new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				SchedulerUtil.setCurrentTimeToStartTime();
			}
		};
		stage.addEventHandler(EventType.ROOT, event);
		return stage;
	}

	/**
	 * Loading FXML files along with beans
	 * 
	 * @return
	 */
	public static <T> T load(URL url) throws IOException {
		FXMLLoader loader = new FXMLLoader(url);
		loader.setControllerFactory(RegistrationAppInitialization.getApplicationContext()::getBean);
		return loader.load();
	}

	/**
	 * Loading FXML files along with beans
	 * 
	 * @return
	 */
	public static <T> T load(URL url, ResourceBundle resource) throws IOException {
		FXMLLoader loader = new FXMLLoader(url, resource);
		loader.setControllerFactory(RegistrationAppInitialization.getApplicationContext()::getBean);
		return loader.load();
	}

	/**
	 * 
	 * /* Alert creation with specified title, header, and context
	 * 
	 * @param title     alert title
	 * @param alertType type of alert
	 * @param header    alert header
	 * @param context   alert context
	 */
	protected void generateAlert(String title, AlertType alertType, String header, String context) {
		Alert alert = new Alert(alertType);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.setTitle(title);
		alert.showAndWait();
	}

	/**
	 * Alert creation with specified title and context
	 * 
	 * @param title     alert title
	 * @param alertType type of alert
	 * @param context   alert context
	 */
	protected void generateAlert(String title, AlertType alertType, String context) {
		Alert alert = new Alert(alertType);
		alert.setContentText(context);
		alert.setHeaderText(null);
		alert.setTitle(title);
		alert.showAndWait();

	}

	/**
	 * Alert creation with specified title and context
	 * 
	 * @param alertType type of alert
	 * @param title     alert title
	 * @param header    alert header
	 */
	protected void generateAlert(AlertType alertType, String title, String header) {
		Alert alert = new Alert(alertType);
		alert.setHeaderText(header);
		alert.setContentText(null);
		alert.setTitle(title);
		alert.showAndWait();

	}

	protected ResponseDTO validateSyncStatus() {

		return syncStatusValidatorService.validateSyncStatus();
	}

	/**
	 * Validating Id for Screen Authorization
	 * 
	 * @param screenId the screenId
	 * @return boolean
	 */
	protected boolean validateScreenAuthorization(String screenId) {

		return SessionContext.getInstance().getUserContext().getAuthorizationDTO().getAuthorizationScreenId()
				.contains(screenId);
	}

	/**
	 * Regex validation with specified field and pattern
	 * 
	 * @param field        concerned field
	 * @param regexPattern pattern need to checked
	 */
	protected boolean validateRegex(Control field, String regexPattern) {
		if (field instanceof TextField) {
			if (!((TextField) field).getText().matches(regexPattern))
				return true;
		} else {
			if (field instanceof PasswordField) {
				if (!((PasswordField) field).getText().matches(regexPattern))
					return true;
			}
		}
		return false;
	}

	/**
	 * {@code autoCloseStage} is to close the stage automatically by itself for a
	 * configured amount of time
	 * 
	 * @param stage
	 */
	protected void autoCloseStage(Stage stage) {
		PauseTransition delay = new PauseTransition(Duration.seconds(5));
		delay.setOnFinished(event -> stage.close());
		delay.play();
	}

	/**
	 * {@code globalParams} is to retrieve required global config parameters for
	 * login from config table
	 */
	protected Map<String, Object> globalParams() {
		List<GlobalContextParam> globalContextParam = globalContextParamService
				.findInvalidLoginCount(RegistrationConstants.INVALID_LOGIN_PARAMS);
		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalContextParam.forEach(param -> {
			if (param.getName().equals(RegistrationConstants.INVALID_LOGIN_COUNT)) {
				globalParamMap.put(RegistrationConstants.INVALID_LOGIN_COUNT, param.getVal());
			}
			if (param.getName().equals(RegistrationConstants.INVALID_LOGIN_TIME)) {
				globalParamMap.put(RegistrationConstants.INVALID_LOGIN_TIME, param.getVal());
			}
		});
		return globalParamMap;
	}

	/**
	 * 
	 * Opens the home page screen
	 * 
	 */
	public void goToHomePage() {
		try {
			BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
		} catch (IOException ioException) {
		}
	}

	public static FXMLLoader loadChild(URL url) throws IOException {
		FXMLLoader loader = new FXMLLoader(url);
		loader.setControllerFactory(RegistrationAppInitialization.getApplicationContext()::getBean);
		return loader;
	}

	public void getFingerPrintStatus() {

	}

}
