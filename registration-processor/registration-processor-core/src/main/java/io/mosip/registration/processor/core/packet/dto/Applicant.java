package io.mosip.registration.processor.core.packet.dto;
/**
 * 	
 * This class contains the attributes to be displayed for Applicant object in
 * PacketMetaInfo JSON.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Applicant {

	/** The left eye. */
	private BiometricDetails leftEye;
	
	/** The right eye. */
	private BiometricDetails rightEye;
	
	/** The left slap. */
	private BiometricDetails leftSlap;
	
	/** The right slap. */
	private BiometricDetails rightSlap;
	
	/** The thumbs. */
	private BiometricDetails thumbs;

	/**
	 * Gets the left eye.
	 *
	 * @return the leftEye
	 */
	public BiometricDetails getLeftEye() {
		return leftEye;
	}

	/**
	 * Sets the left eye.
	 *
	 * @param leftEye            the leftEye to set
	 */
	public void setLeftEye(BiometricDetails leftEye) {
		this.leftEye = leftEye;
	}

	/**
	 * Gets the right eye.
	 *
	 * @return the rightEye
	 */
	public BiometricDetails getRightEye() {
		return rightEye;
	}

	/**
	 * Sets the right eye.
	 *
	 * @param rightEye            the rightEye to set
	 */
	public void setRightEye(BiometricDetails rightEye) {
		this.rightEye = rightEye;
	}

	/**
	 * Gets the left slap.
	 *
	 * @return the leftSlap
	 */
	public BiometricDetails getLeftSlap() {
		return leftSlap;
	}

	/**
	 * Sets the left slap.
	 *
	 * @param leftSlap            the leftSlap to set
	 */
	public void setLeftSlap(BiometricDetails leftSlap) {
		this.leftSlap = leftSlap;
	}

	/**
	 * Gets the right slap.
	 *
	 * @return the rightSlap
	 */
	public BiometricDetails getRightSlap() {
		return rightSlap;
	}

	/**
	 * Sets the right slap.
	 *
	 * @param rightSlap            the rightSlap to set
	 */
	public void setRightSlap(BiometricDetails rightSlap) {
		this.rightSlap = rightSlap;
	}

	/**
	 * Gets the thumbs.
	 *
	 * @return the thumbs
	 */
	public BiometricDetails getThumbs() {
		return thumbs;
	}

	/**
	 * Sets the thumbs.
	 *
	 * @param thumbs            the thumbs to set
	 */
	public void setThumbs(BiometricDetails thumbs) {
		this.thumbs = thumbs;
	}
}
