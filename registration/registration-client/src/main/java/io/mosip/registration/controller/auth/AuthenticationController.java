package io.mosip.registration.controller.auth;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.PacketHandlerController;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.device.fp.MosipFingerprintProvider;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.service.AuthenticationService;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.util.common.OTPManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Class for Operator Authentication
 *
 * 
 * 
 * 
 */
@Controller
public class AuthenticationController extends BaseController {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AuthenticationController.class);

	@FXML
	private AnchorPane temporaryLogin;
	@FXML
	private AnchorPane pwdBasedLogin;
	@FXML
	private AnchorPane otpBasedLogin;
	@FXML
	private AnchorPane fingerprintBasedLogin;
	@FXML
	private AnchorPane irisBasedLogin;
	@FXML
	private AnchorPane faceBasedLogin;
	@FXML
	private Label otpValidity;
	@FXML
	private Label otpLabel;
	@FXML
	private Label fingerPrintLabel;
	@FXML
	private Label irisLabel;
	@FXML
	private Label faceLabel;
	@FXML
	private TextField fpUserId;
	@FXML
	private TextField irisUserId;
	@FXML
	private TextField faceUserId;
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

	@Autowired
	private FingerprintFacade fingerprintFacade;

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
	private PacketHandlerController packetHandlerController;

	@Autowired
	private AuthenticationService authService;

	@Autowired
	private OTPManager otpGenerator;

	@Autowired
	private LoginService loginService;

	@Value("${USERNAME_PWD_LENGTH}")
	private int usernamePwdLength;

	private boolean isSupervisor = false;

	private boolean isEODAuthentication = false;

	private List<String> userAuthenticationTypeList;

	private int authCount = 0;

	private String userNameField;

	@Autowired
	private BaseController baseController;

	/**
	 * to generate OTP in case of OTP based authentication
	 */
	public void generateOtp() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Generate OTP for OTP based Authentication");

		if (!otpUserId.getText().isEmpty()) {
			// Response obtained from server
			ResponseDTO responseDTO = null;

			// Service Layer interaction
			responseDTO = otpGenerator.getOTP(otpUserId.getText());
			if (responseDTO.getSuccessResponseDTO() != null) {
				// Enable submit button
				// Generate alert to show OTP
				SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
				generateAlert(RegistrationConstants.ALERT_INFORMATION, successResponseDTO.getMessage());
			} else if (responseDTO.getErrorResponseDTOs() != null) {
				// Generate Alert to show INVALID USERNAME
				ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
				generateAlert(RegistrationConstants.ERROR, errorResponseDTO.getMessage());
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
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating OTP for OTP based Authentication");

		if (isSupervisor) {
			if (!otpUserId.getText().isEmpty()) {
				if (fetchUserRole(otpUserId.getText())) {
					if (otp.getText() != null) {
						if (otpGenerator.validateOTP(otpUserId.getText(), otp.getText())
								.getSuccessResponseDTO() != null) {
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
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.OTP_FIELD_EMPTY);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_AUTHORIZED);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		} else {
			if (otp.getText() != null) {
				if (otpGenerator.validateOTP(otpUserId.getText(), otp.getText()).getSuccessResponseDTO() != null) {
					if (!isEODAuthentication) {
						getOSIData().setOperatorAuthenticatedByPIN(true);
					}
					loadNextScreen();
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.OTP_VALIDATION_ERROR_MESSAGE);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.OTP_FIELD_EMPTY);
			}
		}
	}

	public void validatePwd() {
		String status = "";
		if (isSupervisor) {
			if (!username.getText().isEmpty()) {
				if (fetchUserRole(username.getText())) {
					status = validatePwd(username.getText(), password.getText());
					if (!isEODAuthentication) {
						getOSIData().setSupervisorID(userNameField);
						getOSIData().setSuperviorAuthenticatedByPassword(true);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_AUTHORIZED);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		} else {
			if (!username.getText().isEmpty()) {
				status = validatePwd(username.getText(), password.getText());
				if (!isEODAuthentication) {
					getOSIData().setOperatorAuthenticatedByPassword(true);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		}

		if (RegistrationConstants.SUCCESS.equals(status)) {
			userNameField = username.getText();
			loadNextScreen();
		} else if (RegistrationConstants.FAILURE.equals(status)) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.INCORRECT_PWORD);
		}
	}

	/**
	 * to validate the fingerprint in case of fingerprint based authentication
	 */
	public void validateFingerprint() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating Fingerprint for Fingerprint based Authentication");

		if (isSupervisor) {
			if (!fpUserId.getText().isEmpty()) {
				if (fetchUserRole(fpUserId.getText())) {
					if (captureAndValidateFP(fpUserId.getText())) {
						userNameField = fpUserId.getText();
						if (!isEODAuthentication) {
							getOSIData().setSupervisorID(userNameField);
						}
						loadNextScreen();
					} else {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FINGER_PRINT_MATCH);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_AUTHORIZED);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		} else {
			if (captureAndValidateFP(fpUserId.getText())) {
				loadNextScreen();
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FINGER_PRINT_MATCH);
			}
		}
	}

	/**
	 * to validate the iris in case of iris based authentication
	 */
	public void validateIris() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating Iris for Iris based Authentication");

		if (isSupervisor) {
			if (!irisUserId.getText().isEmpty()) {
				if (fetchUserRole(irisUserId.getText())) {
					if (captureAndValidateIris(irisUserId.getText())) {
						userNameField = irisUserId.getText();
						if (!isEODAuthentication) {
							getOSIData().setSupervisorID(userNameField);
						}
						loadNextScreen();
					} else {
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.IRIS_MATCH);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USER_NOT_AUTHORIZED);
				}
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			}
		} else {
			if (captureAndValidateIris(irisUserId.getText())) {
				loadNextScreen();
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.IRIS_MATCH);
			}
		}
	}

	/**
	 * to validate the face in case of face based authentication
	 */
	public void validateFace() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating Face for Face based Authentication");

		if (isSupervisor) {
			if (!faceUserId.getText().isEmpty()) {
				if (fetchUserRole(faceUserId.getText())) {
					if (captureAndValidateFace(faceUserId.getText())) {
						userNameField = faceUserId.getText();
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
			if (captureAndValidateFace(faceUserId.getText())) {
				loadNextScreen();
			} else {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FACE_MATCH);
			}
		}
	}

	/**
	 * to get the configured modes of authentication
	 */
	private void getAuthenticationModes(String authType) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Loading configured modes of authentication");

		Set<String> roleSet = new HashSet<>();
		roleSet.add("*");

		userAuthenticationTypeList = loginService.getModesOfLogin(authType, roleSet);

		if (userAuthenticationTypeList.isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHENTICATION_ERROR_MSG);
		} else {
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
		Boolean toogleBioException = (Boolean) SessionContext.getInstance().getUserContext().getUserMap()
				.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);

		if (!userAuthenticationTypeList.isEmpty()) {
			authCount++;
			String authenticationType = String
					.valueOf(userAuthenticationTypeList.get(RegistrationConstants.PARAM_ZERO));
			userAuthenticationTypeList.remove(RegistrationConstants.PARAM_ZERO);

			loadAuthenticationScreen(authenticationType);
		} else {
			if (!isSupervisor) {
				if (toogleBioException != null && toogleBioException.booleanValue()) {
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
	}

	/**
	 * to enable the respective authentication mode
	 * 
	 * @param loginMode - name of authentication mode
	 */
	public void loadAuthenticationScreen(String loginMode) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Loading the respective authentication screen in UI");

		switch (loginMode) {
		case RegistrationConstants.OTP:
			enableOTP();
			break;
		case RegistrationConstants.PWORD:
			enablePWD();
			break;
		case RegistrationConstants.BIO:
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
	}

	/**
	 * to enable the OTP based authentication mode and disable rest of modes
	 */
	private void enableOTP() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling OTP based Authentication Screen in UI");

		pwdBasedLogin.setVisible(false);
		otpBasedLogin.setVisible(true);
		fingerprintBasedLogin.setVisible(false);
		faceBasedLogin.setVisible(false);
		irisBasedLogin.setVisible(false);
		otp.clear();
		otpUserId.clear();
		otpUserId.setEditable(false);
		if (isSupervisor) {
			otpLabel.setText(RegistrationConstants.SUPERVISOR_VERIFICATION);
			if (authCount > 1 && !userNameField.isEmpty()) {
				otpUserId.setText(userNameField);
			} else {
				otpUserId.setEditable(true);
			}
		} else

		{
			otpUserId.setText(SessionContext.getInstance().getUserContext().getUserId());
		}
	}

	/**
	 * to enable the password based authentication mode and disable rest of modes
	 */
	private void enablePWD() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling Password based Authentication Screen in UI");

		pwdBasedLogin.setVisible(true);
		otpBasedLogin.setVisible(false);
		fingerprintBasedLogin.setVisible(false);
		irisBasedLogin.setVisible(false);
		faceBasedLogin.setVisible(false);
		username.clear();
		password.clear();
		username.setEditable(false);
		if (isSupervisor) {
			passwdLabel.setText(RegistrationConstants.SUPERVISOR_VERIFICATION);
			if (authCount > 1 && !userNameField.isEmpty()) {
				username.setText(userNameField);
			} else {
				username.setEditable(true);
			}
		} else {
			username.setText(SessionContext.getInstance().getUserContext().getUserId());
		}
	}

	/**
	 * to enable the fingerprint based authentication mode and disable rest of modes
	 */
	private void enableFingerPrint() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling Fingerprint based Authentication Screen in UI");

		fingerprintBasedLogin.setVisible(true);
		faceBasedLogin.setVisible(false);
		irisBasedLogin.setVisible(false);
		otpBasedLogin.setVisible(false);
		pwdBasedLogin.setVisible(false);
		fpUserId.clear();
		fpUserId.setEditable(false);
		if (isSupervisor) {
			fingerPrintLabel.setText(RegistrationConstants.SUPERVISOR_FINGERPRINT_LOGIN);
			if (authCount > 1 && !userNameField.isEmpty()) {
				fpUserId.setText(userNameField);
			} else {
				fpUserId.setEditable(true);
			}
		} else {
			fpUserId.setText(SessionContext.getInstance().getUserContext().getUserId());
		}
	}

	/**
	 * to enable the iris based authentication mode and disable rest of modes
	 */
	private void enableIris() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling Iris based Authentication Screen in UI");

		irisBasedLogin.setVisible(true);
		fingerprintBasedLogin.setVisible(false);
		otpBasedLogin.setVisible(false);
		pwdBasedLogin.setVisible(false);
		irisUserId.clear();
		irisUserId.setEditable(false);
		if (isSupervisor) {
			irisLabel.setText(RegistrationConstants.SUPERVISOR_VERIFICATION);
			if (authCount > 1 && !userNameField.isEmpty()) {
				irisUserId.setText(userNameField);
			} else {
				irisUserId.setEditable(true);
			}
		} else {
			irisUserId.setText(SessionContext.getInstance().getUserContext().getUserId());
		}
	}

	/**
	 * to enable the face based authentication mode and disable rest of modes
	 */
	private void enableFace() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Enabling Face based Authentication Screen in UI");

		faceBasedLogin.setVisible(true);
		irisBasedLogin.setVisible(false);
		fingerprintBasedLogin.setVisible(false);
		otpBasedLogin.setVisible(false);
		pwdBasedLogin.setVisible(false);
		faceUserId.clear();
		faceUserId.setEditable(false);
		if (isSupervisor) {
			faceLabel.setText(RegistrationConstants.SUPERVISOR_VERIFICATION);
			if (authCount > 1 && !userNameField.isEmpty()) {
				faceUserId.setText(userNameField);
			} else {
				faceUserId.setEditable(true);
			}
		} else {
			faceUserId.setText(SessionContext.getInstance().getUserContext().getUserId());
		}
	}

	/**
	 * to check the role of supervisor in case of biometric exception
	 * 
	 * @param userId - username entered by the supervisor in the authentication
	 *               screen
	 * @return boolean variable "true", if the person is authenticated as supervisor
	 *         or "false", if not
	 */
	private boolean fetchUserRole(String userId) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Fetching the user role in case of Supervisor Authentication");

		UserDetail userDetail = loginService.getUserDetail(userId);
		if (userDetail != null) {
			return userDetail.getUserRole().stream().anyMatch(userRole -> userRole.getUserRoleID().getRoleCode()
					.equalsIgnoreCase(RegistrationConstants.SUPERVISOR_NAME));
		}
		return false;
	}

	/**
	 * to capture and validate the fingerprint for authentication
	 * 
	 * @param userId - username entered in the textfield
	 * @return true/false after validating fingerprint
	 */
	private boolean captureAndValidateFP(String userId) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Capturing and Validating Fingerprint");

		boolean fpMatchStatus = false;
		MosipFingerprintProvider fingerPrintConnector = fingerprintFacade.getFingerprintProviderFactory(providerName);
		int statusCode = fingerPrintConnector.captureFingerprint(qualityScore, captureTimeOut, "");
		if (statusCode != 0) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.DEVICE_FP_NOT_FOUND);
		} else {
			// Thread to wait until capture the bio image/ minutia from FP. based on the
			// error code or success code the respective action will be taken care.
			waitToCaptureBioImage(5, 2000, fingerprintFacade);
			LOGGER.info("REGISTRATION - SCAN_FINGER - SCAN_FINGER_COMPLETED", APPLICATION_NAME, APPLICATION_ID,
					"Fingerprint scan done");

			fingerPrintConnector.uninitFingerPrintDevice();
			if (RegistrationConstants.EMPTY.equals(fingerprintFacade.getMinutia())) {
				// if FP data fetched then retrieve the user specific detail from db.
				AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
				List<FingerprintDetailsDTO> fingerprintDetailsDTOs = new ArrayList<>();
				FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
				fingerprintDetailsDTO.setFingerPrint(fingerprintFacade.getIsoTemplate());
				fingerprintDetailsDTOs.add(fingerprintDetailsDTO);
				if (!isEODAuthentication) {
					if (isSupervisor) {
						RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
								.get(RegistrationConstants.REGISTRATION_DATA);
						registrationDTO.getBiometricDTO().getSupervisorBiometricDTO()
								.setFingerprintDetailsDTO(fingerprintDetailsDTOs);
					} else {
						RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
								.get(RegistrationConstants.REGISTRATION_DATA);
						registrationDTO.getBiometricDTO().getOperatorBiometricDTO()
								.setFingerprintDetailsDTO(fingerprintDetailsDTOs);
					}
				}
				authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
				authenticationValidatorDTO.setUserId(userId);
				authenticationValidatorDTO.setAuthValidationType(RegistrationConstants.VALIDATION_TYPE_FP_SINGLE);
				fpMatchStatus = authService.authValidator(RegistrationConstants.FINGERPRINT,
						authenticationValidatorDTO);

				if (fpMatchStatus) {
					if (isSupervisor) {
						fingerprintDetailsDTO.setFingerprintImageName(
								"supervisor".concat(fingerprintDetailsDTO.getFingerType()).concat(".jpg"));
					} else {
						fingerprintDetailsDTO.setFingerprintImageName(
								"officer".concat(fingerprintDetailsDTO.getFingerType()).concat(".jpg"));
					}
				}
			}
		}
		return fpMatchStatus;
	}

	/**
	 * to capture and validate the iris for authentication
	 * 
	 * @param userId - username entered in the textfield
	 * @return true/false after validating iris
	 */
	private boolean captureAndValidateIris(String userId) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Capturing and Validating Iris");

		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		List<IrisDetailsDTO> irisDetailsDTOs = new ArrayList<>();
		IrisDetailsDTO irisDetailsDTO = new IrisDetailsDTO();
		irisDetailsDTO.setIris(RegistrationConstants.IRIS_STUB.getBytes());
		irisDetailsDTOs.add(irisDetailsDTO);
		if (!isEODAuthentication) {
			if (isSupervisor) {
				RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
						.get(RegistrationConstants.REGISTRATION_DATA);
				registrationDTO.getBiometricDTO().getSupervisorBiometricDTO().setIrisDetailsDTO(irisDetailsDTOs);
				SessionContext.getInstance().getMapObject().get(RegistrationConstants.REGISTRATION_DATA);
			} else {
				RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
						.get(RegistrationConstants.REGISTRATION_DATA);
				registrationDTO.getBiometricDTO().getOperatorBiometricDTO().setIrisDetailsDTO(irisDetailsDTOs);
			}
		}
		authenticationValidatorDTO.setIrisDetails(irisDetailsDTOs);
		authenticationValidatorDTO.setUserId(userId);
		boolean irisMatchStatus = authService.authValidator(RegistrationConstants.IRIS, authenticationValidatorDTO);

		if (irisMatchStatus) {
			if (isSupervisor) {
				irisDetailsDTO.setIrisImageName("supervisor".concat(irisDetailsDTO.getIrisType()).concat(".jpg"));
			} else {
				irisDetailsDTO.setIrisImageName("officer".concat(irisDetailsDTO.getIrisType()).concat(".jpg"));
			}
		}
		return irisMatchStatus;
	}

	/**
	 * to capture and validate the iris for authentication
	 * 
	 * @param userId - username entered in the textfield
	 * @return true/false after validating face
	 */
	private boolean captureAndValidateFace(String userId) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Capturing and Validating Face");

		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();

		FaceDetailsDTO faceDetailsDTO = new FaceDetailsDTO();
		faceDetailsDTO.setFace(RegistrationConstants.FACE.toLowerCase().getBytes());

		if (!isEODAuthentication) {
			if (isSupervisor) {
				RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
						.get(RegistrationConstants.REGISTRATION_DATA);
				registrationDTO.getBiometricDTO().getSupervisorBiometricDTO().setFaceDetailsDTO(faceDetailsDTO);
				SessionContext.getInstance().getMapObject().get(RegistrationConstants.REGISTRATION_DATA);
			} else {
				RegistrationDTO registrationDTO = (RegistrationDTO) SessionContext.getInstance().getMapObject()
						.get(RegistrationConstants.REGISTRATION_DATA);
				registrationDTO.getBiometricDTO().getOperatorBiometricDTO().setFaceDetailsDTO(faceDetailsDTO);
			}
		}

		authenticationValidatorDTO.setFaceDetail(faceDetailsDTO);
		authenticationValidatorDTO.setUserId(userId);
		return authService.authValidator(RegistrationConstants.FACE, authenticationValidatorDTO);
	}

	/**
	 * to submit the registration after successful authentication
	 */
	public void submitRegistration() {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Submit Registration after Operator Authentication");

		packetHandlerController.showReciept(capturePhotoUsingDevice);
	}

	/**
	 * event class to exit from authentication window. pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow(ActionEvent event) {
		Stage primaryStage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		primaryStage.close();

	}

	/**
	 * Setting the init method to the Basecontroller
	 * 
	 * @param parentControllerObj
	 */
	public void init(BaseController parentControllerObj, String authType) {
		authCount = 0;
		otpValidity.setText("Valid for " + otpValidityInMins + " minutes");
		isSupervisor = true;
		isEODAuthentication = true;
		baseController = parentControllerObj;
		getAuthenticationModes(authType);

	}

	public void initData(String authType) {
		authCount = 0;
		otpValidity.setText("Valid for " + otpValidityInMins + " minutes");
		isSupervisor = false;
		isEODAuthentication = false;
		getAuthenticationModes(authType);
	}

	private OSIDataDTO getOSIData() {
		return ((RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA)).getOsiDataDTO();
	}

}
