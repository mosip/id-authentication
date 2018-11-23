package io.mosip.registration.util.biometric;

import javafx.scene.image.WritableImage;

public interface MosipFingerprintProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * 
	 * MosipFingerprintProvider#scoreCalculator(byte[], byte[])
	 */
	double scoreCalculator(byte[] isoImage1, byte[] isoImage2);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#scoreCalculator(java.lang.String, java.lang.String)
	 * 
	 */
	double scoreCalculator(String fingerImage1, String fingerImage2);

	void captureFingerprint(int qualityScore, int captureTimeOut, String outputType);

	void uninitFingerPrintDevice();

	String getMinutia();

	byte[] getIsoTemplate();

	String getErrorMessage();

	WritableImage getFingerPrintImage();

}