package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_HOMEPAGE_IO_EXCEPTION;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationConstants;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Class for Home Page 
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Component
public class RegistrationOfficerController extends BaseController implements Initializable {
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	@FXML
	private VBox mainBox;

	/**
	 * Building Home screen on Login success
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {

			LOGGER.debug("REGISTRATION - REGSITRATION_HOME_PAGE_LAYOUT", APPLICATION_NAME,
					APPLICATION_ID, "Constructing Registration Home Page");

			HBox headerRoot = BaseController.load(getClass().getResource(RegistrationConstants.HEADER_PAGE));
			mainBox.getChildren().add(headerRoot);
			AnchorPane updateRoot = BaseController.load(getClass().getResource(RegistrationConstants.UPDATE_PAGE));
			mainBox.getChildren().add(updateRoot);
			AnchorPane optionRoot = BaseController
					.load(getClass().getResource(RegistrationConstants.OFFICER_PACKET_PAGE));
			mainBox.getChildren().add(optionRoot);

			RegistrationAppInitialization.getScene().setRoot(mainBox);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			RegistrationAppInitialization.getScene().getStylesheets()
					.add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());

		} catch (IOException | RuntimeException exception) {
			generateAlert(RegistrationConstants.ALERT_ERROR, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					REG_UI_HOMEPAGE_IO_EXCEPTION.getErrorMessage());
		}
	}
}
