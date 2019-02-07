package io.mosip.kernel.lkeymanager.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * Exception class for License Key Manager service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class LicenseKeyServiceException extends BaseUncheckedException {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 2506481216920647423L;

	private final List<ServiceError> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public LicenseKeyServiceException(List<ServiceError> list) {
		this.list = list;
	}

	/**
	 * Getter for error list.
	 * 
	 * @return The error list.
	 */
	public List<ServiceError> getList() {
		return list;
	}
}
