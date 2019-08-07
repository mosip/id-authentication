package io.mosip.registration.controller.device;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import io.mosip.registration.controller.reg.DemographicDetailController;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.controller.reg.UserOnboardParentController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.service.bio.BioService;
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
import javafx.scene.layout.GridPane;
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
	private GridPane applicantImagePane;
	@FXML
	private ImageView applicantImage;
	@FXML
	private GridPane exceptionImagePane;
	@FXML
	private ImageView exceptionImage;
	@FXML
	public Button biometricPrevBtn;
	@FXML
	public Button saveBiometricDetailsBtn;
	@FXML
	private AnchorPane applicantFaceTrackerImg;
	@FXML
	private AnchorPane exceptionFaceTrackerImg;

	@Autowired
	private RegistrationController registrationController;

	@Autowired
	private WebCameraController webCameraController;

	@Autowired
	private UserOnboardParentController userOnboardParentController;

	@Autowired
	private DemographicDetailController demographicDetailController;

	@Autowired
	private GuardianBiometricsController guardianBiometricsController;

	@Autowired
	private BioService bioService;

	@Autowired
	private Streamer streamer;

	private Timestamp lastPhotoCaptured;

	private Timestamp lastExceptionPhotoCaptured;

	@FXML
	private Label registrationNavlabel;

	@FXML
	private Label photoAlert;
	@FXML
	private ImageView backImageView;
	@FXML
	private ImageView scanImageView;
	@FXML
	private ImageView startOverImageView;
	@FXML
	private Button startOverBtn;
	@FXML
	private Label exceptionImageLabel;

	private BufferedImage applicantBufferedImage;
	private byte[] applicantImageIso;
	private BufferedImage exceptionBufferedImage;
	private byte[] exceptionImageIso;
	private Image defaultImage;
	private Image defaultExceptionImage;
	private boolean applicantImageCaptured;
	private boolean exceptionImageCaptured;

	private boolean hasBiometricException;

	private boolean hasLowBiometrics;

	private Map<String, List<String>> lowQualityBiometrics = new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("REGISTRATION - UI - FACE_CAPTURE_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Loading of FaceCapture screen started");

		setImagesOnHover();

		if (getRegistrationDTOFromSession() != null && getRegistrationDTOFromSession().getSelectionListDTO() != null) {
			registrationNavlabel.setText(ApplicationContext.applicationLanguageBundle()
					.getString(RegistrationConstants.UIN_UPDATE_UINUPDATENAVLBL));
		}

		if (getRegistrationDTOFromSession() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory() != null
				&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
						.equals(RegistrationConstants.PACKET_TYPE_LOST)) {
			registrationNavlabel.setText(
					ApplicationContext.applicationLanguageBundle().getString(RegistrationConstants.LOSTUINLBL));
		}

		disableNextButton();

		takePhoto.setDisable(true);

		// for applicant biometrics
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {

			if (getBiometricDTOFromSession() != null
					&& getBiometricDTOFromSession().getOperatorBiometricDTO().getFace().getFace() != null) {
				applicantImage.setImage(convertBytesToImage(
						getBiometricDTOFromSession().getOperatorBiometricDTO().getFace().getFace()));
			} else {
				defaultImage = applicantImage.getImage();
				applicantImageCaptured = false;
			}
		} else {
			hasLowBiometrics = false;
			hasBiometricException = false;

			defaultExceptionImage = new Image(
					getClass().getResourceAsStream(RegistrationConstants.DEFAULT_EXCEPTION_IMAGE_PATH));
			exceptionImage.setImage(defaultExceptionImage);

			defaultImage = applicantImage.getImage();
			defaultExceptionImage = exceptionImage.getImage();
			applicantImageCaptured = false;
			exceptionImageCaptured = false;
			exceptionBufferedImage = null;
		}
	}

	private void setImagesOnHover() {
		Image backInWhite = new Image(getClass().getResourceAsStream(RegistrationConstants.BACK_FOCUSED));
		Image backImage = new Image(getClass().getResourceAsStream(RegistrationConstants.BACK));
		Image scanInWhite = new Image(getClass().getResourceAsStream(RegistrationConstants.SCAN_FOCUSED));
		Image scanImage = new Image(getClass().getResourceAsStream(RegistrationConstants.SCAN));

		biometricPrevBtn.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				backImageView.setImage(backInWhite);
			} else {
				backImageView.setImage(backImage);
			}
		});
		takePhoto.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				scanImageView.setImage(scanInWhite);
			} else {
				scanImageView.setImage(scanImage);
			}
		});
		startOverBtn.hoverProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue) {
				startOverImageView.setImage(scanInWhite);
			} else {
				startOverImageView.setImage(scanImage);
			}
		});
	}

	/**
	 * 
	 * To open camera for the type of image that is to be captured
	 * 
	 * @param imageType type of image that is to be captured
	 */
	public void openWebCamWindow(String imageType) {
		auditFactory.audit(
				RegistrationConstants.APPLICANT_IMAGE.equals(imageType) ? AuditEvent.REG_BIO_FACE_CAPTURE
						: AuditEvent.REG_BIO_EXCEP_FACE_CAPTURE,
				Components.REG_BIOMETRICS, SessionContext.userId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening WebCamera to capture photograph");
		if (bioService.isMdmEnabled())
			streamer.startStream(RegistrationConstants.FACE_FULLFACE, webCameraController.camImageView, null);
		else if (!webCameraController.isWebcamPluggedIn()) 
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.WEBCAM_ALERT_CONTEXT);

		try {
			Stage primaryStage = new Stage();
			primaryStage.initStyle(StageStyle.UNDECORATED);
			FXMLLoader loader = BaseController.loadChild(getClass().getResource(RegistrationConstants.WEB_CAMERA_PAGE));
			Parent webCamRoot = loader.load();

			WebCameraController cameraController = loader.getController();
			cameraController.init(this, imageType);
			Scene scene = new Scene(webCamRoot);
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();
			scene.getStylesheets().add(classLoader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(fXComponents.getStage());
			cameraController.setWebCameraStage(primaryStage);
			primaryStage.show();
		} catch (IOException ioException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
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
				if (registrationController.saveBiometricDetails(applicantBufferedImage, exceptionBufferedImage,
						applicantImageIso, exceptionImageIso)) {
					if (getBiometricDTOFromSession().getOperatorBiometricDTO().getFace().getFace() != null) {
						userOnboardParentController.showCurrentPage(RegistrationConstants.FACE_CAPTURE,
								getOnboardPageDetails(RegistrationConstants.FACE_CAPTURE, RegistrationConstants.NEXT));
					}
				} else {
					applicantBufferedImage = null;
					applicantImageIso = null;
					saveBiometricDetailsBtn.setDisable(true);
				}
			}
		} else {
			if (validateApplicantImage()) {
				registrationController.saveBiometricDetails(applicantBufferedImage, exceptionBufferedImage,
						applicantImageIso, exceptionImageIso);
				applicantFaceTrackerImg.setVisible(false);
				exceptionFaceTrackerImg.setVisible(true);
			}
			if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				demographicDetailController.saveDetail();
			}
		}
		webCameraController.closeWebcam();
	}

	@FXML
	private void goToPreviousPane() {
		auditFactory.audit(AuditEvent.REG_BIO_FACE_CAPTURE_BACK, Components.REG_BIOMETRICS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		webCameraController.closeWebcam();

		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			/*
			 * if (validateOperatorPhoto()) {
			 * registrationController.saveBiometricDetails(applicantBufferedImage,
			 * exceptionBufferedImage); if
			 * (getBiometricDTOFromSession().getOperatorBiometricDTO().getFace().getFace()
			 * != null) { userOnboardParentController.showCurrentPage(RegistrationConstants.
			 * FACE_CAPTURE, getOnboardPageDetails(RegistrationConstants.FACE_CAPTURE,
			 * RegistrationConstants.PREVIOUS)); }
			 */
			// } else {
			userOnboardParentController.showCurrentPage(RegistrationConstants.FACE_CAPTURE,
					getOnboardPageDetails(RegistrationConstants.FACE_CAPTURE, RegistrationConstants.PREVIOUS));
			// }

		} else {

			try {
				if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
					SessionContext.map().put(RegistrationConstants.UIN_UPDATE_FACECAPTURE, false);

					long fingerPrintCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
							.getFingerprintDetailsDTO().stream().count();

					long irisCount = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
							.getIrisDetailsDTO().stream().count();

					long fingerPrintExceptionCount = biomerticExceptionCount(RegistrationConstants.FINGERPRINT);

					long irisExceptionCount = biomerticExceptionCount(RegistrationConstants.IRIS);

					if (fingerPrintExceptionCount == 10 && irisExceptionCount == 2) {
						SessionContext.map().put(RegistrationConstants.UIN_UPDATE_BIOMETRICEXCEPTION, true);
					} else if ((RegistrationConstants.ENABLE
							.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.IRIS_DISABLE_FLAG))
							|| irisCount > 0 || irisExceptionCount > 0) && !isChild()) {
						SessionContext.map().put(RegistrationConstants.UIN_UPDATE_IRISCAPTURE, true);
					} else if ((RegistrationConstants.ENABLE.equalsIgnoreCase(
							getValueFromApplicationContext(RegistrationConstants.FINGERPRINT_DISABLE_FLAG))
							|| fingerPrintCount > 0 || fingerPrintExceptionCount > 0) && !isChild()) {
						SessionContext.map().put(RegistrationConstants.UIN_UPDATE_FINGERPRINTCAPTURE, true);
					} else if (isChild()) {
						SessionContext.map().put(RegistrationConstants.UIN_UPDATE_PARENTGUARDIAN_DETAILS, true);
					} else if (RegistrationConstants.ENABLE
							.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.DOC_DISABLE_FLAG))) {
						SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DOCUMENTSCAN, true);
					} else {
						SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DEMOGRAPHICDETAIL, true);
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
	public void saveApplicantPhoto(BufferedImage capturedImage, String photoType,
			CaptureResponseDto captureResponseDto) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening WebCamera to capture photograph");

		byte[] isoBytes = bioService.getSingleBiometricIsoTemplate(captureResponseDto);
		if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE) && capturedImage != null) {

			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			try {
				applicantImage.setImage(capture);
				applicantBufferedImage = capturedImage;

				if (null != captureResponseDto && null != isoBytes)
					applicantImageIso = isoBytes;

				applicantImageCaptured = true;
				applicantImagePane.getStyleClass().add(RegistrationConstants.PHOTO_CAPTUREPANES_SELECTED);
				if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(applicantBufferedImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE,
							byteArrayOutputStream);
					byte[] photoInBytes = byteArrayOutputStream.toByteArray();
					((BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA))
							.getOperatorBiometricDTO().getFace().setFace(photoInBytes);
					((BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA))
							.getOperatorBiometricDTO().getFace().setFaceISO(isoBytes);
				}
			} catch (Exception ioException) {
				LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			}
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE) && capturedImage != null) {
			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			exceptionImage.setImage(capture);
			exceptionImagePane.getStyleClass().add(RegistrationConstants.PHOTO_CAPTUREPANES_SELECTED);
			exceptionBufferedImage = capturedImage;
			if (null != captureResponseDto && null != isoBytes) {
				exceptionImageIso = isoBytes;
			}
			exceptionImageCaptured = true;
		} else if (photoType.equals(RegistrationConstants.GUARDIAN_IMAGE) && capturedImage != null) {
			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			applicantBufferedImage = capturedImage;
			if (null != captureResponseDto && null != isoBytes)
				applicantImageIso = isoBytes;
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(applicantBufferedImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE,
						byteArrayOutputStream);
				byte[] photoInBytes = byteArrayOutputStream.toByteArray();
				getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO().getFace()
						.setFace(photoInBytes);
				getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO().getFace()
						.setFaceISO(isoBytes);
				guardianBiometricsController.getBiometricImage().setImage(capture);
				guardianBiometricsController.getBiometricPane().getStyleClass().clear();
				guardianBiometricsController.getBiometricPane().getStyleClass()
						.add(RegistrationConstants.FINGERPRINT_PANES_SELECTED);
//				guardianBiometricsController.setParentBufferedImage(capturedImage);
				guardianBiometricsController.getContinueBtn().setDisable(false);
			} catch (Exception ioException) {
				LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						ioException.getMessage() + ExceptionUtils.getStackTrace(ioException));
			}
		}

		if (capturedImage != null)
			capturedImage.flush();
		else
			generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.FACE_SCANNING_ERROR);

		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER) && validateOperatorPhoto()) {
			saveBiometricDetailsBtn.setDisable(false);

		} else if (validateApplicantImage()) {
			applicantFaceTrackerImg.setVisible(false);
			exceptionFaceTrackerImg.setVisible(true);
			saveBiometricDetailsBtn.setDisable(false);
		}
	}

	@Override
	public void calculateRecaptureTime(String photoType) {
		int configuredSeconds = Integer
				.parseInt(getValueFromApplicationContext(RegistrationConstants.FACE_RECAPTURE_TIME));

		if (photoType.equals(RegistrationConstants.GUARDIAN_IMAGE)) {
			Timestamp lastGuardianPhotoCaptured = getCurrentTimestamp();
			if (!validatePhotoTimer(lastGuardianPhotoCaptured, configuredSeconds,
					guardianBiometricsController.getPhotoAlert(), photoType)) {
				guardianBiometricsController.getScanBtn().setDisable(true);
			}
		} else if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE)) {
			/* Set Time which last photo was captured */
			lastPhotoCaptured = getCurrentTimestamp();
			if (!validatePhotoTimer(lastPhotoCaptured, configuredSeconds, photoAlert, photoType)) {
				takePhoto.setDisable(true);
			}
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE)) {
			/* Set Time which last Exception photo was captured */
			lastExceptionPhotoCaptured = getCurrentTimestamp();
			if (!validatePhotoTimer(lastExceptionPhotoCaptured, configuredSeconds, photoAlert, photoType)) {
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

		if (photoType.equalsIgnoreCase(RegistrationConstants.GUARDIAN_IMAGE)) {
			guardianBiometricsController.getBiometricImage().setImage(defaultImage);
			guardianBiometricsController.getContinueBtn().setDisable(true);
		} else if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE) && applicantBufferedImage != null) {
			applicantImage.setImage(defaultImage);
			applicantBufferedImage = null;
			applicantImageIso = null;
			applicantImageCaptured = false;
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE) && exceptionBufferedImage != null) {
			exceptionImage.setImage(defaultExceptionImage);
			exceptionBufferedImage = null;
			exceptionImageIso = null;
			exceptionImageCaptured = false;
		}
		disableNextButton();
	}

	/**
	 * To validate the applicant image while going to next section
	 */
	private boolean validateApplicantImage() {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "validating applicant biometrics");

		boolean imageCaptured = false;
		if (applicantImageCaptured && hasBiometricException) {
			if (exceptionImageCaptured) {
				imageCaptured = true;
			}
		} else {
			if (applicantImageCaptured) {
				imageCaptured = true;
			}
		}
		return imageCaptured;
	}

	private boolean validateOperatorPhoto() {
		return getBiometricDTOFromSession().getOperatorBiometricDTO().getFace().getFace() != null ? true : false;
	}

	public void clearExceptionImage() {
		if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			exceptionBufferedImage = null;
			exceptionImageCaptured = false;
			if (exceptionImage != null)
				exceptionImage.setImage(defaultExceptionImage);
			BiometricInfoDTO applicantBiometricDTO = getFaceDetailsDTO();
			if (applicantBiometricDTO != null && applicantBiometricDTO.getExceptionFace().getFace() != null) {
				applicantBiometricDTO.getExceptionFace().setFace(null);
				applicantBiometricDTO.getExceptionFace().setPhotographName(null);
				applicantBiometricDTO.setHasExceptionPhoto(false);
			}
			disableNextButton();
		}
	}

	/**
	 * 
	 * To enable the capture of applicant/exception image upon validating the
	 * request
	 *
	 * @param mouseEvent the event which occurs on mouse click of 'Take Photo'
	 *                   button
	 */
	@FXML
	private void enableCapture(MouseEvent mouseEvent) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Enabling the capture button based on selected pane");

		/*
		 * check if the applicant has biometric exception
		 */
		if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			hasBiometricException = false;

			boolean hasMissingBiometrics = (Boolean) SessionContext.userContext().getUserMap()
					.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);

			hasLowBiometrics = validateBiometrics(hasBiometricException);

			/* if there is no missing biometric, check for low quality of biometrics */
			hasBiometricException = hasMissingBiometrics || hasLowBiometrics;
			SessionContext.userMap().put(RegistrationConstants.IS_LOW_QUALITY_BIOMETRICS, hasBiometricException);
		}

		/* get the selected pane to capture photo */
		Pane sourcePane = (Pane) mouseEvent.getSource();
		sourcePane.requestFocus();
		selectedPhoto = sourcePane;

		takePhoto.setDisable(true);
		if (selectedPhoto.getId().equals(RegistrationConstants.APPLICANT_PHOTO_PANE)) {
			if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
				applicantFaceTrackerImg.setVisible(true);
				exceptionFaceTrackerImg.setVisible(false);
			}

			if (validatePhotoTimer(lastPhotoCaptured,
					Integer.parseInt(getValueFromApplicationContext(RegistrationConstants.FACE_RECAPTURE_TIME)),
					photoAlert, RegistrationConstants.APPLICANT_IMAGE)) {
				takePhoto.setDisable(false);
				photoAlert.setVisible(false);
			}
		} else if (selectedPhoto.getId().equals(RegistrationConstants.EXCEPTION_PHOTO_PANE) && hasBiometricException
				&& validatePhotoTimer(lastExceptionPhotoCaptured,
						Integer.parseInt(getValueFromApplicationContext(RegistrationConstants.FACE_RECAPTURE_TIME)),
						photoAlert, RegistrationConstants.EXCEPTION_IMAGE)) {
			applicantFaceTrackerImg.setVisible(false);
			exceptionFaceTrackerImg.setVisible(true);
			takePhoto.setDisable(false);
			photoAlert.setVisible(false);
		}
	}

	/**
	 * To validate biometrics to check if the applicant's biometrics are
	 * force-captured or not
	 *
	 * @param hasBiometricException the boolean variable which has to be returned to
	 *                              know whether exception photo should be enabled
	 *                              or not
	 * @return hasBiometricException - the boolean variable which will be returned
	 *         to know whether exception photo should be enabled or not
	 */
	private boolean validateBiometrics(boolean hasBiometricException) {
		lowQualityBiometrics.clear();
		RegistrationDTO registration = getRegistrationDTOFromSession();
		List<FingerprintDetailsDTO> capturedFingers;
		List<IrisDetailsDTO> capturedIrises;

		if (getRegistrationDTOFromSession().isUpdateUINNonBiometric()
				|| (boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
			capturedFingers = registration.getBiometricDTO().getIntroducerBiometricDTO().getFingerprintDetailsDTO();
			capturedIrises = registration.getBiometricDTO().getIntroducerBiometricDTO().getIrisDetailsDTO();
		} else {
			capturedFingers = registration.getBiometricDTO().getApplicantBiometricDTO().getFingerprintDetailsDTO();
			capturedIrises = registration.getBiometricDTO().getApplicantBiometricDTO().getIrisDetailsDTO();
		}
		hasBiometricException = markReasonForFingerprintException(capturedFingers, hasBiometricException);
		hasBiometricException = markReasonForIrisException(capturedIrises, hasBiometricException);

		if (!lowQualityBiometrics.isEmpty()) {
			markReasonForException();
		}

		return hasBiometricException;
	}

	/**
	 * To validate fingerprints if the applicant's fingerprints are force-captured
	 * or not
	 */
	private boolean markReasonForFingerprintException(List<FingerprintDetailsDTO> capturedFingers,
			boolean hasBiometricException) {
		if (capturedFingers != null && !capturedFingers.isEmpty()) {

			String leftSlapQualityThreshold = getValueFromApplicationContext(
					RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD);
			String rightSlapQualityThreshold = getValueFromApplicationContext(
					RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD);
			String thumbQualityThreshold = getValueFromApplicationContext(
					RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD);
			String fingerprintRetries = getValueFromApplicationContext(RegistrationConstants.FINGERPRINT_RETRIES_COUNT);
			List<String> missingFingerTypes = new ArrayList<>();

			for (FingerprintDetailsDTO capturedFinger : capturedFingers) {
				List<FingerprintDetailsDTO> segmentedFingers = capturedFinger.getSegmentedFingerprints();
				for (FingerprintDetailsDTO segmentedFinger : segmentedFingers) {
					if (validateFingerprint(segmentedFinger, leftSlapQualityThreshold, rightSlapQualityThreshold,
							thumbQualityThreshold, fingerprintRetries)) {
						hasBiometricException = true;
						missingFingerTypes.add(segmentedFinger.getFingerType());
					}
				}
			}
			if (!missingFingerTypes.isEmpty()) {
				lowQualityBiometrics.put(RegistrationConstants.FINGERPRINT.toLowerCase(), missingFingerTypes);
			}
		}
		return hasBiometricException;
	}

	/**
	 * To validate irises if the applicant's irises are force-captured or not
	 */
	private boolean markReasonForIrisException(List<IrisDetailsDTO> capturedIrises, boolean hasBiometricException) {
		if (capturedIrises != null && !capturedIrises.isEmpty()) {
			String irisQualityThreshold = getValueFromApplicationContext(RegistrationConstants.IRIS_THRESHOLD);
			String irisRetries = getValueFromApplicationContext(RegistrationConstants.IRIS_RETRY_COUNT);
			double irisThreshold = Double.parseDouble(irisQualityThreshold);
			int numOfRetries = Integer.parseInt(irisRetries);
			List<String> missingIrisTypes = new ArrayList<>();

			for (IrisDetailsDTO capturedIris : capturedIrises) {
				if (validateIris(capturedIris, irisThreshold, numOfRetries)) {
					hasBiometricException = true;
					missingIrisTypes.add(capturedIris.getIrisType());
				}
			}
			if (!missingIrisTypes.isEmpty()) {
				lowQualityBiometrics.put(RegistrationConstants.IRIS.toLowerCase(), missingIrisTypes);
			}
		}
		return hasBiometricException;
	}

	/**
	 * To mark reason for exception if there are any biometrics that are
	 * force-captured
	 */
	private void markReasonForException() {
		List<BiometricExceptionDTO> capturedExceptions;
		if (getRegistrationDTOFromSession().isUpdateUINNonBiometric()
				|| (boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
			capturedExceptions = getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
					.getBiometricExceptionDTO();
		} else {
			capturedExceptions = getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
					.getBiometricExceptionDTO();
		}
		if (capturedExceptions != null) {
			Iterator<BiometricExceptionDTO> iterator = capturedExceptions.iterator();
			while (iterator.hasNext()) {
				BiometricExceptionDTO biometricExceptionDTO = iterator.next();
				if (biometricExceptionDTO.getReason().equalsIgnoreCase(RegistrationConstants.LOW_QUALITY_BIOMETRICS)) {
					iterator.remove();
				}
			}
		} else {
			capturedExceptions = new ArrayList<>();
		}

		for (Entry<String, List<String>> entry : lowQualityBiometrics.entrySet()) {
			List<String> missingBiometricTypes = entry.getValue();
			for (String missingBiometricType : missingBiometricTypes) {
				BiometricExceptionDTO biometricExceptionDTO = new BiometricExceptionDTO();
				biometricExceptionDTO.setBiometricType(entry.getKey());
				biometricExceptionDTO.setMissingBiometric(missingBiometricType);
				biometricExceptionDTO.setReason(RegistrationConstants.LOW_QUALITY_BIOMETRICS);
				biometricExceptionDTO.setExceptionType(RegistrationConstants.TEMPORARY_EXCEPTION);
				biometricExceptionDTO.setIndividualType(RegistrationConstants.INDIVIDUAL);

				capturedExceptions.add(biometricExceptionDTO);
			}
		}

		if (getRegistrationDTOFromSession().isUpdateUINNonBiometric()
				|| (boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
			getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
					.setBiometricExceptionDTO(capturedExceptions);
		} else {
			getRegistrationDTOFromSession().getBiometricDTO().getApplicantBiometricDTO()
					.setBiometricExceptionDTO(capturedExceptions);
		}
	}

	/**
	 * To validate iris whether its quality threshold is met within configured
	 * number of retries
	 */
	private boolean validateIris(IrisDetailsDTO capturedIris, double irisThreshold, int numOfRetries) {
		return (Double.compare(capturedIris.getQualityScore(), irisThreshold) < 0
				&& capturedIris.getNumOfIrisRetry() == numOfRetries);
	}

	/**
	 * To validate fingerprint whether its quality threshold is met within
	 * configured number of retries
	 */
	private boolean validateFingerprint(FingerprintDetailsDTO capturedFinger, String leftSlapQualityThreshold,
			String rightSlapQualityThreshold, String thumbQualityThreshold, String fingerprintRetries) {
		if (capturedFinger.getFingerType().toLowerCase().contains(RegistrationConstants.LEFT.toLowerCase())) {
			return capturedFinger.getQualityScore() < Double.parseDouble(leftSlapQualityThreshold)
					&& capturedFinger.getNumRetry() == Integer.parseInt(fingerprintRetries);
		} else if (capturedFinger.getFingerType().toLowerCase().contains(RegistrationConstants.RIGHT.toLowerCase())) {
			return capturedFinger.getQualityScore() < Double.parseDouble(rightSlapQualityThreshold)
					&& capturedFinger.getNumRetry() == Integer.parseInt(fingerprintRetries);
		} else if (capturedFinger.getFingerType().toLowerCase().contains(RegistrationConstants.THUMBS.toLowerCase())) {
			return capturedFinger.getQualityScore() < Double.parseDouble(thumbQualityThreshold)
					&& capturedFinger.getNumRetry() == Integer.parseInt(fingerprintRetries);
		}
		return false;
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
	private boolean validatePhotoTimer(Timestamp lastPhoto, int configuredSecs, Label photoLabel, String photoType) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating time to allow re-capture");

		if (lastPhoto == null) {
			return true;
		}

		int diffSeconds = Seconds.secondsBetween(new DateTime(lastPhoto.getTime()), DateTime.now()).getSeconds();
		if (diffSeconds >= configuredSecs) {
			return true;
		} else {
			setTimeLabel(photoLabel, configuredSecs, diffSeconds, photoType);
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
	private void setTimeLabel(Label photoLabel, int configuredSecs, int diffSeconds, String photoType) {
		LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Setting label to display time to recapture");

		SimpleIntegerProperty timeDiff = new SimpleIntegerProperty((Integer) (configuredSecs - diffSeconds));

		photoLabel.setVisible(true);
		// Bind the photoLabel text property to the timeDiff property
		photoLabel.textProperty().bind(Bindings.concat(RegistrationUIConstants.RECAPTURE + " ", timeDiff.asString(),
				" " + RegistrationUIConstants.SECONDS));
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds((Integer) (configuredSecs - diffSeconds)), new KeyValue(timeDiff, 1)));
		timeline.setOnFinished(event -> {
			photoLabel.setVisible(false);
			webCameraController.capture.setDisable(false);
			if (photoType.equals(RegistrationConstants.GUARDIAN_IMAGE)) {
				guardianBiometricsController.getScanBtn().setDisable(false);
			}
		});
		timeline.play();
	}

	/**
	 * Clear all captured images.
	 */
	public void clearAllCapturedImages() {
		clearPhoto(RegistrationConstants.APPLICANT_IMAGE);
		clearPhoto(RegistrationConstants.EXCEPTION_IMAGE);
		disableNextButton();
	}

	/**
	 * Disable next button.
	 */
	public void disableNextButton() {
		if (!validateApplicantImage()) {
			saveBiometricDetailsBtn.setDisable(true);
		}
	}

	/**
	 * Sets the value of the exception photo based on the individual whose exception
	 * photo has to be captured. If exception photo of Parent or Guardian is
	 * required, text will be displayed as Parent Or guardian exception photo. While
	 * for Individual, text will be displayed as Exception photo.
	 * 
	 * @param isParentOrGuardianBiometricsCaptured boolean value indicating whose
	 *                                             exception photo has to be
	 *                                             captured either individual or
	 *                                             parent/ guardian
	 */
	public void setExceptionFaceDescriptionText(boolean isParentOrGuardianBiometricsCaptured) {
		ResourceBundle applicationLanguage = ApplicationContext.applicationLanguageBundle();

		String exceptionFaceDescription = applicationLanguage.getString("exceptionimage");

		if (isParentOrGuardianBiometricsCaptured) {
			exceptionFaceDescription = applicationLanguage.getString("parentOrGuardian").concat(" ")
					.concat(exceptionFaceDescription.toLowerCase());
		}

		exceptionImageLabel.setText(exceptionFaceDescription);
	}
}
