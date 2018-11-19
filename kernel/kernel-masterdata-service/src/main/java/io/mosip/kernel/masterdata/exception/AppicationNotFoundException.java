package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class AppicationNotFoundException extends BaseUncheckedException {

	/**
	 * Generated Serial Version Id
	 */
	private static final long serialVersionUID = -8957980816726856449L;

	public AppicationNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
