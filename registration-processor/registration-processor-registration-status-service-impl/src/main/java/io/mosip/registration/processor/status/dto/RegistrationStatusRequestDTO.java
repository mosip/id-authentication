package io.mosip.registration.processor.status.dto;

import java.util.List;
import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new registration status request DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class RegistrationStatusRequestDTO extends BaseRestRequestDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2987693214912415439L;
	/** The request. */
	private List<RegistrationStatusSubRequestDto> request;
		
}
