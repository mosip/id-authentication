package io.mosip.registration.processor.packet.service.dto.json.demo;

import io.mosip.registration.processor.packet.service.dto.demographic.AddressDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This the class for JSON object for Address
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Address extends AddressDTO {
	/**
	*
	*/
	private static final long serialVersionUID = 5170367565734470755L;
}
