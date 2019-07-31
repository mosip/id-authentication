package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.applicanttype.exception.InvalidApplicantArgumentException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.PridValidator;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.transliteration.spi.Transliteration;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import io.mosip.registration.controller.VirtualKeyboard;
import io.mosip.registration.controller.device.FaceCaptureController;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.IndividualTypeDto;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.CBEFFFilePropertiesDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.dto.demographic.ValuesDTO;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * {@code DemographicDetailController} is to capture the demographic details
 * 
 * @author Taleev.Aalam
 * @since 1.0.0
 *
 */

@Controller
public class DemographicDetailController extends BaseController {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(DemographicDetailController.class);

	@FXML
	public TextField preRegistrationId;

	@FXML
	private TextField fullName;

	@FXML
	private TextField fullNameLocalLanguage;

	@FXML
	private Label fullNameLocalLanguageLabel;

	@FXML
	private Label fullNameLocalLanguageMessage;

	@FXML
	private Label ageFieldLocalLanguageLabel;

	@FXML
	private Label genderLocalLanguageLabel;

	@FXML
	private Label regionLocalLanguageMessage;

	@FXML
	private Label regionLocalLanguageLabel;

	@FXML
	private Label cityLocalLanguageLabel;

	@FXML
	private Label cityLocalLanguageMessage;

	@FXML
	private Label provinceLocalLanguageLabel;

	@FXML
	private Label provinceLocalLanguageMessage;

	@FXML
	private Label localAdminAuthorityLocalLanguageLabel;

	@FXML
	private Label localAdminAuthorityLocalLanguageMessage;

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
	private Label parentNameLocalLanguageMessage;

	@FXML
	private Label languageLabelLocalLanguage;

	@FXML
	private TextField ageField;

	@FXML
	private TextField ageFieldLocalLanguage;
	@FXML
	private Label uinRidToggleLabel1;
	@FXML
	private Label uinRidToggleLabel2;
	@FXML
	private Label uinRidToggleLabel1LocalLanguage;
	@FXML
	private Label uinRidToggleLabel2LocalLanguage;

	@FXML
	private Label mmLabel;
	@FXML
	private Label ddLabel;
	@FXML
	private Label yyyyLabel;
	@FXML
	private GridPane parentDetailPane;
	@FXML
	private ScrollPane parentScrollPane;
	private SimpleBooleanProperty switchedOnParentUinOrRid;

	@FXML
	private TextField addressLine1;

	@FXML
	private Label addressLine1Label;

	@FXML
	private Label addressLine1Message;

	@FXML
	private TextField addressLine1LocalLanguage;

	@FXML
	private Label addressLine1LocalLanguageLabel;

	@FXML
	private Label addressLine1LocalLanguageMessage;

	@FXML
	private TextField addressLine2;

	@FXML
	private Label addressLine2Label;

	@FXML
	private Label addressLine2Message;

	@FXML
	private TextField addressLine2LocalLanguage;

	@FXML
	private Label addressLine2LocalLanguageLabel;

	@FXML
	private Label addressLine2LocalLanguageMessage;

	@FXML
	private TextField addressLine3;

	@FXML
	private Label addressLine3Label;

	@FXML
	private Label addressLine3Message;

	@FXML
	private Label parentRegIdLabel;

	@FXML
	private TextField addressLine3LocalLanguage;

	@FXML
	private Label addressLine3LocalLanguageLabel;

	@FXML
	private Label addressLine3LocalLanguageMessage;
	
	@FXML
	private Label postalCodeMessage;
	
	@FXML
	private Label postalCodeLocalLanguageMessage;
	
	@FXML
	private Label mobileNoMessage;
	
	@FXML
	private Label mobileNoLocalLanguageMessage;
	
	@FXML
	private Label emailIdMessage;
	
	@FXML
	private Label emailIdLocalLanguageMessage;
	
	@FXML
	private Label cniOrPinNumberMessage;
	
	@FXML
	private Label cniOrPinNumberLocalLanguageMessage;
	
	@FXML
	private TextField emailId;

	@FXML
	private VBox emailIdPane;

	@FXML
	private TextField emailIdLocalLanguage;

	@FXML
	private TextField mobileNo;

	@FXML
	private VBox applicationMobileNumber;

	@FXML
	private VBox applicationAddressLine1;

	@FXML
	private VBox localAddressLine1;

	@FXML
	private VBox localAddressLine2;

	@FXML
	private VBox localAddressLine3;

	@FXML
	private VBox applicationAddressLine2;

	@FXML
	private VBox applicationAddressLine3;

	@FXML
	private VBox applicationRegion;

	@FXML
	private VBox applicationProvince;

	@FXML
	private VBox applicationCity;

	@FXML
	private VBox applicationlocalAdminAuthority;

	@FXML
	private VBox applicationPostalCode;

	@FXML
	private TextField mobileNoLocalLanguage;

	@FXML
	private ComboBox<LocationDto> region;

	@FXML
	private Label regionMessage;

	@FXML
	private Label regionLabel;

	@FXML
	private ComboBox<LocationDto> regionLocalLanguage;

	@FXML
	private VBox regionLocalLanguagePane;

	@FXML
	private ComboBox<LocationDto> city;

	@FXML
	private Label cityMessage;

	@FXML
	private Label cityLabel;

	@FXML
	private ComboBox<LocationDto> cityLocalLanguage;

	@FXML
	private VBox cityLocalLanguagePane;

	@FXML
	private ComboBox<LocationDto> province;

	@FXML
	private Label provinceLabel;

	@FXML
	private Label provinceMessage;

	@FXML
	private ComboBox<LocationDto> provinceLocalLanguage;

	@FXML
	private VBox provinceLocalLanguagePane;

	@FXML
	private TextField postalCode;

	@FXML
	private TextField postalCodeLocalLanguage;

	@FXML
	private VBox postalCodeLocalLanguagePane;

	@FXML
	private VBox localMobileNumberPane;

	@FXML
	private ComboBox<LocationDto> localAdminAuthority;

	@FXML
	private Label localAdminAuthorityMessage;

	@FXML
	private Label localAdminAuthorityLabel;

	@FXML
	private ComboBox<LocationDto> localAdminAuthorityLocalLanguage;

	@FXML
	private VBox localAdminAuthorityLocalLanguagePane;

	@FXML
	private VBox localEmailIdPane;

	@FXML
	private VBox localCniOrPinPane;

	@FXML
	private TextField cniOrPinNumber;

	@FXML
	private VBox cniOrPinNumberPane;

	@FXML
	private TextField cniOrPinNumberLocalLanguage;

	@FXML
	private TextField parentNameLocalLanguage;

	@FXML
	private TextField parentName;

	@FXML
	private Label parentNameMessage;

	@FXML
	private Label parentNameLabel;

	@FXML
	private Label ageFieldLabel;

	@FXML
	private Label ageFieldLocalLanguageMessage;

	@FXML
	private TextField parentRegId;

	@FXML
	private Label parentRegIdMessage;

	@FXML
	private Label parentRegIdLocalLanguageMessage;

	@FXML
	private TextField parentRegIdLocalLanguage;

	@FXML
	private Label parentRegIdLocalLanguageLabel;

	@FXML
	private TextField parentUinId;

	@FXML
	private Label parentUinIdMessage;

	@FXML
	private Label parentUinIdLocalLanguageMessage;

	@FXML
	private TextField parentUinIdLocalLanguage;

	@FXML
	private Label parentUinIdLocalLanguageLabel;

	@FXML
	private Label parentUinIdLabel;

	private boolean isChild;

	private Node keyboardNode;

	@FXML
	protected Button autoFillBtn;

	@FXML
	protected Button copyPrevious;

	@FXML
	protected Button fetchBtn;

	@FXML
	private HBox dob;

	@FXML
	private HBox dobLocallanguage;

	@FXML
	private TextField dd;

	@FXML
	private TextField mm;

	@FXML
	private TextField yyyy;

	@FXML
	private TextField ddLocalLanguage;

	@FXML
	private Label ddLocalLanguageLabel;

	@FXML
	private Label dobMessage;

	@FXML
	private TextField mmLocalLanguage;

	@FXML
	private Label mmLocalLanguageLabel;

	@FXML
	private TextField yyyyLocalLanguage;

	@FXML
	private Label yyyyLocalLanguageLabel;

	@FXML
	private Label residenceLblLocalLanguage;

	@Autowired
	private PridValidator<String> pridValidatorImpl;
	@Autowired
	private UinValidator<String> uinValidator;
	@Autowired
	private RidValidator<String> ridValidator;
	@Autowired
	private Validations validation;
	@Autowired
	private MasterSyncService masterSync;
	@FXML
	private AnchorPane dateAnchorPane;
	@FXML
	private GridPane residenceParentpane;
	@FXML
	private AnchorPane dateAnchorPaneLocalLanguage;
	@FXML
	private VBox applicationLanguageAddressPane;
	@FXML
	private VBox localLanguageAddressPane;
	@FXML
	private Label preRegistrationLabel;
	@FXML
	private Label fullNameLabel;
	@FXML
	private Label fullNameMessage;
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
	@FXML
	private FlowPane parentFlowPane;
	@FXML
	private Button national;
	@FXML
	private Button male;
	@FXML
	private Button female;
	@FXML
	private Button maleLocalLanguage;
	@FXML
	private Button femaleLocalLanguage;
	@FXML
	private GridPane demographicDetail;
	@FXML
	private GridPane dobParentPane;
	@FXML
	private GridPane demographicParentPane;
	@FXML
	private GridPane fullNameParentPane;
	@FXML
	private GridPane emailIdCniParentPane;
	@FXML
	private GridPane childParentDetail;
	@FXML
	private GridPane genderParentPane;
	@FXML
	private TextField updateUinId;
	@FXML
	private Button foreigner;
	@FXML
	private TextField residence;
	@FXML
	private TextField genderValue;
	@FXML
	private TextField genderValueLocalLanguage;
	@FXML
	private Button nationalLocalLanguage;
	@FXML
	private Button foreignerLocalLanguage;
	@FXML
	private TextField residenceLocalLanguage;
	@FXML
	private VBox applicationFullName;
	@FXML
	private GridPane fullNameGridPane;
	@FXML
	private ImageView fullNameKeyboardImage;
	@FXML
	private ImageView addressLine1KeyboardImage;
	@FXML
	private ImageView addressLine2KeyboardImage;
	@FXML
	private ImageView addressLine3KeyboardImage;
	@FXML
	private ImageView parentNameKeyboardImage;
	@FXML
	private VBox localFullName;
	@FXML
	private GridPane applicationAge;
	@FXML
	private GridPane localAge;
	@FXML
	private VBox localUinIdPane;
	@FXML
	private VBox applicationUinIdPane;
	@FXML
	private AnchorPane localRidOrUinToggle;
	@FXML
	private VBox localRidPane;
	@FXML
	private VBox applicationRidPane;
	@FXML
	private GridPane applicationGender;
	@FXML
	private GridPane localGender;
	@FXML
	private GridPane applicationResidence;
	@FXML
	private GridPane localResidence;
	@FXML
	private GridPane localAddressPane;
	@FXML
	private GridPane preRegParentPane;
	@FXML
	private VBox applicationemailIdPane;
	@FXML
	private VBox applicationCniOrPinNumberPane;
	@Autowired
	private DateValidation dateValidation;
	@Autowired
	private PreRegistrationDataSyncService preRegistrationDataSyncService;
	@Autowired
	private RegistrationController registrationController;
	@Autowired
	private DocumentScanController documentScanController;
	@Autowired
	private Transliteration<String> transliteration;

	private FXUtils fxUtils;
	private Date dateOfBirth;
	private int minAge;
	private int maxAge;

	@FXML
	private HBox parentDetailsHbox;
	@FXML
	private HBox localParentDetailsHbox;
	@FXML
	private AnchorPane ridOrUinToggle;

	@Autowired
	private MasterSyncService masterSyncService;
	@FXML
	private GridPane borderToDo;
	@FXML
	private Label registrationNavlabel;
	@FXML
	private AnchorPane keyboardPane;
	private boolean lostUIN = false;
	private ResourceBundle applicationLabelBundle;
	private String textMale;
	private String textFemale;
	private String textMaleLocalLanguage;
	private String textFemaleLocalLanguage;
	private String textMaleCode;
	@FXML
	private Label ageOrDOBLocalLanguageLabel;
	@FXML
	private Label ageOrDOBLabel;
	@Autowired
	private FaceCaptureController faceCaptureController;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize()
	 */
	@FXML
	private void initialize() {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the Demographic Details Screen");
		try {
			RegistrationConstants.CNI_MANDATORY = String.valueOf(false);
			if (getRegistrationDTOFromSession() == null) {
				validation.updateAsLostUIN(false);
				registrationController.createRegistrationDTOObject(RegistrationConstants.PACKET_TYPE_NEW);
			}

			if (getRegistrationDTOFromSession() != null
					&& getRegistrationDTOFromSession().getSelectionListDTO() == null) {
				getRegistrationDTOFromSession().setUpdateUINNonBiometric(false);
				getRegistrationDTOFromSession().setUpdateUINChild(false);
			}
			postalCode.setDisable(true);
			validation.setChild(false);
			parentDetailPane.setManaged(false);
			lostUIN = false;
			changeOrientation(NodeOrientation.RIGHT_TO_LEFT);
			fxUtils = FXUtils.getInstance();
			fxUtils.setTransliteration(transliteration);
			isChild = false;
			disableLocalFields();
			switchedOnParentUinOrRid = new SimpleBooleanProperty(true);
			toggleFunctionForParentUinOrRid();
			ageBasedOperation();
			listenerOnFields();
			loadLocalLanguageFields();
			loadKeyboard();
			ageFieldLocalLanguage.setDisable(true);
			renderComboBoxes();
			addRegions();
			minAge = Integer.parseInt(getValueFromApplicationContext(RegistrationConstants.MIN_AGE));
			maxAge = Integer.parseInt(getValueFromApplicationContext(RegistrationConstants.MAX_AGE));
			applicationLabelBundle = ApplicationContext.getInstance().getApplicationLanguageBundle();
			List<IndividualTypeDto> applicantType = masterSyncService.getIndividualType(
					RegistrationConstants.ATTR_NON_FORINGER, ApplicationContext.applicationLanguage());
			residence.setText(applicantType.get(0).getName());
			residence.setId(applicantType.get(0).getCode());
			List<IndividualTypeDto> applicantTypeLocal = masterSyncService
					.getIndividualType(RegistrationConstants.ATTR_NON_FORINGER, ApplicationContext.localLanguage());
			residenceLocalLanguage.setText(applicantTypeLocal.get(0).getName());
			genderSettings();
			if (getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
					.equals(RegistrationConstants.PACKET_TYPE_LOST)) {
				preRegParentPane.setVisible(false);
				preRegParentPane.setManaged(false);
				national.getStyleClass().addAll("residence", "button");
				nationalLocalLanguage.getStyleClass().addAll("residence", "button");
				residence.setText(RegistrationConstants.EMPTY);
			} else {
				national.getStyleClass().addAll("selectedResidence", "button");
				nationalLocalLanguage.getStyleClass().addAll("selectedResidence", "button");
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_DEMOGRAPHIC_PAGE);

		}
	}

	private void genderSettings() {
		textMale = masterSyncService.getGenderDtls(ApplicationContext.applicationLanguage()).stream()
				.filter(dto -> dto.getCode().equals(RegistrationConstants.MALE_CODE)).findFirst().get().getGenderName();
		textMaleCode = masterSyncService.getGenderDtls(ApplicationContext.applicationLanguage()).stream()
				.filter(dto -> dto.getCode().equals(RegistrationConstants.MALE_CODE)).findFirst().get().getCode();
		textFemale = masterSyncService.getGenderDtls(ApplicationContext.applicationLanguage()).stream()
				.filter(dto -> dto.getCode().equals(RegistrationConstants.FEMALE_CODE)).findFirst().get()
				.getGenderName();
		textMaleLocalLanguage = masterSyncService.getGenderDtls(ApplicationContext.localLanguage()).stream()
				.filter(dto -> dto.getCode().equals(RegistrationConstants.MALE_CODE)).findFirst().get().getGenderName();
		textFemaleLocalLanguage = masterSyncService.getGenderDtls(ApplicationContext.localLanguage()).stream()
				.filter(dto -> dto.getCode().equals(RegistrationConstants.FEMALE_CODE)).findFirst().get()
				.getGenderName();
		male.setText(textMale);
		female.setText(textFemale);
		maleLocalLanguage.setText(textMaleLocalLanguage);
		femaleLocalLanguage.setText(textFemaleLocalLanguage);
		male(null);
	}

	/**
	 * setting the registration navigation label to lost uin
	 */
	protected void lostUIN() {
		lostUIN = true;
		registrationNavlabel
				.setText(ApplicationContext.applicationLanguageBundle().getString(RegistrationConstants.LOSTUINLBL));
	}

	/**
	 * TO change the orientation based on language
	 *
	 * @param NodeOrientation
	 */
	private void changeOrientation(NodeOrientation orientation) {
		if ((boolean) applicationContext.isPrimaryLanguageRightToLeft()) {
			fullName.setNodeOrientation(orientation);
			fullNameLabel.setAlignment(Pos.CENTER_RIGHT);
			fullNameMessage.setAlignment(Pos.CENTER_RIGHT);
			addressLine1.setNodeOrientation(orientation);
			addressLine1Label.setAlignment(Pos.CENTER_RIGHT);
			addressLine1Message.setAlignment(Pos.CENTER_RIGHT);
			addressLine2.setNodeOrientation(orientation);
			addressLine2Label.setAlignment(Pos.CENTER_RIGHT);
			addressLine2Message.setAlignment(Pos.CENTER_RIGHT);
			addressLine3.setNodeOrientation(orientation);
			addressLine3Label.setAlignment(Pos.CENTER_RIGHT);
			addressLine3Message.setAlignment(Pos.CENTER_RIGHT);
			province.setNodeOrientation(orientation);
			provinceLabel.setAlignment(Pos.CENTER_RIGHT);
			provinceMessage.setAlignment(Pos.CENTER_RIGHT);
			city.setNodeOrientation(orientation);
			cityLabel.setAlignment(Pos.CENTER_RIGHT);
			cityMessage.setAlignment(Pos.CENTER_RIGHT);
			region.setNodeOrientation(orientation);
			regionLabel.setAlignment(Pos.CENTER_RIGHT);
			regionMessage.setAlignment(Pos.CENTER_RIGHT);
			localAdminAuthority.setNodeOrientation(orientation);
			localAdminAuthorityLabel.setAlignment(Pos.CENTER_RIGHT);
			localAdminAuthorityMessage.setAlignment(Pos.CENTER_RIGHT);
			parentName.setNodeOrientation(orientation);
			parentNameLabel.setAlignment(Pos.CENTER_RIGHT);
			parentNameMessage.setAlignment(Pos.CENTER_RIGHT);

		}
		if ((boolean) applicationContext.isSecondaryLanguageRightToLeft()) {
			fullNameLocalLanguage.setNodeOrientation(orientation);
			fullNameLocalLanguageLabel.setAlignment(Pos.CENTER_RIGHT);
			fullNameLocalLanguageMessage.setAlignment(Pos.CENTER_RIGHT);
			addressLine1LocalLanguage.setNodeOrientation(orientation);
			addressLine1LocalLanguageLabel.setAlignment(Pos.CENTER_RIGHT);
			addressLine1LocalLanguageMessage.setAlignment(Pos.CENTER_RIGHT);
			addressLine2LocalLanguage.setNodeOrientation(orientation);
			addressLine2LocalLanguageLabel.setAlignment(Pos.CENTER_RIGHT);
			addressLine2LocalLanguageMessage.setAlignment(Pos.CENTER_RIGHT);
			addressLine3LocalLanguage.setNodeOrientation(orientation);
			addressLine3LocalLanguageLabel.setAlignment(Pos.CENTER_RIGHT);
			addressLine3LocalLanguageMessage.setAlignment(Pos.CENTER_RIGHT);
			provinceLocalLanguage.setNodeOrientation(orientation);
			provinceLocalLanguageLabel.setAlignment(Pos.CENTER_RIGHT);
			provinceLocalLanguageMessage.setAlignment(Pos.CENTER_RIGHT);
			cityLocalLanguage.setNodeOrientation(orientation);
			cityLocalLanguageLabel.setAlignment(Pos.CENTER_RIGHT);
			cityLocalLanguageMessage.setAlignment(Pos.CENTER_RIGHT);
			regionLocalLanguage.setNodeOrientation(orientation);
			regionLocalLanguageLabel.setAlignment(Pos.CENTER_RIGHT);
			regionLocalLanguageMessage.setAlignment(Pos.CENTER_RIGHT);
			localAdminAuthorityLocalLanguage.setNodeOrientation(orientation);
			localAdminAuthorityLocalLanguageLabel.setAlignment(Pos.CENTER_RIGHT);
			localAdminAuthorityLocalLanguageMessage.setAlignment(Pos.CENTER_RIGHT);
			parentNameLocalLanguage.setNodeOrientation(orientation);
			parentNameLocalLanguageLabel.setAlignment(Pos.CENTER_RIGHT);
			parentNameLocalLanguageMessage.setAlignment(Pos.CENTER_RIGHT);

		}
	}

	/**
	 * Disabe local language fields
	 */
	private void disableLocalFields() {
		localResidence.setDisable(true);
		localGender.setDisable(true);
		regionLocalLanguagePane.setDisable(true);
		provinceLocalLanguagePane.setDisable(true);
		cityLocalLanguagePane.setDisable(true);
		localAdminAuthorityLocalLanguagePane.setDisable(true);
		localEmailIdPane.setDisable(true);
		localCniOrPinPane.setDisable(true);
		postalCodeLocalLanguagePane.setDisable(true);
		localMobileNumberPane.setDisable(true);
		localAge.setDisable(true);
		localUinIdPane.setDisable(true);
		localRidPane.setDisable(true);
		localRidOrUinToggle.setDisable(true);
	}

	/**
	 * Toggle functionality between age field and date picker.
	 */
	private void toggleFunctionForParentUinOrRid() {
		try {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"Entering into toggle function for parent uin or rid");

			switchedOnParentUinOrRid.addListener((observableValue, oldValue, newValue) -> {
				if (newValue) {
					uinRidToggleLabel1.setLayoutX(0);
					uinRidToggleLabel1LocalLanguage.setLayoutX(0);

					parentRegIdLocalLanguage.clear();
					parentRegId.clear();
					parentUinIdLocalLanguage.clear();
					parentUinId.clear();
					applicationRidPane.setDisable(false);
					applicationUinIdPane.setDisable(true);
					parentRegIdMessage.setVisible(false);
					parentRegIdLocalLanguageMessage.setVisible(false);
					parentUinIdMessage.setVisible(false);
					parentUinIdLocalLanguageMessage.setVisible(false);

				} else {
					uinRidToggleLabel1.setLayoutX(30);
					uinRidToggleLabel1LocalLanguage.setLayoutX(30);

					parentRegIdLocalLanguage.clear();
					parentRegId.clear();
					parentUinIdLocalLanguage.clear();
					parentUinId.clear();
					applicationRidPane.setDisable(true);
					applicationUinIdPane.setDisable(false);
					parentRegIdMessage.setVisible(false);
					parentRegIdLocalLanguageMessage.setVisible(false);
					parentUinIdMessage.setVisible(false);
					parentUinIdLocalLanguageMessage.setVisible(false);

				}

				parentRegId.getStyleClass().remove(RegistrationConstants.DEMOGRAPHIC_TEXTFIELD_FOCUSED);
				parentRegId.getStyleClass().add(RegistrationConstants.DEMOGRAPHIC_TEXTFIELD);

				parentUinId.getStyleClass().remove(RegistrationConstants.DEMOGRAPHIC_TEXTFIELD_FOCUSED);
				parentUinId.getStyleClass().add(RegistrationConstants.DEMOGRAPHIC_TEXTFIELD);

				parentRegIdLabel.setVisible(false);
				parentRegId.setPromptText(parentRegIdLabel.getText());

				parentUinIdLabel.setVisible(false);
				parentUinId.setPromptText(parentUinIdLabel.getText());

			});

			uinRidToggleLabel1
					.setOnMouseClicked(event -> switchedOnParentUinOrRid.set(!switchedOnParentUinOrRid.get()));
			uinRidToggleLabel2
					.setOnMouseClicked(event -> switchedOnParentUinOrRid.set(!switchedOnParentUinOrRid.get()));
			uinRidToggleLabel1LocalLanguage
					.setOnMouseClicked(event -> switchedOnParentUinOrRid.set(!switchedOnParentUinOrRid.get()));
			uinRidToggleLabel2LocalLanguage
					.setOnMouseClicked(event -> switchedOnParentUinOrRid.set(!switchedOnParentUinOrRid.get()));

			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"Exiting the toggle function for parent uin or rid");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - TOGGLING OF DOB AND AGE FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * method action when national button is pressed
	 * 
	 * @param ActionEvent
	 *            the action event
	 */
	@FXML
	private void national(ActionEvent event) {
		List<IndividualTypeDto> applicantType = masterSyncService
				.getIndividualType(RegistrationConstants.ATTR_NON_FORINGER, ApplicationContext.applicationLanguage());
		residence.setText(applicantType.get(0).getName());
		residence.setId(applicantType.get(0).getCode());
		List<IndividualTypeDto> applicantTypeLocal = masterSyncService
				.getIndividualType(RegistrationConstants.ATTR_NON_FORINGER, ApplicationContext.localLanguage());
		residenceLocalLanguage.setText(applicantTypeLocal.get(0).getName());
		national.getStyleClass().clear();
		foreigner.getStyleClass().clear();
		nationalLocalLanguage.getStyleClass().clear();
		foreignerLocalLanguage.getStyleClass().clear();
		nationalLocalLanguage.getStyleClass().addAll("selectedResidence", "button");
		foreignerLocalLanguage.getStyleClass().addAll("residence", "button");
		national.getStyleClass().addAll("selectedResidence", "button");
		foreigner.getStyleClass().addAll("residence", "button");
	}

	/**
	 * method action when mail button is pressed
	 * 
	 * @param ActionEvent
	 *            the action event
	 */
	@FXML
	private void male(ActionEvent event) {
		genderValue.setText(textMale);
		genderValueLocalLanguage.setText(textMaleLocalLanguage);
		male.getStyleClass().clear();
		female.getStyleClass().clear();
		maleLocalLanguage.getStyleClass().clear();
		femaleLocalLanguage.getStyleClass().clear();
		if (getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
				.equals(RegistrationConstants.PACKET_TYPE_LOST) && event == null) {
			male.getStyleClass().addAll("residence", "button");
			maleLocalLanguage.getStyleClass().addAll("residence", "button");
			genderValue.setText(RegistrationConstants.EMPTY);
			genderValueLocalLanguage.setText(RegistrationConstants.EMPTY);
		} else {
			male.getStyleClass().addAll("selectedResidence", "button");
			maleLocalLanguage.getStyleClass().addAll("selectedResidence", "button");
			genderValue.setText(textMale);
			genderValueLocalLanguage.setText(textMaleLocalLanguage);
		}
		femaleLocalLanguage.getStyleClass().addAll("residence", "button");
		female.getStyleClass().addAll("residence", "button");
	}

	/**
	 * method action when foriegner button is pressed
	 * 
	 * @param ActionEvent
	 *            the action event
	 */
	@FXML
	private void foreigner(ActionEvent event) {
		List<IndividualTypeDto> applicantType = masterSyncService.getIndividualType(RegistrationConstants.ATTR_FORINGER,
				ApplicationContext.applicationLanguage());
		residence.setText(applicantType.get(0).getName());
		residence.setId(applicantType.get(0).getCode());
		List<IndividualTypeDto> applicantTypeLocal = masterSyncService
				.getIndividualType(RegistrationConstants.ATTR_FORINGER, ApplicationContext.localLanguage());
		residenceLocalLanguage.setText(applicantTypeLocal.get(0).getName());
		national.getStyleClass().clear();
		foreigner.getStyleClass().clear();
		nationalLocalLanguage.getStyleClass().clear();
		foreignerLocalLanguage.getStyleClass().clear();
		nationalLocalLanguage.getStyleClass().addAll("residence", "button");
		foreignerLocalLanguage.getStyleClass().addAll("selectedResidence", "button");
		foreigner.getStyleClass().addAll("selectedResidence", "button");
		national.getStyleClass().addAll("residence", "button");
	}

	/**
	 * method action when female button is pressed
	 * 
	 * @param ActionEvent
	 *            the action event
	 */
	@FXML
	private void female(ActionEvent event) {
		genderValue.setText(textFemale);
		genderValueLocalLanguage.setText(textFemaleLocalLanguage);
		male.getStyleClass().clear();
		female.getStyleClass().clear();
		maleLocalLanguage.getStyleClass().clear();
		femaleLocalLanguage.getStyleClass().clear();
		maleLocalLanguage.getStyleClass().addAll("residence", "button");
		femaleLocalLanguage.getStyleClass().addAll("selectedResidence", "button");
		male.getStyleClass().addAll("residence", "button");
		female.getStyleClass().addAll("selectedResidence", "button");
	}

	/**
	 * To restrict the user not to eavcdnter any values other than integer values.
	 */
	private void ageBasedOperation() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the age given by age field");
			fxUtils.validateLabelFocusOut(dobParentPane, ageField, ageFieldLocalLanguage);
			ageField.focusedProperty().addListener((obsValue, oldValue, newValue) -> {
				if (oldValue) {
					ageValidation(oldValue);
				}
			});
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the age given by age field");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - AGE FIELD VALIDATION FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	public void ageValidation(Boolean oldValue) {
		int age = 0;
		if (ageField.getText().matches(RegistrationConstants.NUMBER_OR_NOTHING_REGEX)) {
			if (ageField.getText().matches(RegistrationConstants.NUMBER_REGEX)) {
				if (maxAge >= Integer.parseInt(ageField.getText())) {
					age = Integer.parseInt(ageField.getText());

					// Not to recalulate DOB and populate DD, MM and YYYY UI fields based on Age,
					// since Age was calculated based on DOB entered by the user. Calculate DOB and
					// populate DD, MM and YYYY UI fields based on user entered Age.
					if(!getRegistrationDTOFromSession().isAgeCalculatedByDOB()) {
						Calendar defaultDate = Calendar.getInstance();
						defaultDate.set(Calendar.DATE, 1);
						defaultDate.set(Calendar.MONTH, 0);
						defaultDate.add(Calendar.YEAR, -age);
						
						dateOfBirth = Date.from(defaultDate.toInstant());
						dd.setText(String.valueOf(defaultDate.get(Calendar.DATE)));
						mm.setText(String.valueOf(defaultDate.get(Calendar.MONTH + 1)));
						yyyy.setText(String.valueOf(defaultDate.get(Calendar.YEAR)));
					}

					if (age <= minAge) {
						if (RegistrationConstants.DISABLE.equalsIgnoreCase(
								getValueFromApplicationContext(RegistrationConstants.FINGERPRINT_DISABLE_FLAG))
								&& RegistrationConstants.DISABLE.equalsIgnoreCase(
										getValueFromApplicationContext(RegistrationConstants.IRIS_DISABLE_FLAG))) {
							isChild = true;
							validation.setChild(isChild);
							generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PARENT_BIO_MSG);

						} else {
							updatePageFlow(RegistrationConstants.GUARDIAN_BIOMETRIC, true);
							updatePageFlow(RegistrationConstants.FINGERPRINT_CAPTURE, false);
							updatePageFlow(RegistrationConstants.IRIS_CAPTURE, false);
							parentRegIdLocalLanguage.clear();
							parentRegId.clear();
							parentUinIdLocalLanguage.clear();
							parentUinId.clear();
							parentDetailPane.setManaged(true);
							parentDetailPane.setVisible(true);
							parentDetailPane.setDisable(false);
							parentName.clear();
							parentNameLocalLanguage.clear();
							parentRegId.clear();
							isChild = true;
							parentNameKeyboardImage.setDisable(!isChild);
							keyboardNode.setManaged(!isChild);
							validation.setChild(isChild);

							if (getRegistrationDTOFromSession() != null
									&& getRegistrationDTOFromSession().getSelectionListDTO() != null) {
								enableParentUIN();
							}
						}
					} else {
						if (getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO() != null) {

							getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
									.setFingerprintDetailsDTO(new ArrayList<>());

							getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
									.setIrisDetailsDTO(new ArrayList<>());

							getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
									.setBiometricExceptionDTO(new ArrayList<>());

							getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
									.setExceptionFace(new FaceDetailsDTO());

							getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
									.setFace(new FaceDetailsDTO());

							getRegistrationDTOFromSession().getBiometricDTO().getIntroducerBiometricDTO()
									.setHasExceptionPhoto(false);

						}

						updatePageFlow(RegistrationConstants.GUARDIAN_BIOMETRIC, false);
						updateBioPageFlow(RegistrationConstants.FINGERPRINT_DISABLE_FLAG,
								RegistrationConstants.FINGERPRINT_CAPTURE);
						updateBioPageFlow(RegistrationConstants.IRIS_DISABLE_FLAG, RegistrationConstants.IRIS_CAPTURE);

						parentFieldValidation();
					}
					fxUtils.validateOnFocusOut(dobParentPane, ageField, validation, ageFieldLocalLanguage, false,
							oldValue);
				} else {
					ageField.getStyleClass().remove("demoGraphicTextFieldOnType");
					ageField.getStyleClass().add("demoGraphicTextFieldFocused");
					Label ageFieldLabel = (Label)dobParentPane.lookup("#"+ageField.getId()+"Label");
					ageFieldLabel.getStyleClass().add("demoGraphicFieldLabel");
					ageField.getStyleClass().remove("demoGraphicFieldLabelOnType");
					dobMessage.setText(RegistrationUIConstants.INVALID_AGE + maxAge);
					dobMessage.setVisible(true);
					
					generateAlert(dobParentPane, RegistrationConstants.DOB, dobMessage.getText());
					parentFieldValidation();
				}
			}
		} else {
			ageField.setText(RegistrationConstants.EMPTY);
		}
	}

	private void parentFieldValidation() {
		parentDetailPane.setManaged(false);
		parentDetailPane.setVisible(false);
		parentDetailPane.setDisable(true);
		isChild = false;
		validation.setChild(isChild);
		keyboardNode.setManaged(isChild);
		keyboardNode.setVisible(isChild);
		parentName.clear();
		parentNameLocalLanguage.clear();
		parentRegId.clear();
		parentRegIdLocalLanguage.clear();
		parentRegId.clear();
		parentUinIdLocalLanguage.clear();
		parentUinId.clear();
	}

	/**
	 * Listening on the fields for any operation
	 */
	private void listenerOnFields() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Populating the local language fields");
			boolean hasToBeTransliterated = true;
			
			
			fxUtils.validateOnFocusOut(parentFlowPane, fullName, validation, fullNameLocalLanguage,
					hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, addressLine1, validation, addressLine1LocalLanguage,
					hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, addressLine2, validation, addressLine2LocalLanguage,
					hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, addressLine3, validation, addressLine3LocalLanguage,
					hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, parentName, validation, parentNameLocalLanguage,
					hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, parentRegId, validation, parentRegIdLocalLanguage,
					!hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, parentUinId, validation, parentUinIdLocalLanguage,
					!hasToBeTransliterated);

			fxUtils.validateOnFocusOut(parentFlowPane, mobileNo, validation, mobileNoLocalLanguage,
					!hasToBeTransliterated);
			fxUtils.validateOnType(parentFlowPane, ageField, validation, ageFieldLocalLanguage, !hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, postalCode, validation, postalCodeLocalLanguage,
					!hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, emailId, validation, emailIdLocalLanguage,
					!hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, cniOrPinNumber, validation, cniOrPinNumberLocalLanguage,
					!hasToBeTransliterated);

			fxUtils.focusedAction(parentFlowPane,dd );
			fxUtils.focusedAction(parentFlowPane,mm );
			fxUtils.focusedAction(parentFlowPane,yyyy );
			fxUtils.populateLocalComboBox(parentFlowPane, city, cityLocalLanguage);
			fxUtils.populateLocalComboBox(parentFlowPane, region, regionLocalLanguage);
			fxUtils.populateLocalComboBox(parentFlowPane, province, provinceLocalLanguage);
			fxUtils.populateLocalComboBox(parentFlowPane, localAdminAuthority, localAdminAuthorityLocalLanguage);

			dateValidation.validateDate(parentFlowPane, dd, mm, yyyy, validation, fxUtils, ddLocalLanguage, ageField,
					ageFieldLocalLanguage, dobMessage);
			dateValidation.validateMonth(parentFlowPane, dd, mm, yyyy, validation, fxUtils, mmLocalLanguage, ageField,
					ageFieldLocalLanguage, dobMessage);
			dateValidation.validateYear(parentFlowPane, dd, mm, yyyy, validation, fxUtils, yyyyLocalLanguage, ageField,
					ageFieldLocalLanguage, dobMessage);
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - Listner method failed ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * Loading the the labels of local language fields
	 * 
	 */
	private void loadLocalLanguageFields() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading label fields of local language");
			ResourceBundle localProperties = applicationContext.getLocalLanguageProperty();
			fullNameLocalLanguageLabel.setText(localProperties.getString("fullName"));
			fullNameLocalLanguage.setPromptText(localProperties.getString("fullName"));
			addressLine1LocalLanguageLabel.setText(localProperties.getString("addressLine1"));
			addressLine1LocalLanguage.setPromptText(localProperties.getString("addressLine1"));
			addressLine2LocalLanguageLabel.setText(localProperties.getString("addressLine2"));
			addressLine2LocalLanguage.setPromptText(localProperties.getString("addressLine2"));
			addressLine3LocalLanguageLabel.setText(localProperties.getString("addressLine3"));
			addressLine3LocalLanguage.setPromptText(localProperties.getString("addressLine3"));
			ageFieldLocalLanguageLabel.setText(localProperties.getString("ageField"));
			ageFieldLocalLanguage.setPromptText(localProperties.getString("ageField"));
			genderLocalLanguageLabel.setText(localProperties.getString("gender"));
			maleLocalLanguage.setText(localProperties.getString("male"));
			femaleLocalLanguage.setText(localProperties.getString("female"));
			regionLocalLanguageLabel.setText(localProperties.getString("region"));
			cityLocalLanguageLabel.setText(localProperties.getString("city"));
			provinceLocalLanguageLabel.setText(localProperties.getString("province"));
			localAdminAuthorityLocalLanguageLabel.setText(localProperties.getString("localAdminAuthority"));
			cniOrPinNumberLocalLanguageLabel.setText(localProperties.getString("cniOrPinNumber"));
			cniOrPinNumberLocalLanguage.setPromptText(localProperties.getString("cniOrPinNumber"));
			postalCodeLocalLanguageLabel.setText(localProperties.getString("postalCode"));
			postalCodeLocalLanguage.setPromptText(localProperties.getString("postalCode"));
			mobileNoLocalLanguageLabel.setText(localProperties.getString("mobileNo"));
			mobileNoLocalLanguage.setPromptText(localProperties.getString("mobileNo"));
			emailIdLocalLanguageLabel.setText(localProperties.getString("emailId"));
			emailIdLocalLanguage.setPromptText(localProperties.getString("emailId"));
			parentNameLocalLanguageLabel.setText(localProperties.getString("parentName"));
			parentNameLocalLanguage.setPromptText(localProperties.getString("parentName"));
			parentUinIdLocalLanguageLabel.setText(localProperties.getString("parentUinId"));
			parentRegIdLocalLanguageLabel.setText(localProperties.getString("parentRegId"));
			parentRegIdLocalLanguage.setPromptText(localProperties.getString("parentRegId"));
			parentUinIdLocalLanguage.setPromptText(localProperties.getString("parentUinId"));
			residenceLblLocalLanguage.setText(localProperties.getString("residence"));
			nationalLocalLanguage.setText(localProperties.getString("national"));
			foreignerLocalLanguage.setText(localProperties.getString("foreigner"));
			localAdminAuthorityLocalLanguage.setPromptText(localProperties.getString("localAdminAuthority"));
			cityLocalLanguage.setPromptText(localProperties.getString("city"));
			regionLocalLanguage.setPromptText(localProperties.getString("region"));
			provinceLocalLanguage.setPromptText(localProperties.getString("province"));
			ddLocalLanguage.setPromptText(localProperties.getString("dd"));
			mmLocalLanguage.setPromptText(localProperties.getString("mm"));
			yyyyLocalLanguage.setPromptText(localProperties.getString("yyyy"));
			languageLabelLocalLanguage.setText(localProperties.getString("language"));
			ddLocalLanguageLabel.setText(localProperties.getString("dd"));
			mmLocalLanguageLabel.setText(localProperties.getString("mm"));
			yyyyLocalLanguageLabel.setText(localProperties.getString("yyyy"));
			parentRegIdLabel.setMinWidth(Region.USE_PREF_SIZE);
			parentRegIdLabel.setMaxWidth(Region.USE_PREF_SIZE);
			parentRegIdLocalLanguageLabel.setMinWidth(Region.USE_PREF_SIZE);
			parentRegIdLocalLanguageLabel.setMaxWidth(Region.USE_PREF_SIZE);
			parentUinIdLabel.setMinWidth(Region.USE_PREF_SIZE);
			parentUinIdLabel.setMaxWidth(Region.USE_PREF_SIZE);
			parentUinIdLocalLanguageLabel.setMinWidth(Region.USE_PREF_SIZE);
			parentUinIdLocalLanguageLabel.setMaxWidth(Region.USE_PREF_SIZE);
			ageOrDOBLocalLanguageLabel.setText(localProperties.getString("ageOrDOBField"));

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING LOCAL LANGUAGE FIELDS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * Loading the virtual keyboard
	 */
	private void loadKeyboard() {
		try {
			VirtualKeyboard vk = VirtualKeyboard.getInstance();
			keyboardNode = vk.view();
			keyboardNode.setVisible(false);
			keyboardNode.setManaged(false);
			keyboardPane.getChildren().add(keyboardNode);
			vk.changeControlOfKeyboard(fullNameLocalLanguage);
			vk.changeControlOfKeyboard(addressLine1LocalLanguage);
			vk.changeControlOfKeyboard(addressLine2LocalLanguage);
			vk.changeControlOfKeyboard(addressLine3LocalLanguage);
			vk.changeControlOfKeyboard(parentNameLocalLanguage);
			vk.focusListener(fullNameLocalLanguage, 200.00, keyboardNode);
			vk.focusListener(addressLine1LocalLanguage, 470.00, keyboardNode);
			vk.focusListener(addressLine2LocalLanguage, 555.00, keyboardNode);
			vk.focusListener(addressLine3LocalLanguage, 630.00, keyboardNode);
			vk.focusListener(parentNameLocalLanguage, 1110.00, keyboardNode);
		} catch (NullPointerException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * To load the regions in the selection list based on the language code
	 */
	private void addRegions() {
		try {
			region.getItems().clear();
			regionLocalLanguage.getItems().clear();
			postalCode.setText(RegistrationConstants.EMPTY);
			postalCodeLocalLanguage.setText(RegistrationConstants.EMPTY);

			region.getItems()
					.addAll(masterSync.findLocationByHierarchyCode(
							ApplicationContext.applicationLanguageBundle().getString(region.getId()),
							ApplicationContext.applicationLanguage()));
			regionLocalLanguage.getItems()
					.addAll(masterSync.findLocationByHierarchyCode(
							ApplicationContext.localLanguageBundle().getString(region.getId()),
							ApplicationContext.localLanguage()));
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING FAILED FOR REGION SELECTION LIST ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * To load the provinces in the selection list based on the language code
	 */
	@FXML
	private void addProvince() {
		try {
			retrieveAndPopulateLocationByHierarchy(region, province, provinceLocalLanguage);

			city.getItems().clear();
			cityLocalLanguage.getItems().clear();
			localAdminAuthority.getItems().clear();
			localAdminAuthorityLocalLanguage.getItems().clear();
			postalCode.setText(RegistrationConstants.EMPTY);
			postalCodeLocalLanguage.setText(RegistrationConstants.EMPTY);
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING FAILED FOR PROVINCE SELECTION LIST ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

		}

	}

	/**
	 * To load the cities in the selection list based on the language code
	 */
	@FXML
	private void addCity() {
		try {
			retrieveAndPopulateLocationByHierarchy(province, city, cityLocalLanguage);

			localAdminAuthority.getItems().clear();
			localAdminAuthorityLocalLanguage.getItems().clear();
			postalCode.setText(RegistrationConstants.EMPTY);
			postalCodeLocalLanguage.setText(RegistrationConstants.EMPTY);
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING FAILED FOR CITY SELECTION LIST ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));

		}
	}

	/**
	 * To load the localAdminAuthorities selection list based on the language code
	 */
	@FXML
	private void addlocalAdminAuthority() {
		try {
			retrieveAndPopulateLocationByHierarchy(city, localAdminAuthority, localAdminAuthorityLocalLanguage);

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING FAILED FOR LOCAL ADMIN AUTHORITY SELECTOIN LIST ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	@FXML
	private void populatePincode() {
		try {
			LocationDto locationDTO = localAdminAuthority.getSelectionModel().getSelectedItem();

			if (null != locationDTO) {
				List<LocationDto> locationDtos = masterSync.findProvianceByHierarchyCode(locationDTO.getCode(),
						locationDTO.getLangCode());

				postalCode.setText(locationDtos.get(0).getName());
				postalCodeLocalLanguage.setText(locationDtos.get(0).getName());
			}

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - Populating of Pin Code Failed ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * 
	 * Saving the detail into concerned DTO'S
	 * 
	 */
	public void saveDetail() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Saving the fields to DTO");
		try {
			auditFactory.audit(AuditEvent.SAVE_DETAIL_TO_DTO, Components.REGISTRATION_CONTROLLER,
					SessionContext.userContext().getUserId(), RegistrationConstants.ONBOARD_DEVICES_REF_ID_TYPE);

			RegistrationDTO registrationDTO = getRegistrationDTOFromSession();
			DemographicInfoDTO demographicInfoDTO;

			OSIDataDTO osiDataDTO = registrationDTO.getOsiDataDTO();
			RegistrationMetaDataDTO registrationMetaDataDTO = registrationDTO.getRegistrationMetaDataDTO();
			String platformLanguageCode = ApplicationContext.applicationLanguage();
			String localLanguageCode = ApplicationContext.localLanguage();
			registrationMetaDataDTO.setFullName(buildValues(platformLanguageCode, localLanguageCode, fullName.getText(),
					fullNameLocalLanguage.getText()));
			SessionContext.map().put(RegistrationConstants.IS_Child, isChild);
			demographicInfoDTO = buildDemographicInfo();

			if (isChild) {
				osiDataDTO.setIntroducerType(IntroducerType.PARENT.getCode());
			}

			registrationMetaDataDTO.setParentOrGuardianRID(parentRegId.getText());

			osiDataDTO.setOperatorID(SessionContext.userContext().getUserId());

			registrationDTO.getDemographicDTO().setDemographicInfoDTO(demographicInfoDTO);

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Saved the demographic fields to DTO");

		} catch (Exception exception) {
			LOGGER.error("REGISTRATION - SAVING THE DETAILS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * Building demographic info dto
	 */
	private DemographicInfoDTO buildDemographicInfo() {

		// Get Application/Platform and Local/Secondary languages from
		// ApplicationContext
		String platformLanguageCode = ApplicationContext.applicationLanguage();
		String localLanguageCode = ApplicationContext.localLanguage();

		// Get RegistrationDTO from SessionContext
		RegistrationDTO registrationDTO = getRegistrationDTOFromSession();
		Map<String, DocumentDetailsDTO> documents = registrationDTO.getDemographicDTO().getApplicantDocumentDTO()
				.getDocuments();
		boolean isDocumentsMapEmpty = documents.isEmpty();

		// Get Applicant and Introducer Biometrics DTO
		BiometricInfoDTO applicantBiometric = registrationDTO.getBiometricDTO().getApplicantBiometricDTO();
		BiometricInfoDTO introducerBiometric = registrationDTO.getBiometricDTO().getIntroducerBiometricDTO();

		return Builder.build(DemographicInfoDTO.class)
				.with(demographicInfo -> demographicInfo
						.setIdentity((IndividualIdentity) Builder.build(IndividualIdentity.class)
								.with(identity -> identity.setFullName(buildDemoTextValues(platformLanguageCode,
										localLanguageCode, fullName, fullNameLocalLanguage,
										isNameNotRequired(fullName, registrationDTO.isNameNotUpdated()))))
								.with(identity -> identity.setDateOfBirth(applicationAge.isDisable()
										|| (dd.getText().isEmpty() && ageField.getText().isEmpty() && lostUIN) ? null
												: DateUtils.formatDate(dateOfBirth, "yyyy/MM/dd")))
								.with(identity -> identity
										.setAge(applicationAge.isDisable() || ageField.getText().isEmpty() ? null
												: Integer.parseInt(ageField.getText())))
								.with(identity -> identity
										.setResidenceStatus(buildDemoTextValues(platformLanguageCode, localLanguageCode,
												residence, residenceLocalLanguage, isTextFieldNotRequired(residence))))
								.with(identity -> identity.setGender(
										buildDemoTextValues(platformLanguageCode, localLanguageCode, genderValue,
												genderValueLocalLanguage, isTextFieldNotRequired(genderValue))))
								.with(identity -> identity.setAddressLine1(
										buildDemoTextValues(platformLanguageCode, localLanguageCode, addressLine1,
												addressLine1LocalLanguage, isTextFieldNotRequired(addressLine1))))
								.with(identity -> identity.setAddressLine2(
										buildDemoTextValues(platformLanguageCode, localLanguageCode, addressLine2,
												addressLine2LocalLanguage, isTextFieldNotRequired(addressLine2))))
								.with(identity -> identity.setAddressLine3(
										buildDemoTextValues(platformLanguageCode, localLanguageCode, addressLine3,
												addressLine3LocalLanguage, isTextFieldNotRequired(addressLine3))))
								.with(identity -> identity
										.setRegion(buildDemoComboValues(platformLanguageCode, localLanguageCode, region,
												regionLocalLanguage, isComboBoxValueNotRequired(region))))
								.with(identity -> identity
										.setProvince(buildDemoComboValues(platformLanguageCode, localLanguageCode,
												province, provinceLocalLanguage, isComboBoxValueNotRequired(province))))
								.with(identity -> identity.setCity(buildDemoComboValues(platformLanguageCode,
										localLanguageCode, city, cityLocalLanguage, isComboBoxValueNotRequired(city))))
								.with(identity -> identity.setLocalAdministrativeAuthority(
										buildDemoComboValues(platformLanguageCode, localLanguageCode,
												localAdminAuthority, localAdminAuthorityLocalLanguage,
												isComboBoxValueNotRequired(localAdminAuthority))))
								.with(identity -> identity.setPostalCode(
										buildDemoTextValue(postalCode, postalCodeFieldValidation(postalCode))))
								.with(identity -> identity
										.setPhone(buildDemoTextValue(mobileNo, isTextFieldNotRequired(mobileNo))))
								.with(identity -> identity
										.setEmail(buildDemoTextValue(emailId, isTextFieldNotRequired(emailId))))
								.with(identity -> identity.setCnieNumber(
										buildDemoTextValue(cniOrPinNumber, isTextFieldNotRequired(cniOrPinNumber))))
								.with(identity -> identity.setParentOrGuardianRID(
										buildDemoObjectValue(parentRegId, isTextFieldNotRequired(parentRegId))))
								.with(identity -> identity.setParentOrGuardianUIN(
										buildDemoObjectValue(parentUinId, isTextFieldNotRequired(parentUinId))))
								.with(identity -> identity.setParentOrGuardianName(
										buildDemoTextValues(platformLanguageCode, localLanguageCode, parentName,
												parentNameLocalLanguage, isTextFieldNotRequired(parentName))))
								.with(identity -> identity.setProofOfIdentity(getDocumentFromMap(
										RegistrationConstants.POI_DOCUMENT, documents, isDocumentsMapEmpty)))
								.with(identity -> identity.setProofOfAddress(getDocumentFromMap(
										RegistrationConstants.POA_DOCUMENT, documents, isDocumentsMapEmpty)))
								.with(identity -> identity.setProofOfRelationship(getDocumentFromMap(
										RegistrationConstants.POR_DOCUMENT, documents, isDocumentsMapEmpty)))
								.with(identity -> identity.setProofOfDateOfBirth(getDocumentFromMap(
										RegistrationConstants.DOB_DOCUMENT, documents, isDocumentsMapEmpty)))
								.with(identity -> identity.setIdSchemaVersion(1.0)).with(identity -> {
									String uin = getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getUin();
									identity.setUin(uin == null ? null : new BigInteger(uin));
								})
								.with(identity -> identity
										.setIndividualBiometrics(buildCBEFFDTO(isCBEFFNotAvailable(applicantBiometric),
												RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME)))
								.with(identity -> identity.setParentOrGuardianBiometrics(
										buildCBEFFDTO(isParentORGuardian(registrationDTO, introducerBiometric),
												RegistrationConstants.AUTHENTICATION_BIO_CBEFF_FILE_NAME)))
								.get()))
				.get();
	}

	private boolean isParentORGuardian(RegistrationDTO registrationDTO, BiometricInfoDTO introducerBiometric) {
		return !((registrationDTO.getSelectionListDTO() != null && registrationDTO.isUpdateUINChild()
				&& !isCBEFFNotAvailable(introducerBiometric))
				|| (registrationDTO.getSelectionListDTO() == null && !isCBEFFNotAvailable(introducerBiometric)));
	}

	private List<ValuesDTO> buildDemoComboValues(String platformLanguageCode, String localLanguageCode,
			ComboBox<LocationDto> comboField, ComboBox<LocationDto> comboFieldLocalLang, boolean isComboValueRequired) {
		return isComboValueRequired ? null
				: buildValues(platformLanguageCode, localLanguageCode, comboField.getValue().getName(),
						comboFieldLocalLang.getValue().getName());
	}

	private List<ValuesDTO> buildDemoTextValues(String platformLanguageCode, String localLanguageCode,
			TextField demoField, TextField demoFieldLocalLang, boolean isTextFieldNotRequired) {
		return isTextFieldNotRequired ? null
				: buildValues(platformLanguageCode, localLanguageCode, demoField.getText(),
						demoFieldLocalLang.getText());
	}

	@SuppressWarnings("unchecked")
	private List<ValuesDTO> buildValues(String platformLanguageCode, String localLanguageCode, String valueInAppLang,
			String valueInLocalLang) {
		List<ValuesDTO> valuesDTO = (List<ValuesDTO>) Builder.build(LinkedList.class)
				.with(values -> values
						.add(Builder.build(ValuesDTO.class).with(value -> value.setLanguage(platformLanguageCode))
								.with(value -> value.setValue(valueInAppLang)).get()))
				.get();

		if (localLanguageCode != null && !platformLanguageCode.equalsIgnoreCase(localLanguageCode))
			valuesDTO.add(Builder.build(ValuesDTO.class).with(value -> value.setLanguage(localLanguageCode))
					.with(value -> value.setValue(valueInLocalLang)).get());

		return valuesDTO;
	}

	private String buildDemoTextValue(TextField demoField, boolean isTextFieldNotRequired) {
		return isTextFieldNotRequired ? null : demoField.getText();
	}

	private boolean isNameNotRequired(TextField fullName, boolean isNameNotUpdated) {
		return isTextFieldNotRequired(fullName) || isNameNotUpdated;
	}

	private boolean isTextFieldNotRequired(TextField demoField) {
		return demoField.isDisabled() || demoField.getText().isEmpty();
	}

	private boolean postalCodeFieldValidation(TextField demoField) {
		return demoField.getText().isEmpty();
	}

	private boolean isComboBoxValueNotRequired(ComboBox<?> demoComboBox) {
		return demoComboBox.isDisable() || demoComboBox.getValue() == null;
	}

	private BigInteger buildDemoObjectValue(TextField demoField, boolean isTextFieldNotRequired) {
		return isTextFieldNotRequired ? null : new BigInteger(demoField.getText());
	}

	private DocumentDetailsDTO getDocumentFromMap(String documentCategory, Map<String, DocumentDetailsDTO> documents,
			boolean isDocumentsMapEmpty) {
		return isDocumentsMapEmpty ? null : documents.get(documentCategory);
	}

	private CBEFFFilePropertiesDTO buildCBEFFDTO(boolean isCBEFFNotRequired, String cbeffFileName) {
		return isCBEFFNotRequired ? null
				: (CBEFFFilePropertiesDTO) Builder.build(CBEFFFilePropertiesDTO.class)
						.with(cbeffProperties -> cbeffProperties.setFormat(RegistrationConstants.CBEFF_FILE_FORMAT))
						.with(cbeffProperty -> cbeffProperty.setValue(cbeffFileName
								.replace(RegistrationConstants.XML_FILE_FORMAT, RegistrationConstants.EMPTY)))
						.with(cbeffProperty -> cbeffProperty.setVersion(1.0)).get();
	}

	private boolean isCBEFFNotAvailable(BiometricInfoDTO personBiometric) {
		return personBiometric.getFingerprintDetailsDTO().isEmpty() && personBiometric.getIrisDetailsDTO().isEmpty()
				&& personBiometric.getFace().getFace() == null;
	}

	/**
	 * Method will be called for uin Update
	 *
	 */
	public void uinUpdate() {
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {

			clearAllValues();
			documentScanController.getBioExceptionToggleLabel1().setLayoutX(0);
			SessionContext.userMap().put(RegistrationConstants.TOGGLE_BIO_METRIC_EXCEPTION, false);

			keyboardNode.setDisable(false);
			keyboardNode.setManaged(false);
			RegistrationConstants.CNI_MANDATORY = String.valueOf(true);

			copyPrevious.setDisable(true);
			autoFillBtn.setVisible(false);
			registrationNavlabel.setText(applicationLabelBundle.getString("uinUpdateNavLbl"));
			parentFlowPane.setDisable(false);
			fetchBtn.setVisible(false);
			parentRegIdLabel.setText(applicationLabelBundle.getString("uinIdUinUpdate"));
			preRegistrationLabel.setText(RegistrationConstants.UIN_LABEL);
			updateUinId.setVisible(true);
			updateUinId.setDisable(true);
			preRegistrationId.setVisible(false);
			getRegistrationDTOFromSession().getRegistrationMetaDataDTO()
					.setUin(getRegistrationDTOFromSession().getSelectionListDTO().getUinId());
			updateUinId.setText(getRegistrationDTOFromSession().getSelectionListDTO().getUinId());
			applicationFullName.setDisable(false);
			fullNameKeyboardImage.setDisable(false);
			localFullName.setDisable(false);
			applicationAge.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAge());
			applicationGender.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isGender());

			applicationAddressLine1.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			addressLine1KeyboardImage.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			addressLine2KeyboardImage.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			addressLine3KeyboardImage.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			applicationAddressLine2.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			applicationAddressLine3.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			localAddressLine1.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			localAddressLine2.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			localAddressLine3.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			applicationRegion.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			applicationProvince.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			applicationCity.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			applicationPostalCode.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			applicationlocalAdminAuthority
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			applicationMobileNumber.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isPhone());
			applicationemailIdPane.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isEmail());

			residenceParentpane.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isForeigner());

			applicationCniOrPinNumberPane
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isCnieNumber());

			parentDetailPane
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails());
			parentDetailPane
					.setVisible(getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails());
			parentDetailPane
					.setManaged(getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails());
			parentNameKeyboardImage
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails());

			isChild = getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails();

			if (SessionContext.map().get(RegistrationConstants.IS_Child) != null) {
				isChild = (boolean) SessionContext.map().get(RegistrationConstants.IS_Child);
				parentDetailPane.setDisable(!isChild);
				parentDetailPane.setVisible(isChild);
				parentNameKeyboardImage.setDisable(!isChild);
			}

			enableParentUIN();
		}
	}

	private void enableParentUIN() {
		if (isChild || (null != ageField.getText() && !ageField.getText().isEmpty()
				&& Integer.parseInt(ageField.getText()) <= 5)) {

			applicationUinIdPane.setDisable(false);
			applicationRidPane.setDisable(true);
			applicationRidPane.setVisible(false);
			applicationRidPane.setManaged(false);
			ridOrUinToggle.setVisible(false);
			ridOrUinToggle.setManaged(false);
			localRidPane.setDisable(true);
			localRidPane.setVisible(false);
			localRidPane.setManaged(false);
			localRidOrUinToggle.setVisible(false);
			localRidOrUinToggle.setManaged(false);

			parentDetailsHbox.setAlignment(Pos.CENTER_LEFT);
			localParentDetailsHbox.setAlignment(Pos.CENTER_LEFT);
		}
	}

	/**
	 * This method is to prepopulate all the values for edit operation
	 */
	public void prepareEditPageContent() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Preparing the Edit page content");

			RegistrationDTO registrationDTO = getRegistrationDTOFromSession();
			IndividualIdentity individualIdentity = (IndividualIdentity) registrationDTO.getDemographicDTO()
					.getDemographicInfoDTO().getIdentity();

			List<ValuesDTO> fullNameValues = individualIdentity.getFullName();
			if (registrationDTO.getSelectionListDTO() != null && !registrationDTO.isNameNotUpdated()) {

				fullNameValues = registrationDTO.getRegistrationMetaDataDTO().getFullName();
			}
			populateFieldValue(fullName, fullNameLocalLanguage, fullNameValues);
			populateFieldValue(addressLine1, addressLine1LocalLanguage, individualIdentity.getAddressLine1());
			populateFieldValue(addressLine2, addressLine2LocalLanguage, individualIdentity.getAddressLine2());
			populateFieldValue(addressLine3, addressLine3LocalLanguage, individualIdentity.getAddressLine3());
			populateFieldValue(region, regionLocalLanguage, individualIdentity.getRegion());
			populateFieldValue(province, provinceLocalLanguage, individualIdentity.getProvince());
			populateFieldValue(city, cityLocalLanguage, individualIdentity.getCity());

			if (individualIdentity.getResidenceStatus() != null && !individualIdentity.getResidenceStatus().isEmpty()) {
				if (RegistrationConstants.ATTR_FORINGER
						.equalsIgnoreCase(individualIdentity.getResidenceStatus().get(0).getValue())) {
					foreigner(null);
				} else {
					national(null);
				}
			}
			postalCode.setText(individualIdentity.getPostalCode());
			mobileNo.setText(individualIdentity.getPhone());
			emailId.setText(individualIdentity.getEmail());
			ageField.setText(individualIdentity.getAge() == null ? RegistrationConstants.EMPTY
					: String.valueOf(individualIdentity.getAge()));
			cniOrPinNumber.setText(individualIdentity.getCnieNumber());
			postalCodeLocalLanguage.setText(individualIdentity.getPostalCode());
			postalCodeLocalLanguage.setAccessibleHelp(individualIdentity.getPostalCode());
			mobileNoLocalLanguage.setText(individualIdentity.getPhone());
			emailIdLocalLanguage.setText(individualIdentity.getEmail());
			cniOrPinNumberLocalLanguage.setText(individualIdentity.getCnieNumber());
			parentRegId.setText(individualIdentity.getParentOrGuardianRID() == null ? ""
					: String.valueOf(individualIdentity.getParentOrGuardianRID()));
			parentUinId.setText(individualIdentity.getParentOrGuardianUIN() == null ? ""
					: String.valueOf(individualIdentity.getParentOrGuardianUIN()));

			populateFieldValue(genderValue, genderValueLocalLanguage, individualIdentity.getGender());

			if (individualIdentity.getGender() != null && individualIdentity.getGender().size() > 0) {
				if (individualIdentity.getGender().get(0).getValue().equalsIgnoreCase(textMale)
						|| individualIdentity.getGender().get(0).getValue().equalsIgnoreCase(textMaleLocalLanguage)
						|| individualIdentity.getGender().get(0).getValue().equalsIgnoreCase(textMaleCode)) {
					male(null);
				} else {
					female(null);
				}
			}
			if (individualIdentity.getDateOfBirth() != null) {
				String[] date = individualIdentity.getDateOfBirth().split("/");
				if (date.length == 3) {
					yyyy.setText(date[0]);
					mm.setText(date[1]);
					dd.setText(date[2]);
				}
			}

			populateFieldValue(localAdminAuthority, localAdminAuthorityLocalLanguage,
					individualIdentity.getLocalAdministrativeAuthority());

			if (SessionContext.map().get(RegistrationConstants.IS_Child) != null) {

				boolean isChild = (boolean) SessionContext.map().get(RegistrationConstants.IS_Child);
				parentDetailPane.setDisable(!isChild);
				parentDetailPane.setVisible(isChild);
			}
			preRegistrationId.setText(registrationDTO.getPreRegistrationId());

		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}

	}

	/**
	 * Method to populate the local field value
	 *
	 */
	private void populateFieldValue(Node nodeForPlatformLang, Node nodeForLocalLang, List<ValuesDTO> fieldValues) {
		if (fieldValues != null) {
			String platformLanguageCode = applicationContext.getApplicationLanguage();
			String localLanguageCode = applicationContext.getLocalLanguage();
			String valueInPlatformLang = RegistrationConstants.EMPTY;
			String valueinLocalLang = RegistrationConstants.EMPTY;

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
				fxUtils.selectComboBoxValue((ComboBox<?>) nodeForPlatformLang, valueInPlatformLang);
			}
		}
	}

	/**
	 * Method to fetch the pre-Registration details
	 */
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
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PRE_REG_ID_NOT_VALID);
				LOGGER.error("PRID VALIDATION FAILED", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						invalidIDException.getMessage() + ExceptionUtils.getStackTrace(invalidIDException));
				return;
			}
		}

		auditFactory.audit(AuditEvent.REG_DEMO_PRE_REG_DATA_FETCH, Components.REG_DEMO_DETAILS, SessionContext.userId(),
				AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

		registrationController.createRegistrationDTOObject(RegistrationConstants.PACKET_TYPE_NEW);
		documentScanController.clearDocSection();

		ResponseDTO responseDTO = preRegistrationDataSyncService.getPreRegistration(preRegId);

		SuccessResponseDTO successResponseDTO = responseDTO.getSuccessResponseDTO();
		List<ErrorResponseDTO> errorResponseDTOList = responseDTO.getErrorResponseDTOs();

		if (successResponseDTO != null && successResponseDTO.getOtherAttributes() != null
				&& successResponseDTO.getOtherAttributes().containsKey(RegistrationConstants.REGISTRATION_DTO)) {
			SessionContext.map().put(RegistrationConstants.REGISTRATION_DATA,
					successResponseDTO.getOtherAttributes().get(RegistrationConstants.REGISTRATION_DTO));
			prepareEditPageContent();

		} else if (errorResponseDTOList != null && !errorResponseDTOList.isEmpty()) {
			generateAlertLanguageSpecific(RegistrationConstants.ERROR, errorResponseDTOList.get(0).getMessage());
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
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading address from previous entry");

			if (SessionContext.map().get(RegistrationConstants.ADDRESS_KEY) == null) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PREVIOUS_ADDRESS);
				LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Previous registration details not available.");

			} else {
				LocationDTO locationDto = ((AddressDTO) SessionContext.map().get(RegistrationConstants.ADDRESS_KEY))
						.getLocationDTO();
				if (locationDto.getRegion() != null) {
					fxUtils.selectComboBoxValue(region, locationDto.getRegion());
					retrieveAndPopulateLocationByHierarchy(region, province, provinceLocalLanguage);
				}
				if (locationDto.getProvince() != null) {
					fxUtils.selectComboBoxValue(province, locationDto.getProvince());
					retrieveAndPopulateLocationByHierarchy(province, city, cityLocalLanguage);
				}
				if (locationDto.getCity() != null) {
					fxUtils.selectComboBoxValue(city, locationDto.getCity());
					retrieveAndPopulateLocationByHierarchy(city, localAdminAuthority, localAdminAuthorityLocalLanguage);
				}
				if (locationDto.getLocalAdministrativeAuthority() != null) {
					fxUtils.selectComboBoxValue(localAdminAuthority, locationDto.getLocalAdministrativeAuthority());
				}

				if (locationDto.getPostalCode() != null) {
					postalCode.setText(locationDto.getPostalCode());
				}
				LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Loaded address from previous entry");
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING ADDRESS FROM PREVIOUS ENTRY FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
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
			keyboardNode.setLayoutX(fullNameGridPane.getWidth());
			Node node = (Node) event.getSource();

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE1)) {
				addressLine1LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(470.00);
				keyboardNode.setManaged(true);
			}

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE2)) {
				addressLine2LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(555.00);
				keyboardNode.setManaged(true);

			}

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE3)) {
				addressLine3LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(630.00);
				keyboardNode.setManaged(true);

			}

			if (node.getId().equals(RegistrationConstants.FULL_NAME)) {
				fullNameLocalLanguage.requestFocus();
				keyboardNode.setLayoutY(200.00);
				keyboardNode.setManaged(true);

			}

			if (node.getId().equals(RegistrationConstants.PARENT_NAME)) {
				parentNameLocalLanguage.requestFocus();
				keyboardNode.setLayoutY(1110.00);
				keyboardNode.setManaged(true);

			}
			keyboardNode.setVisible(!keyboardNode.isVisible());
			keyboardNode.visibleProperty().addListener((abs, old, newValue) -> {
				if (old) {
					keyboardPane.maxHeight(parentFlowPane.getHeight());
					fullNameLocalLanguage.requestFocus();
				} 
			});

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - SETTING FOCUS ON LOCAL FIELD FAILED", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	public void clickMe() {
		validation.setValidationMessage();
		fullName.setText("أيوب توفيق");
		int age = 27;
		ageField.setText("" + age);
		addressLine1.setText("٣٠ ر أم عربية");
		addressLine2.setText("عربية");
		if (!region.getItems().isEmpty()) {
			region.getSelectionModel().select(0);
			retrieveAndPopulateLocationByHierarchy(region, province, provinceLocalLanguage);
		}
		if (!province.getItems().isEmpty()) {
			province.getSelectionModel().select(0);
			retrieveAndPopulateLocationByHierarchy(province, city, cityLocalLanguage);
		}
		if (!city.getItems().isEmpty()) {
			city.getSelectionModel().select(0);
			retrieveAndPopulateLocationByHierarchy(city, localAdminAuthority, localAdminAuthorityLocalLanguage);
		}
		if (!localAdminAuthority.getItems().isEmpty()) {
			localAdminAuthority.getSelectionModel().select(0);
		}
		mobileNo.setText("9965625706");
		emailId.setText("ayoub.toufiq@gmail.com");
		cniOrPinNumber.setText("4545343123");
		registrationController.displayValidationMessage(validation.getValidationMessage().toString());
	}

	/**
	 * Method to go back to previous page
	 */
	@FXML
	private void back() {
		try {
			if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				Parent uinUpdate = BaseController.load(getClass().getResource(RegistrationConstants.UIN_UPDATE));
				getScene(uinUpdate);
			} else {
				goToHomePageFromRegistration();
			}
		} catch (IOException exception) {
			LOGGER.error("COULD NOT LOAD HOME PAGE", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
		}
	}

	/**
	 * Method to go back to next page
	 */
	@FXML
	private void next() throws InvalidApplicantArgumentException, ParseException {

		if (getRegistrationDTOFromSession().getSelectionListDTO() != null
				&& parentUinId.getText().equals(getRegistrationDTOFromSession().getSelectionListDTO().getUinId())) {
			generateAlert(RegistrationConstants.ERROR,
					RegistrationUIConstants.UPDATE_UIN_INDIVIDUAL_AND_PARENT_SAME_UIN_ALERT);
		} else {
			if (validateThisPane()) {
				disableTheMessages();
				if (dd != null && mm != null && yyyy != null && dd.getText().matches(RegistrationConstants.NUMBER_REGEX)
						&& mm.getText().matches(RegistrationConstants.NUMBER_REGEX)
						&& yyyy.getText().matches(RegistrationConstants.NUMBER_REGEX)) {

					LocalDate currentYear = LocalDate.of(Integer.parseInt(yyyy.getText()),
							Integer.parseInt(mm.getText()), Integer.parseInt(dd.getText()));
					dateOfBirth = Date.from(currentYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
				}
				saveDetail();

				/*
				 * SessionContext.map().put("demographicDetail", false);
				 * SessionContext.map().put("documentScan", true);
				 */

				documentScanController.populateDocumentCategories();

				auditFactory.audit(AuditEvent.REG_DEMO_NEXT, Components.REG_DEMO_DETAILS, SessionContext.userId(),
						AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

				// Set Exception Photo Type Description
				faceCaptureController
						.setExceptionFaceDescriptionText(getRegistrationDTOFromSession().isUpdateUINNonBiometric()
								|| (SessionContext.map().get(RegistrationConstants.IS_Child) != null
										&& (boolean) SessionContext.map().get(RegistrationConstants.IS_Child)));

				if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
					SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DEMOGRAPHICDETAIL, false);
					if (RegistrationConstants.ENABLE
							.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.DOC_DISABLE_FLAG))
							|| (RegistrationConstants.ENABLE.equalsIgnoreCase(
									getValueFromApplicationContext(RegistrationConstants.FINGERPRINT_DISABLE_FLAG))
									|| RegistrationConstants.ENABLE.equalsIgnoreCase(
											getValueFromApplicationContext(RegistrationConstants.IRIS_DISABLE_FLAG)))) {
						SessionContext.map().put(RegistrationConstants.UIN_UPDATE_DOCUMENTSCAN, true);
					} else {
						updateUINMethodFlow();
					}
					registrationController.showUINUpdateCurrentPage();
				} else {
					registrationController.showCurrentPage(RegistrationConstants.DEMOGRAPHIC_DETAIL,
							getPageDetails(RegistrationConstants.DEMOGRAPHIC_DETAIL, RegistrationConstants.NEXT));
				}
			}
		}
	}
	/**
	 * Disables the messages once the pane is validated
	 */
	private void disableTheMessages() {
		fullNameMessage.setVisible(false);
		fullNameLocalLanguageMessage.setVisible(false);
		dobMessage.setVisible(false);
		addressLine1Message.setVisible(false);
		addressLine1LocalLanguageMessage.setVisible(false);
		addressLine2Message.setVisible(false);
		addressLine2LocalLanguageMessage.setVisible(false);
		addressLine3Message.setVisible(false);
		addressLine3LocalLanguageMessage.setVisible(false);
		postalCodeMessage.setVisible(false);
		postalCodeLocalLanguageMessage.setVisible(false);
		mobileNoMessage.setVisible(false);
		mobileNoLocalLanguageMessage.setVisible(false);
		emailIdMessage.setVisible(false);
		emailIdLocalLanguageMessage.setVisible(false);
		cniOrPinNumberMessage.setVisible(false);
		cniOrPinNumberLocalLanguageMessage.setVisible(false);
		parentNameMessage.setVisible(false);
		parentNameLocalLanguageMessage.setVisible(false);
		parentRegIdMessage.setVisible(false);
		parentUinIdMessage.setVisible(false);
	}

	/**
	 * Method to validate the details entered
	 */
	public boolean validateThisPane() {
		boolean isValid = true;
		isValid = registrationController.validateDemographicPane(parentFlowPane);
		if (isValid && !applicationAge.isDisable()) {
			isValid = validateDateOfBirth(isValid);
			if (ageField.getText().length() > 0 && Integer.parseInt(ageField.getText()) > maxAge) {
				dobMessage.setText(RegistrationUIConstants.INVALID_AGE + maxAge);
				dobMessage.setVisible(true);
				isValid = false;
			}
		}

		if (isChild
				&& RegistrationConstants.DISABLE.equalsIgnoreCase(
						getValueFromApplicationContext(RegistrationConstants.FINGERPRINT_DISABLE_FLAG))
				&& RegistrationConstants.DISABLE
						.equalsIgnoreCase(getValueFromApplicationContext(RegistrationConstants.IRIS_DISABLE_FLAG))) {
			isValid = false;
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.PARENT_BIO_MSG);
		}
		if (isValid)
			isValid = validation.validateUinOrRid(parentFlowPane, parentUinId, parentRegId, isChild, uinValidator,
					ridValidator);

		return isValid;

	}

	private boolean validateDateOfBirth(boolean isValid) {
		int age;
		if (getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
				.equals(RegistrationConstants.PACKET_TYPE_LOST) && dd.getText().isEmpty() && mm.getText().isEmpty()
				&& yyyy.getText().isEmpty()) {
			return true;
		}
		LocalDate date = null;
		try {
			date = LocalDate.of(Integer.parseInt(yyyy.getText()), Integer.parseInt(mm.getText()),
					Integer.parseInt(dd.getText()));
		} catch (NumberFormatException | DateTimeException exception) {
			if (exception.getMessage().contains("Invalid value for DayOfMonth")) {
				dobMessage.setText(RegistrationUIConstants.INVALID_DATE);
			} else if (exception.getMessage().contains("Invalid value for MonthOfYear")) {
				dobMessage.setText(RegistrationUIConstants.INVALID_MONTH);
			} else {
				dobMessage.setText(RegistrationUIConstants.INVALID_YEAR);
			}
			if (dd.getText().isEmpty()) {
				dobMessage.setText(dd.getPromptText() + " " + RegistrationUIConstants.REG_LGN_001);
			} else if (mm.getText().isEmpty()) {
				dobMessage.setText(mm.getPromptText() + " " + RegistrationUIConstants.REG_LGN_001);
			} else if (yyyy.getText().isEmpty()) {
				dobMessage.setText(yyyy.getPromptText() + " " + RegistrationUIConstants.REG_LGN_001);
			}
			dobMessage.setVisible(true);
			return false;
		}
		LocalDate localDate = LocalDate.now();

		if (localDate.compareTo(date) >= 0) {

			age = Period.between(date, localDate).getYears();
			if (age <= maxAge) {
				ageField.setText(age + "");
				ageFieldLocalLanguage.setText(age + "");
			} else {
				dobMessage.setText(RegistrationUIConstants.INVALID_AGE + maxAge);
				dobMessage.setVisible(true);
				isValid = false;
			}

		} else {
			ageField.clear();
			ageFieldLocalLanguage.clear();
			dobMessage.setText(RegistrationUIConstants.FUTURE_DOB);
			dobMessage.setVisible(true);
			isValid = false;

		}
		return isValid;
	}

	/**
	 * Fetching the combobox details
	 */
	@SuppressWarnings("unchecked")
	private <T> void renderComboBoxes() {
		LOGGER.info("REGISTRATION - INDIVIDUAL_REGISTRATION - RENDER_COMBOBOXES", RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME, "Rendering of comboboxes started");

		try {
			StringConverter<T> uiRenderForComboBox = fxUtils.getStringConverterForComboBox();

			region.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			province.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			city.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			localAdminAuthority.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			regionLocalLanguage.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			provinceLocalLanguage.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			cityLocalLanguage.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			localAdminAuthorityLocalLanguage.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.REGISTRATION_CONTROLLER,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException), runtimeException);
		}
		LOGGER.info("REGISTRATION - INDIVIDUAL_REGISTRATION - RENDER_COMBOBOXES", RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME, "Rendering of comboboxes ended");
	}

	/**
	 * Retrieving and populating the location by hierarchy
	 */
	private void retrieveAndPopulateLocationByHierarchy(ComboBox<LocationDto> srcLocationHierarchy,
			ComboBox<LocationDto> destLocationHierarchy, ComboBox<LocationDto> destLocationHierarchyInLocal) {
		LOGGER.info("REGISTRATION - INDIVIDUAL_REGISTRATION - RETRIEVE_AND_POPULATE_LOCATION_BY_HIERARCHY",
				RegistrationConstants.APPLICATION_ID, RegistrationConstants.APPLICATION_NAME,
				"Retrieving and populating of location by selected hirerachy started");

		try {
			LocationDto selectedLocationHierarchy = srcLocationHierarchy.getSelectionModel().getSelectedItem();
			destLocationHierarchy.getItems().clear();
			destLocationHierarchyInLocal.getItems().clear();

			if (selectedLocationHierarchy != null) {
				destLocationHierarchy.getItems().addAll(masterSync.findProvianceByHierarchyCode(
						selectedLocationHierarchy.getCode(), selectedLocationHierarchy.getLangCode()));
				destLocationHierarchyInLocal.getItems().addAll(masterSync.findProvianceByHierarchyCode(
						selectedLocationHierarchy.getCode(), ApplicationContext.localLanguage()));
			}
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.REGISTRATION_CONTROLLER,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException), runtimeException);
		}
		LOGGER.info("REGISTRATION - INDIVIDUAL_REGISTRATION - RETRIEVE_AND_POPULATE_LOCATION_BY_HIERARCHY",
				RegistrationConstants.APPLICATION_ID, RegistrationConstants.APPLICATION_NAME,
				"Retrieving and populating of location by selected hirerachy ended");
	}

	protected String getSelectedNationalityCode() {
		return residence.getText() != null ? residence.getId() : null;

	}

	private void updateBioPageFlow(String flag, String pageId) {
		if (RegistrationConstants.DISABLE.equalsIgnoreCase(String.valueOf(ApplicationContext.map().get(flag)))) {
			updatePageFlow(pageId, false);
		} else {
			updatePageFlow(pageId, true);
		}
	}
}
