package io.mosip.kernel.cryptomanager.exception;



import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;


public class KeymanagerServiceException extends BaseUncheckedException {
	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 8152409863253682472L;

	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<ServiceError> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public KeymanagerServiceException(List<ServiceError> list) {
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
