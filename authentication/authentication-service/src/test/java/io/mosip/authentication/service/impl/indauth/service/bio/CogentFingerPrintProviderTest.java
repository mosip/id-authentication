package io.mosip.authentication.service.impl.indauth.service.bio;

import org.junit.Test;	
import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;

/**
 * The Class CogentFingerPrintProviderTest.
 *
 * @author Manoj SP
 */
public class CogentFingerPrintProviderTest {

	/** The cogent finger print provider. */
	CogentFingerprintProvider cogentFingerPrintProvider = new CogentFingerprintProvider();

	/**
	 * Test cogent finger print test.
	 */
	@Test
	public void testCogentFingerPrintTest() {

		cogentFingerPrintProvider.captureFingerprint(null, null);
		cogentFingerPrintProvider.deviceInfo();
		cogentFingerPrintProvider.segmentFingerprint(null);
		cogentFingerPrintProvider.createMinutiae(null);
	}

}
