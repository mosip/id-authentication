package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

@Controller
public class FaceCaptureController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(FaceCaptureController.class);

	@FXML
	private Button takePhoto;

	private Pane selectedPhoto;
	@FXML
	private Pane applicantImagePane;
	@FXML
	private ImageView applicantImage;
	@FXML
	private Pane exceptionImagePane;
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

	private Timestamp lastPhotoCaptured;

	private Timestamp lastExceptionPhotoCaptured;

	@FXML
	private Label photoAlert;

	private BufferedImage applicantBufferedImage;
	private BufferedImage exceptionBufferedImage;
	private Image defaultImage;
	private boolean applicantImageCaptured;
	private boolean exceptionImageCaptured;
	private Stage popupStage;

	private Boolean toggleBiometricException = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("REGISTRATION - UI - FACE_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Loading of FaceCapture screen started");

		takePhoto.setDisable(true);
		applicantImage.setPreserveRatio(true);
		exceptionImage.setPreserveRatio(true);
		if (capturePhotoUsingDevice.equals(RegistrationConstants.ENABLE)
				|| (boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			// for applicant biometrics
			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {

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
	 * To open camera for the type of image that is to be captured
	 * 
	 * @param imageType
	 *            type of image that is to be captured
	 */
	private void openWebCamWindow(String imageType) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening WebCamera to capture photograph");
		if (webCameraController.isWebcamPluggedIn()) {
			try {
				Stage primaryStage = new Stage();
				primaryStage.initStyle(StageStyle.UNDECORATED);
				FXMLLoader loader = BaseController
						.loadChild(getClass().getResource(RegistrationConstants.WEB_CAMERA_PAGE));
				Parent webCamRoot = loader.load();

				WebCameraController cameraController = loader.getController();
				cameraController.init(this, imageType);

				primaryStage.setTitle(RegistrationConstants.WEB_CAMERA_PAGE_TITLE);
				Scene scene = new Scene(webCamRoot);
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				scene.getStylesheets()
						.add(classLoader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.initModality(Modality.WINDOW_MODAL);
				primaryStage.initOwner(fXComponents.getStage());
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
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "saving the details of applicant biometrics");
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
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
				registrationController.saveBiometricDetails(applicantBufferedImage, exceptionBufferedImage);
			}
		}
	}

	@FXML
	private void goToPreviousPane() {
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			if (validateOperatorPhoto()) {
				registrationController.saveBiometricDetails(applicantBufferedImage, exceptionBufferedImage);
				if (getBiometricDTOFromSession().getOperatorBiometricDTO().getFaceDetailsDTO().getFace() != null) {
					loadPage(RegistrationConstants.USER_ONBOARD_IRIS);
				}
			}

		} else {
			SessionContext.map().put("faceCapture", false);
			SessionContext.map().put("irisCapture", true);
			registrationController.showCurrentPage();
		}
	}

	@Override
	public void saveApplicantPhoto(BufferedImage capturedImage, String photoType) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening WebCamera to capture photograph");

		if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE)) {

			/* Set Time which last photo was captured */
			lastPhotoCaptured = getCurrentTimestamp();

			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			applicantImage.setImage(capture);
			applicantBufferedImage = capturedImage;
			applicantImageCaptured = true;
			try {
				if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(applicantBufferedImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE,
							byteArrayOutputStream);
					byte[] photoInBytes = byteArrayOutputStream.toByteArray();
					((BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA))
							.getOperatorBiometricDTO().getFaceDetailsDTO().setFace(photoInBytes);
				}
			} catch (Exception ioException) {
				LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, ioException.getMessage());
			}
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE)) {

			/* Set Time which last Exception photo was captured */
			lastExceptionPhotoCaptured = getCurrentTimestamp();
			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			exceptionImage.setImage(capture);
			exceptionBufferedImage = capturedImage;
			exceptionImageCaptured = true;
		}
	}

	@Override
	public void clearPhoto(String photoType) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
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
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "validating applicant biometrics");

		toggleBiometricException = (Boolean) SessionContext.userContext().getUserMap()
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

	private RegistrationDTO getRegistrationDTOFromSession() {
		return (RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA);
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
		return (BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA);
	}

	/**
	 * Method to load fxml page
	 * 
	 * @param fxml
	 *            file name
	 */
	private void loadPage(String page) {
		VBox mainBox = new VBox();
		try {
			HBox headerRoot = BaseController.load(getClass().getResource(RegistrationConstants.HEADER_PAGE));
			mainBox.getChildren().add(headerRoot);
			Parent createRoot = BaseController.load(getClass().getResource(page));
			mainBox.getChildren().add(createRoot);
			getScene(mainBox).setRoot(mainBox);
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

	@FXML
	private void enableCapture(MouseEvent mouseEvent) {
		boolean hasBiometricException = (Boolean) SessionContext.userContext().getUserMap()
				.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);

		Pane sourcePane = (Pane) mouseEvent.getSource();
		sourcePane.requestFocus();
		selectedPhoto = sourcePane;
		takePhoto.setDisable(true);
		if (selectedPhoto.getId().equals(RegistrationConstants.APPLICANT_PHOTO_PANE)
				|| (selectedPhoto.getId().equals(RegistrationConstants.EXCEPTION_PHOTO_PANE)
						&& hasBiometricException)) {
			takePhoto.setDisable(false);
		}
	}

	@FXML
	private void takePhoto() {
		if (selectedPhoto.getId().equals(RegistrationConstants.APPLICANT_PHOTO_PANE)) {
			if (webCameraController.webCameraPane == null
					|| !(webCameraController.webCameraPane.getScene().getWindow().isShowing())) {
				if (validatePhotoTimer(lastPhotoCaptured, 10, photoAlert)) {
					openWebCamWindow(RegistrationConstants.APPLICANT_IMAGE);
				} else {
					takePhoto.setDisable(true);
				}
			}
		} else if (selectedPhoto.getId().equals(RegistrationConstants.EXCEPTION_PHOTO_PANE)) {
			if (webCameraController.webCameraPane == null
					|| !(webCameraController.webCameraPane.getScene().getWindow().isShowing())) {

				if (validatePhotoTimer(lastExceptionPhotoCaptured, 10, photoAlert)) {
					openWebCamWindow(RegistrationConstants.EXCEPTION_IMAGE);
				} else {
					takePhoto.setDisable(true);
				}
			}
		}
	}

	private boolean validatePhotoTimer(Timestamp lastPhoto, int configuredSecs, Label photoLabel) {
		if (lastPhoto == null) {
			return true;
		}
		/* Get Calendar instance */
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Timestamp(System.currentTimeMillis()));
		cal.add(Calendar.SECOND, -configuredSecs);

		int diffSeconds = Seconds.secondsBetween(new DateTime(lastPhoto.getTime()), DateTime.now()).getSeconds();
		if (diffSeconds >= configuredSecs) {
			return true;
		} else {
			setTimeLabel(photoLabel, configuredSecs, diffSeconds);
			return false;
		}
	}

	private void setTimeLabel(Label photoLabel, int configuredSecs, int diffSeconds) {
		SimpleIntegerProperty timeDiff = new SimpleIntegerProperty((Integer) (configuredSecs - diffSeconds));

		photoLabel.setVisible(true);
		// Bind the photoLabel text property to the timeDiff property
		photoLabel.textProperty().bind(Bindings.concat("Recapture after ", timeDiff.asString(), " seconds"));
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds((Integer) (configuredSecs - diffSeconds)), new KeyValue(timeDiff, 1)));
		timeline.setOnFinished(event -> {
			takePhoto.setDisable(false);
			photoLabel.setVisible(false);
		});
		timeline.play();

	}

	private Timestamp getCurrentTimestamp() {
		return Timestamp.from(Instant.now());
	}
}
