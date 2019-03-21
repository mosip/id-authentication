package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.OptionalInt;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.transliteration.spi.Transliteration;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.controller.reg.Validations;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.GenderDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

/**
 * Class for JavaFx utilities operation
 * 
 * @author Taleev.Aalam
 * @since 1.0.0
 *
 */
public class FXUtils {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationController.class);

	private Transliteration<String> transliteration;
	private static FXUtils fxUtils = null;
	private static String promptText = RegistrationConstants.EMPTY;

	public static FXUtils getInstance() {
		if (fxUtils == null)
			fxUtils = new FXUtils();

		return fxUtils;
	}

	/**
	 * Listener to change the style when field is selected for
	 */
	public void listenOnSelectedCheckBox(CheckBox field) {

		field.selectedProperty().addListener((obsValue, oldValue, newValue) -> {
			if (newValue) {
				field.getStyleClass().remove("updateUinCheckBox");
				field.getStyleClass().add("updateUinCheckBoxSelected");
			} else {
				field.getStyleClass().remove("updateUinCheckBoxSelected");
				field.getStyleClass().add("updateUinCheckBox");
			}
		});
	}

	private FXUtils() {

	}

	/**
	 * @param transliteration
	 *            the transliteration to set
	 */
	public void setTransliteration(Transliteration<String> transliteration) {
		this.transliteration = transliteration;
	}

	/**
	 * Validator method for field during onType
	 */
	public void validateOnType(Pane parentPane, TextField field, Validations validation) {
		field.textProperty().addListener((obsValue, oldValue, newValue) -> {
			if (!validation.validateTextField(parentPane, field, field.getId() + "_ontype",
					(String) SessionContext.map().get(RegistrationConstants.IS_CONSOLIDATED))) {
				field.setText(oldValue);
			}
		});
	}

	public void populateLocalComboBox(Pane parentPane, ComboBox<?> applicationField, ComboBox<?> localField) {
		applicationField.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			selectComboBoxValueByCode(localField, applicationField.getValue());
			((Label) parentPane.lookup(RegistrationConstants.HASH + applicationField.getId() + RegistrationConstants.LABEL)).setVisible(true);
			((Label) parentPane.lookup(RegistrationConstants.HASH + localField.getId() + RegistrationConstants.LABEL)).setVisible(true);
			((Label) parentPane.lookup(RegistrationConstants.HASH + applicationField.getId() + RegistrationConstants.MESSAGE)).setVisible(false);
			((Label) parentPane.lookup(RegistrationConstants.HASH + localField.getId() + RegistrationConstants.MESSAGE)).setVisible(false);
		});
	}

	/**
	 * Validator method for field during onType and the local field population
	 */
	public void validateOnType(Pane parentPane, TextField field, Validations validation, TextField localField) {

		focusUnfocusListener(parentPane, field, localField);

		field.textProperty().addListener((obsValue, oldValue, newValue) -> {
			if (!validation.validateTextField(parentPane, field, field.getId() + "_ontype",
					(String) SessionContext.map().get(RegistrationConstants.IS_CONSOLIDATED))) {
				field.setText(oldValue);
			} else {
				if (localField != null) {
					localField.setText(transliteration.transliterate(ApplicationContext.applicationLanguage(),
							ApplicationContext.localLanguage(), field.getText()));
				}
			}
			field.requestFocus();
		});
		
		onTypeFocusUnfocusListener(parentPane, localField);

	}

	public void onTypeFocusUnfocusListener(Pane parentPane, TextField field) {
		if(field!=null) {
			field.textProperty().addListener((obsValue, oldValue, newValue) -> {
				
				if(newValue.length()>0) {
				
					try {
						((Label) parentPane.lookup(RegistrationConstants.HASH + field.getId() + RegistrationConstants.LABEL)).setVisible(true);
						if (field.getId().matches("dd|mm|yyyy|ddLocalLanguage|mmLocalLanguage|yyyyLocalLanguage")) {
							((Label) parentPane.lookup(RegistrationConstants.HASH + RegistrationConstants.DOB_MESSAGE)).setVisible(false);
						} else {
							((Label) parentPane.lookup(RegistrationConstants.HASH + field.getId() + RegistrationConstants.MESSAGE)).setVisible(false);
						}
					} catch (RuntimeException runtimeException) {
						LOGGER.info("ID NOT FOUND ", APPLICATION_NAME,
								RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
					}
				}else {
					((Label) parentPane.lookup(RegistrationConstants.HASH + field.getId() + RegistrationConstants.LABEL)).setVisible(false);
				}

			});

		}
	}

	public void focusUnfocusListener(Pane parentPane, TextField field, TextField localField) {
		focusAction(parentPane, field);
		focusAction(parentPane, localField);
	}

	private void focusAction(Pane parentPane, TextField field) {
		if(field!=null) {
			field.focusedProperty().addListener((obsValue, oldValue, newValue) -> {
			if (newValue) {
				try {
					System.out.println(field.getId());
					((Label) parentPane.lookup(RegistrationConstants.HASH + field.getId() + RegistrationConstants.LABEL)).setVisible(true);
					promptText = ((TextField) parentPane.lookup(RegistrationConstants.HASH + field.getId())).getPromptText();
					((TextField) parentPane.lookup(RegistrationConstants.HASH + field.getId())).setPromptText(null);
					if (field.getId().matches("dd|mm|yyyy|ddLocalLanguage|mmLocalLanguage|yyyyLocalLanguage")) {
						((Label) parentPane.lookup(RegistrationConstants.HASH + RegistrationConstants.DOB_MESSAGE)).setVisible(false);
					} else {
						((Label) parentPane.lookup(RegistrationConstants.HASH + field.getId() + RegistrationConstants.MESSAGE)).setVisible(false);
					}
				} catch (RuntimeException runtimeException) {
					LOGGER.info("ID NOT FOUND ", APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
				}
			} else {
				((TextField) parentPane.lookup(RegistrationConstants.HASH + field.getId())).setPromptText(promptText);
				if (!(field.getText().length() > 0)) {
					try {
						((Label) parentPane.lookup(RegistrationConstants.HASH + field.getId() + RegistrationConstants.LABEL)).setVisible(false);
					}  catch (RuntimeException runtimeException) {
						LOGGER.info("ID NOT FOUND", APPLICATION_NAME,
								RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
					}
				}
			}

		});
	}}

	/**
	 * Populate the local field value based on the application field.
	 * Transliteration will not done for these fields
	 */
	public void populateLocalFieldOnType(Pane parentPane, TextField field, Validations validation,
			TextField localField) {
		focusUnfocusListener(parentPane, field, localField);
		field.textProperty().addListener((obsValue, oldValue, newValue) -> {
			if (!validation.validateTextField(parentPane, field, field.getId() + "_ontype",
					(String) SessionContext.map().get(RegistrationConstants.IS_CONSOLIDATED))) {
				field.setText(oldValue);
			} else {
				if (localField != null) {
					localField.setText(field.getText());
				}
			}
			field.requestFocus();
		});
		
		onTypeFocusUnfocusListener(parentPane, localField);

	}

	public void dobListener(TextField field, TextField fieldToPopulate, String regex) {
		field.textProperty().addListener((obsValue, oldValue, newValue) -> {
			if (field.getText().matches(regex)) {
				int year = Integer.parseInt(field.getText());
				int age = LocalDate.now().getYear() - year;
				if (age >= 0 && age <= 118) {
					fieldToPopulate.setText(RegistrationConstants.EMPTY + age);
				}
			}
		});
	}

	/**
	 * To display the selected date in the date picker in specific
	 * format("dd-mm-yyyy").
	 */
	public void dateFormatter(DatePicker ageDatePicker) {
		try {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the date format");

			ageDatePicker.setConverter(new StringConverter<LocalDate>() {
				String pattern = "dd-MM-yyyy";
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

				{
					ageDatePicker.setPromptText(pattern.toLowerCase());
				}

				@Override
				public String toString(LocalDate date) {
					return date != null ? dateFormatter.format(date) : RegistrationConstants.EMPTY;
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
	 * Disabling the future days in the date picker calendar.
	 */
	public void disableFutureDays(DatePicker ageDatePicker) {
		try {
			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Disabling future dates");

			ageDatePicker.setDayCellFactory(picker -> new DateCell() {
				@Override
				public void updateItem(LocalDate date, boolean empty) {
					super.updateItem(date, empty);
					LocalDate today = LocalDate.now();

					setDisable(empty || date.compareTo(today) > 0);
				}
			});

			LOGGER.info(RegistrationConstants.REGISTRATION_CONTROLLER, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Future dates disabled");
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - DISABLE FUTURE DATE FAILED", APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
		}
	}

	private void selectComboBoxValueByCode(ComboBox<?> comboBox, Object selectedOption) {
		ObservableList<?> comboBoxValues = comboBox.getItems();

		if (!comboBoxValues.isEmpty() && selectedOption != null) {
			IntPredicate findIndexOfSelectedItem = null;
			if (comboBoxValues.get(0) instanceof LocationDto && selectedOption instanceof LocationDto) {
				findIndexOfSelectedItem = index -> ((LocationDto) comboBoxValues.get(index)).getCode()
						.equals(((LocationDto) selectedOption).getCode());
			} else if (comboBoxValues.get(0) instanceof GenderDto && selectedOption instanceof GenderDto) {
				findIndexOfSelectedItem = index -> ((GenderDto) comboBoxValues.get(index)).getCode()
						.equals(((GenderDto) selectedOption).getCode());
			} else if (comboBoxValues.get(0) instanceof DocumentCategoryDto
					&& selectedOption instanceof DocumentCategoryDto) {
				findIndexOfSelectedItem = index -> ((DocumentCategoryDto) comboBoxValues.get(index)).getCode()
						.equals(((DocumentCategoryDto) selectedOption).getCode());
			}

			OptionalInt indexOfSelectedLocation = getIndexOfSelectedItem(comboBoxValues, findIndexOfSelectedItem);

			if (indexOfSelectedLocation.isPresent()) {
				comboBox.getSelectionModel().select(indexOfSelectedLocation.getAsInt());
			}
		}
	}

	/**
	 * Shows the selected value in the combo-box
	 * 
	 * @param comboBox
	 *            the combo-box from which selected value has to be shown
	 * @param selectedValue
	 *            the selected value from the combo-box
	 */
	public void selectComboBoxValue(ComboBox<?> comboBox, String selectedValue) {
		ObservableList<?> comboBoxValues = comboBox.getItems();

		if (!comboBoxValues.isEmpty()) {
			IntPredicate findIndexOfSelectedItem = null;
			if (comboBoxValues.get(0) instanceof LocationDto) {
				findIndexOfSelectedItem = index -> ((LocationDto) comboBoxValues.get(index)).getName()
						.equals(selectedValue);
			} else if (comboBoxValues.get(0) instanceof GenderDto) {
				findIndexOfSelectedItem = index -> ((GenderDto) comboBoxValues.get(index)).getGenderName()
						.equals(selectedValue);
			} else if (comboBoxValues.get(0) instanceof DocumentCategoryDto) {
				findIndexOfSelectedItem = index -> ((DocumentCategoryDto) comboBoxValues.get(index)).getName()
						.equals(selectedValue);
			}

			OptionalInt indexOfSelectedLocation = getIndexOfSelectedItem(comboBoxValues, findIndexOfSelectedItem);

			if (indexOfSelectedLocation.isPresent()) {
				comboBox.getSelectionModel().select(indexOfSelectedLocation.getAsInt());
			}
		}
	}

	private OptionalInt getIndexOfSelectedItem(ObservableList<?> comboBoxValues, IntPredicate lambdaExpression) {
		return IntStream.range(0, comboBoxValues.size()).filter(lambdaExpression).findFirst();
	}

	/**
	 * The custom {@link StringConverter} for displaying only the name in the
	 * combo-box based on the combo-box type
	 * 
	 * @return the custom {@link StringConverter}
	 */
	public <T> StringConverter<T> getStringConverterForComboBox() {
		return new StringConverter<T>() {
			@Override
			public String toString(T object) {
				String value = null;
				if (object instanceof LocationDto) {
					value = ((LocationDto) object).getName();
				} else if (object instanceof GenderDto) {
					value = ((GenderDto) object).getGenderName();
				} else if (object instanceof DocumentCategoryDto) {
					value = ((DocumentCategoryDto) object).getName();
				}
				return value;
			}

			@Override
			public T fromString(String string) {
				return null;
			}
		};
	}

}
