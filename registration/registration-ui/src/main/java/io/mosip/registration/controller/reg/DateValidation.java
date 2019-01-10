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

	public  DateValidation() {
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
		dateMapper.put("10", "31");
		dateMapper.put("11", "30");
		dateMapper.put("12", "31");
	}

	public void validateDate(TextField date, TextField month,Validations validations,FXUtils fxUtils) {

		fxUtils.validateOnType(date, validation);
		date.textProperty().addListener((obsValue, oldValue, newValue) -> {

			if (date.getText().matches("\\d+")) {
				int dateVal = Integer.parseInt(date.getText());
				if (dateVal > 31 || dateVal < 1) {
					date.setText(oldValue);
				}
			}
			if (date.getText().matches("\\d+") && month.getText().matches("\\d+")) {
				if (Integer.parseInt(date.getText()) > Integer.parseInt(dateMapper.get(month.getText()))) {
					generateAlert("wrong");
				}
			}
		});
	}

	public void validateMonth(TextField date, TextField month,Validations validations,FXUtils fxUtils) {
		fxUtils.validateOnType(month, validation);
		month.textProperty().addListener((obsValue, oldValue, newValue) -> {
			if (month.getText().matches("\\d+")) {
				int monthVal = Integer.parseInt(month.getText());
				if (monthVal > 12 || monthVal < 1) {
					month.setText(oldValue);
				}
				if (date.getText().matches("\\d+") && month.getText().matches("\\d+")) {
					if (Integer.parseInt(date.getText()) > Integer.parseInt(dateMapper.get(month.getText()))) {
						generateAlert("wrong");
						date.clear();
					}
				}
			}
		});

	}

	public void validateYear(TextField date, TextField month, TextField year,Validations validations,FXUtils fxUtils) {
		fxUtils.validateOnType(year, validation);
		year.textProperty().addListener((obsValue, oldValue, newValue) -> {
			if (year.getText().matches("\\d{4}")) {
				int yearVal = Integer.parseInt(year.getText());
				int monthVal=Integer.parseInt(month.getText());
				int dateVal=Integer.parseInt(date.getText());
				LocalDate localDate=LocalDate.now();
				if (yearVal < 1900 || yearVal > localDate.getYear()||monthVal>localDate.getMonthValue()||dateVal>localDate.getDayOfMonth()) {
					year.setText(oldValue);
				}
				if (!(yearVal % 4 == 0)) {
					dateMapper.put("2", "28");
				}
				if ((yearVal % 4 == 0)) {
					dateMapper.put("2", "29");
				}
				if (date.getText().matches("\\d+") && month.getText().matches("\\d+")) {

					if (Integer.parseInt(date.getText()) > Integer.parseInt(dateMapper.get(month.getText()))) {
						date.clear();
						generateAlert("wrong");
					}

				}
			}
		});

	}


}
