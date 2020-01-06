/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.demographic.exception.util;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.dao.DataIntegrityViolationException;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.InvalidParamSpecException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.crypto.exception.NullKeyException;
import io.mosip.kernel.core.crypto.exception.NullMethodException;
import io.mosip.kernel.core.crypto.exception.SignatureException;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.DecryptionFailedException;
import io.mosip.preregistration.core.exception.EncryptionFailedException;
import io.mosip.preregistration.core.exception.HashingException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.PreIdInvalidForUserIdException;
import io.mosip.preregistration.core.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.core.exception.RestCallException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.demographic.errorcodes.ErrorCodes;
import io.mosip.preregistration.demographic.errorcodes.ErrorMessages;
import io.mosip.preregistration.demographic.exception.BookingDeletionFailedException;
import io.mosip.preregistration.demographic.exception.CryptocoreException;
import io.mosip.preregistration.demographic.exception.DemographicServiceException;
import io.mosip.preregistration.demographic.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.demographic.exception.DuplicatePridKeyException;
import io.mosip.preregistration.demographic.exception.IdValidationException;
import io.mosip.preregistration.demographic.exception.InvalidDateFormatException;
import io.mosip.preregistration.demographic.exception.MissingRequestParameterException;
import io.mosip.preregistration.demographic.exception.OperationNotAllowedException;
import io.mosip.preregistration.demographic.exception.RecordFailedToUpdateException;
import io.mosip.preregistration.demographic.exception.RecordNotFoundException;
import io.mosip.preregistration.demographic.exception.RecordNotFoundForPreIdsException;
import io.mosip.preregistration.demographic.exception.SchemaValidationException;
import io.mosip.preregistration.demographic.exception.system.DateParseException;
import io.mosip.preregistration.demographic.exception.system.JsonParseException;
import io.mosip.preregistration.demographic.exception.system.JsonValidationException;
import io.mosip.preregistration.demographic.exception.system.SystemFileIOException;
import io.mosip.preregistration.demographic.exception.system.SystemIllegalArgumentException;
import io.mosip.preregistration.demographic.exception.system.SystemUnsupportedEncodingException;

/**
 * This class is used to catch the exceptions that occur while creating the
 * pre-registration
 * 
 * @author Ravi C Balaji
 * 
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
	public void handle(Exception ex, MainResponseDTO<?> mainResponsedto) {
		if (ex instanceof DataAccessLayerException) {
			throw new TableNotAccessibleException(((DataAccessLayerException) ex).getErrorCode(),
					((DataAccessLayerException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof ParseException) {
			throw new JsonParseException(((ParseException) ex).getErrorCode(), ((ParseException) ex).getErrorText(),
					mainResponsedto);
		} else if (ex instanceof RecordNotFoundException) {
			throw new RecordNotFoundException(((RecordNotFoundException) ex).getErrorCode(),
					((RecordNotFoundException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof RecordNotFoundForPreIdsException) {
			throw new RecordNotFoundForPreIdsException(((RecordNotFoundForPreIdsException) ex).getErrorCode(),
					((RecordNotFoundForPreIdsException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof MissingRequestParameterException) {
			throw new MissingRequestParameterException(((MissingRequestParameterException) ex).getErrorCode(),
					((MissingRequestParameterException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof DocumentFailedToDeleteException) {
			throw new DocumentFailedToDeleteException(((DocumentFailedToDeleteException) ex).getErrorCode(),
					((DocumentFailedToDeleteException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof SystemIllegalArgumentException) {
			throw new SystemIllegalArgumentException(((SystemIllegalArgumentException) ex).getErrorCode(),
					((SystemIllegalArgumentException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof SystemUnsupportedEncodingException) {
			throw new SystemUnsupportedEncodingException(((SystemUnsupportedEncodingException) ex).getErrorCode(),
					((SystemUnsupportedEncodingException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof DateParseException) {
			throw new DateParseException(((DateParseException) ex).getErrorCode(),
					((DateParseException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof RecordFailedToUpdateException) {
			throw new RecordFailedToUpdateException(((RecordFailedToUpdateException) ex).getErrorCode(),
					((RecordFailedToUpdateException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof RecordFailedToDeleteException) {
			throw new RecordFailedToDeleteException(((RecordFailedToDeleteException) ex).getErrorCode(),
					((RecordFailedToDeleteException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof InvalidDateFormatException) {
			throw new InvalidDateFormatException(((InvalidDateFormatException) ex).getErrorCode(),
					((InvalidDateFormatException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof BookingDeletionFailedException) {
			throw new BookingDeletionFailedException(((BookingDeletionFailedException) ex).getErrorCode(),
					((BookingDeletionFailedException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof HashingException) {
			throw new HashingException(((HashingException) ex).getErrorCode(), ((HashingException) ex).getErrorText(),
					mainResponsedto);
		} else if (ex instanceof OperationNotAllowedException) {
			throw new OperationNotAllowedException(((OperationNotAllowedException) ex).getErrorCode(),
					((OperationNotAllowedException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof DecryptionFailedException) {
			throw new DecryptionFailedException(((DecryptionFailedException) ex).getErrorCode(),
					((DecryptionFailedException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof JsonMappingException) {
			throw new JsonValidationException(((JsonMappingException) ex).getErrorCode(),
					((JsonMappingException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof IOException) {
			throw new JsonValidationException(((IOException) ex).getErrorCode(), ((IOException) ex).getErrorText(),
					mainResponsedto);
		} else if (ex instanceof RestCallException) {
			throw new RestCallException(((RestCallException) ex).getErrorCode(),
					((RestCallException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof SchemaValidationException) {
			throw new SchemaValidationException(((SchemaValidationException) ex).getErrorCode(),
					((SchemaValidationException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof IdObjectIOException) {
			throw new SchemaValidationException(((IdObjectIOException) ex).getErrorCode(),
					((IdObjectIOException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof IdObjectValidationFailedException) {
			throw new IdValidationException(((IdObjectValidationFailedException) ex).getErrorCode(),
					((IdObjectValidationFailedException) ex).getErrorTexts(), mainResponsedto);
		} else if (ex instanceof PreIdInvalidForUserIdException) {
			throw new PreIdInvalidForUserIdException(((PreIdInvalidForUserIdException) ex).getErrorCode(),
					((PreIdInvalidForUserIdException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof EncryptionFailedException) {
			throw new EncryptionFailedException(((EncryptionFailedException) ex).getValidationErrorList(),
					mainResponsedto);
		} else if (ex instanceof BeanCreationException) {
			throw new SchemaValidationException(
					io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_016.getCode(),
					ex.getLocalizedMessage(), mainResponsedto);
		} else if (ex instanceof SystemFileIOException) {
			throw new SystemFileIOException(((SystemFileIOException) ex).getErrorCode(),
					((SystemFileIOException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof DemographicServiceException) {
			throw new DemographicServiceException(((DemographicServiceException) ex).getValidationErrorList(),
					mainResponsedto);
		} else if (ex instanceof InvalidDataException) {
			throw new CryptocoreException(((InvalidDataException) ex).getErrorCode(),
					((InvalidDataException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof SignatureException) {
			throw new CryptocoreException(((SignatureException) ex).getErrorCode(),
					((SignatureException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof InvalidKeyException) {
			throw new CryptocoreException(((InvalidKeyException) ex).getErrorCode(),
					((InvalidKeyException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof InvalidParamSpecException) {
			throw new CryptocoreException(((InvalidParamSpecException) ex).getErrorCode(),
					((InvalidParamSpecException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof NullDataException) {
			throw new CryptocoreException(((NullDataException) ex).getErrorCode(),
					((NullDataException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof NullKeyException) {
			throw new CryptocoreException(((NullKeyException) ex).getErrorCode(),
					((NullKeyException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof NullMethodException) {
			throw new CryptocoreException(((NullMethodException) ex).getErrorCode(),
					((NullMethodException) ex).getErrorText(), mainResponsedto);
		} else if (ex instanceof NoSuchAlgorithmException) {
			throw new InvalidRequestParameterException(((NoSuchAlgorithmException) ex).getErrorCode(),
					((NoSuchAlgorithmException) ex).getErrorText(), mainResponsedto);
		}
		else if (ex instanceof DataIntegrityViolationException ) {
			throw new DuplicatePridKeyException(ErrorCodes.PRG_PAM_APP_021.getCode(),ErrorMessages.DUPLICATE_KEY.getMessage(), mainResponsedto);
		}else if (ex instanceof ConstraintViolationException ) {
			throw new DuplicatePridKeyException(ErrorCodes.PRG_PAM_APP_021.getCode(),ErrorMessages.DUPLICATE_KEY.getMessage(), mainResponsedto);
		}

	}

}
