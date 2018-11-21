package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.machinezoo.sourceafis.FingerprintTemplate;

import MFS100.FingerData;
import MFS100.MFS100;
import MFS100.MFS100Event;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.service.LoginService;
import io.mosip.registration.util.biometric.FingerprintProvider;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

@Controller
public class FingerPrintAuthenticationController extends BaseController implements MFS100Event, Initializable {

	@FXML
	private AnchorPane authenticateRootPane;
	@FXML
	private AnchorPane authenticateRootSubPane;
	@FXML
	private AnchorPane leftPalmAnchorPane;
	@FXML
	private AnchorPane rightPalmAnchorPane;
	@FXML
	private ImageView leftPalmImageView;
	@FXML
	private ImageView rightPalmImageView;
	@FXML
	private Button scanBtn;
	@FXML
	private ProgressIndicator scanProgress;

	@FXML
	private ComboBox<String> deviceCmbBox;
	@FXML
	private ImageView fingerScannedImage;

	@Autowired
	LoginService loginService;

	@Value("${FINGER_PRINT_SCORE}")
	private long fingerPrintScore;

	private FingerprintProvider fingerprintProvider = new FingerprintProvider();

	/**
	 * Instance of {@link MosipLogger}
	 */
	private Logger LOGGER = AppConfig.getLogger(FingerPrintAuthenticationController.class);

	/**
	 * Stage
	 */
	private Stage primarystage;

	@Value("${QUALITY_SCORE}")
	private int qualityScore;

	@Value("${CAPTURE_TIME_OUT}")
	private int captureTimeOut;

	private MFS100 fpDevice = new MFS100(this, "");

	private String minutia = "";

	@Autowired
	BaseController baseController;

	public void init(BaseController parentControllerObj) throws IOException {
		baseController = parentControllerObj;
	}

	private FingerData fingerDataContent;
	private String errorMessage;

	public void scanFinger(ActionEvent event) {
		primarystage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		if (fpDevice.Init() != 0) {
			generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.DEVICE_INFO_MESSAGE, RegistrationConstants.DEVICE_FP_NOT_FOUND);
		} else {
			fingerDataContent = null;
			errorMessage = "";
			int scanoutput = fpDevice.StartCapture(qualityScore, captureTimeOut, false);
			int count = 0;

			if (scanoutput != 0) {
				generateAlert(RegistrationConstants.LOGIN_ALERT_TITLE,
						AlertType.valueOf(RegistrationConstants.ALERT_ERROR), RegistrationConstants.DEVICE_INFO_MESSAGE,
						fpDevice.GetLastError());
			}

			while (count < 10) {
				if (fingerDataContent != null || errorMessage != "") {
					break;
				} else {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						LOGGER.error("FINGERPRINT_AUTHENTICATION_CONTROLLER - ERROR_SCANNING_FINGER", APPLICATION_NAME,
								APPLICATION_ID, e.getMessage());
					}
				}
				count++;
			}
			fingerPrintCheck(fingerDataContent, errorMessage);

		}
	}

	@Override
	public void OnCaptureCompleted(boolean status, int errorCode, String errorMsg, FingerData fingerData) {
		fingerDataContent = fingerData;
		errorMessage = errorMsg;
		if (fingerData != null) {
			FingerprintTemplate fingerprintTemplate = new FingerprintTemplate().convert(fingerData.ISOTemplate());
			minutia = fingerprintTemplate.serialize();

		}
	}

	private void fingerPrintCheck(FingerData fingerDataContent, String errorMessage) {
		if (fingerDataContent != null) {
			OnPreview(fingerDataContent);
			FingerprintTemplate fingerprintTemplate = new FingerprintTemplate()
					.convert(fingerDataContent.ISOTemplate());
			minutia = fingerprintTemplate.serialize();
			validateFingerPrint(minutia);
			primarystage.close();
		}
	}

	/**
	 * Validate the Scanned Finger print
	 * 
	 * @param minutia
	 * @param detail
	 */
	public void validateFingerPrint(String minutia) {// String minutia, RegistrationUserDetail detail) {
		RegistrationUserDetail detail = loginService.getUserDetail("mosip");

		if (validateBiometric(minutia, detail)) {
			generateAlert("Info", AlertType.INFORMATION, "Records approved Successfully.");
			baseController.getFingerPrintStatus();
		} else {
			generateAlert("Info", AlertType.INFORMATION, "Records approved Successfully.");
			baseController.getFingerPrintStatus();
		}
	}

	/**
	 * Compare the scanned finger print with the database
	 * 
	 * @param minutia
	 * @param registrationUserDetail
	 * @return
	 */
	private boolean validateBiometric(String minutia, RegistrationUserDetail registrationUserDetail) {

		return registrationUserDetail.getUserBiometric().stream()
				.anyMatch(bio -> fingerprintProvider.scoreCalculator(minutia, bio.getBioMinutia()) > fingerPrintScore);
	}

	@Override
	public void OnPreview(FingerData fingerData) {
		if (null != fingerData.FingerImage()) {
			BufferedImage l_objBufferImg = null;
			try {
				l_objBufferImg = ImageIO.read(new ByteArrayInputStream(fingerData.FingerImage()));
			} catch (IOException ex) {
				System.out.println("Image failed to load.");
			}

			WritableImage l_objWritableImg = null;
			if (l_objBufferImg != null) {
				l_objWritableImg = new WritableImage(l_objBufferImg.getWidth(), l_objBufferImg.getHeight());
				PixelWriter pw = l_objWritableImg.getPixelWriter();
				for (int x = 0; x < l_objBufferImg.getWidth(); x++) {
					for (int y = 0; y < l_objBufferImg.getHeight(); y++) {
						pw.setArgb(x, y, l_objBufferImg.getRGB(x, y));
					}
				}
			}
			this.fingerScannedImage.setImage(l_objWritableImg);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		deviceCmbBox.getItems().clear();
		deviceCmbBox.setItems(FXCollections.observableArrayList(RegistrationConstants.ONBOARD_DEVICE_TYPES));
		deviceCmbBox.getSelectionModel().selectFirst();
	}

	/**
	 * event class to exit from authentication window. pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow(ActionEvent event) {
		primarystage = (Stage) ((Node) event.getSource()).getParent().getScene().getWindow();
		primarystage.close();

	}

}
