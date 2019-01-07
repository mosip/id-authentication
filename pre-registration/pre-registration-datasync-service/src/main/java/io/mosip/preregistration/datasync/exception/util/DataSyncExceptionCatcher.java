package io.mosip.preregistration.datasync.exception.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
import io.mosip.preregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.preregistration.datasync.exception.DemographicGetDetailsException;
import io.mosip.preregistration.datasync.exception.DocumentGetDetailsException;
import io.mosip.preregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.preregistration.datasync.exception.ZipFileCreationException;
import io.mosip.preregistration.datasync.exception.system.SystemFileIOException;
import io.mosip.preregistration.datasync.exception.system.SystemFileNotFoundException;

/**
 * This class is used to catch the exceptions that occur while doing Datasync
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
public class DataSyncExceptionCatcher {
	/**
	 * Method to handle the respective exceptions
	 * 
	 * @param ex
	 *            pass the exception
	 */
	public void handle(Exception ex) {
		if (ex instanceof DataAccessLayerException) {
			throw new ReverseDataFailedToStoreException(ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());
		} else if (ex instanceof DocumentGetDetailsException) {
			throw new DocumentGetDetailsException(((DocumentGetDetailsException) ex).getErrorCode(),
					((DocumentGetDetailsException) ex).getErrorText());
		} else if (ex instanceof DemographicGetDetailsException) {
			throw new DemographicGetDetailsException(((DemographicGetDetailsException) ex).getErrorCode(),
					((DemographicGetDetailsException) ex).getErrorText());
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText());
		} else if (ex instanceof DataSyncRecordNotFoundException) {
			throw new DataSyncRecordNotFoundException(ErrorCodes.PRG_DATA_SYNC_004.toString(),
					ErrorMessages.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());
		} else if (ex instanceof TablenotAccessibleException) {
			throw new TablenotAccessibleException(ErrorMessages.REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());

		} else if (ex instanceof ZipFileCreationException) {
			throw new ZipFileCreationException(ErrorCodes.PRG_DATA_SYNC_005.toString(),
					ErrorMessages.FAILED_TO_CREATE_A_ZIP_FILE.toString(), ex.getCause());
		} else if (ex instanceof FileNotFoundException) {
			throw new SystemFileNotFoundException(ErrorCodes.PRG_DATA_SYNC_015.toString(),
					ErrorMessages.FILE_NOT_FOUND.toString(), ex.getCause());
		} else if (ex instanceof IOException) {
			throw new SystemFileIOException(ErrorCodes.PRG_DATA_SYNC_014.toString(),
					ErrorMessages.FILE_IO_EXCEPTION.toString(), ex.getCause());
		} else if (ex instanceof ReverseDataFailedToStoreException) {
			throw new ReverseDataFailedToStoreException(ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());

		}
	}
}
