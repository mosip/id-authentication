package io.mosip.kernel.masterdata.constant;

import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;

/**
 * Error Code and Messages ENUM for {@link RegistrationCenterTypeService}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public enum RegistrationCenterTypeErrorCode {
	REGISTRATION_CENTER_TYPE_FETCH_EXCEPTION("KER-MSD-RC1","exception during fetching data from db"),
	REGISTRATION_CENTER_TYPE_INSERT_EXCEPTION("KER-MSD-RC2","exception during inserting data into db"),
	REGISTRATION_CENTER_TYPE_NOT_FOUND_EXCEPTION("KER-MSD-RC3","No documents found for specified document category code and language code"),
	REGISTRATION_CENTER_TYPE_MAPPING_EXCEPTION("KER-MSD-RC4", "Error occured while mapping document category");
	
	/**
	 * The error code.
	 */
	private final String errorCode;
	/**
	 * The error message.
	 */
	private final String errorMessage;

	/**
	 * Constructor for RegistrationCenterTypeErrorCode.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	private RegistrationCenterTypeErrorCode(final String errorCode, final String errorMessage) {
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
