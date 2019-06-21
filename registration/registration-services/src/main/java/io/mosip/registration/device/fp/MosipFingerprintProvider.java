package io.mosip.registration.device.fp;

/**
 * This interface will have the list of FP related functions which needs to
 * implemented in the device specific class. The client class will invoke this
 * interface to communicate with the respective provider based class.
 * 
 * @author M1046564
 *
 */
public interface MosipFingerprintProvider {

	/**
	 * This method is used to calculate the fingerprint score based on ISO Images.
	 *
	 * @param isoImage1
	 *            - the ISO image
	 * @param isoImage2
	 *            - the ISO image
	 * @return the calculated fingerprint score
	 */
	double scoreCalculator(byte[] isoImage1, byte[] isoImage2);

	/**
	 * This method is used to calculate the fingerprint score based on serialized
	 * fingerprint template in JSON format.
	 *
	 * @param fingerImage1
	 *            the serialized fingerprint template in JSON format
	 * @param fingerImage2
	 *            the serialized fingerprint template in JSON format
	 * @return the calculated fingerprint score
	 */
	double scoreCalculator(String fingerImage1, String fingerImage2);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.fingerprintauth.provider.
	 * MosipFingerprintProvider#captureFingerprint(java.lang.Integer,
	 * java.lang.Integer, java.lang.String)
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

	/**
	 * This method is used to get the minutia of the fingerprint.
	 *
	 * @return the minutia of fingerprint
	 */
	String getMinutia();

	/**
	 * This method gets the ISO template.
	 *
	 * @return the ISO template
	 */
	byte[] getIsoTemplate();

	/**
	 * This method gets the error message.
	 *
	 * @return the error message
	 */
	String getErrorMessage();
}