package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.service.device.PhotoCaptureService;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Class for Opening Web Camera
 *
 * @author Himaja Dhanyamraju
 */
@Controller
public class WebCameraController extends BaseController implements Initializable {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(WebCameraController.class);

	@FXML
	private AnchorPane demographicPane;

	@FXML
	private SwingNode webcamera;

	@FXML
	private Button clear;
	
	@FXML
	private Button close;

	private BaseController parentController = new BaseController();

	@Autowired
	private PhotoCaptureService photoCaptureService;

	private BufferedImage capturedImage = null;

	private Webcam webcam;
	private String imageType;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");
		if (webcam != null) {
			photoCaptureService.close(webcam);
		}
		LOGGER.debug("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Connecting to the webcam");
		webcam = photoCaptureService.connect(640, 480);
		if (webcam != null) {
			WebcamPanel cameraPanel = new WebcamPanel(webcam);
			JPanel jPanelWindow = new JPanel();
			jPanelWindow.add(cameraPanel);
			jPanelWindow.setVisible(true);
			webcamera.setContent(jPanelWindow);
		} else {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.WEBCAM_ALERT_CONTEXT);
			((Stage) demographicPane.getScene().getWindow()).close();
		}
	}

	public void init(BaseController parentController, String imageType) {
		LOGGER.debug("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Initializing the controller to be used and imagetype to be captured");
		
		this.parentController = parentController;
		this.imageType = imageType;
	}

	@FXML
	public void captureImage(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"capturing the image from webcam");
		if (capturedImage != null) {
			capturedImage.flush();
		}			
		capturedImage = photoCaptureService.captureImage(webcam);		
		parentController.saveApplicantPhoto(capturedImage, imageType);
		
		clear.setDisable(false);
	}

	@FXML
	public void clearImage(ActionEvent event) {	
		LOGGER.debug("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"clearing the image from webcam");
		
		parentController.clearPhoto(imageType);
		clear.setDisable(true);		
	}
	
	@FXML
	public void closeWindow(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"closing the webcam window");
		
		photoCaptureService.close(webcam);
		Stage stage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		stage.close();
	}
}
