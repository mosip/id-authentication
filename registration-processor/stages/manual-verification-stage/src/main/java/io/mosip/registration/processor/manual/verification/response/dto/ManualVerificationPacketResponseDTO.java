package io.mosip.registration.processor.manual.verification.response.dto;

import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Instantiates a new manual verification packet response DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class ManualVerificationPacketResponseDTO extends BaseRequestResponseDTO {
	
	/** The response. */
	private PacketMetaInfo response;
	
	/** The error. */
	private ManualVerificationErrorDTO error;
	
}
