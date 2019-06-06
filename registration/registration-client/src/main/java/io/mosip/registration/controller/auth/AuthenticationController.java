package io.mosip.registration.controller.auth;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.PacketHandlerController;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.controller.reg.Validations;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.bio.BioService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.security.AuthenticationService;
import io.mosip.registration.util.common.OTPManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Class for Operator Authentication
 *
 * 
 * 
 * 
 */
@Controller
public class AuthenticationController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AuthenticationController.class);

	@FXML
	private AnchorPane temporaryLogin;
	@FXML
	private GridPane pwdBasedLogin;
	@FXML
	private GridPane otpBasedLogin;
	@FXML
	private GridPane fingerprintBasedLogin;
	@FXML
	private GridPane irisBasedLogin;
	@FXML
	private GridPane faceBasedLogin;
	@FXML
	private GridPane errorPane;
	@FXML
	private Label errorLabel;
	@FXML
	private Label errorText1;
	@FXML
	private Label errorText2;
	@FXML
	private Label otpValidity;
	@FXML
	private TextField fpUserId;
	@FXML
	private TextField username;
	@FXML
	private TextField password;
	@FXML
	private TextField otpUserId;
	@FXML
	private TextField otp;
	@FXML
	private GridPane operatorAuthenticationPane;
	@FXML
	private Button operatorAuthContinue;
	@FXML
	private Label registrationNavlabel;
	@FXML
	private Label otpLabel;
	@FXML
	private Label fpLabel;
	@FXML
	private Label irisLabel;
	@FXML
	private Label photoLabel;
	@FXML
	private Label pwdLabel;
	@FXML
	private Button getOTP;
	
	@Autowired
	private PacketHandlerController packetHandlerController;

	@Autowired
	private RegistrationController registrationController;
	
	@Autowired
	private OTPManager otpManager;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private Validations validations;

	private boolean isSupervisor = false;

	private boolean isEODAuthentication = false;

	private List<String> userAuthenticationTypeList;

	private List<String> userAuthenticationTypeListValidation;

	private List<String> userAuthenticationTypeListSupervisorValidation;

	private int authCount = 0;

	private String userNameField;

	@Autowired
	private BaseController baseController;

	@Autowired
	private BioService bioService;

	/**
	 * to generate OTP in case of OTP based authentication
	 */
	public void generateOtp() {

		auditFactory.audit(isSupervisor ? AuditEvent.REG_SUPERVISOR_AUTH_GET_OTP : AuditEvent.REG_OPERATOR_AUTH_GET_OTP,
				Components.REG_OS_AUTH, otpUserId.getText(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Generate OTP for OTP based Authentication");

		if (!otpUserId.getText().isEmpty()) {
			// Response obtained from server
			ResponseDTO responseDTO = null;

			// Service Layer interaction
			responseDTO = otpManager.getOTP(otpUserId.getText());
			if (responseDTO.getSuccessResponseDTO() != null) {
				// Enable submit button
				// Generate alert to show OTP
				getOTP.setVisible(false);
				generateAlert(RegistrationConstants.ALERT_INFORMATION,
						RegistrationUIConstants.OTP_GENERATION_SUCCESS_MESSAGE);
			} else if (responseDTO.getErrorResponseDTOs() != null) {
				// Generate Alert to show INVALID USERNAME
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.OTP_GENERATION_ERROR_MESSAGE);
			}

		} else {
			// Generate Alert to show username field was empty
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
		}
	}

	/**
	 * to validate OTP in case of OTP based authentication
	 */
	public void validateOTP() {

		auditFactory.audit(
				isSupervisor ? AuditEvent.REG_SUPERVISOR_AUTH_SUBMIT_OTP : AuditEvent.REG_OPERATOR_AUTH_SUBMIT_OTP,
				Components.REG_OS_AUTH, otpUserId.getText(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating OTP for OTP based Authentication");
		if (validations.validateTextField(operatorAuthenticationPane, otp, otp.getId(), true)) {
			if (isSupervisor) {
				if (!otpUserId.getText().isEmpty()) {
					if (fetchUserRole(otpUserId.getText())) {
						if (null != authenticationService.authValidator(RegistrationConstants.OTP, otpUserId.getText(), otp.getText())) {
							userAuthenticationTypeListValidation.remove(0);
							userNameField = otpUserId.getText();
							if (!isEODAuthentication) {
								getOSIData().setSupervisorID(userNameField);
								getOSIData().setSuperviorAuthenticatedByPIN(true);
							}
							loadNextScreen();
						} else {
							generateAlert(RegistrationConstants.ERROR,
									RegistrationUIConstants.OTP_VALIDATION_ERROR_MESSAGE);
						}
					} else {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_AUTHORIZED);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
				}
			} else {
				if (null != authenticationService.authValidator(RegistrationConstants.OTP, otpUserId.getText(), otp.getText())) {
					if (!isEODAuthentication) {
						getOSIData().setOperatorAuthenticatedByPIN(true);
					}
					userAuthenticationTypeListValidation.remove(0);
					loadNextScreen();
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.OTP_VALIDATION_ERROR_MESSAGE);
				}
			}
		}
	}

	public void validatePwd() {

		auditFactory.audit(
				isSupervisor ? AuditEvent.REG_SUPERVISOR_AUTH_PASSWORD : AuditEvent.REG_OPERATOR_AUTH_PASSWORD,
				Components.REG_OS_AUTH, username.getText(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		String status = RegistrationConstants.EMPTY;
		if (isSupervisor) {
			if (!username.getText().isEmpty()) {
				if (fetchUserRole(username.getText())) {
					if (password.getText().isEmpty()) {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PWORD_FIELD_EMPTY);
					} else {
						status = validatePwd(username.getText(), password.getText());
						if (RegistrationConstants.SUCCESS.equals(status)) {
							userAuthenticationTypeListValidation.remove(0);
							userNameField = username.getText();
							if (!isEODAuthentication) {
								getOSIData().setSupervisorID(userNameField);
								getOSIData().setSuperviorAuthenticatedByPassword(true);
							}
							loadNextScreen();
						} else if (RegistrationConstants.FAILURE.equals(status)) {
							generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHENTICATION_FAILURE);
						}
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_AUTHORIZED);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		} else {
			if (!username.getText().isEmpty()) {
				if (password.getText().isEmpty()) {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PWORD_FIELD_EMPTY);
				} else {
					status = validatePwd(username.getText(), password.getText());
					if (RegistrationConstants.SUCCESS.equals(status)) {
						userAuthenticationTypeListValidation.remove(0);
						userNameField = username.getText();
						if (!isEODAuthentication) {
							getOSIData().setOperatorAuthenticatedByPassword(true);
						}
						loadNextScreen();
					} else if (RegistrationConstants.FAILURE.equals(status)) {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHENTICATION_FAILURE);
					}
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		}

	}

	/**
	 * to validate the fingerprint in case of fingerprint based authentication
	 */
	public void validateFingerprint() {

		auditFactory.audit(
				isSupervisor ? AuditEvent.REG_SUPERVISOR_AUTH_FINGERPRINT : AuditEvent.REG_OPERATOR_AUTH_FINGERPRINT,
				Components.REG_OS_AUTH, fpUserId.getText(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating Fingerprint for Fingerprint based Authentication");

		if (isSupervisor) {
			if (!fpUserId.getText().isEmpty()) {
				if (fetchUserRole(fpUserId.getText())) {
					try {
						if (captureAndValidateFP(fpUserId.getText())) {
							userAuthenticationTypeListValidation.remove(0);
							userNameField = fpUserId.getText();
							if (!isEODAuthentication) {
								getOSIData().setSupervisorID(userNameField);
							}
							loadNextScreen();
						} else {
							generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FINGER_PRINT_MATCH);
						}
					} catch (RegBaseCheckedException | IOException e) {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.NO_DEVICE_FOUND);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_AUTHORIZED);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		} else {
			try {
				if (captureAndValidateFP(fpUserId.getText())) {
					userAuthenticationTypeListValidation.remove(0);
					loadNextScreen();
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FINGER_PRINT_MATCH);
				}
			} catch (RegBaseCheckedException | IOException exception) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.NO_DEVICE_FOUND);
			}
		}
	}

	/**
	 * to validate the iris in case of iris based authentication
	 */
	public void validateIris() {

		auditFactory.audit(isSupervisor ? AuditEvent.REG_SUPERVISOR_AUTH_IRIS : AuditEvent.REG_OPERATOR_AUTH_IRIS,
				Components.REG_OS_AUTH, fpUserId.getText(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating Iris for Iris based Authentication");

		if (isSupervisor) {
			if (!fpUserId.getText().isEmpty()) {
				if (fetchUserRole(fpUserId.getText())) {
					try {
						if (captureAndValidateIris(fpUserId.getText())) {
							userAuthenticationTypeListValidation.remove(0);
							userNameField = fpUserId.getText();
							if (!isEODAuthentication) {
								getOSIData().setSupervisorID(userNameField);
							}
							loadNextScreen();
						} else {
							generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.IRIS_MATCH);
						}
					} catch (RegBaseCheckedException | IOException exception) {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.NO_DEVICE_FOUND);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_AUTHORIZED);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		} else {
			try {
				if (captureAndValidateIris(fpUserId.getText())) {
					userAuthenticationTypeListValidation.remove(0);
					loadNextScreen();
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.IRIS_MATCH);
				}
			} catch (RegBaseCheckedException | IOException exception) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.NO_DEVICE_FOUND);
			}
		}
	}

	/**
	 * to validate the face in case of face based authentication
	 */
	public void validateFace() {

		auditFactory.audit(isSupervisor ? AuditEvent.REG_SUPERVISOR_AUTH_FACE : AuditEvent.REG_OPERATOR_AUTH_FACE,
				Components.REG_OS_AUTH, fpUserId.getText(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating Face for Face based Authentication");

		if (isSupervisor) {
			if (!fpUserId.getText().isEmpty()) {
				if (fetchUserRole(fpUserId.getText())) {
					if (captureAndValidateFace(fpUserId.getText())) {
						userAuthenticationTypeListValidation.remove(0);
						userNameField = fpUserId.getText();
						if (!isEODAuthentication) {
							getOSIData().setSupervisorID(userNameField);
						}
						loadNextScreen();
					} else {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FACE_MATCH);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_AUTHORIZED);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		} else {
			if (captureAndValidateFace(fpUserId.getText())) {
				userAuthenticationTypeListValidation.remove(0);
				loadNextScreen();
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FACE_MATCH);
			}
		}
	}

	/**
	 * to get the configured modes of authentication
	 * 
	 * @throws RegBaseCheckedException
	 */
	private void getAuthenticationModes(String authType) throws RegBaseCheckedException {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Loading configured modes of authentication");

		Set<String> roleSet = new HashSet<>(SessionContext.userContext().getRoles());

		userAuthenticationTypeList = loginService.getModesOfLogin(authType, roleSet);
		userAuthenticationTypeListValidation = loginService.getModesOfLogin(authType, roleSet);
		userAuthenticationTypeListSupervisorValidation = loginService.getModesOfLogin(authType, roleSet);

		if (userAuthenticationTypeList.isEmpty()) {
			isSupervisor = false;
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHENTICATION_ERROR_MSG);
			if (isEODAuthentication) {
				throw new RegBaseCheckedException();
			}
		} else {

			LOGGER.info(LoggerConstants.LOG_REG_AUTH, APPLICATION_NAME, APPLICATION_ID,
					"Ignoring FingerPrint, Iris, Face Authentication if the configuration is off");

			String fingerprintDisableFlag = getValueFromApplicationContext(
					RegistrationConstants.FINGERPRINT_DISABLE_FLAG);
			String irisDisableFlag = getValueFromApplicationContext(RegistrationConstants.IRIS_DISABLE_FLAG);
			String faceDisableFlag = getValueFromApplicationContext(RegistrationConstants.FACE_DISABLE_FLAG);

			removeAuthModes(userAuthenticationTypeList, fingerprintDisableFlag,
					RegistrationConstants.FINGERPRINT_UPPERCASE);
			removeAuthModes(userAuthenticationTypeList, irisDisableFlag, RegistrationConstants.IRIS);
			removeAuthModes(userAuthenticationTypeList, faceDisableFlag, RegistrationConstants.FACE);

			LOGGER.info(LoggerConstants.LOG_REG_AUTH, APPLICATION_NAME, APPLICATION_ID,
					"Ignoring FingerPrint, Iris, Face Supervisror Authentication if the configuration is off");

			removeAuthModes(userAuthenticationTypeListValidation, fingerprintDisableFlag,
					RegistrationConstants.FINGERPRINT);
			removeAuthModes(userAuthenticationTypeListValidation, irisDisableFlag, RegistrationConstants.IRIS);
			removeAuthModes(userAuthenticationTypeListValidation, faceDisableFlag, RegistrationConstants.FACE);

			removeAuthModes(userAuthenticationTypeListSupervisorValidation, fingerprintDisableFlag,
					RegistrationConstants.FINGERPRINT);
			removeAuthModes(userAuthenticationTypeListSupervisorValidation, irisDisableFlag,
					RegistrationConstants.IRIS);
			removeAuthModes(userAuthenticationTypeListSupervisorValidation, faceDisableFlag,
					RegistrationConstants.FACE);

			loadNextScreen();
		}
	}

	/**
	 * to load the respective screen with respect to the list of configured
	 * authentication modes
	 */
	private void loadNextScreen() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Loading next authentication screen");
		try {
			Boolean toogleBioException = false;
			if (!SessionContext.userMap().isEmpty()) {
				if (SessionContext.userMap().get(RegistrationConstants.IS_LOW_QUALITY_BIOMETRICS) == null) {
					SessionContext.userMap().put(RegistrationConstants.IS_LOW_QUALITY_BIOMETRICS, false);
				}
				toogleBioException = (Boolean) SessionContext.userContext().getUserMap()
						.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION)
						|| (Boolean) SessionContext.userContext().getUserMap()
								.get(RegistrationConstants.IS_LOW_QUALITY_BIOMETRICS);
			}

			if (!userAuthenticationTypeList.isEmpty()) {
				authCount++;
				String authenticationType = String
						.valueOf(userAuthenticationTypeList.get(RegistrationConstants.PARAM_ZERO));

				if(authenticationType.equalsIgnoreCase(RegistrationConstants.OTP)) {
					getOTP.setVisible(true);
				}
				if ((RegistrationConstants.DISABLE.equalsIgnoreCase(
						getValueFromApplicationContext(RegistrationConstants.FINGERPRINT_DISABLE_FLAG))
						&& authenticationType.equalsIgnoreCase(RegistrationConstants.FINGERPRINT))
						|| (RegistrationConstants.DISABLE.equalsIgnoreCase(
								getValueFromApplicationContext(RegistrationConstants.IRIS_DISABLE_FLAG))
								&& authenticationType.equalsIgnoreCase(RegistrationConstants.IRIS))
						|| (RegistrationConstants.DISABLE.equalsIgnoreCase(
								getValueFromApplicationContext(RegistrationConstants.FACE_DISABLE_FLAG))
								&& authenticationType.equalsIgnoreCase(RegistrationConstants.FACE))) {

					enableErrorPage();
					if (!isEODAuthentication) {
						operatorAuthContinue.setDisable(true);
					}
				} else {
					loadAuthenticationScreen(authenticationType);
					if (!isEODAuthentication) {
						operatorAuthContinue.setDisable(false);
					}
				}
			} else {
				if (!isSupervisor) {

					/*
					 * Check whether the biometric exceptions are enabled and
					 * supervisor authentication is required
					 */
					if ((toogleBioException != null && toogleBioException.booleanValue())
							&& RegistrationConstants.ENABLE.equalsIgnoreCase(
									getValueFromApplicationContext(RegistrationConstants.SUPERVISOR_AUTH_CONFIG))) {
						authCount = 0;
						isSupervisor = true;
						getAuthenticationModes(ProcessNames.EXCEPTION.getType());
					} else {
						submitRegistration();
					}
				} else {
					if (isEODAuthentication) {

						baseController.updateAuthenticationStatus();
					} else {
						submitRegistration();
					}
				}
			}
		} catch (RegBaseCheckedException exception) {
			LOGGER.error("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * to enable the respective authentication mode
	 * 
	 * @param loginMode
	 *            - name of authentication mode
	 */
	public void loadAuthenticationScreen(String loginMode) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Loading the respective authentication screen in UI");
		errorPane.setVisible(false);
		pwdBasedLogin.setVisible(false);
		otpBasedLogin.setVisible(false);
		fingerprintBasedLogin.setVisible(false);
		faceBasedLogin.setVisible(false);
		irisBasedLogin.setVisible(false);

		switch (loginMode.toUpperCase()) {
		case RegistrationConstants.OTP:
			enableOTP();
			break;
		case RegistrationConstants.PWORD:
			enablePWD();
			break;
		case RegistrationConstants.FINGERPRINT_UPPERCASE:
			enableFingerPrint();
			break;
		case RegistrationConstants.IRIS:
			enableIris();
			break;
		case RegistrationConstants.FACE:
			enableFace();
			break;
		default:
			enablePWD();
		}

		userAuthenticationTypeList.remove(RegistrationConstants.PARAM_ZERO);
	}

	/**
	 * to enable the OTP based authentication mode and disable rest of modes
	 */
	private void enableErrorPage() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling OTP based Authentication Screen in UI");

		errorPane.setVisible(true);
		errorText1.setText(RegistrationUIConstants.BIOMETRIC_DISABLE_SCREEN_1);
		errorText2.setText(RegistrationUIConstants.BIOMETRIC_DISABLE_SCREEN_2);
		if (isSupervisor) {
			errorLabel.setText(RegistrationUIConstants.SUPERVISOR_VERIFICATION);
		}
	}

	/**
	 * to enable the OTP based authentication mode and disable rest of modes
	 */
	private void enableOTP() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling OTP based Authentication Screen in UI");

		otpLabel.setText(ApplicationContext.applicationLanguageBundle().getString("otpAuthentication"));
		otpBasedLogin.setVisible(true);
		otp.clear();
		otpUserId.clear();
		otpUserId.setEditable(false);
		if (isSupervisor) {
			otpLabel.setText(ApplicationContext.applicationLanguageBundle().getString("supervisorOtpAuth"));
			if (authCount > 1 && !userNameField.isEmpty()) {
				otpUserId.setText(userNameField);
			} else {
				otpUserId.setEditable(true);
			}
		} else {
			otpUserId.setText(SessionContext.userContext().getUserId());
		}
	}

	/**
	 * to enable the password based authentication mode and disable rest of
	 * modes
	 */
	private void enablePWD() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling Password based Authentication Screen in UI");

		pwdLabel.setText(ApplicationContext.applicationLanguageBundle().getString("pwdAuthentication"));
		pwdBasedLogin.setVisible(true);
		username.clear();
		password.clear();
		username.setEditable(false);
		if (isSupervisor) {
			pwdLabel.setText(ApplicationContext.applicationLanguageBundle().getString("supervisorPwdAuth"));
			if (authCount > 1 && !userNameField.isEmpty()) {
				username.setText(userNameField);
			} else {
				username.setEditable(true);
			}
		} else {
			username.setText(SessionContext.userContext().getUserId());
		}
	}

	/**
	 * to enable the fingerprint based authentication mode and disable rest of
	 * modes
	 */
	private void enableFingerPrint() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling Fingerprint based Authentication Screen in UI");

		fpLabel.setText(ApplicationContext.applicationLanguageBundle().getString("fpAuthentication"));
		fingerprintBasedLogin.setVisible(true);
		fpUserId.clear();
		fpUserId.setEditable(false);
		if (isSupervisor) {
			fpLabel.setText(ApplicationContext.applicationLanguageBundle().getString("supervisorFpAuth"));
			if (authCount > 1 && !userNameField.isEmpty()) {
				fpUserId.setText(userNameField);
			} else {
				fpUserId.setEditable(true);
			}
		} else {
			fpUserId.setText(SessionContext.userContext().getUserId());
		}
	}

	/**
	 * to enable the iris based authentication mode and disable rest of modes
	 */
	private void enableIris() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling Iris based Authentication Screen in UI");

		irisLabel.setText(ApplicationContext.applicationLanguageBundle().getString("irisAuthentication"));
		irisBasedLogin.setVisible(true);
		fpUserId.clear();
		fpUserId.setEditable(false);
		if (isSupervisor) {
			irisLabel.setText(ApplicationContext.applicationLanguageBundle().getString("supervisorIrisAuth"));
			if (authCount > 1 && !userNameField.isEmpty()) {
				fpUserId.setText(userNameField);
			} else {
				fpUserId.setEditable(true);
			}
		} else {
			fpUserId.setText(SessionContext.userContext().getUserId());
		}
	}

	/**
	 * to enable the face based authentication mode and disable rest of modes
	 */
	private void enableFace() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling Face based Authentication Screen in UI");

		photoLabel.setText(ApplicationContext.applicationLanguageBundle().getString("photoAuthentication"));
		faceBasedLogin.setVisible(true);
		fpUserId.clear();
		fpUserId.setEditable(false);
		if (isSupervisor) {
			photoLabel.setText(ApplicationContext.applicationLanguageBundle().getString("supervisorPhotoAuth"));
			if (authCount > 1 && !userNameField.isEmpty()) {
				fpUserId.setText(userNameField);
			} else {
				fpUserId.setEditable(true);
			}
		} else {
			fpUserId.setText(SessionContext.userContext().getUserId());
		}
	}

	/**
	 * to check the role of supervisor in case of biometric exception
	 * 
	 * @param userId
	 *            - username entered by the supervisor in the authentication
	 *            screen
	 * @return boolean variable "true", if the person is authenticated as
	 *         supervisor or "false", if not
	 */
	private boolean fetchUserRole(String userId) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Fetching the user role in case of Supervisor Authentication");

		UserDTO userDTO = loginService.getUserDetail(userId);
		if (userDTO != null) {
			return userDTO.getUserRole().stream()
					.anyMatch(userRole -> userRole.getRoleCode().equalsIgnoreCase(RegistrationConstants.SUPERVISOR)
							|| userRole.getRoleCode().equalsIgnoreCase(RegistrationConstants.ADMIN_ROLE));
		}
		return false;
	}

	/**
	 * to capture and validate the fingerprint for authentication
	 * 
	 * @param userId
	 *            - username entered in the textfield
	 * @return true/false after validating fingerprint
	 * @throws IOException
	 * @throws RegBaseCheckedException
	 */
	private boolean captureAndValidateFP(String userId) throws RegBaseCheckedException, IOException {
		return bioService.validateFingerPrint(userId);
	}

	/**
	 * to capture and validate the iris for authentication
	 * 
	 * @param userId
	 *            - username entered in the textfield
	 * @return true/false after validating iris
	 * @throws IOException
	 */
	private boolean captureAndValidateIris(String userId) throws RegBaseCheckedException, IOException {
		return bioService.validateIris(userId);
	}

	/**
	 * to capture and validate the iris for authentication
	 * 
	 * @param userId
	 *            - username entered in the textfield
	 * @return true/false after validating face
	 */
	private boolean captureAndValidateFace(String userId) {
		try {
			return bioService.validateFace(userId);
		} catch (RegBaseCheckedException exception) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FACE_SCANNING_ERROR);
			LOGGER.error(LoggerConstants.LOG_REG_AUTH, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			return false;
		}
	}

	/**
	 * to submit the registration after successful authentication
	 */
	public void submitRegistration() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Submit Registration after Operator Authentication");

		packetHandlerController.showReciept();
	}

	/**
	 * event class to exit from authentication window. pop up window.
	 * 
	 * @param event
	 *            - the action event
	 */
	public void exitWindow(ActionEvent event) {
		Stage primaryStage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		primaryStage.close();

	}

	/**
	 * Setting the init method to the Basecontroller
	 * 
	 * @param parentControllerObj
	 *            - Parent Controller name
	 * @param authType
	 *            - Authentication Type
	 * @throws RegBaseCheckedException
	 */
	public void init(BaseController parentControllerObj, String authType) throws RegBaseCheckedException {
		authCount = 0;
		isSupervisor = true;
		isEODAuthentication = true;
		baseController = parentControllerObj;
		getAuthenticationModes(authType);

	}

	public void initData(String authType) throws RegBaseCheckedException {
		authCount = 0;
		int otpExpirySeconds = Integer
				.parseInt((getValueFromApplicationContext(RegistrationConstants.OTP_EXPIRY_TIME)).trim());
		int minutes = otpExpirySeconds / 60;
		String seconds = String.valueOf(otpExpirySeconds % 60);
		seconds = seconds.length() < 2 ? "0" + seconds : seconds;
		otpValidity.setText(RegistrationUIConstants.OTP_VALIDITY + " " + minutes + ":" + seconds + " "
				+ RegistrationUIConstants.MINUTES);
		stopTimer();
		isSupervisor = false;
		isEODAuthentication = false;
		getAuthenticationModes(authType);
	}

	private OSIDataDTO getOSIData() {
		return ((RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA)).getOsiDataDTO();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		int otpExpirySeconds = Integer
				.parseInt((getValueFromApplicationContext(RegistrationConstants.OTP_EXPIRY_TIME)).trim());
		int minutes = otpExpirySeconds / 60;
		String seconds = String.valueOf(otpExpirySeconds % 60);
		seconds = seconds.length() < 2 ? "0" + seconds : seconds;
		otpValidity.setText(RegistrationUIConstants.OTP_VALIDITY + " " + minutes + ":" + seconds);
		stopTimer();
		if (getRegistrationDTOFromSession() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
						.equals(RegistrationConstants.PACKET_TYPE_LOST)) {
			registrationNavlabel.setText(
					ApplicationContext.applicationLanguageBundle().getString(RegistrationConstants.LOSTUINLBL));
		}

		if (getRegistrationDTOFromSession() != null && getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			registrationNavlabel.setText(ApplicationContext.applicationLanguageBundle()
					.getString(RegistrationConstants.UIN_UPDATE_UINUPDATENAVLBL));
		}
	}

	public void goToPreviousPage() {
		auditFactory.audit(AuditEvent.REG_PREVIEW_BACK, Components.REG_PREVIEW, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_OPERATORAUTHENTICATIONPANE, false);
			SessionContext.map().put(RegistrationConstants.UIN_UPDATE_REGISTRATIONPREVIEW, true);
			registrationController.showUINUpdateCurrentPage();

		} else {
			registrationController.showCurrentPage(RegistrationConstants.OPERATOR_AUTHENTICATION,
					getPageDetails(RegistrationConstants.OPERATOR_AUTHENTICATION, RegistrationConstants.PREVIOUS));
		}
	}

	public void goToNextPage() {
		if (userAuthenticationTypeListValidation.isEmpty()) {
			userAuthenticationTypeListValidation = userAuthenticationTypeListSupervisorValidation;
		}

		switch (userAuthenticationTypeListValidation.get(0).toUpperCase()) {
		case RegistrationConstants.OTP:
			validateOTP();
			break;
		case RegistrationConstants.PWORD:
			validatePwd();
			break;
		case RegistrationConstants.FINGERPRINT_UPPERCASE:
			validateFingerprint();
			break;
		case RegistrationConstants.IRIS:
			validateIris();
			break;
		case RegistrationConstants.FACE:
			validateFace();
			break;
		default:

		}

	}

	/**
	 * This method will remove the auth method from list
	 * 
	 * @param authList
	 *            authentication list
	 * @param disableFlag
	 *            configuration flag
	 * @param authCode
	 *            auth mode
	 */
	private void removeAuthModes(List<String> authList, String flag, String authCode) {

		LOGGER.info(LoggerConstants.LOG_REG_AUTH, APPLICATION_NAME, APPLICATION_ID,
				"Ignoring FingerPrint, Iris, Face Authentication if the configuration is off");

		authList.removeIf(auth -> authList.size() > 1 && RegistrationConstants.DISABLE.equalsIgnoreCase(flag)
				&& auth.equalsIgnoreCase(authCode));
	}

}
