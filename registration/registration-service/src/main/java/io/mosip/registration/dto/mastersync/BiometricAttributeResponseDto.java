package io.mosip.registration.dto.mastersync;

import java.util.List;

/**
 * 
 * @author Sreekar Chukka
 * @version 1.0.0
 * @since 23-10-2016
 */

public class BiometricAttributeResponseDto {
	List<BiometricAttributeDto> biometricattribute;

	/**
	 * @return the biometricattribute
	 */
	public List<BiometricAttributeDto> getBiometricattribute() {
		return biometricattribute;
	}

	/**
	 * @param biometricattribute the biometricattribute to set
	 */
	public void setBiometricattribute(List<BiometricAttributeDto> biometricattribute) {
		this.biometricattribute = biometricattribute;
	}
	
	
	
	
}
