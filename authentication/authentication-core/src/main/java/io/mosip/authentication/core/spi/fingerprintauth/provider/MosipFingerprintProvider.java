package io.mosip.authentication.core.spi.fingerprintauth.provider;

import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;

/**
 * The Interface MosipFingerprintProvider.
 *
 * @author Manoj SP
 */
public interface MosipFingerprintProvider {
	
	/**
	 * Contains the fingerprint TDevice info.
	 *
	 * @return the fingerprint device info
	 */
	FingerprintDeviceInfo deviceInfo();
	
	/**
	 * Capture fingerprint.
	 *
	 * @param quality the quality
	 * @param timeout the timeout
	 * @return Fingerprint image
	 */
	Optional<byte[]> captureFingerprint(Integer quality, Integer timeout);
	
	/**
	 * Score calculator based on ISO Template image, compliant of ISO 19794-4.
	 *
	 * @param isoImage1 the iso image 1
	 * @param isoImage2 the iso image 2
	 * @return Match Score
	 */
	double scoreCalculator(byte[] isoImage1, byte[] isoImage2);
	
	/**
	 * Score calculator based on Minutiae in Json format, Compliant of ISO 19794-2.
	 *
	 * @param fingerImage1 the finger image 1
	 * @param fingerImage2 the finger image 2
	 * @return Match Score
	 */
	double scoreCalculator(String fingerImage1, String fingerImage2);
	
	/**
	 * Segment fingerprint.
	 *
	 * @param fingerImage the finger image
	 * @return the optional
	 */
	Optional<Map> segmentFingerprint(byte[] fingerImage);
}