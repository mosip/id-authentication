package org.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.constants.RegistrationUIExceptionCode;
import org.mosip.registration.context.SessionContext;
import org.mosip.registration.dto.UserDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.scheduler.SchedulerUtil;
import org.mosip.registration.service.LoginServiceImpl;
import org.mosip.registration.ui.constants.RegistrationUIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

@PropertySource("classpath:registration.properties")
public class BaseController {

	@Autowired
	private LoginServiceImpl loginServiceImpl;

	public static final UserDTO userDTO = new UserDTO();

	@Value("${TIME_OUT_INTERVAL:30}")
	private long timeoutInterval;

	@Value("${IDEAL_TIME}")
	private long idealTime;

	@Value("${REFRESHED_LOGIN_TIME}")
	private long refreshedLoginTime;

	protected static Stage stage;

	/**
	 * Adding events to the stage
	 * 
	 * @return
	 */
	protected static Stage getStage() {
		EventHandler<Event> event = new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				SchedulerUtil.setCurrentTimeToStartTime();
			}
		};
		stage.addEventHandler(EventType.ROOT, event);
		return stage;
	}

	public static <T> T load(URL url) throws IOException {
		FXMLLoader loader = new FXMLLoader(url);
		loader.setControllerFactory(RegistrationAppInitialization.getApplicationContext()::getBean);
		return loader.load();
	}

	/**
	 * 
	 * /* Alert creation with specified title, header, and context
	 * 
	 * @param title
	 *            alert title
	 * @param alertType
	 *            type of alert
	 * @param header
	 *            alert header
	 * @param context
	 *            alert context
	 */
	protected void generateAlert(String title, AlertType alertType, String header, String context) {
		Alert alert = new Alert(alertType);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.setTitle(title);
		alert.showAndWait();
	}

	/**
	 * Alert creation with specified title and context
	 * 
	 * @param title
	 *            alert title
	 * @param alertType
	 *            type of alert
	 * @param context
	 *            alert context
	 */
	protected void generateAlert(String title, AlertType alertType, String context) {
		Alert alert = new Alert(alertType);
		alert.setContentText(context);
		alert.setHeaderText(null);
		alert.setTitle(title);
		alert.showAndWait();

	}

	/**
	 * Alert creation with specified title and context
	 * 
	 * @param alertType
	 *            type of alert
	 * @param title
	 *            alert title
	 * @param header
	 *            alert header
	 */
	protected void generateAlert(AlertType alertType, String title, String header) {
		Alert alert = new Alert(alertType);
		alert.setHeaderText(header);
		alert.setContentText(null);
		alert.setTitle(title);
		alert.showAndWait();

	}

	protected void setSessionContext(String userId) {
		Map<String, String> userDetail = loginServiceImpl.getUserDetail(userId);

		userDTO.setUsername(userDetail.get("name"));
		userDTO.setUserId(userId);
		userDTO.setCenterId(userDetail.get(RegistrationUIConstants.CENTER_ID));
		userDTO.setCenterLocation(loginServiceImpl.getCenterName(userDetail.get(RegistrationUIConstants.CENTER_ID)));

		SessionContext sessionContext = SessionContext.getInstance();

		sessionContext.setLoginTime(new Date());
		sessionContext.setRefreshedLoginTime(refreshedLoginTime);
		sessionContext.setIdealTime(timeoutInterval);
		sessionContext.setTimeoutInterval(idealTime);
		SessionContext.UserContext userContext = sessionContext.getUserContext();
		userContext.setUserId(userId);
		userContext.setName(userDetail.get("name"));
		userContext.setRegistrationCenterDetailDTO(
				loginServiceImpl.getRegistrationCenterDetails(userDetail.get(RegistrationUIConstants.CENTER_ID)));
		userContext.setRoles(loginServiceImpl.getRoles(userId));
	}

}
