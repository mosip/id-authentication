package io.mosip.registration.processor.packet.receiver.dto;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new packet receiver response DTO.
 * @author Rishabh Keshari
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PacketReceiverResponseDTO extends BaseRestResponseDTO {
	

	private static final long serialVersionUID = -6943589722277098292L;

	/** The response. */
	private ResponseDTO response;
	
	/** The error. */
	private ErrorDTO error;
	
}
