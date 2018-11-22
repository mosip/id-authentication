package io.mosip.authentication.service.factory;

import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;

/**
 * A factory for providing the proper FingerprintProvider class using make and
 * type of the fingerprint device.
 *
 * @author Manoj SP
 */
public class FingerprintFactory {

	/**
	 * Gets the fingerprint provider.
	 *
	 * @param make
	 *            the make
	 * @param type
	 *            the type
	 * @return the fingerprint provider
	 */
	public FingerprintProvider getFingerprintProvider(String make,
			String type) {
		return null;
	}
}
