package io.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/* This class provides the controller method required to generate OTP.
* 
* @author Yaswanth S
* @since 1.0.0
*/
@Controller
@PropertySource("classpath:application.properties")
public class LoginOTPController extends BaseController implements Initializable {

	@Autowired
	private Environment environment;

	/**
	 * The reference to loginService interface.
	 */
	@Autowired
	private LoginService loginService;

	@Autowired
	private SchedulerUtil schedulerUtil;

	/**
	 * Field to enter UIN at UI
	 */
	@FXML
	private TextField eoUsername;

	@FXML
	private PasswordField otp;

	@FXML
	private Button submit;

	@FXML
	private Button getOTP;

	@FXML
	private Button resend;

	@FXML
	private Label otpValidity;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		otpValidity.setText("Valid for " + environment.getProperty("otp_validity_in_mins") + " minutes");
	}

	/**
	 * Generate OTP based on EO username
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	@FXML
	public void generateOtp(ActionEvent event) {

		if (!eoUsername.getText().isEmpty()) {
			// Response obtained from server
			ResponseDTO responseDTO = null;

			// Service Layer interaction
			responseDTO = loginService.getOTP(eoUsername.getText());

			if (responseDTO.getSuccessResponseDTO() != null) {
				// Enable submit button
				changeToOTPSubmitMode();

				// Generate alert to show OTP
				SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(successResponseDTO.getCode()), RegistrationConstants.OTP_INFO_MESSAGE,
						successResponseDTO.getMessage());

			} else if (responseDTO.getErrorResponseDTOs() != null) {
				// Generate Alert to show INVALID USERNAME
				ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(errorResponseDTO.getCode()),
						RegistrationConstants.OTP_INFO_MESSAGE, errorResponseDTO.getMessage());

			}

		} else {
			// Generate Alert to show username field was empty
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
					AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.OTP_INFO_MESSAGE,
					RegistrationConstants.USERNAME_FIELD_EMPTY);

		}

	}

	/**
	 * Validate User through username and otp
	 * 
	 * @param event
	 */
	@FXML
	public void validateUser(ActionEvent event) {
		try {
			if (!otp.getText().isEmpty() && otp.getText().length() != 3) {

				ResponseDTO responseDTO = null;

				responseDTO = loginService.validateOTP(eoUsername.getText(), otp.getText());

				if (responseDTO != null) {
					if (responseDTO.getSuccessResponseDTO() != null) {
						//Validating User status
						if (validateUserStatus(eoUsername.getText())) {
							generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
									AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
									RegistrationConstants.LOGIN_INFO_MESSAGE,
									RegistrationConstants.BLOCKED_USER_ERROR);
						} else {
							SessionContext sessionContext = SessionContext.getInstance();
							// Resetting login sequence to the Session context after log out
							if (sessionContext.getMapObject() == null) {
								Map<String, Object> userLoginMode = loginService.getModesOfLogin();
								sessionContext.setMapObject(userLoginMode);
								sessionContext.getMapObject().put(RegistrationConstants.LOGIN_INITIAL_SCREEN,
										RegistrationConstants.OTP);
							}

							int counter = (int) sessionContext.getMapObject().get(RegistrationConstants.LOGIN_SEQUENCE);
							counter++;
							if (sessionContext.getMapObject().containsKey(String.valueOf(counter))) {
								String mode = sessionContext.getMapObject().get(String.valueOf(counter)).toString();
								sessionContext.getMapObject().remove(String.valueOf(counter));
								if (mode.equals(RegistrationConstants.LOGIN_METHOD_PWORD)) {
									BorderPane pane = (BorderPane) ((Node) event.getSource()).getParent().getParent();
									AnchorPane loginType = BaseController
											.load(getClass().getResource(RegistrationConstants.LOGIN_PWORD_PAGE));
									pane.setCenter(loginType);
								}
							} else {
								String authInfo = setInitialLoginInfoAndSessionContext(eoUsername.getText());
								if(authInfo != null && authInfo.equals(RegistrationConstants.ROLES_EMPTY)) {
									generateAlert(RegistrationConstants.AUTHORIZATION_ALERT_TITLE,
											AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
											RegistrationConstants.AUTHORIZATION_ERROR,
											RegistrationConstants.ROLES_EMPTY_ERROR);
								} else if(authInfo != null && authInfo.equals(RegistrationConstants.MACHINE_MAPPING)) {
									generateAlert(RegistrationConstants.AUTHORIZATION_ALERT_TITLE,
											AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
											RegistrationConstants.AUTHORIZATION_ERROR,
											RegistrationConstants.MACHINE_MAPPING_ERROR);
								} else {
									schedulerUtil.startSchedulerUtil();
									BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
								}
							}

						}

					} else {
						// Generate invalid otp alert
						ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
						generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
								AlertType.valueOf(errorResponseDTO.getCode()), RegistrationConstants.LOGIN_FAILURE,
								errorResponseDTO.getMessage());
					}
				}

			} else if (eoUsername.getText().length() == 3) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.OTP_INFO_MESSAGE, RegistrationConstants.USERNAME_FIELD_ERROR);
			} else {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.OTP_INFO_MESSAGE, RegistrationConstants.OTP_FIELD_EMPTY);
			}
		} catch (IOException | RuntimeException | RegBaseCheckedException exception) {
			generateAlert(RegistrationConstants.ALERT_ERROR, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.LOGIN_FAILURE, RegistrationExceptions.REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
		}
	}

	/**
	 * Mode of login with set of fields enabling and disabling
	 */
	private void changeToOTPSubmitMode() {
		submit.setDisable(false);
		getOTP.setVisible(false);
		resend.setVisible(true);

	}

}
