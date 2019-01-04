package io.mosip.authentication.core.spi.bioauth.provider;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public interface MosipBiometricProvider {

	/**
	 * Score calculator for Minutiae
	 * 
	 * @param inputMinutiae
	 * @param storedMinutiae
	 * @return
	 */
	double matchScoreCalculator(String inputMinutiae, String storedMinutiae);

	/**
	 * Score calculator for ISO Template
	 * 
	 * @param inputIsoTemplate
	 * @param storedIsoTemplate
	 * @return
	 */
	double matchScoreCalculator(byte[] inputIsoTemplate, byte[] storedIsoTemplate);

	/**
	 * Method to Create Minutiae
	 * 
	 * @param inputImage
	 * @return
	 */
	String createMinutiae(byte[] inputImage);

}
