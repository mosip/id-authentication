package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_CAPTURE_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.device.ScanPopUpViewController;
import io.mosip.registration.controller.device.WebCameraController;
import io.mosip.registration.device.fp.FingerprintFacade;
import io.mosip.registration.device.iris.IrisFacade;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.UserOnboardService;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
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

/**
 * {@code UserOnboardController} is to capture and display the captured
 * fingerprints,Iris and face.
 * 
 * @author Dinesh Ashokan
 * @version 1.0
 *
 */
@Controller
public class UserOnboardController extends BaseController implements Initializable {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(UserOnboardController.class);

	// Fingerprint
	/** The finger print capture pane. */
	@FXML
	private AnchorPane fingerPrintCapturePane;

	/** The left hand palm pane. */
	@FXML
	private AnchorPane leftHandPalmPane;

	/** The right hand palm pane. */
	@FXML
	private AnchorPane rightHandPalmPane;

	/** The thumb pane. */
	@FXML
	private AnchorPane thumbPane;

	/** The left hand palm imageview. */
	@FXML
	private ImageView leftHandPalmImageview;

	/** The right hand palm imageview. */
	@FXML
	private ImageView rightHandPalmImageview;

	/** The thumb imageview. */
	@FXML
	private ImageView thumbImageview;

	/** The left slap quality score. */
	@FXML
	private Label leftSlapQualityScore;

	/** The right slap quality score. */
	@FXML
	private Label rightSlapQualityScore;

	/** The thumbs quality score. */
	@FXML
	private Label thumbsQualityScore;

	/** The left slap threshold score label. */
	@FXML
	private Label leftSlapThresholdScoreLbl;

	/** The right slap threshold score label. */
	@FXML
	private Label rightSlapThresholdScoreLbl;

	/** The thumbs threshold score label. */
	@FXML
	private Label thumbsThresholdScoreLbl;

	/** The duplicate check label. */
	@FXML
	private Label duplicateCheckLbl;

	/** The selected pane. */
	private AnchorPane selectedPane = null;

	/** The scan btn. */
	@FXML
	private Button scanBtn;

	private BiometricDTO biometricDTO;

	@Autowired
	private ScanPopUpViewController scanPopUpViewController;

	@Autowired
	private FingerprintFacade fingerPrintFacade;	

	private Boolean init = false;
	private String pageName;

	// Iris

	@FXML
	private ImageView leftIrisImage;
	@FXML
	private Label rightIrisThreshold;
	@FXML
	private Label leftIrisQualityScore;
	@FXML
	private Pane rightIrisPane;
	@FXML
	private Pane leftIrisPane;
	@FXML
	private Label leftIrisThreshold;
	@FXML
	private Label rightIrisQualityScore;
	@FXML
	private ImageView rightIrisImage;
	@FXML
	private Button scanIris;
	@FXML
	protected ImageView applicantImage;
	@FXML
	protected ImageView exceptionImage;
	private BufferedImage applicantBufferedImage;
	private BufferedImage exceptionBufferedImage;
	private boolean applicantImageCaptured;
	private boolean exceptionImageCaptured;
	private Image defaultImage;

	private Pane selectedIris;

	@Autowired
	private IrisFacade irisFacade;

	// WebCam
	@Autowired
	private WebCameraController webCameraController;

	@Autowired
	private UserOnboardService userOnboardService;

	private Stage popupStage;

	/**
	 * This method initializes the Fingerprint,Iris and face Capture page.
	 * 
	 * 
	 */

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		if (init) {

			if (pageName == RegistrationConstants.FINGERPRINT) {

				LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
						"Loading of FingerprintCapture screen started");
				try {
					scanBtn.setDisable(true);

					EventHandler<Event> mouseClick = event -> {
						if (event.getSource() instanceof AnchorPane) {
							AnchorPane sourcePane = (AnchorPane) event.getSource();
							sourcePane.requestFocus();
							selectedPane = sourcePane;
							scanBtn.setDisable(true);
							duplicateCheckLbl.setText("");

							// Get the Fingerprint from RegistrationDTO based on selected Fingerprint Pane
							FingerprintDetailsDTO fpDetailsDTO = getFingerprintBySelectedPane().findFirst()
									.orElse(null);

							if (fpDetailsDTO == null
									|| (fpDetailsDTO.getFingerType().equals(RegistrationConstants.LEFTPALM)
											&& fpDetailsDTO.getQualityScore() < Double
													.parseDouble(getValueFromSessionMap(
															RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD)))
									|| (fpDetailsDTO.getFingerType().equals(RegistrationConstants.RIGHTPALM)
											&& fpDetailsDTO.getQualityScore() < Double
													.parseDouble(getValueFromSessionMap(
															RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD)))
									|| (fpDetailsDTO.getFingerType().equals(RegistrationConstants.THUMBS)
											&& fpDetailsDTO.getQualityScore() < Double
													.parseDouble(getValueFromSessionMap(
															RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD)))) {
								scanBtn.setDisable(false);
							}
						}
					};

					// Add event handler object to mouse click event
					leftHandPalmPane.setOnMouseClicked(mouseClick);
					rightHandPalmPane.setOnMouseClicked(mouseClick);
					thumbPane.setOnMouseClicked(mouseClick);

					leftSlapThresholdScoreLbl.setText(getQualityScore(Double.parseDouble(
							getValueFromSessionMap(RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD))));

					rightSlapThresholdScoreLbl.setText(getQualityScore(Double.parseDouble(
							getValueFromSessionMap(RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD))));
					thumbsThresholdScoreLbl.setText(getQualityScore(Double
							.parseDouble(getValueFromSessionMap(RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD))));

					loadingImageFromSessionContext();

					LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
							"Loading of FingerprintCapture screen ended");
				} catch (RuntimeException runtimeException) {
					LOGGER.error(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
							String.format(
									"%s -> Exception while initializing Fingerprint Capture page for user registration  %s",
									RegistrationConstants.USER_REG_FINGERPRINT_PAGE_LOAD_EXP,
									runtimeException.getMessage()));

					throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_PAGE_LOAD_EXP,
							String.format(
									"Exception while initializing Fingerprint Capture page for user registration  %s",
									runtimeException.getMessage()));
				}
			} else if (pageName == RegistrationConstants.EYE) {
				try {
					LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
							"Initializing Iris Capture page for user registration");

					// Set Threshold
					String irisThreshold = getValueFromApplicationMap(RegistrationConstants.IRIS_THRESHOLD);
					leftIrisThreshold.setText(irisThreshold.concat(RegistrationConstants.PERCENTAGE));
					rightIrisThreshold.setText(irisThreshold.concat(RegistrationConstants.PERCENTAGE));

					// Disable Scan button
					scanIris.setDisable(true);

					// Display the Captured Iris
					if (biometricDTO.getOperatorBiometricDTO().getIrisDetailsDTO() != null) {
						for (IrisDetailsDTO capturedIris : getIrises()) {
							if (capturedIris.getIrisType().contains(RegistrationConstants.LEFT)) {
								leftIrisImage.setImage(convertBytesToImage(capturedIris.getIris()));
								leftIrisQualityScore.setText(getQualityScoreAsString(capturedIris.getQualityScore()));
							} else if (capturedIris.getIrisType().contains(RegistrationConstants.RIGHT)) {
								rightIrisImage.setImage(convertBytesToImage(capturedIris.getIris()));
								rightIrisQualityScore.setText(getQualityScoreAsString(capturedIris.getQualityScore()));
							}
						}
					}

					LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
							"Initializing Iris Capture page for user registration completed");
				} catch (RuntimeException runtimeException) {
					LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
							"%s -> Exception while initializing Iris Capture page for user registration  %s",
							RegistrationConstants.USER_REG_IRIS_CAPTURE_PAGE_LOAD_EXP, runtimeException.getMessage()));

					throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_CAPTURE_PAGE_LOAD_EXP,
							String.format("Exception while initializing Iris Capture page for user registration  %s",
									runtimeException.getMessage()));
				}

			} else if (pageName == "webCam") {
				defaultImage = applicantImage.getImage();
				applicantImageCaptured = false;
				exceptionImageCaptured = false;
				exceptionBufferedImage = null;

				if (biometricDTO.getOperatorBiometricDTO().getFaceDetailsDTO().getFace() != null) {
					applicantImage.setImage(
							convertBytesToImage(biometricDTO.getOperatorBiometricDTO().getFaceDetailsDTO().getFace()));
				}
			}
		}
	}

	@FXML
	private void initUserOnboard() {
		loadPage("/fxml/BiometricException.fxml");
	}

	/**
	 * Method to load the biometric fingerprint page
	 */
	public void loadFingerPrint() {
		init = true;
		pageName = RegistrationConstants.FINGERPRINT;
		biometricDTO = new BiometricDTO();
		biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
		loadPage(RegistrationConstants.USER_ONBOARD_FP);
	}

	private Stream<FingerprintDetailsDTO> getFingerprintBySelectedPane() {
		return biometricDTO.getOperatorBiometricDTO().getFingerprintDetailsDTO().stream().filter(fingerprint -> {
			String fingerType;
			if (StringUtils.containsIgnoreCase(selectedPane.getId(), leftHandPalmPane.getId())) {
				fingerType = RegistrationConstants.LEFTPALM;
			} else {
				if (StringUtils.containsIgnoreCase(selectedPane.getId(), rightHandPalmPane.getId())) {
					fingerType = RegistrationConstants.RIGHTPALM;
				} else {
					fingerType = RegistrationConstants.THUMBS;
				}
			}
			return fingerprint.getFingerType().contains(fingerType);
		});
	}

	/**
	 * Validating finger prints.
	 *
	 * @return true, if successful
	 */
	private boolean validateFingerPrints() {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating Fingerprints captured started");

			List<FingerprintDetailsDTO> segmentedFingerprintDetailsDTOs = new ArrayList<>();
			boolean isValid = false;
			boolean isleftHandSlapCaptured = false;
			boolean isrightHandSlapCaptured = false;
			boolean isthumbsCaptured = false;

			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = biometricDTO.getOperatorBiometricDTO()
					.getFingerprintDetailsDTO();

			for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
				for (FingerprintDetailsDTO segmentedFingerprintDetailsDTO : fingerprintDetailsDTO
						.getSegmentedFingerprints()) {
					segmentedFingerprintDetailsDTOs.add(segmentedFingerprintDetailsDTO);
				}
			}

			for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
				if (validateQualityScore(fingerprintDetailsDTO)) {
					if (fingerprintDetailsDTO.getFingerType().equalsIgnoreCase(RegistrationConstants.LEFTPALM)) {
						isleftHandSlapCaptured = true;
					} else if (fingerprintDetailsDTO.getFingerType()
							.equalsIgnoreCase(RegistrationConstants.RIGHTPALM)) {
						isrightHandSlapCaptured = true;
					} else if (fingerprintDetailsDTO.getFingerType().equalsIgnoreCase(RegistrationConstants.THUMBS)) {
						isthumbsCaptured = true;
					}
				} else {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.IRIS_QUALITY_SCORE_ERROR);
					return isValid;
				}
			}

			if (isleftHandSlapCaptured && isrightHandSlapCaptured && isthumbsCaptured) {
				isValid = true;
			} else {
				generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.FINGERPRINT_SCAN_ALERT);
			}
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating Fingerprints captured ended");
			return isValid;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_VALIDATION_EXP,
					String.format("Exception while validating the captured fingerprints of individual: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * Validating quality score of captured fingerprints.
	 *
	 * @param fingerprintDetailsDTO the fingerprint details DTO
	 * @return true, if successful
	 */
	private boolean validateQualityScore(FingerprintDetailsDTO fingerprintDetailsDTO) {
		try {
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating quality score of captured fingerprints started");
			if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.LEFTPALM)) {
				return fingerprintDetailsDTO.getQualityScore() >= Double
						.parseDouble(getValueFromSessionMap(RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD))
						|| (fingerprintDetailsDTO.getQualityScore() < Double.parseDouble(
								getValueFromSessionMap(RegistrationConstants.LEFTSLAP_FINGERPRINT_THRESHOLD)))
						|| fingerprintDetailsDTO.isForceCaptured();
			} else if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.RIGHTPALM)) {
				return fingerprintDetailsDTO.getQualityScore() >= Double
						.parseDouble(getValueFromSessionMap(RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD))
						|| (fingerprintDetailsDTO.getQualityScore() < Double.parseDouble(
								getValueFromSessionMap(RegistrationConstants.RIGHTSLAP_FINGERPRINT_THRESHOLD)))
						|| fingerprintDetailsDTO.isForceCaptured();
			} else if (fingerprintDetailsDTO.getFingerType().equals(RegistrationConstants.THUMBS)) {
				return fingerprintDetailsDTO.getQualityScore() >= Double
						.parseDouble(getValueFromSessionMap(RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD))
						|| (fingerprintDetailsDTO.getQualityScore() < Double.parseDouble(
								getValueFromSessionMap(RegistrationConstants.THUMBS_FINGERPRINT_THRESHOLD)))
						|| fingerprintDetailsDTO.isForceCaptured();
			}
			LOGGER.debug(LOG_REG_FINGERPRINT_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating quality score of captured fingerprints ended");
			return false;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCORE_VALIDATION_EXP,
					String.format(
							"Exception while validating the quality score of captured Fingerprints: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	private String getValueFromSessionMap(String key) {
		return (String) applicationContext.getApplicationMap().get(key);
	}

	private String getQualityScore(Double qulaityScore) {
		return String.valueOf(Math.round(qulaityScore)).concat(RegistrationConstants.PERCENTAGE);
	}

	private void loadingImageFromSessionContext() {

		if (null != biometricDTO.getOperatorBiometricDTO().getFingerprintDetailsDTO()) {
			biometricDTO.getOperatorBiometricDTO().getFingerprintDetailsDTO().forEach(item -> {
				if (item.getFingerType().equals(RegistrationConstants.LEFTPALM)) {
					leftHandPalmImageview.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
					leftSlapQualityScore.setText(getQualityScore(item.getQualityScore()));
				} else if (item.getFingerType().equals(RegistrationConstants.RIGHTPALM)) {
					rightHandPalmImageview.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
					rightSlapQualityScore.setText(getQualityScore(item.getQualityScore()));
				} else if (item.getFingerType().equals(RegistrationConstants.THUMBS)) {
					thumbImageview.setImage(new Image(new ByteArrayInputStream(item.getFingerPrint())));
					thumbsQualityScore.setText(getQualityScore(item.getQualityScore()));
				}
			});
		}
	}

	private BiometricInfoDTO createBiometricInfoDTO() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setBiometricExceptionDTO(new ArrayList<>());
		biometricInfoDTO.setFingerprintDetailsDTO(new ArrayList<>());
		biometricInfoDTO.setIrisDetailsDTO(new ArrayList<>());
		FaceDetailsDTO obj = new FaceDetailsDTO();
		biometricInfoDTO.setFaceDetailsDTO(obj);
		return biometricInfoDTO;
	}

	/**
	 * This method displays the Biometric Scan pop-up window. This method will be
	 * invoked when Scan button is clicked.
	 */
	public void scan() {
		if (pageName == RegistrationConstants.FINGERPRINT) {
			scanPopUpViewController.init(this, RegistrationConstants.FINGERPRINT);
		} else if (pageName == RegistrationConstants.EYE) {
			scanPopUpViewController.init(this, "Iris");
			// Disable the scan button
			scanIris.setDisable(true);
		}

	}

	public void goToNextPage() {
		if (validateFingerPrints()) {
			pageName = RegistrationConstants.EYE;
			loadPage(RegistrationConstants.USER_ONBOARD_IRIS);
		}
	}

	/**
	 * This method will be invoked when Next button is clicked. The next section
	 * will be displayed.
	 */
	@FXML
	private void previousSection() {
		pageName = RegistrationConstants.FINGERPRINT;
		loadPage(RegistrationConstants.USER_ONBOARD_FP);
	}

	/**
	 * This method will be invoked when Previous button is clicked. The previous
	 * section will be displayed.
	 */
	@FXML
	private void nextSection() {
		if (validateIris()) {
			pageName = "webCam";
			loadPage(RegistrationConstants.USER_ONBOARD_WEBCAM);
		}
	}

	@Override
	public void scan(Stage popupStage) {
		if (pageName == RegistrationConstants.FINGERPRINT) {

			try {

				FingerprintDetailsDTO detailsDTO = null;

				List<FingerprintDetailsDTO> fingerprintDetailsDTOs = biometricDTO.getOperatorBiometricDTO()
						.getFingerprintDetailsDTO();

				if (fingerprintDetailsDTOs == null || fingerprintDetailsDTOs.isEmpty()) {
					fingerprintDetailsDTOs = new ArrayList<>(3);
					biometricDTO.getOperatorBiometricDTO().setFingerprintDetailsDTO(fingerprintDetailsDTOs);
				}

				if (selectedPane.getId() == leftHandPalmPane.getId()) {

					scanFingers(detailsDTO, fingerprintDetailsDTOs, RegistrationConstants.LEFTPALM,

							RegistrationConstants.LEFTHAND_SEGMNTD_FILE_PATHS, leftHandPalmImageview,

							leftSlapQualityScore, popupStage);

				} else if (selectedPane.getId() == rightHandPalmPane.getId()) {

					if (SessionContext.getInstance().getMapObject()
							.containsKey(RegistrationConstants.DUPLICATE_FINGER)) {

						scanFingers(detailsDTO, fingerprintDetailsDTOs, RegistrationConstants.RIGHTPALM,

								RegistrationConstants.RIGHTHAND_SEGMNTD_FILE_PATHS, rightHandPalmImageview,

								rightSlapQualityScore, popupStage);

					} else {
						scanFingers(detailsDTO, fingerprintDetailsDTOs, RegistrationConstants.RIGHTPALM,

								RegistrationConstants.RIGHTHAND_SEGMNTD_DUPLICATE_FILE_PATHS, rightHandPalmImageview,

								rightSlapQualityScore, popupStage);
					}

				} else if (selectedPane.getId() == thumbPane.getId()) {

					scanFingers(detailsDTO, fingerprintDetailsDTOs, RegistrationConstants.THUMBS,

							RegistrationConstants.THUMBS_SEGMNTD_FILE_PATHS, thumbImageview, thumbsQualityScore,
							popupStage);

				}
			} catch (RuntimeException runtimeException) {

			} catch (RegBaseCheckedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (pageName == RegistrationConstants.EYE) {
			try {
				LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
						"Scanning of iris details for user registration");

				Optional<IrisDetailsDTO> captiredIrisDetailsDTO = getIrisBySelectedPane().findFirst();

				IrisDetailsDTO irisDetailsDTO = null;
				if (!captiredIrisDetailsDTO.isPresent()) {
					irisDetailsDTO = new IrisDetailsDTO();
					getIrises().add(irisDetailsDTO);
				} else {
					irisDetailsDTO = captiredIrisDetailsDTO.get();
					irisDetailsDTO.setNumOfIrisRetry(irisDetailsDTO.getNumOfIrisRetry() + 1);
				}

				String irisType = StringUtils.containsIgnoreCase(selectedIris.getId(), RegistrationConstants.LEFT)
						? RegistrationConstants.LEFT
						: RegistrationConstants.RIGHT;

				irisFacade.getIrisImageAsDTO(irisDetailsDTO, irisType.concat(RegistrationConstants.EYE));

				// Display the Scanned Iris Image in the Scan pop-up screen
				scanPopUpViewController.getScanImage().setImage(convertBytesToImage(irisDetailsDTO.getIris()));

				generateAlert(RegistrationConstants.ALERT_INFORMATION, "Iris captured successfully");

				if (irisType.equals(RegistrationConstants.LEFT)) {
					leftIrisImage.setImage(convertBytesToImage(irisDetailsDTO.getIris()));
					leftIrisQualityScore.setText(getQualityScoreAsString(irisDetailsDTO.getQualityScore()));
				} else {
					rightIrisImage.setImage(convertBytesToImage(irisDetailsDTO.getIris()));
					rightIrisQualityScore.setText(getQualityScoreAsString(irisDetailsDTO.getQualityScore()));
				}

				popupStage.close();

				LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
						"Scanning of iris details for user registration completed");
			} catch (RegBaseCheckedException regBaseCheckedException) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.IRIS_SCANNING_ERROR);
			} catch (RuntimeException runtimeException) {
				LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, String.format(
						"%s Exception while getting the scanned iris details for user registration: %s caused by %s",
						RegistrationConstants.USER_REG_IRIS_SAVE_EXP, runtimeException.getMessage(),
						runtimeException.getCause()));

				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.IRIS_SCANNING_ERROR);
			} finally {
				selectedIris.requestFocus();
			}
		}
	}

	private void scanFingers(FingerprintDetailsDTO detailsDTO, List<FingerprintDetailsDTO> fingerprintDetailsDTOs,
			String fingerType, String[] segmentedFingersPath, ImageView fingerImageView, Label scoreLabel,
			Stage popupStage) throws RegBaseCheckedException {

		ImageView imageView = fingerImageView;
		Label qualityScoreLabel = scoreLabel;
		if (fingerprintDetailsDTOs != null) {

			for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
				if (fingerprintDetailsDTO.getFingerType().equals(fingerType)) {
					detailsDTO = fingerprintDetailsDTO;

					for (String segmentedFingerPath : segmentedFingersPath) {
						String[] path = segmentedFingerPath.split("/");
						for (FingerprintDetailsDTO segmentedfpDetailsDTO : fingerprintDetailsDTO
								.getSegmentedFingerprints()) {
							if (segmentedfpDetailsDTO.getFingerType().equals(path[3])) {
								fingerprintDetailsDTO.getSegmentedFingerprints().remove(segmentedfpDetailsDTO);
								break;
							}
						}
					}

					// detailsDTO.setNumRetry(fingerprintDetailsDTO.getNumRetry() + 1);
					break;
				}
			}
			if (detailsDTO == null) {
				detailsDTO = new FingerprintDetailsDTO();
				fingerprintDetailsDTOs.add(detailsDTO);
			}
		}
		fingerPrintFacade.getFingerPrintImageAsDTO(detailsDTO, fingerType);

		fingerPrintFacade.segmentFingerPrintImage(detailsDTO, segmentedFingersPath);

		scanPopUpViewController.getScanImage().setImage(convertBytesToImage(detailsDTO.getFingerPrint()));

		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.FP_CAPTURE_SUCCESS);

		popupStage.close();

		imageView.setImage(convertBytesToImage(detailsDTO.getFingerPrint()));
		qualityScoreLabel.setText(getQualityScore(detailsDTO.getQualityScore()));
		scanBtn.setDisable(true);
	}

	/**
	 * This event handler will be invoked when left iris or right iris {@link Pane}
	 * is clicked.
	 * 
	 * @param mouseEvent the triggered {@link MouseEvent} object
	 */
	@FXML
	private void enableScan(MouseEvent mouseEvent) {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Enabling scan button for user registration");

			Pane sourcePane = (Pane) mouseEvent.getSource();
			sourcePane.requestFocus();
			selectedIris = sourcePane;
			scanIris.setDisable(true);

			// Get the Iris from RegistrationDTO based on selected Iris Pane
			IrisDetailsDTO irisDetailsDTO = getIrisBySelectedPane().findFirst().orElse(null);

			boolean isExceptionIris = getIrisExceptions().stream()
					.anyMatch(exceptionIris -> StringUtils.containsIgnoreCase(exceptionIris.getBiometricType(),
							StringUtils.containsIgnoreCase(selectedIris.getId(), RegistrationConstants.LEFT)
									? RegistrationConstants.LEFT
									: RegistrationConstants.RIGHT));

			// Enable the scan button, if any of the following satisfies
			// 1. If Iris was not scanned
			// 2. Quality score of the scanned image is less than threshold
			// 3. If iris is not forced captured
			// 4. If iris is an exception iris
			if (irisDetailsDTO == null || isExceptionIris
					|| (Double.compare(irisDetailsDTO.getQualityScore(),
							Double.parseDouble(getValueFromApplicationMap(RegistrationConstants.IRIS_THRESHOLD))) < 0)
					|| irisDetailsDTO.isForceCaptured()) {
				scanIris.setDisable(false);
			}

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Enabling scan button for user registration completed");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while enabling scan button for user registration  %s %s",
							RegistrationConstants.USER_REG_IRIS_CAPTURE_POPUP_LOAD_EXP, runtimeException.getMessage(),
							runtimeException.getStackTrace()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.UNABLE_LOAD_IRIS_SCAN_POPUP);
		}
	}

	private String getValueFromApplicationMap(String key) {
		return (String) applicationContext.getApplicationMap().get(key);
	}

	private List<IrisDetailsDTO> getIrises() {
		return biometricDTO.getOperatorBiometricDTO().getIrisDetailsDTO();
	}

	private String getQualityScoreAsString(double qualityScore) {
		return String.valueOf(Math.round(qualityScore)).concat(RegistrationConstants.PERCENTAGE);
	}

	private Stream<IrisDetailsDTO> getIrisBySelectedPane() {
		return getIrises().stream()
				.filter(iris -> iris.getIrisType()
						.contains(StringUtils.containsIgnoreCase(selectedIris.getId(), RegistrationConstants.LEFT)
								? RegistrationConstants.LEFT
								: RegistrationConstants.RIGHT));
	}

	private List<BiometricExceptionDTO> getIrisExceptions() {
		return biometricDTO.getOperatorBiometricDTO().getBiometricExceptionDTO();
	}

	private boolean validateIris() {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the captured irises of individual");

			boolean isValid = false;
			boolean isLeftEyeCaptured = false;
			boolean isRightEyeCaptured = false;

			for (BiometricExceptionDTO exceptionIris : getIrisExceptions()) {
				if (exceptionIris.getMissingBiometric()
						.equalsIgnoreCase(RegistrationConstants.LEFT.concat(RegistrationConstants.EYE))) {
					isLeftEyeCaptured = true;
				} else if (exceptionIris.getMissingBiometric()
						.equalsIgnoreCase(RegistrationConstants.RIGHT.concat(RegistrationConstants.EYE))) {
					isRightEyeCaptured = true;
				}
			}

			for (IrisDetailsDTO irisDetailsDTO : getIrises()) {
				if (validateIrisCapture(irisDetailsDTO)) {
					if (irisDetailsDTO.getIrisType()
							.equalsIgnoreCase(RegistrationConstants.LEFT.concat(RegistrationConstants.EYE))) {
						isLeftEyeCaptured = true;
					} else if (irisDetailsDTO.getIrisType()
							.equalsIgnoreCase(RegistrationConstants.RIGHT.concat(RegistrationConstants.EYE))) {
						isRightEyeCaptured = true;
					}
				} else {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.IRIS_QUALITY_SCORE_ERROR);
					return isValid;
				}
			}

			if (isLeftEyeCaptured && isRightEyeCaptured) {
				isValid = true;
			} else {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.IRIS_VALIDATION_ERROR);
			}

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the captured irises of individual completed");

			return isValid;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_VALIDATION_EXP,
					String.format("Exception while validating the captured irises of individual: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	private boolean validateIrisCapture(IrisDetailsDTO irisDetailsDTO) {
		try {
			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the quality score of the captured iris");

			// Get Configured Threshold and Number of Retries from properties file
			double irisThreshold = Double.parseDouble(getValueFromApplicationMap(RegistrationConstants.IRIS_THRESHOLD));

			LOGGER.debug(LOG_REG_IRIS_CAPTURE_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					"Validating the quality score of the captured iris completed");

			return irisDetailsDTO.getQualityScore() >= irisThreshold
					|| (Double.compare(irisDetailsDTO.getQualityScore(), irisThreshold) < 0)
					|| irisDetailsDTO.isForceCaptured();
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_SCORE_VALIDATION_EXP,
					String.format("Exception while validating the quality score of captured iris: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	// WebCam

	@FXML
	private void goToPreviousPane() {
		pageName = RegistrationConstants.EYE;
		loadPage(RegistrationConstants.USER_ONBOARD_IRIS);

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
		try {
			Stage primaryStage = new Stage();
			FXMLLoader loader = BaseController.loadChild(getClass().getResource(RegistrationConstants.WEB_CAMERA_PAGE));
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
	}

	@Override
	public void saveApplicantPhoto(BufferedImage capturedImage, String photoType) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Opening WebCamera to capture photograph");

		if (photoType.equals(RegistrationConstants.APPLICANT_IMAGE)) {
			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			applicantImage.setImage(capture);
			applicantBufferedImage = capturedImage;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try {
				ImageIO.write(applicantBufferedImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE,
						byteArrayOutputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			biometricDTO.getOperatorBiometricDTO().getFaceDetailsDTO().setFace(byteArrayOutputStream.toByteArray());
			applicantImageCaptured = true;
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
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.UNABLE_LOAD_USERONBOARD_SCREEN);
		}
	}

	/**
	 * Validate and save operator biometrics
	 */
	@FXML
	private void submit() {

		ResponseDTO response = userOnboardService.validate(biometricDTO);
		if (response != null && response.getErrorResponseDTOs() != null
				&& response.getErrorResponseDTOs().get(0) != null) {
			generateAlert(RegistrationConstants.ALERT_ERROR, response.getErrorResponseDTOs().get(0).getMessage());
		} else if (response != null && response.getSuccessResponseDTO() != null) {
			try {
				popupStage = new Stage();
				popupStage.initStyle(StageStyle.DECORATED);
				Parent scanPopup = BaseController.load(getClass().getResource("/fxml/UserOnboardSuccess.fxml"));
				popupStage.setResizable(false);
				Scene scene = new Scene(scanPopup);
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				scene.getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
				popupStage.setScene(scene);
				popupStage.initModality(Modality.WINDOW_MODAL);
				popupStage.initOwner(fXComponents.getStage());
				popupStage.show();
			} catch (IOException exception) {
				LOGGER.error("REGISTRATION - USERONBOARD CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
						exception.getMessage());
				generateAlert(RegistrationConstants.ALERT_ERROR,
						RegistrationUIConstants.UNABLE_LOAD_USERONBOARD_SCREEN);
			}
		}
	}

	@FXML
	private void loadLoginScreen() {
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.NEW_USER, false);
		if (popupStage.isShowing()) {
			popupStage.close();
			goToHomePage();
		}
	}
}