package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the information on captured Finger prints.
 *
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class FingerprintDetailsDTO extends BaseDTO {

	/** The finger print. */
	private byte[] fingerPrint;
	
	/** The fingerprint image name. */
	protected String fingerprintImageName;
	
	/** The quality score. */
	protected double qualityScore;
	
	/** The is force captured. */
	protected boolean isForceCaptured;
	
	/** The finger type. */
	protected String fingerType;
	
	/** The num retry. */
	protected int numRetry;
	
	/**
	 * Gets the finger print.
	 *
	 * @return the finger print
	 */
	public byte[] getFingerPrint() {
		return fingerPrint;
	}
	
	/**
	 * Sets the finger print.
	 *
	 * @param fingerPrint the new finger print
	 */
	public void setFingerPrint(byte[] fingerPrint) {
		this.fingerPrint = fingerPrint;
	}
	
	/**
	 * Gets the fingerprint image name.
	 *
	 * @return the fingerprint image name
	 */
	public String getFingerprintImageName() {
		return fingerprintImageName;
	}
	
	/**
	 * Sets the fingerprint image name.
	 *
	 * @param fingerprintImageName the new fingerprint image name
	 */
	public void setFingerprintImageName(String fingerprintImageName) {
		this.fingerprintImageName = fingerprintImageName;
	}
	
	/**
	 * Gets the quality score.
	 *
	 * @return the quality score
	 */
	public double getQualityScore() {
		return qualityScore;
	}
	
	/**
	 * Sets the quality score.
	 *
	 * @param qualityScore the new quality score
	 */
	public void setQualityScore(double qualityScore) {
		this.qualityScore = qualityScore;
	}
	
	/**
	 * Checks if is force captured.
	 *
	 * @return true, if is force captured
	 */
	public boolean isForceCaptured() {
		return isForceCaptured;
	}
	
	/**
	 * Sets the force captured.
	 *
	 * @param isForceCaptured the new force captured
	 */
	public void setForceCaptured(boolean isForceCaptured) {
		this.isForceCaptured = isForceCaptured;
	}
	
	/**
	 * Gets the finger type.
	 *
	 * @return the finger type
	 */
	public String getFingerType() {
		return fingerType;
	}
	
	/**
	 * Sets the finger type.
	 *
	 * @param fingerType the new finger type
	 */
	public void setFingerType(String fingerType) {
		this.fingerType = fingerType;
	}
	
	/**
	 * Gets the num retry.
	 *
	 * @return the num retry
	 */
	public int getNumRetry() {
		return numRetry;
	}
	
	/**
	 * Sets the num retry.
	 *
	 * @param numRetry the new num retry
	 */
	public void setNumRetry(int numRetry) {
		this.numRetry = numRetry;
	}
	
}
