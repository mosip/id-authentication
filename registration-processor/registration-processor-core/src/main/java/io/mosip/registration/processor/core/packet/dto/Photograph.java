package io.mosip.registration.processor.core.packet.dto;

/**
 * 	
 * This class contains the attributes to be displayed for Photograph object in
 * PacketMetaInfo JSON.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Photograph {

	/** The language. */
	private String language;
	
	/** The label. */
	private String label;
	
	/** The photograph name. */
	private String photographName;
	
	/** The num retry. */
	private int numRetry;
	
	/** The quality score. */
	private double qualityScore;

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
	 * Gets the photograph name.
	 *
	 * @return the photographName
	 */
	public String getPhotographName() {
		return photographName;
	}

	/**
	 * Sets the photograph name.
	 *
	 * @param photographName            the photographName to set
	 */
	public void setPhotographName(String photographName) {
		this.photographName = photographName;
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

}
