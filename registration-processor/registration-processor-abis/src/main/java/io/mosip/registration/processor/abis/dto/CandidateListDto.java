package io.mosip.registration.processor.abis.dto;

/**
 * The Class CandidateListDto.
 */
public class CandidateListDto {
	
	/** The count. */
	private String count;
	
	/** The candidates. */
	private CandidatesDto[] candidates;
	
	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public String getCount() {
		return count;
	}
	
	/**
	 * Sets the count.
	 *
	 * @param count the new count
	 */
	public void setCount(String count) {
		this.count = count;
	}
	
	/**
	 * Gets the candidates.
	 *
	 * @return the candidates
	 */
	public CandidatesDto[] getCandidates() {
		return candidates;
	}
	
	/**
	 * Sets the candidates.
	 *
	 * @param candidates the new candidates
	 */
	public void setCandidates(CandidatesDto[] candidates) {
		this.candidates = candidates;
	}
	
}
