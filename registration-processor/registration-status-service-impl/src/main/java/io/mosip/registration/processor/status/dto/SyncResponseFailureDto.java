package io.mosip.registration.processor.status.dto;

/**
 * The Class SyncResponseFailureDto.
 * 
 * @author Ranjitha Siddegowda
 */
public class SyncResponseFailureDto extends SyncResponseDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7322317597489527406L;

	/** The errorCode. */
	private String errorCode;

	/**
	 * Instantiates a new sync response failure dto.
	 */
	public SyncResponseFailureDto() {
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

}
