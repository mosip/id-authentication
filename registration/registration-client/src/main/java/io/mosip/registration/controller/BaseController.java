package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.device.FaceCaptureController;
import io.mosip.registration.controller.device.FingerPrintCaptureController;
import io.mosip.registration.controller.device.GuardianBiometricsController;
import io.mosip.registration.controller.device.IrisCaptureController;
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.controller.device.WebCameraController;
import io.mosip.registration.controller.eodapproval.RegistrationApprovalController;
import io.mosip.registration.controller.reg.AlertController;
import io.mosip.registration.controller.reg.BiometricExceptionController;
import io.mosip.registration.controller.reg.DemographicDetailController;
import io.mosip.registration.controller.reg.HeaderController;
import io.mosip.registration.controller.reg.HomeController;
import io.mosip.registration.controller.reg.PacketHandlerController;
import io.mosip.registration.controller.reg.RegistrationPreviewController;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.remap.CenterMachineReMapService;
import io.mosip.registration.service.security.AuthenticationService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;
import io.mosip.registration.service.template.TemplateService;
import io.mosip.registration.util.acktemplate.TemplateGenerator;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;
import javafx.animation.PauseTransition;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Base class for all controllers.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */

@Component
public class BaseController {

	@Autowired
	private SyncStatusValidatorService syncStatusValidatorService;
	@Autowired
	protected AuditManagerService auditFactory;
	@Autowired
	protected GlobalParamService globalParamService;

	@Autowired
	protected ServiceDelegateUtil serviceDelegateUtil;

	@Autowired
	protected FXComponents fXComponents;

	@Autowired
	private DemographicDetailController demographicDetailController;
	@Autowired
	public RegistrationPreviewController registrationPreviewController;
	@Autowired
	private FingerPrintCaptureController fingerPrintCaptureController;
	@Autowired
	private BiometricExceptionController biometricExceptionController;
	@Autowired
	private IrisCaptureController irisCaptureController;
	@Autowired
	private FaceCaptureController faceCaptureController;
	@Autowired
	private GuardianBiometricsController guardianBiometricsController;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private TemplateManagerBuilder templateManagerBuilder;

	@Autowired
	private TemplateGenerator templateGenerator;

	@Autowired
	private UserOnboardService userOnboardService;

	@Autowired
	private CenterMachineReMapService centerMachineReMapService;

	@Autowired
	private PacketHandlerController packetHandlerController;

	@Autowired
	private HeaderController headerController;

	@Autowired
	private WebCameraController webCameraController;

	@Autowired
	private HomeController homeController;

	@Autowired
	private AlertController alertController;

	@Autowired
	private ScanPopUpViewController scanPopUpViewController;

	@Autowired
	private RegistrationApprovalController registrationApprovalController;

	protected ApplicationContext applicationContext = ApplicationContext.getInstance();

	protected Scene scene;

	private List<String> pageDetails = new ArrayList<>();

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(BaseController.class);

	@Autowired
	private RestartController restartController;

	/**
	 * Adding events to the stage.
	 *
	 * @return the stage
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

	/**
	 * Load screen.
	 *
	 * @param screen the screen
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void loadScreen(String screen) throws IOException {
		Parent createRoot = BaseController.load(getClass().getResource(screen),
				applicationContext.getApplicationLanguageBundle());
		getScene(createRoot);
	}

	/**
	 * Gets the scene.
	 *
	 * @param borderPane the border pane
	 * @return the scene
	 */
	protected Scene getScene(Parent borderPane) {
		scene = fXComponents.getScene();
		if (scene == null) {
			scene = new Scene(borderPane);
			fXComponents.setScene(scene);
		}
		scene.setRoot(borderPane);
		fXComponents.getStage().setScene(scene);
		scene.getStylesheets().add(
				ClassLoader.getSystemClassLoader().getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
		return scene;
	}

	/**
	 * Loading FXML files along with beans.
	 *
	 * @param     <T> the generic type
	 * @param url the url
	 * @return T
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static <T> T load(URL url) throws IOException {
		FXMLLoader loader = new FXMLLoader(url, ApplicationContext.applicationLanguageBundle());
		loader.setControllerFactory(Initialization.getApplicationContext()::getBean);
		return loader.load();
	}

	/**
	 * Loading FXML files along with beans.
	 *
	 * @param          <T> the generic type
	 * @param url      the url
	 * @param resource the resource
	 * @return T
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static <T> T load(URL url, ResourceBundle resource) throws IOException {
		FXMLLoader loader = new FXMLLoader(url, resource);
		loader.setControllerFactory(Initialization.getApplicationContext()::getBean);
		return loader.load();
	}

	/**
	 * /* Alert creation with specified title, header, and context.
	 *
	 * @param title   alert title
	 * @param context alert context
	 */
	protected void generateAlert(String title, String context) {
		try {
			Stage alertStage = new Stage();
			Pane authRoot = BaseController.load(getClass().getResource(RegistrationConstants.ALERT_GENERATION));
			Scene scene = new Scene(authRoot);
			scene.getStylesheets().add(ClassLoader.getSystemClassLoader()
					.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			alertStage.initStyle(StageStyle.UNDECORATED);
			alertStage.setScene(scene);
			alertStage.initModality(Modality.WINDOW_MODAL);
			alertController.getAlertGridPane().setPrefHeight(context.length() / 2 + 110);
			if (scanPopUpViewController.getPopupStage() != null
					&& scanPopUpViewController.getPopupStage().isShowing()) {
				alertController.generateAlertResponse(title, context);
				alertStage.initOwner(scanPopUpViewController.getPopupStage());
				alertStage.showAndWait();
			} else if (registrationApprovalController.getPrimaryStage() != null
					&& registrationApprovalController.getPrimaryStage().isShowing()) {
				alertController.generateAlertResponse(title, context);
				alertStage.initOwner(registrationApprovalController.getPrimaryStage());
				alertStage.showAndWait();
			} else {
				alertStage.initOwner(fXComponents.getStage());
				alertStage.show();
				alertController.generateAlertResponse(title, context);
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - ALERT - BASE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - ALERT - BASE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * /* Alert creation with specified title, header, and context.
	 *
	 * @param title   the title
	 * @param context alert context
	 */
	protected void generateAlertLanguageSpecific(String title, String context) {
		generateAlert(title, RegistrationUIConstants.getMessageLanguageSpecific(context));
	}

	/**
	 * Alert specific for page navigation confirmation
	 * 
	 * @return
	 */
	protected boolean pageNavigantionAlert() {
		if (!fXComponents.getScene().getRoot().getId().equals("mainBox") && !SessionContext.map()
				.get(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ).equals(RegistrationConstants.ENABLE)) {

			Alert alert = createAlert(AlertType.CONFIRMATION, RegistrationUIConstants.INFORMATION,
					RegistrationUIConstants.ALERT_NOTE_LABEL, RegistrationUIConstants.PAGE_NAVIGATION_MESSAGE,
					RegistrationConstants.PAGE_NAVIGATION_CONFIRM, RegistrationConstants.PAGE_NAVIGATION_CANCEL);

			alert.show();
			Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();		
			Double xValue = screenSize.getWidth()/2 - alert.getWidth()+250;
			Double yValue = screenSize.getHeight()/2 - alert.getHeight();
			alert.hide();
			alert.setX(xValue);
			alert.setY(yValue);
			alert.showAndWait();
			/* Get Option from user */
			ButtonType result = alert.getResult();
			if (result == ButtonType.OK) {
				return true;
			}else{
				return false;
			}
		}
		return true;
	}

	/**
	 * Alert creation with specified context.
	 *
	 * @param parentPane the parent pane
	 * @param id         the id
	 * @param context    alert context
	 */
	protected void generateAlert(Pane parentPane, String id, String context) {
		if (id.contains(RegistrationConstants.ONTYPE)) {
			id = id.replaceAll(RegistrationConstants.UNDER_SCORE + RegistrationConstants.ONTYPE,
					RegistrationConstants.EMPTY);
		}
		if (id.matches(RegistrationConstants.DTAE_MONTH_YEAR_REGEX)) {
			id = RegistrationConstants.DOB;
			parentPane = (Pane) parentPane.getParent().getParent();
		}
		Label label = ((Label) (parentPane.lookup(RegistrationConstants.HASH + id + RegistrationConstants.MESSAGE)));
		if (!(label.isVisible() && id.equals(RegistrationConstants.DOB))) {
			String[] split = context.split("#TYPE#");
			label.setText(split[0]);
		}

		Tooltip tool = new Tooltip(context);
		tool.getStyleClass().add(RegistrationConstants.TOOLTIP);
		label.setTooltip(tool);
		label.setVisible(true);

	}

	/**
	 * Validate sync status.
	 *
	 * @return the response DTO
	 */
	protected ResponseDTO validateSyncStatus() {

		return syncStatusValidatorService.validateSyncStatus();
	}

	/**
	 * Validating Id for Screen Authorization.
	 *
	 * @param screenId the screenId
	 * @return boolean
	 */
	protected boolean validateScreenAuthorization(String screenId) {

		return SessionContext.userContext().getAuthorizationDTO().getAuthorizationScreenId().contains(screenId);
	}

	/**
	 * Regex validation with specified field and pattern.
	 *
	 * @param field        concerned field
	 * @param regexPattern pattern need to checked
	 * @return true, if successful
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
	 * configured amount of time.
	 *
	 * @param stage the stage
	 */
	protected void autoCloseStage(Stage stage) {
		PauseTransition delay = new PauseTransition(Duration.seconds(5));
		delay.setOnFinished(event -> stage.close());
		delay.play();
	}

	/**
	 * {@code globalParams} is to retrieve required global config parameters for
	 * login from config table.
	 *
	 */
	protected void getGlobalParams() {
		ApplicationContext.setApplicationMap(globalParamService.getGlobalParams());
	}

	/**
	 * Get the details form Global Param Map is the values existed or not.
	 *
	 * @return Response DTO
	 */
	protected ResponseDTO getSyncConfigData() {
		return globalParamService.synchConfigData(false);
	}

	/**
	 * Opens the home page screen.
	 */
	public void goToHomePage() {
		webCameraController.closeWebcam();
		try {
			if (pageNavigantionAlert()) {
				BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
				if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
					clearOnboardData();
				} else {
					SessionContext.map().put(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ,
							RegistrationConstants.ENABLE);
				}
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REDIRECTHOME - BASE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_HOME_PAGE);
		} catch (RuntimeException runtimException) {
			LOGGER.error("REGISTRATION - REDIRECTHOME - BASE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					runtimException.getMessage() + ExceptionUtils.getStackTrace(runtimException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_HOME_PAGE);
		}
	}

	/**
	 * Opens the home page screen.
	 */
	public void loadLoginScreen() {
		try {
			Parent root = load(getClass().getResource(RegistrationConstants.INITIAL_PAGE));
			getStage().setScene(getScene(root));
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REDIRECTLOGIN - BASE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
		}
	}

	/**
	 * This method is used clear all the new registration related mapm values and
	 * navigates to the home page.
	 */
	public void goToHomePageFromRegistration() {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to home page");

		webCameraController.closeWebcam();
		clearRegistrationData();
		clearOnboardData();
		goToHomePage();

	}

	/**
	 * This method is used clear all the new onboard related mapm values and
	 * navigates to the home page.
	 */
	public void goToHomePageFromOnboard() {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to home page");

		goToHomePage();
	}

	/**
	 * Clear registration data.
	 */
	protected void clearRegistrationData() {

		SessionContext.map().remove(RegistrationConstants.REGISTRATION_ISEDIT);
		SessionContext.map().remove(RegistrationConstants.REGISTRATION_PANE1_DATA);
		SessionContext.map().remove(RegistrationConstants.REGISTRATION_PANE2_DATA);
		SessionContext.map().remove(RegistrationConstants.REGISTRATION_AGE_DATA);
		SessionContext.map().remove(RegistrationConstants.REGISTRATION_DATA);
		SessionContext.map().remove(RegistrationConstants.IS_Child);
		SessionContext.map().remove(RegistrationConstants.DD);
		SessionContext.map().remove(RegistrationConstants.MM);
		SessionContext.map().remove(RegistrationConstants.YYYY);
		SessionContext.map().remove(RegistrationConstants.DOB_TOGGLE);
		SessionContext.map().remove(RegistrationConstants.UIN_UPDATE_DEMOGRAPHICDETAIL);
		SessionContext.map().remove(RegistrationConstants.UIN_UPDATE_DOCUMENTSCAN);
		SessionContext.map().remove(RegistrationConstants.UIN_UPDATE_FINGERPRINTCAPTURE);
		SessionContext.map().remove(RegistrationConstants.UIN_UPDATE_BIOMETRICEXCEPTION);
		SessionContext.map().remove(RegistrationConstants.UIN_UPDATE_FACECAPTURE);
		SessionContext.map().remove(RegistrationConstants.UIN_UPDATE_IRISCAPTURE);
		SessionContext.map().remove(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW);
		SessionContext.map().remove(RegistrationConstants.UIN_UPDATE_OPERATORAUTHENTICATIONPANE);
		SessionContext.map().remove(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION);
		SessionContext.map().remove(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION);

		clearAllValues();

		SessionContext.userMap().remove(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);
		SessionContext.userMap().remove(RegistrationConstants.IS_LOW_QUALITY_BIOMETRICS);
		SessionContext.map().remove(RegistrationConstants.DUPLICATE_FINGER);

		updatePageFlow(RegistrationConstants.BIOMETRIC_EXCEPTION,
				(boolean) ApplicationContext.map().get(RegistrationConstants.BIOMETRIC_EXCEPTION_FLOW));

		updatePageFlow(RegistrationConstants.FINGERPRINT_CAPTURE,
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG))
						.equalsIgnoreCase(RegistrationConstants.ENABLE));

		updatePageFlow(RegistrationConstants.IRIS_CAPTURE,
				String.valueOf(ApplicationContext.map().get(RegistrationConstants.IRIS_DISABLE_FLAG))
						.equalsIgnoreCase(RegistrationConstants.ENABLE));

		updatePageFlow(RegistrationConstants.GUARDIAN_BIOMETRIC, false);
	}

	/**
	 * Clear onboard data.
	 */
	protected void clearOnboardData() {
		SessionContext.map().put(RegistrationConstants.ONBOARD_USER_UPDATE, false);
		SessionContext.map().put(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ, RegistrationConstants.DISABLE);
		SessionContext.map().put(RegistrationConstants.ONBOARD_USER, false);
		SessionContext.map().remove(RegistrationConstants.USER_ONBOARD_DATA);
		SessionContext.map().remove(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION);
		SessionContext.map().remove(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION);
	}

	/**
	 * Load child.
	 *
	 * @param url the url
	 * @return the FXML loader
	 */
	public static FXMLLoader loadChild(URL url) {
		FXMLLoader loader = new FXMLLoader(url, ApplicationContext.applicationLanguageBundle());
		loader.setControllerFactory(Initialization.getApplicationContext()::getBean);
		return loader;
	}

	/**
	 * Gets the finger print status.
	 *
	 */
	public void updateAuthenticationStatus() {

	}

	/**
	 * Scans documents.
	 *
	 * @param popupStage the stage
	 */
	public void scan(Stage popupStage) {

	}

	/**
	 * This method is for saving the Applicant Image and Exception Image which are
	 * captured using webcam.
	 *
	 * @param capturedImage BufferedImage that is captured using webcam
	 * @param imageType     Type of image that is to be saved
	 */
	public void saveApplicantPhoto(BufferedImage capturedImage, String imageType) {
		// will be implemented in the derived class.
	}

	/**
	 * This method used to clear the images that are captured using webcam.
	 *
	 * @param imageType Type of image that is to be cleared
	 */
	public void clearPhoto(String imageType) {
		// will be implemented in the derived class.
	}

	/**
	 * it will wait for the mentioned time to get the capture image from Bio Device.
	 *
	 * @param count             the count
	 * @param waitTimeInSec     the wait time in sec
	 * @param fingerprintFacade the fingerprint facade
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
				} catch (InterruptedException interruptedException) {
					LOGGER.error("FINGERPRINT_AUTHENTICATION_CONTROLLER - ERROR_SCANNING_FINGER", APPLICATION_NAME,
							APPLICATION_ID,
							interruptedException.getMessage() + ExceptionUtils.getStackTrace(interruptedException));
				}
			}
			counter++;
		}
	}

	/**
	 * Convert bytes to image.
	 *
	 * @param imageBytes the image bytes
	 * @return the image
	 */
	protected Image convertBytesToImage(byte[] imageBytes) {
		Image image = null;
		if (imageBytes != null) {
			image = new Image(new ByteArrayInputStream(imageBytes));
		}
		return image;
	}

	/**
	 * Online availability check.
	 *
	 * @return the timer
	 */
	protected Timer onlineAvailabilityCheck() {
		Timer timer = new Timer();
		fXComponents.setTimer(timer);
		return timer;
	}

	/**
	 * Stop timer.
	 */
	protected void stopTimer() {
		if (fXComponents.getTimer() != null) {
			fXComponents.getTimer().cancel();
			fXComponents.getTimer().purge();
			fXComponents.setTimer(null);
		}
	}

	/**
	 * Gets the timer.
	 *
	 * @return the timer
	 */
	public Timer getTimer() {
		return fXComponents.getTimer() == null ? onlineAvailabilityCheck() : fXComponents.getTimer();
	}

	/**
	 * to validate the password in case of password based authentication.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the string
	 */
	protected String validatePwd(String username, String password) {

		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID, "Validating Password");

		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId(username);
		authenticationValidatorDTO.setPassword(password);

		if (authenticationService.validatePassword(authenticationValidatorDTO)
				.equals(RegistrationConstants.PWD_MATCH)) {
			return RegistrationConstants.SUCCESS;
		}
		return RegistrationConstants.FAILURE;
	}

	/**
	 * Clear all values.
	 */
	protected void clearAllValues() {
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			((BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA))
					.setOperatorBiometricDTO(createBiometricInfoDTO());
			biometricExceptionController.clearSession();
			fingerPrintCaptureController.clearFingerPrintDTO();
			irisCaptureController.clearIrisData();
			faceCaptureController.clearPhoto(RegistrationConstants.APPLICANT_IMAGE);
		} else {
			if (SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA) != null) {
				((RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO()
						.setApplicantBiometricDTO(createBiometricInfoDTO());
				((RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO()
						.setIntroducerBiometricDTO(createBiometricInfoDTO());
				biometricExceptionController.clearSession();
				fingerPrintCaptureController.clearFingerPrintDTO();
				irisCaptureController.clearIrisData();
				faceCaptureController.clearPhoto(RegistrationConstants.APPLICANT_IMAGE);
				faceCaptureController.clearPhoto(RegistrationConstants.EXCEPTION_IMAGE);
				guardianBiometricsController.clearCapturedBioData();
			}
		}
	}

	/**
	 * Creates the biometric info DTO.
	 *
	 * @return the biometric info DTO
	 */
	protected BiometricInfoDTO createBiometricInfoDTO() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setBiometricExceptionDTO(new ArrayList<>());
		biometricInfoDTO.setFingerprintDetailsDTO(new ArrayList<>());
		biometricInfoDTO.setIrisDetailsDTO(new ArrayList<>());
		biometricInfoDTO.setFace(new FaceDetailsDTO());
		biometricInfoDTO.setExceptionFace(new FaceDetailsDTO());
		return biometricInfoDTO;
	}

	/**
	 * Gets the notification template.
	 *
	 * @param templateCode the template code
	 * @return the notification template
	 */
	protected Writer getNotificationTemplate(String templateCode) {
		RegistrationDTO registrationDTO = getRegistrationDTOFromSession();
		Writer writeNotificationTemplate = new StringWriter();
		try {
			// get the data for notification template
			String platformLanguageCode = ApplicationContext.applicationLanguage();
			String notificationTemplate = templateService.getHtmlTemplate(templateCode, platformLanguageCode);
			if (notificationTemplate != null && !notificationTemplate.isEmpty()) {
				// generate the notification template
				writeNotificationTemplate = templateGenerator.generateNotificationTemplate(notificationTemplate,
						registrationDTO, templateManagerBuilder);
			}

		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			LOGGER.error("REGISTRATION - UI - GENERATE_NOTIFICATION", APPLICATION_NAME, APPLICATION_ID,
					regBaseUncheckedException.getMessage() + ExceptionUtils.getStackTrace(regBaseUncheckedException));
		}
		return writeNotificationTemplate;
	}

	/**
	 * Gets the registration DTO from session.
	 *
	 * @return the registration DTO from session
	 */
	protected RegistrationDTO getRegistrationDTOFromSession() {
		return (RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA);
	}

	/**
	 * Gets the Face DTO from session.
	 *
	 * @return the faceDetailsDTO DTO from session
	 */
	protected BiometricInfoDTO getFaceDetailsDTO() {
		return getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO();
	}

	/**
	 * Gets the biometric DTO from session.
	 *
	 * @return the biometric DTO from session
	 */
	protected BiometricDTO getBiometricDTOFromSession() {
		return (BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA);
	}

	/**
	 * to return to the next page based on the current page and action for User
	 * Onboarding.
	 *
	 * @param currentPage - Id of current Anchorpane
	 * @param action      - action to be performed previous/next
	 * @return id of next Anchorpane
	 */

	@SuppressWarnings("unchecked")
	protected String getOnboardPageDetails(String currentPage, String action) {

		LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
				"Updating OnBoard flow based on visibility and returning next page details");

		return getReturnPage((List<String>) ApplicationContext.map().get(RegistrationConstants.ONBOARD_LIST),
				currentPage, action);
	}

	/**
	 * to return to the next page based on the current page and action for New
	 * Registration.
	 *
	 * @param currentPage - Id of current Anchorpane
	 * @param action      - action to be performed previous/next
	 * @return id of next Anchorpane
	 */
	@SuppressWarnings("unchecked")
	protected String getPageDetails(String currentPage, String action) {

		LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
				"Updating RegistrationMap based on visibility");

		for (Map.Entry<String, Map<String, Boolean>> entry : ((Map<String, Map<String, Boolean>>) ApplicationContext
				.map().get(RegistrationConstants.REGISTRATION_MAP)).entrySet()) {
			if (entry.getValue().get(RegistrationConstants.VISIBILITY)) {
				pageDetails.add(entry.getKey());
			}
		}

		LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID, "Returning Next page details");

		return getReturnPage(pageDetails, currentPage, action);

	}

	/**
	 * to return to the next page based on the current page and action.
	 *
	 * @param pageList    - List of Anchorpane Ids
	 * @param currentPage - Id of current Anchorpane
	 * @param action      - action to be performed previous/next
	 * @return id of next Anchorpane
	 */
	private String getReturnPage(List<String> pageList, String currentPage, String action) {

		LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
				"Fetching the next page based on action");

		String returnPage = "";

		if (action.equalsIgnoreCase(RegistrationConstants.NEXT)) {

			LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
					"Fetching the next page based from list of ids for Next action");

			returnPage = pageList.get((pageList.indexOf(currentPage)) + 1);
		} else if (action.equalsIgnoreCase(RegistrationConstants.PREVIOUS)) {

			LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
					"Fetching the next page based from list of ids for Previous action");

			returnPage = pageList.get((pageList.indexOf(currentPage)) - 1);
		}

		if (returnPage.equalsIgnoreCase(RegistrationConstants.REGISTRATION_PREVIEW)) {

			LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
					"Invoking Save Detail before redirecting to Preview");

			demographicDetailController.saveDetail();
			registrationPreviewController.setUpPreviewContent();

			LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
					"Details saved and content of preview is set");
		} else if (returnPage.equalsIgnoreCase(RegistrationConstants.ONBOARD_USER_SUCCESS)) {

			LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID, "Validating User Onboard data");

			ResponseDTO response = userOnboardService
					.validate((BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA));
			if (response != null && response.getErrorResponseDTOs() != null
					&& response.getErrorResponseDTOs().get(0) != null) {

				LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
						"Displaying Alert if validation is not success");

				generateAlertLanguageSpecific(RegistrationConstants.ERROR,
						response.getErrorResponseDTOs().get(0).getMessage());
				returnPage = currentPage;
			} else if (response != null && response.getSuccessResponseDTO() != null) {

				LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
						"User Onboard is success and clearing Onboard data");

				clearOnboardData();
				SessionContext.map().put(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ,
						RegistrationConstants.ENABLE);
				goToHomePage();
				onboardAlertMsg();

				LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
						"Redirecting to Home page after success onboarding");
				returnPage = RegistrationConstants.EMPTY;
			}
		}

		LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID,
				"Returning the corresponding next page based on given action" + returnPage);

		pageDetails.clear();
		return returnPage;
	}

	/**
	 * to navigate to the next page based on the current page.
	 *
	 * @param pageId     - Parent Anchorpane where other panes are included
	 * @param notTosShow - Id of Anchorpane which has to be hidden
	 * @param show       - Id of Anchorpane which has to be shown
	 * 
	 */
	protected void getCurrentPage(Pane pageId, String notTosShow, String show) {

		LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID, "Navigating to next page");

		if (notTosShow != null) {
			((Pane) pageId.lookup(RegistrationConstants.HASH + notTosShow)).setVisible(false);
		}
		if (show != null) {
			((Pane) pageId.lookup(RegistrationConstants.HASH + show)).setVisible(true);
		}

		LOGGER.info(LoggerConstants.LOG_REG_BASE, APPLICATION_NAME, APPLICATION_ID, "Navigated to next page");
	}

	/**
	 * to calculate the time for re-capture since last capture time.
	 *
	 * @param imageType the type of image that is selected to capture
	 */
	public void calculateRecaptureTime(String imageType) {
		// will be implemented in the derived class.
	}

	/**
	 * Checks if the machine is remapped to another center and starts the subsequent
	 * processing accordingly.
	 *
	 * @return true, if is machine remap process started
	 * @throws IOException
	 */
	public boolean isMachineRemapProcessStarted() {

		Boolean isRemapped = centerMachineReMapService.isMachineRemapped();
		if (isRemapped) {

			String message = RegistrationUIConstants.REMAP_NO_ACCESS_MESSAGE;

			if (isPacketsPendingForEOD()) {
				message += RegistrationConstants.NEW_LINE + RegistrationUIConstants.REMAP_EOD_PROCESS_MESSAGE;
			}
			message += RegistrationConstants.NEW_LINE + RegistrationUIConstants.REMAP_CLICK_OK;
			generateAlert(RegistrationConstants.ALERT_INFORMATION, message);

			disableHomePage(true);

			Service<String> service = new Service<String>() {
				@Override
				protected Task<String> createTask() {
					return new Task<String>() {

						@Override
						protected String call() {

							packetHandlerController.getProgressIndicator().setVisible(true);

							for (int i = 1; i <= 4; i++) {
								/* starts the remap process */
								centerMachineReMapService.handleReMapProcess(i);
								this.updateProgress(i, 4);
							}
							LOGGER.info("BASECONTROLLER_REGISTRATION CENTER MACHINE REMAP : ", APPLICATION_NAME,
									APPLICATION_ID, "center remap process completed");
							return null;
						}
					};
				}
			};
			packetHandlerController.getProgressIndicator().progressProperty().bind(service.progressProperty());

			service.restart();

			service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					handleRemapResponse(service);
				}
			});
			service.setOnFailed(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					handleRemapResponse(service);
				}
			});

		}
		return isRemapped;
	}

	private void handleRemapResponse(Service<String> service) {
		service.reset();
		disableHomePage(false);
		packetHandlerController.getProgressIndicator().setVisible(false);

		if (!centerMachineReMapService.isPacketsPendingForProcessing()) {
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.REMAP_PROCESS_SUCCESS);
			headerController.logoutCleanUp();
		} else {
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.REMAP_PROCESS_STILL_PENDING);
		}
	}

	private void disableHomePage(boolean isDisabled) {

		if (null != homeController.getMainBox())
			homeController.getMainBox().setDisable(isDisabled);

	}

	/**
	 * Checks if is packets pending for EOD.
	 *
	 * @return true, if is packets pending for EOD
	 */
	protected boolean isPacketsPendingForEOD() {

		return centerMachineReMapService.isPacketsPendingForEOD();
	}

	/**
	 * Popup statge.
	 *
	 * @param messgae    the messgae
	 * @param imageUrl   the image url
	 * @param styleClass the style class
	 */
	public void onboardAlertMsg() {
		packetHandlerController.getUserOnboardMessage().setVisible(true);
		fXComponents.getStage().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (packetHandlerController.getUserOnboardMessage().isVisible()) {
					packetHandlerController.getUserOnboardMessage().setVisible(false);
				}
			}
		});
	}

	/**
	 * Create alert with given title, header and context.
	 *
	 * @param alertType type of alert
	 * @param title     alert's title
	 * @param header    alert's header
	 * @param context   alert's context
	 * @return alert
	 */
	protected Alert createAlert(AlertType alertType, String title, String header, String context,
			String confirmButtonText, String cancelButtonText) {

		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.setGraphic(null);
		alert.setResizable(true);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(500);
		alert.getDialogPane().getStylesheets().add(
				ClassLoader.getSystemClassLoader().getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
		Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setText(RegistrationUIConstants.getMessageLanguageSpecific(confirmButtonText));

		if (alertType == Alert.AlertType.CONFIRMATION) {
			Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
			cancelButton.setText(RegistrationUIConstants.getMessageLanguageSpecific(cancelButtonText));
		}
		alert.initStyle(StageStyle.UNDECORATED);
		alert.initModality(Modality.WINDOW_MODAL);
		alert.initOwner(fXComponents.getStage());
		return alert;
	}

	/**
	 * Update UIN method flow.
	 */
	protected void updateUINMethodFlow() {
		if ((Boolean) SessionContext.userContext().getUserMap()
				.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION)) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_BIOMETRICEXCEPTION, true);
		} else if (updateUINNextPage(RegistrationConstants.FINGERPRINT_DISABLE_FLAG) && !isChild()) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_FINGERPRINTCAPTURE, true);
		} else if (updateUINNextPage(RegistrationConstants.IRIS_DISABLE_FLAG) && !isChild()) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_IRISCAPTURE, true);
		} else if (isChild()) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_PARENTGUARDIAN_DETAILS, true);
		} else if (RegistrationConstants.ENABLE
				.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.FACE_DISABLE_FLAG))) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_FACECAPTURE, true);
		} else {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW, true);
			registrationPreviewController.setUpPreviewContent();
		}
	}

	/**
	 * Update UIN next page.
	 *
	 * @param pageFlag the page flag
	 * @return true, if successful
	 */
	protected boolean updateUINNextPage(String pageFlag) {
		return RegistrationConstants.ENABLE.equalsIgnoreCase(getValueFromApplicationContext(pageFlag))
				&& !(Boolean) SessionContext.userMap().get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);
	}

	/**
	 * Update UIN next page.
	 *
	 * @return true, if successful
	 */
	protected boolean isChild() {
		return getRegistrationDTOFromSession().isUpdateUINChild();
	}

	/**
	 * Biomertic exception count.
	 *
	 * @param biometric the biometric
	 * @return the long
	 */
	protected long biomerticExceptionCount(String biometric) {
		return getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO().getBiometricExceptionDTO()
				.stream().filter(bio -> bio.getBiometricType().equalsIgnoreCase(biometric)).count();
	}

	/**
	 * Gets the value from application context.
	 *
	 * @param key the key
	 * @return the value from application context
	 */
	protected String getValueFromApplicationContext(String key) {

		LOGGER.info(LoggerConstants.LOG_REG_BASE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetching value from application Context");

		return (String) applicationContext.getApplicationMap().get(key);
	}

	/**
	 * Gets the quality score.
	 *
	 * @param qulaityScore the qulaity score
	 * @return the quality score
	 */
	protected String getQualityScore(Double qulaityScore) {

		LOGGER.info(LoggerConstants.LOG_REG_BASE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetching Quality score while capturing Biometrics");

		return String.valueOf(Math.round(qulaityScore)).concat(RegistrationConstants.PERCENTAGE);
	}

	/**
	 * Updates the Page Flow
	 *
	 * @param pageId id of the page
	 * @param val    value to be set
	 */
	@SuppressWarnings("unchecked")
	protected void updatePageFlow(String pageId, boolean val) {

		LOGGER.info(LoggerConstants.LOG_REG_BASE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Updating page flow to navigate next or previous");

		((Map<String, Map<String, Boolean>>) ApplicationContext.map().get(RegistrationConstants.REGISTRATION_MAP))
				.get(pageId).put(RegistrationConstants.VISIBILITY, val);

	}

	protected void restartApplication() {

		generateAlert(RegistrationConstants.SUCCESS.toUpperCase(), RegistrationUIConstants.RESTART_APPLICATION);
		restartController.restart();

	}

	/**
	 * Exception fingers count.
	 */
	protected Map<String, Integer> exceptionFingersCount(int leftSlapCount, int rightSlapCount, int thumbCount,
			int irisCount) {

		Map<String, Integer> exceptionCountMap = new HashMap<>();
		List<BiometricExceptionDTO> biometricExceptionDTOs;
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			biometricExceptionDTOs = getBiometricDTOFromSession().getOperatorBiometricDTO().getBiometricExceptionDTO();
		} else if (getRegistrationDTOFromSession().isUpdateUINNonBiometric()
				|| (boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
			biometricExceptionDTOs = getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
					.getBiometricExceptionDTO();
		} else {
			biometricExceptionDTOs = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
					.getBiometricExceptionDTO();
		}
		for (BiometricExceptionDTO biometricExceptionDTO : biometricExceptionDTOs) {

			if ((biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.LEFT.toLowerCase())
					&& biometricExceptionDTO.isMarkedAsException())
					&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.THUMB)
					&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.EYE)) {
				leftSlapCount++;
			}
			if ((biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.RIGHT.toLowerCase())
					&& biometricExceptionDTO.isMarkedAsException())
					&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.THUMB)
					&& !biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.EYE)) {
				rightSlapCount++;
			}
			if ((biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.THUMB)
					&& biometricExceptionDTO.isMarkedAsException())) {
				thumbCount++;
			}
			if ((biometricExceptionDTO.getMissingBiometric().contains(RegistrationConstants.EYE)
					&& biometricExceptionDTO.isMarkedAsException())) {
				irisCount++;
			}
		}
		exceptionCountMap.put(RegistrationConstants.LEFTSLAPCOUNT, leftSlapCount);
		exceptionCountMap.put(RegistrationConstants.RIGHTSLAPCOUNT, rightSlapCount);
		exceptionCountMap.put(RegistrationConstants.THUMBCOUNT, thumbCount);
		exceptionCountMap.put(RegistrationConstants.EXCEPTIONCOUNT,
				leftSlapCount + rightSlapCount + thumbCount + irisCount);

		return exceptionCountMap;
	}

	protected List<BiometricExceptionDTO> getIrisExceptions() {
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			return getBiometricDTOFromSession().getOperatorBiometricDTO().getBiometricExceptionDTO();
		} else if (getRegistrationDTOFromSession().isUpdateUINNonBiometric()
				|| (SessionContext.map().get(RegistrationConstants.IS_Child) != null
						&& (boolean) SessionContext.map().get(RegistrationConstants.IS_Child))) {
			return getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
					.getBiometricExceptionDTO();
		} else {
			return getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
					.getBiometricExceptionDTO();
		}
	}

	/**
	 * Any iris exception.
	 *
	 * @param iris the iris
	 * @return true, if successful
	 */
	protected boolean anyIrisException(String iris) {
		return getIrisExceptions().stream().anyMatch(exceptionIris -> exceptionIris.isMarkedAsException() && StringUtils
				.containsIgnoreCase(exceptionIris.getMissingBiometric(), (iris).concat(RegistrationConstants.EYE)));
	}

	/**
	 * To get the current timestamp
	 * 
	 * @return Timestamp returns the current timestamp
	 */
	protected Timestamp getCurrentTimestamp() {
		return Timestamp.from(Instant.now());
	}

	/**
	 * Restricts the re-ordering of the columns in {@link TableView}. This is
	 * generic method.
	 * 
	 * @param table
	 *            the instance of {@link TableView} for which re-ordering of columns
	 *            had to be restricted
	 */
	@SuppressWarnings("restriction")
	protected void disableColumnsReorder(TableView<?> table) {
		if (table != null) {
			table.widthProperty().addListener((source, oldWidth, newWidth) -> {
				TableHeaderRow header = (TableHeaderRow) table.lookup("TableHeaderRow");
				header.reorderingProperty()
						.addListener((observable, oldValue, newValue) -> header.setReordering(false));
			});
		}
	}

}
