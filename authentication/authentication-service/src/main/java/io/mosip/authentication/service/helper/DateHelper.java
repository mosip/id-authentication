package io.mosip.authentication.service.helper;

import java.text.SimpleDateFormat;
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

}
