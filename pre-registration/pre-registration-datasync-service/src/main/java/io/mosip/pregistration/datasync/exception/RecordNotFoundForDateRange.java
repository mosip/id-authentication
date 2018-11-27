package io.mosip.pregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * RecordNotFoundForDateRange Exception
 * 
 * @author M1043226
 *
 */
public class RecordNotFoundForDateRange extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public RecordNotFoundForDateRange() {
		super();
	}
	
	public RecordNotFoundForDateRange(String errorCodes) {
		super(errorCodes, errorCodes);
	}
}