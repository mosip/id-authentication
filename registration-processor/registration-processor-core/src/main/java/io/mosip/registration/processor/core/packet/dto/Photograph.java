package io.mosip.registration.processor.core.packet.dto;

/**
 * This class contains the attributes to be displayed for Photograph object in
 * PacketMetaInfo JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Photograph {

	private String language;
	private String label;
	private String photographName;
	private int numRetry;
	private double qualityScore;

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the photographName
	 */
	public String getPhotographName() {
		return photographName;
	}

	/**
	 * @param photographName
	 *            the photographName to set
	 */
	public void setPhotographName(String photographName) {
		this.photographName = photographName;
	}

	/**
	 * @return the numRetry
	 */
	public int getNumRetry() {
		return numRetry;
	}

	/**
	 * @param numRetry
	 *            the numRetry to set
	 */
	public void setNumRetry(int numRetry) {
		this.numRetry = numRetry;
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

}
