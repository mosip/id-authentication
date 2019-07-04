package io.mosip.kernel.idgenerator.partnerid.constant;

/**
 * Exception constants for partner id generator.
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
public enum PartnerIdExceptionConstant {

	PARTNERID_FETCH_EXCEPTION("KER-PTG-001", "Error occured While Fetching Partner ID"),

	PARTNERID_INSERTION_EXCEPTION("KER-PTG-002", "Error occured While Inserting Partner ID");

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for partnerIdExceptionConstant.
	 * 
	 * @param errorCode
	 *            the errorCode.
	 * @param errorMessage
	 *            the errorMessage.
	 */
	PartnerIdExceptionConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
