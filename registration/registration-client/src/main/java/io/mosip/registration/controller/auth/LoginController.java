
package io.mosip.registration.controller.auth;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.auth.adapter.config.LoggerConfiguration;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.LoginMode;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.Initialization;
import io.mosip.registration.controller.reg.HeaderController;
import io.mosip.registration.controller.reg.Validations;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.config.JobConfigurationService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.operator.UserMachineMappingService;
import io.mosip.registration.update.SoftwareUpdateHandler;
import io.mosip.registration.util.common.OTPManager;
import io.mosip.registration.util.common.PageFlow;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Class for loading Login screen with Username and password
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Controller
public class LoginController extends BaseController implements Initializable {
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(LoginController.class);

	@FXML
	private AnchorPane userIdPane;

	@FXML
	private AnchorPane credentialsPane;

	@FXML
	private AnchorPane otpPane;

	@FXML
	private AnchorPane fingerprintPane;

	@FXML
	private AnchorPane irisPane;

	@FXML
	private AnchorPane facePane;

	@FXML
	private AnchorPane errorPane;

	@FXML
	private TextField userId;

	@FXML
	private TextField password;

	@FXML
	private TextField otp;

	@FXML
	private Button submit;

	@FXML
	private Button otpSubmit;

	@FXML
	private Button getOTP;

	@FXML
	private Button resend;

	@FXML
	private Label otpValidity;

	@Autowired
	private LoginService loginService;

	@Autowired
	private OTPManager otpGenerator;

	@Autowired
	private SchedulerUtil schedulerUtil;

	@Autowired
	private Validations validations;

	@Autowired
	private PageFlow pageFlow;

	@FXML
	private ProgressIndicator progressIndicator;

	private Service<List<String>> taskService;

	private List<String> loginList = new ArrayList<>();

	@Autowired
	private JobConfigurationService jobConfigurationService;

	private boolean isInitialSetUp;

	@Autowired
	private SoftwareUpdateHandler softwareUpdateHandler;

	private BorderPane loginRoot;

	private boolean isUserNewToMachine;

	@FXML
	private ProgressIndicator passwordProgressIndicator;

	@Autowired
	private UserMachineMappingService machineMappingService;

	private boolean hasUpdate;

	@Autowired
	private HeaderController headerController;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {

			// Check for updates
			hasUpdate = headerController.hasUpdate();

		} else {
			hasUpdate = RegistrationConstants.ENABLE.equalsIgnoreCase(
					getValueFromApplicationContext(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE));
		}

		try {
			isInitialSetUp = RegistrationConstants.ENABLE
					.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.INITIAL_SETUP));

			stopTimer();
			password.textProperty().addListener((obsValue, oldValue, newValue) -> {
				String passwordLength = getValueFromApplicationContext(RegistrationConstants.PWORD_LENGTH);
				if (passwordLength != null && passwordLength.matches("\\d+")
						&& (newValue.length() > Integer.parseInt(passwordLength))) {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PWORD_LENGTH);
				}
			});

			String otpExpiryTime = getValueFromApplicationContext(RegistrationConstants.OTP_EXPIRY_TIME);
			int otpExpirySeconds = 0;
			if (otpExpiryTime != null) {
				otpExpirySeconds = Integer
						.parseInt((getValueFromApplicationContext(RegistrationConstants.OTP_EXPIRY_TIME)).trim());
			}
			int minutes = otpExpirySeconds / 60;
			String seconds = String.valueOf(otpExpirySeconds % 60);
			seconds = seconds.length() < 2 ? "0" + seconds : seconds;
			otpValidity.setText(RegistrationUIConstants.OTP_VALIDITY + " " + minutes + ":" + seconds + " "
					+ RegistrationUIConstants.MINUTES);
		} catch (RuntimeException runtimeExceptionexception) {
			LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					runtimeExceptionexception.getMessage() + ExceptionUtils.getStackTrace(runtimeExceptionexception));
		}
	}

	/**
	 * To get the Sequence of which Login screen to be displayed
	 * 
	 * @param primaryStage
	 *            primary Stage
	 */
	public void loadInitialScreen(Stage primaryStage) {

		/* Save Global Param Values in Application Context's application map */
		getGlobalParams();
		ApplicationContext.loadResources();

		try {

			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Retrieve Login mode");

			fXComponents.setStage(primaryStage);

			validations.setResourceBundle();
			loginRoot = BaseController.load(getClass().getResource(RegistrationConstants.INITIAL_PAGE));

			scene = getScene(loginRoot);
			pageFlow.getInitialPageDetails();
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();
			primaryStage.setX(bounds.getMinX());
			primaryStage.setY(bounds.getMinY());
			primaryStage.setWidth(bounds.getWidth());
			primaryStage.setHeight(bounds.getHeight());
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(getClass().getResource(RegistrationConstants.LOGO).toExternalForm()));
			primaryStage.show();


			org.apache.log4j.Logger.getLogger(Initialization.class).info("Mosip client Screen loaded");

			// Execute SQL file (Script files on update)
			executeSQLFile();

			if (hasUpdate) {

				// Update Application
				headerController.softwareUpdate(loginRoot, progressIndicator, RegistrationUIConstants.UPDATE_LATER,
						isInitialSetUp);

			} else if (!isInitialSetUp) {
				executePreLaunchTask(loginRoot, progressIndicator);
				jobConfigurationService.startScheduler();
			}

		} catch (IOException | RuntimeException runtimeException) {

			LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
		}
	}

	private void executeSQLFile() {

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Started Execute SQL file check");

		String version = getValueFromApplicationContext(RegistrationConstants.SERVICES_VERSION_KEY);

		try {
			if (!softwareUpdateHandler.getCurrentVersion().equalsIgnoreCase(version)) {

				LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Software Updated found");

				loginRoot.setDisable(true);
				ResponseDTO responseDTO = softwareUpdateHandler
						.executeSqlFile(softwareUpdateHandler.getCurrentVersion(), version);
				loginRoot.setDisable(false);

				if (responseDTO.getErrorResponseDTOs() != null) {

					ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);

					if (RegistrationConstants.BACKUP_PREVIOUS_SUCCESS.equalsIgnoreCase(errorResponseDTO.getMessage())) {
						generateAlert(RegistrationConstants.ERROR,
								RegistrationUIConstants.SQL_EXECUTION_FAILED_AND_REPLACED
										+ RegistrationUIConstants.RESTART_APPLICATION);
						SessionContext.destroySession();
					} else {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.BIOMETRIC_DISABLE_SCREEN_2);

						new Initialization().stop();

					}

					restartApplication();
				}
			}
		} catch (RuntimeException | IOException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.BIOMETRIC_DISABLE_SCREEN_2);

			new Initialization().stop();

		}

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Completed Execute SQL file check");

	}

	/**
	 * Validate user id.
	 *
	 * @param event
	 *            the event
	 */
	@SuppressWarnings("unchecked")
	public void validateUserId(ActionEvent event) {

		auditFactory.audit(AuditEvent.LOGIN_AUTHENTICATE_USER_ID, Components.LOGIN,
				userId.getText().isEmpty() ? "NA" : userId.getText(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials entered through UI");

		if (userId.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
		} else if (isInitialSetUp) {
			// For Initial SetUp
			initialSetUpOrNewUserLaunch();

		} else {
			try {
				ResponseDTO responseDTO = loginService.validateUser(userId.getText());
				List<ErrorResponseDTO> errorResponseDTOList = responseDTO.getErrorResponseDTOs();
				if (errorResponseDTOList != null && !errorResponseDTOList.isEmpty()) {
					generateAlertLanguageSpecific(RegistrationConstants.ERROR,
							errorResponseDTOList.get(0).getMessage());
				} else {

					if (validateInvalidLogin((UserDTO) responseDTO.getSuccessResponseDTO().getOtherAttributes()
							.get(RegistrationConstants.USER_DTO), "")) {
						isUserNewToMachine = machineMappingService.isUserNewToMachine(userId.getText())
								.getErrorResponseDTOs() != null;
						if (isUserNewToMachine) {
							initialSetUpOrNewUserLaunch();
						} else {
							loginList = loginService.getModesOfLogin(ProcessNames.LOGIN.getType(),
									(Set<String>) responseDTO.getSuccessResponseDTO().getOtherAttributes()
											.get(RegistrationConstants.ROLES_LIST));

							String fingerprintDisableFlag = getValueFromApplicationContext(
									RegistrationConstants.FINGERPRINT_DISABLE_FLAG);
							String irisDisableFlag = getValueFromApplicationContext(
									RegistrationConstants.IRIS_DISABLE_FLAG);
							String faceDisableFlag = getValueFromApplicationContext(
									RegistrationConstants.FACE_DISABLE_FLAG);

							LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
									"Ignoring FingerPrint login if the configuration is off");

							removeLoginParam(fingerprintDisableFlag, RegistrationConstants.FINGERPRINT);
							removeLoginParam(irisDisableFlag, RegistrationConstants.IRIS);
							removeLoginParam(faceDisableFlag, RegistrationConstants.FINGERPRINT);

							String loginMode = !loginList.isEmpty() ? loginList.get(RegistrationConstants.PARAM_ZERO)
									: null;

							LOGGER.debug(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
									"Retrieved corresponding Login mode");

							if (loginMode == null) {
								userIdPane.setVisible(false);
								errorPane.setVisible(true);
							} else {

								if ((RegistrationConstants.DISABLE.equalsIgnoreCase(fingerprintDisableFlag)
										&& RegistrationConstants.FINGERPRINT.equalsIgnoreCase(loginMode))
										|| (RegistrationConstants.DISABLE.equalsIgnoreCase(irisDisableFlag)
												&& RegistrationConstants.IRIS.equalsIgnoreCase(loginMode))
										|| (RegistrationConstants.DISABLE.equalsIgnoreCase(faceDisableFlag)
												&& RegistrationConstants.FACE.equalsIgnoreCase(loginMode))) {

									generateAlert(RegistrationConstants.ERROR,
											RegistrationUIConstants.BIOMETRIC_DISABLE_SCREEN_1
													.concat(RegistrationUIConstants.BIOMETRIC_DISABLE_SCREEN_2));

								} else {
									userIdPane.setVisible(false);
									loadLoginScreen(loginMode);
								}
							}
						}
					}
				}
			} catch (RegBaseUncheckedException regBaseUncheckedException) {

				LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
						regBaseUncheckedException.getMessage()
								+ ExceptionUtils.getStackTrace(regBaseUncheckedException));

				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
			}
		}
	}

	private void initialSetUpOrNewUserLaunch() {
		userIdPane.setVisible(false);
		loadLoginScreen(LoginMode.PASSWORD.toString());
	}

	/**
	 * 
	 * Validating User credentials on Submit
	 * 
	 * @param event
	 *            event for validating credentials
	 */
	public void validateCredentials(ActionEvent event) {

		auditFactory.audit(AuditEvent.LOGIN_WITH_PASSWORD, Components.LOGIN, userId.getText(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials entered through UI");

		UserDTO userDTO = loginService.getUserDetail(userId.getText());

		if (isInitialSetUp || isUserNewToMachine) {

			if (!RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.NO_INTERNET_CONNECTION);

			} else {
				LoginUserDTO loginUserDTO = new LoginUserDTO();
				loginUserDTO.setUserId(userId.getText());
				loginUserDTO.setPassword(password.getText());

				ApplicationContext.map().put(RegistrationConstants.USER_DTO, loginUserDTO);

				try {
					// Get Auth Token
					ApplicationContext.map().put(RegistrationConstants.USER_DTO, loginUserDTO);
					if (SessionContext.create(userDTO, RegistrationConstants.PWORD, isInitialSetUp, isUserNewToMachine,
							null)) {
						if (isInitialSetUp) {
							executePreLaunchTask(credentialsPane, passwordProgressIndicator);

						} else {
							validateUserCredentialsInLocal(userDTO);
						}
					} else {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_TO_GET_AUTH_TOKEN);
					}

				} catch (Exception exception) {
					LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, String.format(
							"Exception while getting AuthZ Token --> %s", ExceptionUtils.getStackTrace(exception)));

					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_TO_GET_AUTH_TOKEN);

					SessionContext.destroySession();
					loadInitialScreen(Initialization.getPrimaryStage());
				}
			}
		} else {
			validateUserCredentialsInLocal(userDTO);
		}

	}

	private void validateUserCredentialsInLocal(UserDTO userDTO) {
		boolean pwdValidationStatus = false;
		if (password.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PWORD_FIELD_EMPTY);
		} else {
			if (userDTO != null) {

				AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
				authenticationValidatorDTO.setUserId(userId.getText());
				authenticationValidatorDTO.setPassword(password.getText());

				if (SessionContext.create(userDTO, RegistrationConstants.PWORD, false, false,
						authenticationValidatorDTO)) {
					pwdValidationStatus = validateInvalidLogin(userDTO, "");
				} else {
					pwdValidationStatus = validateInvalidLogin(userDTO, RegistrationUIConstants.INCORRECT_PWORD);
				}

				if (pwdValidationStatus) {

					LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							"Loading next login screen");

					credentialsPane.setVisible(false);
					loadNextScreen(userDTO, RegistrationConstants.PWORD);
				}
			} else {
				loadInitialScreen(Initialization.getPrimaryStage());
			}
		}

	}

	/**
	 * Generate OTP based on EO username
	 * 
	 * @param event
	 *            event for generating OTP
	 */
	@FXML
	public void generateOtp(ActionEvent event) {

		if (userId.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
		} else {

			auditFactory.audit(AuditEvent.LOGIN_GET_OTP, Components.LOGIN, userId.getText(),
					AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

			// Response obtained from server
			ResponseDTO responseDTO = otpGenerator.getOTP(userId.getText());

			if (responseDTO.getSuccessResponseDTO() != null) {
				// Enable submit button
				changeToOTPSubmitMode();

				// Generate alert to show OTP
				generateAlert(RegistrationConstants.ALERT_INFORMATION,
						RegistrationUIConstants.OTP_GENERATION_SUCCESS_MESSAGE);

			} else if (responseDTO.getErrorResponseDTOs() != null) {
				// Generate Alert to show INVALID USERNAME
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.OTP_GENERATION_ERROR_MESSAGE);

			}
		}
	}

	/**
	 * Validate User through username and otp
	 * 
	 * @param event
	 *            event for validating OTP
	 */
	@FXML
	public void validateOTP(ActionEvent event) {

		auditFactory.audit(AuditEvent.LOGIN_SUBMIT_OTP, Components.LOGIN, userId.getText(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		if (validations.validateTextField(otpPane, otp, otp.getId(), true)) {

			UserDTO userDTO = loginService.getUserDetail(userId.getText());

			boolean otpLoginStatus = false;

			AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
			authenticationValidatorDTO.setUserId(userId.getText());
			authenticationValidatorDTO.setOtp(otp.getText());

			if (SessionContext.create(userDTO, RegistrationConstants.OTP, false, false, authenticationValidatorDTO)) {
				otpLoginStatus = validateInvalidLogin(userDTO, "");
			} else {
				otpLoginStatus = validateInvalidLogin(userDTO, RegistrationUIConstants.OTP_VALIDATION_ERROR_MESSAGE);
			}

			if (otpLoginStatus) {
				otpPane.setVisible(false);
				int otpExpirySeconds = Integer
						.parseInt((getValueFromApplicationContext(RegistrationConstants.OTP_EXPIRY_TIME)).trim());
				int minutes = otpExpirySeconds / 60;
				String seconds = String.valueOf(otpExpirySeconds % 60);
				seconds = seconds.length() < 2 ? "0" + seconds : seconds;
				otpValidity.setText(RegistrationUIConstants.OTP_VALIDITY + " " + minutes + ":" + seconds + " "
						+ RegistrationUIConstants.MINUTES);
				loadNextScreen(userDTO, RegistrationConstants.OTP);

			}

		}
	}

	/**
	 * Validate User through username and fingerprint
	 * 
	 * @param event
	 *            event for capturing fingerprint
	 */
	public void captureFingerPrint(ActionEvent event) {

		auditFactory.audit(AuditEvent.LOGIN_WITH_FINGERPRINT, Components.LOGIN, userId.getText(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Capturing finger print for validation");

		UserDTO userDTO = loginService.getUserDetail(userId.getText());

		boolean bioLoginStatus = false;

		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId(userId.getText());

		if (SessionContext.create(userDTO, RegistrationConstants.FINGERPRINT_UPPERCASE, false, false,
				authenticationValidatorDTO)) {
			bioLoginStatus = validateInvalidLogin(userDTO, "");
		} else {
			bioLoginStatus = validateInvalidLogin(userDTO, RegistrationUIConstants.FINGER_PRINT_MATCH);
		}

		if (bioLoginStatus) {
			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					"FingerPrint validation succeded");
			fingerprintPane.setVisible(false);
			loadNextScreen(userDTO, RegistrationConstants.FINGERPRINT);
		} else {
			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					"Finger print validation failed");
		}

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Fingerprint validation done");
	}

	/**
	 * Validate User through username and Iris
	 * 
	 * @param event
	 *            event for capturing Iris
	 */
	public void captureIris(ActionEvent event) {

		auditFactory.audit(AuditEvent.LOGIN_WITH_IRIS, Components.LOGIN, userId.getText(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Capturing the iris for validation");

		UserDTO userDTO = loginService.getUserDetail(userId.getText());

		boolean irisLoginStatus = false;

		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId(userId.getText());

		if (SessionContext.create(userDTO, RegistrationConstants.IRIS, false, false, authenticationValidatorDTO)) {
			irisLoginStatus = validateInvalidLogin(userDTO, "");
		} else {
			irisLoginStatus = validateInvalidLogin(userDTO, RegistrationUIConstants.IRIS_MATCH);
		}
		if (irisLoginStatus) {
			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Iris validation succeded");
			irisPane.setVisible(false);
			loadNextScreen(userDTO, RegistrationConstants.IRIS);
		} else {
			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Iris validation failed");
		}

	}

	/**
	 * Validate User through username and face
	 * 
	 * @param event
	 *            event to capture face
	 */
	public void captureFace(ActionEvent event) {

		auditFactory.audit(AuditEvent.LOGIN_WITH_FACE, Components.LOGIN, userId.getText(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Capturing face for validation");

		UserDTO userDTO = loginService.getUserDetail(userId.getText());

		boolean faceLoginStatus = false;

		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId(userId.getText());

		if (SessionContext.create(userDTO, RegistrationConstants.FACE, false, false, authenticationValidatorDTO)) {
			faceLoginStatus = validateInvalidLogin(userDTO, "");
		} else {
			faceLoginStatus = validateInvalidLogin(userDTO, RegistrationUIConstants.FACE_MATCH);
		}

		if (faceLoginStatus) {
			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Face validation succeeded");
			facePane.setVisible(false);
			loadNextScreen(userDTO, RegistrationConstants.FACE);
		} else {
			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Face validation failed");
		}

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Face validation done");
	}

	/**
	 * Mode of login with set of fields enabling and disabling
	 */
	private void changeToOTPSubmitMode() {
		submit.setDisable(false);
		otpSubmit.setDisable(false);
		getOTP.setVisible(false);
		resend.setVisible(true);
		otpValidity.setVisible(true);
	}

	/**
	 * Load login screen depending on Loginmode
	 * 
	 * @param loginMode
	 *            login screen to be loaded
	 */
	public void loadLoginScreen(String loginMode) {

		switch (loginMode.toUpperCase()) {
		case RegistrationConstants.OTP:
			otpPane.setVisible(true);
			break;
		case RegistrationConstants.PWORD:
			credentialsPane.setVisible(true);
			break;
		case RegistrationConstants.FINGERPRINT_UPPERCASE:
			fingerprintPane.setVisible(true);
			break;
		case RegistrationConstants.IRIS:
			irisPane.setVisible(true);
			break;
		case RegistrationConstants.FACE:
			facePane.setVisible(true);
			break;
		default:
			credentialsPane.setVisible(true);
		}

		if (!loginList.isEmpty()) {
			loginList.remove(RegistrationConstants.PARAM_ZERO);
		}
	}

	/**
	 * Loading next login screen in case of multifactor authentication
	 * 
	 * @param userDetail
	 *            the userDetail
	 * @param loginMode
	 *            the loginMode
	 */
	private void loadNextScreen(UserDTO userDTO, String loginMode) {

		if (!loginList.isEmpty()) {

			LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
					"Loading next login screen in case of multifactor authentication");

			loadLoginScreen(loginList.get(RegistrationConstants.PARAM_ZERO));

		} else {

			if (null != SessionContext.userContext().getUserId()) {

				auditFactory.audit(AuditEvent.NAV_HOME, Components.LOGIN, userId.getText(),
						AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

				try {

					LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Loading Home screen");
					schedulerUtil.startSchedulerUtil();
					loginList.clear();

					BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
					// to add events to the stage
					getStage();

					userDTO.setLastLoginMethod(loginMode);
					userDTO.setLastLoginDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
					userDTO.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ZERO);

					loginService.updateLoginParams(userDTO);
				} catch (IOException ioException) {

					LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
				} catch (RegBaseCheckedException regBaseCheckedException) {

					LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							regBaseCheckedException.getMessage()
									+ ExceptionUtils.getStackTrace(regBaseCheckedException));

					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);

				} catch (RuntimeException runtimeException) {

					LOGGER.error(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
							runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);

				}
			}
		}
	}

	public void executePreLaunchTask(Pane pane, ProgressIndicator progressIndicator) {

		progressIndicator.setVisible(true);
		pane.setDisable(true);

		/**
		 * This anonymous service class will do the pre application launch task
		 * progress.
		 * 
		 */
		taskService = new Service<List<String>>() {
			@Override
			protected Task<List<String>> createTask() {
				return /**
						 * @author SaravanaKumar
						 *
						 */
				new Task<List<String>>() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see javafx.concurrent.Task#call()
					 */
					@Override
					protected List<String> call() {

						LOGGER.info("REGISTRATION - HANDLE_PACKET_UPLOAD_START - PACKET_UPLOAD_CONTROLLER",
								APPLICATION_NAME, APPLICATION_ID, "Handling all the packet upload activities");

						return loginService.initialSync();
					}
				};
			}
		};

		progressIndicator.progressProperty().bind(taskService.progressProperty());
		taskService.start();
		taskService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

				if (taskService.getValue().contains(RegistrationConstants.FAILURE)) {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.SYNC_CONFIG_DATA_FAILURE);
					if (isInitialSetUp) {
						loadInitialScreen(Initialization.getPrimaryStage());
						return;
					}
				} else if (taskService.getValue().contains(RegistrationConstants.SUCCESS) && isInitialSetUp) {

					// update initial set up flag

					globalParamService.update(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.DISABLE);
					restartApplication();

				}

				if (taskService.getValue().contains(RegistrationConstants.RESTART)) {
					restartApplication();
				}
				pane.setDisable(false);
				progressIndicator.setVisible(false);
			}

		});

	}

	/**
	 * Validating invalid number of login attempts
	 * 
	 * @param userDetail
	 *            user details
	 * @param userId
	 *            entered userId
	 * @return boolean
	 */
	private boolean validateInvalidLogin(UserDTO userDTO, String errorMessage) {

		boolean validate = false;

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID, "Fetching invalid login params");

		int invalidLoginCount = Integer
				.parseInt(getValueFromApplicationContext(RegistrationConstants.INVALID_LOGIN_COUNT));

		int invalidLoginTime = Integer
				.parseInt(getValueFromApplicationContext(RegistrationConstants.INVALID_LOGIN_TIME));

		String unlockMessage = String.format("%s %s %s %s %s", RegistrationUIConstants.USER_ACCOUNT_LOCK_MESSAGE_NUMBER,
				String.valueOf(invalidLoginCount), RegistrationUIConstants.USER_ACCOUNT_LOCK_MESSAGE,
				String.valueOf(invalidLoginTime), RegistrationUIConstants.USER_ACCOUNT_LOCK_MESSAGE_MINUTES);

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Invoking validation of login attempts");

		String val = loginService.validateInvalidLogin(userDTO, errorMessage, invalidLoginCount, invalidLoginTime);

		if (val.equalsIgnoreCase(RegistrationConstants.ERROR)) {
			generateAlert(RegistrationConstants.ERROR, unlockMessage);
			loadLoginScreen();
		} else if (val.equalsIgnoreCase(errorMessage)) {
			generateAlert(RegistrationConstants.ERROR, errorMessage);
		} else {
			validate = Boolean.valueOf(val);
		}

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Validated number of login attempts");

		return validate;

	}

	/**
	 * This method will remove the loginmethod from list
	 * 
	 * @param disableFlag
	 *            configuration flag
	 * @param loginMethod
	 *            login method
	 */
	private void removeLoginParam(String disableFlag, String loginMethod) {

		loginList.removeIf(login -> loginList.size() > 1 && RegistrationConstants.DISABLE.equalsIgnoreCase(disableFlag)
				&& login.equalsIgnoreCase(loginMethod));

		LOGGER.info(LoggerConstants.LOG_REG_LOGIN, APPLICATION_NAME, APPLICATION_ID,
				"Ignoring login method if the configuration is off");

	}

}
