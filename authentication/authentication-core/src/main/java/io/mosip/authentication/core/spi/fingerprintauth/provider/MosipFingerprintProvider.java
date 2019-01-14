package io.mosip.authentication.core.spi.fingerprintauth.provider;

import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;
import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;

/**
 * The Interface MosipFingerprintProvider.
 *
 * @author Manoj SP
 */
public interface MosipFingerprintProvider extends MosipBiometricProvider {
	
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
	 * Segment fingerprint.
	 *
	 * @param fingerImage the finger image
	 * @return the optional
	 */
	Optional<Map> segmentFingerprint(byte[] fingerImage);
}