package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;

/**
 * The Class AbisIdentifyResponseDto.
 * @author M1048860 Kiran Raj
 */
public class AbisIdentifyResponseDto extends AbisCommonResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1344142383925376038L;

	/** The candidate list. */
	private CandidateListDto candidateList;
	
	

	/**
	 * Gets the candidate list.
	 *
	 * @return the candidate list
	 */
	public CandidateListDto getCandidateList() {
		return candidateList;
	}

	/**
	 * Sets the candidate list.
	 *
	 * @param candidateList the new candidate list
	 */
	public void setCandidateList(CandidateListDto candidateList) {
		this.candidateList = candidateList;
	}
	
}
