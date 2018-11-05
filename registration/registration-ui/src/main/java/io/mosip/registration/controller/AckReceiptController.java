package io.mosip.registration.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.PacketHandlerService;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
public class AckReceiptController extends BaseController implements Initializable{
	
	@Autowired
	private PacketHandlerService packetHandlerService;
	@Autowired
	private RegistrationOfficerDetailsController officerDetailsController;
	
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
        	ImageIO.write(SwingFXUtils.fromFXImage(ackImage, null), RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);
        	acknowledgement = byteArrayOutputStream.toByteArray();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorCode(), RegistrationExceptions.REG_ACK_TEMPLATE_IO_EXCEPTION.getErrorMessage());
		}
        
        registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceipt(acknowledgement);
        
        registrationData.getDemographicDTO().getApplicantDocumentDTO().setAcknowledgeReceiptName(registrationData.getRegistrationId()+"_Ack."+RegistrationConstants.IMAGE_FORMAT);
        ResponseDTO response = packetHandlerService.handle(registrationData);
        
        generateAlert("Success",AlertType.INFORMATION, "Packet Created Successfully!");
      //Adding individual address to session context
        if(response.getSuccessResponseDTO().getCode().equals("Success")) {
             AddressDTO addressDTO = registrationData.getDemographicDTO().getDemoInLocalLang().getAddressDTO();           
             Map<String, Object> addr= SessionContext.getInstance().getMapObject();
             addr.put("PrevAddress", addressDTO);
             SessionContext.getInstance().setMapObject(addr);
        } 

        Stage stage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
        stage.close();
        
        officerDetailsController.redirectHome(event);
	}
}
