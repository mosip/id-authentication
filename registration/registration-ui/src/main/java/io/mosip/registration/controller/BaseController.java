package io.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import io.mosip.registration.controller.RegistrationAppInitialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.LoginServiceImpl;
import io.mosip.registration.ui.constants.RegistrationUIConstants;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Base class for all controllers
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
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

	/**
	 * Loading FXML files along with beans 
	 * 
	 * @return
	 */
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

	/**
	 * Setting values for Session context and User context
	 * 
	 * @return
	 */
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
