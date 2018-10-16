package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_HOMEPAGE_IO_EXCEPTION;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.ui.constants.RegistrationUIConstants;
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

			LOGGER.debug("REGISTRATION - REGSITRATION_HOME_PAGE_LAYOUT", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Constructing Registration Home Page");

			HBox headerRoot = BaseController.load(getClass().getResource(RegistrationUIConstants.HEADER_PAGE));
			mainBox.getChildren().add(headerRoot);
			AnchorPane updateRoot = BaseController.load(getClass().getResource(RegistrationUIConstants.UPDATE_PAGE));
			mainBox.getChildren().add(updateRoot);
			AnchorPane optionRoot = BaseController
					.load(getClass().getResource(RegistrationUIConstants.OFFICER_PACKET_PAGE));
			mainBox.getChildren().add(optionRoot);

			RegistrationAppInitialization.getScene().setRoot(mainBox);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			RegistrationAppInitialization.getScene().getStylesheets()
					.add(loader.getResource(RegistrationUIConstants.CSS_FILE_PATH).toExternalForm());

		} catch (IOException | RuntimeException exception) {
			generateAlert(RegistrationUIConstants.ALERT_ERROR, AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
					REG_UI_HOMEPAGE_IO_EXCEPTION.getErrorMessage());
		}
	}
}
