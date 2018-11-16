package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationExceptions.REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.LocationDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * The enums for introducer types
 * 
 * @author Taleev Aalam
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
	private TextField fullName_lc;

	@FXML
	private Label fullName_lc_label;

	@FXML
	private DatePicker ageDatePicker;

	@FXML
	private TextField ageField;

	@FXML
	private Label toggleLabel1;

	@FXML
	private Label toggleLabel2;

	@FXML
	private AnchorPane childSpecificFields;

	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);

	@FXML
	private ComboBox<String> gender;

	@FXML
	private TextField addressLine1;

	@FXML
	private TextField addressLine1_lc;

	@FXML
	private Label addressLine1_lc_label;

	@FXML
	private TextField addressLine2;

	@FXML
	private TextField addressLine2_lc;

	@FXML
	private Label addressLine2_lc_label;

	@FXML
	private TextField addressLine3;

	@FXML
	private TextField addressLine3_lc;

	@FXML
	private Label addressLine3_lc_label;

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
	private TextField cni_or_pin_number;

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
	private Label poa_label;

	@FXML
	private ComboBox<String> poiDocuments;

	@FXML
	private Label poi_label;

	@FXML
	private ComboBox<String> porDocuments;

	@FXML
	private Label por_label;

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
	private AnchorPane anchor_pane_registration;

	private static AnchorPane demoGraphicPane1Content;

	private static AnchorPane demoGraphicPane2Content;

	public static RegistrationDTO registrationDTOContent;

	public static DatePicker ageDatePickerContent;

	private boolean toggleAgeOrDobField = false;

	private boolean isChild = false;

	private static boolean isEditPage;

	VirtualKeyboard keyboard = new VirtualKeyboard();

	Node keyboardNode = keyboard.view();

	@FXML
	private void initialize() {
		try {
			LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					"Entering the LOGIN_CONTROLLER");
			switchedOn.set(false);
			ageDatePicker.setDisable(false);
			ageField.setDisable(true);
			disableFutureDays();
			toggleFunction();
			ageFieldValidations();
			ageValidationInDatePicker();
			dateFormatter();
			loadAddressFromPreviousEntry();
			populateTheLocalLangFields();
			loadLanguageSpecificKeyboard();
			demoGraphicPane1.getChildren().add(keyboardNode);
			keyboardNode.setVisible(false);
			loadLocalLanguageFields();
			loadListOfDocuments();

			if (isEditPage && registrationDTOContent != null) {
				DemographicDTO demographicDTO = registrationDTOContent.getDemographicDTO();
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
				cni_or_pin_number.setText(demographicInfoDTO.getCneOrPINNumber());
				localAdminAuthority.setText(demographicInfoDTO.getLocalAdministrativeAuthority());
				if (demographicDTO.getIntroducerRID() != null) {
					uinId.setText(demographicDTO.getIntroducerRID());
				} else {
					uinId.setText(demographicDTO.getIntroducerUIN());
				}
				parentName.setText(demographicInfoDTO.getParentOrGuardianName());
				preRegistrationId.setText(registrationDTOContent.getPreRegistrationId());

				isEditPage = false;
				ageFieldValidations();
				ageValidationInDatePicker();

			}
		} catch (IOException | RuntimeException exception) {
			LOGGER.error("REGISTRATION - LOGIN_MODE - LOGIN_CONTROLLER", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
			generateAlert(RegistrationConstants.ALERT_ERROR, AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION.getErrorMessage());
		}
	}

	/**
	 * 
	 * Loading the address detail from previous entry
	 * 
	 */
	public void loadAddressFromPreviousEntry() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Loading address from previous entry");
		Map<String, Object> sessionMapObject = SessionContext.getInstance().getMapObject();
		AddressDTO addressDto = (AddressDTO) sessionMapObject.get("PrevAddress");
		if (addressDto != null) {
			LocationDTO locationDto = addressDto.getLocationDTO();
			region.setText(locationDto.getRegion());
			city.setText(locationDto.getCity());
			province.setText(locationDto.getProvince());
			postalCode.setText(locationDto.getPostalCode());
		}
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Loaded address from previous entry");
	}

	/**
	 * 
	 * Loading the second demographic pane
	 * 
	 */
	public void gotoSecondDemographicPane() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Loading the second demographic pane");
		if (validatePaneOne()) {
			demoGraphicTitlePane.setContent(null);
			demoGraphicTitlePane.setExpanded(false);
			demoGraphicTitlePane.setContent(demoGraphicPane2);
			demoGraphicTitlePane.setExpanded(true);
			anchor_pane_registration.setMaxHeight(700);
		}

	}

	/**
	 * 
	 * Setting the focus to address line 1 local
	 * 
	 */
	public void adressLine1Focus() {
		addressLine1_lc.requestFocus();
		keyboardNode.setLayoutX(300.00);
		keyboardNode.setLayoutY(270.00);
		keyboardNode.setVisible(true);
	}

	/**
	 * 
	 * Setting the focus to address line 2 local
	 * 
	 */
	public void adressLine2Focus() {
		addressLine2_lc.requestFocus();
		keyboardNode.setLayoutX(300);
		keyboardNode.setLayoutY(320);
		keyboardNode.setVisible(true);
	}

	/**
	 * 
	 * Setting the focus to address line 3 local
	 * 
	 */
	public void adressLine3Focus() {
		addressLine3_lc.requestFocus();
		keyboardNode.setLayoutX(300);
		keyboardNode.setLayoutY(375);
		keyboardNode.setVisible(true);
	}

	/**
	 * 
	 * Setting the focus to full name local
	 * 
	 */
	public void fullNameFocus() {
		fullName_lc.requestFocus();
		keyboardNode.setLayoutX(300);
		keyboardNode.setLayoutY(120);
		keyboardNode.setVisible(true);
	}

	/**
	 * 
	 * Saving the detail into concerned DTO'S
	 * 
	 */
	public void saveDetail() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Saving the fields to DTO");
		RegistrationDTO registrationDTO = new RegistrationDTO();
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		LocationDTO locationDto = new LocationDTO();
		AddressDTO addressDto = new AddressDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		OSIDataDTO osiDataDto = new OSIDataDTO();
		if (validatePaneTwo()) {
			demographicInfoDTO.setFullName(fullName.getText());
			if (ageDatePicker.getValue() != null) {
				demographicInfoDTO.setDateOfBirth(
						Date.from(ageDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
			}
			demographicInfoDTO.setAge(ageField.getText());
			demographicInfoDTO.setGender(gender.getValue());
			addressDto.setAddressLine1(addressLine1.getText());
			addressDto.setAddressLine2(addressLine2.getText());
			addressDto.setLine3(addressLine3.getText());
			locationDto.setProvince(province.getText());
			locationDto.setCity(city.getText());
			locationDto.setRegion(region.getText());
			locationDto.setPostalCode(postalCode.getText());
			addressDto.setLocationDTO(locationDto);
			demographicInfoDTO.setAddressDTO(addressDto);
			demographicInfoDTO.setMobile(mobileNo.getText());
			demographicInfoDTO.setEmailId(emailId.getText());
			demographicInfoDTO.setChild(isChild);
			demographicInfoDTO.setCneOrPINNumber(cni_or_pin_number.getText());
			demographicInfoDTO.setLocalAdministrativeAuthority(localAdminAuthority.getText());
			if (isChild) {
				if (uinId.getText().length() == 28) {
					demographicDTO.setIntroducerRID(uinId.getText());
				} else {
					demographicDTO.setIntroducerUIN(uinId.getText());
				}
				osiDataDto.setIntroducerType(IntroducerType.PARENT.getCode());
				demographicInfoDTO.setParentOrGuardianName(parentName.getText());
			}
			demographicDTO.setDemoInUserLang(demographicInfoDTO);
			osiDataDto.setOperatorID(SessionContext.getInstance().getUserContext().getUserId());

			registrationDTO.setPreRegistrationId(preRegistrationId.getText());
			registrationDTO.setOsiDataDTO(osiDataDto);
			registrationDTO.setDemographicDTO(demographicDTO);

			LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					"Saved the fields to DTO");

			try {
				demoGraphicPane1Content = demoGraphicPane1;
				demoGraphicPane2Content = demoGraphicPane2;
				registrationDTOContent = registrationDTO;
				if (ageDatePicker.getValue() != null) {
					ageDatePickerContent = new DatePicker();
					ageDatePickerContent.setValue(ageDatePicker.getValue());
				}
				nextBtn.setVisible(false);
				pane2NextBtn.setVisible(false);
				loadScreen(RegistrationConstants.DEMOGRAPHIC_PREVIEW);
			} catch (IOException ioException) {
				LOGGER.error("REGISTRATION - UI- Demographic Preview ", APPLICATION_NAME,
						RegistrationConstants.APPLICATION_ID, ioException.getMessage());
			}

		}

	}

	public static void loadScreen(String screen) throws IOException {
		Parent createRoot = BaseController.load(RegistrationController.class.getResource(screen),
				ApplicationContext.getInstance().getApplicationLanguageBundle());
		LoginController.getScene().setRoot(createRoot);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		LoginController.getScene().getStylesheets().add(loader.getResource(RegistrationConstants.CSS_FILE_PATH).toExternalForm());
	}

	/**
	 * Validating the age field for the child/Infant check.
	 */
	public void ageValidationInDatePicker() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
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
		}
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validated the age given by DatePiker");
	}

	/**
	 * Disabling the future days in the date picker calendar.
	 */
	private void disableFutureDays() {
		ageDatePicker.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();

				setDisable(empty || date.compareTo(today) > 0);
			}
		});

		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Future dates disabled");
	}

	/**
	 * Populating the user language fields to local language fields
	 */
	private void populateTheLocalLangFields() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Populating the local language fields");
		fullName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				if (!newValue.matches("([A-z]+\\s?\\.?)+")) {
					generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							RegistrationConstants.FULL_NAME_EMPTY, "Numbers are not allowed");
					fullName.setText(fullName.getText().replaceAll("\\d+", ""));
					fullName.requestFocus();
				} else {
					fullName_lc.setText(fullName.getText());
				}
			}
		});

		addressLine1.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				addressLine1_lc.setText(addressLine1.getText());
			}
		});

		addressLine2.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				addressLine2_lc.setText(addressLine2.getText());
			}
		});

		addressLine3.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> obsVal, final String oldValue,
					final String newValue) {
				addressLine3_lc.setText(addressLine3.getText());
			}
		});
	}

	/**
	 * To restrict the user not to enter any values other than integer values.
	 */
	private void loadLanguageSpecificKeyboard() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Loading the local language keyboard");
		addressLine1_lc.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (oldValue) {
					keyboardNode.setVisible(false);
				}

			}
		});

		addressLine2_lc.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (oldValue) {
					keyboardNode.setVisible(false);
				}

			}
		});

		addressLine3_lc.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (oldValue) {
					keyboardNode.setVisible(false);
				}

			}
		});

		fullName_lc.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (oldValue) {
					keyboardNode.setVisible(false);
				}

			}
		});

	}

	/**
	 * To restrict the user not to enter any values other than integer values.
	 */
	private void ageFieldValidations() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Validating the age given by age field");
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
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
				"Validating the age given by age field");
	}

	/**
	 * Toggle functionality between age field and date picker.
	 */
	private void toggleFunction() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
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
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Exiting the toggle function for toggle label 1 and toggle level 2");
	}

	/**
	 * To dispaly the selected date in the date picker in specific
	 * format("dd-mm-yyyy").
	 */
	private void dateFormatter() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
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
	}

	/**
	 * 
	 * Opens the home page screen
	 * 
	 */
	public void goToHomePage() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Going to home page");

		try {
			isEditPage = false;
			demoGraphicPane1Content = null;
			demoGraphicPane2Content = null;
			ageDatePickerContent = null;
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
	private boolean validatePaneOne() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the fields in first demographic pane");
		boolean gotoNext = false;
		if (validateRegex(fullName, "([A-z]+\\s?\\.?)+")) {
			generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.FULL_NAME_EMPTY, "Numbers are not allowed");
			fullName.requestFocus();
		} else {
			if (!validateAgeorDob()) {

			} else {
				if (gender.getValue() == null) {
					generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							RegistrationConstants.GENDER_EMPTY);
					gender.requestFocus();
				} else {
					if (validateRegex(addressLine1, "^.{6,50}$")) {
						generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
								RegistrationConstants.ADDRESS_LINE_1_EMPTY, RegistrationConstants.ADDRESS_LINE_WARNING);
						addressLine1.requestFocus();
					} else {
						if (validateRegex(addressLine2, "^.{6,50}$")) {
							generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
									RegistrationConstants.ADDRESS_LINE_2_EMPTY,
									RegistrationConstants.ADDRESS_LINE_WARNING);
							addressLine2.requestFocus();
						} else {
							if (validateRegex(region, "^.{6,50}$")) {
								generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
										RegistrationConstants.REGION_EMPTY, RegistrationConstants.ONLY_ALPHABETS + " "
												+ RegistrationConstants.TEN_LETTER_INPUT_LIMT);
								region.requestFocus();
							} else {
								if (validateRegex(city, "^.{6,10}$")) {
									generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
											RegistrationConstants.CITY_EMPTY, RegistrationConstants.ONLY_ALPHABETS + " "
													+ RegistrationConstants.TEN_LETTER_INPUT_LIMT);
									city.requestFocus();
								} else {
									if (validateRegex(province, "^.{6,10}$")) {
										generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
												RegistrationConstants.PROVINCE_EMPTY,
												RegistrationConstants.ONLY_ALPHABETS + " "
														+ RegistrationConstants.TEN_LETTER_INPUT_LIMT);
										province.requestFocus();
									} else {
										if (validateRegex(postalCode, "\\d{5}")) {
											generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
													RegistrationConstants.POSTAL_CODE_EMPTY,
													RegistrationConstants.FIVE_DIGIT_INPUT_LIMT);
											postalCode.requestFocus();
										} else {
											if (validateRegex(localAdminAuthority, "^.{6,10}$")) {
												generateAlert("Error",
														AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
														RegistrationConstants.LOCAL_ADMIN_AUTHORITY_EMPTY,
														RegistrationConstants.ONLY_ALPHABETS);
												localAdminAuthority.requestFocus();
											} else {
												if (validateRegex(mobileNo, "\\d{10}")) {
													generateAlert("Error",
															AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
															RegistrationConstants.MOBILE_NUMBER_EMPTY,
															RegistrationConstants.MOBILE_NUMBER_EXAMPLE);
													mobileNo.requestFocus();
												} else {
													if (validateRegex(emailId,
															"^([\\w\\-\\.]+)@((\\[([0-9]{1,3}\\.){3}[0-9]{1,3}\\])|(([\\w\\-]+\\.)+)([a-zA-Z]{2,4}))$")) {
														generateAlert("Error",
																AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
																RegistrationConstants.EMAIL_ID_EMPTY,
																RegistrationConstants.EMAIL_ID_EXAMPLE);
														emailId.requestFocus();
													} else {
														if (validateRegex(cni_or_pin_number, "\\d{5}")) {
															generateAlert("Error",
																	AlertType
																			.valueOf(RegistrationConstants.ALERT_ERROR),
																	RegistrationConstants.CNIE_OR_PIN_NUMBER_EMPTY,
																	RegistrationConstants.FIVE_DIGIT_INPUT_LIMT);
															cni_or_pin_number.requestFocus();
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
		}
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validated the fields");
		return gotoNext;
	}

	/**
	 * 
	 * Validate the fields of demographic pane 2
	 * 
	 */

	private boolean validatePaneTwo() {
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
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
		LOGGER.debug("REGISTRATION_CONTROLLER", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Toggling for parent/guardian fields");
		boolean gotoNext = false;

		if (isChild) {
			if (validateRegex(parentName, "[[A-z]+\\s?\\.?]+")) {
				generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						"Please provide parent name");
				parentName.requestFocus();
			} else {
				if (validateRegex(uinId, "\\d{6,28}")) {
					generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							"Please provide parent UIN Id");
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
		Properties properties = ApplicationContext.getInstance().getLocalLanguageProperty();
		fullName_lc_label.setText(properties.getProperty("full_name"));
		addressLine1_lc_label.setText(properties.getProperty("address_line1"));
		addressLine2_lc_label.setText(properties.getProperty("address_line2"));
		addressLine3_lc_label.setText(properties.getProperty("address_line3"));
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
									.getInstance().getLocalLanguageProperty().getProperty("titleDemographicPane"));

				}
			}
		});
	}

	/**
	 * 
	 * Loading the the labels of local language fields
	 * 
	 */
	private void loadListOfDocuments() {
		poaDocuments.getItems().addAll(RegistrationConstants.getPoaDocumentList());
		poiDocuments.getItems().addAll(RegistrationConstants.getPoiDocumentList());
		porDocuments.getItems().addAll(RegistrationConstants.getPorDocumentList());
	}

	private boolean validateAgeorDob() {
		boolean gotoNext = false;
		if (toggleAgeOrDobField) {
			if (validateRegex(ageField, "\\d{1,2}")) {
				generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.AGE_EMPTY, RegistrationConstants.AGE_WARNING);
				ageField.requestFocus();
			} else {
				gotoNext = true;
			}
		} else if (!toggleAgeOrDobField) {
			if (ageDatePicker.getValue() == null) {
				generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.DATE_OF_BIRTH_EMPTY);
				ageDatePicker.requestFocus();
			} else {
				gotoNext = true;
			}
		}
		return gotoNext;
	}

	public void scanPoaDocument() {
		if (poaDocuments.getValue() == null) {
			generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.POA_DOCUMENT_EMPTY, "Numbers are not allowed");
			poaDocuments.requestFocus();
		} else {
			poa_label.setId("doc_label");
			poa_label.setText(poaDocuments.getValue());
			;
		}
	}

	public void scanPoiDocument() {
		if (poiDocuments.getValue() == null) {
			generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.POI_DOCUMENT_EMPTY, "Numbers are not allowed");
			poiDocuments.requestFocus();
		} else {
			poi_label.setId("doc_label");
			poi_label.setText(poiDocuments.getValue());
			;
		}
	}

	public void scanPorDocument() {
		if (porDocuments.getValue() == null) {
			generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.POR_DOCUMENT_EMPTY, "Numbers are not allowed");
			porDocuments.requestFocus();
		} else {
			por_label.setId("doc_label");
			por_label.setText(porDocuments.getValue());
			;
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

}
