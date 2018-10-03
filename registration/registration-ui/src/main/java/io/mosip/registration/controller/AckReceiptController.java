package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationUIExceptionEnum.REG_ACK_TEMPLATE_IO_EXCEPTION;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.PacketHandlerService;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

@Controller
public class AckReceiptController extends BaseController implements Initializable{
	
	@Autowired
	PacketHandlerService packetHandlerService;
	
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
		engine = webView.getEngine(); 
		engine.loadContent(stringWriter.toString());		
	}
	
	@FXML
	public void saveReceipt(ActionEvent event) throws RegBaseCheckedException {
		WritableImage ackImage = webView.snapshot(null, null);

		byte[] acknowledgement; 
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
        	ImageIO.write(SwingFXUtils.fromFXImage(ackImage, null), RegConstants.IMAGE_FORMAT, byteArrayOutputStream);
        	acknowledgement = byteArrayOutputStream.toByteArray();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorCode(), REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorMessage());
		}
        
        registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceipt(acknowledgement);
        String acknowledgementReceiptName = registrationData.getRegistrationId().replaceAll("[^0-9]", "")+"_Ack";
        
        registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceiptName(acknowledgementReceiptName);
        packetHandlerService.handle(registrationData);
        
        generateAlert("Success",AlertType.INFORMATION, "Packet Created Successfully!");
	}
}
