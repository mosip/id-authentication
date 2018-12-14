package io.mosip.authentication.service.helper;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;

@Component
public class DateHelper {
	
	@Autowired
	private Environment env;
	
	public Date convertStringToDate(String inputDate) throws IDDataValidationException {
		String dateFormat = env.getProperty("datetime.pattern");
		try {
			return new SimpleDateFormat(dateFormat).parse(inputDate);
		} catch (java.text.ParseException e) {
			throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "inputDate"), e);
		}
	}
	
	public static String[] getDateAndTime(String requestTime, String pattern) {

		String[] dateAndTime = new String[2];

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(pattern);

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(requestTime, isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		ZonedDateTime dateTime3 = ZonedDateTime.now(zone);
		ZonedDateTime dateTime = dateTime3.withZoneSameInstant(zone);
		String date = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		dateAndTime[0] = date;
		String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		dateAndTime[1] = time;

		return dateAndTime;

	}

}
