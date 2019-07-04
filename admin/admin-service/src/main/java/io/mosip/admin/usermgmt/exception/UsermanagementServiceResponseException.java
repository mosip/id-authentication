package io.mosip.admin.usermgmt.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;

public class UsermanagementServiceResponseException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3670603332439889288L;
	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<ServiceError> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public UsermanagementServiceResponseException(List<ServiceError> list) {
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
