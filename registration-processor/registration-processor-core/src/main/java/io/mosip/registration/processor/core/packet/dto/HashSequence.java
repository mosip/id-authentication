package io.mosip.registration.processor.core.packet.dto;

public class HashSequence {

	private BiometricSequence biometricSequence;
	private DemographicSequence demographicSequence;

	public HashSequence(BiometricSequence biometricSequence, DemographicSequence demographicSequence) {
		super();
		this.biometricSequence = biometricSequence;
		this.demographicSequence = demographicSequence;
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

}