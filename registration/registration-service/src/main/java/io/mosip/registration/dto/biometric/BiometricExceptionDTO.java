package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class contains the information on exception fingerprint data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class BiometricExceptionDTO extends BaseDTO {
	// Fingerprint or Iris
	protected String biometricType;
	protected String missingBiometric;
	protected byte[] biometricISOImage;
	protected String exceptionDescription;
	// Permanent or Temporary
	protected String exceptionType;
	public String getBiometricType() {
		return biometricType;
	}
	public void setBiometricType(String biometricType) {
		this.biometricType = biometricType;
	}
	public String getMissingBiometric() {
		return missingBiometric;
	}
	public void setMissingBiometric(String missingBiometric) {
		this.missingBiometric = missingBiometric;
	}
	public String getExceptionDescription() {
		return exceptionDescription;
	}
	public void setExceptionDescription(String exceptionDescription) {
		this.exceptionDescription = exceptionDescription;
	}
	public String getExceptionType() {
		return exceptionType;
	}
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	/**
	 * @return the biometricISOImage
	 */
	public byte[] getBiometricISOImage() {
		return biometricISOImage;
	}
	/**
	 * @param biometricISOImage the biometricISOImage to set
	 */
	public void setBiometricISOImage(byte[] biometricISOImage) {
		this.biometricISOImage = biometricISOImage;
	}
	
}