package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_BIOMETRIC_SCAN_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * {@code FingerPrintScanController} is to scan fingerprint biometrics.
 * 
 * @author Mahesh Kumar
 * @since 1.0
 */
@Controller
public class FingerPrintScanController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(FingerPrintScanController.class);

	/** The fingerprint capture controller. */
	@Autowired
	private FingerPrintCaptureController fpCaptureController;

	/** The selected anchor pane. */
	@FXML
	private AnchorPane selectedAnchorPane;

	/** The finger print scan image. */
	@FXML
	private ImageView fingerPrintScanImage;
	@FXML
	private Label popupTitle;

	/**
	 * @param popupTitle the popupTitle to set
	 */
	public void setPopupTitle(String popupTitle) {
		this.popupTitle.setText(popupTitle);
	}

	/** The primary stage. */
	private Stage primarystage;

	/** The fingerprint details DTOs for validation. */
	private List<FingerprintDetailsDTO> fingerprintDetailsDTOs = null;

	/** The fingerprint details DTOs. */
	private List<FingerprintDetailsDTO> fingerprintDTOs = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Loading of FingerprintCapture screen started");

		fingerprintDetailsDTOs = new ArrayList<>();
		fingerprintDTOs = new ArrayList<>();

		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Loading of FingerprintCapture screen ended");

	}

	/**
	 * {@code init} method is to collect the values passed from
	 * {@link FingerPrintCaptureController}
	 *
	 * @param selectedPane
	 * @param stage
	 * @param detailsDTOs
	 */
	public void init(AnchorPane selectedPane, Stage stage, List<FingerprintDetailsDTO> fpDetailsDTOs,
			List<FingerprintDetailsDTO> fpDTOs) {
		selectedAnchorPane = selectedPane;
		primarystage = stage;
		fingerprintDetailsDTOs = fpDetailsDTOs;
		fingerprintDTOs = fpDTOs;
		popupTitle.setText("Fingerprint");
	}

	/**
	 * This method scans the biometric of the individual
	 */
	@FXML
	private void scan() {
		try {
			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of biometric details for user registration");

			if (popupTitle.getText().equalsIgnoreCase("Fingerprint")) {
				scanFinger();
			} else {
				scanIris();
			}

			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of biometric details for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("Exception while scanning biometric details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * {@code ScanFinger} is to scan the fingers.
	 * 
	 * 
	 */
	private void scanFinger() {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Scan Finger has started");

		if (selectedAnchorPane.getId() == fpCaptureController.getLeftHandPalmPane().getId()) {

			readFingerPrints("src/main/resources/FINGER PRINTS/LEFT HAND");

			Image img = new Image(
					new ByteArrayInputStream(getImageBytes("src/main/resources/FINGER PRINTS/LeftPalm.jpg")));

			fingerPrintScanImage.setImage(img);

			FingerprintDetailsDTO fpDetailsDTO = new FingerprintDetailsDTO();

			fpDetailsDTO.setFingerPrint(getImageBytes("src/main/resources/FINGER PRINTS/LeftPalm.jpg"));
			fpDetailsDTO.setFingerprintImageName("LeftPalm.jpg");
			fpDetailsDTO.setFingerType("LeftPalm");
			fpDetailsDTO.setForceCaptured(false);
			fpDetailsDTO.setNumRetry(2);
			fpDetailsDTO.setQualityScore(85.0);

			fingerprintDTOs.add(fpDetailsDTO);

			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
			primarystage.close();
			fpCaptureController.getLeftHandPalmImageview().setImage(img);
			fpCaptureController.getLeftSlapQualityScore().setText(String.valueOf(fpDetailsDTO.getQualityScore()) + "%");

		} else if (selectedAnchorPane.getId() == fpCaptureController.getRightHandPalmPane().getId()) {

			readFingerPrints("src/main/resources/FINGER PRINTS/RIGHT HAND");

			Image img = new Image(
					new ByteArrayInputStream(getImageBytes("src/main/resources/FINGER PRINTS/rightPalm.jpg")));
			fingerPrintScanImage.setImage(img);
			FingerprintDetailsDTO fpDetailsDTO = new FingerprintDetailsDTO();

			fpDetailsDTO.setFingerPrint(getImageBytes("src/main/resources/FINGER PRINTS/rightPalm.jpg"));
			fpDetailsDTO.setFingerprintImageName("RightPalm.jpg");
			fpDetailsDTO.setFingerType("RightPalm");
			fpDetailsDTO.setForceCaptured(false);
			fpDetailsDTO.setNumRetry(2);
			fpDetailsDTO.setQualityScore(85.0);

			fingerprintDTOs.add(fpDetailsDTO);

			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
			primarystage.close();
			fpCaptureController.getRightHandPalmImageview().setImage(img);
			fpCaptureController.getRightSlapQualityScore()
					.setText(String.valueOf(fpDetailsDTO.getQualityScore()) + "%");

		} else if (selectedAnchorPane.getId() == fpCaptureController.getThumbPane().getId()) {

			readFingerPrints("src/main/resources/FINGER PRINTS/THUMB");

			Image img = new Image(
					new ByteArrayInputStream(getImageBytes("src/main/resources/FINGER PRINTS/thumb.jpg")));
			fingerPrintScanImage.setImage(img);

			FingerprintDetailsDTO fpDetailsDTO = new FingerprintDetailsDTO();

			fpDetailsDTO.setFingerPrint(getImageBytes("src/main/resources/FINGER PRINTS/thumb.jpg"));
			fpDetailsDTO.setFingerprintImageName("thumb.jpg");
			fpDetailsDTO.setFingerType("BothThumbs");
			fpDetailsDTO.setForceCaptured(false);
			fpDetailsDTO.setNumRetry(1);
			fpDetailsDTO.setQualityScore(85.0);

			fingerprintDTOs.add(fpDetailsDTO);

			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.FP_CAPTURE_SUCCESS);
			primarystage.close();
			fpCaptureController.getThumbImageview().setImage(img);
			fpCaptureController.getThumbsQualityScore().setText(String.valueOf(fpDetailsDTO.getQualityScore()) + "%");

		}
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Scan Finger has ended");
	}

	/**
	 * {@code readFingerPrints} is to read the scanned fingerprints.
	 * 
	 * @param path
	 */
	private void readFingerPrints(String path) {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Reading scanned Finger has started");

		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
			paths.filter(Files::isRegularFile).forEach(e -> {
				File file = e.getFileName().toFile();
				if (file.getName().equals("ISOTemplate.iso")) {
					try {

						FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
						byte[] allBytes = Files.readAllBytes(e.toAbsolutePath());

						fingerprintDetailsDTO.setFingerPrint(allBytes);
						fingerprintDetailsDTO.setFingerType(e.toFile().getParentFile().getName());
						fingerprintDetailsDTO.setFingerprintImageName(e.toFile().getParentFile().getName());
						fingerprintDetailsDTO.setNumRetry(1);
						fingerprintDetailsDTO.setForceCaptured(false);
						fingerprintDetailsDTO.setQualityScore(90);

						fingerprintDetailsDTOs.add(fingerprintDetailsDTO);

					} catch (IOException ioException) {
						LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
								ioException.getMessage());
					}
				}
			});
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());

		}
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Reading scanned Finger has ended");
	}

	/**
	 * event class to exit from present pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow(ActionEvent event) {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Exit window has been called");
		primarystage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		primarystage.close();

	}

	private void scanIris() {

		try {
			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration");

			LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("Exception while scanning iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}

	}

	private byte[] getImageBytes(String filePath) {
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Converting scanned image to bytes started");
		File fi = new File(filePath);
		byte[] fileContent = null;
		try {
			fileContent = Files.readAllBytes(fi.toPath());
		} catch (IOException ioException) {
			LOGGER.error(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());

		}
		LOGGER.debug(LOG_REG_BIOMETRIC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
				"Converting scanned image to bytes ended");
		return fileContent;
	}

}
