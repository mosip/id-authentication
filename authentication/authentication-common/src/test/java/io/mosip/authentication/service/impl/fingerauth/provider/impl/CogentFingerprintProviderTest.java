package io.mosip.authentication.service.impl.fingerauth.provider.impl;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Manoj SP
 *
 */
public class CogentFingerprintProviderTest {
	
	CogentFingerprintProvider fp = new CogentFingerprintProvider();

	@Test
	public void testDeviceInfo() {
		assertNull(fp.deviceInfo());
	}
	
	@Test
	public void testCaptureFingerprint() {
		assertFalse(fp.captureFingerprint(0, 0).isPresent());
	}
	
	@Test
	public void testSegmentFingerprint() {
		assertFalse(fp.segmentFingerprint(new byte[1]).isPresent());
	}
}
