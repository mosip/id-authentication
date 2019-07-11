package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.Writer;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.controller.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Class for showing the Acknowledgement Receipt
 * 
 * @author Himaja Dhanyamraju
 *
 */
@Controller
public class AckReceiptController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(AckReceiptController.class);

	@Autowired
	private PacketHandlerController packetController;

	private Writer stringWriter;

	@FXML
	protected GridPane rootPane;

	@FXML
	private WebView webView;

	@FXML
	private Button newRegistration;

	@FXML
	private Button print;

	@FXML
	private Text registrationNavLabel;

	@FXML
	private Button sendNotification;

	@Autowired
	private SendNotificationController sendNotificationController;

	public void setStringWriter(Writer stringWriter) {
		this.stringWriter = stringWriter;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");

		if (getValueFromApplicationContext(RegistrationConstants.MODE_OF_COMMUNICATION) != null
				&& RegistrationConstants.ENABLE.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.NOTIFICATION_DISABLE_FLAG))) {

			sendNotification.setVisible(true);
		} else {
			sendNotification.setVisible(false);
		}


		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			registrationNavLabel.setText(ApplicationContext.applicationLanguageBundle().getString(RegistrationConstants.UIN_UPDATE_UINUPDATENAVLBL));
		}
		
		if (getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
						.equals(RegistrationConstants.PACKET_TYPE_LOST)) {

			registrationNavLabel.setText(ApplicationContext.applicationLanguageBundle().getString(RegistrationConstants.LOSTUINLBL));
		}

		WebEngine engine = webView.getEngine();
		// loads the generated HTML template content into webview
		engine.loadContent(stringWriter.toString());
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Acknowledgement template has been loaded to webview");
	}

	/**
	 * To print the acknowledgement receipt after packet creation when the user
	 * clicks on print button.
	 * 
	 * @param event
	 *            - the event that happens on click of print button
	 */
	@FXML
	public void printReceipt(ActionEvent event) {
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Printing the Acknowledgement Receipt");

		PrinterJob job = PrinterJob.createPrinterJob();
		if (job != null) {
			job.getJobSettings().setJobName(getRegistrationDTOFromSession().getRegistrationId() + "_Ack");
			webView.getEngine().print(job);
			job.endJob();
		}
		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.PRINT_INITIATION_SUCCESS);
		goToHomePageFromRegistration();
	}

	@FXML
	public void sendNotification(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UI - ACKRECEIPTCONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to Send Notification Popup Window");

		sendNotificationController.init();
	}

	@FXML
	public void goToNewRegistration(ActionEvent event) {
		LOGGER.info("REGISTRATION - UI - ACKRECEIPTCONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to New Registration Page after packet creation");

		clearRegistrationData();
		packetController.createPacket();
	}

}