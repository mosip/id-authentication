package io.mosip.registration.controller.reg;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationUIConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

@Component
public class DateValidation extends BaseController {

	@Autowired
	private Validations validation;

	private Map<String, String> dateMapper;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(DateValidation.class);

	public DateValidation() {
		dateMapper = new HashMap<>();
		dateMapper.put("1", "31");
		dateMapper.put("2", "29");
		dateMapper.put("3", "31");
		dateMapper.put("4", "30");
		dateMapper.put("5", "31");
		dateMapper.put("6", "30");
		dateMapper.put("7", "31");
		dateMapper.put("8", "31");
		dateMapper.put("9", "30");
		dateMapper.put("01", "31");
		dateMapper.put("02", "29");
		dateMapper.put("03", "31");
		dateMapper.put("04", "30");
		dateMapper.put("05", "31");
		dateMapper.put("06", "30");
		dateMapper.put("07", "31");
		dateMapper.put("08", "31");
		dateMapper.put("09", "30");
		dateMapper.put("10", "31");
		dateMapper.put("11", "30");
		dateMapper.put("12", "31");
	}

	public void validateDate(Pane parentPane, TextField date, TextField month, TextField year, Validations validations, FXUtils fxUtils,
			TextField localField) {

		try {
			fxUtils.populateLocalFieldOnType(parentPane,date, validation, localField);
			date.textProperty().addListener((obsValue, oldValue, newValue) -> {
				int dateVal = 1;
				if (date.getText().matches("\\d+")) {
					dateVal = Integer.parseInt(date.getText());
					if (dateVal > 31) {
						date.setText(oldValue);
					}
				}
				if (date.getText().matches("\\d+") && month.getText().matches("\\d+")) {
					try {
						if (Integer.parseInt(date.getText()) > Integer.parseInt(dateMapper.get(month.getText()))) {
							generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.DATE_VALIDATION_MSG);
							date.setText(oldValue);
						}
					} catch (RuntimeException runTimeException) {
						LOGGER.error("DATE VALIDATOINS", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
								runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

					}
				}
				validateTheDate(date, month, year);
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error("DATE VALIDATOINS", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

	public void validateMonth(Pane parentPane,TextField date, TextField month, TextField year, Validations validations, FXUtils fxUtils,
			TextField localField) {
		try {
			fxUtils.populateLocalFieldOnType(parentPane, month, validation, localField);
			month.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if (month.getText().matches("\\d+")) {
					int monthVal = Integer.parseInt(month.getText());
					if (monthVal > 12) {
						month.setText(oldValue);
					}
					if (date.getText().matches("\\d+") && month.getText().matches("\\d+")) {
						try {
							if (Integer.parseInt(date.getText()) > Integer.parseInt(dateMapper.get(month.getText()))) {
								generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.DATE_VALIDATION_MSG);
								date.clear();
							}
						} catch (RuntimeException runTimeException) {
							LOGGER.error("DATE VALIDATOINS", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
									runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

						}
					}
				}
				validateTheDate(date, month, year);
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error("DATE VALIDATOINS", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
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
			LOGGER.error("DATE VALIDATOINS", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

	public void validateYear(Pane parentPane, TextField date, TextField month, TextField year, Validations validations, FXUtils fxUtils,
			TextField localField) {
		try {
			fxUtils.populateLocalFieldOnType(parentPane, year, validation, localField);
			year.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if (year.getText().matches("\\d{4}")) {
					int yearVal = Integer.parseInt(year.getText());
					LocalDate localDate = LocalDate.now();
					int minYear = 1900;
					if (getRegistrationDTOFromSession().getSelectionListDTO() != null
							&& getRegistrationDTOFromSession().getSelectionListDTO().isChild()) {
						minYear = LocalDate.now().getYear() - 5;
					}
					if (yearVal < minYear || yearVal > localDate.getYear()) {
						year.setText(oldValue);
					}

					if (getRegistrationDTOFromSession().getSelectionListDTO() != null
							&& !getRegistrationDTOFromSession().getSelectionListDTO().isChild()
							&& localDate.getYear() - yearVal <= 5) {
						year.setText(oldValue);
					}

					if (!(yearVal % 4 == 0)) {
						dateMapper.put("2", "28");
					}
					if ((yearVal % 4 == 0)) {
						dateMapper.put("2", "29");
					}
					if (date.getText().matches("\\d+") && month.getText().matches("\\d+")) {

						try {
							if (Integer.parseInt(date.getText()) > Integer.parseInt(dateMapper.get(month.getText()))) {
								date.clear();
								generateAlert(RegistrationConstants.ALERT_INFORMATION, RegistrationUIConstants.DATE_VALIDATION_MSG);
							}
						} catch (RuntimeException runTimeException) {
							LOGGER.error("DATE VALIDATOINS", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
									runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

						}

					}
				}
				validateTheDate(date, month, year);
			});
		} catch (RuntimeException runTimeException) {
			LOGGER.error("DATE VALIDATOINS", APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					runTimeException.getMessage() + ExceptionUtils.getStackTrace(runTimeException));

		}
	}

}
