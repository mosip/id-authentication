package io.mosip.registration.processor.quality.check.dto;

import io.mosip.registration.processor.core.packet.dto.Demographic;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public class ApplicantInfoDto extends Demographic {

	
	private Photograph applicantPhoto;
	

	
}
