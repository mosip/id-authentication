package io.mosip.authentication.service.factory;

import org.junit.Test;

/**
 * The Class FingerprintFactoryTest.
 *
 * @author Manoj SP
 */
public class FingerprintFactoryTest {
	
	/** The factory. */
	FingerprintFactory factory = new FingerprintFactory();
	
	/**
	 * Testget fingerprint provider.
	 */
	@Test
	public void testgetFingerprintProvider() {
		factory.getFingerprintProvider(null, null);
	}

}
