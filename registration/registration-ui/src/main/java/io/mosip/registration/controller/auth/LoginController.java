package io.mosip.registration.controller.auth;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.URL;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.device.fp.MosipFingerprintProvider;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.AuthenticationService;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.util.common.OTPManager;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
	private TextField userId;

	@FXML
	private TextField password;

	@FXML
	private Button submit;

	@FXML
	private Button submitUserId;

	@FXML
	private Button getOTP;

	@FXML
	private Button resend;

	@FXML
	private Label otpValidity;

	@FXML
	private Label bioText;

	@FXML
	private ImageView bioImage;

	@FXML
	private VBox mainBox;

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
	private AuthenticationService authService;

	@Autowired
	private OTPManager otpGenerator;

	@Autowired
	private SchedulerUtil schedulerUtil;

	@Autowired
	private FingerprintFacade fingerprintFacade;

	private boolean isNewUser = false;

	AnchorPane optionRoot;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		otpValidity.setText("Valid for " + otpValidityImMins + " minutes");
		stopTimer();
	}
	
	private List<String> loginList = new ArrayList<>();

	/**
	 * To get the Sequence of which Login screen to be displayed
	 * 
	 * @return String loginMode
	 * @throws RegBaseCheckedException
	 */
	public void loadInitialScreen(Stage primaryStage) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Retrieve Login mode");

		fXComponents.setStage(primaryStage);

		try {

			BorderPane loginRoot = BaseController.load(getClass().getResource(RegistrationConstants.INITIAL_PAGE));

			scene = getScene(loginRoot);

			enableUserId();
			getGlobalParams();

			primaryStage.setMaximized(true);
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException ioException) {

			LOGGER.error(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
		} catch (RuntimeException runtimeException) {

			LOGGER.error(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
		}

	}

	/**
	 * Validate user id.
	 *
	 * @param event the event
	 */
	public RegistrationUserDetail validateUserId(ActionEvent event) {

		LOGGER.debug("REGISTRATION - LOGIN_MODE_PWORD - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials entered through UI");
		
		if (SessionContext.getInstance().getMapObject() == null) {
			loginList = loginService.getModesOfLogin(ProcessNames.LOGIN.getType());
			SessionContext.getInstance().setMapObject(new HashMap<String, Object>());
		}
		
		RegistrationUserDetail userDetail = loginService.getUserDetail(userId.getText());

		if (null != userDetail) {

			if (getCenterMachineStatus(userDetail)) {

				String loginMode = null;

				try {

					SessionContext.getInstance().getMapObject().put("isNewUser", isNewUser);

					if (!loginList.isEmpty()) {
						loginMode = loginList.get(RegistrationConstants.PARAM_ZERO);
					}

					loginList.remove(RegistrationConstants.PARAM_ZERO);
					// Load the property files for application and local language

					LOGGER.debug(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME,
							APPLICATION_ID, "Retrieved corresponding Login mode");

					if (loginMode == null) {
						AnchorPane loginType = BaseController
								.load(getClass().getResource(RegistrationConstants.ERROR_PAGE));
						scene = getScene(loginType);
					} else {
						loadLoginScreen(loginMode);
					}

				} catch (IOException ioException) {

					LOGGER.error(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME,
							APPLICATION_ID, ioException.getMessage());

					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
				}
			} else {
				SessionContext.getInstance().getMapObject().put("isNewUser", true);
				enablePWD();
			}

		} else {

		}
		return userDetail;

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
		
		RegistrationUserDetail userDetail = loginService.getUserDetail(userId.getText());
		
		LoginUserDTO userDTO = new LoginUserDTO();
		userDTO.setUserId(userId.getText().toLowerCase());
		userDTO.setPassword(password.getText());

		boolean serverStatus = getConnectionCheck(userDTO);
		boolean offlineStatus = false;
		boolean flag = (boolean) SessionContext.getInstance().getMapObject().get("isNewUser");
		if (!serverStatus) {
			String status = validatePwd(userId.getText().toLowerCase(), password.getText().toLowerCase());

			if (RegistrationConstants.SUCCESS.equals(status)) {

				if (!flag) {
					offlineStatus = validateInvalidLogin(userDetail, "");
				} else {
					offlineStatus = validateInvalidLogin(userDetail, "");
					enableOTP();
				}
			} else if (RegistrationConstants.FAILURE.equals(status)) {
				offlineStatus = validateInvalidLogin(userDetail, RegistrationUIConstants.INCORRECT_PWORD);
			}
		}

		if (!flag) {
		if (serverStatus || offlineStatus) {
			try {

				LOGGER.debug(RegistrationConstants.REGISTRATION_LOGIN_PWORD_LOGIN_CONTROLLER, APPLICATION_NAME,
						APPLICATION_ID, "Loading next login screen");

				SessionContext sessionContext = SessionContext.getInstance();
				loadNextScreen(userDetail, sessionContext, RegistrationConstants.LOGIN_METHOD_PWORD);

			} catch (IOException | RuntimeException | RegBaseCheckedException exception) {

				LOGGER.error(RegistrationConstants.REGISTRATION_LOGIN_PWORD_LOGIN_CONTROLLER, APPLICATION_NAME,
						APPLICATION_ID, exception.getMessage());

				generateAlert(RegistrationConstants.ALERT_ERROR,
						RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
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
			responseDTO = otpGenerator.getOTP(userId.getText());

			if (responseDTO.getSuccessResponseDTO() != null) {
				// Enable submit button
				changeToOTPSubmitMode();

				// Generate alert to show OTP
				SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
				generateAlert(RegistrationConstants.ALERT_ERROR, successResponseDTO.getMessage());

			} else if (responseDTO.getErrorResponseDTOs() != null) {
				// Generate Alert to show INVALID USERNAME
				ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
				generateAlert(RegistrationConstants.ALERT_ERROR, errorResponseDTO.getMessage());

			}

		} else {
			// Generate Alert to show username field was empty
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);

		}

	}

	/**
	 * Validate User through username and otp
	 * 
	 * @param event
	 */
	@FXML
	public void validateOTP(ActionEvent event) {
		if (!password.getText().isEmpty() && password.getText().length() != 3) {

			RegistrationUserDetail userDetail = loginService.getUserDetail(userId.getText());

			if (userDetail == null) {

				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USER_NOT_ONBOARDED);

			} else {
				boolean otpLoginStatus = false;
				
				if (otpGenerator.validateOTP(userId.getText(), password.getText())) {
						otpLoginStatus = validateInvalidLogin(userDetail, "");
				} else {
					otpLoginStatus = validateInvalidLogin(userDetail, RegistrationUIConstants.OTP_VALIDATION_ERROR_MESSAGE);
				}
				
				if (otpLoginStatus) {
					// // Validating User status
					if (validateUserStatus(userId.getText())) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.BLOCKED_USER_ERROR);
					} else {
						try {
							boolean flag = (boolean) SessionContext.getInstance().getMapObject().get("isNewUser");
							if (!flag) {
								SessionContext sessionContext = SessionContext.getInstance();
								loadNextScreen(userDetail, sessionContext, RegistrationConstants.LOGIN_METHOD_OTP);
							} else {
								setInitialLoginInfo(userDetail.getId());
								Parent parent = BaseController
										.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
								getScene(parent);
							}

						} catch (IOException | RuntimeException | RegBaseCheckedException exception) {

							LOGGER.error("REGISTRATION - LOGIN_OTP - LOGIN_CONTROLLER", APPLICATION_NAME,
									APPLICATION_ID, exception.getMessage());

							generateAlert(RegistrationConstants.ALERT_ERROR,
									RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
						}

					}

				}
			}

		} else if (userId.getText().length() == 3) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USERNAME_FIELD_ERROR);
		} else {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.OTP_FIELD_EMPTY);
		}
	}

	/**
	 * Validate User through username and fingerprint
	 * 
	 * @param event
	 */
	public void validateFingerPrint(ActionEvent event) {

		LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials for Biometric login");

		if (userId.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
		} else {

			RegistrationUserDetail detail = loginService.getUserDetail(userId.getText());

			if (detail == null) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USER_NOT_ONBOARDED);
			} else {

				LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
						"Validating Fingerprint with minutia");

				boolean bioLoginStatus = false;

				if (validateBiometricFP()) {
					bioLoginStatus = validateInvalidLogin(detail, "");
				} else {
					bioLoginStatus = validateInvalidLogin(detail, RegistrationUIConstants.FINGER_PRINT_MATCH);
				}
				if (bioLoginStatus) {
					if (detail.getStatusCode() != null
							&& detail.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED)) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.BLOCKED_USER_ERROR);
					} else {
						try {
							SessionContext sessionContext = SessionContext.getInstance();
							loadNextScreen(detail, sessionContext, RegistrationConstants.LOGIN_METHOD_BIO);
						} catch (IOException | RuntimeException | RegBaseCheckedException exception) {

							LOGGER.error("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME,
									APPLICATION_ID, exception.getMessage());

							generateAlert(RegistrationConstants.ALERT_ERROR,
									RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
						}
					}
				}
			}

			LOGGER.debug("REGISTRATION - SCAN_FINGER - FINGER_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
					"Fingerprint validation done");

		}
	}
	
	/**
	 * Validate User through username and Iris
	 * 
	 * @param event
	 */
	public void validateIris(ActionEvent event) {
		LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Biometric login with Iris");

		if (userId.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
		} else {

			RegistrationUserDetail detail = loginService.getUserDetail(userId.getText());

			if (detail == null) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USER_NOT_ONBOARDED);
			} else {

				LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
						"Validating Iris with stored data");

				boolean irisLoginStatus = false;

				if (validateBiometricIris()) {
					irisLoginStatus = validateInvalidLogin(detail, "");
				} else {
					irisLoginStatus = validateInvalidLogin(detail, RegistrationUIConstants.IRIS_MATCH);
				}
				if (irisLoginStatus) {
					if (detail.getStatusCode() != null
							&& detail.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED)) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.BLOCKED_USER_ERROR);
					} else {
						try {
							SessionContext sessionContext = SessionContext.getInstance();
							loadNextScreen(detail, sessionContext, RegistrationConstants.LOGIN_METHOD_IRIS);
						} catch (IOException | RuntimeException | RegBaseCheckedException exception) {

							LOGGER.error("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME,
									APPLICATION_ID, exception.getMessage());

							generateAlert(RegistrationConstants.ALERT_ERROR,
									RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
						}
					}
				}
			}

			LOGGER.debug("REGISTRATION - SCAN_IRIS - IRIS_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
					"Iris validation done");

		}
	}
	
	/**
	 * Validate User through username and face
	 * 
	 * @param event
	 */
	public void validateFace(ActionEvent event) {
		LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Biometric login with Iris");

		if (userId.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
		} else {

			RegistrationUserDetail detail = loginService.getUserDetail(userId.getText());

			if (detail == null) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.USER_NOT_ONBOARDED);
			} else {

				LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
						"Validating Face with stored data");

				boolean faceLoginStatus = false;

				if (validateBiometricFace()) {
					faceLoginStatus = validateInvalidLogin(detail, "");
				} else {
					faceLoginStatus = validateInvalidLogin(detail, RegistrationUIConstants.FACE_MATCH);
				}
				if (faceLoginStatus) {
					if (detail.getStatusCode() != null
							&& detail.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED)) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.BLOCKED_USER_ERROR);
					} else {
						try {
							SessionContext sessionContext = SessionContext.getInstance();
							loadNextScreen(detail, sessionContext, RegistrationConstants.LOGIN_METHOD_FACE);
						} catch (IOException | RuntimeException | RegBaseCheckedException exception) {

							LOGGER.error("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME,
									APPLICATION_ID, exception.getMessage());

							generateAlert(RegistrationConstants.ALERT_ERROR,
									RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
						}
					}
				}
			}

			LOGGER.debug("REGISTRATION - SCAN_IRIS - IRIS_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
					"Face validation done");

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
		userIdPane.setVisible(false);
		credentialsPane.setVisible(true);
		password.clear();
		password.setVisible(true);
		password.setPromptText("Enter OTP");
		otpValidity.setVisible(true);
		getOTP.setVisible(true);
		bioText.setVisible(false);
		bioImage.setVisible(false);
		getOTP.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				generateOtp(event);
			}
		});
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				validateOTP(event);
			}
		});
	}

	/**
	 * Enable PWD login specific attributes
	 */
	private void enablePWD() {
		userIdPane.setVisible(false);
		credentialsPane.setVisible(true);
		submit.setVisible(true);
		password.clear();
		password.setVisible(true);
		otpValidity.setVisible(false);
		getOTP.setVisible(false);
		resend.setVisible(false);
		bioText.setVisible(false);
		bioImage.setVisible(false);
		if(!userId.getText().isEmpty()) {
			userId.setEditable(false);
		}
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				validateCredentials(event);
			}
		});
	}

	/**
	 * Enable Fingerprint login specific attributes
	 */
	private void enableFingerPrint() {
		userIdPane.setVisible(false);
		credentialsPane.setVisible(true);
		password.setVisible(false);
		otpValidity.setVisible(false);
		getOTP.setVisible(false);
		resend.setVisible(false);
		bioText.setVisible(true);
		bioText.setText(RegistrationConstants.FINGERPRINT_TEXT);
		bioImage.setVisible(true);
		Image image = new Image(this.getClass().getResourceAsStream(RegistrationConstants.FP_IMG_PATH));
		bioImage.setImage(image);
		if(!userId.getText().isEmpty()) {
			userId.setEditable(false);
		}
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				validateFingerPrint(event);
			}
		});
	}
	
/**
	 * Enable Iris login specific attributes
	 */
	public void enableUserId() {
		userIdPane.setVisible(true);
		credentialsPane.setVisible(false);
		bioText.setText("Enter your username");
	}

	/**
	 * Enable Iris login specific attributes
	 */
	private void enableIris(){
		password.setVisible(false);
		otpValidity.setVisible(false);
		getOTP.setVisible(false);
		resend.setVisible(false);
		bioText.setVisible(true);
		bioText.setText(RegistrationConstants.IRIS_TEXT);
		bioImage.setVisible(true);
		Image image = new Image(this.getClass().getResourceAsStream(RegistrationConstants.IRIS_IMG_PATH));
		bioImage.setImage(image);
		if(!userId.getText().isEmpty()) {
			userId.setEditable(false);
		}
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				validateIris(event);
			}
		});
	}
	
	/**
	 * Enable Face login specific attributes
	 */
	private void enableFace(){
		password.setVisible(false);
		otpValidity.setVisible(false);
		getOTP.setVisible(false);
		resend.setVisible(false);
		bioText.setVisible(true);
		bioText.setText(RegistrationConstants.FACE_TEXT);
		bioImage.setVisible(true);
		Image image = new Image(this.getClass().getResourceAsStream(RegistrationConstants.FACE_IMG_PATH));
		bioImage.setImage(image);
		if(!userId.getText().isEmpty()) {
			userId.setEditable(false);
		}
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				validateFace(event);
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
		case RegistrationConstants.LOGIN_METHOD_IRIS:
			enableIris();
			break;
		case RegistrationConstants.LOGIN_METHOD_FACE:
			enableFace();
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
		if (!((roleList.contains(RegistrationConstants.SUPERVISOR) || roleList.contains(RegistrationConstants.OFFICER)))) {
			authInfo = RegistrationConstants.ROLES_EMPTY;
		} else if (roleList.contains(RegistrationConstants.ADMIN_ROLE)) {
			authInfo = RegistrationConstants.SUCCESS_MSG;
		} else {
			// checking for machine mapping
			/*
			 * if (!getCenterMachineStatus(userDetail)) { authInfo =
			 * RegistrationConstants.MACHINE_MAPPING; }
			 */ // else {
			authInfo = RegistrationConstants.SUCCESS_MSG;
			// }
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
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.ROLES_EMPTY_ERROR);
		} /*
			 * else if (authInfo != null &&
			 * authInfo.equals(RegistrationConstants.MACHINE_MAPPING)) {
			 * generateAlert(RegistrationConstants.ALERT_ERROR,
			 * RegistrationUIConstants.MACHINE_MAPPING_ERROR); }
			 */ else if (authInfo != null && authInfo.equalsIgnoreCase(RegistrationConstants.SUCCESS_MSG)) {
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
			userContext.setAuthorizationDTO(loginService.getScreenAuthorizationDetails(roleList));
			userContext.setUserMap(new HashMap<String,Object>());
			result = true;
		}
		return result;
	}

	/**
	 * Loading next login screen in case of multifactor authentication
	 * 
	 * @param sessionContext
	 *            the sessionContext
	 */
	private void loadNextScreen(RegistrationUserDetail userDetail, SessionContext sessionContext, String loginMode)
			throws IOException, RegBaseCheckedException {

		if (!loginList.isEmpty()) {

			LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					"Loading next login screen in case of multifactor authentication");

			loadLoginScreen(loginList.get(RegistrationConstants.PARAM_ZERO));
			loginList.remove(RegistrationConstants.PARAM_ZERO);
			
		} else {
			if (setInitialLoginInfo(userId.getText())) {

				LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
						"Loading Home screen");
				try {
					schedulerUtil.startSchedulerUtil();

					BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));

					userDetail.setLastLoginMethod(loginMode);
					userDetail.setLastLoginDtimes(Timestamp.valueOf(LocalDateTime.now()));
					userDetail.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ZERO);

					loginService.updateLoginParams(userDetail);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Validating User Biometrics using Minutia
	 * 
	 * @return boolean
	 */
	private boolean validateBiometricFP() {

		LOGGER.debug("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Initializing FingerPrint device");

		MosipFingerprintProvider fingerPrintConnector = fingerprintFacade.getFingerprintProviderFactory(deviceName);

		if (fingerPrintConnector.captureFingerprint(qualityScore, captureTimeOut, "") != 0) {

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.DEVICE_FP_NOT_FOUND);

			return false;
		} else {
			// Thread to wait until capture the bio image/ minutia from FP. based on the
			// error code or success code the respective action will be taken care.
			waitToCaptureBioImage(5, 2000, fingerprintFacade);

			LOGGER.debug("REGISTRATION - SCAN_FINGER - SCAN_FINGER_COMPLETED", APPLICATION_NAME, APPLICATION_ID,
					"Fingerprint scan done");

			fingerPrintConnector.uninitFingerPrintDevice();

			LOGGER.debug("REGISTRATION - LOGIN - BIOMETRICS", APPLICATION_NAME, APPLICATION_ID,
					"Validation of fingerprint through Minutia");

			boolean fingerPrintStatus = false;

			if (RegistrationConstants.EMPTY.equals(fingerprintFacade.getMinutia())) {
				
				AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
				List<FingerprintDetailsDTO> fingerprintDetailsDTOs = new ArrayList<>();
				FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
				fingerprintDetailsDTO.setFingerPrint(fingerprintFacade.getIsoTemplate());
				fingerprintDetailsDTOs.add(fingerprintDetailsDTO);
				
				authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
				authenticationValidatorDTO.setUserId(userId.getText());
				authenticationValidatorDTO.setAuthValidationType(RegistrationConstants.VALIDATION_TYPE_FP_SINGLE);
				fingerPrintStatus = authService.authValidator(RegistrationConstants.VALIDATION_TYPE_FP,
						authenticationValidatorDTO);

			} else if (!RegistrationConstants.EMPTY.equals(fingerprintFacade.getErrorMessage())) {
				if (fingerprintFacade.getErrorMessage().equals("Timeout")) {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.FP_DEVICE_TIMEOUT);
				} else {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.FP_DEVICE_ERROR);
				}
			}
			return fingerPrintStatus;
		}

	}
	
	/**
	 * Validating User Biometrics using Iris
	 * 
	 * @return boolean
	 */
	private boolean validateBiometricIris() {
		
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		List<IrisDetailsDTO> irisDetailsDTOs = new ArrayList<>();
		IrisDetailsDTO irisDetailsDTO = new IrisDetailsDTO();
		irisDetailsDTO.setIris("leftIris".getBytes());
		irisDetailsDTOs.add(irisDetailsDTO);
		authenticationValidatorDTO.setUserId(userId.getText());
		authenticationValidatorDTO.setIrisDetails(irisDetailsDTOs);
		return authService.authValidator(RegistrationConstants.VALIDATION_TYPE_IRIS,
				authenticationValidatorDTO);
	}
	
	/**
	 * Validating User Biometrics using Face
	 * 
	 * @return boolean
	 */
	private boolean validateBiometricFace() {
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		FaceDetailsDTO faceDetailsDTO = new FaceDetailsDTO();
		faceDetailsDTO.setFace("face".getBytes());
		authenticationValidatorDTO.setUserId(userId.getText());
		authenticationValidatorDTO.setFaceDetail(faceDetailsDTO);
		return authService.authValidator(RegistrationConstants.VALIDATION_TYPE_FACE,
				authenticationValidatorDTO);
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
				applicationContext.getApplicationMap().get(RegistrationConstants.INVALID_LOGIN_COUNT)));

		int invalidLoginTime = Integer.parseInt(String.valueOf(
				applicationContext.getApplicationMap().get(RegistrationConstants.INVALID_LOGIN_TIME)));

		LOGGER.debug("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
				"validating invalid login params");

		if (validateLoginTime(loginCount, invalidLoginCount, loginTime, invalidLoginTime)) {

			loginCount = RegistrationConstants.PARAM_ZERO;
			registrationUserDetail.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ZERO);

			loginService.updateLoginParams(registrationUserDetail);

		}

		String unlockMessage = RegistrationUIConstants.USER_ACCOUNT_LOCK_MESSAGE_NUMBER.concat(" ")
				.concat(String.valueOf(invalidLoginCount)).concat(" ")
				.concat(RegistrationUIConstants.USER_ACCOUNT_LOCK_MESSAGE).concat(" ")
				.concat(String.valueOf(invalidLoginTime).concat(" ")
						.concat(RegistrationUIConstants.USER_ACCOUNT_LOCK_MESSAGE_MINUTES));

		if (loginCount >= invalidLoginCount) {

			LOGGER.debug("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
					"validating login count and time ");

			if (TimeUnit.MILLISECONDS.toMinutes(loginTime.getTime() - System.currentTimeMillis()) > invalidLoginTime) {

				registrationUserDetail.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ONE);

				loginService.updateLoginParams(registrationUserDetail);

			} else {

				generateAlert(RegistrationConstants.ALERT_ERROR, unlockMessage);

			}
			return false;

		} else {
			if (!errorMessage.isEmpty()) {

				LOGGER.debug("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
						"updating login count and time for invalid login attempts");
				loginCount = loginCount + RegistrationConstants.PARAM_ONE;
				registrationUserDetail.setUserlockTillDtimes(Timestamp.valueOf(LocalDateTime.now()));
				registrationUserDetail.setUnsuccessfulLoginCount(loginCount);

				loginService.updateLoginParams(registrationUserDetail);

				if (loginCount >= invalidLoginCount) {

					generateAlert(RegistrationConstants.ALERT_ERROR, unlockMessage);

				} else {

					generateAlert(RegistrationConstants.ALERT_ERROR, errorMessage);

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
