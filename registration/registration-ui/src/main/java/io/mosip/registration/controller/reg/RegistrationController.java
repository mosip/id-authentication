package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.MappedCodeForLanguage;
import io.mosip.registration.constants.ProcessNames;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import io.mosip.registration.controller.VirtualKeyboard;
import io.mosip.registration.controller.auth.AuthenticationController;
import io.mosip.registration.controller.device.FaceCaptureController;
import io.mosip.registration.controller.device.FingerPrintCaptureController;
import io.mosip.registration.controller.device.IrisCaptureController;
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
import io.mosip.registration.dto.demographic.CBEFFFilePropertiesDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.dto.demographic.ValuesDTO;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import io.mosip.registration.util.dataprovider.DataProvider;
import io.mosip.registration.util.kernal.RIDGenerator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.stage.Modality;
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
	private Label ageFieldLocalLanguageLabel;

	@FXML
	private Label genderLocalLanguageLabel;

	@FXML
	private Label regionLocalLanguageLabel;

	@FXML
	private Label cityLocalLanguageLabel;

	@FXML
	private Label provinceLocalLanguageLabel;

	@FXML
	private Label localAdminAuthorityLocalLanguageLabel;

	@FXML
	private Label postalCodeLocalLanguageLabel;

	@FXML
	private Label mobileNoLocalLanguageLabel;

	@FXML
	private Label emailIdLocalLanguageLabel;

	@FXML
	private Label cniOrPinNumberLocalLanguageLabel;

	@FXML
	private Label parentNameLocalLanguageLabel;

	@FXML
	private Label uinIdLocalLanguageLabel;

	@FXML
	private TextField ageField;

	@FXML
	private TextField ageFieldLocalLanguage;

	@FXML
	private Label bioExceptionToggleLabel1;

	@FXML
	private Label bioExceptionToggleLabel2;

	@FXML
	private Label toggleLabel1;

	@FXML
	private Label toggleLabel2;

	@FXML
	private Label toggleLabel1LocalLanguage;

	@FXML
	private Label toggleLabel2LocalLanguage;

	@FXML
	private AnchorPane childSpecificFields;

	@FXML
	private AnchorPane childSpecificFieldsLocal;

	@FXML
	private ScrollPane demoScrollPane;

	private SimpleBooleanProperty switchedOn;

	private SimpleBooleanProperty switchedOnForBiometricException;

	@FXML
	private ComboBox<String> gender;

	@FXML
	private ComboBox<String> genderLocalLanguage;

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
	private TextField emailIdLocalLanguage;

	@FXML
	private TextField mobileNo;

	@FXML
	private TextField mobileNoLocalLanguage;

	@FXML
	private ComboBox<String> region;

	@FXML
	private ComboBox<String> regionLocalLanguage;

	@FXML
	private ComboBox<String> city;

	@FXML
	private ComboBox<String> cityLocalLanguage;

	@FXML
	private ComboBox<String> province;

	@FXML
	private ComboBox<String> provinceLocalLanguage;

	@FXML
	private TextField postalCode;

	@FXML
	private TextField postalCodeLocalLanguage;

	@FXML
	private ComboBox<String> localAdminAuthority;

	@FXML
	private ComboBox<String> localAdminAuthorityLocalLanguage;

	@FXML
	private TextField cniOrPinNumber;

	@FXML
	private TextField cniOrPinNumberLocalLanguage;

	@FXML
	private TextField uinIdLocalLanguage;

	@FXML
	private TextField parentNameLocalLanguage;

	@FXML
	private TextField parentName;

	@FXML
	private TextField uinId;

	@FXML
	private TitledPane demoGraphicTitlePane;

	@FXML
	private TitledPane biometricTitlePane;

	@FXML
	private Label titleDemographicPaneApplicationLanguage;

	@FXML
	private Label titleDemographicPaneLocalLanguage;

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

	private boolean isChild;

	private Node keyboardNode;

	@Value("${capture_photo_using_device}")
	public String capturePhotoUsingDevice;

	@FXML
	private AnchorPane biometricsPane;
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

	@FXML
	private AnchorPane biometricException;

	private boolean toggleBiometricException;

	@FXML
	private TitledPane authenticationTitlePane;

	@FXML
	private AnchorPane dob;

	@FXML
	private AnchorPane dobLocalLanguage;

	@FXML
	private TextField dd;

	@FXML
	private TextField mm;

	@FXML
	private TextField yyyy;

	@FXML
	private TextField ddLocalLanguage;

	@FXML
	private TextField mmLocalLanguage;

	@FXML
	private TextField yyyyLocalLanguage;

	@Autowired
	private PreRegistrationDataSyncService preRegistrationDataSyncService;

	@Autowired
	private FaceCaptureController faceCaptureController;

	@Autowired
	private IdValidator<String> pridValidatorImpl;
	@Autowired
	private IdValidator<String> uinValidator;
	@Autowired
	private RidValidator<String> ridValidator;
	@Autowired
	private Validations validation;
	@Autowired
	MasterSyncService masterSync;
	@FXML
	private Text paneLabel;
	@FXML
	private AnchorPane dateAnchorPane;
	@FXML
	private AnchorPane dateAnchorPaneLocalLanguage;
	@FXML
	private AnchorPane applicationLanguageAddressAnchorPane;
	@FXML
	private AnchorPane localLanguageAddressAnchorPane;
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
	private Label cniOrPinNumberLabel;
	@FXML
	private AnchorPane applicationLanguagePane;
	@FXML
	private AnchorPane localLanguagePane;
	@Autowired
	private DateValidation dateValidation;
	@Autowired
	private FingerPrintCaptureController fingerPrintCaptureController;
	@Autowired
	private BiometricExceptionController biometricExceptionController;
	@Autowired
	private IrisCaptureController irisCaptureController;

	FXUtils fxUtils;
	List<LocationDto> locationDtoRegion;
	List<LocationDto> locationDtoProvince;
	List<LocationDto> locationDtoCity;
	List<LocationDto> locactionlocalAdminAuthority;
	private String titlePaneText;

	@FXML
	private void initialize() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");
		try {
			demoScrollPane.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight());

			auditFactory.audit(AuditEvent.GET_REGISTRATION_CONTROLLER, Components.REGISTRATION_CONTROLLER,
					"initializing the registration controller",
					SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			// Create RegistrationDTO Object
			if (getRegistrationDtoContent() == null) {
				createRegistrationDTOObject(RegistrationConstants.PACKET_TYPE_NEW);
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
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_CONSOLIDATED,
					RegistrationConstants.DISABLE);
			switchedOn = new SimpleBooleanProperty(false);
			switchedOnForBiometricException = new SimpleBooleanProperty(false);
			isChild = false;
			toggleFunction();
			toggleFunctionForBiometricException();
			ageFieldValidations();
			listenerOnFields();
			loadLocalLanguageFields();
			loadKeyboard();
			ageField.setDisable(true);
			accord.setExpandedPane(demoGraphicTitlePane);
			addRegions();

			if (isEditPage() && getRegistrationDtoContent() != null) {
				prepareEditPageContent();
			}
			uinUpdate();
			titlePaneText = demoGraphicTitlePane.getText();
			demoGraphicTitlePane.setText("");

		} catch (IOException | RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
		}
	}

	private void uinUpdate() {
		if (getRegistrationDtoContent().getSelectionListDTO() != null) {

			ObservableList<Node> nodes = demoGraphicPane1.getChildren();

			for (Node node : nodes) {
				node.setDisable(true);
			}
			keyboardNode.setDisable(false);

			applicationLanguagePane.setDisable(false);
			localLanguagePane.setDisable(false);
			paneLabel.setText(RegistrationConstants.UIN_NAV_LABEL);
			fetchBtn.setVisible(false);
			headerImage.setVisible(false);
			nextBtn.setDisable(false);
			preRegistrationLabel.setText(RegistrationConstants.UIN_LABEL);

			getRegistrationDtoContent().getRegistrationMetaDataDTO()
					.setUin(getRegistrationDtoContent().getSelectionListDTO().getUinId());
			preRegistrationId.setText(getRegistrationDtoContent().getSelectionListDTO().getUinId());

			fullName.setDisable(false);
			fullNameLocalLanguage.setDisable(false);
			fullNameLocalLanguageLabel.setDisable(false);
			fullNameLabel.setDisable(false);

			dateAnchorPane.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isAge());
			dateAnchorPaneLocalLanguage.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isAge());

			gender.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isGender());
			genderLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isGender());
			genderLocalLanguage.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isGender());
			genderLocalLanguageLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isGender());

			applicationLanguageAddressAnchorPane
					.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isAddress());
			localLanguageAddressAnchorPane.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isAddress());

			mobileNo.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
			mobileNoLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
			mobileNoLocalLanguage.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
			mobileNoLocalLanguageLabel
					.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
			emailId.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
			emailIdLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
			emailIdLocalLanguage.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());
			emailIdLocalLanguageLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isContactDetails());

			cniOrPinNumber.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isCnieNumber());
			cniOrPinNumberLabel.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isCnieNumber());
			cniOrPinNumberLocalLanguage.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isCnieNumber());
			cniOrPinNumberLocalLanguageLabel
					.setDisable(!getRegistrationDtoContent().getSelectionListDTO().isCnieNumber());

			if (!isChild)
				isChild = getRegistrationDtoContent().getSelectionListDTO().isChild()
						|| getRegistrationDtoContent().getSelectionListDTO().isParentOrGuardianDetails();

			childSpecificFields.setDisable(!isChild);
			childSpecificFieldsLocal.setDisable(!isChild);
			childSpecificFields.setVisible(isChild);
			childSpecificFieldsLocal.setVisible(isChild);

			if (SessionContext.getInstance().getMapObject().get(RegistrationConstants.IS_Child) != null) {
				isChild = (boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.IS_Child);
				childSpecificFields.setDisable(!isChild);
				childSpecificFields.setVisible(isChild);
				childSpecificFieldsLocal.setDisable(!isChild);
				childSpecificFieldsLocal.setVisible(isChild);
			}

			if (getRegistrationDtoContent().getSelectionListDTO().isBiometricException()) {
				bioExceptionToggleLabel1.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
				bioExceptionToggleLabel2.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
				toggleBiometricException = true;
				SessionContext.getInstance().getUserContext().getUserMap()
						.put(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION, toggleBiometricException);
				faceCaptureController.disableExceptionPhotoCapture(false);
			} else {
				bioExceptionToggleLabel1.setDisable(true);
				bioExceptionToggleLabel2.setDisable(true);
				bioExceptionToggleLabel1.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
				bioExceptionToggleLabel2.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
				toggleBiometricException = false;
				SessionContext.getInstance().getUserContext().getUserMap()
						.put(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION, toggleBiometricException);
				faceCaptureController.disableExceptionPhotoCapture(true);
				faceCaptureController.clearExceptionImage();
			}

		}
	}

	public void init(SelectionListDTO selectionListDTO) {
		createRegistrationDTOObject(RegistrationConstants.PACKET_TYPE_UPDATE);
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
			vk.changeControlOfKeyboard(parentNameLocalLanguage);
			vk.focusListener(fullNameLocalLanguage, 120.00, keyboardNode);
			vk.focusListener(addressLine1LocalLanguage, 270, keyboardNode);
			vk.focusListener(addressLine2LocalLanguage, 320.00, keyboardNode);
			vk.focusListener(addressLine3LocalLanguage, 375.00, keyboardNode);
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

			populateFieldValue(fullName, fullNameLocalLanguage, demo.getIdentity().getFullName());
			populateFieldValue(gender, genderLocalLanguage, demo.getIdentity().getGender());
			populateFieldValue(addressLine1, addressLine1LocalLanguage, demo.getIdentity().getAddressLine1());
			populateFieldValue(addressLine2, addressLine2LocalLanguage, demo.getIdentity().getAddressLine2());
			populateFieldValue(addressLine3, addressLine3LocalLanguage, demo.getIdentity().getAddressLine3());
			populateFieldValue(region, regionLocalLanguage, demo.getIdentity().getRegion());
			populateFieldValue(province, provinceLocalLanguage, demo.getIdentity().getProvince());
			populateFieldValue(city, cityLocalLanguage, demo.getIdentity().getCity());
			populateFieldValue(gender, genderLocalLanguage, demo.getIdentity().getGender());
			switchedOn.set((Boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.DOB_TOGGLE));
			postalCode.setText(demo.getIdentity().getPostalCode());
			mobileNo.setText(demo.getIdentity().getPhone());
			emailId.setText(demo.getIdentity().getEmail());
			ageField.setText(demo.getIdentity().getAge() + "");
			cniOrPinNumber.setText(demo.getIdentity().getCnieNumber() + "");
			postalCodeLocalLanguage.setAccessibleHelp(demo.getIdentity().getPostalCode());
			mobileNoLocalLanguage.setText(demo.getIdentity().getPhone());
			emailIdLocalLanguage.setText(demo.getIdentity().getEmail());
			cniOrPinNumberLocalLanguage.setText(demo.getIdentity().getCnieNumber() + "");
			dd.setText((String) SessionContext.getInstance().getMapObject().get("dd"));
			mm.setText((String) SessionContext.getInstance().getMapObject().get("mm"));
			yyyy.setText((String) SessionContext.getInstance().getMapObject().get("yyyy"));
			populateFieldValue(localAdminAuthority, localAdminAuthorityLocalLanguage,
					demo.getIdentity().getLocalAdministrativeAuthority());

			if (SessionContext.getInstance().getMapObject().get(RegistrationConstants.IS_Child) != null) {

				boolean isChild = (boolean) SessionContext.getInstance().getMapObject()
						.get(RegistrationConstants.IS_Child);
				childSpecificFields.setDisable(!isChild);
				childSpecificFields.setVisible(isChild);
				childSpecificFieldsLocal.setDisable(!isChild);
				childSpecificFieldsLocal.setVisible(isChild);
			}
			if (demo.getIdentity().getParentOrGuardianRIDOrUIN() != null) {
				populateFieldValue(parentName, parentNameLocalLanguage, demo.getIdentity().getParentOrGuardianName());
				uinId.setText(demo.getIdentity().getParentOrGuardianRIDOrUIN() + "");
			}
			preRegistrationId.setText(getRegistrationDtoContent().getPreRegistrationId());

			documentScanController.prepareEditPageContent();
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_ISEDIT, false);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}

	}

	private void populateFieldValue(Node nodeForPlatformLang, Node nodeForLocalLang, List<ValuesDTO> fieldValues) {
		if (fieldValues != null) {
			String platformLanguageCode = MappedCodeForLanguage
					.valueOf(AppConfig.getApplicationProperty(RegistrationConstants.APPLICATION_LANGUAGE))
					.getMappedCode();
			String localLanguageCode = MappedCodeForLanguage
					.valueOf(AppConfig.getApplicationProperty(RegistrationConstants.REGISTRATION_LOCAL_LANGUAGE))
					.getMappedCode();
			String valueInPlatformLang = "";
			String valueinLocalLang = "";

			for (ValuesDTO fieldValue : fieldValues) {
				if (fieldValue.getLanguage().equalsIgnoreCase(platformLanguageCode)) {
					valueInPlatformLang = fieldValue.getValue();
				} else if (nodeForLocalLang != null && fieldValue.getLanguage().equalsIgnoreCase(localLanguageCode)) {
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
	}

	@FXML
	private void fetchPreRegistration() {
		String preRegId = preRegistrationId.getText();

		if (StringUtils.isEmpty(preRegId)) {
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PRE_REG_ID_EMPTY);
			return;
		} else {
			try {
				pridValidatorImpl.validateId(preRegId);
			} catch (InvalidIDException invalidIDException) {
				generateAlert(RegistrationConstants.ERROR, invalidIDException.getErrorText());
				return;
			}
		}
		ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistration(preRegId);

		SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
		List<ErrorResponseDTO> errorResponseDTOList = responseDTO.getErrorResponseDTOs();

		if (successResponseDTO != null && successResponseDTO.getOtherAttributes() != null
				&& successResponseDTO.getOtherAttributes().containsKey(RegistrationConstants.REGISTRATION_DTO)) {
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_DATA,
					successResponseDTO.getOtherAttributes().get(RegistrationConstants.REGISTRATION_DTO));
			prepareEditPageContent();

		} else if (errorResponseDTOList != null && !errorResponseDTOList.isEmpty()) {
			generateAlert(RegistrationConstants.ERROR, errorResponseDTOList.get(0).getMessage());
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
				generateAlert(RegistrationConstants.ERROR, "Address could not be loaded as there is no previous entry");
				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID,
						"Address could not be loaded as there is no previous entry");

			} else {
				LocationDTO locationDto = ((AddressDTO) SessionContext.getInstance().getMapObject()
						.get(RegistrationConstants.ADDRESS_KEY)).getLocationDTO();
				region.setValue(locationDto.getRegion());
				city.setValue(locationDto.getCity());
				province.setValue(locationDto.getProvince());
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
				anchorPaneRegistration.setPrefHeight(700.00);
				demoGraphicTitlePane.setExpanded(true);
				LocalDate currentYear = LocalDate.of(Integer.parseInt(yyyy.getText()), Integer.parseInt(mm.getText()),
						Integer.parseInt(dd.getText()));
				dateOfBirth = Date.from(currentYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
				SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_AGE_DATA,
						dateOfBirth);
				SessionContext.getInstance().getMapObject().put("dd", dd.getText());
				SessionContext.getInstance().getMapObject().put("mm", mm.getText());
				SessionContext.getInstance().getMapObject().put("yyyy", yyyy.getText());
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
			keyboardNode.setLayoutX(400.00);
			Node node = (Node) event.getSource();

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE1)) {
				addressLine1LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(310.00);
			}

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE2)) {
				addressLine2LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(360.00);
			}

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE3)) {
				addressLine3LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(415.00);
			}

			if (node.getId().equals(RegistrationConstants.FULL_NAME)) {
				fullNameLocalLanguage.requestFocus();
				keyboardNode.setLayoutY(165.00);
			}

			if (node.getId().equals(RegistrationConstants.PARENT_NAME)) {
				parentNameLocalLanguage.requestFocus();
				keyboardNode.setLayoutY(705.00);
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
	@FXML
	private void saveDetail() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Saving the fields to DTO");
		try {
			auditFactory.audit(AuditEvent.SAVE_DETAIL_TO_DTO, Components.REGISTRATION_CONTROLLER,
					"Saving the details to respected DTO", SessionContext.getInstance().getUserContext().getUserId(),
					RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			RegistrationDTO registrationDTO = getRegistrationDtoContent();
			DemographicInfoDTO demographicInfoDTO;

			OSIDataDTO osiDataDTO = registrationDTO.getOsiDataDTO();
			RegistrationMetaDataDTO registrationMetaDataDTO = registrationDTO.getRegistrationMetaDataDTO();
			if (validateDemographicPane(demoGraphicPane2)) {
				SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_Child, isChild);
				demographicInfoDTO = buildDemographicInfo();

				if (isChild) {

					osiDataDTO.setIntroducerType(IntroducerType.PARENT.getCode());

					registrationMetaDataDTO.setApplicationType(RegistrationConstants.CHILD);
				} else {
					registrationMetaDataDTO.setApplicationType(RegistrationConstants.ADULT);
				}

				osiDataDTO.setOperatorID(SessionContext.getInstance().getUserContext().getUserId());

				registrationDTO.setPreRegistrationId(preRegistrationId.getText());
				registrationDTO.getDemographicDTO().setDemographicInfoDTO(demographicInfoDTO);

				LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Saved the demographic fields to DTO");

				toggleIrisCaptureVisibility(false);
				togglePhotoCaptureVisibility(false);

				if (toggleBiometricException) {
					biometricException.setVisible(true);
					toggleFingerprintCaptureVisibility(false);
				} else {
					biometricException.setVisible(false);
					toggleFingerprintCaptureVisibility(true);
				}
				biometricTitlePane.setExpanded(true);

				if (registrationDTO.getSelectionListDTO() != null) {

					if (registrationDTO.getSelectionListDTO().isBiometricException()) {
						toggleBiometricExceptionVisibility(true);
						toggleFingerprintCaptureVisibility(false);
						toggleIrisCaptureVisibility(false);
						togglePhotoCaptureVisibility(false);
					} else if (registrationDTO.getSelectionListDTO().isBiometricFingerprint()
							&& !registrationDTO.getSelectionListDTO().isBiometricException()) {
						toggleFingerprintCaptureVisibility(true);
						toggleIrisCaptureVisibility(false);
						togglePhotoCaptureVisibility(false);
					} else if (registrationDTO.getSelectionListDTO().isBiometricIris()
							&& !registrationDTO.getSelectionListDTO().isBiometricException()) {
						toggleFingerprintCaptureVisibility(false);
						toggleIrisCaptureVisibility(true);
						togglePhotoCaptureVisibility(false);
					} else {
						toggleFingerprintCaptureVisibility(false);
						toggleIrisCaptureVisibility(false);
						togglePhotoCaptureVisibility(true);
					}

				}
				SessionContext.getInstance().getMapObject().put("toggleAgeOrDob", switchedOn.get());
			}
		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
			LOGGER.error("REGISTRATION - SAVING THE DETAILS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	Date dateOfBirth;

	@SuppressWarnings("unchecked")
	private DemographicInfoDTO buildDemographicInfo() {

		String platformLanguageCode = MappedCodeForLanguage
				.valueOf(AppConfig.getApplicationProperty(RegistrationConstants.APPLICATION_LANGUAGE)).getMappedCode()
				.toLowerCase();
		String localLanguageCode = MappedCodeForLanguage
				.valueOf(AppConfig.getApplicationProperty(RegistrationConstants.REGISTRATION_LOCAL_LANGUAGE))
				.getMappedCode().toLowerCase();
		Identity demographicIdentity = getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO()
				.getIdentity();

		return Builder.build(DemographicInfoDTO.class)
				.with(demographicInfo -> demographicInfo.setIdentity((Identity) Builder.build(Identity.class)
						.with(identity -> identity.setFullName(fullName.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(fullName.getText())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(fullNameLocalLanguage.getText())).get()))
										.get()))
						.with(identity -> identity.setDateOfBirth(dateAnchorPane.isDisabled() ? null :
								(dateOfBirth != null ? DateUtils.formatDate(dateOfBirth, "yyyy/MM/dd") : "")))
						.with(identity -> identity
								.setAge(ageField.isDisabled() ? null : Integer.parseInt(ageField.getText())))
						.with(identity -> identity.setGender(gender.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(gender.getValue())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(genderLocalLanguage.getValue())).get()))
										.get()))
						.with(identity -> identity.setAddressLine1(addressLine1.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(addressLine1.getText())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(addressLine1LocalLanguage.getText()))
												.get()))
										.get()))
						.with(identity -> identity.setAddressLine2(addressLine2.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(addressLine2.getText())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(addressLine2LocalLanguage.getText()))
												.get()))
										.get()))
						.with(identity -> identity.setAddressLine3(addressLine3.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(addressLine3.getText())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(addressLine3LocalLanguage.getText()))
												.get()))
										.get()))
						.with(identity -> identity.setRegion(region.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(region.getValue())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(regionLocalLanguage.getValue())).get()))
										.get()))
						.with(identity -> identity.setProvince(province.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(province.getValue())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(provinceLocalLanguage.getValue())).get()))
										.get()))
						.with(identity -> identity.setCity(city.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(city.getValue())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(cityLocalLanguage.getValue())).get()))
										.get()))
						.with(identity -> identity.setPostalCode(postalCode.isDisabled() ? null : postalCode.getText()))
						.with(identity -> identity.setPhone(mobileNo.isDisabled() ? null : mobileNo.getText()))
						.with(identity -> identity.setEmail(emailId.isDisabled() ? null : emailId.getText()))
						.with(identity -> identity.setCnieNumber(
								cniOrPinNumber.isDisabled() ? null : new BigInteger(cniOrPinNumber.getText())))
						.with(identity -> identity.setLocalAdministrativeAuthority(localAdminAuthority.isDisabled()
								? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(localAdminAuthority.getValue())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value
														.setValue(localAdminAuthorityLocalLanguage.getValue()))
												.get()))
										.get()))
						.with(identity -> identity.setParentOrGuardianRIDOrUIN(
								uinId.isDisabled() ? null : new BigInteger(uinId.getText())))
						.with(identity -> identity.setParentOrGuardianName(parentName.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(parentName.getText())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(parentNameLocalLanguage.getText()))
												.get()))
										.get()))
						.with(identity -> identity.setProofOfIdentity(demographicIdentity.getProofOfIdentity()))
						.with(identity -> identity.setProofOfAddress(demographicIdentity.getProofOfAddress()))
						.with(identity -> identity.setProofOfRelationship(demographicIdentity.getProofOfRelationship()))
						.with(identity -> identity.setProofOfDateOfBirth(demographicIdentity.getProofOfDateOfBirth()))
						.with(identity -> identity.setIdSchemaVersion(1.0))
						.with(identity -> identity
								.setUin(getRegistrationDtoContent().getRegistrationMetaDataDTO().getUin() == null ? null
										: new BigInteger(
												getRegistrationDtoContent().getRegistrationMetaDataDTO().getUin())))
						.with(identity -> identity.setIndividualBiometrics(Builder.build(CBEFFFilePropertiesDTO.class)
								.with(cbeff -> cbeff.setFormat("cbeff")).with(cbeff -> cbeff.setVersion(1.0))
								.with(cbeff -> cbeff.setValue(
										RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME.replace(".xml", "")))
								.get()))
						.get()))
				.get();
	}

	@FXML
	public void goToPreviousPane() {
		try {
			if (getRegistrationDtoContent().getSelectionListDTO() != null) {

				if (getRegistrationDtoContent().getSelectionListDTO().isBiometricIris()
						&& getRegistrationDtoContent().getSelectionListDTO().isBiometricFingerprint()
						|| getRegistrationDtoContent().getSelectionListDTO().isBiometricIris()) {
					toggleIrisCaptureVisibility(true);
					togglePhotoCaptureVisibility(false);
				} else if (getRegistrationDtoContent().getSelectionListDTO().isBiometricFingerprint()) {
					togglePhotoCaptureVisibility(false);
					toggleFingerprintCaptureVisibility(true);
				} else if (!getRegistrationDtoContent().getSelectionListDTO().isBiometricFingerprint()
						&& !getRegistrationDtoContent().getSelectionListDTO().isBiometricIris()) {
					demoGraphicTitlePane.setExpanded(true);
				}
			} else {
				toggleIrisCaptureVisibility(true);
				togglePhotoCaptureVisibility(false);
			}

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - COULD NOT GO TO DEMOGRAPHIC TITLE PANE ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	/**
	 * To detect the face part from the applicant photograph to use it for QR Code
	 * generation
	 * 
	 * @param applicantImage
	 *            the image that is captured as applicant photograph
	 * @return BufferedImage the face that is detected from the applicant photograph
	 */
	private BufferedImage detectApplicantFace(BufferedImage applicantImage) {
		BufferedImage detectedFace = null;
		HaarCascadeDetector detector = new HaarCascadeDetector();
		List<DetectedFace> faces = null;
		faces = detector.detectFaces(ImageUtilities.createFImage(applicantImage));
		if (!faces.isEmpty()) {
			Iterator<DetectedFace> dfi = faces.iterator();
			while (dfi.hasNext()) {
				DetectedFace face = dfi.next();
				FImage image1 = face.getFacePatch();
				detectedFace = ImageUtilities.createBufferedImage(image1);
			}
		}
		return detectedFace;
	}

	/**
	 * To compress the detected face from the image of applicant and store it in DTO
	 * to use it for QR Code generation
	 * 
	 * @param applicantImage
	 *            the image that is captured as applicant photograph
	 */
	private void compressImageForQRCode(BufferedImage detectedFace) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Iterator<ImageWriter> writers = ImageIO
					.getImageWritersByFormatName(RegistrationConstants.WEB_CAMERA_IMAGE_TYPE);
			ImageWriter writer = writers.next();

			ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
			writer.setOutput(imageOutputStream);

			ImageWriteParam param = writer.getDefaultWriteParam();

			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0); // Change the quality value you
											// prefer
			writer.write(null, new IIOImage(detectedFace, null, null), param);

			byte[] compressedPhoto = byteArrayOutputStream.toByteArray();
			ApplicantDocumentDTO applicantDocumentDTO = getRegistrationDtoContent().getDemographicDTO()
					.getApplicantDocumentDTO();
			applicantDocumentDTO.setCompressedFacePhoto(compressedPhoto);
			byteArrayOutputStream.close();
			imageOutputStream.close();
			writer.dispose();
		} catch (IOException ioException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, ioException.getMessage());
		}
	}

	private void saveBiometricDetails() {
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

	public void saveBiometricDetails(BufferedImage applicantBufferedImage, BufferedImage exceptionBufferedImage) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "saving the details of applicant biometrics");
		boolean isValid = true;
		isValid = validateDemographicPane(demoGraphicPane1);
		if (isValid) {
			isValid = validateDemographicPane(demoGraphicPane2);
		}
		if (!isValid) {
			demoGraphicTitlePane.setExpanded(true);
			toggleIrisCaptureVisibility(true);
		}
		if (isValid) {
			try {
				BufferedImage detectedFace = detectApplicantFace(applicantBufferedImage);
				if (detectedFace != null) {
					compressImageForQRCode(detectedFace);
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
				} else {
					generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.FACE_CAPTURE_ERROR);
				}
			} catch (IOException ioException) {
				LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, ioException.getMessage());
			}
		}
	}

	private void setPreviewContent() {
		faceCaptureController.setPreviewContent();
		nextBtn.setVisible(false);
		pane2NextBtn.setVisible(false);
		pane2PrevBtn.setVisible(false);
		autoFillBtn.setVisible(false);
		fetchBtn.setVisible(false);
		documentScanController.setPreviewContent();
		SessionContext.getInstance().getMapObject().put("demoGraphicPane1Content", demoGraphicPane1);
		SessionContext.getInstance().getMapObject().put("demoGraphicPane2Content", demoGraphicPane2);
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
			fxUtils.validateOnType(mobileNo, validation, mobileNoLocalLanguage);
			fxUtils.validateOnType(postalCode, validation, postalCodeLocalLanguage);
			fxUtils.validateOnType(emailId, validation, emailIdLocalLanguage);
			fxUtils.validateOnType(cniOrPinNumber, validation, cniOrPinNumberLocalLanguage);
			fxUtils.validateOnType(parentName, validation, parentNameLocalLanguage);
			fxUtils.validateOnType(uinId, validation, uinIdLocalLanguage);
			fxUtils.validateOnType(fullNameLocalLanguage, validation);
			fxUtils.populateLocalComboBox(gender, genderLocalLanguage);
			fxUtils.populateLocalComboBox(city, cityLocalLanguage);
			fxUtils.populateLocalComboBox(region, regionLocalLanguage);
			fxUtils.populateLocalComboBox(province, provinceLocalLanguage);
			fxUtils.populateLocalComboBox(localAdminAuthority, localAdminAuthorityLocalLanguage);
			dateValidation.validateDate(dd, mm, yyyy, validation, fxUtils, ddLocalLanguage);
			dateValidation.validateDate(ddLocalLanguage, mmLocalLanguage, yyyyLocalLanguage, validation, fxUtils, null);
			dateValidation.validateMonth(dd, mm, yyyy, validation, fxUtils, mmLocalLanguage);
			dateValidation.validateMonth(ddLocalLanguage, mmLocalLanguage, yyyyLocalLanguage, validation, fxUtils,
					null);
			dateValidation.validateYear(dd, mm, yyyy, validation, fxUtils, yyyyLocalLanguage);
			dateValidation.validateYear(ddLocalLanguage, mmLocalLanguage, yyyyLocalLanguage, validation, fxUtils, null);
			fxUtils.dobListener(yyyy, ageField, "\\d{4}");
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
				ageFieldLocalLanguage.setText(newValue);
				if (!validation.validateTextField(ageField, ageField.getId() + "_ontype",
						RegistrationConstants.DISABLE)) {
					ageField.setText(oldValue);
				}
				int age = 0;
				if (newValue.matches("\\d{1,3}")) {
					if (Integer.parseInt(ageField.getText()) > Integer
							.parseInt(AppConfig.getApplicationProperty("max_age"))) {
						ageField.setText(oldValue);
						generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.MAX_AGE_WARNING + " "
								+ AppConfig.getApplicationProperty("max_age"));
					} else {
						age = Integer.parseInt(ageField.getText());
						LocalDate currentYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
						dateOfBirth = Date
								.from(currentYear.minusYears(age).atStartOfDay(ZoneId.systemDefault()).toInstant());
						if (age < Integer.parseInt(AppConfig.getApplicationProperty("age_limit_for_child"))) {
							childSpecificFields.setVisible(true);
							childSpecificFieldsLocal.setVisible(true);
							childSpecificFields.setDisable(false);
							childSpecificFieldsLocal.setDisable(false);
							parentName.clear();
							uinId.clear();
							isChild = true;
						} else {
							isChild = false;
							childSpecificFields.setVisible(false);
							childSpecificFieldsLocal.setVisible(false);
							childSpecificFields.setDisable(true);
							childSpecificFieldsLocal.setDisable(true);
							parentName.clear();
							uinId.clear();
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

			toggleLabel1.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
			toggleLabel2.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
			toggleLabel1LocalLanguage.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
			toggleLabel2LocalLanguage.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
			switchedOn.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						toggleLabel1.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
						toggleLabel2.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						toggleLabel1LocalLanguage.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
						toggleLabel2LocalLanguage.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						ageField.clear();
						childSpecificFields.setVisible(false);
						childSpecificFieldsLocal.setVisible(false);
						ageField.setDisable(false);
						ageFieldLocalLanguage.setDisable(false);
						ageFieldLocalLanguage.clear();
						dob.setDisable(true);
						dobLocalLanguage.setDisable(true);

					} else {
						toggleLabel1.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						toggleLabel2.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
						ageField.clear();
						ageFieldLocalLanguage.clear();
						childSpecificFields.setVisible(false);
						childSpecificFieldsLocal.setVisible(false);
						ageField.setDisable(true);
						ageFieldLocalLanguage.setDisable(true);
						dob.setDisable(false);
						dobLocalLanguage.setDisable(false);

					}
					dd.clear();
					mm.clear();
					yyyy.clear();

					ddLocalLanguage.clear();
					mmLocalLanguage.clear();
					yyyyLocalLanguage.clear();
				}
			});

			toggleLabel1.setOnMouseClicked((event) -> {
				switchedOn.set(!switchedOn.get());
			});
			toggleLabel2.setOnMouseClicked((event) -> {
				switchedOn.set(!switchedOn.get());
			});

			toggleLabel1LocalLanguage.setOnMouseClicked((event) -> {
				switchedOn.set(!switchedOn.get());
			});
			toggleLabel2LocalLanguage.setOnMouseClicked((event) -> {
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
	 * Validates the fields of demographic pane1
	 * 
	 */
	private boolean validateDemographicPane(AnchorPane paneToValidate) {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the fields in demographic pane");

		boolean gotoNext = true;
		List<String> excludedIds = new ArrayList<String>();
		excludedIds.add("preRegistrationId");
		excludedIds.add("virtualKeyboard");

		validation.setChild(isChild);
		validation.setValidationMessage();
		gotoNext = validation.validate(paneToValidate, excludedIds, gotoNext, masterSync);
		/*
		 * if(gotoNext) gotoNext = validation.validateUinOrRid(uinId, isChild,
		 * uinValidator, ridValidator);
		 */ displayValidationMessage(validation.getValidationMessage().toString());

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
			fullNameLocalLanguageLabel.setText(localProperties.getString("fullName"));
			addressLine1LocalLanguagelabel.setText(localProperties.getString("addressLine1"));
			addressLine2LocalLanguagelabel.setText(localProperties.getString("addressLine2"));
			addressLine3LocalLanguagelabel.setText(localProperties.getString("addressLine3"));
			addressLine3LocalLanguagelabel.setText(localProperties.getString("addressLine3"));
			ageFieldLocalLanguageLabel.setText(localProperties.getString("ageField"));
			genderLocalLanguageLabel.setText(localProperties.getString("gender"));
			regionLocalLanguageLabel.setText(localProperties.getString("region"));
			cityLocalLanguageLabel.setText(localProperties.getString("city"));
			provinceLocalLanguageLabel.setText(localProperties.getString("province"));
			localAdminAuthorityLocalLanguageLabel.setText(localProperties.getString("localAdminAuthority"));
			cniOrPinNumberLocalLanguageLabel.setText(localProperties.getString("cniOrPinNumber"));
			postalCodeLocalLanguageLabel.setText(localProperties.getString("postalCode"));
			mobileNoLocalLanguageLabel.setText(localProperties.getString("mobileNo"));
			emailIdLocalLanguageLabel.setText(localProperties.getString("emailId"));
			parentNameLocalLanguageLabel.setText(localProperties.getString("parentName"));
			uinIdLocalLanguageLabel.setText(localProperties.getString("uinId"));
			genderLocalLanguage.setPromptText(localProperties.getString("select"));
			localAdminAuthorityLocalLanguage.setPromptText(localProperties.getString("select"));
			cityLocalLanguage.setPromptText(localProperties.getString("select"));
			regionLocalLanguage.setPromptText(localProperties.getString("select"));
			provinceLocalLanguage.setPromptText(localProperties.getString("select"));
			titleDemographicPaneLocalLanguage.setText(localProperties.getString("titleDemographicPane"));
			demoGraphicTitlePane.expandedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						demoGraphicTitlePane.setText("");
					}
					if (oldValue) {
						demoGraphicTitlePane.setText(titlePaneText);
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

	private Boolean isEditPage() {
		if (SessionContext.getInstance().getMapObject().get(RegistrationConstants.REGISTRATION_ISEDIT) != null)
			return (Boolean) SessionContext.getInstance().getMapObject().get(RegistrationConstants.REGISTRATION_ISEDIT);
		return false;
	}

	public void clickMe() {
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_CONSOLIDATED,
				RegistrationConstants.ENABLE);
		validation.setValidationMessage();
		fullName.setText("Taleev Aalam");
		int age = 45;
		switchedOn.set(true);
		ageField.setText("" + age);
		gender.setValue("MALE");
		addressLine1.setText("Mind Tree Ltd");
		addressLine2.setText("RamanuJan It park");
		addressLine3.setText("Taramani");
		region.setValue("Taramani");
		city.setValue("Chennai");
		province.setValue("Tamilnadu");
		postalCode.setText("600111");
		localAdminAuthority.setValue("MindTree");
		mobileNo.setText("9965625706");
		emailId.setText("taleevaalam@mindtree.com");
		cniOrPinNumber.setText("012345678901234567890123456789");
		parentName.setText("Mokhtar");
		uinId.setText("93939939");
		displayValidationMessage(validation.getValidationMessage().toString());
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_CONSOLIDATED,
				RegistrationConstants.DISABLE);
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
			primaryStage.initModality(Modality.WINDOW_MODAL);
			primaryStage.initOwner(fXComponents.getStage());
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
		anchorPaneRegistration.setPrefHeight(1100.00);
		demoGraphicTitlePane.setExpanded(true);
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
				bioExceptionToggleLabel1.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
				bioExceptionToggleLabel2.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
			} else {
				bioExceptionToggleLabel1.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
				bioExceptionToggleLabel2.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
			}

			switchedOnForBiometricException.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					clearAllValues();
					if (newValue) {
						bioExceptionToggleLabel1.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
						bioExceptionToggleLabel2.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						toggleBiometricException = true;
						faceCaptureController.disableExceptionPhotoCapture(false);
					} else {
						bioExceptionToggleLabel1.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						bioExceptionToggleLabel2.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
						toggleBiometricException = false;
						faceCaptureController.disableExceptionPhotoCapture(true);
						faceCaptureController.clearExceptionImage();
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
			authenticationController.initData(ProcessNames.PACKET.getType());
			/*
			 * if (toggleBiometricException) {
			 * authenticationController.initData(ProcessNames.EXCEPTION.getType( )); }
			 */
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
			if (capturePhotoUsingDevice.equals(RegistrationConstants.ENABLE)) {
				getBiometricsPane().setVisible(true);
			} else if (capturePhotoUsingDevice.equals(RegistrationConstants.DISABLE)) {
				saveBiometricDetails();
				getBiometricsPane().setVisible(false);
			}
		} else {
			getBiometricsPane().setVisible(visibility);
		}
	}

	protected void createRegistrationDTOObject(String registrationCategory) {
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
		registrationMetaDataDTO.setRegistrationCategory(registrationCategory);
		registrationMetaDataDTO.setGeoLatitudeLoc(Double.parseDouble(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterLatitude()));
		registrationMetaDataDTO.setGeoLongitudeLoc(Double.parseDouble(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterLongitude()));
		registrationMetaDataDTO.setCenterId(String.valueOf(SessionContext.getInstance().getUserContext()
				.getRegistrationCenterDetailDTO().getRegistrationCenterId()));
		registrationDTO.setRegistrationMetaDataDTO(registrationMetaDataDTO);

		// Put the RegistrationDTO object to SessionContext Map
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.REGISTRATION_DATA, registrationDTO);
	}

	private BiometricInfoDTO createBiometricInfoDTO() {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setBiometricExceptionDTO(new ArrayList<>());
		biometricInfoDTO.setFingerprintDetailsDTO(new ArrayList<>());
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

	/**
	 * This method toggles the visible property of the BiometricException Pane.
	 * 
	 * @param visibility
	 *            the value of the visible property to be set
	 */
	public void toggleBiometricExceptionVisibility(boolean visibility) {
		this.biometricException.setVisible(visibility);
	}

	/**
	 * To load the regions in the selection list based on the language code
	 */
	private void addRegions() {
		try {
			locationDtoRegion = masterSync.findLocationByHierarchyCode(applicationContext.getApplicationLanguageBundle().getString(region.getId()),
					MappedCodeForLanguage
							.valueOf(AppConfig.getApplicationProperty(RegistrationConstants.APPLICATION_LANGUAGE))
							.getMappedCode());
			region.getItems().addAll(
					locationDtoRegion.stream().map(location -> location.getName()).collect(Collectors.toList()));
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING FAILED FOR REGION SELECTION LIST ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());

		}

	}

	/**
	 * To load the provinces in the selection list based on the language code
	 */
	@FXML
	private void addProvince() {
		try {
			List<LocationDto> listOfCodes = locationDtoRegion.stream()
					.filter(location -> location.getName().equals(region.getValue())).collect(Collectors.toList());
			String code = "";
			String langCode="";
			if (!listOfCodes.isEmpty()) {
				code = listOfCodes.get(0).getCode();
				langCode=listOfCodes.get(0).getLangCode();
				locationDtoProvince = masterSync.findProvianceByHierarchyCode(code,langCode);
				province.getItems().clear();
				province.getItems().addAll(
						locationDtoProvince.stream().map(location -> location.getName()).collect(Collectors.toList()));
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING FAILED FOR PROVINCE SELECTION LIST ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());

		}

	}

	/**
	 * To load the cities in the selection list based on the language code
	 */
	@FXML
	private void addCity() {
		try {
			List<LocationDto> listOfCodes = locationDtoProvince.stream()
					.filter(location -> location.getName().equals(province.getValue())).collect(Collectors.toList());
			String code = "";
			String langCode = "";
			if (!listOfCodes.isEmpty()) {
				code = listOfCodes.get(0).getCode();
				langCode=listOfCodes.get(0).getLangCode();
				locationDtoCity = masterSync.findProvianceByHierarchyCode(code,langCode);
				city.getItems().clear();
				city.getItems().addAll(
						locationDtoCity.stream().map(location -> location.getName()).collect(Collectors.toList()));
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING FAILED FOR CITY SELECTION LIST ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());

		}
	}

	/**
	 * To load the localAdminAuthorities selection list based on the language code
	 */
	@FXML
	private void addlocalAdminAuthority() {
		try {
			List<LocationDto> listOfCodes = locationDtoCity.stream()
					.filter(location -> location.getName().equals(city.getValue())).collect(Collectors.toList());
			String code = "";
			String langCode = "";
			if (!listOfCodes.isEmpty()) {
				code = listOfCodes.get(0).getCode();
				langCode=listOfCodes.get(0).getLangCode();
				List<LocationDto> locationlocalAdminAuthority = masterSync.findProvianceByHierarchyCode(code,langCode);
				localAdminAuthority.getItems().clear();
				localAdminAuthority.getItems().addAll(
						locationlocalAdminAuthority.stream().map(loc -> loc.getName()).collect(Collectors.toList()));
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING FAILED FOR LOCAL ADMIN AUTHORITY SELECTOIN LIST ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());

		}

	}

	private void clearAllValues() {
		((RegistrationDTO) SessionContext.getInstance().getMapObject().get(RegistrationConstants.REGISTRATION_DATA))
				.getBiometricDTO().setApplicantBiometricDTO(createBiometricInfoDTO());
		biometricExceptionController.setExceptionImage();
		fingerPrintCaptureController.clearFingerPrintDTO();
		irisCaptureController.clearIrisData();
	}

}
