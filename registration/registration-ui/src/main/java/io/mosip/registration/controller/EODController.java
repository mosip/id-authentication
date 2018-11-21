package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.layout.AnchorPane;

/**
 * The EODController is controller for.
 */
@Controller
public class EODController extends BaseController implements Initializable {

	@Autowired
	private RegistrationPendingActionController pendingActionController;
	/** The approval accordian. */
	@FXML
	private Accordion approvalAccordian;

	/** The pending approval anchor pane. */
	@FXML
	private AnchorPane pendingApprovalAnchorPane;

	/** The pending action anchor pane. */
	@FXML
	private AnchorPane pendingActionAnchorPane;

	/** The re Register anchor pane. */
	@FXML
	private AnchorPane reRegisterAnchorPane;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationOfficerPacketController.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {

			Parent pendingActionRoot = BaseController
					.load(getClass().getResource("/fxml/RegistrationPendingAction.fxml"));

			Parent pendingApprovalRoot = BaseController
					.load(getClass().getResource("/fxml/RegistrationPendingApproval.fxml"));

			Parent reRegisterRoot = BaseController.load(getClass().getResource("/fxml/ReRegistration.fxml"));

			ObservableList<Node> approvalNodes = pendingApprovalAnchorPane.getChildren();
			approvalNodes.add(pendingApprovalRoot);

			ObservableList<Node> actionNodes = pendingActionAnchorPane.getChildren();
			actionNodes.add(pendingActionRoot);

			ObservableList<Node> reregisterNodes = reRegisterAnchorPane.getChildren();
			reregisterNodes.add(reRegisterRoot);

		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - EOD - CONTROLLER", APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}

	}

	public void loadPendingActionController() {
		pendingActionController.reloadTableView();
	}

}
