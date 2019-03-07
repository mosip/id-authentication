package io.mosip.registration.processor.packet.service.dto.json.metadata;

import java.util.List;

public class HashSequence {

	private DemographicSequence demographicSequence;
	private List<String> osiDataHashSequence;

	public HashSequence(DemographicSequence demographicSequence, List<String> osiDataHashSequence) {
		super();

		this.demographicSequence = demographicSequence;
		this.osiDataHashSequence = osiDataHashSequence;
	}

	public DemographicSequence getDemographicSequence() {
		return demographicSequence;
	}

	public void setDemographicSequence(DemographicSequence demographicSequence) {
		this.demographicSequence = demographicSequence;
	}

	public List<String> getOsiDataHashSequence() {
		return osiDataHashSequence;
	}

	public void setOsiDataHashSequence(List<String> osiDataHashSequence) {
		this.osiDataHashSequence = osiDataHashSequence;
	}

}