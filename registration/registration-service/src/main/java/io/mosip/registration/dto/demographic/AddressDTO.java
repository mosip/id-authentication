package io.mosip.registration.dto.demographic;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * This class used to capture the Address of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class AddressDTO extends BaseDTO {
	
	protected String line1;
	protected String line2;
	protected String line3;
	protected LocationDTO locationDTO;
}
