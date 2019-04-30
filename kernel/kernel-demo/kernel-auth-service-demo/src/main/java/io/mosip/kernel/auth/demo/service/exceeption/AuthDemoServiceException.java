package io.mosip.kernel.auth.demo.service.exceeption;



import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;


public class AuthDemoServiceException extends BaseUncheckedException {
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
	public AuthDemoServiceException(List<ServiceError> list) {
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
