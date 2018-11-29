package io.mosip.registration.processor.manual.adjudication.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new applicant details dto.
 */
@Data

/* (non-Javadoc)
 * @see java.lang.Object#hashCode()
 */
@EqualsAndHashCode(callSuper=false)
public class ApplicantDetailsDto {
	
	private DemographicDto demographicDto;
	
	private BiometricDto biometricDto;
	private DocumentDto documentDto;
	private byte[] exceptionPhoto;
}
