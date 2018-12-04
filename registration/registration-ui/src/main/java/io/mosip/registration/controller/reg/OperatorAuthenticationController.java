package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.URL;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.util.biometric.FingerprintFacade;
import io.mosip.registration.util.biometric.MosipFingerprintProvider;
import io.mosip.registration.validator.AuthenticationValidatorFactory;
import io.mosip.registration.validator.AuthenticationValidatorImplementation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Class for Operator Authentication
 *
 */
@Controller
public class OperatorAuthenticationController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(OperatorAuthenticationController.class);

	@FXML
	private AnchorPane temporaryLogin;
	@FXML
	private AnchorPane pwdBasedLogin;
	@FXML
	private AnchorPane otpBasedLogin;
	@FXML
	private AnchorPane fingerprintBasedLogin;
	@FXML
	private Label otpValidity;
	@FXML
	private Label otpLabel;
	@FXML
	private Label fingerPrintLabel;
	@FXML
	private TextField fpUserId;
	@FXML
	private TextField username;
	@FXML
	private TextField password;
	@FXML
	private Label passwdLabel;
	@FXML
	private TextField otpUserId;
	@FXML
	private TextField otp;

	private FingerprintFacade fingerprintFacade = null;

	@Value("${QUALITY_SCORE}")
	private int qualityScore;

	@Value("${CAPTURE_TIME_OUT}")
	private int captureTimeOut;

	@Value("${PROVIDER_NAME}")
	private String providerName;

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;

	@Value("${otp_validity_in_mins}")
	private long otpValidityInMins;

	@Autowired
	private RegistrationOfficerPacketController registrationOfficerPacketController;

	@Autowired
	private AuthenticationValidatorFactory validator;

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private AuthenticationValidatorImplementation authenticationValidatorImplementation;

	@Value("${USERNAME_PWD_LENGTH}")
	private int usernamePwdLength;

	private int count = 1;

	private boolean isSupervisor = false;

	private Map<String, Object> userAuthenticationTypeMap = new LinkedHashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION_OPERATOR_AUTHENTICATION_CONTROLLER", APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the Operator Authentication Page");

		otpValidity.setText("Valid for " + otpValidityInMins + " minutes");
		fingerprintFacade = new FingerprintFacade();
		getAuthenticationModes();
	}

	/**
	 * to validate the password in case of password based authentication
	 */
	public void validatePwd() {
		if (username.getText().isEmpty() && password.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.CREDENTIALS_FIELD_EMPTY);
		} else if (username.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.USERNAME_FIELD_EMPTY);
		} else if (password.getText().isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.PWORD_FIELD_EMPTY);
		} else if (username.getText().length() > usernamePwdLength) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.USRNAME_PWORD_LENGTH);
		} else if (password.getText().length() > usernamePwdLength) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.USRNAME_PWORD_LENGTH);
		} else {
			String hashPassword = null;

			// password hashing
			if (!(password.getText().isEmpty())) {
				byte[] bytePassword = password.getText().getBytes();
				hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));
			}
			LoginUserDTO userDTO = new LoginUserDTO();
			userDTO.setUserId(username.getText());
			userDTO.setPassword(hashPassword);
			// Server connection check
			boolean serverStatus = getConnectionCheck(userDTO);
			AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
			authenticationValidatorDTO.setUserId(username.getText());
			authenticationValidatorDTO.setPassword(hashPassword);
			String userStatus = authenticationValidatorImplementation.validatePassword(authenticationValidatorDTO);			

			if (userStatus.equals(RegistrationConstants.USER_NOT_ONBOARDED)) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.USER_NOT_ONBOARDED);
			} else {
				if (!serverStatus) {
					if (userStatus.equals(RegistrationConstants.PWD_MATCH)) {
						loadNextScreen();
					} else {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.INCORRECT_PWORD);
					}
				}
				/*
				 * if (serverStatus || offlineStatus) { if
				 * (validateUserStatus(username.getText())) {
				 * 
				 * LOGGER.debug("REGISTRATION - LOGIN_PWORD - LOGIN_CONTROLLER",
				 * APPLICATION_NAME, APPLICATION_ID, "Validating user status");
				 * 
				 * generateAlert(RegistrationConstants.ALERT_ERROR,
				 * RegistrationConstants.BLOCKED_USER_ERROR); } else { try {
				 * 
				 * LOGGER.debug("REGISTRATION - LOGIN_PWORD - LOGIN_CONTROLLER",
				 * APPLICATION_NAME, APPLICATION_ID, "Loading next login screen");
				 * 
				 * SessionContext sessionContext = SessionContext.getInstance();
				 * loadNextScreen(userDetail, sessionContext,
				 * RegistrationConstants.LOGIN_METHOD_PWORD);
				 * 
				 * } catch (IOException | RuntimeException | RegBaseCheckedException exception)
				 * {
				 * 
				 * LOGGER.error("REGISTRATION - LOGIN_PWORD - LOGIN_CONTROLLER",
				 * APPLICATION_NAME, APPLICATION_ID, exception.getMessage());
				 * 
				 * generateAlert(RegistrationConstants.ALERT_ERROR,
				 * RegistrationConstants.UNABLE_LOAD_LOGIN_SCREEN); } } }
				 */
			}
		}

	}

	/**
	 * Checking server status
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
	 * to validate the OTP in case of OTP based authentication
	 */
	public void validateOTP() {
		otpBasedLogin.setVisible(false);
		fingerprintBasedLogin.setVisible(true);
	}

	/**
	 * to validate the fingerprint in case of fingerprint based authentication
	 */
	public void validateFingerprint() {
		if (isSupervisor) {
			if (fpUserId.getText() != null) {
				if (fetchUserRole(fpUserId.getText())) {
					if (captureAndValidateFP(fpUserId.getText())) {
						loadNextScreen();
					} else {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.FINGER_PRINT_MATCH);
					}
				} else {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.USER_NOT_ONBOARDED);
				}
			} else {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.USERNAME_FIELD_EMPTY);
			}
		} else {
			if (captureAndValidateFP(fpUserId.getText())) {
				loadNextScreen();
			} else {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.FINGER_PRINT_MATCH);
			}
		}
	}

	
	/**
	 * to get the configured modes of authentication
	 */
	private void getAuthenticationModes() {
		count = 1;
		userAuthenticationTypeMap = loginService.getModesOfLogin();
		userAuthenticationTypeMap.remove(RegistrationConstants.LOGIN_SEQUENCE);
		loadNextScreen();
	}

	/**
	 * to load the respective screen with respect to the list of configured authentication modes
	 */
	private void loadNextScreen() {
		if (!userAuthenticationTypeMap.isEmpty()) {
			String authenticationType = String.valueOf(userAuthenticationTypeMap.get(String.valueOf(count)));
			userAuthenticationTypeMap.remove(String.valueOf(count));
			count++;
			loadAuthenticationScreen(authenticationType);
		} else {
			if (!isSupervisor) {
				if (RegistrationController.toggleBiometricException) {
					isSupervisor = true;
					getAuthenticationModes();
				} else {
					submitRegistration();
				}
			} else {
				submitRegistration();
			}
		}
	}

	/**
	 * to enable the respective authentication mode
	 * @param loginMode	- name of authentication mode
	 */
	public void loadAuthenticationScreen(String loginMode) {
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
	 * to enable the OTP based authentication mode and disable rest of modes
	 */
	private void enableOTP() {
		pwdBasedLogin.setVisible(false);
		otpBasedLogin.setVisible(true);
		fingerprintBasedLogin.setVisible(false);
		otp.clear();
		otpUserId.clear();
		if (isSupervisor) {
			otpLabel.setText(RegistrationConstants.SUPERVISOR_VERIFICATION);
			otpLabel.setLayoutX(336);
			otpLabel.setLayoutY(72);
			otpUserId.setEditable(true);
		} else {
			otpUserId.setText(SessionContext.getInstance().getUserContext().getUserId());
			otpUserId.setEditable(false);
		}
	}

	/**
	 * to enable the password based authentication mode and disable rest of modes
	 */
	private void enablePWD() {
		pwdBasedLogin.setVisible(true);
		otpBasedLogin.setVisible(false);
		fingerprintBasedLogin.setVisible(false);
		username.clear();
		password.clear();
		if (isSupervisor) {
			passwdLabel.setText(RegistrationConstants.SUPERVISOR_VERIFICATION);
			passwdLabel.setLayoutX(336);
			passwdLabel.setLayoutY(79);
			username.setEditable(true);
		} else {
			username.setText(SessionContext.getInstance().getUserContext().getUserId());
			username.setEditable(false);
		}
	}

	/**
	 * to enable the fingerprint based authentication mode and disable rest of modes
	 */
	private void enableFingerPrint() {
		fingerprintBasedLogin.setVisible(true);
		otpBasedLogin.setVisible(false);
		pwdBasedLogin.setVisible(false);
		fpUserId.clear();
		if (isSupervisor) {
			fpUserId.setEditable(true);
			fingerPrintLabel.setText(RegistrationConstants.SUPERVISOR_FINGERPRINT_LOGIN);
		} else {
			fpUserId.setText(SessionContext.getInstance().getUserContext().getUserId());
			fpUserId.setEditable(false);
		}
	}

	
	/**
	 * to check the role of supervisor in case of biometric exception
	 * @param userId - username entered by the supervisor in the authentication screen
	 * @return boolean variable "true", if the person is authenticated as supervisor or "false", if not
	 */
	private boolean fetchUserRole(String userId) {
		RegistrationUserDetail registrationUserDetail = loginService.getUserDetail(userId);
		if (registrationUserDetail != null) {
			return registrationUserDetail.getUserRole().stream().anyMatch(userRole -> userRole
					.getRegistrationUserRoleID().getRoleCode().equalsIgnoreCase(RegistrationConstants.SUPERVISOR_NAME));
		}
		return false;
	}

	
	/**
	 * to capture and validate the fingerprint for authentication
	 * @param userId - username entered in the textfield
	 * @return true/false after validating fingerprint
	 */
	private boolean captureAndValidateFP(String userId) {
		boolean fpMatchStatus = false;
		MosipFingerprintProvider fingerPrintConnector = fingerprintFacade.getFingerprintProviderFactory(providerName);
		int statusCode = fingerPrintConnector.captureFingerprint(qualityScore, captureTimeOut, "");
		if (statusCode != 0) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DEVICE_FP_NOT_FOUND);
		} else {
			// Thread to wait until capture the bio image/ minutia from FP. based on the
			// error code or success code the respective action will be taken care.
			waitToCaptureBioImage(5, 2000, fingerprintFacade);
			LOGGER.debug("REGISTRATION - SCAN_FINGER - SCAN_FINGER_COMPLETED", APPLICATION_NAME, APPLICATION_ID,
					"Fingerprint scan done");

			fingerPrintConnector.uninitFingerPrintDevice();
			if (RegistrationConstants.EMPTY.equals(fingerprintFacade.getMinutia())) {
				// if FP data fetched then retrieve the user specific detail from db.
				AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
				List<FingerprintDetailsDTO> fingerprintDetailsDTOs = new ArrayList<>();
				FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
				fingerprintDetailsDTO.setFingerPrint(fingerprintFacade.getIsoTemplate());
				fingerprintDetailsDTOs.add(fingerprintDetailsDTO);
				authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
				authenticationValidatorDTO.setUserId(userId);
				AuthenticationValidatorImplementation authenticationValidatorImplementation = validator
						.getValidator("Fingerprint");
				authenticationValidatorImplementation.setFingerPrintType("single");
				fpMatchStatus = authenticationValidatorImplementation.validate(authenticationValidatorDTO);
			}
		}
		return fpMatchStatus;
	}

	/**
	 * to submit the registration after successful authentication
	 */
	public void submitRegistration() {
		registrationOfficerPacketController.showReciept((RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA), capturePhotoUsingDevice);
	}
}
