package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.CBEFFFilePropertiesDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.dto.demographic.ValuesDTO;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.MasterSyncService;
import io.mosip.registration.service.sync.PreRegistrationDataSyncService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/**
 * Class for Registration Page Controller
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
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationController.class);

	@FXML
	public TextField preRegistrationId;

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
	private Label languageLabelLocalLanguage;

	@FXML
	private TextField ageField;

	@FXML
	private TextField ageFieldLocalLanguage;

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

	private SimpleBooleanProperty toggleSwitchForResidence;

	@FXML
	private ComboBox<GenderDto> gender;

	@FXML
	private ComboBox<GenderDto> genderLocalLanguage;

	@FXML
	private TextField addressLine1;

	@FXML
	private TextField addressLine1LocalLanguage;

	@FXML
	private Label addressLine1LocalLanguageLabel;

	@FXML
	private TextField addressLine2;

	@FXML
	private TextField addressLine2LocalLanguage;

	@FXML
	private Label addressLine2LocalLanguageLabel;

	@FXML
	private TextField addressLine3;

	@FXML
	private TextField addressLine3LocalLanguage;

	@FXML
	private Label addressLine3LocalLanguageLabel;

	@FXML
	private TextField emailId;

	@FXML
	private TextField emailIdLocalLanguage;

	@FXML
	private TextField mobileNo;

	@FXML
	private TextField mobileNoLocalLanguage;

	@FXML
	private ComboBox<LocationDto> region;

	@FXML
	private ComboBox<LocationDto> regionLocalLanguage;

	@FXML
	private ComboBox<LocationDto> city;

	@FXML
	private ComboBox<LocationDto> cityLocalLanguage;

	@FXML
	private ComboBox<LocationDto> province;

	@FXML
	private ComboBox<LocationDto> provinceLocalLanguage;

	@FXML
	private TextField postalCode;

	@FXML
	private TextField postalCodeLocalLanguage;

	@FXML
	private ComboBox<LocationDto> localAdminAuthority;

	@FXML
	private ComboBox<LocationDto> localAdminAuthorityLocalLanguage;

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

	public boolean isChild;

	private Node keyboardNode;

	@FXML
	protected Button autoFillBtn;

	@FXML
	protected Button fetchBtn;

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

	@FXML
	private AnchorPane dateAnchorPane;
	@FXML
	private AnchorPane residentStatusLocalLanguage;
	@FXML
	private AnchorPane residentStatus;
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
	@FXML
	private AnchorPane demoGraphicPane;
	@FXML
	private Button national;
	@FXML
	private AnchorPane demographicDetail;
	@FXML
	private TextField updateUinId;
	@FXML
	private Button foreigner;
	@FXML
	private TextField residence;
	@FXML
	private Button nationalLocalLanguage;
	@FXML
	private Button foreignerLocalLanguage;
	@FXML
	private TextField residenceLocalLanguage;
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
	ResourceBundle applicationLabelBundle;
	ResourceBundle localLabelBundle;

	@FXML
	private void initialize() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");
		try {
			fxUtils = FXUtils.getInstance();
			fxUtils.setTransliteration(transliteration);
			SessionContext.map().put(RegistrationConstants.IS_CONSOLIDATED, RegistrationConstants.DISABLE);
			switchedOn = new SimpleBooleanProperty(true);
			toggleSwitchForResidence = new SimpleBooleanProperty(false);
			isChild = false;
			toggleFunction();
			ageFieldValidations();
			listenerOnFields();
			loadLocalLanguageFields();
			loadKeyboard();
			dob.setDisable(true);
			renderComboBoxes();
			addRegions();
			populateGender();

			toggleFunctionForResidence();
			applicationLabelBundle = ApplicationContext.getInstance().getApplicationLanguageBundle();
			localLabelBundle = ApplicationContext.getInstance().getLocalLanguageProperty();
			residence.setText(applicationLabelBundle.getString("national"));
			residenceLocalLanguage.setText(localLabelBundle.getString("national"));
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_DEMOGRAPHIC_PAGE);

		}
	}

	/**
	 * Toggle functionality between age field and date picker.
	 */
	private void toggleFunction() {
		try {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
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
						if (!(getRegistrationDTOFromSession().getSelectionListDTO() != null
								&& getRegistrationDTOFromSession().getSelectionListDTO().isChild())) {
							childSpecificFields.setVisible(false);
							childSpecificFieldsLocal.setVisible(false);
						}
						ageField.setDisable(false);
						ageFieldLocalLanguage.setDisable(true);
						ageFieldLocalLanguage.clear();
						dob.setDisable(true);
						dobLocalLanguage.setDisable(true);
					} else {
						toggleLabel1.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						toggleLabel2.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
						toggleLabel1LocalLanguage.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						toggleLabel2LocalLanguage.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
						ageField.clear();
						ageFieldLocalLanguage.clear();
						if (!(getRegistrationDTOFromSession().getSelectionListDTO() != null
								&& getRegistrationDTOFromSession().getSelectionListDTO().isChild())) {
							childSpecificFields.setVisible(false);
							childSpecificFieldsLocal.setVisible(false);
						}
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
	 * Toggle functionality for residence.
	 */
	private void toggleFunctionForResidence() {
		try {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Entering into toggle function for resident status");

			toggleSwitchForResidence.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						residence.setText(applicationLabelBundle.getString("foreigner"));
						residenceLocalLanguage.setText(localLabelBundle.getString("foreigner"));
						national.getStyleClass().clear();
						foreigner.getStyleClass().clear();
						nationalLocalLanguage.getStyleClass().clear();
						foreignerLocalLanguage.getStyleClass().clear();
						nationalLocalLanguage.getStyleClass().addAll("residence", "button");
						foreignerLocalLanguage.getStyleClass().addAll("selectedResidence", "button");
						foreigner.getStyleClass().addAll("selectedResidence", "button");
						national.getStyleClass().addAll("residence", "button");
					} else {
						residence.setText(applicationLabelBundle.getString("national"));
						residenceLocalLanguage.setText(localLabelBundle.getString("national"));
						national.getStyleClass().clear();
						foreigner.getStyleClass().clear();
						nationalLocalLanguage.getStyleClass().clear();
						foreignerLocalLanguage.getStyleClass().clear();
						nationalLocalLanguage.getStyleClass().addAll("selectedResidence", "button");
						foreignerLocalLanguage.getStyleClass().addAll("residence", "button");
						national.getStyleClass().addAll("selectedResidence", "button");
						foreigner.getStyleClass().addAll("residence", "button");
					}
				}
			});

			national.setOnMouseClicked((event) -> {
				toggleSwitchForResidence.set(!toggleSwitchForResidence.get());
			});
			foreigner.setOnMouseClicked((event) -> {
				toggleSwitchForResidence.set(!toggleSwitchForResidence.get());
			});

			nationalLocalLanguage.setOnMouseClicked((event) -> {
				toggleSwitchForResidence.set(!toggleSwitchForResidence.get());
			});
			foreignerLocalLanguage.setOnMouseClicked((event) -> {
				toggleSwitchForResidence.set(!toggleSwitchForResidence.get());
			});

			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Exiting the toggle function for resident status");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - TOGGLING OF DOB AND AGE FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * To restrict the user not to enter any values other than integer values.
	 */
	private void ageFieldValidations() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the age given by age field");
			fxUtils.focusUnfocusListener(demoGraphicPane, ageField, ageFieldLocalLanguage);
			ageField.textProperty().addListener((obsValue, oldValue, newValue) -> {
				ageFieldLocalLanguage.setText(newValue);
				if (!validation.validateTextField(demoGraphicPane, ageField, ageField.getId() + "_ontype",
						RegistrationConstants.DISABLE)) {
					ageField.setText(oldValue);
				}
				int age = 0;
				if (newValue.matches("\\d{1,3}")) {
					int maxAge = Integer.parseInt(AppConfig.getApplicationProperty("max_age"));
					int minAge = Integer.parseInt(AppConfig.getApplicationProperty("age_limit_for_child"));
					if (getRegistrationDTOFromSession().getSelectionListDTO() != null
							&& getRegistrationDTOFromSession().getSelectionListDTO().isChild())
						maxAge = 5;
					if (Integer.parseInt(ageField.getText()) > maxAge) {
						ageField.setText(oldValue);
						generateAlert(RegistrationConstants.ERROR,
								RegistrationUIConstants.MAX_AGE_WARNING + " " + maxAge);
					} else {
						age = Integer.parseInt(ageField.getText());
						if (getRegistrationDTOFromSession().getSelectionListDTO() != null
								&& !getRegistrationDTOFromSession().getSelectionListDTO().isChild()) {
							if (age <= 5) {
								ageField.setText(oldValue);
								generateAlert(RegistrationConstants.ERROR,
										RegistrationUIConstants.MIN_AGE_WARNING + " " + minAge);
							}
						}
						LocalDate currentYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
						dateOfBirth = Date
								.from(currentYear.minusYears(age).atStartOfDay(ZoneId.systemDefault()).toInstant());
						if (age <= Integer.parseInt(AppConfig.getApplicationProperty("age_limit_for_child"))
								&& !(getRegistrationDTOFromSession().getSelectionListDTO() != null
										&& !getRegistrationDTOFromSession().getSelectionListDTO().isChild())) {
							childSpecificFields.setVisible(true);
							childSpecificFieldsLocal.setVisible(true);
							childSpecificFields.setDisable(false);
							childSpecificFieldsLocal.setDisable(false);
							parentName.clear();
							uinId.clear();
							isChild = true;
							validation.setChild(isChild);
						} else {
							isChild = false;
							validation.setChild(isChild);
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
			fxUtils.validateOnType(demographicDetail, fullName, validation, fullNameLocalLanguage);
			fxUtils.validateOnType(demographicDetail, addressLine1, validation, addressLine1LocalLanguage);
			fxUtils.validateOnType(demographicDetail, addressLine2, validation, addressLine2LocalLanguage);
			fxUtils.validateOnType(demographicDetail, addressLine3, validation, addressLine3LocalLanguage);
			fxUtils.populateLocalFieldOnType(demographicDetail, mobileNo, validation, mobileNoLocalLanguage);
			fxUtils.populateLocalFieldOnType(demographicDetail, postalCode, validation, postalCodeLocalLanguage);
			fxUtils.populateLocalFieldOnType(demographicDetail, emailId, validation, emailIdLocalLanguage);
			fxUtils.populateLocalFieldOnType(demographicDetail, cniOrPinNumber, validation,
					cniOrPinNumberLocalLanguage);
			fxUtils.validateOnType(demographicDetail, parentName, validation, parentNameLocalLanguage);
			fxUtils.populateLocalFieldOnType(demographicDetail, uinId, validation, uinIdLocalLanguage);
			fxUtils.validateOnType(demographicDetail, fullNameLocalLanguage, validation);
			fxUtils.populateLocalComboBox(demographicDetail, gender, genderLocalLanguage);
			fxUtils.populateLocalComboBox(demographicDetail, city, cityLocalLanguage);
			fxUtils.populateLocalComboBox(demographicDetail, region, regionLocalLanguage);
			fxUtils.populateLocalComboBox(demographicDetail, province, provinceLocalLanguage);

			fxUtils.populateLocalComboBox(demographicDetail, localAdminAuthority, localAdminAuthorityLocalLanguage);
			dateValidation.validateDate(demographicDetail, dd, mm, yyyy, validation, fxUtils, ddLocalLanguage);
			dateValidation.validateDate(demographicDetail, ddLocalLanguage, mmLocalLanguage, yyyyLocalLanguage,
					validation, fxUtils, null);
			dateValidation.validateMonth(demographicDetail, dd, mm, yyyy, validation, fxUtils, mmLocalLanguage);
			dateValidation.validateMonth(demographicDetail, ddLocalLanguage, mmLocalLanguage, yyyyLocalLanguage,
					validation, fxUtils, null);
			dateValidation.validateYear(demographicDetail, dd, mm, yyyy, validation, fxUtils, yyyyLocalLanguage);
			dateValidation.validateYear(demographicDetail, ddLocalLanguage, mmLocalLanguage, yyyyLocalLanguage,
					validation, fxUtils, null);
			fxUtils.dobListener(yyyy, ageField, "\\d{4}");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - Listner method failed ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	/**
	 * 
	 * Loading the the labels of local language fields
	 * 
	 */
	private void loadLocalLanguageFields() {
		try {
			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading label fields of local language");
			ResourceBundle localProperties = applicationContext.getLocalLanguageProperty();
			fullNameLocalLanguageLabel.setText(localProperties.getString("fullName"));
			addressLine1LocalLanguageLabel.setText(localProperties.getString("addressLine1"));
			addressLine2LocalLanguageLabel.setText(localProperties.getString("addressLine2"));
			addressLine3LocalLanguageLabel.setText(localProperties.getString("addressLine3"));
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
			residenceLblLocalLanguage.setText(localProperties.getString("residence"));
			nationalLocalLanguage.setText(localProperties.getString("national"));
			foreignerLocalLanguage.setText(localProperties.getString("foreigner"));
			genderLocalLanguage.setPromptText(localProperties.getString("select"));
			localAdminAuthorityLocalLanguage.setPromptText(localProperties.getString("select"));
			cityLocalLanguage.setPromptText(localProperties.getString("select"));
			regionLocalLanguage.setPromptText(localProperties.getString("select"));
			provinceLocalLanguage.setPromptText(localProperties.getString("select"));
			ddLocalLanguage.setPromptText(localProperties.getString("dd"));
			mmLocalLanguage.setPromptText(localProperties.getString("mm"));
			yyyyLocalLanguage.setPromptText(localProperties.getString("yyyy"));
			languageLabelLocalLanguage.setText(localProperties.getString("language"));
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
			demoGraphicPane.getChildren().add(keyboardNode);
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
	 * To load the localAdminAuthorities selection list based on the language
	 * code
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
				jsonValidator.validateJson(JsonUtils.javaObjectToJsonString(demographicInfoDTO),
						"mosip-identity-json-schema.json");
			} catch (JsonValidationProcessingException | JsonIOException | JsonSchemaIOException | FileIOException
					| JsonProcessingException exception) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.REG_ID_JSON_VALIDATION_FAILED);
				LOGGER.error("JASON VALIDATOIN FAILED ", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						exception.getMessage() + ExceptionUtils.getStackTrace(exception));
				return;
			} catch (RuntimeException runtimeException) {
				generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.REG_ID_JSON_VALIDATION_FAILED);
				LOGGER.error("JASON VALIDATOIN FAILED ", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
						runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
				return;
			}

			if (isChild) {
				osiDataDTO.setIntroducerType(IntroducerType.PARENT.getCode());
				registrationMetaDataDTO.setApplicationType(RegistrationConstants.CHILD);
			} else {
				registrationMetaDataDTO.setApplicationType(RegistrationConstants.ADULT);
			}

			registrationMetaDataDTO.setParentOrGuardianUINOrRID(uinId.getText());

			osiDataDTO.setOperatorID(SessionContext.userContext().getUserId());

			registrationDTO.setPreRegistrationId(preRegistrationId.getText());
			registrationDTO.getDemographicDTO().setDemographicInfoDTO(demographicInfoDTO);

			LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Saved the demographic fields to DTO");

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - SAVING THE DETAILS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}
	}

	@SuppressWarnings("unchecked")
	public DemographicInfoDTO buildDemographicInfo() {

		String platformLanguageCode = ApplicationContext.applicationLanguage();
		String localLanguageCode = ApplicationContext.localLanguage();

		Map<String, DocumentDetailsDTO> documents = getRegistrationDTOFromSession().getDemographicDTO()
				.getApplicantDocumentDTO().getDocuments();
		BiometricInfoDTO applicantBiometric = getRegistrationDTOFromSession().getBiometricDTO()
				.getApplicantBiometricDTO();
		BiometricInfoDTO introducerBiometric = getRegistrationDTOFromSession().getBiometricDTO()
				.getIntroducerBiometricDTO();

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
						.with(identity -> identity.setDateOfBirth(DateUtils.formatDate(dateOfBirth, "yyyy/MM/dd")))
						.with(identity -> identity.setAge(Integer.parseInt(ageField.getText())))
						.with(identity -> identity.setGender(gender.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class).with(values -> values.add(Builder
										.build(ValuesDTO.class).with(value -> value.setLanguage(platformLanguageCode))
										.with(value -> value.setValue(gender.getValue().getGenderName())).get())).with(
												values -> values.add(Builder.build(ValuesDTO.class)
														.with(value -> value.setLanguage(localLanguageCode))
														.with(value -> value.setValue(
																genderLocalLanguage.getValue().getGenderName()))
														.get()))
										.get()))
						.with(identity -> identity.setResidenceStatus(residence.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(residence.getText())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(residenceLocalLanguage.getText())).get()))
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
												.with(value -> value.setValue(region.getValue().getName())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(regionLocalLanguage.getValue().getName()))
												.get()))
										.get()))
						.with(identity -> identity.setProvince(province.isDisabled() ? null
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
						.with(identity -> identity.setCity(city.isDisabled() ? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(city.getValue().getName())).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(cityLocalLanguage.getValue().getName()))
												.get()))
										.get()))
						.with(identity -> identity.setPostalCode(postalCode.isDisabled() ? null : postalCode.getText()))
						.with(identity -> identity.setPhone(mobileNo.isDisabled() ? null : mobileNo.getText()))
						.with(identity -> identity.setEmail(emailId.isDisabled() ? null : emailId.getText()))
						.with(identity -> identity
								.setCnieNumber(cniOrPinNumber.isDisabled() ? null : cniOrPinNumber.getText()))
						.with(identity -> identity.setLocalAdministrativeAuthority(localAdminAuthority.isDisabled()
								? null
								: (List<ValuesDTO>) Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue(localAdminAuthority.getValue().getName()))
												.get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue(
														localAdminAuthorityLocalLanguage.getValue().getName()))
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
						.with(identity -> identity.setProofOfIdentity(documents.get("POI")))
						.with(identity -> identity.setProofOfAddress(documents.get("POA")))
						.with(identity -> identity.setProofOfRelationship(documents.get("POR")))
						.with(identity -> identity.setProofOfDateOfBirth(documents.get("POB")))
						.with(identity -> identity.setIdSchemaVersion(1.0))
						.with(identity -> identity.setUin(
								getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getUin() == null ? null
										: new BigInteger(
												getRegistrationDTOFromSession().getRegistrationMetaDataDTO().getUin())))
						.with(identity -> identity.setIndividualBiometrics(!applicantBiometric
								.getFingerprintDetailsDTO().isEmpty()
								|| !applicantBiometric.getIrisDetailsDTO().isEmpty()
										? null
										: (CBEFFFilePropertiesDTO) Builder.build(CBEFFFilePropertiesDTO.class)
												.with(cbeffProperties -> cbeffProperties
														.setFormat(RegistrationConstants.CBEFF_FILE_FORMAT))
												.with(cbeffProperty -> cbeffProperty
														.setValue(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME
																.replace(RegistrationConstants.XML_FILE_FORMAT,
																		RegistrationConstants.EMPTY)))
												.with(cbeffProperty -> cbeffProperty.setVersion(1.0)).get()))
						.with(identity -> identity.setParentOrGuardianBiometrics(!introducerBiometric
								.getFingerprintDetailsDTO().isEmpty()
								|| !introducerBiometric.getIrisDetailsDTO().isEmpty()
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

	public void uinUpdate() {
		if (getRegistrationDTOFromSession().getSelectionListDTO() != null) {

			ObservableList<Node> nodes = demoGraphicPane.getChildren();

			for (Node node : nodes) {
				node.setDisable(true);
			}
			keyboardNode.setDisable(false);

			applicationLanguagePane.setDisable(false);
			localLanguagePane.setDisable(false);
			fetchBtn.setVisible(false);
			preRegistrationLabel.setText(RegistrationConstants.UIN_LABEL);
			updateUinId.setVisible(true);
			preRegistrationId.setVisible(false);
			getRegistrationDTOFromSession().getRegistrationMetaDataDTO()
					.setUin(getRegistrationDTOFromSession().getSelectionListDTO().getUinId());
			updateUinId.setText(getRegistrationDTOFromSession().getSelectionListDTO().getUinId());
			fullName.setDisable(false);
			fullNameLocalLanguage.setDisable(false);
			fullNameLocalLanguageLabel.setDisable(false);
			fullNameLabel.setDisable(false);

			dateAnchorPane.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAge());
			dateAnchorPaneLocalLanguage.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAge());

			gender.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isGender());
			genderLabel.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isGender());
			genderLocalLanguage.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isGender());
			genderLocalLanguageLabel.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isGender());

			applicationLanguageAddressAnchorPane
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());
			localLanguageAddressAnchorPane
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isAddress());

			mobileNo.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isContactDetails());
			mobileNoLabel.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isContactDetails());
			mobileNoLocalLanguage.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isContactDetails());
			mobileNoLocalLanguageLabel
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isContactDetails());
			emailId.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isContactDetails());
			emailIdLabel.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isContactDetails());
			emailIdLocalLanguage.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isContactDetails());
			emailIdLocalLanguageLabel
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isContactDetails());

			residentStatus.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isForeigner());
			residentStatusLocalLanguage
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isForeigner());

			cniOrPinNumber.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isCnieNumber());
			cniOrPinNumberLabel.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isCnieNumber());
			cniOrPinNumberLocalLanguage
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isCnieNumber());
			cniOrPinNumberLocalLanguageLabel
					.setDisable(!getRegistrationDTOFromSession().getSelectionListDTO().isCnieNumber());
			switchedOn.set(true);
			if (!isChild)
				isChild = getRegistrationDTOFromSession().getSelectionListDTO().isChild()
						|| getRegistrationDTOFromSession().getSelectionListDTO().isParentOrGuardianDetails();

			childSpecificFields.setDisable(!isChild);
			childSpecificFieldsLocal.setDisable(!isChild);
			childSpecificFields.setVisible(isChild);
			childSpecificFieldsLocal.setVisible(isChild);

			if (SessionContext.map().get(RegistrationConstants.IS_Child) != null) {
				isChild = (boolean) SessionContext.map().get(RegistrationConstants.IS_Child);
				childSpecificFields.setDisable(!isChild);
				childSpecificFields.setVisible(isChild);
				childSpecificFieldsLocal.setDisable(!isChild);
				childSpecificFieldsLocal.setVisible(isChild);
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

			DemographicInfoDTO demo = getRegistrationDTOFromSession().getDemographicDTO().getDemographicInfoDTO();

			populateFieldValue(fullName, fullNameLocalLanguage, demo.getIdentity().getFullName());
			populateFieldValue(gender, genderLocalLanguage, demo.getIdentity().getGender());
			populateFieldValue(addressLine1, addressLine1LocalLanguage, demo.getIdentity().getAddressLine1());
			populateFieldValue(addressLine2, addressLine2LocalLanguage, demo.getIdentity().getAddressLine2());
			populateFieldValue(addressLine3, addressLine3LocalLanguage, demo.getIdentity().getAddressLine3());
			populateFieldValue(region, regionLocalLanguage, demo.getIdentity().getRegion());
			populateFieldValue(province, provinceLocalLanguage, demo.getIdentity().getProvince());
			populateFieldValue(city, cityLocalLanguage, demo.getIdentity().getCity());
			populateFieldValue(gender, genderLocalLanguage, demo.getIdentity().getGender());
			Boolean isSwitchedOn = (Boolean) SessionContext.map().get(RegistrationConstants.DOB_TOGGLE);
			switchedOn.set(isSwitchedOn == null ? false : isSwitchedOn);
			postalCode.setText(demo.getIdentity().getPostalCode() + "");
			mobileNo.setText(demo.getIdentity().getPhone() + "");
			emailId.setText(demo.getIdentity().getEmail() + "");
			if (demo.getIdentity().getAge() != null)
				ageField.setText(demo.getIdentity().getAge() + "");
			cniOrPinNumber.setText(demo.getIdentity().getCnieNumber() + "");
			postalCodeLocalLanguage.setAccessibleHelp(demo.getIdentity().getPostalCode());
			mobileNoLocalLanguage.setText(demo.getIdentity().getPhone());
			emailIdLocalLanguage.setText(demo.getIdentity().getEmail());
			cniOrPinNumberLocalLanguage.setText(demo.getIdentity().getCnieNumber() + "");

			if (!switchedOn.get()) {
				if (demo.getIdentity().getDateOfBirth() != null) {
					String[] date = demo.getIdentity().getDateOfBirth().split("/");
					if (date.length == 3) {
						yyyy.setText(date[0]);
						mm.setText(date[1]);
						dd.setText(date[2]);
					}
				}
			}

			populateFieldValue(localAdminAuthority, localAdminAuthorityLocalLanguage,
					demo.getIdentity().getLocalAdministrativeAuthority());

			if (SessionContext.map().get(RegistrationConstants.IS_Child) != null) {

				boolean isChild = (boolean) SessionContext.map().get(RegistrationConstants.IS_Child);
				childSpecificFields.setDisable(!isChild);
				childSpecificFields.setVisible(isChild);
				childSpecificFieldsLocal.setDisable(!isChild);
				childSpecificFieldsLocal.setVisible(isChild);
			}
			if (demo.getIdentity().getParentOrGuardianRIDOrUIN() != null) {
				populateFieldValue(parentName, parentNameLocalLanguage, demo.getIdentity().getParentOrGuardianName());
				uinId.setText(demo.getIdentity().getParentOrGuardianRIDOrUIN() + "");
			}
			preRegistrationId.setText(getRegistrationDTOFromSession().getPreRegistrationId());

		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Loading address from previous entry");

			if (SessionContext.map().get(RegistrationConstants.ADDRESS_KEY) == null) {
				generateAlert(RegistrationConstants.ERROR, "Previous registration details not available.");
				LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Previous registration details not available.");

			} else {
				LocationDTO locationDto = ((AddressDTO) SessionContext.map().get(RegistrationConstants.ADDRESS_KEY))
						.getLocationDTO();
				fxUtils.selectComboBoxValue(region, locationDto.getRegion());
				retrieveAndPopulateLocationByHierarchy(region, province, provinceLocalLanguage);
				fxUtils.selectComboBoxValue(province, locationDto.getProvince());
				retrieveAndPopulateLocationByHierarchy(province, city, cityLocalLanguage);
				fxUtils.selectComboBoxValue(city, locationDto.getCity());
				retrieveAndPopulateLocationByHierarchy(city, localAdminAuthority, localAdminAuthorityLocalLanguage);

				postalCode.setText(locationDto.getPostalCode());
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
			keyboardNode.setLayoutX(470.00);
			Node node = (Node) event.getSource();

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE1)) {
				addressLine1LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(450.00);
			}

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE2)) {
				addressLine2LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(520.00);
			}

			if (node.getId().equals(RegistrationConstants.ADDRESS_LINE3)) {
				addressLine3LocalLanguage.requestFocus();
				keyboardNode.setLayoutY(585.00);
			}

			if (node.getId().equals(RegistrationConstants.FULL_NAME)) {
				fullNameLocalLanguage.requestFocus();
				keyboardNode.setLayoutY(165.00);
			}

			if (node.getId().equals(RegistrationConstants.PARENT_NAME)) {
				parentNameLocalLanguage.requestFocus();
				keyboardNode.setLayoutY(920.00);
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
		fullName.setText("Ayoub Toufiq");
		int age = 27;
		switchedOn.set(true);
		ageField.setText("" + age);
		populateGender();
		if (!gender.getItems().isEmpty()) {
			gender.getSelectionModel().select(0);
			genderLocalLanguage.getSelectionModel().select(0);
		}
		addressLine1.setText("30 Rue Oum Errabia");
		addressLine2.setText("Errabia");
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

	@FXML
	private void back() {
		goToHomePageFromRegistration();
	}

	@FXML
	private void next() {
		if (validateThisPane()) {
			if (!switchedOn.get()) {

				if (dd.getText().matches("\\d+") && mm.getText().matches("\\d+") && yyyy.getText().matches("\\d+")) {

					LocalDate currentYear = LocalDate.of(Integer.parseInt(yyyy.getText()),
							Integer.parseInt(mm.getText()), Integer.parseInt(dd.getText()));
					dateOfBirth = Date.from(currentYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
				}
			}
			saveDetail();
			SessionContext.map().put("demographicDetail", false);
			SessionContext.map().put("documentScan", true);
			if (!isEditPage()) {
				documentScanController.populateDocumentCategories();
			}

			auditFactory.audit(AuditEvent.REG_DEMO_NEXT, Components.REG_DEMO_DETAILS, SessionContext.userId(),
					AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

			registrationController.showCurrentPage(RegistrationConstants.DEMOGRAPHIC_DETAIL,
					getPageDetails(RegistrationConstants.DEMOGRAPHIC_DETAIL, RegistrationConstants.NEXT));
		}

	}

	private Boolean isEditPage() {
		if (SessionContext.map().get(RegistrationConstants.REGISTRATION_ISEDIT) != null)
			return (Boolean) SessionContext.map().get(RegistrationConstants.REGISTRATION_ISEDIT);
		return false;
	}

	public boolean validateThisPane() {
		boolean isValid = true;
		isValid = registrationController.validateDemographicPane(demoGraphicPane);
		if (isValid)
			isValid = validation.validateUinOrRid(uinId, isChild, uinValidator, ridValidator);
		registrationController.displayValidationMessage(validation.getValidationMessage().toString());

		return isValid;
	}

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
			gender.setConverter((StringConverter<GenderDto>) uiRenderForComboBox);
			regionLocalLanguage.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			provinceLocalLanguage.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			cityLocalLanguage.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			localAdminAuthorityLocalLanguage.setConverter((StringConverter<LocationDto>) uiRenderForComboBox);
			genderLocalLanguage.setConverter((StringConverter<GenderDto>) uiRenderForComboBox);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.REGISTRATION_CONTROLLER,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException), runtimeException);
		}
		LOGGER.info("REGISTRATION - INDIVIDUAL_REGISTRATION - RENDER_COMBOBOXES", RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME, "Rendering of comboboxes ended");
	}

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

	private void populateGender() {
		LOGGER.info("REGISTRATION - INDIVIDUAL_REGISTRATION - POPULATE_GENDER", RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME, "Fetching Gender based on Application Language started");

		try {
			gender.getItems().clear();
			genderLocalLanguage.getItems().clear();
			gender.getItems().addAll(masterSync.getGenderDtls(ApplicationContext.applicationLanguage()));
			genderLocalLanguage.getItems().addAll(masterSync.getGenderDtls(ApplicationContext.localLanguage()));
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.REGISTRATION_CONTROLLER,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException), runtimeException);
		}
		LOGGER.info("REGISTRATION - INDIVIDUAL_REGISTRATION - POPULATE_GENDER", RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME, "Fetching Gender based on Application Language ended");
	}
}
