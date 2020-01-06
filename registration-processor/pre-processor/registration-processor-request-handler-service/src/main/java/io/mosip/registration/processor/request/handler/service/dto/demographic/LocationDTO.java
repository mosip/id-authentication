/**
 * 
 */
package io.mosip.registration.processor.request.handler.service.dto.demographic;

import java.io.Serializable;

import io.mosip.registration.processor.request.handler.service.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Sowmya
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LocationDTO extends BaseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5199989198801823468L;
	protected String region;
	protected String city;
	protected String province;
	protected String postalCode;

}
