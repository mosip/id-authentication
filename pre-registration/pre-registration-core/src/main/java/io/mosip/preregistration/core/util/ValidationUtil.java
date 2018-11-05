package io.mosip.preregistration.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {
	public static boolean emailValidator(String loginId) {
		String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern pattern = Pattern.compile(emailExpression,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(loginId);
		if (matcher.matches()) {
			return true;
		} else
			return false;

	}
	
	public static boolean phoneValidator(String loginId) {
		String phoneExpression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
		Pattern pattern = Pattern.compile(phoneExpression);
		Matcher matcher = pattern.matcher(loginId);
		if (matcher.matches()) {
			return true;
		} else
			return false;

	}

}
