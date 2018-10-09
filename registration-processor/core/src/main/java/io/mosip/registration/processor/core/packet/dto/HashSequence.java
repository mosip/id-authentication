package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class HashSequence {

	private BiometricSequence biometricSequence;
	private DemographicSequence demographicSequence;
}
