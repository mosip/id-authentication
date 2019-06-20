package io.mosip.registration.device.fp;

import java.util.List;

import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;

public interface FingerPrintCaptureService {

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.device.impl.FingerPrintCaptureService#validateFingerprint(java.util.List)
	 */
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.device.impl.FingerPrintCaptureService#validateFingerprint(java.util.List)
	 */
	boolean validateFingerprint(List<FingerprintDetailsDTO> fingerprintDetailsDTOs);

}