package io.mosip.preregistration.core.util;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.mosip.preregistration.core.code.RequestCodes;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

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

	public static boolean requestValidator(Map<String, String> requestMap, Map<String, String> requiredRequestMap) {
		for (String key : requestMap.keySet()) {
			if (key.equals(RequestCodes.ID) && (requestMap.get(RequestCodes.ID) == null
					|| !requestMap.get(RequestCodes.ID).equals(requiredRequestMap.get(RequestCodes.ID)))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.toString(),
						ErrorMessages.INVALID_REQUEST_ID.toString());
			} else if (key.equals(RequestCodes.VER) && (requestMap.get(RequestCodes.VER) == null
					|| !requestMap.get(RequestCodes.VER).equals(requiredRequestMap.get(RequestCodes.VER)))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_002.toString(),
						ErrorMessages.INVALID_REQUEST_VERSION.toString());
			} else if (key.equals(RequestCodes.REQ_TIME) && requestMap.get(RequestCodes.REQ_TIME) == null) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_003.toString(),
						ErrorMessages.INVALID_REQUEST_DATETIME.toString());
			} else if (key.equals(RequestCodes.REQ_TIME) && requestMap.get(RequestCodes.REQ_TIME) != null) {
				try {
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(requestMap.get(RequestCodes.REQ_TIME));
				} catch (Exception ex) {
					throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_003.toString(),
							ErrorMessages.INVALID_REQUEST_DATETIME.toString());
				}
			} else if (key.equals(RequestCodes.REQUEST) && (requestMap.get(RequestCodes.REQUEST) == null
					|| requestMap.get(RequestCodes.REQUEST).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_004.toString(),
						ErrorMessages.INVALID_REQUEST_BODY.toString());
			}
		}
		return true;
	}

	public static boolean requstParamValidator(Map<String, String> requestMap) {
		for (String key : requestMap.keySet()) {
			if (key.equals(RequestCodes.USER_ID) && (requestMap.get(RequestCodes.USER_ID) == null
					|| requestMap.get(RequestCodes.USER_ID).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.toString(),
						ErrorMessages.INVALID_REQUEST_ID.toString());
			} else if (key.equals(RequestCodes.PRE_REGISTRATION_ID)
					&& (requestMap.get(RequestCodes.PRE_REGISTRATION_ID) == null
							|| requestMap.get(RequestCodes.PRE_REGISTRATION_ID).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.toString(),
						ErrorMessages.INVALID_PRE_REGISTRATION_ID.toString());
			} else if (key.equals(RequestCodes.STATUS_CODE) && (requestMap.get(RequestCodes.STATUS_CODE) == null
					|| requestMap.get(RequestCodes.STATUS_CODE).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.toString(),
						ErrorMessages.INVALID_STATUS_CODE.toString());
			} else if (key.equals(RequestCodes.FROM_DATE) && (requestMap.get(RequestCodes.FROM_DATE) == null
					|| requestMap.get(RequestCodes.FROM_DATE).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.toString(),
						ErrorMessages.INVALID_DATE.toString());
			}

			else if (key.equals(RequestCodes.TO_DATE) && (requestMap.get(RequestCodes.TO_DATE) == null
					|| requestMap.get(RequestCodes.TO_DATE).equals(""))) {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.toString(),
						ErrorMessages.INVALID_DATE.toString());
			}

		}
		return true;
	}

	public static boolean isvalidPreRegId(String preRegId) {
		if (preRegId.matches("[0-9]+") && preRegId.length() == 14) {
			return true;
		}else {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_CORE_REQ_001.toString(),
					ErrorMessages.INVALID_PRE_REGISTRATION_ID.toString());
		}
	}
}
