package org.mosip.registration.dto.biometric;

import org.mosip.registration.dto.BaseDTO;

/**
 * This class contains the information on captured Finger prints
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class FingerprintDetailsDTO extends BaseDTO {
	private byte[] fingerPrint;
	private String fingerPrintName;
	private double qualityScore;
	private boolean isForceCaptured;
	private String fingerType;

	/**
	 * @return the fingerPrint
	 */
	public byte[] getFingerPrint() {
		return fingerPrint;
	}

	/**
	 * @param fingerPrint
	 *            the fingerPrint to set
	 */
	public void setFingerPrint(byte[] fingerPrint) {
		this.fingerPrint = fingerPrint;
	}

	/**
	 * @return the fingerPrintName
	 */
	public String getFingerPrintName() {
		return fingerPrintName;
	}

	/**
	 * @param fingerPrintName
	 *            the fingerPrintName to set
	 */
	public void setFingerPrintName(String fingerPrintName) {
		this.fingerPrintName = fingerPrintName;
	}

	/**
	 * @return the qualityScore
	 */
	public double getQualityScore() {
		return qualityScore;
	}

	/**
	 * @param qualityScore
	 *            the qualityScore to set
	 */
	public void setQualityScore(double qualityScore) {
		this.qualityScore = qualityScore;
	}

	/**
	 * @return the isForceCaptured
	 */
	public boolean isForceCaptured() {
		return isForceCaptured;
	}

	/**
	 * @param isForceCaptured
	 *            the isForceCaptured to set
	 */
	public void setForceCaptured(boolean isForceCaptured) {
		this.isForceCaptured = isForceCaptured;
	}

	/**
	 * @return the fingerType
	 */
	public String getFingerType() {
		return fingerType;
	}

	/**
	 * @param fingerType
	 *            the fingerType to set
	 */
	public void setFingerType(String fingerType) {
		this.fingerType = fingerType;
	}
}
