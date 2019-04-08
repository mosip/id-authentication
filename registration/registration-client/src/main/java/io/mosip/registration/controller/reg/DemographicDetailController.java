package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.transliteration.spi.Transliteration;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
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
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.IndividualTypeDto;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.CBEFFFilePropertiesDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.dto.demographic.MoroccoIdentity;
import io.mosip.registration.dto.demographic.ValuesDTO;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.MasterSyncService;
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
	private Label uinIdLocalLanguageLabel;

	@FXML
	private Label languageLabelLocalLanguage;

	@FXML
	private TextField ageField;

	@FXML
	private TextField ageFieldLocalLanguage;

	@FXML
	private Label toggleLabel1;

	@FXML
	private Label mmLabel;
	@FXML
	private Label ddLabel;
	@FXML
	private Label yyyyLabel;

	@FXML
	private Label toggleLabel2;

	@FXML
	private Label toggleLabel1LocalLanguage;

	@FXML
	private Label toggleLabel2LocalLanguage;

	@FXML
	private GridPane parentDetailPane;

	@FXML
	private ScrollPane demoScrollPane;

	private SimpleBooleanProperty switchedOn;

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
	private Label uinIdLabel;
	
	@FXML
	private TextField addressLine3LocalLanguage;

	@FXML
	private Label addressLine3LocalLanguageLabel;

	@FXML
	private Label addressLine3LocalLanguageMessage;

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
	private TextField uinIdLocalLanguage;

	@FXML
	private TextField parentNameLocalLanguage;

	@FXML
	private TextField parentName;

	@FXML
	private Label parentNameMessage;

	@FXML
	private Label parentNameLabel;

	@FXML
	private Label ageFieldMessage;

	@FXML
	private Label ageFieldLabel;

	@FXML
	private Label ageFieldLocalLanguageMessage;

	@FXML
	private TextField uinId;

	public boolean isChild;

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
	MasterSyncService masterSync;

	@Autowired
	HomeController homeController;

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
	@Autowired
	private JsonValidator jsonValidator;

	private FXUtils fxUtils;
	private Date dateOfBirth;
	ResourceBundle localLabelBundle;
	private int minAge;
	private int maxAge;

	@Autowired
	private MasterSyncService masterSyncService;
	@FXML
	private GridPane borderToDo;
	@FXML
	private Label registrationNavlabel;
	@FXML
	private AnchorPane keyboardPane;
	private boolean lostUIN = false;
	ResourceBundle applicationLabelBundle;
	private String textMale;
	private String textFemale;
	private String textMaleLocalLanguage;
	private String textFemaleLocalLanguage;
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize()
	 */
	@FXML
	private void initialize() {

		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");
		try {
			lostUIN = false;
			changeOrientation(NodeOrientation.RIGHT_TO_LEFT);
			fxUtils = FXUtils.getInstance();
			fxUtils.setTransliteration(transliteration);
			SessionContext.map().put(RegistrationConstants.IS_CONSOLIDATED, RegistrationConstants.DISABLE);
			switchedOn = new SimpleBooleanProperty(true);
			switchedOn.setValue(true);
			isChild = false;
			toggleFunction();
			ageFieldValidations();
			listenerOnFields();
			loadLocalLanguageFields();
			loadKeyboard();
			ageField.setDisable(true);
			ageFieldLocalLanguage.setDisable(true);
			renderComboBoxes();
			addRegions();
			minAge = Integer.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.MIN_AGE)));
			maxAge = Integer.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.MAX_AGE)));
			applicationLabelBundle = ApplicationContext.getInstance().getApplicationLanguageBundle();
			localLabelBundle = ApplicationContext.getInstance().getLocalLanguageProperty();
			List<IndividualTypeDto> applicantType = masterSyncService.getIndividualType(
					RegistrationConstants.ATTR_NON_FORINGER, ApplicationContext.applicationLanguage());
			residence.setText(applicantType.get(0).getName());
			residence.setId(applicantType.get(0).getCode());
			List<IndividualTypeDto> applicantTypeLocal = masterSyncService
					.getIndividualType(RegistrationConstants.ATTR_NON_FORINGER, ApplicationContext.localLanguage());
			residenceLocalLanguage.setText(applicantTypeLocal.get(0).getName());
			genderSettings();
			disableLocalFields();
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_DEMOGRAPHIC_PAGE);

		}
	}

	private void genderSettings() {
		textMale=applicationLabelBundle.getString("male");
		textFemale=applicationLabelBundle.getString("female");
		textMaleLocalLanguage=localLabelBundle.getString("male");
		textFemaleLocalLanguage=localLabelBundle.getString("female");
		male(null);
	}

	/**
	 * setting the registration navigation label to lost uin
	 */
	protected void lostUIN() {
		lostUIN = true;
		registrationNavlabel.setText(ApplicationContext.applicationLanguageBundle().getString("/lostuin"));
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
			parentName.setNodeOrientation(orientation);
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
	}

	/**
	 * Toggle functionality between age field and date picker.
	 */
	private void toggleFunction() {
		try {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"Entering into toggle function for toggle label 1 and toggle level 2");

			switchedOn.addListener((observableValue, oldValue, newValue) -> {
				if (newValue) {
					toggleLabel1.setLayoutX(0);
					toggleLabel1LocalLanguage.setLayoutX(0);
					ageField.clear();
					ageFieldLocalLanguage.clear();
					parentDetailPane.setVisible(false);
					ageField.setDisable(true);
					dob.setDisable(false);
					dobLocallanguage.setDisable(false);
				} else {
					toggleLabel1.setLayoutX(30);
					toggleLabel1LocalLanguage.setLayoutX(30);
					ageField.clear();
					parentDetailPane.setVisible(false);
					ageField.setDisable(false);
					ageFieldLocalLanguage.clear();
					dob.setDisable(true);
					dobLocallanguage.setDisable(true);
				}

				dd.clear();
				mm.clear();
				yyyy.clear();

				ddLocalLanguage.clear();
				mmLocalLanguage.clear();
				yyyyLocalLanguage.clear();

				ageFieldMessage.setVisible(false);
				ageFieldLabel.setVisible(false);
				ageFieldLocalLanguageLabel.setVisible(false);
				ageFieldLocalLanguageMessage.setVisible(false);
				dobMessage.setVisible(false);
				ddLabel.setVisible(false);
				mmLabel.setVisible(false);
				yyyyLabel.setVisible(false);
				ddLocalLanguageLabel.setVisible(false);
				mmLocalLanguageLabel.setVisible(false);
				yyyyLocalLanguageLabel.setVisible(false);
			});

			toggleLabel1.setOnMouseClicked(event -> switchedOn.set(!switchedOn.get()));
			toggleLabel2.setOnMouseClicked(event -> switchedOn.set(!switchedOn.get()));
			toggleLabel1LocalLanguage.setOnMouseClicked(event -> switchedOn.set(!switchedOn.get()));
			toggleLabel2LocalLanguage.setOnMouseClicked(event -> switchedOn.set(!switchedOn.get()));

			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"Exiting the toggle function for toggle label 1 and toggle level 2");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - TOGGLING OF DOB AND AGE FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * method action when national button is pressed
	 * @param ActionEvent
	 *          the action event
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
	 * @param ActionEvent
	 *          the action event
	 */
	@FXML
	private void male(ActionEvent event) {
		genderValue.setText(textMale);
		genderValueLocalLanguage.setText(textMaleLocalLanguage);
		male.getStyleClass().clear();
		female.getStyleClass().clear();
		maleLocalLanguage.getStyleClass().clear();
		femaleLocalLanguage.getStyleClass().clear();
		maleLocalLanguage.getStyleClass().addAll("selectedResidence", "button");
		femaleLocalLanguage.getStyleClass().addAll("residence", "button");
		male.getStyleClass().addAll("selectedResidence", "button");
		female.getStyleClass().addAll("residence", "button");
	}

	/**
	 * method action when foriegner button is pressed
	 * @param ActionEvent
	 *          the action event
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
	 * @param ActionEvent
	 *          the action event
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
	 * To restrict the user not to enter any values other than integer values.
	 */

	private void ageFieldValidations() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the age given by age field");
			fxUtils.focusUnfocusListener(dobParentPane, ageField, ageFieldLocalLanguage);
			fxUtils.onTypeFocusUnfocusListener(dobParentPane, ageFieldLocalLanguage);
			ageField.textProperty().addListener((obsValue, oldValue, newValue) -> {
			int age = 0;
				if (newValue.matches("\\d+")) {
					if (Integer.parseInt(ageField.getText()) > maxAge) {
						ageField.setText(oldValue);
						generateAlert(RegistrationConstants.ERROR,
								RegistrationUIConstants.MAX_AGE_WARNING + " " + maxAge);
					} else {
						age = Integer.parseInt(ageField.getText());
						LocalDate currentYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
						dateOfBirth = Date
								.from(currentYear.minusYears(age).atStartOfDay(ZoneId.systemDefault()).toInstant());
						if (age <= minAge) {
							parentDetailPane.setVisible(true);
							parentDetailPane.setDisable(false);
							parentName.clear();
							uinId.clear();
							isChild = true;
							validation.setChild(isChild);
						} else {
							isChild = false;
							validation.setChild(isChild);
							parentDetailPane.setVisible(false);
							parentDetailPane.setDisable(true);
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
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * Listening on the fields for any operation
	 */
	private void listenerOnFields() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Populating the local language fields");
			boolean hasToBeTransliterated = true;
			fxUtils.validateOnFocusOut(parentFlowPane, fullName, validation, fullNameLocalLanguage, hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, addressLine1, validation, addressLine1LocalLanguage, hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, addressLine2, validation, addressLine2LocalLanguage, hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, addressLine3, validation, addressLine3LocalLanguage, hasToBeTransliterated);
			fxUtils.validateOnType(parentFlowPane, parentName, validation, parentNameLocalLanguage, hasToBeTransliterated);
			fxUtils.validateOnType(parentFlowPane, uinId, validation, uinIdLocalLanguage, !hasToBeTransliterated);

			fxUtils.validateOnType(parentFlowPane, fullNameLocalLanguage, validation);

			fxUtils.validateOnFocusOut(parentFlowPane, mobileNo, validation, mobileNoLocalLanguage, !hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, postalCode, validation, postalCodeLocalLanguage, !hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, emailId, validation, emailIdLocalLanguage, !hasToBeTransliterated);
			fxUtils.validateOnFocusOut(parentFlowPane, cniOrPinNumber, validation, cniOrPinNumberLocalLanguage, !hasToBeTransliterated);

			fxUtils.populateLocalComboBox(parentFlowPane, city, cityLocalLanguage);
			fxUtils.populateLocalComboBox(parentFlowPane, region, regionLocalLanguage);
			fxUtils.populateLocalComboBox(parentFlowPane, province, provinceLocalLanguage);
			fxUtils.populateLocalComboBox(parentFlowPane, localAdminAuthority, localAdminAuthorityLocalLanguage);

			dateValidation.validateDate(parentFlowPane, dd, mm, yyyy, validation, fxUtils, ddLocalLanguage);
			dateValidation.validateDate(parentFlowPane, ddLocalLanguage, mmLocalLanguage, yyyyLocalLanguage, validation,
					fxUtils, null);
			dateValidation.validateMonth(parentFlowPane, dd, mm, yyyy, validation, fxUtils, mmLocalLanguage);
			dateValidation.validateMonth(parentFlowPane, ddLocalLanguage, mmLocalLanguage, yyyyLocalLanguage,
					validation, fxUtils, null);
			dateValidation.validateYear(parentFlowPane, dd, mm, yyyy, validation, fxUtils, yyyyLocalLanguage);
			dateValidation.validateYear(parentFlowPane, ddLocalLanguage, mmLocalLanguage, yyyyLocalLanguage, validation,
					fxUtils, null);
			fxUtils.dobListener(yyyy, ageField, "\\d{4}");
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
			uinIdLocalLanguageLabel.setText(localProperties.getString("uinId"));
			uinIdLocalLanguage.setPromptText(localProperties.getString("uinId"));
			residenceLblLocalLanguage.setText(localProperties.getString("residence"));
			nationalLocalLanguage.setText(localProperties.getString("national"));
			foreignerLocalLanguage.setText(localProperties.getString("foreigner"));
			localAdminAuthorityLocalLanguage.setPromptText(localProperties.getString("select"));
			cityLocalLanguage.setPromptText(localProperties.getString("select"));
			regionLocalLanguage.setPromptText(localProperties.getString("select"));
			provinceLocalLanguage.setPromptText(localProperties.getString("select"));
			ddLocalLanguage.setPromptText(localProperties.getString("dd"));
			mmLocalLanguage.setPromptText(localProperties.getString("mm"));
			yyyyLocalLanguage.setPromptText(localProperties.getString("yyyy"));
			languageLabelLocalLanguage.setText(localProperties.getString("language"));
			ddLocalLanguageLabel.setText(localProperties.getString("dd"));
			mmLocalLanguageLabel.setText(localProperties.getString("mm"));
			yyyyLocalLanguageLabel.setText(localProperties.getString("yyyy"));

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
			keyboardPane.getChildren().add(keyboardNode);
			vk.changeControlOfKeyboard(fullNameLocalLanguage);
			vk.changeControlOfKeyboard(addressLine1LocalLanguage);
			vk.changeControlOfKeyboard(addressLine2LocalLanguage);
			vk.changeControlOfKeyboard(addressLine3LocalLanguage);
			vk.changeControlOfKeyboard(parentNameLocalLanguage);
			vk.focusListener(fullNameLocalLanguage, 190.00, keyboardNode);
			vk.focusListener(addressLine1LocalLanguage, 535.00, keyboardNode);
			vk.focusListener(addressLine2LocalLanguage, 625.00, keyboardNode);
			vk.focusListener(addressLine3LocalLanguage, 710.00, keyboardNode);
			vk.focusListener(parentNameLocalLanguage, 1180.00, keyboardNode);
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
			SessionContext.map().put(RegistrationConstants.IS_Child, isChild);
			demographicInfoDTO = buildDemographicInfo();

			try {
				jsonValidator.validateJson(JsonUtils.javaObjectToJsonString(demographicInfoDTO));
			} catch (JsonValidationProcessingException | JsonIOException | JsonSchemaIOException | FileIOException
					| JsonProcessingException exception) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.REG_ID_JSON_VALIDATION_FAILED);
				LOGGER.error("JSON VALIDATION FAILED ", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));
				throw exception;
			} catch (RuntimeException runtimeException) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.REG_ID_JSON_VALIDATION_FAILED);
				LOGGER.error("JSON VALIDATION FAILED ", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
				throw runtimeException;
			}

			if (isChild) {
				osiDataDTO.setIntroducerType(IntroducerType.PARENT.getCode());
			}

			registrationMetaDataDTO.setParentOrGuardianUINOrRID(uinId.getText());

			osiDataDTO.setOperatorID(SessionContext.userContext().getUserId());

			registrationDTO.setPreRegistrationId(preRegistrationId.getText());
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
	@SuppressWarnings("unchecked")
	private DemographicInfoDTO buildDemographicInfo() {

		String platformLanguageCode = ApplicationContext.applicationLanguage();
		String localLanguageCode = ApplicationContext.localLanguage();

		Map<String, DocumentDetailsDTO> documents = getRegistrationDTOFromSession().getDemographicDTO()
				.getApplicantDocumentDTO().getDocuments();
		BiometricInfoDTO applicantBiometric = getRegistrationDTOFromSession().getBiometricDTO()
				.getApplicantBiometricDTO();
		BiometricInfoDTO introducerBiometric = getRegistrationDTOFromSession().getBiometricDTO()
				.getIntroducerBiometricDTO();
		ApplicantDocumentDTO applicantDocumentDTO = getRegistrationDTOFromSession().getDemographicDTO()
				.getApplicantDocumentDTO();

		boolean isParentOrGuradianUINOrRIDAvail = !(uinId.isDisabled() || uinId.getText().isEmpty());
		boolean isParentOrGuardianUIN = isParentOrGuradianUINOrRIDAvail && uinId.getText().length() == Integer
				.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.UIN_LENGTH)));
		boolean isParentOrGuardianRID = isParentOrGuradianUINOrRIDAvail && uinId.getText().length() != Integer
				.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.UIN_LENGTH)));

		return Builder.build(DemographicInfoDTO.class).with(demographicInfo -> demographicInfo.setIdentity(
				(MoroccoIdentity) Builder.build(MoroccoIdentity.class)
						.with(identity -> identity
								.setFullName(fullName.isDisabled() || fullName.getText().isEmpty() ? null
										: (List<ValuesDTO>) Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(fullName.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(fullNameLocalLanguage.getText()))
														.get()))
												.get()))
						.with(identity -> identity
								.setDateOfBirth(applicationAge.isDisable() || (dd.getText().isEmpty() && lostUIN) ? null
										: DateUtils.formatDate(dateOfBirth, "yyyy/MM/dd")))
						.with(identity -> identity
								.setAge(applicationAge.isDisable() || ageField.getText().isEmpty() ? null
										: Integer.parseInt(ageField.getText())))
						.with(identity -> identity
								.setResidenceStatus(residence.isDisabled() || residence.getText().isEmpty() ? null
										: (List<ValuesDTO>) Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(residence.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(residenceLocalLanguage.getText()))
														.get()))
												.get()))
						.with(identity -> identity
								.setGender(genderValue.isDisabled() || genderValue.getText().isEmpty() ? null
										: (List<ValuesDTO>) Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(genderValue.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(genderValueLocalLanguage.getText()))
														.get()))
												.get()))
						.with(identity -> identity
								.setAddressLine1(addressLine1.isDisabled() || addressLine1.getText().isEmpty() ? null
										: (List<ValuesDTO>) Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(addressLine1.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value
																.setValue(addressLine1LocalLanguage.getText()))
														.get()))
												.get()))
						.with(identity -> identity
								.setAddressLine2(addressLine2.isDisabled() || addressLine2.getText().isEmpty() ? null
										: (List<ValuesDTO>) Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(addressLine2.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value
																.setValue(addressLine2LocalLanguage.getText()))
														.get()))
												.get()))
						.with(identity -> identity
								.setAddressLine3(addressLine3.isDisabled() || addressLine3.getText().isEmpty() ? null
										: (List<ValuesDTO>) Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(addressLine3.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value
																.setValue(addressLine3LocalLanguage.getText()))
														.get()))
												.get()))
						.with(identity -> identity.setRegion(region.isDisabled() || region.getValue() == null ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(region.getValue().getName())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(regionLocalLanguage.getValue().getName()))
												.get()))
										.get()))
						.with(identity -> identity.setProvince(province.isDisabled() || province.getValue() == null
								? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(province.getValue().getName())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value
														.setValue(provinceLocalLanguage.getValue().getName()))
												.get()))
										.get()))
						.with(identity -> identity.setCity(city.isDisabled() || city.getValue() == null ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(city.getValue().getName())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(cityLocalLanguage.getValue().getName()))
												.get()))
										.get()))
						.with(identity -> identity
								.setPostalCode(postalCode.isDisabled() || postalCode.getText().isEmpty() ? null
										: postalCode.getText()))
						.with(identity -> identity.setPhone(
								mobileNo.isDisabled() || mobileNo.getText().isEmpty() ? null : mobileNo.getText()))
						.with(identity -> identity.setEmail(
								emailId.isDisabled() || emailId.getText().isEmpty() ? null : emailId.getText()))
						.with(identity -> identity
								.setCnieNumber(cniOrPinNumber.isDisabled() || cniOrPinNumber.getText().isEmpty() ? null
										: cniOrPinNumber.getText()))
						.with(identity -> identity.setLocalAdministrativeAuthority(
								localAdminAuthority.isDisabled() || localAdminAuthority.getValue() == null ? null
										: (List<ValuesDTO>) Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value
																.setValue(localAdminAuthority.getValue().getName()))
														.get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(
																localAdminAuthorityLocalLanguage.getValue().getName()))
														.get()))
												.get()))
						.with(identity -> identity
								.setParentOrGuardianRID(isParentOrGuardianRID ? new BigInteger(uinId.getText()) : null))
						.with(identity -> identity
								.setParentOrGuardianUIN(isParentOrGuardianUIN ? new BigInteger(uinId.getText()) : null))
						.with(identity -> identity.setParentOrGuardianName(
								parentName.isDisabled() || parentName.getText().isEmpty() ? null
										: (List<ValuesDTO>) Builder.build(LinkedList.class)
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(platformLanguageCode))
														.with(value -> value.setValue(parentName.getText())).get()))
												.with(values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value
																.setValue(parentNameLocalLanguage.getText()))
														.get()))
												.get()))
						.with(identity -> identity
								.setProofOfIdentity(documents.isEmpty() ? null : documents.get("POI")))
						.with(identity -> identity.setProofOfAddress(documents.isEmpty() ? null : documents.get("POA")))
						.with(identity -> identity
								.setProofOfRelationship(documents.isEmpty() ? null : documents.get("POR")))
						.with(identity -> identity
								.setProofOfDateOfBirth(documents.isEmpty() ? null : documents.get("POB")))
						.with(identity -> identity.setIdSchemaVersion(1.0))
						.with(identity -> identity.setUin(
								getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getUin() == null ? null
										: new BigInteger(
												getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getUin())))
						.with(identity -> identity.setIndividualBiometrics(applicantBiometric.getFingerprintDetailsDTO()
								.isEmpty() && applicantBiometric.getIrisDetailsDTO().isEmpty()
								&& applicantDocumentDTO.getPhoto() == null
										? null
										: (CBEFFFilePropertiesDTO) Builder.build(CBEFFFilePropertiesDTO.class)
												.with(cbeffProperties -> cbeffProperties
														.setFormat(RegistrationConstants.CBEFF_FILE_FORMAT))
												.with(cbeffProperty -> cbeffProperty
														.setValue(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME
																.replace(RegistrationConstants.XML_FILE_FORMAT,
																		RegistrationConstants.EMPTY)))
												.with(cbeffProperty -> cbeffProperty.setVersion(1.0)).get()))
						.with(identity -> identity.setParentOrGuardianBiometrics(introducerBiometric
								.getFingerprintDetailsDTO().isEmpty()
								&& introducerBiometric.getIrisDetailsDTO().isEmpty()
								&& introducerBiometric.getFaceDetailsDTO().getFace() == null
										? null
										: (CBEFFFilePropertiesDTO) Builder.build(CBEFFFilePropertiesDTO.class)
												.with(cbeffProperties -> cbeffProperties
														.setFormat(RegistrationConstants.CBEFF_FILE_FORMAT))
												.with(cbeffProperty -> cbeffProperty
														.setValue(RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME
																.replace(RegistrationConstants.XML_FILE_FORMAT,
																		RegistrationConstants.EMPTY)))
												.with(cbeffProperty -> cbeffProperty.setVersion(1.0)).get()))
						.get()))
				.get();
	}


	/**
	 * Method will be called for uin Update
	 *
	 */
	public void uinUpdate() {
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {

			keyboardNode.setDisable(false);

			copyPrevious.setDisable(true);
			autoFillBtn.setVisible(false);
			registrationNavlabel.setText(applicationLabelBundle.getString("uinUpdateNavLbl"));
			parentFlowPane.setDisable(false);
			fetchBtn.setVisible(false);
			
			uinIdLabel.setText(applicationLabelBundle.getString("uinIdUinUpdate"));
			uinIdLocalLanguageLabel.setText(localLabelBundle.getString("uinIdUinUpdate"));
			uinId.setPromptText(applicationLabelBundle.getString("uinIdUinUpdate"));
			uinIdLocalLanguage.setPromptText(localLabelBundle.getString("uinIdUinUpdate"));
			
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

			switchedOn.set(true);

			parentDetailPane.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails());
			parentDetailPane.setVisible(getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails());
			parentNameKeyboardImage.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails());

			isChild = getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails();
			
			if (SessionContext.map().get(RegistrationConstants.IS_Child) != null) {
				isChild = (boolean) SessionContext.map().get(RegistrationConstants.IS_Child);
				parentDetailPane.setDisable(!isChild);
				parentDetailPane.setVisible(isChild);
				parentNameKeyboardImage.setDisable(!isChild);
			}

		}
	}

	/**
	 * This method is to prepopulate all the values for edit operation
	 */
	public void prepareEditPageContent() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Preparing the Edit page content");

			MoroccoIdentity moroccoIdentity = (MoroccoIdentity) getRegistrationDTOFromSession().getDemographicDTO()
					.getDemographicInfoDTO().getIdentity();

			populateFieldValue(fullName, fullNameLocalLanguage, moroccoIdentity.getFullName());
			populateFieldValue(addressLine1, addressLine1LocalLanguage, moroccoIdentity.getAddressLine1());
			populateFieldValue(addressLine2, addressLine2LocalLanguage, moroccoIdentity.getAddressLine2());
			populateFieldValue(addressLine3, addressLine3LocalLanguage, moroccoIdentity.getAddressLine3());
			populateFieldValue(region, regionLocalLanguage, moroccoIdentity.getRegion());
			populateFieldValue(province, provinceLocalLanguage, moroccoIdentity.getProvince());
			populateFieldValue(city, cityLocalLanguage, moroccoIdentity.getCity());
			Boolean isSwitchedOn = (Boolean) SessionContext.map().get(RegistrationConstants.DOB_TOGGLE);
			switchedOn.set(isSwitchedOn == null ? false : isSwitchedOn);
			postalCode.setText(moroccoIdentity.getPostalCode() + "");
			mobileNo.setText(moroccoIdentity.getPhone() + "");
			emailId.setText(moroccoIdentity.getEmail() + "");
			if (moroccoIdentity.getAge() != null)
				ageField.setText(moroccoIdentity.getAge() + "");
			cniOrPinNumber.setText(moroccoIdentity.getCnieNumber() + "");
			postalCodeLocalLanguage.setAccessibleHelp(moroccoIdentity.getPostalCode());
			mobileNoLocalLanguage.setText(moroccoIdentity.getPhone());
			emailIdLocalLanguage.setText(moroccoIdentity.getEmail());
			cniOrPinNumberLocalLanguage.setText(moroccoIdentity.getCnieNumber() + "");

			populateFieldValue(genderValue, genderValueLocalLanguage, moroccoIdentity.getGender());
			
			if(moroccoIdentity.getGender()!=null && moroccoIdentity.getGender().size()>0)
			{
				if(moroccoIdentity.getGender().get(0).getValue().equals(textMale)||moroccoIdentity.getGender().get(0).getValue().equals(textMaleLocalLanguage)) {
					male(null);
				}else {
					female(null);
				}
			}
			if (switchedOn.get()) {
				if (moroccoIdentity.getDateOfBirth() != null) {
					String[] date = moroccoIdentity.getDateOfBirth().split("/");
					if (date.length == 3) {
						yyyy.setText(date[0]);
						mm.setText(date[1]);
						dd.setText(date[2]);
					}
				}
			}

			populateFieldValue(localAdminAuthority, localAdminAuthorityLocalLanguage,
					moroccoIdentity.getLocalAdministrativeAuthority());

			if (SessionContext.map().get(RegistrationConstants.IS_Child) != null) {

				boolean isChild = (boolean) SessionContext.map().get(RegistrationConstants.IS_Child);
				parentDetailPane.setDisable(!isChild);
				parentDetailPane.setVisible(isChild);
			}
			if (!(moroccoIdentity.getParentOrGuardianRID() == null && moroccoIdentity.getParentOrGuardianUIN()== null)) {
				populateFieldValue(parentName, parentNameLocalLanguage, moroccoIdentity.getParentOrGuardianName());
				uinId.setText(moroccoIdentity.getParentOrGuardianRID() == null
						? moroccoIdentity.getParentOrGuardianUIN().toString()
						: moroccoIdentity.getParentOrGuardianRID().toString());
			}
			preRegistrationId.setText(getRegistrationDTOFromSession().getPreRegistrationId());

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
			keyboardNode.setLayoutX(500.00);
			Node node = (Node) event.getSource();

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE1)) {
				addressLine1LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(535.00);
			}

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE2)) {
				addressLine2LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(625.00);
			}

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE3)) {
				addressLine3LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(710.00);
			}

			if (node.getId().equals(RegistrationConstants.FULL_NAME)) {
				fullNameLocalLanguage.requestFocus();
				keyboardNode.setLayoutY(190.00);
			}

			if (node.getId().equals(RegistrationConstants.PARENT_NAME)) {
				parentNameLocalLanguage.requestFocus();
				keyboardNode.setLayoutY(1180.00);
			}
			keyboardNode.setVisible(!keyboardNode.isVisible());

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - SETTING FOCUS ON LOCAL FIELED FAILED", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	public void clickMe() {
		SessionContext.map().put(RegistrationConstants.IS_CONSOLIDATED, RegistrationConstants.ENABLE);
		validation.setValidationMessage();
		fullName.setText("أيوب توفيق");
		int age = 27;
		switchedOn.set(false);
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
		postalCode.setText("600111");
		mobileNo.setText("9965625706");
		emailId.setText("ayoub.toufiq@gmail.com");
		cniOrPinNumber.setText("4545343123");
		registrationController.displayValidationMessage(validation.getValidationMessage().toString());
		SessionContext.map().put(RegistrationConstants.IS_CONSOLIDATED, RegistrationConstants.DISABLE);
	}
	

	/**
	 * Method to go back to previous page
	 */
	@FXML
	private void back() {
		try {
			if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
				clearRegistrationData();
				Parent root = BaseController.load(getClass().getResource(RegistrationConstants.HOME_PAGE));
				Parent uinUpdate = BaseController.load(getClass().getResource(RegistrationConstants.UIN_UPDATE));
				homeController.getMainBox().add(uinUpdate, 0, 1);
				getScene(root);
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
				&& uinId.getText().equals(getRegistrationDTOFromSession().getSelectionListDTO().getUinId())) {
			generateAlert(RegistrationConstants.ERROR,
					RegistrationUIConstants.UPDATE_UIN_INDIVIDUAL_AND_PARENT_SAME_UIN_ALERT);
		} else {
			if (validateThisPane()) {
				if (!switchedOn.get()) {

					if (dd.getText().matches("\\d+") && mm.getText().matches("\\d+")
							&& yyyy.getText().matches("\\d+")) {

						LocalDate currentYear = LocalDate.of(Integer.parseInt(yyyy.getText()),
								Integer.parseInt(mm.getText()), Integer.parseInt(dd.getText()));
						dateOfBirth = Date.from(currentYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
					}
				}
				saveDetail();

				/*
				 * SessionContext.map().put("demographicDetail", false);
				 * SessionContext.map().put("documentScan", true);
				 */

				documentScanController.populateDocumentCategories();

				auditFactory.audit(AuditEvent.REG_DEMO_NEXT, Components.REG_DEMO_DETAILS, SessionContext.userId(),
						AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

				if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {
						SessionContext.map().put("demographicDetail", false);
						SessionContext.map().put("documentScan", true);
						registrationController.showUINUpdateCurrentPage();
				} else {
					registrationController.showCurrentPage(RegistrationConstants.DEMOGRAPHIC_DETAIL,
							getPageDetails(RegistrationConstants.DEMOGRAPHIC_DETAIL, RegistrationConstants.NEXT));
				}
			}
		}
	}


	/**
	 * Method to validate the details entered
	 */
	public boolean validateThisPane() {
		boolean isValid = true;
		isValid = registrationController.validateDemographicPane(parentFlowPane);

		if (isValid && switchedOn.get() && !applicationAge.isDisable()) {
			SimpleDateFormat dateOfBirth = new SimpleDateFormat("dd-MM-yyyy");
			dateOfBirth.setLenient(false);
			try {
				dateOfBirth.parse(dd.getText() + "-" + mm.getText() + "-" + yyyy.getText());
			} catch (ParseException exception) {
				if (getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getRegistrationCategory()
						.equals(RegistrationConstants.PACKET_TYPE_LOST)) {
					if (dd.getText().isEmpty() && mm.getText().isEmpty() && yyyy.getText().isEmpty()) {
						isValid = true;
					}
				} else {
					dobMessage.setText(RegistrationUIConstants.INVALID_DATE_OF_BIRTH);
					dobMessage.setVisible(true);
					isValid = false;
				}
			}
		}
		if (isValid)
			isValid = validation.validateUinOrRid(uinId, isChild, uinValidator, ridValidator);
		registrationController.displayValidationMessage(validation.getValidationMessage().toString());

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
}
