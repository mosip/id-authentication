package io.mosip.registration.dto.mastersync;

import java.util.List;

/**
 * 
 * @author Uday Kumar
 * @version 1.0.0
 * @since 23-10-2016
 */

public class BiometricTypeResponseDto {
	
	List<BiometricTypeDto> biometrictype;

	/**
	 * @return the biometrictype
	 */
	public List<BiometricTypeDto> getBiometrictype() {
		return biometrictype;
	}

	/**
	 * @param biometrictype the biometrictype to set
	 */
	public void setBiometrictype(List<BiometricTypeDto> biometrictype) {
		this.biometrictype = biometrictype;
		System.out.println();
	}

}
