/**
 * 
 */
package io.mosip.registration.dto.demographic;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LocationDTO extends BaseDTO {

	protected String line4;
	protected String line5;
	protected String line6;
	protected String line7;
	protected String line8;
}
