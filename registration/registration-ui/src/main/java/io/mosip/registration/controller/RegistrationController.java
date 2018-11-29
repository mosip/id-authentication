package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.LocationDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Class for Registration Page Controller
 * 
 * @author Taleev.Aalam
 * @since 1.0.0
 *
 */

@Controller
public class RegistrationController extends BaseController {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationController.class);

	@FXML
	private TextField preRegistrationId;

	@FXML
	private TextField fullName;

	@FXML
	private TextField fullNameLocalLanguage;

	@FXML
	private Label fullNameLocalLanguageLabel;

	@FXML
	private DatePicker ageDatePicker;

	@FXML
	private TextField ageField;

	@FXML
	private Label bioExceptionToggleLabel1;

	@FXML
	private Label bioExceptionToggleLabel2;

	@FXML
	private Label toggleLabel1;

	@FXML
	private Label toggleLabel2;

	@FXML
	private AnchorPane childSpecificFields;

	private SimpleBooleanProperty switchedOn;

	private SimpleBooleanProperty switchedOnForBiometricException;

	@FXML
	private ComboBox<String> gender;

	@FXML
	private TextField addressLine1;

	@FXML
	private TextField addressLine1LocalLanguage;

	@FXML
	private Label addressLine1LocalLanguagelabel;

	@FXML
	private TextField addressLine2;

	@FXML
	private TextField addressLine2LocalLanguage;

	@FXML
	private Label addressLine2LocalLanguagelabel;

	@FXML
	private TextField addressLine3;

	@FXML
	private TextField addressLine3LocalLanguage;

	@FXML
	private Label addressLine3LocalLanguagelabel;

	@FXML
	private TextField emailId;

	@FXML
	private TextField mobileNo;

	@FXML
	private TextField region;

	@FXML
	private TextField city;

	@FXML
	private TextField province;

	@FXML
	private TextField postalCode;

	@FXML
	private TextField localAdminAuthority;

	@FXML
	private TextField cniOrPinNumber;

	@FXML
	private TextField parentName;

	@FXML
	private TextField uinId;

	@FXML
	private TitledPane demoGraphicTitlePane;

	@FXML
	private TitledPane biometricTitlePane;

	@FXML
	private Accordion accord;

	@FXML
	private AnchorPane demoGraphicPane1;

	@FXML
	private ComboBox<String> poaDocuments;

	@FXML
	private Label poaLabel;

	@FXML
	private ComboBox<String> poiDocuments;

	@FXML
	private Label poiLabel;

	@FXML
	private ImageView headerImage;

	@FXML
	private ComboBox<String> porDocuments;

	@FXML
	private Label porLabel;

	@FXML
	private AnchorPane documentFields;

	@FXML
	private Button nextBtn;

	@FXML
	private Button pane2NextBtn;

	@FXML
	private VBox demoGraphicVBox;

	@FXML
	private AnchorPane demoGraphicPane2;

	@FXML
	private AnchorPane anchorPaneRegistration;

	@FXML
	private Button prevAddressButton;

	private static AnchorPane demoGraphicPane1Content;

	private static AnchorPane demoGraphicPane2Content;

	private static DatePicker ageDatePickerContent;

	private boolean toggleAgeOrDobField;

	private boolean toggleBiometricException;

	private boolean isChild;

	private static boolean isEditPage;

	Node keyboardNode;

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;

	@FXML
	protected Button biometricsNext;
	@FXML
	private Label biometrics;
	@FXML
	protected AnchorPane biometricsPane;
	@FXML
	protected ImageView applicantImage;
	@FXML
	protected ImageView exceptionImage;
	@FXML
	protected Button captureImage;
	@FXML
	protected Button captureExceptionImage;
	@FXML
	protected Button saveBiometricDetailsBtn;
	@FXML
	protected Button biometricPrevBtn;
	@FXML
	protected Button pane2PrevBtn;
	@FXML
	protected Button autoFillBtn;
	@FXML
	protected Button fetchBtn;
	@FXML
	protected Button poaScanBtn;
	@FXML
	protected Button poiScanBtn;
	@FXML
	protected Button porScanBtn;

	@FXML
	private AnchorPane fingerPrintCapturePane;

	protected BufferedImage applicantBufferedImage;
	protected BufferedImage exceptionBufferedImage;
	private boolean applicantImageCaptured = false;
	private Image defaultImage;

	@FXML
	private TitledPane authenticationTitlePane;

	@Autowired
	private RegistrationOfficerPacketController registrationOfficerPacketController;

	@FXML
	private void initialize() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");

		try {
			auditFactory.audit(AuditEvent.GET_REGISTRATION_CONTROLLER, Components.REGISTRATION_CONTROLLER,
					"initializing the registration controller",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);
			if (capturePhotoUsingDevice.equals("Y")) {
				defaultImage = applicantImage.getImage();
				biometrics.setVisible(false);
				biometricsNext.setVisible(false);
				biometricsPane.setVisible(true);
				if (!isEditPage) {
					applicantImageCaptured = false;
					exceptionBufferedImage = null;
				}
			} else if (capturePhotoUsingDevice.equals("N")) {

				biometrics.setVisible(true);
				biometricsNext.setVisible(true);
				biometricsPane.setVisible(false);
				biometricsNext.setDisable(false);
			}

			demoGraphicTitlePane.expandedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						headerImage.setImage(new Image(RegistrationConstants.DEMOGRAPHIC_DETAILS_LOGO));
					}
				}
			});
			biometricTitlePane.expandedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						headerImage.setImage(new Image(RegistrationConstants.APPLICANT_BIOMETRICS_LOGO));
					}
				}
			});
			authenticationTitlePane.expandedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						headerImage.setImage(new Image(RegistrationConstants.OPERATOR_AUTHENTICATION_LOGO));
					}
				}
			});

			switchedOn = new SimpleBooleanProperty(false);
			switchedOnForBiometricException = new SimpleBooleanProperty(false);
			toggleAgeOrDobField = false;
			toggleBiometricException = false;
			isChild = true;
			ageDatePicker.setDisable(false);
			ageField.setDisable(true);
			keyboardNode = new VirtualKeyboard().view();
			accord.setExpandedPane(demoGraphicTitlePane);
			disableFutureDays();
			toggleFunction();
			toggleFunctionForBiometricException();
			ageFieldValidations();
			ageValidationInDatePicker();
			dateFormatter();
			populateTheLocalLangFields();
			loadLanguageSpecificKeyboard();
			demoGraphicPane1.getChildren().add(keyboardNode);
			keyboardNode.setVisible(false);
			loadLocalLanguageFields();
			loadListOfDocuments();
			if (SessionContext.getInstance().getMapObject().get(RegistrationConstants.ADDRESS_KEY) == null) {
				prevAddressButton.setVisible(false);
			}
			if (isEditPage && getRegistrationDtoContent() != null) {
				prepareEditPageContent();
			}
		} catch (IOException | RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ALERT_ERROR, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					generateErrorMessage(RegistrationConstants.UNABLE_LOAD_REG_PAGE));
		}
	}

	/**
	 * This method is to prepopulate all the values for edit operation
	 */
	private void prepareEditPageContent() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Preparing the Edit page content");

			DemographicDTO demographicDTO = getRegistrationDtoContent().getDemographicDTO();
			DemographicInfoDTO demographicInfoDTO = demographicDTO.getDemoInUserLang();

			AddressDTO addressDTO = demographicInfoDTO.getAddressDTO();
			LocationDTO locationDTO = addressDTO.getLocationDTO();
			fullName.setText(demographicInfoDTO.getFullName());
			if (demographicInfoDTO.getDateOfBirth() != null && ageDatePickerContent != null) {
				ageDatePicker.setValue(ageDatePickerContent.getValue());
			} else {
				switchedOn.set(true);
				ageDatePicker.setDisable(true);
				ageField.setDisable(false);
				ageField.setText(demographicInfoDTO.getAge());

			}
			gender.setValue(demographicInfoDTO.getGender());
			addressLine1.setText(addressDTO.getAddressLine1());
			addressLine2.setText(addressDTO.getAddressLine2());
			addressLine3.setText(addressDTO.getAddressLine3());
			province.setText(locationDTO.getProvince());
			city.setText(locationDTO.getCity());
			region.setText(locationDTO.getRegion());
			postalCode.setText(locationDTO.getPostalCode());
			mobileNo.setText(demographicInfoDTO.getMobile());
			emailId.setText(demographicInfoDTO.getEmailId());
			cniOrPinNumber.setText(demographicInfoDTO.getCneOrPINNumber());
			localAdminAuthority.setText(demographicInfoDTO.getLocalAdministrativeAuthority());
			if (demographicDTO.getIntroducerRID() != null) {
				uinId.setText(demographicDTO.getIntroducerRID());
			} else {
				uinId.setText(demographicDTO.getIntroducerUIN());
			}
			parentName.setText(demographicInfoDTO.getParentOrGuardianName());
			preRegistrationId.setText(getRegistrationDtoContent().getPreRegistrationId());

			// for applicant biometrics
			if (getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO() != null) {
				if (getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getPhoto() != null) {
					byte[] photoInBytes = getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO()
							.getPhoto();
					if (photoInBytes != null) {
						ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(photoInBytes);
						applicantImage.setImage(new Image(byteArrayInputStream));
					}
				}
				if (getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO()
						.getExceptionPhoto() != null) {
					byte[] exceptionPhotoInBytes = getRegistrationDtoContent().getDemographicDTO()
							.getApplicantDocumentDTO().getExceptionPhoto();
					if (exceptionPhotoInBytes != null) {
						ByteArrayInputStream inputStream = new ByteArrayInputStream(exceptionPhotoInBytes);
						exceptionImage.setImage(new Image(inputStream));
					}
				}
			}
			isEditPage = false;
			ageFieldValidations();
			ageValidationInDatePicker();

		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}

	}

	/**
	 * 
	 * Loading the address detail from previous entry
	 * 
	 */
	@FXML
	private void loadAddressFromPreviousEntry() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading address from previous entry");

			LocationDTO locationDto = ((AddressDTO) SessionContext.getInstance().getMapObject()
					.get(RegistrationConstants.ADDRESS_KEY)).getLocationDTO();
			region.setText(locationDto.getRegion());
			city.setText(locationDto.getCity());
			province.setText(locationDto.getProvince());
			postalCode.setText(locationDto.getPostalCode());
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loaded address from previous entry");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING ADDRESS FROM PREVIOUS ENTRY FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * 
	 * Loading the second demographic pane
	 * 
	 */
	@FXML
	private void gotoSecondDemographicPane() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading the second demographic pane");

			if (validateDemographicPaneOne()) {
				demoGraphicTitlePane.setContent(null);
				demoGraphicTitlePane.setExpanded(false);
				demoGraphicTitlePane.setContent(demoGraphicPane2);
				demoGraphicTitlePane.setExpanded(true);
				anchorPaneRegistration.setMaxHeight(700);
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - COULD NOT GO TO SECOND DEMOGRAPHIC PANE", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * 
	 * Setting the focus to specific fields when keyboard loads
	 * 
	 */
	@FXML
	private void setFocusonLocalField(MouseEvent event) {
		try {
			keyboardNode.setLayoutX(300.00);
			Node node = (Node) event.getSource();

			if (node.getId().equals("addressLine1")) {
				addressLine1LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(270.00);
			}

			if (node.getId().equals("addressLine2")) {
				addressLine2LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(320.00);
			}

			if (node.getId().equals("addressLine3")) {
				addressLine3LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(375.00);
			}

			if (node.getId().equals("fullName")) {
				fullNameLocalLanguage.requestFocus();
				keyboardNode.setLayoutY(120.00);
			}

			keyboardNode.setVisible(true);
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - SETTING FOCUS ON LOCAL FIELED FAILED", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * 
	 * Saving the detail into concerned DTO'S
	 * 
	 */
	@FXML
	private void saveDetail() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Saving the fields to DTO");

		try {
			auditFactory.audit(AuditEvent.SAVE_DETAIL_TO_DTO, Components.REGISTRATION_CONTROLLER,
					"Saving the details to respected DTO", SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			RegistrationDTO registrationDTO = new RegistrationDTO();
			DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
			LocationDTO locationDTO = new LocationDTO();
			AddressDTO addressDTO = new AddressDTO();
			DemographicDTO demographicDTO = new DemographicDTO();
			OSIDataDTO osiDataDTO = new OSIDataDTO();
			if (validateDemographicPaneTwo()) {
				demographicInfoDTO.setFullName(fullName.getText());
				if (ageDatePicker.getValue() != null) {
					demographicInfoDTO.setDateOfBirth(Date
							.from(ageDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
				}
				demographicInfoDTO.setAge(ageField.getText());
				demographicInfoDTO.setGender(gender.getValue());
				addressDTO.setAddressLine1(addressLine1.getText());
				addressDTO.setAddressLine2(addressLine2.getText());
				addressDTO.setLine3(addressLine3.getText());
				locationDTO.setProvince(province.getText());
				locationDTO.setCity(city.getText());
				locationDTO.setRegion(region.getText());
				locationDTO.setPostalCode(postalCode.getText());
				addressDTO.setLocationDTO(locationDTO);
				demographicInfoDTO.setAddressDTO(addressDTO);
				demographicInfoDTO.setMobile(mobileNo.getText());
				demographicInfoDTO.setEmailId(emailId.getText());
				demographicInfoDTO.setChild(isChild);
				demographicInfoDTO.setCneOrPINNumber(cniOrPinNumber.getText());
				demographicInfoDTO.setLocalAdministrativeAuthority(localAdminAuthority.getText());
				if (isChild) {
					if (uinId.getText().length() == Integer.parseInt(AppConfig.getApplicationProperty("uin_length"))) {
						demographicDTO.setIntroducerRID(uinId.getText());
					} else {
						demographicDTO.setIntroducerUIN(uinId.getText());
					}
					osiDataDTO.setIntroducerType(IntroducerType.PARENT.getCode());
					demographicInfoDTO.setParentOrGuardianName(parentName.getText());
				}
				demographicDTO.setDemoInUserLang(demographicInfoDTO);
				osiDataDTO.setOperatorID(SessionContext.getInstance().getUserContext().getUserId());

				// local language
				demographicInfoDTO = new DemographicInfoDTO();
				locationDTO = new LocationDTO();
				addressDTO = new AddressDTO();
				addressDTO.setLocationDTO(locationDTO);
				demographicInfoDTO.setAddressDTO(addressDTO);
				demographicInfoDTO.setFullName(fullNameLocalLanguage.getText());
				addressDTO.setAddressLine1(addressLine1LocalLanguage.getText());
				addressDTO.setAddressLine2(addressLine2LocalLanguage.getText());
				addressDTO.setLine3(addressLine3LocalLanguage.getText());

				demographicDTO.setDemoInLocalLang(demographicInfoDTO);

				registrationDTO.setPreRegistrationId(preRegistrationId.getText());
				registrationDTO.setOsiDataDTO(osiDataDTO);
				registrationDTO.setDemographicDTO(demographicDTO);

				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Saved the demographic fields to DTO");

				SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_DATA,
						registrationDTO);

				if (ageDatePicker.getValue() != null) {
					ageDatePickerContent = new DatePicker();
					ageDatePickerContent.setValue(ageDatePicker.getValue());
				}

				biometricTitlePane.setExpanded(true);
				// TODO : load fxml

				try {
					Parent pendingActionRoot = BaseController
							.load(getClass().getResource("/fxml/FingerPrintCapture.fxml"));
					ObservableList<Node> approvalNodes = fingerPrintCapturePane.getChildren();
					approvalNodes.add(pendingActionRoot);

				} catch (IOException e) {
					e.printStackTrace();
				}

				if (capturePhotoUsingDevice.equals("N")) {
					biometricsNext.setDisable(false);
				}
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - SAVING THE DETAILS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	@FXML
	private void goToPreviousPane() {
		try {
			demoGraphicTitlePane.setExpanded(true);
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - COULD NOT GO TO DEMOGRAPHIC TITLE PANE ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * 
	 * To open camera to capture Applicant Image
	 * 
	 */
	@FXML
	private void openCamForApplicantPhoto() {
		openWebCamWindow(RegistrationConstants.APPLICANT_IMAGE);
	}

	/**
	 * 
	 * To open camera to capture Exception Image
	 * 
	 */
	@FXML
	private void openCamForExceptionPhoto() {
		openWebCamWindow(RegistrationConstants.EXCEPTION_IMAGE);
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
			applicantImageCaptured = true;
		} else if (photoType.equals(RegistrationConstants.EXCEPTION_IMAGE)) {
			Image capture = SwingFXUtils.toFXImage(capturedImage, null);
			exceptionImage.setImage(capture);
			exceptionBufferedImage = capturedImage;
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
		}
	}

	@FXML
	private void saveBiometricDetails() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "saving the details of applicant biometrics");

		if (capturePhotoUsingDevice.equals("Y")) {
			if (validateApplicantImage()) {
				try {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(applicantBufferedImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE,
							byteArrayOutputStream);
					byte[] photoInBytes = byteArrayOutputStream.toByteArray();
					ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
					applicantDocumentDTO.setPhoto(photoInBytes);
					applicantDocumentDTO.setPhotographName(RegistrationConstants.APPLICANT_PHOTOGRAPH_NAME);
					byteArrayOutputStream.close();
					if (exceptionBufferedImage != null) {
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						ImageIO.write(exceptionBufferedImage, RegistrationConstants.WEB_CAMERA_IMAGE_TYPE,
								outputStream);
						byte[] exceptionPhotoInBytes = outputStream.toByteArray();
						applicantDocumentDTO.setExceptionPhoto(exceptionPhotoInBytes);
						applicantDocumentDTO.setExceptionPhotoName(RegistrationConstants.EXCEPTION_PHOTOGRAPH_NAME);
						applicantDocumentDTO.setHasExceptionPhoto(true);
						outputStream.close();
					} else {
						applicantDocumentDTO.setHasExceptionPhoto(false);
					}
					getRegistrationDtoContent().getDemographicDTO().setApplicantDocumentDTO(applicantDocumentDTO);
					LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID, "showing demographic preview");

					setPreviewContent();
					loadScreen(RegistrationConstants.DEMOGRAPHIC_PREVIEW);
				} catch (IOException ioException) {
					LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID, ioException.getMessage());
				}
			}

		} else {
			try {
				setPreviewContent();
				loadScreen(RegistrationConstants.DEMOGRAPHIC_PREVIEW);
			} catch (IOException ioException) {
				LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, ioException.getMessage());
			}
		}

	}

	private void setPreviewContent() {
		saveBiometricDetailsBtn.setVisible(false);
		biometricPrevBtn.setVisible(false);
		nextBtn.setVisible(false);
		pane2NextBtn.setVisible(false);
		pane2PrevBtn.setVisible(false);
		autoFillBtn.setVisible(false);
		fetchBtn.setVisible(false);
		poaScanBtn.setVisible(false);
		poiScanBtn.setVisible(false);
		porScanBtn.setVisible(false);
		prevAddressButton.setVisible(false);
		demoGraphicPane1Content = demoGraphicPane1;
		demoGraphicPane2Content = demoGraphicPane2;
	}

	private boolean validateApplicantImage() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "validating applicant biometrics");

		boolean imageCaptured = false;
		if (applicantImageCaptured) {
			if (getRegistrationDtoContent() != null && getRegistrationDtoContent().getDemographicDTO() != null) {
				imageCaptured = true;
			} else {
				generateAlert(RegistrationConstants.DEMOGRAPHIC_DETAILS_ERROR, AlertType.ERROR,
						generateErrorMessage(RegistrationConstants.DEMOGRAPHIC_DETAILS_ERROR_CONTEXT));
			}
		} else {
			generateAlert(RegistrationConstants.APPLICANT_BIOMETRICS_ERROR, AlertType.ERROR,
					generateErrorMessage(RegistrationConstants.APPLICANT_IMAGE_ERROR));
		}
		return imageCaptured;
	}

	public static void loadScreen(String screen) throws IOException {
		Parent createRoot = BaseController.load(RegistrationController.class.getResource(screen),
				ApplicationContext.getInstance().getApplicationLanguageBundle());
		LoginController.getScene().setRoot(createRoot);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		LoginController.getScene().getStylesheets()
				.add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
	}

	/**
	 * Validating the age field for the child/Infant check.
	 */
	public void ageValidationInDatePicker() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the age given by DatePiker");

			if (ageDatePicker.getValue() != null) {
				LocalDate selectedDate = ageDatePicker.getValue();
				Date date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
				long ageInMilliSeconds = new Date().getTime() - date.getTime();
				long ageInDays = TimeUnit.MILLISECONDS.toDays(ageInMilliSeconds);
				int age = (int) ageInDays / 365;
				if (age < Integer.parseInt(AppConfig.getApplicationProperty("age_limit_for_child"))) {
					childSpecificFields.setVisible(true);
					isChild = true;
					documentFields.setLayoutY(134.00);
				} else {
					isChild = false;
					childSpecificFields.setVisible(false);
					documentFields.setLayoutY(25.00);
				}
				// to populate age based on date of birth
				ageField.setText("" + (Period.between(ageDatePicker.getValue(), LocalDate.now()).getYears()));
			}
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validated the age given by DatePiker");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - VALIDATION OF AGE FOR DATEPICKER FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * Disabling the future days in the date picker calendar.
	 */
	private void disableFutureDays() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Disabling future dates");

			ageDatePicker.setDayCellFactory(picker -> new DateCell() {
				@Override
				public void updateItem(LocalDate date, boolean empty) {
					super.updateItem(date, empty);
					LocalDate today = LocalDate.now();

					setDisable(empty || date.compareTo(today) > 0);
				}
			});

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Future dates disabled");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - DISABLE FUTURE DATE FAILED", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * Populating the user language fields to local language fields
	 */
	private void populateTheLocalLangFields() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Populating the local language fields");
			fullName.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					if (!newValue.matches("([A-z]+\\s?\\.?)+")) {
						generateAlert(generateErrorMessage(RegistrationConstants.FULL_NAME_EMPTY),
								generateErrorMessage(RegistrationConstants.ONLY_ALPHABETS));

						fullName.setText(fullName.getText().replaceAll("\\d+", ""));
						fullName.requestFocus();
					} else {
						fullNameLocalLanguage.setText(fullName.getText());
					}
				}
			});

			addressLine1.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					addressLine1LocalLanguage.setText(addressLine1.getText());
				}
			});

			addressLine2.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					addressLine2LocalLanguage.setText(addressLine2.getText());
				}
			});

			addressLine3.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					addressLine3LocalLanguage.setText(addressLine3.getText());
				}
			});
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOCAL FIELD POPULATION FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * To restrict the user not to enter any values other than integer values.
	 */
	private void loadLanguageSpecificKeyboard() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading the local language keyboard");
			addressLine1LocalLanguage.focusedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

					if (oldValue) {
						keyboardNode.setVisible(false);
					}

				}
			});

			addressLine2LocalLanguage.focusedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

					if (oldValue) {
						keyboardNode.setVisible(false);
					}

				}
			});

			addressLine3LocalLanguage.focusedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

					if (oldValue) {
						keyboardNode.setVisible(false);
					}

				}
			});

			fullNameLocalLanguage.focusedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

					if (oldValue) {
						keyboardNode.setVisible(false);
					}

				}
			});
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - KEYBOARD LOADING FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * To restrict the user not to enter any values other than integer values.
	 */
	private void ageFieldValidations() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the age given by age field");
			// to populate date of birth based on age
			ageField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					// to auto-populate DOB based on age
					if (ageField.getText().length() > 0) {
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern(RegistrationConstants.DEMOGRAPHIC_DOB_FORMAT);
						StringBuilder dob = new StringBuilder();
						dob.append(RegistrationConstants.DEMOGRAPHIC_DOB);
						dob.append(Calendar.getInstance().getWeekYear() - Integer.parseInt(ageField.getText()));
						LocalDate date = LocalDate.parse(dob, formatter);
						ageDatePicker.setValue(date);
					}
				}
			});
			ageField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					if (ageField.getText().length() > 2) {
						String age = ageField.getText().substring(0, 2);
						ageField.setText(age);
					}
					if (!newValue.matches("\\d*")) {
						ageField.setText(newValue.replaceAll("[^\\d]", ""));
					}
				}
			});
			ageField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
						Boolean newPropertyValue) {
					int ageValue = 0;
					if (!newPropertyValue && !ageField.getText().equals("")) {
						ageValue = Integer.parseInt(ageField.getText());
					}
					if (ageValue < Integer.parseInt(AppConfig.getApplicationProperty("age_limit_for_child"))
							&& ageValue != 0) {
						childSpecificFields.setVisible(true);
						isChild = true;
						documentFields.setLayoutY(134.00);
					} else {
						isChild = false;
						childSpecificFields.setVisible(false);
						documentFields.setLayoutY(25.00);
					}
				}
			});
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the age given by age field");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - AGE FIELD VALIDATION FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * Toggle functionality between age field and date picker.
	 */
	private void toggleFunction() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"Entering into toggle function for toggle label 1 and toggle level 2");

			toggleLabel1.setId("toggleLabel1");
			toggleLabel2.setId("toggleLabel2");
			switchedOn.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						toggleLabel1.setId("toggleLabel2");
						toggleLabel2.setId("toggleLabel1");
						ageField.clear();
						ageDatePicker.setValue(null);
						parentName.clear();
						uinId.clear();
						childSpecificFields.setVisible(false);
						ageDatePicker.setDisable(true);
						ageField.setDisable(false);
						toggleAgeOrDobField = true;

					} else {
						toggleLabel1.setId("toggleLabel1");
						toggleLabel2.setId("toggleLabel2");
						ageField.clear();
						ageDatePicker.setValue(null);
						parentName.clear();
						uinId.clear();
						childSpecificFields.setVisible(false);
						ageDatePicker.setDisable(false);
						ageField.setDisable(true);
						toggleAgeOrDobField = false;

					}
				}
			});

			toggleLabel1.setOnMouseClicked((event) -> {
				switchedOn.set(!switchedOn.get());
			});
			toggleLabel2.setOnMouseClicked((event) -> {
				switchedOn.set(!switchedOn.get());
			});
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"Exiting the toggle function for toggle label 1 and toggle level 2");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - TOGGLING OF DOB AND AGE FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * To dispaly the selected date in the date picker in specific
	 * format("dd-mm-yyyy").
	 */
	private void dateFormatter() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the date format");

			ageDatePicker.setConverter(new StringConverter<LocalDate>() {
				String pattern = "dd-MM-yyyy";
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

				{
					ageDatePicker.setPromptText(pattern.toLowerCase());
				}

				@Override
				public String toString(LocalDate date) {
					return date != null ? dateFormatter.format(date) : "";

				}

				@Override
				public LocalDate fromString(String string) {
					if (string != null && !string.isEmpty()) {
						return LocalDate.parse(string, dateFormatter);
					} else {
						return null;
					}
				}
			});
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - DATE FORMAT VALIDATION FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * 
	 * Opens the home page screen
	 * 
	 */
	public void goToHomePage() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to home page");

		try {
			isEditPage = false;
			demoGraphicPane1Content = null;
			demoGraphicPane2Content = null;
			ageDatePickerContent = null;
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_DATA);
			BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REGSITRATION_HOME_PAGE_LAYOUT_LOADING_FAILED", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, ioException.getMessage());
		}
	}

	/**
	 * 
	 * Validates the fields of demographic pane1
	 * 
	 */
	private boolean validateDemographicPaneOne() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the fields in first demographic pane");

		boolean gotoNext = false;
		if (validateRegex(fullName, "([A-z]+\\s?\\.?)+")) {
			generateAlert(generateErrorMessage(RegistrationConstants.FULL_NAME_EMPTY),
					generateErrorMessage(RegistrationConstants.ONLY_ALPHABETS));

			fullName.requestFocus();
		} else {
			if (validateAgeOrDob()) {
				if (gender.getValue() == null) {
					generateAlert(generateErrorMessage(RegistrationConstants.GENDER_EMPTY));

					gender.requestFocus();
				} else {
					if (validateRegex(addressLine1, "^.{6,50}$")) {

						generateAlert(generateErrorMessage(RegistrationConstants.ADDRESS_LINE_1_EMPTY),
								generateErrorMessage(RegistrationConstants.ADDRESS_LINE_WARNING));
						addressLine1.requestFocus();
					} else {
						if (validateRegex(region, "^.{6,50}$")) {

							generateAlert(generateErrorMessage(RegistrationConstants.REGION_EMPTY),
									generateErrorMessage(RegistrationConstants.ONLY_ALPHABETS) + " "
											+ generateErrorMessage(RegistrationConstants.TEN_LETTER_INPUT_LIMT));
							region.requestFocus();
						} else {
							if (validateRegex(city, "^.{6,10}$")) {

								generateAlert(generateErrorMessage(RegistrationConstants.CITY_EMPTY),
										generateErrorMessage(RegistrationConstants.ONLY_ALPHABETS) + " "
												+ generateErrorMessage(RegistrationConstants.TEN_LETTER_INPUT_LIMT));
								city.requestFocus();
							} else {
								if (validateRegex(province, "^.{6,10}$")) {

									generateAlert(generateErrorMessage(RegistrationConstants.PROVINCE_EMPTY),
											generateErrorMessage(RegistrationConstants.ONLY_ALPHABETS) + " "
													+ generateErrorMessage(
															RegistrationConstants.TEN_LETTER_INPUT_LIMT));
									province.requestFocus();
								} else {
									if (validateRegex(postalCode, "\\d{6}")) {
										generateAlert(generateErrorMessage(RegistrationConstants.POSTAL_CODE_EMPTY),

												generateErrorMessage(RegistrationConstants.SIX_DIGIT_INPUT_LIMT));
										postalCode.requestFocus();
									} else {
										if (validateRegex(localAdminAuthority, "^.{6,10}$")) {
											generateAlert(
													generateErrorMessage(
															RegistrationConstants.LOCAL_ADMIN_AUTHORITY_EMPTY),

													generateErrorMessage(RegistrationConstants.ONLY_ALPHABETS));
											localAdminAuthority.requestFocus();
										} else {
											if (validateRegex(mobileNo, "\\d{9}")) {

												generateAlert(
														generateErrorMessage(RegistrationConstants.MOBILE_NUMBER_EMPTY),

														generateErrorMessage(
																RegistrationConstants.MOBILE_NUMBER_EXAMPLE));
												mobileNo.requestFocus();
											} else {
												if (validateRegex(emailId,
														"^([\\w\\-\\.]+)@((\\[([0-9]{1,3}\\.){3}[0-9]{1,3}\\])|(([\\w\\-]+\\.)+)([a-zA-Z]{2,4}))$")) {

													generateAlert(
															generateErrorMessage(RegistrationConstants.EMAIL_ID_EMPTY),

															generateErrorMessage(
																	RegistrationConstants.EMAIL_ID_EXAMPLE));
													emailId.requestFocus();
												} else {
													if (validateRegex(cniOrPinNumber, "\\d{30}")) {

														generateAlert(
																generateErrorMessage(
																		RegistrationConstants.CNIE_OR_PIN_NUMBER_EMPTY),
																generateErrorMessage(
																		RegistrationConstants.THIRTY_DIGIT_INPUT_LIMT));
														cniOrPinNumber.requestFocus();
													} else {
														gotoNext = true;
													}

												}

											}
										}
									}
								}
							}
						}
					}
				}

			}
		}
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validated the fields");
		return gotoNext;
	}

	/**
	 * 
	 * Validate the fields of demographic pane 2
	 * 
	 */

	private boolean validateDemographicPaneTwo() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the fields in second demographic pane");
		boolean gotoNext = false;
		if (isChild) {
			gotoNext = getParentToggle();
		} else {
			gotoNext = true;
		}

		return gotoNext;
	}

	/**
	 * 
	 * Toggles the parent fields
	 * 
	 */
	private boolean getParentToggle() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Toggling for parent/guardian fields");
		boolean gotoNext = false;

		if (isChild) {
			if (validateRegex(parentName, "[[A-z]+\\s?\\.?]+")) {

				generateAlert(generateErrorMessage(RegistrationConstants.PARENT_NAME_EMPTY),
						generateErrorMessage(RegistrationConstants.ONLY_ALPHABETS));
				parentName.requestFocus();
			} else {
				if (validateRegex(uinId, "\\d{6,28}")) {
					generateAlert(generateErrorMessage(RegistrationConstants.UIN_ID_EMPTY));

					uinId.requestFocus();
				} else {
					gotoNext = true;
				}
			}
		}
		return gotoNext;
	}

	/**
	 * 
	 * Loading the the labels of local language fields
	 * 
	 */
	private void loadLocalLanguageFields() throws IOException {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading label fields of local language");

			ResourceBundle properties = ApplicationContext.getInstance().getLocalLanguageProperty();
			fullNameLocalLanguageLabel.setText(properties.getString("full_name"));
			addressLine1LocalLanguagelabel.setText(properties.getString("address_line1"));
			addressLine2LocalLanguagelabel.setText(properties.getString("address_line2"));
			addressLine3LocalLanguagelabel.setText(properties.getString("address_line3"));
			String userlangTitle = demoGraphicTitlePane.getText();
			demoGraphicTitlePane.expandedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

					if (oldValue) {
						demoGraphicTitlePane.setText(userlangTitle);
					}

					if (newValue) {
						demoGraphicTitlePane.setText("    " + userlangTitle
								+ "                                                              " + ApplicationContext
										.getInstance().getLocalLanguageProperty().getString("titleDemographicPane"));

					}
				}
			});
		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - LOADING LOCAL LANGUAGE FIELDS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
		}
	}

	/**
	 * 
	 * Loading the the labels of local language fields
	 * 
	 */
	private void loadListOfDocuments() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading list of documents");

			poaDocuments.getItems().addAll(RegistrationConstants.getPoaDocumentList());
			poiDocuments.getItems().addAll(RegistrationConstants.getPoiDocumentList());
			porDocuments.getItems().addAll(RegistrationConstants.getPorDocumentList());
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING LIST OF DOCUMENTS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	private boolean validateAgeOrDob() {
		boolean gotoNext = false;
		if (toggleAgeOrDobField) {
			if (validateRegex(ageField, "\\d{1,2}")) {
				generateAlert(generateErrorMessage(RegistrationConstants.AGE_EMPTY));

				ageField.requestFocus();
			} else {
				if (Integer.parseInt(ageField.getText()) < 5) {
					childSpecificFields.setVisible(true);
				}
				gotoNext = true;
			}
		} else if (!toggleAgeOrDobField) {
			if (ageDatePicker.getValue() == null) {
				generateAlert(generateErrorMessage(RegistrationConstants.DATE_OF_BIRTH_EMPTY));

				ageDatePicker.requestFocus();
			} else {
				gotoNext = true;
			}
		}
		return gotoNext;
	}

	@FXML
	private void scanPoaDocument() {
		if (poaDocuments.getValue() == null) {

			generateAlert(generateErrorMessage(RegistrationConstants.POA_DOCUMENT_EMPTY));
			poaDocuments.requestFocus();
		} else {
			poaLabel.setId("doc_label");
			poaLabel.setText(poaDocuments.getValue());

		}
	}

	@FXML
	private void scanPoiDocument() {
		if (poiDocuments.getValue() == null) {

			generateAlert(generateErrorMessage(RegistrationConstants.POI_DOCUMENT_EMPTY));
			poiDocuments.requestFocus();
		} else {
			poiLabel.setId("doc_label");
			poiLabel.setText(poiDocuments.getValue());

		}
	}

	@FXML
	private void scanPorDocument() {
		if (porDocuments.getValue() == null) {

			generateAlert(generateErrorMessage(RegistrationConstants.POR_DOCUMENT_EMPTY));
			porDocuments.requestFocus();
		} else {
			porLabel.setId("doc_label");
			porLabel.setText(porDocuments.getValue());

		}
	}

	public static AnchorPane getDemoGraphicContent() {
		return demoGraphicPane1Content;
	}

	public static AnchorPane getDemoGraphicPane2Content() {
		return demoGraphicPane2Content;
	}

	public static boolean isEditPage() {
		return isEditPage;
	}

	public static void setEditPage(boolean isEditPage) {
		RegistrationController.isEditPage = isEditPage;
	}

	private RegistrationDTO getRegistrationDtoContent() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}

	public void clickMe() {
		fullName.setText("Taleev Aalam");
		int age = 3;
		if (age < 5) {
			childSpecificFields.setVisible(true);
			isChild = true;
		}
		ageField.setText("" + age);
		toggleAgeOrDobField = true;
		gender.setValue("MALE");
		addressLine1.setText("Mind Tree Ltd");
		addressLine2.setText("RamanuJan It park");
		addressLine3.setText("Taramani");
		region.setText("Taramani");
		city.setText("Chennai");
		province.setText("Tamilnadu");
		postalCode.setText("600112");
		localAdminAuthority.setText("MindTree");
		mobileNo.setText("866769383");
		emailId.setText("taleev.aalam@mindtree.com");
		cniOrPinNumber.setText("012345678901234567890123456789");
		parentName.setText("Mokhtar");
		uinId.setText("93939939");
	}

	@FXML
	private void gotoFirstDemographicPane() {
		demoGraphicTitlePane.setContent(null);
		demoGraphicTitlePane.setExpanded(false);
		demoGraphicTitlePane.setContent(demoGraphicPane1);
		demoGraphicTitlePane.setExpanded(true);
		anchorPaneRegistration.setMaxHeight(900);
	}

	/**
	 * Toggle functionality for biometric exception
	 */
	private void toggleFunctionForBiometricException() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Entering into toggle function for Biometric exception");

			bioExceptionToggleLabel1.setId("toggleLabel1");
			bioExceptionToggleLabel2.setId("toggleLabel2");
			switchedOnForBiometricException.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						bioExceptionToggleLabel1.setId("toggleLabel2");
						bioExceptionToggleLabel2.setId("toggleLabel1");
						toggleBiometricException = true;
						captureExceptionImage.setDisable(false);
					} else {
						bioExceptionToggleLabel1.setId("toggleLabel1");
						bioExceptionToggleLabel2.setId("toggleLabel2");
						toggleBiometricException = false;
						captureExceptionImage.setDisable(true);
					}
				}
			});

			bioExceptionToggleLabel1.setOnMouseClicked((event) -> {
				switchedOnForBiometricException.set(!switchedOnForBiometricException.get());
			});
			bioExceptionToggleLabel2.setOnMouseClicked((event) -> {
				switchedOnForBiometricException.set(!switchedOnForBiometricException.get());
			});
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Exiting the toggle function for Biometric exception");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - TOGGLING FOR BIOMETRIC EXCEPTION SWITCH FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	public void submitRegistration() {
		registrationOfficerPacketController.showReciept((RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA), capturePhotoUsingDevice);
	}

	public void goToAuthenticationPage() {
		try {
			setEditPage(true);
			loadScreen(RegistrationConstants.CREATE_PACKET_PAGE);

			accord.setExpandedPane(authenticationTitlePane);
			headerImage.setImage(new Image(RegistrationConstants.OPERATOR_AUTHENTICATION_LOGO));

			biometricTitlePane.setDisable(true);
			demoGraphicTitlePane.setDisable(true);
			authenticationTitlePane.setDisable(false);

		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REGSITRATION_OPERATOR_AUTHENTICATION_PAGE_LOADING_FAILED", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, ioException.getMessage());
		}
	}
}
