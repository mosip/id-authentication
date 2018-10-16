package io.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class HashSequence {
	
	private BiometricSequence biometricSequence;
	private DemographicSequence demographicSequence;
	
	public HashSequence(BiometricSequence biometricSequence, DemographicSequence demographicSequence) {
		super();
		this.biometricSequence = biometricSequence;
		this.demographicSequence = demographicSequence;
	}
}