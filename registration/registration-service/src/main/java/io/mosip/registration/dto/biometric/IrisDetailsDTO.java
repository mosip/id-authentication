package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the information on captured Iris.
 *
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class IrisDetailsDTO extends BaseDTO {

	/** The iris. */
	private byte[] iris;

	/** The iris image name. */
	protected String irisImageName;

	/** The quality score. */
	protected double qualityScore;

	/** The is force captured. */
	protected boolean isForceCaptured;

	/** The iris type. */
	protected String irisType;
	/** The num of iris retry. */
	private int numOfIrisRetry;

	/**
	 * Gets the iris.
	 *
	 * @return the iris
	 */
	public byte[] getIris() {
		return iris;
	}

	/**
	 * Sets the iris.
	 *
	 * @param iris
	 *            the new iris
	 */
	public void setIris(byte[] iris) {
		this.iris = iris;
	}

	/**
	 * Gets the iris image name.
	 *
	 * @return the iris image name
	 */
	public String getIrisImageName() {
		return irisImageName;
	}

	/**
	 * Sets the iris image name.
	 *
	 * @param irisImageName
	 *            the new iris image name
	 */
	public void setIrisImageName(String irisImageName) {
		this.irisImageName = irisImageName;
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
	 * @param qualityScore
	 *            the new quality score
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
	 * @param isForceCaptured
	 *            the new force captured
	 */
	public void setForceCaptured(boolean isForceCaptured) {
		this.isForceCaptured = isForceCaptured;
	}

	/**
	 * Gets the iris type.
	 *
	 * @return the iris type
	 */
	public String getIrisType() {
		return irisType;
	}

	/**
	 * Sets the iris type.
	 *
	 * @param irisType
	 *            the new iris type
	 */
	public void setIrisType(String irisType) {
		this.irisType = irisType;
	}

	/**
	 * @return the numOfIrisRetry
	 */
	public int getNumOfIrisRetry() {
		return numOfIrisRetry;
	}

	/**
	 * @param numOfIrisRetry
	 *            the numOfIrisRetry to set
	 */
	public void setNumOfIrisRetry(int numOfIrisRetry) {
		this.numOfIrisRetry = numOfIrisRetry;
	}

}
