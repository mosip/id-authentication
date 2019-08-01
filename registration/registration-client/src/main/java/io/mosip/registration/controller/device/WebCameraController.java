package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.device.webcam.IMosipWebcamService;
import io.mosip.registration.device.webcam.PhotoCaptureFacade;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
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
	public GridPane webCameraPane;

	@FXML
	private SwingNode webcamera;

	@FXML
	protected Button capture;

	@FXML
	private Button clear;

	@FXML
	private Button close;

	private BaseController parentController = null;

	private BufferedImage capturedImage = null;

	private IMosipWebcamService photoProvider = null;
	@Autowired
	private PhotoCaptureFacade photoCaptureFacade;

	private String imageType;
	
	private Stage webCameraStage;
	
	public Stage getWebCameraStage() {
		return webCameraStage;
	}

	public void setWebCameraStage(Stage webCameraStage) {
		this.webCameraStage = webCameraStage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");

		JPanel jPanelWindow = photoProvider.getCameraPanel();
		webcamera.setContent(jPanelWindow);
	}

	public void init(BaseController parentController, String imageType) {
		LOGGER.info("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Initializing the controller to be used and imagetype to be captured");

		this.parentController = parentController;
		this.imageType = imageType;
	}

	public boolean isWebcamPluggedIn() {
		LOGGER.info("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Connecting to the webcam");

		photoProvider = photoCaptureFacade
				.getPhotoProviderFactory(getValueFromApplicationContext(RegistrationConstants.WEBCAM_LIBRARY_NAME));

		if (!photoProvider.isWebcamConnected()) {
			photoProvider.connect(640, 480);
		}
		return photoProvider.isWebcamConnected();
	}

	@FXML
	public void captureImage(ActionEvent event) {
		LOGGER.info("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"capturing the image from webcam");
		if (capturedImage != null) {
			capturedImage.flush();
		}
		capturedImage = photoProvider.captureImage();
		parentController.saveApplicantPhoto(capturedImage, imageType);
		parentController.calculateRecaptureTime(imageType);
		capture.setDisable(true);

		clear.setDisable(false);
	}

	@FXML
	public void clearImage(ActionEvent event) {
		LOGGER.info("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"clearing the image from webcam");

		parentController.clearPhoto(imageType);
		clear.setDisable(true);
	}

	@FXML
	public void closeWindow(ActionEvent event) {
		LOGGER.info("REGISTRATION - UI - WEB_CAMERA_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"closing the webcam window");
		if (capturedImage != null) {
			capturedImage.flush();
		}
		Stage stage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		stage.close();
	}

	public void closeWebcam() {
		if (photoProvider != null) {
			photoProvider.close();
		}
	}
}
