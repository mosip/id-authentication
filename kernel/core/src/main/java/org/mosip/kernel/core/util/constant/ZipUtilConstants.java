package org.mosip.kernel.core.util.constant;

public enum ZipUtilConstants {
		FILE_NOT_FOUND_ERROR_CODE("COK-UTL-ZUN-001", "File Not Found"), 
		IO_ERROR_CODE("COK-UTL-ZUN-002","Interrupted IO Operation"), 
		NULL_POINTER_ERROR_CODE("COK-UTL-ZUN-003", "Null Reference found"),
	    DATA_FORMATE_ERROR_CODE("COK-UTL-ZUN-004","Attempting to unzip file that is not zipped");

	
	/**  Error code. */
	public final String errorCode;

	/**  Exception Message */
	public final String message;
	/**
	 * @param errorCode source Error code to use when no
     * localized code is available
     * @param exceptionMessage source exception message to use when no
     * localized message is available.
	 */
		ZipUtilConstants(final String errorCode, final String message) {
			this.errorCode = errorCode;
			this.message = message;
		}

		public String getErrorCode() {
			return errorCode;
		}

		public String getMessage() {
			return message;
		}

	}



