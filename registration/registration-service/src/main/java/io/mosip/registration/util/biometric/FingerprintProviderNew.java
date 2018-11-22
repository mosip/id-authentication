package io.mosip.registration.util.biometric;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import com.google.gson.JsonSyntaxException;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import javafx.scene.image.WritableImage;

public abstract class FingerprintProviderNew {

	protected String minutia = "";
	protected byte isoTemplate[] = null;
	protected String errorMessage = null;
	protected WritableImage fingerPrintImage = null;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(FingerprintProvider.class);

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

	public abstract void captureFingerprint(int qualityScore, int captureTimeOut, String outputType);
	public abstract void uninitFingerPrintDevice();

	public String getMinutia() {
		return minutia;
	}

	public byte[] getIsoTemplate() {
		return isoTemplate;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public WritableImage getFingerPrintImage() {
		return fingerPrintImage;
	}
	
}
