package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class for {@link IdTypeMappingException}
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class IdTypeMappingException extends BaseUncheckedException {

	private static final long serialVersionUID = -7671857277050227006L;

	public IdTypeMappingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
