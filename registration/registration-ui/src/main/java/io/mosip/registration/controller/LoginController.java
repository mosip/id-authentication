package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.URL;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * Class for loading Login screen with Username and password
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Controller
public class LoginController extends BaseController {

	@FXML
	private TextField userId;

	@FXML
	private TextField password;

	@Autowired
	private LoginService loginService;

	@Autowired
	private SchedulerUtil schedulerUtil;
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender
	 *            the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * To get the Sequence of which Login screen to be displayed
	 * 
	 * @return String loginMode
	 * @throws RegBaseCheckedException
	 */
	public String loadInitialScreen() throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Retrieve Login mode");

		String loginMode = null;
		try {
			Map<String, Object> userLoginMode = loginService.getModesOfLogin();

			if (userLoginMode.containsKey((RegistrationConstants.LOGIN_INITIAL_VAL))) {
				loginMode = String.valueOf(userLoginMode.get(RegistrationConstants.LOGIN_INITIAL_VAL));
			}

			// To maintain the login mode sequence
			SessionContext.getInstance().setMapObject(userLoginMode);
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.LOGIN_INITIAL_SCREEN, loginMode);

			LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "Retrieved correspondingLogin mode");

		} catch (NullPointerException nullPointerException) {
			generateAlert(RegistrationConstants.ALERT_ERROR, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
		}

		return loginMode;
	}

	/**
	 * 
	 * Validating User credentials on Submit
	 * 
	 * @return String loginMode
	 * @throws RegBaseCheckedException
	 */
	public void validateCredentials(ActionEvent event) {

		try {
			if (userId.getText().isEmpty() && password.getText().isEmpty()) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.MISSING_MANDATOTY_FIELDS,
						RegistrationConstants.CREDENTIALS_FIELD_EMPTY);
			} else if (userId.getText().isEmpty()) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.MISSING_MANDATOTY_FIELDS, RegistrationConstants.USERNAME_FIELD_EMPTY);
			} else if (password.getText().isEmpty()) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.MISSING_MANDATOTY_FIELDS, RegistrationConstants.PWORD_FIELD_EMPTY);
			} else {

				String hashPassword = null;

				// password hashing
				if (!(password.getText().isEmpty())) {
					byte[] bytePassword = password.getText().getBytes();
					hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));
				}
				LoginUserDTO userDTO = new LoginUserDTO();
				userDTO.setUserId(userId.getText());
				userDTO.setPassword(hashPassword);
				// Server connection check
				boolean serverStatus = getConnectionCheck(userDTO);
				boolean offlineStatus = false;

				if (!serverStatus) {
					LOGGER.debug("REGISTRATION - USER_PASSWORD - LOGIN_CONTROLLER", APPLICATION_NAME,
							APPLICATION_ID, "Retrieving User Password from database");

					offlineStatus = loginService.validateUserPassword(userDTO.getUserId(), hashPassword);
					if (!offlineStatus) {
						generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
								AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
								RegistrationConstants.LOGIN_INFO_MESSAGE, RegistrationConstants.INCORRECT_PWORD);
					}
				}
				if (serverStatus || offlineStatus) {
					if (validateUserStatus(userId.getText())) {
						generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
								AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
								RegistrationConstants.LOGIN_INFO_MESSAGE, RegistrationConstants.BLOCKED_USER_ERROR);
					} else {
						SessionContext sessionContext = SessionContext.getInstance();
						// Resetting login sequence to the Session context after log out
						if (sessionContext.getMapObject() == null) {
							Map<String, Object> userLoginMode = loginService.getModesOfLogin();
							sessionContext.setMapObject(userLoginMode);
							sessionContext.getMapObject().put(RegistrationConstants.LOGIN_INITIAL_SCREEN,
									RegistrationConstants.LOGIN_METHOD_PWORD);
						}

						int counter = (int) sessionContext.getMapObject().get(RegistrationConstants.LOGIN_SEQUENCE);
						counter++;
						if (sessionContext.getMapObject().containsKey(String.valueOf(counter))) {
							String mode = sessionContext.getMapObject().get(String.valueOf(counter)).toString();
							sessionContext.getMapObject().remove(String.valueOf(counter));
							if (mode.equals(RegistrationConstants.OTP)) {
								BorderPane pane = (BorderPane) ((Node) event.getSource()).getParent().getParent();
								AnchorPane loginType = BaseController
										.load(getClass().getResource(RegistrationConstants.LOGIN_OTP_PAGE));
								pane.setCenter(loginType);
							}
						} else {
							String authInfo = setInitialLoginInfoAndSessionContext(userId.getText());
							if (authInfo != null && authInfo.equals(RegistrationConstants.ROLES_EMPTY)) {
								generateAlert(RegistrationConstants.AUTHORIZATION_ALERT_TITLE,
										AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
										RegistrationConstants.LOGIN_FAILURE,
										RegistrationConstants.ROLES_EMPTY_ERROR);
							} else if (authInfo != null && authInfo.equals(RegistrationConstants.MACHINE_MAPPING)) {
								generateAlert(RegistrationConstants.AUTHORIZATION_ALERT_TITLE,
										AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
										RegistrationConstants.LOGIN_FAILURE,
										RegistrationConstants.MACHINE_MAPPING_ERROR);
							} else {
								schedulerUtil.startSchedulerUtil();
								BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
							}
						}

					}
				}
			}
		} catch (IOException | RuntimeException | RegBaseCheckedException exception) {
			generateAlert(RegistrationConstants.ALERT_ERROR, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.LOGIN_FAILURE, REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
		}

	}

	/**
	 * Checking server status
	 * 
	 * @param LoginUserDTO
	 *            the UserDTO object
	 * @return boolean
	 */
	private boolean getConnectionCheck(LoginUserDTO userObj) {

		HttpEntity<LoginUserDTO> loginEntity = new HttpEntity<>(userObj);
		ResponseEntity<String> tokenId = null;
		boolean serverStatus = false;

		try {
			tokenId = new RestTemplate().exchange(URL, HttpMethod.POST, loginEntity, String.class);
			if (tokenId.getStatusCode().is2xxSuccessful()) {
				serverStatus = true;
			}
		} catch (RestClientException resourceAccessException) {
			LOGGER.error("REGISTRATION - SERVER_CONNECTION_CHECK", APPLICATION_NAME,
					APPLICATION_ID, resourceAccessException.getMessage());
		}
		return serverStatus;
	}
}
