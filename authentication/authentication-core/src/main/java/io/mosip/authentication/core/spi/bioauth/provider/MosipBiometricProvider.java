package io.mosip.authentication.core.spi.bioauth.provider;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public interface MosipBiometricProvider {

	double matchScoreCalculator(String inputMinutiae, String storedMinutiae);

	double matchScoreCalculator(byte[] inputIsoTemplate, byte[] storedIsoTemplate);

	String createMinutiae(byte[] inputImage);

}
