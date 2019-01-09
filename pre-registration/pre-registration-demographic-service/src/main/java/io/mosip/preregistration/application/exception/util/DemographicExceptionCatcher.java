/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.util;

import org.json.simple.parser.ParseException;
import org.springframework.web.client.RestClientException;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.preregistration.application.errorcodes.ErrorCodes;
import io.mosip.preregistration.application.errorcodes.ErrorMessages;
import io.mosip.preregistration.application.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.application.exception.MissingRequestParameterException;
import io.mosip.preregistration.application.exception.RecordNotFoundException;
import io.mosip.preregistration.application.exception.system.DateParseException;
import io.mosip.preregistration.application.exception.system.JsonParseException;
import io.mosip.preregistration.application.exception.system.JsonValidationException;
import io.mosip.preregistration.application.exception.system.SystemFileIOException;
import io.mosip.preregistration.application.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.application.exception.system.SystemUnsupportedEncodingException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;

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
	 * @param ex pass the exception
	 */
	public void handle(Exception ex) {
		if (ex instanceof HttpRequestException) {
			throw new JsonValidationException(ErrorCodes.PRG_PAM_APP_007.name(),
					ErrorMessages.JSON_HTTP_REQUEST_EXCEPTION.name(), ex.getCause());
		} else if (ex instanceof DataAccessLayerException) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_PAM_APP_002.toString(),
					ErrorMessages.PRE_REGISTRATION_TABLE_NOT_ACCESSIBLE.toString(), ex.getCause());
		} else if (ex instanceof JsonValidationProcessingException) {
			throw new JsonValidationException(ErrorCodes.PRG_PAM_APP_007.name(),
					ErrorMessages.JSON_VALIDATION_PROCESSING_EXCEPTION.name(), ex.getCause());
		} else if (ex instanceof JsonIOException) {
			throw new JsonValidationException(ErrorCodes.PRG_PAM_APP_007.name(), ErrorMessages.JSON_IO_EXCEPTION.name(),
					ex.getCause());
		} else if (ex instanceof JsonSchemaIOException) {
			throw new JsonValidationException(ErrorCodes.PRG_PAM_APP_007.name(),
					ErrorMessages.JSON_SCHEMA_IO_EXCEPTION.name(), ex.getCause());
		} else if (ex instanceof FileIOException) {
			throw new SystemFileIOException(ErrorCodes.PRG_PAM_APP_009.name(), ErrorMessages.FILE_IO_EXCEPTION.name(),
					ex.getCause());
		} else if (ex instanceof ParseException) {
			throw new JsonParseException(ErrorCodes.PRG_PAM_APP_007.toString(),
					ErrorMessages.JSON_PARSING_FAILED.toString(), ex.getCause());
		} else if (ex instanceof RecordNotFoundException) {
			throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.name(),
					ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.name());
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText());
		} else if (ex instanceof MissingRequestParameterException) {
			throw new MissingRequestParameterException(((MissingRequestParameterException) ex).getErrorCode(),
					((MissingRequestParameterException) ex).getErrorText());
		} else if (ex instanceof RestClientException) {
			throw new DocumentFailedToDeleteException(((DocumentFailedToDeleteException) ex).getErrorCode(),
					((DocumentFailedToDeleteException) ex).getErrorText());
		} else if (ex instanceof DocumentFailedToDeleteException) {
			throw new DocumentFailedToDeleteException(ErrorCodes.PRG_PAM_DOC_015.name(),
					ErrorMessages.DOCUMENT_FAILED_TO_DELETE.name());
		} else if (ex instanceof IllegalArgumentException) {
			throw new SystemIllegalArgumentException(ErrorCodes.PRG_PAM_APP_006.toString(),
					ErrorMessages.INVAILD_STATUS_CODE.toString(), ex);
		} else if (ex instanceof SystemUnsupportedEncodingException) {
			throw new SystemUnsupportedEncodingException(ErrorCodes.PRG_PAM_APP_009.toString(),
					ErrorMessages.UNSUPPORTED_ENCODING_CHARSET.toString(), ex.getCause());
		} else if (ex instanceof DateParseException) {
			throw new DateParseException(ErrorCodes.PRG_PAM_APP_011.toString(),
					ErrorMessages.UNSUPPORTED_DATE_FORMAT.toString(), ex.getCause());
		} else if (ex instanceof java.text.ParseException) {
			throw new DateParseException(ErrorCodes.PRG_PAM_APP_011.toString(),
					ErrorMessages.UNSUPPORTED_DATE_FORMAT.toString(), ex.getCause());
		}
	}

}
