package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.text.SimpleDateFormat;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.auth.LoginController;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.config.GlobalParamService;
import javafx.application.Application;
import javafx.application.HostServices;
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

	@Override
	public void start(Stage primaryStage) throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - LOGIN SCREEN INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
				APPLICATION_ID, "Login screen initilization "
						+ new SimpleDateFormat(RegistrationConstants.HH_MM_SS).format(System.currentTimeMillis()));

		BaseController baseController = applicationContext.getBean("baseController", BaseController.class);

		GlobalParamService globalParamService = applicationContext.getBean(GlobalParamService.class);
		ResponseDTO responseDTO = globalParamService.synchConfigData();
		if (responseDTO.getErrorResponseDTOs() != null) {
			ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
			baseController.generateAlert(RegistrationConstants.ERROR, errorResponseDTO.getMessage());
		} else {

			LoginController loginController = applicationContext.getBean(LoginController.class);
			loginController.loadInitialScreen(primaryStage);
		}

		io.mosip.registration.context.ApplicationContext.getInstance().getApplicationMap().put("hostServices",
				getHostServices());
		
		LOGGER.debug("REGISTRATION - LOGIN SCREEN INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
				APPLICATION_ID, "Login screen loaded"
						+ new SimpleDateFormat(RegistrationConstants.HH_MM_SS).format(System.currentTimeMillis()));

	}

	public static void main(String[] args) {
		System.setProperty("java.net.useSystemProxies", "true");
		applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
		
		launch(args);

		LOGGER.debug("REGISTRATION - APPLICATION INITILIZATION - REGISTRATIONAPPINITILIZATION", APPLICATION_NAME,
				APPLICATION_ID, "Application Initilization"
						+ new SimpleDateFormat(RegistrationConstants.HH_MM_SS).format(System.currentTimeMillis()));
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		System.exit(0);
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
