package io.mosip.registration.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.service.SyncStatusValidatorService;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Base class for all controllers
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@PropertySource("classpath:application.properties")
public class BaseController {

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private SyncStatusValidatorService syncStatusValidatorService;

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
		RegistrationUserDetail userDetail = loginService.getUserDetail(userId);
		String result = null;
		List<String> roleList = new ArrayList<>();

		userDetail.getUserRole().forEach(roleCode -> {
			if(roleCode.getIsActive()) {
				roleList.add(String
						.valueOf(roleCode.getRegistrationUserRoleID().getRoleCode()));
			}
		});

		// Checking roles
		if (roleList.isEmpty()) {
			result = RegistrationConstants.ROLES_EMPTY;
		} else if (roleList.contains(RegistrationConstants.ADMIN_ROLE)) {
			result = RegistrationConstants.SUCCESS_MSG;
		} else {
			// checking for machine mapping
			if (!getCenterMachineStatus(userDetail)) {
				result = RegistrationConstants.MACHINE_MAPPING;
			} else {
				result = RegistrationConstants.SUCCESS_MSG;
			}
		}
		if (result != null && result.equalsIgnoreCase(RegistrationConstants.SUCCESS_MSG)) {
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
					loginService.getRegistrationCenterDetails(userDetail.getCntrId()));

			String userRole = !userContext.getRoles().isEmpty() ? userContext.getRoles().get(0) : null;
			userContext.setAuthorizationDTO(loginService.getScreenAuthorizationDetails(userRole));

		}
		return result;
	}
	
	protected ResponseDTO validateSyncStatus() {
		
		return syncStatusValidatorService.validateSyncStatus();
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
		return machineList.contains(RegistrationSystemPropertiesChecker.getMachineId()) && centerList.contains(userDetail.getCntrId());
	}

	/**
	 * Validating user status
	 * 
	 * @param userId
	 *            the userId
	 * @return boolean
	 */
	protected boolean validateUserStatus(String userId) {
		RegistrationUserDetail userDetail = loginService.getUserDetail(userId);
		return userDetail.getUserStatus() != null
				&& userDetail.getUserStatus().equalsIgnoreCase(RegistrationConstants.BLOCKED);
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
