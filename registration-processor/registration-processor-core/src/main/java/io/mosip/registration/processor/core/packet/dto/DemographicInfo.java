package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class DemographicInfo {

	private DemoInLocalLang demoInLocalLang;
	private DemoInUserLang demoInUserLang;

}