package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.io.IOException;
import java.util.Map;

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

/**
 * Class for Registration Officer details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
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

		LOGGER.debug("REGISTRATION - OFFICER_DETAILS - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
				"Displaying Registration Officer details");

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
			Map<String, Object> restoreMap = SessionContext.getInstance().getMapObject();

			LOGGER.debug("REGISTRATION - LOGOUT - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "Clearing Session context");

			SessionContext.destroySession();
			SchedulerUtil.stopScheduler();

			LOGGER.debug("REGISTRATION - LOGOUT - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID),
					"Restoring Login sequence after Logout");

			SessionContext.getInstance().setMapObject(restoreMap);
			SessionContext.getInstance().getMapObject().put("sequence", 1);

			String fxmlPath = "";
			switch (initialMode) {
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

			BorderPane loginpage = BaseController.load(getClass().getResource("/fxml/RegistrationLogin.fxml"));
			AnchorPane loginType = BaseController.load(getClass().getResource(fxmlPath));
			loginpage.setCenter(loginType);
			RegistrationAppInitialization.getScene().setRoot(loginpage);
			
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI - Logout ", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), ioException.getMessage());
		}
	}

	/**
	 * Redirecting to Home page
	 */
	public void redirectHome(ActionEvent event) {
		try {

			LOGGER.debug("REGISTRATION - REDIRECT_HOME - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), "Redirecting to Home page");

			VBox homePage = BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
			RegistrationAppInitialization.getScene().setRoot(homePage);

		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI- Redirect Home Page ", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), ioException.getMessage());
		}
	}
}
