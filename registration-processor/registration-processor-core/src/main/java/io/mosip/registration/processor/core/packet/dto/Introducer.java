package io.mosip.registration.processor.core.packet.dto;
	
/**
 * This class contains the attributes to be displayed for Introducer object in
 * PacketMetaInfo JSON.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Introducer {

	/** The introducer fingerprint. */
	private BiometricDetails introducerFingerprint;
	
	/** The introducer iris. */
	private BiometricDetails introducerIris;
	
	/** The introducer image. */
	private BiometricDetails introducerImage;

	/**
	 * Gets the introducer fingerprint.
	 *
	 * @return the introdcerFingerprint
	 */
	public BiometricDetails getIntroducerFingerprint() {
		return introducerFingerprint;
	}

	/**
	 * Sets the introducer fingerprint.
	 *
	 * @param introducerFingerprint            the introducerFingerprint to set
	 */
	public void setIntroducerFingerprint(BiometricDetails introducerFingerprint) {
		this.introducerFingerprint = introducerFingerprint;
	}

	/**
	 * Gets the introducer iris.
	 *
	 * @return the introducerIris
	 */
	public BiometricDetails getIntroducerIris() {
		return introducerIris;
	}

	/**
	 * Sets the introducer iris.
	 *
	 * @param introducerIris            the introducerIris to set
	 */
	public void setIntroducerIris(BiometricDetails introducerIris) {
		this.introducerIris = introducerIris;
	}

	/**
	 * Gets the introducer image.
	 *
	 * @return the introducerImage
	 */
	public BiometricDetails getIntroducerImage() {
		return introducerImage;
	}

	/**
	 * Sets the introducer image.
	 *
	 * @param introducerImage            the introducerImage to set
	 */
	public void setIntroducerImage(BiometricDetails introducerImage) {
		this.introducerImage = introducerImage;
	}

}
