package io.mosip.registration.processor.manual.adjudication.dto;

public class ManualVerificationDTO {
	
	private String regId;
	
	private String mvUsrId;
	
	private String statusCode;
	
	private String matchedRefId;
	
	private String matchedRefType;

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getMvUsrId() {
		return mvUsrId;
	}

	public void setMvUsrId(String mvUsrId) {
		this.mvUsrId = mvUsrId;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMatchedRefId() {
		return matchedRefId;
	}

	public void setMatchedRefId(String matchedRefId) {
		this.matchedRefId = matchedRefId;
	}

	public String getMatchedRefType() {
		return matchedRefType;
	}

	public void setMatchedRefType(String matchedRefType) {
		this.matchedRefType = matchedRefType;
	}
}
