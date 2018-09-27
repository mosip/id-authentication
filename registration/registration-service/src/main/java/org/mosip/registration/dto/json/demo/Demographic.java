package org.mosip.registration.dto.json.demo;

import lombok.Data;

@Data
public class Demographic {
	
	private DemographicInfo demoInLocalLang;
    private DemographicInfo demoInUserLang;
}
