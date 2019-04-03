package io.mosip.registration.processor.packet.service.dto.json.demo;

import java.io.Serializable;

import lombok.Data;

/**
 * This class is used in JSON Parsing of Registration Packet
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
public class Demographic implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1338847982631308824L;
	private DemographicInfo demoInLocalLang;
	private DemographicInfo demoInUserLang;

}
