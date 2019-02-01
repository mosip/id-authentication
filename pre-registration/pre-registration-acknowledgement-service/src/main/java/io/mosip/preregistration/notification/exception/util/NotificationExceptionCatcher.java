/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.notification.exception.util;

import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.notification.error.ErrorCodes;
import io.mosip.preregistration.notification.error.ErrorMessages;
import io.mosip.preregistration.notification.exception.IOException;
import io.mosip.preregistration.notification.exception.IllegalParamException;
import io.mosip.preregistration.notification.exception.JsonValidationException;
import io.mosip.preregistration.notification.exception.MandatoryFieldException;
import io.mosip.preregistration.notification.exception.MissingRequestParameterException;


/**
 * This class is used to catch the exceptions that occur while creating the
 * acknowledgement application
 * 
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
public class NotificationExceptionCatcher {

	/**
	 * Method to handle the respective exceptions
	 * 
	 * @param ex
	 *            pass the exception
	 */
	public void handle(Exception ex) {
		if (ex instanceof MandatoryFieldException) {
			throw new MandatoryFieldException(ErrorCodes.PRG_ACK_001.getCode(),
					ErrorMessages.MOBILE_NUMBER_OR_EMAIL_ADDRESS_NOT_FILLED.getCode());
		}
		if (ex instanceof IOException) {
			throw new IOException(ErrorCodes.PRG_ACK_005.getCode(), 
					ErrorMessages.INPUT_OUTPUT_EXCEPTION.getCode());
		}
		if (ex instanceof HttpRequestException) {
			throw new JsonValidationException(ErrorCodes.PRG_ACK_003.getCode(),
					ErrorMessages.JSON_HTTP_REQUEST_EXCEPTION.getCode(), ex.getCause());
		} else if (ex instanceof NullPointerException) {
			throw new IllegalParamException(ErrorCodes.PRG_ACK_002.getCode(),
					ErrorMessages.INCORRECT_MANDATORY_FIELDS.getCode(), ex.getCause());
		} else if (ex instanceof JsonParseException) {
			throw new JsonValidationException(ErrorCodes.PRG_ACK_004.getCode(), ErrorMessages.JSON_PARSING_FAILED.getCode(),
					ex.getCause());
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText());
		} else if (ex instanceof MissingRequestParameterException) {
			throw new MissingRequestParameterException(((MissingRequestParameterException) ex).getErrorCode(),
					((MissingRequestParameterException) ex).getErrorText());
		}
	}

}
