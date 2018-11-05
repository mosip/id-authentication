package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_AUTHORIZATION_EXCEPTION;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_HOMEPAGE_IO_EXCEPTION;

import java.io.IOException;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.scheduler.SchedulerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Class for Registration Officer details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Controller
public class RegistrationOfficerDetailsController extends BaseController {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final MosipLogger LOGGER = AppConfig.getLogger(RegistrationOfficerDetailsController.class);

	@FXML
	private Label registrationOfficerName;

	@FXML
	private Label registrationOfficeId;

	@FXML
	private Label registrationOfficeLocation;

	@FXML
	private MenuBar menu;

	/**
	 * Mapping Registration Officer details
	 */
	public void initialize() {

		LOGGER.debug("REGISTRATION - OFFICER_DETAILS - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
				APPLICATION_NAME, APPLICATION_ID,
				"Displaying Registration Officer details");

		SessionContext sessionContext = SessionContext.getInstance();
		registrationOfficerName.setText(sessionContext.getUserContext().getName());
		registrationOfficeId
				.setText(sessionContext.getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterId());
		registrationOfficeLocation
				.setText(sessionContext.getUserContext().getRegistrationCenterDetailDTO().getRegistrationCenterName());
		menu.setBackground(Background.EMPTY);
		menu.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
	}

	/**
	 * Redirecting to Home page on Logout and destroying Session context
	 */
	public void logout(ActionEvent event) {
		try {
			String initialMode = SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.LOGIN_INITIAL_SCREEN).toString();

			LOGGER.debug("REGISTRATION - LOGOUT - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
					APPLICATION_NAME, APPLICATION_ID, "Clearing Session context");

			SessionContext.destroySession();
			SchedulerUtil.stopScheduler();

			String fxmlPath = null;
			switch (initialMode) {
			case RegistrationConstants.OTP:
				fxmlPath = RegistrationConstants.LOGIN_OTP_PAGE;
				break;
			case RegistrationConstants.LOGIN_METHOD_PWORD:
				fxmlPath = RegistrationConstants.LOGIN_PWORD_PAGE;
				break;
			default:
				fxmlPath = RegistrationConstants.LOGIN_PWORD_PAGE;
			}

			BorderPane loginpage = BaseController.load(getClass().getResource(RegistrationConstants.INITIAL_PAGE));
			AnchorPane loginType = BaseController.load(getClass().getResource(fxmlPath));
			loginpage.setCenter(loginType);
			RegistrationAppInitialization.getScene().setRoot(loginpage);

		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI - Logout ", APPLICATION_NAME,
					APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * Redirecting to Home page
	 */
	public void redirectHome(ActionEvent event) {
		try {

			LOGGER.debug("REGISTRATION - REDIRECT_HOME - REGISTRATION_OFFICER_DETAILS_CONTROLLER",
					APPLICATION_NAME, APPLICATION_ID, "Redirecting to Home page");

			VBox homePage = BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
			RegistrationAppInitialization.getScene().setRoot(homePage);

		} catch (IOException | RuntimeException exception) {
			generateAlert(RegistrationConstants.ALERT_ERROR, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					REG_UI_HOMEPAGE_IO_EXCEPTION.getErrorMessage());
		}
	}

	/**
	 * change On-Board user Perspective
	 * 
	 * @param event
	 *            is an action event
	 * @throws IOException
	 */
	public void onBoardUser(ActionEvent event) throws IOException {
		AnchorPane onBoardRoot = BaseController
				.load(getClass().getResource(RegistrationConstants.USER_MACHINE_MAPPING));

		if (!validateScreenAuthorization(onBoardRoot.getId())) {
			generateAlert(RegistrationConstants.AUTHORIZATION_ALERT_TITLE,
					AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.AUTHORIZATION_INFO_MESSAGE,
					REG_UI_AUTHORIZATION_EXCEPTION.getErrorMessage());
		} else {
			VBox pane = (VBox) menu.getParent().getParent().getParent();
			Object parent = pane.getChildren().get(0);
			pane.getChildren().clear();
			pane.getChildren().add((Node) parent);
			pane.getChildren().add(onBoardRoot);

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
				generateAlert(RegistrationConstants.AUTHORIZATION_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.AUTHORIZATION_INFO_MESSAGE,
						REG_UI_AUTHORIZATION_EXCEPTION.getErrorMessage());
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

}