package org.mosip.registration.dto.json.metadata;

import java.util.List;

import lombok.Data;
@Data
public class FingerprintData {
	private List<Fingerprints> fingerprints;
	private int numRetry;
	private List<ExceptionFingerprints> exceptionFingerprints;
}
