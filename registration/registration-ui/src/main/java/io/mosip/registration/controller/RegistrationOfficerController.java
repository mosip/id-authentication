package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
	VBox mainBox;	
	
	/**
	 * Building Home screen on Login success
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {	
			HBox headerRoot = BaseController.load(getClass().getResource("/fxml/Header.fxml"));
			mainBox.getChildren().add(headerRoot);
			AnchorPane updateRoot = BaseController.load(getClass().getResource("/fxml/UpdateLayout.fxml"));
			mainBox.getChildren().add(updateRoot);
			AnchorPane optionRoot = BaseController.load(getClass().getResource("/fxml/RegistrationOfficerPacketLayout.fxml"));
			mainBox.getChildren().add(optionRoot);
			
			RegistrationAppInitialization.getScene().setRoot(mainBox);			
			ClassLoader loader = Thread.currentThread().getContextClassLoader(); 
			RegistrationAppInitialization.getScene().getStylesheets().add(loader.getResource("application.css").toExternalForm());
			
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REGSITRATION_HOME_PAGE_LAYOUT", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), ioException.getMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - REGSITRATION_HOME_PAGE_LAYOUT - VIEW", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), runtimeException.getMessage());
		}
	}	
}

	
