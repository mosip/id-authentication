package io.mosip.registration.util.biometric;

import java.io.IOException;

import javafx.scene.image.WritableImage;

/**
 * This interface will have the list of FP related functions which needs to implemented in the device specific class.
 * The client class will invoke this interface to communicate with the respective provider based class.
 * 
 * @author M1046564
 *
 */
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

	int captureFingerprint(int qualityScore, int captureTimeOut, String outputType);

	void uninitFingerPrintDevice();

	String getMinutia();

	byte[] getIsoTemplate();

	String getErrorMessage();

	WritableImage getFingerPrintImage()throws IOException;

}