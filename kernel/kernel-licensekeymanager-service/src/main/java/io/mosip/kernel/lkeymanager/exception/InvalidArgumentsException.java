package io.mosip.kernel.lkeymanager.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;

/**
 * Class to handle invalid arguments exception.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class InvalidArgumentsException extends BaseUncheckedException {
	/**
	 * Serializable version ID..
	 */
	private static final long serialVersionUID = -7670097659608957076L;

	/**
	 * The error list.
	 */
	private final List<ServiceError> list;

	/**
	 * Constructor with list as the argument.
	 * 
	 * @param list
	 *            the error list.
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
