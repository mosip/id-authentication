package io.mosip.registration.processor.packet.service.dto.json.metadata;

import java.util.List;

/**
 * @author Sowmya The Class HashSequence.
 */
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

	/**
	 * Gets the demographic sequence.
	 *
	 * @return the demographic sequence
	 */
	public DemographicSequence getDemographicSequence() {
		return demographicSequence;
	}

	/**
	 * Sets the demographic sequence.
	 *
	 * @param demographicSequence
	 *            the new demographic sequence
	 */
	public void setDemographicSequence(DemographicSequence demographicSequence) {
		this.demographicSequence = demographicSequence;
	}

	/**
	 * Gets the osi data hash sequence.
	 *
	 * @return the osi data hash sequence
	 */
	public List<String> getOsiDataHashSequence() {
		return osiDataHashSequence;
	}

	/**
	 * Sets the osi data hash sequence.
	 *
	 * @param osiDataHashSequence
	 *            the new osi data hash sequence
	 */
	public void setOsiDataHashSequence(List<String> osiDataHashSequence) {
		this.osiDataHashSequence = osiDataHashSequence;
	}

}