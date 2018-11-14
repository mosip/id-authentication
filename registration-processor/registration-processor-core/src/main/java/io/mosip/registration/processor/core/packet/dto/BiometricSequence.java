package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

import lombok.Data;

/**
 * Instantiates a new biometric sequence.
 */
@Data
public class BiometricSequence {

	/** The applicant. */
	private List<String> applicant;

	/** The introducer. */
	private List<String> introducer;
}
