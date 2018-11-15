/**
 * 
 */
package io.mosip.registration.processor.core.packet.dto;

/**
 * @author M1022006
 *
 */
public class Introducer {

	private BiometricData introducerFingerprint;
	private BiometricData introducerIris;
	private BiometricData introducerImage;
	public BiometricData getIntroducerFingerprint() {
		return introducerFingerprint;
	}
	public void setIntroducerFingerprint(BiometricData introducerFingerprint) {
		this.introducerFingerprint = introducerFingerprint;
	}
	public BiometricData getIntroducerIris() {
		return introducerIris;
	}
	public void setIntroducerIris(BiometricData introducerIris) {
		this.introducerIris = introducerIris;
	}
	public BiometricData getIntroducerImage() {
		return introducerImage;
	}
	public void setIntroducerImage(BiometricData introducerImage) {
		this.introducerImage = introducerImage;
	}
	
	
}
