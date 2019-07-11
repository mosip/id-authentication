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
import io.mosip.registration.controller.device.GuardianBiometricsController;
import io.mosip.registration.controller.device.IrisCaptureController;
import io.mosip.registration.controller.vo.ExceptionListVO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

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
	private Button previousBtn;
	@FXML
	private GridPane biometricException;
	@FXML
	private GridPane operatorExceptionLayout;
	@FXML
	private GridPane rightEyePaneHolder;
	@FXML
	private GridPane leftEyePaneHolder;
	@FXML
	private GridPane registrationExceptionHeader;
	@FXML
	private GridPane operatorExceptionHeader;
	@FXML
	private GridPane exceptionDocProof;
	@FXML
	private GridPane userOnboardFooter;
	@FXML
	private GridPane registrationFooter;
	@FXML
	private GridPane spliterLine;
	@FXML
	private GridPane onboardTrackerImg;
	@FXML
	private GridPane registrationTrackerImg;
	@FXML
	private TableView<ExceptionListVO> exceptionTable;
	@FXML
	private TableColumn<ExceptionListVO, String> exceptionTableColumn;
	@FXML
	private Label irisExceptionLabel;
	@FXML
	private Label fpExceptionLabel;

	@Autowired
	private RegistrationController registrationController;

	private static final Logger LOGGER = AppConfig.getLogger(BiometricExceptionController.class);

	@Autowired
	private UserOnboardParentController userOnboardParentController;

	@Autowired
	private FingerPrintCaptureController fingerPrintCaptureController;

	@Autowired
	private IrisCaptureController irisCaptureController;

	@Autowired
	private GuardianBiometricsController guardianBiometricsController;

	@FXML
	private Label registrationNavlabel;

	@FXML
	private Button continueBtn;

	private List<String> fingerList = new ArrayList<>();
	private List<String> irisList = new ArrayList<>();
	ResourceBundle applicationLabelBundle;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		applicationLabelBundle = ApplicationContext.getInstance().getApplicationLanguageBundle();

		continueBtn.setDisable(true);

		setExceptionImage();
		fingerException();
		irisException();
		if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
			employeeName.setText(SessionContext.userContext().getName());
			regCenterID
					.setText(SessionContext.userContext().getRegistrationCenterDetailDTO().getRegistrationCenterId());
			employeeCode.setText(SessionContext.userContext().getUserId());
			machineID.setText(getValueFromApplicationContext(RegistrationConstants.USER_STATION_ID));
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
			onboardTrackerImg.setVisible(true);
			registrationTrackerImg.setVisible(false);
			registrationExceptionHeader.setVisible(false);
			exceptionDocProof.setVisible(false);
			registrationFooter.setVisible(false);
			userOnboardFooter.setVisible(true);
			operatorExceptionLayout.setVisible(true);
			operatorExceptionHeader.setVisible(true);
			spliterLine.setVisible(true);
		} else {
			if (getRegistrationDTOFromSession() != null
					&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory() != null
					&& getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
							.equals(RegistrationConstants.PACKET_TYPE_LOST)) {
				registrationNavlabel.setText(
						ApplicationContext.applicationLanguageBundle().getString(RegistrationConstants.LOSTUINLBL));
			}

			if (getRegistrationDTOFromSession() != null
					&& getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				registrationNavlabel.setText(ApplicationContext.applicationLanguageBundle()
						.getString(RegistrationConstants.UIN_UPDATE_UINUPDATENAVLBL));
			}

			if (!((Map<String, Map<String, Boolean>>) ApplicationContext.map()
					.get(RegistrationConstants.REGISTRATION_MAP)).get(RegistrationConstants.BIOMETRIC_EXCEPTION)
							.get(RegistrationConstants.FINGER_PANE)) {
				fingerPane.setManaged(false);
				fingerPane.setVisible(false);
				fpExceptionLabel.setVisible(false);
			}
			if (!((Map<String, Map<String, Boolean>>) ApplicationContext.map()
					.get(RegistrationConstants.REGISTRATION_MAP)).get(RegistrationConstants.BIOMETRIC_EXCEPTION)
							.get(RegistrationConstants.IRIS_PANE)) {
				irisPane.setManaged(false);
				irisPane.setVisible(false);
				irisExceptionLabel.setVisible(false);
			}
			onboardTrackerImg.setVisible(false);
			registrationTrackerImg.setVisible(true);
			exceptionDocProof.setVisible(false);
			registrationFooter.setVisible(true);
			registrationExceptionHeader.setVisible(true);
			spliterLine.setVisible(false);
			userOnboardFooter.setVisible(false);
			operatorExceptionLayout.setVisible(false);
			operatorExceptionHeader.setVisible(false);
		}
	}

	private void irisException() {
		irisExceptionListener(leftEye);
		irisExceptionListener(rightEye);
	}

	public void fingerException() {
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
	}

	public void clearIrisException() {
		irisException();
		rightEyePaneHolder.getStyleClass().clear();
		leftEyePaneHolder.getStyleClass().clear();
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
			 * javafx.beans.value.ChangeListener#changed(javafx.beans.value.
			 * ObservableValue, java.lang.Object, java.lang.Object)
			 */
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (newValue && !fingerList.contains(fingerImage.getId())) {
					fingerList.add(fingerImage.getId());
					fingerImage.setOpacity(1.0);
					showExceptionList();
				} else {
					if (fingerList.indexOf(fingerImage.getId()) >= 0) {
						fingerList.remove(fingerImage.getId());
						showExceptionList();
					}
					fingerImage.setOpacity(0.0);
				}
				continueBtn.setDisable((fingerList.isEmpty() && irisList.isEmpty()));
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

		toggleFunctionForIris.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (newValue && !irisList.contains(irisImage.getId())) {
					irisList.add(irisImage.getId());
					showExceptionList();
				} else {
					if (irisList.indexOf(irisImage.getId()) >= 0) {
						irisList.remove(irisImage.getId());
						showExceptionList();
					}
				}
				continueBtn.setDisable((fingerList.isEmpty() && irisList.isEmpty()));

			}
		});
		irisImage.setOnMouseClicked(event -> {
			auditFactory.audit(AuditEvent.REG_BIO_EXCEPTION_MARKING, Components.REG_BIOMETRICS, SessionContext.userId(),
					AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
			if(irisImage.getParent().getStyleClass().contains(RegistrationConstants.BIO_IRIS_SELECTED)) {
				irisImage.getParent().getStyleClass().remove(RegistrationConstants.BIO_IRIS_SELECTED);
			}else {
				irisImage.getParent().getStyleClass().add(RegistrationConstants.BIO_IRIS_SELECTED);
			}
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

				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_BIOMETRICEXCEPTION, false);

				if (RegistrationConstants.ENABLE.equalsIgnoreCase(
						getValueFromApplicationContext(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)) && !isChild()) {
					SessionContext.map().put(RegistrationConstants.UIN_UPDATE_FINGERPRINTCAPTURE, true);

				} else if (RegistrationConstants.ENABLE.equalsIgnoreCase(
						getValueFromApplicationContext(RegistrationConstants.IRIS_DISABLE_FLAG)) && !isChild()) {
					SessionContext.map().put(RegistrationConstants.UIN_UPDATE_IRISCAPTURE, true);
				} else if (isChild()) {
					SessionContext.map().put(RegistrationConstants.UIN_UPDATE_PARENTGUARDIAN_DETAILS, true);
				}
				registrationController.showUINUpdateCurrentPage();
			} else {
				registrationController.showCurrentPage(RegistrationConstants.BIOMETRIC_EXCEPTION, getPageDetails(
						RegistrationConstants.UIN_UPDATE_BIOMETRICEXCEPTION, RegistrationConstants.NEXT));
			}
			fingerPrintCaptureController.clearImage();
			irisCaptureController.clearIrisBasedOnExceptions();
			guardianBiometricsController.manageBiometricsListBasedOnExceptions();
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
		List<BiometricExceptionDTO> biometricExceptionList = new ArrayList<>();
		if (!bioList.isEmpty()) {
			bioList.forEach(bioType -> {
				BiometricExceptionDTO biometricExceptionDTO = new BiometricExceptionDTO();
				if (bioType.contains(RegistrationConstants.EYE)) {
					biometricExceptionDTO.setBiometricType(RegistrationConstants.IRIS.toLowerCase());
				} else {
					biometricExceptionDTO.setBiometricType(RegistrationConstants.FINGERPRINT);
				}
				biometricExceptionDTO.setMissingBiometric(bioType);
				biometricExceptionDTO.setExceptionType(RegistrationConstants.PERMANENT_EXCEPTION);
				biometricExceptionDTO.setReason(RegistrationConstants.MISSING_BIOMETRICS);
				biometricExceptionDTO.setMarkedAsException(true);
				if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
					biometricExceptionDTO
							.setIndividualType((boolean) SessionContext.map().get(RegistrationConstants.IS_Child)
									? RegistrationConstants.PARENT
									: RegistrationConstants.INDIVIDUAL);
				}
				biometricExceptionList.add(biometricExceptionDTO);
			});
			SessionContext.map().put(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION, biometricExceptionList);
			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)
					|| (boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER_UPDATE)) {
				((BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA))
						.getOperatorBiometricDTO().setBiometricExceptionDTO(biometricExceptionList);
			} else if (getRegistrationDTOFromSession().isUpdateUINNonBiometric()
					|| (boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
				((RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO()
						.getIntroducerBiometricDTO().setBiometricExceptionDTO(biometricExceptionList);
			} else {
				((RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO()
						.getApplicantBiometricDTO().setBiometricExceptionDTO(biometricExceptionList);
			}

		} else {
			SessionContext.map().put(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION, biometricExceptionList);
			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)
					|| (boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER_UPDATE)) {
				((BiometricDTO) SessionContext.map().get(RegistrationConstants.USER_ONBOARD_DATA))
						.getOperatorBiometricDTO().setBiometricExceptionDTO(biometricExceptionList);
			}
		}

		LOGGER.info("REGISTRATION - EXCEPTION_DTO_CREATION_END - BIOMETRIC_EXCEPTION_LISTENER", APPLICATION_NAME,
				APPLICATION_ID, "End of exception dto creation functionality");

	}

	/**
	 * This method will call on click of previous button and toggle the
	 * visibility based
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
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_BIOMETRICEXCEPTION, false);
				SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DOCUMENTSCAN, true);
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
					ImageView fingerImage = (ImageView) leftHandPane.lookup("#" + bioException.getMissingBiometric());
					fingerImage.setOpacity(1.0);

				} else if (bioException.getMissingBiometric().contains("right")
						&& !bioException.getMissingBiometric().contains("Eye")) {
					fingerList.add(bioException.getMissingBiometric());
					ImageView fingerImage = (ImageView) rightHandPane.lookup("#" + bioException.getMissingBiometric());
					fingerImage.setOpacity(1.0);

				} else if (bioException.getMissingBiometric().contains("Eye")) {
					irisList.add(bioException.getMissingBiometric());
				}
			});
			showExceptionList();
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
			showExceptionList();
		}
	}

	public void clearSession() {
		SessionContext.map().put(RegistrationConstants.OLD_BIOMETRIC_EXCEPTION, new ArrayList<>());
		SessionContext.map().put(RegistrationConstants.NEW_BIOMETRIC_EXCEPTION, new ArrayList<>());
		setExceptionImage();
	}

	private void loadPage(String page) {
		try {
			BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
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
	}

	/**
	 * Method to show the exception list
	 */
	private void showExceptionList() {
		List<ExceptionListVO> exceptionList = new ArrayList<>();
		fingerList.forEach(finger -> exceptionList.add(new ExceptionListVO(applicationLabelBundle.getString(finger))));
		irisList.forEach(iris -> exceptionList.add(new ExceptionListVO(applicationLabelBundle.getString(iris))));
		ObservableList<ExceptionListVO> listOfException = FXCollections.observableArrayList(exceptionList);
		exceptionTableColumn.setCellValueFactory(new PropertyValueFactory<ExceptionListVO, String>("exceptionItem"));
		exceptionTable.getItems().clear();
		exceptionTable.setItems(listOfException);
	}

}
