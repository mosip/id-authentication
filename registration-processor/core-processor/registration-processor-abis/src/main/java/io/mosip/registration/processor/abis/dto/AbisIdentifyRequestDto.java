package io.mosip.registration.processor.abis.dto;

/**
 * The Class AbisIdentifyRequestDto.
 */
public class AbisIdentifyRequestDto {

	/** The id. */
	private String id;
	
	/** The ver. */
	private String ver;
	
	/** The request id. */
	private String requestId;
	
	/** The timestamp. */
	private String timestamp;
	
	/** The reference id. */
	private String referenceId;
	
	/** The max results. */
	private Integer maxResults;
	
	/** The target FPIR. */
	private Integer targetFPIR;

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
	 * Gets the ver.
	 *
	 * @return the ver
	 */
	public String getVer() {
		return ver;
	}

	/**
	 * Sets the ver.
	 *
	 * @param ver the new ver
	 */
	public void setVer(String ver) {
		this.ver = ver;
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
	 * Gets the reference id.
	 *
	 * @return the reference id
	 */
	public String getReferenceId() {
		return referenceId;
	}

	/**
	 * Sets the reference id.
	 *
	 * @param referenceId the new reference id
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	/**
	 * Gets the max results.
	 *
	 * @return the max results
	 */
	public Integer getMaxResults() {
		return maxResults;
	}

	/**
	 * Sets the max results.
	 *
	 * @param maxResults the new max results
	 */
	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * Gets the target FPIR.
	 *
	 * @return the target FPIR
	 */
	public Integer getTargetFPIR() {
		return targetFPIR;
	}

	/**
	 * Sets the target FPIR.
	 *
	 * @param targetFPIR the new target FPIR
	 */
	public void setTargetFPIR(Integer targetFPIR) {
		this.targetFPIR = targetFPIR;
	}

}
