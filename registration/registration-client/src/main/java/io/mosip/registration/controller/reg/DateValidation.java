package io.mosip.registration.controller.reg;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.FXUtils;
import javafx.scene.control.TextField;

@Component
public class DateValidation extends BaseController {

	@Autowired
	private Validations validation;

	private Map<String, String> dateMapper;

	public DateValidation() {
		dateMapper = new HashMap<String, String>();
		dateMapper.put("1", "31");
		dateMapper.put("2", "29");
		dateMapper.put("3", "30");
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

	public void validateDate(TextField date, TextField month, TextField year, Validations validations, FXUtils fxUtils,
			TextField localField) {

		try {
			fxUtils.validateOnType(date, validation, localField);
			date.textProperty().addListener((obsValue, oldValue, newValue) -> {
				int dateVal = 1;
				if (date.getText().matches("\\d+")) {
					dateVal = Integer.parseInt(date.getText());
					if (dateVal > 31 || dateVal < 1) {
						date.setText(oldValue);
					}
				}
				if (date.getText().matches("\\d+") && month.getText().matches("\\d+")) {
					try {
						if (Integer.parseInt(date.getText()) > Integer.parseInt(dateMapper.get(month.getText()))) {
							generateAlert("Date", "Please enter the appropriate value");
							date.setText(oldValue);
						}
					} catch (NumberFormatException e) {
					}
				}
				validateTheDate(date, month, year);
			});
		} catch (Exception e) {

		}
	}

	public void validateMonth(TextField date, TextField month, TextField year, Validations validations, FXUtils fxUtils,
			TextField localField) {
		try {
			fxUtils.validateOnType(month, validation, localField);
			month.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if (month.getText().matches("\\d+")) {
					int monthVal = Integer.parseInt(month.getText());
					if (monthVal > 12 || monthVal < 1) {
						month.setText(oldValue);
					}
					if (date.getText().matches("\\d+") && month.getText().matches("\\d+")) {
						try {
							if (Integer.parseInt(date.getText()) > Integer.parseInt(dateMapper.get(month.getText()))) {
								generateAlert("Please enter the appropriate value");
								date.clear();
							}
						} catch (NumberFormatException e) {
						}
					}
				}
				validateTheDate(date, month, year);
			});
		} catch (Exception e) {

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
		} catch (Exception e) {

		}
	}

	public void validateYear(TextField date, TextField month, TextField year, Validations validations, FXUtils fxUtils,
			TextField localField) {
		try {
			fxUtils.validateOnType(year, validation, localField);
			year.textProperty().addListener((obsValue, oldValue, newValue) -> {
				if (year.getText().matches("\\d{4}")) {
					int yearVal = Integer.parseInt(year.getText());
					LocalDate localDate = LocalDate.now();
					if (yearVal < 1900 || yearVal > localDate.getYear()) {
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
								generateAlert("Please enter the appropriate value");
							}
						} catch (NumberFormatException e) {
						}

					}
				}
				validateTheDate(date, month, year);
			});
		} catch (Exception e) {

		}
	}

}
