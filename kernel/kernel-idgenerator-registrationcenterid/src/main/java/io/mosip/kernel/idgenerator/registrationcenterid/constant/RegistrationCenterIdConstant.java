package io.mosip.kernel.idgenerator.registrationcenterid.constant;

/**
 * Constant class for Registration Center ID.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum RegistrationCenterIdConstant {
	ID_BASE(10), 
	REG_CEN_ID_FETCH_EXCEPTION("KER-RIG-001","Error occured while fetching id"), 
	REG_CEN_ID_INSERT_EXCEPTION("KER-RIG-002","Error occured while inserting id");

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
	 * Constructor with value as the argument.
	 * 
	 * @param value
	 *            - the value
	 */
	private RegistrationCenterIdConstant(int value) {
		this.value = value;
	}

	/**
	 * Constructor with errorCode and errorMessage as the argument.
	 * 
	 * @param errorCode
	 *            - the error code.
	 * @param errorMessage
	 *            - the error message.
	 */
	private RegistrationCenterIdConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for value.
	 * 
	 * @return - the value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return - the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return - the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
