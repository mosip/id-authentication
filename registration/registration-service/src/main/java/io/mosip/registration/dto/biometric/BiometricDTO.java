package io.mosip.registration.dto.biometric;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class contains the Biometric details
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BiometricDTO extends BaseDTO {
	private BiometricInfoDTO applicantBiometricDTO;
	private BiometricInfoDTO introducerBiometricDTO;
	private BiometricInfoDTO supervisorBiometricDTO;
	private BiometricInfoDTO operatorBiometricDTO;

}
