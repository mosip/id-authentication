package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.device.FingerPrintCaptureController;
import io.mosip.registration.controller.device.IrisCaptureController;
import io.mosip.registration.controller.reg.BiometricExceptionController;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;
import io.mosip.registration.service.template.NotificationService;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
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
import javafx.scene.layout.Region;
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

	@Autowired
	private FingerPrintCaptureController fingerPrintCaptureController;
	@Autowired
	private BiometricExceptionController biometricExceptionController;
	@Autowired
	private IrisCaptureController irisCaptureController;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	@Autowired
	private TemplateGenerator templateGenerator;
	
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
		FXMLLoader loader = new FXMLLoader(url, ApplicationContext.applicationLanguageBundle());
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
	 * @param title
	 *            alert title
	 * @param alertType
	 *            type of alert
	 * @param header
	 *            alert header
	 * @param context
	 *            alert context
	 */
	protected void generateAlert(String title, String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(context);
		alert.setTitle(title);
		alert.setGraphic(null);
		alert.setResizable(true);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

	/**
	 * 
	 * /* Alert creation with specified title, header, and context
	 * 
	 * @param alertType
	 *            type of alert
	 * @param header
	 *            alert header
	 * @param context
	 *            alert context
	 */
	protected void generateAlert(String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(context);
		alert.setGraphic(null);
		alert.setResizable(true);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
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
		if (isConsolidated.equals(RegistrationConstants.DISABLE)) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText(null);
			alert.setContentText(context);
			alert.setGraphic(null);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.setResizable(true);
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
	 * @param screenId
	 *            the screenId
	 * @return boolean
	 */
	protected boolean validateScreenAuthorization(String screenId) {

		return SessionContext.userContext().getAuthorizationDTO().getAuthorizationScreenId().contains(screenId);
	}

	/**
	 * Regex validation with specified field and pattern
	 * 
	 * @param field
	 *            concerned field
	 * @param regexPattern
	 *            pattern need to checked
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
	 * Get the details form Global Param Map is the values existed or not
	 * 
	 * @return Response DTO
	 */
	protected ResponseDTO getSyncConfigData() {
		return globalParamService.synchConfigData();
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
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_HOME_PAGE);
		}
	}

	/**
	 * This method is used clear all the new registration related mapm values and
	 * navigates to the home page
	 * 
	 * 
	 */
	public void goToHomePageFromRegistration() {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to home page");

		clearRegistrationData();

		goToHomePage();
	}

	protected void clearRegistrationData() {

		SessionContext.map().remove(RegistrationConstants.REGISTRATION_ISEDIT);
		SessionContext.map().remove(RegistrationConstants.REGISTRATION_PANE1_DATA);
		SessionContext.map().remove(RegistrationConstants.REGISTRATION_PANE2_DATA);
		SessionContext.map().remove(RegistrationConstants.REGISTRATION_AGE_DATA);
		SessionContext.map().remove(RegistrationConstants.REGISTRATION_DATA);
		SessionContext.map().remove(RegistrationConstants.IS_Child);
		SessionContext.map().remove("dd");
		SessionContext.map().remove("mm");
		SessionContext.map().remove("yyyy");
		SessionContext.map().remove("toggleAgeOrDob");
		SessionContext.map().remove(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION);
		SessionContext.map().remove(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION);

		SessionContext.map().remove("demographicDetail");
		SessionContext.map().remove("documentScan");
		SessionContext.map().remove("fingerPrintCapture");
		SessionContext.map().remove("biometricException");
		SessionContext.map().remove("faceCapture");
		SessionContext.map().remove("irisCapture");
		SessionContext.map().remove("operatorAuthentication");
		SessionContext.map().remove("registrationPreview");

		SessionContext.userMap().remove(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);
		SessionContext.map().remove(RegistrationConstants.DUPLICATE_FINGER);
	}

	protected void clearOnboardData() {
		SessionContext.map().put(RegistrationConstants.ONBOARD_USER_UPDATE, false);
		SessionContext.map().put(RegistrationConstants.ONBOARD_USER, false);
		SessionContext.map().remove(RegistrationConstants.USER_ONBOARD_DATA);
		SessionContext.map().remove(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION);
		SessionContext.map().remove(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION);
	}

	public static FXMLLoader loadChild(URL url) {
		FXMLLoader loader = new FXMLLoader(url, ApplicationContext.applicationLanguageBundle());
		loader.setControllerFactory(Initialization.getApplicationContext()::getBean);
		return loader;
	}

	/**
	 * Gets the finger print status.
	 *
	 * @param PrimaryStage
	 *            the primary stage
	 * @return the finger print status
	 */
	public void updateAuthenticationStatus() {

	}

	/**
	 * Scans documents
	 *
	 * @param popupStage
	 *            the stage
	 */
	public void scan(Stage popupStage) {

	}

	/**
	 * This method is for saving the Applicant Image and Exception Image which are
	 * captured using webcam
	 * 
	 * @param capturedImage
	 *            BufferedImage that is captured using webcam
	 * @param imageType
	 *            Type of image that is to be saved
	 */
	public void saveApplicantPhoto(BufferedImage capturedImage, String imageType) {
		// will be implemented in the derived class.
	}

	/**
	 * This method used to clear the images that are captured using webcam
	 * 
	 * @param imageType
	 *            Type of image that is to be cleared
	 */
	public void clearPhoto(String imageType) {
		// will be implemented in the derived class.
	}

	private static void clearDeviceOnboardingContext() {
		if (SessionContext.isSessionContextAvailable()) {
			SessionContext.map().remove(RegistrationConstants.ONBOARD_DEVICES_MAP);
			SessionContext.map().remove(RegistrationConstants.ONBOARD_DEVICES_MAP_UPDATED);
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
		Image image = null;
		if (imageBytes != null) {
			image = new Image(new ByteArrayInputStream(imageBytes));
		}
		return image;
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

		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID, "Validating Password");

		if (password.isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PWORD_FIELD_EMPTY);
			return RegistrationUIConstants.PWORD_FIELD_EMPTY;
		} else if (password.length() > usernamePwdLength) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PWORD_LENGTH);
			return RegistrationUIConstants.PWORD_LENGTH;
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

			if (validatePassword(authenticationValidatorDTO).equals(RegistrationConstants.PWD_MATCH)) {
				return RegistrationConstants.SUCCESS;
			}
			return RegistrationConstants.FAILURE;
		}
	}

	/**
	 * to validate the password and send appropriate message to display
	 * 
	 * @param authenticationValidatorDTO
	 *            - DTO which contains the username and password entered by the user
	 * @return appropriate message after validation
	 */
	private String validatePassword(AuthenticationValidatorDTO authenticationValidatorDTO) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating credentials using database");

		UserDetail userDetail = loginService.getUserDetail(authenticationValidatorDTO.getUserId());
		if (userDetail.getUserPassword().getPwd().equals(authenticationValidatorDTO.getPassword())) {
			return RegistrationConstants.PWD_MATCH;
		} else {
			return RegistrationConstants.PWD_MISMATCH;
		}
	}

	protected void clearAllValues() {
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			((BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA))
					.setOperatorBiometricDTO(createBiometricInfoDTO());
			biometricExceptionController.clearSession();
		} else {
			((RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO()
					.setApplicantBiometricDTO(createBiometricInfoDTO());
			biometricExceptionController.clearSession();
			fingerPrintCaptureController.clearFingerPrintDTO();
			irisCaptureController.clearIrisData();
		}
	}

	protected BiometricInfoDTO createBiometricInfoDTO() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setBiometricExceptionDTO(new ArrayList<>());
		biometricInfoDTO.setFingerprintDetailsDTO(new ArrayList<>());
		biometricInfoDTO.setIrisDetailsDTO(new ArrayList<>());
		FaceDetailsDTO obj = new FaceDetailsDTO();
		biometricInfoDTO.setFaceDetailsDTO(obj);
		return biometricInfoDTO;
	}

	private Writer getNotificationTemplate() {
		RegistrationDTO registrationDTO = getRegistrationDTOFromSession();
		Writer writeNotificationTemplate = new StringWriter();
		try {
			// network availability check
			if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				// get the mode of communication
				String notificationServiceName = String.valueOf(
						applicationContext.getApplicationMap().get(RegistrationConstants.MODE_OF_COMMUNICATION));

				if (notificationServiceName != null && !notificationServiceName.equals("NONE")) {
					// get the data for notification template
					String notificationTemplate = templateService
							.getHtmlTemplate(RegistrationConstants.NOTIFICATION_TEMPLATE);
					if (!notificationTemplate.isEmpty()) {
						// generate the notification template
						writeNotificationTemplate = templateGenerator.generateNotificationTemplate(notificationTemplate,
								registrationDTO, templateManagerBuilder);
					}
				}
			}
		} catch (RegBaseCheckedException regBaseCheckedException) {
			LOGGER.error("REGISTRATION - UI - GENERATE_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
					regBaseCheckedException.getMessage());
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error("REGISTRATION - UI - GENERATE_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
					regBaseUncheckedException.getMessage());
		}
		return writeNotificationTemplate;
	}

	public ResponseDTO sendSMSNotification(String mobile) {
		RegistrationDTO registrationDTO = getRegistrationDTOFromSession();
		ResponseDTO smsNotificationResponse = new ResponseDTO();
		try {
			String notificationServiceName = String
					.valueOf(applicationContext.getApplicationMap().get(RegistrationConstants.MODE_OF_COMMUNICATION));
			Writer writeNotificationTemplate = getNotificationTemplate();

			String rid = registrationDTO.getRegistrationId();

			if (mobile != null && notificationServiceName.contains(RegistrationConstants.SMS_SERVICE.toUpperCase())) {
				// send sms
				smsNotificationResponse = notificationService.sendSMS(writeNotificationTemplate.toString(), mobile,
						rid);
			}
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error("REGISTRATION - UI - GENERATE_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
					regBaseUncheckedException.getMessage());
		}
		return smsNotificationResponse;
	}

	public ResponseDTO sendEmailNotification(String email) {
		RegistrationDTO registrationDTO = getRegistrationDTOFromSession();
		ResponseDTO emailNotificationResponse = new ResponseDTO();
		try {
			String notificationServiceName = String
					.valueOf(applicationContext.getApplicationMap().get(RegistrationConstants.MODE_OF_COMMUNICATION));
			Writer writeNotificationTemplate = getNotificationTemplate();
			String rid = registrationDTO.getRegistrationId();

			if (email != null && notificationServiceName.contains(RegistrationConstants.EMAIL_SERVICE.toUpperCase())) {
				// send email
				emailNotificationResponse = notificationService.sendEmail(writeNotificationTemplate.toString(), email,
						rid);
			}
		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error("REGISTRATION - UI - GENERATE_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
					regBaseUncheckedException.getMessage());
		}
		return emailNotificationResponse;
	}

	private RegistrationDTO getRegistrationDTOFromSession() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}
}
