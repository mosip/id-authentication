package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

import lombok.Data;

@Data
public class FingerprintData {
	private List<Fingerprint> fingerprints;
	private List<ExceptionFingerprint> exceptionFingerprints;
}
