package io.mosip.authentication.service.factory;

import org.junit.Test;

/**
 * @author Manoj SP
 *
 */
public class FingerprintFactoryTest {
	
	FingerprintFactory factory = new FingerprintFactory();
	
	@Test
	public void testgetFingerprintProvider() {
		factory.getFingerprintProvider(null, null);
	}

}
