package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.RegistrationConstants;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	@FXML
	private TextField preRegistrationId;

	@FXML
	private TextField fullName;

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
	private TextField addressLine2;

	@FXML
	private TextField addressLine3;

	@FXML
	private TextField emailId;

	@FXML
	private TextField landLineNo;

	@FXML
	private TextField mobileNo;

	@FXML
	private ComboBox<String> country;

	@FXML
	private ComboBox<String> state;

	@FXML
	private ComboBox<String> district;

	@FXML
	private ComboBox<String> region;

	@FXML
	private ComboBox<String> pin;

	@FXML
	private TextField parentName;

	@FXML
	private TextField uinId;

	private boolean toggleAgeORDobField = false;

	private boolean isChild = false;

	@Autowired
	private Environment environment;

	@Autowired
	private RegistrationOfficerPacketController registrationOfficerPacketController;

	@FXML
	private void initialize() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME,
				environment.getProperty(APPLICATION_ID), "Entering the LOGIN_CONTROLLER");

		switchedOn.set(false);
		ageDatePicker.setDisable(false);
		ageField.setDisable(true);
		disableFutureDays();
		toggleFunction();
		ageFieldValidations();
		ageValidationInDatePicker();
		dateFormatter();

		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME,
				environment.getProperty(APPLICATION_ID), "Getting the list of countries");
		country.getItems().addAll(RegistrationConstants.getCountries());

		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME,
				environment.getProperty(APPLICATION_ID), "Exiting the REGISTRATION_CONTROLLER");
	}

	/**
	 * 
	 * Saving the detail into concerned DTO'S
	 * 
	 */
	public void saveDetail() {
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Saving the fields to DTO");
		RegistrationDTO registrationDTO = new RegistrationDTO();
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		LocationDTO locationDto = new LocationDTO();
		AddressDTO addressDto = new AddressDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		OSIDataDTO osiDataDto = new OSIDataDTO();
		if (validateTheFields()) {
			demographicInfoDTO.setFullName(fullName.getText());
			if(ageDatePicker.getValue() != null) {
				demographicInfoDTO.setDateOfBirth(
					Date.from(ageDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
			}
			demographicInfoDTO.setAge(ageField.getText());
			demographicInfoDTO.setGender(gender.getValue());
			addressDto.setLine1(addressLine1.getText());
			addressDto.setLine2(addressLine2.getText());
			addressDto.setLine3(addressLine3.getText());
			locationDto.setLine4(country.getValue());
			locationDto.setLine5(state.getValue());
			locationDto.setLine6(district.getValue());
			locationDto.setLine7(region.getValue());
			locationDto.setLine8(pin.getValue());
			addressDto.setLocationDTO(locationDto);
			demographicInfoDTO.setAddressDTO(addressDto);
			demographicInfoDTO.setMobile(mobileNo.getText());
			demographicInfoDTO.setLandLine(landLineNo.getText());
			demographicInfoDTO.setChild(isChild);
			if (isChild) {
				if (uinId.getText().length() == 28) {
					demographicDTO.setIntroducerRID(uinId.getText());
				} else {
					demographicDTO.setIntroducerUIN(uinId.getText());
				}
				osiDataDto.setIntroducerType(IntroducerType.PARENT.getCode());
				osiDataDto.setIntroducerName(parentName.getText());
			}
			demographicDTO.setDemoInUserLang(demographicInfoDTO);
			osiDataDto.setOperatorID(SessionContext.getInstance().getUserContext().getUserId());

			registrationDTO.setPreRegistrationId(preRegistrationId.getText());
			registrationDTO.setOsiDataDTO(osiDataDto);
			registrationDTO.setDemographicDTO(demographicDTO);

			LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME,
					environment.getProperty(APPLICATION_ID), "Saved the fields to DTO");

			registrationOfficerPacketController.showReciept(registrationDTO);
		}

	}

	/**
	 * 
	 * get the states from service class
	 * 
	 */
	public void getStates() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME,
				environment.getProperty(APPLICATION_ID), "Getting the states");
		state.getItems().addAll(RegistrationConstants.getStates());
	}

	/**
	 * 
	 * get the districts from service class
	 * 
	 */
	public void getDistricts() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME,
				environment.getProperty(APPLICATION_ID), "Getting the districts");
		district.getItems().addAll(RegistrationConstants.getDistricts());
	}

	/**
	 * 
	 * get the regions from service class
	 * 
	 */
	public void getRegions() {
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Getting the regions");
		region.getItems().addAll(RegistrationConstants.getRegions());
	}

	/**
	 * 
	 * get the pins from service class
	 * 
	 */
	public void getPins() {
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Getting the Pins");
		pin.getItems().addAll(RegistrationConstants.getPins());
	}

	/**
	 * Validating the age field for the child/Infant check.
	 */
	public void ageValidationInDatePicker() {
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Validating the age given by DatePiker");
		if (ageDatePicker.getValue() != null) {
			LocalDate selectedDate = ageDatePicker.getValue();
			Date date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			long ageInMilliSeconds = new Date().getTime() - date.getTime();
			long ageInDays = TimeUnit.MILLISECONDS.toDays(ageInMilliSeconds);
			int age = (int) ageInDays / 365;
			if (age < 5) {
				childSpecificFields.setVisible(true);
				isChild = true;
			} else {
				childSpecificFields.setVisible(false);
			}
		}
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Validated the age given by DatePiker");
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

		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Future dates disabled");
	}

	/**
	 * To restrict the user not to enter any values other than integer values.
	 */
	private void ageFieldValidations() {
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME,
				environment.getProperty(APPLICATION_ID), "Validating the age given by age field");
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
				if (ageValue < 5 && ageValue != 0) {
					childSpecificFields.setVisible(true);
					isChild = true;
				} else {
					childSpecificFields.setVisible(false);
				}
			}
		});
		LOGGER.debug("REGISTRATION_CONTROLLER", APPLICATION_NAME,
				environment.getProperty(APPLICATION_ID), "Validating the age given by age field");
	}

	/**
	 * Toggle functionality between age field and date picker.
	 */
	private void toggleFunction() {
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID),
				"Entering into toggle function for toggle label 1 and toggle level 2");

		toggleLabel1.setStyle("-fx-background-color: grey;");
		toggleLabel2.setStyle("-fx-background-color: white;");
		switchedOn.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					toggleLabel1.setStyle("-fx-background-color: white;");
					toggleLabel2.setStyle("-fx-background-color: grey;");
					ageField.clear();
					ageDatePicker.setValue(null);
					parentName.clear();
					uinId.clear();
					childSpecificFields.setVisible(false);
					ageDatePicker.setDisable(true);
					ageField.setDisable(false);
					toggleAgeORDobField = true;

				} else {
					toggleLabel1.setStyle("-fx-background-color: grey;");
					toggleLabel2.setStyle("-fx-background-color: white;");
					ageField.clear();
					ageDatePicker.setValue(null);
					parentName.clear();
					uinId.clear();
					childSpecificFields.setVisible(false);
					ageDatePicker.setDisable(false);
					ageField.setDisable(true);
					toggleAgeORDobField = false;

				}
			}
		});

		toggleLabel1.setOnMouseClicked((event) -> {
			switchedOn.set(!switchedOn.get());
		});
		toggleLabel2.setOnMouseClicked((event) -> {
			switchedOn.set(!switchedOn.get());
		});
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID),
				"Exiting the toggle function for toggle label 1 and toggle level 2");
	}

	/**
	 * To dispaly the selected date in the date picker in specific
	 * format("dd-mm-yyyy").
	 */
	private void dateFormatter() {
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Validating the date format");
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
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Going to home page");

		try {
			BaseController.load(getClass().getResource("/fxml/RegistrationOfficerLayout.fxml"));
		} catch (IOException ioException) {
			LOGGER.error("REGISTRATION - REGSITRATION_HOME_PAGE_LAYOUT_LOADING_FAILED",
					APPLICATION_NAME, environment.getProperty(APPLICATION_ID),
					ioException.getMessage());
		}
	}

	/**
	 * 
	 * Validates the entered fields
	 * 
	 */
	private boolean validateTheFields() {
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Validating the fields");
		boolean gotoNext = false;
		if (validateRegex(fullName, "([A-z]+\\s?\\.?)+")) {
			generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
					RegistrationConstants.FIRST_NAME_EMPTY, "Numbers are not allowed");
			fullName.requestFocus();
		} else {
			if (gender.getValue() == null) {
				generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
						RegistrationConstants.GENDER_EMPTY);
				gender.requestFocus();
			} else {
				if (validateRegex(addressLine1, "^.{6,20}$")) {
					generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
							RegistrationConstants.ADDRESS_LINE_1_EMPTY, RegistrationConstants.ADDRESS_LINE_WARNING);
					addressLine1.requestFocus();
				} else {
					if (validateRegex(addressLine2, "^.{6,20}$")) {
						generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
								RegistrationConstants.ADDRESS_LINE_2_EMPTY,
								RegistrationConstants.ADDRESS_LINE_WARNING);
						addressLine2.requestFocus();
					} else {
						if (country.getValue() == null) {
							generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
									RegistrationConstants.COUNTRY_EMPTY);
							country.requestFocus();
						} else {
							if (state.getValue() == null) {
								generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
										RegistrationConstants.STATE_EMPTY);
								state.requestFocus();
							} else {
								if (district.getValue() == null) {
									generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
											RegistrationConstants.DISTRICT_EMPTY);
									district.requestFocus();
								} else {
									if (region.getValue() == null) {
										generateAlert("Error", AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
												RegistrationConstants.REGION_EMPTY);
										region.requestFocus();
									} else {
										if (pin.getValue() == null) {
											generateAlert("Error",
													AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
													RegistrationConstants.PIN_EMPTY);
											pin.requestFocus();
										} else {
											if (validateRegex(mobileNo, "\\d{1,2}\\-\\d{1,4}\\-\\d{1,4}")) {
												generateAlert("Error",
														AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
														RegistrationConstants.MOBILE_NUMBER_EMPTY,
														RegistrationConstants.MOBILE_NUMBER_EXAMPLE);
												mobileNo.requestFocus();
											} else {
												if (validateRegex(landLineNo, "\\d{1,2}\\-\\d{1,4}")) {
													generateAlert("Error",
															AlertType.valueOf(RegistrationConstants.ALERT_ERROR),
															RegistrationConstants.LAND_LINE_NUMBER_EMPTY,
															RegistrationConstants.LAND_LINE_NUMBER_EXAMPLE);
													landLineNo.requestFocus();
												} else {
													if (toggleAgeORDobField) {
														if (validateRegex(ageField, "\\d{1,2}")) {
															generateAlert("Error",
																	AlertType.valueOf(
																			RegistrationConstants.ALERT_ERROR),
																	RegistrationConstants.AGE_EMPTY,
																	RegistrationConstants.AGE_WARNING);
															ageField.requestFocus();
														} else {
															if (isChild) {
																gotoNext = getParentToggle();
															} else {
																gotoNext = true;
															}
														}
													} else if (!toggleAgeORDobField) {
														if (ageDatePicker.getValue() == null) {
															generateAlert("Error",
																	AlertType.valueOf(
																			RegistrationConstants.ALERT_ERROR),
																	RegistrationConstants.DATE_OF_BIRTH_EMPTY);
															ageDatePicker.requestFocus();
														} else {
															if (isChild) {
																gotoNext = getParentToggle();
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
		}
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Validated the fields");
		return gotoNext;
	}

	/**
	 * 
	 * Toggles the parent fields
	 * 
	 */
	private boolean getParentToggle() {
		LOGGER.debug("REGISTRATION_CONTROLLER", environment.getProperty(APPLICATION_NAME),
				environment.getProperty(APPLICATION_ID), "Toggling for parent/guardian fields");
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
}