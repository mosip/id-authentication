package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This controller class is to handle the preview screen of the Biometric
 * details
 * 
 * @author M1046540
 *
 */
@Controller
public class BiometricPreviewController extends BaseController {

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;

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
	private ImageView individualPhoto;

	@FXML
	private ImageView exceptionPhoto;

	@Autowired
	private RegistrationController registrationController;

	@Autowired
	private DemographicPreviewController demographicPreviewController;

	@Autowired
	private RegistrationOfficerPacketController registrationOfficerPacketController;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(BiometricPreviewController.class);

	@FXML
	private void initialize() {
		LOGGER.debug("BIOMETRIC_PREVIEW_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Entering the BIOMETRIC_PREVIEW_CONTROLLER");
		RegistrationDTO registrationDTOContent = (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
		registrationDTOContent.getBiometricDTO();
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
			}
		}
	}

	/**
	 * This method is used to handle the edit action of registration preview screen
	 */
	public void handleEdit() {
		demographicPreviewController.handleEdit();
	}

	/**
	 * This method is used to handle the next button action of registration preview
	 * screen
	 */
	public void handleNextBtnAction() {
		registrationOfficerPacketController.showReciept((RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA), capturePhotoUsingDevice);
	}

	/**
	 * This method is used to navigate the screen to home page
	 */
	public void goToHomePage() {
		registrationController.goToHomePage();
	}
}
