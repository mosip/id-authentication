package io.mosip.authentication.core.spi.provider.bio;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * The Class CogentFingerprintProviderTest.
 *
 * @author Manoj SP
 */
public class CogentFingerprintProviderTest {

	/** The fp. */
	CogentFingerprintProvider fp = new CogentFingerprintProvider();

	/**
	 * Test device info.
	 */
	/*
	 * @Test public void testDeviceInfo() { assertNull(fp.deviceInfo()); }
	 */

	/**
	 * Test capture fingerprint.
	 */
	@Test
	public void testCaptureFingerprint() {
		assertFalse(fp.captureFingerprint(0, 0).isPresent());
	}

	/**
	 * Test segment fingerprint.
	 */
	@Test
	public void testSegmentFingerprint() {
		assertFalse(fp.segmentFingerprint(new byte[1]).isPresent());
	}

	@Test
	public void TestcreateMinutiae() {
		fp.createMinutiae("Test".getBytes());
	}
}
