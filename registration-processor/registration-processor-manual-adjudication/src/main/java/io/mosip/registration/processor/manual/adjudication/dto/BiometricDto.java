package io.mosip.registration.processor.manual.adjudication.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BiometricDto {
	private byte[] leftHandPalm;
	private byte[] rightHandPalm;
	private byte[] bothThumbs;
	private String qualityOfLeftHandPalm;
	private String qualityOfRightHandPalm;
	private String qualityOfBothThumbs;
	private byte[] leftEye;
	private byte[] rightEye;
	private String qualityOfLeftEye;
	private String qualityOfRightEye;
}
