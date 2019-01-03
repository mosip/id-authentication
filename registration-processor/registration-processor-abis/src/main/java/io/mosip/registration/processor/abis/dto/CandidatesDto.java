package io.mosip.registration.processor.abis.dto;

public class CandidatesDto {
	
	private String referenceId;
	private String scaledScore;
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getScaledScore() {
		return scaledScore;
	}
	public void setScaledScore(String scaledScore) {
		this.scaledScore = scaledScore;
	}
}
