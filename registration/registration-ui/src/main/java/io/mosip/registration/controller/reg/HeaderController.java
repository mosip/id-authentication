package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.Initialization;
import io.mosip.registration.controller.auth.LoginController;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.service.config.JobConfigurationService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Class for Registration Officer details
 * 
 * @author Sravya Surampalli
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Controller
public class HeaderController extends BaseController {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(HeaderController.class);

	@FXML
	private Label registrationOfficerName;

	@FXML
	private Label registrationOfficeId;

	@FXML
	private Label registrationOfficeLocation;

	@FXML
	private MenuBar menu;

	@FXML
	private ImageView availableIcon;

	@Autowired
	PreRegistrationDataSyncService preRegistrationDataSyncService;

	@Autowired
	JobConfigurationService jobConfigurationService;

	@Autowired
	MasterSyncService masterSyncService;

	/**
	 * Mapping Registration Officer details
	 */
	public void initialize() {

		LOGGER.debug("REGISTRATION - OFFICER_DETAILS - REGISTRATION_OFFICER_DETAILS_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Displaying Registration Officer details");

		SessionContext sessionContext = SessionContext.getInstance();
		registrationOfficerName.setText(sessionContext.getUserContext().getName());
		registrationOfficeId
				.setText(sessionContext.getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterId());
		registrationOfficeLocation
				.setText(sessionContext.getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterName());
		menu.setBackground(Background.EMPTY);
		menu.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

		getTimer().schedule(new TimerTask() {

			@Override
			public void run() {
				availableIcon.setVisible(RegistrationAppHealthCheckUtil.isNetworkAvailable());
			}
		}, 0, 5000);
	}

	/**
	 * Redirecting to Home page on Logout and destroying Session context
	 */
	public void logout(ActionEvent event) {
		try {
			String initialMode = SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.LOGIN_INITIAL_SCREEN).toString();

			LOGGER.debug("REGISTRATION - LOGOUT - REGISTRATION_OFFICER_DETAILS_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "Clearing Session context");

			SessionContext.destroySession();
			SchedulerUtil.stopScheduler();

			BorderPane loginpage = BaseController.load(getClass().getResource(RegistrationConstants.INITIAL_PAGE));
			LoginController loginController = Initialization.getApplicationContext().getBean(LoginController.class);
			loginController.loadLoginScreen(initialMode);
			getScene(loginpage);

		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - LOGOUT - REGISTRATION_OFFICER_DETAILS_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, ioException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_LOGOUT_PAGE);
		}
	}

	/**
	 * Redirecting to Home page
	 */
	public void redirectHome(ActionEvent event) {
		try {

			LOGGER.debug("REGISTRATION - REDIRECT_HOME - REGISTRATION_OFFICER_DETAILS_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "Redirecting to Home page");

			VBox homePage = BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
			getScene(homePage);

		} catch (IOException | RuntimeException exception) {

			LOGGER.error("REGISTRATION - REDIRECTHOME - REGISTRATION_OFFICER_DETAILS_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, exception.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_HOME_PAGE);
		}
	}

	/**
	 * change On-Board user Perspective
	 * 
	 * @param event is an action event
	 * @throws IOException
	 */
	public void onBoardUser(ActionEvent event) throws IOException {
		AnchorPane onBoardRoot = BaseController
				.load(getClass().getResource(RegistrationConstants.USER_MACHINE_MAPPING));

		if (!validateScreenAuthorization(onBoardRoot.getId())) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.AUTHORIZATION_ERROR);
		} else {
			VBox pane = (VBox) menu.getParent().getParent().getParent();
			Object parent = pane.getChildren().get(0);
			pane.getChildren().clear();
			pane.getChildren().add((Node) parent);
			pane.getChildren().add(onBoardRoot);

		}
	}

	/**
	 * Sync master data.
	 *
	 * @param event the event
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	public void syncMasterData(ActionEvent event) throws RegBaseCheckedException {

		ResponseDTO masterResponse = null;

		String errorMessage = "";

		try {

			LOGGER.debug("REGISTRATION - SYNC MASTER DATA - REGISTRATION_OFFICER_DETAILS_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, "Syncing master data");

			masterResponse = masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001);

			generateAlert(RegistrationConstants.ALERT_INFORMATION, masterResponse.getSuccessResponseDTO().getMessage());

		} catch (RuntimeException exception) {

			LOGGER.error("REGISTRATION - SYNC MASTER DATA - REGISTRATION_OFFICER_DETAILS_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID, exception.getMessage());

			if (null != masterResponse) {
				List<ErrorResponseDTO> errorMsgList = masterResponse.getErrorResponseDTOs();

				for (ErrorResponseDTO errorResponseDTO : errorMsgList) {
					errorMessage = errorResponseDTO.getMessage();
				}
			}

			generateAlert(RegistrationConstants.ALERT_INFORMATION, errorMessage);
		}
	}

	/**
	 * Redirecting to PacketStatusSync Page
	 */
	public void syncPacketStatus(ActionEvent event) {
		try {
			AnchorPane syncServerClientRoot = BaseController
					.load(getClass().getResource(RegistrationConstants.SYNC_STATUS));

			if (!validateScreenAuthorization(syncServerClientRoot.getId())) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.AUTHORIZATION_ERROR);
			} else {
				VBox pane = (VBox) (menu.getParent().getParent().getParent());
				for (int index = pane.getChildren().size() - 1; index > 0; index--) {
					pane.getChildren().remove(index);
				}
				pane.getChildren().add(syncServerClientRoot);

			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI - Officer Sync Packet Status ", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage());
		}
	}

	/**
	 * Redirects to Device On-Boarding UI Page.
	 * 
	 * @param actionEvent is an action event
	 */
	public void onBoardDevice(ActionEvent actionEvent) {
		LOGGER.debug(LoggerConstants.DEVICE_ONBOARD_PAGE_NAVIGATION, APPLICATION_NAME, APPLICATION_ID,
				"Navigating to Device Onboarding Page");

		try {
			AnchorPane onBoardRoot = BaseController
					.load(getClass().getResource(RegistrationConstants.DEVICE_ONBOARDING_PAGE));

			if (!validateScreenAuthorization(onBoardRoot.getId())) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.AUTHORIZATION_ERROR);
			} else {
				VBox pane = (VBox) menu.getParent().getParent().getParent();
				Object parent = pane.getChildren().get(0);
				pane.getChildren().clear();
				pane.getChildren().add((Node) parent);
				pane.getChildren().add(onBoardRoot);
			}
		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.DEVICE_ONBOARD_PAGE_NAVIGATION, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.DEVICE_ONBOARD_PAGE_NAVIGATION_EXCEPTION
							+ "-> Exception while navigating to Device Onboarding page:" + ioException.getMessage());

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DEVICE_ONBOARD_ERROR_MSG);
		} finally {
			LOGGER.debug(LoggerConstants.DEVICE_ONBOARD_PAGE_NAVIGATION, APPLICATION_NAME, APPLICATION_ID,
					"Navigation to Device Onboarding page completed");
		}
	}

	@FXML
	public void downloadPreRegData(ActionEvent event) {
		// preRegistrationDataSyncService.getPreRegistrationIds("syncJobId");
	}

	@FXML
	public void syncInitializer(ActionEvent event) {
		ResponseDTO responseDTO = jobConfigurationService.startScheduler(Initialization.getApplicationContext());

		if (responseDTO.getErrorResponseDTOs() != null) {
			ErrorResponseDTO errorresponse = responseDTO.getErrorResponseDTOs().get(0);
			generateAlert(errorresponse.getCode(), errorresponse.getMessage());
		} else if (responseDTO.getSuccessResponseDTO() != null) {
			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
			generateAlert(successResponseDTO.getCode(), successResponseDTO.getMessage());
		}

	}

}