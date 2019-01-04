package io.mosip.registration.processor.abis.dto;

public class IdentityRequestDto {

	private String id;
	private String ver;
	private String requestId;
	private String timestamp;
	private String referenceId;
	private Integer maxResults;
	private Integer targetFPIR;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
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

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public Integer getTargetFPIR() {
		return targetFPIR;
	}

	public void setTargetFPIR(Integer targetFPIR) {
		this.targetFPIR = targetFPIR;
	}

}
