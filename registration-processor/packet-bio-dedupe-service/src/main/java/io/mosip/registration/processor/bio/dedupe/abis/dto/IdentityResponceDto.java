package io.mosip.registration.processor.bio.dedupe.abis.dto;

/**
 * The Class IdentityResponceDto.
 */
public class IdentityResponceDto {

	/** The id. */
	private String id;

	/** The request id. */
	private String requestId;

	/** The timestamp. */
	private String timestamp;

	/** The return value. */
	private Integer returnValue;

	/** The candidate list. */
	private CandidateListDto candidateList;

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
	 * @param id
	 *            the new id
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
	 * @param requestId
	 *            the new request id
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
	 * @param timestamp
	 *            the new timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets the return value.
	 *
	 * @return the return value
	 */
	public Integer getReturnValue() {
		return returnValue;
	}

	/**
	 * Sets the return value.
	 *
	 * @param returnValue
	 *            the new return value
	 */
	public void setReturnValue(Integer returnValue) {
		this.returnValue = returnValue;
	}

	/**
	 * Gets the candidate list.
	 *
	 * @return the candidate list
	 */
	public CandidateListDto getCandidateList() {
		return candidateList;
	}

	/**
	 * Sets the candidate list.
	 *
	 * @param candidateList
	 *            the new candidate list
	 */
	public void setCandidateList(CandidateListDto candidateList) {
		this.candidateList = candidateList;
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
	 * @param failureReason
	 *            the new failure reason
	 */
	public void setFailureReason(Integer failureReason) {
		this.failureReason = failureReason;
	}

}
