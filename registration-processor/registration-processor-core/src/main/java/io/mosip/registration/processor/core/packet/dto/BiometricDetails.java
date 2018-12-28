package io.mosip.registration.processor.core.packet.dto;

/**
 * 	
 * This class contains the attributes to be displayed for Biometric object in
 * PacketMetaInfo JSON.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class BiometricDetails {

	/** The language. */
	private String language;
	
	/** The label. */
	private String label;
	
	/** The image name. */
	private String imageName;
	
	/** The type. */
	private String type;
	
	/** The quality score. */
	private double qualityScore;
	
	/** The num retry. */
	private int numRetry;
	
	/** The force captured. */
	private boolean forceCaptured;

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 *
	 * @param language            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 *
	 * @param label            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the image name.
	 *
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * Sets the image name.
	 *
	 * @param imageName            the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the quality score.
	 *
	 * @return the qualityScore
	 */
	public double getQualityScore() {
		return qualityScore;
	}

	/**
	 * Sets the quality score.
	 *
	 * @param qualityScore            the qualityScore to set
	 */
	public void setQualityScore(double qualityScore) {
		this.qualityScore = qualityScore;
	}

	/**
	 * Gets the num retry.
	 *
	 * @return the numRetry
	 */
	public int getNumRetry() {
		return numRetry;
	}

	/**
	 * Sets the num retry.
	 *
	 * @param numRetry            the numRetry to set
	 */
	public void setNumRetry(int numRetry) {
		this.numRetry = numRetry;
	}

	/**
	 * Checks if is force captured.
	 *
	 * @return the forceCaptured
	 */
	public boolean isForceCaptured() {
		return forceCaptured;
	}

	/**
	 * Sets the force captured.
	 *
	 * @param forceCaptured            the forceCaptured to set
	 */
	public void setForceCaptured(boolean forceCaptured) {
		this.forceCaptured = forceCaptured;
	}

}
