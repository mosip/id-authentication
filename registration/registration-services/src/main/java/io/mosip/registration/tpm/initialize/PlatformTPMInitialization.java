package io.mosip.registration.tpm.initialize;

import tss.Tpm;
import tss.TpmFactory;

/**
 * The class to initialize the {@link Tpm}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class PlatformTPMInitialization {

	private static Tpm tpm;

	private PlatformTPMInitialization() {
	}

	/**
	 * Gets the instance of the platform TPM
	 * 
	 * @return the instance of {@link Tpm}
	 */
	public static Tpm getTPMInstance() {
		if (tpm == null) {
			tpm = TpmFactory.platformTpm();
		}
		return tpm;
	}

}
