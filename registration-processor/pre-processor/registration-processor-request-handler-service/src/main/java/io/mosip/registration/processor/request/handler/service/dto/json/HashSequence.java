package io.mosip.registration.processor.request.handler.service.dto.json;

import java.util.List;

import lombok.Data;

/**
 * @author Sowmya The Class HashSequence.
 */
@Data
public class HashSequence {

	/** The demographic sequence. */
	private DemographicSequence demographicSequence;

	/** The osi data hash sequence. */
	private List<String> osiDataHashSequence;

	/**
	 * Instantiates a new hash sequence.
	 *
	 * @param demographicSequence
	 *            the demographic sequence
	 * @param osiDataHashSequence
	 *            the osi data hash sequence
	 */
	public HashSequence(DemographicSequence demographicSequence, List<String> osiDataHashSequence) {
		super();

		this.demographicSequence = demographicSequence;
		this.osiDataHashSequence = osiDataHashSequence;
	}

}