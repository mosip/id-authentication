package io.mosip.registration.processor.core.packet.dto.abis;

import lombok.Data;

/**
 * The Class CandidateListDto.
 * 
 * @author M1048860 Kiran Raj
 */
@Data
public class CandidateListDto {

	/** The count. */
	private String count;

	/** The candidates. */
	private CandidatesDto[] candidates;

	public CandidatesDto[] getCandidates() {
		return candidates != null ? candidates.clone() : null;
	}

	public void setCandidates(CandidatesDto[] candidates) {
		this.candidates = candidates != null ? candidates : null;
	}

}
