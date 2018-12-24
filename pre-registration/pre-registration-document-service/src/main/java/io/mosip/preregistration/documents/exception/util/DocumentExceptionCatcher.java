package io.mosip.preregistration.documents.exception.util;

import org.json.JSONException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.core.exceptions.InvalidRequestParameterException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.ConnectionUnavailableException;
import io.mosip.preregistration.documents.exception.DTOMappigException;
import io.mosip.preregistration.documents.exception.DocumentFailedToUploadException;
import io.mosip.preregistration.documents.exception.FileNotFoundException;
import io.mosip.preregistration.documents.exception.InvalidConnectionParameters;
import io.mosip.preregistration.documents.exception.MandatoryFieldNotFoundException;
import io.mosip.preregistration.documents.exception.ParsingException;

public class DocumentExceptionCatcher {
	public void handle(Exception ex) {
		if (ex instanceof DataAccessLayerException) {
			throw new DocumentFailedToUploadException(ErrorCodes.PRG_PAM_DOC_009.toString(),
					ErrorMessages.DOCUMENT_FAILED_TO_UPLOAD.toString(), ex.getCause());

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
			throw new MandatoryFieldNotFoundException(ErrorCodes.PRG_PAM_DOC_014.toString(),
					ErrorMessages.MANDATORY_FIELD_NOT_FOUND.toString());
		}else if (ex instanceof AmazonS3Exception) {
			if(((AmazonServiceException) ex).getStatusCode() == 403) {
				throw new InvalidConnectionParameters(ErrorCodes.PRG_PAM_DOC_015.toString(),
						ErrorMessages.INVALID_CEPH_CONNECTION.toString());
			}
			else if(((AmazonServiceException) ex).getStatusCode() == 404) {
				throw new FileNotFoundException(ErrorCodes.PRG_PAM_DOC_005.toString(),
						ErrorMessages.DOCUMENT_NOT_PRESENT.toString());
			}
		}else if (ex instanceof SdkClientException) {
			throw new ConnectionUnavailableException(ErrorCodes.PRG_PAM_DOC_017.toString(),
					ErrorMessages.CONNECTION_UNAVAILABLE.toString());
		}

	}

}
