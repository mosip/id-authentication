package io.mosip.registration.dto.json.metadata;

import java.util.List;

public class HashSequence {
	
	private BiometricSequence biometricSequence;
	private DemographicSequence demographicSequence;
	private List<String> osiDataHashSequence;
	
	public HashSequence(BiometricSequence biometricSequence, DemographicSequence demographicSequence, List<String> osiDataHashSequence) {
		super();
		this.biometricSequence = biometricSequence;
		this.demographicSequence = demographicSequence;
		this.osiDataHashSequence = osiDataHashSequence;
	}

	public BiometricSequence getBiometricSequence() {
		return biometricSequence;
	}

	public void setBiometricSequence(BiometricSequence biometricSequence) {
		this.biometricSequence = biometricSequence;
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