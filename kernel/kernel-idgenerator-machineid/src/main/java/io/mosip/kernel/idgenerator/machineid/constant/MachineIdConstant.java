package io.mosip.kernel.idgenerator.machineid.constant;

/**
 * This ENUM provides the constants for Machine ID.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum MachineIdConstant {
	ID_BASE(10),
	MID_FETCH_EXCEPTION("KER-MIG-001", "Error occured while fetching id"),
	MID_INSERT_EXCEPTION("KER-MIG-002", "Error occured while inserting id");

	/**
	 * The value.
	 */
	private int value;

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * Constructor with the value argument.
	 * 
	 * @param value
	 *            the value.
	 */
	private MachineIdConstant(int value) {
		this.value = value;
	}

	/**
	 * Constructor with errorCode and errorMessage as the arguments.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	private MachineIdConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for value.
	 * 
	 * @return the value.
	 */
	public int getValue() {
		return value;
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
