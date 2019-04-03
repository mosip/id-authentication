package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;
import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new registration sync request DTO.
 * 
 * @author Rishabh Keshari
 */
@Data

/*
 * (non-Javadoc)
 * 
 * @see io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO#
 * hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class RegistrationSyncRequestDTO extends BaseRestRequestDTO implements Serializable {

	private static final long serialVersionUID = 7914304502765754692L;
	/** The request. */
	private List<SyncRegistrationDTO> request;

}
