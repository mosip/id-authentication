package io.mosip.registration.dto.json.metadata;

/**
 * This class contains the attributes to be displayed for Introducer object in
 * PacketMetaInfo JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Introducer {

	private BiometricDetails introducerFingerprint;
	private BiometricDetails introducerIris;
	private BiometricDetails introducerImage;

	/**
	 * @return the introdcerFingerprint
	 */
	public BiometricDetails getIntroducerFingerprint() {
		return introducerFingerprint;
	}

	/**
	 * @param introducerFingerprint
	 *            the introducerFingerprint to set
	 */
	public void setIntroducerFingerprint(BiometricDetails introducerFingerprint) {
		this.introducerFingerprint = introducerFingerprint;
	}

	/**
	 * @return the introducerIris
	 */
	public BiometricDetails getIntroducerIris() {
		return introducerIris;
	}

	/**
	 * @param introducerIris
	 *            the introducerIris to set
	 */
	public void setIntroducerIris(BiometricDetails introducerIris) {
		this.introducerIris = introducerIris;
	}

	/**
	 * @return the introducerImage
	 */
	public BiometricDetails getIntroducerImage() {
		return introducerImage;
	}

	/**
	 * @param introducerImage
	 *            the introducerImage to set
	 */
	public void setIntroducerImage(BiometricDetails introducerImage) {
		this.introducerImage = introducerImage;
	}

}
