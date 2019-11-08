package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

	@FXML
	private ImageView sendNotificationImageView;

	@Autowired
	private SendNotificationController sendNotificationController;

	public void setStringWriter(Writer stringWriter) {
		this.stringWriter = stringWriter;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("REGISTRATION - UI - ACK_RECEIPT_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");

		setImagesOnHover();
		String notificationType = getValueFromApplicationContext(RegistrationConstants.MODE_OF_COMMUNICATION); 
		if (notificationType != null && !notificationType.trim().isEmpty() && !notificationType.equals("NONE")) {

			sendNotification.setVisible(true);
		} else {
			sendNotification.setVisible(false);
		}

		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			registrationNavLabel.setText(ApplicationContext.applicationLanguageBundle()
					.getString(RegistrationConstants.UIN_UPDATE_UINUPDATENAVLBL));
		}

		if (getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
						.equals(RegistrationConstants.PACKET_TYPE_LOST)) {

			registrationNavLabel.setText(
					ApplicationContext.applicationLanguageBundle().getString(RegistrationConstants.LOSTUINLBL));
		}

		WebEngine engine = webView.getEngine();
		// loads the generated HTML template content into webview
		engine.loadContent(stringWriter.toString());
		LOGGER.info("REGISTRATION - UI - ACK-RECEIPT_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Acknowledgement template has been loaded to webview");
	}

	private void setImagesOnHover() {
		Image sendEmailInWhite = new Image(
				getClass().getResourceAsStream(RegistrationConstants.SEND_EMAIL_FOCUSED_IMAGE_PATH));
		Image sendEmailImage = new Image(getClass().getResourceAsStream(RegistrationConstants.SEND_EMAIL_IMAGE_PATH));

		sendNotification.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				sendNotificationImageView.setImage(sendEmailInWhite);
			} else {
				sendNotificationImageView.setImage(sendEmailImage);
			}
		});
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
		LOGGER.info("REGISTRATION - UI - ACK_RECEIPT_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
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
		LOGGER.debug("REGISTRATION - UI - ACK_RECEIPT_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to Send Notification Popup Window");

		sendNotificationController.init();
	}

	@FXML
	public void goToNewRegistration(ActionEvent event) {
		LOGGER.info("REGISTRATION - UI - ACK_RECEIPT_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to New Registration Page after packet creation");

		clearRegistrationData();
		packetController.createPacket();
	}
	
	/**
	 * Go to home ack template.
	 */
	public void goToHomeAckTemplate() {
		try {
			BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
			if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
				clearOnboardData();
				clearRegistrationData();
			} else {
				SessionContext.map().put(RegistrationConstants.ISPAGE_NAVIGATION_ALERT_REQ,
						RegistrationConstants.ENABLE);
			}
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI - ACK_RECEIPT_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_HOME_PAGE);
		} catch (RuntimeException runtimException) {
			LOGGER.error("REGISTRATION - UI - ACK_RECEIPT_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					runtimException.getMessage() + ExceptionUtils.getStackTrace(runtimException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_HOME_PAGE);
		}

	}

}