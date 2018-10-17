package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegConstants.URL;
import static io.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION;
import static io.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

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
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.LoginServiceImpl;
import io.mosip.registration.ui.constants.RegistrationUIConstants;
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

	@Autowired(required = true)
	private LoginServiceImpl loginServiceImpl;

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
		LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Retrieve Login mode");

		String loginMode = null;
		try {
			Map<String, Object> userLoginMode = loginServiceImpl.getModesOfLogin();

			if (userLoginMode.containsKey((RegistrationUIConstants.LOGIN_INITIAL_VAL))) {
				loginMode = String.valueOf(userLoginMode.get(RegistrationUIConstants.LOGIN_INITIAL_VAL));
			}

			// To maintain the login mode sequence
			SessionContext.getInstance().setMapObject(userLoginMode);
			SessionContext.getInstance().getMapObject().put(RegistrationUIConstants.LOGIN_INITIAL_SCREEN, loginMode);

			LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Retrieved correspondingLogin mode");

		} catch (NullPointerException nullPointerException) {
			generateAlert(RegistrationUIConstants.ALERT_ERROR, AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
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
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
						RegistrationUIConstants.MISSING_MANDATOTY_FIELDS,
						RegistrationUIConstants.CREDENTIALS_FIELD_EMPTY);
			} else if (userId.getText().isEmpty()) {
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
						RegistrationUIConstants.MISSING_MANDATOTY_FIELDS, RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			} else if (password.getText().isEmpty()) {
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
						RegistrationUIConstants.MISSING_MANDATOTY_FIELDS, RegistrationUIConstants.PWORD_FIELD_EMPTY);
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
					LOGGER.debug("REGISTRATION - USER_PASSWORD - LOGIN_CONTROLLER", getPropertyValue(APPLICATION_NAME),
							getPropertyValue(APPLICATION_ID), "Retrieving User Password from database");

					offlineStatus = loginServiceImpl.validateUserPassword(userDTO.getUserId(), hashPassword);
					if (!offlineStatus) {
						generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
								AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
								RegistrationUIConstants.LOGIN_INFO_MESSAGE, RegistrationUIConstants.INCORRECT_PWORD);
					}
				}
				if (serverStatus || offlineStatus) {
					if (validateUserStatus(userId.getText())) {
						generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
								AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
								RegistrationUIConstants.LOGIN_INFO_MESSAGE, RegistrationUIConstants.BLOCKED_USER_ERROR);
					} else {
						SessionContext sessionContext = SessionContext.getInstance();
						// Resetting login sequence to the Session context after log out
						if (sessionContext.getMapObject() == null) {
							Map<String, Object> userLoginMode = loginServiceImpl.getModesOfLogin();
							sessionContext.setMapObject(userLoginMode);
							sessionContext.getMapObject().put(RegistrationUIConstants.LOGIN_INITIAL_SCREEN,
									RegistrationUIConstants.LOGIN_METHOD_PWORD);
						}

						int counter = (int) sessionContext.getMapObject().get(RegConstants.LOGIN_SEQUENCE);
						counter++;
						if (sessionContext.getMapObject().containsKey(String.valueOf(counter))) {
							String mode = sessionContext.getMapObject().get(String.valueOf(counter)).toString();
							sessionContext.getMapObject().remove(String.valueOf(counter));
							if (mode.equals(RegistrationUIConstants.OTP)) {
								BorderPane pane = (BorderPane) ((Node) event.getSource()).getParent().getParent();
								AnchorPane loginType = BaseController
										.load(getClass().getResource(RegistrationUIConstants.LOGIN_OTP_PAGE));
								pane.setCenter(loginType);
							}
						} else {
							String authInfo = setInitialLoginInfoAndSessionContext(userId.getText());
							if (authInfo != null && authInfo.equals(RegistrationUIConstants.ROLES_EMPTY)) {
								generateAlert(RegistrationUIConstants.AUTHORIZATION_ALERT_TITLE,
										AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
										RegistrationUIConstants.LOGIN_FAILURE,
										RegistrationUIConstants.ROLES_EMPTY_ERROR);
							} else if (authInfo != null && authInfo.equals(RegistrationUIConstants.MACHINE_MAPPING)) {
								generateAlert(RegistrationUIConstants.AUTHORIZATION_ALERT_TITLE,
										AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
										RegistrationUIConstants.LOGIN_FAILURE,
										RegistrationUIConstants.MACHINE_MAPPING_ERROR);
							} else {
								schedulerUtil.startSchedulerUtil();
								BaseController.load(getClass().getResource(RegistrationUIConstants.HOME_PAGE));
							}
						}

					}
				}
			}
		} catch (IOException | RuntimeException | RegBaseCheckedException exception) {
			generateAlert(RegistrationUIConstants.ALERT_ERROR, AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR),
					RegistrationUIConstants.LOGIN_FAILURE, REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
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
			LOGGER.error("REGISTRATION - SERVER_CONNECTION_CHECK", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), resourceAccessException.getMessage());
		}
		return serverStatus;
	}
}
