package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;

/**
 * This controller class is to handle the preview screen of the Biometric
 * details
 * 
 * @author M1046540
 *
 */
@Controller
public class BiometricPreviewController extends BaseController {

	@FXML
	private Button nextBtn;
	@FXML
	private Button editBtn;
	@FXML
	private TitledPane biometricPreviewPane;

	@FXML
	private ImageView leftPalm;

	@FXML
	private ImageView rightPalm;

	@FXML
	private ImageView leftEye;

	@FXML
	private ImageView rightEye;

	@FXML
	private ImageView thumb;

	@FXML
	private Label exceptionLabel;

	@FXML
	private ImageView individualPhoto;

	@FXML
	private ImageView exceptionPhoto;

	@FXML
	private Text leftEyeQualityScore;

	@FXML
	private Text rightEyeQualityScore;

	@FXML
	private Text leftPalmQualityScore;

	@FXML
	private Text rightPalmQualityScore;

	@FXML
	private Text thumbsQualityScore;

	@FXML
	private Text leftPalmThresholdScoreLbl;

	@FXML
	private Text rightPalmThresholdScoreLbl;

	@FXML
	private Text thumbsThresholdScoreLbl;

	@FXML
	private Text leftIrisThreshold;

	@FXML
	private Text rightIrisThreshold;

	@FXML
	private ScrollPane bioScrollPane;

	@FXML
	private AnchorPane fingerprintAnchorPane;

	@FXML
	private AnchorPane irisAnchorPane;

	@FXML
	private AnchorPane photoAnchorPane;

	@FXML
	private AnchorPane leftSlapAnchorPane;

	@FXML
	private AnchorPane rightSlapAnchorPane;

	@FXML
	private AnchorPane thumbAnchorPane;

	@FXML
	private AnchorPane leftIrisAnchorPane;

	@FXML
	private AnchorPane rightIrisAnchorPane;

	@Autowired
	private RegistrationController registrationController;

	@FXML
	private Text registrationNavLabel;
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(BiometricPreviewController.class);

	@FXML
	private void initialize() {
		LOGGER.debug("BIOMETRIC_PREVIEW_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Entering the BIOMETRIC_PREVIEW_CONTROLLER");
		bioScrollPane.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight());
		RegistrationDTO registrationDTOContent = (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);

		if (registrationDTOContent.getSelectionListDTO() != null) {
			registrationNavLabel.setText(RegistrationConstants.UIN_NAV_LABEL);
		}
		
		paneVisibility(registrationDTOContent);

		String irisThreshold = ((String) applicationContext.getApplicationMap()
				.get(RegistrationConstants.IRIS_THRESHOLD)).concat(RegistrationConstants.PERCENTAGE);
		leftIrisThreshold.setText(irisThreshold);
		rightIrisThreshold.setText(irisThreshold);

		leftPalmThresholdScoreLbl.setText(getQualityScore(
				Double.parseDouble(getValueFromSessionMap(RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD))));
		rightPalmThresholdScoreLbl.setText(getQualityScore(
				Double.parseDouble(getValueFromSessionMap(RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD))));
		thumbsThresholdScoreLbl.setText(getQualityScore(
				Double.parseDouble(getValueFromSessionMap(RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD))));

		if (null != registrationDTOContent.getDemographicDTO().getApplicantDocumentDTO()) {

			if (registrationDTOContent.getDemographicDTO().getApplicantDocumentDTO().getPhoto() != null) {
				byte[] photoInBytes = registrationDTOContent.getDemographicDTO().getApplicantDocumentDTO().getPhoto();
				if (photoInBytes != null) {
					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(photoInBytes);
					individualPhoto.setImage(new Image(byteArrayInputStream));
				}
			}

			if (registrationDTOContent.getDemographicDTO().getApplicantDocumentDTO().getExceptionPhoto() != null) {
				byte[] exceptionPhotoInBytes = registrationDTOContent.getDemographicDTO().getApplicantDocumentDTO()
						.getExceptionPhoto();
				if (exceptionPhotoInBytes != null) {
					ByteArrayInputStream inputStream = new ByteArrayInputStream(exceptionPhotoInBytes);
					exceptionPhoto.setImage(new Image(inputStream));
				}
			} else {
				exceptionPhoto.setImage(null);
				exceptionLabel.setVisible(false);
			}

			for (FingerprintDetailsDTO fpDetailsDTO : registrationDTOContent.getBiometricDTO()
					.getApplicantBiometricDTO().getFingerprintDetailsDTO()) {
				if (fpDetailsDTO.getFingerType().contains(RegistrationConstants.LEFTPALM)) {
					leftPalm.setImage(convertBytesToImage(fpDetailsDTO.getFingerPrint()));
					leftPalmQualityScore.setText(getQualityScore(fpDetailsDTO.getQualityScore()));

				} else if (fpDetailsDTO.getFingerType().contains(RegistrationConstants.RIGHTPALM)) {
					rightPalm.setImage(convertBytesToImage(fpDetailsDTO.getFingerPrint()));
					rightPalmQualityScore.setText(getQualityScore(fpDetailsDTO.getQualityScore()));

				} else if (fpDetailsDTO.getFingerType().contains(RegistrationConstants.THUMBS)) {
					thumb.setImage(convertBytesToImage(fpDetailsDTO.getFingerPrint()));
					thumbsQualityScore.setText(getQualityScore(fpDetailsDTO.getQualityScore()));

				}
			}

		}

		for (IrisDetailsDTO capturedIris : registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO()
				.getIrisDetailsDTO()) {
			if (capturedIris.getIrisType().contains(RegistrationConstants.LEFT)) {
				leftEye.setImage(convertBytesToImage(capturedIris.getIris()));
				leftEyeQualityScore.setText(getQualityScore(capturedIris.getQualityScore()));

			} else if (capturedIris.getIrisType().contains(RegistrationConstants.RIGHT)) {
				rightEye.setImage(convertBytesToImage(capturedIris.getIris()));
				rightEyeQualityScore.setText(getQualityScore(capturedIris.getQualityScore()));
			}
		}
	}

	/**
	 * Pane visibility depending on capturing.
	 *
	 * @param registrationDTOContent the registration DTO content
	 */
	private void paneVisibility(RegistrationDTO registrationDTOContent) {
		leftSlapAnchorPane.setVisible(false);
		rightSlapAnchorPane.setVisible(false);
		thumbAnchorPane.setVisible(false);
		leftIrisAnchorPane.setVisible(false);
		rightIrisAnchorPane.setVisible(false);

		if (registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO().isEmpty()
				&& registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO().getIrisDetailsDTO().isEmpty()) {
			fingerprintAnchorPane.setVisible(false);
			irisAnchorPane.setVisible(false);
			photoAnchorPane.setLayoutY(14);

		} else if (registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO()
				.isEmpty()) {
			irisAnchorPane.setLayoutY(14);
			photoAnchorPane.setLayoutY(303);
			fingerprintAnchorPane.setVisible(false);
		} else if (registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO().getIrisDetailsDTO().isEmpty()) {
			fingerprintAnchorPane.setLayoutY(14);
			photoAnchorPane.setLayoutY(303);
			irisAnchorPane.setVisible(false);
		}

		boolean isLeftEyeCaptured = registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO()
				.getIrisDetailsDTO().stream().anyMatch(bio -> bio.getIrisType()
						.equalsIgnoreCase(RegistrationConstants.LEFT.concat(RegistrationConstants.EYE)));

		boolean isRightEyeCaptured = registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO()
				.getIrisDetailsDTO().stream().anyMatch(bio -> bio.getIrisType()
						.equalsIgnoreCase(RegistrationConstants.RIGHT.concat(RegistrationConstants.EYE)));

		boolean isLeftPalmCaptured = registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO()
				.getFingerprintDetailsDTO().stream()
				.anyMatch(bio -> bio.getFingerType().equalsIgnoreCase(RegistrationConstants.LEFTPALM));
		boolean isRightPalmCaptured = registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO()
				.getFingerprintDetailsDTO().stream()
				.anyMatch(bio -> bio.getFingerType().equalsIgnoreCase(RegistrationConstants.RIGHTPALM));
		boolean isThumbCaptured = registrationDTOContent.getBiometricDTO().getApplicantBiometricDTO()
				.getFingerprintDetailsDTO().stream()
				.anyMatch(bio -> bio.getFingerType().equalsIgnoreCase(RegistrationConstants.THUMBS));

		leftSlapAnchorPane.setVisible(isLeftPalmCaptured);
		rightSlapAnchorPane.setVisible(isRightPalmCaptured);
		thumbAnchorPane.setVisible(isThumbCaptured);
		leftIrisAnchorPane.setVisible(isLeftEyeCaptured);
		rightIrisAnchorPane.setVisible(isRightEyeCaptured);

		if (!isLeftPalmCaptured && !isRightPalmCaptured && isThumbCaptured) {
			thumbAnchorPane.setLayoutX(0);
		} else if (isLeftPalmCaptured && !isRightPalmCaptured && isThumbCaptured) {
			thumbAnchorPane.setLayoutX(258);
		} else if (!isLeftPalmCaptured && isRightPalmCaptured) {
			rightSlapAnchorPane.setLayoutX(0);
			thumbAnchorPane.setLayoutX(258);
		}

		if (!isLeftEyeCaptured && isRightEyeCaptured) {
			rightIrisAnchorPane.setLayoutX(0);
		}
	}

	/**
	 * This method is used to handle the edit action of registration preview screen
	 */
	public void handleEdit() {
		try {
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
			loadScreen(RegistrationConstants.CREATE_PACKET_PAGE);
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - UI-  Preview ", APPLICATION_NAME, APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * This method is used to handle the next button action of registration preview
	 * screen
	 */
	public void handleNextBtnAction() {
		registrationController.goToAuthenticationPage();
	}

	/**
	 * This method is used to navigate the screen to home page
	 */
	public void handleHomeButton() {
		goToHomePageFromRegistration();
	}

	private String getQualityScore(Double qulaityScore) {
		return String.valueOf(Math.round(qulaityScore)).concat(RegistrationConstants.PERCENTAGE);
	}

	private String getValueFromSessionMap(String key) {
		return (String) applicationContext.getApplicationMap().get(key);
	}
}
