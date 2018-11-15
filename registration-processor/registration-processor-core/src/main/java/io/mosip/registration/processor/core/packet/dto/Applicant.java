/**
 * 
 */
package io.mosip.registration.processor.core.packet.dto;

/**
 * @author M1022006
 *
 */
public class Applicant {

	private BiometricData leftEye;
	private BiometricData rightEye;
	private BiometricData leftSlap;
	private BiometricData rightSlap;
	private BiometricData thumbs;

	public BiometricData getLeftEye() {
		return leftEye;
	}

	public void setLeftEye(BiometricData leftEye) {
		this.leftEye = leftEye;
	}

	public BiometricData getRightEye() {
		return rightEye;
	}

	public void setRightEye(BiometricData rightEye) {
		this.rightEye = rightEye;
	}

	public BiometricData getLeftSlap() {
		return leftSlap;
	}

	public void setLeftSlap(BiometricData leftSlap) {
		this.leftSlap = leftSlap;
	}

	public BiometricData getRightSlap() {
		return rightSlap;
	}

	public void setRightSlap(BiometricData rightSlap) {
		this.rightSlap = rightSlap;
	}

	public BiometricData getThumbs() {
		return thumbs;
	}

	public void setThumbs(BiometricData thumbs) {
		this.thumbs = thumbs;
	}

}
