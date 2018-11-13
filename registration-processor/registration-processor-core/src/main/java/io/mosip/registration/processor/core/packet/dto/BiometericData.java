package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new biometeric data.
 */
@Data
public class BiometericData {

	/** The fingerprint data. */
	private FingerprintData fingerprintData;

	/** The iris data. */
	private IrisData irisData;
}
