package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class for {@link IdTypeFetchException}
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class IdTypeFetchException extends BaseUncheckedException {
	private static final long serialVersionUID = 394577185157864405L;

	public IdTypeFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
