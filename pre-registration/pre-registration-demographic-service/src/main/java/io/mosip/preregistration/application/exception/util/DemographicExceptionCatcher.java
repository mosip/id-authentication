/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.util;

import org.json.simple.parser.ParseException;
import org.springframework.web.client.RestClientException;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.exception.UnidentifiedJsonException;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.MissingRequestParameterException;
import io.mosip.preregistration.application.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.system.DateParseException;
import io.mosip.preregistration.application.exception.system.JsonParseException;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemFileIOException;
import io.mosip.preregistration.application.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * This class is used to catch the exceptions that occur while creating the
 * pre-registration
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
public class DemographicExceptionCatcher {
	/**
	 * Method to handle the respective exceptions
	 * 
	 * @param ex
	 *            pass the exception
	 */
	public void handle(Exception ex) {
		if (ex instanceof HttpRequestException) {
			throw new JsonValidationException(((HttpRequestException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof DataAccessLayerException) {
			throw new TableNotAccessibleException(((DataAccessLayerException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof JsonValidationProcessingException) {
			throw new JsonValidationException(((BaseUncheckedException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof JsonIOException) {
			throw new JsonValidationException(((BaseUncheckedException) ex).getErrorCode(), ex.getMessage(),
					ex.getCause());
		} else if (ex instanceof JsonSchemaIOException) {
			throw new JsonValidationException(((BaseUncheckedException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof FileIOException) {
			throw new SystemFileIOException(((BaseUncheckedException) ex).getErrorCode(), ex.getMessage(),
					ex.getCause());
		} else if (ex instanceof ParseException) {
			throw new JsonParseException(((BaseUncheckedException) ex).getErrorCode().toString(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof RecordNotFoundException) {
			throw new RecordNotFoundException(((BaseUncheckedException) ex).getErrorCode(),
					ex.getMessage());
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					ex.getMessage());
		} else if (ex instanceof MissingRequestParameterException) {
			throw new MissingRequestParameterException(((MissingRequestParameterException) ex).getErrorCode(),
					ex.getMessage());
		} else if (ex instanceof RestClientException) {
			throw new DocumentFailedToDeleteException(((DocumentFailedToDeleteException) ex).getErrorCode(),
					ex.getMessage());
		} else if (ex instanceof DocumentFailedToDeleteException) {
			throw new DocumentFailedToDeleteException(((BaseUncheckedException) ex).getErrorCode(),
					ex.getMessage());
		} else if (ex instanceof IllegalArgumentException) {
			throw new SystemIllegalArgumentException(((BaseUncheckedException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof SystemUnsupportedEncodingException) {
			throw new SystemUnsupportedEncodingException(((BaseUncheckedException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof DateParseException) {
			throw new DateParseException(((BaseUncheckedException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof java.text.ParseException) {
			throw new DateParseException(((BaseUncheckedException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof UnidentifiedJsonException) {
			throw new JsonValidationException(((UnidentifiedJsonException) ex).getErrorCode(), ex.getMessage());
		}else if (ex instanceof RecordFailedToUpdateException) {
			throw new RecordFailedToUpdateException(((BaseUncheckedException) ex).getErrorCode(), ex.getMessage());
		}
	}

}
