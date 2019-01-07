package io.mosip.registration.processor.bio.dedupe.abis.dto;

public class CandidateListDto {
	private String count;
	private CandidatesDto[] candidates;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public CandidatesDto[] getCandidates() {
		return candidates;
	}
	public void setCandidates(CandidatesDto[] candidates) {
		this.candidates = candidates;
	}
	
}
