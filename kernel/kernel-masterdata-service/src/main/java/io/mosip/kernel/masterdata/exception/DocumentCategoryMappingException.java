package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class throwing exception when not able to map
 * documentCategory and document type data
 * 
 * @author Neha
 * @author Uday Kumar
 * 
 * @since 1.0.0
 */

public class DocumentCategoryMappingException extends BaseUncheckedException {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -500924741465993301L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public DocumentCategoryMappingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
