package org.mosip.registration.controller;

import static org.mosip.registration.constants.RegConstants.URL;
import static org.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION;
import static org.mosip.registration.constants.RegistrationUIExceptionEnum.REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION;

import java.io.IOException;
import java.util.Map;

import org.mosip.kernel.core.utils.HMACUtil;
import org.mosip.registration.context.SessionContext;
import org.mosip.registration.dto.UserDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.scheduler.SchedulerUtil;
import org.mosip.registration.service.LoginServiceImpl;
import org.mosip.registration.ui.constants.RegistrationUIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

	@FXML
	private Label errorMsg;

	@Autowired(required = true)
	private LoginServiceImpl loginServiceImpl;

	@Autowired
	SchedulerUtil schedulerUtil;
	
	/**
	 * To get the Sequence of which Login screen to be displayed
	 * 
	 * @return String loginMode
	 * @throws RegBaseCheckedException 
	 */

	public String loadInitialScreen() throws RegBaseCheckedException {
		
		String loginMode = "";
		
		try {
			Map<String, Object> userLoginMode = loginServiceImpl.getModesOfLogin();
			SessionContext.getInstance().setMapObject(userLoginMode);
			if (userLoginMode.size() > 0) {
				loginMode = userLoginMode.get("1").toString();
			}
			
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
				errorMsg.setVisible(true);
				errorMsg.setText("Please enter UserName and Password.");
			} else {
				errorMsg.setVisible(false);
				String hashPassword = null;
				//password hashing
				
				if (!(password.getText().isEmpty())) {
					byte[] bytePassword = password.getText().getBytes();
					hashPassword = HMACUtil.digestAsPlainText(HMACUtil.generateHash(bytePassword));
				}
				UserDTO userObj = new UserDTO();
				userDTO.setUserId(userId.getText());
				userDTO.setPassword(hashPassword);
				
				boolean offlineStatus = false;
				boolean  serverStatus = getConnectionCheck(userObj);
				if(serverStatus == false) {
					offlineStatus = loginServiceImpl.validateUserPassword(userId.getText(), hashPassword);
				}
				
				if (serverStatus || offlineStatus) {
					SessionContext.getInstance().getMapObject().values().remove(RegistrationUIConstants.LOGIN_METHOD_PWORD);
					if (SessionContext.getInstance().getMapObject().size() > 0) {
						if (SessionContext.getInstance().getMapObject().get("2").equals(RegistrationUIConstants.OTP)) {
							BorderPane pane = (BorderPane) ((Node)event.getSource()).getParent().getParent();
							AnchorPane loginType = BaseController.load(getClass().getResource("/fxml/LoginWithOTP.fxml"));
							pane.setCenter(loginType);
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
	
	private boolean getConnectionCheck(UserDTO userObj) throws RegBaseCheckedException {
		
		HttpEntity<UserDTO> loginEntity = new HttpEntity<UserDTO>(userObj);
		ResponseEntity<String> tokenId = null;
		boolean serverStatus = false;
		
		try {
		tokenId = new RestTemplate().exchange(URL, HttpMethod.POST, loginEntity, String.class);
			if(tokenId.getStatusCode().value() == 200) {
				serverStatus = true;
			}
		} catch(RestClientException resourceAccessException) {
//			throw new RegBaseCheckedException(REG_UI_LOGIN_RESOURCE_EXCEPTION.getErrorCode(),
//			REG_UI_LOGIN_RESOURCE_EXCEPTION.getErrorMessage());
		} 
		return serverStatus;
	}
}
