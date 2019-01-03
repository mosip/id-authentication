package io.mosip.registration.processor.abis.dto;

public class IdentityResponceDto {
	
	private String id;
	private String requestId;
	private String timestamp;
	private String returnValue;
	private CandidateListDto candidateList;
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
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	public CandidateListDto getCandidateList() {
		return candidateList;
	}
	public void setCandidateList(CandidateListDto candidateList) {
		this.candidateList = candidateList;
	}
	
}
