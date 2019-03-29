package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.LocalDate;

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

@Component
public class DateValidation extends BaseController {

	private static final Logger LOGGER = AppConfig.getLogger(DateValidation.class);
	@Autowired
	private Validations validation;

	public void validateDate(Pane parentPane, TextField date, TextField month, TextField year, Validations validations,
			FXUtils fxUtils, TextField localField) {

		try {
			fxUtils.validateOnType(parentPane, date, validation, localField, false);
			date.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if ((newValue == null || newValue.isEmpty()) && !date.isFocused()) {
					fxUtils.hideLabel(parentPane, date);
				}
				validateTheDate(date, month, year);
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

	public void validateMonth(Pane parentPane, TextField date, TextField month, TextField year, Validations validations,
			FXUtils fxUtils, TextField localField) {
		try {
			fxUtils.validateOnType(parentPane, month, validation, localField, false);
			month.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if ((newValue == null || newValue.isEmpty()) && !month.isFocused()) {
					fxUtils.hideLabel(parentPane, month);
				}
				validateTheDate(date, month, year);
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

	private void validateTheDate(TextField date, TextField month, TextField year) {
		try {
			int yearVal;
			int monthVal;
			int dateVal;
			LocalDate localDate = LocalDate.now();
			if (year != null) {
				if (year.getText().matches("\\d{4}")) {
					yearVal = Integer.parseInt(year.getText());
					if (yearVal == localDate.getYear()) {
						if (month != null) {
							if (month.getText().matches("\\d+")) {
								monthVal = Integer.parseInt(month.getText());
								if (monthVal > localDate.getMonth().getValue()) {
									month.setText("1");
								}
								if (monthVal == localDate.getMonth().getValue()) {
									if (date != null) {
										if (date.getText().matches("\\d+")) {
											dateVal = Integer.parseInt(date.getText());
											if (dateVal > localDate.getDayOfMonth()) {
												date.setText("1");
											}
										}
									}
								}
							}
						}
					}
				}

			}
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

	public void validateYear(Pane parentPane, TextField date, TextField month, TextField year, Validations validations,
			FXUtils fxUtils, TextField localField) {
		try {
			fxUtils.validateOnType(parentPane, year, validation, localField, false);
			year.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if ((newValue == null || newValue.isEmpty()) && !year.isFocused()) {
					fxUtils.hideLabel(parentPane, year);
				}
				if (year.getText().matches("\\d{4}")) {
					int yearVal = Integer.parseInt(year.getText());
					LocalDate localDate = LocalDate.now();
					int minYear = 1900;
					if (yearVal < minYear || yearVal > localDate.getYear()) {
						year.setText(oldValue);
					}
				}
				validateTheDate(date, month, year);
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error(LoggerConstants.DATE_VALIDATION, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

}
