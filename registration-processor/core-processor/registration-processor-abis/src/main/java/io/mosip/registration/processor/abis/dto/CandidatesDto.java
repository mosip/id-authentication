package io.mosip.registration.processor.abis.dto;

/**
 * The Class CandidatesDto.
 */
public class CandidatesDto {
	
	/** The reference id. */
	private String referenceId;
	
	/** The scaled score. */
	private String scaledScore;
	
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
	 * Gets the scaled score.
	 *
	 * @return the scaled score
	 */
	public String getScaledScore() {
		return scaledScore;
	}
	
	/**
	 * Sets the scaled score.
	 *
	 * @param scaledScore the new scaled score
	 */
	public void setScaledScore(String scaledScore) {
		this.scaledScore = scaledScore;
	}
}
