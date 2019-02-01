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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
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
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.device.face.FaceFacade;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.device.fp.MosipFingerprintProvider;
import io.mosip.registration.device.iris.IrisFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.AuthenticationService;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.service.UserOnboardService;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.util.common.OTPManager;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
	private Button getOTP;

	@FXML
	private Button resend;

	@FXML
	private Label otpValidity;

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
	private UserOnboardService userOnboardService;

	@Autowired
	private FingerprintFacade fingerprintFacade;

	@Autowired
	private IrisFacade irisFacade;

	@Autowired
	private FaceFacade faceFacade;

	private boolean isNewUser = false;

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

		ResponseDTO responseDTO = getSyncConfigData();

		if (responseDTO.getErrorResponseDTOs() != null) {
			ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
			generateAlert(RegistrationConstants.ERROR, errorResponseDTO.getMessage());
		} else {

			LOGGER.info(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME,
					APPLICATION_ID, "Retrieve Login mode");

			fXComponents.setStage(primaryStage);

			try {

				BorderPane loginRoot = BaseController.load(getClass().getResource(RegistrationConstants.INITIAL_PAGE));

				scene = getScene(loginRoot);
				getGlobalParams();

				primaryStage.setMaximized(true);
				primaryStage.setResizable(false);
				primaryStage.setScene(scene);
				primaryStage.show();

			} catch (IOException ioException) {

				LOGGER.error(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME,
						APPLICATION_ID, ioException.getMessage());

				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
			} catch (RuntimeException runtimeException) {

				LOGGER.error(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME,
						APPLICATION_ID, runtimeException.getMessage());

				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
			}
		}
	}

	/**
	 * Validate user id.
	 *
	 * @param event
	 *            the event
	 */
	public void validateUserId(ActionEvent event) {

		LOGGER.info("REGISTRATION - LOGIN_MODE_PWORD - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials entered through UI");

		if (userId.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
		} else if (userId.getText().length() > usernamePwdLength) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USRNAME_LENGTH);
		} else {

			try {

				UserDetail userDetail = loginService.getUserDetail(userId.getText());

				String regCenter = (String) applicationContext.getApplicationMap()
						.get(RegistrationConstants.REGISTARTION_CENTER);

				if (regCenter
						.equals(userOnboardService.getMachineCenterId().get(RegistrationConstants.USER_CENTER_ID))) {

					String stationID = userOnboardService.getMachineCenterId()
							.get(RegistrationConstants.USER_STATION_ID);
					applicationContext.getApplicationMap().put(RegistrationConstants.MACHINE_ID, stationID);

					if (userDetail == null) {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_ONBOARDED);
					} else if (userDetail.getStatusCode().equalsIgnoreCase(RegistrationConstants.BLOCKED)) {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.BLOCKED_USER_ERROR);
					} else {

						Set<String> roleList = new LinkedHashSet<>();

						userDetail.getUserRole().forEach(roleCode -> {
							if (roleCode.getIsActive()) {
								roleList.add(String.valueOf(roleCode.getUserRoleID().getRoleCode()));
							}
						});

						LOGGER.info("REGISTRATION - ROLES - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
								"Validating roles");
						// Checking roles
						if (roleList.isEmpty() || !(roleList.contains(RegistrationConstants.OFFICER)
								|| roleList.contains(RegistrationConstants.SUPERVISOR)
								|| roleList.contains(RegistrationConstants.ADMIN_ROLE))) {
							generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.ROLES_EMPTY_ERROR);
						} else {

							if (SessionContext.getInstance().getMapObject() == null) {

								SessionContext.getInstance().setMapObject(new HashMap<String, Object>());

								SessionContext.getInstance().getMapObject().put(RegistrationConstants.USER_STATION_ID,
										stationID);

								if (getCenterMachineStatus(userDetail)) {
									SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_USER,
											isNewUser);
									SessionContext.getInstance().getMapObject()
											.put(RegistrationConstants.ONBOARD_USER_UPDATE, false);
									loginList = loginService.getModesOfLogin(ProcessNames.LOGIN.getType(), roleList);
								} else {
									SessionContext.getInstance().getMapObject().put(RegistrationConstants.ONBOARD_USER,
											true);
									SessionContext.getInstance().getMapObject()
											.put(RegistrationConstants.ONBOARD_USER_UPDATE, false);
									Set<String> roleSet = new HashSet<>();
									roleSet.add("*");
									loginList = loginService.getModesOfLogin(ProcessNames.ONBOARD.getType(), roleSet);
								}
								
								if(loginList.size() > 1 && applicationContext.getApplicationMap().get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)
										.equals(RegistrationConstants.ENABLE)) {
										loginList.removeIf(login -> login.equalsIgnoreCase(RegistrationConstants.BIO));
								}
							}

							String loginMode = !loginList.isEmpty() ? loginList.get(RegistrationConstants.PARAM_ZERO)
									: null;

							LOGGER.debug(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME,
									APPLICATION_ID, "Retrieved corresponding Login mode");

							if (loginMode == null) {
								userIdPane.setVisible(false);
								errorPane.setVisible(true);
							} else {

								if (applicationContext.getApplicationMap()
										.get(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)
										.equals(RegistrationConstants.ENABLE)
										&& loginMode.equalsIgnoreCase(RegistrationConstants.BIO)) {
									
									generateAlert(RegistrationConstants.ERROR,
											RegistrationUIConstants.DISABLE_FINGERPRINT_SCREEN);
									
								} else {
									userIdPane.setVisible(false);
									loadLoginScreen(loginMode);
								}
							}
						}
					}
				} else {
					generateAlert(RegistrationConstants.USER_MACHINE_VALIDATION_CODE,
							RegistrationConstants.USER_MACHINE_VALIDATION_MSG);
				}
			} catch (RegBaseUncheckedException regBaseUncheckedException) {

				LOGGER.error(RegistrationConstants.REGISTRATION_LOGIN_MODE_LOGIN_CONTROLLER, APPLICATION_NAME,
						APPLICATION_ID, regBaseUncheckedException.getMessage());

				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
			}
		}
	}

	/**
	 * 
	 * Validating User credentials on Submit
	 * 
	 * @return String loginMode
	 * @throws RegBaseCheckedException
	 */
	public void validateCredentials(ActionEvent event) {

		LOGGER.info("REGISTRATION - LOGIN_MODE_PWORD - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials entered through UI");

		UserDetail userDetail = loginService.getUserDetail(userId.getText());

		LoginUserDTO userDTO = new LoginUserDTO();
		userDTO.setUserId(userId.getText().toLowerCase());
		userDTO.setPassword(password.getText());

		//TODO for temporary fix , but later userDto should be getting from session
		ApplicationContext.getInstance().getApplicationMap().put("userDTO", userDTO);
		
		boolean serverStatus = getConnectionCheck(userDTO);
		boolean offlineStatus = false;

		if (!serverStatus) {

			String status = validatePwd(userId.getText().toLowerCase(), password.getText());

			if (RegistrationConstants.SUCCESS.equals(status)) {
				offlineStatus = validateInvalidLogin(userDetail, "");
			} else if (RegistrationConstants.FAILURE.equals(status)) {
				offlineStatus = validateInvalidLogin(userDetail, RegistrationUIConstants.INCORRECT_PWORD);
			}
		}

		if (serverStatus || offlineStatus) {

			LOGGER.info(RegistrationConstants.REGISTRATION_LOGIN_PWORD_LOGIN_CONTROLLER, APPLICATION_NAME,
					APPLICATION_ID, "Loading next login screen");
			credentialsPane.setVisible(false);
			loadNextScreen(userDetail, RegistrationConstants.PWORD);

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

		if (userId.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
		} else {
			// Response obtained from server
			ResponseDTO responseDTO = otpGenerator.getOTP(userId.getText());

			if (responseDTO.getSuccessResponseDTO() != null) {
				// Enable submit button
				changeToOTPSubmitMode();

				// Generate alert to show OTP
				SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
				generateAlert(RegistrationConstants.ERROR, successResponseDTO.getMessage());

			} else if (responseDTO.getErrorResponseDTOs() != null) {
				// Generate Alert to show INVALID USERNAME
				ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
				generateAlert(RegistrationConstants.ERROR, errorResponseDTO.getMessage());

			}
		}
	}

	/**
	 * Validate User through username and otp
	 * 
	 * @param event
	 */
	@FXML
	public void validateOTP(ActionEvent event) {

		if (!otp.getText().isEmpty()) {

			UserDetail userDetail = loginService.getUserDetail(userId.getText());

			boolean otpLoginStatus = false;

			ResponseDTO responseDTO = otpGenerator.validateOTP(userId.getText(), otp.getText());
			if (responseDTO.getSuccessResponseDTO() != null) {
				otpLoginStatus = validateInvalidLogin(userDetail, "");
			} else {
				// ErrorResponseDTO errorResponseDTO =
				// responseDTO.getErrorResponseDTOs().get(0);
				// generateAlert(errorResponseDTO.getMessage());
				otpLoginStatus = validateInvalidLogin(userDetail, RegistrationUIConstants.OTP_VALIDATION_ERROR_MESSAGE);
			}

			if (otpLoginStatus) {
				otpPane.setVisible(false);
				loadNextScreen(userDetail, RegistrationConstants.OTP);
			}

		} else {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.OTP_FIELD_EMPTY);
		}
	}

	/**
	 * Validate User through username and fingerprint
	 * 
	 * @param event
	 */
	public void validateFingerPrint(ActionEvent event) {

		LOGGER.info("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Credentials for Biometric login");

		UserDetail detail = loginService.getUserDetail(userId.getText());

		boolean bioLoginStatus = false;

		if (validateBiometricFP()) {
			bioLoginStatus = validateInvalidLogin(detail, "");
		} else {
			bioLoginStatus = validateInvalidLogin(detail, RegistrationUIConstants.FINGER_PRINT_MATCH);
		}

		LOGGER.info("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Fingerprint with minutia");

		if (bioLoginStatus) {
			fingerprintPane.setVisible(false);
			loadNextScreen(detail, RegistrationConstants.BIO);
		}

		LOGGER.info("REGISTRATION - SCAN_FINGER - FINGER_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
				"Fingerprint validation done");
	}

	/**
	 * Validate User through username and Iris
	 * 
	 * @param event
	 */
	public void validateIris(ActionEvent event) {
		LOGGER.info("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Biometric login with Iris");

		UserDetail detail = loginService.getUserDetail(userId.getText());

		boolean irisLoginStatus = false;

		if (validateBiometricIris()) {
			irisLoginStatus = validateInvalidLogin(detail, "");
		} else {
			irisLoginStatus = validateInvalidLogin(detail, RegistrationUIConstants.IRIS_MATCH);
		}

		LOGGER.info("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Iris with stored data");

		if (irisLoginStatus) {
			irisPane.setVisible(false);
			loadNextScreen(detail, RegistrationConstants.IRIS);
		}

		LOGGER.info("REGISTRATION - SCAN_IRIS - IRIS_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
				"Iris validation done");
	}

	/**
	 * Validate User through username and face
	 * 
	 * @param event
	 */
	public void validateFace(ActionEvent event) {
		LOGGER.info("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Biometric login with Iris");

		UserDetail detail = loginService.getUserDetail(userId.getText());

		boolean faceLoginStatus = false;

		if (validateBiometricFace()) {
			faceLoginStatus = validateInvalidLogin(detail, "");
		} else {
			faceLoginStatus = validateInvalidLogin(detail, RegistrationUIConstants.FACE_MATCH);
		}

		LOGGER.info("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating Face with stored data");

		if (faceLoginStatus) {
			facePane.setVisible(false);
			loadNextScreen(detail, RegistrationConstants.FACE);
		}

		LOGGER.info("REGISTRATION - SCAN_IRIS - IRIS_VALIDATION", APPLICATION_NAME, APPLICATION_ID,
				"Face validation done");
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
	 * Load login screen depending on Loginmode
	 * 
	 * @param loginMode
	 *            login screen to be loaded
	 */
	public void loadLoginScreen(String loginMode) {
		
		switch (loginMode) {
			case RegistrationConstants.OTP:
				otpPane.setVisible(true);
				break;
			case RegistrationConstants.PWORD:
				credentialsPane.setVisible(true);
				break;
			case RegistrationConstants.BIO:
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
	 * Validating User role and Machine mapping during login
	 * 
	 * @param userId
	 *            entered userId
	 * @throws RegBaseCheckedException
	 */
	private boolean setInitialLoginInfo(String userId) {
		UserDetail userDetail = loginService.getUserDetail(userId);
		String authInfo = RegistrationConstants.SUCCESS;
		List<String> roleList = new ArrayList<>();

		userDetail.getUserRole().forEach(roleCode -> {
			if (roleCode.getIsActive()) {
				roleList.add(String.valueOf(roleCode.getUserRoleID().getRoleCode()));
			}
		});

		LOGGER.info("REGISTRATION - ROLES_MACHINE_MAPPING - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating roles and machine and center mapping");
		// Checking roles
		if (!(roleList.contains(RegistrationConstants.SUPERVISOR)
				|| roleList.contains(RegistrationConstants.OFFICER))) {
			authInfo = RegistrationConstants.ROLES_EMPTY;
		} else if (roleList.contains(RegistrationConstants.ADMIN_ROLE)) {
			authInfo = RegistrationConstants.SUCCESS;
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
	private boolean getCenterMachineStatus(UserDetail userDetail) {
		List<String> machineList = new ArrayList<>();
		List<String> centerList = new ArrayList<>();

		LOGGER.info("REGISTRATION - MACHINE_MAPPING - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating User machine and center mapping");

		userDetail.getUserMachineMapping().forEach(machineMapping -> {
			if (machineMapping.getIsActive()) {
				machineList.add(machineMapping.getMachineMaster().getMacAddress());
				centerList.add(machineMapping.getUserMachineMappingId().getCentreID());
			}
		});
		return machineList.contains(RegistrationSystemPropertiesChecker.getMachineId())
				&& centerList.contains(userDetail.getRegCenterUser().getRegCenterUserId().getRegcntrId());
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
	private boolean setSessionContext(String authInfo, UserDetail userDetail, List<String> roleList) {
		boolean result = false;

		LOGGER.info("REGISTRATION - SESSION_CONTEXT - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Validating roles and machine and center mapping");

		if (authInfo.equals(RegistrationConstants.ROLES_EMPTY)) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.ROLES_EMPTY_ERROR);
		} else if (authInfo.equalsIgnoreCase(RegistrationConstants.SUCCESS)) {
			SessionContext sessionContext = SessionContext.getInstance();

			LOGGER.info("REGISTRATION - SESSION_CONTEXT - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					"Setting values for session context and user context");

			sessionContext.setLoginTime(new Date());
			sessionContext.setRefreshedLoginTime(refreshedLoginTime);
			sessionContext.setIdealTime(idealTime);
			sessionContext.setTimeoutInterval(timeoutInterval);

			SessionContext.UserContext userContext = sessionContext.getUserContext();
			userContext.setUserId(userId.getText());
			userContext.setName(userDetail.getName());
			userContext.setRoles(roleList);
			userContext.setRegistrationCenterDetailDTO(loginService
					.getRegistrationCenterDetails(userDetail.getRegCenterUser().getRegCenterUserId().getRegcntrId()));
			userContext.setAuthorizationDTO(loginService.getScreenAuthorizationDetails(roleList));
			userContext.setUserMap(new HashMap<String, Object>());
			result = true;
		}
		return result;
	}

	/**
	 * Loading next login screen in case of multifactor authentication
	 * 
	 * @param userDetail
	 *            the userDetail
	 * @param loginMode
	 *            the loginMode
	 */
	private void loadNextScreen(UserDetail userDetail, String loginMode) {

		if (!loginList.isEmpty()) {

			LOGGER.info("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					"Loading next login screen in case of multifactor authentication");

			loadLoginScreen(loginList.get(RegistrationConstants.PARAM_ZERO));
			
		} else {
			if (setInitialLoginInfo(userId.getText())) {

				try {

					LOGGER.info("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
							"Loading Home screen");
					schedulerUtil.startSchedulerUtil();

					BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));

					userDetail.setLastLoginMethod(loginMode);
					userDetail.setLastLoginDtimes(Timestamp.valueOf(LocalDateTime.now()));
					userDetail.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ZERO);

					loginService.updateLoginParams(userDetail);
				} catch (IOException | RuntimeException | RegBaseCheckedException exception) {

					LOGGER.error(RegistrationConstants.REGISTRATION_LOGIN_PWORD_LOGIN_CONTROLLER, APPLICATION_NAME,
							APPLICATION_ID, exception.getMessage());

					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGIN_SCREEN);
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

		LOGGER.info("REGISTRATION - LOGIN_BIO - LOGIN_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Initializing FingerPrint device");

		MosipFingerprintProvider fingerPrintConnector = fingerprintFacade.getFingerprintProviderFactory(deviceName);

		if (fingerPrintConnector.captureFingerprint(qualityScore, captureTimeOut, "") != 0) {

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.DEVICE_FP_NOT_FOUND);

			return false;
		} else {
			// Thread to wait until capture the bio image/ minutia from FP. based on the
			// error code or success code the respective action will be taken care.
			waitToCaptureBioImage(5, 2000, fingerprintFacade);

			LOGGER.info("REGISTRATION - SCAN_FINGER - SCAN_FINGER_COMPLETED", APPLICATION_NAME, APPLICATION_ID,
					"Fingerprint scan done");

			fingerPrintConnector.uninitFingerPrintDevice();

			LOGGER.info("REGISTRATION - LOGIN - BIOMETRICS", APPLICATION_NAME, APPLICATION_ID,
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
				fingerPrintStatus = authService.authValidator(RegistrationConstants.FINGERPRINT,
						authenticationValidatorDTO);

			} else if (!RegistrationConstants.EMPTY.equals(fingerprintFacade.getErrorMessage())) {
				if (fingerprintFacade.getErrorMessage().equals("Timeout")) {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FP_DEVICE_TIMEOUT);
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FP_DEVICE_ERROR);
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

		LOGGER.info("REGISTRATION - SCAN_IRIS", APPLICATION_NAME, APPLICATION_ID, "Scanning Iris");

		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		List<IrisDetailsDTO> irisDetailsDTOs = new ArrayList<>();
		IrisDetailsDTO irisDetailsDTO = new IrisDetailsDTO();
		irisDetailsDTO.setIris(irisFacade.captureIris());
		irisDetailsDTOs.add(irisDetailsDTO);
		authenticationValidatorDTO.setUserId(userId.getText());
		authenticationValidatorDTO.setIrisDetails(irisDetailsDTOs);

		LOGGER.info("REGISTRATION - SCAN_IRIS", APPLICATION_NAME, APPLICATION_ID, "Iris scan done");

		return authService.authValidator(RegistrationConstants.IRIS, authenticationValidatorDTO);
	}

	/**
	 * Validating User Biometrics using Face
	 * 
	 * @return boolean
	 */
	private boolean validateBiometricFace() {

		LOGGER.info("REGISTRATION - SCAN_FACE", APPLICATION_NAME, APPLICATION_ID, "Scanning Face");
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		FaceDetailsDTO faceDetailsDTO = new FaceDetailsDTO();
		faceDetailsDTO.setFace(faceFacade.captureFace());
		authenticationValidatorDTO.setUserId(userId.getText());
		authenticationValidatorDTO.setFaceDetail(faceDetailsDTO);

		LOGGER.info("REGISTRATION - SCAN_FACE", APPLICATION_NAME, APPLICATION_ID, "Face scan done");

		return authService.authValidator(RegistrationConstants.FACE, authenticationValidatorDTO);
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
	private boolean validateInvalidLogin(UserDetail userDetail, String errorMessage) {

		LOGGER.info("REGISTRATION - LOGIN - LOCK_USER", APPLICATION_NAME, APPLICATION_ID,
				"Fetching invalid login params");

		int loginCount = userDetail.getUnsuccessfulLoginCount() != null
				? userDetail.getUnsuccessfulLoginCount().intValue()
				: 0;

		Timestamp loginTime = userDetail.getUserlockTillDtimes();

		int invalidLoginCount = Integer.parseInt(
				String.valueOf(applicationContext.getApplicationMap().get(RegistrationConstants.INVALID_LOGIN_COUNT)));

		int invalidLoginTime = Integer.parseInt(
				String.valueOf(applicationContext.getApplicationMap().get(RegistrationConstants.INVALID_LOGIN_TIME)));

		LOGGER.info("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
				"validating invalid login params");

		if (validateLoginTime(loginCount, invalidLoginCount, loginTime, invalidLoginTime)) {

			loginCount = RegistrationConstants.PARAM_ZERO;
			userDetail.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ZERO);

			loginService.updateLoginParams(userDetail);

		}

		String unlockMessage = String.format("%s %s %s %s %s", RegistrationUIConstants.USER_ACCOUNT_LOCK_MESSAGE_NUMBER,
				String.valueOf(invalidLoginCount), RegistrationUIConstants.USER_ACCOUNT_LOCK_MESSAGE,
				String.valueOf(invalidLoginTime), RegistrationUIConstants.USER_ACCOUNT_LOCK_MESSAGE_MINUTES);

		if (loginCount >= invalidLoginCount) {

			LOGGER.info("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
					"validating login count and time ");

			if (TimeUnit.MILLISECONDS.toMinutes(loginTime.getTime() - System.currentTimeMillis()) > invalidLoginTime) {

				userDetail.setUnsuccessfulLoginCount(RegistrationConstants.PARAM_ONE);

				loginService.updateLoginParams(userDetail);

			} else {

				generateAlert(RegistrationConstants.ERROR, unlockMessage);

			}
			return false;

		} else {
			if (!errorMessage.isEmpty()) {

				LOGGER.info("REGISTRATION - LOGIN - LOCKUSER", APPLICATION_NAME, APPLICATION_ID,
						"updating login count and time for invalid login attempts");
				loginCount = loginCount + RegistrationConstants.PARAM_ONE;
				userDetail.setUserlockTillDtimes(Timestamp.valueOf(LocalDateTime.now()));
				userDetail.setUnsuccessfulLoginCount(loginCount);

				loginService.updateLoginParams(userDetail);

				if (loginCount >= invalidLoginCount) {

					generateAlert(RegistrationConstants.ERROR, unlockMessage);

				} else {

					generateAlert(RegistrationConstants.ERROR, errorMessage);

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

		LOGGER.info("REGISTRATION - LOGIN - COMAPRE_TIME_STAMPS", APPLICATION_NAME, APPLICATION_ID,
				"Comparing timestamps in case of invalid login attempts");

		return (loginCount >= invalidLoginCount && TimeUnit.MILLISECONDS
				.toMinutes(System.currentTimeMillis() - loginTime.getTime()) > invalidLoginTime);
	}

}
