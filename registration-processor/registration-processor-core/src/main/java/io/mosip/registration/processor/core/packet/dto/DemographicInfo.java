package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new demographic info.
 */
@Data
public class DemographicInfo {

	/** The demo in local lang. */
	private DemoInLocalLang demoInLocalLang;

	/** The demo in user lang. */
	private DemoInUserLang demoInUserLang;

}