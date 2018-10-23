package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class Demographic {

	private DemographicInfo demoInLocalLang;
	private DemographicInfo demoInUserLang;

}