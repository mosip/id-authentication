package io.mosip.registration.dto.json.metadata;

import java.util.List;

import lombok.Data;

@Data
public class FingerprintData {
	private List<Fingerprints> fingerprints;
	private List<BiometricException> exceptionFingerprints;
}
