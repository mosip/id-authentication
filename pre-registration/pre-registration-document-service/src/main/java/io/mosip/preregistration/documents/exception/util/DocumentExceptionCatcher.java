/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception.util;

import org.json.JSONException;
import org.postgresql.util.PSQLException;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.DecryptionFailedException;
import io.mosip.preregistration.core.exception.EncryptionFailedException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.DTOMappigException;
import io.mosip.preregistration.documents.exception.DemographicGetDetailsException;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
import io.mosip.preregistration.documents.exception.FSServerException;
import io.mosip.preregistration.documents.exception.InvalidDocumentIdExcepion;
import io.mosip.preregistration.documents.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.documents.exception.ParsingException;
import io.mosip.preregistration.documents.exception.PrimaryKeyValidationException;

/**
 * This class is used to catch the exceptions that occur while uploading the
 * document
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 */
public class DocumentExceptionCatcher {
	public void handle(Exception ex, MainResponseDTO<?> response) {
		if (ex instanceof DocumentFailedToUploadException) {
			throw new DocumentFailedToUploadException(((DocumentFailedToUploadException) ex).getErrorCode(),
					((DocumentFailedToUploadException) ex).getErrorText(), response);
		} else if (ex instanceof IOException) {
			// kernel exception
			throw new DTOMappigException(((IOException) ex).getErrorCode(), ((IOException) ex).getErrorText(),
					response);

		} else if (ex instanceof JsonMappingException) {
			throw new DTOMappigException(((JsonMappingException) ex).getErrorCode(),
					((JsonMappingException) ex).getErrorText(), response);
			// kernel exception
		} else if (ex instanceof JsonParseException) {
			// kernel exception
			throw new DTOMappigException(((JsonParseException) ex).getErrorCode(),
					((JsonParseException) ex).getErrorText(), response);
		} else if (ex instanceof JSONException || ex instanceof ParseException) {
			throw new ParsingException(ErrorCodes.PRG_PAM_DOC_015.toString(), ErrorMessages.JSON_EXCEPTION.getMessage(),
					response);

		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText(), response);
		} else if (ex instanceof MandatoryFieldNotFoundException) {
			throw new MandatoryFieldNotFoundException(((MandatoryFieldNotFoundException) ex).getErrorCode(),
					((MandatoryFieldNotFoundException) ex).getErrorText(), response);
		} else if (ex instanceof DocumentNotValidException) {
			throw new DocumentNotValidException(((DocumentNotValidException) ex).getErrorCode(),
					((DocumentNotValidException) ex).getErrorText(), response);
		} else if (ex instanceof DocumentSizeExceedException) {
			throw new DocumentSizeExceedException(((DocumentSizeExceedException) ex).getErrorCode(),
					((DocumentSizeExceedException) ex).getErrorText(), response);
		} else if (ex instanceof VirusScannerException) {
			throw new DocumentVirusScanException(((VirusScannerException) ex).getErrorCode(),
					((VirusScannerException) ex).getErrorText(), response);
		} else if (ex instanceof DocumentVirusScanException) {
			throw new DocumentVirusScanException(((DocumentVirusScanException) ex).getErrorCode(),
					((DocumentVirusScanException) ex).getErrorText(), response);
		} else if (ex instanceof DocumentNotFoundException) {
			throw new DocumentNotFoundException(((DocumentNotFoundException) ex).getErrorCode(),
					((DocumentNotFoundException) ex).getErrorText(), response);
		} else if (ex instanceof DocumentFailedToCopyException) {
			throw new DocumentFailedToCopyException(((DocumentFailedToCopyException) ex).getErrorCode(),
					((DocumentFailedToCopyException) ex).getErrorText(), response);
		} else if (ex instanceof InvalidDocumentIdExcepion) {
			throw new InvalidDocumentIdExcepion(((InvalidDocumentIdExcepion) ex).getErrorCode(),
					((InvalidDocumentIdExcepion) ex).getErrorText(), response);
		} else if (ex instanceof DemographicGetDetailsException) {
			throw new DemographicGetDetailsException(((DemographicGetDetailsException) ex).getErrorCode(),
					((DemographicGetDetailsException) ex).getErrorText(), response);
		} else if (ex instanceof FSServerException) {
			throw new FSServerException(((FSServerException) ex).getErrorCode(),
					((FSServerException) ex).getErrorText(), response);
		} else if (ex instanceof TableNotAccessibleException) {
			throw new TableNotAccessibleException(((TableNotAccessibleException) ex).getErrorCode(),
					((TableNotAccessibleException) ex).getErrorText(), response);
		} else if (ex instanceof PSQLException) {
			throw new PrimaryKeyValidationException(ErrorCodes.PRG_PAM_DOC_021.toString(),
					ErrorMessages.DOCUMENT_ALREADY_PRESENT.getMessage(), response);
		} else if (ex instanceof FSAdapterException) {
			throw new FSServerException(((FSAdapterException) ex).getErrorCode(),
					((FSAdapterException) ex).getErrorText(), response);
		} else if (ex instanceof DecryptionFailedException) {
			throw new EncryptionFailedException(((DecryptionFailedException) ex).getErrorCode(),
					((DecryptionFailedException) ex).getErrorText(), response);
		} else if (ex instanceof EncryptionFailedException) {
			throw new EncryptionFailedException(((EncryptionFailedException) ex).getValidationErrorList(), response);
		} else if (ex instanceof java.text.ParseException) {
			throw new InvalidRequestParameterException(
					io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_003.getCode(),
					io.mosip.preregistration.core.errorcodes.ErrorMessages.INVALID_REQUEST_DATETIME.getMessage(),
					response);

		}

	}

}