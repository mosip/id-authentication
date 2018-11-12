package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new hash sequence.
 */
@Data
public class HashSequence {

	/** The biometric sequence. */
	private BiometricSequence biometricSequence;
	
	/** The demographic sequence. */
	private DemographicSequence demographicSequence;
}
