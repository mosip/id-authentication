package io.mosip.registration.processor.core.packet.dto.masterdata;

import java.util.List;

import io.mosip.registration.processor.core.packet.dto.regcentermachine.ErrorDTO;
import lombok.Data;

@Data
public class StatusResponseDto {

	private String status;

	private List<ErrorDTO> errors;
}
