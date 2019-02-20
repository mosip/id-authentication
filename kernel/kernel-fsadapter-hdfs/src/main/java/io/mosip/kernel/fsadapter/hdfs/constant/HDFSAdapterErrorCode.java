package io.mosip.kernel.fsadapter.hdfs.constant;

/**
 * Constants for HDFSAdapter
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum HDFSAdapterErrorCode {
	HDFS_ADAPTER_EXCEPTION("KER-FSA-001", "Exception occured in HDFS Adapter"),

	FILE_NOT_FOUND_EXCEPTION("KER-FSA-002", "Requested file not found"),

	URI_SYNTAX_EXCEPTION("KER-FSA-003", "URL cannot be converted to URI"),

	LOGIN_EXCEPTION("KER-FSA-004", "Authentication Failed"),

	NO_SUCH_ALGORITHM_EXCEPTION("KER-FSA-005", "Configuration object of the specified type does not exists");

	private final String errorCode;
	private final String errorMessage;

	private HDFSAdapterErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
