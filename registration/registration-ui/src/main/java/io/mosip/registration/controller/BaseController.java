package io.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.context.SessionContext;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.LoginServiceImpl;
import io.mosip.registration.ui.constants.RegistrationUIConstants;
import io.mosip.registration.util.mac.SystemMacAddress;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
	 * Setting values for Session context and User context and Initial info for
	 * Login
	 * 
	 * @param userId
	 *            entered userId
	 * @throws RegBaseCheckedException 
	 */
	protected String setInitialLoginInfoAndSessionContext(String userId) throws RegBaseCheckedException {
		RegistrationUserDetail userDetail = loginServiceImpl.getUserDetail(userId);
		String result = null;
		List<String> roleList = new ArrayList<>();
		
		userDetail.getUserRole().forEach(roleCode -> {
			if(userDetail.getIsActive()) {
				roleList.add(String
						.valueOf(roleCode.getRegistrationUserRoleId().getRoleCode()));
			}
		});
		
		// Checking roles
		if (roleList.isEmpty()) {
			result = RegistrationUIConstants.ROLES_EMPTY;
		} else if (roleList.contains(RegistrationUIConstants.ADMIN_ROLE)) {
			result = RegistrationUIConstants.SUCCESS_MSG;
		} else {
			// checking for machine mapping
			if (!getCenterMachineStatus(userDetail)) {
				result = RegistrationUIConstants.MACHINE_MAPPING;
			} else {
				result = RegistrationUIConstants.SUCCESS_MSG;
			}
		}
		if (result != null && result.equalsIgnoreCase(RegistrationUIConstants.SUCCESS_MSG)) {
			SessionContext sessionContext = SessionContext.getInstance();

			sessionContext.setLoginTime(new Date());
			sessionContext.setRefreshedLoginTime(refreshedLoginTime);
			sessionContext.setIdealTime(idealTime);
			sessionContext.setTimeoutInterval(timeoutInterval);

			SessionContext.UserContext userContext = sessionContext.getUserContext();
			userContext.setUserId(userId);
			userContext.setName(userDetail.getName());
			userContext.setRoles(roleList);
			userContext.setRegistrationCenterDetailDTO(
					loginServiceImpl.getRegistrationCenterDetails(userDetail.getCntrId()));

			String userRole = !userContext.getRoles().isEmpty() ? userContext.getRoles().get(0) : null;
			userContext.setAuthorizationDTO(loginServiceImpl.getScreenAuthorizationDetails(userRole));

		}
		return result;
	}

	/**
	 * Validating Id for Screen Authorization
	 * 
	 * @param screenId
	 *            the screenId
	 * @return boolean
	 */
	protected boolean validateScreenAuthorization(String screenId) {

		return SessionContext.getInstance().getUserContext().getAuthorizationDTO().getAuthorizationScreenId()
				.contains(screenId);
	}

	/**
	 * Fetching and Validating machine and center id
	 * 
	 * @param userDetail
	 *            the userDetail
	 * @return boolean
	 * @throws RegBaseCheckedException 
	 */
	private boolean getCenterMachineStatus(RegistrationUserDetail userDetail) throws RegBaseCheckedException {
		List<String> machineList = new ArrayList<>();
		List<String> centerList = new ArrayList<>();
		userDetail.getUserMachineMapping().forEach(machineMapping -> {
				if(machineMapping.getIsActive()) {
					machineList.add(machineMapping.getUserMachineMappingId().getMachineID());
					centerList.add(machineMapping.getUserMachineMappingId().getCentreID());
				} 
			});
		return machineList.contains(SystemMacAddress.getSystemMacAddress()) && centerList.contains(userDetail.getCntrId());
	}

	/**
	 * Validating user status
	 * 
	 * @param userId
	 *            the userId
	 * @return boolean
	 */
	protected boolean validateUserStatus(String userId) {
		RegistrationUserDetail userDetail = loginServiceImpl.getUserDetail(userId);
		return userDetail.getUserStatus() != null
				&& userDetail.getUserStatus().equalsIgnoreCase(RegistrationUIConstants.BLOCKED);
	}
	
	/**
	 * Regex validation with specified field and pattern
	 * 
	 * @param field
	 *            concerned field
	 * @param regexPattern
	 *            pattern need to checked
	 */
	protected boolean validateRegex(Control field,String regexPattern) {
		if(field instanceof TextField) {
			if(!((TextField) field).getText().matches(regexPattern))
				return true;
		}
		else {
			if(field instanceof PasswordField) {
				if(!((PasswordField) field).getText().matches(regexPattern))
					return true;
			}
		}
		return false;
	}
	

}
