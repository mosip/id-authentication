package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.service.UserOnboardService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Controller
public class FaceCaptureController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(FaceCaptureController.class);

	@FXML
	private Button captureImage;
	@FXML
	public Button captureExceptionImage;
	@FXML
	private HBox applicantImageHBox;
	@FXML
	private ImageView applicantImage;
	@FXML
	private HBox exceptionImageHBox;
	@FXML
	private ImageView exceptionImage;
	@FXML
	public Button biometricPrevBtn;
	@FXML
	public Button saveBiometricDetailsBtn;

	@Autowired
	private RegistrationController registrationController;

	@Autowired
	private WebCameraController webCameraController;

	@Autowired
	private UserOnboardService userOnboardService;

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;

	private BufferedImage applicantBufferedImage;
	private BufferedImage exceptionBufferedImage;
	private Image defaultImage;
	private boolean applicantImageCaptured;
	private boolean exceptionImageCaptured;
	private Stage popupStage;

	private Boolean toggleBiometricException = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - UI - FACE_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Loading of FaceCapture screen started");

		if (capturePhotoUsingDevice.equals(RegistrationConstants.ENABLE)
				|| (boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_USER)) {
			if (toggleBiometricException != null) {
				if (toggleBiometricException) {
					captureExceptionImage.setDisable(false);
				} else {
					captureExceptionImage.setDisable(true);
				}
			}
			// for applicant biometrics
			if ((boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_USER)) {

				if (getBiometricDTOFromSession() != null && getBiometricDTOFromSession().getOperatorBiometricDTO()
						.getFaceDetailsDTO().getFace() != null) {
					applicantImage.setImage(convertBytesToImage(
							getBiometricDTOFromSession().getOperatorBiometricDTO().getFaceDetailsDTO().getFace()));
				} else {
					initialize();
				}
			} else {
				if (getRegistrationDTOFromSession() != null
						&& getRegistrationDTOFromSession().getDemographicDTO().getApplicantDocumentDTO() != null) {
					if (getRegistrationDTOFromSession().getDemographicDTO().getApplicantDocumentDTO()
							.getPhoto() != null) {
						byte[] photoInBytes = getRegistrationDTOFromSession().getDemographicDTO()
								.getApplicantDocumentDTO().getPhoto();
						if (photoInBytes != null) {
							ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(photoInBytes);
							applicantImage.setImage(new Image(byteArrayInputStream));
						}
					}
					if (getRegistrationDTOFromSession().getDemographicDTO().getApplicantDocumentDTO()
							.getExceptionPhoto() != null) {
						byte[] exceptionPhotoInBytes = getRegistrationDTOFromSession().getDemographicDTO()
								.getApplicantDocumentDTO().getExceptionPhoto();
						if (exceptionPhotoInBytes != null) {
							ByteArrayInputStream inputStream = new ByteArrayInputStream(exceptionPhotoInBytes);
							exceptionImage.setImage(new Image(inputStream));
						}
					}
				} else {
					initialize();
				}
			}
		}
	}

	private void initialize() {
		defaultImage = applicantImage.getImage();
		applicantImageCaptured = false;
		exceptionImageCaptured = false;
		exceptionBufferedImage = null;
	}

	/**
	 * 
	 * To open camera to capture Applicant Image
	 * 
	 */
	@FXML
	private void openCamForApplicantPhoto() {
		if (webCameraController.webCameraPane == null
				|| !(webCameraController.webCameraPane.getScene().getWindow().isShowing())) {
			openWebCamWindow(RegistrationConstants.APPLICANT_IMAGE);
		}
	}

	/**
	 * 
	 * To open camera to capture Exception Image
	 * 
	 */
	@FXML
	private void openCamForExceptionPhoto() {
		if (webCameraController.webCameraPane == null
				|| !(webCameraController.webCameraPane.getScene().getWindow().isShowing())) {
			openWebCamWindow(RegistrationConstants.EXCEPTION_IMAGE);
		}
	}

	/**
	 * 
	 * To open camera for the type of image that is to be captured
	 * 
	 * @param imageType type of image that is to be captured
	 */
	private void openWebCamWindow(String imageType) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening WebCamera to capture photograph");
		if (webCameraController.isWebcamPluggedIn()) {
			try {
				Stage primaryStage = new Stage();
				FXMLLoader loader = BaseController
						.loadChild(getClass().getResource(RegistrationConstants.WEB_CAMERA_PAGE));
				Parent webCamRoot = loader.load();

				WebCameraController cameraController = loader.getController();
				cameraController.init(this, imageType);

				primaryStage.setTitle(RegistrationConstants.WEB_CAMERA_PAGE_TITLE);
				Scene scene = new Scene(webCamRoot);
				primaryStage.setScene(scene);
				primaryStage.show();
			} catch (IOException ioException) {
				LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, ioException.getMessage());
			}
		} else {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.WEBCAM_ALERT_CONTEXT);
		}
	}

	@FXML
	private void saveBiometricDetails() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "saving the details of applicant biometrics");
		if ((boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_USER)) {
			if (validateOperatorPhoto()) {
				registrationController.saveBiometricDetails(applicantBufferedImage, exceptionBufferedImage);
				if (getBiometricDTOFromSession().getOperatorBiometricDTO().getFaceDetailsDTO().getFace() != null) {
					ResponseDTO response = userOnboardService.validate(getBiometricDTOFromSession());
					if (response != null && response.getErrorResponseDTOs() != null
							&& response.getErrorResponseDTOs().get(0) != null) {
						generateAlert(RegistrationConstants.ERROR, response.getErrorResponseDTOs().get(0).getMessage());
					} else if (response != null && response.getSuccessResponseDTO() != null) {
						try {
							popupStage = new Stage();
							popupStage.initStyle(StageStyle.DECORATED);
							Parent scanPopup = BaseController
									.load(getClass().getResource("/fxml/UserOnboardSuccess.fxml"));
							popupStage.setResizable(false);
							Scene scene = new Scene(scanPopup);
							ClassLoader loader = Thread.currentThread().getContextClassLoader();
							scene.getStylesheets()
									.add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
							popupStage.setScene(scene);
							popupStage.initModality(Modality.WINDOW_MODAL);
							popupStage.initOwner(fXComponents.getStage());
							popupStage.show();
						} catch (IOException exception) {
							LOGGER.error("REGISTRATION - USERONBOARD CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
									exception.getMessage());
							generateAlert(RegistrationConstants.ERROR,
									RegistrationUIConstants.UNABLE_LOAD_USERONBOARD_SCREEN);
						}
					}
				}
			}
		} else {
			if (validateApplicantImage()) {
				SessionContext.getInstance().getMapObject().put("faceCapture",false); 
				registrationController.saveBiometricDetails(applicantBufferedImage, exceptionBufferedImage);
			}
		}
	}

	@FXML
	private void goToPreviousPane() {
		if ((boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_USER)) {
			if (validateOperatorPhoto()) {
				registrationController.saveBiometricDetails(applicantBufferedImage, exceptionBufferedImage);
				if (getBiometricDTOFromSession().getOperatorBiometricDTO().getFaceDetailsDTO().getFace() != null) {
					loadPage(RegistrationConstants.USER_ONBOARD_IRIS);
				}
			}

		} else

		{
			//registrationController.goToPreviousPane();
		}
	}

	@Override
	public void saveApplicantPhoto(BufferedImage capturedImage, String photoType) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening WebCamera to capture photograph");

		if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE)) {
			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			applicantImage.setImage(capture);
			applicantBufferedImage = capturedImage;
			applicantImageCaptured = true;
			try {
				if ((boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.ONBOARD_USER)) {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(applicantBufferedImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE,
							byteArrayOutputStream);
					byte[] photoInBytes = byteArrayOutputStream.toByteArray();
					((BiometricDTO) SessionContext.getInstance().getMapObject()
							.get(RegistrationConstants.USER_ONBOARD_DATA)).getOperatorBiometricDTO().getFaceDetailsDTO()
									.setFace(photoInBytes);
				}
			} catch (Exception ioException) {
				LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, ioException.getMessage());
			}
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE)) {
			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			exceptionImage.setImage(capture);
			exceptionBufferedImage = capturedImage;
			exceptionImageCaptured = true;
		}
	}

	@Override
	public void clearPhoto(String photoType) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "clearing the image that is captured");

		if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE) && applicantBufferedImage != null) {
			applicantImage.setImage(defaultImage);
			applicantBufferedImage = null;
			applicantImageCaptured = false;
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE) && exceptionBufferedImage != null) {
			exceptionImage.setImage(defaultImage);
			exceptionBufferedImage = null;
			exceptionImageCaptured = false;
		}
	}

	private boolean validateApplicantImage() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "validating applicant biometrics");

		toggleBiometricException = (Boolean) SessionContext.getInstance().getUserContext().getUserMap()
				.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);

		boolean imageCaptured = false;
		if (applicantImageCaptured) {
			if (toggleBiometricException) {
				if (exceptionImageCaptured) {
					if (getRegistrationDTOFromSession() != null
							&& getRegistrationDTOFromSession().getDemographicDTO() != null) {
						imageCaptured = true;
					} else {
						generateAlert(RegistrationConstants.ERROR,
								RegistrationUIConstants.DEMOGRAPHIC_DETAILS_ERROR_CONTEXT);
					}
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.APPLICANT_IMAGE_ERROR);
				}
			} else {
				if (getRegistrationDTOFromSession() != null
						&& getRegistrationDTOFromSession().getDemographicDTO() != null) {
					imageCaptured = true;
				} else {
					generateAlert(RegistrationConstants.ERROR,
							RegistrationUIConstants.DEMOGRAPHIC_DETAILS_ERROR_CONTEXT);
				}
			}
		} else {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.APPLICANT_IMAGE_ERROR);
		}
		return imageCaptured;
	}

	private boolean validateOperatorPhoto() {
		if (getBiometricDTOFromSession().getOperatorBiometricDTO().getFaceDetailsDTO().getFace() != null) {
			return true;
		} else {
			generateAlert(RegistrationConstants.ERROR, "Please capture the photo");
			return false;
		}
	}

	public void setPreviewContent() {
		saveBiometricDetailsBtn.setVisible(false);
		biometricPrevBtn.setVisible(false);
	}

	private RegistrationDTO getRegistrationDTOFromSession() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}

	public void disableExceptionPhotoCapture(boolean value) {
		captureExceptionImage.setDisable(value);
	}

	public void clearExceptionImage() {
		exceptionBufferedImage = null;
		exceptionImage.setImage(defaultImage);
		ApplicantDocumentDTO applicantDocumentDTO = getRegistrationDTOFromSession().getDemographicDTO()
				.getApplicantDocumentDTO();
		if (applicantDocumentDTO != null && applicantDocumentDTO.getExceptionPhoto() != null) {
			applicantDocumentDTO.setExceptionPhoto(null);
			if (applicantDocumentDTO.getExceptionPhotoName() != null) {
				applicantDocumentDTO.setExceptionPhotoName(null);
			}
			applicantDocumentDTO.setHasExceptionPhoto(false);
		}
	}

	private BiometricDTO getBiometricDTOFromSession() {
		return (BiometricDTO) SessionContext.getInstance().getMapObject().get(RegistrationConstants.USER_ONBOARD_DATA);
	}

	/**
	 * Method to load fxml page
	 * 
	 * @param fxml file name
	 */
	private void loadPage(String page) {
		Parent createRoot;
		try {
			createRoot = BaseController.load(getClass().getResource(page));
			getScene(createRoot).setRoot(createRoot);
		} catch (IOException exception) {
			LOGGER.error("REGISTRATION - USERONBOARD CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_USERONBOARD_SCREEN);
		}
	}

	@FXML
	private void loadLoginScreen() {
		clearOnboardData();
		if (popupStage.isShowing()) {
			popupStage.close();
			goToHomePage();
		}
	}
}
