package io.mosip.registration.processor.packet.storage.dto;


import java.util.List;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ApplicantInfoDto {

	
	private PhotographDto applicantPhotograph;
	private List<DemographicInfoDto> demoDedupeList;



	
}
