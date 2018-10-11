package io.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class HashSequence {
	
	public BiometricSequence biometricSequence;
	public DemographicSequence demographicSequence;
	public String registrationID;
	
	public HashSequence(BiometricSequence biometricSequence, DemographicSequence demographicSequence,
			String registrationID) {
		super();
		this.biometricSequence = biometricSequence;
		this.demographicSequence = demographicSequence;
		this.registrationID = registrationID;
	}
}