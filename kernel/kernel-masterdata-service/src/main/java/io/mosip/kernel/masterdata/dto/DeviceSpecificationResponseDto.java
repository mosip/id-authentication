package io.mosip.kernel.masterdata.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO class for Device specification get operation 
 * 
 * @author Uday
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSpecificationResponseDto {
	private List<DeviceSpecificationDto> devicespecifications;
}
