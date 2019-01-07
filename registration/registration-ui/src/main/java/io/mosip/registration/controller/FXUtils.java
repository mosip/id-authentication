package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.controller.reg.RegistrationController;
import io.mosip.registration.controller.reg.Validations;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
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

	private static FXUtils fxUtils = null;

	public static FXUtils getInstance() {
		if (fxUtils == null)
			fxUtils = new FXUtils();

		return fxUtils;
	}

	/**
	 * Validator method for field during onType
	 */
	public void validateOnType(TextField field, Validations validation) {
		field.textProperty().addListener((obsValue, oldValue, newValue) -> {
			if (!validation.validateTextField(field, field.getId() + "_ontype",
					(String) SessionContext.getInstance().getMapObject().get(RegistrationConstants.IS_CONSOLIDATED))) {
				field.setText(oldValue);
			}
		});
	}

	/**
	 * Validator method for field during onType and the local field population
	 */
	public void validateOnType(TextField field, Validations validation, TextField localField) {
		field.textProperty().addListener((obsValue, oldValue, newValue) -> {
			if (!validation.validateTextField(field, field.getId() + "_ontype",
					(String) SessionContext.getInstance().getMapObject().get(RegistrationConstants.IS_CONSOLIDATED))) {
				field.setText(oldValue);
			} else {
				localField.setText(field.getText());
			}
		});

	}

	/**
	 * To display the selected date in the date picker in specific
	 * format("dd-mm-yyyy").
	 */
	public void dateFormatter(DatePicker ageDatePicker) {
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
	 * Disabling the future days in the date picker calendar.
	 */
	public void disableFutureDays(DatePicker ageDatePicker) {
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

}
