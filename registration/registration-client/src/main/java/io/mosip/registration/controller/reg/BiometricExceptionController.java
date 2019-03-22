package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
import io.mosip.registration.controller.device.FingerPrintCaptureController;
import io.mosip.registration.controller.device.IrisCaptureController;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

@Controller
public class BiometricExceptionController extends BaseController implements Initializable {

	@FXML
	private ImageView leftEye;

	@FXML
	private ImageView trackerImage;

	@FXML
	private ImageView rightEye;

	@FXML
	private GridPane fingerPane;
	@FXML
	private GridPane irisPane;
	@FXML
	private ImageView rightLittle;
	@FXML
	private ImageView rightRing;
	@FXML
	private ImageView rightMiddle;
	@FXML
	private ImageView rightIndex;
	@FXML
	private ImageView rightThumb;
	@FXML
	private ImageView leftLittle;
	@FXML
	private ImageView leftRing;
	@FXML
	private ImageView leftMiddle;
	@FXML
	private ImageView leftIndex;
	@FXML
	private ImageView leftThumb;
	@FXML
	private Label employeeCode;
	@FXML
	private Label employeeName;
	@FXML
	private Label machineID;
	@FXML
	private Label regCenterID;
	@FXML
	private Pane leftHandPane;
	@FXML
	private Pane rightHandPane;
	@FXML
	private Pane leftEyePane;
	@FXML
	private Pane rightEyePane;
	@FXML
	private Button previousBtn;
	/*
	 * @FXML private AnchorPane userOnboardTracker;
	 * 
	 * @FXML private ImageView registrationImg;
	 */
	@FXML
	private GridPane biometricException;
	@FXML
	private GridPane operatorExceptionLayout;
	@FXML
	private GridPane registrationExceptionHeader;
	@FXML
	private GridPane operatorExceptionHeader;
	@FXML
	private GridPane exceptionDocProof;
	// @FXML
	// private AnchorPane regExceptionHeader;
	@FXML
	private GridPane userOnboardFooter;
	@FXML
	private GridPane registrationFooter;
	@FXML
	private GridPane spliterLine;

	@Autowired
	private RegistrationController registrationController;

	private static final Logger LOGGER = AppConfig.getLogger(BiometricExceptionController.class);

	@Autowired
	private UserOnboardParentController userOnboardParentController;

	@Autowired
	private FingerPrintCaptureController fingerPrintCaptureController;

	@Autowired
	private IrisCaptureController irisCaptureController;

	@FXML
	private Button continueBtn;
	@FXML
	private Button backBtn;

	private List<String> fingerList = new ArrayList<>();
	private List<String> irisList = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		continueBtn.setDisable(true);
		backBtn.setDisable(true);

		setExceptionImage();
		fingerExceptionListener(rightLittle);
		fingerExceptionListener(rightRing);
		fingerExceptionListener(rightMiddle);
		fingerExceptionListener(rightIndex);
		fingerExceptionListener(rightThumb);
		fingerExceptionListener(leftLittle);
		fingerExceptionListener(leftRing);
		fingerExceptionListener(leftMiddle);
		fingerExceptionListener(leftIndex);
		fingerExceptionListener(leftThumb);
		irisExceptionListener(leftEye);
		irisExceptionListener(rightEye);
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			employeeName.setText(SessionContext.userContext().getName());
			regCenterID
					.setText(SessionContext.userContext().getRegistrationCenterDetailDTO().getRegistrationCenterId());
			employeeCode.setText(SessionContext.userContext().getUserId());
			if (!((Map<String, Map<String, Boolean>>) ApplicationContext.map().get(RegistrationConstants.ONBOARD_MAP))
					.get(RegistrationConstants.BIOMETRIC_EXCEPTION).get(RegistrationConstants.FINGER_PANE)) {
				fingerPane.setManaged(false);
				fingerPane.setVisible(false);
			}
			if (!((Map<String, Map<String, Boolean>>) ApplicationContext.map().get(RegistrationConstants.ONBOARD_MAP))
					.get(RegistrationConstants.BIOMETRIC_EXCEPTION).get(RegistrationConstants.IRIS_PANE)) {
				irisPane.setManaged(false);
				irisPane.setVisible(false);
			}

			// trackerImage.setVisible(false);
			registrationExceptionHeader.setVisible(false);
			exceptionDocProof.setVisible(false);
			// regExceptionHeader.setVisible(false);
			// registrationImg.setVisible(false);
			registrationFooter.setVisible(false);
			userOnboardFooter.setVisible(true);
			// userOnboardTracker.setVisible(true);
			operatorExceptionLayout.setVisible(true);
			operatorExceptionHeader.setVisible(true);
			spliterLine.setVisible(true);
		} else {
			if (!((Map<String, Map<String, Boolean>>) ApplicationContext.map()
					.get(RegistrationConstants.REGISTRATION_MAP)).get(RegistrationConstants.BIOMETRIC_EXCEPTION)
							.get(RegistrationConstants.FINGER_PANE)) {
				fingerPane.setManaged(false);
				fingerPane.setVisible(false);
			}
			if (!((Map<String, Map<String, Boolean>>) ApplicationContext.map()
					.get(RegistrationConstants.REGISTRATION_MAP)).get(RegistrationConstants.BIOMETRIC_EXCEPTION)
							.get(RegistrationConstants.IRIS_PANE)) {
				irisPane.setManaged(false);
				irisPane.setVisible(false);
			}
			exceptionDocProof.setVisible(true);
			// regExceptionHeader.setVisible(true);
			// registrationImg.setVisible(true);
			registrationFooter.setVisible(true);
			registrationExceptionHeader.setVisible(true);
			spliterLine.setVisible(false);
			userOnboardFooter.setVisible(false);
			// userOnboardTracker.setVisible(false);
			operatorExceptionLayout.setVisible(false);
			operatorExceptionHeader.setVisible(false);
		}
	}

	/**
	 * This method is used to capture the finger click from the UI
	 * 
	 * @param fingerLabel
	 */
	private void fingerExceptionListener(ImageView fingerImage) {

		LOGGER.info("REGISTRATION - FINGER_LABEL_LISTENER - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "It will listen the finger click funtionality");

		SimpleBooleanProperty toggleFunctionForFinger = new SimpleBooleanProperty(false);
		toggleFunctionForFinger.addListener(new ChangeListener<Boolean>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue,
			 * java.lang.Object, java.lang.Object)
			 */
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (newValue && !fingerList.contains(fingerImage.getId())) {
					fingerList.add(fingerImage.getId());
					fingerImage.setOpacity(1.0);
				} else {
					if (fingerList.indexOf(fingerImage.getId()) >= 0) {
						fingerList.remove(fingerImage.getId());
					}
					fingerImage.setOpacity(0.0);
				}
				if (fingerList.stream().anyMatch(fingerType -> fingerType.contains("left"))) {
					leftHandPane.getStyleClass().clear();
					leftHandPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
				} else {
					leftHandPane.getStyleClass().clear();
					leftHandPane.getStyleClass().add(RegistrationConstants.REMOVE_BORDER);
				}
				if (fingerList.stream().anyMatch(fingerType -> fingerType.contains("right"))) {
					rightHandPane.getStyleClass().clear();
					rightHandPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
				} else {
					rightHandPane.getStyleClass().clear();
					rightHandPane.getStyleClass().add(RegistrationConstants.REMOVE_BORDER);
				}

				continueBtn.setDisable((fingerList.isEmpty() && irisList.isEmpty()));
				backBtn.setDisable((fingerList.isEmpty() && irisList.isEmpty()));

			}
		});

		fingerImage.setOnMouseClicked(event -> {
			auditFactory.audit(AuditEvent.REG_BIO_EXCEPTION_MARKING, Components.REG_BIOMETRICS, SessionContext.userId(),
					AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

			toggleFunctionForFinger.set(!toggleFunctionForFinger.get());
		});

		LOGGER.info("REGISTRATION - FINGER_LABEL_LISTENER_END - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "End of Functionality");

	}

	/**
	 * This method is used to capture the Iris click from the UI
	 * 
	 * @param irisImage
	 */
	private void irisExceptionListener(ImageView irisImage) {

		LOGGER.info("REGISTRATION - IRIS_EXCEPTION_LISTENER - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "It will listen the iris on click functionality");

		SimpleBooleanProperty toggleFunctionForIris = new SimpleBooleanProperty(false);
		Pane irisPane = (Pane) biometricException.lookup("#" + irisImage.getId() + "Pane");
		toggleFunctionForIris.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				irisPane.getStyleClass().clear();
				if (newValue && !irisList.contains(irisImage.getId())) {
					irisList.add(irisImage.getId());
					irisPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
				} else {
					if (irisList.indexOf(irisImage.getId()) >= 0) {
						irisList.remove(irisImage.getId());
					}
					irisPane.getStyleClass().add(RegistrationConstants.REMOVE_BORDER);
				}

				continueBtn.setDisable((fingerList.isEmpty() && irisList.isEmpty()));
				backBtn.setDisable((fingerList.isEmpty() && irisList.isEmpty()));

			}
		});
		irisImage.setOnMouseClicked(event -> {
			auditFactory.audit(AuditEvent.REG_BIO_EXCEPTION_MARKING, Components.REG_BIOMETRICS, SessionContext.userId(),
					AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

			toggleFunctionForIris.set(!toggleFunctionForIris.get());
		});

		LOGGER.info("REGISTRATION - IRIS_EXCEPTION_LISTENER_END - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "End of Iris Functionality");

	}

	/**
	 * This method will call when click on next button and toggle the visibility
	 */
	public void goToNextPage() {
		auditFactory.audit(AuditEvent.REG_BIO_EXCEPTION_NEXT, Components.REG_BIOMETRICS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		LOGGER.info("REGISTRATION - NEXT_PAGE - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME, APPLICATION_ID,
				"Going to next page");

		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			userOnboardParentController.showCurrentPage(RegistrationConstants.BIOMETRIC_EXCEPTION,
					getOnboardPageDetails(RegistrationConstants.BIOMETRIC_EXCEPTION, RegistrationConstants.NEXT));
			exceptionDTOCreation();
			fingerPrintCaptureController.clearImage();
			irisCaptureController.clearIrisBasedOnExceptions();
		} else {
			exceptionDTOCreation();
			if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {

				List<BiometricExceptionDTO> biometricExceptionDTOs = ((RegistrationDTO) SessionContext.map()
						.get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO().getApplicantBiometricDTO()
								.getBiometricExceptionDTO();

				long fingerPrintCount = biometricExceptionDTOs.stream()
						.filter(bio -> bio.getBiometricType().equals("fingerprint")).count();

				if (getRegistrationDTOFromSession().getSelectionListDTO().isBiometricFingerprint()
						|| fingerPrintCount > 0) {

					SessionContext.map().put("biometricException", false);
					SessionContext.map().put("fingerPrintCapture", true);
				} else {

					SessionContext.map().put("biometricException", false);
					SessionContext.map().put("irisCapture", true);
				}
				registrationController.showUINUpdateCurrentPage();
			} else {
				registrationController.showCurrentPage(RegistrationConstants.BIOMETRIC_EXCEPTION,
						getPageDetails("biometricException", RegistrationConstants.NEXT));
			}
			fingerPrintCaptureController.clearImage();
			irisCaptureController.clearIrisBasedOnExceptions();
		}
	}

	/**
	 * Adding biometric exception details to the Session context
	 */
	private void exceptionDTOCreation() {

		LOGGER.info("REGISTRATION - EXCEPTION_DTO_CREATION - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "Populating the exception dto in session context");
		List<String> bioList = new ArrayList<>();
		bioList.addAll(fingerList);
		bioList.addAll(irisList);
		if (!bioList.isEmpty()) {
			List<BiometricExceptionDTO> biometricExceptionList = new ArrayList<>();
			bioList.forEach(bioType -> {
				BiometricExceptionDTO biometricExceptionDTO = new BiometricExceptionDTO();
				if (bioType.contains("Eye")) {
					biometricExceptionDTO.setBiometricType("iris");
				} else {
					biometricExceptionDTO.setBiometricType("fingerprint");
				}
				biometricExceptionDTO.setMissingBiometric(bioType);
				biometricExceptionDTO.setExceptionType(RegistrationConstants.PERMANENT_EXCEPTION);
				biometricExceptionDTO.setReason(RegistrationConstants.MISSING_BIOMETRICS);
				biometricExceptionDTO.setMarkedAsException(true);
				biometricExceptionList.add(biometricExceptionDTO);
			});
			SessionContext.map().put(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION, biometricExceptionList);
			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)
					|| (boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER_UPDATE)) {
				((BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA))
						.getOperatorBiometricDTO().setBiometricExceptionDTO(biometricExceptionList);
			} else {
				((RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO()
						.getApplicantBiometricDTO().setBiometricExceptionDTO(biometricExceptionList);
			}

		}

		LOGGER.info("REGISTRATION - EXCEPTION_DTO_CREATION_END - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "End of exception dto creation functionality");

	}

	/**
	 * This method will call on click of previous button and toggle the visibility
	 * based
	 */
	public void goToPreviousPage() {
		auditFactory.audit(AuditEvent.REG_BIO_EXCEPTION_BACK, Components.REG_BIOMETRICS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
		LOGGER.info("REGISTRATION - PREVIOUS_PAGE - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME, APPLICATION_ID,
				"It will go to the previous page");

		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER_UPDATE)) {
				loadPage(RegistrationConstants.OFFICER_PACKET_PAGE);
			} else {
				userOnboardParentController.showCurrentPage(RegistrationConstants.BIOMETRIC_EXCEPTION,
						getOnboardPageDetails(RegistrationConstants.BIOMETRIC_EXCEPTION,
								RegistrationConstants.PREVIOUS));
			}
		} else {
			exceptionDTOCreation();
			if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				SessionContext.map().put("biometricException", false);
				if (!RegistrationConstants.DISABLE.equalsIgnoreCase(
						String.valueOf(ApplicationContext.map().get(RegistrationConstants.DOC_DISABLE_FLAG)))) {
					SessionContext.map().put("documentScan", true);
				} else {
					SessionContext.map().put("demographicDetail", true);
				}
				registrationController.showUINUpdateCurrentPage();
			} else {
				registrationController.showCurrentPage(RegistrationConstants.BIOMETRIC_EXCEPTION,
						getPageDetails(RegistrationConstants.BIOMETRIC_EXCEPTION, RegistrationConstants.PREVIOUS));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setExceptionImage() {

		fingerList.clear();
		irisList.clear();

		List<BiometricExceptionDTO> biometricExceptionList = (List<BiometricExceptionDTO>) SessionContext.map()
				.get(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION);

		if (biometricExceptionList != null && !biometricExceptionList.isEmpty()) {

			biometricExceptionList.forEach(bioException -> {

				if (bioException.getMissingBiometric().contains("left")
						&& !bioException.getMissingBiometric().contains("Eye")) {
					fingerList.add(bioException.getMissingBiometric());
					leftHandPane.getStyleClass().clear();
					leftHandPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
					ImageView fingerImage = (ImageView) leftHandPane.lookup("#" + bioException.getMissingBiometric());
					fingerImage.setOpacity(1.0);

				} else if (bioException.getMissingBiometric().contains("right")
						&& !bioException.getMissingBiometric().contains("Eye")) {
					fingerList.add(bioException.getMissingBiometric());
					rightHandPane.getStyleClass().clear();
					rightHandPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
					ImageView fingerImage = (ImageView) rightHandPane.lookup("#" + bioException.getMissingBiometric());
					fingerImage.setOpacity(1.0);

				} else if (bioException.getMissingBiometric().contains("Eye")) {
					irisList.add(bioException.getMissingBiometric());
					Pane irisPane = (Pane) biometricException.lookup("#" + bioException.getMissingBiometric() + "Pane");
					irisPane.getStyleClass().clear();
					irisPane.getStyleClass().add(RegistrationConstants.ADD_BORDER);
				}
			});
		} else {
			rightLittle.setOpacity(0.0);
			rightRing.setOpacity(0.0);
			rightMiddle.setOpacity(0.0);
			rightIndex.setOpacity(0.0);
			rightThumb.setOpacity(0.0);
			leftLittle.setOpacity(0.0);
			leftRing.setOpacity(0.0);
			leftMiddle.setOpacity(0.0);
			leftIndex.setOpacity(0.0);
			leftThumb.setOpacity(0.0);
			leftHandPane.getStyleClass().clear();
			rightHandPane.getStyleClass().clear();
			leftEyePane.getStyleClass().clear();
			rightEyePane.getStyleClass().clear();
		}
	}

	public void clearSession() {
		SessionContext.map().put(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION, new ArrayList<>());
		SessionContext.map().put(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION, new ArrayList<>());
		setExceptionImage();
	}

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
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_USERONBOARD_SCREEN);
		}
	}

	/**
	 * Disable next btn.
	 */
	public void disableNextBtn() {
		continueBtn.setDisable((fingerList.isEmpty() && irisList.isEmpty()));
		backBtn.setDisable((fingerList.isEmpty() && irisList.isEmpty()));
	}

}
