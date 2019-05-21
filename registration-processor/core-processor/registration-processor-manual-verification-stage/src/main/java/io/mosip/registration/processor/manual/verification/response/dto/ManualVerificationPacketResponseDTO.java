package io.mosip.registration.processor.manual.verification.response.dto;

import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
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
public class ManualVerificationPacketResponseDTO extends BaseRestResponseDTO {

	private static final long serialVersionUID = -1848465072707393124L;

	/** The response. */
	private PacketMetaInfo response;
	
	/** The error. */
	private List<ErrorDTO> errors;
	
}
