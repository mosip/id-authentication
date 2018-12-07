package io.mosip.preregistration.core.util;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.constants.RequestCodes;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exceptions.InvalidRequestParameterException;

public class ValidationUtil {
	
	private ValidationUtil() {
		
	}
	
	public static boolean emailValidator(String loginId) {
		String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern pattern = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(loginId);
		return matcher.matches();
	}

	public static boolean phoneValidator(String loginId) {
		String phoneExpression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
		Pattern pattern = Pattern.compile(phoneExpression);
		Matcher matcher = pattern.matcher(loginId);
		return matcher.matches();
	}

	public static InvalidRequestParameterException requestValidator(Map<String, String> requestMap,
			Map<String, String> requiredRequestMap) {
		for (String key : requestMap.keySet()) {
			if (key.equals(RequestCodes.ID)
					&& (requestMap.get(RequestCodes.ID) == null || !requestMap.get(RequestCodes.ID).equals(requiredRequestMap.get(RequestCodes.ID)))) {
				return new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.toString(),
						ErrorMessages.INVALID_REQUEST_ID.toString());
			} else if (key.equals(RequestCodes.VER) && (requestMap.get(RequestCodes.VER) == null
					|| !requestMap.get(RequestCodes.VER).equals(requiredRequestMap.get(RequestCodes.VER)))) {
				return new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_002.toString(),
						ErrorMessages.INVALID_REQUEST_VERSION.toString());
			} else if (key.equals(RequestCodes.REQ_TIME) && requestMap.get(RequestCodes.REQ_TIME) == null) {
				return new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_003.toString(),
						ErrorMessages.INVALID_REQUEST_DATETIME.toString());
			} else if (key.equals(RequestCodes.REQ_TIME) && requestMap.get(RequestCodes.REQ_TIME) != null) {
				try {
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").parse(requestMap.get(RequestCodes.REQ_TIME));
				} catch (Exception ex) {
					return new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_003.toString(),
							ErrorMessages.INVALID_REQUEST_DATETIME.toString());
				}
			} else if (key.equals(RequestCodes.REQUEST)
					&& (requestMap.get(RequestCodes.REQUEST) == null || requestMap.get(RequestCodes.REQUEST).equals(""))) {
				return new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_004.toString(),
						ErrorMessages.INVALID_REQUEST_BODY.toString());
			}
		}
		return null;
	}

}
