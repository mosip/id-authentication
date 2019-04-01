package io.mosip.registration.device.fp;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#captureFingerprint(java.lang.Integer, java.lang.Integer, java.lang.String)
	 * 
	 */
	int captureFingerprint(int qualityScore, int captureTimeOut, String outputType);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#uninitFingerPrintDevice()
	 * 
	 */
	void uninitFingerPrintDevice();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#getMinutia()
	 * 
	 */
	String getMinutia();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#getIsoTemplate()
	 * 
	 */
	byte[] getIsoTemplate();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#getErrorMessage()
	 * 
	 */
	String getErrorMessage();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#getFingerPrintImage()
	 * 
	 */
	WritableImage getFingerPrintImage()throws IOException;
}