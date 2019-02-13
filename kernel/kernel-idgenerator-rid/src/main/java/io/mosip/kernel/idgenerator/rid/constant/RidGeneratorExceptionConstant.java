package io.mosip.kernel.idgenerator.rid.constant;

/**
 * This enum provide all the exception constants for rid generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public enum RidGeneratorExceptionConstant {
	EMPTY_INPUT_ERROR_CODE("KER-RIG-002", "Empty input entered"), 
	INPUT_LENGTH_ERROR_CODE("KER-RIG-003","input length is not sufficient"),
	NULL_VALUE_ERROR_CODE("KER-RIG-001", "Null value entered"),
	INVALID_CENTERID_OR_MACHINEID_TIMESTAMP_LENGTH("KER-RIG-004", "Centre id length or machine id length or timestamp length should be greater than zero"),
	RID_FETCH_EXCEPTION("KER-RIG-005","Error occured while fetching rid"),
	RID_UPDATE_EXCEPTION("KER-RIG-006","Error occured while storing rid"),
	INVALID_SEQ_LENGTH_EXCEPTION("KER-RIG-007","Sequence length should be greater than zero");

	/**
	 * The errorCode.
	 */
	public final String errorCode;

	/**
	 * The errorMessage.
	 */
	public final String errorMessage;

	/**
	 * The constructor to set exception errorCode and errorMessage.
	 * 
	 * @param errorCode
	 *            The error code to be set.
	 * @param errorMessage
	 *            The error message to be set.
	 */
	private RidGeneratorExceptionConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
