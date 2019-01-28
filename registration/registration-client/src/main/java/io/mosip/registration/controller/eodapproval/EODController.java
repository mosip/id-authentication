package io.mosip.registration.controller.eodapproval;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_EOD_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

/**
 * The EODController is controller for.
 * 
 * @author Mahesh Kumar
 * @since 1.0
 */
@Controller
public class EODController extends BaseController implements Initializable {

	/** The approval accordion. */
	@FXML
	private Accordion approvalAccordion;

	@FXML
	private TitledPane pendingApprovalTitledPane;

	@FXML
	private TitledPane reRegisterTitledPane;

	/** The pending approval anchor pane. */
	@FXML
	private AnchorPane pendingApprovalAnchorPane;

	/** The re Register anchor pane. */
	@FXML
	private AnchorPane reRegisterAnchorPane;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = AppConfig.getLogger(EODController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		LOGGER.debug(LOG_REG_EOD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");
		try {

			Parent pendingApprovalRoot = BaseController
					.load(getClass().getResource(RegistrationConstants.PENDING_APPROVAL_PAGE));

			Parent reRegisterRoot = BaseController
					.load(getClass().getResource(RegistrationConstants.REREGISTRATION_PAGE));

			pendingApprovalAnchorPane.getChildren().add(pendingApprovalRoot);
			reRegisterAnchorPane.getChildren().add(reRegisterRoot);

		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_EOD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
		LOGGER.debug(LOG_REG_EOD_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been ended");
	}

	/**
	 * @return the pendingApprovalTitledPane
	 */
	public TitledPane getPendingApprovalTitledPane() {
		return pendingApprovalTitledPane;
	}

	/**
	 * @param pendingApprovalTitledPane the pendingApprovalTitledPane to set
	 */
	public void setPendingApprovalTitledPane(TitledPane pendingApprovalTitledPane) {
		this.pendingApprovalTitledPane = pendingApprovalTitledPane;
	}

	/**
	 * @return the reRegisterTitledPane
	 */
	public TitledPane getReRegisterTitledPane() {
		return reRegisterTitledPane;
	}

	/**
	 * @param reRegisterTitledPane the reRegisterTitledPane to set
	 */
	public void setReRegisterTitledPane(TitledPane reRegisterTitledPane) {
		this.reRegisterTitledPane = reRegisterTitledPane;
	}
}
