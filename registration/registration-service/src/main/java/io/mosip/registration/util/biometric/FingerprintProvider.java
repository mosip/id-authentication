package io.mosip.registration.util.biometric;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import com.google.gson.JsonSyntaxException;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

import MFS100.FingerData;
import MFS100.MFS100Event;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.config.AppConfig;

/**
 * 
 * The Class FingerprintProvider - An Abstract class which contains default
 * implementation for calculating score based on ISO Template and Fingerprint
 * minutiae in Json format and also provides support for adding new fingerprint
 * providers.
 * 
 * @author Sravya Surampalli
 * 
 */
public class FingerprintProvider implements MFS100Event {

	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final MosipLogger LOGGER = AppConfig.getLogger(FingerprintProvider.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * 
	 * MosipFingerprintProvider#scoreCalculator(byte[], byte[])
	 */
	public double scoreCalculator(byte[] isoImage1, byte[] isoImage2) {
		double score = 0;
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().convert(isoImage1);
			FingerprintTemplate template2 = new FingerprintTemplate().convert(isoImage2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			score = matcher.index(template1).match(template2);
		} catch (IllegalArgumentException illegalArgumentException) {
			LOGGER.debug("REGISTRATION - FINGERPRINTPROVIDER - SCORECALCULATOR", APPLICATION_NAME, APPLICATION_ID,
					"Calculating Finger print score for ISO Template");
		}
		return score;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#scoreCalculator(java.lang.String, java.lang.String)
	 * 
	 */
	public double scoreCalculator(String fingerImage1, String fingerImage2) {
		double score = 0.0;
		try {
			FingerprintTemplate template1 = new FingerprintTemplate().deserialize(fingerImage1);
			FingerprintTemplate template2 = new FingerprintTemplate().deserialize(fingerImage2);
			FingerprintMatcher matcher = new FingerprintMatcher();
			score = matcher.index(template1).match(template2);
		} catch (IllegalArgumentException | JsonSyntaxException exception) {
			LOGGER.debug("REGISTRATION - FINGERPRINTPROVIDER - SCORECALCULATOR", APPLICATION_NAME, APPLICATION_ID,
					"Calculating Finger print score for Minutia");
		}
		return score;
	}

	@Override
	public void OnCaptureCompleted(boolean arg0, int arg1, String arg2, FingerData arg3) {

	}

	@Override
	public void OnPreview(FingerData arg0) {

	}
}
