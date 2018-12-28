package io.mosip.preregistration.translitration.exception.util;

import org.springframework.boot.json.JsonParseException;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
import io.mosip.preregistration.translitration.errorcode.ErrorCodes;
import io.mosip.preregistration.translitration.errorcode.ErrorMessage;
import io.mosip.preregistration.translitration.exception.IllegalPraramException;
import io.mosip.preregistration.translitration.exception.JsonValidationException;
import io.mosip.preregistration.translitration.exception.MissingRequestParameterException;

public class TranslitrationExceptionCatcher {
	
	public void handle(Exception ex) {
		if (ex instanceof HttpRequestException) {
			throw new JsonValidationException(ErrorCodes.PRG_TRL_004.name(),
					ErrorMessage.JSON_HTTP_REQUEST_EXCEPTION.name(), ex.getCause());
		} else if (ex instanceof DataAccessLayerException) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_TRL_005.toString(),
					ErrorMessage.PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE.toString(), ex.getCause());
		}
		else if (ex instanceof NullPointerException) {
			throw new IllegalPraramException(ErrorCodes.PRG_TRL_005.toString(),
					ErrorMessage.PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE.toString(), ex.getCause());
		}
		else if (ex instanceof ParseException) {
			throw new io.mosip.preregistration.translitration.exception.JsonParseException(ErrorCodes.PRG_TRL_005.toString(),
					ErrorMessage.PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE.toString(), ex.getCause());
		}
		else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText());
		}
		else if (ex instanceof MissingRequestParameterException) {
			throw new MissingRequestParameterException(((MissingRequestParameterException) ex).getErrorCode(),
					((MissingRequestParameterException) ex).getErrorText());
		} 
	}

}
