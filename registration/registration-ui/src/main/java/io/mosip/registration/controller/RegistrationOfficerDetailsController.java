package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;

import java.io.IOException;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.registration.context.SessionContext;
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

	public void initialize() throws Exception {
		registrationOfficerName.setText(LoginController.userDTO.getUsername());
		registrationOfficeId.setText(LoginController.userDTO.getCenterId());
		registrationOfficeLocation.setText(LoginController.userDTO.getCenterLocation());
	}

	@FXML
	public void logout(ActionEvent event) {
		try {
			SessionContext.getInstance().destroySession();
			BorderPane loginpage = BaseController.load(getClass().getResource("/fxml/RegistrationLogin.fxml"));
			String loginModeFXMLpath = "/fxml/LoginWithCredentials.fxml";
			AnchorPane loginType = BaseController.load(getClass().getResource(loginModeFXMLpath));
			loginpage.setCenter(loginType);
			RegistrationAppInitialization.getScene().setRoot(loginpage);
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI - Logout ", APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}

	@FXML
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
