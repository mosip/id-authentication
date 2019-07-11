package io.mosip.registration.processor.status.dto;

public class SyncResponseFailDto extends SyncResponseDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7322317597489527406L;

	/** The errorCode. */
	private String errorCode;

	/** The message. */
	private String message;

	/**
	 * Instantiates a new sync response failure dto.
	 */
	public SyncResponseFailDto() {
		super();
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the error code.
	 *
	 * @param errorCode
	 *            the new error code
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
