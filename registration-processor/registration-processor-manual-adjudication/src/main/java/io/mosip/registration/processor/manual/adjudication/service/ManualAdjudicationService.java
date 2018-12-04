package io.mosip.registration.processor.manual.adjudication.service;

import org.springframework.stereotype.Service;
import io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;

/**
 * 
 * @author M1049617
 *
 */
@Service
public interface ManualAdjudicationService {

	public ManualVerificationDTO assignStatus(UserDto dto);

	public byte[] getApplicantFile(String regId, String fileName);

	public byte[] getApplicantData(String regId, String fileName);

	public ManualVerificationDTO updatePacketStatus(ManualVerificationDTO manualVerificationDTO);
}
