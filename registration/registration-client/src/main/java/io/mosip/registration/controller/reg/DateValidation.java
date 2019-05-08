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
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
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

	/**
	 * Validate the date and populate its corresponding local or secondary
	 * language field if date is valid
	 *
	 * @param parentPane
	 *            the {@link Pane} containing the date fields
	 * @param date
	 *            the date(dd) {@link TextField}
	 * @param month
	 *            the month {@link TextField}
	 * @param year
	 *            the year {@link TextField}
	 * @param validations
	 *            the instance of {@link Validations}
	 * @param fxUtils
	 *            the instance of {@link FXUtils}
	 * @param localField
	 *            the local field to be populated if input is valid.
	 */
	public void validateDate(Pane parentPane, TextField date, TextField month, TextField year, Validations validations,
			FXUtils fxUtils, TextField localField, TextField ageField, TextField ageLocalField) {

		try {
			fxUtils.validateOnType(parentPane, date, validation, localField, false);
			date.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if (!yearValidator(date, month, year, ageField, ageLocalField)
						&& dateMonthYearNotNullOrEmpty(date, month, year)) {
					date.setText(oldValue);
				}
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

	private boolean dateMonthYearNotNullOrEmpty(TextField date, TextField month, TextField year) {
		return (date != null && !date.getText().isEmpty() && month != null && !month.getText().isEmpty() && year != null
				&& !year.getText().isEmpty());
	}

	/**
	 * Validate the month and populate its corresponding local or secondary
	 * language field if month is valid
	 *
	 * @param parentPane
	 *            the {@link Pane} containing the date fields
	 * @param date
	 *            the date(dd) {@link TextField}
	 * @param month
	 *            the month {@link TextField}
	 * @param year
	 *            the year {@link TextField}
	 * @param validations
	 *            the instance of {@link Validations}
	 * @param fxUtils
	 *            the instance of {@link FXUtils}
	 * @param localField
	 *            the local field to be populated if input is valid.
	 */
	public void validateMonth(Pane parentPane, TextField date, TextField month, TextField year, Validations validations,
			FXUtils fxUtils, TextField localField, TextField ageField, TextField ageLocalField) {
		try {
			fxUtils.validateOnType(parentPane, month, validation, localField, false);
			month.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if (!yearValidator(date, month, year, ageField, ageLocalField)
						&& dateMonthYearNotNullOrEmpty(date, month, year)) {
					month.setText(oldValue);
				}
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

	/**
	 * Validates the date
	 *
	 * @param date
	 *            the date(dd) {@link TextField}
	 * @param month
	 *            the month {@link TextField}
	 * @param year
	 *            the year {@link TextField}
	 * @param ageLocalField
	 * @param ageField
	 */
	private boolean yearValidator(TextField date, TextField month, TextField year, TextField ageField,
			TextField ageLocalField) {
		try {
			LocalDate localDate = LocalDate.now();
			if (year != null) {
				return monthValidator(date, month, year, localDate, ageField, ageLocalField);
			}
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));
		}
		return false;
	}

	/**
	 * Validates the month
	 *
	 * @param date
	 *            the date(dd) {@link TextField}
	 * @param month
	 *            the month {@link TextField}
	 * @param ageLocalField
	 * @param ageField
	 * @param year
	 *            the year {@link TextField}
	 * @param LocalDate
	 *            the localDate {@link LocalDate}
	 */
	private boolean monthValidator(TextField date, TextField month, TextField year, LocalDate localDate,
			TextField ageField, TextField ageLocalField) {
		int yearVal=0;
		if(year.getText().matches(RegistrationConstants.FOUR_NUMBER_REGEX)) {
			yearVal  = Integer.parseInt(year.getText());
		}
		if(yearVal==0)
			return true;
		if (month != null && yearVal == localDate.getYear()
				&& month.getText().matches(RegistrationConstants.NUMBER_REGEX)
				&& Integer.parseInt(month.getText()) > localDate.getMonth().getValue()) {
			month.setText(RegistrationConstants.ONE);
		} else {
			return dateValdidator(date, month, year, Integer.parseInt(month.getText()), yearVal, localDate, ageField,
					ageLocalField);
		}
		return false;
	}

	/**
	 * Validates the month
	 *
	 * @param date
	 *            the date(dd) {@link TextField}
	 * @param ageLocalField
	 * @param ageField
	 * @param month
	 *            the month {@link TextField}
	 * @param LocalDate
	 *            the localDate {@link LocalDate}
	 */
	private boolean dateValdidator(TextField date, TextField month, TextField year, int monthVal, int yearValue,
			LocalDate localDate, TextField ageField, TextField ageLocalField) {
		if (date != null && monthVal == localDate.getMonth().getValue()
				&& date.getText().matches(RegistrationConstants.NUMBER_REGEX)
				&& Integer.parseInt(date.getText()) > localDate.getDayOfMonth()) {
			date.setText(RegistrationConstants.ONE);
		} else {
				int dateValue = Integer.parseInt(date.getText());
				int age = Period.between(LocalDate.of(yearValue, monthVal, dateValue), LocalDate.now()).getYears();
				int maxAge = Integer.parseInt(getValueFromApplicationContext(RegistrationConstants.MAX_AGE));
				if (age > maxAge || age == 0) {
					ageField.setText("");
					ageLocalField.setText("");
					return false;
				} else {
					ageField.setText(RegistrationConstants.EMPTY + age);
					ageLocalField.setText(RegistrationConstants.EMPTY + age);
					return true;
				}
		}
		return false;
	}

	/**
	 * Validate the year and populate its corresponding local or secondary
	 * language field if year is valid
	 *
	 * @param parentPane
	 *            the {@link Pane} containing the date fields
	 * @param date
	 *            the date(dd) {@link TextField}
	 * @param month
	 *            the month {@link TextField}
	 * @param year
	 *            the year {@link TextField}
	 * @param validations
	 *            the instance of {@link Validations}
	 * @param fxUtils
	 *            the instance of {@link FXUtils}
	 * @param localField
	 *            the local field to be populated if input is valid.
	 */
	public void validateYear(Pane parentPane, TextField date, TextField month, TextField year, Validations validations,
			FXUtils fxUtils, TextField localField, TextField ageField, TextField ageLocalField) {
		try {
			fxUtils.validateOnType(parentPane, year, validation, localField, false);
			year.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if (!yearValidator(date, month, year, ageField, ageLocalField)
						&& dateMonthYearNotNullOrEmpty(date, month, year)) {
					year.setText(oldValue);
				}
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

}
