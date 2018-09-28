package org.mosip.registration.dto.json.metadata;

public class Fingerprints {
	private String fingerprintImageName;
	private double qualityScore;
	private boolean isForceCaptured;
	private String fingerType;

	/**
	 * @return the fingerprintImageName
	 */
	public String getFingerprintImageName() {
		return fingerprintImageName;
	}

	/**
	 * @param fingerprintImageName
	 *            the fingerprintImageName to set
	 */
	public void setFingerprintImageName(String fingerprintImageName) {
		this.fingerprintImageName = fingerprintImageName;
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
