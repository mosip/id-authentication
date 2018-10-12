package io.mosip.authentication.core.spi.fingerprintauth.provider;

import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;

/**
 * @author Manoj SP
 *
 */
public interface MosipFingerprintProvider {
	
	FingerprintDeviceInfo deviceInfo();
	
	Optional<byte[]> captureFingerprint(Integer quality, Integer timeout);
	
	double scoreCalculator(byte[] fingerImage1, byte[] fingerImage2);
	
	double scoreCalculator(String fingerImage1, String fingerImage2);
	
	Optional<Map> segmentFingerprint(byte[] fingerImage);
}