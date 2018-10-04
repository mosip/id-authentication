package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.ui.constants.RegistrationUIConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

@Controller
public class RegistrationOfficerDetailsController extends BaseController {
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	@FXML
	private Label registrationOfficerName;

	@FXML
	private Label registrationOfficeId;

	@FXML
	private Label registrationOfficeLocation;

	/**
	 * Mapping Registration Officer details
	 */
	public void initialize() {
		registrationOfficerName.setText(LoginController.userDTO.getUsername());
		registrationOfficeId.setText(LoginController.userDTO.getCenterId());
		registrationOfficeLocation.setText(LoginController.userDTO.getCenterLocation());
	}

	/**
	 * Redirecting to Home page on Logout and destroying Session context
	 */
	public void logout(ActionEvent event) {
		try {
			String initialMode = SessionContext.getInstance().getMapObject().get("initialMode").toString();
			SessionContext.destroySession();
			SchedulerUtil.stopScheduler();
			BorderPane loginpage = BaseController.load(getClass().getResource("/fxml/RegistrationLogin.fxml"));
			
			String fxmlPath = "";
			switch(initialMode) {
				case RegistrationUIConstants.OTP:
					fxmlPath = "/fxml/LoginWithOTP.fxml";
					break;
				case RegistrationUIConstants.LOGIN_METHOD_PWORD:
					fxmlPath = "/fxml/LoginWithCredentials.fxml";
					break;
				default:
					fxmlPath = "/fxml/LoginWithCredentials.fxml";
					break;			
			}
			AnchorPane loginType = BaseController.load(getClass().getResource(fxmlPath));
			loginpage.setCenter(loginType);
			RegistrationAppInitialization.getScene().setRoot(loginpage);
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI - Logout ", APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}
	
	/**
	 * Redirecting to Home page
	 */
	public void redirectHome(ActionEvent event) {
		try {
			VBox homePage = BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
			RegistrationAppInitialization.getScene().setRoot(homePage);
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Redirect Home Page ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}
}
