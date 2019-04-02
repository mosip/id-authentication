package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;
import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new packet generator response dto.
 * 
 * @author Sowmya
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PacketGeneratorResponseDto extends BaseRestResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3175031923383376039L;

	/** The response. */
	private PacketGeneratorResDto response;

	/** The error. */
	private List<ErrorDTO> errors;
}
