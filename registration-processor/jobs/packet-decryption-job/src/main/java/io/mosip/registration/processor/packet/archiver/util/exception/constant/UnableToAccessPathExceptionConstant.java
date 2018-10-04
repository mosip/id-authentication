package io.mosip.registration.processor.packet.archiver.util.exception.constant;

public enum UnableToAccessPathExceptionConstant {
	UNABLE_TO_ACCESS_PATH_ERROR_CODE("RER-ARC-002", "The file path is not accessible");

	public final String errorCode;
	public final String errorMessage;

	UnableToAccessPathExceptionConstant(String string1, String string2) {
		this.errorCode = string1;
		this.errorMessage = string2;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
