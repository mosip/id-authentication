/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception.util;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.transliteration.errorcode.ErrorCodes;
import io.mosip.preregistration.transliteration.errorcode.ErrorMessage;
import io.mosip.preregistration.transliteration.exception.IllegalParamException;
import io.mosip.preregistration.transliteration.exception.JsonValidationException;
import io.mosip.preregistration.transliteration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.transliteration.exception.MissingRequestParameterException;
import io.mosip.preregistration.transliteration.exception.UnSupportedLanguageException;

/**
 * This class is used to catch the exceptions that occur while creating the
 * transliteration application
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class TransliterationExceptionCatcher {
 
	/**
	 * Method to handle the respective exceptions
	 * 
	 * @param ex
	 *            pass the exception
	 */
	public void handle(Exception ex,MainResponseDTO<?> response) {
		 if (ex instanceof DataAccessLayerException) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_TRL_APP_005.getCode(),
					ErrorMessage.PRE_REG_TRANSLITRATION_TABLE_NOT_ACCESSIBLE.getMessage(), ex.getCause());
		} else if (ex instanceof NullPointerException) {
			throw new IllegalParamException(ErrorCodes.PRG_TRL_APP_002.getCode(),
					ErrorMessage.INCORRECT_MANDATORY_FIELDS.getMessage(), ex.getCause(),response);
		} else if (ex instanceof ParseException) {
			throw new io.mosip.preregistration.transliteration.exception.JsonParseException(
					ErrorCodes.PRG_TRL_APP_006.getCode(), ErrorMessage.JSON_PARSING_FAILED.getMessage(), ex.getCause(),response);
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText(),response);
		} else if (ex instanceof MissingRequestParameterException) {
			throw new MissingRequestParameterException(((MissingRequestParameterException) ex).getErrorCode(),
					((MissingRequestParameterException) ex).getErrorText(),response);
		}
		else if (ex instanceof MandatoryFieldRequiredException) {
			throw new MandatoryFieldRequiredException(((MandatoryFieldRequiredException) ex).getErrorCode(),
					((MandatoryFieldRequiredException) ex).getErrorText(),response);
		}
		else if (ex instanceof UnSupportedLanguageException) {
			throw new UnSupportedLanguageException(((UnSupportedLanguageException) ex).getErrorCode(),
					((UnSupportedLanguageException) ex).getErrorText(),response);
		}
	}

}
