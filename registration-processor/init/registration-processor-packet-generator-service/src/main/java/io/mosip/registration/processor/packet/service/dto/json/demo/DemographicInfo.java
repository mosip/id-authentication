package io.mosip.registration.processor.packet.service.dto.json.demo;

import io.mosip.registration.processor.packet.service.dto.demographic.DemographicInfoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class is used in JSON Parsing of Registration Packet
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DemographicInfo extends DemographicInfoDTO {
	/**
	*
	*/
	private static final long serialVersionUID = -2597568446093803290L;
}
