package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.VirtualKeyboard;
import io.mosip.registration.controller.device.ScanController;
import io.mosip.registration.controller.device.WebCameraController;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.PreRegZipHandlingService;
import io.mosip.registration.util.scan.DocumentScanFacade;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
	private VBox poaBox;

	@FXML
	private ScrollPane poaScroll;

	@FXML
	private ComboBox<String> poiDocuments;

	@FXML
	private VBox poiBox;

	@FXML
	private ScrollPane poiScroll;

	@FXML
	private ImageView headerImage;

	@FXML
	private ComboBox<String> porDocuments;

	@FXML
	private ComboBox<String> dobDocuments;

	@FXML
	private VBox porBox;

	@FXML
	private VBox dobBox;

	@FXML
	private ScrollPane porScroll;

	@FXML
	private ScrollPane dobScroll;

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

	private boolean toggleAgeOrDobField;

	protected static boolean toggleBiometricException = false;

	private boolean isChild;

	Node keyboardNode;

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;

	@Value("${DOCUMENT_SIZE}")
	public int documentSize;

	@Value("${SCROLL_CHECK}")
	public int scrollCheck;

	@FXML
	protected Button biometricsNext;
	@FXML
	private Label biometrics;
	@FXML
	private AnchorPane biometricsPane;
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
	protected Button dobScanBtn;

	@FXML
	private AnchorPane fingerPrintCapturePane;
	@FXML
	private AnchorPane irisCapture;

	protected BufferedImage applicantBufferedImage;
	protected BufferedImage exceptionBufferedImage;
	private boolean applicantImageCaptured = false;
	private Image defaultImage;

	private String selectedDocument;

	@Autowired
	private ScanController scanController;

	@FXML
	private TitledPane authenticationTitlePane;

	@Autowired
	PreRegZipHandlingService preRegZipHandlingService;

	@Autowired
	private DocumentScanFacade documentScanFacade;

	private ResourceBundle applicationProperties;

	@FXML
	private void initialize() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");

		try {
			auditFactory.audit(AuditEvent.GET_REGISTRATION_CONTROLLER, Components.REGISTRATION_CONTROLLER,
					"initializing the registration controller",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Create RegistrationDTO Object
			if (getRegistrationDtoContent() == null) {
				createRegistrationDTOObject();
			}

			if (capturePhotoUsingDevice.equals("Y") && !isEditPage()) {
				applicantImageCaptured = false;
				exceptionBufferedImage = null;
			}

			demoGraphicTitlePane.expandedProperty().addListener(
					(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
						if (newValue) {
							headerImage.setImage(new Image(RegistrationConstants.DEMOGRAPHIC_DETAILS_LOGO));
						}
					});
			biometricTitlePane.expandedProperty().addListener(
					(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
						if (newValue) {
							headerImage.setImage(new Image(RegistrationConstants.APPLICANT_BIOMETRICS_LOGO));
						}
					});
			authenticationTitlePane.expandedProperty().addListener(
					(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
						if (newValue) {
							headerImage.setImage(new Image(RegistrationConstants.OPERATOR_AUTHENTICATION_LOGO));
						}
					});

			applicationProperties = applicationContext.getApplicationLanguageBundle();
			switchedOn = new SimpleBooleanProperty(false);
			switchedOnForBiometricException = new SimpleBooleanProperty(false);
			toggleAgeOrDobField = false;
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
			setScrollFalse();
			if (SessionContext.getInstance().getMapObject().get(RegistrationConstants.ADDRESS_KEY) == null) {
				prevAddressButton.setVisible(false);
			}
			if (isEditPage() && getRegistrationDtoContent() != null) {
				prepareEditPageContent();
			}
		} catch (IOException | RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UNABLE_LOAD_REG_PAGE);
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
			if (demographicInfoDTO.getDateOfBirth() != null && getAgeDatePickerContent() != null) {
				ageDatePicker.setValue(getAgeDatePickerContent().getValue());
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

			// for Document scan
			if (getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO() != null
					&& getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO()
							.getDocumentDetailsDTO() != null) {
				getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
						.stream().filter(doc -> doc.getDocumentType().equals(RegistrationConstants.POA_DOCUMENT))
						.findFirst()
						.ifPresent(document -> addDocumentsToScreen(document.getDocumentName(), poaBox, poaScroll));
				getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
						.stream().filter(doc -> doc.getDocumentType().equals(RegistrationConstants.POI_DOCUMENT))
						.findFirst()
						.ifPresent(document -> addDocumentsToScreen(document.getDocumentName(), poiBox, poiScroll));
				getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
						.stream().filter(doc -> doc.getDocumentType().equals(RegistrationConstants.POR_DOCUMENT))
						.findFirst()
						.ifPresent(document -> addDocumentsToScreen(document.getDocumentName(), porBox, porScroll));
				getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
						.stream().filter(doc -> doc.getDocumentType().equals(RegistrationConstants.DOB_DOCUMENT))
						.findFirst()
						.ifPresent(document -> addDocumentsToScreen(document.getDocumentName(), dobBox, dobScroll));

			}
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_ISEDIT, false);
			ageFieldValidations();
			ageValidationInDatePicker();

		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}

	}

	@FXML
	private void fetchPreRegistration() {
		try {
			preRegistrationId.getText();
			RegistrationDTO registrationDTO = preRegZipHandlingService.extractPreRegZipFile(
					FileUtils.readFileToByteArray(new File("C:/Users/M1046540/Desktop/89149679063970.zip")));

			if (registrationDTO != null) {
				SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_DATA,
						registrationDTO);
				prepareEditPageContent();
			}
		} catch (io.mosip.kernel.core.exception.IOException | RegBaseCheckedException | RegBaseUncheckedException e) {
			generateAlert(RegistrationConstants.ALERT_ERROR, "No Details Found for the given  Pre-Registration ID");
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

			RegistrationDTO registrationDTO = getRegistrationDtoContent();
			DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
			LocationDTO locationDTO = new LocationDTO();
			AddressDTO addressDTO = new AddressDTO();
			DemographicDTO demographicDTO = registrationDTO.getDemographicDTO();
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

				if (ageDatePicker.getValue() != null) {
					SessionContext.getInstance().getMapObject().put("ageDatePickerContent", ageDatePicker);
				}

				biometricTitlePane.setExpanded(true);

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
	 * @param imageType
	 *            type of image that is to be captured
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
					ApplicantDocumentDTO applicantDocumentDTO = getRegistrationDtoContent().getDemographicDTO()
							.getApplicantDocumentDTO();
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
		SessionContext.getInstance().getMapObject().put("demoGraphicPane1Content", demoGraphicPane1);
		SessionContext.getInstance().getMapObject().put("demoGraphicPane2Content", demoGraphicPane2);
	}

	private boolean validateApplicantImage() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "validating applicant biometrics");

		boolean imageCaptured = false;
		if (applicantImageCaptured) {
			if (getRegistrationDtoContent() != null && getRegistrationDtoContent().getDemographicDTO() != null) {
				imageCaptured = true;
			} else {
				generateAlert(RegistrationConstants.ALERT_ERROR,
						RegistrationConstants.DEMOGRAPHIC_DETAILS_ERROR_CONTEXT);
			}
		} else {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.APPLICANT_IMAGE_ERROR);
		}
		return imageCaptured;
	}

	private void loadScreen(String screen) throws IOException {
		Parent createRoot = BaseController.load(RegistrationController.class.getResource(screen),
				applicationContext.getApplicationLanguageBundle());
		getScene(createRoot);
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
					if (newValue.length() != 0 && (!newValue.matches(RegistrationConstants.FULL_NAME_REGEX)
							|| newValue.length() > RegistrationConstants.FULL_NAME_LENGTH)) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.ONLY_ALPHABETS);

						fullName.setText(fullName.getText().replaceAll(".$", ""));
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
					if (newValue.length() != 0 && !newValue.matches(RegistrationConstants.ADDRESS_LINE1_REGEX)) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.ADDRESS_LINE_WARNING);

						addressLine1.setText(addressLine1.getText().replaceAll(".$", ""));
						addressLine1.requestFocus();
					} else {
						addressLine1LocalLanguage.setText(addressLine1.getText());
					}
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

			mobileNo.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					if (newValue.length() != 0 && (!newValue.matches(RegistrationConstants.MOBILE_NUMBER_REGEX)
							|| newValue.length() > RegistrationConstants.MOBILE_NUMBER_LENGTH)) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.MOBILE_NUMBER_EXAMPLE);
						mobileNo.setText(mobileNo.getText().replaceAll(".$", ""));
						mobileNo.requestFocus();
					}
				}
			});

			emailId.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					if (newValue.length() != 0 && (!newValue.matches(RegistrationConstants.EMAIL_ID_REGEX_INITIAL)
							|| newValue.length() > 50)) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.EMAIL_ID_EXAMPLE);
						emailId.setText(emailId.getText().replaceAll(".$", ""));
						emailId.requestFocus();
					}
				}
			});

			cniOrPinNumber.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					if (newValue.length() != 0 && !newValue.matches(RegistrationConstants.CNI_OR_PIN_NUMBER_REGEX)) {
						generateAlert(RegistrationConstants.ALERT_ERROR,
								RegistrationConstants.CNIE_OR_PIN_NUMBER_WARNING);
						cniOrPinNumber.setText(cniOrPinNumber.getText().replaceAll(".$", ""));
						cniOrPinNumber.requestFocus();
					}
				}
			});

			parentName.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					if (newValue.length() != 0 && (!newValue.matches(RegistrationConstants.FULL_NAME_REGEX)
							|| newValue.length() > RegistrationConstants.FULL_NAME_LENGTH)) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.ONLY_ALPHABETS);

						parentName.setText(parentName.getText().replaceAll(".$", ""));
						parentName.requestFocus();
					}
				}
			});

			uinId.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
						final String newValue) {
					if (newValue.length() != 0 && !newValue.matches(RegistrationConstants.UIN_REGEX)) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UIN_ID_WARNING);
						uinId.setText(uinId.getText().replaceAll(".$", ""));
						uinId.requestFocus();
					}
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
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_ISEDIT);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_PANE1_DATA);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_PANE2_DATA);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_AGE_DATA);
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
		if (validateRegex(fullName, RegistrationConstants.FULL_NAME_REGEX)) {
			generateAlert(RegistrationConstants.ALERT_ERROR,
					RegistrationConstants.FULL_NAME_EMPTY + " " + RegistrationConstants.ONLY_ALPHABETS);

			fullName.requestFocus();
		} else {
			if (validateAgeOrDob()) {
				if (gender.getValue() == null) {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.GENDER_EMPTY);

					gender.requestFocus();
				} else {
					if (validateRegex(addressLine1, RegistrationConstants.ADDRESS_LINE1_REGEX)) {

						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.ADDRESS_LINE_1_EMPTY
								+ " " + RegistrationConstants.ADDRESS_LINE_WARNING);
						addressLine1.requestFocus();
					} else {
						if (validateRegex(region, "^.{6,50}$")) {

							generateAlert(RegistrationConstants.ALERT_ERROR,
									RegistrationConstants.REGION_EMPTY + " " + RegistrationConstants.ONLY_ALPHABETS
											+ " " + RegistrationConstants.TEN_LETTER_INPUT_LIMT);
							region.requestFocus();
						} else {
							if (validateRegex(city, "^.{6,10}$")) {

								generateAlert(RegistrationConstants.ALERT_ERROR,
										RegistrationConstants.CITY_EMPTY + " " + RegistrationConstants.ONLY_ALPHABETS
												+ " " + RegistrationConstants.TEN_LETTER_INPUT_LIMT);
								city.requestFocus();
							} else {
								if (validateRegex(province, "^.{6,10}$")) {

									generateAlert(RegistrationConstants.ALERT_ERROR,
											RegistrationConstants.PROVINCE_EMPTY + " "
													+ RegistrationConstants.ONLY_ALPHABETS + " "
													+ RegistrationConstants.TEN_LETTER_INPUT_LIMT);
									province.requestFocus();
								} else {
									if (validateRegex(postalCode, "\\d{6}")) {
										generateAlert(RegistrationConstants.ALERT_ERROR,
												RegistrationConstants.POSTAL_CODE_EMPTY + " "
														+ RegistrationConstants.SIX_DIGIT_INPUT_LIMT);
										postalCode.requestFocus();
									} else {
										if (validateRegex(localAdminAuthority, "^.{6,10}$")) {
											generateAlert(RegistrationConstants.ALERT_ERROR,
													RegistrationConstants.LOCAL_ADMIN_AUTHORITY_EMPTY + " "
															+ RegistrationConstants.ONLY_ALPHABETS);
											localAdminAuthority.requestFocus();
										} else {
											if (validateRegex(mobileNo, RegistrationConstants.MOBILE_NUMBER_REGEX)) {

												generateAlert(RegistrationConstants.ALERT_ERROR,
														RegistrationConstants.MOBILE_NUMBER_EMPTY + " "
																+ RegistrationConstants.MOBILE_NUMBER_EXAMPLE);
												mobileNo.requestFocus();
											} else {
												if (validateRegex(emailId, RegistrationConstants.EMAIL_ID_REGEX)) {

													generateAlert(RegistrationConstants.ALERT_ERROR,
															RegistrationConstants.EMAIL_ID_EMPTY + " "
																	+ RegistrationConstants.EMAIL_ID_EXAMPLE);
													emailId.requestFocus();
												} else {
													if (validateRegex(cniOrPinNumber,
															RegistrationConstants.CNI_OR_PIN_NUMBER_REGEX)) {

														generateAlert(RegistrationConstants.ALERT_ERROR,
																RegistrationConstants.CNIE_OR_PIN_NUMBER_EMPTY + " "
																		+ RegistrationConstants.THIRTY_DIGIT_INPUT_LIMT);
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

		if (isChild && validateRegex(parentName, RegistrationConstants.FULL_NAME_REGEX)) {
			generateAlert(RegistrationConstants.ALERT_ERROR,
					RegistrationConstants.PARENT_NAME_EMPTY + " " + RegistrationConstants.ONLY_ALPHABETS);
			parentName.requestFocus();
		} else {
			if (isChild && validateRegex(uinId, RegistrationConstants.UIN_REGEX)) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.UIN_ID_EMPTY);
				uinId.requestFocus();
			} else {
				if (poaBox.getChildren().isEmpty()) {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.POA_DOCUMENT_EMPTY);
				} else {
					if (poiBox.getChildren().isEmpty()) {
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.POI_DOCUMENT_EMPTY);
					} else {
						if (isChild && porBox.getChildren().isEmpty()) {
							generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.POR_DOCUMENT_EMPTY);
						} else {
							if (dobBox.getChildren().isEmpty()) {
								generateAlert(RegistrationConstants.ALERT_ERROR,
										RegistrationConstants.DOB_DOCUMENT_EMPTY);
							} else {
								gotoNext = true;
							}
						}
					}
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
			ResourceBundle localProperties = applicationContext.getLocalLanguageProperty();
			fullNameLocalLanguageLabel.setText(localProperties.getString("full_name"));
			addressLine1LocalLanguagelabel.setText(localProperties.getString("address_line1"));
			addressLine2LocalLanguagelabel.setText(localProperties.getString("address_line2"));
			addressLine3LocalLanguagelabel.setText(localProperties.getString("address_line3"));
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
			dobDocuments.getItems().addAll(RegistrationConstants.getDobDocumentList());
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING LIST OF DOCUMENTS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	private boolean validateAgeOrDob() {
		boolean gotoNext = false;
		if (toggleAgeOrDobField) {
			if (validateRegex(ageField, RegistrationConstants.AGE_REGEX)) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.AGE_EMPTY);

				ageField.requestFocus();
			} else {
				if (Integer.parseInt(ageField.getText()) < Integer
						.parseInt(AppConfig.getApplicationProperty("age_limit_for_child"))) {
					childSpecificFields.setVisible(true);
				}
				gotoNext = true;
			}
		} else if (!toggleAgeOrDobField) {
			if (ageDatePicker.getValue() == null) {
				generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DATE_OF_BIRTH_EMPTY);

				ageDatePicker.requestFocus();
			} else {
				gotoNext = true;
			}
		}
		return gotoNext;
	}

	public RegistrationDTO getRegistrationDtoContent() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}

	private DatePicker getAgeDatePickerContent() {
		return (DatePicker) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_AGE_DATA);
	}

	private Boolean isEditPage() {
		if (SessionContext.getInstance().getMapObject().get(RegistrationConstants.REGISTRATION_ISEDIT) != null)
			return (Boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.REGISTRATION_ISEDIT);
		return false;
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

	public AnchorPane getBiometricsPane() {
		return biometricsPane;
	}

	public void setBiometricsPane(AnchorPane biometricsPane) {
		this.biometricsPane = biometricsPane;
	}

	/**
	 * @return the demoGraphicTitlePane
	 */
	public TitledPane getDemoGraphicTitlePane() {
		return demoGraphicTitlePane;
	}

	/**
	 * @param demoGraphicTitlePane
	 *            the demoGraphicTitlePane to set
	 */
	public void setDemoGraphicTitlePane(TitledPane demoGraphicTitlePane) {
		this.demoGraphicTitlePane = demoGraphicTitlePane;
	}

	// Operator Authentication
	public void goToAuthenticationPage() {
		try {
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_ISEDIT, true);
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

	/**
	 * This method toggles the visible property of the IrisCapture Pane.
	 * 
	 * @param visibility
	 *            the value of the visible property to be set
	 */
	public void toggleIrisCaptureVisibility(boolean visibility) {
		this.irisCapture.setVisible(visibility);
	}

	/**
	 * This method toggles the visible property of the FingerprintCapture Pane.
	 * 
	 * @param visibility
	 *            the value of the visible property to be set
	 */
	public void toggleFingerprintCaptureVisibility(boolean visibility) {
		this.fingerPrintCapturePane.setVisible(visibility);
	}

	/**
	 * This method toggles the visible property of the PhotoCapture Pane.
	 * 
	 * @param visibility
	 *            the value of the visible property to be set
	 */
	public void togglePhotoCaptureVisibility(boolean visibility) {
		if (visibility) {
			if (capturePhotoUsingDevice.equals("Y")) {
				defaultImage = applicantImage.getImage();
				biometrics.setVisible(false);
				biometricsNext.setVisible(false);
				getBiometricsPane().setVisible(true);
			} else if (capturePhotoUsingDevice.equals("N")) {
				biometrics.setVisible(true);
				biometricsNext.setVisible(true);
				getBiometricsPane().setVisible(false);
				biometricsNext.setDisable(false);
			}
		} else {
			biometrics.setVisible(visibility);
			biometricsNext.setVisible(visibility);
			getBiometricsPane().setVisible(visibility);
		}
	}

	/**
	 * This method scans and uploads Proof of Address documents
	 */
	@FXML
	private void scanPoaDocument() {

		if (poaDocuments.getValue() == null) {

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.POA_DOCUMENT_EMPTY);
			poaDocuments.requestFocus();

		} else {

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Displaying Scan window to scan Proof of Address Documents");

			selectedDocument = RegistrationConstants.POA_DOCUMENT;
			scanWindow();
		}
	}

	/**
	 * This method scans and uploads Proof of Identity documents
	 */
	@FXML
	private void scanPoiDocument() {

		if (poiDocuments.getValue() == null) {

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.POI_DOCUMENT_EMPTY);
			poiDocuments.requestFocus();

		} else {

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Displaying Scan window to scan Proof of Identity Documents");

			selectedDocument = RegistrationConstants.POI_DOCUMENT;
			scanWindow();
		}
	}

	/**
	 * This method scans and uploads Proof of Relation documents
	 */
	@FXML
	private void scanPorDocument() {

		if (porDocuments.getValue() == null) {

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.POR_DOCUMENT_EMPTY);
			porDocuments.requestFocus();

		} else {

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Displaying Scan window to scan Proof of Relation Documents");

			selectedDocument = RegistrationConstants.POR_DOCUMENT;
			scanWindow();
		}
	}

	/**
	 * This method scans and uploads Proof of Date of birth documents
	 */
	@FXML
	private void scanDobDocument() {

		if (dobDocuments.getValue() == null) {

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.DOB_DOCUMENT_EMPTY);
			dobDocuments.requestFocus();

		} else {

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Displaying Scan window to scan Proof of Relation Documents");

			selectedDocument = RegistrationConstants.DOB_DOCUMENT;
			scanWindow();
		}
	}

	/**
	 * This method will display Scan window to scan and upload documents
	 */
	private void scanWindow() {

		scanController.init(this, RegistrationConstants.SCAN_DOC_TITLE);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scan window displayed to scan and upload documents");
	}

	/**
	 * This method will allow to scan and upload documents
	 */
	@Override
	public void scan(Stage popupStage) {

		try {

			byte[] byteArray = documentScanFacade.getScannedDocument();

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Converting byte array to image");

			if (byteArray.length > documentSize) {

				generateAlert(RegistrationConstants.ALERT_ERROR,
						"Document size should be less than 1 MB. Please re-scan the document.");

			} else {

				if (selectedDocument != null) {

					LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID, "Adding documents to Screen");

					String docName = "";

					if (selectedDocument.equals(RegistrationConstants.POA_DOCUMENT)) {

						docName = addDocuments(poaDocuments.getValue(), poaBox);
						validateDocuments(docName, poaBox, poaScroll, byteArray);
						poaDocuments.setValue(null);
						poaDocuments.setPromptText(applicationProperties.getString("poaDocumentLabel"));

					} else if (selectedDocument.equals(RegistrationConstants.POI_DOCUMENT)) {

						docName = addDocuments(poiDocuments.getValue(), poiBox);
						validateDocuments(docName, poiBox, poiScroll, byteArray);
						poiDocuments.setValue(null);
						poiDocuments.setPromptText(applicationProperties.getString("poiDocumentLabel"));

					} else if (selectedDocument.equals(RegistrationConstants.POR_DOCUMENT)) {

						docName = addDocuments(porDocuments.getValue(), porBox);
						validateDocuments(docName, porBox, porScroll, byteArray);
						porDocuments.setValue(null);
						porDocuments.setPromptText(applicationProperties.getString("porDocumentLabel"));

					} else if (selectedDocument.equals(RegistrationConstants.DOB_DOCUMENT)) {

						docName = addDocuments(dobDocuments.getValue(), dobBox);
						validateDocuments(docName, dobBox, dobScroll, byteArray);
						dobDocuments.setValue(null);
						dobDocuments.setPromptText(applicationProperties.getString("dobDocumentLabel"));

					}

					popupStage.close();

					LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID, "Documents added successfully");
				}
			}

		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.LOG_REG_REGISTRATION_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while scanning documents for registration  %s -> %s",
							RegistrationConstants.USER_REG_DOC_SCAN_UPLOAD_EXP, ioException.getMessage(),
							ioException.getCause()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.SCAN_DOCUMENT_ERROR);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.LOG_REG_REGISTRATION_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s -> Exception while scanning documents for registration  %s",
							RegistrationConstants.USER_REG_DOC_SCAN_UPLOAD_EXP, runtimeException.getMessage()));

			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.SCAN_DOCUMENT_ERROR);
		}

	}

	/**
	 * This method will validate number of documents
	 */
	private String addDocuments(String document, VBox vboxElement) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating number of documemnts");

		ObservableList<Node> nodes = vboxElement.getChildren();
		if (nodes.isEmpty()) {
			return document;
		} else if (nodes.stream().anyMatch(index -> index.getId().contains(document))) {
			return document.concat("_").concat(String.valueOf(nodes.size()));
		} else {
			return RegistrationConstants.ERROR;
		}
	}

	/**
	 * This method will validate with existing documents
	 */
	private void validateDocuments(String document, VBox vboxElement, ScrollPane scrollPane, byte[] byteArray) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating documents before adding to Screen");

		if (!document.equals(RegistrationConstants.ERROR)) {
			attachDocuments(document, vboxElement, scrollPane, byteArray);
		} else {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationConstants.SCAN_DOC_CATEGORY_MULTIPLE);
		}
	}

	/**
	 * This method will add Hyperlink and Image for scanned documents
	 */
	private void attachDocuments(String document, VBox vboxElement, ScrollPane scrollPane, byte[] byteArray) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Attaching documemnts to Pane");

		scanController.getScanImage().setImage(convertBytesToImage(byteArray));

		DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO.setDocument(byteArray);
		documentDetailsDTO.setDocumentName(document);
		documentDetailsDTO.setDocumentCategory(document);
		documentDetailsDTO.setDocumentType(selectedDocument);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set details to DocumentDetailsDTO");

		getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
				.add(documentDetailsDTO);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Set DocumentDetailsDTO to RegistrationDTO");

		addDocumentsToScreen(document, vboxElement, scrollPane);

		generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationConstants.SCAN_DOC_SUCCESS);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Setting scrollbar policy for scrollpane");

	}

	private void addDocumentsToScreen(String document, VBox vboxElement, ScrollPane scrollPane) {

		GridPane anchorPane = new GridPane();
		anchorPane.setId(document);

		anchorPane.add(createHyperLink(document), 0, vboxElement.getChildren().size());
		anchorPane.add(createImageView(vboxElement, scrollPane), 1, vboxElement.getChildren().size());

		vboxElement.getChildren().add(anchorPane);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scan document added to Vbox element");

		if (vboxElement.getChildren().size() > scrollCheck) {
			scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
			scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		} else {
			scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
			scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		}
	}

	/**
	 * This method will set scrollbar policy for scroll pane
	 */
	private void setScrollFalse() {
		poaScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		poaScroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		poiScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		poiScroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		porScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		porScroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		dobScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		dobScroll.setVbarPolicy(ScrollBarPolicy.NEVER);
	}

	/**
	 * This method will create Hyperlink to view scanned document
	 */
	private Hyperlink createHyperLink(String document) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Creating Hyperlink to display Scanned document");

		Hyperlink hyperLink = new Hyperlink();
		hyperLink.setId(document);
		hyperLink.setText(document);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Binding OnAction event to Hyperlink to display Scanned document");

		hyperLink.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				GridPane pane = (GridPane) ((Hyperlink) actionEvent.getSource()).getParent();
				getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
						.stream().filter(detail -> detail.getDocumentName().equals(pane.getId())).findFirst()
						.ifPresent(doc -> displayDocument(doc.getDocument()));

			}
		});

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Hyperlink added to display Scanned document");

		return hyperLink;
	}

	/**
	 * This method will create Image to delete scanned document
	 */
	private ImageView createImageView(VBox vboxElement, ScrollPane scrollPane) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Binding OnAction event Image to delete the attached document");

		Image image = new Image(this.getClass().getResourceAsStream(RegistrationConstants.CLOSE_IMAGE_PATH));
		ImageView imageView = new ImageView(image);
		imageView.setCursor(Cursor.HAND);

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Creating Image to delete the attached document");

		imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				GridPane gridpane = (GridPane) ((ImageView) event.getSource()).getParent();
				vboxElement.getChildren().remove(gridpane);
				getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO()
						.removeIf(document -> document.getDocumentName().equals(gridpane.getId()));
				if (vboxElement.getChildren().isEmpty()) {
					scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
					scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
				}
				getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocumentDetailsDTO();
			}

		});

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Image added to delete the attached document");

		return imageView;
	}

	/**
	 * This method will display the scanned document
	 */
	private void displayDocument(byte[] document) {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Converting bytes to Image to display scanned document");

		Image img = convertBytesToImage(document);
		ImageView view = new ImageView(img);
		Scene scene = new Scene(new StackPane(view), 700, 600);
		Stage primaryStage = new Stage();
		primaryStage.setTitle(RegistrationConstants.SCAN_DOC_TITLE);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Scanned document displayed succesfully");
	}

	private void createRegistrationDTOObject() {
		RegistrationDTO registrationDTO = new RegistrationDTO();

		// Create objects for Biometric DTOS
		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setApplicantBiometricDTO(createBiometricInfoDTO());
		biometricDTO.setIntroducerBiometricDTO(createBiometricInfoDTO());
		biometricDTO.setOperatorBiometricDTO(createBiometricInfoDTO());
		biometricDTO.setSupervisorBiometricDTO(createBiometricInfoDTO());
		registrationDTO.setBiometricDTO(biometricDTO);

		// Create object for Demographic DTOS
		DemographicDTO demographicDTO = new DemographicDTO();
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		applicantDocumentDTO.setDocumentDetailsDTO(new ArrayList<>());
		demographicDTO.setApplicantDocumentDTO(applicantDocumentDTO);
		demographicDTO.setDemoInLocalLang(new DemographicInfoDTO());
		demographicDTO.setDemoInUserLang(new DemographicInfoDTO());
		registrationDTO.setDemographicDTO(demographicDTO);

		// Create object for OSIData DTO
		registrationDTO.setOsiDataDTO(new OSIDataDTO());

		// Create object for RegistrationMetaData DTO
		registrationDTO.setRegistrationMetaDataDTO(new RegistrationMetaDataDTO());

		// Put the RegistrationDTO object to SessionContext Map
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_DATA, registrationDTO);
	}

	private BiometricInfoDTO createBiometricInfoDTO() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setFingerPrintBiometricExceptionDTO(new ArrayList<>());
		biometricInfoDTO.setFingerprintDetailsDTO(new ArrayList<>());
		biometricInfoDTO.setIrisBiometricExceptionDTO(new ArrayList<>());
		biometricInfoDTO.setIrisDetailsDTO(new ArrayList<>());
		return biometricInfoDTO;
	}

}
