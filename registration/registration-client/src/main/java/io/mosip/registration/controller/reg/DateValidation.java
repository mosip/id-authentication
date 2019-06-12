package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * Class for validating the date fields
 * 
 * @author Taleev.Aalam
 * @author Balaji
 * @since 1.0.0
 *
 */
@Component
public class DateValidation extends BaseController {

	private static final Logger LOGGER = AppConfig.getLogger(DateValidation.class);
	@Autowired
	private Validations validation;

	int maxAge = 0;

	/**
	 * Validate the date and populate its corresponding local or secondary language
	 * field if date is valid
	 *
	 * @param parentPane  the {@link Pane} containing the date fields
	 * @param date        the date(dd) {@link TextField}
	 * @param month       the month {@link TextField}
	 * @param year        the year {@link TextField}
	 * @param validations the instance of {@link Validations}
	 * @param fxUtils     the instance of {@link FXUtils}
	 * @param localField  the local field to be populated if input is valid.
	 */
	public void validateDate(Pane parentPane, TextField date, TextField month, TextField year, Validations validations,
			FXUtils fxUtils, TextField localField, TextField ageField, TextField ageLocalField, Label dobMessage) {
		if (maxAge == 0)
			maxAge = Integer.parseInt(getValueFromApplicationContext(RegistrationConstants.MAX_AGE));
		try {
			fxUtils.validateOnType(parentPane, date, validation, localField, false);
			date.textProperty().addListener((obsValue, oldValue, newValue) -> {
				populateAge(date, month, year, ageField, ageLocalField, dobMessage);
			});

		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

	/**
	 * Validate the month and populate its corresponding local or secondary language
	 * field if month is valid
	 *
	 * @param parentPane  the {@link Pane} containing the date fields
	 * @param date        the date(dd) {@link TextField}
	 * @param month       the month {@link TextField}
	 * @param year        the year {@link TextField}
	 * @param validations the instance of {@link Validations}
	 * @param fxUtils     the instance of {@link FXUtils}
	 * @param localField  the local field to be populated if input is valid.
	 */
	public void validateMonth(Pane parentPane, TextField date, TextField month, TextField year, Validations validations,
			FXUtils fxUtils, TextField localField, TextField ageField, TextField ageLocalField, Label dobMessage) {
		try {			
				fxUtils.validateOnType(parentPane, month, validation, localField, false);
				month.textProperty().addListener((obsValue, oldValue, newValue) -> {
					populateAge(date, month, year, ageField, ageLocalField, dobMessage);
				});			
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

	/**
	 * Validate the year and populate its corresponding local or secondary language
	 * field if year is valid
	 *
	 * @param parentPane  the {@link Pane} containing the date fields
	 * @param date        the date(dd) {@link TextField}
	 * @param month       the month {@link TextField}
	 * @param year        the year {@link TextField}
	 * @param validations the instance of {@link Validations}
	 * @param fxUtils     the instance of {@link FXUtils}
	 * @param localField  the local field to be populated if input is valid.
	 */
	public void validateYear(Pane parentPane, TextField date, TextField month, TextField year, Validations validations,
			FXUtils fxUtils, TextField localField, TextField ageField, TextField ageLocalField, Label dobMessage) {
		try {
			fxUtils.validateOnType(parentPane, year, validation, localField, false);
			year.textProperty().addListener((obsValue, oldValue, newValue) -> {
				populateAge(date, month, year, ageField, ageLocalField, dobMessage);
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}
	
	private void populateAge(TextField date, TextField month, TextField year, TextField ageField,
			TextField ageLocalField, Label dobMessage) {

		if (date != null && month != null && year != null) {
			if (!date.getText().isEmpty() && !month.getText().isEmpty() && !year.getText().isEmpty()
					&& year.getText().matches(RegistrationConstants.FOUR_NUMBER_REGEX)) {
				try {
					LocalDate givenDate = LocalDate.of(Integer.parseInt(year.getText()),
							Integer.parseInt(month.getText()), Integer.parseInt(date.getText()));
					LocalDate localDate = LocalDate.now();

					if (localDate.compareTo(givenDate) >= 0) {

						int age = Period.between(givenDate, localDate).getYears();
						ageField.setText(age + "");
						ageLocalField.setText(age + "");
						dobMessage.setText("");
						dobMessage.setVisible(false);
					} else {
						ageField.clear();
						ageLocalField.clear();
						dobMessage.setText(RegistrationUIConstants.FUTURE_DOB);
						dobMessage.setVisible(true);
					}
				} catch (Exception exception) {
					setErrorMsg(ageField, dobMessage);
					LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME,
							RegistrationConstants.APPLICATION_ID,
							exception.getMessage() + ExceptionUtils.getStackTrace(exception));
				}
			} else if ((!date.getText().isEmpty() && Integer.parseInt(date.getText()) > 31)
					|| (!month.getText().isEmpty() && Integer.parseInt(month.getText()) > 12)
					|| (!year.getText().isEmpty() && Integer.parseInt(year.getText()) > LocalDate.now().getYear())) {
				setErrorMsg(ageField, dobMessage);
			}
		} 
	}

	private void setErrorMsg(TextField ageField, Label dobMessage) {
		dobMessage.setText(RegistrationUIConstants.INVALID_DATE);
		ageField.clear();
		dobMessage.setVisible(true);
	}

}
