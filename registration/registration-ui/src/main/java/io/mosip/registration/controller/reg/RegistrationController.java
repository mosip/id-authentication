package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import io.mosip.registration.controller.VirtualKeyboard;
import io.mosip.registration.controller.auth.AuthenticationController;
import io.mosip.registration.controller.device.WebCameraController;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SelectionListDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.ArrayPropertiesDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.dto.demographic.SimplePropertiesDTO;
import io.mosip.registration.dto.demographic.ValuesDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.external.PreRegZipHandlingService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.util.dataprovider.DataProvider;
import io.mosip.registration.util.kernal.RIDGenerator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

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

	@Autowired
	private DocumentScanController documentScanController;

	@Autowired
	private AuthenticationController authenticationController;
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

	private DatePicker autoAgeDatePicker = new DatePicker();

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

	@FXML
	private ScrollPane demoScrollPane;

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
	private ImageView headerImage;

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
	private Label copyAddressLabel;

	@FXML
	private ImageView copyAddressImage;

	private boolean isChild;

	private Node keyboardNode;

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;

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
	private AnchorPane fingerPrintCapturePane;
	@FXML
	private AnchorPane irisCapture;

	private BufferedImage applicantBufferedImage;
	private BufferedImage exceptionBufferedImage;

	private boolean applicantImageCaptured;
	private boolean exceptionImageCaptured;

	private boolean toggleBiometricException;

	private Image defaultImage;

	@FXML
	private TitledPane authenticationTitlePane;

	@Autowired
	private PreRegZipHandlingService preRegZipHandlingService;

	@Autowired
	private PreRegistrationDataSyncService preRegistrationDataSyncService;

	@Autowired
	private WebCameraController webCameraController;

	private boolean dobSelectionFromCalendar = true;

	@Autowired
	private IdValidator<String> pridValidatorImpl;
	@Autowired
	private Validations validation;
	@FXML
	private Text paneLabel;
	@FXML
	private AnchorPane dateAnchorPane;
	@FXML
	private AnchorPane addressAnchorPane;
	@FXML
	private Label preRegistrationLabel;
	@FXML
	private Label fullNameLabel;
	@FXML
	private Label genderLabel;
	@FXML
	private Label mobileNoLabel;
	@FXML
	private Label emailIdLabel;
	@FXML
	private Label cnieLabel;			 

	FXUtils fxUtils;

	@FXML
	private void initialize() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");
		try {
			demoScrollPane.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight()-5);

			auditFactory.audit(AuditEvent.GET_REGISTRATION_CONTROLLER, Components.REGISTRATION_CONTROLLER,
					"initializing the registration controller",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Create RegistrationDTO Object
			if (getRegistrationDtoContent() == null) {
				createRegistrationDTOObject();
			}

			if (capturePhotoUsingDevice.equals("Y") && !isEditPage()) {
				defaultImage = applicantImage.getImage();
				applicantImageCaptured = false;
				exceptionImageCaptured = false;
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
			fxUtils = FXUtils.getInstance();
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_CONSOLIDATED, "N");
			switchedOn = new SimpleBooleanProperty(false);
			switchedOnForBiometricException = new SimpleBooleanProperty(false);
			isChild = true;
			ageDatePicker.setDisable(false);
			toggleFunction();
			toggleFunctionForBiometricException();
			ageFieldValidations();
			ageValidationInDatePicker();
			listenerOnFields();
			loadLocalLanguageFields();
			loadKeyboard();
			ageField.setDisable(true);
			accord.setExpandedPane(demoGraphicTitlePane);
			fxUtils.dateFormatter(ageDatePicker);
			fxUtils.disableFutureDays(ageDatePicker);

			if (isEditPage() && getRegistrationDtoContent() != null) {
				prepareEditPageContent();
			}
			if (getRegistrationDtoContent().getSelectionListDTO() != null) {

				ObservableList<Node> nodes = demoGraphicPane1.getChildren();

				for (Node node : nodes) {
					node.setDisable(true);
				}
				paneLabel.setText("/ UIN Update");
				fetchBtn.setVisible(false);
				headerImage.setVisible(false);
				nextBtn.setDisable(false);
				preRegistrationLabel.setText("UIN");

				preRegistrationId.setText(getRegistrationDtoContent().getSelectionListDTO().getUinId());
				preRegistrationId.setDisable(false);
				childSpecificFields.setVisible(getRegistrationDtoContent().getSelectionListDTO().isChild());

				fullName.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isName());
				fullNameLocalLanguage.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isName());
fullNameLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isName());
				dateAnchorPane.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isAge());

				gender.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isGender());
				genderLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isGender());						

				addressAnchorPane.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isAddress());

				mobileNo.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
				mobileNoLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
				emailId.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
emailIdLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
				cniOrPinNumber.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isCnieNumber());
																			cnieLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isCnieNumber());

			}

		} catch (IOException | RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}

		public void init(SelectionListDTO selectionListDTO) {
		createRegistrationDTOObject();
		getRegistrationDtoContent().setSelectionListDTO(selectionListDTO);
	}
	/**
	 * Loading the virtual keyboard
	 */
	private void loadKeyboard() {
		try {
			VirtualKeyboard vk = VirtualKeyboard.getInstance();
			keyboardNode = vk.view();
			demoGraphicPane1.getChildren().add(keyboardNode);
			keyboardNode.setVisible(false);
			vk.changeControlOfKeyboard(fullNameLocalLanguage);
			vk.changeControlOfKeyboard(addressLine1LocalLanguage);
			vk.changeControlOfKeyboard(addressLine2LocalLanguage);
			vk.changeControlOfKeyboard(addressLine3LocalLanguage);
		} catch (NullPointerException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
		}
	}

	/**
	 * This method is to prepopulate all the values for edit operation
	 */
	private void prepareEditPageContent() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Preparing the Edit page content");

			DemographicInfoDTO demo = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO();

			populateFieldValue(fullName, fullNameLocalLanguage, demo.getIdentity().getFullName().getValues());
			populateFieldValue(gender, null, demo.getIdentity().getGender().getValues());
			populateFieldValue(addressLine1, addressLine1LocalLanguage,
					demo.getIdentity().getAddressLine1().getValues());
			populateFieldValue(addressLine2, addressLine2LocalLanguage,
					demo.getIdentity().getAddressLine2().getValues());
			populateFieldValue(addressLine3, addressLine3LocalLanguage,
					demo.getIdentity().getAddressLine3().getValues());
			populateFieldValue(region, null, demo.getIdentity().getRegion().getValues());
			populateFieldValue(province, null, demo.getIdentity().getProvince().getValues());
			populateFieldValue(city, null, demo.getIdentity().getCity().getValues());
			populateFieldValue(gender, null, demo.getIdentity().getGender().getValues());
			postalCode.setText(demo.getIdentity().getPostalCode());
			mobileNo.setText(demo.getIdentity().getPhone().getValue());
			emailId.setText(demo.getIdentity().getEmail().getValue());
			cniOrPinNumber.setText(demo.getIdentity().getCnieNumber());

			populateFieldValue(localAdminAuthority, null,
					demo.getIdentity().getLocalAdministrativeAuthority().getValues());

			if (demo.getIdentity().getParentOrGuardianRIDOrUIN() != null
					&& !demo.getIdentity().getParentOrGuardianRIDOrUIN().isEmpty()) {
				populateFieldValue(parentName, null, demo.getIdentity().getParentOrGuardianName().getValues());
				uinId.setText(demo.getIdentity().getParentOrGuardianRIDOrUIN());
			}

			if (demo.getIdentity().getDateOfBirth().getValue() != null && getAgeDatePickerContent() != null
					&& dobSelectionFromCalendar) {
				ageDatePicker.setValue(getAgeDatePickerContent().getValue());
			} else {
				switchedOn.set(true);
				ageDatePicker.setDisable(true);
				ageField.setDisable(false);
				ageField.setText(demo.getIdentity().getAge());
				if (isEditPage())
					autoAgeDatePicker.setValue(getAgeDatePickerContent().getValue());
			}

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

			documentScanController.prepareEditPageContent();
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_ISEDIT, false);
			ageFieldValidations();
			ageValidationInDatePicker();

		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}

	}

	private void populateFieldValue(Node nodeForPlatformLang, Node nodeForLocalLang, List<ValuesDTO> fieldValues) {
		String platformLanguageCode = AppConfig.getApplicationProperty("application_language");
		String localLanguageCode = AppConfig.getApplicationProperty("local_language");
		String valueInPlatformLang = "";
		String valueinLocalLang = "";

		for (ValuesDTO fieldValue : fieldValues) {
			if (fieldValue.getLanguage().equals(platformLanguageCode)) {
				valueInPlatformLang = fieldValue.getValue();
			} else if (nodeForLocalLang != null && fieldValue.getLanguage().equals(localLanguageCode)) {
				valueinLocalLang = fieldValue.getValue();
			}
		}

		if (nodeForPlatformLang instanceof TextField) {
			((TextField) nodeForPlatformLang).setText(valueInPlatformLang);

			if (nodeForLocalLang != null) {
				((TextField) nodeForLocalLang).setText(valueinLocalLang);
			}
		} else if (nodeForPlatformLang instanceof ComboBox) {
			((ComboBox) nodeForPlatformLang).setValue(valueInPlatformLang);

			if (nodeForLocalLang != null) {
				((ComboBox) nodeForLocalLang).setValue(valueinLocalLang);
			}
		}
	}

	@FXML
	private void fetchPreRegistration() {
		String preRegId = preRegistrationId.getText();

		if (StringUtils.isEmpty(preRegId)) {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.PRE_REG_ID_EMPTY);
			return;
		} else {
			try {
				pridValidatorImpl.validateId(preRegId);
			} catch (InvalidIDException invalidIDException) {
				generateAlert(RegistrationConstants.ALERT_ERROR, invalidIDException.getErrorText());
				return;
			}
		}
		ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistration(preRegId);

		SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
		List<ErrorResponseDTO> errorResponseDTOList = responseDTO.getErrorResponseDTOs();

		if (successResponseDTO != null && successResponseDTO.getOtherAttributes() != null
				&& successResponseDTO.getOtherAttributes().containsKey("registrationDto")) {
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_DATA,
					successResponseDTO.getOtherAttributes().get("registrationDto"));
			prepareEditPageContent();

		} else if (errorResponseDTOList != null && !errorResponseDTOList.isEmpty()) {
			generateAlert(RegistrationConstants.ALERT_ERROR, errorResponseDTOList.get(0).getMessage());
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
			if (SessionContext.getInstance().getMapObject().get(RegistrationConstants.ADDRESS_KEY) == null) {
				generateAlert(RegistrationConstants.ALERT_ERROR,
						"Address could not be loaded as there is no previous entry");
				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						"Address could not be loaded as there is no previous entry");

			} else {
				LocationDTO locationDto = ((AddressDTO) SessionContext.getInstance().getMapObject()
						.get(RegistrationConstants.ADDRESS_KEY)).getLocationDTO();
				region.setText(locationDto.getRegion());
				city.setText(locationDto.getCity());
				province.setText(locationDto.getProvince());
				postalCode.setText(locationDto.getPostalCode());
				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Loaded address from previous entry");
			}
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

			if (validateDemographicPane(demoGraphicPane1)) {
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
			keyboardNode.setVisible(!keyboardNode.isVisible());

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
	@SuppressWarnings("unchecked")
	@FXML
	private void saveDetail() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Saving the fields to DTO");
		try {
			auditFactory.audit(AuditEvent.SAVE_DETAIL_TO_DTO, Components.REGISTRATION_CONTROLLER,
					"Saving the details to respected DTO", SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			RegistrationDTO registrationDTO = getRegistrationDtoContent();
			Identity demographicIdentity = registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity();
			DemographicInfoDTO demographicInfoDTO ;

			OSIDataDTO osiDataDTO = registrationDTO.getOsiDataDTO();
			RegistrationMetaDataDTO registrationMetaDataDTO = registrationDTO.getRegistrationMetaDataDTO();

			String platformLanguageCode = AppConfig.getApplicationProperty("application_language");
			String localLanguageCode = AppConfig.getApplicationProperty("local_language");

			if (true) {//validateDemographicPane(demoGraphicPane2)) {

				demographicInfoDTO = Builder.build(DemographicInfoDTO.class)
						.with(demographicDTO -> demographicDTO.setIdentity(Builder.build(Identity.class)
								.with(identity -> identity.setFullName((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class).with(name -> name.setLabel("First Name"))
										.with(name -> name.setValues(Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(fullName.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(fullNameLocalLanguage.getText()))
														.get()))
												.get()))
										.get()))
								.with(identity -> identity.setDateOfBirth(Builder.build(SimplePropertiesDTO.class)
										.with(value -> value.setLabel("Date Of Birth"))
										.with(value -> value.setValue(DateUtils.formatDate(Date.from(
												(ageDatePicker.getValue() == null ? autoAgeDatePicker : ageDatePicker)
														.getValue().atStartOfDay().atZone(ZoneId.systemDefault())
														.toInstant()),
												"yyyy/MM/dd")))
										.get()))

								.with(identity -> identity.setAge(ageField.getText()))
								.with(identity -> identity.setGender((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class)
										.with(genderValue -> genderValue.setLabel("Gender"))
										.with(genderValue -> genderValue.setValues(Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(gender.getValue())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(gender.getValue())).get()))
												.get()))
										.get()))
								.with(identity -> identity.setAddressLine1((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class)
										.with(addressValue -> addressValue.setLabel("Address Line 1"))
										.with(addressValue -> addressValue.setValues(Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(addressLine1.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))

														.with(value -> value
																.setValue(addressLine1LocalLanguage.getText()))
														.get()))
												.get()))
										.get()))
								.with(identity -> identity.setAddressLine2((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class)
										.with(addressValue -> addressValue.setLabel("Address Line 2"))
										.with(addressValue -> addressValue.setValues(Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(addressLine2.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))

														.with(value -> value
																.setValue(addressLine2LocalLanguage.getText()))
														.get()))
												.get()))
										.get()))
								.with(identity -> identity.setAddressLine3((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class)
										.with(addressValue -> addressValue.setLabel("Address Line 3"))
										.with(addressValue -> addressValue.setValues(Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(addressLine3.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))

														.with(value -> value
																.setValue(addressLine3LocalLanguage.getText()))
														.get()))
												.get()))
										.get()))
								.with(identity -> identity.setRegion((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class)
										.with(regionValue -> regionValue.setLabel("Region"))
										.with(regionValue -> regionValue.setValues(Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(region.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(region.getText())).get()))
												.get()))
										.get()))
								.with(identity -> identity.setProvince((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class)
										.with(provinceValue -> provinceValue.setLabel("Province"))
										.with(provinceValue -> provinceValue.setValues(Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(province.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(province.getText())).get()))
												.get()))
										.get()))
								.with(identity -> identity.setCity((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class).with(cityValue -> cityValue.setLabel("City"))
										.with(cityValue -> cityValue.setValues(Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(city.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(city.getText())).get()))
												.get()))
										.get()))
								.with(identity -> identity.setPostalCode(postalCode.getText()))

								.with(identity -> identity.setPhone(Builder.build(SimplePropertiesDTO.class)
										.with(value -> value.setLabel("Land Line"))
										.with(value -> value.setValue(mobileNo.getText())).get()))
								.with(identity -> identity.setEmail(Builder.build(SimplePropertiesDTO.class)
										.with(value -> value.setLabel("Business Email"))
										.with(value -> value.setValue(emailId.getText())).get()))
								.with(identity -> identity.setCnieNumber(cniOrPinNumber.getText()))
								.with(identity -> identity.setLocalAdministrativeAuthority((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class)
										.with(localAdminAuthValue -> localAdminAuthValue
												.setLabel("Local Administrative Authority"))
										.with(localAdminAuthValue -> localAdminAuthValue.setValues(Builder
												.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(localAdminAuthority.getText()))
														.get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(localAdminAuthority.getText()))
														.get()))
												.get()))
										.get()))
								.with(identity -> identity.setParentOrGuardianName((ArrayPropertiesDTO) Builder
										.build(ArrayPropertiesDTO.class)
										.with(parentValue -> parentValue.setLabel("Parent/Guardian"))
										.with(parentValue -> parentValue.setValues(Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(parentName.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(parentName.getText())).get()))
												.get()))
										.get()))
								.with(identity -> identity.setParentOrGuardianRIDOrUIN(uinId.getText()))
								.with(identity -> identity.setProofOfIdentity(demographicIdentity.getProofOfIdentity()))
								.with(identity -> identity.setProofOfAddress(demographicIdentity.getProofOfAddress()))
								.with(identity -> identity
										.setProofOfRelationship(demographicIdentity.getProofOfRelationship()))
								.with(identity -> identity
										.setDateOfBirthProof(demographicIdentity.getDateOfBirthProof()))
								.get()))
						.get();

				dobSelectionFromCalendar = ageDatePicker.getValue() != null;

				if (isChild) {

					osiDataDTO.setIntroducerType(IntroducerType.PARENT.getCode());

					registrationMetaDataDTO.setApplicationType("Child");
				} else {
					registrationMetaDataDTO.setApplicationType("Adult");
				}

				osiDataDTO.setOperatorID(SessionContext.getInstance().getUserContext().getUserId());

				registrationDTO.setPreRegistrationId(preRegistrationId.getText());
				registrationDTO.getDemographicDTO().setDemographicInfoDTO(demographicInfoDTO);

				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Saved the demographic fields to DTO");

				if (ageDatePicker.getValue() != null) {
					SessionContext.getInstance().getMapObject().put("ageDatePickerContent", ageDatePicker);
				} else {
					SessionContext.getInstance().getMapObject().put("ageDatePickerContent", autoAgeDatePicker);
				}
				biometricTitlePane.setExpanded(true);

			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - SAVING THE DETAILS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	@FXML
	private void goToPreviousPane() {
		try {
			toggleIrisCaptureVisibility(true);
			togglePhotoCaptureVisibility(false);
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

	@FXML
	private void saveBiometricDetails() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "saving the details of applicant biometrics");
		boolean isValid = true;
		isValid = validateDemographicPane(demoGraphicPane1);
		if (isValid) {
			isValid = true;//validateDemographicPane(demoGraphicPane2);
		}
		if (!isValid) {
			demoGraphicTitlePane.setExpanded(true);
			toggleIrisCaptureVisibility(true);
		}
		if (isValid) {

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

						LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER,
								RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
								"showing demographic preview");

						setPreviewContent();
						loadScreen(RegistrationConstants.DEMOGRAPHIC_PREVIEW);
					} catch (IOException ioException) {
						LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
								RegistrationConstants.APPLICATION_ID, ioException.getMessage());
					}
				}

			} else {
				try {
					DataProvider.setApplicantDocumentDTO(
							getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO(),
							toggleBiometricException);
					setPreviewContent();
					loadScreen(RegistrationConstants.DEMOGRAPHIC_PREVIEW);
				} catch (IOException ioException) {
					LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID, ioException.getMessage());
				} catch (RegBaseCheckedException regBaseCheckedException) {
					LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID, regBaseCheckedException.getMessage());
				}
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
		SessionContext.getInstance().getMapObject().put("demoGraphicPane1Content", demoGraphicPane1);
		SessionContext.getInstance().getMapObject().put("demoGraphicPane2Content", demoGraphicPane2);
	}

	private boolean validateApplicantImage() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "validating applicant biometrics");

		boolean imageCaptured = false;
		if (applicantImageCaptured) {
			if (toggleBiometricException) {
				if (exceptionImageCaptured) {
					if (getRegistrationDtoContent() != null
							&& getRegistrationDtoContent().getDemographicDTO() != null) {
						imageCaptured = true;
					} else {
						generateAlert(RegistrationConstants.ALERT_ERROR,
								RegistrationUIConstants.DEMOGRAPHIC_DETAILS_ERROR_CONTEXT);
					}
				} else {
					generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.APPLICANT_IMAGE_ERROR);
				}
			} else {
				if (getRegistrationDtoContent() != null && getRegistrationDtoContent().getDemographicDTO() != null) {
					imageCaptured = true;
				} else {
					generateAlert(RegistrationConstants.ALERT_ERROR,
							RegistrationUIConstants.DEMOGRAPHIC_DETAILS_ERROR_CONTEXT);
				}
			}
		} else {
			generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.APPLICANT_IMAGE_ERROR);
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
					// documentFields.setLayoutY(134.00);
				} else {
					isChild = false;
					childSpecificFields.setVisible(false);
					// documentFields.setLayoutY(25.00);
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
	 * Listening on the fields for any operation
	 */
	private void listenerOnFields() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Populating the local language fields");
			fxUtils.validateOnType(fullName, validation, fullNameLocalLanguage);
			fxUtils.validateOnType(addressLine1, validation, addressLine1LocalLanguage);
			fxUtils.validateOnType(addressLine2, validation, addressLine2LocalLanguage);
			fxUtils.validateOnType(addressLine3, validation, addressLine3LocalLanguage);
			fxUtils.validateOnType(mobileNo, validation);
			fxUtils.validateOnType(postalCode, validation);
			fxUtils.validateOnType(emailId, validation);
			fxUtils.validateOnType(cniOrPinNumber, validation);
			copyAddressImage.setOnMouseEntered((e) -> {
				copyAddressLabel.setVisible(true);
			});
			copyAddressImage.setOnMouseExited((e) -> {
				copyAddressLabel.setVisible(false);
			});

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - Listner method failed ", APPLICATION_NAME,
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
			ageField.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if (!validation.validateTextField(ageField, ageField.getId() + "_ontype", "N")) {
					ageField.setText(oldValue);
				}
				int age = 0;
				if (newValue.matches("\\d{1,3}")) {
					if (Integer.parseInt(ageField.getText()) > Integer
							.parseInt(AppConfig.getApplicationProperty("max_age"))) {
						ageField.setText(oldValue);
						generateAlert(RegistrationConstants.ALERT_ERROR, RegistrationUIConstants.MAX_AGE_WARNING + " "
								+ AppConfig.getApplicationProperty("max_age"));
					} else {
						age = Integer.parseInt(ageField.getText());
						LocalDate currentYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
						LocalDate dob = currentYear.minusYears(age);
						autoAgeDatePicker.setValue(dob);
						if (age < Integer.parseInt(AppConfig.getApplicationProperty("age_limit_for_child"))) {
							childSpecificFields.setVisible(true);
							isChild = true;
							documentScanController.documentScan.setLayoutY(134.00);
						} else {
							isChild = false;
							childSpecificFields.setVisible(false);
							documentScanController.documentScan.setLayoutY(25.00);
						}
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
	 * 
	 * Opens the home page screen
	 * 
	 */
	@Override
	public void goToHomePage() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to home page");

		try {
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_ISEDIT);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_PANE1_DATA);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_PANE2_DATA);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_AGE_DATA);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.REGISTRATION_DATA);
			SessionContext.getInstance().getUserContext().getUserMap()
					.remove(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);
			SessionContext.getInstance().getMapObject().remove(RegistrationConstants.DUPLICATE_FINGER);
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
	private boolean validateDemographicPane(AnchorPane paneToValidate) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the fields in demographic pane");

		boolean gotoNext = true;
		List<String> excludedIds = new ArrayList<String>();
		excludedIds.add("preRegistrationId");
		excludedIds.add("region");
		excludedIds.add("city");
		excludedIds.add("province");
		excludedIds.add("localAdminAuthority");
		excludedIds.add("virtualKeyboard");
		validation.setChild(isChild);
		validation.setValidationMessage();
		gotoNext = validation.validate(paneToValidate, excludedIds, gotoNext);
		displayValidationMessage(validation.getValidationMessage().toString());

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validated the fields");
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
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_CONSOLIDATED, "Y");
		validation.setValidationMessage();
		fullName.setText("Taleev Aalam");
		int age = 45;
		switchedOn.set(true);
		ageField.setText("" + age);
		gender.setValue("MALE");
		addressLine1.setText("Mind Tree Ltd");
		addressLine2.setText("RamanuJan It park");
		addressLine3.setText("Taramani");
		region.setText("Taramani");
		city.setText("Chennai");
		province.setText("Tamilnadu");
		postalCode.setText("60011");
		localAdminAuthority.setText("MindTree");
		mobileNo.setText("866769383");
		emailId.setText("taleev.aalam@mindtree.com");
		cniOrPinNumber.setText("012345678901234567890123456789");
		parentName.setText("Mokhtar");
		uinId.setText("93939939");
		displayValidationMessage(validation.getValidationMessage().toString());
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_CONSOLIDATED, "N");
	}

	/**
	 * Display the validation failure messages
	 */
	private void displayValidationMessage(String validationMessage) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Showing the validatoin message");
		if (validationMessage.length() > 0) {
			TextArea view = new TextArea(validationMessage);
			view.setEditable(false);
			Scene scene = new Scene(new StackPane(view), 300, 200);
			Stage primaryStage = new Stage();
			primaryStage.setTitle("Invalid input");
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.show();

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validatoin message shown successfully");
		}
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

			if (SessionContext.getInstance().getUserContext().getUserMap()
					.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION) == null) {
				toggleBiometricException = false;
				SessionContext.getInstance().getUserContext().getUserMap()
						.put(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION, toggleBiometricException);

			} else {
				toggleBiometricException = (boolean) SessionContext.getInstance().getUserContext().getUserMap()
						.get(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION);
			}

			if (toggleBiometricException) {
				bioExceptionToggleLabel1.setId("toggleLabel2");
				bioExceptionToggleLabel2.setId("toggleLabel1");
			} else {
				bioExceptionToggleLabel1.setId("toggleLabel1");
				bioExceptionToggleLabel2.setId("toggleLabel2");
			}

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
					SessionContext.getInstance().getUserContext().getUserMap()
							.put(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION, toggleBiometricException);
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

			if (toggleBiometricException) {
				authenticationController.initData(ProcessNames.EXCEPTION.getType());
			} else {
				authenticationController.initData(ProcessNames.PACKET.getType());

			}
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
	 * This method toggles the visible property of the PhotoCapture Pane.
	 * 
	 * @param visibility
	 *            the value of the visible property to be set
	 */
	public void togglePhotoCaptureVisibility(boolean visibility) {
		if (visibility) {
			if (capturePhotoUsingDevice.equals("Y")) {
				getBiometricsPane().setVisible(true);
			} else if (capturePhotoUsingDevice.equals("N")) {
				saveBiometricDetails();
				getBiometricsPane().setVisible(false);
			}
		} else {
			getBiometricsPane().setVisible(visibility);
		}
	}

	protected void createRegistrationDTOObject() {
		RegistrationDTO registrationDTO = new RegistrationDTO();

		// Set the RID
		registrationDTO.setRegistrationId(RIDGenerator.nextRID());

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

		demographicDTO.setApplicantDocumentDTO(applicantDocumentDTO);
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		Identity identity = new Identity();
		demographicInfoDTO.setIdentity(identity);
		demographicDTO.setDemographicInfoDTO(demographicInfoDTO);

		registrationDTO.setDemographicDTO(demographicDTO);

		// Create object for OSIData DTO
		registrationDTO.setOsiDataDTO(new OSIDataDTO());

		// Create object for RegistrationMetaData DTO
		RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();
		registrationMetaDataDTO.setRegistrationCategory("New");
		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);

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

}
