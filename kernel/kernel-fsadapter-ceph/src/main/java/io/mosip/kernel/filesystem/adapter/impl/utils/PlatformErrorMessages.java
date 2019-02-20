package io.mosip.kernel.filesystem.adapter.impl.utils;

public enum PlatformErrorMessages {


	// File adaptor ceph Exception error code and message
	KER_FAC_CONNECTION_NOT_AVAILABLE(PlatformErrorConstants.KER_FILESYSTEM_ADAPTOR_CEPH_MODULE + "001",
			"The connection Parameters to create a Packet Store connection are not Found"),

	/** The rpr fac invalid connection parameters. */
	KER_FAC_INVALID_CONNECTION_PARAMETERS(PlatformErrorConstants.KER_FILESYSTEM_ADAPTOR_CEPH_MODULE + "002",
			"Invalid connection parameter to create a Packet Store connection"),

	/** The rpr fac packet not available. */
	KER_FAC_PACKET_NOT_AVAILABLE(PlatformErrorConstants.KER_FILESYSTEM_ADAPTOR_CEPH_MODULE + "003",
			"Cannot find the Registration Packet"),

	KER_SYS_TIMEOUT_EXCEPTION(PlatformErrorConstants.KER_SYSTEM_EXCEPTION + "005", "Timeout Error"),

	KER_SYS_UNEXCEPTED_EXCEPTION(PlatformErrorConstants.KER_SYSTEM_EXCEPTION + "001", "Unexpected exception"),
	
	KER_FAC_RUNTIME_EXCEPTION(PlatformErrorConstants.KER_FILESYSTEM_ADAPTOR_CEPH_MODULE + "006", "Exception occured in CEPH Adapter"),;

	private final String errorMessage;

	/** The error code. */
	private final String errorCode;

	/**
	 * Instantiates a new platform error messages.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMsg
	 *            the error msg
	 */
	private PlatformErrorMessages(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMessage = errorMsg;
	}

	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public String getMessage() {
		return this.errorMessage;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getCode() {
		return this.errorCode;
	}

}
