package io.mosip.registration.processor.quality.check.dto;

import java.io.Serializable;

import io.mosip.registration.processor.core.packet.dto.BiometericData;
import io.mosip.registration.processor.core.packet.dto.Demographic;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public class ApplicantInfoDto extends Demographic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BiometericData biometericData;
	private Photograph applicantPhoto;
	

	
}
