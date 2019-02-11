package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import io.mosip.registration.controller.VirtualKeyboard;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.dto.demographic.ValuesDTO;
import io.mosip.registration.dto.mastersync.LocationDto;
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
	@FXML
	private AnchorPane demoGraphicPane;
	@Autowired
	private DateValidation dateValidation;
	@Autowired
	private PreRegistrationDataSyncService preRegistrationDataSyncService;

	@Autowired
	private RegistrationController registrationController;
	@Autowired
	private DocumentScanController documentScanController;

	private FXUtils fxUtils;
	private List<LocationDto> locationDtoRegion;
	private List<LocationDto> locationDtoProvince;
	private List<LocationDto> locationDtoCity;
	private List<LocationDto> locactionlocalAdminAuthority;
	private Date dateOfBirth;

	@FXML
	private void initialize() {
		LOGGER.debug(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Entering the LOGIN_CONTROLLER");
		try {
			fxUtils = FXUtils.getInstance();
			SessionContext.map().put(RegistrationConstants.IS_CONSOLIDATED,
					RegistrationConstants.DISABLE);
			switchedOn = new SimpleBooleanProperty(false);
			isChild = false;
			toggleFunction();
			ageFieldValidations();
			listenerOnFields();
			loadLocalLanguageFields();
			loadKeyboard();
			ageField.setDisable(true);
			addRegions();

		} catch (IOException | RuntimeException exception) {
			LOGGER.error("REGISTRATION - CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			exception.printStackTrace();
			System.out.println("Hello");
			generateAlert(RegistrationConstants.ERROR, RegistrationUIConstants.UNABLE_LOAD_REG_PAGE);
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
						toggleLabel1LocalLanguage.setId(RegistrationConstants.FIRST_TOGGLE_LABEL);
						toggleLabel2LocalLanguage.setId(RegistrationConstants.SECOND_TOGGLE_LABEL);
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

			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					"Exiting the toggle function for toggle label 1 and toggle level 2");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - TOGGLING OF DOB AND AGE FAILED ", APPLICATION_NAME,
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
			ddLocalLanguage.setPromptText(localProperties.getString("dd"));
			mmLocalLanguage.setPromptText(localProperties.getString("mm"));
			yyyyLocalLanguage.setPromptText(localProperties.getString("yyyy"));
		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - LOADING LOCAL LANGUAGE FIELDS FAILED ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
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
					exception.getMessage());
		}
	}

	/**
	 * To load the regions in the selection list based on the language code
	 */
	private void addRegions() {
		try {
			locationDtoRegion = masterSync.findLocationByHierarchyCode(
					applicationContext.getApplicationLanguageBundle().getString(region.getId()),
					applicationContext.getApplicationLanguage());
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
	/**
	 * To load the provinces in the selection list based on the language code
	 */
	@FXML
	private void addProvince() {
		try {
			List<LocationDto> listOfCodes = locationDtoRegion.stream()
					.filter(location -> location.getName().equals(region.getValue())).collect(Collectors.toList());
			String code = "";
			String langCode = "";
			if (!listOfCodes.isEmpty()) {
				code = listOfCodes.get(0).getCode();
				langCode = listOfCodes.get(0).getLangCode();
				locationDtoProvince = masterSync.findProvianceByHierarchyCode(code, langCode);
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
				langCode = listOfCodes.get(0).getLangCode();
				locationDtoCity = masterSync.findProvianceByHierarchyCode(code, langCode);
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
	 * To load the localAdminAuthorities selection list based on the language
	 * code
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
				langCode = listOfCodes.get(0).getLangCode();
				List<LocationDto> locationlocalAdminAuthority = masterSync.findProvianceByHierarchyCode(code, langCode);
				localAdminAuthority.getItems().clear();
				localAdminAuthority.getItems().addAll(
						locationlocalAdminAuthority.stream().map(loc -> loc.getName()).collect(Collectors.toList()));
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING FAILED FOR LOCAL ADMIN AUTHORITY SELECTOIN LIST ", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());

		}

	}

	@SuppressWarnings("unchecked")
	public DemographicInfoDTO buildDemographicInfo() {

		String platformLanguageCode = applicationContext.getApplicationLanguage().toLowerCase();
		String localLanguageCode = applicationContext.getLocalLanguage().toLowerCase();
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
						.with(identity -> identity.setDateOfBirth(dateAnchorPane.isDisabled() ? null
								: (dateOfBirth != null ? DateUtils.formatDate(dateOfBirth, "yyyy/MM/dd") : "")))
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
						.with(identity -> identity.setCnieNumber(cniOrPinNumber.isDisabled() ? null
								: cniOrPinNumber.getText().equals(RegistrationConstants.EMPTY) ? null
										: new BigInteger(cniOrPinNumber.getText())))
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
						.get()))
				.get();
	}

	private RegistrationDTO getRegistrationDtoContent() {

		return registrationController.getRegistrationDtoContent();
	}

	public void uinUpdate() {
		if (getRegistrationDtoContent().getSelectionListDTO() != null) {

			ObservableList<Node> nodes = demoGraphicPane.getChildren();

			for (Node node : nodes) {
				node.setDisable(true);
			}
			keyboardNode.setDisable(false);

			applicationLanguagePane.setDisable(false);
			localLanguagePane.setDisable(false);
			fetchBtn.setVisible(false);
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
			Boolean isSwitchedOn = (Boolean) SessionContext.map().get(RegistrationConstants.DOB_TOGGLE);
			switchedOn.set(isSwitchedOn == null ? false : isSwitchedOn);
			postalCode.setText(demo.getIdentity().getPostalCode());
			mobileNo.setText(demo.getIdentity().getPhone());
			emailId.setText(demo.getIdentity().getEmail());
			if (demo.getIdentity().getAge() != null)
				ageField.setText(demo.getIdentity().getAge() + "");
			cniOrPinNumber.setText(demo.getIdentity().getCnieNumber() + "");
			postalCodeLocalLanguage.setAccessibleHelp(demo.getIdentity().getPostalCode());
			mobileNoLocalLanguage.setText(demo.getIdentity().getPhone());
			emailIdLocalLanguage.setText(demo.getIdentity().getEmail());
			cniOrPinNumberLocalLanguage.setText(demo.getIdentity().getCnieNumber() + "");

			if (!StringUtils.isEmpty(demo.getIdentity().getDateOfBirth())) {
				String[] dob = demo.getIdentity().getDateOfBirth().split("/");
				dd.setText(dob[2]);
				mm.setText(dob[1]);
				yyyy.setText(dob[0]);
			} else {
				dd.setText((String) SessionContext.map().get("dd"));
				mm.setText((String) SessionContext.map().get("mm"));
				yyyy.setText((String) SessionContext.map().get("yyyy"));
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
			preRegistrationId.setText(getRegistrationDtoContent().getPreRegistrationId());

		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.REGISTRATION_CONTROLLER, APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}

	}

	private void populateFieldValue(Node nodeForPlatformLang, Node nodeForLocalLang, List<ValuesDTO> fieldValues) {
		if (fieldValues != null) {
			String platformLanguageCode = applicationContext.getApplicationLanguage();
			String localLanguageCode = applicationContext.getLocalLanguage();
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

		registrationController.createRegistrationDTOObject(RegistrationConstants.PACKET_TYPE_NEW);
		//documentScanController.clearDocSection();
		
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
				region.setValue(locationDto.getRegion());
				city.setValue(locationDto.getCity());
				province.setValue(locationDto.getProvince());
				postalCode.setText(locationDto.getPostalCode());
				LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, "Loaded address from previous entry");
			}
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - LOADING ADDRESS FROM PREVIOUS ENTRY FAILED ", APPLICATION_NAME,
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

	public void clickMe() {
		SessionContext.map().put(RegistrationConstants.IS_CONSOLIDATED,
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
		registrationController.displayValidationMessage(validation.getValidationMessage().toString());
		SessionContext.map().put(RegistrationConstants.IS_CONSOLIDATED,
				RegistrationConstants.DISABLE);
	}

	public void setPreviewContent() {
		autoFillBtn.setVisible(false);
		fetchBtn.setVisible(false);
		SessionContext.map().put("demoGraphicPaneContent", demoGraphicPane);
	}

	@FXML
	private void back() {
		goToHomePageFromRegistration();
	}

	@FXML
	private void next() {
		if (validateThisPane()) {

				if (!switchedOn.get()) {
					LocalDate currentYear = LocalDate.of(Integer.parseInt(yyyy.getText()),
							Integer.parseInt(mm.getText()), Integer.parseInt(dd.getText()));
					dateOfBirth = Date.from(currentYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
					SessionContext.map().put(RegistrationConstants.REGISTRATION_AGE_DATA,
							dateOfBirth);
					SessionContext.map().put("dd", dd.getText());
					SessionContext.map().put("mm", mm.getText());
					SessionContext.map().put("yyyy", yyyy.getText());
				}
				SessionContext.map().put("demographicDetail", false);
				SessionContext.map().put("documentScan", true);
				registrationController.showCurrentPage();
			
		}

	}
	
	public boolean validateThisPane() {
		boolean isValid=true;
		isValid=registrationController.validateDemographicPane(demoGraphicPane);
		if (isValid)
			isValid = validation.validateUinOrRid(uinId, isChild, uinValidator, ridValidator);
		registrationController.displayValidationMessage(validation.getValidationMessage().toString());

		return isValid;

		
	}

}
