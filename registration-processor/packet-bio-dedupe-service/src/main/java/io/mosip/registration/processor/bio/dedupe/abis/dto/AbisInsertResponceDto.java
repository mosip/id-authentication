package io.mosip.registration.processor.bio.dedupe.abis.dto;

public class AbisInsertResponceDto {
	private String id;
	private String requestId;
	private String timestamp;
	private String returnValue;
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

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

	public Integer getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(Integer failureReason) {
		this.failureReason = failureReason;
	}

}
