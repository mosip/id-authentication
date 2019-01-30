/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.acknowledgement.exception.util;

import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.preregistration.acknowledgement.error.ErrorCodes;
import io.mosip.preregistration.acknowledgement.error.ErrorMessages;
import io.mosip.preregistration.acknowledgement.exception.IllegalParamException;
import io.mosip.preregistration.acknowledgement.exception.IOException;
import io.mosip.preregistration.acknowledgement.exception.JsonParseException;
import io.mosip.preregistration.acknowledgement.exception.JsonValidationException;
import io.mosip.preregistration.acknowledgement.exception.MandatoryFieldException;
import io.mosip.preregistration.acknowledgement.exception.MissingRequestParameterException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

/**
 * This class is used to catch the exceptions that occur while creating the
 * acknowledgement application
 * 
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
public class AcknowledgementExceptionCatcher {

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
		} else if (ex instanceof ParseException) {
			throw new JsonParseException(ErrorCodes.PRG_ACK_004.getCode(), ErrorMessages.JSON_PARSING_FAILED.getCode(),
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
