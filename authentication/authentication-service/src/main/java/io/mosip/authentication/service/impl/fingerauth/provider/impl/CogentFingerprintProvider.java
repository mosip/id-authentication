package io.mosip.authentication.service.impl.fingerauth.provider.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;


/**
 * The Class CogentFingerprintProvider - FingerprintProvider for Cogent devices.
 *
 * @author Manoj SP , Arun Bose S
 */
@Component
public class CogentFingerprintProvider extends FingerprintProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#deviceInfo()
	 */
	@Override
	public FingerprintDeviceInfo deviceInfo() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#captureFingerprint(java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public Optional<byte[]> captureFingerprint(Integer quality, Integer timeout) {
		return Optional.empty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#segmentFingerprint(byte[])
	 */
	@Override
	public Optional<Map> segmentFingerprint(byte[] fingerImage) {
		return Optional.empty();
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#createMinutiae(byte[])
	 */
	@Override
	public String createMinutiae(byte[] inputImage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double matchImage(Object reqInfo, Object entityInfo) {
		// TODO Auto-generated method stub
		return 0;
	}

}
