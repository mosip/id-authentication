package io.mosip.registration.processor.packet.service.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Sowmya
 * 
 */
@Data

public class MachineResponseDto {
	private List<MachineDto> machines;
	private List<ErrorDTO> errors;
}
