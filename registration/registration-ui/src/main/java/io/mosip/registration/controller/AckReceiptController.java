package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.entity.GlobalContextParam;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.GlobalContextParamService;
import io.mosip.registration.service.NotificationService;
import io.mosip.registration.service.PacketHandlerService;
import io.mosip.registration.service.TemplateService;
import io.mosip.registration.util.acktemplate.VelocityPDFGenerator;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Class for showing the Acknowledgement Receipt
 * 
 * @author Himaja Dhanyamraju
 *
 */
@Controller
public class AckReceiptController extends BaseController implements Initializable {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationOfficerPacketController.class);

	@Autowired
	private PacketHandlerService packetHandlerService;
	@Autowired
	private RegistrationController registrationController;
	@Autowired
	private TemplateService templateService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private GlobalContextParamService globalContextParamService;

	private VelocityPDFGenerator velocityGenerator = new VelocityPDFGenerator();

	private RegistrationDTO registrationData;
	private Writer stringWriter;

	@FXML
	private WebView webView;

	private WebEngine engine;

	public RegistrationDTO getRegistrationData() {
		return registrationData;
	}

	public void setRegistrationData(RegistrationDTO registrationData) {
		this.registrationData = registrationData;
	}

	/**
	 * @return the stringWriter
	 */
	public Writer getStringWriter() {
		return stringWriter;
	}

	/**
	 * @param stringWriter the stringWriter to set
	 */
	public void setStringWriter(Writer stringWriter) {
		this.stringWriter = stringWriter;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		if (RegistrationAppHealthCheckUtil.isNetworkAvailable()) {
			List<GlobalContextParam> globalContextParam = globalContextParamService
					.findInvalidLoginCount(RegistrationConstants.MODE_OF_COMMUNICATION);
			if (!globalContextParam.isEmpty() && globalContextParam.get(0).getVal() !=null && !globalContextParam.get(0).getVal().equals("NONE")) {
				ResponseDTO responseDTO = null;
				String notificationTemplate = "";
				try {
					notificationTemplate = templateService.getHtmlTemplate(RegistrationConstants.NOTIFICATION_TEMPLATE);
				} catch (RegBaseCheckedException regBaseCheckedException) {
					LOGGER.error("REGISTRATION - ACK RECEIPT CONTROLLER ", APPLICATION_NAME, APPLICATION_ID,
							regBaseCheckedException.getMessage());
				}
				if (!notificationTemplate.isEmpty()) {
					Writer writeNotificationTemplate = velocityGenerator
							.generateNotificationTemplate(notificationTemplate, getRegistrationData());
					
					String number = getRegistrationData().getDemographicDTO().getDemoInUserLang().getMobile();
					String rid = getRegistrationData() == null ? "RID" : getRegistrationData().getRegistrationId();
					if (!number.isEmpty() && globalContextParam.get(0).getVal().contains("SMS")) {
						responseDTO = notificationService.sendSMS(writeNotificationTemplate.toString(), number,rid);
						if (responseDTO != null && responseDTO.getErrorResponseDTOs() != null
								&& responseDTO.getErrorResponseDTOs().get(0) != null) {
							generateAlert(responseDTO);
						}
					}

					String emailId = getRegistrationData().getDemographicDTO().getDemoInUserLang().getEmailId();

					if (!emailId.isEmpty() && globalContextParam.get(0).getVal().contains("EMAIL")) { 
						responseDTO = notificationService.sendEmail(writeNotificationTemplate.toString(), emailId,rid);
						if (responseDTO != null && responseDTO.getErrorResponseDTOs() != null
								&& responseDTO.getErrorResponseDTOs().get(0) != null) {
							generateAlert(responseDTO);
						}
					}
				}
			}

		}
		engine = webView.getEngine();
		engine.loadContent(stringWriter.toString());
	}

	@FXML
	public void saveReceipt(ActionEvent event) throws RegBaseCheckedException {
		WritableImage ackImage = webView.snapshot(null, null);

		byte[] acknowledgement;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(ackImage, null), RegistrationConstants.IMAGE_FORMAT,
					byteArrayOutputStream);
			acknowledgement = byteArrayOutputStream.toByteArray();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptions.REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorMessage());
		}

		registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceipt(acknowledgement);

		registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceiptName(
				registrationData.getRegistrationId() + "_Ack." + RegistrationConstants.IMAGE_FORMAT);
		ResponseDTO response = packetHandlerService.handle(registrationData);

		generateAlert("Success", AlertType.INFORMATION, "Packet Created Successfully!");
		// Adding individual address to session context
		if (response.getSuccessResponseDTO() != null && response.getSuccessResponseDTO().getMessage().equals("Success")) {
			AddressDTO addressDTO = registrationData.getDemographicDTO().getDemoInUserLang().getAddressDTO();
			Map<String, Object> addr = SessionContext.getInstance().getMapObject();
			addr.put("PrevAddress", addressDTO);
			SessionContext.getInstance().setMapObject(addr);
		}

		Stage stage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		stage.close();

		registrationController.goToHomePage();
	}

	private void generateAlert(ResponseDTO responseDTO) {
		/* Get error response */
		ErrorResponseDTO errorResponseDTO = responseDTO.getErrorResponseDTOs().get(0);
		/* Generate Alert */
		generateAlert(RegistrationConstants.MACHINE_MAPPING_CODE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
				errorResponseDTO.getMessage());
	}

}
