/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.util;



import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.exception.UnidentifiedJsonException;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.MissingRequestParameterException;
import io.mosip.preregistration.application.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.application.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.system.DateParseException;
import io.mosip.preregistration.application.exception.system.JsonParseException;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemFileIOException;
import io.mosip.preregistration.application.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.core.exception.EncryptionFailedException;
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
			throw new JsonValidationException(((HttpRequestException) ex).getErrorCode(),((HttpRequestException) ex).getErrorText());
		} else if (ex instanceof DataAccessLayerException) {
			throw new TableNotAccessibleException(((DataAccessLayerException) ex).getErrorCode(),((DataAccessLayerException) ex).getErrorText());
		} else if (ex instanceof JsonValidationProcessingException) {
			throw new JsonValidationException(((JsonValidationProcessingException) ex).getErrorCode(),((JsonValidationProcessingException) ex).getErrorText());
		} else if (ex instanceof JsonIOException) {
			throw new JsonValidationException(((JsonIOException) ex).getErrorCode(),((JsonIOException) ex).getErrorText());
		} else if (ex instanceof JsonSchemaIOException) {
			throw new JsonValidationException(((JsonSchemaIOException) ex).getErrorCode(),((JsonSchemaIOException) ex).getErrorText());
		} else if (ex instanceof FileIOException) {
			throw new SystemFileIOException(((FileIOException) ex).getErrorCode(),((FileIOException) ex).getErrorText());
		} else if (ex instanceof ParseException) {
			throw new JsonParseException(((ParseException) ex).getErrorCode(),((ParseException) ex).getErrorText());
		} else if (ex instanceof RecordNotFoundException) {
			throw new RecordNotFoundException(((RecordNotFoundException) ex).getErrorCode(),((RecordNotFoundException) ex).getErrorText());
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText());
		} else if (ex instanceof MissingRequestParameterException) {
			throw new MissingRequestParameterException(((MissingRequestParameterException) ex).getErrorCode(),
					((MissingRequestParameterException) ex).getErrorText());
		} else if (ex instanceof DocumentFailedToDeleteException) {
			throw new DocumentFailedToDeleteException(((DocumentFailedToDeleteException) ex).getErrorCode(),((DocumentFailedToDeleteException) ex).getErrorText());
		} else if (ex instanceof IllegalArgumentException) {
			throw new SystemIllegalArgumentException(((BaseUncheckedException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof SystemUnsupportedEncodingException) {
			throw new SystemUnsupportedEncodingException(((SystemUnsupportedEncodingException) ex).getErrorCode(),((SystemUnsupportedEncodingException) ex).getErrorText());
		} else if (ex instanceof DateParseException) {
			throw new DateParseException(((DateParseException) ex).getErrorCode(),((DateParseException) ex).getErrorText());
		} else if (ex instanceof UnidentifiedJsonException) {
			throw new JsonValidationException(((UnidentifiedJsonException) ex).getErrorCode(), ((UnidentifiedJsonException) ex).getErrorText());
		}else if (ex instanceof RecordFailedToUpdateException) {
			throw new RecordFailedToUpdateException(((RecordFailedToUpdateException) ex).getErrorCode(),((RecordFailedToUpdateException) ex).getErrorText());
		}
		else if (ex instanceof RecordFailedToDeleteException) {
			throw new RecordFailedToDeleteException(((RecordFailedToDeleteException) ex).getErrorCode(),((RecordFailedToDeleteException) ex).getErrorText());
		}
	}

}
