package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class thrown exception when not able to fetch data from
 * Machine Detail table
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
public class MachineDetailFetchException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -3556229489431119187L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public MachineDetailFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}