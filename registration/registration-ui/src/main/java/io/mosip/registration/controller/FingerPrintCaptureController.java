package io.mosip.registration.controller;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_PENDING_APPROVAL;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.REG_UI_LOGIN_LOADER_EXCEPTION;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.device.impl.FingerPrintCaptureServiceImpl;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Controller
public class FingerPrintCaptureController extends BaseController implements Initializable {

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;
	@Autowired
	private FingerPrintScanController fingerPrintScanController;
	@Autowired
	private FingerPrintCaptureServiceImpl fingerPrintCaptureServiceImpl;
	@Autowired
	private RegistrationController registrationController;
	@FXML
	private AnchorPane fingerPrintCapturePane;
	@FXML
	protected AnchorPane leftHandPalmPane;
	@FXML
	protected AnchorPane rightHandPalmPane;
	@FXML
	protected AnchorPane thumbPane;
	@FXML
	protected ImageView leftHandPalmImageview;
	@FXML
	protected ImageView rightHandPalmImageview;
	@FXML
	protected ImageView thumbImageview;

	private AnchorPane selectedPane;

	private List<FingerprintDetailsDTO> detailsDTOs;
	private final Border border = new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, null, null));

	private final Border focusedBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null));

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		registrationController.biometricsPane.setVisible(false);
		detailsDTOs = new ArrayList<>();
		selectAnchorPane();
	}

	private void selectAnchorPane() {
		fingerPrintCapturePane.getChildren().stream().filter(obj -> obj instanceof AnchorPane)
				.map(obj -> (AnchorPane) obj).forEach(anchorPane -> anchorPane.setOnMouseClicked(e -> {
					anchorPane.requestFocus();
					selectedPane = anchorPane;
					anchorPane.borderProperty()
							.bind(Bindings.when(anchorPane.focusedProperty()).then(focusedBorder).otherwise(border));
				}));
	}

	public void scan() throws RegBaseCheckedException {
		selectedPane.requestFocus();
		try {
			Stage primaryStage = new Stage();
			primaryStage.initStyle(StageStyle.UNDECORATED);
			Parent ackRoot = BaseController.load(getClass().getResource("/fxml/FingerPrintScan.fxml"));
			fingerPrintScanController.init(selectedPane, primaryStage, detailsDTOs);
			primaryStage.setResizable(false);
			Scene scene = new Scene(ackRoot);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(stage);
			primaryStage.show();

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorCode(),
					RegistrationExceptions.REG_UI_LOGIN_IO_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(REG_UI_LOGIN_LOADER_EXCEPTION, runtimeException.getMessage());
		}
		LOGGER.debug(LOG_REG_PENDING_APPROVAL, APPLICATION_NAME, APPLICATION_ID,
				"Supervisor Authentication has been ended");
	}

	public void saveBiometricDetails() {
		
		//fingerPrintCaptureServiceImpl.validateFingerprint(detailsDTOs);
		fingerPrintCapturePane.setVisible(false);
		if (capturePhotoUsingDevice.equals("Y")) {
			registrationController.biometricsPane.setVisible(true);
		} else {
			registrationController.biometricsPane.setVisible(false);
		}
		/*registrationController.getRegistrationDTOContent().setgetBiometricDTO().getApplicantBiometricDTO()
				.setFingerprintDetailsDTO(detailsDTOs);*/
	}

}
