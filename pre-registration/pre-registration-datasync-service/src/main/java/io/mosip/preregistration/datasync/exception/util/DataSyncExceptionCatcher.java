package io.mosip.preregistration.datasync.exception.util;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.jsonvalidator.exception.HttpRequestException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.datasync.exception.ReverseDataFailedToStoreException;

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
		}
	}
}
