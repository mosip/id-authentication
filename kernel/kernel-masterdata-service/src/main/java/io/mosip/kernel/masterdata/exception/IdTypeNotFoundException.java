package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class for {@link IdTypeNotFoundException}
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class IdTypeNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = -4894384505797820772L;

	public IdTypeNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
