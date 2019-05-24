package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.xml.sax.SAXException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.RestartController;
import io.mosip.registration.controller.auth.LoginController;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.jobs.BaseJob;
import io.mosip.registration.scheduler.SchedulerUtil;
import io.mosip.registration.service.config.JobConfigurationService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.service.sync.SyncStatusValidatorService;
import io.mosip.registration.update.RegistrationUpdate;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
	 * o Instance of {@link Logger}
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

	@FXML
	private GridPane online;

	@FXML
	private GridPane offline;

	@FXML
	private Menu homeSelectionMenu;

	@Autowired
	PreRegistrationDataSyncService preRegistrationDataSyncService;

	@Autowired
	JobConfigurationService jobConfigurationService;

	@Autowired
	MasterSyncService masterSyncService;

	@Autowired
	MasterSyncDao masterSyncDao;

	@Autowired
	PacketHandlerController packetHandlerController;

	@Autowired
	private RestartController restartController;

	@Autowired
	private RegistrationUpdate registrationUpdate;

	@Autowired
	private HomeController homeController;

	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;

	ProgressIndicator progressIndicator;

	@Autowired
	private SyncStatusValidatorService statusValidatorService;

	@Autowired
	private LoginController loginController;

	/**
	 * Mapping Registration Officer details
	 */
	public void initialize() {

		LOGGER.info(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID,
				"Displaying Registration Officer details");

		registrationOfficerName.setText(SessionContext.userContext().getName());
		registrationOfficeId
				.setText(SessionContext.userContext().getRegistrationCenterDetailDTO().getRegistrationCenterId());
		registrationOfficeLocation
				.setText(SessionContext.userContext().getRegistrationCenterDetailDTO().getRegistrationCenterName());
		menu.setBackground(Background.EMPTY);

		menu.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)
				&& !(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER_UPDATE)) {
			homeSelectionMenu.getItems().remove(0, homeSelectionMenu.getItems().size() - 3);
		} else {
			homeSelectionMenu.setDisable(false);
		}

		getTimer().schedule(new TimerTask() {

			@Override
			public void run() {
				Boolean flag = RegistrationAppHealthCheckUtil.isNetworkAvailable();
				online.setVisible(flag);
				offline.setVisible(!flag);
			}
		}, 0, 5000);
	}

	/**
	 * Redirecting to Home page on Logout and destroying Session context
	 * 
	 * @param event
	 *            logout event
	 */
	public void logout(ActionEvent event) {
		auditFactory.audit(AuditEvent.LOGOUT_USER, Components.NAVIGATION, SessionContext.userContext().getUserId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID, "Clearing Session context");

		if (SessionContext.authTokenDTO().getCookie() != null
				&& RegistrationAppHealthCheckUtil.isNetworkAvailable()) {

			serviceDelegateUtil.invalidateToken(SessionContext.authTokenDTO().getCookie());

		}

		logoutCleanUp();
	}

	/**
	 * Logout clean up.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void logoutCleanUp() {

		try {
			ApplicationContext.map().remove(RegistrationConstants.USER_DTO);

			SessionContext.destroySession();
			SchedulerUtil.stopScheduler();

			BorderPane loginpage = BaseController.load(getClass().getResource(RegistrationConstants.INITIAL_PAGE));

			getScene(loginpage);
		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));

			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_LOGOUT_PAGE);
		}
	}

	/**
	 * Redirecting to Home page
	 * 
	 * @param event
	 *            event for redirecting to home
	 */
	public void redirectHome(ActionEvent event) {
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			goToHomePageFromOnboard();
		} else {
			goToHomePageFromRegistration();
		}
	}

	/**
	 * Sync data through batch jobs.
	 *
	 * @param event
	 *            the event
	 */
	public void syncData(ActionEvent event) {

		if (isMachineRemapProcessStarted()) {

			LOGGER.info(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.MACHINE_CENTER_REMAP_MSG);
			return;
		}
		AnchorPane syncData;
		try {
			auditFactory.audit(AuditEvent.NAV_SYNC_DATA, Components.NAVIGATION,
					SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
			executeSyncDataTask();
			while (restartController.isToBeRestarted()) {
				/* Clear the completed job map */
				BaseJob.clearCompletedJobMap();

				/* Restart the application */
				restartController.restart();
			}

			/*
			 * if ("Y".equalsIgnoreCase((String)
			 * ApplicationContext.getInstance().getApplicationMap()
			 * .get(RegistrationConstants.UI_SYNC_DATA))) { syncData =
			 * BaseController.load(getClass().getResource(RegistrationConstants.
			 * SYNC_DATA));
			 * 
			 * VBox pane = (VBox) menu.getParent().getParent().getParent(); Object parent =
			 * pane.getChildren().get(0); pane.getChildren().clear();
			 * pane.getChildren().add((Node) parent); pane.getChildren().add(syncData); }
			 */

		} /*
			 * catch (IOException ioException) {
			 * LOGGER.error(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME,
			 * APPLICATION_ID, ioException.getMessage() +
			 * ExceptionUtils.getStackTrace(ioException));
			 * 
			 * }
			 */ catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}

	}

	/**
	 * Redirecting to PacketStatusSync Page
	 * 
	 * @param event
	 *            event for sync packet status
	 */
	public void syncPacketStatus(ActionEvent event) {
		if (isMachineRemapProcessStarted()) {

			LOGGER.info(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.MACHINE_CENTER_REMAP_MSG);
			return;
		}
		try {
			auditFactory.audit(AuditEvent.SYNC_REGISTRATION_PACKET_STATUS, Components.SYNC_SERVER_TO_CLIENT,
					SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

			AnchorPane syncServerClientRoot = BaseController
					.load(getClass().getResource(RegistrationConstants.SYNC_STATUS));

			if (!validateScreenAuthorization(syncServerClientRoot.getId())) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.AUTHORIZATION_ERROR);
			} else {
				VBox pane = (VBox) (menu.getParent().getParent().getParent());
				for (int index = pane.getChildren().size() - 1; index > 0; index--) {
					pane.getChildren().remove(index);
				}
				pane.getChildren().add(syncServerClientRoot);

			}
		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * This method is to trigger the Pre registration sync service
	 * 
	 * @param event
	 *            event for downloading pre reg data
	 */
	@FXML
	public void downloadPreRegData(ActionEvent event) {
		if (isMachineRemapProcessStarted()) {

			LOGGER.info(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.MACHINE_CENTER_REMAP_MSG);
			return;
		}
		auditFactory.audit(AuditEvent.SYNC_PRE_REGISTRATION_PACKET, Components.SYNC_SERVER_TO_CLIENT,
				SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		ResponseDTO responseDTO = preRegistrationDataSyncService
				.getPreRegistrationIds(RegistrationConstants.JOB_TRIGGER_POINT_USER);

		if (responseDTO.getSuccessResponseDTO() != null) {
			SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
			generateAlertLanguageSpecific(successResponseDTO.getCode(), successResponseDTO.getMessage());

		} else if (responseDTO.getErrorResponseDTOs() != null) {

			ErrorResponseDTO errorresponse = responseDTO.getErrorResponseDTOs().get(0);
			generateAlertLanguageSpecific(errorresponse.getCode(), errorresponse.getMessage());

		}
	}

	public void uploadPacketToServer() {
		if (isMachineRemapProcessStarted()) {

			LOGGER.info(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID,
					RegistrationConstants.MACHINE_CENTER_REMAP_MSG);
			return;
		}
		auditFactory.audit(AuditEvent.SYNC_PRE_REGISTRATION_PACKET, Components.SYNC_SERVER_TO_CLIENT,
				SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		packetHandlerController.uploadPacket();
	}

	public void intiateRemapProcess() {
		
		masterSyncService.getMasterSync(
				RegistrationConstants.OPT_TO_REG_MDS_J00001,
				RegistrationConstants.JOB_TRIGGER_POINT_USER);
		
		if (!isMachineRemapProcessStarted()) {

			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.REMAP_NOT_APPLICABLE);
		}

	}

	@FXML
	public void hasUpdate(ActionEvent event) {

		// Check for updates
		if (hasUpdate()) {

			update(homeController.getMainBox(), packetHandlerController.getProgressIndicator(),
					RegistrationUIConstants.UPDATE_LATER, true);

		}

	}

	private boolean hasUpdate() {
		boolean hasUpdate = false;
		if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
			try {
				if (registrationUpdate.hasUpdate()) {
					hasUpdate = true;
				} else {
					generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.NO_UPDATES_FOUND);

				}

			} catch (RuntimeException | IOException | ParserConfigurationException | SAXException exception) {
				LOGGER.error(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));

				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_FIND_UPDATES);
			}

		} else {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.NO_INTERNET_CONNECTION);
		}
		return hasUpdate;
	}

	private String update() {
		try {

			registrationUpdate.getWithLatestJars();
			return RegistrationConstants.ALERT_INFORMATION;

		} catch (Exception exception) {
			LOGGER.error(LoggerConstants.LOG_REG_HEADER, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			return RegistrationConstants.ERROR;

		}
	}

	private void executeSyncDataTask() {
		progressIndicator = packetHandlerController.getProgressIndicator();
		GridPane gridPane = homeController.getMainBox();
		List<SyncJobDef> syncJobs = masterSyncDao.getSyncJobs();
		double totalJobs = syncJobs.size();
		gridPane.setDisable(true);
		progressIndicator.setVisible(true);
		Service<ResponseDTO> taskService = new Service<ResponseDTO>() {
			@Override
			protected Task<ResponseDTO> createTask() {
				return /**
						 * @author SaravanaKumar
						 *
						 */
				new Task<ResponseDTO>() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see javafx.concurrent.Task#call()
					 */
					@Override
					protected ResponseDTO call() {

						LOGGER.info("REGISTRATION - HANDLE_PACKET_UPLOAD_START - PACKET_UPLOAD_CONTROLLER",
								APPLICATION_NAME, APPLICATION_ID, "Handling all the packet upload activities");

						ResponseDTO responseDto = jobConfigurationService.executeAllJobs();
						double success = 1;
						if (responseDto.getErrorResponseDTOs() == null
								|| responseDto.getErrorResponseDTOs().size() == 0) {
							packetHandlerController.syncProgressBar.setProgress(1);
						} else {
							success = totalJobs - responseDto.getErrorResponseDTOs().size();
							packetHandlerController.syncProgressBar.setProgress(success / totalJobs);
						}
						return responseDto;
					}
				};
			}
		};

		progressIndicator.progressProperty().bind(taskService.progressProperty());
		taskService.start();
		taskService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

				ResponseDTO responseDTO = taskService.getValue();
				if (responseDTO.getErrorResponseDTOs() != null) {
					generateAlert(RegistrationConstants.SYNC_FAILURE,
							responseDTO.getErrorResponseDTOs().get(0).getMessage());
				} else {

					generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.SYNC_SUCCESS);

				}
				gridPane.setDisable(false);
				progressIndicator.setVisible(false);
			}
		});

	}

	public void executeUpdateTask(Pane pane, ProgressIndicator progressIndicator) {

		progressIndicator.setVisible(true);
		pane.setDisable(true);

		/**
		 * This anonymous service class will do the pre application launch task
		 * progress.
		 * 
		 */
		Service<String> taskService = new Service<String>() {
			@Override
			protected Task<String> createTask() {
				return /**
						 * @author SaravanaKumar
						 *
						 */
				new Task<String>() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see javafx.concurrent.Task#call()
					 */
					@Override
					protected String call() {

						LOGGER.info("REGISTRATION - HANDLE_PACKET_UPLOAD_START - PACKET_UPLOAD_CONTROLLER",
								APPLICATION_NAME, APPLICATION_ID, "Handling all the packet upload activities");

						progressIndicator.setVisible(true);
						pane.setDisable(true);
						return update();

					}
				};
			}
		};

		progressIndicator.progressProperty().bind(taskService.progressProperty());
		taskService.start();
		taskService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {

				pane.setDisable(false);
				progressIndicator.setVisible(false);

				if (RegistrationConstants.ERROR.equalsIgnoreCase(taskService.getValue())) {
					// generateAlert(RegistrationConstants.ERROR,
					// RegistrationUIConstants.UNABLE_TO_UPDATE);
					update(pane, progressIndicator, RegistrationUIConstants.UNABLE_TO_UPDATE, true);
				} else if (RegistrationConstants.ALERT_INFORMATION.equalsIgnoreCase(taskService.getValue())) {
					// Update completed Re-Launch application
					generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.UPDATE_COMPLETED);

					restartApplication();
				}

			}

		});

	}

	public void update(Pane pane, ProgressIndicator progressIndicator, String context,
			boolean isPreLaunchTaskToBeStopped) {

		Alert updateAlert = createAlert(AlertType.CONFIRMATION, RegistrationUIConstants.UPDATE_AVAILABLE, null, context,
				RegistrationConstants.UPDATE_NOW_LABEL, RegistrationConstants.UPDATE_LATER_LABEL);

		pane.setDisable(true);
		updateAlert.showAndWait();

		/* Get Option from user */
		ButtonType result = updateAlert.getResult();
		if (result == ButtonType.OK) {

			executeUpdateTask(pane, progressIndicator);
		} else if (result == ButtonType.CANCEL && (statusValidatorService.isToBeForceUpdate())) {
			Alert alert = createAlert(AlertType.INFORMATION, RegistrationUIConstants.UPDATE_AVAILABLE, null,
					RegistrationUIConstants.UPDATE_FREEZE_TIME_EXCEED, RegistrationConstants.UPDATE_NOW_LABEL, null);

			alert.showAndWait();

			/* Get Option from user */
			ButtonType alertResult = alert.getResult();

			if (alertResult == ButtonType.OK) {

				executeUpdateTask(pane, progressIndicator);
			}
		} else {
			pane.setDisable(false);
			if (!isPreLaunchTaskToBeStopped) {
				loginController.executePreLaunchTask(pane, progressIndicator);
				jobConfigurationService.startScheduler();
			}
		}

	}
}