package io.mosip.registration.processor.abis.dto;

public class IdentityResponceDto {

	private String id;
	private String requestId;
	private String timestamp;
	private Integer returnValue;
	private CandidateListDto candidateList;
	private Integer failureReason;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Integer returnValue) {
		this.returnValue = returnValue;
	}

	public CandidateListDto getCandidateList() {
		return candidateList;
	}

	public void setCandidateList(CandidateListDto candidateList) {
		this.candidateList = candidateList;
	}
	
	public Integer getFailureReason() {
		return failureReason;
	}
	
	public void setFailureReason(Integer failureReason) {
		this.failureReason = failureReason;
	}

}
