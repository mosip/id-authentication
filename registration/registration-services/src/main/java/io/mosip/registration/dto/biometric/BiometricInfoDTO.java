package io.mosip.registration.dto.biometric;

import java.util.List;

import io.mosip.registration.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * This class contains the Biometrics Information namely captured finger-prints,
 * missing finger-prints, captured iris and missing iris.
 *
 * @author Dinesh Asokan
 * @since 1.0.0
 */

@Getter
@Setter
public class BiometricInfoDTO extends BaseDTO {

	/** The fingerprint details DTO. */
	private List<FingerprintDetailsDTO> fingerprintDetailsDTO;

	/** The finger print biometric exception DTO. */
	private List<BiometricExceptionDTO> biometricExceptionDTO;

	/** The iris details DTO. */
	private List<IrisDetailsDTO> irisDetailsDTO;

	/** The face details DTO. */
	private FaceDetailsDTO face;
	
	/** The exception face details DTO. */
	private FaceDetailsDTO exceptionFace;
	
	/** The has exception photo. */
	private boolean hasExceptionPhoto;
	
}
