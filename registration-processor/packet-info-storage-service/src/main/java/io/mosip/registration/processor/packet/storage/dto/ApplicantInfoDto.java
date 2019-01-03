package io.mosip.registration.processor.packet.storage.dto;
	

import java.util.List;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new applicant info dto.
 */
@Data

/* (non-Javadoc)
 * @see java.lang.Object#hashCode()
 */
@EqualsAndHashCode(callSuper=false)
public class ApplicantInfoDto {

	
	/** The applicant photograph. */
	private PhotographDto applicantPhotograph;
	
	/** The demo dedupe list. */
	private List<DemographicInfoDto> demoDedupeList;



	
}
