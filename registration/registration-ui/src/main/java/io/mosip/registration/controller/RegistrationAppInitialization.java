package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_LOGIN_IO_EXCEPTION;

import java.io.IOException;
import java.text.SimpleDateFormat;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationUIExceptionCode;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.ui.constants.RegistrationUIConstants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Class for initializing the application
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

@Component
public class RegistrationAppInitialization extends Application {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	private static ApplicationContext applicationContext;

	/*
	 * Load the initial screen by getting values form the database. Maintaining the
	 * same Stage for all the scenes.
	 */
	private static Scene scene;

	@Override
	public void start(Stage primaryStage) throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - LOGIN SCREEN INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
				APPLICATION_ID, "Login screen initilization "
						+ new SimpleDateFormat(RegistrationUIConstants.HH_MM_SS).format(System.currentTimeMillis()));

		BaseController.stage = primaryStage;
		primaryStage = BaseController.getStage();

		LoginController loginController = applicationContext.getBean(LoginController.class);
		String loginMode = loginController.loadInitialScreen();
		String loginModeFXMLpath = null;
		try {
			BorderPane loginRoot = BaseController.load(getClass().getResource("/fxml/RegistrationLogin.fxml"));
			if (loginMode.equals("OTP")) {
				loginModeFXMLpath = "/fxml/LoginWithOTP.fxml";
				AnchorPane loginType = BaseController.load(getClass().getResource(loginModeFXMLpath));
				loginRoot.setCenter(loginType);
			} else if (loginMode.equals(RegistrationUIConstants.LOGIN_METHOD_PWORD)) {
				loginModeFXMLpath = "/fxml/LoginWithCredentials.fxml";
				AnchorPane loginType = BaseController.load(getClass().getResource(loginModeFXMLpath));
				loginRoot.setCenter(loginType);
			}
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene = new Scene(loginRoot, 950, 630);
			scene.getStylesheets().add(loader.getResource("application.css").toExternalForm());

			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationUIExceptionCode.REG_UI_LOGIN_LOADER_EXCEPTION,
					runtimeException.getMessage(), runtimeException);
		}

		LOGGER.debug("REGISTRATION - LOGIN SCREEN INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
				APPLICATION_ID, "Login screen loaded"
						+ new SimpleDateFormat(RegistrationUIConstants.HH_MM_SS).format(System.currentTimeMillis()));

	}

	public static void main(String[] args) {

		applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
		launch(args);
		LOGGER.debug("REGISTRATION - APPLICATION INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
				APPLICATION_ID, "Application Initilization"
						+ new SimpleDateFormat(RegistrationUIConstants.HH_MM_SS).format(System.currentTimeMillis()));

	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Scene getScene() {
		return scene;
	}
}
