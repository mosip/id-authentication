package io.mosip.registration.dto.json.metadata;

/**
 * This class contains the attributes to be displayed for Applicant object in
 * PacketMetaInfo JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Applicant {

	private BiometricDetails leftEye;
	private BiometricDetails rightEye;
	private BiometricDetails leftSlap;
	private BiometricDetails rightSlap;
	private BiometricDetails thumbs;

	/**
	 * @return the leftEye
	 */
	public BiometricDetails getLeftEye() {
		return leftEye;
	}

	/**
	 * @param leftEye
	 *            the leftEye to set
	 */
	public void setLeftEye(BiometricDetails leftEye) {
		this.leftEye = leftEye;
	}

	/**
	 * @return the rightEye
	 */
	public BiometricDetails getRightEye() {
		return rightEye;
	}

	/**
	 * @param rightEye
	 *            the rightEye to set
	 */
	public void setRightEye(BiometricDetails rightEye) {
		this.rightEye = rightEye;
	}

	/**
	 * @return the leftSlap
	 */
	public BiometricDetails getLeftSlap() {
		return leftSlap;
	}

	/**
	 * @param leftSlap
	 *            the leftSlap to set
	 */
	public void setLeftSlap(BiometricDetails leftSlap) {
		this.leftSlap = leftSlap;
	}

	/**
	 * @return the rightSlap
	 */
	public BiometricDetails getRightSlap() {
		return rightSlap;
	}

	/**
	 * @param rightSlap
	 *            the rightSlap to set
	 */
	public void setRightSlap(BiometricDetails rightSlap) {
		this.rightSlap = rightSlap;
	}

	/**
	 * @return the thumbs
	 */
	public BiometricDetails getThumbs() {
		return thumbs;
	}

	/**
	 * @param thumbs
	 *            the thumbs to set
	 */
	public void setThumbs(BiometricDetails thumbs) {
		this.thumbs = thumbs;
	}
}
