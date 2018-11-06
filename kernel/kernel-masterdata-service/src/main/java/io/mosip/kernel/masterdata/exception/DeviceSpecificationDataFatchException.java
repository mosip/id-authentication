package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of error while fetching DeviceSpecification
 * 
 * @author Uday Kumar
 * @since 1.0.0
 */
public class DeviceSpecificationDataFatchException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6920574391573893640L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public DeviceSpecificationDataFatchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
