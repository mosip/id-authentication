package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;
import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Screen;
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

	@Autowired
	private LoginService loginService;

	@Value("${USERNAME_PWD_LENGTH}")
	private int usernamePwdLength;

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

	protected void loadScreen(String screen) throws IOException {
		Parent createRoot = BaseController.load(getClass().getResource(screen),
				applicationContext.getApplicationLanguageBundle());
		getScene(createRoot);
	}
	
	protected Scene getScene(Parent borderPane) {
		
		if (!borderPane.getId().equals("loginScreen")) {
			Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
			borderPane.setLayoutX((fXComponents.getStage().getWidth()-900)/2);
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		scene = fXComponents.getScene();
		if (scene == null) {
			scene = new Scene(borderPane);
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
	
	/**
	 * 
	 * /* Alert creation with specified title, header, and context
	 * 
	 * @param alertType type of alert
	 * @param header    alert header
	 * @param context   alert context
	 */
	protected void generateAlert( String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(context);
		alert.setGraphic(null);
		alert.showAndWait();
	}
	
	/**
	 * 
	 * /* Alert creation with specified context
	 * 
	 * @param alertType
	 *            type of alert
	 * @param context
	 *            alert context
	 */
	protected void generateAlert(String context, String isConsolidated, StringBuilder validationMessage) {
		if (isConsolidated.equals(RegistrationConstants.INDIVIDUAL_VALIDATION)) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setContentText(context);
			alert.setGraphic(null);
			alert.showAndWait();
		} else {
			validationMessage.append("* ").append(context).append(System.getProperty("line.separator"));
		}
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
	public void goToHomePage() {
		try {
			BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
		} catch (IOException | RuntimeException exception) {
			LOGGER.error("REGISTRATION - REDIRECTHOME - BASE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.UNABLE_LOAD_HOME_PAGE);
		}
	}

	/**
	 * This method is used clear all the new registration related mapm values and
	 * navigates to the home page
	 * 
	 * 
	 */
	public void goToHomePageFromRegistration() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to home page");

		clearRegistrationData();

		goToHomePage();
	}

	protected void clearRegistrationData() {
		SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_ISEDIT);
		SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_PANE1_DATA);
		SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_PANE2_DATA);
		SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_AGE_DATA);
		SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_DATA);
		SessionContext.getInstance().getUserContext().getUserMap()
				.remove(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);
		SessionContext.getInstance().getMapObject().remove(RegistrationConstants.DUPLICATE_FINGER);
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
	public void updateAuthenticationStatus() {

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
	 * 
	 * @param count
	 * @param waitTimeInSec
	 * @param fingerprintFacade
	 */
	protected void waitToCaptureBioImage(int count, int waitTimeInSec, FingerprintFacade fingerprintFacade) {
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

	/**
	 * to validate the password in case of password based authentication
	 */
	protected String validatePwd(String username, String password) {

		LOGGER.debug("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID, "Validating Password");

		String validationStatus = "";
		if (username.isEmpty() && password.isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.CREDENTIALS_FIELD_EMPTY);
		} else if (username.isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
		} else if (password.isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.PWORD_FIELD_EMPTY);
		} else if (username.length() > usernamePwdLength) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USRNAME_PWORD_LENGTH);
		} else if (password.length() > usernamePwdLength) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USRNAME_PWORD_LENGTH);
		} else {
			String hashPassword = null;

			// password hashing
			if (!(password.isEmpty())) {
				byte[] bytePassword = password.getBytes();
				hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));
			}

			AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
			authenticationValidatorDTO.setUserId(username);
			authenticationValidatorDTO.setPassword(hashPassword);
			String userStatus = validatePassword(authenticationValidatorDTO);

			if (userStatus.equals(RegistrationUIConstants.USER_NOT_ONBOARDED)) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USER_NOT_ONBOARDED);
			} else {
				if (userStatus.equals(RegistrationConstants.PWD_MATCH)) {
					validationStatus = "Success";
				} else {
					validationStatus = "Fail";
				}
			}
		}
		return validationStatus;
	}

	/**
	 * to validate the password and send appropriate message to display
	 * 
	 * @param authenticationValidatorDTO - DTO which contains the username and
	 *                                   password entered by the user
	 * @return appropriate message after validation
	 */
	private String validatePassword(AuthenticationValidatorDTO authenticationValidatorDTO) {
		LOGGER.debug("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating credentials using database");

		RegistrationUserDetail userDetail = loginService.getUserDetail(authenticationValidatorDTO.getUserId());
		if (userDetail == null) {
			return RegistrationUIConstants.USER_NOT_ONBOARDED;
		} else if (userDetail.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED)) {
			return RegistrationUIConstants.BLOCKED_USER_ERROR;
		} else if (userDetail.getRegistrationUserPassword().getPwd().equals(authenticationValidatorDTO.getPassword())) {
			return RegistrationConstants.PWD_MATCH;
		} else {
			return RegistrationConstants.PWD_MISMATCH;
		}
	}

}
