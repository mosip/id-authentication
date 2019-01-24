/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.exception.util;

import org.json.JSONException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.CephConnectionUnavailableException;
import io.mosip.preregistration.documents.exception.CephServerException;
import io.mosip.preregistration.documents.exception.DTOMappigException;
import io.mosip.preregistration.documents.exception.DemographicGetDetailsException;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.exception.DocumentNotValidException;
import io.mosip.preregistration.documents.exception.DocumentSizeExceedException;
import io.mosip.preregistration.documents.exception.DocumentVirusScanException;
import io.mosip.preregistration.documents.exception.FileNotFoundException;
import io.mosip.preregistration.documents.exception.InvalidConnectionParameters;
import io.mosip.preregistration.documents.exception.InvalidDocumnetIdExcepion;
import io.mosip.preregistration.documents.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.documents.exception.ParsingException;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.ConnectionUnavailableException;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;

/**
 * This class is used to catch the exceptions that occur while uploading the
 * document
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 */
public class DocumentExceptionCatcher {
	public void handle(Exception ex) {
		if (ex instanceof DocumentFailedToUploadException) {
			throw new DocumentFailedToUploadException(((DocumentFailedToUploadException) ex).getErrorCode(),((DocumentFailedToUploadException) ex).getErrorText());
		} else if (ex instanceof IOException) {
			// kernel exception
			throw new DTOMappigException(((IOException) ex).getErrorCode(), ex.getMessage(), ex.getCause());

		} else if (ex instanceof JsonMappingException) {
			throw new DTOMappigException(((JsonMappingException) ex).getErrorCode(), ex.getMessage(), ex.getCause());
			// kernel exception
		} else if (ex instanceof JsonParseException) {
			// kernel exception
			throw new DTOMappigException(((JsonParseException) ex).getErrorCode(), ex.getMessage(), ex.getCause());
		} else if (ex instanceof JSONException || ex instanceof ParseException) {
			throw new ParsingException(ErrorCodes.PRG_PAM_DOC_015.toString(), ErrorMessages.JSON_EXCEPTION.toString(),
					ex.getCause());

		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText());
		} else if (ex instanceof MandatoryFieldNotFoundException) {
			throw new MandatoryFieldNotFoundException(((MandatoryFieldNotFoundException) ex).getErrorCode(),((MandatoryFieldNotFoundException) ex).getErrorText());
		} else if (ex instanceof AmazonS3Exception) {
			if (((AmazonServiceException) ex).getStatusCode() == 403) {
				throw new InvalidConnectionParameters(ErrorCodes.PRG_PAM_DOC_015.toString(),
						ErrorMessages.INVALID_CEPH_CONNECTION.toString());
			} else if (((AmazonServiceException) ex).getStatusCode() == 404) {
				throw new FileNotFoundException(ErrorCodes.PRG_PAM_DOC_005.toString(),
						ErrorMessages.DOCUMENT_NOT_PRESENT.toString());
			}
		} else if (ex instanceof PacketNotFoundException) {
			throw new CephServerException(((PacketNotFoundException) ex).getErrorCode(),ex.getMessage());
		} else if (ex instanceof SdkClientException) {
			throw new CephConnectionUnavailableException(ErrorCodes.PRG_PAM_DOC_017.toString(),
					ErrorMessages.CONNECTION_UNAVAILABLE.toString());
		} else if (ex instanceof DocumentNotValidException) {
			throw new DocumentNotValidException(((DocumentNotValidException) ex).getErrorCode(),((DocumentNotValidException) ex).getErrorText());
		} else if (ex instanceof ConnectionUnavailableException) {
			throw new CephConnectionUnavailableException(((ConnectionUnavailableException) ex).getErrorCode(),((ConnectionUnavailableException) ex).getErrorText());
		} else if (ex instanceof DocumentSizeExceedException) {
			throw new DocumentSizeExceedException(((DocumentSizeExceedException) ex).getErrorCode(),((DocumentSizeExceedException) ex).getErrorText());
		} else if (ex instanceof DocumentVirusScanException) {
			throw new DocumentVirusScanException(((DocumentVirusScanException) ex).getErrorCode(),((DocumentVirusScanException) ex).getErrorText());
		} else if (ex instanceof DocumentNotFoundException) {
			throw new DocumentNotFoundException(((DocumentNotFoundException) ex).getErrorCode(),((DocumentNotFoundException) ex).getErrorText());
		} else if (ex instanceof DocumentFailedToCopyException) {
			throw new DocumentFailedToCopyException(((DocumentFailedToCopyException) ex).getErrorCode(),((DocumentFailedToCopyException) ex).getErrorText());
		} else if (ex instanceof InvalidDocumnetIdExcepion) {
			throw new InvalidDocumnetIdExcepion(((InvalidDocumnetIdExcepion) ex).getErrorCode(),((InvalidDocumnetIdExcepion) ex).getErrorText());
		} else if (ex instanceof DemographicGetDetailsException) {
			throw new DemographicGetDetailsException(((DemographicGetDetailsException) ex).getErrorCode(),
					ex.getMessage());
		}else if(ex instanceof CephServerException) {
			throw new CephServerException(((CephServerException) ex).getErrorCode(),
					ex.getMessage());
		}

	}

}