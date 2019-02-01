/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.exceptions.util;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.batchjobservices.exceptions.NoPreIdAvailableException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * This class is used to catch the exceptions that occur in Batch Service
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public class BatchServiceExceptionCatcher {
	
	/**
	 * Method to handle the respective exceptions
	 * 
	 * @param ex
	 *            pass the exception
	 */
	public void handle(Exception ex) {
		if (ex instanceof NoPreIdAvailableException) {
			throw new NoPreIdAvailableException(((NoPreIdAvailableException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} else if (ex instanceof DataAccessLayerException) {
			throw new TableNotAccessibleException(((DataAccessLayerException) ex).getErrorCode(),
					ex.getMessage(), ex.getCause());
		} 
	}

}
