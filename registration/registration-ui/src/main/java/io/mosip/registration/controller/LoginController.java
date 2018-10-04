package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;
import static io.mosip.registration.constants.RegConstants.URL;
import static io.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION;
import static io.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION;

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
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
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
	SchedulerUtil schedulerUtil;
	
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;
	
	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender the mosip rolling file appender
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
		
		LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieve Login mode");
		
		String loginMode = "";
		
		try {
			Map<String, Object> userLoginMode = loginServiceImpl.getModesOfLogin();
			SessionContext.getInstance().setMapObject(userLoginMode);
			if (userLoginMode.size() > 0) {
				loginMode = userLoginMode.get("1").toString();
			}
			SessionContext.getInstance().getMapObject().put("initialMode", loginMode);
			LOGGER.debug("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", 
					getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
					"Retrieved correspondingLogin mode");
		} catch (NullPointerException nullPointerException) {
			throw new RegBaseCheckedException(REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION.getErrorCode(),
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
	public void validateCredentials(ActionEvent event) throws RegBaseCheckedException {
		try {
			if (userId.getText().isEmpty() && password.getText().isEmpty()) {
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR), RegistrationUIConstants.LOGIN_INFO_MESSAGE,
						RegistrationUIConstants.CREDENTIALS_FIELD_EMPTY);
			} else if(userId.getText().isEmpty()) {
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR), RegistrationUIConstants.LOGIN_INFO_MESSAGE,
						RegistrationUIConstants.USERNAME_FIELD_EMPTY);
			} else if(password.getText().isEmpty()) {
				generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR), RegistrationUIConstants.LOGIN_INFO_MESSAGE,
						RegistrationUIConstants.PWORD_FIELD_EMPTY);
			} else {
				String hashPassword = null;
				//password hashing
				
				if (!(password.getText().isEmpty())) {
					byte[] bytePassword = password.getText().getBytes();
					hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));
				}
				UserDTO userObj = new UserDTO();
				userDTO.setUserId(userId.getText());
				userDTO.setPassword(hashPassword);
				
				boolean offlineStatus = false;
				//Server connection check
				boolean  serverStatus = getConnectionCheck(userObj);
				if(!serverStatus) {
					
					LOGGER.debug("REGISTRATION - USER_PASSWORD - LOGIN_CONTROLLER", 
							getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
							"Retrieving User Password from database");
					//blocked user check
					String blockedUserCheck = loginServiceImpl.getBlockedUserCheck(userId.getText());
					
					if(blockedUserCheck.equals(RegistrationUIConstants.BLOCKED)) {
						generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
								AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR), RegistrationUIConstants.LOGIN_INFO_MESSAGE,
								RegistrationUIConstants.BLOCKED_USER_ERROR);
					} else {
						offlineStatus = loginServiceImpl.validateUserPassword(userId.getText(), hashPassword);
						if(!offlineStatus) {
							generateAlert(RegistrationUIConstants.LOGIN_ALERT_TITLE,
									AlertType.valueOf(RegistrationUIConstants.ALERT_ERROR), RegistrationUIConstants.LOGIN_INFO_MESSAGE,
									RegistrationUIConstants.CREDENTIALS_FIELD_ERROR);
						}
					}
				}
				
				if (serverStatus || offlineStatus) {
					int counter = 0;
					if(SessionContext.getInstance().getMapObject() != null) {
						counter = (int) SessionContext.getInstance().getMapObject().get("sequence");
						counter++;
						if(SessionContext.getInstance().getMapObject().containsKey(""+counter)) {
							String mode = SessionContext.getInstance().getMapObject().get(""+counter).toString();
							if (mode.equals(RegistrationUIConstants.OTP)) {
									BorderPane pane = (BorderPane) ((Node)event.getSource()).getParent().getParent();
									AnchorPane loginType = BaseController.load(getClass().getResource("/fxml/LoginWithOTP.fxml"));
									pane.setCenter(loginType);							
							}
						} else {
							setSessionContext(userId.getText());
							schedulerUtil.startSchedulerUtil();
							BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
						}
					} else {
						setSessionContext(userId.getText());
						schedulerUtil.startSchedulerUtil();
						BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
					}
				}

			}
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorCode(),
					REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorCode(),
					REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION.getErrorMessage(), runtimeException);
		}

	}
	
	private boolean getConnectionCheck(UserDTO userObj) {
		
		HttpEntity<UserDTO> loginEntity = new HttpEntity<UserDTO>(userObj);
		ResponseEntity<String> tokenId = null;
		boolean serverStatus = false;
		
		try {
		tokenId = new RestTemplate().exchange(URL, HttpMethod.POST, loginEntity, String.class);
			if(tokenId.getStatusCode().value() == 200) {
				serverStatus = true;
			}
		} catch(RestClientException resourceAccessException) {
			LOGGER.error("REGISTRATION - SERVER_CONNECTION_CHECK", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), resourceAccessException.getMessage());
		} 
		return serverStatus;
	}
}
