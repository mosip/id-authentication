package io.mosip.kernel.lkeymanager.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;

public class InvalidArgumentsException extends BaseUncheckedException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7670097659608957076L;

	private final List<ServiceError> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public InvalidArgumentsException(List<ServiceError> list) {
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
