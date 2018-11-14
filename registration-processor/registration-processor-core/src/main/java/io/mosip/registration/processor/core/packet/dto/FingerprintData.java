package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

import lombok.Data;

/**
 * Instantiates a new fingerprint data.
 */
@Data
public class FingerprintData {
	
	/** The fingerprints. */
	private List<Fingerprint> fingerprints;
	
	/** The exception fingerprints. */
	private List<ExceptionFingerprint> exceptionFingerprints;
}
