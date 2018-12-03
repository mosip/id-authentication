package io.mosip.registration.processor.manual.adjudication.service;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.manual.adjudication.dto.ApplicantDetailsDto;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
/**
 * 
 * @author M1049617
 *
 */
@Service
public interface ManualAdjudicationService {
	
	public ApplicantDetailsDto assignStatus(UserDto dto);
}
