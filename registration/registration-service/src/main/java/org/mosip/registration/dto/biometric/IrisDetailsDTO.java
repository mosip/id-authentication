package org.mosip.registration.dto.biometric;

import org.mosip.registration.dto.BaseDTO;

/**
 * This class contains the information on captured Iris
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class IrisDetailsDTO extends BaseDTO {
	private byte[] iris;
	private String irisName;
	private double qualityScore;
	private boolean isForceCaptured;
	private String irisType;

	/**
	 * @return the iris
	 */
	public byte[] getIris() {
		return iris;
	}

	/**
	 * @param iris
	 *            the iris to set
	 */
	public void setIris(byte[] iris) {
		this.iris = iris;
	}

	/**
	 * @return the irisName
	 */
	public String getIrisName() {
		return irisName;
	}

	/**
	 * @param irisName
	 *            the irisName to set
	 */
	public void setIrisName(String irisName) {
		this.irisName = irisName;
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
