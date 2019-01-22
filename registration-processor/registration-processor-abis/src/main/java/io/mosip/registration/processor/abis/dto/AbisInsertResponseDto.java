package io.mosip.registration.processor.abis.dto;

/**
 * The Class AbisInsertResponseDto.
 */
public class AbisInsertResponseDto {
	
	/** The id. */
	private String id;
	
	/** The request id. */
	private String requestId;
	
	/** The timestamp. */
	private String timestamp;
	
	/** The return value. */
	private int returnValue;
	
	/** The failure reason. */
	private Integer failureReason;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the request id.
	 *
	 * @return the request id
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Sets the request id.
	 *
	 * @param requestId the new request id
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets the return value.
	 *
	 * @return the return value
	 */
	public int getReturnValue() {
		return returnValue;
	}

	/**
	 * Sets the return value.
	 *
	 * @param i the new return value
	 */
	public void setReturnValue(int i) {
		this.returnValue = i;
	}

	/**
	 * Gets the failure reason.
	 *
	 * @return the failure reason
	 */
	public Integer getFailureReason() {
		return failureReason;
	}

	/**
	 * Sets the failure reason.
	 *
	 * @param failureReason the new failure reason
	 */
	public void setFailureReason(Integer failureReason) {
		this.failureReason = failureReason;
	}

}
