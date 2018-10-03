package org.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.context.SessionContext;
import org.mosip.registration.dto.ErrorResponseDTO;
import org.mosip.registration.dto.ResponseDTO;
import org.mosip.registration.dto.SuccessResponseDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.scheduler.SchedulerUtil;
import org.mosip.registration.service.LoginServiceImpl;
import org.mosip.registration.ui.constants.RegistrationUIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

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
@PropertySource("classpath:registration.properties")
public class LoginOTPController extends BaseController implements Initializable {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}
	
	@Autowired
	Environment environment;

	/**
	 * The reference to loginServiceImpl class.
	 */
	@Autowired
	LoginServiceImpl loginServiceImpl;

	@Autowired
	SchedulerUtil schedulerUtil;

	/**
	 * Field to enter UIN at UI
	 */
	@FXML
	TextField eoUsername;

	@FXML
	PasswordField otp;

	@FXML
	Button submit;

	@FXML
	Button getOTP;

	@FXML
	Button resend;

	@FXML
	Label otpValidity;

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
	public void generateOtp(ActionEvent event) throws RegBaseCheckedException {
		if (!eoUsername.getText().isEmpty()) {
			// Response obtained from server
			ResponseDTO responseDTO = null;

			// Service Layer interaction
			responseDTO = loginServiceImpl.getOTP(eoUsername.getText());

			if (responseDTO.getSuccessResponseDTO() != null) {
				// Enable submit button
				changeToOTPSubmitMode();

				// Generate alert to show OTP
				SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(successResponseDTO.getCode()), RegistrationUIConstants.OTP_INFO_MESSAGE,
						successResponseDTO.getMessage());

			} else if (responseDTO.getErrorResponseDTOs() != null) {
				// Generate Alert to show INVALID USERNAME
				ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(errorResponseDTO.getCode()),
						RegistrationUIConstants.OTP_INFO_MESSAGE, errorResponseDTO.getMessage());

			}

		} else {
			// Generate Alert to show username field was empty
			generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
					AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR), RegistrationUIConstants.OTP_INFO_MESSAGE,
					RegistrationUIConstants.USERNAME_FIELD_EMPTY);

		}
	}

	/**
	 * Validate User through username and otp
	 * 
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	/**
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	/**
	 * @param event
	 * @throws RegBaseCheckedException
	 */
	@FXML
	public void validateUser(ActionEvent event) {
		try {
			if (!otp.getText().isEmpty() && otp.getText().length() != 3) {

				ResponseDTO responseDTO = null;

				responseDTO = loginServiceImpl.validateOTP(eoUsername.getText(), otp.getText());

				if (responseDTO != null) {
					if (responseDTO.getSuccessResponseDTO() != null) {

						int counter = 0;
						if(SessionContext.getInstance().getMapObject() != null) {
							counter = (int) SessionContext.getInstance().getMapObject().get("sequence");
							counter++;
							if(SessionContext.getInstance().getMapObject().containsKey(""+counter)) {
								String mode = SessionContext.getInstance().getMapObject().get(""+counter).toString();
								if (mode.equals(RegistrationUIConstants.LOGIN_METHOD_PWORD)) {
										BorderPane pane = (BorderPane) ((Node)event.getSource()).getParent().getParent();
										AnchorPane loginType = BaseController.load(getClass().getResource("/fxml/LoginWithCredentials.fxml"));
										pane.setCenter(loginType);							
								}
							} else {
								setSessionContext(userDTO.getUserId());
								schedulerUtil.startSchedulerUtil();
								BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
							}
						} else {
							setSessionContext(userDTO.getUserId());
							schedulerUtil.startSchedulerUtil();
							BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
						}

					} else {
						// Generate invalid otp alert
						ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
						generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
								AlertType.valueOf(errorResponseDTO.getCode()), RegistrationUIConstants.LOGIN_FAILURE,
								errorResponseDTO.getMessage());
					}
				}

			} else if (eoUsername.getText().length() == 3) {
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
						RegistrationUIConstants.OTP_INFO_MESSAGE, RegistrationUIConstants.USERNAME_FIELD_ERROR);
			} else {
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
						RegistrationUIConstants.OTP_INFO_MESSAGE, RegistrationUIConstants.OTP_FIELD_EMPTY);
			}
		} catch (RegBaseCheckedException ioException) {

		} catch (IOException e) {
			
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
