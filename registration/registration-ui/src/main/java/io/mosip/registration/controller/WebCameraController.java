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
import io.mosip.registration.service.PhotoCaptureService;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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
	private Button save;

	@FXML
	private Button cancel;

	@Autowired
	private RegistrationController registrationController;

	@Autowired
	private PhotoCaptureService photoCaptureService;

	private BufferedImage bufferedImage = null;

	private Image capture;

	private Webcam webcam;

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
			generateAlert(RegistrationConstants.WEBCAM_ALERT_TITLE, AlertType.ERROR,
					RegistrationConstants.WEBCAM_ALERT_HEADER, RegistrationConstants.WEBCAM_ALERT_CONTEXT);
			((Stage) demographicPane.getScene().getWindow()).close();
		}
	}

	@FXML
	public void saveImage(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"saving the image taken from webcam");
		if (registrationController.captureExceptionImage.isDisabled()) {
			registrationController.applicantImage.setImage(capture);
			registrationController.applicantBufferedImage=bufferedImage;
			registrationController.captureImage.setDisable(true);
			registrationController.captureExceptionImage.setDisable(false);
		} else {
			registrationController.exceptionImage.setImage(capture);
			registrationController.exceptionBufferedImage=bufferedImage;
			registrationController.captureImage.setDisable(false);
			registrationController.exceptionImage.setDisable(true);
		}
		registrationController.saveBiometricDetails.setDisable(false);
		LOGGER.debug("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"closing the webcam");
		photoCaptureService.close(webcam);
		Stage stage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		stage.close();
	}

	@FXML
	public void captureImage(ActionEvent event) {
		LOGGER.debug("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"capturing the image from webcam");
		if (bufferedImage != null) {
			bufferedImage.flush();
		}			
		bufferedImage = photoCaptureService.captureImage(webcam);

		capture = SwingFXUtils.toFXImage(bufferedImage, null);
		if (registrationController.captureExceptionImage.isDisabled()) {
			registrationController.applicantImage.setImage(capture);
		} else {
			registrationController.exceptionImage.setImage(capture);
		}
		save.setDisable(false);
		cancel.setDisable(false);
	}

	@FXML
	public void cancelImage(ActionEvent event) {		
		if (registrationController.captureExceptionImage.isDisabled()) {
			registrationController.applicantImage.setImage(null);
		} else {
			registrationController.exceptionImage.setImage(null);
		}
		save.setDisable(true);
		cancel.setDisable(true);
	}
}
