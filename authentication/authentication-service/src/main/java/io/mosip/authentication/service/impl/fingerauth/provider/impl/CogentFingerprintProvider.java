package io.mosip.authentication.service.impl.fingerauth.provider.impl;

import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;

/**
 * @author Manoj SP
 *
 */
public class CogentFingerprintProvider extends FingerprintProvider {
	
	@Override
	public FingerprintDeviceInfo deviceInfo() {
		return null;
	}

	@Override
	public Optional<byte[]> captureFingerprint(Integer quality, Integer timeout) {
		return Optional.empty();
	}

	@Override
	public Optional<Map> segmentFingerprint(byte[] fingerImage) {
		return Optional.empty();
	}

}
