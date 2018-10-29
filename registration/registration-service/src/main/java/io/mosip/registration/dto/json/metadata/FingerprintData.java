package io.mosip.registration.dto.json.metadata;

import java.util.List;

/**
 * This class is to capture the json parsing finger print data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class FingerprintData {
	private List<Fingerprints> fingerprints;
	private List<BiometricException> exceptionFingerprints;
	public List<Fingerprints> getFingerprints() {
		return fingerprints;
	}
	public void setFingerprints(List<Fingerprints> fingerprints) {
		this.fingerprints = fingerprints;
	}
	public List<BiometricException> getExceptionFingerprints() {
		return exceptionFingerprints;
	}
	public void setExceptionFingerprints(List<BiometricException> exceptionFingerprints) {
		this.exceptionFingerprints = exceptionFingerprints;
	}
	
}
