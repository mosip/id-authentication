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
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.controller.reg.UserOnboardParentController;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
	private AnchorPane applicantImagePane;
	@FXML
	private ImageView applicantImage;
	@FXML
	private AnchorPane exceptionImagePane;
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
	private UserOnboardParentController userOnboardParentController;

	private Timestamp lastPhotoCaptured;

	private Timestamp lastExceptionPhotoCaptured;
	
	@FXML
	private Label registrationNavlabel;

	@FXML
	private Label photoAlert;

	private BufferedImage applicantBufferedImage;
	private BufferedImage exceptionBufferedImage;
	private Image defaultImage;
	private Image defaultExceptionImage;
	private boolean applicantImageCaptured;
	private boolean exceptionImageCaptured;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("REGISTRATION - UI - FACE_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Loading of FaceCapture screen started");

		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			registrationNavlabel.setText(RegistrationConstants.UIN_NAV_LABEL);
		}
		
		takePhoto.setDisable(true);

		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			// for applicant biometrics
			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {

				if (getBiometricDTOFromSession() != null && getBiometricDTOFromSession().getOperatorBiometricDTO()
						.getFaceDetailsDTO().getFace() != null) {
					applicantImage.setImage(convertBytesToImage(
							getBiometricDTOFromSession().getOperatorBiometricDTO().getFaceDetailsDTO().getFace()));
				} else {
					defaultImage = applicantImage.getImage();
					applicantImageCaptured = false;
				}
			} else {
				defaultExceptionImage = new Image(getClass().getResourceAsStream("/images/ExceptionPhoto.png"));
				exceptionImage.setImage(defaultExceptionImage);
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
					defaultImage = applicantImage.getImage();
					defaultExceptionImage = exceptionImage.getImage();
					applicantImageCaptured = false;
					exceptionImageCaptured = false;
					exceptionBufferedImage = null;
				}
			}
		}
	}

	/**
	 * 
	 * To open camera for the type of image that is to be captured
	 * 
	 * @param imageType type of image that is to be captured
	 */
	private void openWebCamWindow(String imageType) {
		auditFactory.audit(AuditEvent.REG_BIO_FACE_CAPTURE, Components.REG_BIOMETRICS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

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
				Scene scene = new Scene(webCamRoot);
				ClassLoader classLoader = ClassLoader.getSystemClassLoader();
				scene.getStylesheets()
						.add(classLoader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.initModality(Modality.WINDOW_MODAL);
				primaryStage.initOwner(fXComponents.getStage());
				primaryStage.show();
			} catch (IOException ioException) {
				LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			}
		} else {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.WEBCAM_ALERT_CONTEXT);
		}
	}

	/**
	 * To save the captured applicant biometrics to the DTO
	 */
	@FXML
	private void saveBiometricDetails() {
		auditFactory.audit(AuditEvent.REG_BIO_FACE_CAPTURE_NEXT, Components.REG_BIOMETRICS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "saving the details of applicant biometrics");
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			if (validateOperatorPhoto()) {
				registrationController.saveBiometricDetails(applicantBufferedImage, exceptionBufferedImage);
				if (getBiometricDTOFromSession().getOperatorBiometricDTO().getFaceDetailsDTO().getFace() != null) {
					userOnboardParentController.showCurrentPage(RegistrationConstants.FACE_CAPTURE,
							getOnboardPageDetails(RegistrationConstants.FACE_CAPTURE, RegistrationConstants.NEXT));
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
		auditFactory.audit(AuditEvent.REG_BIO_FACE_CAPTURE_BACK, Components.REG_BIOMETRICS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			if (validateOperatorPhoto()) {
				registrationController.saveBiometricDetails(applicantBufferedImage, exceptionBufferedImage);
				if (getBiometricDTOFromSession().getOperatorBiometricDTO().getFaceDetailsDTO().getFace() != null) {
					userOnboardParentController.showCurrentPage(RegistrationConstants.FACE_CAPTURE,
							getOnboardPageDetails(RegistrationConstants.FACE_CAPTURE, RegistrationConstants.PREVIOUS));
				}
			}

		} else {

			try {
				if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
					SessionContext.map().put("faceCapture", false);

					long fingerPrintCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
							.getBiometricExceptionDTO().stream()
							.filter(bio -> bio.getBiometricType().equalsIgnoreCase("fingerprint")).count();

					long irisCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
							.getBiometricExceptionDTO().stream()
							.filter(bio -> bio.getBiometricType().equalsIgnoreCase(RegistrationConstants.IRIS)).count();

					
					if (getRegistrationDTOFromSession().getSelectionListDTO().isBiometricIris()
							|| fingerPrintCount > 0) {
						SessionContext.map().put("irisCapture", true);
					} else if (getRegistrationDTOFromSession().getSelectionListDTO().isBiometricFingerprint()
							|| irisCount > 0) {
						SessionContext.map().put("fingerPrintCapture", true);
					} else if(!RegistrationConstants.DISABLE.equalsIgnoreCase(
								String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_DISABLE_FLAG)))) {
						SessionContext.map().put("documentScan", true);
					}else {
						SessionContext.map().put("demographicDetail", true);
					}
					registrationController.showUINUpdateCurrentPage();
				} else {
					registrationController.showCurrentPage(RegistrationConstants.FACE_CAPTURE,
							getPageDetails(RegistrationConstants.FACE_CAPTURE, RegistrationConstants.PREVIOUS));
				}
			} catch (RuntimeException runtimeException) {
				LOGGER.error("REGISTRATION - COULD NOT GO TO DEMOGRAPHIC TITLE PANE ", APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			}
		}
	}

	/**
	 * 
	 * To set the captured image to the imageView in the Applicant Biometrics page
	 * 
	 * @param capturedImage the image that is captured
	 * @param photoType     the type of image whether exception image or applicant
	 *                      image
	 */
	@Override
	public void saveApplicantPhoto(BufferedImage capturedImage, String photoType) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening WebCamera to capture photograph");

		if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE)) {

			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			applicantImage.setImage(capture);
			applicantBufferedImage = capturedImage;
			applicantImageCaptured = true;
			applicantImagePane.getStyleClass().add("photoCapturePanesSelected");
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
						RegistrationConstants.APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			}
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE)) {
			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			exceptionImage.setImage(capture);
			exceptionImagePane.getStyleClass().add("photoCapturePanesSelected");
			exceptionBufferedImage = capturedImage;
			exceptionImageCaptured = true;
		}
	}

	@Override
	public void calculateRecaptureTime(String photoType) {
		int configuredSeconds = Integer
				.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.FACE_RECAPTURE_TIME)));

		if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE)) {
			/* Set Time which last photo was captured */
			lastPhotoCaptured = getCurrentTimestamp();
			if (!validatePhotoTimer(lastPhotoCaptured, configuredSeconds, photoAlert)) {
				takePhoto.setDisable(true);
			}
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE)) {
			/* Set Time which last Exception photo was captured */
			lastExceptionPhotoCaptured = getCurrentTimestamp();
			if (!validatePhotoTimer(lastExceptionPhotoCaptured, configuredSeconds, photoAlert)) {
				takePhoto.setDisable(true);
			}
		}
	}

	/**
	 * 
	 * To clear the captured image from the imageView in the Applicant Biometrics
	 * page
	 *
	 * @param photoType the type of image that is to be cleared, whether exception
	 *                  image or applicant image
	 */
	@Override
	public void clearPhoto(String photoType) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "clearing the image that is captured");

		if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE) && applicantBufferedImage != null) {
			applicantImage.setImage(defaultImage);
			applicantBufferedImage = null;
			applicantImageCaptured = false;
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE) && exceptionBufferedImage != null) {
			exceptionImage.setImage(defaultExceptionImage);
			exceptionBufferedImage = null;
			exceptionImageCaptured = false;
		}
	}

	/**
	 * To validate the applicant image while going to next section
	 */
	private boolean validateApplicantImage() {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "validating applicant biometrics");

		Boolean toggleBiometricException = (Boolean) SessionContext.userContext().getUserMap()
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

	public void clearExceptionImage() {
		exceptionBufferedImage = null;
		exceptionImage.setImage(defaultExceptionImage);
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

	@FXML
	private void enableCapture(MouseEvent mouseEvent) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Enabling the capture button based on selected pane");

		boolean hasBiometricException = false;
		if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			hasBiometricException = (Boolean) SessionContext.userContext().getUserMap()
					.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);
		}
		Pane sourcePane = (Pane) mouseEvent.getSource();
		sourcePane.requestFocus();
		selectedPhoto = sourcePane;
		takePhoto.setDisable(true);
		if (selectedPhoto.getId().equals(RegistrationConstants.APPLICANT_PHOTO_PANE)) {
			if (validatePhotoTimer(lastPhotoCaptured,
					Integer.parseInt(
							String.valueOf(ApplicationContext.map().get(RegistrationConstants.FACE_RECAPTURE_TIME))),
					photoAlert)) {
				takePhoto.setDisable(false);
				photoAlert.setVisible(false);
			}
		} else if (selectedPhoto.getId().equals(RegistrationConstants.EXCEPTION_PHOTO_PANE) && hasBiometricException
				&& validatePhotoTimer(lastExceptionPhotoCaptured,
						Integer.parseInt(String
								.valueOf(ApplicationContext.map().get(RegistrationConstants.FACE_RECAPTURE_TIME))),
						photoAlert)) {
			takePhoto.setDisable(false);
			photoAlert.setVisible(false);
		}
	}

	/**
	 * To open webcam window to capture image for selected type
	 */
	@FXML
	private void takePhoto() {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening Webcam Window to capture Image");

		if (selectedPhoto.getId().equals(RegistrationConstants.APPLICANT_PHOTO_PANE)) {
			if (webCameraController.webCameraPane == null
					|| !(webCameraController.webCameraPane.getScene().getWindow().isShowing())) {
				openWebCamWindow(RegistrationConstants.APPLICANT_IMAGE);
			}
		} else if (selectedPhoto.getId().equals(RegistrationConstants.EXCEPTION_PHOTO_PANE)) {
			if (webCameraController.webCameraPane == null
					|| !(webCameraController.webCameraPane.getScene().getWindow().isShowing())) {
				openWebCamWindow(RegistrationConstants.EXCEPTION_IMAGE);
			}
		}
	}

	/**
	 * To validate the time of last capture to allow re-capture
	 * 
	 * @param lastPhoto      the timestamp when last photo is captured
	 * @param configuredSecs the configured number of seconds for re-capture
	 * @param photoLabel     the label to show the timer for re-capture
	 * @return boolean returns true if recapture is allowed
	 */
	private boolean validatePhotoTimer(Timestamp lastPhoto, int configuredSecs, Label photoLabel) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating time to allow re-capture");

		if (lastPhoto == null) {
			return true;
		}

		int diffSeconds = Seconds.secondsBetween(new DateTime(lastPhoto.getTime()), DateTime.now()).getSeconds();
		if (diffSeconds >= configuredSecs) {
			return true;
		} else {
			setTimeLabel(photoLabel, configuredSecs, diffSeconds);
			return false;
		}
	}

	/**
	 * To set the label that displays time left to re-capture
	 * 
	 * @param photoLabel     the label to show the timer for re-capture
	 * @param configuredSecs the configured number of seconds for re-capture
	 * @param diffSeconds    the difference between last captured time and present
	 *                       time
	 */
	private void setTimeLabel(Label photoLabel, int configuredSecs, int diffSeconds) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Setting label to display time to recapture");

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

	private BiometricDTO getBiometricDTOFromSession() {
		return (BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA);
	}

	/**
	 * To get the current timestamp
	 * 
	 * @return Timestamp returns the current timestamp
	 */
	private Timestamp getCurrentTimestamp() {
		return Timestamp.from(Instant.now());
	}
}
