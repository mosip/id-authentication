package io.mosip.registration.processor.request.handler.service.dto.demographic;

import java.io.Serializable;

import io.mosip.registration.processor.request.handler.service.dto.BaseDTO;
import lombok.Data;

/**
 * This class used to capture the Address of the Individual
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
public class AddressDTO extends BaseDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8889366054582786078L;
	protected String addressLine1;
	protected String addressLine2;
	protected String addressLine3;
	protected LocationDTO locationDTO;

}
