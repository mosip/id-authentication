package org.mosip.registration.processor.dto.json.demo;

import lombok.Data;

@Data
public class Demographic {
	
	private DemographicInfo demoInLocalLang;
    private DemographicInfo demoInUserLang;
}
