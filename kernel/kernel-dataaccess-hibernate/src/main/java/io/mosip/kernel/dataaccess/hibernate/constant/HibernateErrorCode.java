package io.mosip.kernel.dataaccess.hibernate.constant;

/**
 * Error code constants for Hibernate implementation of Dao Manager
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum HibernateErrorCode {
	ERR_DATABASE("KER-DAH-001"), HIBERNATE_EXCEPTION("KER-DAH-002"), NO_RESULT_EXCEPTION("KER-DAH-003");

	/**
	 * Field for error code
	 */
	private final String errorCode;

	/**
	 * Function to set errorcode
	 * 
	 * @param errorCode The errorcode
	 */
	private HibernateErrorCode(final String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Function to get errorcode
	 * 
	 * @return The errorcode
	 */
	public String getErrorCode() {
		return errorCode;
	}

}
