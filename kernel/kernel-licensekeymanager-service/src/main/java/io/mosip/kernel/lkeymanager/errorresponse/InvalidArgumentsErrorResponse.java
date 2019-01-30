package io.mosip.kernel.lkeymanager.errorresponse;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * @author M1044542
 *
 */
public class InvalidArgumentsErrorResponse extends BaseUncheckedException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5896143672403925512L;
	/**
	 * 
	 */
	private final List<ServiceError> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public InvalidArgumentsErrorResponse(List<ServiceError> list) {
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
