package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.REG_UI_LOGIN_LOADER_EXCEPTION;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;
import io.mosip.registration.util.biometric.FingerprintFacade;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
	private GlobalParamService globalParamService;
	
	@Autowired
	protected FXComponents fXComponents;
	
	protected ApplicationContext applicationContext = ApplicationContext.getInstance();
	
	protected Scene scene;
	
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(BaseController.class);

	/**
	 * Adding events to the stage
	 * 
	 * @return
	 */
	protected Stage getStage() {
		EventHandler<Event> event = new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				SchedulerUtil.setCurrentTimeToStartTime();
			}
		};
		fXComponents.getStage().addEventHandler(EventType.ROOT, event);
		return fXComponents.getStage();
	}
	
	protected Scene getScene(Parent borderPane) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		scene = fXComponents.getScene(); 
		if(scene == null) {
			scene = new Scene(borderPane, 950, 630);
			fXComponents.setScene(scene);
		}
		scene.setRoot(borderPane);
		fXComponents.getStage().setScene(scene);
		scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
		return scene;
	}

	/**
	 * Loading FXML files along with beans
	 * 
	 * @return
	 */
	public static <T> T load(URL url) throws IOException {
		clearDeviceOnboardingContext();
		FXMLLoader loader = new FXMLLoader(url);
		loader.setControllerFactory(Initialization.getApplicationContext()::getBean);
		return loader.load();
	}

	/**
	 * Loading FXML files along with beans
	 * 
	 * @return
	 */
	public static <T> T load(URL url, ResourceBundle resource) throws IOException {
		FXMLLoader loader = new FXMLLoader(url, resource);
		loader.setControllerFactory(Initialization.getApplicationContext()::getBean);
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
	protected void generateAlert(String title, String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(context);
		alert.setTitle(title);
		alert.setGraphic(null);
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
	protected void getGlobalParams() {
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());
	}

	/**
	 * 
	 * Opens the home page screen
	 * 
	 * @throws RegBaseCheckedException
	 * 
	 */
	public void goToHomePage() throws RegBaseCheckedException {
		try {
			BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
	}

	public static FXMLLoader loadChild(URL url) throws IOException {
		FXMLLoader loader = new FXMLLoader(url);
		loader.setControllerFactory(Initialization.getApplicationContext()::getBean);
		return loader;
	}

	/**
	 * Gets the finger print status.
	 *
	 * @param PrimaryStage the primary stage
	 * @return the finger print status
	 */
	public void getFingerPrintStatus(Stage primaryStage) {
	
	}
	
	/**
	 * Scans documents
	 *
	 * @param popupStage the stage
	 */
	public void scan(Stage popupStage) {
		
	}

	/**
	 * This method is for saving the Applicant Image and Exception Image which are
	 * captured using webcam
	 * 
	 * @param capturedImage BufferedImage that is captured using webcam
	 * @param imageType     Type of image that is to be saved
	 */
	public void saveApplicantPhoto(BufferedImage capturedImage, String imageType) {
		// will be implemented in the derived class.
	}

	/**
	 * This method used to clear the images that are captured using webcam
	 * 
	 * @param imageType Type of image that is to be cleared
	 */
	public void clearPhoto(String imageType) {
		// will be implemented in the derived class.
	}

	private static void clearDeviceOnboardingContext() {
		if (SessionContext.getInstance().getMapObject() != null) {
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_DEVICES_MAP);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.ONBOARD_DEVICES_MAP_UPDATED);
		}
	}

	/**
	 * it will wait for the mentioned time to get the capture image from Bio Device.
	 * @param count
	 * @param waitTimeInSec
	 * @param fingerprintFacade
	 */
	protected void waitToCaptureBioImage(int count, int waitTimeInSec, FingerprintFacade fingerprintFacade ) {
		int counter = 0;
		while (counter < 5) {
			if (!RegistrationConstants.EMPTY.equals(fingerprintFacade.getMinutia())
					|| !RegistrationConstants.EMPTY.equals(fingerprintFacade.getErrorMessage())) {
				break;
			} else {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					LOGGER.error("FINGERPRINT_AUTHENTICATION_CONTROLLER - ERROR_SCANNING_FINGER", APPLICATION_NAME,
							APPLICATION_ID, e.getMessage());
				}
			}
			counter++;
		}
	}
	
	protected Image convertBytesToImage(byte[] imageBytes) {
		return new Image(new ByteArrayInputStream(imageBytes));
	}
	
	protected Timer onlineAvailabilityCheck() {
		Timer timer = new Timer();
		fXComponents.setTimer(timer);
		return timer;
	}

	protected void stopTimer() {
		if (fXComponents.getTimer() != null) {
			fXComponents.getTimer().cancel();
			fXComponents.getTimer().purge();
			fXComponents.setTimer(null);
		}
	}

	public Timer getTimer() {
		return fXComponents.getTimer() == null ? onlineAvailabilityCheck() : fXComponents.getTimer();
	}
	
}
