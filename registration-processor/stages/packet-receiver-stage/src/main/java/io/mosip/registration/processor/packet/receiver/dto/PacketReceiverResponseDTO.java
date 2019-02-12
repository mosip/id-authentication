package io.mosip.registration.processor.packet.receiver.dto;

import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new packet receiver response DTO.
 * @author Rishabh Keshari
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PacketReceiverResponseDTO extends BaseRequestResponseDTO {
	
	/** The response. */
	private ResponseDTO response;
	
	/** The error. */
	private ErrorDTO error;
	
}
