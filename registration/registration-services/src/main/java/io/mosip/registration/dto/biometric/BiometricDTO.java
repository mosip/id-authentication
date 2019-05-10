package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * This class contains the Biometric details
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
@Getter
@Setter
public class BiometricDTO extends BaseDTO {
	private BiometricInfoDTO applicantBiometricDTO;
	private BiometricInfoDTO introducerBiometricDTO;
	private BiometricInfoDTO supervisorBiometricDTO;
	private BiometricInfoDTO operatorBiometricDTO;
}
