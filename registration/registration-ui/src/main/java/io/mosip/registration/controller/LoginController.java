package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.URL;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.util.biometric.FingerprintFacade;
import io.mosip.registration.util.biometric.MosipFingerprintProvider;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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

	@FXML
	private TextField userId;

	@FXML
	private TextField password;

	@FXML
	private Button submit;

	@FXML
	private Button getOTP;

	@FXML
	private Button resend;

	@FXML
	private Label otpValidity;

	@FXML
	private Label fingerprint;

	@FXML
	private ImageView fingerImage;

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	@Value("${TIME_OUT_INTERVAL}")
	private long timeoutInterval;

	@Value("${IDEAL_TIME}")
	private long idealTime;

	@Value("${REFRESHED_LOGIN_TIME}")
	private long refreshedLoginTime;

	@Value("${otp_validity_in_mins}")
	private long otpValidityImMins;

	@Value("${QUALITY_SCORE}")
	private int qualityScore;

	@Value("${CAPTURE_TIME_OUT}")
	private int captureTimeOut;

	@Value("${USERNAME_PWD_LENGTH}")
	private int usernamePwdLength;

	@Value("${PROVIDER_NAME}")
	private String deviceName;

	@Autowired
	private LoginService loginService;

	@Autowired
	private SchedulerUtil schedulerUtil;

	private FingerprintFacade fingerprintFacade = new FingerprintFacade();

	private static Scene scene;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(LoginController.class);

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		otpValidity.setText("Valid for " + otpValidityImMins + " minutes");
		RegistrationOfficerDetailsController.stopTimer();
	}

	public static Scene getScene() {
		return scene;
	}

	/**
	 * To get the Sequence of which Login screen to be displayed
	 * 
	 * @return String loginMode
	 * @throws RegBaseCheckedException
	 */
	public String loadInitialScreen(Stage primaryStage) throws RegBaseCheckedException {

		LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Retrieve Login mode");

		BaseController.stage = primaryStage;
		String loginMode = null;

		try {
			Map<String, Object> userLoginMode = loginService.getModesOfLogin();

			if (userLoginMode.containsKey((RegistrationConstants.LOGIN_INITIAL_VAL))) {
				loginMode = String.valueOf(userLoginMode.get(RegistrationConstants.LOGIN_INITIAL_VAL));
			}

			// To maintain the login mode sequence
			SessionContext.getInstance().setMapObject(userLoginMode);
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.LOGIN_INITIAL_SCREEN, loginMode);

			// Load the property files for application and local language
			ApplicationContext.getInstance().setApplicationLanguageBundle();
			ApplicationContext.getInstance().setLocalLanguageProperty();

			LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					"Retrieved corresponding Login mode");

			BorderPane loginRoot = BaseController.load(getClass().getResource(RegistrationConstants.INITIAL_PAGE));

			if (loginMode == null) {
				AnchorPane loginType = BaseController.load(getClass().getResource(RegistrationConstants.ERROR_PAGE));
				loginRoot.setCenter(loginType);
			} else {
				loadLoginScreen(loginMode);
			}

			getGlobalParams();

			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene = new Scene(loginRoot, 950, 630);
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());

			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException ioException) {

			LOGGER.error("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {

			LOGGER.error("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage());
			generateAlert(RegistrationConstants.ALERT_ERROR, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
		}

		return loginMode;
	}

	/**
	 * 
	 * Validating User credentials on Submit
	 * 
	 * @return String loginMode
	 * @throws RegBaseCheckedException
	 */
	public void validateCredentials(ActionEvent event) {

		LOGGER.debug("REGISTRATION - LOGIN_MODE_PWORD - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials entered through UI");

		if (userId.getText().isEmpty() && password.getText().isEmpty()) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.MISSING_MANDATOTY_FIELDS, RegistrationConstants.CREDENTIALS_FIELD_EMPTY);
		} else if (userId.getText().isEmpty()) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.MISSING_MANDATOTY_FIELDS, RegistrationConstants.USERNAME_FIELD_EMPTY);
		} else if (password.getText().isEmpty()) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.MISSING_MANDATOTY_FIELDS, RegistrationConstants.PWORD_FIELD_EMPTY);
		} else if (userId.getText().length() > usernamePwdLength) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.LOGIN_INFO_MESSAGE, RegistrationConstants.USRNAME_PWORD_LENGTH);
		} else if (password.getText().length() > usernamePwdLength) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.LOGIN_INFO_MESSAGE, RegistrationConstants.USRNAME_PWORD_LENGTH);
		} else {

			String hashPassword = null;

			// password hashing
			if (!(password.getText().isEmpty())) {
				byte[] bytePassword = password.getText().getBytes();
				hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));
			}
			LoginUserDTO userDTO = new LoginUserDTO();
			userDTO.setUserId(userId.getText());
			userDTO.setPassword(hashPassword);
			// Server connection check
			boolean serverStatus = getConnectionCheck(userDTO);
			boolean offlineStatus = false;
			RegistrationUserDetail userDetail = loginService.getUserDetail(userId.getText());

			if (userDetail == null) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.LOGIN_FAILURE,
						RegistrationConstants.USER_NOT_ONBOARDED);
			} else {
				if (!serverStatus) {
					LOGGER.debug("REGISTRATION - USER_PASSWORD - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
							"Retrieving User Password from database");

					LOGGER.debug("REGISTRATION - VALID_LOGIN_COUNT - LOGIN_CONTROLLER", APPLICATION_NAME,
							APPLICATION_ID, "Validating number of login attempts");

					if (!userDetail.getRegistrationUserPassword().getPwd().equals(hashPassword)) {
						offlineStatus = validateInvalidLogin(userDetail, RegistrationConstants.INCORRECT_PWORD);
					} else {
						offlineStatus = validateInvalidLogin(userDetail, "");
					}
				}
				if (serverStatus || offlineStatus) {
					if (validateUserStatus(userId.getText())) {

						LOGGER.debug("REGISTRATION - LOGIN_PWORD - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
								"Validating user status");

						generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
								AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
								RegistrationConstants.LOGIN_INFO_MESSAGE, RegistrationConstants.BLOCKED_USER_ERROR);
					} else {
						try {

							LOGGER.debug("REGISTRATION - LOGIN_PWORD - LOGIN_CONTROLLER", APPLICATION_NAME,
									APPLICATION_ID, "Loading next login screen");

							SessionContext sessionContext = SessionContext.getInstance();
							loadLoginAfterLogout(sessionContext, RegistrationConstants.LOGIN_METHOD_PWORD);
							loadNextScreen(userDetail, sessionContext, RegistrationConstants.LOGIN_METHOD_PWORD);

						} catch (IOException | RuntimeException | RegBaseCheckedException exception) {

							LOGGER.error("REGISTRATION - LOGIN_PWORD - LOGIN_CONTROLLER", APPLICATION_NAME,
									APPLICATION_ID, REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());

							generateAlert(RegistrationConstants.ALERT_ERROR,
									AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
									RegistrationConstants.LOGIN_FAILURE,
									REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
						}
					}
				}
			}
		}

	}

	/**
	 * Generate OTP based on EO username
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	@FXML
	public void generateOtp(ActionEvent event) {

		if (!userId.getText().isEmpty()) {
			// Response obtained from server
			ResponseDTO responseDTO = null;

			// Service Layer interaction
			responseDTO = loginService.getOTP(userId.getText());

			if (responseDTO.getSuccessResponseDTO() != null) {
				// Enable submit button
				changeToOTPSubmitMode();

				// Generate alert to show OTP
				SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(successResponseDTO.getCode()),
						RegistrationConstants.OTP_INFO_MESSAGE, successResponseDTO.getMessage());

			} else if (responseDTO.getErrorResponseDTOs() != null) {
				// Generate Alert to show INVALID USERNAME
				ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(errorResponseDTO.getCode()),
						RegistrationConstants.OTP_INFO_MESSAGE, errorResponseDTO.getMessage());

			}

		} else {
			// Generate Alert to show username field was empty
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.OTP_INFO_MESSAGE, RegistrationConstants.USERNAME_FIELD_EMPTY);

		}

	}

	/**
	 * Validate User through username and otp
	 * 
	 * @param event
	 */
	@FXML
	public void validateUser(ActionEvent event) {
		if (!password.getText().isEmpty() && password.getText().length() != 3) {

			ResponseDTO responseDTO = null;

			RegistrationUserDetail userDetail = loginService.getUserDetail(userId.getText());

			if (userDetail == null) {

				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.LOGIN_FAILURE,
						RegistrationConstants.USER_NOT_ONBOARDED);

			} else {
				responseDTO = loginService.validateOTP(userId.getText(), password.getText());

				if (responseDTO != null) {

					boolean otpLoginStatus = false;
					if (responseDTO.getSuccessResponseDTO() != null) {
						otpLoginStatus = validateInvalidLogin(userDetail, "");
					} else {
						ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
						otpLoginStatus = validateInvalidLogin(userDetail, errorResponseDTO.getMessage());
					}
					if (otpLoginStatus) {
						// // Validating User status
						if (validateUserStatus(userId.getText())) {
							generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
									AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
									RegistrationConstants.LOGIN_INFO_MESSAGE, RegistrationConstants.BLOCKED_USER_ERROR);
						} else {
							try {
								SessionContext sessionContext = SessionContext.getInstance();
								loadLoginAfterLogout(sessionContext, RegistrationConstants.LOGIN_METHOD_OTP);
								loadNextScreen(userDetail, sessionContext, RegistrationConstants.LOGIN_METHOD_OTP);
							} catch (IOException | RuntimeException | RegBaseCheckedException exception) {

								LOGGER.error("REGISTRATION - LOGIN_OTP - LOGIN_CONTROLLER", APPLICATION_NAME,
										APPLICATION_ID, REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());

								generateAlert(RegistrationConstants.ALERT_ERROR,
										AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
										RegistrationConstants.LOGIN_FAILURE,
										RegistrationExceptions.REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION
												.getErrorMessage());
							}

						}

					}
				}
			}

		} else if (userId.getText().length() == 3) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.OTP_INFO_MESSAGE, RegistrationConstants.USERNAME_FIELD_ERROR);
		} else {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.OTP_INFO_MESSAGE, RegistrationConstants.OTP_FIELD_EMPTY);
		}
	}

	public void validateFingerPrint(ActionEvent event) {

		LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials for Biometric login");

		if (userId.getText().isEmpty()) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.MISSING_MANDATOTY_FIELDS, RegistrationConstants.USERNAME_FIELD_EMPTY);
		} else {

			RegistrationUserDetail detail = loginService.getUserDetail(userId.getText());

			if (detail == null) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.LOGIN_FAILURE,
						RegistrationConstants.USER_NOT_ONBOARDED);
			} else {

				LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
						"Validating Fingerprint with minutia");

				boolean bioLoginStatus = false;

				if (validateBiometric(detail)) {
					bioLoginStatus = validateInvalidLogin(detail, "");
				} else {
					bioLoginStatus = validateInvalidLogin(detail, RegistrationConstants.FINGER_PRINT_MATCH);
				}
				if (bioLoginStatus) {
					if (detail.getStatusCode() != null
							&& detail.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED)) {
						generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
								AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
								RegistrationConstants.LOGIN_INFO_MESSAGE, RegistrationConstants.BLOCKED_USER_ERROR);
					} else {
						try {
							SessionContext sessionContext = SessionContext.getInstance();
							loadLoginAfterLogout(sessionContext, RegistrationConstants.LOGIN_METHOD_BIO);
							loadNextScreen(detail, sessionContext, RegistrationConstants.LOGIN_METHOD_BIO);
						} catch (IOException | RuntimeException | RegBaseCheckedException exception) {

							LOGGER.error("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME,
									APPLICATION_ID, REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());

							generateAlert(RegistrationConstants.ALERT_ERROR,
									AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
									RegistrationConstants.LOGIN_FAILURE,
									REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
						}
					}
				}
			}

			LOGGER.debug("REGISTRATION - SCAN_FINGER - FINGER_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
					"Fingerprint validation done");

		}
	}

	/**
	 * Checking server status
	 * 
	 * @param LoginUserDTO
	 *            the UserDTO object
	 * @return boolean
	 */
	private boolean getConnectionCheck(LoginUserDTO userObj) {

		HttpEntity<LoginUserDTO> loginEntity = new HttpEntity<>(userObj);
		ResponseEntity<String> tokenId = null;
		boolean serverStatus = false;

		try {
			tokenId = new RestTemplate().exchange(URL, HttpMethod.POST, loginEntity, String.class);
			if (tokenId.getStatusCode().is2xxSuccessful()) {
				serverStatus = true;
			}
		} catch (RestClientException resourceAccessException) {

			LOGGER.error("REGISTRATION - SERVER_CONNECTION_CHECK", APPLICATION_NAME, APPLICATION_ID,
					resourceAccessException.getMessage());
		}
		return serverStatus;
	}

	/**
	 * Mode of login with set of fields enabling and disabling
	 */
	private void changeToOTPSubmitMode() {
		submit.setDisable(false);
		getOTP.setVisible(false);
		resend.setVisible(true);
	}

	/**
	 * Enable OTP login specific attributes
	 */
	private void enableOTP() {
		password.clear();
		password.setPromptText("Enter OTP");
		otpValidity.setVisible(true);
		getOTP.setVisible(true);
		fingerprint.setVisible(false);
		fingerImage.setVisible(false);
		getOTP.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				generateOtp(event);
			}
		});
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				validateUser(event);
			}
		});
	}

	/**
	 * Enable PWD login specific attributes
	 */
	private void enablePWD() {
		password.clear();
		password.setPromptText("RO Password");
		otpValidity.setVisible(false);
		getOTP.setVisible(false);
		fingerprint.setVisible(false);
		fingerImage.setVisible(false);
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				validateCredentials(event);
			}
		});
	}

	/**
	 * Enable BIO login specific attributes
	 */
	private void enableFingerPrint() {
		password.setVisible(false);
		otpValidity.setVisible(false);
		getOTP.setVisible(false);
		fingerprint.setVisible(true);
		fingerImage.setVisible(true);
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				validateFingerPrint(event);
			}
		});
	}

	/**
	 * Load login screen depending on Loginmode
	 * 
	 * @param loginMode
	 *            login screen to be loaded
	 */
	public void loadLoginScreen(String loginMode) {
		switch (loginMode) {
		case RegistrationConstants.LOGIN_METHOD_OTP:
			enableOTP();
			break;
		case RegistrationConstants.LOGIN_METHOD_PWORD:
			enablePWD();
			break;
		case RegistrationConstants.LOGIN_METHOD_BIO:
			enableFingerPrint();
			break;
		default:
			enablePWD();
		}
	}

	/**
	 * Validating user status
	 * 
	 * @param userId
	 *            the userId
	 * @return boolean
	 */
	private boolean validateUserStatus(String userId) {
		RegistrationUserDetail userDetail = loginService.getUserDetail(userId);

		LOGGER.debug("REGISTRATION - USER_STATUS - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating User Status");

		return userDetail != null && userDetail.getStatusCode() != null
				&& userDetail.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED);
	}

	/**
	 * Validating User role and Machine mapping during login
	 * 
	 * @param userId
	 *            entered userId
	 * @throws RegBaseCheckedException
	 */
	private boolean setInitialLoginInfo(String userId) throws RegBaseCheckedException {
		RegistrationUserDetail userDetail = loginService.getUserDetail(userId);
		String authInfo = null;
		List<String> roleList = new ArrayList<>();

		userDetail.getUserRole().forEach(roleCode -> {
			if (roleCode.getIsActive()) {
				roleList.add(String.valueOf(roleCode.getRegistrationUserRoleID().getRoleCode()));
			}
		});

		LOGGER.debug("REGISTRATION - ROLES_MACHINE_MAPPING - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating roles and machine and center mapping");

		// Checking roles
		if (roleList.isEmpty()) {
			authInfo = RegistrationConstants.ROLES_EMPTY;
		} else if (roleList.contains(RegistrationConstants.ADMIN_ROLE)) {
			authInfo = RegistrationConstants.SUCCESS_MSG;
		} else {
			// checking for machine mapping
			if (!getCenterMachineStatus(userDetail)) {
				authInfo = RegistrationConstants.MACHINE_MAPPING;
			} else {
				authInfo = RegistrationConstants.SUCCESS_MSG;
			}
		}
		return setSessionContext(authInfo, userDetail, roleList);
	}

	/**
	 * Fetching and Validating machine and center id
	 * 
	 * @param userDetail
	 *            the userDetail
	 * @return boolean
	 * @throws RegBaseCheckedException
	 */
	private boolean getCenterMachineStatus(RegistrationUserDetail userDetail) {
		List<String> machineList = new ArrayList<>();
		List<String> centerList = new ArrayList<>();

		LOGGER.debug("REGISTRATION - MACHINE_MAPPING - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating User machine and center mapping");

		userDetail.getUserMachineMapping().forEach(machineMapping -> {
			if (machineMapping.getIsActive()) {
				machineList.add(machineMapping.getMachineMaster().getMacAddress());
				centerList.add(machineMapping.getUserMachineMappingId().getCentreID());
			}
		});
		return machineList.contains(RegistrationSystemPropertiesChecker.getMachineId()) && centerList
				.contains(userDetail.getRegistrationCenterUser().getRegistrationCenterUserId().getRegcntrId());
	}

	/**
	 * Setting values for Session context and User context and Initial info for
	 * Login
	 * 
	 * @param userId
	 *            entered userId
	 * @param userDetail
	 *            userdetails
	 * @param roleList
	 *            list of user roles
	 * @throws RegBaseCheckedException
	 */
	private boolean setSessionContext(String authInfo, RegistrationUserDetail userDetail, List<String> roleList) {
		boolean result = false;

		LOGGER.debug("REGISTRATION - SESSION_CONTEXT - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating roles and machine and center mapping");

		if (authInfo != null && authInfo.equals(RegistrationConstants.ROLES_EMPTY)) {
			generateAlert(RegistrationConstants.AUTHORIZATION_ALERT_TITLE,
					AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.LOGIN_FAILURE,
					RegistrationConstants.ROLES_EMPTY_ERROR);
		} else if (authInfo != null && authInfo.equals(RegistrationConstants.MACHINE_MAPPING)) {
			generateAlert(RegistrationConstants.AUTHORIZATION_ALERT_TITLE,
					AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.LOGIN_FAILURE,
					RegistrationConstants.MACHINE_MAPPING_ERROR);
		} else if (authInfo != null && authInfo.equalsIgnoreCase(RegistrationConstants.SUCCESS_MSG)) {
			SessionContext sessionContext = SessionContext.getInstance();

			LOGGER.debug("REGISTRATION - SESSION_CONTEXT - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					"Setting values for session context and user context");

			sessionContext.setLoginTime(new Date());
			sessionContext.setRefreshedLoginTime(refreshedLoginTime);
			sessionContext.setIdealTime(idealTime);
			sessionContext.setTimeoutInterval(timeoutInterval);

			SessionContext.UserContext userContext = sessionContext.getUserContext();
			userContext.setUserId(userId.getText());
			userContext.setName(userDetail.getName());
			userContext.setRoles(roleList);
			userContext.setRegistrationCenterDetailDTO(loginService.getRegistrationCenterDetails(
					userDetail.getRegistrationCenterUser().getRegistrationCenterUserId().getRegcntrId()));

			String userRole = !userContext.getRoles().isEmpty() ? userContext.getRoles().get(0) : null;
			userContext.setAuthorizationDTO(loginService.getScreenAuthorizationDetails(userRole));
			result = true;
		}
		return result;
	}

	/**
	 * Loading login screen after logout with multiple screens in case of
	 * multifactor authentication
	 * 
	 * @param sessionContext
	 *            the sessionContext
	 * @param loginModeToLoad
	 *            the loginMode to load
	 */
	private void loadLoginAfterLogout(SessionContext sessionContext, String loginModeToLoad) {

		LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Resetting login sequence to the Session context after log out");

		if (sessionContext.getMapObject() == null) {
			Map<String, Object> userLoginMode = loginService.getModesOfLogin();
			sessionContext.setMapObject(userLoginMode);
			sessionContext.getMapObject().put(RegistrationConstants.LOGIN_INITIAL_SCREEN, loginModeToLoad);
		}
	}

	/**
	 * Loading next login screen in case of multifactor authentication
	 * 
	 * @param sessionContext
	 *            the sessionContext
	 */
	private void loadNextScreen(RegistrationUserDetail userDetail, SessionContext sessionContext, String loginMode)
			throws IOException, RegBaseCheckedException {

		int counter = (int) sessionContext.getMapObject().get(RegistrationConstants.LOGIN_SEQUENCE);
		counter++;

		if (sessionContext.getMapObject().containsKey(String.valueOf(counter))) {

			LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					"Loading next login screen in case of multifactor authentication");

			String mode = sessionContext.getMapObject().get(String.valueOf(counter)).toString();
			sessionContext.getMapObject().remove(String.valueOf(counter));

			loadLoginScreen(mode);
		} else {
			if (setInitialLoginInfo(userId.getText())) {

				LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
						"Loading Home screen");

				schedulerUtil.startSchedulerUtil();

				BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));

				userDetail.setLastLoginMethod(loginMode);
				userDetail.setLastLoginDtimes(Timestamp.valueOf(LocalDateTime.now()));
				userDetail.setUnsuccessfulLoginCount(RegistrationConstants.INVALID_LOGIN_COUNT_RESET);

				loginService.updateLoginParams(userDetail);
			}
		}
	}

	/**
	 * Validating User Biometrics using Minutia
	 * 
	 * @param minutia
	 *            minutia of fingerprint
	 * @param registrationUserDetail
	 *            user details
	 * @return boolean
	 */
	private boolean validateBiometric(RegistrationUserDetail registrationUserDetail) {

		LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Initializing FingerPrint device");

		MosipFingerprintProvider fingerprintProvider = fingerprintFacade.getFingerprintProviderFactory(deviceName);
		
		if(fingerprintProvider.captureFingerprint(qualityScore, captureTimeOut, RegistrationConstants.FINGER_TYPE_MINUTIA) != 0) {
			
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
					AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.DEVICE_INFO_MESSAGE,
					RegistrationConstants.DEVICE_FP_NOT_FOUND);
			
			return false;
		} else {
			// Thread to wait until capture the bio image/ minutia from FP. based on the
			// error code or success code the respective action will be taken care.
			waitToCaptureBioImage(5, 2000, fingerprintFacade);

			LOGGER.debug("REGISTRATION - SCAN_FINGER - SCAN_FINGER_COMPLETED", APPLICATION_NAME, APPLICATION_ID,
					"Fingerprint scan done");

			fingerprintProvider.uninitFingerPrintDevice();

			LOGGER.debug("REGISTRATION - LOGIN - BIOMETRICS", APPLICATION_NAME, APPLICATION_ID,
					"Validation of fingerprint through Minutia");
			
			boolean fingerPrintStatus = false;
			
			if (!RegistrationConstants.EMPTY.equals(fingerprintFacade.getMinutia())) {
				
				fingerPrintStatus = registrationUserDetail.getUserBiometric().stream().anyMatch(bio -> fingerprintProvider
						.scoreCalculator(fingerprintFacade.getMinutia(), bio.getBioMinutia()) > fingerPrintScore);
				
			} else if (!RegistrationConstants.EMPTY.equals(fingerprintFacade.getErrorMessage())) {
				if (fingerprintFacade.getErrorMessage().equals("Timeout")) {
					generateAlert(RegistrationConstants.ALERT_ERROR,
							AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							RegistrationConstants.LOGIN_FAILURE,
							RegistrationConstants.FP_DEVICE_TIMEOUT);
				} else {
					generateAlert(RegistrationConstants.ALERT_ERROR,
							AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							RegistrationConstants.LOGIN_FAILURE,
							RegistrationConstants.FP_DEVICE_ERROR);
				}
			}
			return fingerPrintStatus;
		}

	}

	/**
	 * Validating invalid number of login attempts
	 * 
	 * @param registrationUserDetail
	 *            user details
	 * @param userId
	 *            entered userId
	 * @return boolean
	 */
	private boolean validateInvalidLogin(RegistrationUserDetail registrationUserDetail, String errorMessage) {

		LOGGER.debug("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
				"Fetching invalid login params");

		int loginCount = registrationUserDetail.getUnsuccessfulLoginCount() != null
				? registrationUserDetail.getUnsuccessfulLoginCount().intValue()
				: 0;

		Timestamp loginTime = registrationUserDetail.getUserlockTillDtimes();

		int invalidLoginCount = Integer.parseInt(String.valueOf(
				ApplicationContext.getInstance().getApplicationMap().get(RegistrationConstants.INVALID_LOGIN_COUNT)));

		int invalidLoginTime = Integer.parseInt(String.valueOf(
				ApplicationContext.getInstance().getApplicationMap().get(RegistrationConstants.INVALID_LOGIN_TIME)));

		LOGGER.debug("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
				"validating invalid login params");

		if (validateLoginTime(loginCount, invalidLoginCount, loginTime, invalidLoginTime)) {

			loginCount = RegistrationConstants.INVALID_LOGIN_COUNT_RESET;
			registrationUserDetail.setUnsuccessfulLoginCount(RegistrationConstants.INVALID_LOGIN_COUNT_RESET);

			loginService.updateLoginParams(registrationUserDetail);

		}

		String unlockMessage = RegistrationConstants.USER_ACCOUNT_LOCK_MESSAGE_NUMBER
				.concat(String.valueOf(invalidLoginCount))
				.concat(RegistrationConstants.USER_ACCOUNT_LOCK_MESSAGE).concat(String.valueOf(invalidLoginTime)
						.concat(RegistrationConstants.USER_ACCOUNT_LOCK_MESSAGE_MINUTES));
		
		if (loginCount > invalidLoginCount) {

			LOGGER.debug("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
					"validating login count and time ");

			if (TimeUnit.MILLISECONDS.toMinutes(loginTime.getTime() - System.currentTimeMillis()) > invalidLoginTime) {

				registrationUserDetail.setUnsuccessfulLoginCount(RegistrationConstants.INVALID_LOGIN_COUNT_INCREMENT);

				loginService.updateLoginParams(registrationUserDetail);

			} else {
	
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.LOGIN_INFO_MESSAGE,
						unlockMessage);

			}
			return false;

		} else {
			if (!errorMessage.isEmpty()) {

				LOGGER.debug("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
						"updating login count and time for invalid login attempts");
				loginCount = loginCount + RegistrationConstants.INVALID_LOGIN_COUNT_INCREMENT;
				registrationUserDetail.setUserlockTillDtimes(Timestamp.valueOf(LocalDateTime.now()));
				registrationUserDetail
						.setUnsuccessfulLoginCount(loginCount);

				loginService.updateLoginParams(registrationUserDetail);

				if (loginCount == invalidLoginCount) {

					generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
							AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							RegistrationConstants.LOGIN_INFO_MESSAGE, unlockMessage);

				} else {

					generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
							AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							RegistrationConstants.LOGIN_INFO_MESSAGE, errorMessage);

				}
				return false;
			}
			return true;
		}
	}

	/**
	 * Validating login time and count
	 * 
	 * @param loginCount
	 *            number of invalid attempts
	 * @param invalidLoginCount
	 *            count from global param
	 * @param loginTime
	 *            login time from table
	 * @param invalidLoginTime
	 *            login time from global param
	 * @return boolean
	 */
	private boolean validateLoginTime(int loginCount, int invalidLoginCount, Timestamp loginTime,
			int invalidLoginTime) {

		LOGGER.debug("REGISTRATION - LOGIN - COMAPRE_TIME_STAMPS", APPLICATION_NAME, APPLICATION_ID,
				"Comparing timestamps in case of invalid login attempts");

		return (loginCount >= invalidLoginCount && TimeUnit.MILLISECONDS
				.toMinutes(System.currentTimeMillis() - loginTime.getTime()) > invalidLoginTime);
	}

}
