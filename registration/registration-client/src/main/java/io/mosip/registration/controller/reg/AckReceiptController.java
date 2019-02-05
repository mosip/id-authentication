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
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.RegistrationDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
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

	private RegistrationDTO registrationData;
	
	private Writer stringWriter;
	
	@FXML
	protected AnchorPane rootPane;

	@FXML
	private WebView webView;

	@FXML
	private Button newRegistration;

	@FXML
	private Button print;
	
	@FXML
	private Button back;

	@FXML
	private Text registrationNavLabel;

	@Autowired
	private SendNotificationController sendNotificationController;
	
	public RegistrationDTO getRegistrationData() {
		return registrationData;
	}

	public void setRegistrationData(RegistrationDTO registrationData) {
		this.registrationData = registrationData;
	}

	public void setStringWriter(Writer stringWriter) {
		this.stringWriter = stringWriter;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - UI - ACKRECEIPTCONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");

		if (getRegistrationData().getSelectionListDTO() != null) {
			registrationNavLabel.setText(RegistrationConstants.UIN_NAV_LABEL);
			newRegistration.setVisible(false);
		}

		WebEngine engine = webView.getEngine();
		// loads the generated HTML template content into webview
		engine.loadContent(stringWriter.toString());
		LOGGER.debug("REGISTRATION - UI - ACKRECEIPTCONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Acknowledgement template has been loaded to webview");
	}

	/**
	 * To print the acknowledgement receipt after packet creation when the user
	 * clicks on print button.
	 */
	@FXML
	public void printReceipt(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UI - ACKRECEIPTCONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Printing the Acknowledgement Receipt");

		PrinterJob job = PrinterJob.createPrinterJob();
		if (job != null) {
			job.getJobSettings().setJobName(getRegistrationData().getRegistrationId()+"_Ack");
			webView.getEngine().print(job);
			job.endJob();
		}
		generateAlert(RegistrationConstants.SUCCESS, RegistrationUIConstants.PRINT_INITIATION_SUCCESS);
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
		LOGGER.debug("REGISTRATION - UI - ACKRECEIPTCONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to New Registration Page after packet creation");

		clearRegistrationData();
		packetController.createPacket();
	}
}