package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Device Type 
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeResponseDto {
	private List<DeviceTypeDto> successfullyCreatedDevices;
}
