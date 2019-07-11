/**
 * 
 */
package io.mosip.registration.processor.packet.service.dto.json.demo;

import java.io.Serializable;

import io.mosip.registration.processor.packet.service.dto.demographic.LocationDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This the class for JSON object for Location
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Location extends LocationDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -4348997486325590309L;

}
