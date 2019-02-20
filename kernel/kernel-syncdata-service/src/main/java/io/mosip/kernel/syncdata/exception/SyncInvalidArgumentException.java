package io.mosip.kernel.syncdata.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;

public class SyncInvalidArgumentException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6611958753480242869L;
	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<ServiceError> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public SyncInvalidArgumentException(List<ServiceError> list) {
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
