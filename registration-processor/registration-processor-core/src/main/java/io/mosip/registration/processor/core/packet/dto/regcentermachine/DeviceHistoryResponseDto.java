package io.mosip.registration.processor.core.packet.dto.regcentermachine;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Megha Tanga
 * @version 1.0.0
 */
@Data
public class DeviceHistoryResponseDto {
	private List<DeviceHistoryDto> deviceHistoryDetails;

	private List<ErrorDTO> errors;

}
