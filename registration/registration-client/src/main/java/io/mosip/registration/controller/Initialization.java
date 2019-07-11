package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.text.SimpleDateFormat;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.auth.LoginController;
import io.mosip.registration.tpm.initialize.TPMInitialization;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class for initializing the application
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Component
public class Initialization extends Application {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(Initialization.class);

	private static ApplicationContext applicationContext;
	private static Stage applicationPrimaryStage;

	@Override
	public void start(Stage primaryStage) {
		try {
			LOGGER.info("REGISTRATION - LOGIN SCREEN INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
					APPLICATION_ID, "Login screen initilization "
							+ new SimpleDateFormat(RegistrationConstants.HH_MM_SS).format(System.currentTimeMillis()));

			setPrimaryStage(primaryStage);
			LoginController loginController = applicationContext.getBean(LoginController.class);
			loginController.loadInitialScreen(primaryStage);
			SessionContext.setApplicationContext(applicationContext);

			LOGGER.info("REGISTRATION - LOGIN SCREEN INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
					APPLICATION_ID, "Login screen loaded"
							+ new SimpleDateFormat(RegistrationConstants.HH_MM_SS).format(System.currentTimeMillis()));
		} catch (Exception exception) {
			LOGGER.error("REGISTRATION - APPLICATION INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
					APPLICATION_ID,
					"Application Initilization Error"
							+ new SimpleDateFormat(RegistrationConstants.HH_MM_SS).format(System.currentTimeMillis())
							+ ExceptionUtils.getStackTrace(exception));
		}
	}

	public static void main(String[] args) {
		try {
			System.setProperty("java.net.useSystemProxies", "true");
			System.setProperty("file.encoding", "UTF-8");
			io.mosip.registration.context.ApplicationContext.getInstance();
			applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

			launch(args);

			LOGGER.info("REGISTRATION - APPLICATION INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
					APPLICATION_ID, "Application Initilization"
							+ new SimpleDateFormat(RegistrationConstants.HH_MM_SS).format(System.currentTimeMillis()));
		} catch (Exception exception) {
			LOGGER.error("REGISTRATION - APPLICATION INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
					APPLICATION_ID,
					"Application Initilization Error"
							+ new SimpleDateFormat(RegistrationConstants.HH_MM_SS).format(System.currentTimeMillis())
							+ ExceptionUtils.getStackTrace(exception));
		}
	}

	@Override
	public void stop() {
		try {
			super.stop();
			TPMInitialization.closeTPMInstance();
		} catch (Exception exception) {
			LOGGER.error("REGISTRATION - APPLICATION INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
					APPLICATION_ID,
					"Application Initilization Error"
							+ new SimpleDateFormat(RegistrationConstants.HH_MM_SS).format(System.currentTimeMillis())
							+ ExceptionUtils.getStackTrace(exception));
		} finally {
			System.exit(0);
		}
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		Initialization.applicationContext = applicationContext;
	}

	public static Stage getPrimaryStage() {
		return applicationPrimaryStage;
	}
	
	public static void setPrimaryStage(Stage primaryStage) {
		applicationPrimaryStage =  primaryStage;
	}

}
