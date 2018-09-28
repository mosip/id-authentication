package org.mosip.registration.dto.json.metadata;

public class Iris {
	private String irisImageName;
	private double qualityScore;
	private boolean isForceCaptured;
	private String irisType;

	/**
	 * @return the irisImageName
	 */
	public String getIrisImageName() {
		return irisImageName;
	}

	/**
	 * @param irisImageName
	 *            the irisImageName to set
	 */
	public void setIrisImageName(String irisImageName) {
		this.irisImageName = irisImageName;
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
	 * @return the irisType
	 */
	public String getIrisType() {
		return irisType;
	}

	/**
	 * @param irisType
	 *            the irisType to set
	 */
	public void setIrisType(String irisType) {
		this.irisType = irisType;
	}
}
